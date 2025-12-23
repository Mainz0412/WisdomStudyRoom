package com.huawei.web.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.huawei.web.entity.Log;
import com.huawei.web.entity.Reserve;
import com.huawei.web.service.LogService;
import com.huawei.web.service.ReserveService;
import com.huawei.web.service.SeatTimeService;
import com.huawei.web.service.SignService;
import com.huawei.web.service.UserService;
import com.huawei.web.util.AjaxResult;
import com.huawei.web.util.constant.Constant;

import jakarta.annotation.Resource;

/**
 * @author Yi Chuizhou
 */
@RestController
@Component
@RequestMapping("/sign")
public class SignController {
  @Resource
  private SignService signService;
  @Resource
  private SeatTimeService seatTimeService;
  @Resource
  private ReserveService reserveService;
  @Resource
  private UserService userService;
  @Resource
  private LogService logService;

  /**
   * 签到
   *
   * @param reserve 预约信息
   * @return 签到结果
   */
  @PostMapping("/in")
  @ResponseBody
  public AjaxResult signIn(@RequestBody Reserve reserve) {
    signService.signIn(reserve);
    return AjaxResult.success("签到成功");
  }

  /**
   * 签退
   *
   * @param reserve 预约信息
   * @return 签退结果
   */
  @PostMapping("/out")
  @ResponseBody
  public AjaxResult signOut(@RequestBody Reserve reserve) {
    signService.signOut(reserve);
    seatTimeService.deleteSeat(reserve);
    return AjaxResult.success("签退成功");
  }

  /** 将30分钟未签到的取消预约并释放座位 */
  @Scheduled(cron = "0/10 * * * * ?")
  public void autoSignInCancel() {
    // 查找已超出30分钟未签到的 reserve（开始时间 <= now - 30min）
    Timestamp time = Timestamp.valueOf(LocalDateTime.now().minusMinutes(30));
    List<Reserve> reserveList = signService.selectSignInDelay(time);
    for (Reserve reserve : reserveList) {
      // 再次从数据库中确认预约是否仍然存在且仍处于“正常待签到”状态，
      // 防止管理员或用户在本次扫描与处理之间手动删除/修改预约导致误记违规。
      Reserve current = reserveService.selectById(reserve.getReserveId());
      if (current == null ||
          current.getReserveState() == null ||
          !Constant.SIGN_IN_NORMAL.equals(current.getReserveState())) {
        // 预约已被删除或状态已改变（例如已签到/已取消），跳过违规处理
        continue;
      }

      reserve = current;
      // 获取用户信息
      com.huawei.web.entity.User user = userService.selectUserById(reserve.getUserId());
      if (user != null) {
        reserve.setReserveUserAccount(user.getUserAccount());
      }

      // 删除座位时间
      seatTimeService.deleteSeat(reserve);
      // 添加违规次数
      if (reserve.getReserveUserAccount() != null) {
        userService.addIllegal(reserve.getReserveUserAccount());
        // 添加日志
        Log log = new Log();
        log.setLogState("签到超时");
        log.setLogTime(time);
        log.setUserAccount(reserve.getReserveUserAccount());
        logService.insertLog(log);
      }
      // 更新座位状态
      reserve.setReserveState(Constant.SIGN_IN_TIMEOUT);
      reserveService.updateReserve(reserve);
    }
  }

  /** 到时间自动签退 */
  @Scheduled(cron = "0/10 * * * * ?")
  public void autoSignOutCancel() {
    // 查找已到结束时间但未签退的 reserve（结束时间 <= now）
    Timestamp time = Timestamp.valueOf(LocalDateTime.now());
    List<Reserve> reserveList = signService.selectSignOutDelay(time);
    for (Reserve reserve : reserveList) {
      // 同样在处罚前再次确认预约当前状态，避免管理员在此期间手动删除预约
      Reserve current = reserveService.selectById(reserve.getReserveId());
      if (current == null ||
          current.getReserveState() == null ||
          !Constant.SIGN_IN_NORMAL.equals(current.getReserveState())) {
        continue;
      }

      reserve = current;
      // 获取用户信息
      com.huawei.web.entity.User user = userService.selectUserById(reserve.getUserId());
      if (user != null) {
        reserve.setReserveUserAccount(user.getUserAccount());
      }

      // 删除座位时间
      seatTimeService.deleteSeat(reserve);
      // 添加违规次数
      if (reserve.getReserveUserAccount() != null) {
        userService.addIllegal(reserve.getReserveUserAccount());
        // 添加日志
        Log log = new Log();
        log.setLogState("签退超时");
        log.setLogTime(time);
        log.setUserAccount(reserve.getReserveUserAccount());
        logService.insertLog(log);
      }
      // 更新座位状态
      reserve.setReserveState(Constant.SIGN_OUT_TIMEOUT);
      reserve.setTimeSignOut(time);
      reserveService.updateReserve(reserve);
    }
  }
}

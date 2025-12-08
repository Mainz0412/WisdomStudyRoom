package com.huawei.web.controller;

import com.huawei.web.entity.Log;
import com.huawei.web.entity.Reserve;
import com.huawei.web.service.*;
import com.huawei.web.util.AjaxResult;
import com.huawei.web.util.constant.Constant;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * @author Ruijie Zhao
 */
@RestController
@EnableScheduling
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
    // 查找30分钟未签到的reserve
    Timestamp time = Timestamp.valueOf(LocalDateTime.now().plusMinutes(450));
    List<Reserve> reserveList = signService.selectSignInDelay(time);
    for (Reserve reserve : reserveList) {
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
    // 查找未签退的reserve
    Timestamp time = Timestamp.valueOf(LocalDateTime.now().plusMinutes(475));
    List<Reserve> reserveList = signService.selectSignOutDelay(time);
    for (Reserve reserve : reserveList) {
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

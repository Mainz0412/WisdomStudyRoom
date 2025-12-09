package com.huawei.web.controller;

import com.huawei.web.entity.Log;
import com.huawei.web.entity.User;
import com.huawei.web.service.LogService;
import com.huawei.web.service.UserService;
import com.huawei.web.util.AjaxResult;
import com.huawei.web.util.constant.Constant;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

/**
 * @author Yi Chuizhou
 */
@RestController
@RequestMapping("/log")
public class LogController {
  @Resource LogService logService;
  @Resource UserService userService;

  /**
   * 插入日志
   *
   * @param log 日志
   * @return 添加成功
   */
  @PostMapping("/insert")
  @ResponseBody
  public AjaxResult insertLog(@RequestBody Log log) {
    log.setLogTime(Timestamp.valueOf(LocalDateTime.now().plusMinutes(480)));
    userService.addIllegal(log.getUserAccount());
    logService.insertLog(log);
    return AjaxResult.success();
  }

  /**
   * 更新日志
   *
   * @param log 日志
   * @return 更新成功
   */
  @PutMapping("/update")
  @ResponseBody
  public AjaxResult updateLog(@RequestBody Log log) {
    logService.updateLog(log);
    return AjaxResult.success();
  }

  /**
   * 删除日志
   *
   * @param log 日志
   * @return 删除成功
   */
  @DeleteMapping("/delete")
  public AjaxResult deleteLog(@RequestBody Log log) {
    logService.deleteLog(log);
    return AjaxResult.success();
  }

  /**
   * 获得所有日志list
   *
   * @return 日志list
   */
  @GetMapping(value = "/all")
  public List<Log> listAll() {
    return logService.selectAllList();
  }

  /**
   * 获得用户账号对应的日志list
   *
   * @param userAccount 用户账号
   * @return 日志list
   */
  @GetMapping(value = "/select")
  public List<Log> listUserLog(@RequestParam("userAccount") String userAccount) {
    return logService.selectList(userAccount);
  }

  /** 到时间自动解除封禁 */
  @Scheduled(cron = "0/10 * * * * ?")
  public void autoUnblock() {
    // 查找未签退的reserve
    Timestamp time = Timestamp.valueOf(LocalDateTime.now().plusMinutes(480));
    List<User> userList = userService.selectUnblock(time);
    for (User user : userList) {
      user.setUserIllegalState(Constant.UNBAN);
      userService.updateUser(user);
    }
  }
}

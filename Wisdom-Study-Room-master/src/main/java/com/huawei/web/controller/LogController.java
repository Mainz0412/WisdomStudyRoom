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
    // 先根据账号进行违规计数（仍使用账号作为业务输入），
    // 然后将对应的 userId 填回日志实体再插入到 log 表（log 表存储 user_id）
    userService.addIllegal(log.getUserAccount());
    User u = userService.selectUserAccount(log.getUserAccount());
    if (u != null) {
      log.setUserId(u.getUserId());
    }
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
    // 在删除日志前，尝试回退对应用户的违规计数（如果存在关联用户）
    try {
      if (log.getLogId() != null) {
        Log exist = logService.selectById(log.getLogId());
        if (exist != null) {
          // 优先使用 userId，如果不存在则尝试通过 userAccount 回退
          if (exist.getUserId() != null) {
            com.huawei.web.entity.User u = userService.selectUserById(exist.getUserId());
            if (u != null) {
              userService.decreaseIllegal(u.getUserAccount());
            }
          } else if (exist.getUserAccount() != null) {
            userService.decreaseIllegal(exist.getUserAccount());
          }
        }
      }
    } catch (Exception e) {
      // 保持容错性：若回退失败也尽量继续删除日志并返回结果
      e.printStackTrace();
    }

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
    // 返回时将 user_id 转换为 userAccount，便于前端显示
    List<Log> logs = logService.selectAllList();
    for (Log l : logs) {
      if (l.getUserId() != null) {
        User u = userService.selectUserById(l.getUserId());
        if (u != null) {
          l.setUserAccount(u.getUserAccount());
        }
      }
    }
    return logs;
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

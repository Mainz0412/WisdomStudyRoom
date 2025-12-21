package com.huawei.web.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huawei.web.entity.Log;
import com.huawei.web.entity.User;
import com.huawei.web.mapper.LogMapper;
import com.huawei.web.mapper.UserMapper;
import java.util.Collections;
import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author Yi Chuizhou
 */
@Service
public class LogService {
  @Resource LogMapper logMapper;

  @Resource UserMapper userMapper;

  /**
   * 插入日志
   *
   * @param log 日志
   */
  public void insertLog(Log log) {
    logMapper.insert(log);
  }

  /**
   * 更新预约信息
   *
   * @param log 预约信息
   */
  public void updateLog(Log log) {
    logMapper.update(log, Wrappers.<Log>lambdaQuery().eq(Log::getLogId, log.getLogId()));
  }

  /**
   * 删除预约信息
   *
   * @param log 预约信息
   */
  public void deleteLog(Log log) {
    logMapper.delete(Wrappers.<Log>lambdaQuery().eq(Log::getLogId, log.getLogId()));
  }

  /**
   * 查询用户预约信息
   *
   * @param userAccount 用户
   * @return 预约列表
   */
  public List<Log> selectList(String userAccount) {
    // 根据账号查用户 id，再按 user_id 查询日志表（log 表仅保存 user_id）
    User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUserAccount, userAccount));
    if (user == null) {
      return Collections.emptyList();
    }
    return logMapper.selectList(Wrappers.<Log>lambdaQuery().eq(Log::getUserId, user.getUserId()));
  }

  /**
   * 查询所有用户预约信息
   *
   * @return 预约列表
   */
  public List<Log> selectAllList() {
    return logMapper.selectList(Wrappers.lambdaQuery());
  }

  /**
   * 根据 id 查询日志
   *
   * @param id 日志 id
   * @return 日志或 null
   */
  public Log selectById(Integer id) {
    return logMapper.selectById(id);
  }
}

package com.huawei.web.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huawei.web.entity.Log;
import com.huawei.web.mapper.LogMapper;
import com.huawei.web.mapper.UserMapper;
import java.util.List;
import javax.annotation.Resource;
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
    return logMapper.selectList(Wrappers.<Log>lambdaQuery().eq(Log::getUserAccount, userAccount));
  }

  /**
   * 查询所有用户预约信息
   *
   * @return 预约列表
   */
  public List<Log> selectAllList() {
    return logMapper.selectList(Wrappers.lambdaQuery());
  }
}

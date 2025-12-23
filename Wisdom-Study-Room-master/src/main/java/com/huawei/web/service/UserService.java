package com.huawei.web.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huawei.web.entity.User;
import com.huawei.web.mapper.UserMapper;
import com.huawei.web.util.constant.Constant;

import jakarta.annotation.Resource;

/**
 * @author Yi Chuizhou
 */
@Service
public class UserService {
  @Resource
  UserMapper userMapper;

  /**
   * 根据用户名查询
   *
   * @param user 用户
   * @return 用户
   */
  public User selectUserName(User user) {
    return userMapper.selectOne(
        Wrappers.<User>lambdaQuery().eq(User::getUserName, user.getUserName()));
  }

  /**
   * 根据用户名查询(排除自己)
   *
   * @param user 用户
   * @return 用户
   */
  public User selectUserNameExclude(User user) {
    return userMapper.selectOne(
        Wrappers.<User>lambdaQuery()
            .eq(User::getUserName, user.getUserName())
            .ne(User::getUserId, user.getUserId()));
  }

  /**
   * 插入用户
   *
   * @param user 用户
   */
  public void insertUser(User user) {
    // 加密密码
    user.setUserPassword(cn.hutool.crypto.digest.BCrypt.hashpw(user.getUserPassword()));
    userMapper.insert(user);
  }

  /**
   * 用户登录 (包含懒迁移逻辑)
   *
   * @param user 用户
   * @return 用户
   */
  public User login(User user) {
    // 1. 先根据账号查询用户
    User dbUser = userMapper.selectOne(
        Wrappers.<User>lambdaQuery().eq(User::getUserAccount, user.getUserAccount()));

    if (dbUser == null) {
      return null;
    }

    // 2. 尝试匹配加密密码
    if (cn.hutool.crypto.digest.BCrypt.checkpw(user.getUserPassword(), dbUser.getUserPassword())) {
      return dbUser;
    }

    // 3. 尝试匹配明文密码 (懒迁移)
    if (user.getUserPassword().equals(dbUser.getUserPassword())) {
      // 匹配成功，说明是老用户，立即升级密码
      String newHash = cn.hutool.crypto.digest.BCrypt.hashpw(user.getUserPassword());
      dbUser.setUserPassword(newHash);
      updateUser(dbUser); // 更新数据库
      return dbUser;
    }

    // 4. 都不匹配，登录失败
    return null;
  }

  /**
   * 更新用户信息
   *
   * @param user 用户信息
   */
  public void updateUser(User user) {
    // 如果更新了密码，需要加密
    if (user.getUserPassword() != null && !user.getUserPassword().isEmpty()) {
      // 判断是否已经是加密格式(简单判断长度或前缀，或者直接重新加密)
      // 这里为了简单，假设前端传来的都是明文，直接加密。
      // 注意：如果前端传的是空，则不更新密码。
      // 但这里 update 接口可能包含密码字段。
      // 更好的做法是检查是否以 $2a$ 开头，但 Hutool BCrypt 没有直接的 isHashed 方法。
      // 既然是 update，通常是用户修改密码，所以默认视为明文进行加密。
      // 只有当密码长度 < 20 (BCrypt hash 通常 60 字符) 时才加密，防止重复加密
      if (user.getUserPassword().length() < 50) {
        user.setUserPassword(cn.hutool.crypto.digest.BCrypt.hashpw(user.getUserPassword()));
      }
    }

    // 构造更新条件
    com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<User> updateWrapper =
        Wrappers.<User>lambdaUpdate().eq(User::getUserId, user.getUserId());

    // 按需设置需要更新的字段（仅对非空字段做更新，避免无意覆盖），
    // 但封禁相关字段在解封场景下需要强制更新为 0/NULL。

    if (user.getUserName() != null) {
      updateWrapper.set(User::getUserName, user.getUserName());
    }

    if (user.getUserPassword() != null && !user.getUserPassword().isEmpty()) {
      updateWrapper.set(User::getUserPassword, user.getUserPassword());
    }

    if (user.getUserPrivilege() != null) {
      updateWrapper.set(User::getUserPrivilege, user.getUserPrivilege());
    }

    // 是否封禁字段：如果前端传了，就按传入值更新
    boolean explicitlyUnban =
        user.getUserIllegalState() != null && Constant.UNBAN.equals(user.getUserIllegalState());
    if (user.getUserIllegalState() != null) {
      updateWrapper.set(User::getUserIllegalState, user.getUserIllegalState());
    }

    // 解封场景：同时重置违规次数并清空封禁时间，这里需要“强制写入”0 和 NULL
    if (explicitlyUnban) {
      Integer count = user.getUserIllegal();
      if (count == null || count >= Constant.ILLEGAL_LIMIT) {
        count = 0;
      }
      updateWrapper.set(User::getUserIllegal, count);
      // 这里即便是 null 也要 set，确保数据库字段被更新为 NULL
      updateWrapper.set(User::getUserIllegalDate, null);
    } else {
      // 非解封场景，只在前端显式传值时更新对应字段
      if (user.getUserIllegal() != null) {
        updateWrapper.set(User::getUserIllegal, user.getUserIllegal());
      }
      if (user.getUserIllegalDate() != null) {
        updateWrapper.set(User::getUserIllegalDate, user.getUserIllegalDate());
      }
    }

    userMapper.update(null, updateWrapper);
  }

  /**
   * 删除用户
   *
   * @param user 用户
   */
  public void deleteUser(User user) {
    userMapper.delete(Wrappers.<User>lambdaQuery().eq(User::getUserId, user.getUserId()));
  }

  /**
   * 查询用户信息
   *
   * @param userAccount 用户名
   * @return 用户列表
   */
  public List<User> selectUserList(String userAccount) {
    return userMapper.selectList(
        Wrappers.<User>lambdaQuery().eq(User::getUserAccount, userAccount));
  }

  /**
   * 查找所有用户信息
   *
   * @return 所有用户信息
   */
  public List<User> selectAllList() {
    return userMapper.selectList(Wrappers.lambdaQuery());
  }

  /**
   * 查找Account
   *
   * @param userAccount 用户账户
   */
  public User selectUserAccount(String userAccount) {
    return userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUserAccount, userAccount));
  }

  /**
   * 根据ID查找用户
   *
   * @param userId 用户ID
   * @return 用户
   */
  public User selectUserById(Integer userId) {
    return userMapper.selectById(userId);
  }

  /**
   * 违规状态添加
   *
   * @param userAccount 用户账号
   */
  public void addIllegal(String userAccount) {
    User user = selectUserAccount(userAccount);
    user.setUserIllegal(user.getUserIllegal() + 1);
    if (user.getUserIllegal() >= Constant.ILLEGAL_LIMIT) {
      user.setUserIllegalState(Constant.BAN);
      // 为便于测试，可使用分钟级别的封禁时长（默认由 Constant.BAN_MINUTES 控制）
      Timestamp time = Timestamp.valueOf(
          LocalDateTime.now().plusMinutes(Constant.BAN_MINUTES).plusMinutes(480));
      user.setUserIllegalDate(time);
    }
    updateUser(user);
  }

  /**
   * 减少用户的违规计数（用于撤销日志或人工调整）
   *
   * @param userAccount 用户账号
   */
  public void decreaseIllegal(String userAccount) {
    User user = selectUserAccount(userAccount);
    if (user == null) return;
    Integer count = user.getUserIllegal();
    if (count == null || count <= 0) {
      user.setUserIllegal(0);
    } else {
      user.setUserIllegal(count - 1);
    }

    // 如果违规次数降到阈值以下，同步解封
    if (user.getUserIllegal() < Constant.ILLEGAL_LIMIT) {
      user.setUserIllegalState(Constant.UNBAN);
      user.setUserIllegalDate(null);
    }
    updateUser(user);
  }

  /**
   * 找封禁时间已到的用户
   *
   * @param time 时间
   * @return User列表
   */
  public List<User> selectUnblock(Timestamp time) {
    return userMapper.selectList(Wrappers.<User>lambdaQuery().le(User::getUserIllegalDate, time));
  }
}

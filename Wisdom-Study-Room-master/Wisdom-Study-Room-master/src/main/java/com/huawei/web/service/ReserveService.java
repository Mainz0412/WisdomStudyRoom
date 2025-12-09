package com.huawei.web.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huawei.web.entity.Reserve;
import com.huawei.web.mapper.ReserveMapper;
import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author Yi Chuizhou
 */
@Service
public class ReserveService {
  @Resource
  ReserveMapper reserveMapper;
  @Resource
  UserService userService;

  /**
   * 插入预约信息
   *
   * @param reserve 预约信息
   */
  public void insertReserve(Reserve reserve) {
    if (reserve.getUserId() == null && reserve.getReserveUserAccount() != null) {
      com.huawei.web.entity.User user = userService.selectUserAccount(reserve.getReserveUserAccount());
      if (user != null) {
        reserve.setUserId(user.getUserId());
      }
    }
    reserveMapper.insert(reserve);
  }

  /**
   * 更新预约信息
   *
   * @param reserve 预约信息
   */
  public void updateReserve(Reserve reserve) {
    if (reserve.getUserId() == null && reserve.getReserveUserAccount() != null) {
      com.huawei.web.entity.User user = userService.selectUserAccount(reserve.getReserveUserAccount());
      if (user != null) {
        reserve.setUserId(user.getUserId());
      }
    }
    reserveMapper.update(
        reserve, Wrappers.<Reserve>lambdaQuery().eq(Reserve::getReserveId, reserve.getReserveId()));
  }

  /**
   * 删除预约信息
   *
   * @param reserve 预约信息
   */
  public void deleteReserve(Reserve reserve) {
    reserveMapper.delete(
        Wrappers.<Reserve>lambdaQuery().eq(Reserve::getReserveId, reserve.getReserveId()));
  }

  /**
   * @author Binhui Liu
   */
  /**
   * 查询用户预约信息
   *
   * @param userAccount 用户
   * @return 预约列表
   */
  public List<Reserve> selectList(String userAccount) {
    com.huawei.web.entity.User user = userService.selectUserAccount(userAccount);
    if (user == null) {
      return java.util.Collections.emptyList();
    }
    List<Reserve> list = reserveMapper.selectList(
        Wrappers.<Reserve>lambdaQuery().eq(Reserve::getUserId, user.getUserId()));

    // 填充 userAccount
    for (Reserve r : list) {
      r.setReserveUserAccount(userAccount);
    }
    return list;
  }

  /**
   * @author Ruijie Zhao
   */
  /**
   * 查找所有预约信息
   *
   * @return 所有预约信息
   */
  public List<Reserve> selectAllList() {
    List<Reserve> list = reserveMapper.selectList(Wrappers.lambdaQuery());
    // 批量填充 userAccount 避免 N+1
    // 为简单起见，这里先循环查询，或者查询所有用户做映射
    // 考虑到数据量不大，循环查询尚可，或者查询所有用户
    java.util.List<com.huawei.web.entity.User> users = userService.selectAllList();
    java.util.Map<Integer, String> userMap = users.stream()
        .collect(java.util.stream.Collectors.toMap(com.huawei.web.entity.User::getUserId,
            com.huawei.web.entity.User::getUserAccount));

    for (Reserve r : list) {
      r.setReserveUserAccount(userMap.get(r.getUserId()));
    }
    return list;
  }
}

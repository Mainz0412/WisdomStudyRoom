package com.huawei.web.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huawei.web.entity.Reserve;
import com.huawei.web.entity.Seat;
import com.huawei.web.mapper.ReserveMapper;
import com.huawei.web.mapper.SeatMapper;

import jakarta.annotation.Resource;

/**
 * @author Yi Chuizhou
 */
@Service
public class ReserveService {
  @Resource
  ReserveMapper reserveMapper;
  @Resource
  UserService userService;
  @Resource
  SeatMapper seatMapper;

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
    // 确保 reserve.reserveSeatNumber 存放的是 seat_id（DB 外键）
    if (reserve.getReserveSeatNumber() != null) {
      // 尝试按 seat_id 查找
      Seat seat = seatMapper.selectById(reserve.getReserveSeatNumber());
      if (seat == null && reserve.getReserveRoomId() != null) {
        // 尝试按 seat_number 字符串精确匹配
        try {
          String seatStr = String.valueOf(reserve.getReserveSeatNumber());
          seat = seatMapper.selectOne(
              Wrappers.<Seat>lambdaQuery().eq(Seat::getSeatNumber, seatStr).eq(Seat::getRoomId, reserve.getReserveRoomId()));
        } catch (Exception ignored) {
        }
        // 尝试按房间内 seat_number 尾部数字匹配
        if (seat == null) {
          java.util.List<Seat> seats = seatMapper.selectList(Wrappers.<Seat>lambdaQuery().eq(Seat::getRoomId, reserve.getReserveRoomId()));
          for (Seat s : seats) {
            try {
              String sNum = s.getSeatNumber();
              if (sNum == null) continue;
              java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)$").matcher(sNum);
              if (m.find()) {
                int parsed = Integer.parseInt(m.group(1));
                if (parsed == reserve.getReserveSeatNumber()) {
                  seat = s;
                  break;
                }
              }
            } catch (Exception e) {
              // continue
            }
          }
        }
      }
      if (seat == null) {
        throw new IllegalArgumentException("未找到该座位，请确认座位号与自习室匹配");
      }
      // 将 reserve.reserveSeatNumber 设置为实际 seat_id
      reserve.setReserveSeatNumber(seat.getSeatId());
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
   * 根据预约 ID 查询预约信息
   *
   * @param reserveId 预约ID
   * @return 预约信息，若不存在则返回 null
   */
  public Reserve selectById(Integer reserveId) {
    if (reserveId == null) {
      return null;
    }
    return reserveMapper.selectById(reserveId);
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
      // 填充座位展示标签
      if (r.getReserveSeatNumber() != null) {
        Seat s = seatMapper.selectById(r.getReserveSeatNumber());
        if (s != null) {
          r.setReserveSeatLabel(s.getSeatNumber());
        }
      }
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
      // 填充座位展示标签
      if (r.getReserveSeatNumber() != null) {
        Seat s = seatMapper.selectById(r.getReserveSeatNumber());
        if (s != null) {
          r.setReserveSeatLabel(s.getSeatNumber());
        }
      }
    }
    return list;
  }
}

package com.huawei.web.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huawei.web.entity.Reserve;
import com.huawei.web.entity.Seat;
import com.huawei.web.entity.SeatTime;
import com.huawei.web.mapper.SeatMapper;
import com.huawei.web.mapper.SeatTimeMapper;
import java.sql.Timestamp;
import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author Yi Chuizhou
 */
@Service
public class SeatTimeService {
  @Resource SeatTimeMapper seatTimeMapper;
  @Resource SeatMapper seatMapper;

  /**
   * 查询时间是否冲突
   *
   * @param seatTimeList 时间序列
   * @param reserve 预约信息
   * @return 是否冲突
   */
  public boolean checkSeat(List<SeatTime> seatTimeList, Reserve reserve) {
    Timestamp begin = reserve.getReserveTimeBegin();
    Timestamp end = reserve.getReserveTimeEnd();
    // 表示座位时间是否冲突
    boolean flag = false;
    for (SeatTime seatTime : seatTimeList) {
      // 跳过与自己相同的预约 id（防止更新时误判）
      if (reserve.getReserveId() != null && reserve.getReserveId().equals(seatTime.getReserveId())) {
        continue;
      }
      // 大于0代表没有重叠
      int result1 = begin.compareTo(seatTime.getEndTime());
      // 小于0代表没有重叠
      int result2 = end.compareTo(seatTime.getBeginTime());
      // 发生重叠
      if (!(result1 > 0 || result2 < 0)) {
        flag = true;
        break;
      }
    }
    return flag;
  }

  /**
   * 占位
   *
   * @param reserve 预约信息
   * @return 是否成功
   */
  public SeatTime occupySeat(Reserve reserve) {
    // 找到对应的座椅
    Seat seat = findSeat(reserve);
    if (seat == null) {
      throw new IllegalArgumentException("未找到该座位，请确认座位号与自习室匹配");
    }
    List<SeatTime> seatTimeList = selectSeatTimeList(seat);
    if (checkSeat(seatTimeList, reserve)) {
      return null;
    }
    // 创建座椅时间
    SeatTime seatTime = new SeatTime();
    seatTime.setSeatId(seat.getSeatId());
    seatTime.setReserveId(reserve.getReserveId());
    seatTime.setBeginTime(reserve.getReserveTimeBegin());
    seatTime.setEndTime(reserve.getReserveTimeEnd());
    seatTimeMapper.insert(seatTime);
    return seatTime;
  }

  /**
   * 根据预约信息查找座椅
   *
   * @param reserve 预约信息
   * @return 座椅
   */
  public Seat findSeat(Reserve reserve) {
    // 支持前端传入多种座位表示：
    // 1) 直接传入 seat_id (整数)
    // 2) 传入座位编号字符串（如 "A-104" 或 "104"）
    // 3) 传入数字（104）但 DB 中 seat_number 为 "A-104"，尝试按数字后缀匹配
    if (reserve == null) {
      return null;
    }

    Integer seatVal = reserve.getReserveSeatNumber();
    Integer roomId = reserve.getReserveRoomId();

    // 1) 尝试按 seat_id 查找
    if (seatVal != null) {
      Seat byId = seatMapper.selectById(seatVal);
      if (byId != null && (roomId == null || byId.getRoomId().equals(roomId))) {
        return byId;
      }
    }

    // 2) 尝试按 seat_number 精确匹配（将 seatVal 当作字符串）
    try {
      String seatStr = reserve.getReserveSeatNumber() == null ? null : String.valueOf(reserve.getReserveSeatNumber());
      if (seatStr != null) {
        Seat byNumber = seatMapper.selectOne(
            Wrappers.<Seat>lambdaQuery().eq(Seat::getSeatNumber, seatStr).eq(Seat::getRoomId, roomId));
        if (byNumber != null) {
          return byNumber;
        }
      }
    } catch (Exception ignored) {
      // ignore
    }

    // 3) 尝试在指定房间中按 seat_number 的尾部数字匹配
    if (roomId != null) {
      java.util.List<Seat> seats = seatMapper.selectList(Wrappers.<Seat>lambdaQuery().eq(Seat::getRoomId, roomId));
      if (seats != null && !seats.isEmpty() && seatVal != null) {
        for (Seat s : seats) {
          try {
            String sNum = s.getSeatNumber();
            if (sNum == null) continue;
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)$").matcher(sNum);
            if (m.find()) {
              int parsed = Integer.parseInt(m.group(1));
              if (parsed == seatVal) {
                return s;
              }
            }
          } catch (Exception e) {
            // continue
          }
        }
      }
    }

    return null;
  }

  /**
   * 释放预约
   *
   * @param seatTime 座位时间
   */
  public void releaseSeat(SeatTime seatTime) {
    seatTimeMapper.delete(
        Wrappers.<SeatTime>lambdaQuery().eq(SeatTime::getSeatTimeId, seatTime.getSeatTimeId()));
  }

  /**
   * 删除座位预约
   *
   * @param reserve 预约信息
   */
  public void deleteSeat(Reserve reserve) {
    seatTimeMapper.delete(
        Wrappers.<SeatTime>lambdaQuery().eq(SeatTime::getReserveId, reserve.getReserveId()));
  }

  /**
   * 查询座位时间列表
   *
   * @param seat 座椅
   * @return 座位时间列表
   */
  public List<SeatTime> selectSeatTimeList(Seat seat) {
    if (seat == null) {
      throw new IllegalArgumentException("未找到该座位");
    }
    return seatTimeMapper.selectList(
        Wrappers.<SeatTime>lambdaQuery().eq(SeatTime::getSeatId, seat.getSeatId()));
  }

  /**
   * 更新座椅的reserve的id值
   *
   * @param reserve 预约
   * @param seatTime 座椅时间
   */
  public void updateSeatReserve(Reserve reserve, SeatTime seatTime) {
    seatTime.setReserveId(reserve.getReserveId());
    seatTimeMapper.update(
        seatTime,
        Wrappers.<SeatTime>lambdaQuery().eq(SeatTime::getSeatTimeId, seatTime.getSeatTimeId()));
  }

  /**
   * 找到预约对应的座位时间
   *
   * @param reserve 预约信息
   * @return 座位时间
   */
  public SeatTime findSeatTime(Reserve reserve) {
    return seatTimeMapper.selectOne(
        Wrappers.<SeatTime>lambdaQuery().eq(SeatTime::getReserveId, reserve.getReserveId()));
  }

  /**
   * @author Binhui Liu
   */

  /**
   * 查找所有座位时间信息
   *
   * @return 所有座位时间信息
   */
  public List<SeatTime> selectAllList() {
    return seatTimeMapper.selectList(Wrappers.lambdaQuery());
  }
}

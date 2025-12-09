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
      // 和原预约的id值相同
      if (seatTime.getReserveId().equals(reserve.getReserveId())) {
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
    return seatMapper.selectOne(
        Wrappers.<Seat>lambdaQuery()
            .eq(Seat::getSeatNumber, reserve.getReserveSeatNumber())
            .eq(Seat::getRoomId, reserve.getReserveRoomId()));
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

package com.huawei.web.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huawei.web.entity.Seat;
import com.huawei.web.mapper.SeatMapper;
import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author Yi Chuizhou
 */
@Service
public class SeatService {
  @Resource SeatMapper seatMapper;

  /**
   * 查询房间Id
   *
   * @param seat 座位
   * @return 房间
   */
  public Seat selectRoomId(Seat seat) {
    return seatMapper.selectOne(Wrappers.<Seat>lambdaQuery().eq(Seat::getRoomId, seat.getRoomId()));
  }

  /**
   * 插入座位
   *
   * @param seat 座位
   */
  public void insertSeat(Seat seat) {
    seatMapper.insert(seat);
  }

  /**
   * 删除座位
   *
   * @param seat 座位
   */
  public void deleteSeat(Seat seat) {
    seatMapper.delete(Wrappers.<Seat>lambdaQuery().eq(Seat::getSeatId, seat.getSeatId()));
  }

  /**
   * 更新座位信息
   *
   * @param seat 座位
   */
  public void updateSeat(Seat seat) {
    seatMapper.update(seat, Wrappers.<Seat>lambdaQuery().eq(Seat::getSeatId, seat.getSeatId()));
  }

  /**
   * 查询座位信息
   *
   * @param seatNumber 座位号
   * @return 座位列表
   */
  public List<Seat> selectSeatList(Integer seatNumber) {
    return seatMapper.selectList(Wrappers.<Seat>lambdaQuery().eq(Seat::getSeatNumber, seatNumber));
  }

  /**
   * 查找所有座位信息
   *
   * @return 所有座位信息
   */
  public List<Seat> selectAllList() {
    return seatMapper.selectList(Wrappers.lambdaQuery());
  }
}

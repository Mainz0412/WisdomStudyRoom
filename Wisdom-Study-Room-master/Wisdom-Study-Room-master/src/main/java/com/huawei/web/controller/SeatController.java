package com.huawei.web.controller;

import com.huawei.web.entity.Reserve;
import com.huawei.web.entity.Room;
import com.huawei.web.entity.Seat;
import com.huawei.web.entity.SeatTime;
import com.huawei.web.service.RoomService;
import com.huawei.web.service.SeatService;
import com.huawei.web.service.SeatTimeService;
import com.huawei.web.util.AjaxResult;
import com.huawei.web.util.constant.Constant;
import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author Yi Chuizhou
 */
@RestController
@RequestMapping("/seat")
public class SeatController {
  @Resource SeatTimeService seatTimeService;
  @Resource SeatService seatService;
  @Resource RoomService roomService;

  /**
   * 查询座位时间表
   *
   * @param seat 座位
   * @return 座椅时间表
   */
  @GetMapping("/list")
  @ResponseBody
  public List<SeatTime> findSeatTimeList(Seat seat) {
    return seatTimeService.selectSeatTimeList(seat);
  }

  /**
   * 查找某个座位是否空闲
   *
   * @param reserve 预约信息（包含时间，座位）
   * @return 是否空闲
   */
  @PostMapping("/check")
  @ResponseBody
  public AjaxResult checkSeatOccupy(@RequestBody Reserve reserve) {
    Seat seat = seatTimeService.findSeat(reserve);
    List<SeatTime> list = seatTimeService.selectSeatTimeList(seat);
    // 占用是1，未占用是0
    String code =
        seatTimeService.checkSeat(list, reserve) ? Constant.SEAT_RELEASE : Constant.SEAT_OCCUPY;
    return AjaxResult.success(code);
  }

  /**
   * 获得所有座位时间list
   *
   * @return 座位时间list
   */
  @GetMapping(value = "/allseattime")
  public List<SeatTime> listAllSeatTime() {
    return seatTimeService.selectAllList();
  }

  /**
   * 添加座位
   *
   * @param seat 座位
   * @return msg信息
   */
  @PostMapping("/insert")
  public AjaxResult insert(@RequestBody Seat seat) {
    // 检查房间是否存在
    Room room = roomService.selectRoomId(seat.getRoomId());
    if (room == null) {
      return AjaxResult.error("房间不存在");
    }
    // 检查座位号是否重复
    List<Seat> seatsInRoom = seatService.selectSeatList(seat.getRoomId());
    for (Seat existingSeat : seatsInRoom) {
      if (existingSeat.getSeatNumber().equals(seat.getSeatNumber())) {
        return AjaxResult.error("座位号重复");
      }
    }
    // 添加座位
    seatService.insertSeat(seat);
    return AjaxResult.success();
  }

  /**
   * 更新房座位信息
   *
   * @param seat 座位信息
   * @return msg结果
   */
  @PutMapping("/update")
  public AjaxResult update(@RequestBody Seat seat) {
    // 检查房间是否存在
    Room room = roomService.selectRoomId(seat.getRoomId());
    if (room == null) {
      return AjaxResult.error("房间不存在");
    }
    // 检查座位号是否重复
    List<Seat> seatsInRoom = seatService.selectSeatList(seat.getRoomId());
    for (Seat existingSeat : seatsInRoom) {
      if (existingSeat.getSeatNumber().equals(seat.getSeatNumber())) {
        return AjaxResult.error("座位号重复");
      }
    }
    // 更新结果
    seatService.updateSeat(seat);
    return AjaxResult.success();
  }

  /**
   * 删除座位
   *
   * @param seat 座位
   * @return msg信息
   */
  @DeleteMapping("/delete")
  public AjaxResult delete(@RequestBody Seat seat) {
    // 删除座位
    seatService.deleteSeat(seat);
    return AjaxResult.success();
  }

  /**
   * 获得座位list
   *
   * @param seatNumber 座位
   * @return 座位list
   */
  @GetMapping(value = "/seatlist")
  public List<Seat> list(@RequestParam("seatNumber") Integer seatNumber) {
    return seatService.selectSeatList(seatNumber);
  }

  /**
   * 获得所有座位list
   *
   * @return 座位list
   */
  @GetMapping(value = "/allseat")
  public List<Seat> listAllSeat() {
    return seatService.selectAllList();
  }
}

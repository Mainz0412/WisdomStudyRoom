package com.huawei.web.controller;

import com.huawei.web.entity.Reserve;
import com.huawei.web.entity.SeatTime;
import com.huawei.web.service.ReserveService;
import com.huawei.web.service.SeatTimeService;
import com.huawei.web.util.AjaxResult;
import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for reservation (booking) operations.
 *
 * <p>提供新增、更新、删除及查询预约的接口。
 *
 * @author Yi Chuizhou
 * @since 1.0
 */
@RestController
@RequestMapping("/reserve")
public class ReserveController {

  @Resource ReserveService reserveService;

  @Resource SeatTimeService seatTimeService;

  /**
   * 新增预约。
   *
   * @param reserve 预约信息
   * @return 操作结果
   */
  @PostMapping("/add")
  public AjaxResult reserve(@RequestBody Reserve reserve) {
    SeatTime seatTime = seatTimeService.occupySeat(reserve);
    if (seatTime == null) {
      return AjaxResult.error("座位已被占用");
    }

    reserveService.insertReserve(reserve);
    seatTimeService.updateSeatReserve(reserve, seatTime);
    return AjaxResult.success();
  }

  /**
   * 更新预约信息。
   *
   * @param reserve 预约信息
   * @return 操作结果
   */
  @PutMapping("/update")
  public AjaxResult update(@RequestBody Reserve reserve) {
    SeatTime preSeatTime = seatTimeService.findSeatTime(reserve);

    SeatTime seatTime = seatTimeService.occupySeat(reserve);
    if (seatTime == null) {
      return AjaxResult.error("座位已被占用");
    }

    seatTimeService.releaseSeat(preSeatTime);
    reserveService.updateReserve(reserve);
    return AjaxResult.success();
  }

  /**
   * 删除预约。
   *
   * @param reserve 预约信息
   * @return 操作结果
   */
  @DeleteMapping("/delete")
  public AjaxResult delete(@RequestBody Reserve reserve) {
    seatTimeService.deleteSeat(reserve);
    reserveService.deleteReserve(reserve);
    return AjaxResult.success();
  }

  /**
   * 查询指定用户的预约列表。
   *
   * @param userAccount 用户账号
   * @return 该用户的预约列表
   */
  @GetMapping(value = "/list")
  public List<Reserve> list(@RequestParam("userAccount") String userAccount) {
    return reserveService.selectList(userAccount);
  }

  /**
   * 查询所有预约。
   *
   * @return 所有预约列表
   */
  @GetMapping(value = "/all")
  public List<Reserve> listAll() {
    return reserveService.selectAllList();
  }
}

package com.huawei.web.controller;

import com.huawei.web.entity.Room;
import com.huawei.web.service.RoomService;
import com.huawei.web.util.AjaxResult;
import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for room management.
 *
 * <p>提供房间的增删改查接口。
 *
 * @author Yi Chuizhou
 * @since 1.0
 */
@RestController
@RequestMapping("/room")
public class RoomController {

  @Resource RoomService roomService;

  /**
   * 新增房间。
   *
   * @param room 房间对象
   * @return 操作结果
   */
  @PostMapping("/insert")
  public AjaxResult insert(@RequestBody Room room) {
    Room requestRoom = roomService.selectRoomName(room);
    if (requestRoom != null) {
      return AjaxResult.error("房间已存在");
    }

    roomService.insertRoom(room);
    return AjaxResult.success();
  }

  /**
   * 更新房间信息。
   *
   * @param room 待更新的房间信息
   * @return 操作结果
   */
  @PutMapping("/update")
  public AjaxResult update(@RequestBody Room room) {
    Room requestRoom = roomService.selectRoomName(room);
    if (requestRoom != null) {
      return AjaxResult.error("房间名重复");
    }

    roomService.updateRoom(room);
    return AjaxResult.success();
  }

  /**
   * 删除房间。
   *
   * @param room 房间对象（包含 id）
   * @return 操作结果
   */
  @DeleteMapping("/delete")
  public AjaxResult delete(@RequestBody Room room) {
    roomService.deleteRoom(room);
    return AjaxResult.success();
  }

  /**
   * 根据房间 ID 查询房间列表（通常返回单个或空）。
   *
   * @param roomId 房间 ID
   * @return 房间列表
   */
  @GetMapping(value = "/listbyid")
  public List<Room> list(@RequestParam("roomId") Integer roomId) {
    return roomService.selectRoomListById(roomId);
  }

  /**
   * 根据房间名称查询房间列表。
   *
   * @param roomName 房间名称
   * @return 房间列表
   */
  @GetMapping(value = "/listbyname")
  public List<Room> list(@RequestParam("roomName") String roomName) {
    return roomService.selectRoomListByName(roomName);
  }

  /**
   * 查询所有房间。
   *
   * @return 所有房间列表
   */
  @GetMapping(value = "/all")
  public List<Room> listAll() {
    return roomService.selectAllList();
  }
}

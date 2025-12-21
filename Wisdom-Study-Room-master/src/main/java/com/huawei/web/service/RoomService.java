package com.huawei.web.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huawei.web.entity.Room;
import com.huawei.web.entity.Seat;
import com.huawei.web.mapper.RoomMapper;
import com.huawei.web.mapper.SeatMapper;
import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author Yi Chuizhou
 */
@Service
public class RoomService {
  @Resource RoomMapper roomMapper;

  @Resource SeatMapper seatMapper;

  /**
   * 查找房间Id
   *
   * @param roomId 房间Id
   * @return 房间信息
   */
  public Room selectRoomId(Integer roomId) {
    return roomMapper.selectById(roomId);
  }

  /**
   * 查找房间
   *
   * @param room 房间
   */
  public Room selectRoomName(Room room) {
    return roomMapper.selectOne(
        Wrappers.<Room>lambdaQuery().eq(Room::getRoomName, room.getRoomName()));
  }

  /**
   * 插入房间
   *
   * @param room 房间
   */
  public void insertRoom(Room room) {
    roomMapper.insert(room);
  }

  /**
   * 删除房间
   *
   * @param room 房间
   */
  public void deleteRoom(Room room) {
    roomMapper.delete(Wrappers.<Room>lambdaQuery().eq(Room::getRoomId, room.getRoomId()));
    // 删除房间内的所有座位
    seatMapper.delete(Wrappers.<Seat>lambdaQuery().eq(Seat::getRoomId, room.getRoomId()));
  }

  /**
   * 更新房间信息
   *
   * @param room 房间
   */
  public void updateRoom(Room room) {
    roomMapper.update(room, Wrappers.<Room>lambdaQuery().eq(Room::getRoomId, room.getRoomId()));
  }

  /**
   * 根据房间Id查询房间信息
   *
   * @param roomId 房间名
   * @return 房间列表
   */
  public List<Room> selectRoomListById(Integer roomId) {
    return roomMapper.selectList(Wrappers.<Room>lambdaQuery().eq(Room::getRoomId, roomId));
  }

  /**
   * 根据房间Name查询房间信息
   *
   * @param roomName 房间名
   * @return 房间列表
   */
  public List<Room> selectRoomListByName(String roomName) {
    return roomMapper.selectList(Wrappers.<Room>lambdaQuery().eq(Room::getRoomName, roomName));
  }

  /**
   * 查找所有房间信息
   *
   * @return 所有房间信息
   */
  public List<Room> selectAllList() {
    return roomMapper.selectList(Wrappers.lambdaQuery());
  }
}

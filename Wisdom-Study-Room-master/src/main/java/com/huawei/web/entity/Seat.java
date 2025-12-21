package com.huawei.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Yi Chuizhou
 */
@TableName("seat")
@Data
public class Seat {
  @TableId(type = IdType.AUTO)
  private Integer seatId;

  @TableField("seat_number")
  private String seatNumber;

  @TableField("room_id")
  private Integer roomId;
}

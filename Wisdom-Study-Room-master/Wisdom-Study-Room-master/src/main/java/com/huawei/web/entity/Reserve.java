package com.huawei.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Timestamp;
import lombok.Data;

/**
 * @author Yi Chuizhou
 */
@TableName("reserve")
@Data
public class Reserve {
  @TableId(type = IdType.AUTO)
  private Integer reserveId;

  @TableField("user_id")
  private Integer userId;

  @TableField(exist = false)
  private String reserveUserAccount; // 不映射到数据库字段

  @TableField("seat_id")
  private Integer reserveSeatNumber; // 映射到 reserve.seat_id

  @TableField("room_id")
  private Integer reserveRoomId; // 映射到 reserve.room_id

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Timestamp reserveTimeBegin;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Timestamp reserveTimeEnd;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Timestamp timeSignIn;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Timestamp timeSignOut;

  private Integer reserveState;
}

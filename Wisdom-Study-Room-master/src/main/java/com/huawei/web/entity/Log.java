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
@TableName("log")
@Data
public class Log {
  @TableId(type = IdType.AUTO)
  private Integer logId;
  
  // 数据库中存储为外键 user_id，保留 userAccount 用于接口层显示（不映射到表）
  private Integer userId;

  @TableField(exist = false)
  private String userAccount;

  // 数据库列名为 log_type
  @TableField("log_type")
  private String logState;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Timestamp logTime;
}

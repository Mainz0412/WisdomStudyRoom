package com.huawei.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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

  private String userAccount;
  private String logState;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Timestamp logTime;
}

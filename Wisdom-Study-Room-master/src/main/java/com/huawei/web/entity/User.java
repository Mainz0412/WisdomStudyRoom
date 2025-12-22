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
@TableName("user")
@Data
public class User {
  @TableId(type = IdType.AUTO)
  private Integer userId;

  private String userAccount;
  private String userPassword;
  private String userName;
  private Integer userPrivilege;
  private Integer userIllegal;
  private Integer userIllegalState;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Timestamp userIllegalDate;
}

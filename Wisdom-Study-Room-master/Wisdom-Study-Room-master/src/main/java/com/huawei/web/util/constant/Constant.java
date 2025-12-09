package com.huawei.web.util.constant;

/**
 * @author Yi Chuizhou
 */
public class Constant {
  /** 座椅释放 */
  public static final String SEAT_RELEASE = "0";

  /** 座椅占有 */
  public static final String SEAT_OCCUPY = "1";

  /** 令牌 */
  public static final String TOKEN = "token";

  /** 正常状态 */
  public static final Integer SIGN_IN_NORMAL = 0;

  /** 签到超时 */
  public static final Integer SIGN_IN_TIMEOUT = 1;

  /** 签退超时 */
  public static final Integer SIGN_OUT_TIMEOUT = 2;

  /** 已签退 */
  public static final Integer SIGN_OUT = 3;

  /** 违规限制 */
  public static final Integer ILLEGAL_LIMIT = 3;

  /** 账号禁止 */
  public static final Integer BAN = 1;

  /** 账号未被禁止 */
  public static final Integer UNBAN = 0;

  /** 封禁天数 */
  public static final long BAN_DAY = 7;
}

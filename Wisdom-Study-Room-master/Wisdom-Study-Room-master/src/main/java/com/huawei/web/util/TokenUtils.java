package com.huawei.web.util;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.huawei.web.entity.User;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Yi Chuizhou
 */
@Slf4j
@Component
public class TokenUtils {

  // 实际生产中应从配置文件读取，这里为了演示使用固定强密钥
  private static String SECRET;

  @org.springframework.beans.factory.annotation.Value("${jwt.secret}")
  public void setSecret(String secret) {
    TokenUtils.SECRET = secret;
  }

  /**
   * 生成token
   *
   * @param user 用户
   * @return Token
   */
  public static String genToken(User user) {
    return JWT.create()
        .withExpiresAt(DateUtil.offsetDay(new Date(), 1))
        .withAudience(user.getUserAccount())
        .sign(Algorithm.HMAC256(SECRET));
  }

  /**
   * 验证token
   * 
   * @param token
   */
  public static void verify(String token) {
    JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
  }
}

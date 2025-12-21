package com.huawei;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point for the Spring Boot application.
 *
 * <p>DemoApplication 启动类。
 *
 * <p>不修改此类的行为，仅补充类说明以提高可读性。
 *
 * @author Yi Chuizhou
 * @since 1.0
 */
@SpringBootApplication
@MapperScan("com.huawei.web.mapper")
@EnableScheduling
public class DemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }
}

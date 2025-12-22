-- ----------------------------
-- 用户表
-- ----------------------------
CREATE TABLE `user` (
  `user_id` INT(11) PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `user_account` VARCHAR(50) NOT NULL UNIQUE COMMENT '账号',
  `user_password` VARCHAR(255) NOT NULL COMMENT '密码',
  `user_name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `user_privilege` TINYINT(1) DEFAULT 0 COMMENT '权限等级 (0-普通用户 1-管理员)',
  `user_illegal` INT(11) DEFAULT 0 COMMENT '违规次数',
  `user_illegal_state` TINYINT(1) DEFAULT 0 COMMENT '违规状态 (0-正常 1-封禁)',
  `user_illegal_date` TIMESTAMP NULL COMMENT '最近违规时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ----------------------------
-- 房间表
-- ----------------------------
CREATE TABLE `room` (
  `room_id` INT(11) PRIMARY KEY AUTO_INCREMENT COMMENT '房间ID',
  `room_name` VARCHAR(50) NOT NULL COMMENT '房间名称',
  INDEX `idx_room_name` (`room_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自习室信息表';

-- ----------------------------
-- 座位表（需先创建room表）
-- ----------------------------
CREATE TABLE `seat` (
  `seat_id` INT(11) PRIMARY KEY AUTO_INCREMENT COMMENT '座位ID',
  `room_id` INT(11) NOT NULL COMMENT '所属房间ID',
  `seat_number` VARCHAR(20) NOT NULL COMMENT '座位编号 (如A-101)',
  FOREIGN KEY (`room_id`) REFERENCES `room`(`room_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位信息表';

-- ----------------------------
-- 座位时段表（需先创建seat表）
-- ----------------------------
CREATE TABLE `seat_time` (
  `seat_time_id` INT(11) PRIMARY KEY AUTO_INCREMENT COMMENT '时段ID',
  `seat_id` INT(11) NOT NULL COMMENT '座位ID',
  `reserve_id` INT(11) NULL COMMENT '关联预约ID',
  `begin_time` TIMESTAMP NOT NULL COMMENT '开始时间',
  `end_time` TIMESTAMP NOT NULL COMMENT '结束时间',
  `reserved` TINYINT(1) DEFAULT 0 COMMENT '是否被预约 (0-空闲 1-已预约)',
  FOREIGN KEY (`seat_id`) REFERENCES `seat`(`seat_id`) ON DELETE CASCADE,
  INDEX `idx_time_range` (`begin_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位时段表';

-- ----------------------------
-- 预约记录表（需先创建user/room表）
-- ----------------------------
CREATE TABLE `reserve` (
  `reserve_id` INT(11) PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
  `user_id` INT(11) NOT NULL COMMENT '用户ID',
  `room_id` INT(11) NOT NULL COMMENT '房间ID',
  `seat_id` INT(11) NOT NULL COMMENT '座位ID',
  `reserve_time_begin` TIMESTAMP NOT NULL COMMENT '预约开始时间',
  `reserve_time_end` TIMESTAMP NOT NULL COMMENT '预约结束时间',
  `time_sign_in` TIMESTAMP NULL COMMENT '实际签到时间',
  `time_sign_out` TIMESTAMP NULL COMMENT '实际签退时间',
  `reserve_state` TINYINT(1) DEFAULT 0 COMMENT '状态 (0-未开始 1-进行中 2-已完成 3-已取消)',
  FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
  FOREIGN KEY (`room_id`) REFERENCES `room`(`room_id`) ON DELETE CASCADE,
  FOREIGN KEY (`seat_id`) REFERENCES `seat`(`seat_id`) ON DELETE CASCADE,
  INDEX `idx_reserve_time` (`reserve_time_begin`, `reserve_time_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约记录表';

-- ----------------------------
-- 操作日志表（需先创建user表）
-- ----------------------------
CREATE TABLE `log` (
  `log_id` INT(11) PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
  `user_id` INT(11) NOT NULL COMMENT '用户ID',
  `log_type` VARCHAR(20) NOT NULL COMMENT '操作类型 (登录/预约/取消)',
  `log_detail` TEXT COMMENT '操作详情',
  `log_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
  INDEX `idx_log_time` (`log_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表';
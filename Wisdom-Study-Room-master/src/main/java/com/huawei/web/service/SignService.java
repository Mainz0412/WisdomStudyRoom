package com.huawei.web.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huawei.web.entity.Reserve;
import com.huawei.web.mapper.ReserveMapper;
import com.huawei.web.util.constant.Constant;
import java.sql.Timestamp;
import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author Yi Chuizhou
 */
@Service
public class SignService {
  @Resource ReserveMapper reserveMapper;

  /**
   * 签到
   *
   * @param reserve 预约信息
   */
  public void signIn(Reserve reserve) {
    UpdateWrapper<Reserve> updateWrapper = new UpdateWrapper<>();
    updateWrapper.eq("reserve_id", reserve.getReserveId());
    updateWrapper.set("time_sign_in", reserve.getTimeSignIn());
    reserveMapper.update(reserve, updateWrapper);
  }

  /**
   * 签退
   *
   * @param reserve 预约信息
   */
  public void signOut(Reserve reserve) {
    UpdateWrapper<Reserve> updateWrapper = new UpdateWrapper<>();
    updateWrapper.eq("reserve_id", reserve.getReserveId());
    updateWrapper.set("time_sign_out", reserve.getTimeSignOut());
    reserve.setReserveState(Constant.SIGN_OUT);
    reserveMapper.update(reserve, updateWrapper);
  }

  /**
   * 查找超时签到预约
   *
   * @param time 当前时间加30分钟
   * @return 超时预约
   */
  public List<Reserve> selectSignInDelay(Timestamp time) {
    return reserveMapper.selectList(
        Wrappers.<Reserve>lambdaQuery()
            .le(Reserve::getReserveTimeBegin, time)
            .eq(Reserve::getReserveState, Constant.SIGN_IN_NORMAL));
  }

  /**
   * 查找过时签退预约
   *
   * @param time 当前时间
   * @return 超时预约
   */
  public List<Reserve> selectSignOutDelay(Timestamp time) {
    return reserveMapper.selectList(
        Wrappers.<Reserve>lambdaQuery()
            .le(Reserve::getReserveTimeEnd, time)
            .eq(Reserve::getReserveState, Constant.SIGN_IN_NORMAL));
  }
}

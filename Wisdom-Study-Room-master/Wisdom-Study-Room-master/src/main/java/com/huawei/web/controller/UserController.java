package com.huawei.web.controller;

import static com.huawei.web.util.TokenUtils.genToken;

import com.huawei.web.entity.User;
import com.huawei.web.service.UserService;
import com.huawei.web.util.AjaxResult;
import com.huawei.web.util.constant.Constant;
import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user operations.
 *
 * <p>
 * 提供用户注册、登录、更新、删除及查询接口。
 *
 * @author Yi Chuizhou
 * @since 1.0
 */
@RestController
@RequestMapping("/user")
public class UserController {

  @Resource
  UserService userService;

  /**
   * 用户注册。
   *
   * @param user 注册信息
   * @return 操作结果
   */
  @PostMapping("/register")
  public AjaxResult register(@RequestBody User user) {
    User requestUser = userService.selectUserName(user);
    User requestUser2 = userService.selectUserAccount(user.getUserAccount());
    if (requestUser != null || requestUser2 != null) {
      return AjaxResult.error("用户名或账号重复");
    }

    user.setUserPrivilege(0);
    userService.insertUser(user);
    return AjaxResult.success();
  }

  /**
   * 用户登录。
   *
   * @param user 登录信息（账号/密码）
   * @return 包含 token 或错误信息的结果
   */
  @PostMapping("/login")
  public AjaxResult login(@RequestBody User user) {
    AjaxResult ajax = AjaxResult.success();
    User loginUser = userService.login(user);
    if (loginUser == null) {
      return AjaxResult.error("用户名或密码错误");
    }
    String token = genToken(loginUser);
    ajax.put(Constant.TOKEN, token);
    ajax.put("privilege", loginUser.getUserPrivilege());
    return ajax;
  }

  /**
   * 更新用户信息。
   *
   * @param user 待更新的用户信息
   * @return 操作结果
   */
  @PutMapping("/update")
  public AjaxResult update(@RequestBody User user) {
    User requestUser = userService.selectUserNameExclude(user);
    if (requestUser != null) {
      return AjaxResult.error("用户名重复");
    }

    userService.updateUser(user);
    return AjaxResult.success();
  }

  /**
   * 删除用户。
   *
   * @param user 待删除的用户对象
   * @return 操作结果
   */
  @DeleteMapping("/delete")
  public AjaxResult delete(@RequestBody User user) {
    userService.deleteUser(user);
    return AjaxResult.success();
  }

  /**
   * 根据账号查询用户列表。
   *
   * @param userAccount 用户账号（可做模糊/精确查询）
   * @return 用户列表
   */
  @GetMapping(value = "/list")
  public List<User> list(@RequestParam("userAccount") String userAccount) {
    return userService.selectUserList(userAccount);
  }

  /**
   * 查询所有用户。
   *
   * @return 所有用户列表
   */
  @GetMapping(value = "/all")
  public List<User> listAll() {
    return userService.selectAllList();
  }
}

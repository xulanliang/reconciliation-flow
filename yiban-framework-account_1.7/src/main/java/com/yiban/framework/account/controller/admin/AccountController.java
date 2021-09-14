package com.yiban.framework.account.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.yiban.framework.account.service.AccountService;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.SessionUser;
import com.yiban.framework.core.service.SessionUserService;

/**
 * 账号管理:修改密码
 * @author swing
 *
 * @date 2018年1月19日 上午9:46:49
 */
@Controller
@RequestMapping("/admin/account")
public class AccountController extends FrameworkController {

	@Autowired
	private AccountService accountService;
	@Autowired
	private SessionUserService sessionService;
	
	//退出登录
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout() {
		sessionService.logout();
		return "redirect:/";
	}
	
    @PutMapping
	@ResponseBody
	public ResponseResult doChangePassword(@RequestParam("currentPassword") String currentPassword,
			@RequestParam("newPassword") String newPassword, @RequestParam("newPassword") String comfirmPassword) {
		if (!sessionService.isCurrentUserPassword(currentPassword)) {
			return ResponseResult.failure().message("当前密码输入有误");
		}
		if (!Objects.equal(newPassword, comfirmPassword)) {
			return ResponseResult.failure().message("新密码输入不匹配");
		}
		if (Strings.isNullOrEmpty(newPassword) || newPassword.length() < 6 || newPassword.length() > 30) {
			return ResponseResult.failure().message("新密码格式输入有误");
		}
		try {
			SessionUser currentUser = sessionService.getCurrentSessionUser();
			accountService.changeUserPassword(currentUser.getId(), newPassword);
			sessionService.logout();
			return ResponseResult.success();
		} catch (Exception e) {
			logger.error("doChangePassword", e);
			return this.exceptionAsResult(e);
		}
	}
}

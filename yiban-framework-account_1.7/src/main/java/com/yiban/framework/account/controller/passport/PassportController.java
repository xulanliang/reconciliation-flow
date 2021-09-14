package com.yiban.framework.account.controller.passport;

import java.util.Date;

import com.yiban.framework.account.common.CommonContents;
import com.yiban.framework.account.dao.UserDao;
import com.yiban.framework.account.domain.MessageVerifyCodeReqVo;
import com.yiban.framework.account.util.DateUtil;
import com.yiban.framework.core.domain.ResponseResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.domain.UserStatus;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.account.service.UserService;
import com.yiban.framework.account.util.HttpClientUtil;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.SessionUser;
import com.yiban.framework.core.event.LoginEvent;
import com.yiban.framework.core.service.SessionUserService;
import com.yiban.framework.core.util.IpaddressUtil;
import com.yiban.framework.core.util.ReactorEventUtil;

import net.sf.json.JSONObject;

/**
 * 登录管理
 *
 * @author swing
 *
 * @date 2018年1月19日 上午9:47:50
 */
@Controller
@RequestMapping(value = "/passport")
@Lazy
public class PassportController extends FrameworkController {

	private static final String SHIRO_LOGIN_FAILURE_KEY = "shiroLoginFailure";

	@Autowired
	private SessionUserService sessionService;
	@Autowired
	private UserService userService;
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	@Autowired
	private UserDao userDao;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		// 默认登录页
		return autoView("passport/login");
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String doLogin(@RequestParam("username") String username, @RequestParam("password") String password,
			@RequestParam(value = "rememberMe", required = false, defaultValue = "false") boolean rememberMe,
			@RequestParam(value = "messageVerifyCode", required = false) String messageVerifyCode,
			@RequestParam(value = "mobiles", required = false) String mobiles,
			ModelMap model) {
		try {
			SessionUser sessionUser = null;
			try {
				boolean messageVerifyCodeFlag = Boolean.valueOf(propertiesConfigService.findValueByPkey(ProConstants.MESSAGE_VERIFY_CODE,
						ProConstants.DEFAULT.get(ProConstants.MESSAGE_VERIFY_CODE)));
				if (messageVerifyCodeFlag) {
					// 需要短信验证码验证
					if (StringUtils.isBlank(messageVerifyCode)) {
						model.put(SHIRO_LOGIN_FAILURE_KEY, "请输入短信验证码");
						throw new BusinessException("请输入短信验证码");
					}

					// 验证短信验证码正确性
					ResponseResult responseResult = verificationCode(username, messageVerifyCode);
					int returnCode = responseResult.getCode();
					if (returnCode != 0) {
						logger.error("##### 短信验证码校验失败：{}", responseResult.getMessage());
						model.put(SHIRO_LOGIN_FAILURE_KEY, responseResult.getMessage());
						throw new BusinessException(responseResult.getMessage());
					}
				}

				sessionUser = sessionService.login(username, password, rememberMe);
				String loginIp = IpaddressUtil.getIpAddr();
				// 登录成功后，传播登录事件,所有监听LoginEvent.TOPIC事件的接收者都可以执行
				ReactorEventUtil.publishEvent(LoginEvent.TOPIC, new LoginEvent(sessionUser, new Date(), loginIp));
				if (messageVerifyCodeFlag) {
					// 删除验证码
					User user = userDao.findValideUserByLoginName(username);
					String key = username + "_" + user.getMobilePhone();
					CommonContents.MESSAGE_VERIFY_CODE_MAP.remove(key);
				}
			} catch (BusinessException e) {
				logger.error("Exception：{}", e.getMessage());
				model.put(SHIRO_LOGIN_FAILURE_KEY, e.getMessage());
			}

			if (sessionUser == null) {
				return login();
			}

			return "redirect:/";
		} finally {

		}
	}

	/**
	 * 验证短信验证码合法性
	 * @param username
	 * @param messageVerifyCode
	 * @return
	 */
	private ResponseResult verificationCode(String username, String messageVerifyCode){

		User user = userDao.findValideUserByLoginName(username);
		if (user == null) {
			return ResponseResult.failure("当前用户不存在");
		}

		String key = username + "_" + user.getMobilePhone();
		if (!CommonContents.MESSAGE_VERIFY_CODE_MAP.containsKey(key)){
			return ResponseResult.failure("无效验证码，请核对！");
		}
		MessageVerifyCodeReqVo reqVo = (MessageVerifyCodeReqVo) CommonContents.MESSAGE_VERIFY_CODE_MAP.get(key);
		// 判断验证码是否过期
		String endDateStr = reqVo.getEndDateStr();
		String currentDateStr = DateUtil.getCurrentTimeString();
		int flag = DateUtil.compareDate(currentDateStr, endDateStr, DateUtil.STANDARD_FORMAT);
		if (flag == 1){
			// 验证码过期 删除验证码
			CommonContents.MESSAGE_VERIFY_CODE_MAP.remove(key);
			return ResponseResult.failure("无效验证码，请核对！");
		}
		if (!messageVerifyCode.equals(reqVo.getVerifyCode())) {
			return ResponseResult.failure("验证码不正确，请核对！");
		}
		return ResponseResult.success("验证通过");
	}

	/**
	 * 外联平台登录方法
	 *
	 * @param username
	 * @param password
	 * @param rememberMe
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "login/subsystem", method = RequestMethod.GET)
	public String subSystemLogin(@RequestParam("subsystemUsername") String username, @RequestParam("sign") String sign,
			ModelMap model) {
		logger.error("外联平台登录用户，username = {} ", username);
		try {
			if (StringUtils.isEmpty(sign)) {
				return login();
			}

			User user = userService.queryUserByUsername(username);
			if (user == null) {
				logger.error("没有此用户");
				return login();
			}
			if (user.getStatus() != UserStatus.active.getValue()) {
				logger.error("用户状态为禁用，不可登录");
				return login();
			}
			if (user.getIsDeleted() == 1) {
				logger.error("用户已删除，不可登录");
				return login();
			}

			SessionUser sessionUser = null;
			try {
				// 回调外联平台查询登录状态
				String loginUrl = propertiesConfigService.findValueByPkey(ProConstants.subsystemLoginSignUrl,
						ProConstants.DEFAULT.get(ProConstants.subsystemLoginSignUrl)) + "?sign=" + sign;
				String result = HttpClientUtil.doPost(loginUrl);
				// 解析出result的值，0代表未登录，1代表已登录
				JSONObject resultJSON = JSONObject.fromObject(result);
				if (!resultJSON.containsKey("data") || resultJSON.getInt("data") != 1) {
					logger.error("外联平台返回登录状态不对，resultJSON={}", resultJSON);
					return login();
				}

//				sessionUser = sessionService.login(username, null, false);
				/*使用针对外部免密登录的方法*/
				sessionUser = sessionService.subsystemLogin(username);
				String loginIp = IpaddressUtil.getIpAddr();
				// 登录成功后，传播登录事件,所有监听LoginEvent.TOPIC事件的接收者都可以执行
				ReactorEventUtil.publishEvent(LoginEvent.TOPIC, new LoginEvent(sessionUser, new Date(), loginIp));
			} catch (Exception e) {
				logger.error("Exception", e.getMessage());
				model.put(SHIRO_LOGIN_FAILURE_KEY, e.getMessage());
			}
			if (sessionUser == null) {
				return login();
			}
			return "redirect:/";
		} finally {
		}
	}

	// 无权限时显示的页面
	@RequestMapping(value = "/unauthorized", method = RequestMethod.GET)
	public String unauthorized() {
		return autoView("passport/unauthorized");
	}

	// 登录成功后跳转
	@RequestMapping(value = "/dispatch")
	public String dispatch() {
		return "redirect:/";
	}

	// 用户注册页面
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String register() {
		return autoView("passport/register");
	}
}

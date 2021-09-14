package com.yiban.framework.shiro.service.impl;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Objects;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.SessionUser;
import com.yiban.framework.core.service.LiceneseService;
import com.yiban.framework.core.service.SessionUserService;
import com.yiban.framework.core.util.PasswordUtil;

/**
 * @author swing
 * @date 2018年1月10日 上午10:22:15 类说明
 */
@Service
public class SessionServiceImpl implements SessionUserService {
	@Autowired(required=false)
	private LiceneseService liceneseService;
	
	@Override
	public SessionUser login(String username, String password, boolean rememberMe) throws BusinessException {
		//如果实现了授权认证接口，则进行软件版权授权验证
		if(liceneseService != null){
			liceneseService.doAuthorize(username, password);
		}
		Subject currentUser = SecurityUtils.getSubject();
		SessionUser sessionUser = null;
		if (!currentUser.isAuthenticated()) {
			SubSystemToken token = new SubSystemToken(username, password);
			if (rememberMe) {
				token.setRememberMe(true);
			}
			try {
				currentUser.login(token);
				sessionUser = getCurrentSessionUser();
			} catch (UnknownAccountException uae) {
				throw new BusinessException("您输入的用户名不存在");
			} catch (IncorrectCredentialsException ice) {
				throw new BusinessException("您输入的用户名或密码不对。是否忘记密码了？");
			} catch (LockedAccountException lae) {
				throw new BusinessException("该账户被禁用,请联系管理员！");
			} catch (Exception e) {
				throw new BusinessException("系统当前无法处理您的登录请求，请重试。有任何疑问请联系系统客服！");
			}
		}
		return sessionUser;
	}

	@Override
	public void logout() {
		SecurityUtils.getSubject().logout();
	}

	@Override
	public boolean isCurrentUserPassword(String plainPassword) {
		final User user = (User) this.getCurrentSessionUser();
		if (user == null) {
			return false;
		}
		final String entryptPassword = PasswordUtil.entryptPassword(plainPassword,PasswordUtil.decodeSalt(user.getSalt()));
		return Objects.equal(user.getPassword(), entryptPassword);
	}
	@Override
	public SessionUser forceCurrentSessionUser() {
		SessionUser sessionUser = getCurrentSessionUser();
		if (sessionUser == null) {
			throw new RuntimeException("用户未登录");
		}
		return sessionUser;
	}
	
	@Override
	public SessionUser getCurrentSessionUser() {
		try {
			final Subject subject = SecurityUtils.getSubject();
			if (subject == null) {
				return null;
			}
			return (SessionUser) subject.getPrincipal();
		} catch (Exception e) {

			return null;
		}
	}

	@Override
	public boolean isPermitted(String permit) {
		return SecurityUtils.getSubject().isPermitted(permit);
	}

	@Override
	public SessionUser subsystemLogin(String username) throws BusinessException {
		Subject currentUser = SecurityUtils.getSubject();
		SessionUser sessionUser = null;
		if (!currentUser.isAuthenticated()) {
			SubSystemToken token = null;
			token = new SubSystemToken(username);
			try {
				currentUser.login(token);
				sessionUser = getCurrentSessionUser();
			} catch (UnknownAccountException uae) {
				throw new BusinessException("您输入的用户名不存在");
			} catch (LockedAccountException lae) {
				throw new BusinessException("该账户被禁用,请联系管理员！");
			} catch (Exception e) {
				throw new BusinessException("系统当前无法处理您的登录请求，请重试。有任何疑问请联系系统客服！");
			}
		}
		return sessionUser;
	}
}

package com.yiban.framework.shiro.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springside.modules.utils.Encodes;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.yiban.framework.account.common.CommonContents;
import com.yiban.framework.account.domain.Permissions;
import com.yiban.framework.account.domain.Role;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.AccountService;
import com.yiban.framework.account.service.PermissionsService;

public class ShiroDbRealm extends AuthorizingRealm {
	private static Logger LOGGER = LoggerFactory.getLogger(ShiroDbRealm.class);
	@Autowired
	private AccountService accountService;
	@Autowired
	private PermissionsService permissionsService;
	@Value("${yiban.shrio.filter.user:default}")
	private String userFilterClass;

	// 密码验证器
	@Autowired(required=false)
	private CredentialsMatcher credentialsMatcher;

	public ShiroDbRealm(AccountService accountService) {
		this.accountService = accountService;
	}

	public ShiroDbRealm(CredentialsMatcher credentialsMatcher) {
		this.credentialsMatcher = credentialsMatcher;
	}

	public ShiroDbRealm(AccountService accountService, CredentialsMatcher credentialsMatcher) {
		this.accountService = accountService;
		this.credentialsMatcher = credentialsMatcher;
	}

	/**
	 * 密码验证匹配器
	 */
	@PostConstruct
	public void initCredentialsMatcher() {
		LOGGER.info("initCredentialsMatcher");
		setCredentialsMatcher(credentialsMatcher);
	}

	/**
	 * 用户登录认证
	 */
	@Override
	public AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		SubSystemToken token = (SubSystemToken) authcToken;
//		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		// 通过登录名找到用户对象
		final User user = accountService.findUserByLoginID(token.getUsername());
		if (user != null && user.getIsDeleted() == 1) {
			throw new UnknownAccountException("用户不存在");
		}
		if (user != null && !user.isActive()) {
			throw new LockedAccountException("此用户已经被禁用！");
		}

		if (user != null && user.isActive()) {
			byte[] salt = Encodes.decodeHex(user.getSalt());
			return new SimpleAuthenticationInfo(user, user.getPassword(), ByteSource.Util.bytes(salt), getName());
		}

		return null;
	}

	/**
	 * 用户权限 授权
	 */
	@Override
	public AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		final User user = (User) principals.getPrimaryPrincipal();
		final SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		final Collection<String> roles = getRoleList(user);
		final Collection<String> perms = getPermissions(user);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("User: {}, roles: {}, perms:{}", user.getLoginName(), Arrays.toString(roles.toArray()),
					Arrays.toString(perms.toArray()));
		}
		info.addRoles(getRoleList(user));
		info.addStringPermissions(perms);
		return info;

	}

	private Collection<String> getRoleList(User user) {
		List<Role> roles = accountService.getUserAllRoles(user);
		Function<Role, String> mapper = new Function<Role, String>() {
			@Override
			public String apply(Role input) {
				if (Strings.isNullOrEmpty(input.getRealm())) {
					return input.getName();
				}
				return input.getRealm() + ":" + input.getName();
			}
		};
		return Lists.newArrayList(Iterables.transform(roles, mapper));
	}

	private Collection<String> getPermissions(User user) {
		List<Permissions> permissions = new ArrayList<Permissions>();
		if (CommonContents.DEFAULT_ADMIN_LOGIN_NAME.equals(user.getLoginName())) {
			Permissions Permission = new Permissions();
			Permission.setTarget("*");
			Permission.setMethod("*");
			//去除数据范围
			//Permission.setScope("*");
			permissions.add(Permission);
		} else {
			permissions = permissionsService.getUserPermissions(user);
		}
		Function<Permissions, String> mapper = new Function<Permissions, String>() {
			@Override
			public String apply(Permissions input) {
				return String.format("%s:%s:%s", emptyOrElse(input.getTarget(), "*"),
						emptyOrElse(input.getMethod(), "*"), "*");
			}

			private String emptyOrElse(String t, String e) {
				return (Strings.isNullOrEmpty(t) ? e : t);
			}
		};

		return Lists.newArrayList(Iterables.transform(permissions, mapper));
	}
}

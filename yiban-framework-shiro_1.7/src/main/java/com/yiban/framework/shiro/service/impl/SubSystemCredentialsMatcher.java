package com.yiban.framework.shiro.service.impl;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

import com.yiban.framework.shiro.service.impl.SubSystemToken.LoginType;

public class SubSystemCredentialsMatcher extends HashedCredentialsMatcher {
	
	public SubSystemCredentialsMatcher(String hashAlgorithmName) {
		super(hashAlgorithmName);
	}
	
	@Override
	public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
		SubSystemToken tk = (SubSystemToken) token;
		// 如果是免密登录直接返回true
		if (tk.getType().equals(LoginType.NOPASSWD)) {
			return true;
		}
		// 不是免密登录，调用父类的方法
		return super.doCredentialsMatch(tk, info);
	}
}

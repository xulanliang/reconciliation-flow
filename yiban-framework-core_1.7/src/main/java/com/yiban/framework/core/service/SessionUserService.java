package com.yiban.framework.core.service;



import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.SessionUser;

/**
 * @author swing
 * @date 2018年1月10日 上午10:09:19 类说明
 * 当前登录用户相关接口 ，需要在其他权限框架实现（如shiro框架）
 */
public interface SessionUserService {
    SessionUser login(String username, String password, boolean rememberMe)throws BusinessException;
    
    /**
     * 登录外联平台免密登陆
     * @param username
     * @return
     * @throws BusinessException
     */
    SessionUser subsystemLogin(String username)throws BusinessException;
	void logout();
	boolean isCurrentUserPassword(String plainPassword);
	/**
	 * 获取当前会话用户信息.
	 */
	SessionUser getCurrentSessionUser();
	SessionUser forceCurrentSessionUser();
	public boolean isPermitted(String permit);
}

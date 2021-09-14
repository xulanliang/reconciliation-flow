package com.yiban.rec.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.security.utils.Digests;
import org.springside.modules.utils.Encodes;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.AccountService;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.AppConfig;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.domain.PortalUser;
import com.yiban.rec.util.HttpClientUtil;
import com.yiban.rec.util.StringUtil;




/**
 * 统一门户接口
 * Created by tantian
 *
 * @date 2017/9/6 10:30
 */
@Controller
@RequestMapping("/api/portal")
public class PortalApiController extends FrameworkController {

	@Autowired
	private ApplicationContext ctx;

	@Autowired
	private AccountService accountService;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private AppConfig appConfig;

	/**
	 * 请求绑定页面
	 * @param userId 统一门户账户ID
	 * @param appId 业务系统ID
	 * @return
	 * @throws BusinessException
	 */
	@RequestMapping(value = "/bindPage",method = RequestMethod.GET)
	public  String getBindURL(ModelMap model,String userId,String appId) throws BusinessException, IOException {
		model.put("userId",userId);
		model.put("appId",appId);

		if(userId==null || appId==null || userId.equals("") || appId.equals("")){
			Map<String,String> map=new HashMap<>();
			map.put("success","false");
			map.put("msg","userId或appId不能为空");
            Gson g =new Gson();
			return g.toJson(map);
		}

		return autoView("portal/portalBindPage");
	}

	/**
	 * 用户验证并绑定统一门户
	 * @param username
	 * @param password
	 * @param model
	 * @return
	 */

	@RequestMapping(value = {"/bind"}, method = {RequestMethod.POST})
	@ResponseBody
	public ResponseResult doLogin(@RequestParam("username") String username, @RequestParam("password") String password,
						  @RequestParam("userId") String userId,@RequestParam("appId") String appId,ModelMap model)  {

		String msg="";
		Integer status=0;
		User user=accountService.findValideUserByLoginName(username);
		if(user==null){
			msg="您输入的用户名不存在";
		}else if (!isCurrentUserPassword(user,password)){
			msg="您输入密码错误";
		}else if (user.getStatus()!=0L){
			msg="您输入的用户已被禁用";
		}else {

			// 调用统一门户绑定接口
			Map<String, Object> res = null;
			try {
				res = handlerPortalBind(username, appId, userId);
				if (res.get("success").equals("true")) {
					return ResponseResult.success();
				} else {
					msg=res.get("message").toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
				this.logger.error("Exception", e);
				if (res != null)
					msg=res.get("message").toString();
				else{
					msg="请求统一门户接口失败";
				}
				return ResponseResult.failure(msg);
			}
		}
		return ResponseResult.failure(msg);
	}

	/**
	 * 调用统一门户绑定用户接口
	 * @param appUserName
	 * @param appId
	 * @param userId
	 * @return
	 */
	private Map<String,Object> handlerPortalBind(String appUserName,String appId,String userId) throws Exception {
		//String portalURL=appConfig.getProperty("portalServeUrl", "http://127.0.0.1:9090");
		String portalURL="http://127.0.0.1:9090";

		String url=portalURL+"/api/auth/user/bind";
		Map<String,String> params=new HashMap<>();
		params.put("appUserName",appUserName);
		params.put("appId",appId);
		params.put("userId",userId);
		url=url+"?"+HttpClientUtil.converMapToString(params);

		String res=HttpClientUtil.postMap(url,null, new HashMap<>());
		Map<String,Object> result=new Gson().fromJson(res, new TypeToken<Map<String, String>>() {
				}.getType());
		return  result;

	}

	/**
	 * 统一门户用户绑定
	 * @param portalUser 统一门户用户
	 * @return
	 * @throws BusinessException
	 */
	@RequestMapping(value = "/sync",method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult syncUser(@RequestBody PortalUser portalUser) throws BusinessException {
		if(StringUtil.isEmpty(portalUser.getLoginName())){
			return ResponseResult.failure("用户名不能为空");
		}
		if(StringUtil.isEmpty(portalUser.getLoginPwd())){
			return ResponseResult.failure("用户密码不能为空");
		}

		User user=new User();
		user.setLoginName(portalUser.getLoginName());
		user.setPassword(portalUser.getLoginPwd());

		if(!StringUtil.isEmpty(portalUser.getEmail())){
			user.setEmail(portalUser.getEmail());
		}
		if(!StringUtil.isEmpty(portalUser.getMobile())){
			user.setMobilePhone(portalUser.getMobile());
		}
		if(!StringUtil.isEmpty(portalUser.getSex())){
			user.setGender(portalUser.getSex());
		}
		if(!StringUtil.isEmpty(portalUser.getUseName())){
			user.setName(portalUser.getUseName());
		}

		List<Organization> list=organizationService.findAllTopOrganizations();
		accountService.registerUserAndAddToOrganization(user,list.get(0).getId());

		return ResponseResult.success("用户同步成功");
	}

	/**
	 * 统一门户跳转，用户不存在或禁用时跳转页面
	 * @param userName 统一门户账户名
	 * @return
	 * @throws BusinessException
	 */
	@RequestMapping(value = "/userErrorPage",method = RequestMethod.GET)
	public  String getUserErrorPage(ModelMap model,String userName) throws BusinessException, IOException {
		model.put("userName",userName);

		return autoView("portal/portalUserErrorPage");
	}

	/**
	 * 统一门户跳转，token验证不通过跳转页面
	 * @return
	 * @throws BusinessException
	 */
	@RequestMapping(value = "/validateErrorPage",method = RequestMethod.GET)
	public  String getValidateErrorPage(ModelMap model) throws BusinessException, IOException {
		return autoView("portal/validateErrorPage");
	}

	public boolean isCurrentUserPassword(User user, String plainPassword) {
		final String entryptPassword = entryptPassword(plainPassword, Encodes.decodeHex(user.getSalt()));
		return Objects.equal(user.getPassword(), entryptPassword);
	}

	private String entryptPassword(String plainPassword, byte[] salt) {
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(Charsets.UTF_8), salt, 1024);
		return Encodes.encodeHex(hashPassword);
	}
}


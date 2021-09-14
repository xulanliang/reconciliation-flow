package com.yiban.rec.web.admin.auth;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

import com.google.gson.Gson;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.domain.payproxy.PayApiNameEnum;
import com.yiban.rec.domain.payproxy.PayAuthConfig;
import com.yiban.rec.service.PayAuthConfigService;
import com.yiban.rec.util.PayWayEnum;

/**
 * @author swing
 * @date 2018年7月9日 下午2:11:39 类说明
 * 支付代理
 */
@RestController
@RequestMapping("order")
public class PayOrderProxyController {
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private RestOperations restTemplate;
	@Autowired
	private PayAuthConfigService payAuthConfigService;
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	
	private Gson gson =new Gson();
    private final Logger log=LoggerFactory.getLogger(this.getClass());

	

	/**
	 * 统一下单
	 * 
	 * @param reqOrderSave
	 * @return
	 */
	@RequestMapping(value = {"unified","refund","cancel","refund/query","query"}, method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
	public String unified(@RequestBody Object paramObj) {
		return doAuth(paramObj);
	}

	
	@SuppressWarnings("unchecked")
	private String doAuth(Object paramObj){
		String uri =request.getRequestURI();
		String apiKey =request.getHeader("apikey");
		log.info("当前uri:{},当前key:{}",uri,apiKey);
		if(StringUtils.isEmpty(apiKey)){
			return gson.toJson(ResponseResult.failure("请求头缺少apikey"));
		}
		PayAuthConfig authConfig = payAuthConfigService.findByApiKey(apiKey);
		if(authConfig == null){
			return gson.toJson(ResponseResult.failure("apikey不存在"));
		}
		if(authConfig.getState() == 0){
			return gson.toJson(ResponseResult.failure("apikey已经被禁用"));
		}
		//拥有的接口权限
		String apiName =authConfig.getApiName();
		if(apiName.contains(uri)){
			//如果是下单,则需要判断参数里面的payCode
			if(uri.equals(PayApiNameEnum.ORDER.getName())){
				if(StringUtils.isNotEmpty(authConfig.getResouce())){
					Map<String, String> el=gson.fromJson(gson.toJson(paramObj), Map.class);
					if(el != null && el.get("payCode") != null){
							String payCode =el.get("payCode");
							if(! authConfig.getResouce().contains(payCode)){
								String payType =PayWayEnum.getByCode(payCode).getPayType();
								return gson.toJson(ResponseResult.failure("支付权限不够:"+payType));
							}
					}
				}else{
					return gson.toJson(ResponseResult.failure("没有支付权限"));
				}
				
			}
		}else{
			return gson.toJson(ResponseResult.failure("非法URI"));
		}
		String payCenterURl = propertiesConfigService.findValueByPkey(ProConstants.payCenterUrl, ProConstants.DEFAULT.get(ProConstants.payCenterUrl));
		String url = payCenterURl  + uri;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Object> entity = new HttpEntity<Object>(paramObj, headers);
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
		log.info("入参:{}",gson.toJson(paramObj));
		log.info("出参:{}",responseEntity.getBody());
		return responseEntity.getBody();
	}
}


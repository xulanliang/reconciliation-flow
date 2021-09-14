package com.yiban.rec.web.admin.auth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.rec.domain.payproxy.PayApiNameEnum;
import com.yiban.rec.domain.payproxy.PayAuthConfig;
import com.yiban.rec.service.PayAuthConfigService;
import com.yiban.rec.util.PayWayEnum;

/**
 * @author swing
 * @date 2018年7月5日 下午6:53:31 类说明
 */
@Controller
@RequestMapping("/admin/authconfig")
public class PayAuthConfigContorller extends CurrentUserContoller {
	
	@Autowired
	private PayAuthConfigService payAuthConfigService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String index(ModelMap model) {
		return autoView("auth/authconfig");
	}

	@RestController
	@RequestMapping("/admin/authconfig/data")
	class DataController extends FrameworkController {
		// 查询授权
		@GetMapping
		public WebUiPage<PayAuthConfig> queryList(@RequestParam(required=false) String clientName) {
			Page<PayAuthConfig> page = null;
			if (StringUtils.isNotBlank(clientName)) {
				page = payAuthConfigService.findByClientName(clientName, getRequestPageable());
			} else {
				page = payAuthConfigService.findAll(getRequestPageable());
			}
			return toWebUIPage(page);
		}

		// 添加授权
		@PostMapping
		public ResponseResult save(@Valid PayAuthConfig payConfig) {
			ResponseResult result = null;
			try {
				payAuthConfigService.save(payConfig);
				result = ResponseResult.success();
			} catch (Exception e) {
				result = ResponseResult.failure("授权失败");
			}
			return result;
		}
		
		@PutMapping
		public ResponseResult update(Long id,@Valid PayAuthConfig payConfig) {
			
			ResponseResult result = null;
			PayAuthConfig dbConfig =payAuthConfigService.findById(id);
			if(dbConfig == null){
				result = ResponseResult.failure("授权配置已经不存在");
				return result;
			}
			try {
				dbConfig.setApiName(payConfig.getApiName());
				dbConfig.setClientName(payConfig.getClientName());
				dbConfig.setState(payConfig.getState());
				dbConfig.setResouce(payConfig.getResouce());
				dbConfig.setUpdateTime(new Date());
				payAuthConfigService.save(dbConfig);
				result = ResponseResult.success();
			} catch (Exception e) {
				result = ResponseResult.failure("授权失败");
			}
			return result;
		}

		

		@DeleteMapping("/{id}")
		public ResponseResult delete(@PathVariable Long id) {
			ResponseResult result = null;
			try {
				payAuthConfigService.delete(id);
				result = ResponseResult.success();
			} catch (Exception e) {
				result = ResponseResult.failure("删除失败");
			}
			return result;
		}

		// 获取支付类型，系统编码枚举类型
		@GetMapping(value = "/combox")
		public List<ValueText> removeCoboxType(@RequestParam(value = "isIncludeAll") boolean isIncludeAll, int type) {
			List<ValueText> resultList=new ArrayList<>();
			if(type == 1){
				PayWayEnum[] arr =PayWayEnum.values();
				for(PayWayEnum enumEl:arr){
					ValueText v =new ValueText(enumEl.getCode(), enumEl.getPayType());
					resultList.add(v);
				}
				//支付类型
			}else if(type == 3){
				PayApiNameEnum[] arr =PayApiNameEnum.values();
				for(PayApiNameEnum enumEl:arr){
					ValueText v =new ValueText(enumEl.getName(),enumEl.getLabel());
					resultList.add(v);
				}
			}
			Collections.sort(resultList);
			return resultList;
		}
		
		@GetMapping(value = "/apikey")
		public String createApiKey(){
			return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
		}
	}
}
class ValueText implements Comparable<ValueText>{
	private String id;
	private String value;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public ValueText(String id, String value) {
		this.id = id;
		this.value = value;
	}
	@Override
	public int compareTo(ValueText o) {
		return this.value.compareTo(o.getValue());
	}
}

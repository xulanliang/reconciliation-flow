package com.yiban.rec.web.admin;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.util.ErrorValidate;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.OrganConfig;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.OrganConfigService;
import com.yiban.rec.util.StringUtil;


/**
*<p>文件名称:OrganConfigController.java
*<p>
*<p>文件描述:本类描述
*<p>版权所有:深圳市依伴数字科技有限公司版权所有(C)2017</p>
*<p>内容摘要:机构配置信息维护
</p>
*<p>其他说明:其它内容的说明
</p>
*<p>完成日期:2017年4月25日下午7:36:36</p>
*<p>
*@author fangzuxing
 */
@Controller
@RequestMapping("/admin/organConfig")
public class OrganConfigController extends FrameworkController {

	@Autowired
	private OrganConfigService organConfigService;
	
	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private GatherService gatherService;

	@GetMapping
	public String index(ModelMap model) {
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.valueAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		return autoView("reconciliation/organConfig");
		
	}
	
	@GetMapping({ "/pageList" })
	@ResponseBody   
	public WebUiPage<OrganConfig> pageList(@RequestParam(value = "orgNo", required = false) String orgNo) throws BusinessException {
		PageRequest pagerequest = this.getRequestPageable();
		Page<OrganConfig> data = organConfigService.getOrganConfigList(pagerequest,orgNo);
		return toWebUIPage(data);
	}
	
	@Logable(operation = "新增机构配置信息")
	@PostMapping
	@ResponseBody
	public ResponseResult save(@Valid OrganConfig organConfig, BindingResult result) throws BusinessException {
		if (result.hasErrors()) {
			String message = ErrorValidate.convertErrorMessage(result);
			return ResponseResult.failure(message).debugMessage(result.toString());
		}
		if(!StringUtil.isNullOrEmpty(organConfig.getOrgNo())){
			OrganConfig organConfigOld=organConfigService.getOrganConfigByOrgNo(organConfig.getOrgNo());
			if(organConfigOld!=null){
				return ResponseResult.failure("机构配置信息已经存在");
			}
		}
		
		ResponseResult res = organConfigService.save(organConfig);
		return res;
	}

	@Logable( operation = "修改机构配置信息")
	@PutMapping
	@ResponseBody
	public ResponseResult update(@Valid OrganConfig organConfig, BindingResult result ) throws BusinessException {
		if (result.hasErrors()) {
			String message = ErrorValidate.convertErrorMessage(result);
			return ResponseResult.failure(message).debugMessage(result.toString());
		}
		if(!StringUtil.isNullOrEmpty(organConfig.getOrgNo())){
			OrganConfig organConfigOld=organConfigService.getOrganConfigByOrgNo(organConfig.getOrgNo());
			if(organConfigOld!=null && (organConfigOld.getId() != organConfig.getId())){
				return ResponseResult.failure("机构配置信息已经存在");
			}
		}
		return organConfigService.update(organConfig);
	}

	@Logable( operation = "删除机构配置信息")
	@DeleteMapping("/{id}")
	@ResponseBody
	public ResponseResult delete(@PathVariable Long id)
			throws BusinessException {
		return organConfigService.delete(id);
	}
	
}

package com.yiban.rec.web.admin;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.util.ErrorValidate;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.rec.domain.ServiceMonitor;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.ServiceMonitorService;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.StringUtil;

@Controller
@RequestMapping("/admin/serviceMonitor")
public class ServiceMonitorController extends FrameworkController {

	@Autowired
	private ServiceMonitorService serviceMonitorService;
	@Autowired
	private GatherService gatherService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String index(ModelMap model) {
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(CommonEnum.IsActive.asMap()));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		return autoView("reconciliation/serviceMonitor");
	}
	
	@RequestMapping(value = "/pageList", method = RequestMethod.POST)
	@ResponseBody   
	public WebUiPage<ServiceMonitor> pageList(@RequestParam(value = "orgNo", required = false) Long orgNo) throws BusinessException {
		PageRequest pagerequest = this.getRequestPageable();
		Page<ServiceMonitor> data = serviceMonitorService.getServiceMonitorList(pagerequest,orgNo);
		return toWebUIPage(data);
	}
	
	@RequestMapping(value = "/getServiceMonitor", method = RequestMethod.POST)
	@ResponseBody
	public ServiceMonitor getServiceMonitor(@RequestParam(value = "id", required = true) Long id) {
		ServiceMonitor ServiceMonitor = serviceMonitorService.getServiceMonitorById(id);
		return ServiceMonitor;
	}
	
	@Logable(operation = "新增服务监测信息")
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult save(@Valid ServiceMonitor serviceMonitor, BindingResult result) throws BusinessException {
		if (result.hasErrors()) {
			String message = ErrorValidate.convertErrorMessage(result);
			return ResponseResult.failure(message).debugMessage(result.toString());
		}
		if(!StringUtil.isNullOrEmpty(serviceMonitor.getOrgNo())){
			ServiceMonitor ServiceMonitorOld = serviceMonitorService.getServiceMonitorByOrgNo(serviceMonitor.getOrgNo());
			if(ServiceMonitorOld!=null){
				return ResponseResult.failure("服务监测信息已经存在");
			}
		}
		
		ResponseResult res = serviceMonitorService.save(serviceMonitor);
		return res;
	}

	@Logable(operation = "修改服务监测信息")
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult update(@Valid ServiceMonitor ServiceMonitor, BindingResult result) throws BusinessException {
		if (result.hasErrors()) {
			String message = ErrorValidate.convertErrorMessage(result);
			return ResponseResult.failure(message).debugMessage(result.toString());
		}
		return serviceMonitorService.update(ServiceMonitor);
	}

	@Logable( operation = "删除服务监测信息")
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult delete(@RequestParam(value = "id", required = true) Long id)
			throws BusinessException {
		return serviceMonitorService.delete(id);
	}

}

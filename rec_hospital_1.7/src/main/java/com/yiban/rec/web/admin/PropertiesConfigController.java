package com.yiban.rec.web.admin;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.PropertiesConfig;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;

@Controller
@RequestMapping(value = "admin/propertiesConfig")
public class PropertiesConfigController extends CurrentUserContoller {

	@Autowired
	private PropertiesConfigService propertiesConfigService;

	@GetMapping("")
	public String index() {
		return "baseinfo/propertiesConfig";
	}

	@RequestMapping(value = "data", method = RequestMethod.GET)
	@ResponseBody
	public WebUiPage<PropertiesConfig> page(@RequestParam(required = false) String pkey,
			@RequestParam(required = false) String type, @RequestParam(required = false) String model,
			@RequestParam(required = false) String description) {
		Sort sort = new Sort(Direction.DESC, "sort");
		return super.toWebUIPage(propertiesConfigService.findPage(pkey, type, model, description,
				this.getRequestPageabledWithInitSort(sort)));
	}

	@PostMapping("saveOrUpdate")
	@ResponseBody
	public ResponseResult saveOrUpdate(@Valid PropertiesConfig propertiesConfig, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseResult.failure(result.getAllErrors().get(0).getDefaultMessage());
		}
		try {
			logger.info(new Gson().toJson(propertiesConfig));
			PropertiesConfig config = propertiesConfigService.findOneByPkey(propertiesConfig.getPkey());
			if (null != config) {
				// 只要不是修改本身的
				if (propertiesConfig.getId() == null || propertiesConfig.getId() != config.getId()) {
					return ResponseResult.failure("属性键不能重复添加");
				}
			}
			propertiesConfigService.saveOrUpdate(propertiesConfig);
		} catch (Exception e) {
			logger.error("更新异常,{}", e);
			return ResponseResult.failure("更新异常");
		}
		return ResponseResult.success();
	}

	@PostMapping("del")
	@ResponseBody
	public ResponseResult del(@RequestParam(required = true) Long id) {
		try {
			propertiesConfigService.delete(id);
		} catch (Exception e) {
			logger.error("删除异常, {}", e);
			return ResponseResult.failure("删除异常");
		}
		return ResponseResult.success();
	}
}

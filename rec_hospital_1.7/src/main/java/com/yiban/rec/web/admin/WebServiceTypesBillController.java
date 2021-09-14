package com.yiban.rec.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.rec.domain.WebServiceFieldMappingEntity;
import com.yiban.rec.service.WebServiceFieldMappingService;

@Controller
@RequestMapping(value = "webservice/config")
public class WebServiceTypesBillController extends CurrentUserContoller {

	@Autowired
	private WebServiceFieldMappingService webServiceFieldMappingService;

	@RequestMapping("")
	public String page() {
		return autoView("billConfig/webServiceTypesBill");
	}

	@GetMapping(value = "data")
	@ResponseBody
	public WebUiPage<WebServiceFieldMappingEntity> data() {
		Sort sort = new Sort(Direction.DESC, "id");
		Page<WebServiceFieldMappingEntity> page = webServiceFieldMappingService
				.getPage(this.getRequestPageabledWithInitSort(sort));
		return toWebUIPage(page);
	}

	@PostMapping(value = "saveOrUpdate")
	@ResponseBody
	public ResponseResult saveOrUpdate(WebServiceFieldMappingEntity entity) {
		try {
			webServiceFieldMappingService.saveOrUpdate(entity);
		} catch (Exception e) {
			return ResponseResult.failure("更新失败");
		}
		return ResponseResult.success();
	}

	@PostMapping(value = "del")
	@ResponseBody
	public ResponseResult del(@RequestParam(name = "id", required = true) Long id) {
		try {
			webServiceFieldMappingService.del(id);
		} catch (Exception e) {
			return ResponseResult.failure("删除失败");
		}
		return ResponseResult.success();
	}

}

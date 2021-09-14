package com.yiban.rec.web.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yiban.framework.core.domain.BusinessException;
import com.yiban.rec.domain.basicInfo.CycleType;
import com.yiban.rec.service.RecCommonService;

/**
 * 
 * @ClassName: CommonController
 * @Description: 公共controller
 * @author tuchun@clearofchina.com
 * @date 2017年3月29日 下午5:05:20
 * @version V1.0
 *
 */
@Controller
@RequestMapping("/admin/recCommon")
public class CommonController {
	@Autowired
	private RecCommonService recCommonService;

	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ResponseBody
	public List<CycleType> list(@RequestParam(value = "type", required = false) Integer type) throws BusinessException {

		return recCommonService.findCycleTypesByType(type);
	}
}

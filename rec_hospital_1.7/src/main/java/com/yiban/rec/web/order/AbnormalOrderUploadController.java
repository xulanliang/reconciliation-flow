package com.yiban.rec.web.order;


import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.service.OrderAbnormalUplodeService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumType;

import net.sf.json.JSONObject;

/**
 * 异常订单上送
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = "abnormal/order")
public class AbnormalOrderUploadController extends CurrentUserContoller {
	
	@Autowired
	private HospitalConfigService hospitalConfigService;
	
	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private PropertiesConfigService propertiesConfigService;

	@Autowired
	private OrderAbnormalUplodeService orderAbnormalUplodeService;
	
	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String index(ModelMap model) {
		AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
		model.put("typesJSON", JSONObject.fromObject(ValueTexts.asMap(metaDataService.valueAsList())));
		model.put("orgJSON", JSONObject.fromObject(gatherService.getOrgMap()));
		model.put("org", EnumType.DATA_SOURCE_TYPE_ORG_CHANNEL.getValue());
		model.put("accountDate", DateUtil.getSpecifiedDayBefore(new Date()));
		model.put("flag", CommonConstant.ALL_ID);
		model.put("hConfig", hConfig);
		String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
		model.put("orgCode", orgCode);
		model.put("date", DateUtil.getSpecifiedDayBeforeMonth(DateUtil.getCurrentDate(), 3));
		return autoView("abnormal/abnormalUplode");
	}
	
	
	
}

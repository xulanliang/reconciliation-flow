package com.yiban.rec.web.reports;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.rec.service.SummaryService;
import com.yiban.rec.util.DateUtil;

/**
 * @author swing
 * @date 2018年8月8日 上午9:42:02 类说明 自助结算
 */
@Controller
@RequestMapping("/admin/summaryByPayType")
public class SummaryByPayTypeController extends FrameworkController {
	
	@Autowired
	private SummaryService summaryService;
	
	@Autowired
	private PropertiesConfigService propertiesConfigService;

	@GetMapping
	public String main(ModelMap model) {
		String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
		model.put("orgNo", orgCode);
		model.put("tradeDate", DateUtil.getSpecifiedDayBefore(new Date()));
		return autoView("reports/summaryByPayType");
	}

	@RestController
	@RequestMapping(value = "/admin/SummaryByPayType/data")
	class OperationLogDataController extends BaseController {
		/**
		 * 加载列表数据
		 * 
		 * @return
		 */
		@GetMapping
		public WebUiPage<Map<String, Object>> findList(SummaryService.SummaryByPayTypeVo query) {
			
			boolean bool = SecurityUtils.getSubject().isPermitted("one:manage");
			if(bool){
				User user = (User)SecurityUtils.getSubject().getPrincipal();
				query.setTerminalNo(user.getLoginName());
			}
			
			if(StringUtils.isBlank(query.getOrgCode())){
				String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
				query.setOrgCode(orgCode);
			}
			
			if(StringUtils.isBlank(query.getBeginTime())){
				query.setBeginTime(DateUtil.getSpecifiedDayBefore(new Date()));
			}
			
			if(StringUtils.isBlank(query.getEndTime())){
				query.setEndTime(DateUtil.getSpecifiedDayBefore(new Date()));
			}
			
			List<Map<String, Object>> resultList = summaryService.findAllOfSummaryByPayType(query);
			return new WebUiPage<>(resultList.size(), resultList);
		}

	}
}

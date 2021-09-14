package com.yiban.rec.web.reports;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.vo.PayMethodSummaryVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.PayMethodSummaryService;
import com.yiban.rec.util.Configure;
import com.yiban.rec.util.DateUtil;

@Controller
@RequestMapping("/admin/payMethodSummaryReports")
public class PayMethodSummaryController extends CurrentUserContoller {

	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private GatherService gatherService;
	@Autowired
	private PayMethodSummaryService payMethodSummaryService;
	
	@RequestMapping("")
	public String index(ModelMap model) {
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.NameAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		model.put("orgNo", Configure.getPropertyBykey("yiban.projectid"));
		model.put("tradeDate", DateUtil.getSpecifiedDayBefore(new Date()));
		return autoView("reports/payMethodSummaryReport");
	}
	
	
	
	@RestController
	@RequestMapping({"/admin/payMethodSummaryReports/data"})
	class PayMethodSummaryDataController extends BaseController {
		
		@GetMapping
		public ResponseResult getPayMethodSummaryData(String orgNo, String startTime,String endTime) {
			ResponseResult rs = ResponseResult.success();
			try {
				List<PayMethodSummaryVo> list = payMethodSummaryService.count(orgNo,startTime,endTime);
				rs.data(list);
			} catch (Exception e) {
				rs = ResponseResult.failure("异常");
				e.printStackTrace();
				return rs;
			}
			return rs;
		}
	}
}

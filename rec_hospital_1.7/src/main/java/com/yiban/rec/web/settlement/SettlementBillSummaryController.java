package com.yiban.rec.web.settlement;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.vo.RecHisSettlementResultVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.settlement.RecHisSettlementResultService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.ExportDataUtil;

@Controller
@RequestMapping("/admin/settlementSummary")
public class SettlementBillSummaryController extends CurrentUserContoller {

	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private RecHisSettlementResultService recHisSettlementResultService;
	
	
	@RequestMapping("")
	public String index(ModelMap model) {
		 model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.valueAsList())));
		 model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		 model.put("accountDate", DateUtil.getSpecifiedDayBeforeDay(new Date(),1));
		return autoView("settlement/settlementSummary");
	}
	
	@RestController
	@RequestMapping("/admin/settlementBill/summary")
	class SettlementBillSummaryDataController extends FrameworkController {
		@GetMapping
		public List<Map<String, Object>> summaryQuery(RecHisSettlementResultVo cqvo) {
			Sort sort = new Sort(Direction.ASC, "settleDate");
			List<Organization> orgList = null;
			if(StringUtils.isNotBlank(cqvo.getOrgNo())){
				orgList = organizationService.findByParentCode(cqvo.getOrgNo());
			}
			List<Map<String, Object>> page = null;
			try {
				page = recHisSettlementResultService.getSettlementPage(cqvo, orgList, this.getRequestPageabledWithInitSort(sort));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return page;
		}
		/**
		 * ??????
		 */
		@GetMapping("dcExcel")
		public void exportExcel(RecHisSettlementResultVo cqvo,
				HttpServletRequest request, HttpServletResponse response) throws Exception {
			Sort sort = new Sort(Direction.ASC, "settleDate");
			List<Organization> orgList = null;
			if (null != cqvo.getOrgNo()) {
				orgList = organizationService.findByParentCode(cqvo.getOrgNo());
			}
			List<Map<String, Object>> list=recHisSettlementResultService.getSettlementPage(cqvo, orgList, this.getRequestPageabledWithInitSort(sort));

			List<Map<String, Object>> mapList = new ArrayList<>();
			for (Map<String, Object> th : list) {
				mapList.add(th);
			}
			String[] titleArray = { "??????", "??????????????????(???)", "HIS???????????????(???)", "????????????(???)", "HIS???????????????(???)" , "???????????????(???)", "?????????????????????(???)", "??????????????????(???)", "??????????????????(???)", "??????(???)"};
			String[] cellValue = { "settleDate","channelAmount","hisAmount","exceptionAmount","hisSettlementAmount","yesterdayAmount",
					"todayUnsettleAmount","beforeSettlementAmount","omissionAmount","sumAmount"};
			String fileName = cqvo.getSettleDate() + " ??????????????????";
			ExportDataUtil exportDataUtil = new ExportDataUtil(18,titleArray.length-1,titleArray,cellValue);
			exportDataUtil.commonExportExcel(fileName, "??????????????????", request, response, mapList);
		}
	}
	
}

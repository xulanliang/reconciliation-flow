package com.yiban.rec.web.recon.noncash;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.framework.export.view.excel.ExcelDecoratedEntry;
import com.yiban.rec.domain.HealthCareOfficial;
import com.yiban.rec.domain.RecCash;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.CashQueryVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.service.ReconciliationService;
import com.yiban.rec.service.ThirdTradeService;
import com.yiban.rec.service.WeiNanThirdTradeService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.ExportDataUtil;


/**
 * 
 * <p>
 * ????????????:ReconciliationThirdTradeController.java
 * <p>
 * <p>
 * ????????????:????????????
 * <p>
 * ????????????:???????????????????????????????????????????????????(C)2017
 * </p>
 * <p>
 * ????????????:????????????--->??????????????????????????????
 * </p>
 * <p>
 * ????????????:?????????????????????
 * </p>
 * <p>
 * ????????????:2017???3???21?????????2:40:16
 * </p>
 * <p>
 * 
 * @author fangzuxing
 */
@Controller
@RequestMapping("/admin/thirdtrade")
public class ReconciliationThirdTradeController extends CurrentUserContoller {

	@Autowired
	private ReconciliationService reconciliationService;

	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private GatherService gatherService;
	
	
	@Autowired
	private ThirdTradeService thirdtradeService;
	
	@Autowired
	private WeiNanThirdTradeService weinanThirdtradeService;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private HospitalConfigService hospitalConfigService;
	
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	
	
	@RequestMapping("")
	public String index(ModelMap model) {
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.valueAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		model.put("accountDate", DateUtil.getSpecifiedDayBefore(new Date()));
		model.put("flag",CommonConstant.ALL_ID);

		String thirdtradeSummaryDisplay = propertiesConfigService.findValueByPkey(ProConstants.thirdtradeSummaryDisplay,
				ProConstants.DEFAULT.get(ProConstants.thirdtradeSummaryDisplay));
		model.put("thirdtradeSummaryDisplay", "true".equals(thirdtradeSummaryDisplay) ? true : false);
		
		// ????????????????????????????????????????????????????????????????????????isDisplay
		AppRuntimeConfig runtimeConfig = hospitalConfigService.loadConfig();
		model.put("isDisplay", "true".equals(thirdtradeSummaryDisplay) ? 0
				: StringUtils.isNotBlank(runtimeConfig.getIsDisplay()) ? runtimeConfig.getIsDisplay() : 0);
		return autoView("reconciliation/thirdtrade");
	}
	@RequestMapping("weinan")
	public String weinanIndex(ModelMap model) {
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.valueAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		model.put("accountDate", DateUtil.getSpecifiedDayBefore(new Date()));
		model.put("flag",CommonConstant.ALL_ID);

		String thirdtradeSummaryDisplay = propertiesConfigService.findValueByPkey(ProConstants.thirdtradeSummaryDisplay,
				ProConstants.DEFAULT.get(ProConstants.thirdtradeSummaryDisplay));
		model.put("thirdtradeSummaryDisplay", "true".equals(thirdtradeSummaryDisplay) ? true : false);
		
		// ????????????????????????????????????????????????????????????????????????isDisplay
		AppRuntimeConfig runtimeConfig = hospitalConfigService.loadConfig();
		model.put("isDisplay", "true".equals(thirdtradeSummaryDisplay) ? 0
				: StringUtils.isNotBlank(runtimeConfig.getIsDisplay()) ? runtimeConfig.getIsDisplay() : 0);
		return autoView("reconciliation/weinanthirdtrade");
	}
	@RestController
	@RequestMapping({"/admin/thirdTrade/data"})
	class ThirdTradeDataController extends BaseController {
		@GetMapping
		public WebUiPage<Map<String, Object>> recThridtradeQuery(CashQueryVo cqvo) {
			Sort sort = getSortFromDatagridOrElse(new Sort(Direction.DESC, "tradeDatatime"));
			try {
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(StringUtils.isNotBlank(cqvo.getStartTime())) {
					cqvo.setStartDate(sdf.parse(cqvo.getStartTime().trim()));
				}
				if(StringUtils.isNotBlank(cqvo.getEndTime())) {
					cqvo.setEndDate(sdf.parse(cqvo.getEndTime().trim()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			List<Organization> orgList = null;
			if(null != cqvo.getOrgNo()){
				orgList = organizationService.findByParentCode(cqvo.getOrgNo());
			}
			
			Page<Map<String, Object>> platPage = reconciliationService.getThridAllPage(cqvo,orgList,
					this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(platPage);
		}
		
		@GetMapping("/collect")
		public ResponseResult getCollect(@RequestParam(value = "orgNo", required = false) String orgNo,@RequestParam(value = "startDate", required = false) String startDate,
				@RequestParam(value = "endDate", required = false) String endDate,
				String payType,String payFlowNo,String billSource,String shopFlowNo,String orderState){
			List<Organization> orgList = null;
			if(StringUtils.isNotBlank(orgNo)){
				orgList = organizationService.findByParentCode(orgNo);
			}
			Map<String,Object> map = thirdtradeService.getTradeCollect(orgNo, startDate, endDate,payType,payFlowNo,billSource,orgList,shopFlowNo,orderState);
			return ResponseResult.success().data(map);
		}
		@GetMapping("/weinan/collect")
		public ResponseResult weinanGetCollect(@RequestParam(value = "orgNo", required = false) String orgNo,@RequestParam(value = "startDate", required = false) String startDate,
				@RequestParam(value = "endDate", required = false) String endDate,
				String payType,String payFlowNo,String billSource,String shopFlowNo,String orderState){
			List<Organization> orgList = null;
			if(StringUtils.isNotBlank(orgNo)){
				orgList = organizationService.findByParentCode(orgNo);
			}
			Map<String,Object> map = weinanThirdtradeService.getTradeCollect(orgNo, startDate, endDate,payType,payFlowNo,billSource,orgList,shopFlowNo,orderState);
			return ResponseResult.success().data(map);
		}
		
		@GetMapping("/summary")
		public ResponseResult summary(@RequestParam(value = "orgNo", required = false) String orgNo,
				@RequestParam(value = "startDate", required = false) String startDate,
				@RequestParam(value = "endDate", required = false) String endDate, String payType, String payFlowNo,
				String billSource, String shopFlowNo, String orderState) {
			List<Organization> orgList = null;
			if (StringUtils.isNotBlank(orgNo)) {
				orgList = organizationService.findByParentCode(orgNo);
			}
			Map<String, Object> map = thirdtradeService.summary(orgNo, startDate, endDate, payType, payFlowNo,
					billSource, orgList, shopFlowNo, orderState);
			return ResponseResult.success().data(map);
		}
		
		@GetMapping("/cashDetails")
		public WebUiPage<RecCash> cashDetails(@RequestParam(value = "id", required = true) String id){
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			Integer numId = Integer.parseInt(id.substring(0, id.length()-2));
			//????????????
			Page<RecCash> vo = reconciliationService.getCash(numId,this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(vo);
		}
		@GetMapping("/yibaoDetails")
		public WebUiPage<HealthCareOfficial> yibaoDetails(@RequestParam(value = "id", required = true) String id){
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			Integer numId = Integer.parseInt(id.substring(0, id.length()-2));
			//????????????
			Page<HealthCareOfficial> vo = reconciliationService.getYiBao(numId,this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(vo);
		}
		@GetMapping("/billDetails")
		public WebUiPage<ThirdBill> billDetails(@RequestParam(value = "id", required = true) String id){
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			Integer numId = Integer.parseInt(id.substring(0, id.length()-2));
			//????????????
			Page<ThirdBill> vo = reconciliationService.getBill(numId,this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(vo);
		}
		
		
		
		@Logable( operation = "??????????????????????????????")
		@GetMapping("/dcExcel")
		public void toDcExcel(CashQueryVo cqvo,ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception{ 
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				if(StringUtils.isNotBlank(cqvo.getStartTime())) {
					cqvo.setStartDate(sdf.parse(cqvo.getStartTime().trim()));
				}
				if(StringUtils.isNotBlank(cqvo.getEndTime())) {
					cqvo.setEndDate(sdf.parse(cqvo.getEndTime().trim()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			List<Organization> orgListTemp = organizationService.findByParentCode(cqvo.getOrgNo());
			Sort sort = getSortFromDatagridOrElse(new Sort(Direction.DESC, "tradeDatatime"));
			Pageable page = this.getPageRequest(CommonConstant.exportPageSize, sort);
			List<Map<String, Object>> hisMapList = reconciliationService.getThridAllNoPage(cqvo,orgListTemp,page);
			/*List<Map<String, Object>> hisMapList = new ArrayList<>();
			for (ThirdBillVo thirdBillVo : hisList) {
				Map<String, Object> hisMap = new HashMap<>();
				hisMap.put("orgName", thirdBillVo.getOrgName());
				hisMap.put("payTypeName", thirdBillVo.getPayTypeName());
				hisMap.put("tradeDatatime", sdf.format(thirdBillVo.getTradeDatatime()));
				hisMap.put("billSourceName", thirdBillVo.getBillSourceName());
				hisMap.put("payFlowNo", thirdBillVo.getPayFlowNo());
				hisMap.put("orderStateName", thirdBillVo.getOrderStateName());
				hisMap.put("payAmount", thirdBillVo.getPayAmount().toString());
				hisMapList.add(hisMap);
			}*/
			List<ExcelDecoratedEntry> ops = new ArrayList<ExcelDecoratedEntry>();
			ops.add(new ExcelDecoratedEntry("orgName", "????????????",5000));
			ops.add(new ExcelDecoratedEntry("payTypeName", "????????????"));
			ops.add(new ExcelDecoratedEntry("tradeDatatime", "????????????",5000));
			ops.add(new ExcelDecoratedEntry("billSourceName", "????????????"));
			ops.add(new ExcelDecoratedEntry("payFlowNo", "???????????????"));
			ops.add(new ExcelDecoratedEntry("orderStateName", "????????????"));
			ops.add(new ExcelDecoratedEntry("payAmount", "??????(???)"));
			ops.add(new ExcelDecoratedEntry("businessFlowNo", "???????????????"));
			SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
			
			String[] thirdTitleArray = { "????????????", "????????????", "????????????", "????????????", "???????????????", "????????????", "??????(???)","???????????????"};
			String[] cellValue = { "orgName","payTypeName","tradeDatatime","billSourceName","payFlowNo","orderStateName","payAmount","businessFlowNo"};
			ExportDataUtil exportDataUtil = new ExportDataUtil(18,7,thirdTitleArray,cellValue);
			String fileName = sdf2.format(sdf2.parse(cqvo.getStartTime()))+"???"+sdf2.format(sdf2.parse(cqvo.getEndTime()))+cqvo.getOrgName()+"??????????????????";
			exportDataUtil.commonExportExcel(fileName, "??????????????????", request, response, hisMapList);
		}
	}
}

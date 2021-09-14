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
 * 文件名称:ReconciliationThirdTradeController.java
 * <p>
 * <p>
 * 文件描述:本类描述
 * <p>
 * 版权所有:深圳市依伴数字科技有限公司版权所有(C)2017
 * </p>
 * <p>
 * 内容摘要:对账管理--->支付渠道交易明细查询
 * </p>
 * <p>
 * 其他说明:其它内容的说明
 * </p>
 * <p>
 * 完成日期:2017年3月21日下午2:40:16
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
		
		// 如果支付渠道交易明细页面的数据汇总显示，则不显示isDisplay
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
		
		// 如果支付渠道交易明细页面的数据汇总显示，则不显示isDisplay
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
			//现金详情
			Page<RecCash> vo = reconciliationService.getCash(numId,this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(vo);
		}
		@GetMapping("/yibaoDetails")
		public WebUiPage<HealthCareOfficial> yibaoDetails(@RequestParam(value = "id", required = true) String id){
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			Integer numId = Integer.parseInt(id.substring(0, id.length()-2));
			//医保详情
			Page<HealthCareOfficial> vo = reconciliationService.getYiBao(numId,this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(vo);
		}
		@GetMapping("/billDetails")
		public WebUiPage<ThirdBill> billDetails(@RequestParam(value = "id", required = true) String id){
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			Integer numId = Integer.parseInt(id.substring(0, id.length()-2));
			//渠道详情
			Page<ThirdBill> vo = reconciliationService.getBill(numId,this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(vo);
		}
		
		
		
		@Logable( operation = "导出支付渠道交易明细")
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
			ops.add(new ExcelDecoratedEntry("orgName", "机构名称",5000));
			ops.add(new ExcelDecoratedEntry("payTypeName", "支付类型"));
			ops.add(new ExcelDecoratedEntry("tradeDatatime", "账单日期",5000));
			ops.add(new ExcelDecoratedEntry("billSourceName", "渠道名称"));
			ops.add(new ExcelDecoratedEntry("payFlowNo", "支付流水号"));
			ops.add(new ExcelDecoratedEntry("orderStateName", "订单状态"));
			ops.add(new ExcelDecoratedEntry("payAmount", "金额(元)"));
			ops.add(new ExcelDecoratedEntry("businessFlowNo", "商户流水号"));
			SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
			
			String[] thirdTitleArray = { "机构名称", "支付类型", "账单日期", "渠道名称", "支付流水号", "订单状态", "金额(元)","商户流水号"};
			String[] cellValue = { "orgName","payTypeName","tradeDatatime","billSourceName","payFlowNo","orderStateName","payAmount","businessFlowNo"};
			ExportDataUtil exportDataUtil = new ExportDataUtil(18,7,thirdTitleArray,cellValue);
			String fileName = sdf2.format(sdf2.parse(cqvo.getStartTime()))+"至"+sdf2.format(sdf2.parse(cqvo.getEndTime()))+cqvo.getOrgName()+"渠道交易明细";
			exportDataUtil.commonExportExcel(fileName, "渠道交易明细", request, response, hisMapList);
		}
	}
}

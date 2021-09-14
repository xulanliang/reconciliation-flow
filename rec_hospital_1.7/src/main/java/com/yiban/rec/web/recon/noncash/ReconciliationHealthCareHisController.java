package com.yiban.rec.web.recon.noncash;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.framework.export.view.excel.ExcelDecoratedEntry;
import com.yiban.framework.export.view.excel.ExcelResult;
import com.yiban.rec.domain.HealthCareHis;
import com.yiban.rec.domain.vo.HealthCareDetailQueryVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.HealthCareHisService;
import com.yiban.rec.service.UserOrganizationPerService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumTypeOfInt;

/**
 * 
 * <p>
 * 文件名称:ReconciliationController.java
 * <p>
 * <p>
 * 文件描述:本类描述
 * <p>
 * 版权所有:深圳市依伴数字科技有限公司版权所有(C)2017
 * </p>
 * <p>
 * 内容摘要:对账管理--->医保his交易明细查询
 * </p>
 * <p>
 * 其他说明:其它内容的说明
 * </p>
 * <p>
 * 完成日期:2018年5月13日 
 * </p>
 * <p>
 * 
 * @author tanjian
 */
@Controller
@RequestMapping("/admin/healthCareHis")
public class ReconciliationHealthCareHisController extends CurrentUserContoller {

	@Autowired
	private HealthCareHisService healthCareHisService;

	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private UserOrganizationPerService userOrganizationPerService;
	
	
	@RequestMapping("")
	public String index(ModelMap model) {
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.getNameValueAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		model.put("accountDate", DateUtil.getSpecifiedDayBefore(new Date()));
		model.put("flag",CommonConstant.ALL_ID);
		return autoView("reconciliation/healthCareHis");
	}
	@RestController
	@RequestMapping({"/admin/healthCareHis/data"})
	class healthCareHisDataController extends BaseController {
		@GetMapping
		public WebUiPage<HealthCareHis> recHishealthCareHisQuery(HealthCareDetailQueryVo cqvo) {
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			User user = currentUser();
			try {
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				if(StringUtils.isNotBlank(cqvo.getStartTime())) {
					cqvo.setStartDate(sdf.parse(cqvo.getStartTime().trim()));
				}
				if(StringUtils.isNotBlank(cqvo.getEndTime())) {
					cqvo.setEndDate(sdf.parse(cqvo.getEndTime().trim()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
			Page<HealthCareHis> platPage = healthCareHisService.getHealthCareHisPage(cqvo,orgListTemp,
					this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(platPage);
		}
		
		/**
		* @date：2017年4月7日 
		* @Description：导出
		* @param model
		* @param request
		* @return: 返回结果描述
		* @return ModelAndView: 返回值类型
		* @throws
		 */
		@Logable( operation = "导出交易明细")
		@GetMapping("/api/dcExcel")
		public ModelAndView toDcExcel(HealthCareDetailQueryVo cqvo){ 
			User user = currentUser();
			try {
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				if(StringUtils.isNotBlank(cqvo.getStartTime())) {
					cqvo.setStartDate(sdf.parse(cqvo.getStartTime().trim()));
				}
				if(StringUtils.isNotBlank(cqvo.getEndTime())) {
					cqvo.setEndDate(sdf.parse(cqvo.getEndTime().trim()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			Pageable page = this.getPageRequest(CommonConstant.exportPageSize, sort);
			List<HealthCareHis> hisList = healthCareHisService.getHealthCareHisList(cqvo,orgListTemp,page);
			List<ExcelDecoratedEntry> ops = new ArrayList<ExcelDecoratedEntry>();
			ops.add(new ExcelDecoratedEntry("tradeDatatime", "账单日期"));
			ops.add(new ExcelDecoratedEntry("orgName", "机构名称"));
			ops.add(new ExcelDecoratedEntry("operationTypeName", "交易类型"));
			ops.add(new ExcelDecoratedEntry("payFlowNo", "支付方流水号"));
			ops.add(new ExcelDecoratedEntry("patientName", "患者姓名"));
			/*ops.add(new ExcelDecoratedEntry("healthcareTypeName", "医保类型"));*/
			ops.add(new ExcelDecoratedEntry("businessCycleNo", "业务周期号"));
			ops.add(new ExcelDecoratedEntry("costBasic", "基本医疗费用"));
			ops.add(new ExcelDecoratedEntry("costAccount", "账户支付金额"));
			ops.add(new ExcelDecoratedEntry("costCash", "现金支付金额"));
			ops.add(new ExcelDecoratedEntry("costWhole", "统筹支付金额"));
			ops.add(new ExcelDecoratedEntry("costAll", "医疗费总额"));
			ops.add(new ExcelDecoratedEntry("costRescue", "救助基金支付金额"));
			ops.add(new ExcelDecoratedEntry("costSubsidy", "补助支付金额"));
			BigDecimal costAll = new BigDecimal(0);
			BigDecimal costBasic = new BigDecimal(0);
			BigDecimal costAccount = new BigDecimal(0);
			BigDecimal costCash = new BigDecimal(0);
			BigDecimal costWhole = new BigDecimal(0);
			BigDecimal costRescue = new BigDecimal(0);
			BigDecimal costSubsidy = new BigDecimal(0);
			List<HealthCareHis> listVo = new ArrayList<HealthCareHis>();
			for(HealthCareHis flow : hisList) {
				if(EnumTypeOfInt.TRADE_TYPE_REFUND.getValue().equals(flow.getOrderState())) {//退费
					costAll = costAll.add(flow.getCostAll() == null ? new BigDecimal(0) : flow.getCostAll());
					costAccount = costAccount.add(flow.getCostAccount() == null ? new BigDecimal(0) : flow.getCostAccount().abs().negate());
					costCash = costCash.add(flow.getCostCash() == null ? new BigDecimal(0) : flow.getCostCash().abs().negate());
					costWhole = costWhole.add(flow.getCostWhole() == null ? new BigDecimal(0) : flow.getCostWhole().abs().negate());
					costBasic= costBasic.add(flow.getCostBasic() == null ? new BigDecimal(0) : flow.getCostBasic().abs().negate());
					costRescue= costRescue.add(flow.getCostRescue() == null ? new BigDecimal(0) : flow.getCostRescue().abs().negate());
					costSubsidy= costRescue.add(flow.getCostSubsidy() == null ? new BigDecimal(0) : flow.getCostSubsidy().abs().negate());
				}else {
					costAll = costAll.add(flow.getCostAll());
					costAccount = costAccount.add(flow.getCostAccount() == null ? new BigDecimal(0) : flow.getCostAccount());
					costCash = costCash.add(flow.getCostCash() == null ? new BigDecimal(0) : flow.getCostCash());
					costWhole = costWhole.add(flow.getCostWhole() == null ? new BigDecimal(0) : flow.getCostWhole());
					costBasic= costBasic.add(flow.getCostBasic() == null ? new BigDecimal(0) : flow.getCostBasic());
					costRescue= costRescue.add(flow.getCostRescue() == null ? new BigDecimal(0) : flow.getCostRescue());
					costSubsidy= costSubsidy.add(flow.getCostSubsidy() == null ? new BigDecimal(0) : flow.getCostSubsidy());
				}
				listVo.add(flow);
			}
			HealthCareHis hisVo=new HealthCareHis();
			hisVo.setOrgName("合计:");
			hisVo.setCostAll(costAll.setScale(2, BigDecimal.ROUND_HALF_UP));
			hisVo.setCostBasic(costBasic.setScale(2, BigDecimal.ROUND_HALF_UP));
			hisVo.setCostAccount(costAccount.setScale(2, BigDecimal.ROUND_HALF_UP));
			hisVo.setCostCash(costCash.setScale(2, BigDecimal.ROUND_HALF_UP));
			hisVo.setCostWhole(costWhole.setScale(2, BigDecimal.ROUND_HALF_UP));
			hisVo.setCostRescue(costRescue.setScale(2, BigDecimal.ROUND_HALF_UP));
			hisVo.setCostSubsidy(costSubsidy.setScale(2, BigDecimal.ROUND_HALF_UP));
			listVo.add(hisVo);
			ExcelResult viewExcel = null;
			viewExcel = new ExcelResult(listVo, ops,cqvo.getStartTime()+"至"+cqvo.getEndTime()+cqvo.getOrgName()+"医保his交易明细",12);
			return new ModelAndView(viewExcel); 
		}
	}
}

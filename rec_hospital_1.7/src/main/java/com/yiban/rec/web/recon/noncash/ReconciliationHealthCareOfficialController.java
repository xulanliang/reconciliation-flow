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
import com.yiban.rec.domain.HealthCareOfficial;
import com.yiban.rec.domain.vo.HealthCareDetailQueryVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.HealthCareOfficialService;
import com.yiban.rec.service.UserOrganizationPerService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.DateUtil;

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
 * 内容摘要:对账管理--->医保交易明细查询
 * </p>
 * <p>
 * 其他说明:其它内容的说明
 * </p>
 * <p>
 * 完成日期:2018年5月10日
 * </p>
 * <p>
 * 
 * @author tanjian
 */
@Controller
@RequestMapping("/admin/healthCareOfficial")
public class ReconciliationHealthCareOfficialController extends CurrentUserContoller {

	@Autowired
	private HealthCareOfficialService healthCareOfficialService;

	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private UserOrganizationPerService userOrganizationPerService;
	
	@Autowired
	private MetaDataService metaDataService;
	
	@RequestMapping("")
	public String index(ModelMap model) {
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.getNameValueAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		model.put("accountDate", DateUtil.getSpecifiedDayBefore(new Date()));
		model.put("flag",CommonConstant.ALL_ID);
		return autoView("reconciliation/healthCareOfficial");
	}
	@RestController
	@RequestMapping({"/admin/healthCareOfficial/data"})
	class healthCareOfficialDataController extends BaseController {
		@GetMapping
		public WebUiPage<HealthCareOfficial> recThridtradeQuery(HealthCareDetailQueryVo cqvo) {
			Sort sort = new Sort(Direction.DESC, "orgNo");
//			User user = currentUser();
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
//			List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
			Page<HealthCareOfficial> platPage = healthCareOfficialService.getHealthCareOfficialPage(cqvo,
					this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(platPage);
		}
		
		/**
		 * @throws UnsupportedEncodingException 
		* @date：2017年4月7日 
		* @Description：导出
		* @param model
		* @param request
		* @return: 返回结果描述
		* @return ModelAndView: 返回值类型
		* @throws
		 */
		@Logable( operation = "导出支付渠道交易明细")
		//@RequestMapping(value="/dcExcel",method=RequestMethod.GET)
		@GetMapping("/dcExcel")
		public ModelAndView toDcExcel(HealthCareDetailQueryVo cqvo) throws Exception{ 
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
			List<HealthCareOfficial> hisList = healthCareOfficialService.getHealthCareOfficialList(cqvo,orgListTemp,page);
			List<ExcelDecoratedEntry> ops = new ArrayList<ExcelDecoratedEntry>();
			ops.add(new ExcelDecoratedEntry("tradeDatatime", "账单日期"));
			ops.add(new ExcelDecoratedEntry("orgName", "机构名称"));
			ops.add(new ExcelDecoratedEntry("operationTypeName", "操作类型"));
			ops.add(new ExcelDecoratedEntry("payFlowNo", "支付商户流水号"));
			ops.add(new ExcelDecoratedEntry("healthcareTypeName", "医保类型"));
			ops.add(new ExcelDecoratedEntry("businessCycleNo", "业务周期号"));
			ops.add(new ExcelDecoratedEntry("costBasic", "基本医疗费用"));
			ops.add(new ExcelDecoratedEntry("costAccount", "账户支付金额"));
			ops.add(new ExcelDecoratedEntry("costCash", "现金支付金额"));
			ops.add(new ExcelDecoratedEntry("costWhole", "统筹支付金额"));
			ops.add(new ExcelDecoratedEntry("costAll", "医疗费总额"));
			BigDecimal costAll = new BigDecimal(0);
			BigDecimal costBasic = new BigDecimal(0);
			BigDecimal costAccount = new BigDecimal(0);
			BigDecimal costCash = new BigDecimal(0);
			BigDecimal costWhole = new BigDecimal(0);
			List<HealthCareOfficial> listVo = new ArrayList<HealthCareOfficial>();
			for(HealthCareOfficial flow : hisList) {
				costAll = costAll.add(flow.getCostAll());
				costAccount = costAccount.add(flow.getCostAccount());
				costCash = costCash.add(flow.getCostCash());
				costWhole = costWhole.add(flow.getCostWhole());
				costBasic= costBasic.add(flow.getCostBasic());
				listVo.add(flow);
			}
			HealthCareOfficial hisVo=new HealthCareOfficial();
			hisVo.setOrgName("合计:");
			hisVo.setCostAll(costAll.setScale(2, BigDecimal.ROUND_HALF_UP));
			hisVo.setCostAll(costAll.setScale(2, BigDecimal.ROUND_HALF_UP));
			hisVo.setCostBasic(costBasic.setScale(2, BigDecimal.ROUND_HALF_UP));
			hisVo.setCostAccount(costAccount.setScale(2, BigDecimal.ROUND_HALF_UP));
			hisVo.setCostCash(costCash.setScale(2, BigDecimal.ROUND_HALF_UP));
			hisVo.setCostWhole(costWhole.setScale(2, BigDecimal.ROUND_HALF_UP));
			listVo.add(hisVo);
			ExcelResult viewExcel = null;
			viewExcel = new ExcelResult(listVo, ops);
			viewExcel.setFileName(DateUtil.getCurrentDateString()+" - 医保中心详细数据");
			return new ModelAndView(viewExcel); 
		}
		
	}
}

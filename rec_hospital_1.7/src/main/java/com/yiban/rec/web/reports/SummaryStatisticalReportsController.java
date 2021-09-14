package com.yiban.rec.web.reports;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.framework.report.view.IReportView;
import com.yiban.framework.report.view.IreportFormatEnum;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.reports.SummaryStatisticalReportsJasper;
import com.yiban.rec.domain.vo.HisPayQueryVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.HisTransactionFlowService;
import com.yiban.rec.service.UserOrganizationPerService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.PayBusinssTypeEnum;
import com.yiban.rec.util.PayTypeEnum;
import com.yiban.rec.util.StringUtil;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * 
 *
 * 项目名称：rec_hospital
 * 类名称：SummaryStatisticalReportsController
 * 类描述：
 * 创建人：huangguojie
 * 创建时间：2018年5月16日 下午3:59:11
 * 修改人：huangguojie
 * 修改时间：2018年5月16日 下午3:59:11
 * 修改备注：
 * @version
 *
 */
@Controller
@RequestMapping("/admin/summaryStatisticalReports")
public class SummaryStatisticalReportsController extends CurrentUserContoller {
	
	private static List<String> payTypes = PayTypeEnum.getAllCode();
	private static List<String> payBusinessTypes = PayBusinssTypeEnum.getAllCode();
	
	@Autowired
	private UserOrganizationPerService userOrganizationPerService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private HisTransactionFlowService hisTransactionFlowService;
	@Autowired
	private GatherService gatherService;
	@Autowired
	private MetaDataService metaDataService;
	
	@RequestMapping("")
	public String index(ModelMap model) {
		model.put("accountDate", DateUtil.getInputDateOnlyDay(new Date()));
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.valueAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		return autoView("reports/summaryStatisticalReports");
	}
	
	@RestController
	@RequestMapping({"/admin/summaryStatisticalReports/data"})
	class SummaryStatisticalReportsDataController extends BaseController {
		
		@Autowired
		private UserOrganizationPerService userOrganizationPerService;
		@Autowired
		private HisTransactionFlowService hisTransactionFlowService;
		@Autowired
		private OrganizationService organizationService;
		
		@GetMapping
		public WebUiPage<HisTransactionFlow> recHistradeQuery(HisPayQueryVo vo) {
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			User user = currentUser();
			List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
			try {
				Date date = DateUtil.getInputDateOnlyDay(new Date());
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				if(StringUtils.isNotBlank(vo.getStartTime())) {
					vo.setStartDate(sdf.parse(vo.getStartTime()));
				} else if(null == vo.getStartTime()) {
					vo.setStartDate(date);
				}
				if(StringUtils.isNotBlank(vo.getEndTime())) {
					vo.setEndDate(sdf.parse(vo.getEndTime()));
				} else if(null == vo.getEndTime()) {
					vo.setEndDate(date);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Organization organization = organizationService.findByCode(vo.getOrgNo());
			if(organization == null){
				organization = organizationService.findOrganizationById(Long.valueOf(vo.getOrgNo()));
				vo.setOrgNo(organization.getCode());
			}
			Page<HisTransactionFlow> platPage = hisTransactionFlowService.getHisPayPage(vo,orgListTemp, payTypes, payBusinessTypes, this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(platPage);
		}
	}
	
	@Logable(operation = "打印报表")
	@RequestMapping("/exportJasper")
	public ModelAndView export(Model model, HisPayQueryVo vo, HttpServletRequest request) throws BusinessException, ParseException {
		Sort sort = new Sort(Direction.DESC, "tradeDatatime");
		User user = currentUser();
		List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
		Organization organization = organizationService.findByCode(vo.getOrgNo());
		if(organization == null){
			organization = organizationService.findOrganizationById(Long.valueOf(vo.getOrgNo()));
			vo.setOrgNo(organization.getCode());
		}
		try {
			Date date = DateUtil.getInputDateOnlyDay(new Date());
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			if(StringUtils.isNotBlank(vo.getStartTime())) {
				vo.setStartDate(sdf.parse(vo.getStartTime()));
			} else if(null == vo.getStartTime()) {
				vo.setStartDate(date);
			}
			if(StringUtils.isNotBlank(vo.getEndTime())) {
				vo.setEndDate(sdf.parse(vo.getEndTime()));
			} else if(null == vo.getEndTime()) {
				vo.setEndDate(date);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<HisTransactionFlow> datas = hisTransactionFlowService.getHisPayList(vo, orgListTemp, payTypes, payBusinessTypes, sort);
		organization = null;
		if(orgListTemp.size() > 1) {
			for(Organization entity : orgListTemp) {
				if(StringUtil.equals(vo.getOrgNo(), entity.getCode())) {
					organization = entity;
					break;
				}
			}
		} else {
			organization = orgListTemp.get(0);
		}
		
		// 预交金
		List<SummaryStatisticalReportsJasper> jasper = new ArrayList<>();
		if(!StringUtil.isNullOrEmpty(organization.getChildren())) {
			getSummaryStatisticalReportsJasper(datas, organization, jasper);
			List<Organization> children = organization.getChildren();
			for(Organization entity : children) {
				if(null != entity) {
					getSummaryStatisticalReportsJasper(datas, entity, jasper);
				}
			}
		}else {
			getSummaryStatisticalReportsJasper(datas, organization, jasper);
		}
		// 挂号费
		BigDecimal amountMoney = statisticsRegistrationAmount(datas);
		JRDataSource jrDataSource = new JRBeanCollectionDataSource(jasper);
		// 指定报表模板
		Map<String, Object> dataMap = new HashMap<>();
		// 定位
		dataMap.put("url", "classpath:jsper/summaryStatisticalReports.jasper");
		dataMap.put("format", IreportFormatEnum.PDF.getValue());
		dataMap.put("jrMainDataSource", jrDataSource);
		dataMap.put("reportTitle", organization.getName() + "自助汇总报表");
		dataMap.put("benginDate", DateUtil.transferDateToString("yyyy-MM-dd", vo.getStartDate()));
		dataMap.put("endDate", DateUtil.transferDateToString("yyyy-MM-dd", vo.getEndDate()));
		dataMap.put("amountMoney", amountMoney.doubleValue());

		model.addAllAttributes(dataMap);
		return new ModelAndView(IReportView.name);
	}
	
	// 挂号费总额
	private BigDecimal statisticsRegistrationAmount(List<HisTransactionFlow> hisTransactionFlows) {
		// 挂号费
		BigDecimal amountMoney = new BigDecimal(0.00);
		if (null != hisTransactionFlows && hisTransactionFlows.size() > 0) {
			for (HisTransactionFlow flow : hisTransactionFlows) {
				if (flow.getPayBusinessType().equals(PayBusinssTypeEnum.REGISTRATION.getCode())) {
					amountMoney = amountMoney.add(flow.getPayAmount());
				}
			}
		}
		return amountMoney;
	}

	// 指定报表
	private void getSummaryStatisticalReportsJasper(List<HisTransactionFlow> hisTransactionFlows, Organization org, List<SummaryStatisticalReportsJasper> jasperList) {
		BigDecimal wechatMoney = new BigDecimal(0.00);
		BigDecimal alipayMoney = new BigDecimal(0.00);
		BigDecimal bankMoney = new BigDecimal(0.00);
		BigDecimal cashMoney = new BigDecimal(0.00);
		SummaryStatisticalReportsJasper jasper = new SummaryStatisticalReportsJasper();
		if(null != hisTransactionFlows && hisTransactionFlows.size() > 0) {
			for(HisTransactionFlow flow : hisTransactionFlows) {
				if(StringUtil.equals(org.getCode(), flow.getOrgNo()) && StringUtil.equals(flow.getPayBusinessType(), PayBusinssTypeEnum.RECHARGE.getCode())) {
					if(StringUtil.equals(flow.getPayType(), PayTypeEnum.WECHAT.getCode())) {
						wechatMoney = wechatMoney.add(flow.getPayAmount());
					}else if (StringUtil.equals(flow.getPayType(), PayTypeEnum.ALIPAY.getCode())) {
						alipayMoney = alipayMoney.add(flow.getPayAmount());
					}else if (StringUtil.equals(flow.getPayType(), PayTypeEnum.BANK.getCode())) {
						bankMoney = bankMoney.add(flow.getPayAmount());
					}else if (StringUtil.equals(flow.getPayType(), PayTypeEnum.CASH.getCode())) {
						cashMoney = cashMoney.add(flow.getPayAmount());
					}
				}
			}
		}
		jasper.setHosName(org.getName());
		jasper.setWxRecharge(formatDouble(wechatMoney));
		jasper.setAliRecharge(formatDouble(alipayMoney));
		jasper.setBankRecharge(formatDouble(bankMoney));
		jasper.setCashRecharge(formatDouble(cashMoney));
		jasperList.add(jasper);
	}
	
	private static double formatDouble(BigDecimal decimal) {
		double temp = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//		double temp = decimal.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
		return temp;
	}
}

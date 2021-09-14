package com.yiban.rec.web.recon.noncash;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.core.util.OprPageRequest;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.framework.export.view.excel.ExcelDecoratedEntry;
import com.yiban.framework.export.view.excel.ExcelResult;
import com.yiban.rec.domain.vo.FinanceVo;
import com.yiban.rec.service.FinanceService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.UserOrganizationPerService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumType;

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
 * 内容摘要:对账管理--->财务汇总查询
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
@RequestMapping("/admin/finance")
public class ReconciliationFinanceController extends CurrentUserContoller {

	@Autowired
	private FinanceService financeService;

	@Autowired
	private MetaDataService metaDataService;

	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private UserOrganizationPerService userOrganizationPerService;
	
	@Autowired
	private OrganizationService organizationService;

	@RequestMapping("")
	public String index(ModelMap model) {
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.valueAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		model.put("org", EnumType.DATA_SOURCE_TYPE_ORG_CHANNEL.getValue());
		model.put("accountDate", DateUtil.getSpecifiedDayBefore(new Date()));
		model.put("flag",CommonConstant.ALL_ID);
		return autoView("reconciliation/finance");
	}

	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	@ResponseBody
	public WebUiPage<Map<String, Object>> recDinanceQuery(FinanceVo fvo) throws BusinessException {
		OprPageRequest pagerequest = super.URL2PageRequest();
//		User user = currentUser();
//		List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
//		List<String> orgList = userOrganizationPerService.filtersOrg2List(orgListTemp);
		List<String> strList = getOrgListOfString(fvo);
		Page<Map<String, Object>> financePage = financeService.getFinanceData(pagerequest, fvo,strList);
		return toWebUIPage(financePage);
	}

	private List<String> getOrgListOfString(FinanceVo fvo) {
		List<Organization> orgList = organizationService.findByParentCode(fvo.getOrgNo());
		List<String> strList = new ArrayList<String>();
		strList.add(fvo.getOrgNo());
		if(null != orgList && orgList.size() > 0){
			for (Organization organization : orgList) {
				strList.add(organization.getCode());
			}
		}
		return strList;
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
	@Logable( operation = "导出财务汇总明细")
	@RequestMapping(value="/api/dcExcel",method=RequestMethod.GET) 
	public ModelAndView toDcExcel(FinanceVo fvo,ModelMap model, HttpServletRequest request){ 
		List<ExcelDecoratedEntry> ops = new ArrayList<ExcelDecoratedEntry>();
		List<String> strList = getOrgListOfString(fvo);
		ops.add(new ExcelDecoratedEntry("tradeDatatime", "账单日期"));
		ops.add(new ExcelDecoratedEntry("payAmount", "累计支付金额(元)","#.##"));
		ops.add(new ExcelDecoratedEntry("refundAmount", "累计退费金额(元)","#.##"));
		ops.add(new ExcelDecoratedEntry("payBusinessType", "业务类型"));
		ops.add(new ExcelDecoratedEntry("orgNo", "机构名称"));
		ops.add(new ExcelDecoratedEntry("payType", "支付类型"));
		ops.add(new ExcelDecoratedEntry("paySource", "支付来源"));
		List<Map<String,Object>> detailList = financeService.exportFinanceData(fvo,strList);
		ExcelResult viewExcel = new ExcelResult(detailList, ops,fvo.getStartDate()+"至"+fvo.getEndDate()+fvo.getOrgName()+"财务汇总统计",6);
		return new ModelAndView(viewExcel); 
	}

}

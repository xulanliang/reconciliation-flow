package com.yiban.rec.web.recon.noncash;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.framework.export.view.excel.ExcelDecoratedEntry;
import com.yiban.framework.export.view.excel.ExcelResult;
import com.yiban.rec.domain.HisPayResult;
import com.yiban.rec.domain.vo.TradeDetailQueryVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.ReconciliationService;
import com.yiban.rec.service.UserOrganizationPerService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumType;

import net.sf.json.JSONObject;

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
 * 内容摘要:对账管理--->交易明细查询
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
@RequestMapping("/admin/trade")
public class ReconciliationTradeController extends CurrentUserContoller {

	@Autowired
	private ReconciliationService reconciliationService;

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
		model.put("typesJSON", JSONObject.fromObject(ValueTexts.asMap(metaDataService.valueAsList())));
		model.put("orgJSON", JSONObject.fromObject(gatherService.getOrgMap()));
		model.put("org", EnumType.DATA_SOURCE_TYPE_ORG_CHANNEL.getValue());
		model.put("accountDate", DateUtil.getSpecifiedDayBefore(new Date()));
		model.put("flag",CommonConstant.ALL_ID);
		return autoView("reconciliation/trade");
	}
	@RestController
	@RequestMapping({"/admin/tradeData"})
	class TradeDataController extends BaseController {
		@GetMapping
		public WebUiPage<HisPayResult> recHistradeQuery(TradeDetailQueryVo cqvo) {
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			List<Organization> orgList = null;
			if(StringUtils.isNotBlank(cqvo.getOrgNo())){
				orgList = organizationService.findByParentCode(cqvo.getOrgNo());
			}
			Page<HisPayResult> hisPage = reconciliationService.getTradeDetailPageHis(cqvo,orgList,
					this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(hisPage);
		}
		
		//统计笔数与金额
		@GetMapping("/countSum")
		public ResponseResult getCollect(TradeDetailQueryVo vo){
			List<Organization> orgList = null;
			if(StringUtils.isNotBlank(vo.getOrgNo())){
				orgList = organizationService.findByParentCode(vo.getOrgNo());
			}
			Map<String,Object> map = reconciliationService.getTradeCollect(vo,orgList);
			return ResponseResult.success().data(map);
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
		public ModelAndView toDcExcel(TradeDetailQueryVo cqvo,ModelMap model, HttpServletRequest request){ 
			List<ExcelDecoratedEntry> ops = new ArrayList<ExcelDecoratedEntry>();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			User user = currentUser();
			List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
			ops.add(new ExcelDecoratedEntry("tradeDatatime", "账单日期"));
			ops.add(new ExcelDecoratedEntry("orgName", "机构名称"));
			ops.add(new ExcelDecoratedEntry("orderStateName", "订单状态"));
			ops.add(new ExcelDecoratedEntry("tradeCodeName", "交易类型"));
			ops.add(new ExcelDecoratedEntry("payBusinessTypeName", "业务类型"));
			ops.add(new ExcelDecoratedEntry("payTypeName", "支付类型"));
			ops.add(new ExcelDecoratedEntry("paySourceName", "支付来源"));
			ops.add(new ExcelDecoratedEntry("payTermNo", "终端号"));
			ops.add(new ExcelDecoratedEntry("deviceNo", "设备编码"));
			ops.add(new ExcelDecoratedEntry("custName", "客户名称"));
			ops.add(new ExcelDecoratedEntry("payAccount", "支付账号"));
			ops.add(new ExcelDecoratedEntry("payFlowNo", "支付流水号"));
			ops.add(new ExcelDecoratedEntry("payAmount", "金额(单位：元)"));
			ExcelResult viewExcel = null;
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			Pageable page = this.getPageRequest(CommonConstant.exportPageSize, sort);
			List<HisPayResult> hisList = reconciliationService.getRecCashHisList(cqvo,orgListTemp,page);
			try {
				viewExcel = new ExcelResult(hisList, ops,sdf.format(sdf.parse(cqvo.getStartDate()))+"至"+sdf.format(sdf.parse(cqvo.getEndDate()))+cqvo.getOrgName()+"平台交易明细",12);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new ModelAndView(viewExcel); 
		}
	}
}

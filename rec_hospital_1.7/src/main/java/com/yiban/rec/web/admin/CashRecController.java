package com.yiban.rec.web.admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.core.util.StringUtil;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.framework.export.view.excel.ExcelDecoratedEntry;
import com.yiban.framework.export.view.excel.ExcelResult;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.vo.TradeCheckFollowVo;
import com.yiban.rec.service.CashRecService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.Configure;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumTypeOfInt;

/**
 * 现金对账controller
 * 
 * @author clearofchina
 *
 */
@Controller
@RequestMapping(value = "admin/cashrec")
public class CashRecController extends CurrentUserContoller {

	@Autowired
	private PropertiesConfigService propertiesConfigService;
	@Autowired
	private CashRecService cashRecService;
	@Autowired
	private MetaDataService metaDataService;
	@Autowired
	private GatherService gatherService;

	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String index(ModelMap model, String orgNo, String startDate, String endDate) {
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.NameAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		model.put("orgNo", StringUtil.isEmpty(orgNo) ? Configure.getPropertyBykey("yiban.projectid") : orgNo);
		if (StringUtils.isNotBlank(startDate)) {
			model.put("startDate", startDate);
		} else {
			model.put("startDate", DateUtil.getSpecifiedDayBefore(new Date()));
		}
		if (StringUtils.isNotBlank(endDate)) {
			model.put("endDate", endDate);
		} else {
			model.put("endDate", DateUtil.getSpecifiedDayBefore(new Date()));
		}
//		model.put("startDate", DateUtil.getNDayOfBeforeStart(new Date(), 1));
//		model.put("endDate", DateUtil.getNDayOfBeforeEnd(new Date(), 1));
		return autoView("reconciliation/cashRec");
	}

	/**
	 * 现金对账汇总数据
	 * 
	 * @param orgNo
	 * @param date
	 * @return
	 */
	@RequestMapping(value = "data", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult data(@RequestParam("orgNo") String orgNo, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) {
		// 存在日期，先把日期格式转化成年月日格式，后面要改
		if (StringUtils.isBlank(startDate)) {
			startDate = DateUtil.getSpecifiedDayBefore(new Date());
		} else {
			startDate = DateUtil.formatStringDate("yyyy-MM-dd", startDate);
		}
		if (StringUtils.isBlank(endDate)) {
			endDate = DateUtil.getSpecifiedDayBefore(new Date());
		} else {
			endDate = DateUtil.formatStringDate("yyyy-MM-dd", endDate);
		}
		if (orgNo == null || orgNo.trim().equals("")) {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			orgNo = orgCode;
		}
		ResponseResult result = cashRecService.getFollowRecMap(orgNo, startDate, endDate);
		return result;
	}
	
	@RequestMapping(value = "exceptionTrade/detail", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult exceptionTradeDetail(@RequestParam(value = "recHisId") String recHisId,
			@RequestParam(value = "recThirdId") String recThirdId,
			@RequestParam(value = "businessNo") String businessNo, @RequestParam(value = "orgNo") String orgNo,
			String tradeTime) {
		return cashRecService.getExceptionTradeDetail(recHisId, recThirdId, businessNo, orgNo, tradeTime);
	}

	@RequestMapping(value = "dealFollow", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult dealFollow(@RequestParam Long id, @RequestParam(required = false) String description,
			@RequestParam(required = false) MultipartFile file) {
		if ((description == null || description.trim().equals("")) && file == null) {
			return ResponseResult.failure("请输入原因或者上传图片");
		}
		if (null != description && description.length() >= 200) {
			return ResponseResult.failure("请输入文字少于200个");
		}
		User user = currentUser();
		return cashRecService.deal(id, description, file, user.getName());
	}

	/**
	 * 异常账单查询
	 * 
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "exceptionTrade", method = RequestMethod.GET)
	@ResponseBody
	public WebUiPage<TradeCheckFollow> findByOrgNoAndTradeDate(TradeCheckFollowVo vo) {
		PageRequest pageable = this.getRequestPageabledWithInitSort(this.getIdDescSort());
		if (StringUtils.isBlank(vo.getOrgNo())) {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			vo.setOrgNo(orgCode);
		}
		Page<TradeCheckFollow> data = cashRecService.findByOrgNoAndTradeDate(vo, pageable);
		for (TradeCheckFollow tcf : data.getContent()) {
			for (TradeCheckFollow tcf2 : data.getContent()) {
				try {
					if ((tcf.getTradeName() != null) && (tcf2.getTradeName() != null)) {
						if (tcf.getId() != tcf2.getId() && tcf.getBusinessNo().equals(tcf2.getBusinessNo())
								&& tcf.getTradeAmount().compareTo(tcf2.getTradeAmount()) == 0
								&& ((tcf.getTradeName().equals(EnumTypeOfInt.PAY_CODE.getValue())
										&& tcf2.getTradeName().equals(EnumTypeOfInt.REFUND_CODE.getValue()))
										|| (tcf.getTradeName().equals(EnumTypeOfInt.REFUND_CODE.getValue())
												&& tcf2.getTradeName().equals(EnumTypeOfInt.PAY_CODE.getValue())))) {
							tcf.setIsCorrection(EnumTypeOfInt.TRADE_CODE_REVERSAL.getValue());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return this.toWebUIPage(data);
	}

	/**
	 * 导出Excel
	 * 
	 * @param orgNo
	 * @param date
	 * @return
	 * @throws Exception
	 */
	@Logable(operation = "导出现金对账账单")
	@RequestMapping(value = "dcExcel", method = RequestMethod.GET)
	public ModelAndView dcExcel(@RequestParam("orgNo") String orgNo, @RequestParam("orgName") String orgName,
			@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
			HttpServletRequest request) throws Exception {
		if (StringUtils.isBlank(startDate)) {
			startDate = DateUtil.getSpecifiedDayBefore(new Date());
		}
		if (StringUtils.isBlank(endDate)) {
			endDate = DateUtil.getSpecifiedDayBefore(new Date());
		}
		if (orgNo == null || orgNo.trim().equals("")) {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			orgNo = orgCode;
		}

		TradeCheckFollowVo vo = new TradeCheckFollowVo();
		vo.setDataSourceType("all");
		vo.setOrgNo(orgNo);
		vo.setStartDate(startDate);
		vo.setEndDate(endDate);
		Sort sort = new Sort(Direction.DESC, "tradeDate");
		Pageable page = this.getPageRequest(CommonConstant.exportPageSize, sort);
		Page<TradeCheckFollow> result = cashRecService.findByOrgNoAndTradeDate(vo, page);

		List<ExcelDecoratedEntry> ops = new ArrayList<ExcelDecoratedEntry>();
		ops.add(new ExcelDecoratedEntry("payNo", "支付方流水号"));
		ops.add(new ExcelDecoratedEntry("hisFlowNo", "his流水号"));
		ops.add(new ExcelDecoratedEntry("userId", "柜员号"));
		ops.add(new ExcelDecoratedEntry("billSource", "渠道名称"));
		ops.add(new ExcelDecoratedEntry("tradeAmount", "金额(元)", "#.##"));
		ops.add(new ExcelDecoratedEntry("patientName", "患者名称"));
		ops.add(new ExcelDecoratedEntry("tradeTime", "交易时间"));
		ops.add(new ExcelDecoratedEntry("checkStateValue", "异常类型"));
		ExcelResult viewExcel = new ExcelResult(result.getContent(), ops);
		String fileName = startDate + "至" + endDate + orgName + "现金对账";
		viewExcel.setFileName(fileName);
		return new ModelAndView(viewExcel);
	}
	
    @Logable(operation = "查询图片")
    @RequestMapping("/readImage")
    public void readImage(@RequestParam(required = true) String adress, HttpServletRequest request,
                           HttpServletResponse response) {
        String fileLocation = Configure.getPropertyBykey("file.location");
        fileLocation = fileLocation + adress;
        File file = new File(fileLocation);
        if (file != null && file.exists()) {
            try {
                response.getOutputStream().write(FileUtils.readFileToByteArray(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

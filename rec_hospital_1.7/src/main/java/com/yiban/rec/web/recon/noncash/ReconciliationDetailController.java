package com.yiban.rec.web.recon.noncash;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
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
import org.springframework.web.servlet.ModelAndView;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.framework.export.view.excel.ExcelDecoratedEntry;
import com.yiban.framework.export.view.excel.ExcelResult;
import com.yiban.rec.domain.Reconciliation;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.baseinfo.ShopInfo;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.RecQueryVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.service.ReconciliationService;
import com.yiban.rec.service.ShopInfoService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.ExportExcel;
import com.yiban.rec.util.StringUtil;

/**
 * 
 * <p>
 * ????????????:ReconciliationController.java
 * <p>
 * <p>
 * ????????????:????????????
 * <p>
 * ????????????:???????????????????????????????????????????????????(C)2017
 * </p>
 * <p>
 * ????????????:????????????--->??????????????????
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
@RequestMapping("/admin/reconciliation/detail")
public class ReconciliationDetailController extends CurrentUserContoller {

	@Autowired
	private ReconciliationService reconciliationService;
	
	@Autowired
	private MetaDataService metaDataService;

	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private ShopInfoService shopInfoService;
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	
	@Autowired
	private HospitalConfigService hospitalConfigService;

	@RequestMapping("")
	public String index(ModelMap model,HttpServletRequest request,String orgNo,String payDate) {
		String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
		//???????????????????????????????????????????????????????????????????????????????????????
		int fag = 0;
		if(orgNo != null || payDate != null) {
			fag = 1;
			model.put("accountDate", payDate);
			model.put("accountOrgNo", orgNo);
		}else {
			model.put("accountDate", DateUtil.getSpecifiedDayBefore(new Date()));
		}
		//?????????????????????????????????????????????????????????/???????????????????????????/???????????????
		String recType = getPayType();
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.getNameValueAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		model.put("deviceNo", CommonConstant.DEVICE_NO);
		model.put("orgNo", orgCode);
		model.put("flag",fag);
		model.put("recType", recType);
		
		return autoView("reconciliation/detail");
	}
	
	@RequestMapping("/check")
	public String indexDetail(@RequestParam("orgNo") Long orgNo,@RequestParam("payDate") String payDate,
			@RequestParam("isDifferent") Integer isDifferent,ModelMap model) {
			model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.valueAsList())));
			model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
			model.put("accountOrgNo", orgNo);
			model.put("accountDate", payDate);
			model.put("isDifferent", isDifferent);
			return autoView("reconciliation/detail");
	}

	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	@ResponseBody
	public WebUiPage<Reconciliation> recDetailQuery(RecQueryVo rqvo) {
		
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd"); 
		if(null != rqvo.getStartTime()) {
			try {
				rqvo.setStartDate(formatter.parse(rqvo.getStartTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(null != rqvo.getEndTime()) { 
			
			try {
				rqvo.setEndDate(formatter.parse(rqvo.getEndTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(!StringUtil.isNullOrEmpty(rqvo.getBankTypeId())){
			List<ShopInfo> list = shopInfoService.getAllShopInfo();
			for(ShopInfo shopInfo : list){
				if(shopInfo.getMetaDataBankId() != null && rqvo.getBankTypeId().equals(shopInfo.getMetaDataBankId().toString())){
					rqvo.getPayShopNoList().add(shopInfo.getPayShopNo());
				}
			}
			if(StringUtil.isNullOrEmpty(rqvo.getPayShopNoList())){
				rqvo.getPayShopNoList().add("0");
			}
		}
		Sort sort = new Sort(Direction.DESC, "isDifferent");
		PageRequest pageable = this.getRequestPageabledWithInitSort(sort);
		try {
			Page<Reconciliation> recPage = reconciliationService.getRecpage(rqvo, pageable);
			return toWebUIPage(recPage);
		} catch (Exception e) {
			e.printStackTrace();
			return toWebUIPage(null);
		}
	}
	
	@RequestMapping(value = "/detailTwo", method = RequestMethod.GET)
	@ResponseBody
	public WebUiPage<TradeCheckFollow> recTwoDetailQuery(RecQueryVo rqvo) {
		
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd"); 
		if(null != rqvo.getStartTime()) {
			try {
				rqvo.setStartDate(formatter.parse(rqvo.getStartTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(null != rqvo.getEndTime()) {
			
			try {
				rqvo.setEndDate(formatter.parse(rqvo.getEndTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(!StringUtil.isNullOrEmpty(rqvo.getBankTypeId())){
			List<ShopInfo> list = shopInfoService.getAllShopInfo();
			for(ShopInfo shopInfo : list){
				if(shopInfo.getMetaDataBankId() != null && rqvo.getBankTypeId().equals(shopInfo.getMetaDataBankId().toString())){
					rqvo.getPayShopNoList().add(shopInfo.getPayShopNo());
				}
			}
			if(StringUtil.isNullOrEmpty(rqvo.getPayShopNoList())){
				rqvo.getPayShopNoList().add("0");
			}
		}
		Sort sort = new Sort(Direction.ASC, "tradeTime");
		PageRequest pageable = this.getRequestPageabledWithInitSort(sort);
		try {
//			List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
			Page<TradeCheckFollow> recPage = reconciliationService.getTwoRecpage(rqvo, pageable);
			
			return toWebUIPage(recPage);
		} catch (Exception e) {
			e.printStackTrace();
			return toWebUIPage(null);
		}
	}

	/**
	 * ????????????
	 * @param detail
	 * @param result
	 * @return
	 */
	@Logable( operation = "?????????????????????")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult add(@RequestParam("detailId") Long detailId, @RequestParam("platformAmount") BigDecimal platformAmount,
			@RequestParam("remarkInfo") String remarkInfo,@RequestParam("handleCode") Integer handleCode) throws BusinessException {
		User user = currentUser();
		ResponseResult res = reconciliationService.edit(detailId, platformAmount, remarkInfo,handleCode,user.getLoginName());
		return res;
	}

	/**
	* @date???2017???4???16??? 
	* @Description?????????????????????
	* @param flowNo
	* @return
	* @throws BusinessException: ??????????????????
	* @return ResponseResult: ???????????????
	* @throws
	 */
	@RequestMapping(value = "platlog", method = RequestMethod.GET)
	@ResponseBody
	public ResponseResult getPlatformflowLog(@RequestParam("flowNo") String flowNo) throws BusinessException {
		ResponseResult rs = ResponseResult.success();
		Map<String,Object> map = reconciliationService.getPlatformflowLog(flowNo);
		rs.data(map);
		return rs;
	}
	
	/**
	* @date???2017???4???7??? 
	* @Description?????????
	* @param model
	* @param request
	* @return: ??????????????????
	* @return ModelAndView: ???????????????
	* @throws
	 */
	@Logable(operation = "??????????????????")
	@RequestMapping(value="/api/dcExcel",method=RequestMethod.GET) 
	public ModelAndView toDcExcel(RecQueryVo rqvo,ModelMap model, HttpServletRequest request) throws BusinessException{ 
		
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd"); 
		if(null != rqvo.getStartTime()) { 
			try {
				rqvo.setStartDate(formatter.parse(rqvo.getStartTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(null != rqvo.getEndTime()) { 
			
			try {
				rqvo.setEndDate(formatter.parse(rqvo.getEndTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		List<ExcelDecoratedEntry> ops = new ArrayList<ExcelDecoratedEntry>();
		
		ops.add(new ExcelDecoratedEntry("payDateStamName", "????????????"));
		ops.add(new ExcelDecoratedEntry("orgName", "????????????"));
		ops.add(new ExcelDecoratedEntry("tradeCodeName", "????????????"));
		ops.add(new ExcelDecoratedEntry("payBusinessTypeName", "????????????"));
		ops.add(new ExcelDecoratedEntry("custName", "????????????"));
		ops.add(new ExcelDecoratedEntry("custIdentifyName", "????????????"));
		ops.add(new ExcelDecoratedEntry("flowNo", "???????????????"));
		ops.add(new ExcelDecoratedEntry("thirdAmount", "????????????(???)","#.##"));
		ops.add(new ExcelDecoratedEntry("payTypeName", "????????????"));
		ops.add(new ExcelDecoratedEntry("orgAmount", "his????????????(???)","#.##"));
		ops.add(new ExcelDecoratedEntry("deviceNo", "????????????"));
		ops.add(new ExcelDecoratedEntry("platformAmount", "????????????(???)","#.##"));
		ops.add(new ExcelDecoratedEntry("payTermNo", "???????????????"));
		ops.add(new ExcelDecoratedEntry("payAccount", "????????????"));
		ops.add(new ExcelDecoratedEntry("orderStateName", "????????????"));
		ops.add(new ExcelDecoratedEntry("reconciliationDate", "????????????"));
		ops.add(new ExcelDecoratedEntry("remarkInfo", "??????"));
		Sort sort = new Sort(Direction.DESC, "isDifferent");
		Pageable page = this.getPageRequest(CommonConstant.exportPageSize, sort);
		List<Reconciliation> detailList = reconciliationService.getRecDetailList(rqvo,page);
		ExcelResult viewExcel = new ExcelResult(detailList, ops,rqvo.getStartTime()+"???"+rqvo.getEndTime()+rqvo.getOrgName()+"????????????",17);
		return new ModelAndView(viewExcel); 
	}
	
	/**
	* @date???2017???4???7??? 
	* @Description?????????
	* @param model
	* @param request
	* @return: ??????????????????
	* @return ModelAndView: ???????????????
	* @throws
	 */
	@Logable(operation = "??????????????????")
	@RequestMapping(value="/api/dcTwoExcel",method=RequestMethod.GET) 
	public void toDcTwoExcel(RecQueryVo rqvo,ModelMap model, HttpServletRequest request, HttpServletResponse response) throws IOException{ 
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd"); 
		if(null != rqvo.getStartTime()) { 
			try {
				rqvo.setStartDate(formatter.parse(rqvo.getStartTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(null != rqvo.getEndTime()) { 
			
			try {
				rqvo.setEndDate(formatter.parse(rqvo.getEndTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Sort sort = new Sort(Direction.ASC, "tradeTime");
		Pageable page = this.getPageRequest(CommonConstant.exportPageSize, sort);
		
		response.setContentType("application/vnd.ms-excel");
		response.setContentType("application/octet-stream;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		String fileName = rqvo.getStartTime()+"???"+rqvo.getEndTime()+rqvo.getOrgName()+"????????????";
		response.addHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes(),"iso-8859-1") + ".xls");
		
		
		ExportExcel ee = new ExportExcel();
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = ee.getSheet(wb, rqvo.getStartTime()+"???"+rqvo.getEndTime()+rqvo.getOrgName()+"????????????");
		
		//????????????
		CellStyle cellStyle = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)14, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)0,(short)0,(short)0,(short)0);
		Row titleRow = ee.getRow(sheet, 0, cellStyle, 35);
		
		ee.getCell(titleRow, 0, cellStyle, rqvo.getStartTime()+"???"+rqvo.getEndTime()+rqvo.getOrgName()+"????????????");
		sheet.addMergedRegion(new CellRangeAddress(0,0,0,7));
		
		//????????????
		CellStyle cellStyleHead = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)12, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)1,(short)1,(short)1,(short)1);
		Row headRow = ee.getRow(sheet, 1, null, 20);
		
		ee.getCell(headRow, 0, cellStyleHead, "????????????" );
		ee.getCell(headRow, 1, cellStyleHead, "????????????" );
		ee.getCell(headRow, 2, cellStyleHead, "????????????" );
		ee.getCell(headRow, 3, cellStyleHead, "????????????" );
		ee.getCell(headRow, 4, cellStyleHead, "????????????" );
		ee.getCell(headRow, 5, cellStyleHead, "????????????" );
		ee.getCell(headRow, 6, cellStyleHead, "???????????????" );
		ee.getCell(headRow, 7, cellStyleHead, "????????????(???)" );
		ee.getCell(headRow, 8, cellStyleHead, "????????????" );
		ee.getCell(headRow, 9, cellStyleHead, "????????????" );

		CellStyle cellStyleContent = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)12, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)1,(short)1,(short)1,(short)1);
		@SuppressWarnings("unchecked")
		List<TradeCheckFollow> detailList = reconciliationService.getRecTwoDetailList(rqvo,page);
		if(!StringUtil.isNullOrEmpty(detailList)){
			for(int i=0;i<detailList.size();i++){
				 Row platRow = ee.getRow(sheet, i+2, null, 20);
				 
				 String tradeDate = detailList.get(i).getTradeDate()==null?"":detailList.get(i).getTradeDate().toString();
				 String billSource = detailList.get(i).getBillSource();
				 String orgNo = detailList.get(i).getOrgNo();
				 String patType = detailList.get(i).getPatType();
				 String tradeName = detailList.get(i).getTradeName();
				 String custName = detailList.get(i).getPatientName();
				 String businessNo = detailList.get(i).getBusinessNo();
				 String tradeAmount = String.valueOf(detailList.get(i).getTradeAmount());
				 String payName = detailList.get(i).getPayName();
				 String tradeTime = detailList.get(i).getTradeTime()==null?"-":detailList.get(i).getTradeTime().toString();

				 ee.getCell(platRow, 0, cellStyleContent, tradeDate);
				 ee.getCell(platRow, 1, cellStyleContent, billSource);
				 ee.getCell(platRow, 2, cellStyleContent, orgNo);
				 ee.getCell(platRow, 3, cellStyleContent, patType);
				 ee.getCell(platRow, 4, cellStyleContent, tradeName);
				 ee.getCell(platRow, 5, cellStyleContent, custName);
				 ee.getCell(platRow, 6, cellStyleContent, businessNo);
				 ee.getCell(platRow, 7, cellStyleContent, tradeAmount);
				 ee.getCell(platRow, 8, cellStyleContent, payName);
				 ee.getCell(platRow, 9, cellStyleContent, tradeTime);
			}
		}
		
		sheet.setColumnWidth(0, 5000);
		sheet.setColumnWidth(1, 5000);
		sheet.setColumnWidth(2, 5000);
		sheet.setColumnWidth(3, 5000);
		sheet.setColumnWidth(4, 5000);
		sheet.setColumnWidth(5, 5000);
		sheet.setColumnWidth(6, 9000);
		sheet.setColumnWidth(7, 5000);
		sheet.setColumnWidth(8, 5000);
		sheet.setColumnWidth(9, 9000);
		
		OutputStream out = response.getOutputStream();
		wb.write(out);
		out.flush();
		out.close();
	}
	
	private String getPayType(){
		String str = null;
		AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
		if(null != hConfig){
//			str = hConfig.getCheckWays();
		}
		return str;
	}

}

package com.yiban.rec.web.admin;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.framework.operatelog.util.OperationLogUtil;
import com.yiban.rec.domain.FollowRecResult;
import com.yiban.rec.domain.Reconciliation;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.RecQueryVo;
import com.yiban.rec.service.AutoReconciliationService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.service.ReconciliationService;
import com.yiban.rec.service.ThridFollowingRecService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.Configure;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.ExportExcel;
import com.yiban.rec.util.RestUtil;
import com.yiban.rec.util.StringUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/admin/reconciliation/thridFollowing")
public class NextDayThridAccountController extends CurrentUserContoller {


	
	@Autowired
	private MetaDataService metaDataService;
	@Autowired
	private ReconciliationService reconciliationService;
	
	@Autowired
	private ThridFollowingRecService thridFollowingRecService;
	
	@Autowired
	private AutoReconciliationService autoReconciliationService;
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private HospitalConfigService hospitalConfigService;
	
	
	@RequestMapping("")
	public String index(ModelMap model) {
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.NameAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		model.put("orgNo", Configure.getPropertyBykey("yiban.projectid"));
		model.put("tradeDate", DateUtil.getSpecifiedDayBefore(new Date()));
		return autoView("reconciliation/thridFollowing");
	}
	
	
	@RestController
	@RequestMapping({"/admin/nextDayThridAccount/data"})
	class NextDayThridAccountDataController extends BaseController {
		/**
		 * 获取对账汇总信息和异常账单明细
		 * @param orgNo
		 * @param payDate
		 * @return
		 */
		@GetMapping
		public ResponseResult recGatherQuery(String orgNo,String tradeDate) {
			ResponseResult rs = ResponseResult.success();
			try {
				List<FollowRecResult> list = new ArrayList<FollowRecResult>();
				if(StringUtils.isBlank(tradeDate)) {
					tradeDate=DateUtil.getSpecifiedDayBefore(new Date());
				}
				if(StringUtils.isBlank(orgNo)) {
					orgNo=Configure.getPropertyBykey("yiban.projectid");
				}
					
				AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
//				hConfig.setOrgCode(orgNo);
				List<FollowRecResult> parentList = thridFollowingRecService.getFollowRecMap(tradeDate,tradeDate,hConfig);
				list.addAll(parentList);
				rs.data(list);
				
			} catch (Exception e) {
				rs = ResponseResult.failure("异常");
				e.printStackTrace();
				return rs;
			}
			
			return rs;
		}
		
		@GetMapping("/exceptionTrade")
		public WebUiPage<Reconciliation> findByOrgNoAndTradeDate(String orgNo, String tradeDate) {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			RecQueryVo rqvo=new RecQueryVo();
			rqvo.setOrgNo(orgNo);
			try {
				if(StringUtils.isNotBlank(tradeDate)) {
					rqvo.setStartDate(sdf.parse(tradeDate));
					rqvo.setEndDate(sdf.parse(tradeDate));
				}else {
					rqvo.setStartDate(sdf.parse(DateUtil.getSpecifiedDayBefore(new Date())));
					rqvo.setEndDate(sdf.parse(DateUtil.getSpecifiedDayBefore(new Date())));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Sort sort = new Sort(Direction.DESC, "payFlowNo");
			PageRequest pageable = this.getRequestPageabledWithInitSort(sort);
			Page<Reconciliation> data = reconciliationService.getRecpage(rqvo, pageable);
			return this.toWebUIPage(data);
		}
		
		/**
		 * 隔日对账
		 * @param orgNo
		 * @param tradeDate
		 * @return
		 */
		@Logable(operation = "隔日对账开始")
		@PostMapping("/account")
		public ResponseResult startRec(@RequestParam("orgNo") String orgNo,@RequestParam("tradeDate") String tradeDate) {
			//机构的医保配置
			AppRuntimeConfig config = hospitalConfigService.loadConfig();
			return autoReconciliationService.isAutoRecSuccess(orgNo, tradeDate,config);
		}

		/**
		 * 隔日对账退费
		 * @param id
		 * @param handleRemark
		 * @return
		 * @throws BusinessException
		 */
		@Logable(operation = "隔日对账-退费")
		@PostMapping("/{orderNo}/refund")
		public ResponseResult startRefund(@RequestParam(value = "orderNo", required = false) String orderNo)throws BusinessException{
			 String resutl = "";
			try {
				//1、数据主装  2、调用接口
				refund(orderNo,"退费");
				/*User user = currentUser();
				resutl = followingRecService.checkRefund(id, user);*/
			} catch (Exception e) {
				e.printStackTrace();
				OperationLogUtil.quickSave("隔日对账-退费失败"+e);
				return ResponseResult.failure("退费失败");
			}
			return ResponseResult.success(resutl);
		}
		
		@Logable( operation = "导出对账明细")
		@GetMapping("/dcExcel")
		public ResponseResult recGatherExport(@RequestParam("orgNo") String orgNo,@RequestParam("tradeDate") String tradeDate,@RequestParam("orgName") String orgName,
				HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException {
				response.setContentType("application/vnd.ms-excel;charset=utf-8");
				request.setCharacterEncoding("utf-8");
				response.setHeader("Pragma", "no-cache");
				response.setHeader("Cache-Control", "no-cache");
				String fileName = tradeDate+orgName+"三方对账汇总";
				response.addHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes(),"iso-8859-1") + ".xls");
				Map<String, String> typeMap = ValueTexts.asMap(metaDataService.NameAsList());
				Map<String, Object> orgMap = gatherService.getOrgMap();
				
				List<FollowRecResult> platMap = new ArrayList<FollowRecResult>();
				AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
//				hConfig.setOrgCode(orgNo);
				List<FollowRecResult> parentList = thridFollowingRecService.getFollowRecMap(tradeDate,tradeDate,hConfig);
				platMap.addAll(parentList);
				Sort sort = new Sort(Direction.DESC, "payFlowNo");
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				RecQueryVo rqvo=new RecQueryVo();
				rqvo.setOrgNo(orgNo);
				if(StringUtils.isNotBlank(tradeDate)) {
					rqvo.setStartDate(sdf.parse(tradeDate));
					rqvo.setEndDate(sdf.parse(tradeDate));
				}
				List<Reconciliation>  list = reconciliationService.getRecpageNopage(rqvo,sort);
				//List<TradeCheckFollow> list = followingRecService.findByOrgNoAndTradeDateNoPage(orgNo, tradeDate);
				ExportExcel ee = new ExportExcel();
				Workbook wb = new HSSFWorkbook();
				Sheet sheet = ee.getSheet(wb, tradeDate+orgName+"三方对账汇总");
				//标题样式
				CellStyle cellStyle = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)16, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)0,(short)0,(short)0,(short)0);
				Row titleRow = ee.getRow(sheet, 0, cellStyle, 35);
				
				ee.getCell(titleRow, 0, cellStyle, tradeDate+orgName+"三方对账汇总");
				sheet.addMergedRegion(new CellRangeAddress(0,0,0,9));
				
				//头部样式
				CellStyle cellStyleHead = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)12, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)1,(short)1,(short)1,(short)1);
				Row headRow = ee.getRow(sheet, 1, null, 20);
				
				//总账
				ee.getCell(headRow, 0, cellStyleHead, "患者类型");
				ee.getCell(headRow, 1, cellStyleHead, "账单日期");
				ee.getCell(headRow, 2, cellStyleHead, "HIS总金额(元)");
				ee.getCell(headRow, 3, cellStyleHead, "支付渠道交易总金额(元)");
				ee.getCell(headRow, 4, cellStyleHead, "平台交易总金额(元)");
				ee.getCell(headRow, 5, cellStyleHead, "支付宝总金额(元)");
				ee.getCell(headRow, 6, cellStyleHead, "微信总金额(元)");
				ee.getCell(headRow, 7, cellStyleHead, "银行总金额(元)");
				ee.getCell(headRow, 8, cellStyleHead, "社保总金额(元)");
				ee.getCell(headRow, 9, cellStyleHead, "异常结果");
				

				CellStyle cellStyleContent = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)12, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)1,(short)1,(short)1,(short)1);
				if(!StringUtil.isNullOrEmpty(platMap)){
					for(int i=0;i<platMap.size();i++){
						FollowRecResult vo = platMap.get(i);
						 Row platRow = ee.getRow(sheet, i+2, null, 40);
						 ee.getCell(platRow, 0, cellStyleContent,typeMap.get(vo.getPatType()));
						 ee.getCell(platRow, 1, cellStyleContent, String.valueOf(vo.getTradeDate()));
						 ee.getCell(platRow, 2, cellStyleContent, String.valueOf(vo.getHisAllAmount()==null?"0":vo.getHisAllAmount()));
						 ee.getCell(platRow, 3, cellStyleContent, String.valueOf(vo.getPayAllAmount()==null?"0":vo.getPayAllAmount()));
						 ee.getCell(platRow, 4, cellStyleContent, String.valueOf(vo.getPayAllAmount()==null?"0":vo.getRecPayAllAmount()));
						 ee.getCell(platRow, 5, cellStyleContent, String.valueOf(vo.getAlipayAllAmount()==null?"0":vo.getAlipayAllAmount()));
						 ee.getCell(platRow, 6, cellStyleContent, String.valueOf(vo.getWechatAllAmount()==null?"0":vo.getWechatAllAmount()));
						 ee.getCell(platRow, 7, cellStyleContent, String.valueOf(vo.getBankAllAmount()==null?"0":vo.getBankAllAmount()));
						 ee.getCell(platRow, 8, cellStyleContent, String.valueOf(vo.getBankAllAmount()==null?"0":vo.getSocialInsuranceAmount()));
						 ee.getCell(platRow, 9, cellStyleContent, String.valueOf(vo.getExceptionResult()==null?"0":vo.getExceptionResult()));
					}
				}
				//异常账单
				Row brRow = ee.getRow(sheet, 6, null, 35);
				ee.getCell(brRow, 0, ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)16, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)0,(short)0,(short)0,(short)0), "异常账单");
				sheet.addMergedRegion(new CellRangeAddress(6,6,0,9));
				
				Row hjRow = ee.getRow(sheet, 7, null, 20);
				ee.getCell(hjRow, 0, cellStyleHead, "院区");
				ee.getCell(hjRow, 1, cellStyleHead, "支付商户流水号");
				ee.getCell(hjRow, 2, cellStyleHead, "支付类型");
				ee.getCell(hjRow, 3, cellStyleHead, "订单状态");
				ee.getCell(hjRow, 4, cellStyleHead, "his金额");
				ee.getCell(hjRow, 5, cellStyleHead, "平台金额 ");
				ee.getCell(hjRow, 6, cellStyleHead, "支付渠道金额");
				ee.getCell(hjRow, 7, cellStyleHead, "患者名称");
				ee.getCell(hjRow, 8, cellStyleHead, "交易时间");
				ee.getCell(hjRow, 9, cellStyleHead, "业务类型");
				if(!StringUtil.isNullOrEmpty(list)){
					for(int i=0;i<=list.size()-1;i++){
						Reconciliation rec = list.get(i);
						 Row hjMisRow = ee.getRow(sheet, i+8, null, 20);
						 	ee.getCell(hjMisRow, 0, cellStyleHead, (String)orgMap.get(rec.getOrgNo()));
							ee.getCell(hjMisRow, 1, cellStyleHead, rec.getPayFlowNo());
							ee.getCell(hjMisRow, 2, cellStyleHead, typeMap.get(rec.getPayType()));
							ee.getCell(hjMisRow, 3, cellStyleHead, typeMap.get(rec.getOrderState()));
							ee.getCell(hjMisRow, 4, cellStyleHead, rec.getOrgAmount().toString());
							ee.getCell(hjMisRow, 5, cellStyleHead, rec.getPlatformAmount().toString());
							ee.getCell(hjMisRow, 6, cellStyleHead, rec.getThirdAmount().toString());
							ee.getCell(hjMisRow, 7, cellStyleHead, rec.getCustName());
							ee.getCell(hjMisRow, 8, cellStyleHead, String.valueOf(DateUtil.transferDateToString("yyyy-MM-dd HH:mm:ss", rec.getReconciliationDate())));
							ee.getCell(hjMisRow, 9, cellStyleHead, typeMap.get(rec.getPayBusinessType()));
					}
				}
				sheet.setColumnWidth(0, 8000);
				sheet.setColumnWidth(1, 4000);
				sheet.setColumnWidth(2, 4000);
				sheet.setColumnWidth(3, 4000);
				sheet.setColumnWidth(4, 5000);
				sheet.setColumnWidth(5, 3000);
				sheet.setColumnWidth(6, 8000);
				sheet.setColumnWidth(7, 8000);
				sheet.setColumnWidth(8, 8000);
				sheet.setColumnWidth(9, 8000);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.flush();
				out.close();
				return null;
		}
	}
	private String refund(String orderNo,String reason) {
		String url=propertiesConfigService.findValueByPkey(ProConstants.payRefundUrl);
		Map<String,Object> map = new HashMap<String,Object>(10);
		map.put("orderNo", orderNo);
		map.put("reason", reason);
		String retStr=null;
		try {
			retStr = new RestUtil().doPost(url, map, CommonConstant.CODING_FORMAT);
			JSONObject json =JSONObject.fromObject(retStr);
			if(json.getBoolean("success")) {
				return "success";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "fail";
	}
	
	/*
	private String formatterBusinessType(String code) {
		String name = "-";
		List<ValueTextable<String>> datas = metaDataService.NameAsList();
		for(ValueTextable<String> str : datas) {
			if(str.getValue().equals(code)) {
				name = str.getText();
			}
		}
		return name;	
	}*/
	
}

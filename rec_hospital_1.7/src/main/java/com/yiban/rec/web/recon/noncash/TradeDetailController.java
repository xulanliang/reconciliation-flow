package com.yiban.rec.web.recon.noncash;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.framework.operatelog.util.OperationLogUtil;
import com.yiban.rec.dao.recon.TradeCheckFollowDealDao;
import com.yiban.rec.domain.recon.TradeCheckFollowDeal;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.DataSource;
import com.yiban.rec.domain.vo.HisRequestVo;
import com.yiban.rec.domain.vo.HisResponseVo;
import com.yiban.rec.domain.vo.RefundVo;
import com.yiban.rec.domain.vo.TradeDetailVo;
import com.yiban.rec.domain.vo.TradeHISDetailVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.HisService;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.service.OrderUploadService;
import com.yiban.rec.service.ReconciliationService;
import com.yiban.rec.service.RefundService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.Configure;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumType;
import com.yiban.rec.util.ExportDataUtil;
import com.yiban.rec.util.HisInterfaceType;
import com.yiban.rec.util.JsonChangeVo;
import com.yiban.rec.util.JsonUtil2;
import com.yiban.rec.util.StringUtil;
import com.yiban.rec.util.TitleStateEnum;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Controller
@RequestMapping("/admin/tradeDetail")
public class TradeDetailController extends CurrentUserContoller {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ReconciliationService reconciliationService;

	@Autowired
	private MetaDataService metaDataService;

	@Autowired
	private GatherService gatherService;

	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private TradeCheckFollowDealDao tradeCheckFollowDealDao;
	@Autowired
	private OrderUploadService orderUploadService;
	@Autowired
	private RefundService refundService;
	@Autowired
	private HospitalConfigService hospitalConfigService;
	@Autowired
	private HisService hisService;
	@Autowired
	private PropertiesConfigService propertiesConfigService;

	@RequestMapping("")
	public String index(ModelMap model, String orgNo, String date, String Order_State) {
		AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
		model.put("typesJSON", JSONObject.fromObject(ValueTexts.asMap(metaDataService.valueAsList())));
		model.put("orgJSON", JSONObject.fromObject(gatherService.getOrgMap()));
		model.put("org", EnumType.DATA_SOURCE_TYPE_ORG_CHANNEL.getValue());
		model.put("accountDate", DateUtil.getSpecifiedDayBefore(new Date()));
		model.put("flag", CommonConstant.ALL_ID);
		model.put("hConfig", hConfig);
		if (orgNo != null) {
			model.put("orgCode", orgNo);
		} else {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			model.put("orgCode", orgCode);
		}
		model.put("orderState", Order_State);
		if(StringUtils.isNotBlank(date)){
			model.put("date",date);
			model.put("accountDate", date);
		}else if (StringUtils.isNotEmpty(Order_State)) {
			model.put("date", DateUtil.getSpecifiedDayBeforeMonth(DateUtil.getCurrentDate(), 3));
		}
		model.put("isDisplay", StringUtils.isNotBlank(hConfig.getIsDisplay()) ? hConfig.getIsDisplay() : 0);

		String tradeDetailPullButtonDisplay = propertiesConfigService.findValueByPkey(
				ProConstants.tradeDetailPullButtonDisplay,
				ProConstants.DEFAULT.get(ProConstants.tradeDetailPullButtonDisplay));
		model.put("tradeDetailPullButtonDisplay",
				StringUtils.isNotEmpty(tradeDetailPullButtonDisplay) ? tradeDetailPullButtonDisplay : false);
		return autoView("reconciliation/tradeDetail");
	}

	@RestController
	@RequestMapping({"/admin/tradeDetailData"})
	class TradeDataController extends BaseController {
		@GetMapping
		public WebUiPage<Map<String, Object>> recHistradeQuery(TradeDetailVo cqvo) {
			List<Organization> orgList = null;
			if(StringUtils.isNotBlank(cqvo.getOrgCode())){
				orgList = organizationService.findByParentCode(cqvo.getOrgCode());
			}
			if(StringUtils.isNotEmpty(cqvo.getStartDate())) {
				cqvo.setStartDate(cqvo.getStartDate().trim());
			}
			if(StringUtils.isNotEmpty(cqvo.getEndDate())) {
				cqvo.setEndDate(cqvo.getEndDate().trim());
			}
			Page<Map<String, Object>> hisPage = reconciliationService.getTradeDetailPage(cqvo,orgList,
					this.getRequestPageabledWithInitSort(this.getIdDescSort()));
			return toWebUIPage(hisPage);
		}

		@GetMapping("/tradeCondition")
		public WebUiPage<Map<String, Object>> tradeCondition(TradeDetailVo cqvo) {
			Sort sort = new Sort(Direction.ASC, "orderState");
			List<Organization> orgList = null;
			if(StringUtils.isNotBlank(cqvo.getOrgCode())){
				orgList = organizationService.findByParentCode(cqvo.getOrgCode());
			}
			if(StringUtils.isNotEmpty(cqvo.getStartDate())) {
				cqvo.setStartDate(cqvo.getStartDate().trim());
			}
			if(StringUtils.isNotEmpty(cqvo.getEndDate())) {
				cqvo.setEndDate(cqvo.getEndDate().trim());
			}
			Page<Map<String, Object>> hisPage = reconciliationService.getTradeDetailPage(cqvo,orgList,
					this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(hisPage);
		}

		//统计笔数与金额
		@GetMapping("/countSum")
		public ResponseResult getCollect(TradeDetailVo vo){
			List<Organization> orgList = null;
			if(StringUtils.isNotBlank(vo.getOrgCode())){
				orgList = organizationService.findByParentCode(vo.getOrgCode());
			}
			AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
			List<Map<String, Object>> list =new ArrayList<>();
			if(StringUtils.isNotBlank(hConfig.getIsDisplay())&&hConfig.getIsDisplay().equals("1")) {
				list = reconciliationService.getTradeDetailCollect(vo,orgList);
			}
			return ResponseResult.success().data(list);
		}

		/**
		 *  交易明细查询-his账单详情接口
		 * @param orgNo
		 * @param payFlowNo
		 * @param businessNo
		 * @param tradeTime
		 * @return
		 */
		@GetMapping("/getHISInfo")
		public ResponseResult getHISInfo(@RequestParam(value = "orgNo", required = false)String orgNo,
										 @RequestParam(value = "payFlowNo", required = false)String payFlowNo,
										 @RequestParam(value = "hisOrderNO", required = false)String businessNo,
										 @RequestParam(value = "tradeDataTime", required = false)String tradeTime){

			String datasourceIp = propertiesConfigService.findValueByPkey(ProConstants.hisOrderDatasourceIp);
			String datasourceUsername = propertiesConfigService.findValueByPkey(ProConstants.hisOrderDatasourceUsername);
			String datasourcePassword = propertiesConfigService.findValueByPkey(ProConstants.hisOrderDatasourcePassword);
			String datasourcePort = propertiesConfigService.findValueByPkey(ProConstants.hisOrderDatasourcePort);
			String datasourceDataBase = propertiesConfigService.findValueByPkey(ProConstants.hisOrderDatasourceDatabase);

			String webserviceFunctionName = propertiesConfigService.findValueByPkey(ProConstants.hisOrderWebserviceFunctionName);
			String httpType = propertiesConfigService.findValueByPkey(ProConstants.hisOrderHttpType,
					ProConstants.DEFAULT.get(ProConstants.hisOrderHttpType));
			String webserviceUrl=propertiesConfigService.findValueByPkey(ProConstants.hisOrderWebserviceUrl);
			String datasourceSql=propertiesConfigService.findValueByPkey(ProConstants.hisOrderDatasourceSql);
			String httpUrl = propertiesConfigService.findValueByPkey(ProConstants.hisOrderHttpUrl);

			HisRequestVo vo = new HisRequestVo();
			Map<String, String> params = new HashMap<>();
			params.put("orgCode",orgNo);
			params.put("tsnOrderNo",payFlowNo);
			params.put("hisOrderNo",businessNo);
			params.put("startDateTime",tradeTime);
			params.put("EndDateTime",tradeTime);
			TradeHISDetailVo hisDetailVo =new TradeHISDetailVo();
			DataSource dataSource = new DataSource();
			dataSource.setIp(datasourceIp);
			dataSource.setUsername(datasourceUsername);
			dataSource.setPassword(datasourcePassword);
			dataSource.setPort(StringUtils.isNotBlank(datasourcePort) ? Integer.valueOf(datasourcePort) : 8080);
			dataSource.setDataBaseName(datasourceDataBase);
			vo.setDataSource(dataSource);
			vo.setFunctionName(webserviceFunctionName);
			vo.setHttpType(httpType);
			vo.setServerUrl(StringUtils.isNotBlank(httpUrl)?httpUrl:webserviceUrl);
			vo.setSql(datasourceSql);
			vo.setType(returnHisType());
			vo.setDataSource(dataSource);
			vo.setParams(params);
			/*map.put("orgNo", orgNo);
			map.put("payFlowNo", payFlowNo);
			vo.setType(HisInterfaceType.HTTP);
			vo.setHttpType("post");
			vo.setServerUrl(hisOrderUrl);
			vo.setParams(map);*/
			hisDetailVo.setTitleState(TitleStateEnum.NORMAL.getName());
			// 是否接入his接口参数
			if (StringUtils.isBlank(httpUrl) && (datasourceIp == null || StringUtil.isNullOrEmpty(datasourceIp))
					&& (webserviceUrl == null || StringUtil.isNullOrEmpty(webserviceUrl))) {
				hisDetailVo.setTitleState(TitleStateEnum.UNABUTMENTHIS.getName());
				return ResponseResult.success().data(hisDetailVo);
			}
			try {
				HisResponseVo responseVo = hisService.service(vo);
				Object hisData = responseVo.getData();
				// 校验his接口返回的数据类型
				if(hisData != null && hisData instanceof TradeHISDetailVo){
					hisDetailVo = (TradeHISDetailVo) hisData;
					hisDetailVo.setTitleState(TitleStateEnum.NORMAL.getName());
					return ResponseResult.success().data(hisDetailVo);
				}else if(hisData == null){
					hisDetailVo.setTitleState(TitleStateEnum.NOTHINGNESS.getName());
					return ResponseResult.success().data(hisDetailVo);
				}else if (!JsonUtil2.getInstance().validate(hisData.toString())) {
					hisDetailVo.setTitleState(TitleStateEnum.NOTHINGNESS.getName());
					return ResponseResult.success().data(hisDetailVo);
				}
				JSONArray data=(JSONArray) hisData;
				JSONObject jsonObj=(JSONObject) data.get(0);
				if (StringUtil.isNullOrEmpty(jsonObj.getString("hisNo"))
						&& StringUtil.isNullOrEmpty(jsonObj.getString("patientName"))) {
					hisDetailVo.setTitleState(TitleStateEnum.NOTHINGNESS.getName());
					return ResponseResult.success().data(hisDetailVo);
				}
				hisDetailVo.setHisNo(jsonObj.getString("hisNo"));
				hisDetailVo.setOrderState(jsonObj.getString("orderState"));
				hisDetailVo.setPatientNo(jsonObj.getString("visitNumber"));
				hisDetailVo.setPatientName(jsonObj.getString("patientName"));
				hisDetailVo.setPatientType(jsonObj.getString("patientType"));
				hisDetailVo.setPayNo(jsonObj.getString("payNo"));
				hisDetailVo.setPayType(jsonObj.getString("payType"));
				hisDetailVo.setTradeAmount(jsonObj.getString("tradeAmount"));
				hisDetailVo.setTradeTime(jsonObj.getString("tradeTime"));

			} catch (Exception e) {
				e.printStackTrace();
				hisDetailVo.setTitleState(TitleStateEnum.NETWORKTIMEOUT.getName());
				return ResponseResult.success().data(hisDetailVo);
			}
			return ResponseResult.success().data(hisDetailVo);

		}

		private HisInterfaceType returnHisType() {
			String hisOrderType = propertiesConfigService.findValueByPkey(ProConstants.hisOrderType,
					ProConstants.DEFAULT.get(ProConstants.hisOrderType));
			if (hisOrderType.equalsIgnoreCase(HisInterfaceType.HTTP.getValue())) {
				return HisInterfaceType.HTTP;
			}else if (hisOrderType.equalsIgnoreCase(HisInterfaceType.DATASOURCE.getValue())) {
				return HisInterfaceType.DATASOURCE;
			}else if (hisOrderType.equalsIgnoreCase(HisInterfaceType.WEBSERVICE.getValue())) {
				return HisInterfaceType.WEBSERVICE;
			}else if (hisOrderType.equalsIgnoreCase(HisInterfaceType.WEBAPI.getValue())) {
				return HisInterfaceType.WEBAPI;
			}else if (hisOrderType.equalsIgnoreCase(HisInterfaceType.EXE.getValue())) {
				return HisInterfaceType.EXE;
			}
			return HisInterfaceType.HTTP;
		}


		//调用his接口
		@GetMapping("/getRefundInfo")
		public ResponseResult getRefundInfo(@RequestParam(value = "orgNo", required = false)String orgNo,
											@RequestParam(value = "payFlowNo", required = false)String payFlowNo){
			if (payFlowNo == null) {
				return ResponseResult.failure();
			}
			List<Map<String, Object>> refundList = reconciliationService.getRefundInfo(orgNo,payFlowNo);
			return ResponseResult.success().data(refundList);

		}

		//申请退款
		/*@GetMapping("/refundButton")
		public ResponseResult refundButton(@RequestParam(value = "id", required = false)String id,
				@RequestParam(value = "reason", required = false)String reason){
			ResponseResult map = null;
			Map<String,Object> refundMap = reconciliationService.getTradeRefundDetail(id);
			logger.info(refundMap.toString());
			RefundVo refundVo = new RefundVo();
			User user = currentUser();
			try {
				refundVo.setTsn(refundMap.get("tsn").toString());
				refundVo.setOrgCode(refundMap.get("orgCode").toString());
				refundVo.setPayType(refundMap.get("payType").toString());
				refundVo.setPayCode(refundMap.get("payCode").toString());
				refundVo.setUser(user);
				refundVo.setReason(reason);
				refundVo.setBillSource(refundMap.get("billSource").toString());
				map = refundService.refundAll(refundVo);
				if (map.isSuccess()) {
					reconciliationService.updateDetailById(Long.valueOf(id), user);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ResponseResult.success().data(map);
		}*/

		/**
		 * @throws Exception
		 * @throws
		 * @date：2017年4月7日
		 * @Description：导出
		 * @param model
		 * @param request
		 * @return: 返回结果描述
		 * @return ModelAndView: 返回值类型
		 * @throws
		 */
		/*@Logable( operation = "导出交易明细")
		@GetMapping("/api/dcExcel")
		public ModelAndView toDcExcel(TradeDetailVo cqvo,ModelMap model, HttpServletRequest request){ 
			List<ExcelDecoratedEntry> ops = new ArrayList<ExcelDecoratedEntry>();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//			ops.add(new ExcelDecoratedEntry("orgCode", "机构名称"));
			ops.add(new ExcelDecoratedEntry("systemFrom", "系统来源"));
			ops.add(new ExcelDecoratedEntry("tradeDataTime", "交易时间",5000));
			ops.add(new ExcelDecoratedEntry("custName", "患者姓名"));
			ops.add(new ExcelDecoratedEntry("payAmount", "金额(单位：元)"));
			ops.add(new ExcelDecoratedEntry("paySystemNo", "支付方流水号"));
			ops.add(new ExcelDecoratedEntry("payType", "支付类型"));
			ops.add(new ExcelDecoratedEntry("visitNumber", "就诊卡号"));
			ops.add(new ExcelDecoratedEntry("cashier", "柜员号"));
			ops.add(new ExcelDecoratedEntry("orderState", "订单状态"));
			ExcelResult viewExcel = null;
			List<Organization> orgList = null;
			if(StringUtils.isNotBlank(cqvo.getOrgCode())){
				orgList = organizationService.findByParentCode(cqvo.getOrgCode());
			}
			List<Map<String, Object>> hisList = reconciliationService.exportTradeDetail(cqvo,orgList);
			Map<String, Object> orgMap =gatherService.getOrgMap();
			try {
				viewExcel = new ExcelResult(hisList, ops,sdf.format(sdf.parse(cqvo.getStartDate()))+"至"+sdf.format(sdf.parse(cqvo.getEndDate()))+orgMap.get(cqvo.getOrgCode())+"交易明细",8);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new ModelAndView(viewExcel); 
		}*/

		@Logable( operation = "导出交易明细")
		@GetMapping("/api/dcExcel")
		public void toDcExcel(TradeDetailVo cqvo,ModelMap model, HttpServletRequest request,HttpServletResponse response) throws Exception{
			List<Organization> orgList = null;
			if(StringUtils.isNotBlank(cqvo.getOrgCode())){
				orgList = organizationService.findByParentCode(cqvo.getOrgCode());
			}
			if(StringUtils.isNotEmpty(cqvo.getStartDate())) {
				cqvo.setStartDate(cqvo.getStartDate().trim());
			}
			if(StringUtils.isNotEmpty(cqvo.getEndDate())) {
				cqvo.setEndDate(cqvo.getEndDate().trim());
			}
			List<Map<String, Object>> hisList = reconciliationService.exportTradeDetail(cqvo,orgList, this.getRequestPageabledWithInitSort(this.getIdDescSort()));
			Map<String, Object> orgMap =gatherService.getOrgMap();

			String[] thirdTitleArray = { "渠道名称", "交易时间", "患者姓名", "金额(元)", "支付方流水号", "支付类型", "就诊卡号", "柜员号", "订单状态"};
			String[] cellValue = { "systemFrom","tradeDataTime","custName","payAmount","paySystemNo","payType","visitNumber","cashier","orderState"};
			//合计总笔数数组
			String[] sumValue = { "allCount","wxCount","zfbCount","bankCount","ybCount"};
			//合计交易金额数组
			String[] amountValue = { "allAmount","wxAmount","zfbAmount","bankAmount","ybAmount"};
			ExportDataUtil exportDataUtil = new ExportDataUtil(18,8,thirdTitleArray,cellValue,amountValue,sumValue);
			String fileName = cqvo.getStartDate() + "至" + cqvo.getEndDate() + orgMap.get(cqvo.getOrgCode())+"交易明细";
			exportDataUtil.commonExportExcel(fileName, "交易明细", request, response, hisList);
		}

		/**
		 * 交易明细查询  订单退费
		 * @param orderNo
		 * @param id
		 * @param payCode
		 * @param tradeAmount
		 * @param batchRefundNo
		 * @param billSource
		 * @param reason
		 * @param file
		 * @param state
		 * @param time
		 * @param tradeDataTime
		 * @param extendArea
		 * @return
		 * @throws BusinessException
		 */
		@Logable(operation = "退费")
		@PostMapping("/refundButton")
		public ResponseResult startRefund(@RequestParam(value = "orderNo", required = false) String orderNo,Long id,
										  String payCode,String tradeAmount,String batchRefundNo,String payAmount,
										  String billSource, String reason, @RequestParam(required = false) MultipartFile file,
										  @RequestParam(value = "state", required = false) String state,@RequestParam(value = "time", required = false) String time,
										  String tradeDataTime, String extendArea,String payBusinessType,String outTradeNo,
										  @RequestParam(value = "sqm", required = false) String sqm,
										  @RequestParam(value = "pjh", required = false) String pjh,
										  @RequestParam(value = "sysNo", required = false) String sysNo,
										  @RequestParam(value = "cashier", required = false) String cashier,
										  @RequestParam(value = "counterNo", required = false) String counterNo,
										  @RequestParam(value = "bocNo", required = false) String bocNo,
										  @RequestParam(value = "invoiceNo", required = false) String invoiceNo,
										  @RequestParam(value = "patType", required = false) String patType
		)
				throws BusinessException {
			ResponseResult result = ResponseResult.success();
			User user = currentUser();
//			if (StringUtils.isBlank(billSource)) {
//				return ResponseResult.failure("支付厂家未知,无法退费！");
//			}
			if ((reason == null || reason.trim().equals("")) && file == null) {
				return ResponseResult.failure("请输入退款原因或者上传图片");
			}
			if (null != reason && reason.length() >= 200) {
				return ResponseResult.failure("请输入文字少于200个");
			}
			// 上传图片
			String imgUrl = "";
			if (file != null) {
				imgUrl = saveImage(file);
			}
			try {
				// 验证是否可以退款
				Map<String,Object> refundMap = reconciliationService.getTradeRefundDetail(String.valueOf(id));
				RefundVo refundVo = new RefundVo();
				orderNo=refundMap.get("tsn").toString();
				refundVo.setTsn(refundMap.get("tsn").toString());
				refundVo.setOrderNo(refundMap.get("paySystemNo").toString());
				refundVo.setOrgCode(refundMap.get("orgCode").toString());
				refundVo.setPayType(refundMap.get("payType").toString());
				refundVo.setPayCode(refundMap.get("payCode").toString());
				refundVo.setUser(user);
				refundVo.setReason(reason);
//				refundVo.setTradeAmount(refundMap.get("tradeAmount").toString());
				// 退款金额
				refundVo.setTradeAmount(tradeAmount);
				// 订单金额
				refundVo.setPayAmount(payAmount);
				refundVo.setBillSource(refundMap.get("billSource").toString());
				refundVo.setImgUrl(imgUrl);
				refundVo.setOutTradeNo(outTradeNo);
				refundVo.setBusinessType(payBusinessType);
				refundVo.setTradetime(tradeDataTime);
				refundVo.setTime(tradeDataTime.substring(0,10));
				refundVo.setOutTradeNo(sysNo);
				refundVo.setSqm(sqm);
				refundVo.setPjh(pjh);
				refundVo.setCashier(cashier);
				refundVo.setCounterNo(counterNo);
				refundVo.setBocNo(bocNo);
				refundVo.setExtendArea(JsonChangeVo.getJson(refundVo));
				// 发票号
				refundVo.setInvoiceNo(invoiceNo);
				result = refundService.refundAll(refundVo);
				logger.error("退款结果：{}", result.getMessage());

				//退费成功后其他逻辑处理,0表示交易明细页面提交的退费
				if(StringUtils.isNotBlank(state)&&state.equals("1")) {
					//在抹平记录表中保存一条状态为11的单
					String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
					TradeCheckFollowDeal deal = tradeCheckFollowDealDao.findFirstByPayFlowNoAndOrgCodeAndTradeDatetimeOrderByCreatedDateDesc(orderNo,orgCode,time);
					if(deal==null) {
						TradeCheckFollowDeal vo =new TradeCheckFollowDeal();
						vo.setPayFlowNo(orderNo);
						vo.setDescription(reason);
						vo.setCreatedDate(new Date());
						vo.setExceptionState(String.valueOf(CommonEnum.BillBalance.NORECOVER.getValue()));
						vo.setDealAmount(new BigDecimal(tradeAmount));
						vo.setOrgCode(orgCode);
						vo.setTradeDatetime(time);
						vo.setPatType(patType);
						tradeCheckFollowDealDao.save(vo);
					}
				}else {
					//更新交易明细表（t_order_upload）的退费状态
					updateOrder(result,orderNo);
				}
			} catch (Exception e) {
				logger.error("退费异常 ： " + e);
				OperationLogUtil.quickSave("交易明细申请退款失败 " + e);
				result = ResponseResult.failure(e.getMessage());
			}
			return result;
		}

		private void updateOrder(ResponseResult result,String orderNo) {
			//退款流程走通
			if(result.isSuccess()) {
				try {
					orderUploadService.updateOrder(orderNo,String.valueOf(result.getData()));
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}

		private String saveImage(MultipartFile file) {
			String fileName = file.getOriginalFilename();
			String fileLocation = Configure.getPropertyBykey("file.location");
			if(fileName.indexOf(".")>=0) {
				fileName = UUID.randomUUID().toString().replace("-", "").toLowerCase() + fileName.substring(fileName.lastIndexOf("."),fileName.length());
			}
			try {
				uploadFile(file.getBytes(), fileLocation, fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return fileName ;
		}

		private void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
			File targetFile = new File(filePath);
			if(!targetFile.exists()){
				targetFile.mkdirs();
			}
			FileOutputStream out = new FileOutputStream(filePath+fileName);
			out.write(file);
			out.flush();
			out.close();
		}

		@RequestMapping(value="/api/pullhis")
		public ResponseResult getHisTradeDetails(String orgCode, String startTime, String endTime) {
			if (StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)) {
				return ResponseResult.failure();
			}
			if(StringUtils.isEmpty(orgCode)) {
				String orgNo = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
				orgCode = orgNo;
			}
			return ResponseResult.success();
		}
	}
}

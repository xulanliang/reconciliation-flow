package com.yiban.rec.web.recon.noncash;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.ExcepHandingRecord;
import com.yiban.rec.domain.vo.ExcepHandingRecordVo;
import com.yiban.rec.domain.vo.RefundRecordVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.OrderUploadService;
import com.yiban.rec.service.RefundRecordService;
import com.yiban.rec.service.UserOrganizationPerService;
import com.yiban.rec.service.UserRoleService;
import com.yiban.rec.util.Configure;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.ExportDataUtil;

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
@RequestMapping("/admin/refund")
public class ReconciliationOnlineRefundController extends CurrentUserContoller {


	@Autowired
	private MetaDataService metaDataService;
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private UserOrganizationPerService userOrganizationPerService;
	
	@Autowired
	private OrderUploadService orderUploadService;
	
	@Autowired
	private RefundRecordService refundRecordService;
	
	
	@RequestMapping("")
	public String index(ModelMap model,String date , String state ,String orgNo) {
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.valueAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		model.put("accountDate", DateUtil.getSpecifiedDayBeforeDay(new Date(),0));
		User user = currentUser();
		String type=null;
		List<Object> list = userRoleService.getRoleName(String.valueOf(user.getId()));
		if("admin".equals(user.getLoginName())){
			type="admin";
		}else if(list.contains("???????????????")||list.contains("????????????")) {
			type="1";
		}else {
			type="2";
		}
		String fileLocation = Configure.getPropertyBykey("file.location");
		model.put("fileLocation",fileLocation);
		model.put("roleType", type);
		//??????????????????
		model.put("state", state);
		// ????????????????????????
		if (StringUtils.isNotEmpty(state)) {
//			model.put("startDate", DateUtil.getSpecifiedDayBeforeMonth(DateUtil.getCurrentDate(), 3));
			model.put("allDate", true);
		}else{			
			model.put("allDate", false);
		}
		model.put("orgNo", orgNo);
		return autoView("reconciliation/refundRecord");
	}
	@RestController
	@RequestMapping({"/admin/onlineRefund/data"})
	class RefundDataController extends BaseController {
		@GetMapping
		public WebUiPage<ExcepHandingRecord> refund(RefundRecordVo revo) {
			User user = currentUser();
			String type=null;
			List<Object> list = userRoleService.getRoleName(String.valueOf(user.getId()));
			if(list.contains("???????????????")||list.contains("????????????")||"admin".equals(user.getLoginName())) {
				type="1";
			}else {
				type="2";
			}
			revo.setType(type);
			List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
			Sort sort = new Sort(Direction.DESC, "handleDateTime");
			Page<ExcepHandingRecord> platPage = refundRecordService.getExRecordData(revo,orgListTemp,this.getRequestPageabledWithInitSort(sort),user);
			return toWebUIPage(platPage);
		}
		
		//????????????
		@PostMapping
		public ResponseResult rejectOrExamine(ExcepHandingRecordVo vo,MultipartFile file) {
			User user = currentUser();
			try {
				if(StringUtils.isBlank(vo.getHandleRemark())) {
					return ResponseResult.failure().message("??????????????????");
				}
				//????????????
				String imgUrl = "";
				if(file != null){
					imgUrl = saveImage(file);
				}
				vo.setImgUrl(imgUrl);
				ExcepHandingRecord sVo = refundRecordService.rejectOrExamine(vo,user);
				//????????????????????????t_order_upload??????????????????
				orderUploadService.updateOrder(sVo.getPaymentRequestFlow(),sVo.getState());
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseResult.failure().message(e.getMessage());
			}
			return ResponseResult.success();
		}
		//??????
		@PostMapping("/delete")
		public ResponseResult delete(Long id){
			try {
				refundRecordService.delete(id);
			} catch (Exception e) {
				return ResponseResult.failure().message("????????????");
			}
			return ResponseResult.success();
		}
		
		//??????
		@GetMapping("/details")
		public ResponseResult details(Long id){
			ResponseResult rs = ResponseResult.success();
			try {
				List<ExcepHandingRecord> list = refundRecordService.details(id);
				rs.data(list);
			} catch (Exception e) {
				rs = ResponseResult.failure().message("??????");
				e.printStackTrace();
				return rs;
			}
			return rs;
		}
		
		
		
		private String saveImage(MultipartFile file) throws Exception {
			String fileName = file.getOriginalFilename();
			String fileLocation = Configure.getPropertyBykey("file.location");
	        if(fileName.indexOf(".")>=0) {
	        	fileName = UUID.randomUUID().toString().replace("-", "").toLowerCase() + fileName.substring(fileName.lastIndexOf("."),fileName.length());     
	        }
			try {
				uploadFile(file.getBytes(), fileLocation, fileName);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
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
		
		/**
		 * @throws Exception 
		* @date???2017???4???7??? 
		* @Description?????????
		* @param model
		* @param request
		* @return: ??????????????????
		* @return ModelAndView: ???????????????
		* @throws
		 */
		@GetMapping("/dcExcel")
		public void toDcExcel(RefundRecordVo revo,ModelMap model, HttpServletRequest request,HttpServletResponse response) throws Exception{
			User user = currentUser();
			String type=null;
			List<Object> rList = userRoleService.getRoleName(String.valueOf(user.getId()));
			if(rList.contains("????????????")||"admin".equals(user.getLoginName())) {
				type="1";
			}else {
				type="2";
			}
			revo.setType(type);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			List<ExcelDecoratedEntry> ops = new ArrayList<ExcelDecoratedEntry>();
			Map<String, Object> map = gatherService.getOrgMap();
			Map<String, String> vo = ValueTexts.asMap(metaDataService.valueAsList());
			List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
//			ops.add(new ExcelDecoratedEntry("orgNo", "????????????"));
//			ops.add(new ExcelDecoratedEntry("handleDateTime", "????????????"));
//			ops.add(new ExcelDecoratedEntry("userName", "?????????"));
//			ops.add(new ExcelDecoratedEntry("paymentRequestFlow", "???????????????"));
//			ops.add(new ExcelDecoratedEntry("patientName", "????????????"));
//			ops.add(new ExcelDecoratedEntry("patientNo", "?????????"));
//			ops.add(new ExcelDecoratedEntry("tradeAmount", "????????????(???)"));
//			ops.add(new ExcelDecoratedEntry("state", "??????"));
//			ops.add(new ExcelDecoratedEntry("handleRemark", "????????????"));
//			ExcelResult viewExcel = null;
			Sort sort = new Sort(Direction.DESC, "handleDateTime");
			List<ExcepHandingRecord> list = refundRecordService.getExRecordDataNopage(revo,orgListTemp,sort,user);
			BigDecimal costAll = new BigDecimal(0);
			List<ExcepHandingRecord> listVo = new ArrayList<ExcepHandingRecord>();
			for(ExcepHandingRecord flow : list) {
				costAll = costAll.add(flow.getTradeAmount());
				flow.setOrgNo((String)map.get(flow.getOrgNo()));
				flow.setBusinessType((String)vo.get(flow.getBusinessType()));
				if("1".equals(flow.getState())) {//?????????
					flow.setState("?????????");
				}else if("2".equals(flow.getState())) {//??????
					flow.setState("?????????");
				}else if("3".equals(flow.getState())) {//?????????
					flow.setState("?????????");
				}else{
					flow.setState("");
				}
				listVo.add(flow);
			}
//			ExcepHandingRecord hisVo=new ExcepHandingRecord();
//			hisVo.setOrgNo("??????:");
//			hisVo.setTradeAmount(costAll.setScale(2, BigDecimal.ROUND_HALF_UP));
//			listVo.add(hisVo);
			List<Map<String, Object>> excepMapList = new ArrayList<>();
			for (ExcepHandingRecord excepHandingRecord : listVo) {
				HashMap<String, Object> excepMap = new HashMap<>();
				excepMap.put("orgNo", excepHandingRecord.getOrgNo());
				excepMap.put("handleDateTime",sdf.format(excepHandingRecord.getHandleDateTime()));
				excepMap.put("userName", excepHandingRecord.getUserName());
				excepMap.put("paymentRequestFlow", excepHandingRecord.getPaymentRequestFlow());
				excepMap.put("patientName", excepHandingRecord.getPatientName());
				excepMap.put("patientNo", excepHandingRecord.getPatientNo());
				excepMap.put("tradeAmount", excepHandingRecord.getTradeAmount());
				excepMap.put("state", excepHandingRecord.getState());
				excepMapList.add(excepMap);
			}
			
			
//			viewExcel = new ExcelResult(listVo, ops,revo.getStartTime()+"???"+revo.getEndTime()+"????????????",8);
//			return new ModelAndView(viewExcel); 
			
			String[] thirdTitleArray = { "????????????", "????????????", "?????????", "??????????????????", "????????????", "?????????", "????????????(???)", "??????"};
			String[] cellValue = { "orgNo","handleDateTime","userName","paymentRequestFlow","patientName","patientNo","tradeAmount","state"};
			ExportDataUtil exportDataUtil = new ExportDataUtil(18,7,thirdTitleArray,cellValue);
			String fileName = revo.getStartTime()+"???"+revo.getEndTime()+"????????????";
			exportDataUtil.commonExportExcel(fileName, "????????????", request, response, excepMapList);
		}
	}
}

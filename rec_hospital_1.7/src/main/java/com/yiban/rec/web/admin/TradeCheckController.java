package com.yiban.rec.web.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.framework.operatelog.util.OperationLogUtil;
import com.yiban.rec.domain.TradeCheck;
import com.yiban.rec.domain.basicInfo.HisChannelParaSendInfo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.TradeCheckService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.JsonUtil;
import com.yiban.rec.ws.client.IPaymentService;
import com.yiban.rec.ws.client.PaymentServiceLocator;

/**
 * 
*<p>文件名称:TradeCheckController.java
*<p>
*<p>文件描述:交易明细校验、退费处理
*<p>版权所有:深圳市依伴数字科技有限公司版权所有(C)2017</p>
*<p>内容摘要:简要描述本文件的内容，包括主要模块、函数及能的说明
</p>
*<p>其他说明:其它内容的说明
</p>
*<p>完成日期:2017年10月25日上午11:29:29</p>
*<p>
*@author fangzuxing
 */
@Controller
@RequestMapping("/admin/tradeCheck")
public class TradeCheckController extends CurrentUserContoller{
	
	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private TradeCheckService tradeCheckService;
	
	@RequestMapping("")
	public String index(ModelMap model) {
		 model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.valueAsList())));
		 model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		 model.put("deviceNo", CommonConstant.DEVICE_NO);
		 model.put("accountDate", DateUtil.getCurrentDateString());
		 model.put("accountOrgNo", CommonConstant.ALL_ID);
		return autoView("reconciliation/tradeCheck");
	}
	@RestController
	@RequestMapping({"/admin/tradeCheck/data"})
	class FollowingRecDataController extends BaseController {
		@GetMapping
		public WebUiPage<TradeCheck> recHistoryQuery(TradeCheck tradeCheck){
			Sort sort = new Sort(Direction.DESC, "checkState");
			if(StringUtils.isBlank(tradeCheck.getTradeDate())) {
				tradeCheck.setTradeDate(DateUtil.getCurrentDateString());
			}
			User user = currentUser();
			Page<TradeCheck> tradeCheckPage = tradeCheckService.getTradeCheckPage(tradeCheck, user, 
					this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(tradeCheckPage);
		}  
		
		/**
		* @date：2017年10月26日 
		* @Description：对账文件导入
		* @param file
		* @param result
		* @return: 返回结果描述
		* @return ResponseResult: 返回值类型
		* @throws
		 */
		@PostMapping
		public ResponseResult importSave(@RequestParam(value = "wechatApplyId", required = false) String wechatApplyId,
				@RequestParam(value = "wechatPayShopNo", required = false) String wechatPayShopNo,
				@RequestParam(value = "alipayApplyId", required = false) String alipayApplyId,
				@RequestParam(value = "alipayPayShopNo", required = false) String alipayPayShopNo,HttpServletRequest request)throws BusinessException{
			try {
					List<MultipartFile> files =((MultipartHttpServletRequest)request).getFiles("file");
					User user = currentUser();
					tradeCheckService.dataSwitch(files,user,wechatPayShopNo,wechatApplyId,alipayPayShopNo,alipayApplyId);
				} catch (Exception e) {
					e.printStackTrace();
					OperationLogUtil.quickSave("导入支付宝微信账单失败"+e);
					return ResponseResult.failure("文件导入出错!");
				}
			 return ResponseResult.success("文件导入成功!");
		}
		
		/**
		* @date：2017年10月28日 
		* @Description：当日账单数据校验
		* @return
		* @throws BusinessException: 返回结果描述
		* @return ResponseResult: 返回值类型
		* @throws
		 */
		@PostMapping("/{orgNo}/account")
		public ResponseResult startRec(@RequestParam(value = "orgNo", required = false) String orgNo,
				@RequestParam(value = "accountDate", required = false) String accountDate)throws BusinessException{
					String result = "";
					HisChannelParaSendInfo hisChannelParaSendInfo = new HisChannelParaSendInfo();
				try {
					User user = currentUser();
					hisChannelParaSendInfo.setOrg_no(orgNo);
					hisChannelParaSendInfo.setPay_type("");
					hisChannelParaSendInfo.setTrade_code(CommonConstant.NOW_BILL_CHECK);
					hisChannelParaSendInfo.setPay_date(accountDate);
					String message = JsonUtil.bean2json(hisChannelParaSendInfo);
					IPaymentService iPaymentService = new PaymentServiceLocator().getBasicHttpBinding_IPaymentService();
					result = iPaymentService.entrance(message);
					System.out.println("当日对账获取his账单返回==="+result);
					logger.info("当日对账获取his账单返回==="+result);
					result = tradeCheckService.handleBill(accountDate,result,user);
				} catch (Exception e) {  
					e.printStackTrace();
					OperationLogUtil.quickSave("当日对账失败"+e);
					return ResponseResult.failure(e.getMessage());
				}
			 return ResponseResult.success(result);
		}
		
		/**
		 * @date：2017年10月28日 
		 * @Description：退费处理
		 * @return
		 * @throws BusinessException: 返回结果描述
		 * @return ResponseResult: 返回值类型
		 * @throws
		 */
		@PostMapping("/{id}/refund")
		public ResponseResult startRefund(@RequestParam(value = "id", required = false) Long id,
					@RequestParam(value = "handleRemark", required = false) String handleRemark)throws BusinessException{
			 String resutl = "";
			try {
				//1、数据主装  2、调用接口
				User user = currentUser();
				resutl = tradeCheckService.checkRefund(id, user,handleRemark);
			} catch (Exception e) {
				e.printStackTrace();
				OperationLogUtil.quickSave("当日对账退费失败"+e);
				return ResponseResult.failure("退费失败");
			}
			return ResponseResult.success(resutl);
		}
		
		/**
		* @date：2017年12月4日 
		* @Description：处理非账平数据
		* @param id
		* @return
		* @throws BusinessException: 返回结果描述
		* @return ResponseResult: 返回值类型
		* @throws
		 */
		@PostMapping("/handlerZp")
		public ResponseResult handlerZp(@RequestParam(value = "id", required = false) Long id)throws BusinessException{
			try {
				tradeCheckService.handlerZp(id);
			} catch (Exception e) {
				e.printStackTrace();
				OperationLogUtil.quickSave("当日对账-处理账平失败"+e);
				return ResponseResult.failure("处理失败");
			}
			return ResponseResult.success("处理成功");
		}
	}
}

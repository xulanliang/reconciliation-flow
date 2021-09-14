package com.yiban.rec.web.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.operatelog.util.OperationLogUtil;
import com.yiban.rec.dao.recon.TradeCheckFollowDealDao;
import com.yiban.rec.domain.recon.TradeCheckFollowDeal;
import com.yiban.rec.domain.vo.RefundRequestVo;
import com.yiban.rec.service.OrderUploadService;
import com.yiban.rec.service.ReconciliationService;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.Configure;

/**
 *
 * <p>
 * 文件名称:RefundRecordController.java
 * <p>
 * <p>
 * 文件描述:本类描述
 * <p>
 * 版权所有:深圳市依伴数字科技有限公司版权所有(C)2017
 * </p>
 * <p>
 * 内容摘要:对账管理--->退费记录查询
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
@RequestMapping("/admin/refundRecord")
public class RefundRecordController extends CurrentUserContoller {

	private final Logger log = LoggerFactory.getLogger(this.getClass());


	@Autowired
	private ReconciliationService reconciliationService;
	@Autowired
	private OrderUploadService  orderUploadService;


	@Autowired
	private TradeCheckFollowDealDao tradeCheckFollowDealDao;
	@Autowired
	private PropertiesConfigService propertiesConfigService;

	@RestController
	@RequestMapping({"/admin/refund/data"})
	class NextDayAccountDataController extends BaseController {

		@Logable(operation = "退费")
		@PostMapping("/refund")
		public ResponseResult startRefund(RefundRequestVo requestVo, @RequestParam(required = false) MultipartFile file)
				throws BusinessException {
			ResponseResult result = ResponseResult.success();
			User user = currentUser();
			if (StringUtils.isBlank(requestVo.getBillSource())) {
				return ResponseResult.failure("支付厂家未知,无法退费！");
			}
			if ((requestVo.getReason() == null || requestVo.getReason().trim().equals("")) && file == null) {
				return ResponseResult.failure("请输入退款原因");
			}
			if (null != requestVo.getReason() && requestVo.getReason().length() >= 200) {
				return ResponseResult.failure("请输入文字少于200个");
			}
			// 上传图片
			String imgUrl = "";
			if (file != null) {
				imgUrl = saveImage(file);
			}
			requestVo.setImgUrl(imgUrl);

			try {
				// 验证是否可以退款
				if (StringUtils.isNotBlank(requestVo.getOrderNo())) {
					result = reconciliationService.electronicRecRefund(requestVo, user);
				} else {
					return ResponseResult.failure("退费失败,订单号不能为空");
				}
			} catch (Exception e) {
				logger.error("退费异常 ： " + e);
				OperationLogUtil.quickSave("隔日对账-退费失败 " + e);
				result = ResponseResult.failure(e.getMessage());
			}
			// 退费成功后其他逻辑处理,1表示电子对账异常账单页面提交的退费
			String state = requestVo.getState();
			if (StringUtils.isNotBlank(state) && state.equals("1")) {
				// 在抹平记录表中保存一条状态为11的单
				String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
				TradeCheckFollowDeal deal = tradeCheckFollowDealDao.findFirstByPayFlowNoAndOrgCodeAndTradeDatetimeOrderByCreatedDateDesc(
						requestVo.getOrderNo(), orgCode, requestVo.getTime());
				if (deal == null) {
					TradeCheckFollowDeal vo = new TradeCheckFollowDeal();
					vo.setPayFlowNo(requestVo.getOrderNo());
					vo.setDescription(requestVo.getReason());
					vo.setCreatedDate(new Date());
					vo.setExceptionState(String.valueOf(CommonEnum.BillBalance.NORECOVER.getValue()));
					vo.setDealAmount(new BigDecimal(requestVo.getTradeAmount()));
					vo.setOrgCode(orgCode);
					vo.setTradeDatetime(requestVo.getTime());
					vo.setPayType(requestVo.getPayType());
					vo.setBillSource(requestVo.getBillSource());
					vo.setPatType(requestVo.getPatType());
					tradeCheckFollowDealDao.save(vo);
				}else {
					deal.setDescription(requestVo.getReason());
					tradeCheckFollowDealDao.save(deal);
				}
			}
			updateOrder(result, requestVo.getOrderNo());
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
	}



}

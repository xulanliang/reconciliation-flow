package com.yiban.rec.web.log;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;

import org.apache.commons.lang3.StringUtils;
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
import org.springside.modules.persistence.SearchFilter;
import org.springside.modules.persistence.SearchFilter.Operator;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.service.standardbill.BillParseService;
import com.yiban.rec.domain.basicInfo.HisChannelParaSendInfo;
import com.yiban.rec.domain.log.RecLog;
import com.yiban.rec.domain.log.RecLogDetails;
import com.yiban.rec.emailbill.service.ThirdBillService;
import com.yiban.rec.reconciliations.ReconciliationsService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.RecLogDetailsService;
import com.yiban.rec.service.RecLogService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumType;
import com.yiban.rec.util.JsonUtil;
import com.yiban.rec.ws.client.IPaymentService;
import com.yiban.rec.ws.client.PaymentServiceLocator;

import net.sf.json.JSONObject;

/**
 * 日志管理
 * @Author WY
 * @Date 2018年9月28日
 */
@Controller
@RequestMapping("/admin/log")
public class ReconciliationLogController extends CurrentUserContoller {

	@Autowired
	private RecLogService recLogService;
	
	@Autowired
	private RecLogDetailsService recLogDetailsService;

	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private GatherService gatherService;
	
	@Autowired
    private BillParseService billParseService;
	
	@Autowired
    private ReconciliationsService reconciliationsService;
	
	@Autowired
	private ThirdBillService thirdBillService;
	@Autowired
	private PropertiesConfigService propertiesConfigService;

	@RequestMapping("")
	public String indexLog(ModelMap model) {
	    model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.NameAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		return autoView("log/reclog");
	}

	@RestController
	@RequestMapping("/admin/log/reconlog")
	class DataController extends FrameworkController {
	    
		/**
		 * 分页查询日志
		 * @param orgCode
		 * @param orderDate
		 * @param offset
		 * @param pageSize
		 * @param request
		 * @return
		 * WebUiPage<RecLog>
		 */
		@GetMapping()
		public WebUiPage<RecLog> reclogQuery(
		        @RequestParam(value="orgCode",required=false) String orgCode,
				@RequestParam(value="orderDate",required=false) String orderDate,
				@RequestParam(value="offset",required=false) Integer offset, 
	            @RequestParam(value="limit",required=false) Integer pageSize,
	            HttpServletRequest request) {
			
		    int pageNumber = offset/pageSize;
		    PageRequest pageable = this.getPageRequest(pageNumber, pageSize, 
	                new Sort(Direction.DESC, "createdDate"));
		    List<SearchFilter> filters = new ArrayList<SearchFilter>();
	        if(StringUtils.isNotBlank(orgCode)) {
	            filters.add(new SearchFilter("orgCode", Operator.EQ, orgCode));
	        }
	        if(StringUtils.isNotBlank(orderDate)) {
	            filters.add(new SearchFilter("orderDate", Operator.EQ, orderDate));
	        }else {
	        	String time = DateUtil.getSpecifiedDayBeforeDay(new Date(),90);
	        	filters.add(new SearchFilter("orderDate", Operator.GTE, time));
	        }
	        Page<RecLog> pageData = recLogService.findPageByQueryParameters(filters, 
	                pageable);
			return toWebUIPage(pageData);
		}

		/**
		 * 重新对账
		 * @param orgCode
		 * @param orderDate
		 * @return
		 * ResponseResult
		 */
		@PostMapping
		public ResponseResult repeatRec(
		        @RequestParam(value="orgCode")String orgCode, 
		        @RequestParam(value="orderDate")String orderDate) {
			// 清空历史日志
			recLogDetailsService.deleteByOrderDateAndOrgCode(orderDate, orgCode);
			
			// 清除渠道所有账单数据
			try {
				boolean billDataParseDeleteFlag = Boolean.valueOf(propertiesConfigService.findValueByPkey(ProConstants.billDataParseDeleteFlag, 
						ProConstants.DEFAULT.get(ProConstants.billDataParseDeleteFlag)));
				if(billDataParseDeleteFlag ) {
					logger.info("拉取账单先删除:当前所有数据");
				    thirdBillService.delete(orderDate+" 00:00:00", orderDate+" 23:59:59", orgCode);
					logger.info("拉取账单先删除:当前所有数据成功");
				}
			} catch (Exception e) {
				return ResponseResult.failure("拉取账单删除数据异常：" + e.getMessage());
			}
			boolean tangduHisbillFirst = Boolean.valueOf(propertiesConfigService.findValueByPkey(ProConstants.tangduHisbillFirst, 
					ProConstants.DEFAULT.get(ProConstants.tangduHisbillFirst)));
			if (tangduHisbillFirst) {
				getHisBill(orgCode, orderDate);
			}
			
			// 拉取账单
			try {
			    billParseService.parse(orgCode, orderDate);
            } catch (BillParseException e) {
                logger.error("拉取账单异常：", e);
                return ResponseResult.failure("拉取账单异常：" + e.getMessage());
            }

			if (!tangduHisbillFirst) {
				getHisBill(orgCode, orderDate);
			}
			// 对账：电子对账和医保对账
			
		    try {
                reconciliationsService.compareBill(orgCode, orderDate);
                reconciliationsService.compareHealthBill(orgCode, orderDate);
            } catch (Exception e) {
                logger.error("对账发生异常：", e);
                return ResponseResult.failure("对账发生异常：" + e.getMessage());
            }   
			return ResponseResult.success("操作成功");
		}
		
		private void getHisBill(String orgCode, String orderDate) {
			/**
			 * 通过银行获取his账单数据，暂时处理方案，后续推荐在账单解析逻辑实现 
			 * 在汇总后面执行，因为异步逻辑，汇总是上次的获取数据
			 */
			try {
				String yinyiBillFlag = propertiesConfigService.findValueByPkey(ProConstants.donetBillUrl);
				// 如果银医接口地址为空则不通过银医接口拉取数据
				if (StringUtils.isNotBlank(yinyiBillFlag )) {
					insertBillByYinYi(orgCode, orderDate);
				}
			} catch (Exception e) {
				logger.error("调用银医获取his账单数据接口发生异常：", e);
			}
		}
		
		/**
		 * 调用银医接口插入数据
		 * @param orgCode
		 * @param payDate
		 */
        private void insertBillByYinYi(String orgCode, String payDate) {
            HisChannelParaSendInfo hcpsi = new HisChannelParaSendInfo();
            hcpsi.setOrg_no(orgCode);
            hcpsi.setPay_type("");
            hcpsi.setTrade_code(EnumType.TRADE_CODE.getValue());
            hcpsi.setPay_date(payDate);
            String message = JsonUtil.bean2json(hcpsi);
            logger.info("调用银医接口插入HIS数据, 请求参数：" + message);
            IPaymentService iPaymentService = null;
            try {
                iPaymentService = new PaymentServiceLocator().getBasicHttpBinding_IPaymentService();
            } catch (ServiceException e) {
                logger.error("调用银医接口发生异常：", e);
            }
            String result = null;
            try {
                result = iPaymentService.entrance(message);
                logger.info("调用银医接口响应数据：result = " + result);
            } catch (RemoteException e) {
                logger.error("调用银医接口发生异常：", e);
            }
            JSONObject jsonObject = JSONObject.fromObject(result);
            logger.info("调用银医获取账单接口reloadData"+jsonObject.toString());
        }
		
		/**
		 * 日志明细
		 * @param orgCode
		 * @param orderDate
		 * @return
		 * List<RecLogDetails>
		 */
		@PostMapping("/logdetails")
		public List<RecLogDetails> logdetails(
                @RequestParam(value="orgCode")String orgCode, 
                @RequestParam(value="orderDate")String orderDate) {
		    return recLogDetailsService.findByOrderDateAndOrgCode(
		            orderDate, orgCode);
		}
	}
}

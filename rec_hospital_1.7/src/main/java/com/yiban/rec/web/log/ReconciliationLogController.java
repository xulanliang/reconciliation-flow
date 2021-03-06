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
 * ????????????
 * @Author WY
 * @Date 2018???9???28???
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
		 * ??????????????????
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
		 * ????????????
		 * @param orgCode
		 * @param orderDate
		 * @return
		 * ResponseResult
		 */
		@PostMapping
		public ResponseResult repeatRec(
		        @RequestParam(value="orgCode")String orgCode, 
		        @RequestParam(value="orderDate")String orderDate) {
			// ??????????????????
			recLogDetailsService.deleteByOrderDateAndOrgCode(orderDate, orgCode);
			
			// ??????????????????????????????
			try {
				boolean billDataParseDeleteFlag = Boolean.valueOf(propertiesConfigService.findValueByPkey(ProConstants.billDataParseDeleteFlag, 
						ProConstants.DEFAULT.get(ProConstants.billDataParseDeleteFlag)));
				if(billDataParseDeleteFlag ) {
					logger.info("?????????????????????:??????????????????");
				    thirdBillService.delete(orderDate+" 00:00:00", orderDate+" 23:59:59", orgCode);
					logger.info("?????????????????????:????????????????????????");
				}
			} catch (Exception e) {
				return ResponseResult.failure("?????????????????????????????????" + e.getMessage());
			}
			boolean tangduHisbillFirst = Boolean.valueOf(propertiesConfigService.findValueByPkey(ProConstants.tangduHisbillFirst, 
					ProConstants.DEFAULT.get(ProConstants.tangduHisbillFirst)));
			if (tangduHisbillFirst) {
				getHisBill(orgCode, orderDate);
			}
			
			// ????????????
			try {
			    billParseService.parse(orgCode, orderDate);
            } catch (BillParseException e) {
                logger.error("?????????????????????", e);
                return ResponseResult.failure("?????????????????????" + e.getMessage());
            }

			if (!tangduHisbillFirst) {
				getHisBill(orgCode, orderDate);
			}
			// ????????????????????????????????????
			
		    try {
                reconciliationsService.compareBill(orgCode, orderDate);
                reconciliationsService.compareHealthBill(orgCode, orderDate);
            } catch (Exception e) {
                logger.error("?????????????????????", e);
                return ResponseResult.failure("?????????????????????" + e.getMessage());
            }   
			return ResponseResult.success("????????????");
		}
		
		private void getHisBill(String orgCode, String orderDate) {
			/**
			 * ??????????????????his??????????????????????????????????????????????????????????????????????????? 
			 * ???????????????????????????????????????????????????????????????????????????
			 */
			try {
				String yinyiBillFlag = propertiesConfigService.findValueByPkey(ProConstants.donetBillUrl);
				// ??????????????????????????????????????????????????????????????????
				if (StringUtils.isNotBlank(yinyiBillFlag )) {
					insertBillByYinYi(orgCode, orderDate);
				}
			} catch (Exception e) {
				logger.error("??????????????????his?????????????????????????????????", e);
			}
		}
		
		/**
		 * ??????????????????????????????
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
            logger.info("????????????????????????HIS??????, ???????????????" + message);
            IPaymentService iPaymentService = null;
            try {
                iPaymentService = new PaymentServiceLocator().getBasicHttpBinding_IPaymentService();
            } catch (ServiceException e) {
                logger.error("?????????????????????????????????", e);
            }
            String result = null;
            try {
                result = iPaymentService.entrance(message);
                logger.info("?????????????????????????????????result = " + result);
            } catch (RemoteException e) {
                logger.error("?????????????????????????????????", e);
            }
            JSONObject jsonObject = JSONObject.fromObject(result);
            logger.info("??????????????????????????????reloadData"+jsonObject.toString());
        }
		
		/**
		 * ????????????
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

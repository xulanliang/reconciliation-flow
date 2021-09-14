package com.yiban.rec.web.log;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.settlement.RecLogSettlement;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.settlement.RecHisSettlementService;
import com.yiban.rec.service.settlement.RecLogSettlementService;

@Controller
@RequestMapping("/admin/settlementLog")
public class SettlementController extends CurrentUserContoller {

	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private RecLogSettlementService recLogSettlementService;
	
	@Autowired
	private RecHisSettlementService recHisSettlementService;
	
	
	@RequestMapping("")
	public String index(ModelMap model) {
		 model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.valueAsList())));
		 model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		return autoView("log/settlementLog");
	}
	
	@RestController
	@RequestMapping("/admin/log/settlementLog")
	class SettlementLogDataController extends FrameworkController {
		
		@GetMapping()
		public WebUiPage<RecLogSettlement> settlementlogQuery(
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
	        }
	        Page<RecLogSettlement> pageData = recLogSettlementService.findPageByQueryParameters(filters, 
	                pageable);
			return toWebUIPage(pageData);
		}
		
		/**
		 * 重新汇总
		 */
		@PostMapping
		public ResponseResult repeatRec(
				@RequestParam(value="orgCode")String orgCode, 
		        @RequestParam(value="orderDate")String orderDate){
			try {
				recHisSettlementService.getAndSaveHisOrders(orderDate);
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseResult.failure("操作失败："+e.getMessage());
			}
			return ResponseResult.success("操作成功");
		}
	}
	
}

package com.yiban.rec.web.blendrefund;



import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.MixRefund;
import com.yiban.rec.domain.MixRefundDetails;
import com.yiban.rec.domain.vo.BlendRefundVo;
import com.yiban.rec.domain.vo.ResponseVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.MixRefundDetailsService;
import com.yiban.rec.service.MixRefundService;
import com.yiban.rec.util.DateUtil;

@Controller
@RequestMapping("/admin/blendRefund")
public class BlendRefundIndexController extends CurrentUserContoller {
	
	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private MixRefundService mixRefundService;
	
	@Autowired
	private MixRefundDetailsService mixRefundDetailsService;
	
	@Autowired
	private OrganizationService organizationService;
	

	@RequestMapping("")
	public String index(ModelMap model) {
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.NameAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		model.put("accountDate", DateUtil.getSpecifiedDayBeforeDay(new Date(),0));
		return autoView("blendRefund/blendRefund");
	}
	
	@RestController
	@RequestMapping({"/admin/blendRefund/data"})
	class NextDayAccountDataController extends BaseController {
		
		/**
		 * 
		 * @param vo
		 * @return
		 */
		@GetMapping
		public WebUiPage<MixRefund> refundData(BlendRefundVo vo) {
			List<Organization> orgList = null;
			if(null != vo.getOrgNo()){
				orgList = organizationService.findByParentCode(vo.getOrgNo());
			}
			Sort sort = new Sort(Direction.DESC, "refundDateTime");
			Page<MixRefund> data = mixRefundService.fundData(vo,orgList,this.getRequestPageabledWithInitSort(sort));
			return this.toWebUIPage(data);
		}
		
		@GetMapping("/detail")
		public ResponseResult refundDataDetails(BlendRefundVo vo) {
			ResponseResult rs = ResponseResult.success();
			try {
				List<List<MixRefundDetails>> list = mixRefundDetailsService.mixRefundDetailsData(vo);
				rs.data(list);
			} catch (Exception e) {
				rs = ResponseResult.failure("异常");
				e.printStackTrace();
				return rs;
			}
			return rs;
		}
		
		@GetMapping("/retryApply")
		public ResponseResult retryApply(BlendRefundVo vo) {
			ResponseResult rs = ResponseResult.success();
			try {
				ResponseVo rec = mixRefundDetailsService.retryApply(vo.getRefundOrderNo(),vo.getId());
				if(!rec.getResultMsg().equals("成功")) {
					return ResponseResult.failure("退费异常:"+rec.getResultMsg());
				}
			} catch (Exception e) {
				rs = ResponseResult.failure(e.getMessage());
				e.printStackTrace();
				return rs;
			}
			return rs;
		}
	}
}

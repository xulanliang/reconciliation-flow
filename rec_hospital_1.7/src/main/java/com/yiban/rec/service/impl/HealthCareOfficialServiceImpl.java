package com.yiban.rec.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.dao.HealthCareOfficialDao;
import com.yiban.rec.domain.HealthCareOfficial;
import com.yiban.rec.domain.vo.HealthCareDetailQueryVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.HealthCareOfficialService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.StringUtil;

@Service("healthCareOfficialService")
public class HealthCareOfficialServiceImpl implements HealthCareOfficialService {

	@Autowired
	private HealthCareOfficialDao healthCareOfficialDao;
	
	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private OrganizationService organizationService;
	
	
	protected List<Predicate> healthCareSearchCash(HealthCareDetailQueryVo cqvo ,Root<?> root,
			CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();
		//组装orgNo
		packageOrgNo(cqvo, root, cb, predicates);
//		//组装操作类型
		if (!StringUtils.isEmpty(cqvo.getOperationType())) {
			if(cqvo.getOperationType().indexOf(",")>-1){
				String[] tradeTypeArr = cqvo.getOperationType().split(",");
				In<String> in = cb.in(root.get("operationType").as(String.class));
				for(int i=0;i<tradeTypeArr.length;i++){
					in.value(tradeTypeArr[i]);
				}
				predicates.add(in);
			}else{
				Path<Integer> tradeCodeExp = root.get("operationType");
				predicates.add(cb.equal(tradeCodeExp, cqvo.getOperationType()));
			}
		}
		//组装医保类型
		if (!StringUtils.isEmpty(cqvo.getHealthcareTypeCode())) {
			if(cqvo.getHealthcareTypeCode().indexOf(",")>-1){
				String[] tradeTypeArr = cqvo.getHealthcareTypeCode().split(",");
				In<String> in = cb.in(root.get("healthcareTypeCode").as(String.class));
				for(int i=0;i<tradeTypeArr.length;i++){
					in.value(tradeTypeArr[i]);
				}
				predicates.add(in);
			}else{
				Path<Integer> tradeCodeExp = root.get("healthcareTypeCode");
				predicates.add(cb.equal(tradeCodeExp, cqvo.getHealthcareTypeCode()));
			}
		}
		if (!StringUtils.isEmpty(cqvo.getPayFlowNo())) {
			Path<Integer> paySourceExp = root.get("payFlowNo");
			predicates.add(cb.equal(paySourceExp, cqvo.getPayFlowNo()));
		}
		if (!StringUtils.isEmpty(cqvo.getBusinessCycleNo())) {
			Path<String> paySystemNoExp = root.get("businessCycleNo");
			predicates.add(cb.equal(paySystemNoExp, cqvo.getBusinessCycleNo()));
		}
		if (!StringUtils.isEmpty(cqvo.getStartDate())) {
			Path<Date> payDateStartExp = root.get("tradeDatatime");
			predicates.add(cb.greaterThanOrEqualTo(payDateStartExp, cqvo.getStartDate()));
		}
		if (!StringUtils.isEmpty(cqvo.getEndDate())) {
			Path<Date> payDateEndExp = root.get("tradeDatatime");
			predicates.add(cb.lessThanOrEqualTo(payDateEndExp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", DateFormatUtils.format(cqvo.getEndDate(), "yyyy-MM-dd 23:59:59"))));
		}
		return predicates;
	}
	
	//组装orgNo
	private void packageOrgNo(HealthCareDetailQueryVo cqvo,  Root<?> root,
			CriteriaBuilder cb, List<Predicate> predicates) {
		
		List<Organization> orgList = organizationService.findByParentCode(cqvo.getOrgNo());
		In<String> in = cb.in(root.get("orgNo").as(String.class));
		in.value(cqvo.getOrgNo());
		if(orgList != null ){
			for (int i = 0; i < orgList.size(); i++) {
		    	Organization org = orgList.get(i);
		    	in.value(org.getCode());
		    }
			predicates.add(in);
		}
	}

	//分页查询医保账单明细
	@Override
	public Page<HealthCareOfficial> getHealthCareOfficialPage(HealthCareDetailQueryVo cqvo, Pageable pageable) {
		Specification<HealthCareOfficial> specification = new Specification<HealthCareOfficial>() {
			@Override
			public Predicate toPredicate(Root<HealthCareOfficial> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates =healthCareSearchCash(cqvo,root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return healthCareOfficialDao.findAll(specification, pageable);
	}
	
	//导出医保账单明细
	@Override
	public List<HealthCareOfficial> getHealthCareOfficialList(HealthCareDetailQueryVo cqvo,
			List<Organization> orgListTemp, Pageable page) {
		Specification<HealthCareOfficial> specification = new Specification<HealthCareOfficial>() {
			@Override
			public Predicate toPredicate(Root<HealthCareOfficial> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates =healthCareSearchCash(cqvo, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		List<HealthCareOfficial> officialList = healthCareOfficialDao.findAll(specification);
		Map<String,String> metaMap = ValueTexts.asMap(metaDataService.getNameValueAsList());
		Map<String,Object> orgMap = gatherService.getOrgMap();
		if(!StringUtil.isNullOrEmpty(officialList)){
			for(HealthCareOfficial hc : officialList){
				hc.setOrgName(String.valueOf(orgMap.get(String.valueOf(hc.getOrgNo()))));
				hc.setOperationTypeName(metaMap.get(String.valueOf(hc.getOperationType())));
				hc.setHealthcareTypeName(metaMap.get(String.valueOf(hc.getHealthcareTypeCode())));
			}
		}
		return officialList;
	}
	
	@Override
	public HealthCareOfficial findByPayFlowNo(String payFlowNo) {
		return healthCareOfficialDao.findByPayFlowNo(payFlowNo);
	}

	@Override
	public void saveOrUpdate(HealthCareOfficial hco) {
		healthCareOfficialDao.save(hco);
	}
}

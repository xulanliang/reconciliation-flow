package com.yiban.rec.service.impl;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.rec.dao.recon.CitizenCardBillDao;
import com.yiban.rec.domain.CitizenCardBill;
import com.yiban.rec.domain.vo.CitizenCardBillVo;
import com.yiban.rec.service.CitizenCardBillService;
import com.yiban.rec.service.base.BaseOprService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CitizenCardBillServiceImpl extends BaseOprService implements CitizenCardBillService {
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private CitizenCardBillDao citizenCardBillDao;
	
	@Override
	public Page<CitizenCardBill> findPage(CitizenCardBillVo vo, PageRequest pageable) {
		List<Organization> orgList = organizationService.findByParentCode(vo.getOrgCode());
		Specification<CitizenCardBill> spec = new Specification<CitizenCardBill>() {

			@Override
			public Predicate toPredicate(Root<CitizenCardBill> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicate = new ArrayList<>();
				if(orgList!=null&&orgList.size()>0){
	                In<String> in = cb.in(root.get("orgNo"));
	                for (Organization org : orgList) {
	                    in.value(org.getCode());
	                }
	                predicate.add(in);
				}else{
					predicate.add(cb.equal(root.get("orgNo"), vo.getOrgCode()));
				}
				if(StringUtils.isNotBlank(vo.getOrderState())){
					predicate.add(cb.equal(root.get("orderState"), vo.getOrderState()));
				}
                if (StringUtils.isNotBlank(vo.getPayFlowNo())) {
                    predicate.add(cb.like(root.get("payFlowNo"), "%" + vo.getPayFlowNo() + "%"));
                }
                predicate.add(cb.between(root.get("tradeDatatime").as(String.class), vo.getStartDate().trim()+" 00:00:00", vo.getEndDate().trim()+" 23:59:59"));
                Predicate[] pre = new Predicate[predicate.size()];
                return query.where(predicate.toArray(pre)).getRestriction();
			}
			 
		};
		return citizenCardBillDao.findAll(spec, pageable);
	}

	@Override
	public List<Map<String, Object>> summary(CitizenCardBillVo vo) {
		List<Organization> orgList = organizationService.findByParentCode(vo.getOrgCode());
		String orgs = "'"+vo.getOrgCode()+"'";
		if(orgList!=null&&orgList.size()>0){
			for (Organization org : orgList) {
				orgs+=",'"+org.getCode()+"'";
			}
		}
		StringBuffer sbuf=new StringBuffer();
		sbuf.append(" SELECT order_state,COUNT(t.id),SUM(ABS(pay_amount)) FROM t_citizen_card t ");
		sbuf.append(" WHERE 1=1 ");
		sbuf.append(" AND t.org_no in (").append(orgs).append(") ");
//		if(StringUtils.isNotBlank(vo.getPayFlowNo())){
//			sbuf.append(" AND t.pay_flow_no like '%").append(vo.getPayFlowNo()).append("%' ");
//		}
//		if(StringUtils.isNotBlank(vo.getOrderState())){
//			sbuf.append(" AND t.order_state = '").append(vo.getOrderState()).append("' ");
//		}
		if(StringUtils.isNotBlank(vo.getStartDate())){
			sbuf.append(" AND t.Trade_datatime >= '").append(vo.getStartDate().trim()).append(" 00:00:00' ");
		}
		if(StringUtils.isNotBlank(vo.getEndDate())){
			sbuf.append(" AND t.Trade_datatime <= '").append(vo.getEndDate().trim()).append(" 23:59:59' ");
		}
		sbuf.append(" GROUP BY order_state ");
		return super.handleNativeSql(sbuf.toString(), new String[]{"orderState","count","money"});
	}
}

package com.yiban.rec.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.yiban.framework.account.dao.UserDao;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.dao.ExcepHandingRecordDao;
import com.yiban.rec.dao.recon.TradeCheckFollowDealDao;
import com.yiban.rec.domain.ExcepHandingRecord;
import com.yiban.rec.domain.vo.ExcepHandingRecordVo;
import com.yiban.rec.domain.vo.RefundRecordVo;
import com.yiban.rec.domain.vo.RefundVo;
import com.yiban.rec.service.OrderUploadService;
import com.yiban.rec.service.RefundRecordService;
import com.yiban.rec.service.RefundService;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.JsonChangeVo;
import com.yiban.rec.util.RefundStateEnum;
import com.yiban.rec.util.StringUtil;


@Service
public class RefundRecordServiceImpl implements RefundRecordService{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ExcepHandingRecordDao excepHandingRecordDao;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private RefundService refundService;
	@Autowired
	private OrderUploadService orderUploadService;
	@Autowired
	private TradeCheckFollowDealDao tradeCheckFollowDealDao;
	@Autowired
	private UserDao userDao;

	@Override
	public Page<ExcepHandingRecord> getExRecordData(RefundRecordVo revo,List<Organization> orgListTemp,
			Pageable pageable,User user) {
		Specification<ExcepHandingRecord> specification = new Specification<ExcepHandingRecord>() {
			@Override
			public Predicate toPredicate(Root<ExcepHandingRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearch(revo,orgListTemp,root, query, cb,user);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return setUserName(excepHandingRecordDao.findAll(specification, pageable));
	}
	
	private Page<ExcepHandingRecord> setUserName(Page<ExcepHandingRecord> vo){
		for(ExcepHandingRecord v:vo.getContent()) {
			User user = userDao.findById(v.getOperationUserId());
			if(user!=null) {
				v.setUserName(user.getName());
			}
		}
		return vo;
	}
	
	@Override
	public List<ExcepHandingRecord> getExRecordDataNopage(RefundRecordVo revo,List<Organization> orgListTemp,
			Sort sort,User user) {
		Specification<ExcepHandingRecord> specification = new Specification<ExcepHandingRecord>() {
			@Override
			public Predicate toPredicate(Root<ExcepHandingRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearch(revo,orgListTemp,root, query, cb,user);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return setUserName(excepHandingRecordDao.findAll(specification,sort));
	}
	private List<ExcepHandingRecord> setUserName(List<ExcepHandingRecord> vo){
		for(ExcepHandingRecord v:vo) {
			User user = userDao.findById(v.getOperationUserId());
			if(user!=null) {
				v.setUserName(user.getName());
			}
		}
		return vo;
	}
	
	protected List<Predicate> converSearch(RefundRecordVo revo,List<Organization> orgListTemp, Root<?> root,
			CriteriaQuery<?> query, CriteriaBuilder cb,User user) {
		List<Predicate> predicates = Lists.newArrayList();
		if (!StringUtils.isEmpty(revo.getOrgNo())) {
			//获取所有的机构编码
			List<Organization> orgList = organizationService.findByParentCode(revo.getOrgNo());
			In<String> in = cb.in(root.get("orgNo"));
			in.value(revo.getOrgNo());
			if (orgList != null && orgList.size() > 0) {
				for(Organization org : orgList){
					if(org != null){
						in.value(org.getCode());
					}
				}
			}
			predicates.add(in);
		}
		
		if(org.apache.commons.lang.StringUtils.isNotBlank(revo.getPaymentRequestFlow())) {
			Path<String> paymentRequestFlowExp = root.get("paymentRequestFlow");
			predicates.add(cb.equal(paymentRequestFlowExp, revo.getPaymentRequestFlow()));
		}
		if (org.apache.commons.lang.StringUtils.isNotBlank(revo.getState())) {
			Path<Integer> state = root.get("state");
			predicates.add(cb.equal(state, revo.getState()));
		}
		if(org.apache.commons.lang.StringUtils.isNotBlank(revo.getBusinessType())) {
			Path<String> businessTypeExp = root.get("businessType");
			predicates.add(cb.equal(businessTypeExp, revo.getBusinessType()));
		}
		if (org.apache.commons.lang.StringUtils.isNotBlank(revo.getStartTime())) {
			Path<Date> payDateStartExp = root.get("handleDateTime");
			predicates.add(cb.greaterThanOrEqualTo(payDateStartExp,  DateUtil.transferStringToDate("yyyy-MM-dd",revo.getStartTime())));
		}
		if (org.apache.commons.lang.StringUtils.isNotBlank(revo.getEndTime())) {
			Path<Date> payDateEndExp = root.get("handleDateTime");
			predicates.add(cb.lessThanOrEqualTo(payDateEndExp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", revo.getEndTime()+" 23:59:59")));
		}
		Path<Long> fId = root.get("fatherId");
		predicates.add(cb.or(cb.equal(fId, 0),cb.isNull(fId)));
		if(org.apache.commons.lang.StringUtils.isNotBlank(revo.getType())&&revo.getType().equals("2")) {
			Path<Long> userId = root.get("operationUserId");
			predicates.add(cb.equal(userId,user.getId()));
		}
		return predicates;
	}
	
	@Transactional(rollbackOn=Exception.class)
	public ExcepHandingRecord rejectOrExamine(ExcepHandingRecordVo recordVo,User user) throws Exception{
		try {
			ExcepHandingRecord vo=new ExcepHandingRecord();
			//查找申请记录
			ExcepHandingRecord sVo = excepHandingRecordDao.findOne(recordVo.getId());
			//复制申请记录
			BeanUtils.copyProperties(sVo, vo);
			//修改记录，得到实体
			vo.setId(null);
			vo.setHandleRemark(recordVo.getHandleRemark());
			vo.setHandleDateTime(new Date());
			vo.setImgUrl(recordVo.getImgUrl());
			vo.setOperationUserId(user.getId());
			vo.setUserName(user.getLoginName());
			vo.setRefundType(recordVo.getRefundType());
			if (!StringUtil.isEmpty(recordVo.getTradeAmount())){
				vo.setTradeAmount(new BigDecimal(recordVo.getTradeAmount()));
				sVo.setTradeAmount(new BigDecimal(recordVo.getTradeAmount()));
			}
			//修改申请记录
			if(RefundStateEnum.unExamine.getValue().equals(recordVo.getState())) {//审核通过
				sVo.setState(RefundStateEnum.refund.getValue());
				vo.setState(RefundStateEnum.refund.getValue());
			}else if(RefundStateEnum.reject.getValue().equals(recordVo.getState())) {//驳回
				sVo.setState(RefundStateEnum.reject.getValue());
				vo.setState(RefundStateEnum.reject.getValue());
			}else {//重新申请
				sVo.setState(RefundStateEnum.unExamine.getValue());
				vo.setState(RefundStateEnum.unExamine.getValue());
			}
			vo.setFatherId(recordVo.getId());
			//入库
			excepHandingRecordDao.save(vo);
			excepHandingRecordDao.save(sVo);
			if(RefundStateEnum.reject.getValue().equals(sVo.getState())||RefundStateEnum.unExamine.getValue().equals(sVo.getState())) {
				return sVo;
			}
			//调用退款接口
			RefundVo rVo = null;
			if (!StringUtil.isEmpty(sVo.getExtendArea())) {
				rVo = (RefundVo) JsonChangeVo.getVo(sVo.getExtendArea(), RefundVo.class);
				rVo.setState(RefundStateEnum.refund.getValue());
				rVo.setReason(recordVo.getHandleRemark());
				rVo.setUser(user);
			} else {
				rVo = new RefundVo();
				rVo.setState(RefundStateEnum.refund.getValue());
				rVo.setOrderNo(sVo.getPaymentRequestFlow());
				rVo.setTsn(sVo.getPaymentRequestFlow());
				rVo.setBillSource(sVo.getBillSource());
				rVo.setOrgCode(sVo.getOrgNo());
				rVo.setReason(recordVo.getHandleRemark());
				rVo.setPayCode(sVo.getPayName());
				rVo.setPayType(sVo.getBusinessType());
				rVo.setTradeAmount(String.valueOf(sVo.getTradeAmount()));
				rVo.setUser(user);
				
			}
			rVo.setSource(1);
			ResponseResult rec = refundService.refundAll(rVo);
			if(rec.isSuccess()) {
				//得到退款单号
				vo.setPaymentFlow(rVo.getPaymentFlow());
			}else {
				logger.error("退款失败：{}", rec.getMessage());
				Exception exception=new Exception("退款接口调用失败");
				throw exception;
			}
			return sVo;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@org.springframework.transaction.annotation.Transactional
	public int delete(Long id) throws Exception{
		try {
		    ExcepHandingRecord vo = excepHandingRecordDao.findOne(id);
		    if(null != vo) {
		        //更新交易明细表（t_order_upload）的退费状态
				orderUploadService.updateOrder(vo.getPaymentRequestFlow(), null);
				String exceptionState = String.valueOf(CommonEnum.BillBalance.NORECOVER.getValue());
				tradeCheckFollowDealDao.deleteByPayFlowNoAndOrgCodeAndTradeDatetimeAndExceptionState(
						vo.getPaymentRequestFlow(), vo.getOrgNo(), DateUtil.getNormalTime(vo.getTradeTime().getTime()),
						exceptionState );
		        excepHandingRecordDao.deleteRecord(id);
		    }
		} catch (Exception e) {
			throw new Exception("删除失败");
		}
		return 1;
	}

	public List<ExcepHandingRecord> details(Long id){
		return excepHandingRecordDao.details(id);
	}
	
}

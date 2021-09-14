package com.yiban.rec.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.dao.MessageUploadDao;
import com.yiban.rec.domain.MessageUpload;
import com.yiban.rec.service.MessageUploadService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.Configure;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.StringUtil;

@Service
@Transactional
public class MessageUploadServiceImpl extends BaseOprService implements MessageUploadService {
	
	@Autowired
	private MessageUploadDao messageUploadDao;

	@Override
	@Transactional
	public ResponseResult delete(Long id) {
		messageUploadDao.delete(id);
		return ResponseResult.success("删除成功！");
	}

	@Override
	public Page<MessageUpload> getMessageUploadList(PageRequest pagerequest) {
		Specification<MessageUpload> specification = new Specification<MessageUpload>() {
			@Override
			public Predicate toPredicate(Root<MessageUpload> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = Lists.newArrayList();
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return messageUploadDao.findAll(specification, pagerequest);
	}

	@Override
	public void save() {
		String orgNo = Configure.getPropertyBykey("yiban.projectid");
		List<MessageUpload> messageUploadDb = messageUploadDao.findByOrgNo(orgNo);
		if(StringUtil.isNullOrEmpty(messageUploadDb)){
			MessageUpload messageUpload = new MessageUpload();
			messageUpload.setOrgNo(orgNo);
			messageUpload.setDataTime(new Date());
			messageUpload.setPayType(EnumTypeOfInt.CASH_PAYTYPE.getValue());
			messageUpload.setTradeCode(EnumTypeOfInt.TRADE_CODE_PAY.getValue());
			messageUpload.setPayBusinessType(EnumTypeOfInt.PAY_BUSINESS_REGISTER.getValue());
			messageUpload.setPayAccount("42090516198536");
			messageUpload.setPayAmount(new BigDecimal(5.00));
			messageUpload.setState(EnumTypeOfInt.HANDLE_SUCCESS.getId());
			messageUpload.setCreateDate(new Date());
			messageUpload.setUpdateDate(new Date());
			messageUploadDao.save(messageUpload);
		}else{
			messageUploadDao.save(messageUploadDb);
		}
		
	}

	@Override
	public List<MessageUpload> getMessageUploadList(String orgNo) {
		
		return messageUploadDao.findByOrgNo(orgNo);
	}


}

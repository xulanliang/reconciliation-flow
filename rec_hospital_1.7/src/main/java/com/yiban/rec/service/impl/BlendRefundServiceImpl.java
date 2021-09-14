package com.yiban.rec.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yiban.rec.bill.parse.util.EnumTypeOfInt;
import com.yiban.rec.bill.parse.util.RefundEnumType;
import com.yiban.rec.dao.MixRefundDao;
import com.yiban.rec.dao.MixRefundDetailsDao;
import com.yiban.rec.domain.MixRefund;
import com.yiban.rec.domain.MixRefundDetails;
import com.yiban.rec.domain.vo.AllRefundVo;
import com.yiban.rec.domain.vo.ResponseVo;
import com.yiban.rec.service.BlendRefundService;
import com.yiban.rec.service.customized.impl.ClearRefundClass;
import com.yiban.rec.service.customized.impl.JindieRefundClass;

@Service
public class BlendRefundServiceImpl implements BlendRefundService {

	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MixRefundDetailsDao mixRefundDetailsDao;
	
	@Autowired
	private MixRefundDao mixRefundDao;
	
	

	/**
	 * 退款入口
	 * @throws Exception 
	 */
	@Transactional(rollbackOn=Exception.class)
	public ResponseVo BlendRefund(AllRefundVo vo) throws Exception {
		MixRefund old = mixRefundDao.findByRefundOrderNo(vo.getRefundOrderNo());
		if(old!=null) {
			return ResponseVo.failure("不能重复提交退款");
		}
		//需要存入的退款对象
		MixRefund mixRefund=new MixRefund();
		List<MixRefundDetails> list=new ArrayList<>();
		ResponseVo rec = saveData(vo,mixRefund,list);
		List<MixRefundDetails> oldList=new ArrayList<>();
		oldList.addAll(list);
		if(!rec.resultSuccess()) return rec;
		//得到退款类型的类集合
		log.info("退款开始");
		//退款
		ResponseVo refundRec = refund(mixRefund,list,vo.getSettlementType());
		if(!refundRec.resultSuccess()) throw new Exception(refundRec.getResultMsg());
		insertData(mixRefund,oldList);
		//跟新稍后的数据
		mixRefundDetailsDao.save(list);
		log.info("退款结束");
		return ResponseVo.success();
	}


	@Override
	public ResponseVo refund(MixRefund mixRefund, List<MixRefundDetails> list, String type) {
		String message="成功";
		int successNum=0;
		if(type.equals(EnumTypeOfInt.SETTLEMENT_TYPE.getValue())) {//是否自费
			BigDecimal sourceAmount=mixRefund.getRefundAmount();
			for(MixRefundDetails v:list) {
				v.setRefundOrderNo(mixRefund.getRefundOrderNo());
				//区分定时器和正常流程金额
				if(v.getRefundAmount()!=null) {
					sourceAmount=v.getRefundAmount();
				}
				//判断金额是否为0
				if(new BigDecimal(0).compareTo(sourceAmount)>=0) {
					break;
				}
				boolean num=false;
				//处理可能异常的数据
				try {
					//start
					if(v.getBillSource().equals(RefundEnumType.BILL_SOURCE_JD.getValue())||v.getBillSource().equals(RefundEnumType.BILL_SOURCE.getValue())) {//巨鼎
						num=true;
						new ClearRefundClass(v,mixRefund,sourceAmount);
						continue;
					}
					if((v.getBillSource()).equals(RefundEnumType.BILL_SOURCE_JIND.getValue())) {//唐都金蝶
						num=true;
						new JindieRefundClass(v,mixRefund,sourceAmount);
						continue;
					}
					//如果有新的类型继续往后加 
					//end
					return ResponseVo.failure("退费失败,有不支持的退费类型");
				} catch (Exception e) {
					log.info("退费异常:"+e.getMessage());
					v.setRefundStateInfo(e.getMessage());
					message=e.getMessage();
				}finally {
					if(num) {
						if(v.getRefundState()==RefundEnumType.REFUND_SUCCESS.getId()) {
							v.setRefundStateInfo("退费成功");
							successNum++;
						}
						if(v.getRefundState()==RefundEnumType.REFUND_NO_EXCEPTION.getId()) {
							v.setRetryTimes(0);
						}
						v.setRefundCount(v.getRefundCount()+1);
						sourceAmount=sourceAmount.subtract(v.getPayAmount());
						num=false;
					}
					//判断是否全部退费成功
					if(successNum<list.size()) {//有失败的
						return ResponseVo.partFail("部分成功");
					}
				}
			}
		}else {//非自费
			
		}
		return ResponseVo.success(message);
	}
	

	
	//插入数据
	@Transactional(rollbackOn=Exception.class)
	private void insertData(MixRefund mixRefund,List<MixRefundDetails> list) throws Exception {
		//插入退费数据
		mixRefundDao.save(mixRefund);
		//插入退费明细数据
		mixRefundDetailsDao.save(list);
	}
	
	
	private ResponseVo saveData(AllRefundVo vo,MixRefund mixRefund,List<MixRefundDetails> list) {
		mixRefund.setOrgCode(vo.getOrgCode());
		mixRefund.setSettlementType(vo.getSettlementType());
		mixRefund.setRefundOrderNo(vo.getRefundOrderNo());
		mixRefund.setRefundDateTime(vo.getRefundDateTime());
		mixRefund.setRefundAmount(vo.getRefundAmount());
		mixRefund.setYbPayAmount(vo.getYbPayAmount());
		mixRefund.setYbSerialNo(vo.getYbSerialNo());
		mixRefund.setYbBillNo(vo.getYbBillNo());
		mixRefund.setPayBusinessType(vo.getPayBusinessType());
		mixRefund.setPatType(vo.getPatType());
		mixRefund.setCashier(vo.getCashier());
		mixRefund.setRefundStrategy(StringUtils.isNotBlank(vo.getRefundStrategy())?vo.getRefundStrategy():"02");
		mixRefund.setRefundReason(vo.getRefundReason());
		//注入支付结果集
		/*if(StringUtils.isNotBlank(vo.getOrderItems())) {
			JSONArray json=JSONArray.fromObject(vo.getOrderItems());
			for(int i=0;i<json.size();i++) {
				JSONObject objec=json.getJSONObject(i);
				ResponseResult rec = checkData(objec);
				if(!rec.isSuccess()) return rec;
				MixRefundDetails detailsVo=new MixRefundDetails();
				detailsVo.setTsnOrderNo(objec.getString("tsnOrderNo"));
				detailsVo.setHisOrderNo(objec.getString("hisOrderNo"));
				detailsVo.setPayAmount(new BigDecimal(objec.getString("payAmount")));
				detailsVo.setPayDateTime(objec.getString("payDateTime"));
				detailsVo.setPayType(objec.getString("payType"));
				detailsVo.setBillSource(objec.getString("billSource"));
				list.add(detailsVo);
			}
		}*/
		if(vo.getOrderItems()!=null) {
			for(MixRefundDetails v:vo.getOrderItems()) {
				ResponseVo rec = checkData(v);
				if(!rec.resultSuccess()) return rec;
			}
			list.addAll(vo.getOrderItems());
		}
		
		//策略排序
		if(mixRefund.getRefundStrategy().equals("01")) {//先进先出
			Collections.sort(list,new Comparator<MixRefundDetails>(){
	            public int compare(MixRefundDetails arg0, MixRefundDetails arg1) {
	                return arg0.getPayDateTime().compareTo(arg1.getPayDateTime());
	            }
	        });
		}else {//先大后小
			Collections.sort(list,new Comparator<MixRefundDetails>(){
	            public int compare(MixRefundDetails arg0, MixRefundDetails arg1) {
	                return arg1.getPayAmount().compareTo(arg0.getPayAmount());
	            }
	        });
		}
		return ResponseVo.success();
	}
	
	private ResponseVo checkData(MixRefundDetails objec) {
		if(StringUtils.isBlank(objec.getTsnOrderNo())) {
			return ResponseVo.failure("自费类型:第三方业务系统流水号不能为空");
		}
		if(StringUtils.isBlank(objec.getHisOrderNo())) {
			return ResponseVo.failure("自费类型:医院His系统订单号不能为空");
		}
		if(objec.getPayAmount()==null) {
			return ResponseVo.failure("自费类型:支付订单金额不能为空");
		}
		if(StringUtils.isBlank(objec.getPayDateTime())) {
			return ResponseVo.failure("自费类型:支付时间不能为空");
		}
		if(StringUtils.isBlank(objec.getPayType())) {
			return ResponseVo.failure("自费类型:支付类型不能为空");
		}
		if(StringUtils.isBlank(objec.getBillSource())) {
			return ResponseVo.failure("自费类型:账单来源不能为空");
		}
		return ResponseVo.success();
	}
}

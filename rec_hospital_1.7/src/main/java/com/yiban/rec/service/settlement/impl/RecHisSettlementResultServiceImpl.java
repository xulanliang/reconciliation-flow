package com.yiban.rec.service.settlement.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.dict.domain.MetaData;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.bill.parse.util.DateUtil;
import com.yiban.rec.dao.settlement.RecHisSettlementResultDao;
import com.yiban.rec.domain.settlement.RecHisSettlement;
import com.yiban.rec.domain.settlement.RecHisSettlementResult;
import com.yiban.rec.domain.vo.RecHisSettlementResultVo;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.service.settlement.RecHisSettlementResultService;


@Service
public class RecHisSettlementResultServiceImpl extends BaseOprService implements RecHisSettlementResultService {
    
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
    private RecHisSettlementResultDao recHisSettlementResultDao;
    
    
    @Autowired
    private MetaDataService metaDataService;
    
    
    /**
     * 汇总数据
     */
    @Transactional(rollbackFor=Exception.class)
    public void summary(List<RecHisSettlement> list,String date) throws Exception {
    	if(list==null) {
    		return;
    	}
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    	SimpleDateFormat sdfs=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	//得到所有订单来源
    	List<MetaData> billList = metaDataService.findMetaDataByDataTypeValue("bill_source");
    	//汇总之前先清除
    	recHisSettlementResultDao.deleteBySettleDate(sdf.parse(date));
    	for(MetaData z:billList) {
    		//得到汇总实体
        	RecHisSettlementResult vo=new RecHisSettlementResult();
        	vo.setOrgCode(list.get(0).getOrgCode());
        	//设置订单来源
        	vo.setBillSource(z.getValue());
        	//设置日期
        	vo.setSettleDate(sdf.parse(date));
        	//得到：商户收款金额当日金额
        	vo.setChannelAmount(summaryBill(date,z.getValue()));
        	//得到：HIS当日总金额（除去现金）
        	vo.setHisAmount(summaryHis(date,z.getValue()));
        	//HIS结算总金额
        	BigDecimal hisSettlementAmount=new BigDecimal(0);
        	//前一日金额
        	BigDecimal yesterdayAmount=new BigDecimal(0);
        	//当前结算金额
        	BigDecimal todayAmount=new BigDecimal(0);
        	//得出当前结算日期往前最近一个汇总日期
        	//String yesterToday = getDate(date,z.getValue());
        	//需要更新的结算日集合（不等于当前结算日期内有的交易日期）
        	//HashSet<String> set = new HashSet<>();
        	String yesterday = DateUtil.getSpecifiedDayAfter(date, -1);
        	//遍历明细数据
        	for(RecHisSettlement v:list) {
        		if(z.getValue().equals(v.getBillSource())&&!v.getPayType().equals("0449")) {
        			//前一日金额计算(交易时间是结算日前一天的账单金额汇总)
            		if(sdfs.parse(yesterday+" 00:00:00").getTime()<=v.getPayTime().getTime()&&
            				v.getPayTime().getTime()<=sdfs.parse(yesterday+" 23:59:59").getTime()) {
            			yesterdayAmount=yesterdayAmount.add(v.getAmount());
            		}
            		//计算当前结算金额(交易日期是结算日期的金额)
            		if(sdfs.parse(date+" 00:00:00").getTime()<=v.getPayTime().getTime()&&
            				v.getPayTime().getTime()<=sdfs.parse(date+" 23:59:59").getTime()) {
            			todayAmount=todayAmount.add(v.getAmount());
            		}
            		//计算HIS结算总金额
            		hisSettlementAmount=hisSettlementAmount.add(v.getAmount());
            		/*String time = sdf.format(v.getPayTime());
            		if(!date.equals(time)&&!set.contains(time)) {
            			set.add(time);
            		}*/
        		}
        	}
        	//前一日金额
        	vo.setYesterdayAmount(yesterdayAmount);
        	//HIS结算总金额
        	vo.setHisSettlementAmount(hisSettlementAmount);
        	//当日结算后金额
        	vo.setTodayUnsettleAmount(vo.getHisAmount().subtract(todayAmount));
        	//结算以前金额
        	vo.setBeforeSettlementAmount(hisSettlementAmount.subtract(todayAmount.add(yesterdayAmount)));
        	//遗漏结账金额
        	vo.setOmissionAmount(new BigDecimal(0));
        	//更新处理
        	update(date,vo,z.getValue());
        	/*set.add(yesterToday);
        	for(String v:set) {
        		update(date,vo,z.getValue(),v);
        	}*/
        	
        	/*//得到请求结算日金额后一日
        	String tomorrowDate = DateUtil.getSpecifiedDayAfter(date, 1);
        	//查询出后一结算日的统计信息
        	RecHisSettlementResult tomorrowData = recHisSettlementResultDao.findBySettleDateAndBillSource(sdf.parse(tomorrowDate),z.getValue());
        	//更新后一结算日的当日未结金额
        	if(tomorrowData!=null) {
        		vo.setTodayUnsettleAmount(tomorrowData.getYesterdayAmount());
        	}*/
        	//保存
        	recHisSettlementResultDao.save(vo);
    	}
    }
    
    private String getDate(String date,String billSource,String orgCode) throws Exception {
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    	//查询出当前结算日的前一条的统计信息
    	List<RecHisSettlementResult> yesterList = recHisSettlementResultDao.findBySettleDateLessThanEqualAndBillSourceAndOrgCodeOrderBySettleDateDesc(sdf.parse(date),billSource,orgCode);
    	if(yesterList!=null&&yesterList.size()>0) {
    		return sdf.format(yesterList.get(0).getSettleDate());
    	}
    	return null;
    }
    
    /**
     * 更新前一天的数据
     */
    private void update(String date,RecHisSettlementResult vo,String billSource) throws Exception {
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    	String yesterToday=getDate(date,billSource,vo.getOrgCode());
    	if(StringUtils.isNotBlank(yesterToday)) {
    		//查询出当前结算日往前最近一个汇总日期
        	RecHisSettlementResult yesterData = recHisSettlementResultDao.findBySettleDateAndBillSourceAndOrgCode(sdf.parse(yesterToday),billSource,vo.getOrgCode());
        	//更新前一结算日的 (当日结账后金额) 和 (遗漏结账金额 )
        	if(yesterData!=null) {
        		//遗漏结账金额
        		yesterData.setOmissionAmount(yesterData.getTodayUnsettleAmount().subtract(vo.getYesterdayAmount()));
        		//当日结账后金额
        		yesterData.setTodayUnsettleAmount(vo.getYesterdayAmount());
        		recHisSettlementResultDao.save(yesterData);
        	}
    	}
    }
    
    
    //汇总渠道
    private BigDecimal summaryBill(String date,String billSource){
    	String sql="select ifnull(SUM(if(order_state='0256',-ABS(t.pay_amount),t.pay_amount)),0) "
    			+ "from t_thrid_bill t where t.trade_datatime >= '"+date+" 00:00:00' and t.trade_datatime <= '"+date+" 23:59:59' and t.bill_source='"+billSource+"'";
    	String sum = getSum(sql);
    	logger.info("汇总渠道查询sql=="+sql);
		return new BigDecimal(sum);
    }
    
    //汇总His
    private BigDecimal summaryHis(String date,String billSource){
    	String sql="select ifnull(SUM(if(order_state='0256',-ABS(t.pay_amount),t.pay_amount)),0) "
    			+ "from t_rec_histransactionflow t where t.trade_datatime >= '"+date+" 00:00:00' and t.trade_datatime <= '"+date+" 23:59:59' and t.pay_type!='0049'"
    					+ " and t.bill_source='"+billSource+"'";
    	String sum = getSum(sql);
    	logger.info("汇总His查询sql=="+sql);
		return new BigDecimal(sum);
    }
    
    /*//汇总当日未结金额
    private BigDecimal summaryTodayAmount(String yesterToday,String billSource,String orgCode){
    	String sql="select ifnull(SUM(if(t.order_type='0256',-ABS(t.amount),t.amount)),0) "
    			+"from t_rec_his_settlement t where 1=1 "
    			+ " and t.bill_source='"+billSource+"'"
    			+ " and t.settlement_date <>'"+yesterToday+"'"
    			+ " and t.pay_time >= '"+yesterToday+" 00:00:00'"
    			+ " and t.pay_time <= '"+yesterToday+" 23:59:59'"
    			+ " and t.org_code='"+orgCode+"'";
    	String sum = getSum(sql);
    	logger.info("汇总当日未结金额查询sql=="+sql);
		return new BigDecimal(sum);
    }*/
    
    /*private String getDate(String date,String billSource) throws Exception {
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    	//查询出当前结算日的前一条的统计信息
    	List<RecHisSettlementResult> yesterList = recHisSettlementResultDao.findBySettleDateLessThanEqualAndBillSourceOrderBySettleDateDesc(sdf.parse(date),billSource);
    	if(yesterList!=null&&yesterList.size()>0) {
    		return sdf.format(yesterList.get(0).getSettleDate());
    	}
    	return null;
    }
    
    
    private void update(String date,RecHisSettlementResult vo,String billSource,
    		String yesterToday) throws Exception {
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    	if(StringUtils.isNotBlank(yesterToday)) {
    		//查询出当前结算日往前最近一个汇总日期
        	RecHisSettlementResult yesterData = recHisSettlementResultDao.findBySettleDateAndBillSource(sdf.parse(yesterToday),billSource);
        	//更新前一结算日的当日未结金额
        	if(yesterData!=null) {
        		yesterData.setTodayUnsettleAmount(summaryTodayAmount(yesterToday,billSource,vo.getOrgCode()));
        		recHisSettlementResultDao.save(yesterData);
        	}
    	}
    }*/
    
    
    /*private void update(String date,RecHisSettlementResult vo,String billSource,
    		String yesterToday,BigDecimal yesterTodayAmount) throws Exception {
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    	if(StringUtils.isNotBlank(yesterToday)) {
    		//查询出当前结算日往前最近一个汇总日期
        	RecHisSettlementResult yesterData = recHisSettlementResultDao.findBySettleDateAndBillSource(sdf.parse(yesterToday),billSource);
        	//更新前一结算日的当日未结金额
        	if(yesterData!=null) {
        		yesterData.setTodayUnsettleAmount(yesterTodayAmount);
        		recHisSettlementResultDao.save(yesterData);
        	}
    	}
    	//得到请求结算日金额后一日
    	String tomorrowDate = DateUtil.getSpecifiedDayAfter(date, 1);
    	//查询出后一结算日的统计信息
    	RecHisSettlementResult tomorrowData = recHisSettlementResultDao.findBySettleDateAndBillSource(sdf.parse(tomorrowDate),billSource);
    	//更新后一结算日的当日未结金额
    	if(tomorrowData!=null) {
    		vo.setTodayUnsettleAmount(tomorrowData.getYesterdayAmount());
    	}
    }*/

	@Override
	public List<Map<String, Object>> getSettlementPage(RecHisSettlementResultVo vo, List<Organization> orgList,
			PageRequest pageRequest) throws ParseException {
		//String orderBySql = concatOrderBySql(pageRequest);
		String sql = settlementSql(vo, orgList);
		//统计与合计
		List<Map<String, Object>> list = handleNativeSql(sql,
				new String[] {"settleDate","channelAmount","hisAmount","hisSettlementAmount","yesterdayAmount","todayUnsettleAmount","beforeSettlementAmount","billSource","orgCode","omissionAmount"});
		//合计map
		Map<String, Object> sumMap=new HashMap<>();
		for(Map<String, Object> v:list) {
			//异常金额 商户收款金额减HIS当日总金额
			BigDecimal exceptionAmount=new BigDecimal(0);
			//小计HIS结算总金额 - 前一日金额 - 结算以前金额 +当日结算后金额+漏结金额
			BigDecimal sumAmount=new BigDecimal(0);
			exceptionAmount=new BigDecimal(String.valueOf(v.get("channelAmount"))).subtract(new BigDecimal(String.valueOf(v.get("hisAmount"))));
			sumAmount=new BigDecimal(String.valueOf(v.get("hisSettlementAmount"))).subtract(new BigDecimal(String.valueOf(v.get("yesterdayAmount"))));
			sumAmount=sumAmount.subtract(new BigDecimal(String.valueOf(v.get("beforeSettlementAmount"))));
			sumAmount=sumAmount.add(new BigDecimal(String.valueOf(v.get("todayUnsettleAmount"))));
			sumAmount=sumAmount.add(new BigDecimal(String.valueOf(v.get("omissionAmount"))));
			//加入异常金额与小计
			v.put("exceptionAmount", exceptionAmount);
			v.put("sumAmount", sumAmount);
			if(sumMap.size()>0) {
				sumMap.put("channelAmount", new BigDecimal(String.valueOf(sumMap.get("channelAmount"))).add(new BigDecimal(String.valueOf(v.get("channelAmount")))));
				sumMap.put("hisAmount", new BigDecimal(String.valueOf(sumMap.get("hisAmount"))).add(new BigDecimal(String.valueOf(v.get("hisAmount")))));
				sumMap.put("hisSettlementAmount", new BigDecimal(String.valueOf(sumMap.get("hisSettlementAmount"))).add(new BigDecimal(String.valueOf(v.get("hisSettlementAmount")))));
				sumMap.put("yesterdayAmount", new BigDecimal(String.valueOf(sumMap.get("yesterdayAmount"))).add(new BigDecimal(String.valueOf(v.get("yesterdayAmount")))));
				sumMap.put("todayUnsettleAmount", new BigDecimal(String.valueOf(sumMap.get("todayUnsettleAmount"))).add(new BigDecimal(String.valueOf(v.get("todayUnsettleAmount")))));
				sumMap.put("exceptionAmount", new BigDecimal(String.valueOf(sumMap.get("exceptionAmount"))).add(new BigDecimal(String.valueOf(v.get("exceptionAmount")))));
				sumMap.put("beforeSettlementAmount", new BigDecimal(String.valueOf(sumMap.get("beforeSettlementAmount"))).add(new BigDecimal(String.valueOf(v.get("beforeSettlementAmount")))));
				sumMap.put("sumAmount", new BigDecimal(String.valueOf(sumMap.get("sumAmount"))).add(new BigDecimal(String.valueOf(v.get("sumAmount")))));
				sumMap.put("omissionAmount", new BigDecimal(String.valueOf(sumMap.get("omissionAmount"))).add(new BigDecimal(String.valueOf(v.get("omissionAmount")))));
			}else {
				sumMap.put("settleDate", "合计");
				sumMap.put("channelAmount", v.get("channelAmount"));
				sumMap.put("hisAmount", v.get("hisAmount"));
				sumMap.put("hisSettlementAmount", v.get("hisSettlementAmount"));
				sumMap.put("yesterdayAmount", v.get("yesterdayAmount"));
				sumMap.put("todayUnsettleAmount", v.get("todayUnsettleAmount"));
				sumMap.put("exceptionAmount", v.get("exceptionAmount"));
				sumMap.put("beforeSettlementAmount", v.get("beforeSettlementAmount"));
				sumMap.put("sumAmount", v.get("sumAmount"));
				sumMap.put("omissionAmount", v.get("omissionAmount"));
			}
		}
		if(list!=null&&list.size()>0) {
			list.add(sumMap);
		}
		return list;
	}
	
	private String settlementSql(RecHisSettlementResultVo vo, List<Organization> orgList) {
		String sql ="select t.settle_date settleDate,t.channel_amount channelAmount,t.his_amount hisAmount,t.his_settlement_amount hisSettlementAmount," + 
				"	t.yesterday_amount yesterdayAmount,t.today_unsettle_amount todayUnsettleAmount,t.before_settlement_amount beforeSettlementAmount," +
				" t.bill_source billSource ,t.org_code orgCode ,t.omission_amount omissionAmount"+
				" from t_rec_his_settlement_result t where 1=1 ";
		if(orgList!=null&&orgList.size()>0) {
			String orgCodes="";
			for(Organization v:orgList) {
				if(StringUtils.isBlank(orgCodes)) {
					orgCodes="'"+v.getCode()+"'";
				}else {
					orgCodes=orgCodes+",'"+v.getCode()+"'";
				}
			}
			sql=sql+ " and t.org_code in("+orgCodes+")";
		}
		if(StringUtils.isNotBlank(vo.getBillSource())) {
			sql=sql+ " and t.bill_source='"+vo.getBillSource()+"'";
		}
		if(StringUtils.isNotBlank(vo.getSettleDate())) {
			String[] list = vo.getSettleDate().split("~");
			sql=sql+ " and t.settle_date >='"+list[0].trim()+"' and t.settle_date <='"+list[1].trim()+"'";
		}
		sql=sql+" ORDER BY t.settle_date ASC";
		logger.info("结算日对账查询sql=="+sql);
		return sql;
	}
}

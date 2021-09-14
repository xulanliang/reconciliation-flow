package com.yiban.rec.reconciliations.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.rec.dao.HealthExceptionDao;
import com.yiban.rec.dao.RecLogDao;
import com.yiban.rec.dao.TradeCheckFollowDao;
import com.yiban.rec.domain.HealthException;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.log.RecLog;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.reconciliations.ReconciliationsService;
import com.yiban.rec.reconciliations.ReconciliationsSummaryService;
import com.yiban.rec.reconciliations.Reconciliationsable;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.LogCons;

/**
 * @author swing
 * @date 2018年7月19日 下午1:44:21 类说明 对账接口实现类(包括隔日对账，汇总)
 */
@Service
public class ReconciliationsServiceImpl implements ReconciliationsService {
	@Autowired
	private HospitalConfigService hospitalConfigService;
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("reconciliationsable-pool-%d")
			.build();
	private final ExecutorService pool = new ThreadPoolExecutor(5, 8, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(216), threadFactory);
	@Autowired
	private TradeCheckFollowDao tradeCheckFollowDao;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private ReconciliationsSummaryService reconciliationsSummaryService;
	
	@Autowired
	private HealthExceptionDao healthExceptionDao;
	
	@Autowired
	private RecLogDao recLogDao;
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	@Autowired
	private EntityManager entityManager;
	

	/**
	 * 注册对账实现类
	 * 
	 * @param orgCode
	 * @param date
	 * @return
	 */
	private List<Reconciliationsable> retistService(String orgCode, String date) {
		List<Reconciliationsable> servieList = new ArrayList<>();
		AppRuntimeConfig runtimeConfig = hospitalConfigService.loadConfig();
		String recType = runtimeConfig.getRecType();
		// 根据配置决定启用哪种对账实现
		if (StringUtils.isNotEmpty(recType)) {
			// 微信
			if (recType.contains(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue())) {
				servieList.add(new WechatPayReconciliations(orgCode, date));
			}
			// 支付宝
			if (recType.contains(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue())) {
				servieList.add(new AliPayReconciliations(orgCode, date));
			}
			// 银行
			if (recType.contains(EnumTypeOfInt.PAY_TYPE_BANK.getValue())) {
				servieList.add(new BankReconciliations(orgCode, date));
			}
			
			// 医保
			if (recType.contains(EnumTypeOfInt.PAY_TYPE_HEALTH.getValue())) {
				servieList.add(new HealthTypeReconciliations(orgCode, date));
			}
			// 聚合支付
			if (recType.contains(EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue())) {
				servieList.add(new AggregateReconciliations(orgCode, date));
			}
			// 武进一账通
			if (recType.contains(EnumTypeOfInt.PAY_TYPE_WJYZT.getValue())) {
				servieList.add(new WJYZTReconciliations(orgCode, date));
			}
			// 云闪付
			if (recType.contains(EnumTypeOfInt.PAY_TYPE_UNIONPAY.getValue())) {
				servieList.add(new UnionPayReconciliations(orgCode, date));
			}
			// 现金对账
			if (recType.contains(EnumTypeOfInt.CASH_PAYTYPE.getValue())) {
				servieList.add(new CashReconciliations(orgCode, date));
            }
			// 社保卡银行卡对账
			if(recType.contains(EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue())){
				servieList.add(new HealthCareBankReconciliations(orgCode, date));
			}
			// 网银对账
			if(recType.contains(EnumTypeOfInt.PAY_TYPE_ONLINE_BANK.getValue())){
				servieList.add(new OnlineBankReconciliations(orgCode, date));
			}
		}
		return servieList;
	}

	/**
	 * 获取子机构编码（如果有）
	 * 
	 * @param orgCode
	 * @return
	 */
	private Set<String> initOrgCodeSet(String orgCode) {
		Set<String> orgCodeSet = new HashSet<>();
		orgCodeSet.add(orgCode);
		Organization org = organizationService.findByCode(orgCode);
		if (org != null) {
			for (Organization child : org.getChildren()) {
				orgCodeSet.add(child.getCode());
			}
		}
		return orgCodeSet;
	}

	

	/**
	 * 多线程对账逻辑 每个线程跑一个对账实现,最后汇总对账结果
	 */
	@Override
	@Transactional()
	public void compareBill(String orgCode, String date) {
		log.info("对账任务开始");
		RecLog recLog = recLogDao.findByOrderDateAndOrgCode(date, orgCode);
		if(null == recLog) {
		    recLog = new RecLog();
		}
		recLog.setRecResult(LogCons.REC_SUCCESS);
		recLog.setCreatedDate(DateUtil.getCurrentDateTime());
		recLog.setOrgCode(orgCode);
		recLog.setOrderDate(date);
		long time = System.currentTimeMillis();
		Set<String> orgCodeSet = initOrgCodeSet(orgCode);
		List<Reconciliationsable> servieList = retistService(orgCode, date);
		// 多个对账线程的结果集
		List<TradeCheckFollow> resultList = new ArrayList<>();
		if (servieList.size() > 0) {
			// 每个对账任务的结果集
			List<Future<List<TradeCheckFollow>>> taskList = new ArrayList<>(50);
			final CountDownLatch countDownLatch = new CountDownLatch(servieList.size());
		
			// 循环所有的对账任务，每个线程一个对账任务
			for (Reconciliationsable service : servieList) {
				Future<List<TradeCheckFollow>> future = pool.submit(new Callable<List<TradeCheckFollow>>() {
					@Override
					public List<TradeCheckFollow> call() throws Exception {
						List<TradeCheckFollow> result = new ArrayList<>();
						try {
							// 对账抛出异常
							result = service.compareBill();
						} catch (Exception e) {
							log.error("对账异常:{}", e);
						} finally {
							countDownLatch.countDown();
						}
						return result;
					}
				});
				taskList.add(future);
			}

			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
			    recLog.setRecResult(LogCons.REC_FAIL);
				log.error(e.getMessage());
			}

			// 合并所有对账结果
			for (Future<List<TradeCheckFollow>> task : taskList) {
				List<TradeCheckFollow> list = null;
				try {
					list = task.get();
					resultList.addAll(list);
				} catch (InterruptedException | ExecutionException e) {
				    recLog.setRecResult(LogCons.REC_FAIL);
					log.error("获取对账结果失败");
				}
			}
	
			String[] orgCodeArr = new String[orgCodeSet.size()];
			orgCodeSet.toArray(orgCodeArr);
			// 清除对账记录
			tradeCheckFollowDao.deleteTradeCheckFollow(date, date, orgCodeArr);
			if(resultList.size() > 0){
				Long start = System.currentTimeMillis();
				log.info("start ------ 批量保存异常账单：" + resultList.size());
				// 保存数据
				tradeCheckFollowDao.save(resultList);
//				this.saveBatch(resultList);
				log.info("end ------ 批量保存异常账单   耗时：" + (System.currentTimeMillis() - start));
			}
			// 以下 改成异步汇总
			// 删除汇总记录
//			reconciliationsSummaryService.delete(orgCode, date);
			// 重新汇总
//			reconciliationsSummaryService.summary(orgCode, date);
		}else {
		    //recLog.setRecResult(LogCons.REC_FAIL);
		}
		time = System.currentTimeMillis() - time;
		log.info("对账任务结束,异常账单数:{},耗时:{}毫秒", resultList.size(), time);
		recLogDao.save(recLog);
	}
	
	

	/**
	 * 注册医保对账
	 */
	public List<Reconciliationsable> healthRetistService(String orgCode, String date){
		List<Reconciliationsable> servieList = new ArrayList<>();
		AppRuntimeConfig runtimeConfig = hospitalConfigService.loadConfig();
		String recType = runtimeConfig.getRecType();
		if (StringUtils.isNotEmpty(recType)) {
			// 医保
			if (recType.contains(EnumTypeOfInt.PAY_TYPE_HEALTH.getValue())) {
				//得到医保对账需要对账的金额维度
				String healthAmountType = propertiesConfigService.findValueByPkey(ProConstants.healthAmountTypeKey);
				servieList.add(new HealthCareOfficiaReconciliations(orgCode, date,healthAmountType));
			}
		}
		return servieList;
	}

	@Override
	@Transactional()
	public void compareHealthBill(String orgCode, String date) throws Exception {
		log.info("对账任务开始");
		RecLog recLog = recLogDao.findByOrderDateAndOrgCode(date, orgCode);
        if(null == recLog) {
            recLog = new RecLog();
        }
        recLog.setRecResult(LogCons.REC_SUCCESS);
        recLog.setCreatedDate(DateUtil.getCurrentDateTime());
        recLog.setOrgCode(orgCode);
        recLog.setOrderDate(date);
		long time = System.currentTimeMillis();
		Set<String> orgCodeSet = initOrgCodeSet(orgCode);
		List<Reconciliationsable> servieList = healthRetistService(orgCode, date);
		// 多个对账线程的结果集
		List<HealthException> resultList = new ArrayList<>();
		if (servieList.size() > 0) {
			// 每个对账任务的结果集
			List<Future<List<HealthException>>> taskList = new ArrayList<>(50);
			final CountDownLatch countDownLatch = new CountDownLatch(servieList.size());
		
			// 循环所有的对账任务，每个线程一个对账任务
			for (Reconciliationsable service : servieList) {
				Future<List<HealthException>> future = pool.submit(new Callable<List<HealthException>>() {
					@Override
					public List<HealthException> call() throws Exception {
						List<HealthException> result = new ArrayList<>();
						try {
							// 对账抛出异常
							result = service.compareHealthBill();
						} catch (Exception e) {
							log.error("对账异常:{}", e.getMessage());
							e.printStackTrace();
						} finally {
							countDownLatch.countDown();
						}
						return result;
					}
				});
				taskList.add(future);
			}

			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
			    recLog.setRecResult(LogCons.REC_FAIL);
				log.error(e.getMessage());
			}

			// 合并所有对账结果
			for (Future<List<HealthException>> task : taskList) {
				List<HealthException> list = null;
				try {
					list = task.get();
					resultList.addAll(list);
				} catch (InterruptedException | ExecutionException e) {
				    recLog.setRecResult(LogCons.REC_FAIL);
					log.error("获取对账结果失败");
				}
			}
	
			String[] orgCodeArr = new String[orgCodeSet.size()];
			orgCodeSet.toArray(orgCodeArr);
			// 清除对账记录
			healthExceptionDao.deleteException(DateUtil.stringLineToDateTime(date,"yyyy-MM-dd"), DateUtil.stringLineToDateTime(date+" 23:59:59","yyyy-MM-dd HH:mm:ss"), orgCodeArr);
			if(resultList.size() > 0){
				// 保存数据
				healthExceptionDao.save(resultList);
			}
		}else {
		   // recLog.setRecResult(LogCons.REC_FAIL);
		}
		time = System.currentTimeMillis() - time;
		log.info("对账任务结束,异常账单数:{},耗时:{}毫秒", resultList.size(), time);
		recLogDao.save(recLog);
	}
	
	private void saveBatch(List<TradeCheckFollow> resultList) {
		for (int i = 0; i < resultList.size(); i++) {
			entityManager.persist(resultList.get(i));
			if (i % 30 == 0) {
				entityManager.flush();
				entityManager.clear();
			}
		}
	} 
}

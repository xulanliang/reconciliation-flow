package com.yiban.rec.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.AccountService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.util.OprPageRequest;
import com.yiban.rec.dao.RetryOfHisToCenterTaskFailInfoDao;
import com.yiban.rec.dao.task.HisToCenterTaskDao;
import com.yiban.rec.domain.HisPayResult;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.Platformflow;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.domain.basicInfo.RetryOfHisToCenterTaskFailInfo;
import com.yiban.rec.domain.task.HisToCenterTaskInfo;
import com.yiban.rec.service.HisToCenterTaskService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.SqlUtil;
import com.yiban.rec.util.StringUtil;

@Service
@Transactional(readOnly = true)
public class HisToCenterTaskServiceImpl extends BaseOprService implements HisToCenterTaskService {

	@Autowired
	private HisToCenterTaskDao hisToCenterTaskDao;
	@Autowired
	private AccountService accoutService; 
	@Autowired
	private RetryOfHisToCenterTaskFailInfoDao retryOfHisToCenterTaskFailInfoDao;
	@Override
	public HisToCenterTaskInfo getHisToCenterTaskInfoById(Long id) {
		return hisToCenterTaskDao.findOne(id);
	}

	@Override
	public HisToCenterTaskInfo getHisToCenterTaskInfoByName(String jobName) {
		
		return hisToCenterTaskDao.getHisToCenterTaskInfoByName(jobName);
	}

	@Override
	@Transactional
	public ResponseResult save(HisToCenterTaskInfo hisToCenterTask) {
		try {
			User user = accoutService.getCurrentUser();
			hisToCenterTask.setLastModifiedById(user.getId());
			hisToCenterTask.setCreatedById(user.getId());
			hisToCenterTask.setStartat(null);
			//默认激活
			hisToCenterTask.setIsActived(CommonEnum.IsActive.ISACTIVED.getValue());
			//默认运行
			hisToCenterTask.setJobsstatus(CommonEnum.IsActive.NOTACTIVE.getValue());
			hisToCenterTaskDao.save(hisToCenterTask);
		} catch (Exception e) {
			logger.error("保存HisToCenter任务异常，"+e.getMessage());
			return ResponseResult.failure("保存HisToCenter任务异常，"+e.getMessage());
		}
		return ResponseResult.success("保存HisToCenter任务成功");
	}

	/**
	 * @param type
	 * @param hour
	 * @param minute
	 * @param second
	 */
	private String getJobcorn(Integer type, Integer hour, Integer minute, Integer second) {
		/**
		 * type 1 按小时 2 按分钟 3按秒
		 */
		String jobcorn = "";
		switch (type) {
		case 1:
			jobcorn += second + " " + minute + " " + hour + " " + "*" + " " + "*" + " " + "?" + " " + "*";
			break;
		case 2:
			jobcorn += second + " " + minute + " " + "*" + " " + "*" + " " + "*" + " " + "?" + " " + "*";
			break;
		case 3:
			jobcorn += second + " " + "*" + " " + "*" + " " + "*" + " " + "*" + " " + "?" + " " + "*";
			break;
		default:
			break;
		}
		return jobcorn;

	}

	@Override
	@Transactional
	public ResponseResult delete(Long id) {
		try {
			hisToCenterTaskDao.delete(id);
		} catch (Exception e) {
			logger.error("删除HisToCenter任务异常,"+e.getMessage());
			return ResponseResult.failure("删除HisToCenter任务异常,"+e.getMessage());
		}
		return ResponseResult.success("删除HisToCenter任务成功");
	}

	@Override
	@Transactional
	public ResponseResult update(HisToCenterTaskInfo hisToCenterTask) {
		try {
			User user = accoutService.getCurrentUser();
			Long id = hisToCenterTask.getId();
			if (!StringUtil.isNullOrEmpty(id)) {
				HisToCenterTaskInfo hisToCenterTaskDb = hisToCenterTaskDao.findOne(id);
				if (hisToCenterTaskDb != null) {
					// 存在则更新
					hisToCenterTaskDb.setOrgNo(hisToCenterTask.getOrgNo());
					hisToCenterTaskDb.setJobname(hisToCenterTask.getJobname());
					hisToCenterTaskDb.setJobclass(hisToCenterTask.getJobclass());
					hisToCenterTaskDb.setJobcorn(hisToCenterTask.getJobcorn());
					hisToCenterTaskDb.setJobparam(hisToCenterTask.getJobparam());
					hisToCenterTaskDb.setJobinterface(hisToCenterTask.getJobinterface());
					hisToCenterTaskDb.setStartat(hisToCenterTask.getStartat());
					hisToCenterTaskDb.setJobdesc(hisToCenterTask.getJobdesc());
					hisToCenterTaskDb.setLastModifiedById(user.getId());
					hisToCenterTaskDao.save(hisToCenterTaskDb);
					return ResponseResult.success("更新HisToCenter任务成功");
				}
			}
			// 新增
			hisToCenterTaskDao.save(hisToCenterTask);
		} catch (Exception e) {
			logger.error("更新HisToCenter任务异常，"+e.getMessage());
			return ResponseResult.failure("更新HisToCenter任务异常，"+e.getMessage());
		}
		return ResponseResult.success("更新HisToCenter任务成功");
	}

	@Override
	public Page<Map<String, Object>> getHisToCenterTaskInfoList(OprPageRequest pagerequest, Long orgId) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.id,s.created_date createdDate,s.is_actived isActived,s.is_deleted isDeleted,"
				+ "s.last_modified_date lastModifiedDate,s.version,s.jobclass,s.jobcorn,s.jobdesc,"
				+ "s.jobinterface,s.jobname,s.jobparam,s.jobsstatus,s.startat,s.created_by_id createdById,"
				+ "s.last_modified_by_id lastModifiedById,s.org_no orgNo,org. NAME orgName"
				+ " FROM t_his_to_center_schedule s LEFT JOIN t_organization org ON s.org_no = org.id");
		sb.append(" WHERE s.is_deleted=0 AND s.is_actived=1");
		if (!StringUtil.isNullOrEmpty(orgId))
			sb.append(" AND s.org_no= '").append(orgId).append("'");
		System.out.println(sb.toString());
		Page<Map<String, Object>> page = super.handleNativeSql(sb.toString(), pagerequest,
				new String[] { "id", "createdDate", "isActived", "isDeleted", "lastModifiedDate", "version", "jobclass",
						"jobcorn", "jobdesc", "jobinterface", "jobname", "jobparam", "jobsstatus", "startat",
						"createdById", "lastModifiedById", "orgNo", "orgName" });
		return page;
	}

	
	@Override
	@Transactional
	public ResponseResult updateStatus(Integer status, Long id) {
		try {
			if(StringUtil.isNullOrEmpty(status)||(status!=1&&status!=0)){
				return ResponseResult.failure("状态非法");
			}
			hisToCenterTaskDao.updateStatus(status, id);
		} catch (Exception e) {
			logger.error("更新状态异常,"+e.getMessage());
			return ResponseResult.failure("更新状态异常,"+e.getMessage());
		}
		return ResponseResult.success("更新状态成功");
	}

	@Override
	public List<HisToCenterTaskInfo> getHisToCenterTaskInfo() {
		return hisToCenterTaskDao.getHisToCenterTaskInfoOfYesterday();
	}

	@Override
	public HisToCenterTaskInfo getHisToCenterInfo(Long orgNo){
		return hisToCenterTaskDao.getHisToCenterInfoByOrgNo(orgNo);
	}
	
	@Override
	public List<HisPayResult> getHisPayResultOfYesterday(Date startDate,Date endDate, List<Long> orgIds) {
		return hisToCenterTaskDao.getHisPayResultOfYesterday(startDate,endDate, orgIds);
	}

	@Override
	public List<Platformflow> getPlatformflowsOfYesterday(Date startDate,Date endDate, List<Long> orgIds) {
		return hisToCenterTaskDao.getPlatformflowsOfYesterday(startDate,endDate, orgIds);
	}

	@Override
	public List<ThirdBill> getThirdBillsOfYesterday(Date startDate,Date endDate, List<Long> orgIds) {
		return hisToCenterTaskDao.getThirdBillsOfYesterday(startDate,endDate, orgIds);
	}
	@Override
	public List<HisPayResult> getHisPayResultOfYesterdayById(Date startDate,Date endDate, String orgId) {
		return hisToCenterTaskDao.getHisPayResultOfYesterdayById(startDate,endDate, orgId);
	}

	@Override
	public List<HisTransactionFlow> getPlatformflowsOfYesterdayById(Date startDate,Date endDate, String orgId) {
		return hisToCenterTaskDao.getPlatformflowsOfYesterdayById(startDate,endDate, orgId);
	}

	@Override
	public List<ThirdBill> getThirdBillsOfYesterdayById(Date startDate,Date endDate, String orgId) {
		return hisToCenterTaskDao.getThirdBillsOfYesterdayById(startDate,endDate, orgId);
	}

	@Override
	public List<HisPayResult> getHisPayResultOfYesterday2() {
		return hisToCenterTaskDao.getHisPayResultOfYesterday2();
	}

	@Override
	public List<Platformflow> getPlatformflowsOfYesterday2() {
		return hisToCenterTaskDao.getPlatformflowsOfYesterday2();
	}

	@Override
	public List<ThirdBill> getThirdBillsOfYesterday2() {
		return hisToCenterTaskDao.getThirdBillsOfYesterday2();
	}

	@Override
	public List<HisPayResult> getHisPayResultOfFail(Date startDate,Date endDate, String orgNo, Integer startPosition,
			Integer endPosition) {
		
		return hisToCenterTaskDao.getHisPayResultOfFail(startDate,endDate, orgNo);
	}

	@Override
	public List<HisTransactionFlow> getPlatformflowsOfFail(Date startDate,Date endDate, String orgNo, Integer startPosition,
			Integer endPosition) {
		return hisToCenterTaskDao.getPlatformflowsOfFail(startDate,endDate, orgNo);
	}

	@Override
	public List<ThirdBill> getThirdBillsOfFail(Date startDate,Date endDate, String orgNo, Integer startPosition, Integer endPosition) {
		return hisToCenterTaskDao.getThirdBillsOfFail(startDate,endDate, orgNo);
	}
	
	@Override
	public List<Map<String, Object>> getOrgSimpleInfosByOrgNo(Set<Long> orgNos) {
		
		StringBuffer sb = new StringBuffer("SELECT org.id orgId,org.code orgNo FROM t_organization org WHERE 1=1 ");
		//org.code in ('1.12.','1.3.')
		if(!StringUtil.isNullOrEmpty(orgNos)){
			sb.append(" AND org.id in "+SqlUtil.getSetInConditionInt(orgNos));
		}
		System.out.println("sql----------"+sb.toString());
		List<Map<String, Object>> list = super.handleNativeSql(sb.toString(), new String[] { "orgId", "orgNo"});

		return list;
	}
	@Override
	public Map<String, Object> getOrgSimpleInfosByOrgNo(Long orgNo) {
		
		StringBuffer sb = new StringBuffer("SELECT org.id orgId,org.code orgNo FROM t_organization org WHERE 1=1 ");
		//org.code in ('1.12.','1.3.')
		if(!StringUtil.isNullOrEmpty(orgNo)){
			sb.append(" AND org.id = "+orgNo);
		}
		System.out.println("sql----------"+sb.toString());
		List<Map<String, Object>> list = super.handleNativeSql(sb.toString(), new String[] { "orgId", "orgNo"});
		Map<String, Object> result=null;
		if(!StringUtil.isNullOrEmpty(list)){
			result=list.get(0);
		}
		return result;
	}
	@Override
	public List<Map<String, Object>> getOrgSimpleInfosByOrgCode(Set<String> orgCodes) {
		
		StringBuffer sb = new StringBuffer("SELECT org.id orgId,org.code orgNo,org.parent_id parentId FROM t_organization org WHERE 1=1 ");
		//org.code in ('1.12.','1.3.')
		if(!StringUtil.isNullOrEmpty(orgCodes)){
			sb.append(" AND org.code in "+SqlUtil.getSetInConditionStr(orgCodes));
		}
		System.out.println("sql----------"+sb.toString());
		List<Map<String, Object>> list = super.handleNativeSql(sb.toString(), new String[] { "orgId", "orgNo","parentId"});

		return list;
	}
	@Override
	public Map<String, Object> getOrgSimpleInfosByOrgCode(String orgCode) {
		
		StringBuffer sb = new StringBuffer("SELECT org.id orgId,org.code orgNo,org.parent_id parentId FROM t_organization org WHERE 1=1 ");
		//org.code in ('1.12.','1.3.')
		if(!StringUtil.isEmpty(orgCode)){
			sb.append(" AND org.code = "+orgCode);
		}
		System.out.println("sql----------"+sb.toString());
		List<Map<String, Object>> list = super.handleNativeSql(sb.toString(), new String[] { "orgId", "orgNo","parentId"});
		Map<String, Object> result=null;
		if(!StringUtil.isNullOrEmpty(list)){
			result=list.get(0);
		}
		return result;
	}
	@Override
	public List<Map<String, Object>> getSimpleMetaDatasByPayType(Set<Integer> payTypes) {
		
		StringBuffer sb = new StringBuffer("SELECT m.id id,m.value value,m.name name FROM t_meta_data m WHERE 1=1 ");
		if(!StringUtil.isNullOrEmpty(payTypes)){
			sb.append(" AND m.id in "+SqlUtil.getSetInConditionInt(payTypes));
		}
		System.out.println("sql----------"+sb.toString());
		List<Map<String, Object>> list = super.handleNativeSql(sb.toString(), new String[] { "id", "value","name"});

		return list;
	}
	@Override
	public Page<RetryOfHisToCenterTaskFailInfo> getetryOfHisToCenterTaskFailInfo(String orgNo, Integer billType,String billDate,Integer startPosition,Integer endPosition) {
		Sort sort=new Sort(Direction.DESC, "id");
		Integer page=startPosition;
		Integer size=endPosition+1-startPosition;
		Pageable pageable=new PageRequest(page, size, sort);
		Specification<RetryOfHisToCenterTaskFailInfo> specification = new Specification<RetryOfHisToCenterTaskFailInfo>() {
			@Override
			public Predicate toPredicate(Root<RetryOfHisToCenterTaskFailInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (!StringUtils.isEmpty(orgNo)) {
					Path<Long> orgNoExp = root.get("orgNo");
					predicates.add(cb.equal(orgNoExp, orgNo));
				}
				if (!StringUtil.isNullOrEmpty(billType)) {
					Path<Date> billTypeExp = root.get("billType");
					predicates.add(cb.equal(billTypeExp, billType));
				}
				if (!StringUtils.isEmpty(billDate)) {
					Path<Long> billDateExp = root.get("billDate");
					predicates.add(cb.equal(billDateExp, billDate));
				}
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		Page<RetryOfHisToCenterTaskFailInfo> result=retryOfHisToCenterTaskFailInfoDao.findAll(specification, pageable);
		return result;
	}

	@Override
	public List<Map<String, Object>> getSimpleMetaDatasByPayCode(Set<String> payCodes) {
		StringBuffer sb = new StringBuffer("SELECT m.id id,m.value value,m.name name FROM t_meta_data m WHERE 1=1 ");
		if(!StringUtil.isNullOrEmpty(payCodes)){
			sb.append(" AND m.value in "+SqlUtil.getSetInConditionStr(payCodes));
		}
		System.out.println("sql----------"+sb.toString());
		List<Map<String, Object>> list = super.handleNativeSql(sb.toString(), new String[] { "id", "value"});

		return list;
	}
	


}

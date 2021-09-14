package com.yiban.rec.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.AccountService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.util.OprPageRequest;
import com.yiban.rec.dao.task.ChannelScheduleInfoDao;
import com.yiban.rec.domain.task.ChannelScheduleInfo;
import com.yiban.rec.service.ChannelScheduleInfoService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.StringUtil;

@Service
@Transactional(readOnly = true)
public class ChannelScheduleInfoServiceImpl extends BaseOprService implements ChannelScheduleInfoService {

	@Autowired
	private ChannelScheduleInfoDao channelScheduleInfoDao;
	@Autowired
	private AccountService accoutService;

	@Override
	public ChannelScheduleInfo getChannelScheduleInfoById(Long id) {
		return channelScheduleInfoDao.findOne(id);
	}

	@Override
	public ChannelScheduleInfo getChannelScheduleInfoByName(String jobName) {
		return channelScheduleInfoDao.getChannelScheduleInfosByName(jobName);
	}

	@Override
	@Transactional
	public ResponseResult save(ChannelScheduleInfo channelScheduleInfo) {
		try {

			User user = accoutService.getCurrentUser();
			channelScheduleInfo.setLastModifiedById(user.getId());
			channelScheduleInfo.setCreatedById(user.getId());
			// 开始时间废弃
			channelScheduleInfo.setStartat(null);
			// 默认激活
			channelScheduleInfo.setIsActived(CommonEnum.IsActive.ISACTIVED.getValue());
			// 默认运行
			channelScheduleInfo.setJobsStatus(CommonEnum.IsActive.NOTACTIVE.getValue());
			channelScheduleInfoDao.save(channelScheduleInfo);

		} catch (Exception e) {
			logger.error("保存渠道数据同步任务异常," + e.getMessage());
			return ResponseResult.failure("保存渠道数据同步任务异常," + e.getMessage());
		}
		return ResponseResult.success("保存渠道数据同步任务成功");

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
			channelScheduleInfoDao.delete(id);
		} catch (Exception e) {
			logger.error("删除渠道数据同步任务异常，" + e.getMessage());
			return ResponseResult.failure("删除渠道数据同步任务异常，" + e.getMessage());
		}
		return ResponseResult.success("删除渠道数据同步任务成功");
	}

	@Override
	@Transactional
	public ResponseResult update(ChannelScheduleInfo channelScheduleInfo) {
		try {
			User user = accoutService.getCurrentUser();
			Long id = channelScheduleInfo.getId();
			if (!StringUtil.isNullOrEmpty(id)) {
				ChannelScheduleInfo channelScheduleInfoDb = channelScheduleInfoDao.findOne(id);
				if (channelScheduleInfoDb != null) {
					// 存在则更新
					channelScheduleInfoDb.setOrgNo(channelScheduleInfo.getOrgNo());
					channelScheduleInfoDb.setMetaPayId(channelScheduleInfo.getMetaPayId());
					channelScheduleInfoDb.setJobName(channelScheduleInfo.getJobName());
					channelScheduleInfoDb.setJobClass(channelScheduleInfo.getJobClass());
					channelScheduleInfoDb.setJobCorn(channelScheduleInfo.getJobCorn());
					channelScheduleInfoDb.setJobParam(channelScheduleInfo.getJobParam());
					channelScheduleInfoDb.setJobInterface(channelScheduleInfo.getJobInterface());
					channelScheduleInfoDb.setStartat(channelScheduleInfo.getStartat());
					channelScheduleInfoDb.setJobDesc(channelScheduleInfo.getJobDesc());
					channelScheduleInfoDb.setLastModifiedById(user.getId());
					channelScheduleInfoDao.save(channelScheduleInfoDb);
					return ResponseResult.success("更新渠道数据同步任务成功");
				}
			}
			channelScheduleInfoDao.save(channelScheduleInfo);
		} catch (Exception e) {
			logger.error("更新渠道数据同步任务异常" + e.getMessage());
			return ResponseResult.failure("更新渠道数据同步任务异常," + e.getMessage());
		}
		return ResponseResult.success("更新渠道数据同步任务成功");
	}

	@Override
	public Page<Map<String, Object>> getChannelScheduleInfoList(OprPageRequest pagerequest, Long orgId) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.id,s.created_date createdDate,s.is_actived isActived,s.is_deleted isDeleted,"
				+ "s.last_modified_date lastModifiedDate,s.version,s.job_class jobClass,s.job_corn jobCorn,s.job_desc jobDesc,"
				+ "s.job_interface jobInterface,s.job_name jobName,s.job_param jobParam,s.jobs_status jobsStatus,s.startat,s.created_by_id createdById,"
				+ "s.last_modified_by_id lastModifiedById,s.org_no orgNo,s.meta_pay_id metaPayId,org.name orgName,m.value payName"
				+ " FROM t_rec_channel_schedule_cfg s LEFT JOIN t_organization org ON s.org_no = org.id"
				+ " LEFT JOIN t_meta_data m ON s.meta_pay_id=m.id");
		sb.append(" WHERE s.is_deleted=0 AND s.is_actived=1");
		if (!StringUtil.isNullOrEmpty(orgId))
			sb.append(" AND s.org_no= '").append(orgId).append("'");
		System.out.println(sb.toString());
		Page<Map<String, Object>> page = super.handleNativeSql(sb.toString(), pagerequest,
				new String[] { "id", "createdDate", "isActived", "isDeleted", "lastModifiedDate", "version", "jobClass",
						"jobCorn", "jobDesc", "jobInterface", "jobName", "jobParam", "jobsStatus", "startat",
						"createdById", "lastModifiedById", "orgNo", "metaPayId", "orgName", "payName" });
		return page;
	}

	@Override
	@Transactional
	public ResponseResult updateStatus(Integer status, Long id) {
		try {
			if (StringUtil.isNullOrEmpty(status) || (status != 1 && status != 0)) {
				return ResponseResult.failure("状态非法");
			}
			channelScheduleInfoDao.updateStatus(status, id);
		} catch (Exception e) {
			logger.error("更新状态异常," + e.getMessage());
			return ResponseResult.failure("更新状态异常," + e.getMessage());
		}
		return ResponseResult.success("更新状态成功");
	}

	@Override
	public List<ChannelScheduleInfo> getChannelScheduleInfosOfYesterday() {
		List<ChannelScheduleInfo> channelYesterdayList = channelScheduleInfoDao.getChannelScheduleInfosOfYesterday();
		return channelYesterdayList;
	}

	@Override
	public List<ChannelScheduleInfo> getChannelScheduleInfosOfBeforeTwoDay() {
		List<ChannelScheduleInfo> channelYesterdayList = null;
		try {
			String twoDayBeforeStr = DateUtil.getNDayBefore(new Date(), 2);
			Date beforeTwoDay = DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", twoDayBeforeStr);
			channelYesterdayList = channelScheduleInfoDao.getChannelScheduleInfosOfBeforeTwoDay(beforeTwoDay);
		} catch (Exception e) {
			logger.error("查询t_rec_channel_schedule_cfg失败"+e.getMessage());
		}

		return channelYesterdayList;
	}

}

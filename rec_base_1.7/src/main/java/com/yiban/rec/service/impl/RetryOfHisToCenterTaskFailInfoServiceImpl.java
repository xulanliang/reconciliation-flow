package com.yiban.rec.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.AccountService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.dao.RetryOfHisToCenterTaskFailInfoDao;
import com.yiban.rec.domain.basicInfo.RetryOfHisToCenterTaskFailInfo;
import com.yiban.rec.service.RetryOfHisToCenterTaskFailInfoService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.CommonEnum;

@Service
@Transactional(readOnly = true)
public class RetryOfHisToCenterTaskFailInfoServiceImpl extends BaseOprService implements RetryOfHisToCenterTaskFailInfoService {
	@Autowired
	private RetryOfHisToCenterTaskFailInfoDao retryOfTaskFailInfoDao;
	@Autowired
	private AccountService accoutService;

	@Override
	public ResponseResult save(RetryOfHisToCenterTaskFailInfo retryOfTaskFailInfo) {
		User user = accoutService.getCurrentUser();
		retryOfTaskFailInfo.setLastModifiedById(user.getId());
		retryOfTaskFailInfo.setCreatedById(user.getId());
		retryOfTaskFailInfoDao.save(retryOfTaskFailInfo);
		return ResponseResult.success("保存HisToCenter重试失败任务成功");
	}

	@Override
	public ResponseResult save(List<RetryOfHisToCenterTaskFailInfo> channelOrderTaskInfos) {
		try {
			User user = accoutService.getCurrentUser();
			if (channelOrderTaskInfos != null && channelOrderTaskInfos.size() > 0) {
				for (RetryOfHisToCenterTaskFailInfo hisToCenterTaskInfo : channelOrderTaskInfos) {
					hisToCenterTaskInfo.setLastModifiedById(user.getId());
					hisToCenterTaskInfo.setCreatedById(user.getId());
				}
				retryOfTaskFailInfoDao.save(channelOrderTaskInfos);
				return ResponseResult.success("保存HisToCenter重试失败任务成功");
			} else {
				return ResponseResult.success("没有HisToCenter重试失败任务");
			}

		} catch (Exception e) {
			logger.error("保存HisToCenter重试失败任务失败",e);
			return ResponseResult.failure("保存HisToCenter重试失败任务失败");
		}

	}

	@Override
	public ResponseResult delete(Long id) {
		retryOfTaskFailInfoDao.delete(id);
		return ResponseResult.success("删除HisToCenter重试失败任务成功");
	}

	@Override
	public ResponseResult updateStatus(Integer status, String orgNo,Integer billType,Integer startPosition,Integer endPosition,String billDate) {
		User user = accoutService.getCurrentUser();
		RetryOfHisToCenterTaskFailInfo retryOfTaskFailInfoDb = retryOfTaskFailInfoDao.findByOrgNoAndBillTypeAndStartPositionAndEndPositionAndBillDate(orgNo, billType, startPosition, endPosition, billDate);
		if (retryOfTaskFailInfoDb != null) {
			// 存在则更新
			retryOfTaskFailInfoDb.setStatus(status);
			retryOfTaskFailInfoDb.setLastModifiedById(user.getId());
			retryOfTaskFailInfoDao.save(retryOfTaskFailInfoDb);
		} else {
			return ResponseResult.failure("HisToCenter重试失败任务不存在");
		}
		return ResponseResult.success("更新HisToCenter重试失败任务成功");
	}

	@Override
	public List<RetryOfHisToCenterTaskFailInfo> listOfFail() {
		int status=CommonEnum.Status.FAIL.getValue();
		return retryOfTaskFailInfoDao.findByStatusOrderByIdAsc(status);
	}

	
}

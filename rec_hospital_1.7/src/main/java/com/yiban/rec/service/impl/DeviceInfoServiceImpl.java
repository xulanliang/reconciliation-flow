package com.yiban.rec.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.AccountService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.dao.baseinfo.DeviceInfoDao;
import com.yiban.rec.domain.baseinfo.DeviceInfo;
import com.yiban.rec.service.ElectronicRecService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.SqlUtil;
import com.yiban.rec.util.StringUtil;

@Service
@Transactional(readOnly=true)
public class DeviceInfoServiceImpl extends  BaseOprService implements DeviceInfoService {

	@Autowired
	private DeviceInfoDao deviceInfoDao;
	@Autowired
	private AccountService accoutService;
	@Autowired
	private ElectronicRecService electronicRecService;
	
	@Override
	public DeviceInfo getDeviceInfoById(Long id) {
		return deviceInfoDao.findOne(id);
	}

	@Override
	@Transactional
	public ResponseResult save(DeviceInfo deviceInfo){
		try {
			User user=accoutService.getCurrentUser();
			deviceInfo.setIsActived(CommonEnum.IsActive.ISACTIVED.getValue());
			deviceInfo.setLastModifiedById(user.getId());
			deviceInfo.setCreatedById(user.getId());
			deviceInfoDao.save(deviceInfo);
		} catch (Exception e) {
			logger.error("保存设备信息异常,"+e.getMessage());
			return ResponseResult.failure("保存设备信息异常,"+e.getMessage());
		}
		return ResponseResult.success("保存设备信息成功");
	}

	@Override
	@Transactional
	public ResponseResult delete(Long id) {
		try {
			deviceInfoDao.delete(id);
			//删除商户-设备表
//			deviceShopRDao.deleteByDeviceId(id);
		} catch (Exception e) {
			logger.error("删除设备信息异常，"+e.getMessage());
			return ResponseResult.failure("删除设备信息异常，"+e.getMessage());
		}
		return ResponseResult.success("删除设备信息成功");
	}

	@Override
	@Transactional
	public ResponseResult update(DeviceInfo deviceInfo) {
		try {
			User user=accoutService.getCurrentUser();
			Long id=deviceInfo.getId();
			if(!StringUtil.isNullOrEmpty(id)){
				DeviceInfo deviceInfoDb=deviceInfoDao.findOne(id);
				if(deviceInfoDb!=null){
					//存在则更新
					deviceInfoDb.setDeviceNo(deviceInfo.getDeviceNo());
					deviceInfoDb.setDeviceSn(deviceInfo.getDeviceSn());
					deviceInfoDb.setDeviceMackey(deviceInfo.getDeviceMackey());
					deviceInfoDb.setOrgNo(deviceInfo.getOrgNo());
					deviceInfoDb.setDeviceArea(deviceInfo.getDeviceArea());
					deviceInfoDb.setRemarkInfo(deviceInfo.getRemarkInfo());
					deviceInfoDb.setLastModifiedById(user.getId());
					deviceInfoDao.save(deviceInfoDb);
					return ResponseResult.success("更新设备信息成功");
				}
			}
			//新增
			deviceInfoDao.save(deviceInfo);
		} catch (Exception e) {
			logger.error("更新设备信息异常,"+e.getMessage());
			return ResponseResult.failure("更新设备信息异常,"+e.getMessage());
		}
		return ResponseResult.success("更新设备信息成功");
	}
	@Override
	public Page<Map<String,Object>> getDeviceInfoList(PageRequest pagerequest,String deviceNo,String orgNo) {
		String orgCodes = electronicRecService.concatOrgNoSql(orgNo);
		StringBuffer sb=new StringBuffer();
		sb.append("SELECT d.id,d.org_no orgNo,d.device_no deviceNo,d.device_sn deviceSn,d.device_area deviceArea,d.device_mackey deviceMackey,"
				 +"d.created_by_id createdById,d.last_modified_by_id lastModifiedById,d.remark_info remarkInfo,org.name orgName,d.last_modified_date lastModifiedDate,d.created_date createdDate,u.name modifiedName"
				 +" FROM t_device d left join t_organization org on d.org_no=org.code"
				 +" LEFT JOIN t_user u ON d.last_modified_by_id=u.id"
				);
		sb.append(" WHERE d.is_deleted=0 AND d.is_actived=1");
		if(!StringUtil.isEmpty(deviceNo))
			sb.append(" AND d.device_no LIKE '").append("%"+deviceNo+"%").append("'");
		if(!StringUtil.isEmpty(orgNo))
			sb.append(" AND d.org_no in (").append(orgCodes).append(")");
		Page<Map<String,Object>> page = super.handleNativeSql(sb.toString(),pagerequest,new String[]
				{"id","orgNo","deviceNo","deviceSn","deviceArea","deviceMackey","createdById","lastModifiedById","remarkInfo","orgName","lastModifiedDate","createdDate","modifiedName"});
		return page;
	}
	@Override
	public List<Map<String, Object>> getDeviceInfoByDeviceNos(String[] deviceNos) {
		
		StringBuffer sb = new StringBuffer("SELECT dev.id deviceId,dev.device_no deviceNo FROM t_device dev WHERE 1=1 ");
		if(!StringUtil.isNullOrEmpty(deviceNos)){
			sb.append(" AND dev.device_no in "+SqlUtil.getSetInConditionStr(deviceNos));
		}
		List<Map<String, Object>> list = super.handleNativeSql(sb.toString(), new String[] { "deviceId", "deviceNo"});

		return list;
	}
	
	@Override
	public List<DeviceInfo> findByOrgNo(String orgNo) {
		return deviceInfoDao.findByOrgNo(orgNo);
	}
	@Override
	public List<Map<String, Object>> getDeviceInfoByDeviceId(String[] deviceNos) {
		
		StringBuffer sb = new StringBuffer("SELECT dev.id deviceId,dev.device_no deviceNo FROM t_device dev WHERE 1=1 ");
		if(!StringUtil.isNullOrEmpty(deviceNos)){
			sb.append(" AND dev.id in "+SqlUtil.getSetInConditionStr(deviceNos));
		}
		List<Map<String, Object>> list = super.handleNativeSql(sb.toString(), new String[] { "deviceId", "deviceNo"});
		return list;
	}
	@Override
	public DeviceInfo findDeviceInfoByDeviceNo(String deviceNo) {
		return deviceInfoDao.findDeviceInfoByDeviceNo(deviceNo);
	}

	@Override
	public List<DeviceInfo> findDeviceInfo() {
		return deviceInfoDao.findAll();
	}
	
}

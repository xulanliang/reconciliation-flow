package com.yiban.rec.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.AccountService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.dao.baseinfo.DeviceShopRDao;
import com.yiban.rec.dao.baseinfo.ShopInfoDao;
import com.yiban.rec.domain.baseinfo.DeviceShopR;
import com.yiban.rec.domain.baseinfo.ShopInfo;
import com.yiban.rec.service.ShopInfoService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.SqlUtil;
import com.yiban.rec.util.StringUtil;

@Service
@Transactional(readOnly = true)
public class ShopInfoServiceImpl extends BaseOprService implements ShopInfoService {

	@Autowired
	private ShopInfoDao shopInfoDao;
	@Autowired
	private AccountService accoutService;
	@Autowired
	private DeviceInfoService deviceInfoService;
	@Autowired
	private DeviceShopRDao deviceShopRDao;
	@Autowired
	private MetaDataService metaDataService;
	@Override
	public ShopInfo getShopInfoById(Long id) {

		return shopInfoDao.findOne(id);
	}

	@Override
	@Transactional
	public ResponseResult save(ShopInfo shopInfo) {
		try {
			String deviceNosStr=shopInfo.getDeviceNos();
			String[] deviceNos=deviceNosStr.split(",");
			User user = accoutService.getCurrentUser();
			shopInfo.setLastModifiedById(user.getId());
			shopInfo.setCreatedById(user.getId());
			shopInfo.setIsActived(CommonEnum.IsActive.ISACTIVED.getValue());
			shopInfoDao.save(shopInfo);
			
			//保存商户-设备表
			if(!StringUtil.isNullOrEmpty(deviceNos)){
				List<Map<String,Object>> simpleDeviceList=deviceInfoService.getDeviceInfoByDeviceId(deviceNos);
				List<DeviceShopR> deviceShopRs=new ArrayList<DeviceShopR>();
				for(Map<String,Object> simpleDevice:simpleDeviceList){
					DeviceShopR deviceShopR=new DeviceShopR();
					Long deviceId=MapUtils.getLong(simpleDevice, "deviceId");
					deviceShopR.setPayShopId(shopInfo.getId());
					deviceShopR.setDeviceId(deviceId);
					deviceShopR.setOrgNo(shopInfo.getOrgNo());
					deviceShopR.setUpdatedBy(user.getId());
					deviceShopR.setCreateTime(new Date());
					deviceShopR.setUpdatedTime(new Date());
					deviceShopRs.add(deviceShopR);
				}
				deviceShopRDao.save(deviceShopRs);
			}
			
		} catch (Exception e) {
			logger.error("保存商户信息异常," + e.getMessage());
			return ResponseResult.failure("保存商户信息异常," + e.getMessage());
		}
		return ResponseResult.success("保存商户信息成功");
	}

	@Override
	@Transactional
	public ResponseResult delete(Long id) {
		shopInfoDao.delete(id);
		
		//删除商户-设备关系表
		deviceShopRDao.deleteByPayShopId(id);
		return ResponseResult.success("删除商户信息成功");
	}

	@Override
	@Transactional
	public ResponseResult update(ShopInfo shopInfo) {
		try {
			User user = accoutService.getCurrentUser();
			Long id = shopInfo.getId();
			if (!StringUtil.isNullOrEmpty(id)) {
				ShopInfo shopInfoDb = shopInfoDao.findOne(id);
				if (shopInfoDb != null) {
					// 存在则更新
					shopInfoDb.setOrgNo(shopInfo.getOrgNo());
					shopInfoDb.setPayShopNo(shopInfo.getPayShopNo());
					shopInfoDb.setApplyId(shopInfo.getApplyId());
					shopInfoDb.setPayName(shopInfo.getPayName());
					shopInfoDb.setMetaDataPayId(shopInfo.getMetaDataPayId());
					shopInfoDb.setPinAlgorithm(shopInfo.getPinAlgorithm());
					shopInfoDb.setMacAlgorithm(shopInfo.getMacAlgorithm());
					shopInfoDb.setPayMackeyId(shopInfo.getPayMackeyId());
					shopInfoDb.setPayPinkeyId(shopInfo.getPayPinkeyId());
					shopInfoDb.setPayPkeyId(shopInfo.getPayPkeyId());
					shopInfoDb.setMetaDataBankId(shopInfo.getMetaDataBankId());
					shopInfoDb.setPayTpdu(shopInfo.getPayTpdu());
					shopInfoDb.setPayTermNo(shopInfo.getPayTermNo());
					shopInfoDb.setBussShortname(shopInfo.getBussShortname());
					shopInfoDb.setServiceUrl(shopInfo.getServiceUrl());
					shopInfoDb.setDescription(shopInfo.getDescription());
					shopInfoDb.setLastModifiedById(user.getId());
					shopInfoDb.setQrcodeTimeout(shopInfo.getQrcodeTimeout());
					shopInfoDb.setOrderTimeout(shopInfo.getOrderTimeout());
					shopInfoDb.setServiceAddress(shopInfo.getServiceAddress());
					shopInfoDb.setBillFilePath(shopInfo.getBillFilePath());
					shopInfoDb.setCompanyPid(shopInfo.getCompanyPid());
					shopInfoDb.setWxPayKey(shopInfo.getWxPayKey());
					shopInfoDb.setWxSslcertPassword(shopInfo.getWxSslcertPassword());
					shopInfoDao.save(shopInfoDb);
					
					//更新商户-设备表
					String deviceNosStr=shopInfo.getDeviceNos();
					String[] deviceNos=deviceNosStr.split(",");
					//先删除商户-设备表 后增加
					deviceShopRDao.deleteByPayShopId(id);
					if(!StringUtil.isNullOrEmpty(deviceNos)){
						List<Map<String,Object>> simpleDeviceList=deviceInfoService.getDeviceInfoByDeviceId(deviceNos);
						List<DeviceShopR> deviceShopRs=new ArrayList<DeviceShopR>();
						for(Map<String,Object> simpleDevice:simpleDeviceList){
							DeviceShopR deviceShopR=new DeviceShopR();
							Long deviceId=MapUtils.getLong(simpleDevice, "deviceId");
							deviceShopR.setPayShopId(shopInfo.getId());
							deviceShopR.setDeviceId(deviceId);
							deviceShopR.setOrgNo(shopInfo.getOrgNo());
							deviceShopR.setUpdatedBy(user.getId());
							deviceShopR.setUpdatedTime(new Date());
							deviceShopRs.add(deviceShopR);
						}
						deviceShopRDao.save(deviceShopRs);
					}
					return ResponseResult.success("更新商户信息成功");
				}
			}
			return ResponseResult.failure("商户信息不存在");
		} catch (Exception e) {
			logger.error("更新商户信息失败,"+e.getMessage());
			return ResponseResult.failure("更新商户信息失败,"+e.getMessage());
		}
		
	}

	@Override
	public Page<Map<String, Object>> getShopInfoList(PageRequest pagerequest, String orgNo) {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"SELECT s.id,s.pay_name payName,s.pay_shop_no payShopNo,s.org_no orgNo,s.apply_id applyId,s.description,s.buss_shortname bussShortname,s.service_url serviceUrl,s.pin_algorithm pinAlgorithm,"
						+ "s.pay_tpdu payTpdu,s.mac_algorithm macAlgorithm,s.pay_pinkey_id payPinkeyId,s.pay_mackey_id payMackeyId,s.pay_pkey_id payPkeyId, "
						+ "s.pay_term_no payTermNo,s.created_by_id createdById,s.last_modified_by_id lastModifiedById,"
						+ "s.meta_data_pay_id metaDataPayId,s.meta_data_bank_id metaDataBankId,s.qrcode_timeout qrcodeTimeout,"
						+ "s.order_timeout orderTimeout,s.service_address serviceAddress,s.bill_file_path billFilePath,"
						+ "s.company_pid companyPid,s.wx_pay_key wxPayKey,s.wx_sslcert_password wxSslcertPassword,s.last_modified_date "
						+ "lastModifiedDate,s.created_date createdDate,org.name orgName,meta.value"
						+ " FROM t_shop_info s LEFT JOIN t_organization org ON s.org_no = org. id"
						+ " LEFT JOIN t_meta_data meta ON s.meta_data_pay_id=meta.id");
		sb.append(" WHERE s.is_deleted=0 AND s.is_actived=1");

		// System.out.println(orgNo);
		if (!StringUtil.isEmpty(orgNo)) {
			sb.append(" AND s.org_no ='").append(orgNo).append("'");
		}else{
			sb.append(" ORDER BY s.id desc");
		}
		// System.out.println(sb.toString());
		Page<Map<String, Object>> page = super.handleNativeSql(sb.toString(), pagerequest,
				new String[] { "id", "payName", "payShopNo", "orgNo", "applyId", "description", "bussShortname",
						"serviceUrl", "pinAlgorithm", "payTpdu", "macAlgorithm", "payPinkeyId", "payMackeyId",
						"payPkeyId", "payTermNo", "createdById", "lastModifiedById", "metaDataPayId","metaDataBankId",
						"qrcodeTimeout","orderTimeout","serviceAddress","billFilePath","companyPid","wxPayKey","wxSslcertPassword",
						"lastModifiedDate", "createdDate", "orgName", "value" });
		List<Map<String,Object>> maps=page.getContent();
		Set<Long> shopIds=new HashSet<Long>();
		if(!StringUtil.isNullOrEmpty(maps)){
			for(Map<String,Object> map:maps){
				shopIds.add(MapUtils.getLong(map, "id"));
			}
		}
		StringBuffer sbR = new StringBuffer();
		sbR.append(
				"SELECT s.id shopId,d.id deviceId,d.device_no deviceNo "+
				" FROM t_shop_info s INNER JOIN t_device_shop_r r ON s.id=r.pay_shop_id"+
				" INNER JOIN t_device d ON r.device_id=d.id");
		if(!StringUtil.isNullOrEmpty(shopIds)){
			sbR.append(" WHERE s.id in "+SqlUtil.getSetInConditionInt(shopIds));
		}
		
		List<Map<String, Object>> list = super.handleNativeSql(sbR.toString(), new String[] { "shopId","deviceId", "deviceNo"});
		if(!StringUtil.isNullOrEmpty(maps)){
			for(Map<String,Object> map:maps){
				Long shopId=MapUtils.getLong(map, "id");
				Set<String> deviceNos=new HashSet<String>();
				for(Map<String,Object> mapR:list){
					if(shopId.equals(MapUtils.getLong(mapR, "shopId"))){
						deviceNos.add(MapUtils.getString(mapR, "deviceNo"));
					}
				}
				map.put("deviceNos", SqlUtil.getSetInConditionStrCom(deviceNos));
			}
		}
		
		return page;
	}

	protected List<Predicate> converSearch(List<Predicate> predicates, String orgNo, Root<ShopInfo> root,
			CriteriaQuery<?> query, CriteriaBuilder cb) {
		if (orgNo != null && !"".equals(orgNo)) {

			Path<Long> orgNoExp = root.get("orgNo");
			// Path<Organization> orgNoExp =
			// root.get("organization").get("code");
			predicates.add(cb.equal(orgNoExp, orgNo));
		}
		return predicates;
	}

	@Override
	public ShopInfo findShopInfoByOrgNoAndApplyIdAndPayShopNo(String orgNo, String applyId, String payShopNo) {
		return shopInfoDao.findShopInfoByOrgNoAndApplyIdAndPayShopNo(orgNo,applyId,payShopNo);
	}

	@Override
	public List<ShopInfo> getAllShopInfo() {
		Map<String,String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());
		List<ShopInfo> list = shopInfoDao.findAll();
		if(!StringUtil.isNullOrEmpty(list)){
			for(ShopInfo shopInfo : list){
				shopInfo.setMetaDataBankName(metaMap.get(String.valueOf(shopInfo.getMetaDataBankId())));
			}
		}
		return list;
	}

	@Override
	public ShopInfo getApplyIdByPayShopNo(String payShopNo) {
		return shopInfoDao.findShopInfoByPayShopNo(payShopNo);
	}

	@Override
	public List<ShopInfo> getShopInfoByOrgNo(String orgNo,String payName) {
		
		Specification<ShopInfo> specification = new Specification<ShopInfo>() {
			@Override
			public Predicate toPredicate(Root<ShopInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearch(orgNo,payName,root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return shopInfoDao.findAll(specification);
	}
	
	protected List<Predicate> converSearch(String orgNo,String payName,Root<?> root,
			CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();
		if (!StringUtils.isEmpty(orgNo)) {
			Path<String> orgNoExp = root.get("orgNo");
			predicates.add(cb.equal(orgNoExp, orgNo));
		}
		if (!StringUtils.isEmpty(payName)) {
			Path<String> payNameExp = root.get("payName");
			predicates.add(cb.like(payNameExp, "%"+payName+"%"));
		}
		return predicates;
	}

	@Override
	public Map<String, String> getTerm2PayTerm() {
		List<DeviceShopR> list = deviceShopRDao.findAll();
		Map<String,String> map = new HashMap<String,String>();
		if(!StringUtil.isNullOrEmpty(list)){
			for(DeviceShopR deviceShopR : list){
				if(!StringUtil.isNullOrEmpty(deviceShopR.getDeviceInfo())){
					map.put(deviceShopR.getDeviceInfo().getDeviceNo(), deviceShopR.getShopInfo().getPayTermNo());
				}
			}
		}
		
		return map;
	}
	
}

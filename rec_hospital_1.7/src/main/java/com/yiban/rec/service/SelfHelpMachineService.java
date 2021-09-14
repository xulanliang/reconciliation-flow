package com.yiban.rec.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;

import com.yiban.rec.domain.OrderSettlement;

/**
 * @author swing
 * @date 2018年8月8日 下午5:08:49 类说明 自助机结算接口
 */
public interface SelfHelpMachineService {
	
	/**
	 * 查询明细
	 * @param query
	 * @return
	 */
	List<Map<String, Object>> findAllList(SelfHelpMachineService.SelfHelpMachineQuery query);
	
	


	
	/**
	 * 汇总已结算的数据
	 * @param query
	 * @return
	 */
	Map<String, Object> countCheckOutAll(SelfHelpMachineService.SelfHelpMachineQuery query);

	/**
	 * 保存结账记录
	 * 
	 * @param orderSettlement
	 */
	void saveOrderSettlement(OrderSettlement orderSettlement);
	
	/**
	 * 标记已结算
	 * @param ids
	 */
	void updateCheckOutStat(List<Long> ids);
	void settlemnt(SelfHelpMachineService.SelfHelpMachineQuery query);

	/**
	 * 根据数据模型导出excel(定制化，只适合本需求，不通用其他需求)
	 * 
	 * @param list
	 * @return
	 */
	Workbook generateExcel(List<DataModel> list);

	class DataModel {
		private String name;
		private Map<String, Object> mzModel;
		private Map<String, Object> zyModel;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Map<String, Object> getMzModel() {
			return mzModel;
		}

		public void setMzModel(Map<String, Object> mzModel) {
			this.mzModel = mzModel;
		}

		public Map<String, Object> getZyModel() {
			return zyModel;
		}

		public void setZyModel(Map<String, Object> zyModel) {
			this.zyModel = zyModel;
		}
	}

	class SelfHelpMachineQuery {
		private String orgCode;
		private String deviceNo;
		private String[] deviceNos;
		private String beginTime;
		private String endTime;
		private String patType;
		private String orgName;
		// 0未结算,1已结算
		private int checkOut;

		public String getPatType() {
			return patType;
		}

		public void setPatType(String patType) {
			this.patType = patType;
		}

		public String getOrgCode() {
			return orgCode;
		}

		public void setOrgCode(String orgCode) {
			this.orgCode = orgCode;
		}

		

	

		public String getOrgName() {
			return orgName;
		}

		public void setOrgName(String orgName) {
			this.orgName = orgName;
		}

		public int getCheckOut() {
			return checkOut;
		}

		public void setCheckOut(int checkOut) {
			this.checkOut = checkOut;
		}

		public String getDeviceNo() {
			return deviceNo;
		}

		public void setDeviceNo(String deviceNo) {
			this.deviceNo = deviceNo;
		}

		public String[] getDeviceNos() {
			if(deviceNos == null){
				return null;
			}
			Set<String> idSet =new HashSet<>(deviceNos.length);
			for(String id:deviceNos){
				idSet.add(id);
			}
			return idSet.toArray(new String[0]);
		}

		public void setDeviceNos(String[] deviceNos) {
			this.deviceNos = deviceNos;
		}

		public String getBeginTime() {
			return beginTime;
		}

		public void setBeginTime(String beginTime) {
			this.beginTime = beginTime;
		}

		public String getEndTime() {
			return endTime;
		}

		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
	}
}

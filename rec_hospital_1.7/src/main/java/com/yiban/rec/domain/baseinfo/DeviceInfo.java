package com.yiban.rec.domain.baseinfo;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.yiban.rec.domain.BaseEntityEx;

@Entity
@Table(name = DeviceInfo.TABLE_NAME)
public class DeviceInfo extends BaseEntityEx {

	private static final long serialVersionUID = -1442129913871350291L;

	public static final String TABLE_NAME = "t_device";

	/**
	 * 机构编码
	 */
	private String orgNo;
	/**
	 * 设备编号
	 */
	private String deviceNo;
	/**
	 * 设备系列号
	 */
	private String deviceSn;

	/**
	 * 备注新信息
	 */
	private String remarkInfo;
	/**
	 * 设备所在区域
	 */
	private String deviceArea;
	/**
	 * 设备传输密钥
	 */
	private String deviceMackey;

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}

	public String getDeviceSn() {
		return deviceSn;
	}

	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}

	public String getRemarkInfo() {
		return remarkInfo;
	}

	public void setRemarkInfo(String remarkInfo) {
		this.remarkInfo = remarkInfo;
	}

	public String getDeviceArea() {
		return deviceArea;
	}

	public void setDeviceArea(String deviceArea) {
		this.deviceArea = deviceArea;
	}

	public String getDeviceMackey() {
		return deviceMackey;
	}

	public void setDeviceMackey(String deviceMackey) {
		this.deviceMackey = deviceMackey;
	}

}

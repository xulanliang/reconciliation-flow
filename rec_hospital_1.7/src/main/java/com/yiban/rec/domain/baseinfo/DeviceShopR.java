package com.yiban.rec.domain.baseinfo;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.yiban.framework.core.domain.base.IdEntity;
@Entity
@Table(name = DeviceShopR.TABLE_NAME)
public class DeviceShopR extends IdEntity{
	
	private static final long serialVersionUID = -946181271884042180L;
	public static final String TABLE_NAME = "t_device_shop_r";
	// 机构编码
	private String orgNo;
	// 设备ID
	private Long deviceId;
	
	@OneToOne
	@JoinColumn(name="deviceId",referencedColumnName="id",insertable=false,updatable=false)
	private DeviceInfo deviceInfo;
	
	// 支付商户ID
	private Long payShopId;
	
	@OneToOne
	@JoinColumn(name="payShopId",referencedColumnName="id",insertable=false,updatable=false)
	private ShopInfo shopInfo;
	//更新者ID
	private Long updatedBy;
	private Date updatedTime;
	private Date createTime;
	public String getOrgNo() {
		return orgNo;
	}
	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}
	public Long getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}
	public Long getPayShopId() {
		return payShopId;
	}
	public void setPayShopId(Long payShopId) {
		this.payShopId = payShopId;
	}
	public Long getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}
	public Date getUpdatedTime() {
		return updatedTime;
	}
	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(DeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	public ShopInfo getShopInfo() {
		return shopInfo;
	}
	public void setShopInfo(ShopInfo shopInfo) {
		this.shopInfo = shopInfo;
	}
	
}

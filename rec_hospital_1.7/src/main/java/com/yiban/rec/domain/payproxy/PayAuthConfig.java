package com.yiban.rec.domain.payproxy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;

import com.yiban.framework.core.domain.base.IdEntity;
import com.yiban.rec.util.PayWayEnum;

/**
 * @author swing
 * @date 2018年7月5日 上午9:41:55 类说明
 */
@Entity
@Table(name = "t_auth_config")
public class PayAuthConfig extends IdEntity {
	private static final long serialVersionUID = 1L;
	@NotNull
	private String apiKey;
	@NotNull
	private String clientName;
	@Transient
	private String apiLabel;
	private Date createTime=new Date();
	private Date updateTime=new Date();

	/**
	 * api名称
	 */
	@NotNull
	private String apiName;
	private String resouce;
	@Transient
	private String resourceLabel;

	/**
	 * 1:启用,0:禁用
	 */
	private int state;

	public Date getCreateTime() {
		return createTime;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getResourceLabel() {
		if (StringUtils.isNotEmpty(resouce)) {
			String[] payCodeArr = resouce.split(",");
			List<String> payCodeLabelList = new ArrayList<>();
			for (String payCode : payCodeArr) {
				PayWayEnum payEnum = PayWayEnum.getByCode(payCode);
				if (payEnum != null) {
					payCodeLabelList.add(payEnum.getPayType());
				} else {
					payCodeLabelList.add("未知");
				}
			}
			return StringUtils.join(payCodeLabelList, ",");
		}
		return "";
	}

	public void setResourceLabel(String resourceLabel) {
		this.resourceLabel = resourceLabel;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public String getResouce() {
		return resouce;
	}

	public void setResouce(String resouce) {
		this.resouce = resouce;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getApiLabel() {
		if (StringUtils.isNotEmpty(apiName)) {
			String[] apiArr = apiName.split(",");
			List<String> payCodeLabelList = new ArrayList<>();
			for (String api : apiArr) {
				String apiLabel = PayApiNameEnum.getApiLabel(api);
				if (apiLabel != null) {
					payCodeLabelList.add(apiLabel);
				} else {
					payCodeLabelList.add("未知");
				}
			}
			return StringUtils.join(payCodeLabelList, ",");
		}
		return "";

	}

	public void setApiLabel(String apiLabel) {
		this.apiLabel = apiLabel;
	}

}

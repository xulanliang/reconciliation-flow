package com.yiban.rec.bill.parse.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.yiban.framework.core.domain.base.IdEntity;

/**
 * @author swing
 * @date 2018年8月3日 下午1:44:25 类说明
 */
@Entity
@Table(name = "t_parse_task_cfg")
public class ParseTaskConfig extends IdEntity {
	private static final long serialVersionUID = 1L;
	private String name;
	private String target;
	private String param;
	private int active = 1;
	private String payType;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
}

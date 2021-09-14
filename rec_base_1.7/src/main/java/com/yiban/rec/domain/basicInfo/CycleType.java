package com.yiban.rec.domain.basicInfo;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.yiban.framework.core.domain.base.IdEntity;

/**
 * 
* @ClassName: CycleType 
* @Description: 周期类型
* @author tuchun@clearofchina.com 
* @date 2017年3月29日 下午5:11:28 
* @version V1.0 
*
 */
@Entity
@Table(name = CycleType.TABLE_NAME)
public class CycleType extends IdEntity {

	private static final long serialVersionUID = -646226909057984592L;

	public static final String TABLE_NAME = "t_cycle_type";

	private Integer cKey;
	
	private String cValue;
	/**
	 * 类型 1、每隔N类型 2、小时数 3、分钟数 4、秒数
	 */
	private Integer cType;
	public Integer getcKey() {
		return cKey;
	}
	public void setcKey(Integer cKey) {
		this.cKey = cKey;
	}
	public String getcValue() {
		return cValue;
	}
	public void setcValue(String cValue) {
		this.cValue = cValue;
	}
	public Integer getcType() {
		return cType;
	}
	public void setcType(Integer cType) {
		this.cType = cType;
	}
	
}

package com.yiban.rec.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.yiban.framework.core.domain.base.IdEntity;

/**
 * 医院配置
 * 
 * @author huangguojie
 *
 */
@Entity
@Table(name = "t_hospital_configuration")
public class HospitalConfiguration extends IdEntity {
	private static final long serialVersionUID = 6213898765103883944L;

	private String keyWord;
	private String keyValue;
	// 1:激活,0禁用
	private int active=1;



	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

}

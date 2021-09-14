package com.yiban.rec.bill.parse.vo;

import org.apache.commons.lang.StringUtils;

/**
 * payCenter账单扩展字段vo
 * 
 */
public class ExtraParamVo {

	private String cardType;
	// 新版本
	private String bsName;
	private String bsCardNo;

	// 老版本字段命名
	private String realName;
	private String cardno;
	private String cardNo;

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getBsName() {
		if (StringUtils.isNotBlank(this.bsName)) {
			return this.bsName;
		} else {
			return this.realName;
		}
	}

	public void setBsName(String bsName) {
		this.bsName = bsName;
	}

	public String getBsCardNo() {
		if (StringUtils.isNotBlank(this.bsCardNo)) {
			return this.bsCardNo;
		} else if (StringUtils.isNotBlank(this.cardno)) {
			return this.cardno;
		} else {
			return this.cardNo;
		}
	}

	public void setBsCardNo(String bsCardNo) {
		this.bsCardNo = bsCardNo;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public void setCardno(String cardno) {
		this.cardno = cardno;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	@Override
	public String toString() {
		return "ExtraParamVo [getCardType()=" + getCardType() + ", getBsName()=" + getBsName() + ", getBsCardNo()="
				+ getBsCardNo() + "]";
	}
}

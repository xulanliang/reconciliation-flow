package com.yiban.rec.domain.vo;

import java.math.BigDecimal;

public class SZYBBill {
	private String 	akc190;//门诊流水号（住院流水号）
	private String bke384;//医药机构结算业务序列号
	private String ckc618;//结算业务号
	private String aka130;//交易渠道
	private String aaz500;//社保卡号（医疗证号）
	private String alc005;	//工伤事故编号
	private String aac003;	//姓名
	private String aka018;	//结算状态
	private BigDecimal akc264=new BigDecimal(0);	//医疗费总额 
	private BigDecimal akb068=new BigDecimal(0);	//基金支付金额
	private BigDecimal akb066=new BigDecimal(0);	//个人账户支付金额
	private BigDecimal akb067=new BigDecimal(0);	//个人支付金额
	private String cke555;//交易渠道
	private String aae011;//操作员编码
	public String getCke555() {
		return cke555;
	}
	public void setCke555(String cke555) {
		this.cke555 = cke555;
	}
	public String getAae011() {
		return aae011;
	}
	public void setAae011(String aae011) {
		this.aae011 = aae011;
	}
	public String getAkc190() {
		return akc190;
	}
	public void setAkc190(String akc190) {
		this.akc190 = akc190;
	}
	public String getBke384() {
		return bke384;
	}
	public void setBke384(String bke384) {
		this.bke384 = bke384;
	}
	public String getCkc618() {
		return ckc618;
	}
	public void setCkc618(String ckc618) {
		this.ckc618 = ckc618;
	}
	public String getAka130() {
		return aka130;
	}
	public void setAka130(String aka130) {
		this.aka130 = aka130;
	}
	public String getAaz500() {
		return aaz500;
	}
	public void setAaz500(String aaz500) {
		this.aaz500 = aaz500;
	}
	public String getAlc005() {
		return alc005;
	}
	public void setAlc005(String alc005) {
		this.alc005 = alc005;
	}
	public String getAac003() {
		return aac003;
	}
	public void setAac003(String aac003) {
		this.aac003 = aac003;
	}
	public String getAka018() {
		return aka018;
	}
	public void setAka018(String aka018) {
		this.aka018 = aka018;
	}
	public BigDecimal getAkc264() {
		return akc264;
	}
	public void setAkc264(BigDecimal akc264) {
		this.akc264 = akc264;
	}
	public BigDecimal getAkb068() {
		return akb068;
	}
	public void setAkb068(BigDecimal akb068) {
		this.akb068 = akb068;
	}
	public BigDecimal getAkb066() {
		return akb066;
	}
	public void setAkb066(BigDecimal akb066) {
		this.akb066 = akb066;
	}
	public BigDecimal getAkb067() {
		return akb067;
	}
	public void setAkb067(BigDecimal akb067) {
		this.akb067 = akb067;
	}

	
}

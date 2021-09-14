package com.yiban.rec.domain.vo;

import java.math.BigDecimal;

public class SZSRHisZyVo {
	//流水号
	private String JZDJH;
	private BigDecimal GRZFJE=new BigDecimal(0);//自费金额
    private BigDecimal GRZHZFJE=new BigDecimal(0);//个人账户结算金额
    private BigDecimal JJZFJE=new BigDecimal(0);//统筹基金结算金额
    private BigDecimal YLFZE=new BigDecimal(0);//总金额
    private String JSRQ;
    
	public String getJSRQ() {
		return JSRQ;
	}
	public void setJSRQ(String jSRQ) {
		JSRQ = jSRQ;
	}
	public String getJZDJH() {
		return JZDJH;
	}
	public void setJZDJH(String jZDJH) {
		JZDJH = jZDJH;
	}
	public BigDecimal getGRZFJE() {
		return GRZFJE;
	}
	public void setGRZFJE(BigDecimal gRZFJE) {
		GRZFJE = gRZFJE;
	}
	public BigDecimal getGRZHZFJE() {
		return GRZHZFJE;
	}
	public void setGRZHZFJE(BigDecimal gRZHZFJE) {
		GRZHZFJE = gRZHZFJE;
	}
	public BigDecimal getJJZFJE() {
		return JJZFJE;
	}
	public void setJJZFJE(BigDecimal jJZFJE) {
		JJZFJE = jJZFJE;
	}
	public BigDecimal getYLFZE() {
		return YLFZE;
	}
	public void setYLFZE(BigDecimal yLFZE) {
		YLFZE = yLFZE;
	}
    
}

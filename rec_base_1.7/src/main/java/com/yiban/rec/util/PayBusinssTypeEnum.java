package com.yiban.rec.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public enum PayBusinssTypeEnum {
	REGISTRATION("0451", "挂号")
	, RECHARGE("0151", "门诊")
	, ORDER("0851", "预约支付"),
	ZYYJ("0751","住院押金")
	;
	private String code;
    private String name;
    
	private PayBusinssTypeEnum() {
	}
	private PayBusinssTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public static PayBusinssTypeEnum getByCode(String code) {
        if(StringUtils.isBlank(code)){
            return null;
        }
        for(PayBusinssTypeEnum tmp: PayBusinssTypeEnum.values()) {
            if(tmp.code.equals(code)) {
                return tmp;
            }
        }
        return null;
    }
	
	public static List<String> getAllCode() {
		List<String> array = new ArrayList<>();
		for(PayBusinssTypeEnum tmp: PayBusinssTypeEnum.values()) {
			array.add(tmp.code);
        }
		return array;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    
}

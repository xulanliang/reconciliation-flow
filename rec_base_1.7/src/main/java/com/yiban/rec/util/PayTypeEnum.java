package com.yiban.rec.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public enum PayTypeEnum {
	WECHAT("0249", "微信"), ALIPAY("0349", "支付宝"), BANK("0149", "银行卡"), CASH("0049", "现金");
	private String code;
    private String name;
    
	private PayTypeEnum() {
	}
	private PayTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public static PayTypeEnum getByCode(String code) {
        if(StringUtils.isBlank(code)){
            return null;
        }
        for(PayTypeEnum tmp: PayTypeEnum.values()) {
            if(tmp.code.equals(code)) {
                return tmp;
            }
        }
        return null;
    }
	
	public static List<String> getAllCode() {
		List<String> array = new ArrayList<>();
		for(PayTypeEnum tmp: PayTypeEnum.values()) {
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

package com.yiban.rec.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public enum TangDuPayTypeEnum {
	WECHAT("0249", "98"), ALIPAY("0349", "99"), BANK("0149", "银行"), CASH("0049", "现金");
	private String code;
    private String name;
    
	private TangDuPayTypeEnum() {
	}
	private TangDuPayTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public static TangDuPayTypeEnum getByCode(String code) {
        if(StringUtils.isBlank(code)){
            return null;
        }
        for(TangDuPayTypeEnum tmp: TangDuPayTypeEnum.values()) {
            if(tmp.code.equals(code)) {
                return tmp;
            }
        }
        return null;
    }
	
	public static List<String> getAllCode() {
		List<String> array = new ArrayList<>();
		for(TangDuPayTypeEnum tmp: TangDuPayTypeEnum.values()) {
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

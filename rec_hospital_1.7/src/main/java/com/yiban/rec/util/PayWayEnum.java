package com.yiban.rec.util;

/**
 * 支付类型编号枚举类
 * @author WY
 * @date 2017-06-29
 * @说明：type:01微信支付类型，02：支付宝，其他待定
 */
public enum PayWayEnum {
    
	PAY_WECHAT_SCAN_CODE(1, "151", "01", "微信扫码支付"),
	PAY_ALIPAY_SCAN_CODE(2, "152", "02", "支付宝扫码支付"),
	PAY_WECHAT_WEB(3, "153", "01", "微信公众号支付"),
	PAY_ALIPAY_H5(4, "154", "02", "支付宝H5支付"),
	PAY_WECHAT_APP(5, "155","01", "微信APP支付"),
	PAY_ALIPAY_APP(6, "156", "02", "支付宝APP支付"),
	PAY_WECHAT_BAR_CODE(7, "157", "01", "微信条码支付"),
	PAY_ALIPAY_BAR_CODE(8, "158", "02", "支付宝条码支付"),
	PAY_WECHAT_H5(3, "159", "01", "微信H5支付"),
	PAY_ALIPAY_FACE(10, "1510", "02", "支付宝刷脸支付"),
	PAY_WECHAT_MINIPROGRAM(11, "1511", "01", "微信小程序支付"),
	;
    private Integer payId;
    private String code;
    private String type;
    private String payType;
    
    /**
     * 支付宝支付类型
     * @param code
     * @return
     */
    public static boolean isAliPayType(String code) {
        if("02".equals(getPayWayType(code))) {
            return true;
        }
        return false;
    }
    
    /**
     * 微信支付类型
     * @param code
     * @return
     */
    public static boolean isWechatPayType(String code) {
        if("01".equals(getPayWayType(code))) {
            return true;
        }
        return false;
    }
    
    /**
     * 获取支付类型：01-微信，02-支付宝
     * @param code
     * @return
     */
    public static String getPayWayType(String code) {
        String payType = null;
        PayWayEnum e = getByCode(code);
        if(null != e) {
            payType = e.getType();
        }
        return payType;
    }
    
    /**
     * 获取支付类型：01-微信，02-支付宝
     * @param code
     * @return
     */
    public static String getPayWayTypeForCode(String code) {
        String payType = null;
        PayWayEnum e = getByCode(code);
        if(null != e) {
            payType = e.getPayType();
        }
        return payType;
    }
    
    
    public static PayWayEnum getByCode(String code) {
        PayWayEnum payWayEnum = null;
        if(StringUtil.isEmpty(code)){
            return payWayEnum;
        }
        for(PayWayEnum e: PayWayEnum.values()) {
            if(code.equals(e.getCode())) {
                payWayEnum = e;
                break;
            }
        }
        return payWayEnum;
    }

    public static PayWayEnum getById(Integer payId) {
        if(null==payId){
            return null;
        }
        for(PayWayEnum tmp: PayWayEnum.values()) {
            if(tmp.payId.intValue() == payId.intValue()) {
                return tmp;
            }
        }
        return null;
    }
    
    /**
     * 查询编号是否合法
     * @param code
     * @return
     */
    public static boolean containsCode(String code) {
        boolean flag = false;
        if(StringUtil.isEmpty(code)) {
            return flag;
        }
        for(PayWayEnum payway: PayWayEnum.values()) {
            if(code.equals(payway.getCode())) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    PayWayEnum(Integer payId, String code, String type, String payType) {
        this.payId = payId;
        this.code = code;
        this.type = type;
        this.payType = payType;
    }

    public Integer getPayId() {
        return payId;
    }

    public void setPayId(Integer payId) {
        this.payId = payId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
}

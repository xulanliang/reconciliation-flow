package com.yiban.rec.util;

public enum WeichatTradeStateEnum {
	
	
	TRADE_STATE_SUCCESS("SUCCESS","支付成功"),
	TRADE_STATE_REFUND("REFUND","转入退款"),
	TRADE_STATE_NOTPAY("NOTPAY","未支付"),
	TRADE_STATE_CLOSED("CLOSED","已关闭"),
	TRADE_STATE_REVOKED("REVOKED","已撤销(刷卡支付)"),
	TRADE_STATE_USERPAYING("USERPAYING","用户支付中"),
	TRADE_STATE_PAYERROR("PAYERROR","支付失败(其他原因，如银行返回失败)"),
    ;


    private String code;

    private String payType;


    WeichatTradeStateEnum(String code, String payType) {
        this.code = code;
        this.payType = payType;
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
}

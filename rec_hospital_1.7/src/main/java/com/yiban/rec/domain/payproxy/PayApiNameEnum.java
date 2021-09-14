package com.yiban.rec.domain.payproxy;

/**
 * @author swing
 * @date 2018年7月6日 上午11:39:16 类说明
 */
public enum PayApiNameEnum {
	ORDER("/order/unified","支付"),
	REFUND("/order/refund","退款"),
	CANCEL("/order/cancel","取消订单"),
	REFUND_QUERY("/order/refund/query","退款订单查询"),
	ORDER_QUERY("/order/query","订单查询");
	private String name;
	private String label;

	PayApiNameEnum(String name, String label) {
		this.name = name;
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
    public static String getApiLabel(String apiName){
    	PayApiNameEnum[] arr=PayApiNameEnum.values();
    	for(PayApiNameEnum apiEnum:arr){
    		if(apiEnum.getName().equals(apiName)){
    			return apiEnum.getLabel();
    		}
    	}
    	return null;
    }
   
}

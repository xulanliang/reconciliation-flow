package com.yiban.rec.util;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yiban.framework.core.domain.base.ValueTextable;
import com.yiban.framework.core.domain.base.ValueTexts;
import org.apache.commons.lang.StringUtils;

public class CommonEnum {
	/**
     * 注册商户来源
     * 
     */
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public static enum MerchantSource implements ValueTextable<Integer> {

        WEB(1, "管理后台"), WEIXIN(2, "微信"), APP(3, "手机APP");

        MerchantSource(Integer value, String text) {
            this.value = value;
            this.text = text;
        }

        private Integer value;
        private String text;
        private boolean selected;
        private String description;

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        private static Map<Integer, String> map = ValueTexts.asMap(newArrayList(MerchantSource.values()));

        public static Map<Integer, String> asMap() {
            return map;
        }
    }
    
    /**
     * 数据记录是否删除状态
     * 
     */
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public static enum DeleteStatus implements ValueTextable<Integer> {

        UNDELETE(0, "未删除"), DELETE(1, "已删除");

        DeleteStatus(Integer value, String text) {
            this.value = value;
            this.text = text;
        }

        private Integer value;
        private String text;
        private boolean selected;
        private String description;

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        private static Map<Integer, String> map = ValueTexts.asMap(newArrayList(DeleteStatus.values()));

        public static Map<Integer, String> asMap() {
            return map;
        }
    }
    
    /**
     * 性别
     * 
     */
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public static enum GenderStatus implements ValueTextable<Integer> {

        MALE(0, "男"), FEMALE(1, "女");

        GenderStatus(Integer value, String text) {
            this.value = value;
            this.text = text;
        }

        private Integer value;
        private String text;
        private boolean selected;
        private String description;

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        private static Map<Integer, String> map = ValueTexts.asMap(newArrayList(DeleteStatus.values()));

        public static Map<Integer, String> asMap() {
            return map;
        }
    }
    /**
     * 数据记录是否激活状态
     * 
     */
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public static enum IsActive implements ValueTextable<Integer> {

        NOTACTIVE(0, "未激活"), ISACTIVED(1, "已激活");

    	IsActive(Integer value, String text) {
            this.value = value;
            this.text = text;
        }

        private Integer value;
        private String text;
        private boolean selected;
        private String description;

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        private static Map<Integer, String> map = ValueTexts.asMap(newArrayList(IsActive.values()));

        public static Map<Integer, String> asMap() {
            return map;
        }
    }
    /**
     * 数据状态
     * 
     */
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public static enum Status implements ValueTextable<Integer> {

        SUCCESS(125, "成功"), FAIL(126, "失败"),CONFIRM(127,"确认");

    	Status(Integer value, String text) {
            this.value = value;
            this.text = text;
        }

        private Integer value;
        private String text;
        private boolean selected;
        private String description;

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        private static Map<Integer, String> map = ValueTexts.asMap(newArrayList(DeleteStatus.values()));

        public static Map<Integer, String> asMap() {
            return map;
        }
    }
    /**
     *  
	 * 账单类型  
     */
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public static enum BillType implements ValueTextable<Integer> {

        PLATFORM(128, "his账单信息"), HIS(129, "平台账单信息"),THIRD(130,"第三方账单");

    	BillType(Integer value, String text) {
            this.value = value;
            this.text = text;
        }

        private Integer value;
        private String text;
        private boolean selected;
        private String description;

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        private static Map<Integer, String> map = ValueTexts.asMap(newArrayList(DeleteStatus.values()));

        public static Map<Integer, String> asMap() {
            return map;
        }
    }
    
    /**
     *  
	 * 对账账平标志
     */
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public static enum BillBalance implements ValueTextable<Integer> {

        zp(0, "账平"),HANDLER(1, "处理后账平"), HISDC(2, "医院多出"),THIRDDC(3,"支付渠道多出"),PLATDC(4,"平台多出"),HEALTHCAREOFFI(5,"医保中心多出"),HEALTHCAREHIS(6,"医保his多出"),
        WAITEXAMINE(7, "待审核"), REJECT(8, "已驳回"), REFUND(9, "已退费"),RECOVER(10, "已追回"),NORECOVER(11, "电子对账预处理，没有实际页面代表意义");

    	BillBalance(Integer value, String text) {
            this.value = value;
            this.text = text;
        }

        private Integer value;
        private String text;
        private boolean selected;
        private String description;

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        private static Map<Integer, String> map = ValueTexts.asMap(newArrayList(BillBalance.values()));

        public static Map<Integer, String> asMap() {
            return map;
        }
    }

    /**
     *
	 * 窗口现金核对状态
     */
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public static enum CashCheckState implements ValueTextable<Integer> {

        NORMAL(0, "正常"), UNUSUAL(1, "异常"), ADOPT(2, "已通过") ;

        CashCheckState(Integer value, String text) {
            this.value = value;
            this.text = text;
        }

        private Integer value;
        private String text;
        private boolean selected;
        private String description;

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        private static Map<Integer, String> map = ValueTexts.asMap(newArrayList(BillBalance.values()));

        public static Map<Integer, String> asMap() {
            return map;
        }

        public static CashCheckState getByCode(String value) {
            if(StringUtils.isBlank(value)){
                return null;
            }
            for(CashCheckState tmp: CashCheckState.values()) {
                if(tmp.value.toString().equals(value)) {
                    return tmp;
                }
            }
            return null;
        }

    }
    
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public static enum ServiceState implements ValueTextable<Integer> {

        NORMAL(1, "正常"), WARNING(2, "预警"), DISCONNECT(3, "离线");

    	ServiceState(Integer value, String text) {
            this.value = value;
            this.text = text;
        }

        private Integer value;
        private String text;
        private boolean selected;
        private String description;

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        private static Map<Integer, String> map = ValueTexts.asMap(newArrayList(ServiceState.values()));

        public static Map<Integer, String> asMap() {
            return map;
        }
    }
}

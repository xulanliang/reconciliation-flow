package com.yiban.rec.domain;

import org.apache.commons.lang.StringUtils;

/**
 * 账单上送接口返回实体
 * @Author xll
 * @Date 2020年07月16日
 */
public class OrderUploadResponseVo {

    private static final String SUCCESS = "0000";
    private static final String SUCCESS_MSG = "上送成功";
    public static final String REPEAT_CODE = "9001";
    public static final String FAIL_CODE = "9999";

    private String resultCode;
    private String resultMsg;


    public OrderUploadResponseVo(String resultCode, String resultMsg){
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    public static OrderUploadResponseVo success() {
        return new OrderUploadResponseVo(SUCCESS, SUCCESS_MSG);
    }

    public static OrderUploadResponseVo failure(String msg) {
        return new OrderUploadResponseVo(FAIL_CODE, msg);
    }
    public static OrderUploadResponseVo failure(String msg, String code) {
        return new OrderUploadResponseVo(code, msg);
    }

    public boolean isSuccess() {
        return StringUtils.equalsIgnoreCase(SUCCESS, resultCode);
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }
}

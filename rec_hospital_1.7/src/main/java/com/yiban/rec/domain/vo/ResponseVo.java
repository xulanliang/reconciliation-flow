package com.yiban.rec.domain.vo;

import org.apache.commons.lang3.StringUtils;


/**
 * 数据上送统一出参
 * @Author WY
 * @Date 2018年7月25日
 */
public class ResponseVo {

    private static final String CODE_SUCCESS = "SUCCESS";
    private static final String CODE_SUCCESS_MSG = "成功";
    private static final String CODE_FAIL = "FAIL";
    private static final String PART_FAIL = "PartFail";
    
    /** 业务处理结果 */
    private String resultCode;
    /** 业务处理提示 */
    private String resultMsg;
    
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
  
    public ResponseVo() {
        super();
    }
    
    public ResponseVo(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }
    
    /**
     * 成功
     * @return ResponseVo
     */
    public static ResponseVo success() {
        return new ResponseVo(CODE_SUCCESS, CODE_SUCCESS_MSG);
    }
    
    public static ResponseVo success(String message) {
        return new ResponseVo(CODE_SUCCESS, message);
    }
    
    /**
     * 通讯成功、业务失败
     * @return
     * UploadResponseVo
     */
    public static ResponseVo failure(String msg) {
        return new ResponseVo(CODE_FAIL, msg);
    }
    
    /**
     * 部分失败
     * @return
     * UploadResponseVo
     */
    public static ResponseVo partFail(String msg) {
        return new ResponseVo(PART_FAIL, msg);
    }
    
    /**
     * 是否成功
     * @return boolean
     */
    public boolean resultSuccess() {
        return StringUtils.equalsIgnoreCase(resultCode, CODE_SUCCESS);
    }
}

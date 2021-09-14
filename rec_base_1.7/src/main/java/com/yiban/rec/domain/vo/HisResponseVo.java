package com.yiban.rec.domain.vo;

/**
 * HIS接口返回数据封装
 * @Author WY
 * @Date 2018年10月8日
 */
public class HisResponseVo {
    
    private static final String SUCCESS = "success";
    private static final String SUCCESS_MSG = "成功";
    private static final String FAILURE = "failure";
    
    /** 返回状态码 */
    private String returnCode;
    
    /** 返回提示信息 */
    private String returnMsg;
    
    /** 返回查询数据 */
    private Object data;
    
    public String getReturnCode() {
        return returnCode;
    }
    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }
    public String getReturnMsg() {
        return returnMsg;
    }
    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
    
    public HisResponseVo(String returnCode, 
            String returnMsg, 
            Object data) {
        super();
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
        this.data = data;
    }
    
    public static HisResponseVo success(Object data) {
        return new HisResponseVo(SUCCESS, SUCCESS_MSG, data);
    }
    
    public static HisResponseVo failure(String msg) {
        return new HisResponseVo(FAILURE, msg, new Object());
    }
}

package com.yiban.framework.core.domain;

/**
* @date 2018年1月5日 上午10:36:59
* 类说明
*/
public class ResponseResult {

    private boolean success;
    private int code;
    private String message = "";
    private String debugMessage = "";
    private Object data;

    public ResponseResult() {
		super();
	}

	public static ResponseResult success() {
        return new ResponseResult(true);
    }

    public static ResponseResult failure() {
        return new ResponseResult(false);
    }

    public static ResponseResult success(String message) {
        return new ResponseResult(true, message);
    }

    public static ResponseResult failure(String message) {
        return new ResponseResult(false, message);
    }

    public ResponseResult(boolean success) {
        this(success, "");
    }
    
    

    public ResponseResult(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public ResponseResult(boolean success, String message) {
        this(success, message, null);
    }

    public ResponseResult(boolean success, String message, Object data) {
        this(success, success ? 0 : 1, message, data);
    }

    public ResponseResult(boolean success, int code, String message, Object data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public ResponseResult success(boolean success) {
        this.success = success;
        return this;
    }

    public int getCode() {
        return code;
    }

    public ResponseResult code(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResponseResult message(String message) {
        this.message = message;
        return this;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public ResponseResult debugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
        return this;
    }

    public Object getData() {
        return data;
    }

    public ResponseResult data(Object data) {
        this.data = data;
        return this;
    }
}

package com.yiban.framework.core.domain;

import java.io.IOException;
import java.io.StringWriter;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ApiResponseResult {

	protected boolean result;//成功为true，失败为false
	protected String msg = "";
	public void setMsg(String msg) {
		this.msg = msg;
	}

	protected Object data;
	protected String status;//它自定义的状态码
    
    public static ApiResponseResult success() {
        return new ApiResponseResult(true);
    }

    public static ApiResponseResult failure() {
        return new ApiResponseResult(false);
    }

    public static ApiResponseResult success(String message) {
        return new ApiResponseResult(true, message);
    }

    public static ApiResponseResult failure(String message) {
        return new ApiResponseResult(false, message);
    }

    public ApiResponseResult() {
		super();
	}
    
    

	public ApiResponseResult(boolean success) {
        this(success, success?"0":"1");
    }

    public ApiResponseResult(boolean success, String message) {
        this(success, message, null);
    }

    public ApiResponseResult(boolean success, String message, Object data) {
    	this.result = success;
        this.msg = message;
        this.data = data;
        this.status = success?"0":"1";
    }
    
    public ApiResponseResult(String status, String message, Object data) {
        this.msg = message;
        this.data = data;
        this.status = status;
    }
    
    public String toJsonStr()
    {
    	ObjectMapper objMapper = new ObjectMapper();
    	StringWriter str=new StringWriter();
    	try {
			objMapper.writeValue(str, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return str.toString();
    }
  
    public ApiResponseResult(String status, String message) {
        this.msg = message;
        this.status = status;
    }

    public boolean isResult() {
        return result;
    }

    public ApiResponseResult result(boolean result) {
        this.result = result;
        return this;
    }
    
    public String getMsg() {
        return msg;
    }
    

	public ApiResponseResult message(String message) {
        this.msg = message;
        return this;
    }
	
    public void setResult(boolean result) {
		this.result = result;
	}

	public void setMessage(String message) {
		this.msg = message;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Object getData() {
        return data;
    }

    public ApiResponseResult data(Object data) {
        this.data = data;
        return this;
    }
    
    public Object getStatus() {
        return status;
    }

    public ApiResponseResult status(String status) {
        this.status = status;
        return this;
    }

    /**
     * v1.2 新增属性
     * @see com.yiban.medicalrecord.app.service.recommend.RecommendedService#search
     */
    private int count;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	public ApiResponseResult count(int count) {
		this.count = count;
		return this;
	}
	
}

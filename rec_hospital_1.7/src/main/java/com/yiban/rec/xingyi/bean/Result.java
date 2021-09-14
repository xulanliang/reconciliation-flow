package com.yiban.rec.xingyi.bean;

public class Result {
  private String code;
  
  private Object data;
  
  private String message;
  
  public String getCode() {
    return this.code;
  }
  
  public void setCode(String code) {
    this.code = code;
  }
  
  public Object getData() {
    return this.data;
  }
  
  public void setData(Object data) {
    this.data = data;
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public Result(String code, Object data, String message) {
    this.code = code;
    this.data = data;
    this.message = message;
  }
  
  public static com.yiban.rec.xingyi.bean.Result success(Object data) {
    return new com.yiban.rec.xingyi.bean.Result("0", data, null);
  }
  
  public static com.yiban.rec.xingyi.bean.Result fail(String message) {
    return new com.yiban.rec.xingyi.bean.Result("1", null, message);
  }
}

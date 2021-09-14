package com.yiban.rec.domain.vo;

import java.util.Map;

import com.yiban.rec.util.HisInterfaceType;

/**
 * his接口调用入参对象
 * @Author WY
 * @Date 2018年10月8日
 */
public class HisRequestVo {
    /** 接口类型 */
    private HisInterfaceType type;
    /** 接口地址 */
    private String serverUrl;
    /** webService 方法名 */
    private String functionName;
    /** 请求参数 */
    private Map<String, String> params;
    /** 请求参数 */
    private String httpType;
    /** 数据源信息 */
    private DataSource dataSource;
    /** 数据源查询SQL脚本 */
    private String sql;
    /** exe执行脚本 */
    private String command;
    
    public HisInterfaceType getType() {
        return type;
    }
    public void setType(HisInterfaceType type) {
        this.type = type;
    }
    public String getServerUrl() {
        return serverUrl;
    }
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
    public Map<String, String> getParams() {
        return params;
    }
    public void setParams(Map<String, String> params) {
        this.params = params;
    }
    public String getHttpType() {
        return httpType;
    }
    public void setHttpType(String httpType) {
        this.httpType = httpType;
    }
    public DataSource getDataSource() {
        return dataSource;
    }
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public String getSql() {
        return sql;
    }
    public void setSql(String sql) {
        this.sql = sql;
    }
    public String getCommand() {
        return command;
    }
    public void setCommand(String command) {
        this.command = command;
    }
    public String getFunctionName() {
        return functionName;
    }
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
    
}

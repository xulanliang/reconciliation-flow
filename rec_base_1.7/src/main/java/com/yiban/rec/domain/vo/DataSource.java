package com.yiban.rec.domain.vo;

/**
 * 数据源配置基本信息
 * @Author WY
 * @Date 2018年10月8日
 */
public class DataSource {
    /** 数据库ip */
    private String ip;
    /** 数据库端口号 */
    private Integer port;
    /** 数据库账号 */
    private String username;
    /** 数据库密码 */
    private String password;
    /** 数据源类型：Mysql/Oracle */
    private String dataSourceType;
    /** 数据库名称 */
    private String dataBaseName;
    
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public Integer getPort() {
        return port;
    }
    public void setPort(Integer port) {
        this.port = port;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getDataSourceType() {
        return dataSourceType;
    }
    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }
    public String getDataBaseName() {
        return dataBaseName;
    }
    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }
}

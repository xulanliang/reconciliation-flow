package com.yiban.rec.util;

/**
 * HIS接口类型
 * @Author WY
 * @Date 2018年10月8日
 */
public enum HisInterfaceType {
    HTTP("http","调用http接口"),
    WEBAPI("webapi","调用webapi接口"),
    WCF("wcf","调用wcf服务"),
    WEBSERVICE("webservice","调用webservice服务"),
    EXE("exe","执行exe文件"),
    DATASOURCE("datasource","提供数据源方式");
    
    String value;
    String name;
    
    private HisInterfaceType(String value,String name) {
        this.value = value;
        this.name = name;
    }
    
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}

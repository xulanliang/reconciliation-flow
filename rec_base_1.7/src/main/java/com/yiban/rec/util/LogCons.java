package com.yiban.rec.util;

/**
 * 全局变量
 * @Author WY
 * @Date 2018年9月30日
 */
public interface LogCons {

    /** 日志类型：01：拉取账单，02：对账 */
    public static final String LOG_TYPE_BILL = "01";
    public static final String LOG_TYPE_REC = "02";
    
    /** 日志状态：70-异常，71-正常 */
    public static final int REC_FAIL = 70;
    public static final int REC_SUCCESS = 71;
}

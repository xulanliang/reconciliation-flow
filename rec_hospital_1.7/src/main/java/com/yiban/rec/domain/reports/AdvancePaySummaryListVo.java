package com.yiban.rec.domain.reports;

/**
 * describe: 预收款汇总报表入参实体
 *
 * @author xll
 * @date 2020/03/26
 */
public class AdvancePaySummaryListVo {
    private String orgCode;
    private String date;
    private String billSource;

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBillSource() {
        return billSource;
    }

    public void setBillSource(String billSource) {
        this.billSource = billSource;
    }

}

package com.yiban.rec.domain.reports;

/**
 * describe: 增加款、减少款详情列表入参实体
 *
 * @author xll
 * @date 2020/03/26
 */
public class AdvancePaySummaryDetailListVo {
    private String orgCode;
    private String date;
    private String billSource;
    // 详情类型：1：增加款明细   0：减少款明细
    private String detailType;
    private String fileName;
    private String workSheetName;

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

    public String getDetailType() {
        return detailType;
    }

    public void setDetailType(String detailType) {
        this.detailType = detailType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getWorkSheetName() {
        return workSheetName;
    }

    public void setWorkSheetName(String workSheetName) {
        this.workSheetName = workSheetName;
    }
}

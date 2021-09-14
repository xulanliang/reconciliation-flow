package com.yiban.rec.service;

import java.util.List;
import java.util.Map;

public interface DataSummaryService {

    List<Map<String, Object>> findAllSummaryByDate(SummaryQuery query);


    class SummaryQuery {
        private String orgCode;
        /**
         * 时间格式：yyyy-MM-dd
         */
        private String beginTime;
        private String endTime;
        /**
         * 汇总方式， days:按日统计, months：按月统计, years 按年统计
         */
        private String collectType;
        /**
         * 业务类型，相当于用来统计分组的sql
         */
        private String columnSql;

        private String payType;

        private String patType;
        private String dataSource;

        public String getOrgCode() {
            return orgCode;
        }

        public void setOrgCode(String orgCode) {
            this.orgCode = orgCode;
        }

        public String getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getCollectType() {
            return collectType;
        }

        public void setCollectType(String collectType) {
            this.collectType = collectType;
        }

        public String getColumnSql() {
            return columnSql;
        }

        public void setColumnSql(String columnSql) {
            this.columnSql = columnSql;
        }

        public String getPayType() {
            return payType;
        }

        public void setPayType(String payType) {
            this.payType = payType;
        }

        public String getPatType() {
            return patType;
        }

        public void setPatType(String patType) {
            this.patType = patType;
        }

        public String getDataSource() {
            return dataSource;
        }

        public void setDataSource(String dataSource) {
            this.dataSource = dataSource;
        }
    }
}

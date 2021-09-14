package com.yiban.rec.service;

import java.util.List;
import java.util.Map;

/**
 * @author  
 * @date 类说明 根据业务类型分组报表
 */
public interface SummaryService {
	// 按支付类型分组汇总
	List<Map<String, Object>> findAllOfSummaryByPayType(SummaryService.SummaryByPayTypeVo query);
	
	// 按业务类型分组汇总
	List<Map<String, Object>> findAllOfSummaryByBuisinessType(SummaryService.SummaryByPayTypeVo query);
	
	// 按业务类型分组汇总
	List<Map<String, Object>> findAllOfSummaryByDay(SummaryService.SummaryByPayTypeVo query);

	class SummaryByPayTypeVo {
		private String orgCode;
		private String beginTime;
		private String endTime;
		private String terminalNo;
		private String dateType;
		
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
		public String getTerminalNo() {
			return terminalNo;
		}
		public void setTerminalNo(String terminalNo) {
			this.terminalNo = terminalNo;
		}
		public String getDateType() {
			return dateType;
		}
		public void setDateType(String dateType) {
			this.dateType = dateType;
		}
	}
}

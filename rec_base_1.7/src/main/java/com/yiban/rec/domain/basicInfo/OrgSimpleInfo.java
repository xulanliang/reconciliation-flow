package com.yiban.rec.domain.basicInfo;

/** 
* @ClassName: OrgSimpleInfo 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author tuchun@clearofchina.com 
* @date 2017年4月16日 上午11:26:02 
* @version V1.0 
*  
*/
public class OrgSimpleInfo {
	//机构ID
	private Long orgId;
	//机构编码
	private String orgNo;;
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public String getOrgNo() {
		return orgNo;
	}
	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}
	
}

package com.yiban.rec.domain.basicInfo;

import java.util.List;

/**
*<p>
*<p>文件描述:处理放射沙龙平台数据
*<p>版权所有:深圳市依伴数字科技有限公司版权所有(C)2017</p>
*<p>内容摘要:简要描述本文件的内容，包括主要模块、函数及能的说明
</p>
*<p>其他说明:其它内容的说明
</p>
*<p>完成日期:2017年7月27日上午10:23:11</p>
*<p>
*@author fangzuxing
 */
public class RadiationPlatform<T> {
	
	private String orgNo;;
	
	private Integer total;
	
	private String billDate;
	
	List<T> dataList;

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public String getBillDate() {
		return billDate;
	}

	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	
}

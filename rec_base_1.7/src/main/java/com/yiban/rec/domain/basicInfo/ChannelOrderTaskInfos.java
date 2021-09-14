package com.yiban.rec.domain.basicInfo;

import java.util.List;

/** 
* @ClassName: ChannelOrderTaskInfo
* @Description: 获取his渠道数据任务调度信息
* @author tuchun@clearofchina.com 
* @date 2017年4月5日 下午2:59:22 
* @version V1.0 
*  
*/
public class ChannelOrderTaskInfos {
	private List<ChannelOrderTaskInfo> channelTaskList;
	/**
	 * 命令类型
	 */
	private String Trade_Code;
	public List<ChannelOrderTaskInfo> getChannelTaskList() {
		return channelTaskList;
	}
	public void setChannelTaskList(List<ChannelOrderTaskInfo> channelTaskList) {
		this.channelTaskList = channelTaskList;
	}
	public String getTrade_Code() {
		return Trade_Code;
	}
	public void setTrade_Code(String trade_Code) {
		Trade_Code = trade_Code;
	}
	
	
	
	
}

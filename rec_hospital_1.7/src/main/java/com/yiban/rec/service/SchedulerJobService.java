package com.yiban.rec.service;

/**
 * 
*<p>文件名称:SchedulerJobService.java
*<p>
*<p>文件描述:定时任务实现接口
*<p>版权所有:深圳市依伴数字科技有限公司版权所有(C)2017</p>
*<p>内容摘要:简要描述本文件的内容，包括主要模块、函数及能的说明
</p>
*<p>其他说明:其它内容的说明
</p>
*<p>完成日期:2017年7月10日上午10:55:41</p>
*<p>
*@author fangzuxing
 */
public interface SchedulerJobService {
	
	/**
	* @date：2017年7月10日 
	* @Description：方法功能描述: 失败时重复获取渠道数据
	* @return void: 返回值类型
	* @throws
	 */
	public void reChannelJobTask();
	
	/**
	* @date：2017年7月10日 
	* @Description：方法功能描述: 获取渠道数据
	* @return void: 返回值类型
	* @throws
	 */
	public void channelJobTask();
	
	
	/**
	* @date：2017年7月10日 
	* @Description：方法功能描述: 自动对账定时任务
	* @return void: 返回值类型
	* @throws
	 */
	public void autoRecJobTask();
	
	/**
	* @date：2018年9月13日 
	* @Description：方法功能描述: 调用账单服务，获取所有账单
	* @return void: 返回值类型
	* @throws
	 */
	public String billParseJobTask();
	
	/**
	 * 通过银医接口获取his账单
	 * void
	 */
	public void getHisBillJobTask();
	
	/**
	 * 通过银医接口获取his结算清单数据
	 * void
	 */
	public void getHisSettlementBillJobTask();

	public void emailBillLoopDownload();

}

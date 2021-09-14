package com.yiban.rec.service;

import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.domain.vo.AppRuntimeConfig;

/**
*<p>文件名称:AutoReconciliationService.java
*<p>
*<p>文件描述:本类描述
*<p>版权所有:深圳市依伴数字科技有限公司版权所有(C)2017</p>
*<p>内容摘要:自动对账接口
</p>
*<p>其他说明:其它内容的说明
</p>
*<p>完成日期:2017年4月25日上午9:54:10</p>
*<p>
*@author fangzuxing
 */

//@Component
public interface AutoReconciliationService {
	
	public ResponseResult isAutoRecSuccess(String orgNo,String payDate,AppRuntimeConfig config);
	
	
	public ResponseResult isAutoRecCashSuccess(String orgNo,String payDate);
	
	

}

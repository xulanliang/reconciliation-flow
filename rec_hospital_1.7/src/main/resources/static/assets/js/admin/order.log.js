// 支付API调用日志查询
NB.ns("app.admin.order").log = (function() {
	// 表格
	var tableObj = $("#orderLogDataTable");
	// 对话框
	var orderLogInfoDlg = $("#orderLogInfoDlg");
	// 搜索
	var formObj = $("#orderLogSearchForm");
	// 请求路径
	var apiUrl = '/admin/order/data/log';
	
	/**
	 * 绑定操作
	 */ 
	function formaterOpt(id, row) {
		return "<a href='javascript:;' onclick='app.admin.order.log.detail(" 
		+ JSON.stringify(row) + ")' class='btn btn-info btn-sm m-primary' >查看 </a>";
	}

	/**
	 * 搜索
	 */
	function search() {
		var queryData = formObj.serializeObject();
		tableObj.bootstrapTable('refreshOptions', {
			pageNumber : 1,
			queryParams : function(params) {
				var query = $.extend(true, params, queryData);
				return query;
			}
		});
	}

	/**
	 * 初始化表格
	 */
	function initTable() {
		tableObj.bootstrapTable({
			url : apiUrl,
			dataType : "json",
			uniqueId : "id",
			resizable:true,
			singleSelect : true,
			showPaginationSwitch : false,
			pagination : true, 
			sidePagination : 'server'
		});
	}

	/** 查看详情 */
	function detail(row) {
		$("#info_outTradeNo").html(row.outTradeNo);
		$("#info_orderNo").html(row.orderNo);
		$("#info_tsn").html(row.tsn);
		$("#info_requestDateTime").html(row.requestDateTime);
		$("#info_requestParameters").val(row.requestParameters);
		$("#info_responseDateTime").html(row.responseDateTime);
		$("#info_responseParameters").val(row.responseParameters);
		orderLogInfoDlg.modal('show');
	}
	
	function number(value, row, index) {
		   var pageSize=tableObj.bootstrapTable('getOptions').pageSize;
	       var pageNumber=tableObj.bootstrapTable('getOptions').pageNumber;
	       return pageSize * (pageNumber - 1) + index + 1;
	}
	
	/**
	 * 初始化日期
	 */
	function initDate() {
		laydate.render({
			elem : '#requestDateTime',
			theme : '#A9BCF5',
			type: 'datetime',
			range: '~'
		});
	}

	/**
	 * 初始化函数
	 */
	function init() {
		initDate();
		initTable();
	}

	/**
	 * 暴露函数
	 * @returns
	 */
	return {
		init : init,
		search : search,
		detail : detail,
		formaterOpt : formaterOpt,
		number:number,
	}
})();

/**
 * 函数初始化
 * @returns
 */
$(function() {
	app.admin.order.log.init();
});
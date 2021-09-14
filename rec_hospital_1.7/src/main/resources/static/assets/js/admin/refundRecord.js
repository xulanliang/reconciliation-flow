NB.ns("app.admin").serviceMonitor = (function() {

	function init() {
		var orgNo = $('#serviceMonitor_search_org').combotree('getValue');
		var tradeDate = $('#serviceMonitor_endDate').val();
		var url = '/admin/serviceMonitor/detail';
		$('#serviceMonitor_dg').datagrid({
			url : url+"?orgNo="+orgNo+"&tradeDate="+tradeDate+"&t="+new Date().getTime(),
			method:"get",
			required : true
		});
	}
	$('body').on('contextmenu','.datagrid-view1 .datagrid-header',function(e){
		createGridHeaderContextMenu(e,'');
	});
	
	function onSearch() {
		var orgNo = $('#serviceMonitor_search_org').combotree('getValue');
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var tradeDate = $('#serviceMonitor_endDate').val();
		var url = '/admin/serviceMonitor/detail';
		$('#serviceMonitor_dg').datagrid({
			url : url+"?orgNo="+orgNo+"&tradeDate="+tradeDate+"&t="+new Date().getTime(),
			method:"get",
			required : true
		});
	}

	// 刷新
	function refresh() {
		$('#serviceMonitor_dg').datagrid('reload');
	}
	
	$('#serviceMonitor_search_org').combotree({
		url : $('#serviceMonitor_search_org').data("list-url"),
		required : true
	});

	function formatter(val) {
		var typesJSON = $('#serviceMonitor_dg').attr("typesJSON");
		var typesJSON = $.parseJSON(typesJSON);
		return typesJSON[val];
	}

	function orgFormatter(val) {
		var orgJSON = $('#serviceMonitor_dg').attr("orgJSON");
		var orgJSON = $.parseJSON(orgJSON);
		return orgJSON[val];
	}
	function moneyFormat(val, row, index){
		return new Number(val).toFixed(2);
	}
	
	return {
		init : init,
		search : onSearch,
		formatter:formatter,
		orgFormatter:orgFormatter,
		refresh : refresh,
		moneyFormat:moneyFormat
	}

})()
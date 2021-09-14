NB.ns("app.admin").drm = (function() {

	var url = $('#drm_dg').data("search-url");
	function init() {
		var bankTypeId = "";
		var dateTime = $('#drm_dataTime').val();
		$('#drm_dg').datagrid({
			url : url+"?bankTypeId="+bankTypeId+"&dateTime="+dateTime+"&t="+new Date().getTime(),
			method:"get",
			required : true
		});
	}

	$('body').on('contextmenu','.datagrid-view1 .datagrid-header',function(e){
		createGridHeaderContextMenu(e,'');
	});
	
	function onSearch() {
		var url = $('#drm_dg').data("search-url");
		var bankTypeId = $('#drm_bankTypeId').combobox('getValue');
		if(bankTypeId=="9999")bankTypeId="";
		var dateTime = $('#drm_dateTime').val();
		$('#drm_dg').datagrid({
			url : url+"?bankTypeId="+bankTypeId+"&dateTime="+dateTime+"&t="+new Date().getTime(),
			method:"get",
			required : true
		});
	}
	
	// 刷新
	function refresh() {
		$('#drm_dg').datagrid('reload');
	}

	 function formatter(val) {
	   	    var typesJSON = $('#drm_dg').attr("typesJSON");
	        var typesJSON = $.parseJSON(typesJSON);
	        return typesJSON[val];
	    }
	 
	
		function orgFormatter(val) {
			var orgJSON = $('#drm_dg').attr("orgJSON");
			var orgJSON = $.parseJSON(orgJSON);
			return orgJSON[val];
		}
		
		function exportData() {
			var bankTypeId = $('#drm_bankTypeId').combobox('getValue');
			if(bankTypeId=="9999")bankTypeId="";
			var dateTime = $('#drm_dateTime').val();
			$.messager.confirm('确认','一次导出最大的数据量为5000条，确定执行此操作?',function(r){
	            if (r){
	        		where ='&bankTypeId='+ bankTypeId +'&dateTime='+ dateTime+"&t="+new Date().getTime();
	        		url = '/admin/drm/export?' + where;
	        		window.location.href=url;
	            }
	        });
		}
	
	return {
		init : init,
		search : onSearch,
		formatter:formatter,
		orgFormatter:orgFormatter,
		exportData:exportData,
		refresh : refresh
	}

})()
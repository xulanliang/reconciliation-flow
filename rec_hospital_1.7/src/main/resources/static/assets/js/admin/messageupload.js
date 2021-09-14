NB.ns("app.admin").messageUpload = (function() {

	var url;
	function destroymessageUpload() {
		var row = $('#messageUpload_tbl').datagrid('getSelected');
		if (row) {
			$.messager.confirm('Confirm', '确认删除此数据?',
					function(e) {
						if (e) {
							var index = $('#messageUpload_tbl').datagrid(
									'getRowIndex', row);
							$.post('/admin/messageUpload/delete', {
								id : row.id
							}, function(result) {
								if (result.success) {
									$('#messageUpload_tbl').datagrid('deleteRow',
											index);
								} else {
									$.messager.alert('出错了', result.message,
											'error');
								}
							}, 'json');

						}
					});
		} else {
			$.messager.alert("提示", "未选中要操作的行!");
		}
	}

	function init() {
		var url = $('#messageUpload_tbl').data("search-url");
		 $('#messageUpload_tbl').datagrid({
				url: url,
				method : "post"
	         });
	}

	function formatter(val) {
		var typesJSON = $('#messageUpload_tbl').attr("typesJSON");
		var typesJSON = $.parseJSON(typesJSON);
		return typesJSON[val];
	}
	
	function orgFormatter(val) {
		var orgJSON = $('#messageUpload_tbl').attr("orgJSON");
		var orgJSON = $.parseJSON(orgJSON);
		return orgJSON[val];
	}
	
	 function newmessageUpload() {
		 var index = layer.load(1, {
			  shade: [0.1,'#fff'] //0.1透明度的白色背景
			});
		var url = '/admin/messageUpload/upload';
		$.ajax({
			type: "GET",
			url: url,
			dataType: "json",
			timeout:10000,
			error: function() {
				layer.msg("调用结果异常！")
				layer.close(index);
			},
			success: function(result) {
				layer.msg(result.data)
				layer.close(index);
			}
		});
	
	}

	return {
		init : init,
		destroymessageUpload : destroymessageUpload,
		orgFormatter:orgFormatter,
		newmessageUpload : newmessageUpload,
		formatter :formatter
	}

})()
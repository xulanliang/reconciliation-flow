NB.ns("app.admin").serviceMonitor = (function() {

	var url;

	function newserviceMonitor() {
		$('#serviceMonitor-dlg').dialog('open').dialog('setTitle', '新增服务监测信息');

		$('#orgNo').combotree({
			url : $('#orgNo').data("list-url"),
			required : true,
			onSelect : function(node) {
				$('#orgNo').val(node.id);
			}
		});

		url = '/admin/serviceMonitor/save';
		$('#serviceMonitor-fm').form('clear');

	}
	$('body').on('contextmenu','.datagrid-view1 .datagrid-header',function(e){
		createGridHeaderContextMenu(e,'');
	});

	function refresh() {
		$('#serviceMonitor_tbl').datagrid('reload');
	}
	function save() {
		$('#serviceMonitor-fm').form('submit', {
			url : url,
			onSubmit : function() {
				return $(this).form('validate');
			},
			success : function(result) {
				result = JSON.parse(result);
				if (!result.success) {
					$.messager.alert('出错了', result.message, 'error');
				} else {
					$('#serviceMonitor-dlg').dialog('close');
					$('#serviceMonitor_tbl').datagrid('reload');
					$.messager.show({
						title : '提示',
						msg : '操作完成!'
					});
				}
			}
		});
	}
	
	function editserviceMonitor() {
		var row = $('#serviceMonitor_tbl').datagrid('getSelected');
		if (row) {

			$('#serviceMonitor-dlg').dialog('open').dialog('setTitle', '编辑服务监测信息');
			$('#serviceMonitor-fm').form('load', row);
			// 修改值
			$('#orgNo').combotree({
				url : $('#orgNo').data("list-url"),
				required : true,
				onSelect : function(node) {
					$('#orgNo').val(node.id);
				}
			});
			// 默认值
			$('#orgNo').combotree('setValue', row.orgNo);
			var noticeWay = row.noticeWay;
			if(noticeWay.indexOf(",")>=0){
				var noticeWay1 = document.getElementById('noticeWay1');
				noticeWay1.checked = true;
				var noticeWay2 = document.getElementById('noticeWay2');
				noticeWay2.checked = true;
			}
			url = '/admin/serviceMonitor/update';
		} else {
			$.messager.alert("提示", "未选中要操作的行!");
		}
	}

	function destroyserviceMonitor() {
		var row = $('#serviceMonitor_tbl').datagrid('getSelected');
		if (row) {
			$.messager.confirm('Confirm', '确认删除此配置?',
					function(e) {
						if (e) {
							var index = $('#serviceMonitor_tbl').datagrid(
									'getRowIndex', row);
							$.post('/admin/serviceMonitor/delete', {
								id : row.id
							}, function(result) {
								if (result.success) {
									$('#serviceMonitor_tbl').datagrid('deleteRow',
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
		var url = $('#serviceMonitor_tbl').data("search-url");
		 $('#serviceMonitor_tbl').datagrid({
				url: url,
				method : "post"
	         });
	}

	function close() {
		$('#serviceMonitor-dlg').dialog('close');
	}

	function onSearch() {
		var orgNo= $('#organ_search_org_id').combotree('getValue');
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		if(orgNo==9999)orgNo="";
		var url = $('#serviceMonitor_tbl').data("search-url");
		 $('#serviceMonitor_tbl').datagrid({
	           	url: url,
	           	method:"post",
	           	queryParams : {
	           		orgNo:orgNo
	           	}
	         });
	}
	
	function formatter(val) {
		var typesJSON = $('#serviceMonitor_tbl').attr("typesJSON");
		var typesJSON = $.parseJSON(typesJSON);
		return typesJSON[val];
	}
	
	function orgFormatter(val) {
		var orgJSON = $('#serviceMonitor_tbl').attr("orgJSON");
		var orgJSON = $.parseJSON(orgJSON);
		return orgJSON[val];
	}

	return {
		init : init,
		newserviceMonitor : newserviceMonitor,
		editserviceMonitor : editserviceMonitor,
		destroyserviceMonitor : destroyserviceMonitor,
		close : close,
		save : save,
		orgFormatter:orgFormatter,
		refresh : refresh,
		formatter :formatter,
		onSearch : onSearch
	}

})()
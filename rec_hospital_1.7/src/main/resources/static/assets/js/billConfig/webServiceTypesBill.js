NB.ns("app.admin").webServiceTypesBill = (function() {

	var tableObj = $("#webServiceTypesBillDataTable");

	var dlgObj = $("#webServiceTypesBillDlg");

	var digFormObj = dlgObj.find("form");

	var apiUrl = "webservice/config";

	function formatOpt(index, row) {
		var html = "<a href='javascript:;' class='btn btn-info btn-sm m-primary' onclick='app.admin.webServiceTypesBill.update(\""
				+ row.id + "\")'>编辑</a>";
		html += "<a href='javascript:;' class='btn btn-info btn-sm m-primary' onclick='app.admin.webServiceTypesBill.del(\""
				+ row.id + "\")'>删除</a>";
		return html;
	}

	function del(id) {
		bootbox.confirm({
			title : "提示",
			message : "确定删除该记录?",
			buttons : {
				confirm : {
					label : "确认"
				},
				cancel : {
					label : "取消"
				}
			},
			callback : function(r) {
				if (r) {
					var options = {
						url : apiUrl + "/del?id="+id,
						type : "POST",
						dataType : "json",
						success : function(result) {
							if (result.success) {
								$.NOTIFY.showSuccess("提醒", "操作成功", '');
								dlgObj.modal("hide");
								init();
							} else {
								if (result.message) {
									$.NOTIFY.showError("错误", result.message, '');
								} else {
									$.NOTIFY.showError("错误", "操作失败", '');
								}
							}
						}
					};
					digFormObj.ajaxSubmit(options);
				}
			}
		});
	}

	function saveOrUpdate() {
		initValid();
		
		var options = {
			url : apiUrl + "/saveOrUpdate",
			type : "post",
			dataType : "json",
			beforeSubmit:  function(formData, jqForm, options){
            	var flg = digFormObj.data('bootstrapValidator').validate().isValid();
		    	return flg;
            },
			success : function(result) {
				if (result.success) {
					$.NOTIFY.showSuccess("提醒", "操作成功", '');
					dlgObj.modal("hide");
					init();
				} else {
					if (result.message) {
						$.NOTIFY.showError("错误", result.message, '');
					} else {
						$.NOTIFY.showError("错误", "操作失败", '');
					}
				}
			}
		};
		digFormObj.ajaxSubmit(options);
	}

	function update(id) {
		var row = tableObj.bootstrapTable('getRowByUniqueId', id);
		digFormObj.find("input[name=id]").val(row.id);
		digFormObj.find("input[name=dataFieldName]").val(row.dataFieldName);
		digFormObj.find("input[name=classFieldName]").val(row.classFieldName);
		digFormObj.find("input[name=defaultValue]").val(row.defaultValue);
		dlgObj.modal("show");
	}

	function add() {
		digFormObj[0].reset();
		digFormObj.find("input[name=id]").val("");
		dlgObj.modal("show");
	}
	
	function initValid() {
		digFormObj.bootstrapValidator("destroy");
		digFormObj.bootstrapValidator({
			message : '不能为空',
			feedbackIcons : {
				valid : 'glyphicon glyphicon-ok',
				invalid : 'glyphicon glyphicon-remove',
				validating : 'glyphicon glyphicon-refresh'
			},
			fields : {
				dataFieldName : {
					validators : {
						notEmpty : {
							message : 'HIS字段名不能为空'
						}
					}
				},
				classFieldName : {
					validators : {
						notEmpty : {
							message : '表字段名不能为空'
						}
					}
				}
			}
		})
	}

	function init() {
		// 初始化表格
		tableObj.bootstrapTable("destroy");
		tableObj.bootstrapTable({
			url : apiUrl + "/data",
			dataType : "json",
			uniqueId : "id",
			resizable : true,
			singleSelect : true,
			pagination : false, // 是否分页
			sidePagination : 'server',// 选择服务端分页
		});
	}

	return {
		init : init,
		formatOpt : formatOpt,
		add : add,
		saveOrUpdate : saveOrUpdate,
		update : update,
		del : del
	}
})();
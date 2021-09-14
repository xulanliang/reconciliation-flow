NB.ns("app.admin").permission = (function() {
	// 表格
	var tableObj = $("#permissionDataTable");
	// 对话框
	var dlgObj = $('#permissionDlg');
	// 表单
	var formObj = dlgObj.find("form");
	// 请求路径
	var apiUrl = '/admin/permission';
	var ztreeObj;
	var options = {
		beforeSubmit : function(formData, jqForm, options) {
			var flg = formObj.data('bootstrapValidator').validate().isValid();
			return flg;
		},
		success : function(result) {
			if (result.success) {
				dlgObj.modal('hide');
				$.NOTIFY.showSuccess("提醒", result.message, '');
				reflush();
				initTree();
			} else {
				if (result.message) {
					$.NOTIFY.showError("错误", result.message, '');
				}
			}
		},
		url : apiUrl,
		type : 'post',
		dataType : 'json',
		clearForm : false,
		resetForm : false,
		timeout : 3000
	};
	function formatOpt(index, row) {
		return "<a href='javascript:;' onclick='app.admin.permission.edit("
				+ row.id
				+ ")' class='btn btn-info btn-sm m-primary '> 编辑 </a>  &nbsp;"
				+ "<a <a href='javascript:;' onclick='app.admin.permission.destroy("
				+ row.id + ")' class='btn btn-info btn-sm m-danger '> 删除 </a> ";
	}

	function formatType(index, row) {
		if (row.type == 'Menu') {
			return '菜单';
		} else if (row.type == 'Application') {
			return '应用';
		} else if (row.type == 'Business') {
			return '业务';
		} else {
			
		}
	}

	function create() {
		dlgObj.find("h4").text("新增权限");
		options.type = 'post';
		formObj.resetForm();
		formObj.find("input[name='id']").val('');
		ztreeObj.updateValue('');
		resetValidator();
		dlgObj.modal('show');

	}

	function edit(id) {
		options.type = 'put';
		dlgObj.find("h4").text("修改权限");
		var row = tableObj.bootstrapTable('getRowByUniqueId', id);
		formObj.resetForm();
		formObj.loadForm(row);
		resetValidator();
		dlgObj.modal('show');
		ztreeObj.updateValue(row.parent);
	}
	
	function resetValidator() {
		formObj.data('bootstrapValidator').destroy();
		formObj.data('bootstrapValidator', null);
		initValid();
	}
	
	function save() {
		formObj.ajaxSubmit(options);
	}

	function initValid() {
		formObj.bootstrapValidator({
			message : '不能为空',
			feedbackIcons : {
				valid : 'glyphicon glyphicon-ok',
				invalid : 'glyphicon glyphicon-remove',
				validating : 'glyphicon glyphicon-refresh'
			},
			fields : {
				name : {
					validators : {
						notEmpty : {
							message : '名称不能为空'
						},
						stringLength : {
							max : 30,
							message : '权限名称最大30字符'
						}
					}
				},
				type : {
					validators : {
						notEmpty : {
							message : '请选择类型'
						}
					}
				},
				target : {
					validators : {
						notEmpty : {
							message : '权限对象不能为空'
						},
						stringLength : {
							max : 30,
							message : '权限对象最大30字符'
						}
					}
				},
				sort: {
                    validators: {
                    	digits: {
                             message: '排序号只能是数字'
                         }
                    }
                },
				method : {
					validators : {
						notEmpty : {
							message : '方法不能为空'
						},
						stringLength : {
							max : 30,
							message : '权限方法最大30字符'
						}
					}
				},
				description : {
					validators : {
						stringLength : {
							max : 100,
							message : '描述最多100字符'
						}

					}
				}
			}
		})
	}

	function destroy(id) {
		bootbox.confirm({
			title : "提示?",
			message : "确认删除该记录吗?",
			buttons : {
				confirm : {
					className : 'btn-primary btn-sm'
				},
				cancel : {
					className : 'btn-info btn-sm'
				}
			},
			callback : function(r) {
				if (r) {
					$.ajax({
						url : apiUrl + '/' + id,
						type : "delete",
						contentType : "application/json",
						dataType : "json",
						success : function(result) {
							if (result.success) {
								$.NOTIFY.showSuccess("提醒", result.message, '');
								reflush();
								initTree();
							} else {
								if (result.message) {
									$.NOTIFY.showError("错误",result.message, '');
								}
							}
						}
					});
				}
			}
		});
	}
	function reflush() {
		tableObj.bootstrapTable('refresh');
	}

	function init() {
		tableObj
				.bootstrapTable({
					url : apiUrl,
					striped : false,
					sidePagenation : 'server',
					uniqueId : "id",
					treeShowField : 'name',
					parentIdField : 'parent',
					resizable: true,
					onLoadSuccess : function(data) {
//						console.log(data);
						tableObj.treegrid({
									initialState : 'expanded',// 收缩 expanded,collapsed
									treeColumn : 0,// 指明第几列数据改为树形
									expanderExpandedClass : 'glyphicon glyphicon-triangle-bottom',
									expanderCollapsedClass : 'glyphicon glyphicon-triangle-right',
									onChange : function() {
										tableObj.bootstrapTable('resetWidth');
									}
								});
					}
				});
		initValid();
		initTree();
	}

	function initTree() {
		var setting = {
			view : {
				dblClickExpand : false,
				showLine : false,
				selectedMulti : false,
			},
			data : {
				key : {
					isParent : "parent",
					title : ''
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "parent",
					rootPId : null
				}
			}
		};

		$.ajax({
			url : apiUrl,
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) {
				if(ztreeObj){
            		ztreeObj.refresh(data);
            	}else{
            		//这个树形下拉会在和其同级位置生成一个默认隐藏的input输入框以提供表单数据
            		ztreeObj = $("#permissionParentSelect").ztreeview({
            			name: 'name',
            			key: 'id', 
            			//是否
            			clearable:true,
                        expandAll:true,
            			data: data
            		}, setting);
            	}
			}
		});
	}
    
    // 行号
    function number(value, row, index) {
		   var pageSize=tableObj.bootstrapTable('getOptions').pageSize;
	       var pageNumber=tableObj.bootstrapTable('getOptions').pageNumber;
	       return pageSize * (pageNumber - 1) + index + 1;
	}

	return {
		init : init,
		create : create,
		edit : edit,
		destroy : destroy,
		save : save,
//		number:number,
		formatOpt : formatOpt,
		formatType : formatType,
	}
})();

NB.ns("app.admin").organConfig = (function() {
	// 表格
	var tableObj = $("#organConfigDataTable");
	// 对话框
	var dlgObj = $('#organConfigDlg');
	var searchForm = $("#organConfigSearchForm");
	// 表单
	var formObj = dlgObj.find("form");
	// 请求路径
	var apiUrl = '/admin/organConfig';
	// 下拉框对象
	var treeInput = searchForm.find("input[name=parent1]");
	var orgTree = $("#organConfigDlgParentSelect");
	var netThree = dlgObj.find("select[name=networkState]");
	var recTypeThree = dlgObj.find("select[name=recType]");
	var isCashRecThree = dlgObj.find("select[name=isCashRec]");
	// 树对象
	var ztreeObj;
	var ztreeDlgObj;
	var typesJSON;
	var orgJSON;
	var orgList;

	// /////表单提交对象
	var options = {
		beforeSubmit : function(formData, jqForm, options) {
			var flg = formObj.data('bootstrapValidator').validate().isValid();
			return flg;
		},
		success : function(result) {
			$('#organConfigLoading').modal('hide');
			if (result.success) {
				dlgObj.modal('hide');
				$.NOTIFY.showSuccess("提醒", "操作成功", '');
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

	// //////行末，编辑删除按钮
	function formatOpt(index, row) {
		return "<a href='javascript:;' onclick='app.admin.organConfig.edit("
				+ row.id
				+ ")' class='btn btn-info btn-sm m-primary '> 编辑 </a>  &nbsp;"
				+ "<a <a href='javascript:;' onclick='app.admin.organConfig.destroy("
				+ row.id + ")' class='btn btn-info btn-sm m-danger '> 删除 </a> ";
	}

	// /////查询
	function search() {
		var temp = ztreeObj.getVal;
		tableObj.bootstrapTable('refreshOptions', {
			pageNumber : 1,
			queryParams : function(params) {
				var queryObj = {
					orgNo : temp
				};
				var query = $.extend(true, params, queryObj);
				return query;
			}
		});
	}

	// //////新增
	function create() {
		dlgObj.find("h4").text("新增");
		options.type = 'post';
		formObj.resetForm();
		dlgObj.find("input[name=id]").val("");
		resetValidator();
		dlgObj.modal('show');
	}
	// 根据orgCode获取orgId
	function getOrgIdByOrgCode(organizationList, orgCode) {
		var orgId;
		for (var i = 0; i < organizationList.length; i++) {
			if (organizationList[i].code == orgCode) {
				orgId = organizationList[i].id;
				break;
			}
		}
		return orgId;
	}
	// ////////编辑
	function edit(id) {
		options.type = 'put';
		dlgObj.find("h4").text("修改");
		var row = tableObj.bootstrapTable('getRowByUniqueId', id);
		formObj.resetForm();
		formObj.loadForm(row);
		var orgCode = row.orgNo;
		var orgId = getOrgIdByOrgCode(orgList, orgCode);
		resetValidator();
		if (row.orgNo != null) {
			ztreeDlgObj.updateCode(orgId,row.orgNo);
		}
		if (row.networkState != null) {
			netThree.val(row.networkState).trigger("change");
		}
		if (row.recType != null) {
			recTypeThree.val(row.recType).trigger("change");
		}
		if (row.isCashRec != null) {
			isCashRecThree.val(row.isCashRec).trigger("change");
		}
		if (row.isSelf != null) {
			$("#isSelf").val(row.isSelf);
		}
		dlgObj.modal('show');

	}

	// /////保存
	function save() {
		$('#organConfigLoading').modal('show');
		formObj.ajaxSubmit(options);
	}

	// ////删除行
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
								$.NOTIFY.showSuccess("提醒", "删除成功", '');
								reflush();
							} else {
								if (result.message) {
									$.NOTIFY.showError("错误",
											result.message, '');
								}
							}
						}
					});
				}
			}
		});
	}

	// ////弹出框，表单校验
	function initValid() {
		formObj.bootstrapValidator({
			message : '不能为空',
			feedbackIcons : {
				valid : 'glyphicon glyphicon-ok',
				invalid : 'glyphicon glyphicon-remove',
				validating : 'glyphicon glyphicon-refresh'
			},
			fields : {
				orgNoTree : {
					validators : {
						notEmpty : {
							message : '机构名称不能为空'
						},
						stringLength : {
							max : 20,
							message : '最大20字符'
						}
					}
				},

				recTime : {
					validators : {
						notEmpty : {
							message : '对账时间不能为空'
						},
						stringLength : {
							max : 20,
							message : '最大20字符'
						},
						numeric : {
							message : '对账时间只能输入数字'
						}
					}
				},
				recType : {
					validators : {
						notEmpty : {
							message : '对账类型不能为空'
						},
						stringLength : {
							max : 20,
							message : '最大20字符'
						}
					}
				},
				isCashRec : {
					validators : {
						notEmpty : {
							message : '不能为空'
						},
						stringLength : {
							max : 20,
							message : '最大20字符'
						}
					}
				}
			}
		})
	}

	// /////重置校验
	function resetValidator() {
		formObj.data('bootstrapValidator').destroy();
		formObj.data('bootstrapValidator', null);
		initValid();
	}

	// //刷新表格
	function reflush() {
		tableObj.bootstrapTable('refresh');
	}

	// //初始化
	function init(temp_typesJSON, temp_orgJSON) {
		// ///赋值字典数据
		typesJSON = temp_typesJSON;
		orgJSON = temp_orgJSON;
		// ////初始化下拉框，时间控件
		initTree();
		initValid();
	}

	// // 初始化树形下拉框
	function initTree() {
		var setting = {
			view : {
				dblClickExpand : false,
				showLine : false,
				selectedMulti : false,
				fontCss : {
					fontSize : '30px'
				}
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
			url : "/admin/organization",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(msg) {
				orgList = msg;
				// 这个树形下拉会在和其同级位置生成一个默认隐藏的input输入框以提供表单数据
				// ///搜索下拉框
				if (ztreeObj) {
					ztreeObj.refresh(msg);
				} else {
					// 这个树形下拉会在和其同级位置生成一个默认隐藏的input输入框以提供表单数据
					ztreeObj = treeInput.ztreeview({
						name : 'name',
						key : 'code',
						// 是否
						clearable : true,
						expandAll : true,
						data : msg
					}, setting);
					ztreeObj.updateCode(msg[0].id,msg[0].code);
				}

				// //// 弹出框下拉框
				if (ztreeDlgObj) {
					ztreeDlgObj.refresh(msg);
				} else {
					// 这个树形下拉会在和其同级位置生成一个默认隐藏的input输入框以提供表单数据
					ztreeDlgObj = orgTree.ztreeview({
						name : 'name',
						key : 'code',
						// 是否
						clearable : true,
						expandAll : true,
						data : msg
					}, setting);
				}
				// 初始化表
				initTable();
			}
		});

		// /////初始化弹出框当中的下拉框--网络状态
		$.ajax({
			url : "/admin/dict/typeValue?typeValue=network_state",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) {
				netThree.select2({
					placeholder : '==请选择类型==',
					allowClear : true,
					width : '100%',
					minimumResultsForSearch : Infinity,
					data : data,
					templateResult : function(repo) {
						return repo.name;
					},
					templateSelection : function(repo) {
						return repo.name;
					}
				});
			}
		});

		// /////初始化弹出框当中的下拉框--对账类型，
		$.ajax({
			url : "/admin/dict/typeValue?typeValue=rec_type&isIncludeAll=false",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) {
				changeSelectData(data);
				recTypeThree.select2({
					placeholder : '==请选择类型==',
					allowClear : true,
					width : '100%',
					minimumResultsForSearch : Infinity,
					data : data,
					templateResult : function(repo) {
						return repo.name;
					},
					templateSelection : function(repo) {
						return repo.name;
					}
				});
			}
		});
		// /////初始化弹出框当中的下拉框--是否现金对账
		$.ajax({
			url : "/admin/dict/typeValue?typeValue=is_cash_rec&isIncludeAll=false",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) {
				changeSelectData(data);
				isCashRecThree.select2({
					placeholder : '==请选择类型==',
					allowClear : true,
					width : '100%',
					minimumResultsForSearch : Infinity,
					data : data,
					templateResult : function(repo) {
						return repo.name;
					},
					templateSelection : function(repo) {
						return repo.name;
					}
				});
			}
		});
	}

	function initTable() {
		tableObj.bootstrapTable({
			url : apiUrl + "/pageList",
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
			height: $(window).height()-390
		});
		resetTableHeight(tableObj, 390);
	}

	function self(val){
		if(val=='third'){
			return '第三方';
		}else{
			return '巨鼎';
		}
	}
	
	function formatter(val) {
		var typesJSONs = $.parseJSON(typesJSON);
		return typesJSONs[val];
	}

	function orgFormatter(val) {
		var orgJSONs = $.parseJSON(orgJSON);
		return orgJSONs[val];
	}

	return {
		init : init,
		create : create,
		initTree : initTree,
		edit : edit,
		destroy : destroy,
		save : save,
		formatOpt : formatOpt,
		search : search,
		formatter : formatter,
		orgFormatter : orgFormatter,
		getOrgIdByOrgCode : getOrgIdByOrgCode,
		self:self
	}
})();

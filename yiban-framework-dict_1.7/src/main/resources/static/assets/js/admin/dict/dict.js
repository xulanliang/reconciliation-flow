NB.ns("app.admin").dict = (function() {

	//表格
	var tableObj = $("#dictDataTable");
	//对话框
	var dlgObj = $('#dictDlg');
	//表单
	var formObj = dlgObj.find("form");
	var searchDictType = $('#dictTypeList');
	var formDictType = formObj.find("select[name=dictType]");
	//请求路径
	var apiUrl = '/admin/dict';
	//字典类型数据路径 
	var dictTypeUrl ='/admin/dictType/combolist';
	var typesJSON =$.parseJSON(tableObj.attr("typesJSON"));
	var ztreeObj;
	var options = {
		beforeSubmit : function(formData, jqForm, options) {
			var flg = formObj.data('bootstrapValidator').validate().isValid();
			return flg;
		},
		success : function(result) {
			if (result.success) {
				dlgObj.modal('hide');
				reflush();
				$.NOTIFY.showSuccess("提醒", result.message, '');
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
		return "<a href='javascript:;' onclick='app.admin.dict.edit(" + row.id
				+ ")' class='btn btn-info btn-sm m-primary '> 编辑 </a>  &nbsp;"
				+ "<a <a href='javascript:;' onclick='app.admin.dict.destroy("
				+ row.id + ")' class='btn btn-info btn-sm m-danger'> 删除 </a> ";
	}

	
	///////表格字段-字典类型值替换
	function formatType(val, row, index) {
		if(typesJSON[val]){
    		return '<p data-toggle="tooltip" title=\"'+ typesJSON[val] +'\">' + typesJSON[val] +'</p>';
    	}
    	return '<p data-toggle="tooltip" title=\"未知\">未知</p>';
	}
	
	function resetValidator(){
		formObj.data('bootstrapValidator').destroy();
		formObj.data('bootstrapValidator',null);
		initValid();
	}


	function create() {
		dlgObj.find("h4").text("新增字典");
		options.type = 'post';
		formObj.resetForm();
		formDictType.val('').trigger("change");
		resetValidator();
		dlgObj.modal('show');
		
	}

	function edit(id) {
		dlgObj.find("h4").text("修改字典");
		options.type = 'put';
		var row = tableObj.bootstrapTable('getRowByUniqueId', id);
		formObj.resetForm();
		formObj.loadForm(row);
		$("#isActived").val(row.isActived);
		formDictType.val(row.dictType).trigger("change");
		resetValidator();
		dlgObj.modal('show');
	}

	function save() {
		formObj.ajaxSubmit(options);
	}



	function destroy(id) {
		bootbox.confirm({
		    title: "提示?",
		    message: "确认删除该记录吗?",
		    buttons: {
		        confirm: {
		            className: 'btn-primary btn-sm'
		        },
		        cancel: {
		            className: 'btn-info btn-sm'
		        }
		    },
		    callback: function (r) {
		    	if (r) {
					$.ajax({
						url :'/admin/systemMetadata/data/' + id,
						type : "delete",
						contentType : "application/json",
						dataType : "json",
						success : function(result) {
							if (result.success) {
								$.NOTIFY.showSuccess("提醒", result.message, '');
								reflush();
							} else {
								if (result.message) {
									$.NOTIFY.showError("错误", result.message, '');
								}
							}
						}
					});
				}
		    }
		});
	}

	///////刷新表格
	function reflush() {
		tableObj.bootstrapTable('refresh');
	}

	//////点击查询
	function search() {	
		tableObj.bootstrapTable('refreshOptions', {
			  pageNumber:1,
			  queryParams:function(params){
				    var queryObj ={dictType:searchDictType.val()};
	                var query = $.extend( true, params, queryObj);
	                return query;
	            }
		});
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
							max : 20,
							message : '最大20字符'
						}
					}
				},
				value : {
					validators : {
						notEmpty : {
							message : '字典键不能为空'
						},
					
						stringLength : {
							max : 20,
							message : '最大20字符'
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
				},
				dictType : {
					validators : {
						notEmpty : {
							message : '请选择字典类型'
						},
					}
				}
			}
		})
	}
	
	
	function activation(val){
		if(val==0){
			return '<p data-toggle="tooltip" title=\"禁用\">禁用</p>';
		}else{
			return '<p data-toggle="tooltip" title=\"激活\">激活</p>';
		}
	}
	
	function initTable() {
		tableObj.bootstrapTable({
			url : apiUrl,
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable: true,
			pagination : true, // 是否分页
//			height: $(window).height()-360,
			sidePagination : 'server'// 选择服务端分页
		});
//		resetTableHeight(tableObj, 360);
	}
	
	function initType(){
		$.ajax({
			url : "/admin/dictType/combolist",
			type : "GET",
			dataType : "json",
			success : function(data) {
				searchDictType.select2({
					data:data,
					placeholder: '==请选择类型==',
				    width:'150px',
				    allowClear: true,
				    //禁止显示搜索框
				    minimumResultsForSearch: Infinity,
				    templateResult:function(repo){
				    	//下拉显示
				    	return repo.name;
				    },
				    templateSelection:function(repo){
				    	//选中后显示
				    	return repo.name;
				    }
				});
				searchDictType.val('').trigger("change");
				
				formDictType.select2({
					placeholder: '==请选择类型==',
				    allowClear: true,
				    width:'100%',
				    minimumResultsForSearch: Infinity,
				    data:data,
				    templateResult:function(repo){
				    	return repo.name;
				    },
				    templateSelection:function(repo){
				    	return repo.name;
				    }
				});
			}
		});
	}
	
    function init(){
    	//初始化字典类型下拉数据
    	initType();
    	//初始化验证框架
    	initValid();
    	//初始时表格数据
    	initTable();
    }
    
    // 行号
    function number(value, row, index) {
		   var pageSize=tableObj.bootstrapTable('getOptions').pageSize;
	       var pageNumber=tableObj.bootstrapTable('getOptions').pageNumber;
	       return pageSize * (pageNumber - 1) + index + 1;
	}
    
	return {
		init:init,
		create : create,
		edit:edit,
		destroy:destroy,
		save:save,
		number:number,
		formatOpt : formatOpt,
		formatType:formatType,
		search : search,
		activation:activation
	}

})();
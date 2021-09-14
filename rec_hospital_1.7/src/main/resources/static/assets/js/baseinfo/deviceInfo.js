NB.ns("app.admin").deviceInfo = (function() {

	//表格
	var tableObj = $("#deviceInfoTable");
	//对话框
	var dlgObj = $('#deviceInfoDlg');
	//表单
	var formObj = dlgObj.find("form");
	var orgValues = [];
	var formOrg = formObj.find("select[name=orgName]");
	//请求路径
	var apiUrl = '/admin/deviceInfo';
	//字典类型数据路径 
	var dictTypeUrl ='/admin/dictType/combolist';
	var orgNameTree;
	var searchOrgNameTree;
	var options = {
		beforeSubmit : function(formData, jqForm, options) {
			var flg = formObj.data('bootstrapValidator').validate().isValid();
			return flg;
		},
		success : function(result) {
			if (result.success) {
				dlgObj.modal('hide');
				reflush();
				$.NOTIFY.showSuccess("提醒", "操作成功", '');
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
		return "<a href='javascript:;' onclick='app.admin.deviceInfo.edit(" + row.id
				+ ")' class='btn btn-info btn-sm m-primary '> 编辑 </a>  &nbsp;"
				+ "<a <a href='javascript:;' onclick='app.admin.deviceInfo.destroy("
				+ row.id + ")' class='btn btn-info btn-sm m-danger'> 删除 </a> ";
	}

	
	///////表格字段-字典类型值替换
	function formatType(value, row, index) {
		var temp = "未知类型";
		if (orgValues.length > 0) {
			for (var i = 0; i < orgValues.length; i++) {
				if (orgValues[i].id == row.dictType) {
					temp = orgValues[i].value;
				}
			}
		}
		return temp;
	}
	
	function resetValidator(){
		formObj.data('bootstrapValidator').destroy();
		formObj.data('bootstrapValidator',null);
		initValid();
	}


	function create() {
		dlgObj.find("h4").text("新增设备信息");
		options.type = 'post';
		formObj.resetForm();
		formOrg.val('').trigger("change");
		resetValidator();
		dlgObj.modal('show');
	}

	function edit(id) {
		dlgObj.find("h4").text("修改设备信息");
		options.type = 'put';
		options.url= apiUrl;
		var row = tableObj.bootstrapTable('getRowByUniqueId', id);
		formObj.resetForm();
		formObj.loadForm(row);
		formOrg.val(row.orgNo).trigger("change");
		//获取用户机构
		$.ajax({
            url:apiUrl+"/"+id+"/org",
            data:{code:row.orgNo},
            type:"get",
            success:function(data){
            	orgNameTree.updateCode(data,row.orgNo);
            }
        });
		
		resetValidator();
		dlgObj.modal('show');
	}

	function save() {
		var orgCoce = $("#dOrgNameTree").val();
		if(orgCoce == null || orgCoce == ""){
			$.NOTIFY.showError("错误", "请选择所属机构", '');
			return;
		}
		formObj.ajaxSubmit(options);
	}



	function destroy(id){
		bootbox.confirm('确认删除该记录吗?', function(r) {
               if (r) {
            	   $.ajax({
       	            url: apiUrl+'/'+id+"?id="+id,
       	            type:"delete",
       	            contentType : "application/json",
					dataType : "json",
       	            success:function(result){
       	            	if (result.success) {
	       	 				$.NOTIFY.showSuccess("提醒", "删除成功", '');
	       	 				reflush();
	       	 			} else {
	       	 				$.NOTIFY.showError("错误", result.message, '');
	       	 			}
       	            },
       	            error:function(xhr,textstatus,thrown){

       	            }
       	        });
               }
           });
	}

	///////刷新表格
	function reflush() {
		tableObj.bootstrapTable('refresh');
	}

	//////点击查询
	function search(){
		var deviceNo = $("#deviceNo").val();
		var org = searchOrgNameTree.getVal;
		tableObj.bootstrapTable('refreshOptions', {
			  pageNumber:1,
			  queryParams:function(params){
				    var queryObj ={deviceNo:deviceNo,orgNo:org};
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
				deviceNo : {
					validators : {
						notEmpty : {
							message : '设备编码不能为空'
						},
						stringLength : {
							max : 200,
							message : '设备编码最大200字符'
						}
					}
				},
				deviceSn : {
					validators : {
						stringLength : {
							max : 200,
							message : '设备序列号最大200字符'
						}
					}
				},
				deviceMackey : {
					validators : {
						stringLength : {
							max : 200,
							message : '设备传输密钥最大200字符'
						}
					}
				},
				deviceArea : {
					validators : {
						stringLength : {
							max : 100,
							message : '设备所在的区域最多100字符'
						}
					}
				},
				orgNo : {
					validators : {
						notEmpty : {
							message : '所属机构不能为空'
						},
					}
				}
			}
		})
	}
	
	
	function initTable() {
		tableObj.bootstrapTable({
			url : apiUrl+"/pageList",
			dataType : "json",
			uniqueId : "id",
			resizable: true,
			singleSelect : true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
		});
	}
	
	//查询条件机构树
	function formatSearchOrgListSelectPicker(){
		var setting = {
			        view: {
			          dblClickExpand: false,
			          showLine: false,
			          selectedMulti: false,
			          fontCss: {fontSize:'18px'}
			        },
			        data: {
				          key:{
				            isParent: "parent",
				            title:''
				          },
				          simpleData: {
				            enable:true,
				            idKey: "id",
				            pIdKey: "parent",
				            rootPId: null
				          }
				    }
			 };
		$.ajax({
           url:"/admin/organization/data",
           type:"get",
           contentType:"application/json",
           dataType:"json",
           success:function(msg){
        	 //这个树形下拉会在和其同级位置生成一个默认隐藏的input输入框以提供表单数据
        	   searchOrgNameTree = $("#searchOrgNameTree").ztreeview({
	       			name: 'name',
	       			key: 'code', 
	       			clearable:true,
	                expandAll:true,
	       			data: msg
	       		}, setting);
        	   searchOrgNameTree.updateCode(msg[0].id,msg[0].code);
        	   // 选择隐藏还是现实机构下拉选择
				var length = msg.length;
				if(length && length>1){
					$("#searchOrgNameTree").parent().show();
				}else{
					$("#searchOrgNameTree").parent().hide();
				}
        	   orgNameTree = $("#dOrgNameTree").ztreeview({
	       			name: 'name',
	       			key: 'code',
	       			clearable:true,
	                expandAll:true,
	       			data: msg
	       		}, setting);
           }
       });
	}
	
    function init(){
    	//初始化机构名称下拉数据
    	formatSearchOrgListSelectPicker();
    	//初始化验证框架
    	initValid();
    	//初始时表格数据
    	initTable();
    }
    function number(value, row, index) {
 	   var pageSize=tableObj.bootstrapTable('getOptions').pageSize;
        var pageNumber=tableObj.bootstrapTable('getOptions').pageNumber;
        return pageSize * (pageNumber - 1) + index + 1;
 	}
	return {
		init: init,
		create: create,
		edit: edit,
		destroy:destroy,
		save:save,
		formatOpt : formatOpt,
		formatType:formatType,
		search : search,
		number:number
	}
})();
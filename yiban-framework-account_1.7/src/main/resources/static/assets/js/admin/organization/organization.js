NB.ns("app.admin").organization = (function() {
	// 表格
	var tableObj = $("#organizationDataTable");
	// 对话框
	var dlgObj = $('#organizationDlg');
	// 表单
	var formObj = dlgObj.find("form");
	
	// 请求路径
	var apiUrl = '/admin/organization';
	var otherUrl = '/admin/role/role.json';
	var ztreeObj ;
    var options = { 
	            beforeSubmit:  function(formData, jqForm, options){
	            	var flg = formObj.data('bootstrapValidator').validate().isValid();
			    	return flg;
	            },
	            success: function(result){
	            	if(result.success){
	            		dlgObj.modal('hide');
	            		$.NOTIFY.showSuccess ("提醒", result.message,'');
	            		reflush();
	            		initTree();
	            	}else{
	            		if(result.message){
	            			$.NOTIFY.showError  ("错误", result.message,'');
	            		}
	            	}
	            },
	            url:       apiUrl,      
	            type:      'post', 
	            dataType:  'json',
	            clearForm: false ,       
	            resetForm: false ,       
	            timeout:   3000 
	        };
	function formatOpt(index,row) {
		return "<a href='javascript:;' onclick='app.admin.organization.edit("+ row.id + ")' class='btn btn-info btn-sm m-primary'> 编辑 </a>  &nbsp;"
			+ "<a <a href='javascript:;' onclick='app.admin.organization.destroy("+ row.id +")' class='btn btn-info btn-sm m-danger'> 删除 </a> ";
	}
	
	
	function create() {
		dlgObj.find("h4").text("新增组织机构");
		formObj.find("select[name=parent]").html('');
		options.type='post';
		formObj.resetForm();
		resetValidator();
		dlgObj.modal('show');
		
	}

	function edit(id){
		options.type='put';
		dlgObj.find("h4").text("修改组织机构");
		var row = tableObj.bootstrapTable('getRowByUniqueId',id);
		formObj.resetForm();
		resetValidator();
		formObj.loadForm(row);
		dlgObj.modal('show');
		ztreeObj.updateValue(row.parent);
	}
	
	function save(){
		formObj.ajaxSubmit(options); 
	}
	
	 function initValid(){
	    	formObj.bootstrapValidator({
	            message: '不能为空',
	            feedbackIcons: {
	                valid: 'glyphicon glyphicon-ok',
	                invalid: 'glyphicon glyphicon-remove',
	                validating: 'glyphicon glyphicon-refresh'
	            },
	            fields: {
	            	name: {
	                    validators: {
	                        notEmpty: {
	                            message: '名称不能为空'
	                        },
	                        stringLength: {
	                            max: 20,
	                            message: '最大20字符'
	                        }
	                    }
	                },
	                contactPhone: {
	                    validators: {
	                        regexp: {
	                            regexp: /(^(\d{3,4}-)?\d{7,8})$|(1[3|5|7|8]{1}[0-9]{9})/,
	                            message: '请输入正确的电话号码'
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
	                description: {
	                    validators: {
	                        stringLength: {
	                            max: 100,
	                            message: '最大100字符'
	                        }
	                    }
	                }
	            }
	        })
	    }
	
	function destroy(id){
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
       	            url:apiUrl+'/'+id,
       	            type:"delete",
       	            contentType:"application/json",
       	            dataType:"json",
       	            success:function(result){
       	            	if(result.success){
       	            		$.NOTIFY.showSuccess ("提醒", result.message,'');
       	            		reflush();
    	            		initTree();
       	            	}else{
       	            		if(result.message){
    	            			$.NOTIFY.showError  ("错误", result.message,'');
    	            		}
       	            	}
       	            }
       	        });
               }
		    }
		});
		
	}
	function reflush(){
		tableObj.bootstrapTable('refresh');
	}
	
	function search(){
		  $.ajax({
	            url:apiUrl,
	            data:{name:$("#searchOrgName").val()},
	            type:"get",
	            contentType:"application/json",
	            dataType:"json",
	            success:function(result){
	            	  tableObj.bootstrapTable("load",result);
	            	  tableObj.treegrid({
				            initialState: 'collapsed',//收缩 expanded,collapsed
				            treeColumn: 0,//指明第几列数据改为树形
				            expanderExpandedClass: 'glyphicon glyphicon-triangle-bottom',
				            expanderCollapsedClass: 'glyphicon glyphicon-triangle-right',
				            onChange: function () {
				            	tableObj.bootstrapTable('resetWidth');
				            }
				        });
	            }
	        });
	
	}
	
	function init() {
		///组织结构表
		tableObj.bootstrapTable({
		    url:apiUrl,
		    striped: false,
		    resizable: true,
		    sidePagenation: 'server',
			uniqueId : "id",
		    treeShowField: 'name',
		    parentIdField: 'parent',
//		    height: $(window).height()-390,
		    levels: 0,
		    onLoadSuccess: function (data) {
		    	  tableObj.treegrid({
			            initialState: 'collapsed',//收缩 expanded,collapsed
			            treeColumn: 0,//指明第几列数据改为树形
			            expanderExpandedClass: 'glyphicon glyphicon-triangle-bottom',
			            expanderCollapsedClass: 'glyphicon glyphicon-triangle-right',
			            onChange: function () {
			            	tableObj.bootstrapTable('resetWidth');
			            }
			        });
		    }
		});
//		resetTableHeight(tableObj, 390);
		initValid();
		initTree();
	}
	
    function initTree(){
    	var setting = {
    		view: {
    			dblClickExpand: false,
    			showLine: false,
    			selectedMulti: false,
    			fontCss: {fontSize:'30px'}
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
	            url:apiUrl,
	            type:"get",
	            contentType:"application/json",
	            dataType:"json",
	            success:function(data){
	            	if(ztreeObj){
	            		ztreeObj.refresh(data);
	            	}else{
	            		//这个树形下拉会在和其同级位置生成一个默认隐藏的input输入框以提供表单数据
	            		ztreeObj = $("#organizationParentSelect").ztreeview({
	            			name: 'name',
	            			key: 'id', 
	            			//是否
	            			clearable:true,
	                        expandAll:false,
	            			data: data
	            		}, setting);
	            	}
	            }
	        });
    }
	function resetValidator(){
		formObj.data('bootstrapValidator').destroy();
		formObj.data('bootstrapValidator',null);
		initValid();
	}
  
	return {
		init : init,
		create : create,
		edit:edit,
		destroy:destroy,
		save:save,
		formatOpt : formatOpt,
		search:search
	}
})();

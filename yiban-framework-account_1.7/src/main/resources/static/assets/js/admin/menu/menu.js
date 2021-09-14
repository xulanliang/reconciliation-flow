NB.ns("app.admin").menu = (function() {
	//表格
	var tableObj =$("#menuDataTable");
	//对话框
	var dlgObj = $('#menuDlg');
	//表单
	var formObj = dlgObj.find("form");
	//请求路径
	var apiUrl = '/admin/menu';
	var ztreeObj ;
    var options = { 
	            beforeSubmit:  function(formData, jqForm, options){
	            	var flg = formObj.data('bootstrapValidator').validate().isValid();
			    	return flg;
	            },
	            success: function(result){
	            	if(result.success){
	            		dlgObj.modal('hide');
	            		reflush();
	            		initTree();
	            		$.NOTIFY.showSuccess ("提醒", result.message,'');
	            	}else{
	            		if(result.message){
	            			$.NOTIFY.showError  ("错误", result.message,'');
	            		}
	            	}
	            },
	            url:       apiUrl ,      
	            type:      'post', 
	            dataType:  'json',
	            clearForm: false ,       
	            resetForm: false ,       
	            timeout:   3000 
	        };
	function formatOpt(index,row) {
		return "<a href='javascript:;' onclick='app.admin.menu.edit("+ row.id + ")' class='btn btn-info btn-sm m-primary '> 编辑 </a>  &nbsp;"
			+ "<a <a href='javascript:;' onclick='app.admin.menu.destroy("+ row.id +")' class='btn btn-info btn-sm m-danger '> 删除 </a> ";
	}


	function resetValidator(){
		formObj.data('bootstrapValidator').destroy();
		formObj.data('bootstrapValidator',null);
		initValid();
	}
	function create() {
		dlgObj.find("h4").text("新增菜单");
		options.type='post';
		formObj.resetForm();
		formObj.find("input[name='id']").val('');
		resetValidator();
		dlgObj.modal('show');
		
	}

	function edit(id){
		options.type='put';
		dlgObj.find("h4").text("修改菜单");
		var row = tableObj.bootstrapTable('getRowByUniqueId',id);
		formObj.resetForm();
		formObj.loadForm(row);
		resetValidator();
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
	                            max: 30,
	                            message: '名称最大30字符'
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
	                            message: '描述最多100字符'
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
	       	            		$.NOTIFY.showSuccess ("提醒", "删除成功",'');
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
	
	function init() {
		tableObj.bootstrapTable({
		    url:apiUrl,
		    striped: false,
		    resizable: true,
		    sidePagenation: 'server',
			uniqueId : "id",
		    treeShowField: 'name',
//		    height: $(window).height()-360,
		    parentIdField: 'parent',
		    onLoadSuccess: function (data) {
		    	  tableObj.treegrid({
			            initialState: 'expanded',//收缩 expanded,collapsed
			            treeColumn: 0,//指明第几列数据改为树形
			            expanderExpandedClass: 'glyphicon glyphicon-triangle-bottom',
			            expanderCollapsedClass: 'glyphicon glyphicon-triangle-right',
			            onChange: function () {
			            	tableObj.bootstrapTable('resetWidth');
			            }
			        });
		    }
		});
//		resetTableHeight(tableObj, 360);
		initValid();
		initTree();
	}
	
    function initTree(){
    	var setting = {
    		view: {
    			dblClickExpand: false,
    			showLine: false,
    			selectedMulti: false
    		},
    		data: {
    			key:{
    				isParent: "parent",
    				title:'',
    				url:'myrul'
    			},
    			simpleData: {
    				enable:true,
    				idKey: "id",
    				pIdKey: "parent",
    				rootPId: null
    			}
    		},
    		callback: {
    			beforeClick: function(treeId, treeNode, clickFlag){
    				
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
	            		ztreeObj = $("#menuParentSelect").ztreeview({
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

	return {
		init : init,
		create : create,
		edit:edit,
		destroy:destroy,
		save:save,
		formatOpt : formatOpt
	}
})();
 


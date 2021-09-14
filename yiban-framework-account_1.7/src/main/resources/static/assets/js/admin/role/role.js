NB.ns("app.admin").role = (function() {
	//表格
	var tableObj =$("#roleDataTable");
	//对话框
	var dlgObj = $('#roleDlg');
	//对话框2
	var dlgObj2 = $('#zTreePower');
	//表单
	var formObj = dlgObj.find("form");
	//请求路径
	var apiUrl = '/admin/role';
	var currRoleId='';
	//ztree
	var zTreeObj;
    var options = { 
	            beforeSubmit:  function(formData, jqForm, options){
	            	var flg = formObj.data('bootstrapValidator').validate().isValid();
			    	return flg;
	            },
	            success: function(result){
	            	if(result.success){
	            		dlgObj.modal('hide');
	            		reflush();
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
	            rolePermList:'',
	            timeout:   3000 
	        };
	function formaterOpt(index,row) {
		return "<a href='javascript:;' onclick='app.admin.role.edit("+ row.id + ")' class='btn btn-info btn-sm m-primary'> 编辑 </a>  &nbsp;"
			+ "<a <a href='javascript:;' onclick='app.admin.role.rowClick("+ row.id +")' class='btn btn-info btn-sm m-primary'> 授权 </a>  &nbsp;"
			+ "<a <a href='javascript:;' onclick='app.admin.role.destroy("+ row.id +")' class='btn btn-info btn-sm m-danger'> 删除 </a> ";
	}
	//是否有效
	function isActived(index,row){
		return row.isActivedDisplay;
	}
  
	function resetValidator(){
		formObj.data('bootstrapValidator').destroy();
		formObj.data('bootstrapValidator',null);
		initValid();
	}
  
	function create() {
		options.type='post';
		formObj.resetForm();
		formObj.bootstrapValidator("resetForm",true);
		formObj.find("input[name=id]").val('');
		resetValidator();
		dlgObj.modal('show');
	}

	function edit(id){
		options.type='put';
		var row = tableObj.bootstrapTable('getRowByUniqueId',id);
		formObj.resetForm();
		formObj.data('bootstrapValidator').resetForm(true);
		formObj.loadForm(row);
		resetValidator();
		dlgObj.modal('show');
	}
	
	function save(){
		formObj.ajaxSubmit(options); 
	}
	
	
	function rowClick(id){
    	currRoleId=id;
    	getAllPerms();
    }
  
	function getAllPerms(){
		 var setting = {
			 check: {
					enable: true,
					autoCheckTrigger: true
				},
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
            url:apiUrl+'/'+currRoleId+'/permissionsData',
            type:"get",
            data:{
            	id:currRoleId
            },
            dataType:"json",
            success:function(msg){
            	zTreeObj = $.fn.zTree.init($("#tree"), setting, msg);
            	zTreeObj.expandAll(true);
            	dlgObj2.modal('show');
            },
            error:function(xhr,textstatus,thrown){

            }
        });
    }
	
	function powerSave(){
		var nodes = zTreeObj.getCheckedNodes(true);
		var perms="";
    	for(var i=0;i<nodes.length;i++){
    		perms+=nodes[i].id+',';
    	}
    	var reg=/,$/gi;
    	perms=perms.replace(reg,"");
		$.ajax({
            url:apiUrl+'/'+currRoleId+'/modifyPermissions',
            type:"post",
            data:{
            	roleId:currRoleId,
            	permissionsIds:perms
            },
            dataType:"json",
            success:function(msg){
            	$.NOTIFY.showSuccess ("提醒", "修改成功",'');
            	dlgObj2.modal('hide');
            	reflush();
            },
            error:function(xhr,textstatus,thrown){
            	$.NOTIFY.showSuccess ("提醒", "修改失败",'');
            }
        });
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
	       	         	if (result.success) {
							$.NOTIFY.showSuccess("提醒", result.message, '');
							reflush();
						} else {
							if (result.message) {
								$.NOTIFY.showError("错误", result.message, '');
							}
						}
       	            },
       	            error:function(xhr,textstatus,thrown){

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
		var name =$("#rolecfgSearchForm").find("input[name=name]").val();
		var realm =$("#rolecfgSearchForm").find("input[name=realm]").val();
		tableObj.bootstrapTable('refresh', {query:{f_EQ_name:name,f_EQ_realm:realm} })
	}
	function init() {
		tableObj.bootstrapTable({
			url : apiUrl,
			dataType : "json",
			uniqueId : "id",
			resizable: true,
			singleSelect : true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
//			height: $(window).height()-390
		});
//		resetTableHeight(tableObj, 390);
		initValid();
	}
	//初始化验证插件
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
                            message: '名称最多20字符'
                        }
                    }
                },
                realm: {
                    validators: {
                        stringLength: {
                            max: 50,
                            message: '领域最多50字符'
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
    
    // 行号
    function number(value, row, index) {
		   var pageSize=tableObj.bootstrapTable('getOptions').pageSize;
	       var pageNumber=tableObj.bootstrapTable('getOptions').pageNumber;
	       return pageSize * (pageNumber - 1) + index + 1;
	}
    
	return {
		init : init,
		create : create,
		edit:edit,
		destroy:destroy,
		save:save,
		formaterOpt : formaterOpt,
		search:search,
		isActived:isActived,
		reflush:reflush,
		number:number,
		rowClick:rowClick,
		powerSave:powerSave
	}
})();


 


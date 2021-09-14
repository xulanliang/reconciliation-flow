NB.ns("app.admin").user = (function() {
	//表格
	var tableObj =$("#userDataTable");
	//修改用户框
	var dlgObj = $('#userfgDlg');
	var dlgObj2 = $('#userRolefgDlg');
	//表单
	var formObj = dlgObj.find("form");
	//请求路径
	var apiUrl = '/admin/user';
	var userId='';
	var orgZTreeObj;
    var options = { 
	            beforeSubmit:  function(formData, jqForm, options){
	            	 var flg = formObj.data('bootstrapValidator');
				     return flg.validate().isValid();
	            },
	            success: function(result){
	            	if(result.success){
	            		$.NOTIFY.showSuccess ("提醒", "操作成功",'');
	            		dlgObj.modal('hide');
	            		reflush();
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
	function formaterOpt(index,row) {
		var html="<a onclick='app.admin.user.edit("+ row.id + ")' class='btn btn-info btn-sm m-primary'>编辑</a>   &nbsp;";
		html=html+"<a onclick='app.admin.user.editRole("+ row.id + ")' class='btn btn-info btn-sm m-primary'>角色授权</a>  &nbsp;";
		html=html+"<a onclick='app.admin.user.destroy("+ row.id + ")' class='btn btn-info btn-sm m-danger'>删除</a>  &nbsp;";
		html=html+"<a onclick='app.admin.user.resetPassWord("+ row.id + ")' class='btn btn-info btn-sm m-primary'>重置密码</a>  &nbsp;";
		if(row.status==0){
			html=html+"<a onclick='app.admin.user.changeStatus("+row.status+","+ row.id + ")' class='btn btn-info btn-sm m-danger'> 禁用 </a>";
			return html;
		}else if(row.status==1){
			html=html+"<a onclick='app.admin.user.changeStatus("+row.status+","+ row.id + ")' class='btn btn-info btn-sm m-primary'> 激活 </a>";
			return html;
		}
	}

    //禁用激活
	function changeStatus(status,id){
		var conf="确认禁用该用户？"
		if(status==0){
    		conf="确认禁用该用户？"
    	}else if(status==1){
    		conf="确认启用该用户？"
    	}
		bootbox.confirm({
		    title: "提示?",
		    message: conf,
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
	    	            url:apiUrl+'/'+id+'/status',
	    	            type:"put",
	    	            contentType:"application/json",
	    	            dataType:"json",
	    	            success:function(msg){
	    	            	reflush();
	    	            }
	    	           
	    	        });
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
	
	// 性别字段格式化
    function formaterGender(index,row) {
    	if(row.gender==1){
    		return "<span>男</span>";
    	}else{
    		return "<span>女</span>";
    	}
    }
    function formaterStatus(index,row){
    	if(row.status==0){
    		return "<span style='color: #3071ea'>激活</span>";
    	}else{
    		return "<span style='color: #fa4949'>禁用</span>";
    	}
    }
  
	function create() {
		dlgObj.find("h4").text("新增用户");
		options.type='post';
		formObj.find("input[name=id]").val('');
		$("#userPasswordDiv").show();
		options.url=apiUrl;
		formObj.resetForm();
		resetValidator();
		dlgObj.modal('show');
	}

	function edit(id){
		//激活机构树
		dlgObj.find("h4").text("修改用户");
		options.type='put';
		$("#userPasswordDiv").hide();
		var row = tableObj.bootstrapTable('getRowByUniqueId',id);
		formObj.resetForm();
		formObj.loadForm(row);
		resetValidator();
		dlgObj.modal('show');
		
		//获取用户机构
		$.ajax({
            url:apiUrl+'/'+id+'/org',
            type:"get",
            data:{
            	id:id
            },
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	orgZTreeObj.updateValue(msg.data);
            }
        });
	}
	
	function save(){
		formObj.ajaxSubmit(options); 
	}
	function editRole(id){
		options.type='post';
		userId=id;
		getAllRoles(id);
	}
	//得到用户的角色
	function getUserRoles(data,id,zTreeObj){
		var ids="";
		$.ajax({
            url:apiUrl+'/'+id+'/role',
            type:"get",
            data:{
            	id:id
            },
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	for(var i=0;i<msg.length;i++){
            		if(ids==''){
            			ids=msg[i].id+'';
            		}else{
            			ids=ids+","+msg[i].id;
            		}
            	}
            	var nodes = zTreeObj.getCheckedNodes(false);
            	for(var i=0;i<data.length;i++){
            		for(var j=0;j<msg.length;j++){
                		if(data[i].id==msg[j].id){
                			var nodes = zTreeObj.getNodesByParam("id", data[i].id, null);
                    		zTreeObj.checkNode(nodes[0],true,true);
                		}
                	}
//            		if(ids.indexOf(data[i].id)>-1){
//            			var nodes = zTreeObj.getNodesByParam("id", data[i].id, null);
//                		zTreeObj.checkNode(nodes[0],true,true);
//            		}
            	}
            	dlgObj2.modal('show');
            }
          
        });
	}
	
	function getAllRoles(id) {
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
			          simpleData: {
			            enable:true,
			            idKey: "id",
			            rootPId: null
			          }
			        }
			 };
		$.ajax({
            url:apiUrl+'/roles',
            type:"get",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
	           	zTreeObj = $.fn.zTree.init($("#roleTree"), setting, msg);
	           	zTreeObj.expandAll(true);
	           	getUserRoles(msg,id,zTreeObj);
            }
           
        });
    }
	
	
	//编辑保存用户角色
	function rolesave(){
		var ids='';
		//选中的角色id
		var nodes = zTreeObj.getCheckedNodes(true);
		for(var i=0;i<nodes.length;i++){
			if(ids==''){
				ids=nodes[i].id;
			}else{
				ids=ids+","+nodes[i].id;	
			}
		}
		$.ajax({
            url:apiUrl+'/'+userId+'/role',
            type:"post",
            data:{
            	userId:userId,
            	roleIds:ids
            },
            dataType:"json",
            success:function(msg){
            	$.NOTIFY.showSuccess ("提醒", "修改成功",'');
            	dlgObj2.modal('hide');
            }
          
        });
	}
	//机构树
	function initOrgTree(){
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
           url:"/admin/organization/self/data",
           type:"get",
           contentType:"application/json",
           dataType:"json",
           success:function(data){   
            		//这个树形下拉会在和其同级位置生成一个默认隐藏的input输入框以提供表单数据
            		orgZTreeObj = $("#userOrgParentSelect").ztreeview({
            			name: 'name',
            			key: 'id', 
            			//是否
            			clearable:true,
                        expandAll:true,
            			data: data
            		}, setting);
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
       	            success:function(msg){
       	            	$.NOTIFY.showSuccess ("提醒", "删除成功",'');
       	            	reflush();       	            	
       	            }
       	        });
               }
		    }
		});
	}
	function resetValidator(){
		formObj.data('bootstrapValidator').destroy();
		formObj.data('bootstrapValidator',null);
		initValid();
	}
	function reflush(){
		tableObj.bootstrapTable('refresh');
	}
	function search(){
		var name =$("#usercfgSearchForm").find("input[name=name]").val();
		tableObj.bootstrapTable('refreshOptions', {
			resizable : true,
			pageNumber : 1,
			queryParams : function(params) {
				var queryObj = {
					name : name
				};
				var query = $.extend(true, params, queryObj);
				return query;
			}
		});
		
	}
	function init() {
		tableObj.bootstrapTable({
			url : apiUrl+"?f_EQ_isDeleted=0",
			dataType : "json",
			uniqueId : "id",
			resizable: true,
			singleSelect : true,
//			height: $(window).height()-260,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
		});
//		resetTableHeight(tableObj, 360);
		initValid();
		initOrgTree();
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
                loginName: {
                    validators: {
                    	notEmpty: {
                            message: '用户名不能为空'
                        },
                        stringLength: {
                        	min:2,
                            max: 20,
                            message: '用户名长度在2~20字符'
                        }
                    }
                },
                plainPassword: {
                    validators: {
                        notEmpty: {
                            message: '密码不能为空'
                        },
                        stringLength: {
                            max: 20,
                            message: '最大20字符'
                        }
                    }
                },
                name: {
                    validators: {
                        notEmpty: {
                            message: '姓名不能为空'
                        },
                        stringLength: {
                            max: 20,
                            message: '最大20字符'
                        }
                    }
                },
                mobilePhone: {
                    validators: {
                    	notEmpty: {
                            message: '手机不能为空'
                        },
                        regexp: {
                            regexp: /^1[3|5|7|8]{1}[0-9]{9}$/,
                            message: '请输入正确的手机格式'
                        }
                    }
                },
                email: {
                    validators: {
                        emailAddress: {
                            message: '邮箱地址格式有误'
                        }
                    }
                },
            
                position: {
                    validators: {
                        stringLength: {
                            max: 20,
                            message: '最大20字符'
                        }
                    }
                },
                remark: {
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
	
	

	$('#datepicker').datepicker({
		minView : "month",
		language : 'zh-CN',
		format : 'yyyy-mm-dd',
		autoclose : true,
		todayHighlight: true,
		pickerPosition : "bottom-left"
	});
	
	function resetPassWord(id){
		var passPare=/^[a-zA-Z]{1}[a-zA-Z0-9]{5,9}$/;
		bootbox.prompt({
			title: "重置密码",
		    buttons: {
		        confirm: {
		        	 className: 'btn-primary btn-sm'
		        },
		        cancel: {
		        	 className: 'btn-info btn-sm'
		        }
		    },
		    callback: function (r) {
		    	if(r!=null && r!= ''){
		    		if(passPare.test(r)){
		    			$.ajax({
		    				url:apiUrl+'/'+id+'/password',
		    				type:"put",
		    				data:{
		    					id:id,
		    					newPassword:r
		    				},
		    				dataType:"json",
		    				success:function(msg){
		    					$.NOTIFY.showSuccess ("提醒", "重置成功",'');
		    				},
		    				error:function(xhr,textstatus,thrown){
		    					$.NOTIFY.showError  ("错误", '网络错误','');
		    				}
		    			});
		    		}else{
		    			$.NOTIFY.showError  ("错误", '必须是6到10位的字母和数字,且首位是字母','');
		    		}
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
		formaterOpt : formaterOpt,
		search:search,
		number:number,
		formaterGender:formaterGender,
		formaterStatus:formaterStatus,
		changeStatus:changeStatus,
		editRole:editRole,
		rolesave:rolesave,
		resetPassWord:resetPassWord
	}
})();
 


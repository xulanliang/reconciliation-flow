NB.ns("app.admin").authconfig = (function() {
	//表格
	var dataTable = $("#authConfigTable");
	//对话框
	var formDlg = $('#authConfigDlg');
	var formObj = formDlg.find("form");
	var apiUrl = '/admin/authconfig/data';
	var orgFormTree;
	var orgList;
	var payUrl="/order/unified";
	var options = {
		beforeSubmit : function(formData, jqForm, options) {
			var flg = formObj.data('bootstrapValidator').validate().isValid();
			return flg;
		},
		success : function(result) {
			if (result.success) {
				formDlg.modal('hide');
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
		timeout : 60000
	};
	function formatOpt(index, row) {
		return "<a href='javascript:;' onclick='app.admin.authconfig.edit(" + row.id
				+ ")' class='btn btn-info btn-sm m-primary '> 编辑 </a>  &nbsp;"
				+ "<a <a href='javascript:;' onclick='app.admin.authconfig.destroy("
				+ row.id + ")' class='btn btn-info btn-sm m-danger'> 删除 </a> ";
	}

	function formatStat(index,row){
		if(row.state==1){
			return '<p data-toggle="tooltip" title=\"是\">是</p>';
		}else if(row.state==0){
			return '<p data-toggle="tooltip" title=\"否\">否</p>';
		}
		return '<p data-toggle="tooltip" title=\"未知\">未知</p>';
	}

	function resetCheckbox(){
		//所有多选框默认不选中
		$("#authConfigFormPayApiSelect input[type=checkbox]").attr("checked",false);
		$("#authConfigFormPayApiPaytype input[type=checkbox]").attr("checked",false);
		//隐藏支付类型
		$("#authConfigFormPayApiPaytypeGruop").hide();
	}
	
	function resetValidator(){
		formObj.data('bootstrapValidator').destroy();
		formObj.data('bootstrapValidator',null);
		initValid();
	}

	function create() {
		formDlg.find("h4").text("新增授权配置");
		options.type='post';
		resetValidator();
		formObj.resetForm();
		formDlg.modal('show');
		resetCheckbox();
		createApiKey();
		$('#apiKeyId').prop('disabled', false); 
	}

	function edit(id){
		resetCheckbox();
		options.type='put';
		formDlg.find("h4").text("更新授权配置");
		var row = dataTable.bootstrapTable('getRowByUniqueId',id);
		formObj.resetForm();
		resetValidator();
		formObj.loadForm(row);
		
		//给多选框赋值
		var apieNameArr=row.apiName.split(",");
		  $.each(apieNameArr,function(index,val){
			  $("#authConfigFormPayApiSelect input[value='"+val+"']").attr("checked",true);
			  //如果有支付接口,则需要显示支付类型 
			  if(val == payUrl){
				  $("#authConfigFormPayApiPaytypeGruop").show();
			  }
          });
		  //初始化支付类型多选框
		  var payCodeArr=row.resouce.split(",");
		  $.each(payCodeArr,function(index,val){
			  $("#authConfigFormPayApiPaytype input[value='"+val+"']").attr("checked",true);
          });
		  
		  $('#apiKeyId').prop('disabled', true); 
		  formDlg.modal('show');
		  
	}
	
	function save(){
		  var apiNameArr=[];
		  var payCodeArr=[];
		  $.each($('#authConfigFormPayApiSelect input:checkbox:checked'),function(){
			  apiNameArr.push($(this).val());
          });
		  $.each($('#authConfigFormPayApiPaytype input:checkbox:checked'),function(){
			  payCodeArr.push($(this).val());
          });
		var apiNameStr=apiNameArr.join(",");
		var payCodeStr=payCodeArr.join(",");
		formObj.find("input[name=resouce]").val(payCodeStr);
		formObj.find("input[name=apiName]").val(apiNameStr);
		formObj.ajaxSubmit(options); 
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
       	            		$.NOTIFY.showSuccess ("提醒", '删除成功','');
       	            		reflush();
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

	//刷新表格
	function reflush() {
		dataTable.bootstrapTable('refresh');
	}

	 function createApiKey(){
   	    $.get(apiUrl+'/apikey',function(result){
   	       formObj.find("input[name=apiKey]").val(result);
   	    });
   	    return md5Str;
     }
	 
	//点击查询
	function search(){
		var queryData = $("#authConfigSearchForm").serializeObject();
		dataTable.bootstrapTable('refreshOptions', {
			resizable: true,
			pageNumber : 1,
			queryParams : function(params) {
				var query = $.extend(true, params, queryData);
				return query;
			}
		});
	}
	
	function initValid() {
	 	formObj.bootstrapValidator({
            message: '不能为空',
            feedbackIcons: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            fields: {
            	apiKey: {
                    validators: {
                        notEmpty: {
                            message: '秘钥不能为空'
                        }
                    }
                },
            	clientName: {
                    validators: {
                        notEmpty: {
                            message: '系统名称不能为空'
                        }
                    }
                }
            }
        })
	}

	
	
	function initPayApiSelect(){
		  $.ajax({
	            url:apiUrl+'/combox',
	            data:{isIncludeAll:true,type:3},
	            type:"get",
	            contentType:"application/json",
	            dataType:"json",
	            success:function(data){
	            	 var html='';
		              $.each(data,function(index,val){
		            	    	 var h="<label class='checkbox-inline'><input type='checkbox' value='"+val.id+"'/>"+val.value+"</label><br/>";
		            	    	 html+=h;
		            	});
		              $("#authConfigFormPayApiSelect").html(html);
		              $("#authConfigFormPayApiSelect input").change(function(){
		            	  var v =$(this).val();
		            	  var isChecked = $(this).prop('checked');
		            	  if(v ==payUrl){
		            		  if(isChecked){
		            			  $("#authConfigFormPayApiPaytypeGruop").show();
		            		  }else{
		            			  $("#authConfigFormPayApiPaytypeGruop").hide();
		            			  $("#authConfigFormPayApiPaytype input[type=checkbox]").attr("checked",false);
		            		  }
		            	  }
		              });

	            }
	        });
	}
	

	
	function initPayType(){
		  $.ajax({
	            url:apiUrl+'/combox',
	            data:{isIncludeAll:true,type:1},
	            type:"get",
	            contentType:"application/json",
	            dataType:"json",
	            success:function(data){
	              var html='';
	              $.each(data,function(index,val){
	            	    	 var h="<label class='checkbox-inline'><input type='checkbox' value='"+val.id+"'/>"+val.value+"</label><br/>";
	            	    	 html+=h;
	            	});
	              $("#authConfigFormPayApiPaytype").html(html);
	            }
	        });
	}
	
   
    
	function initTable() {
		dataTable.bootstrapTable({
			url : apiUrl,
			dataType : "json",
			uniqueId : "id",
			resizable: true,
			singleSelect : true,
			pagination : true, // 是否分页
			sidePagination : 'server'// 选择服务端分页
			//height: $(window).height()-390
		});
		resetTableHeight(dataTable, 390);
	}
	
    function init(){
    	
    	initValid();
    	//初始时表格数据
    	initTable();
    	initPayApiSelect();
    	initPayType();
    	
    }
   	
	return {
		init: init,
		create:create,
		edit:edit,
		save:save,
		search:search,
		reflush:reflush,
		destroy:destroy,
		formatOpt:formatOpt,
		formatStat:formatStat,
		createApiKey:createApiKey
	}
})();

$(function(){
	app.admin.authconfig.init();
});
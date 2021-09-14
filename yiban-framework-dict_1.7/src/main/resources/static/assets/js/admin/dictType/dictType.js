NB.ns("app.admin").dictTypecfg = (function() {
	//表格
	var tableObj =$("#dictTypeDataTable");
	//对话框
	var dlgObj = $('#dictTypecfgDlg');
	//表单
	var formObj = dlgObj.find("form");
	//请求路径
	var apiUrl = '/admin/dictType';
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
	            	}else{
	            		$.NOTIFY.showError  ("错误", result.message,'');
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
		return "<a href='javascript:;' onclick='app.admin.dictTypecfg.edit("+ row.id + ")' class='btn btn-info btn-sm m-primary '> 编辑 </a>  &nbsp;"
			+ "<a <a href='javascript:;' onclick='app.admin.dictTypecfg.destroy("+ row.id +")' class='btn btn-info btn-sm m-danger '> 删除 </a> ";
	}

	function resetValidator(){
		formObj.data('bootstrapValidator').destroy();
		formObj.data('bootstrapValidator',null);
		initValid();
	}
  
	function create() {
		dlgObj.find("h4").text("新增字典类型");
		options.type='post';
		resetValidator();
		formObj.resetForm();
		dlgObj.modal('show');
		
	}

	function edit(id){
		dlgObj.find("h4").text("修改新增字典类型");
		options.type='put';
		var row = tableObj.bootstrapTable('getRowByUniqueId',id);
		resetValidator();
		formObj.resetForm();
		formObj.loadForm(row);
		dlgObj.modal('show');
	}
	
	function save(){
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
    	            		$.NOTIFY.showSuccess ("提醒", result.message,'');
    	            		reflush();
    	            	}else{
    	            		$.NOTIFY.showError("错误", result.message,'');
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
		tableObj.bootstrapTable('refresh', {
			query : {
				f_EQ_name : $("#searchDictTypeName").val()
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
				}
			}
		})
	}
	
	function init() {
		tableObj.bootstrapTable({
			url : apiUrl,
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable: true,
			pagination : true, // 是否分页
			sidePagination : 'server'// 选择服务端分页
//			height: $(window).height()-390
		});
		initValid();
//		resetTableHeight(tableObj, 390);
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
		search:search,
		edit:edit,
		destroy:destroy,
		save:save,
		number:number,
		formaterOpt : formaterOpt
	}
})();
 

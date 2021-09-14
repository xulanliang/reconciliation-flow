NB.ns("app.admin").systemMetadata = (function() {

	//表格
	var tableObj = $("#systemMetadataDataTable");
	//对话框
	var dlgObj = $('#systemMetadatatDlg');
	
	var dlgObj2 = $('#zTreeSystemCode');
	
	//请求路径
	var apiUrl = '/admin/systemMetadata/data';
	var typesJSON =$.parseJSON(tableObj.attr("typesJSON"));
	var options = {
		beforeSubmit : function(formData, jqForm, options) {
			var flg = formObj.data('bootstrapValidator').validate().isValid();
			return flg;
		},
		success : function(result) {
			if (result.success) {
				dlgObj.modal('hide');
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
		return "<a href='javascript:;' onclick='app.admin.systemMetadata.getAllSystemCode(\"" + row.value
				+ "\")' class='btn btn-info btn-sm m-primary '> 绑定系统来源 </a>";
	}
	
	
	function getAllSystemCode(metaDataCode){
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
           url:apiUrl+'/'+metaDataCode+'/systemCode',
           type:"get",
           data:{
        	   metaDataCode:metaDataCode
           },
           dataType:"json",
           success:function(msg){
           	zTreeObj = $.fn.zTree.init($("#tree"), setting, msg);
           	zTreeObj.expandAll(true);
           	$("#metaDataCode").val(metaDataCode);
           	dlgObj2.modal('show');
           },
           error:function(xhr,textstatus,thrown){

           }
       });
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
	
	function save() {
		var metaDataCode=$("#metaDataCode").val();
		var nodes = zTreeObj.getCheckedNodes(true);
		var codes="";
    	for(var i=0;i<nodes.length;i++){
    		codes+=nodes[i].value+',';
    	}
    	var reg=/,$/gi;
    	codes=codes.replace(reg,"");
		$.ajax({
	           url:apiUrl+'/'+metaDataCode+'/save',
	           type:"get",
	           data:{
	        	   metaDataCode:metaDataCode,
	        	   codes:codes
	           },
	           dataType:"json",
	           success:function(msg){
	        	   $.NOTIFY.showSuccess ("提醒", "修改成功",'');
	            	dlgObj2.modal('hide');
	           },
	           error:function(xhr,textstatus,thrown){
	        	   $.NOTIFY.showError ("提醒", "修改失败",'');
	           }
	       });
	}


	
	function initTable() {
		tableObj.bootstrapTable({
			url : apiUrl,
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable: true,
			pagination : true, // 是否分页
			sidePagination : 'server'// 选择服务端分页
		});
	}
	
    function init(){
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
		save:save,
		number:number,
		formatOpt : formatOpt,
		formatType:formatType,
		getAllSystemCode:getAllSystemCode
	}

})();
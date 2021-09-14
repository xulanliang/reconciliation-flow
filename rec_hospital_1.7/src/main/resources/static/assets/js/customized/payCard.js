NB.ns("app.admin").payCard = (function() {
	
	// 请求路径
	var apiUrl = "/admin/payCardConfig/data";
	var formObj =$("#billconfigaccordinon").find("form");
    var saveBtn=$("#billconfig_save_bt");
	
	var options = {
			beforeSubmit : function(formData, jqForm, options) {
				saveBtn.attr("disabled", true);
				return true;
			},
			success : function(result) {
				saveBtn.attr("disabled", false);
				$.NOTIFY.showSuccess("提醒", "更新成功", '');
			},
			url : apiUrl+"/updateProjectStatus",
			type : 'post',
			dataType : 'json',
			clearForm : false,
			resetForm : false,
			timeout : 30000
		};
	
	// //初始化
	function init() {
		$.ajax({
			type:"get", 
            url:apiUrl+"/projectStatus",
            dataType:"json",
            success:function(msg){
            	if(msg.data==1){
            		$("input[name='type'][value=1]").attr("checked",true); 
            	}else{
            		$("input[name='type'][value=0]").attr("checked",true); 
            	}
            },
			error:function (result) {  
				$.NOTIFY.showError("错误","加载失败", '');
            }
        });
	}

	// /////保存
	function save() {
		formObj.ajaxSubmit(options);
	}
	
	return {
		init : init,
		save : save
	}
})();

NB.ns("app.admin").hospitalConfig = (function() {
	
	// 请求路径
	var apiUrl = '/admin/hospitalConfig';
    var configJson=JSON.parse($("#webconfigaccordinon").attr("appConfigJson"));
    var formObj =$("#webconfigaccordinon").find("form");
    var saveBtn=$("#webconfig_save_bt");
	var options = {
			beforeSubmit : function(formData, jqForm, options) {
				saveBtn.attr("disabled", true);
				return true;
			},
			success : function(result) {
				saveBtn.attr("disabled", false);
				if (result.success) {
					$.NOTIFY.showSuccess("提醒", "更新成功", '');
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
			timeout : 30000
		};
	
	// //初始化
	function init() {
		formObj.loadForm(configJson);
		initCheckbox();
	}
	
	function initCheckbox(){
		var cheboxArr =$(".configCheckbox");
		$.each(cheboxArr, function(){
			    var _this=$(this);
			    var k =_this.attr("checkbox_name");
			    var v =configJson[k];
			    if(v){
			    	var valArr = v.split(",");
			    	     valArr.forEach(function(ele,index){
			    		  _this.find("input[value='"+ele+"']").attr("checked",true);

						}
			    	);
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

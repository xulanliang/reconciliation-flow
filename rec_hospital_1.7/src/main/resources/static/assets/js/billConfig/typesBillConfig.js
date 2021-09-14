NB.ns("app.admin").typesBillConfig = (function() {
	
	// 请求路径
	var apiUrl = '/admin/billConfig/data';
    var formObj =$("#billconfigaccordinon").find("form");
    var saveBtn=$("#billconfig_save_bt");
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
		//formObj.loadForm(configJson);
		//initCheckbox();
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
		var key="";
		//注入映射key值
		$("input[name='mapperKey']").each(function(){
			var str=$(this).val();
			if(str==""||str==null){
				$.NOTIFY.showError("错误", "字段映射key不能为空", '');
				return;
			}
			if(key==""){
				key= $(this).val();
			}else{
				key=key+","+$(this).val();
			}
			
		});
		var val="";
		//注入映射val值
		$("input[name='mapperVal']").each(function(){
			var str=$(this).val();
			if(str==""||str==null){
				$.NOTIFY.showError("错误", "字段映射val不能为空", '');
				return;
			}
			if(val==""){
				val= $(this).val();
			}else{
				val=val+","+$(this).val();
			}
			
		});
		$("#mapperKeyList").val(key);
		$("#mapperValList").val(val);
		formObj.ajaxSubmit(options); 
	}
	
	//新增映射选项
	function newAdd(){
		var html='<div class="form-group">'
			     +'<div class="col-sm-2"><input class="form-control" type="text" name="mapperKey"/></div>'
				 +'<div class="col-sm-2"><input class="form-control" type="text" name="mapperVal"/></div>'			
				 +'</div>';	
		$("#mapperDiv").append(html);
	}
	
	//隐藏选择
	function showOrhide(type){
		if(type==1){
			$("#emailDiv").show();
		}else if(type==2){
			
		}else{
			$("#emailDiv").hide();
		}
	}

	

	return {
		init : init,
		save : save,
		newAdd:newAdd,
		showOrhide:showOrhide
	}
})();

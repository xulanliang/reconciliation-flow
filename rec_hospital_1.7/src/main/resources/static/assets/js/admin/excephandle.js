NB.ns("app.admin").excephandle = (function() {
	//表格
	var tableObj =$("#excephandleDataTable");
	var dlgObj = $('#exceporderDiv');
	//请求路径
	var apiUrl = '/admin/exceporder/data';
	//查询
	function search(){
		var url = apiUrl;
		var payFlowNo = $('#excep_payFlowNo').val();
		var password = $('#password').val();
		if(payFlowNo==null || payFlowNo==''){
			 $.NOTIFY.showError ("提醒", "流水号不能为空");
			return false;
		}
		if(password ==null || password==''){
			 $.NOTIFY.showError ("提醒", "登录密码不能为空");
			return false;
		} 
		$.ajax({
	           url:url,
	           type:"get",
	           data:{
	        	   payFlowNo:payFlowNo,
	        	   password:password
	           },
	           contentType:"application/json",
	           dataType:"json",
	           success:function(msg){
	        	   if (msg.success) {
	        		    $.NOTIFY.showError ("提醒", msg.message);
						$('#patientName').html("");
						$('#patientCardNo').html("");
						$('#patientIdCard').html("");
						$('#payAmount').html("");
					} else{
						$.NOTIFY.showError  ("提醒", msg.message);
						$('#patientName').html(msg.data.patientName);
						$('#patientCardNo').html(msg.data.patientCardNo);
						$('#patientIdCard').html(msg.data.patientIdCard);
						$('#payAmount').html(msg.data.payAmount);
					}
	           }
	       });
	}
	
	//退费
	function doRefund(){
		var payFlowNo = $('#excep_payFlowNo').val();
		var password = $('#password').val();
		if(payFlowNo==""){
			 $.NOTIFY.showError ("提醒", "流水号不能为空");
				return false;
		}
		if(password==""){
			$.NOTIFY.showError ("提醒", "登录密码不能为空");
			return false;
		}
		var url = apiUrl;
		bootbox.confirm({
		    title: "提示?",
		    message: "确认是否退费?",
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
						type : "post",
						url : url,
						timeout : 5000,
						data:{
							 payFlowNo:payFlowNo,
				        	 password:password
						},
						dataType : "json",
						success : function(result) {
							if (result.success) {
								$.NOTIFY.showError ("提醒", result.message);
								$('#patientName').html("");
								$('#patientCardNo').html("");
								$('#patientIdCard').html("");
							} else {
								$.NOTIFY.showSuccess  ("成功", result.message);
							}
						}
					});
               }
		    }
		});
	}
	
	function init() {
		dlgObj.modal("show");
	}
	
	
	return {
		init : init,
		search:search,
		doRefund:doRefund
	}
})();
 


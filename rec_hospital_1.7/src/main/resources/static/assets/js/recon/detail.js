NB.ns("app.admin").detail = (function() {
	// 表格
	var tableObj = $("#twoDetailDataTable");
	var tableObj2 = $("#detailDataTable");
	var tableDlg = $("#detailTableDlg");
	// 请求路径
	var apiUrl = '/admin/reconciliation/detail';
	// 表单
	var formObj = $("#detailSearchForm");
	var formObjDlg = $("#detailSearchFormDlg");
	// 时间控件
	var startobj = formObj.find("input[name=startTime]");
	var endobj =   formObj.find("input[name=endTime]");
	// 下拉框对象
	var treeInput = formObj.find("input[name=orgNoTree]");
	var treeUrl = '/admin/organization';
	var ztreeObj;
	var tradeType = formObj.find("select[name=tradeType]");
	var payType = formObj.find("select[name=payType]");
	var businessType = formObj.find("select[name=businessType]");
	var bankTypeId = formObj.find("select[name=bankTypeId]");
	var deviceNoObj = formObj.find("select[name=deviceNo]");
	var handleCodeObj = formObjDlg.find("select[name=handleCode]");
	var typesJSON ;
	var orgJSON; 
	var deviceNoValue;
	var accountOrgNo;
	var recType ;
	var OrgRecType = "two";
	////弹出框
	var dlgObj1 = $('#detailDlg1');
	var dlgObj2 = $('#detailDlg2'); 
		
	function init(typesJSON_tmp,orgJSON_tmp,accountDate,accountOrgNo_tmp,deviceNo_tmp,flag,recType_temp,orgNo) {
		
		///初始化赋值
		typesJSON = typesJSON_tmp;
		orgJSON = orgJSON_tmp;
		accountOrgNo = accountOrgNo_tmp;
		deviceNoValue = deviceNo_tmp;
		recType = recType_temp;
		
		//控制页面显示
		tableObj = $("#twoDetailDataTable");
		tableObj.parent().css("display","block");
		
		/////初始化校验
		initValid();
		
		/////初始化控件
		initDate(accountDate);  
		initTable();  
		initTree(accountOrgNo);
		
		//查询
		search(accountOrgNo,accountDate,1,orgNo);
	}
	
	
	///////表单提交对象
	var options = {
		beforeSubmit : function(formData, jqForm, options) {
			var flg = formObjDlg.data('bootstrapValidator').validate().isValid();
			return flg;
		},
		success : function(result) {
			
			if (result.success) {
				dlgObj1.modal('hide');
				$.NOTIFY.showSuccess("提醒", "操作成功", '');
				search(null,null,null);
			} else {
				if (result.message) {
					$.NOTIFY.showError("错误", result.message, '');
				}
			} 
		},
		url : apiUrl + "/add",
		type : 'post',
		dataType : 'json',
		clearForm : false,
		resetForm : false,
		timeout : 3000
	};
	
	/////初始化表格
	function initTable(){ 
		///两方对账
		tableObj.bootstrapTable({
			url : "",
			dataType : "json",
			uniqueId : "id",
			resizable: true,
			singleSelect : true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
//			pageSize:10,
//			pageList:[10,20],//分页步进值
			//height: $(window).height()-390,
			rowStyle:function (row, index) {
				var isDifferent = row.isDifferent; 
				var style = {};  
				if(isDifferent==1){
					style={css:{'background-color':'#ed5565'}}; 
				}
	            return style;
	        }
		});
		///三方对账
		tableObj2.bootstrapTable({
			url : "",
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable: true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
//			pageSize:10,
//			pageList:[10,20],//分页步进值
			//height: $(window).height()-390,
			rowStyle:function (row, index) {
				var isDifferent = row.isDifferent; 
				var style = {};  
				if(isDifferent==1){
					style={css:{'background-color':'#ed5565'}}; 
				}
	            return style;
	        }
		});
		
		tableDlg.bootstrapTable({
			url : "",
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable: true,
			pagination : true// 是否分页 
//			pageSize:30,
//			pageList:[30,50]//分页步进值
		});
		//resetTableHeight(tableObj, 390);
		//resetTableHeight(tableObj2, 390);
	}
	

	
	//导出
	function exportData() {
		 
		var orgNo= ztreeObj.getVal;
		var orgName=ztreeObj.getText;
		var tradeTypeValue = tradeType.val();
		var flowNo = formObj.find("input[name=flowNo]").val();
		var payTypeValue = payType.val();
		var businessTypeValue = businessType.val();
		var startDate = startobj.val();
		var endDate = endobj.val();
		var bankTypeIdValue = bankTypeId.val();
		var deviceNoValue = deviceNoObj.val();
		
		bootbox.confirm('确定执行此操作?',function(r){
	        if (r){
	        	var where = "";
	        	if("two" == OrgRecType){
	        		where = '/admin/reconciliation/detail/api/dcTwoExcel?a=1';
	        	}else{
	        		where = '/admin/reconciliation/detail/api/dcExcel?a=1';
	        	}
	    		
	    		if((orgNo != "" && orgNo != null && orgNo != undefined)){
	    			where = where + '&orgNo=' + orgNo ;
	    		}
	    		if((orgName != "" && orgName != null && orgName != undefined)){
	    			where = where + '&orgName=' + orgName ;
	    		}
	    		
	    		if((tradeTypeValue != "" && tradeTypeValue != null && tradeTypeValue != undefined)){
	    			where = where + '&tradeType=' + tradeTypeValue.join(",") ;
	    		}
	    		if((flowNo != "" && flowNo != null && flowNo != undefined)){
	    			where = where + '&flowNo=' + flowNo ;
	    		}
	    		if((payTypeValue != "" && payTypeValue != null && payTypeValue != undefined)){
	    			where = where + '&payType=' + payTypeValue.join(",") ;
	    		}
	    		if(businessTypeValue != "" && businessTypeValue != null && businessTypeValue != undefined){
	    			where = where + '&businessType=' + businessTypeValue ;
	    		}
	    		if((startDate != "" && startDate != null && startDate != undefined)){
	    			where = where + '&startTime=' + startDate ;
	    		}
	    		if((endDate != "" && endDate != null && endDate != undefined)){
	    			where = where + '&endTime=' + endDate ;
	    		}
	    		if((bankTypeIdValue != "" && bankTypeIdValue != null && bankTypeIdValue != undefined)){
	    			where = where + '&bankTypeId=' + bankTypeIdValue ;
	    		}
	    		if((deviceNoValue != "" && deviceNoValue != null && deviceNoValue != undefined)){
	    			where = where + '&deviceNo=' + deviceNoValue ;
	    		}
	    		where = where + '&t='+new Date().getTime();
	    		
	    		window.location.href=where;
	        }
	    });
		
	}
	
	/////初始化时间
	function initDate(){
		///时间控件初始化
		startobj.datepicker({
			minView : "month",
			language : 'zh-CN',
			format : 'yyyy-mm-dd',
			autoclose : true,
			clearBtn: true, 
			todayHighlight: true, 
			pickerPosition : "bottom-left"
				
		}).on('changeDate', function(ev) {
			var starttime = startobj.val();
			endobj.datepicker('setStartDate', starttime);
			startobj.datepicker('hide');
		});

		endobj.datepicker({
			minView : "month",
			language : 'zh-CN',
			format : 'yyyy-mm-dd',
			autoclose : true,
			clearBtn: true,
			todayHighlight: true, 
			pickerPosition : "bottom-left"
				
		}).on('changeDate', function(ev) {
			var starttime = startobj.val();
			var endtime = endobj.val();
			startobj.datepicker('setEndDate', endtime);
			endobj.datepicker('hide');
		});
		
		////控件赋值
		startobj.datepicker("setDate", accountDate);
		endobj.datepicker("setDate", accountDate);
		
		/*var dom = document.querySelector("#twoDetailDataTableDiv>.bootstrap-table .fixed-table-container");
		dom.style.height = "0px" ;
		dom.style.paddingBottom= "0px" ;*/
	} 
	
	///初始化下拉框
	function initTree(accountOrgNo) {
		
		var setting = {
			view : {
				dblClickExpand : false,
				showLine : false,
				selectedMulti : false,
				fontCss : {
					fontSize : '30px'
				}
			},
			data : {
				key : {
					isParent : "parent",
					title : ''
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "parent",
					rootPId : null
				}
			}
		};
		
		/////初始化组织结构
		$.ajax({
			url : treeUrl,
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) {
				/////搜索下拉框
				if(ztreeObj){
            		ztreeObj.refresh(data);
            	}else{
            		
            		//这个树形下拉会在和其同级位置生成一个默认隐藏的input输入框以提供表单数据
            		ztreeObj = treeInput.ztreeview({
            			name: 'name',
            			key: 'code', 
            			//是否
            			clearable:true,
                        expandAll:true,
            			data: data
            		}, setting);
            		ztreeObj.updateCode(data[0].id,data[0].code);
            	}
				
				if((accountOrgNo != "" && accountOrgNo != null && accountOrgNo != undefined)){
					for(var i=0;i<data.length;i++){
						if(accountOrgNo == data[i].code){
							ztreeObj.updateCode(data[i].id,data[i].code);
						}
					}
				}
			}
		});
		
		///////初始化弹出框当中的下拉框--交易类型
		$.ajax({
			url : "/admin/reconciliation/typeValue?typeValue=Trade_Code",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) { 
				changeSelectData(data);
				tradeType.select2({
					placeholder: '==请选择类型==',
				    allowClear: true,
				    minimumResultsForSearch: Infinity,
				    data:data,
				    templateResult:function(repo){
				    	return repo.name;
				    },
				    templateSelection:function(repo){
				    	return repo.name;
				    }
				});
				tradeType.val("").trigger("change");
			}
		}); 
		
		///////初始化弹出框当中的下拉框--支付类型， 
		$.ajax({
			url : "/admin/reconciliation/typeValue?typeValue=Pay_Type",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) {  
				changeSelectData(data);
				payType.select2({
					placeholder: '==请选择类型==',
				    allowClear: true,
				    minimumResultsForSearch: Infinity,
				    data:data,
				    templateResult:function(repo){
				    	return repo.name;
				    },
				    templateSelection:function(repo){
				    	return repo.name;
				    }
				});
				
				payType.val("").trigger("change");
			}
		});
		
		///////初始化弹出框当中的下拉框--业务类型
		$.ajax({
			url : "/admin/reconciliation/typeValue?typeValue=Pay_Business_Type&amp;isIncludeAll=true",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) { 
				changeSelectData(data);
				businessType.select2({
					placeholder: '==请选择类型==',
				    allowClear: true,
				    minimumResultsForSearch: Infinity,
				    data:data,
				    templateResult:function(repo){
				    	return repo.name;
				    },
				    templateSelection:function(repo){
				    	return repo.name;
				    }
				});
				
				businessType.val("").trigger("change");
			}
		});
		
		///////初始化弹出框当中的下拉框--投资银行， 
		$.ajax({
			url : "/admin/reconciliation/typeValue?typeValue=Bank_Type&amp;isIncludeAll=true",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) {  
				changeSelectData(data);
				bankTypeId.select2({
					placeholder: '==请选择类型==',
				    allowClear: true,
				    minimumResultsForSearch: Infinity,
				    data:data,
				    templateResult:function(repo){
				    	return repo.name;
				    },
				    templateSelection:function(repo){
				    	return repo.name;
				    }
				});
				
				bankTypeId.val("").trigger("change");
			}
		});
		
		///////初始化弹出框当中的下拉框--设备编码
		$.ajax({
			url : "/admin/deviceInfo/getDeviceInfos?isIncludeAll=true",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) {
				for (var i = 0; i < data.length; i++) {
                    data[i].value = data[i].deviceNo;
                    data[i].id = data[i].deviceNo;
                };
				deviceNoObj.select2({
					placeholder: '==请选择类型==',
				    allowClear: true,
				    minimumResultsForSearch: Infinity,
				    data:data,
				    templateResult:function(repo){
				    	return repo.deviceNo;
				    },
				    templateSelection:function(repo){
				    	return repo.deviceNo;
				    }
				});
				
				deviceNoObj.val("").trigger("change");
			}
		});
		
		///////初始化弹出框当中的下拉框--支付方式
		$.ajax({
			url : "admin/reconciliation/typeValue?typeValue=handleCode",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) { 
				changeSelectData(data);
				handleCodeObj.select2({
					placeholder: '==请选择类型==',
				    allowClear: true,
				    minimumResultsForSearch: Infinity,
				    data:data,
				    templateResult:function(repo){
				    	return repo.name;
				    },
				    templateSelection:function(repo){
				    	return repo.name;
				    }
				});
				
				handleCodeObj.val("").trigger("change");
			}
		}); 
	}
	
	function initValid() {
		formObjDlg.bootstrapValidator({
			message : '不能为空',
			feedbackIcons : {
				valid : 'glyphicon glyphicon-ok',
				invalid : 'glyphicon glyphicon-remove',
				validating : 'glyphicon glyphicon-refresh'
			},
			fields : {
				platformAmount : {
					validators : {
						notEmpty : {
							message : '金额不能为空'
						},
						stringLength : {
							max : 20,
							message : '最大20字符'
						},
						numeric: {message: '对账时间只能输入数字'}  
					}
				},
				handleCode:{
					validators : {
						notEmpty : {
							message : '处理方式不能为空'
						}
					} 
				}
			}
		})
	}
	
	function formatCheck(val, row, index) {
		var name = '<a href="javascript:" plain="true" onclick="app.admin.detail.check(\''+row.flowNo+'\',\''+row.hisFlowNo +'\',\''+row.payFlowNo +'\')">' + "查看" + "</a>";
		return name;
	}
	
	function resetValidator(){
		formObjDlg.data('bootstrapValidator').destroy();
		formObjDlg.data('bootstrapValidator',null);
		initValid();
	}
	
	function save(){
		formObjDlg.ajaxSubmit(options);
	}
	
	function check(val,hisFlowNo,payFlowNo) {
		
		dlgObj2.modal('show');
		tableDlg.bootstrapTable("load", []);
		$('#timeline').html("");
		var url = '/admin/reconciliation/detail/platlog?flowNo='+val;
		$.get(url, {}, function(result) {
			$("#hisFlowNo").html(hisFlowNo);
			$("#payFlowNo").html(payFlowNo);
			if (result.success) {
				if(null != result.data.goodInfo){
					tableDlg.bootstrapTable("load", result.data.goodInfo);
				}
				for (var i = 0; i < result.data.platLogList.length; i++) {
					if(i%2==0){
						if(result.data.platLogList[i].responseCode=='50'){
							
							var divStr1 = '<li class="event">'+result.data.platLogList[i].tradeDate+'</li>';
							var divStr3 = '<li class="event">"交易来源:"'+ result.data.platLogList[i].tradeFromName+'"，交易目的:"'+ result.data.platLogList[i].tradeToName+'"，交易状态:"'+ result.data.platLogList[i].responseValue+'</li>';
                            var divStr = divStr1 + divStr3;    
                            $('#timeline').append(divStr);
						}else{
							
							var divStr1 = '<li class="event">'+result.data.platLogList[i].tradeDate+'</li>';
							var divStr3 = '<li class="event">"交易来源:"'+ result.data.platLogList[i].tradeFromName+'"，交易目的:"'+ result.data.platLogList[i].tradeToName+'"，交易状态:"'+ result.data.platLogList[i].responseValue+'</li>';
                            var divStr = divStr1 + divStr3; 
							$('#timeline').append(divStr);
						}
					}else{
						if(result.data.platLogList[i].responseCode=='50'){
							var divStr1 = '<li class="event">'+result.data.platLogList[i].tradeDate+'</li>';
							var divStr3 = '<li class="event">"交易来源:"'+ result.data.platLogList[i].tradeFromName+'"，交易目的:"'+ result.data.platLogList[i].tradeToName+'"，交易状态:"'+ result.data.platLogList[i].responseValue+'</li>';
	                        var divStr = divStr1 + divStr3;
							$('#timeline').append(divStr);
						}else{
							var divStr1 = '<li class="event">'+result.data.platLogList[i].tradeDate+'</li>';
							var divStr3 = '<li class="event">"交易来源:"'+ result.data.platLogList[i].tradeFromName+'"，交易目的:"'+ result.data.platLogList[i].tradeToName+'"，交易状态:"'+ result.data.platLogList[i].responseValue+'</li>';
	                        var divStr = divStr1 + divStr3; 
							
							$('#timeline').append(divStr);
						}
					}
				}
			}
		}, 'json');
		
	}
	
	function formatHandler(val, row, index) {
		var id = row.id;
		var handleCode = row.handleCode;
		var remarkInfo = row.remarkInfo;
		var thirdAmount = row.thirdAmount;
		var isDifferent = row.isDifferent;
		var name = '';
		if(isDifferent==1){
			 name = '<a href="javascript:" plain="true" onclick="app.admin.detail.handler('
				+ row.id + ')">' + "处理" + "</a>";
		}else{
			 name = '<a href="javascript:" plain="true" onclick="return false;"  style="cursor: default;">' + "处理" + "</a>";
		}
		return name;
	}
	
	function handler(val) {
		resetValidator();
		//重设弹出框
		formObjDlg.resetForm();
		handleCodeObj.val("").trigger("change");
		
		//显示弹出框
		$('#detailId').val(val);
		dlgObj1.modal('show');
	}
	
	function orgFormatter(val) {
		var orgJSONs = $.parseJSON(orgJSON);
		return orgJSONs[val];
	}
	function formatter(val) {
		var typesJSONs = $.parseJSON(typesJSON);
		return typesJSONs[val]; 
	}
	
	function search(accountOrgNo,accountDate,flag,orgNo) { 
		//控制页面表格显示
		var orgTemp;
		if(null != accountOrgNo){
			orgTemp = accountOrgNo;
		}else{
			if(null != ztreeObj){
				orgTemp = ztreeObj.getVal;
				if (orgTemp === 9999 || orgTemp === null || orgTemp === '') {
					$.NOTIFY.showError("错误", '请选择所属机构!', '');
					return;
				}
			}else{
				orgTemp=orgNo;
			}
		}
		var temFlag = 0;
		//默认查询双方对账
		if(null != orgTemp){
			tableObj = $("#twoDetailDataTable");
			tableObj.parent().css("display","block");
		}else{
			orgTemp=$("#orgNo").val();
		}
		
		if(recType =="three_rec"){
			OrgRecType = "three_rec";
			tableObj = $("#detailDataTable");
			$("#detailDataTableDiv").css("display","block");
			$("#twoDetailDataTableDiv").css("display","none");
		}else{
			tableObj = $("#twoDetailDataTable");
			$("#detailDataTableDiv").css("display","none");
			$("#twoDetailDataTableDiv").css("display","block");
		}
		
		var searchUrl = apiUrl+"/detailTwo";
		if(OrgRecType=="three_rec"){
			searchUrl = apiUrl+"/detail";
		}
		////刷新表格
		tableObj.bootstrapTable('refreshOptions', {
			  pageNumber:1,
			  url:searchUrl,
			  queryParams:function(params){
				  	
				    var queryObj;
				    if(flag == 1){
				    	queryObj ={orgNo:accountOrgNo,startTime:accountDate,endTime:accountDate};
					}else{
						///////参数
						var orgNo;
						if(null != ztreeObj){
							orgNo= ztreeObj.getVal;
						}
						var tradeTypeValue = tradeType.val();
						var flowNo = formObj.find("input[name=flowNo]").val();
						var payTypeValue = payType.val();
						var businessTypeValue = businessType.val();
						var startDate = startobj.val();
						var endDate = endobj.val();
						var bankTypeIdValue = bankTypeId.val();
						var deviceNoValue = deviceNoObj.val();
						
						if((tradeTypeValue != "" && tradeTypeValue != null && tradeTypeValue != undefined)){
							tradeTypeValue = tradeTypeValue.join(",") ;
						}
						if((payTypeValue != "" && payTypeValue != null && payTypeValue != undefined)){
							payTypeValue = payTypeValue.join(",") ;
						}
						queryObj = {orgNo:orgNo,tradeType:tradeTypeValue,flowNo:flowNo,payType:payTypeValue,businessType:businessTypeValue,startTime:startDate,endTime:endDate,bankTypeId:bankTypeIdValue,deviceNo:deviceNoValue};
						
					}
				    var query = $.extend( true, params, queryObj);
	                return query;
	            }
		});
		
	}

	function SerialNumberFormat(value, row, index){
		return index+1;
	}
	
	function moneyFormat(val, row, index){
		return new Number(val).toFixed(2);
	}
	
	return {
		orgFormatter:orgFormatter,
		formatHandler:formatHandler,
		formatCheck:formatCheck,
		formatter:formatter,
		init : init,
		search:search,
		check:check,
		handler:handler,
		save:save,
		exportData:exportData,
		SerialNumberFormat:SerialNumberFormat,
		moneyFormat:moneyFormat
	}
})();





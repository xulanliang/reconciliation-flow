NB.ns("app.admin").onlinerefund = (function() {
	// 表格
	var tableObj = $("#onlinerefundDataTable"); 
	// 请求路径
	var apiUrl = '/admin/onlinerefund';
	// 表单
	var formObj = $("#onlinerefundSearchForm"); 
	// 时间控件
	var startobj = formObj.find("input[name=startTime]");
	var endobj =   formObj.find("input[name=endTime]");
	// 下拉框对象
	var treeInput = formObj.find("input[name=orgNoTree]");
	var treeUrl = '/admin/organization';
	var ztreeObj;
	var tradeType = formObj.find("select[name=tradeType]");
	var businessType = formObj.find("select[name=businessType]");
	var typesJSON ;
	var orgJSON; 
	var statusJSON; 
		
	function init(typesJSON_tmp,orgJSON_tmp,statusJSON_temp,accountDate) { 
		
		///初始化赋值
		typesJSON = typesJSON_tmp;
		orgJSON = orgJSON_tmp;
		statusJSON = statusJSON_temp; 
		 
		/////初始化控件
		initDate(accountDate);  
		initTable();  
		initTree();
		
	}
	
	/////初始化表格
	function initTable(){ 
		tableObj.bootstrapTable({
			url : "",
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
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
	} 
	
	///初始化下拉框
	function initTree() {
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
            			key: 'id', 
            			//是否
            			clearable:true,
                        expandAll:true,
            			data: data
            		}, setting);
            		ztreeObj.updateCode(data[0].id,data[0].code);
            	}
			}
		});
		
		///////初始化弹出框当中的下拉框--支付类型， 
		$.ajax({
			url : "/admin/reconciliation/typeValue?typeValue=Trade_Code",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) {  
				
				tradeType.select2({
					placeholder: '==请选择类型==',
				    allowClear: true,
				    width:'200px',
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
		
		///////初始化弹出框当中的下拉框--业务类型
		$.ajax({
			url : "/admin/reconciliation/typeValue?typeValue=Pay_Business_Type&amp;isIncludeAll=true",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) { 
				businessType.select2({
					placeholder: '==请选择类型==',
				    allowClear: true,
				    width:'200px',
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
	}
	function formatterCheck(val, row, index){
		var name = '<a href="javascript:" plain="true" onclick="app.admin.onlinerefund.check(\''+row.id +'\',\''+row.tradeCode +'\',\''+row.orgNo +'\',\''+row.payShopNo +'\',\''+row.tradeFrom +'\',\''+row.payType +'\',\''+row.payFlowNo +'\',\''+row.thirdAmount +'\',\''+row.oriPayFlowNo +'\',\''+row.deviceNo +'\')">' + "退费" + "</a>";
		return name;
	}
	
	function orgFormatter(val) {
		var orgJSONs = $.parseJSON(orgJSON);
		return orgJSONs[val];
	}
	function formatter(val) {
		var typesJSONs = $.parseJSON(typesJSON);
		return typesJSONs[val]; 
	}
	
	function search() { 
		
		///////参数
		var orgNo= ztreeObj.getVal;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var tradeTypeValue = tradeType.val();
		var businessTypeValue = businessType.val();
		var payTermNo = formObj.find("input[name=payTermNo]").val();
		var startDate = startobj.val();
		var endDate = endobj.val();
		var sysSerial = formObj.find("input[name=sysSerial]").val();
		
		if((tradeTypeValue != "" && tradeTypeValue != null && tradeTypeValue != undefined)){
			tradeTypeValue = tradeTypeValue.join(",") ;
		}
		var queryObj = {orgNo:orgNo,tradeType:tradeTypeValue,sysSerial:sysSerial,businessType:businessTypeValue,startTime:startDate,endTime:endDate,payTermNo:payTermNo};
		
		////刷新表格
		tableObj.bootstrapTable('refreshOptions', {
			  pageNumber:1,
			  url:apiUrl+"/detail",
			  queryParams:function(params){
              	var query = $.extend( true, params, queryObj);
	                return query;
              },
		});
		
	}
	
	function check(id,tradeCode,orgNo,payShopNo,paySource,payType,payFlowNo,thirdAmount,oriPayFlowNo,deviceNo){
		
		var url = '/admin/onlinerefund/plat/refund?Trade_Code='+tradeCode+"&Org_No="+orgNo+"&Pay_Source="+paySource+"&Pay_Type="+payType+"&Pay_Flow_No="+payFlowNo+"&Third_Amount="+thirdAmount+"&Pay_Round="+thirdAmount+"&Ori_Pay_Flow_No="+oriPayFlowNo+"&Device_No="+deviceNo+"&Pay_Shop_No="+payShopNo+"&id="+id;
		$.ajax({
			type: "GET",
			url: url,
			timeout:5000,
			dataType: "json",
			error: function() {
				$.NOTIFY.showError("错误", result.message, '');
			},
			success: function(result) {
				if(result.code==1){
					$.NOTIFY.showError("错误", result.message, '');
				}else{
					$.NOTIFY.showSuccess("提醒", "操作成功", '');
				}
				
				search();
			}
		});
	}
	function SerialNumberFormat(value, row, index){
		return index+1;
	}

	return {
		orgFormatter:orgFormatter,
		formatter:formatter,
		init : init,
		search:search ,
		check :check ,
		formatterCheck:formatterCheck,
		SerialNumberFormat:SerialNumberFormat
	}
})();

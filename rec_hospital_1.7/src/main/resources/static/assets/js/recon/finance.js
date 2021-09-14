NB.ns("app.admin").finance = (function() {
	// 表格
	var tableObj = $("#financeDataTable"); 
	// 请求路径
	var apiUrl = '/admin/finance';
	// 表单
	var formObj = $("#financeSearchForm"); 
	// 时间控件
	var startobj = formObj.find("input[name=startDate]");
	var endobj =   formObj.find("input[name=endDate]");
	// 下拉框对象
	var treeInput = formObj.find("input[name=orgNoTree]");
	var treeUrl = '/admin/organization';
	var ztreeObj;
	var paySource = formObj.find("select[name=paySource]");
	var payType = formObj.find("select[name=payType]");
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
			pageSize:10,
			pageList:[10,20],//分页步进值
			height:680,
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
            			key: 'code', 
            			//是否
            			clearable:true,
                        expandAll:true,
            			data: data
            		}, setting);
            		ztreeObj.updateCode(data[0].id,data[0].code);
            	}
			}
		});
	}
	
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
	
	///////初始化弹出框当中的下拉框--支付来源， 
	$.ajax({
		url : "/admin/reconciliation/typeValue?typeValue=Pay_Source",
		type : "get",
		contentType : "application/json",
		dataType : "json",
		success : function(data) {  
			changeSelectData(data);
			paySource.select2({
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
			
			paySource.val("").trigger("change");
		}
	});
	
	function orgFormatter(val) {
		var orgJSONs = $.parseJSON(orgJSON);
		return orgJSONs[val];
	}
	function formatter(val) {
		var typesJSONs = $.parseJSON(typesJSON);
		return typesJSONs[val]; 
	}
	
	function search() { 
		var orgNoTemp= ztreeObj.getVal;
		if (orgNoTemp === 9999 || orgNoTemp === null || orgNoTemp === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		////刷新表格
		tableObj.bootstrapTable('refreshOptions', {
			  pageNumber:1,
			  url:apiUrl+"/detail",
			  queryParams:function(params){ 
				  
					///////参数
				  	var orgNo= ztreeObj.getVal;
					var businessTypeValue = businessType.val();
					var payTypeValue = payType.val();
					var paySourceValue = paySource.val();
					var startDate = startobj.val();
					var endDate = endobj.val();
				
					if((payTypeValue != "" && payTypeValue != null && payTypeValue != undefined)){
						payTypeValue = payTypeValue.join(",") ;
					}
					
					if((paySourceValue != "" && paySourceValue != null && paySourceValue != undefined)){
						paySourceValue = paySourceValue.join(",") ;
					}
					var queryObj = {orgNo:orgNo,
									businessType:businessTypeValue,
									payType:payTypeValue,
									paySource:paySourceValue,
									startDate:startDate,
									endDate:endDate,
									offset: params.offset,  //页码  
									page: this.pageNumber, 
									rows:this.pageSize
									};
					
				    var query = $.extend( true, params, queryObj);
	                return query;
	            }
		});
	}
	
	function toChange(){
		
		var radioValue = $("input[name='timeFlag']:checked").val();
		if(radioValue==0){//年
			var date = new Date();
			var dateBegin = date.getFullYear()+"-01-01"
			var dateEnd = date.getFullYear()+"-12-31"
			startobj.datepicker('setStartDate', dateBegin);
			endobj.datepicker('setStartDate', dateEnd);
			startobj.val(dateBegin);
			endobj.val(dateEnd);
		}else if(radioValue==1){ //月
		     var date = new Date();
	         var monthDate = new Date(date.getFullYear(), date.getMonth(), 1);
	         var month = monthDate.getMonth() + 1;
	         if (month >= 1 && month <= 9) {
			        month = "0" + month;
			    }
	         var strDate = monthDate.getDate();
	         if (strDate >= 0 && strDate <= 9) {
			        strDate = "0" + strDate;
			    }
	         
	         var ctime = monthDate.getFullYear() + '-' + month+ "-" + strDate;
	         startobj.datepicker('setStartDate', ctime);
	         startobj.val(ctime);
			
	         var MonthNextFirstDay = new Date(date.getFullYear(), date.getMonth() + 1, 1);
	         var lastDay = new Date(MonthNextFirstDay - 86400000);
	         var lastMonth = MonthNextFirstDay.getMonth() ;
	         if (lastMonth >= 1 && lastMonth <= 9) {
	        	 lastMonth = "0" + lastMonth;
			    }
	         var lastDay1 = lastDay.getDate();
	         if(lastDay1>=1&& lastDay1<=9){
	        	 lastDay1 = "0"+lastDay1;
	         }
	         var ctimeone = lastDay.getFullYear() + '-' + lastMonth + '-' + lastDay1;
			 
			 endobj.datepicker('setStartDate', ctimeone);
			 endobj.val(ctimeone);
		}else{ //日
			 var date = new Date();
		    var seperator1 = "-";
		    var month = date.getMonth() + 1;
		    var strDate = date.getDate()-1;
		    if (month >= 1 && month <= 9) {
		        month = "0" + month;
		    }
		    if (strDate >= 0 && strDate <= 9) {
		        strDate = "0" + strDate;
		    }
		    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate;
			 startobj.datepicker('setStartDate', currentdate);
			 endobj.datepicker('setStartDate', currentdate);
			 startobj.val(currentdate);
			 endobj.val(currentdate);
		}
	}
	
	function exportData() {
		
		var orgNo= ztreeObj.getVal;
		var orgName=ztreeObj.getText;
		var payTypeValue = payType.val();
		var businessTypeValue = businessType.val();
		var dataSourceValue = $('#dataSource').val();
		var startDate = startobj.val();
		var endDate = endobj.val();
		var paySourceValue = paySource.val();
		bootbox.confirm('确定执行此操作?',function(r){
            if (r){
            	
            	var where = '/admin/finance/api/dcExcel?a=1';
        		
        		if((orgNo != "" && orgNo != null && orgNo != undefined)){
        			where = where + '&orgNo=' + orgNo ;
        		}
        		if((orgName != "" && orgName != null && orgName != undefined)){
        			where = where + '&orgName=' + orgName ;
        		}
        		if((payTypeValue != "" && payTypeValue != null && payTypeValue != undefined)){
        			where = where + '&payType=' + payTypeValue ;
        		}
        		if(businessTypeValue != "" && businessTypeValue != null && businessTypeValue != undefined){
        			where = where + '&businessType=' + businessTypeValue ;
        		}
        		if(dataSourceValue != "" && dataSourceValue != null && dataSourceValue != undefined){
        			where = where + '&dataSource=' + dataSourceValue ;
        		}
        		if((startDate != "" && startDate != null && startDate != undefined)){
        			where = where + '&startDate=' + startDate ;
        		}
        		if((endDate != "" && endDate != null && endDate != undefined)){
        			where = where + '&endDate=' + endDate ;
        		}
        		if((paySourceValue != "" && paySourceValue != null && paySourceValue != undefined)){
        			where = where + '&paySource=' + paySourceValue ;
        		}
        		where = where + '&t='+new Date().getTime();
        		
        		window.location.href=where;
            }
        });
		
	}
	
	function SerialNumberFormat(value, row, index) {
		var pageSize=tableObj.bootstrapTable('getOptions').pageSize;//通过表的#id 可以得到每页多少条
        var pageNumber=tableObj.bootstrapTable('getOptions').pageNumber;//通过表的#id 可以得到当前第几页
        return pageSize * (pageNumber - 1) + index + 1;    //返回每条的序号： 每页条数 * （当前页 - 1 ）+ 序号
	}
	
	function moneyFormat(val, row, index){
		return new Number(val).toFixed(2);
	}

	return {
		orgFormatter:orgFormatter,
		formatter:formatter,
		init : init,
		search:search,
		exportData:exportData,
		toChange:toChange,
		SerialNumberFormat:SerialNumberFormat,
		moneyFormat:moneyFormat
	}
})();

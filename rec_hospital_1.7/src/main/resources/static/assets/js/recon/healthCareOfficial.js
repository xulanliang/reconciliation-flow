NB.ns("app.admin").healthCareOfficial = (function() {
	//表格
	var tableObj =$("#healthCareOfficialDataTable");
	//表单
	var formObj = $("#healthCareOfficialSearchForm");

	// 时间控件
	var rangeTimeObj = $("#healthCareOfficialTime");
	
	var operationTypeObj = formObj.find("select[name=operationType]");
	var healthcareTypeCodeObj = formObj.find("select[name=healthcareTypeCode]");
	var treeInput = formObj.find("input[name=orgNoTree]");
	var typesJSON;
	var orgJSON;
	var ztreeObj;
	
	//请求路径
	var apiUrl = '/admin/healthCareOfficial/data';
	var treeUrl = '/admin/organization';
	
	//初始化
	function init(typesJSON_temp,orgJSON_temp,accountDate) {
		//初始化赋值
		typesJSON = typesJSON_temp;
		orgJSON = orgJSON_temp;
		
		//初始化时间
		initDate(accountDate);
		//初始化下拉框
		initSelect();
		//初始化表格
		initTable();
		//查询
		search(accountDate,1);
	}
	
	function initSelect(){
		//组织
		formaterOrgProps();
		//医保
		healthcareTypeData();
		//操作类型
		operationTypeData();
	}
	
	function initTable(){
		tableObj.bootstrapTable({
			url : '',
			dataType : "json",
			uniqueId : "id",
			resizable: true,
			singleSelect : true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
		}); 
	}
	
	/**
	 * 初始化日期
	 */
	function initDate(accountDate) {
		var beginTime = accountDate;
		var endTime = accountDate;
		var nowDate = new Date();
		var rangeTime = beginTime + " ~ " + endTime;
		var startLayDate = laydate.render({
			elem : '#healthCareOfficialTime',
			btns: ['confirm'],
			theme : '#A9BCF5',
			type: "date",
			value: rangeTime,
			format:"yyyy-MM-dd",
			range:"~",
			max: nowDate.getTime(),
			ready: function(date){
				var layym = $(".laydate-main-list-0 .laydate-set-ym span").eq(0).attr("lay-ym").split("-")[1];
				if(layym == (nowDate.getMonth()+1)){
					$(".laydate-main-list-0 .laydate-prev-m").click();
				}
			}
		});
	}
	
	var options = {
            beforeSubmit:  function(formData, jqForm, options){
            	 var flg = formObj.data('bootstrapValidator');
			     return flg.validate().isValid();
            },
            success: function(result){
            	if(result.success){
            		$.NOTIFY.showSuccess ("提醒", "操作成功",'');
            		reflush();
            	}else{
            		if(result.message){
            			$.NOTIFY.showError  ("错误", result.message,'');
            		}
            	}
            },
            url:       apiUrl ,      
            type:      'post', 
            dataType:  'json',
            clearForm: false ,       
            resetForm: false ,       
            timeout:   3000 
    };
	
	//导出
	function exportData() {
		var orgNo;
		if(null != ztreeObj){
			orgNo= ztreeObj.getVal;
		}
		var operationTypeValue = operationTypeObj.val();
		var healthcareTypeCodeValue = healthcareTypeCodeObj.val();
		var payFlowNo = formObj.find("input[name=payFlowNo]").val();
		var businessCycleNo = formObj.find("input[name=businessCycleNo]").val();
		
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		}
		
		if((operationTypeValue != "" && operationTypeValue != null && operationTypeValue != undefined)){
			operationTypeValue = operationTypeValue.join(",") ;
		}
		if((healthcareTypeCodeValue != "" && healthcareTypeCodeValue != null && healthcareTypeCodeValue != undefined)){
			healthcareTypeCodeValue = healthcareTypeCodeValue.join(",") ;
		}
		
		bootbox.confirm('确定执行此操作?',function(r){
            if (r){
            	var where = apiUrl+'/dcExcel?a=1'; 
        		
        		if((orgNo != "" && orgNo != null && orgNo != undefined)){
        			where = where + '&orgNo=' + orgNo ;
        		}
        		if((operationTypeValue != "" && operationTypeValue != null && operationTypeValue != undefined)){
        			where = where + '&operationType=' + operationTypeValue ;
        		}
        		if((healthcareTypeCodeValue != "" && healthcareTypeCodeValue != null && healthcareTypeCodeValue != undefined)){
        			where = where + '&healthcareTypeCode=' + healthcareTypeCodeValue ;
        		}
        		if((payFlowNo != "" && payFlowNo != null && payFlowNo != undefined)){
        			where = where + '&payFlowNo=' + payFlowNo ;
        		}
        		if(businessCycleNo != "" && businessCycleNo != null && businessCycleNo != undefined){
        			where = where + '&businessCycleNo=' + businessCycleNo ;
        		}
        		if((startDate != "" && startDate != null && startDate != undefined)){
        			where = where + '&startTime=' + startDate ;
        		}
        		if((endDate != "" && endDate != null && endDate != undefined)){
        			where = where + '&endTime=' + endDate ;
        		}
        		where = where + '&t='+new Date().getTime();
                window.location.href=where;
            }
        });
	}
	//查询
	function search(accountDate,flag, th) {
		if(null == ztreeObj){
			return;
		}
		var orgNoTemp = ztreeObj.getVal;
		if (orgNoTemp === 9999 || orgNoTemp === null || orgNoTemp === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		//刷新表格
		tableObj.bootstrapTable('refreshOptions', {
			  pageNumber:1,
			  url:apiUrl,
			  queryParams:function(params){
				  	var queryObj;
				    if(flag == 1){
				    	queryObj ={startTime:accountDate,endTime:accountDate};
					}else{ 
						///////参数
						var orgNo;
						if(null != ztreeObj){
							orgNo= ztreeObj.getVal;
						}
					    
						var operationTypeValue = operationTypeObj.val();
						var healthcareTypeCodeValue = healthcareTypeCodeObj.val();
						var payFlowNo = formObj.find("input[name=payFlowNo]").val();
						var businessCycleNo = formObj.find("input[name=businessCycleNo]").val();

						var startDate ;
						var endDate  ;
						var rangeTime = rangeTimeObj.val();
						if(rangeTime){
							startDate = rangeTime.split("~")[0];
							endDate = rangeTime.split("~")[1];
						}
						
						if((operationTypeValue != "" && operationTypeValue != null && operationTypeValue != undefined)){
							operationTypeValue = operationTypeValue.join(",") ;
						}
						if((healthcareTypeCodeValue != "" && healthcareTypeCodeValue != null && healthcareTypeCodeValue != undefined)){
							healthcareTypeCodeValue = healthcareTypeCodeValue.join(",") ;
						}
						queryObj = {orgNo:orgNo,operationType:operationTypeValue,healthcareTypeCode:healthcareTypeCodeValue,payFlowNo:payFlowNo,businessCycleNo:businessCycleNo,startTime:startDate,endTime:endDate};
						
					}
				    var query = $.extend( true, params, queryObj);
	                return query;
	            },
				onPreBody:function(data){
	            	$(th).button('loading');
	            },
	            onLoadSuccess:function(data){
	            	$(th).button("reset");
		        }
		});
		
	}
	
	//机构数
	function formaterOrgProps(){
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
	
	//操作类型
	function operationTypeData(){
		$.ajax({
			url : "/admin/reconciliation/typeValue?typeValue=Trade_Type",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) { 
				//将id换成value
				for(var i=0;i<data.length;i++){
					data[i].id = data[i].value;
				}
				//初始化
				operationTypeObj.select2({
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
				operationTypeObj.val("").trigger("change");
			}
		}); 
	}
	
	//医保类型
	function healthcareTypeData(){
		$.ajax({
			url : "/admin/reconciliation/typeValue?typeValue=healthcare_type",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) { 
				//将id换成value
				for(var i=0;i<data.length;i++){
					data[i].id = data[i].value;
				}
				//初始化
				healthcareTypeCodeObj.select2({
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
				healthcareTypeCodeObj.val("").trigger("change");
			}
		}); 
	}
	
	function orgFormatter(val) {
		var orgJSONParse = JSON.parse(orgJSON);
		return orgJSONParse[val];
	}
	function formatter(val) {
		var typesJSONParse = JSON.parse(typesJSON);
		return typesJSONParse[val];
	}
	
	function number(value, row, index) {
	 	   var pageSize=tableObj.bootstrapTable('getOptions').pageSize;
	        var pageNumber=tableObj.bootstrapTable('getOptions').pageNumber;
	        return pageSize * (pageNumber - 1) + index + 1;
	}
	function moneyFormat(val, row, index){
		return new Number(val).toFixed(2);
	}
	
	return {
		init : init,
		search:search,
		exportData:exportData,
		formatter:formatter,
		orgFormatter:orgFormatter,
		number:number,
		moneyFormat:moneyFormat
	}
})();
 


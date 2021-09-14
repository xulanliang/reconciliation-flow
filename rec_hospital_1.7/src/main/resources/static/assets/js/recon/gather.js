NB.ns("app.admin").gather = (function() {
	// 表格
	var tableObjAll = $("#gatherDataTableAll");
	var tableObjOne = $("#gatherDataTableOne");
//	var tableObjCount = $("#gatherDataTableCount");
	// 请求路径
	var apiUrl = '/admin/reconciliation/gather';
	// 组织机构url
	var orgUrl = '/admin/organization';
	// 表单
	var formObj = $("#gatherSearchForm");
	// 时间控件
	var dateObj = formObj.find("input[name=searchDate]");
	// 下拉框对象
	var treeInput = formObj.find("input[name=parent1]");
	// 树形下拉框对象
	var ztreeObj;
	var typesJSON;
	var flag = true;////是否第一进入的标识
	
	function reflush() {
		tableObj.bootstrapTable('refresh');
	}
		
	function init( typesJSON_temp,accountDate ) {
		
		/////初始化后台值
		typesJSON = typesJSON_temp;
		
		////初始化控件
		initDate(accountDate);
		initTree();
		initTable(null,accountDate);
	}
	
	function initTable(orgNo,searchDate){
		var url = apiUrl + "/collect?a=1";
		
		if((orgNo != "" && orgNo != null && orgNo != undefined)){
			url = url + "&orgNo=" + orgNo ;
		}
		if(searchDate != "" && searchDate != null && searchDate != undefined){
			url = url + "&payDate=" + searchDate ;
		}
				
        if(flag){
        	tableObjAll.bootstrapTable({
				striped : false,
				resizable: true,
				sidePagenation : 'server',
				uniqueId : "id",
				treeShowField : 'dataSource',
				parentIdField : 'parent',
				data: []
			});
        	
        	tableObjAll.treegrid({
				initialState : 'expanded',// 收缩 expanded,collapsed
				treeColumn : 0,// 指明第几列数据改为树形
				expanderExpandedClass : 'glyphicon glyphicon-triangle-bottom',
				expanderCollapsedClass : 'glyphicon glyphicon-triangle-right',
				onChange : function() {
					tableObjAll.bootstrapTable('resetWidth');
				}
			});
        	
			////表格初始化
			tableObjOne.bootstrapTable({
				dataType : "json",
				uniqueId : "id",
				singleSelect : true,
				resizable: true,
				pagination : true, // 是否分页
				pageSize:10,
				data: [],
				pageList:[10,30]//分页步进值
//				height:150
			});
			
			////表格初始化
//			tableObjCount.bootstrapTable({
//				dataType : "json",
//				uniqueId : "id",
//				singleSelect : true,
//				pagination : true, // 是否分页
//				pageSize:10,
//				data: [],
//				pageList:[10,30],//分页步进值
//				height:260
//			});
			
			flag = false;
        }else{
        	$.ajax({
    			url : url,
    			contentType : "application/json",
    			dataType : "json",
    			success : function(result) { 
    				
    				var dataList = result.data.gatherList;
    				var totalList = [];
    				if(dataList != null && dataList.length>0){
    					for(var i=0;i<dataList.length;i++){
    						var obj = dataList[i];
    						if(obj.parent=="root"){
    							dataList[i].parent = null;
    							totalList.push(obj);
    						}
    					}
    				}
    				if (result.success) {
                    	tableObjAll.bootstrapTable("load",dataList);
                    	tableObjAll.treegrid({
							initialState : 'expanded',// 收缩 expanded,collapsed
							treeColumn : 0,// 指明第几列数据改为树形
							expanderExpandedClass : 'glyphicon glyphicon-triangle-bottom',
							expanderCollapsedClass : 'glyphicon glyphicon-triangle-right',
							onChange : function() {
								tableObjAll.bootstrapTable('resetWidth');
							}
						});
                    	tableObjOne.bootstrapTable("load",result.data.singleList);
//                    	tableObjCount.bootstrapTable("load",totalList);
    				}
			}
        	}
		);
	}
	}
	
	function initDate(accountDate){
		///时间控件初始化
		dateObj.datepicker({
			minView : "month",
			language : 'zh-CN',
			format : 'yyyy-mm-dd',
			autoclose : true,
			clearBtn: true,
			todayHighlight: true, 
			pickerPosition : "bottom-left"
				
		});
		
		////控件赋值
		dateObj.datepicker("setDate", accountDate);
	}
	
	function initTree(){
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
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "parent",
					rootPId : null
				}
			}
		};
		
		$.ajax({
			url : orgUrl,
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
	
	function search() {
		var orgNo = ztreeObj.getVal;
		var orgName = ztreeObj.getText;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var payDate = dateObj.val();
		if(null == payDate || "" == payDate){
			$.NOTIFY.showError("错误", "请选择日期", '');
			return;
		}
		var orgNo = ztreeObj.getVal;
		var searchDate = dateObj.val();
		initTable(orgNo,searchDate);
	}
	
	function formatter(val) {
		var typesJSONs = $.parseJSON(typesJSON);
		var name = typesJSONs[val];
		if(name == undefined){
			var payType = val.substring(0,4);
			var billSource = val.substring(4);
			return typesJSONs[billSource]+ " - " + typesJSONs[payType];
		}
		return name;
	}
	
	function receiptFormatter(index,row){
		return row.paySum - Math.abs(row.refundSum);
	}
	
	function receiptAmountFormatter(index,row){
		var subtractAmount = (row.payAmount - row.refundAmount);
		return new Number(subtractAmount).toFixed(2);
//		return decimal((row.payAmount - row.refundAmount),3);
	}
	
	//对多位小数进行四舍五入
	//num是要处理的数字  v为要保留的小数位数
	function decimal(num,v){
		var vv = Math.pow(10,v);
		return Math.round(num*vv)/vv;
	}
	
	function formatOpt(index,row) {
		
		if(row.count != 0){
			return "<a href='javascript:;' onclick='app.admin.gather.handler()' class='btn btn-info btn-sm m-primary' style='color:green'> 详情 </a>" ;
		}else{
			return "<a href='javascript:;' onclick='return false;' class='btn btn-info btn-sm m-primary disabled'> 详情 </a>" ;
		}
	}
	
	function exportData() {
		var orgNo = ztreeObj.getVal;
		var orgName = ztreeObj.getText;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var payDate = dateObj.val();
		if(null == payDate || "" == payDate){
			$.NOTIFY.showError("错误", "请选择日期", '');
			return;
		}
		bootbox.confirm('确定执行此操作?',function(r){
            if (r){
            	
            	var where = '/admin/reconciliation/gather/api/dcExcel?a=1';
        		
        		if((orgNo != "" && orgNo != null && orgNo != undefined)){
        			where = where + '&orgNo=' + orgNo ;
        		}
        		if((orgName != "" && orgName != null && orgName != undefined)){
        			where = where + '&orgName=' + orgName ;
        		}
        		if(payDate != "" && payDate != null && payDate != undefined){
        			where = where + '&payDate=' + payDate ;
        		}
        		where = where + '&t='+new Date().getTime();
        		
        		window.location.href=where;
            }
        });

	}
	
	function handler() {
		var orgNo = ztreeObj.getVal;
		var payDate = dateObj.val();
		
		var url = "/admin/reconciliation/detail?aa=1" ; 
		if((orgNo != "" && orgNo != null && orgNo != undefined)){
			url = url + '&orgNo=' + orgNo ;
		}
		
		if((payDate != "" && payDate != null && payDate != undefined)){
			url = url + '&payDate=' + payDate ;
		}
		
		$.AdminLTE.sidebar.addTab("对账明细查询",url,null);
	}
	
	function moneyFormat(val, row, index){
		return new Number(val).toFixed(2);
	}

	return {
		init : init,
		search:search,
		formatter:formatter,
		formatOpt:formatOpt,
		exportData:exportData,
		handler:handler,
		receiptFormatter:receiptFormatter,
		receiptAmountFormatter:receiptAmountFormatter,
		moneyFormat:moneyFormat
	}
})();

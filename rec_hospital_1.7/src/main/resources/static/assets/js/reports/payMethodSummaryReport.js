NB.ns("app.admin").payMethodSummaryReport = (function() {
	//表格
	var tableObj =$("#payMethodSummaryDataTable");
	//表单
	var formObj = $("#payMethodSummarySearchForm");
	
	// 时间控件
	var startobj = formObj.find("input[name=startTime]");
	var endobj =   formObj.find("input[name=endTime]");
	//请求路径
	var apiUrl = '/admin/payMethodSummaryReports/data';
	var payMethodSummaryTree;
	
	//导出
	function exportData() {
		
		var starttime = startobj.val();
  	   var endtime = endobj.val();
  	   var treeName = payMethodSummaryTree.getText;
		
		var name = starttime +"至"+ endtime+treeName+"支付方式汇总";
		
		tableObj.tableExport({
			type:"excel",
			fileName: name,
			escape:"false",
		});
	}
	//查询
	function search(){
		var orgNo= payMethodSummaryTree.getVal;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		if(orgNo=="全部" || orgNo==null )orgNo="";
		var startDate='';
		if(startobj.val()!=null && startobj.val()!=''){
			startDate = startobj.val();
		}
		var endDate='';
		if(endobj.val()!=null && endobj.val()!=''){
			endDate = endobj.val();
		}
		$.ajax({
			   url:apiUrl,
	           type:"get",
	           data:{
	        	   orgNo:payMethodSummaryTree.getVal,
	        	   startTime:startDate,
	        	   endTime:endDate
	           },
	           contentType:"application/json",
	           dataType:"json",
	           success:function(msg){
	        	   tableObj.bootstrapTable('refreshOptions',{
		        		data:msg.data,
		        		dataType : "json",
		       			uniqueId : "id",
		       			singleSelect : true
	       			});
	        	   
	        	   //设置表格标题
	        	   var starttime = startobj.val();
 	        	   var endtime = endobj.val();
 	        	   var treeName = payMethodSummaryTree.getText;
 	        	   setTableTitle(tableObj,"支付方式汇总",starttime,endtime,treeName);
	           }
	    });
	}
	//机构树
	function formaterOrgProps(){
		var setting = {
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
           url:"/admin/organization/data",
           type:"get",
           contentType:"application/json",
           dataType:"json",
           success:function(msg){
        	 //这个树形下拉会在和其同级位置生成一个默认隐藏的input输入框以提供表单数据
        	   payMethodSummaryTree = $("#payMethodSummaryTree").ztreeview({
	       			name: 'name',
	       			key: 'code', 
	       			//是否
	       			clearable:true,
	                expandAll:true,
	       			data: msg
	       		}, setting);
        	   payMethodSummaryTree.updateCode(msg[0].id,msg[0].code);
        	   var startDate = startobj.val();
       		   var endDate = endobj.val();
	       	   $.ajax({
	 	           url:apiUrl,
	 	           data:{
		        	   orgNo:payMethodSummaryTree.getVal,
		        	   startTime:startDate,
		        	   endTime:endDate
		           },
	 	           type:"get",
	 	           contentType:"application/json",
	 	           dataType:"json",
	 	           success:function(msg){
	 	        	   if(msg.data==null){
	 	        		  tableObj.bootstrapTable({
		 		        		dataType : "json",
		 		       			uniqueId : "id",
		 		       			singleSelect : true
		 	       			});
	 	        	   }else{
	 	        		  tableObj.bootstrapTable({
		 		        		data:msg.data,
		 		        		dataType : "json",
		 		       			uniqueId : "id",
		 		       			singleSelect : true
		 	       			});
	 	        	   }
	 	        	   
	 	        	   //设置表格标题
	 	        	   var starttime = startobj.val();
	 	        	   var endtime = endobj.val();
	 	        	   var treeName = payMethodSummaryTree.getText;
	 	        	   setTableTitle(tableObj,"支付方式汇总",starttime,endtime,treeName);
	 	           }
	       	   });
	        	   
           }
       });
	}
	
	function orgFormatter(val) {
		var org = $('#payMethodOrgNo').val();
		var orgJSON = JSON.parse(org); 
		return orgJSON[val];
	}
	function formatter(val) {
		var typeJSON = $('#payMethodType').val();
		var typesJSON = JSON.parse(typeJSON); 
		return typesJSON[val];
	}
	
	function init() {
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
		startobj.datepicker("setDate", $("#payMethodDate").val());
		endobj.datepicker("setDate", $("#payMethodDate").val());
		formaterOrgProps();
	}
	function number(value, row, index) {
	 	   var pageSize=tableObj.bootstrapTable('getOptions').pageSize;
	        var pageNumber=tableObj.bootstrapTable('getOptions').pageNumber;
	        return pageSize * (pageNumber - 1) + index + 1;
	}
	function moneyFormat(val, row, index){
	    return new Number(val).toFixed(2);
	  }
	function setBold(val,row,index,field){
		if((index+1)%7==0){
			var style ={css:{'font-weight':'bolder','background-color':'RGB(22,209,141)'}};                
			return style;
		}else{
			var style ={};
			return style;
		}
	}
	
	return {
		init : init,
		search:search,
		exportData:exportData,
		formatter:formatter,
		orgFormatter:orgFormatter,
		number:number,
		moneyFormat:moneyFormat,
		setBold:setBold
	}
})();
NB.ns("app.admin").summaryByPayBusinessType= (function() {
	// 表格
	var tableObj = $("#summaryByPayBusinessTypeDataTable");
	// 请求路径
	var apiUrl = '/admin/SummaryByPayBusinessType/data';
	// 表单
	var formObj = $("#summaryByPayBusinessTypeSearchForm");
	// 时间控件
	var startobj = formObj.find("input[name=beginTime]");
	var endobj =   formObj.find("input[name=endTime]");
	
	function reflush() {
		tableObj.bootstrapTable('refresh');
	}
	
	//导出列表
	function exportList(){
		
		var starttime = startobj.val();
		var endtime = endobj.val();
		var treeName = summaryByPayBusinessTypeTree.getText;
		
		var name = starttime +"至"+ endtime+treeName+"收费员业务汇总";
		
		tableObj.tableExport({
			type:"excel",
			fileName: name,
			escape:"false",
		});
	}
	function initOrgTree(orgNo,tradeDate){
		
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
           success:function(data){
        	   
	        	////表格初始化
	       		tableObj.bootstrapTable({
	       			url : apiUrl,
	       			dataType : "json",
	       			uniqueId : "id",
	       			singleSelect : false,
	       			clickToSelect:true,
	       			pagination : false, // 是否分页
	       			sidePagination : 'server',// 选择服务端分页
	       			height: $(window).height()-360,
	       			pageSize:40,
	       			pageList:[30,50]//分页步进值
	       		});
        	   
        	   summaryByPayBusinessTypeTree = $("#summaryByPayBusinessTypeOrgSelect").ztreeview({
		       			name: 'name',
		       			key: 'code', 
		       			//是否
		       			clearable:true,
		                expandAll:true,
		       			data: data
		       }, setting);
        	   summaryByPayBusinessTypeTree.updateCode(data[0].id,data[0].code);
	           	if((orgNo != "" && orgNo != null && orgNo != undefined)){
					for(var i=0;i<data.length;i++){
						if(orgNo == data[i].code){
							summaryByPayBusinessTypeTree.updateCode(data[i].id,data[i].code);
						}
					}
				}
	           	var treeName = summaryByPayBusinessTypeTree.getText;
	    		setTableTitle(tableObj,"收费员业务汇总",tradeDate,tradeDate,treeName);
           }
       });
	}
	
	function init(orgNo,tradeDate) {
		initDate(tradeDate);
		initOrgTree(orgNo,tradeDate);
		resetTableHeight(tableObj, 360);
	}
	
	function search() {
		tableObj.bootstrapTable('refreshOptions', {
			  pageNumber:1,
			  queryParams:function(params){
				    var queryObj =formObj.serializeObject();
	                var query = $.extend( true, params, queryObj);
	                return query;
	            }
		});
		
		var starttime = startobj.val();
		var endtime = endobj.val();
		var treeName = summaryByPayBusinessTypeTree.getText;
		setTableTitle(tableObj,"收费员业务汇总",starttime,endtime,treeName);
	}
	
	function otherAcount(val, row, index){
		var count;
		count = row.allAcount - row.registerAcount - row.makeAppointmentAcount - row.payAcount - row.clinicAcount - row.prepaymentForHospitalizationAcount;
		if(isNaN(count)){
			count = 0;
		}
		if(row.terminalNo == "合计"){
			var html = "<span style='font-weight: bold;'>"+count+"</span>";
			return html;
		}
		return count;
	}
	
	function otherAmount(val, row, index){
		var amount;
		amount = row.allAmount - row.registerAmount - row.makeAppointmentAmount - row.payAmount - row.clinicAmount - row.prepaymentForHospitalizationAmount;
		if(isNaN(amount)){
			amount = 0;
		}
		if(row.terminalNo == "合计"){
			var html = "<span style='font-weight: bold;'>"+new Number(amount).toFixed(2)+"</span>";
			return html;
		}
		return new Number(amount).toFixed(2);
	}
	
	function moneyFormat(val, row, index){
		
		if(row.terminalNo == "合计"){
			var html = "<span style='font-weight: bold;'>"+new Number(val).toFixed(2)+"</span>";
			return html;
		}
		return new Number(val).toFixed(2);
	}
	
	function normal(val, row, index){
		if(val == null){
			val = 0 ;
		}
		if(row.terminalNo == "合计"){
			var html = "<span style='font-weight: bold;'>"+val+"</span>";
			return html;
		}
		return val;
	}
	
	/////初始化时间
	function initDate(tradeDate){
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
		
//		////控件赋值
		startobj.datepicker("setDate", tradeDate);
		endobj.datepicker("setDate", tradeDate);
	} 

	return {
		init : init,
		search:search,
		exportList:exportList,
		otherAcount:otherAcount,
		otherAmount:otherAmount,
		moneyFormat:moneyFormat,
		normal:normal
	}
})();

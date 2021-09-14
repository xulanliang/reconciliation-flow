NB.ns("app.admin").summaryByPayType= (function() {
	// 表格
	var tableObj = $("#summaryByPayTypeDataTable");
	// 请求路径
	var apiUrl = '/admin/SummaryByPayType/data';
	// 表单
	var formObj = $("#summaryByPayTypeSearchForm");
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
		var treeName = summaryByPayTypeTree.getText;
		
		var name = starttime +"至"+ endtime+treeName+"收费员支付方式汇总";
		
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
       			height: $(window).height()-390,
       			pageSize:40,
       			pageList:[30,50]//分页步进值
       		});
       		resetTableHeight(tableObj, 390);
       		
        	   
        	   summaryByPayTypeTree = $("#summaryByPayTypeOrgSelect").ztreeview({
		       			name: 'name',
		       			key: 'code', 
		       			//是否
		       			clearable:true,
		                expandAll:true,
		       			data: data
		       		}, setting);
        	   summaryByPayTypeTree.updateCode(data[0].id,data[0].code);
	           	if((orgNo != "" && orgNo != null && orgNo != undefined)){
					for(var i=0;i<data.length;i++){
						if(orgNo == data[i].code){
							summaryByPayTypeTree.updateCode(data[i].id,data[i].code);
						}
					}
				}
	           	var treeName = summaryByPayTypeTree.getText;
	           	setTableTitle(tableObj,"收费员支付方式",tradeDate,tradeDate,treeName);
           }
       });
	}
	
	function init(orgNo,tradeDate) {
		initDate(tradeDate);
		initOrgTree(orgNo,tradeDate);
		
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
		
		//设置表格时间
		var starttime = startobj.val();
		var endtime = endobj.val();
		var treeName = summaryByPayTypeTree.getText;
		setTableTitle(tableObj,"日期汇总",starttime,endtime,treeName);
	}
	
	function otherAcount(val, row, index){
		var count;
		count = row.allAcount - row.wechatAcount - row.zfbAcount - row.bankAcount - row.cashAcount - row.unionAcount;
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
		amount = Math.abs(row.allAmount) - Math.abs(row.wechatAmount) - Math.abs(row.zfbAmount) - Math.abs(row.bankAmount) - Math.abs(row.cashAmount) - Math.abs(row.unionAmount);
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

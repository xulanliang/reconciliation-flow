NB.ns("app.admin").summaryByDay= (function() {
	// 表格
	var tableObj = $("#summaryByDayDataTable");
	// 请求路径
	var apiUrl = '/admin/summaryByDay/data';
	// 表单
	var formObj = $("#summaryByDaySearchForm");
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
		var treeName = summaryByDayTree.getText;
		
		var name = starttime +"至"+ endtime+treeName+"日期业务汇总";
		
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
        	   summaryByDayTree = $("#summaryByDayOrgSelect").ztreeview({
		       			name: 'name',
		       			key: 'code', 
		       			//是否
		       			clearable:true,
		                expandAll:true,
		       			data: data
		       }, setting);
        	   summaryByDayTree.updateCode(data[0].id,data[0].code);
	           	if((orgNo != "" && orgNo != null && orgNo != undefined)){
					for(var i=0;i<data.length;i++){
						if(orgNo == data[i].code){
							summaryByDayTree.updateCode(data[i].id,data[i].code);
						}
					}
				}
	           	var treeName = summaryByDayTree.getText;
	    		setTableTitle(tableObj,"日期汇总",tradeDate,tradeDate,treeName);
           }
       });
	}
	
	function init(orgNo,tradeDate) {
		initDate(tradeDate);
		////表格初始化
		tableObj.bootstrapTable({
			url : apiUrl,
			dataType : "json",
			uniqueId : "id",
			singleSelect : false,
			clickToSelect:true,
			pagination : false, // 是否分页
			sidePagination : 'server',// 选择服务端分页
			pageSize:40,
			pageList:[30,50]//分页步进值
		});
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
		var treeName = summaryByDayTree.getText;
		setTableTitle(tableObj,"日期汇总",starttime,endtime,treeName);
	}
	
	function otherMoneyFormat(val, row, index){
		var amount;
		amount = row.otherAllAmount - row.otherRegisterAmount - row.otherRechargeAmount - row.otherPayAmount ;
		if(isNaN(amount)){
			amount = 0;
		}
		if(row.tradeTime == "合计"){
			var html = "<span style='font-weight: bold;'>"+new Number(amount).toFixed(2)+"</span>";
			return html;
		}
		return new Number(amount).toFixed(2);
	}
	
	function moneyFormat(val, row, index){
		if(row.tradeTime == "合计"){
			var html = "<span style='font-weight: bold;'>"+new Number(val).toFixed(2)+"</span>";
			return html;
		}
		return new Number(val).toFixed(2);
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
	
	function get7DaysBefore(date){
		// 获取前一个星期
	    var date = date || new Date(),
	        timestamp, newDate;
	    if(!(date instanceof Date)){
	        date = new Date(date.replace(/-/g, '/'));
	    }
	    timestamp = date.getTime();
	    newDate = new Date(timestamp - 7 * 24 * 3600 * 1000);
	    var t7 = [[newDate.getFullYear(), newDate.getMonth() + 1, newDate.getDate()].join('-')];
	    startobj.datepicker("setDate", t7);
	    //查询
	    search();
	}
	
	function getPreThMonth() {
		// 获取前三个月
		var date = getNowFormatDate();
	    var arr = [];
	    var year = null; //获取当前日期的年份
	    var month = null; //获取当前日期的月份
	    var day = null; //获取当前日期的日
	    if(typeof(date) == "string"){
	        arr = date.split('-');
	        year = arr[0]; //获取当前日期的年份
	        month = arr[1]; //获取当前日期的月份
	        day = arr[2]; //获取当前日期的日
	    }else if(typeof(date) == "date"){
	        year = nowDate.getFullYear(); //获取当前日期的年份
	        month = nowDate.getMonth(); //获取当前日期的月份
	        day = nowDate.getDate(); //获取当前日期的日
	    }

	    var days = new Date(year, month, 0);
	    days = days.getDate(); //获取当前日期中月的天数
	    var year2 = year;
	    var month2 = parseInt(month) - 3;
	    if (month2 == 0) {
	        year2 = parseInt(year2) - 1;
	        month2 = 12;
	    }
	    var day2 = day;
	    var days2 = new Date(year2, month2, 0);
	    days2 = days2.getDate();
	    if (day2 > days2) {
	        day2 = days2;
	    }
	    if (month2 < 10) {
	        month2 = '0' + month2;
	    }
	    var t3 = year2 + '-' + month2 + '-' + day2;
	    startobj.datepicker("setDate", t3);
	    
	    //查询
	    search();
	}
	
	function getNowFormatDate() {
	    var date = new Date();
	    var seperator1 = "-";
	    var seperator2 = ":";
	    var year = date.getFullYear();
	    var month = date.getMonth() + 1;
	    var strDate = date.getDate();
	    if (month >= 1 && month <= 9) {
	        month = "0" + month;
	    }
	    if (strDate >= 0 && strDate <= 9) {
	        strDate = "0" + strDate;
	    }
	    var currentdate = year + seperator1 + month + seperator1 + strDate;
	    return currentdate;
	}

	return {
		init : init,
		search:search,
		exportList:exportList,
		moneyFormat:moneyFormat,
		otherMoneyFormat:otherMoneyFormat,
		get7DaysBefore:get7DaysBefore,
		getPreThMonth:getPreThMonth,
	}
})();

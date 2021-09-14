NB.ns("app.admin").recSummaryReport = (function() {
	
	var tableObj=$('#recSummaryReportDataTable');
	var exceptionTableObj=$('#recSummaryReportExceptionTable');
	var exceptionSummaryTableObj=$('#recSummaryReportExceptionSummaryTable');
	var formObj = $("#recSummaryReportSearchForm");
	var startObj=$('#recSummaryReportRecTime');
	var payFlowNoObj = formObj.find('input[name=payFlowNo]');
	var orderStateObj = formObj.find('select[name=orderState]');
	var recSummaryReportShortDetail = $('#recSummaryReportShortDetail');
	var recSummaryReportSummary = $('#recSummaryReportSummary');
	var recSummaryReportShortDetailTable = $('#recSummaryReportShortDetailTable');
	var recSummaryReportShortSummaryTable = $('#recSummaryReportShortSummaryTable');
	var orgTree;
	var apiUrl = '/admin/recSummaryReport';
	//机构树
	function initTree() {
		var setting = {
			view : {
				dblClickExpand : false,
				showLine : false,
				selectedMulti : false,
				fontCss : {
					fontSize : '18px'
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
		$.ajax({
			url : "/admin/organization/data",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(msg) {
				//这个树形下拉会在和其同级位置生成一个默认隐藏的input输入框以提供表单数据
				orgTree = $("#recSummaryReportOrgTree").ztreeview({
					name : 'name',
					key : 'code',
					//是否
					clearable : true,
					expandAll : true,
					data : msg
				}, setting);
				orgTree.updateCode(msg[0].id, msg[0].code);
				
				var accountOrgNo = $("#recSummaryReportOrgNoInit").val();
				if((accountOrgNo != "" && accountOrgNo != null && accountOrgNo != undefined)){
					for(var i=0;i<msg.length;i++){
						if(accountOrgNo == msg[i].code){
							orgTree.updateCode(msg[i].id,msg[i].code);
						}
					}
				}
				// 选择隐藏还是现实机构下拉选择
				var length = msg.length;
				if(length && length>1){
					$("#recSummaryReportOrgTree").parent().parent().parent().show();
				}else{
					$("#recSummaryReportOrgTree").parent().parent().parent().hide();
				}
//				search();
				initAllTable();
			}
		});
	}
	function initShortSummaryTable(){
		recSummaryReportShortSummaryTable.bootstrapTable({
			url : apiUrl+"/shortSummary",
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable: true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
			onLoadSuccess: function (data) {
            },
			queryParams:function(params){
				  var queryObj ={billSource:'init'};
				  var query = $.extend( true, params, queryObj);
				  return query;
			  }
		});
	}
	function initShortDetailTable(){
		recSummaryReportShortDetailTable.bootstrapTable({
			url : apiUrl+"/shortDetail",
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable: true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
			onLoadSuccess: function (data) {
//			     $(exceptionTableObj).bootstrapTable('resetView');
            },
			queryParams:function(params){
				  var queryObj ={billSource:'init'};
				  var query = $.extend( true, params, queryObj);
				  return query;
			  }
		});
	}
	function initAllTable(){
		initTable();
		initShortDetailTable();
		initShortSummaryTable();
//		initExceptionSummaryTable();
//		initExceptionTable();
	}
	function appendSummary(target,billSource){
		var orgNo= orgTree.getVal;
		var rangeTime = startObj.val();
		var starttime = "";
	    var endtime = "";
	    if(rangeTime){
            starttime = rangeTime.split("~")[0];
            endtime = rangeTime.split("~")[1];
        }
		$.ajax({
	    	 url:apiUrl+'/summaryAmount',
	    	 async:false,
	    	 data:{
	    		orgCode:orgNo,
	    		startDate:starttime,
	    		endDate:endtime,
	    		billSource:billSource
	    	 },
	    	 type : "get",
			 dataType : "json",
			 success:function(res){
				 $(target).bootstrapTable('append', res);
			 }
	     })
	}
	function initTable(){
		var orgNo= orgTree.getVal;
		var rangeTime = startObj.val();
		var starttime = "";
	    var endtime = "";
	    if(rangeTime){
            starttime = rangeTime.split("~")[0];
            endtime = rangeTime.split("~")[1];
        }
	    billSources.forEach((val)=>{
	    	$('#recSummaryReportDataTable_'+val.value).bootstrapTable({
				url : apiUrl + "/summary",
				dataType : "json",
				uniqueId : "id",
				singleSelect : true,
				resizable: true,
				pagination : true, // 是否分页
				sidePagination : 'server',// 选择服务端分页
	//			showFooter: true,
				onLoadSuccess: function (data) {
					 var cellFileds = new Array("tradeDate","hisAmount","billSource","thirdAmount");
					 appendSummary($('#recSummaryReportDataTable_'+val.value),val.value);
					 $('#recSummaryReportDataTable_'+val.value).mergeCells(cellFileds);
	            },
	            queryParams:function(params){
					  var queryObj ={orgCode:orgNo,startDate:starttime,endDate:endtime,billSource:val.value};
					  var query = $.extend( true, params, queryObj);
					  return query;
				  }
			});
	    })
//		tableObj.bootstrapTable({
//			url : apiUrl + "/summary",
//			dataType : "json",
//			uniqueId : "id",
//			singleSelect : true,
//			resizable: true,
//			pagination : true, // 是否分页
//			sidePagination : 'server',// 选择服务端分页
////			showFooter: true,
//			onLoadSuccess: function (data) {
//				 var cellFileds = new Array("tradeDate","hisAmount","billSource","thirdAmount");
////				 appendSummary();
//			     $(tableObj).mergeCells(cellFileds);
////			     $(tableObj).bootstrapTable('resetView');
//            },
//            queryParams:function(params){
//				  var queryObj ={orgCode:orgNo,startDate:starttime,endDate:endtime};
//				  var query = $.extend( true, params, queryObj);
//				  return query;
//			  }
//		});
	}
	
	function initExceptionTable(){
		var orgNo= orgTree.getVal;
		var rangeTime = startObj.val();
		var starttime = "";
	    var endtime = "";
	    if(rangeTime){
            starttime = rangeTime.split("~")[0];
            endtime = rangeTime.split("~")[1];
        }
		exceptionTableObj.bootstrapTable({
			url : "/admin/unusualBill/data",
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable: true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
			onLoadSuccess: function (data) {
//			     $(exceptionTableObj).bootstrapTable('resetView');
            },
			queryParams:function(params){
				  var queryObj ={orgNo:orgNo,startDate:starttime,endDate:endtime};
				  var query = $.extend( true, params, queryObj);
				  return query;
			  }
		});
	}
	function initExceptionSummaryTable(){
		var orgNo= orgTree.getVal;
		var rangeTime = startObj.val();
		var starttime = "";
	    var endtime = "";
	    if(rangeTime){
            starttime = rangeTime.split("~")[0];
            endtime = rangeTime.split("~")[1];
        }
		exceptionSummaryTableObj.bootstrapTable({
			url : apiUrl + "/exceptionSummary",
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable: true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
			onLoadSuccess: function (data) {
//				$(exceptionSummaryTableObj).bootstrapTable('resetView');		
            },
            queryParams:function(params){
				  var queryObj ={orgCode:orgNo,startDate:starttime,endDate:endtime};
				  var query = $.extend( true, params, queryObj);
				  return query;
			  }
		});
	}
	function search(){
		var orgNo= orgTree.getVal;
		var rangeTime = startObj.val();
		var starttime = "";
	    var endtime = "";
	    if(rangeTime){
            starttime = rangeTime.split("~")[0];
            endtime = rangeTime.split("~")[1];
        }
	    billSources.forEach((val)=>{
	    	$('#recSummaryReportDataTable_'+val.value).bootstrapTable('refreshOptions', {
				  resizable: true,
				  pageNumber:1,
				  queryParams:function(params){
					  var queryObj ={orgCode:orgNo,startDate:starttime,endDate:endtime,billSource:val.value};
					  var query = $.extend( true, params, queryObj);
					  return query;
				  }
			});
	    })
	    
//		tableObj.bootstrapTable('refreshOptions', {
//			  resizable: true,
//			  pageNumber:1,
//			  queryParams:function(params){
//				  var queryObj ={orgCode:orgNo,startDate:starttime,endDate:endtime};
//				  var query = $.extend( true, params, queryObj);
//				  return query;
//			  }
//		});
//		exceptionTableObj.bootstrapTable('refreshOptions', {
//			  resizable: true,
//			  pageNumber:1,
//			  queryParams:function(params){
//				  var queryObj ={orgNo:orgNo,startDate:starttime,endDate:endtime};
//				  var query = $.extend( true, params, queryObj);
//				  return query;
//			  }
//		});
//		exceptionSummaryTableObj.bootstrapTable('refreshOptions', {
//			  resizable: true,
//			  pageNumber:1,
//			  queryParams:function(params){
//				  var queryObj ={orgCode:orgNo,startDate:starttime,endDate:endtime};
//				  var query = $.extend( true, params, queryObj);
//				  return query;
//			  }
//		});
	}
	/**
	 * 初始化日期
	 */
	function initDate() {
		var nowDate = new Date();
		var rangeTime = startDate + " ~ " + endDate;
		var startLayDate = laydate.render({
            elem : '#recSummaryReportRecTime',
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
	function longMoneyFormat(val,row){
		val = moneyFormat(val)
		if(row.tradeDate == "合计"){
			var html = "<span style='font-weight: bold;'>"+val+"</span>";
			return html;
		}
		var color = '';
		if(val!=0){
			color = 'color:#9BBB59'
		}
		return '<span style="'+color+'">'+val+'</span>'
	}
	function shortMoneyFormat(val,row){
		val = moneyFormat(val)
		if(row.tradeDate == "合计"){
			var html = "<span style='font-weight: bold;'>"+val+"</span>";
			return html;
		}
		var color = '';
		if(val!=0){
			color = 'color:#FF0000'
			return '<a title="短款明细汇总" class="shortMoneyBtn" onclick="app.admin.recSummaryReport.shortBtnClick(\''+row.billSource+'\',\''+row.tradeDate+'\',\''+row.payType+'\',\''+val+'\')"><span style="'+color+'">'+val+'</span></a>'
		}
		return '<span style="'+color+'">'+val+'</span>'
	}
	function shortBtnClick(billSource,tradeDate,payType,money){
		recSummaryReportShortDetail.modal('show');
		var orgNo= orgTree.getVal;
		recSummaryReportShortDetailTable.bootstrapTable('refreshOptions', {
			  resizable: true,
			  pageNumber:1,
			  onLoadSuccess: function (data) {
				  recSummaryReportShortDetailTable.bootstrapTable('append', {businessNo:'合计',refundAmount:Math.abs(money)});
	          },
			  queryParams:function(params){
				  var queryObj ={orgCode:orgNo,tradeDate:tradeDate,payType:payType,billSource:billSource};
				  var query = $.extend( true, params, queryObj);
				  return query;
			  }
		});
		recSummaryReportShortSummaryTable.bootstrapTable('refreshOptions', {
			  resizable: true,
			  pageNumber:1,
			  onLoadSuccess: function (data) {
				  recSummaryReportShortSummaryTable.bootstrapTable('append', {payDate:'合计',refundAmount:Math.abs(money)});
	          },
			  queryParams:function(params){
				  var queryObj ={orgCode:orgNo,tradeDate:tradeDate,payType:payType,billSource:billSource};
				  var query = $.extend( true, params, queryObj);
				  return query;
			  }
		});
	}
	function exportData(){
		var orgNo= orgTree.getVal;
		var rangeTime = startObj.val();
		var starttime = "";
	    var endtime = "";
	    if(rangeTime){
            starttime = rangeTime.split("~")[0];
            endtime = rangeTime.split("~")[1];
        }
	    var hour = $.fn.getHour(starttime, endtime);
		if(hour > 24*31){
			$.NOTIFY.showError("错误", '时间范围超过31天，请缩短时间范围', '');
			return false;
		}
		bootbox.confirm('确定执行此操作?',function(r){
            if (r){
            	var where ='&orgCode='+ orgNo +'&startDate='+starttime +
            	'&endDate=' + endtime + '&limit=9999999';
        		var url = apiUrl+'/exportData?' + where;
        		window.location.href=url;
            }
        });
	}
	function formatHandler(val,row){
		return '操作按钮';
	}
	function formatterTypeValue(val,row){
		var value = typesJSON[val];
		if(value){
			return value;
		}
		return val;
	}
	function hjFormat(val,row){
		if(row&&(row.tradeDate == "合计"||row.businessNo=='合计'||row.payDate=='合计')){
			var html = "<span style='font-weight: bold;'>"+val+"</span>";
			return html;
		}
		return val;
	}
	function formatterType(val,row){
		var billSource = typesJSON[row.billSource];
		if(!billSource){
			billSource = "未知渠道"
		}
		var payType = typesJSON[row.payType]
		if(!payType){
			payType = "未知"
		}
		return billSource + " - " +payType
	}
	function moneyFormat(val, row, index){
		if(row&&(row.tradeDate == "合计"||row.businessNo=='合计'||row.payDate=='合计')){
			var html = "<span style='font-weight: bold;'>"+new Number(val).toFixed(2)+"</span>";
			return html;
		}
		if(!val){
			val='0';
		}
		return new Number(val).toFixed(2);
	}
	function exceptionState(val, row, index){
		var checkState = row.checkState;
		
		var color = "";
		var value = "";
		if ("2" == checkState || "6" == checkState) {
			color = "green";
			value = "短款";
		} else if ("3" == checkState || "5" == checkState) {
			color = "red";
			value = "长款";
		} else if ("7" == checkState) {
			value = "待审核";
		} else if ("8" == checkState) {
			value = "已驳回";
		} else if ("9" == checkState) {
			value = "已退费";
		} else if ("1" == checkState) {
			value = "已抹平";
		} else if ("10" == checkState) {
			value = "已追回";
		}
		var res = "";
		if(color != ""){
			res = "<span style=\"color:"+color+"\">"+value+"</span>";
		}else{
			res = "<span>"+value+"</span>";
		}
		return res;
	}
	function init(){
		initDate();
		initTree();
//		initAllTable();
	}
	
	
	return {
		init : init,
		formatterTypeValue:formatterTypeValue,
		moneyFormat:moneyFormat,
		formatHandler:formatHandler,
		search:search,
		exportData:exportData,
		exceptionState:exceptionState,
		formatterType:formatterType,
		longMoneyFormat:longMoneyFormat,
		shortMoneyFormat:shortMoneyFormat,
		shortBtnClick:shortBtnClick,
		hjFormat:hjFormat
	}
})();
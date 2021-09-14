NB.ns("app.admin").hisSettlementDetail = (function() {

	// 表格
	var tableObj = $("#hisSettlementDetailDataTable");
	var formObj = $("#hisSettlementDetailSearchForm");
	var hisSettlementDetailInfoDlg = $("#hisSettlementDetailInfoDlg");
	var billSourceObj = formObj.find("select[name=billSource]");
	var payTypeObj = formObj.find("select[name=payType]");
	var tradeTypeObj = formObj.find("select[name=tradeType]");
	var payBusinessTypeObj = formObj.find("select[name=payBusinessType]");
	var typesJSON = $.parseJSON(tableObj.attr("typesJSON"));
	var orgJSON = $.parseJSON(tableObj.attr("orgJSON"));
	// 时间控件
	var rangeTimeObj = $("#hisSettlementDetailTime");
	
	var orgSelectObj = $("#hisSettlementDetailOrgSelect");
	
	// 请求路径
	var apiUrl = '/admin/hissettlement';

	var ztreeObj_search;

	function formatOpt(val,row,index ) {
		return '<a href="javascript:;" onclick="app.admin.hisSettlementDetail.detail(\'' + val + '\',\'' + row.outTradeNo + '\')" class="btn btn-info btn-sm m-primary" >查看 </a>';
	}

	function formatOrg(val, index, row) {
		if (orgJSON[val]) {
			return '<p data-toggle="tooltip" title=\"'+ orgJSON[val] +'\">' + orgJSON[val] +'</p>'
		}
		return '<p data-toggle="tooltip" title=\"未知\">未知</p>'
	}
	
	function formatType(val, index, row) {
		if (typesJSON[val]) {
			return typesJSON[val];
		}
		return '未知';
	}
	function _formatType(val, index, row) {
		if (typesJSON[val]) {
			return typesJSON[val];
		}
		return '';
	}
	//导出
	function exportData() {
		var orgNo = ztreeObj_search.getVal;
		var orgName = ztreeObj_search.getText;
		var sortName = tableObj.bootstrapTable('getOptions').sortName||'';
		var sortOrder = tableObj.bootstrapTable('getOptions').sortOrder;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var startDate = "";
		var endDate = "";
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0].trim();
			endDate = rangeTime.split("~")[1].trim();
		}
		var hour = $.fn.getHour(startDate, endDate);
		if(hour > 24*31){
			$.NOTIFY.showError("错误", '时间范围超过31天，请缩短时间范围', '');
			return false;
		}
		var queryData = formObj.serializeObject();
		var settlementSerialNo = queryData['settlementSerialNo'];
		var tnsOrderNo = queryData['tnsOrderNo'];
		var hisOrderNo = queryData['hisOrderNo'];
		var outTradeNo = queryData['outTradeNo'];
		var billSource = queryData['billSource'];
		var payType = queryData['payType'];
		var tradeType = queryData['tradeType'];
		var payBusinessType = queryData['payBusinessType'];
		bootbox.confirm('确定执行此操作?',function(r){
            if (r){
            	var where ='sort='+sortName+'&order='+sortOrder+'&orgNo='+ orgNo +'&settlementStartDate='+startDate +"&settlementEndDate="+endDate+ 
            	"&orgName=" + orgName +"&settlementSerialNo="+settlementSerialNo+"&tnsOrderNo="+tnsOrderNo+"&hisOrderNo="+hisOrderNo+
            	"&outTradeNo="+outTradeNo+"&billSource="+billSource+"&payType="+payType+'&tradeType='+tradeType+'&payBusinessType='+payBusinessType;
        		var url = apiUrl+'/dcHisSettlementDetailExcel?' + where;
        		window.location.href=url;
            }
        });
	}

	function search(th) {
		var orgNo = ztreeObj_search.getVal;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var queryData = formObj.serializeObject();
		
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0].trim();
			endDate = rangeTime.split("~")[1].trim();
			
			queryData['settlementStartDate'] = startDate;
			queryData['settlementEndDate'] = endDate;
			
		}
		tableObj.bootstrapTable('refreshOptions', {
			pageNumber : 1,
			queryParams : function(params) {
				var query = $.extend(true, params, queryData);
				return query;
			},
			onPreBody:function(data){
            	$(th).button('loading');
            },
            onLoadSuccess:function(data){
            	$(th).button("reset");
	        }
		});
		
		countSum();
	}
	
	function countSum(){
		var orgNo = ztreeObj_search.getVal;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var queryData = formObj.serializeObject();
		
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0].trim();
			endDate = rangeTime.split("~")[1].trim();
			
			queryData['settlementStartDate'] = startDate;
			queryData['settlementEndDate'] = endDate;
			
		}
		// 查询汇总数据
		$.ajax({
			url : apiUrl + "/sum",
			type : "post",
			data : queryData,
			dataType : "json",
			success : function(data) {
				var amount = data.data.amountSum;
				var billsCount = data.data.billsCount;
				if (!amount) {
					amount = "0";
				}
				$("#hisSettlementDetailBillsCount").html(billsCount);
				$("#hisSettlementDetailBillsAmount").html(new Number(amount).toFixed(2));
			}
		});
	}

	function initTable() {
		tableObj.bootstrapTable({
			url : apiUrl+"/page",
			dataType : "json",
			uniqueId : "id",
			resizable: true,
			singleSelect : true,
			showPaginationSwitch : false,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
		});
	}

	function initOrgTree() {
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
		$.ajax({
			url : '/admin/organization/data',
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) {
				ztreeObj_search = orgSelectObj.ztreeview({
					name : 'name',
					key : 'code',
					// 是否
					clearable : true,
					expandAll : true,
					data : data
				}, setting);
				ztreeObj_search.updateCode(data[0].id,data[0].code);

				// 选择隐藏还是现实机构下拉选择
				var length = data.length;
				if(length && length>1){
					orgSelectObj.parent().parent().parent().show();
				}else{
					orgSelectObj.parent().parent().parent().hide();
				}
				
				countSum();
			}
		});
	}

	function initDict() {
		// 支付类型
	   $.ajax({
			url : apiUrl + '/select',
			data : {
				value : "Pay_Type"
			},
			type : "post",
			dataType : "json",
            success:function(data){
            	payTypeObj.select2({
					data:data,
				    width:'220px',
				    allowClear: false,
				    //禁止显示搜索框
				    minimumResultsForSearch: Infinity,
				    templateResult:function(repo){
				    	return repo.value;
				    },
				    templateSelection:function(repo){
				    	return repo.value;
				    }
				});
            }
        });
	   // 渠道
	   $.ajax({
		   url : apiUrl + '/select',
		   data : {
			   value : "bill_source"
		   },
		   type : "post",
		   dataType : "json",
		   success:function(data){
			   billSourceObj.select2({
				   data:data,
				   width:'220px',
				   allowClear: false,
				   //禁止显示搜索框
				   minimumResultsForSearch: Infinity,
				   templateResult:function(repo){
					   return repo.value;
				   },
				   templateSelection:function(repo){
					   return repo.value;
				   }
			   });
		   }
	   });
	// 交易类型
	   $.ajax({
		   url : apiUrl + '/select',
		   data : {
			   value : "Trade_Type"
		   },
		   type : "post",
		   dataType : "json",
		   success:function(data){
			   tradeTypeObj.select2({
				   data:data,
				   width:'220px',
				   allowClear: false,
				   //禁止显示搜索框
				   minimumResultsForSearch: Infinity,
				   templateResult:function(repo){
					   return repo.value;
				   },
				   templateSelection:function(repo){
					   return repo.value;
				   }
			   });
		   }
	   });
	   $.ajax({
		   url : apiUrl + '/select',
		   data : {
			   value : "Pay_Business_Type"
		   },
		   type : "post",
		   dataType : "json",
		   success:function(data){
			   payBusinessTypeObj.select2({
				   data:data,
				   width:'220px',
				   allowClear: false,
				   //禁止显示搜索框
				   minimumResultsForSearch: Infinity,
				   templateResult:function(repo){
					   return repo.value;
				   },
				   templateSelection:function(repo){
					   return repo.value;
				   }
			   });
		   }
	   });
	}

	function detail(id,outTradeNo) {
		var row = tableObj.bootstrapTable('getRowByUniqueId',id);	
		hisSettlementDetailInfoDlg.loadDetailReset();
		hisSettlementDetailInfoDlg.loadDetail(row);
		hisSettlementDetailInfoDlg.find("dd[data-name=patientId]").html(row.patientId);
		hisSettlementDetailInfoDlg.find("dd[data-name=patientName]").html(row.patientName);
		hisSettlementDetailInfoDlg.find("dd[data-name=hisOrderNo]").html(row.hisOrderNo);
		hisSettlementDetailInfoDlg.find("dd[data-name=outTradeNo]").html(row.outTradeNo);
		hisSettlementDetailInfoDlg.find("dd[data-name=tnsOrderNo]").html(row.tnsOrderNo);
		hisSettlementDetailInfoDlg.find("dd[data-name=amount]").html(moneyFormat(row.amount) +"元");
		hisSettlementDetailInfoDlg.find("dd[data-name=payType]").html(formatType(row.payType));
		hisSettlementDetailInfoDlg.find("dd[data-name=orderType]").html(formatType(row.orderType));
		hisSettlementDetailInfoDlg.find("dd[data-name=settlementSerialNo]").html(row.settlementSerialNo);
		hisSettlementDetailInfoDlg.find("dd[data-name=billSource]").html(formatType(row.billSource));
		hisSettlementDetailInfoDlg.find("dd[data-name=payTime]").html(row.payTime);
		hisSettlementDetailInfoDlg.find("dd[data-name=settlementTime]").html(row.settlementTime);
		hisSettlementDetailInfoDlg.find("dd[data-name=settlementDate]").html(row.settlementDate);
		hisSettlementDetailInfoDlg.find("dd[data-name=payBusinessType]").html(_formatType(row.payBusinessType));
		hisSettlementDetailInfoDlg.modal('show');
	}
	
	/**
	 * 初始化日期
	 */
	function initDate() {
		
		var nowDate = new Date();
		var rangeTime = $("#hisSettlementDate").val() + " ~ " + $("#hisSettlementDate").val();
		var startLayDate = laydate.render({
			elem : '#hisSettlementDetailTime',
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

	function number(value, row, index) {
		   var pageSize=tableObj.bootstrapTable('getOptions').pageSize;
	       var pageNumber=tableObj.bootstrapTable('getOptions').pageNumber;
	       return pageSize * (pageNumber - 1) + index + 1;
	}
	
	function init() {
		initDate();
		initOrgTree();
		initDict();
		initTable();
	}
	
	function moneyFormat(val, row, index){
		return new Number(val).toFixed(2);
	}

	return {
		init : init,
		search : search,
		formatOrg : formatOrg,
		formatType : formatType,
		detail : detail,
		formatOpt : formatOpt,
		moneyFormat:moneyFormat,
		number:number,
		exportData:exportData
	}

})();
$(function() {
	app.admin.hisSettlementDetail.init();
});
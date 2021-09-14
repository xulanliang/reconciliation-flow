NB.ns("app.settlement").settlementSummary = (function() {
	//表格
	var tableObj = $("#settlementSummaryDataTable");
	// 商户收款明细表格
	var thirdTableObj = $("#thirdBillDetailTable");
	var hisTableObj = $("#hisBillDetailTable");
	var omissionAmountTableObj = $("#omissionAmountDetailTable");
	var exceptionTableObj = $("#exceptionBillDetailTable");
	var hissettlementTableObj = $("#hissettlementBillDetailTable");
	var beforeSettlementTableObj = $("#beforeSettlementBillDetailTable");
	var tradeTypeObj = $('#settlementSearchForm').find("select[name=tradeType]");
	//请求路径
	var apiUrl = '/admin/settlementBill/summary';
	var thirdApiUrl = "merchant/bill/";
	var hisApiUrl = "/admin/accountday/";
	var omissionAmountApiUrl = "/admin/omissionAmount/";
	var exceptionApiUrl = "/admin/exceptionbill/";
	var hissettlementApiUrl = "/admin/hissettlement/";
	var beforeSettlementApiUrl = "/admin/beforeSettlement/";
	
	// 字典键值对数组
	var typesJSON = $.parseJSON($("#settlementType").val());
	// 机构名称和编码对象数组
	var orgJSON = $.parseJSON($("#settlementOrgNo").val());
	// 机构搜索树
	var ztreeObj_search;
	
	
	//不同系统选项卡数据
	function settlementSelectTypeData(){
		$.ajax({
            url:"/admin/reconciliation/typeValue?typeValue=bill_source&isIncludeAll=true",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	var buttonHtml = "";
            	TabNav.destroy(".tab-nav-settlement");
            	for(var i=1,l=msg.length;i<l;i++){
            		var name = msg[i]["name"];
            		var value = msg[i]["value"];
            		var button = "<li class=\"btn btn-default tab-item\"  name=\""+value+"\" >"+name+"</li>";
            		buttonHtml += button;
            	}
            	$('#settlementFollowTable').html(buttonHtml);
            	TabNav.init(".tab-nav-settlement");
            	initTable();
            	search();
            }
        });
	}
	//交易类型选项框
	function settlementTradeTypeSelect(){
		$.ajax({
            url:'/admin/hissettlement/select',
	 		data : {
			   value : "Trade_Type"
			},
			type : "post",
			dataType : "json",
            success:function(data){
            	$('#thirdBillDetailDialog').find('select[name=tradeType]').select2({
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
            	$('#hisBillDetailDialog').find('select[name=tradeType]').select2({
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
            	$('#exceptionBillDetailDialog').find('select[name=tradeType]').select2({
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
            	}).on("change", function(){
            		searchExceptionBill()
        		});
            	$('#hissettlementBillDetailDialog').find('select[name=tradeType]').select2({
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
            	}).on("change", function(){
            		searchHissettlementBill()
        		});
            	$('#omissionAmountDetailDialog').find('select[name=tradeType]').select2({
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
            	})
            	$('#beforeSettlementBillDetailDialog').find('select[name=tradeType]').select2({
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
            	}).on("change", function(){
            		searchBeforeSettlementBill();
        		});
            }
        });
	}
	function showTradeCondition() {
		search();
	}
	

	// 翻译机构
	function formatOrg(val, index, row) {
		if (orgJSON[val]) {
			return '<p data-toggle="tooltip" title=\"'+ orgJSON[val] +'\">' + orgJSON[val] +'</p>';
		}
		return '未知';
	}
	
	// 翻译字典
	function formatStat(val, index, row) {
		if (typesJSON[val]) {
			return '<p data-toggle="tooltip" title=\"'+ typesJSON[val] +'\">' + typesJSON[val] +'</p>';
		}
		return '未知';
	}

	// 搜索列表
	function search() {
		var orgCodeTemp = ztreeObj_search.getVal;
		if (orgCodeTemp === 9999 || orgCodeTemp === null || orgCodeTemp === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		//控制时间天数
		var times=$("#summaryTime").val().split("~");
		var sDate1 = Date.parse(times[0]);
	    var sDate2 = Date.parse(times[1]);
	    var dateSpan = sDate2 - sDate1;
	    dateSpan = Math.abs(dateSpan);
        var iDays = Math.floor(dateSpan / (24 * 3600 * 1000));
		if(iDays>93){
			$.NOTIFY.showError("错误", '请选择时间小于或等于3个月!', '');
			return;
		}
		tableObj.bootstrapTable('refreshOptions', {
			resizable : true,
			pageNumber : 1,
			queryParams : function(params) {
				var queryObj = {
					orgNo : ztreeObj_search.getVal,
					settleDate : $("#summaryTime").val(),
					billSource : $('#settlementSelectType').val(),
					tradeType : tradeTypeObj.val()
				};
				var query = $.extend(true, params, queryObj);
				return query;
			}
		});
	}

	// 初始化表格数据
	function initTable() {
		tableObj.bootstrapTable({
			url : apiUrl,
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable : true,
			showPaginationSwitch : false,
			pagination : false // 是否分页
		});
	}
	
	// 初始化搜索条件机构树
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
				ztreeObj_search = $("#settlementTree").ztreeview({
					name : 'name',
					key : 'code',
					//是否
					clearable : true,
					expandAll : true,
					data : data
				}, setting);
				ztreeObj_search.updateCode(data[0].id, data[0].code);
				// 选择隐藏还是现实机构下拉选择
				var length = data.length;
				if(length && length>1){
					$("#settlementTree").parent().parent().parent().show();
				}else{
					$("#settlementTree").parent().parent().parent().hide();
				}
				//tab页面生成
				settlementSelectTypeData();
			}
		});
	}

	/**
	 * 初始化日期
	 */
	function initDate() {
		var nowDate = new Date();
		var rangeTime = $("#accountDate").val() + " ~ " + $("#accountDate").val();
		var startLayDate = laydate.render({
			elem : '#summaryTime',
			btns: ['confirm'],
			theme : '#A9BCF5',
			type: "date",
			value: rangeTime,
			format:"yyyy-MM-dd",
			range:"~",
			max: $("#accountDate").val(),
			ready: function(date){
				var layym = $(".laydate-main-list-0 .laydate-set-ym span").eq(0).attr("lay-ym").split("-")[1];
				if(layym == (nowDate.getMonth()+1)){
					$(".laydate-main-list-0 .laydate-prev-m").click();
				}
			}
		});
	}
	
	function moneyFormat(val, row, index){
		return new Number(val).toFixed(2);
	}
	
	function orderStateMoneyFormat(val, row, index){
		var orderState = row.orderState;
		if(orderState == "0256"){
			val = "-"+Math.abs(val);
		}
		return new Number(val).toFixed(2);
	}
	
	function checkStateMoneyFormat(val, row, index){
		var checkState = row.checkState;
		if(checkState == "2" || checkState == "6"){
			val = "-"+Math.abs(val);
		}
		return new Number(val).toFixed(2);
	}
	
	function orderTypeMoneyFormat(val, row, index){
		var orderState = row.orderType;
		if(orderState == "0256"){
			val = "-"+Math.abs(val);
		}
		return new Number(val).toFixed(2);
	}

	// 商户收款汇总点击方法
	function thirdFormatter(index, row){
		// 交易时间
		var tradeDate = row.settleDate;
		var channelAmount = row.channelAmount;
		var thirdBtn = null;
		if(row.settleDate != "合计"){
			thirdBtn = "<a href='javascript:void(0)' onclick='app.settlement.settlementSummary.showThirdBillDlog(\"" 
				+ tradeDate + "\")'>"+new Number(channelAmount).toFixed(2)+"</a>";
		}else{
			thirdBtn = new Number(channelAmount).toFixed(2);
		}
		return thirdBtn;
	}
	// 显示模态框
	function showThirdBillDlog(tradeDate){
		$("#thirdBillDetailDialog").find(".modal-title").html(tradeDate + " 商户收款明细");
		$("#thirdTradeDate").val(tradeDate);
		$("#thirdBillDetailDialog").find("[name=payFlowNo]").val("");
		$("#thirdBillDetailDialog").find('select[name=tradeType]').val('').trigger("change");
		initThirdTable();
		searchThirdBill();
		$("#thirdBillDetailDialog").modal("show");
	}
	// 查询商户收款账单明细列表
	function searchThirdBill(th){
		var orgCode = ztreeObj_search.getVal;
		var billSource = $('#settlementSelectType').val();
		var date = $("#thirdTradeDate").val();
		var tsnOrderNo = $("#thirdBillDetailDialog").find("[name=payFlowNo]").val();
		var tradeType = $("#thirdBillDetailDialog").find("select[name=tradeType]").val();
		
		if (orgCode === 9999 || orgCode === null || orgCode === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		// 查询汇总数据
		$.ajax({
			url : thirdApiUrl + "sum",
			method : "post",
			data : {
				orgCode : orgCode,
				date : date,
				billSource : billSource,
				tsnOrderNo : tsnOrderNo,
				tradeType: tradeType
			},
			dataType : "json",
			success : function(data) {
				var amount = data.data;
				if (!amount) {
					amount = "0";
				}
				$("#thirdSumMoney").html(new Number(amount).toFixed(2));
			}
		});
		// 查询账单列表数据
		thirdTableObj.bootstrapTable('refreshOptions', {
			resizable : true,
			pageNumber : 1,
			queryParams : function(params) {
				var queryObj = {
					orgCode : orgCode,
					date : date,
					billSource : billSource,
					tsnOrderNo : tsnOrderNo,
					tradeType : tradeType
				};
				var query = $.extend(true, params, queryObj);
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
	// 初始化商户收款账单明细表格数据
	function initThirdTable() {
		thirdTableObj.bootstrapTable("destroy");
		thirdTableObj.bootstrapTable({
			url : thirdApiUrl + "page",
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable : true,
			showPaginationSwitch : false,
			sidePagination : 'server',// 选择服务端分页
			pagination : true // 是否分页
		});
	}
	// 导出商户收款明细
	function exportThirdData() {
		var orgCode = ztreeObj_search.getVal;
		var billSource = $('#settlementSelectType').val();
		var date = $("#thirdTradeDate").val();
		var tsnOrderNo = $("#thirdBillDetailDialog").find("[name=payFlowNo]").val();
		var tradeType = $("#thirdBillDetailDialog").find("select[name=tradeType]").val();
		
		if (orgCode === 9999 || orgCode === null || orgCode === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		bootbox.confirm('确定执行此操作?',function(r){
			if (r){
				var where ='orgCode='+ orgCode +'&billSource='+billSource + "&date=" + date + "&tsnOrderNo=" + tsnOrderNo + '&tradeType='+tradeType;
				var url = thirdApiUrl+'dcExcel?' + where;
				window.location.href=url;
			}
		});
	}
	// 导出结账汇总数据
	function exportSettlementData() {
		var orgCode = ztreeObj_search.getVal;
		var settleDate = $("#summaryTime").val();
		var billSource = $('#settlementSelectType').val();
		
		if (orgCode === 9999 || orgCode === null || orgCode === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		bootbox.confirm('确定执行此操作?',function(r){
			if (r){
				var where ='orgCode='+ orgCode +'&settleDate='+settleDate + "&billSource=" + billSource;
				var url = apiUrl+'/dcExcel?' + where;
				window.location.href=url;
			}
		});
	}
	
	// 导出异常账单
	function exportSettlementExceptionData(){
		var orgCode = ztreeObj_search.getVal;
		var orgName = ztreeObj_search.getText;
		var billSource = typesJSON[$('#settlementSelectType').val()];
		var tradeType = $("#exceptionBillDetailDialog").find("select[name=tradeType]").val();
		var sortName = "tradeTime";
		var sortOrder = "desc";
		
		if (orgCode === 9999 || orgCode === null || orgCode === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}

		bootbox.confirm('确定执行此操作?',function(r){
            if (r){
                var starttime = "";
                var endtime = "";
                var rangeTime = $("#summaryTime").val();
                if(rangeTime){
                    starttime = rangeTime.split("~")[0];
                    endtime = rangeTime.split("~")[1];
                }
                var where = 'sort='+sortName+'&order='+sortOrder+'&billSource='+billSource+'&orgNo=' + orgCode + '&startDate=' + starttime + '&endDate=' 
                	+ endtime+'&tradeType=tradeType' + "&orgName=" + orgName + "&fileName=" + starttime +"至"+ endtime + orgName + "异常账单汇总报表" +"&workSheetName="+"异常账单汇总表"+"&offset=0&limit=1000000"+"&dataSourceType=all";
        		var url = '/admin/unusualBill/dcExcel?' + where;
        		window.location.href=url;
            }
        });
	}
	
	// -----------------------------------------
	// HIS当日总金额点击方法
	function hisFormatter(val, row, index){
		// 交易时间
		var tradeDate = row.settleDate;

		var thirdBtn = null;
		if(row.settleDate != "合计"){
			thirdBtn = "<a href='javascript:void(0)' onclick='app.settlement.settlementSummary.showHisBillDlog(\"" 
				+ tradeDate + "\")'>"+new Number(val).toFixed(2)+"</a>";
		}else{
			thirdBtn = new Number(val).toFixed(2);
		}
		return thirdBtn;
	}
	// 显示模态框
	function showHisBillDlog(tradeDate){
		$("#hisBillDetailDialog").find(".modal-title").html(tradeDate + " HIS当日交易明细");
		$("#hisTradeDate").val(tradeDate);
		$("#hisBillDetailDialog").find("[name=payFlowNo]").val("");
		$("#hisBillDetailDialog").find("[name=hisFlowNo]").val("");
		$("#hisBillDetailDialog").find("[name=custName]").val("");
		$("#hisBillDetailDialog").find('select[name=tradeType]').val('').trigger("change");
		initHisTable();
		searchHisBill();
		$("#hisBillDetailDialog").modal("show");
	}
	function initHisTable() {
		hisTableObj.bootstrapTable("destroy");
		hisTableObj.bootstrapTable({
			url : hisApiUrl + "page",
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable : true,
			showPaginationSwitch : false,
			sidePagination : 'server',// 选择服务端分页
			pagination : true // 是否分页
		});
	}
	function searchHisBill(th){
		var orgCode = ztreeObj_search.getVal;
		var billSource = $('#settlementSelectType').val();
		var date = $("#hisTradeDate").val();
		var tsnOrderNo = $("#hisBillDetailDialog").find("[name=payFlowNo]").val();
		var hisFlowNo = $("#hisBillDetailDialog").find("[name=hisFlowNo]").val();
		var custName = $("#hisBillDetailDialog").find("[name=custName]").val();
		var tradeType = $("#hisBillDetailDialog").find("select[name=tradeType]").val();
		if (orgCode === 9999 || orgCode === null || orgCode === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		// 查询汇总数据
		$.ajax({
			url : hisApiUrl + "sum",
			method : "post",
			data : {
				orgNo : orgCode,
				date : date,
				billSource : billSource,
				payFlowNo : tsnOrderNo,
				hisFlowNo : hisFlowNo,
				custName:custName,
				tradeType:tradeType
			},
			dataType : "json",
			success : function(data) {
				var amount = data.data.allAmount;
				if (!amount) {
					amount = "0";
				}
				$("#hisSumMoney").html(new Number(amount).toFixed(2));
			}
		});
		// 查询账单列表数据
		hisTableObj.bootstrapTable('refreshOptions', {
			resizable : true,
			pageNumber : 1,
			queryParams : function(params) {
				var queryObj = {
					orgNo : orgCode,
					date : date,
					billSource : billSource,
					payFlowNo : tsnOrderNo,
					hisFlowNo : hisFlowNo,
					custName : custName,
					tradeType:tradeType
				};
				var query = $.extend(true, params, queryObj);
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
	function exportHisData() {
		var orgCode = ztreeObj_search.getVal;
		var billSource = $('#settlementSelectType').val();
		var date = $("#hisTradeDate").val();
		var tsnOrderNo = $("#hisBillDetailDialog").find("[name=payFlowNo]").val();
		var hisFlowNo = $("#hisBillDetailDialog").find("[name=hisFlowNo]").val();
		var custName = $("#hisBillDetailDialog").find("[name=custName]").val();
		var tradeType = $("#hisBillDetailDialog").find("select[name=tradeType]").val();
		if (orgCode === 9999 || orgCode === null || orgCode === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		bootbox.confirm('确定执行此操作?',function(r){
			if (r){
				var where ='orgNo='+ orgCode +'&billSource='+billSource + "&date=" + date + "&payFlowNo=" + tsnOrderNo
				 	+ "&hisFlowNo=" + hisFlowNo + "&custName=" + custName +'&tradeType='+tradeType;
				var url = hisApiUrl+'dcExcel?' + where;
				window.location.href=url;
			}
		});
	}
	
	// 遗漏结账金额点击方法
	function omissionAmountFormat(val, row, index){
		// 交易时间
		var tradeDate = row.settleDate;
		
		var thirdBtn = null;
		if(row.settleDate != "合计"){
			thirdBtn = "<a href='javascript:void(0)' onclick='app.settlement.settlementSummary.showOmissionAmountDlog(\"" 
				+ tradeDate + "\")'>"+new Number(val).toFixed(2)+"</a>";
		}else{
			thirdBtn = new Number(val).toFixed(2);
		}
		return thirdBtn;
	}
	// 漏结金额显示模态框
	function showOmissionAmountDlog(tradeDate){
		$("#omissionAmountDetailDialog").find(".modal-title").html(tradeDate + " 漏结明细");
		$("#omissionAmountTradeDate").val(tradeDate);
		$("#omissionAmountDetailDialog").find("[name=payFlowNo]").val("");
		$("#omissionAmountDetailDialog").find("[name=hisFlowNo]").val("");
		$("#omissionAmountDetailDialog").find("[name=custName]").val("");
		$("#omissionAmountDetailDialog").find('select[name=tradeType]').val('').trigger("change");
		initOmissionAmountTable();
		searchOmissionAmountBill();
		$("#omissionAmountDetailDialog").modal("show");
	}
	
	function initOmissionAmountTable() {
		omissionAmountTableObj.bootstrapTable("destroy");
		omissionAmountTableObj.bootstrapTable({
			url : omissionAmountApiUrl + "page",
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable : true,
			showPaginationSwitch : false,
			sidePagination : 'server',// 选择服务端分页
			pagination : true // 是否分页
		});
	}
	function searchOmissionAmountBill(th){
		var orgCode = ztreeObj_search.getVal;
		var billSource = $('#settlementSelectType').val();
		var date = $("#omissionAmountTradeDate").val();
		var tsnOrderNo = $("#omissionAmountDetailDialog").find("[name=payFlowNo]").val();
		var hisFlowNo = $("#omissionAmountDetailDialog").find("[name=hisFlowNo]").val();
		var custName = $("#omissionAmountDetailDialog").find("[name=custName]").val();
		var tradeType = $("#omissionAmountDetailDialog").find("select[name=tradeType]").val();
		if (orgCode === 9999 || orgCode === null || orgCode === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		// 查询汇总数据
		$.ajax({
			url : omissionAmountApiUrl + "sum",
			method : "post",
			data : {
				orgCode : orgCode,
				tradeDate : date,
				billSource : billSource,
				outTradeNo : tsnOrderNo,
				hisOrderNo : hisFlowNo,
				patientName: custName,
				tradeType:tradeType
			},
			dataType : "json",
			success : function(data) {
				var amount = data.data.allAmount;
				if (!amount) {
					amount = "0";
				}
				$("#omissionAmountSumMoney").html(new Number(amount).toFixed(2));
			}
		});
		// 查询账单列表数据
		omissionAmountTableObj.bootstrapTable('refreshOptions', {
			resizable : true,
			pageNumber : 1,
			queryParams : function(params) {
				var queryObj = {
					orgCode : orgCode,
					tradeDate : date,
					billSource : billSource,
					outTradeNo : tsnOrderNo,
					hisOrderNo : hisFlowNo,
					patientName: custName,
					tradeType:tradeType
				};
				var query = $.extend(true, params, queryObj);
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
	function exportOmissionAmountData() {
		var orgCode = ztreeObj_search.getVal;
		var billSource = $('#settlementSelectType').val();
		var date = $("#omissionAmountTradeDate").val();
		var outTradeNo = $("#omissionAmountDetailDialog").find("[name=payFlowNo]").val();
		var hisFlowNo = $("#omissionAmountDetailDialog").find("[name=hisFlowNo]").val();
		var custName = $("#omissionAmountDetailDialog").find("[name=custName]").val();
		var tradeType = $("#omissionAmountDetailDialog").find("select[name=tradeType]").val();
		if (orgCode === 9999 || orgCode === null || orgCode === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		bootbox.confirm('确定执行此操作?',function(r){
			if (r){
				var where ='orgCode='+ orgCode +'&billSource='+billSource + "&tradeDate=" + date + "&outTradeNo=" + outTradeNo
				 	+ "&hisOrderNo=" + hisFlowNo + "&patientName=" + custName+'&tradeType='+tradeType+"&offset=0&limit=100000";
				var url = omissionAmountApiUrl+'dcExcel?' + where;
				window.location.href=url;
			}
		});
	}
	
	// -----------------------------------------
	// 异常金额点击方法
	function exceptionFormatter(val, row, index){
		// 交易时间
		var tradeDate = row.settleDate;
		var thirdBtn = null;
		if(row.settleDate != "合计"){
			thirdBtn = "<a href='javascript:void(0)' onclick='app.settlement.settlementSummary.showExceptionBillDlog(\"" 
				+ tradeDate + "\", \""+ val +"\")'>"+new Number(val).toFixed(2)+"</a>";
		}else{
			thirdBtn = new Number(val).toFixed(2);
		}
		return thirdBtn;
	}
	// 显示模态框
	function showExceptionBillDlog(tradeDate, amount){
		$("#exceptionBillDetailDialog").find(".modal-title").html(tradeDate + " 异常账单明细");
		$("#exceptionTradeDate").val(tradeDate);
		$("#exceptionSumMoney").html(new Number(amount).toFixed(2));
		$("#exceptionBillDetailDialog").find('select[name=tradeType]').val('').trigger("change");
		initExceptionTable();
		searchExceptionBill();
		$("#exceptionBillDetailDialog").modal("show");
	}
	function initExceptionTable() {
		exceptionTableObj.bootstrapTable("destroy");
		exceptionTableObj.bootstrapTable({
			url : exceptionApiUrl + "page",
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable : true,
			showPaginationSwitch : false,
			sidePagination : 'server',// 选择服务端分页
			pagination : true // 是否分页
		});
	}
	function searchExceptionBill(){
		var orgCode = ztreeObj_search.getVal;
		var billSource = $('#settlementSelectType').val();
		var date = $("#exceptionTradeDate").val();
		var tradeType = $("#exceptionBillDetailDialog").find("select[name=tradeType]").val();
		if (orgCode === 9999 || orgCode === null || orgCode === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}

		// 查询账单列表数据
		exceptionTableObj.bootstrapTable('refreshOptions', {
			resizable : true,
			pageNumber : 1,
			queryParams : function(params) {
				var queryObj = {
						orgCode : orgCode,
						date : date,
						billSource : billSource,
						tradeType:tradeType
				};
				var query = $.extend(true, params, queryObj);
				return query;
			}
		});
	}
	function exportExceptionData() {
		var orgCode = ztreeObj_search.getVal;
		var billSource = $('#settlementSelectType').val();
		var date = $("#exceptionTradeDate").val();
		var amount = $("#exceptionSumMoney").html();
		var tradeType = $("#exceptionBillDetailDialog").find("select[name=tradeType]").val();
		if (orgCode === 9999 || orgCode === null || orgCode === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		bootbox.confirm('确定执行此操作?',function(r){
			if (r){
				var where ='orgCode='+ orgCode +'&billSource='+billSource + "&date=" + date + "&amount=" + amount+'&tradeType='+tradeType;
				var url = exceptionApiUrl+'dcExcel?' + where;
				window.location.href=url;
			}
		});
	}
	// -----------------------------------------
	// H结账结账总金额点击方法
	function hissettlementFormatter(val, row, index){
		// 交易时间
		var settleDate = row.settleDate;

		var thirdBtn = null;
		if(row.settleDate != "合计"){
			thirdBtn = "<a href='javascript:void(0)' onclick='app.settlement.settlementSummary.showHissettlementBillDlog(\"" 
				+ settleDate + "\")'>"+new Number(val).toFixed(2)+"</a>";
		}else{
			thirdBtn = new Number(val).toFixed(2);
		}
		return thirdBtn;
	}
	
	// 显示模态框
	function showHissettlementBillDlog(settleDate){
		$("#hissettlementBillDetailDialog").find(".modal-title").html(settleDate + " HIS结账明细");
		$("#hissettlementDate").val(settleDate);
		// 初始化选项 orderState tradeDate
		initSelectTradeDate();
		$("#hissettlementOrderState option:first").prop("selected", "selected");
		$("#hissettlementBillDetailDialog").find('select[name=tradeType]').val('').trigger("change");
		initHissettlementTable();
		searchHissettlementBill();
		$("#hissettlementBillDetailDialog").modal("show");
	}
	function initHissettlementTable() {
		hissettlementTableObj.bootstrapTable("destroy");
		hissettlementTableObj.bootstrapTable({
			url : hissettlementApiUrl + "page",
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable : true,
			showPaginationSwitch : false,
			sidePagination : 'server',// 选择服务端分页
			pagination : true // 是否分页
		});
	}
	function searchHissettlementBill(){
		
		var orgCode = ztreeObj_search.getVal;
		var billSource = $('#settlementSelectType').val();
		var settlementDate = $("#hissettlementDate").val();
		var tradeDate = $(".tradedate-tab-nav>.tradeActive").attr("name");
		var orderState = $("#hissettlementOrderState option:selected").val();
		var tradeType = $("#hissettlementBillDetailDialog").find("select[name=tradeType]").val();
		
		if (orgCode === 9999 || orgCode === null || orgCode === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		// 查询汇总数据
		$.ajax({
			url : hissettlementApiUrl + "sum",
			type : "post",
			data : {
				orgCode : orgCode,
				settlementDate : settlementDate,
				billSource : billSource,
				tradeDate : tradeDate,
				orderState : orderState,
				tradeType:tradeType
			},
			dataType : "json",
			success : function(data) {
				var amount = data.data.amountSum;
				if (!amount) {
					amount = "0";
				}
				$("#hissettlementSumMoney").html(new Number(amount).toFixed(2));
			}
		});
		// 查询账单列表数据
		hissettlementTableObj.bootstrapTable('refreshOptions', {
			resizable : true,
			pageNumber : 1,
			queryParams : function(params) {
				var queryObj = {
					orgCode : orgCode,
					settlementDate : settlementDate,
					billSource : billSource,
					tradeDate : tradeDate,
					orderState : orderState,
					tradeType:tradeType
				};
				var query = $.extend(true, params, queryObj);
				return query;
			}
		});
	}
	function exportHissettlementData() {
		var orgCode = ztreeObj_search.getVal;
		var billSource = $('#settlementSelectType').val();
		var settlementDate = $("#hissettlementDate").val();
		var tradeDate = $(".tradedate-tab-nav>.tradeActive").attr("name");
		var orderState = $("#hissettlementOrderState option:selected").val();
		var tradeType = $("#hissettlementBillDetailDialog").find("select[name=tradeType]").val();
		if (orgCode === 9999 || orgCode === null || orgCode === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		bootbox.confirm('确定执行此操作?',function(r){
			if (r){
				var where ='orgCode='+ orgCode +'&billSource='+billSource + "&settlementDate=" + settlementDate 
				 	+ "&tradeDate=" + tradeDate + "&orderState=" + orderState+'&tradeType='+tradeType+"&offset=0&limit=100000";
				var url = hissettlementApiUrl+'dcExcel?' + where;
				window.location.href=url;
			}
		});
	}
	
	// 结账以前金额点击方法
	function beforeSettlementFormat(val, row, index){
		// 交易时间
		var settleDate = row.settleDate;
		
		var thirdBtn = null;
		if(row.settleDate != "合计"){
			thirdBtn = "<a href='javascript:void(0)' onclick='app.settlement.settlementSummary.showBeforeSettlementBillDlog(\"" 
				+ settleDate + "\")'>"+new Number(val).toFixed(2)+"</a>";
		}else{
			thirdBtn = new Number(val).toFixed(2);
		}
		return thirdBtn;
	}
	// 显示模态框
	function showBeforeSettlementBillDlog(settleDate){
		$("#beforeSettlementBillDetailDialog").find(".modal-title").html(settleDate + " 结算以前金额交易明细");
		$("#beforeSettlementDate").val(settleDate);
		// 初始化选项 orderState tradeDate
		initBeforeSettlementSelectTradeDate();
		$("#beforeSettlementOrderState option:first").prop("selected", "selected");
		$("#beforeSettlementBillDetailDialog").find('select[name=tradeType]').val('').trigger("change");
		initBeforeSettlementTable();
		searchBeforeSettlementBill();
		$("#beforeSettlementBillDetailDialog").modal("show");
	}
	function initBeforeSettlementTable() {
		beforeSettlementTableObj.bootstrapTable("destroy");
		beforeSettlementTableObj.bootstrapTable({
			url : beforeSettlementApiUrl + "page",
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable : true,
			showPaginationSwitch : false,
			sidePagination : 'server',// 选择服务端分页
			pagination : true // 是否分页
		});
	}
	function searchBeforeSettlementBill(){
		
		var orgCode = ztreeObj_search.getVal;
		var billSource = $('#settlementSelectType').val();
		var settlementDate = $("#beforeSettlementDate").val();
		var tradeDate = $(".before-tradedate-tab-nav>.tradeActive").attr("name");
		var orderState = $("#beforeSettlementOrderState option:selected").val();
		var tradeType = $("#beforeSettlementBillDetailDialog").find("select[name=tradeType]").val();
		if (orgCode === 9999 || orgCode === null || orgCode === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		// 查询汇总数据
		$.ajax({
			url : beforeSettlementApiUrl + "sum",
			type : "post",
			data : {
				orgCode : orgCode,
				billSource : billSource,
				settlementDate : settlementDate,
				tradeDate:tradeDate,
				orderState : orderState,
				tradeType:tradeType
			},
			dataType : "json",
			success : function(data) {
				var amount = data.data.allAmount;
				if (!amount) {
					amount = "0";
				}
				$("#beforeSettlementSumMoney").html(new Number(amount).toFixed(2));
			}
		});
		// 查询账单列表数据
		beforeSettlementTableObj.bootstrapTable('refreshOptions', {
			resizable : true,
			pageNumber : 1,
			queryParams : function(params) {
				var queryObj = {
					orgCode : orgCode,
					billSource : billSource,
					settlementDate : settlementDate,
					tradeDate:tradeDate,
					orderState : orderState,
					tradeType:tradeType
				};
				var query = $.extend(true, params, queryObj);
				return query;
			}
		});
	}
	function exportBeforeSettlementData() {
		var orgCode = ztreeObj_search.getVal;
		var billSource = $('#settlementSelectType').val();
		var settlementDate = $("#beforeSettlementDate").val();
		var tradeDate = $(".before-tradedate-tab-nav>.tradeActive").attr("name");
		var orderState = $("#beforeSettlementOrderState option:selected").val();
		var tradeType = $("#beforeSettlementBillDetailDialog").find("select[name=tradeType]").val();
		if (orgCode === 9999 || orgCode === null || orgCode === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		bootbox.confirm('确定执行此操作?',function(r){
			if (r){
				var where ='orgCode='+ orgCode +'&billSource='+billSource + '&settlementDate='+settlementDate
				 	+ "&tradeDate=" + tradeDate + "&orderState=" + orderState+'&tradeType='+tradeType+"&offset=0&limit=100000";
				var url = beforeSettlementApiUrl+'dcExcel?' + where;
				window.location.href=url;
			}
		});
	}
	function initBeforeSettlementSelectTradeDate(){
		var orgCode = ztreeObj_search.getVal;
		var billSource = $('#settlementSelectType').val();
		var settlementDate = $("#beforeSettlementDate").val();
		
		$.ajax({
            url: beforeSettlementApiUrl + "datelist",
            type: 'POST',
            data:{
            	orgCode : orgCode,
				settlementDate : settlementDate,
				billSource : billSource
            },
            dataType:"json",
            async: false,
            success:function(msg){
            	var dateList = msg.data.dateList;
            	if(dateList.length<=1){
            		$('#beforeSettlementSelectTradeDateTable').hide();
            		return;
            	}else{
            		$('#beforeSettlementSelectTradeDateTable').show();
            	}
            	var buttonHtml = "<li class=\"btn btn-default tab-item tradeActive\" name=\"\">全部</li>";
            	TabNav.destroy(".tradedate-tab-nav");
            	for(var i=0,l=dateList.length;i<l;i++){
            		var button = "<li class=\"btn btn-default tab-item\"  name=\""+dateList[i]["payDate"]+"\" >"+dateList[i]["payDate"]+"</li>";
            		buttonHtml += button;
            	}
            	$('#beforeSettlementSelectTradeDateTable').html(buttonHtml);
            	TabNav.init(".before-tradedate-tab-nav");
            }
        });
	}
	
	function initSelectTradeDate(){
		var orgCode = ztreeObj_search.getVal;
		var billSource = $('#settlementSelectType').val();
		var settlementDate = $("#hissettlementDate").val();
		
		$.ajax({
            url: hissettlementApiUrl + "datelist",
            type: 'POST',
            data:{
            	orgCode : orgCode,
				settlementDate : settlementDate,
				billSource : billSource
            },
            dataType:"json",
            async: false,
            success:function(msg){
            	var dateList = msg.data.dateList;
            	var buttonHtml = "<li class=\"btn btn-default tab-item tradeActive\" name=\"\">全部</li>";
            	TabNav.destroy(".tradedate-tab-nav");
            	for(var i=0,l=dateList.length;i<l;i++){
            		var button = "<li class=\"btn btn-default tab-item\"  name=\""+dateList[i]["payDate"]+"\" >"+dateList[i]["payDate"]+"</li>";
            		buttonHtml += button;
            	}
            	$('#selectTradeDateTable').html(buttonHtml);
            	TabNav.init(".tradedate-tab-nav");
            }
        });
	}
	
	// 初始化行数
	function init() {
		initOrgTree();
		initDate();
		settlementTradeTypeSelect();
		$("#settlementDiv .tab-nav-settlement").on("tabChange",function (event) {
			//在这些做你想做的任何事
			var name = $(event.item).attr("name");
			$('#settlementSelectType').val(name);
			//显示列表的系统来源列
			showTradeCondition(name);
		});
		// 注册结账总金额的时间tab插件触发事件
		$(".tradedate-tab-nav").on("click",function (event) {
			searchHissettlementBill();
		});
		$("#hissettlementOrderState").on("change", function(){
			searchHissettlementBill();
		});
		// 注册结账前金额的时间tab插件触发事件
		$(".before-tradedate-tab-nav").on("click",function (event) {
			searchBeforeSettlementBill();
		});
		$("#beforeSettlementOrderState").on("change", function(){
			searchBeforeSettlementBill();
		});
		// 解决模态框中点击导出按钮后，模态框不能滚动操作的情况
		$(document).on("hidden.bs.modal", ".bootbox", function () {
			  if(!$("body").hasClass("modal-open")){
			    $("body").addClass("modal-open");
			  }
			});
	}
	
	function formatter(val) {
		if(typesJSON[val]==null || typesJSON[val]==undefined){
			return typesJSON[val];
		}
		return typesJSON[val];
	}
	
	function checkStateformatter(val, row, index){
		var checkStateName = "";
		if(val == "2" || val == "6"){
			checkStateName = "短款";
		}else if(val == "3" || val == "5"){
			checkStateName = "长款";
		}
		return checkStateName;
	}
	function remarkformatter(index, row){
		var checkState = row.checkState;
		var orderState = row.tradeName;
		
		var remark = "";
		if(checkState == "3" || checkState == "5"){
			if(orderState == "0156"){
				remark = "建议：HIS补数据";
			}else{
				remark = "建议：商户退给患者";
			}
		}else if(checkState == "2" || checkState == "6"){
			if(orderState == "0156"){
				remark = "建议：商户补金额";
			}else{
				remark = "建议：HIS减数据";
			}
		}
		return remark;
	}
	
	// 暴露函数
	return {
		init : init,
		formatOrg : formatOrg,
		formatStat : formatStat,
		search : search,
		moneyFormat:moneyFormat,
		formatter:formatter,
		showTradeCondition:showTradeCondition,
		thirdFormatter:thirdFormatter,
		showThirdBillDlog:showThirdBillDlog,
		searchThirdBill:searchThirdBill,
		exportThirdData:exportThirdData,
		hisFormatter:hisFormatter,
		showHisBillDlog:showHisBillDlog,
		searchHisBill:searchHisBill,
		exportHisData:exportHisData,
		exceptionFormatter:exceptionFormatter,
		showExceptionBillDlog:showExceptionBillDlog,
		searchExceptionBill:searchExceptionBill,
		exportExceptionData:exportExceptionData,
		checkStateformatter:checkStateformatter,
		remarkformatter:remarkformatter,
		hissettlementFormatter:hissettlementFormatter,
		showHissettlementBillDlog:showHissettlementBillDlog,
		searchHissettlementBill:searchHissettlementBill,
		exportHissettlementData:exportHissettlementData,
		orderStateMoneyFormat:orderStateMoneyFormat,
		checkStateMoneyFormat:checkStateMoneyFormat,
		orderTypeMoneyFormat:orderTypeMoneyFormat,
		exportSettlementData:exportSettlementData,
		exportSettlementExceptionData:exportSettlementExceptionData,
		omissionAmountFormat:omissionAmountFormat,
		beforeSettlementFormat:beforeSettlementFormat,
		exportOmissionAmountData:exportOmissionAmountData,
		showOmissionAmountDlog:showOmissionAmountDlog,
		searchOmissionAmountBill:searchOmissionAmountBill,
		exportOmissionAmountData:exportOmissionAmountData,
		showBeforeSettlementBillDlog:showBeforeSettlementBillDlog,
		searchBeforeSettlementBill:searchBeforeSettlementBill,
		exportBeforeSettlementData:exportBeforeSettlementData,
		searchExceptionBill:searchExceptionBill
	}
})();

// 初始化执行
$(function() {
	app.settlement.settlementSummary.init();
});

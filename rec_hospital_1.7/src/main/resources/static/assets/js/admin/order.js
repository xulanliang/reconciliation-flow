NB.ns("app.admin").order = (function() {

	// 表格
	var tableObj = $("#orderDataTable");
	var formObj = $("#orderSearchForm");
	var orderInfoDlg = $("#orderInfoDlg");
	var orderDlgTable = $("#orderInfoTableDlg");
	var platFormType = formObj.find("select[name=platFormType]");
	var payCode = formObj.find("select[name=payCode]");
	var payState = formObj.find("select[name=payState]");
	var orderState = formObj.find("select[name=orderState]");
	var refundState = formObj.find("select[name=refundState]");
	var typesJSON = $.parseJSON(tableObj.attr("typesJSON"));
	var orgJSON = $.parseJSON(tableObj.attr("orgJSON"));
	var dlgFormObj =$("#detail");
	// 时间控件
	var rangeTimeObj = $("#orderTime");
	// 请求路径
	var apiUrl = '/admin/order/data';

	var ztreeObj_search;

	function formatOpt(val,row,index ) {
		
		return '<a href="javascript:;" onclick="app.admin.order.detail(\'' + val + '\',\'' + row.outTradeNo + '\')" class="btn btn-info btn-sm m-primary" >查看 </a>';
	}

	function formatOrg(val, index, row) {
		if (orgJSON[val]) {
			return '<p data-toggle="tooltip" title=\"'+ orgJSON[val] +'\">' + orgJSON[val] +'</p>'
//			return orgJSON[val];
		}
//		return '未知';
		return '<p data-toggle="tooltip" title=\"未知\">未知</p>'
	}
	//设备状态
	function formatPlatFormType(val){
		if(val == "1"){
//			result = "IOS"
			return '<p data-toggle="tooltip" title=\"IOS\">IOS</p>'
		}else if(val == "2"){
			return '<p data-toggle="tooltip" title=\"安卓\">安卓</p>'
//			result = "安卓"
		}else if(val == "3"){
			return '<p data-toggle="tooltip" title=\"自助机\">自助机</p>'
//			result = "自助机"
		}else if(val == "4"){
			return '<p data-toggle="tooltip" title=\"PC\">PC</p>'
//			result = "PC"
		}else if(val == "5"){
			return '<p data-toggle="tooltip" title=\"Pad\">Pad</p>'
//			result = "Pad"
		}else{
			return '<p data-toggle="tooltip" title=\"未知\">未知</p>'
//			result = "未知"
		}
	}
	//退款状态
	function formatRefundState(val){
		if(val == "0"){
//			return '<p data-toggle="tooltip" title=\"退款申请已提交\">退款申请已提交</p>'
			return "退款申请已提交"
		}else if(val == "1"){
//			return '<p data-toggle="tooltip" title=\"第三方处理中\">第三方处理中</p>'
			return "第三方处理中"
		}else if(val == "2"){
//			return '<p data-toggle="tooltip" title=\"退款成功\">退款成功</p>'
			return "退款成功"
		}else if(val == "3"){
//			return '<p data-toggle="tooltip" title=\"退款失败\">退款失败</p>'
			return "退款失败"
		}else if(val == "4"){
//			return '<p data-toggle="tooltip" title=\"退款失败,需要人工退款\">退款失败,需要人工退款</p>'
			return "退款失败,需要人工退款"
		}else{
//			return '<p data-toggle="tooltip" title=\"-\">-</p>'
			return "-"
		}
	}

	function formatType(val, index, row) {
		
		if (typesJSON[val]) {
			return '<p data-toggle="tooltip" title=\"'+ typesJSON[val] +'\">' + typesJSON[val] +'</p>'
//			return typesJSON[val];
		}
		return '<p data-toggle="tooltip" title=\"未知\">未知</p>'
//		return '未知';
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
			
			queryData['orderStartTime'] = startDate;
			queryData['orderEndTime'] = endDate;
			
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
	}

	function initTable() {
		tableObj.bootstrapTable({
			url : apiUrl,
			dataType : "json",
			uniqueId : "id",
			resizable: true,
			singleSelect : true,
			showPaginationSwitch : false,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
		});

		// /dlg 初始化
		orderDlgTable.bootstrapTable({
			data : [],
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable:true
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
//				console.log(data);
				ztreeObj_search = $("#orderOrgSelect").ztreeview({
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
					$("#orderOrgSelect").parent().parent().parent().show();
				}else{
					$("#orderOrgSelect").parent().parent().parent().hide();
				}
			}
		});
	}

	function initDict() {
		// 支付类型
		   $.ajax({
				url : apiUrl + '/combox',
				data : {
					isIncludeAll : true,
					type : 1
				},
				type : "get",
				contentType : "application/json",
				dataType : "json",
	            success:function(data){
	            	payCode.select2({
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
		// 支付状态
		$.ajax({
			url : apiUrl + '/combox',
			data : {
				isIncludeAll : true,
				type : 2
			},
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) {
				
//				console.log(data);
				payState.select2({
					data : data,
					width : '220px',
					allowClear : false,
					// 禁止显示搜索框
					minimumResultsForSearch : Infinity,
					templateResult : function(repo) {
						return repo.value;
					},
					templateSelection : function(repo) {
						return repo.value;
					}
				});
			}
		});

		//订单状态
		/*$.ajax({
            url:"/admin/reconciliation/typeValue?typeValue=Order_State&isIncludeAll=true",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	changeSelectData(msg);
            	orderState.select2({
					data:msg,
					placeholder: '==请选择类型==',
					minimumResultsForSearch: -1,
				    width:'220px',
				    allowClear: true,
				    templateResult:function(repo){
				    	return repo.name;
				    },
				    templateSelection:function(repo){
				    	return repo.name;
				    }
				});
            }
        });*/
		   $.ajax({
	            url:apiUrl+'/combox',
	            data:{isIncludeAll:true,type:3},
	            type:"get",
	            contentType:"application/json",
	            dataType:"json",
	            success:function(data){
	            	orderState.select2({
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
		   
        //退款状态
		   $.ajax({
	            url:apiUrl+'/combox',
	            data:{isIncludeAll:true,type:5},
	            type:"get",
	            contentType:"application/json",
	            dataType:"json",
	            success:function(data){
	              	refundState.select2({
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
		orderInfoDlg.loadDetailReset();
		orderInfoDlg.loadDetail(row);
		orderInfoDlg.find("dd[data-name=orgCode]").html(formatOrg(row.orgCode));
		orderInfoDlg.find("dd[data-name=systemCode]").html(formatType(row.systemCode));
		orderInfoDlg.find("dd[data-name=uid]").html(row.uid);
		orderInfoDlg.find("dd[data-name=orderNo]").html(row.orderNo);
		orderInfoDlg.find("dd[data-name=outTradeNo]").html(row.outTradeNo);
		orderInfoDlg.find("dd[data-name=tsn]").html(row.tsn);
		orderInfoDlg.find("dd[data-name=platFormType]").html(formatPlatFormType(row.platFormType));
		orderInfoDlg.find("dd[data-name=contactName]").html(row.contactName);
		orderInfoDlg.find("dd[data-name=contactMobile]").html(row.contactMobile);
		orderInfoDlg.find("dd[data-name=orderAmount]").html(moneyFormat(row.orderAmount) +"元");
		orderInfoDlg.find("dd[data-name=orderState]").html(formatType(row.orderState));
		orderInfoDlg.find("dd[data-name=payName]").html(row.payName);
		orderInfoDlg.find("dd[data-name=payAmount]").html(moneyFormat(row.payAmount) +"元");
		orderInfoDlg.find("dd[data-name=refundState]").html(formatRefundState(row.refundState));
		if(row.refundState!=2){
			orderInfoDlg.find("dd[data-name=refundAmount]").html(moneyFormat(row.refundAmount) +"元");
			orderInfoDlg.modal('show');
		}else{
			//单独查询退费金额
			$.ajax({
	            url:apiUrl+'/refundDetail',
	            data:{orderNo:row.orderNo},
	            type:"get",
	            contentType:"application/json",
	            dataType:"json",
	            success:function(data){
	            	orderInfoDlg.find("dd[data-name=refundAmount]").html(moneyFormat(data) +"元");
	            	orderInfoDlg.modal('show');
	            }
	        });
		}
		
	}
	
	/**
	 * 初始化日期
	 */
	function initDate() {
		
		var nowDate = new Date();
		var rangeTime = $("#accountDate").val() + " ~ " + $("#accountDate").val();
		var startLayDate = laydate.render({
			elem : '#orderTime',
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
		formatPlatFormType:formatPlatFormType
	}

})();
$(function() {
	app.admin.order.init();
});
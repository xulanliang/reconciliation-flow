NB.ns("app.settlement").settlementlog = (function() {
	//表格
	var tableObj = $("#settlementlogDataTable");
	//对话框
	var dlgObj = $('#settlementlogDedailedDlg');
	// 详情页表单对象
	var dlgFormObj = dlgObj.find("dl[form=detail]");
	//请求路径
	var apiUrl = '/admin/log/settlementLog';
	// 字典键值对数组
	var typesJSON = $.parseJSON(tableObj.attr("typesJSON"));
	// 机构名称和编码对象数组
	var orgJSON = $.parseJSON(tableObj.attr("orgJSON"));
	// 机构搜索树
	var ztreeObj_search;
	
	// 操作按钮
	function formatOpt(index, row) {
		var orgCode = row.orgCode;
		var orderDate = row.orderDate;
		var details_btn = "<a href='javascript:;' onclick='app.settlement.settlementlog.detail("
				+ JSON.stringify(row) + ")' class='btn btn-info btn-sm m-primary '> 详情 </a>";
		var rerec_btn = '<a href="javascript:;" onclick="app.settlement.settlementlog.recon(\''
				+ orgCode
				+ '\',\''
				+ orderDate
				+ '\')" class="btn btn-info btn-sm m-primary "> 重新获取 </a>&nbsp;&nbsp;&nbsp;&nbsp;';
    	return rerec_btn + details_btn;
	}

	// 详情页
	function detail(row) {
		dlgFormObj.html("");
		dlgFormObj.append("<dt style='text-align:center;font-size: 15px;'>结果说明:</dt>"+"<dd>"+row.resultInfo+"</dd>");
		dlgObj.modal('show');
	}
	
	function formatDeletePayTitle(val){
		if(val==null || val == ''){
			return '未知';
		}
		var length = val.lastIndexOf("支付");
		if(length == -1){
			return val;
		}
		return val.substr(0,val.lastIndexOf("支付"));
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
	//汇总
	function con(){
		var orgCode = ztreeObj_search.getVal;
		var orderDate = $("#settlementlogOrderDate").val();
		if(orderDate== null || orderDate==""){
			$.NOTIFY.showError("错误", '请选择账单日期!', '');
			return;
		}
		bootbox.confirm({
			title : "提示?",
			message : '确定要汇总吗?',
			buttons : {
				confirm : {
					className : 'btn-primary btn-sm'
				},
				cancel : {
					className : 'btn-info btn-sm'
				}
			},
			callback : function(r) {
				if (r) {
					$('#followLoading').modal('show');
					$.post(apiUrl, {
						orgCode : orgCode,
						orderDate : orderDate
					}, function(result) {
						$('#followLoading').modal('hide');
						tableObj.bootstrapTable('refreshOptions', {
							resizable : true,
							pageNumber : 1
						});
						if (result.success) {
							$.NOTIFY.showSuccess("提醒", "操作完成", '');
						} else {
							if (result.message) {
								$.NOTIFY.showError("错误", result.message, '');
							}
						}
					}, "json");
				}
			}
		});
	}

	// 重新拉取账单汇总
	function recon(orgCode, orderDate) {
		bootbox.confirm({
			title : "提示?",
			message : '确定要重新汇总吗?',
			buttons : {
				confirm : {
					className : 'btn-primary btn-sm'
				},
				cancel : {
					className : 'btn-info btn-sm'
				}
			},
			callback : function(r) {
				if (r) {
					$('#followLoading').modal('show');
					$.post(apiUrl, {
						orgCode : orgCode,
						orderDate : orderDate
					}, function(result) {
						$('#followLoading').modal('hide');
						tableObj.bootstrapTable('refreshOptions', {
							resizable : true,
							pageNumber : 1
						});
						if (result.success) {
							$.NOTIFY.showSuccess("提醒", "操作完成", '');
						} else {
							if (result.message) {
								$.NOTIFY.showError("错误", result.message, '');
							}
						}
					}, "json");
				}
			}
		});
	}

	// 搜索列表
	function search() {
		var orgCodeTemp = ztreeObj_search.getVal;
		if (orgCodeTemp === 9999 || orgCodeTemp === null || orgCodeTemp === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		tableObj.bootstrapTable('refreshOptions', {
			resizable : true,
			pageNumber : 1,
			queryParams : function(params) {
				var queryObj = {
					orgCode : ztreeObj_search.getVal,
					orderDate : $("#settlementlogOrderDate").val()
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
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
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
				ztreeObj_search = $("#settlementlogOrgSelect").ztreeview({
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
					$("#settlementlogOrgSelect").parent().parent().parent().show();
				}else{
					$("#settlementlogOrgSelect").parent().parent().parent().hide();
				}
			}
		});
	}

	// 列表行号计算
	function number(value, row, index) {
		var pageSize = tableObj.bootstrapTable('getOptions').pageSize;
		var pageNumber = tableObj.bootstrapTable('getOptions').pageNumber;
		return pageSize * (pageNumber - 1) + index + 1;
	}
	
	/**
	 * 初始化日期
	 */
	function initDate() {
		var nowDate = new Date();
		var startLayDate = laydate.render({
			elem : '#settlementlogOrderDate',
			btns: ['confirm'],
			theme : '#A9BCF5',
			type: "date",
			format:"yyyy-MM-dd",
			max: nowDate.getTime()
		});
	}

	
	// 初始化行数
	function init() {
		initOrgTree();
		initTable();
		initDate();
	}

	// 暴露函数
	return {
		init : init,
		formatOpt : formatOpt,
		recon : recon,
		formatOrg : formatOrg,
		formatStat : formatStat,
		search : search,
		number : number,
		detail : detail,
		con:con
	}
})();

// 初始化执行
$(function() {
	app.settlement.settlementlog.init();
});

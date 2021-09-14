NB.ns("app.reconciliation").reconlog = (function() {
	// 表格
	var tableObj = $("#reconlogDataTable");
	// 对话框
	var dlgObj = $('#reconlogDedailedDlg');
	// 详情页表单对象
	var dlgFormObj = dlgObj.find("dl[form=detail]");
	// 请求路径
	var apiUrl = '/admin/log/reconlog';
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
		var details_btn = "<a href='javascript:;' onclick='app.reconciliation.reconlog.detail("
				+ JSON.stringify(row)
				+ ")' class='btn btn-info btn-sm m-primary '> 详情 </a>";
		var rerec_btn = '<a href="javascript:;" onclick="app.reconciliation.reconlog.recon(\''
				+ orgCode
				+ '\',\''
				+ orderDate
				+ '\')" class="btn btn-info btn-sm m-primary "> 重新对账 </a>&nbsp;&nbsp;&nbsp;&nbsp;';
		return rerec_btn + details_btn;
	}

	// 详情页
	function detail(row) {
		$.post('/admin/log/reconlog/logdetails', {
			orgCode : row.orgCode,
			orderDate : row.orderDate
		}, function(data) {
			if (data) {
				dlgFormObj.html('');
				// 拉取账单日志
				var billArr = new Array();
				// 对账日志
				var recArr = new Array();
				var dataSize = data.length;
				for (var i = 0; i < dataSize; i++) {
					var logDetail = data[i];
					// 对账类型：微信、支付宝、银行等数据来源于字典
					var payType = logDetail.payType;
					// 异常描述
					var exceptionRemark = logDetail.exceptionRemark;
					// 日志类型：01：拉取账单，02：对账
					var logType = logDetail.logType;
					if (logType == '01') {
						billArr.push('<dt>'
								+ formatDeletePayTitle(typesJSON[payType])
								+ '</dt><dd data-name="log">' + exceptionRemark
								+ '</dd>');
					} else if (logType == '02') {
						recArr.push('<dt>'
								+ formatDeletePayTitle(typesJSON[payType])
								+ '</dt><dd data-name="log">' + exceptionRemark
								+ '</dd>');
					}
				}
				var html_text = '';
				html_text += '<fieldset>';
				html_text += '<legend>获取账单状态</legend>';
				if (billArr.length > 0) {
					html_text += billArr.join('');
				} else {
					html_text += '<dt>暂无</dt><dd data-name=""></dd>';
				}
				html_text += '</fieldset>';
				html_text += '<fieldset>';
				html_text += '<legend>对账状态</legend>';
				if (recArr.length > 0) {
					html_text += recArr.join('');
				} else {
					html_text += '<dt>暂无</dt><dd data-name=""></dd>';
				}
				html_text += '</fieldset>';
				dlgFormObj.append(html_text);
			}
			dlgObj.modal('show');
		}, "json");
	}

	function formatDeletePayTitle(val) {
		if (val == null || val == '') {
			return '未知';
		}
		if (val == "支付宝") {

			return val;
		}

		var length = val.lastIndexOf("支付");
		if (length == -1) {
			return val;
		}
		return val.substr(0, val.lastIndexOf("支付"));
	}

	// 翻译机构
	function formatOrg(val, index, row) {
		if (orgJSON[val]) {
			return '<p data-toggle="tooltip" title=\"' + orgJSON[val] + '\">'
					+ orgJSON[val] + '</p>';
		}
		return '未知';
	}

	// 翻译字典
	function formatStat(val, index, row) {
		if (typesJSON[val]) {
			return '<p data-toggle="tooltip" title=\"' + typesJSON[val] + '\">'
					+ typesJSON[val] + '</p>';
		}
		return '未知';
	}
	// 对账
	function con() {
		var orgCode = ztreeObj_search.getVal;
		var orderDate = $("#reconlogOrderDate").val();
		if (orderDate == null || orderDate == "") {
			$.NOTIFY.showError("错误", '请选择账单日期!', '');
			return;
		}
		bootbox.confirm({
			title : "提示?",
			message : '确定要对账吗?',
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
					// $('#followLoading').modal('show');
					$.bootstrapLoading.start({
						loadingTips : "正在对账，请稍候..."
					});
					$.post(apiUrl, {
						orgCode : orgCode,
						orderDate : orderDate
					}, function(result) {
						$.bootstrapLoading.end();
						tableObj.bootstrapTable('refreshOptions', {
							resizable : true,
							pageNumber : 1
						});
						if (result.success) {
							$.NOTIFY.showSuccess("提醒", "对账完成", '');
						} else {
							if (result.message) {
								$.bootstrapLoading.end();
								$.NOTIFY.showError("错误", result.message, '');
							}
						}
					}, "json");
				}
			}
		});
	}

	// 重新对账
	function recon(orgCode, orderDate) {
		bootbox.confirm({
			title : "提示?",
			message : '确定要重新对账吗?',
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
					$.bootstrapLoading.start({
						loadingTips : "正在对账，请稍候..."
					});
					$.post(apiUrl, {
						orgCode : orgCode,
						orderDate : orderDate
					}, function(result) {
						$.bootstrapLoading.end();
						tableObj.bootstrapTable('refreshOptions', {
							resizable : true,
							pageNumber : 1
						});
						if (result.success) {
							$.NOTIFY.showSuccess("提醒", "对账完成", '');
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
					orderDate : $("#reconlogOrderDate").val()
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
				ztreeObj_search = $("#reconlogOrgSelect").ztreeview({
					name : 'name',
					key : 'code',
					// 是否
					clearable : true,
					expandAll : true,
					data : data
				}, setting);
				ztreeObj_search.updateCode(data[0].id, data[0].code);
				// 选择隐藏还是现实机构下拉选择
				var length = data.length;
				if (length && length > 1) {
					$("#reconlogOrgSelect").parent().parent().parent().show();
				} else {
					$("#reconlogOrgSelect").parent().parent().parent().hide();
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
			elem : '#reconlogOrderDate',
			btns : [ 'confirm' ],
			theme : '#A9BCF5',
			type : "date",
			format : "yyyy-MM-dd",
			max : nowDate.getTime()
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
		con : con
	}
})();

// 初始化执行
$(function() {
	app.reconciliation.reconlog.init();
});

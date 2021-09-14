NB.ns("app.admin").selfhelp = (function() {
	// 表格
	var tableObj = $("#selfHelpDataTable");
	// 请求路径
	var apiUrl = '/admin/selfhelp';
	// 表单
	var formObj = $("#selfHelpSearchForm");
	// 时间控件
	var startobj = formObj.find("input[name=beginTime]");
	var endobj = formObj.find("input[name=endTime]");
	var deviceNoSelect = formObj.find("input[name=deviceNo]");
	var selfhelpOrgSelect;
	var c1 = "#D1EEEE";
	var c2 = "#D8BFD8";
	var c3 = "#CDC9A5";
	var c4 = "#C1FFC1";
	var c5 = "#FFB6C1";
	
	function wetChatCellStyle(value, row, index, field) {
		var style = {
			css : {
				'background-color' : c1
			}
		};
		return style;
	}

	function aliCellStyle(value, row, index, field) {
		var style = {
			css : {
				'background-color' : c2
			}
		};
		return style;
	}

	function bankCellStyle(value, row, index, field) {
		var style = {
			css : {
				'background-color' : c3
			}
		};
		return style;
	}

	function cashCellStyle(value, row, index, field) {
		var style = {
			css : {
				'background-color' : c4
			}
		};
		return style;
	}
	
	function yibaoCellStyle(value, row, index, field) {
		var style = {
			css : {
				'background-color' : c5
			}
		};
		return style;
	}

	function formaterOpt(index, row) {
		var deviceNo = row.deviceNo;
		// var html='<button type="button" class="btn btn-default"
		// onclick="">查看明细</button>';
		return '';
	}
	
	function formatTime(index, row) {
		if (row.createTime == "0") {
			return "--";
		} else {
			return row.createTime;
		}
	}

	function reflush() {
		tableObj.bootstrapTable('refresh');
	}
	
	function checkOutChange(obj) {
		var v = $(obj).val();
		if (v == "0") {
			$("#selfhelp_machine_export_all").attr("disabled", true);
			$("#selfhelp_machine_export_single").attr("disabled", true);
			$("#selfhelp_machine_settlement").attr("disabled", false);
		} else if (v == "1") {
			$("#selfhelp_machine_export_all").attr("disabled", false);
			$("#selfhelp_machine_export_single").attr("disabled", false);
			$("#selfhelp_machine_settlement").attr("disabled", true);
		}
	}

	// 导出列表
	function exportList() {

		var starttime = $('#selfHelpBeginTime').val();
		var endtime = $('#selfHelpEndTime').val();
		var treeName = selfhelpOrgSelect.getText;

		var name = starttime + "至" + endtime + treeName + "自助机结算汇总";
		tableObj.tableExport({
			type : "excel",
			fileName : name,
			escape : "false",
			ignoreColumn : [ 0, 0 ], // 忽略某一列的索引
		});
	}

	// 汇总导出
	function exportTotal() {
		var p = formObj.serialize();
		var url = apiUrl + "/export/all?" + p + "&orgName="
				+ selfhelpOrgSelect.getText;
		location.href = url;
	}

	// 单台导出
	function exportSingle() {
		var param = formObj.serialize();
		var allSelects = tableObj.bootstrapTable('getSelections');
		if (allSelects.length == 0) {
			alert("请选择数据");
			return;
		}
		var p = "";
		$.each(allSelects, function(index, val) {
			if (p.length > 0) {
				p += "&";
			}
			p += "deviceNos=" + val.deviceNo;
		});
		if (p.length > 0) {
			param = param + "&" + p;
		}
		location.href = apiUrl + "/export/single?" + param + "&orgName="
				+ selfhelpOrgSelect.getText;
	}
	
	// 结算
	function doSettlement() {
		var allSelects = tableObj.bootstrapTable('getSelections');
		if (allSelects.length == 0) {
			alert("请选择数据行");
			return;
		}
		bootbox.confirm({
			title : "提示?",
			message : "确定进行结账吗?",
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
					var param = formObj.serialize();
					var p = "";
					$.each(allSelects, function(index, val) {
						if (p.length > 0) {
							p += "&";
						}
						p += "deviceNos=" + val.deviceNo;
					});
					if (p.length > 0) {
						param = param + "&" + p;
					}
					$.ajax({
						url : apiUrl + "?" + param,
						type : "post",
						contentType : "application/json",
						dataType : "json",
						success : function(result) {
							var code = result.code;
							var message = result.message;
							if (code == '0') {
								$.NOTIFY.showSuccess("提醒", message, '');
								reflush();
							} else {
								$.NOTIFY.showError("错误", message, '');
							}
						}
					});
				}
			}
		});
	}

	function initOrgTree() {
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
			url : "/admin/organization",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(msg) {
				selfhelpOrgSelect = $("#selfhelpOrgSelect").ztreeview({
					name : 'name',
					key : 'code',
					// 是否
					clearable : true,
					expandAll : true,
					data : msg
				}, setting);

				var accountOrgNo = $("#selfHelpOrgNoInit").val();
				if ((accountOrgNo != "" && accountOrgNo != null && accountOrgNo != undefined)) {
					for (var i = 0; i < msg.length; i++) {
						if (accountOrgNo == msg[i].code) {
							selfhelpOrgSelect.updateCode(msg[i].id,
									msg[i].code);
						}
					}
				}

				// //表格初始化
				tableObj.bootstrapTable({
					url : apiUrl,
					dataType : "json",
					uniqueId : "id",
					singleSelect : false,
					clickToSelect : true,
					pagination : false, // 是否分页
					sidePagination : 'server',// 选择服务端分页
					pageSize : 40,
					pageList : [ 30, 50 ],// 分页步进值
					height : 680
				});
				
				// 设置表格时间
				var treeName = selfhelpOrgSelect.getText;
				setTableTitle(tableObj, "自助机结算汇总", null, null, treeName);
			}
		});
	}

	function init() {
		initOrgTree();
		initDeviceNoSelect();
		

		/*laydate.render({
			elem : '#selfHelpBeginTime',
			type : 'datetime'
		});

		laydate.render({
			elem : '#selfHelpEndTime',
			type : 'datetime'
		});*/

		///时间控件初始化
		startobj.datepicker({
			minView : "month",
			language : 'zh-CN',
			format : 'yyyy-mm-dd',
			autoclose : true,
			clearBtn : true,
			todayHighlight : true,
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
			clearBtn : true,
			todayHighlight : true,
			pickerPosition : "bottom-left"

		}).on('changeDate', function(ev) {
			var starttime = startobj.val();
			var endtime = endobj.val();
			startobj.datepicker('setEndDate', endtime);
			endobj.datepicker('hide');
		});

		////控件赋值
		startobj.datepicker("setDate", $("#selfHelpBeginTime").val());
		endobj.datepicker("setDate", $("#selfHelpEndTime").val());
	}

	function initDeviceNoSelect() {
		$.ajax({
			url : "/admin/deviceInfo/getDeviceInfos",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) {
				for (var i = 0; i < data.length; i++) {
					data[i].id = data[i].deviceNo;
				}
				// 初始化
				$("#selfhelpDeviceNoSelect").select2({
					placeholder : '==请选择类型==',
					allowClear : true,
					minimumResultsForSearch : Infinity,
					data : data,
					templateResult : function(repo) {
						return repo.deviceNo;
					},
					templateSelection : function(repo) {
						return repo.deviceNo;
					}
				});
			}
		});
	}

	function search() {
		var p = formObj.serialize();
		var opt = {
			url : apiUrl + "?" + p,
			silent : true,
		};
		tableObj.bootstrapTable('refresh', opt);

		// 设置表格时间
		var starttime = $('#selfHelpBeginTime').val();
		var endtime = $('#selfHelpEndTime').val();
		var treeName = selfhelpOrgSelect.getText;
		setTableTitle(tableObj, "自助机结算汇总", starttime, endtime, treeName);
	}

	return {
		init : init,
		search : search,
		formaterOpt : formaterOpt,
		formatTime : formatTime,
		exportTotal : exportTotal,
		exportList : exportList,
		exportSingle : exportSingle,
		doSettlement : doSettlement,
		wetChatCellStyle : wetChatCellStyle,
		aliCellStyle : aliCellStyle,
		bankCellStyle : bankCellStyle,
		cashCellStyle : cashCellStyle,
		yibaoCellStyle : yibaoCellStyle,
		checkOutChange : checkOutChange
	}
})();

NB.ns("app.admin").summaryStatisticalReports = (function() {
	// 表格
	var tableObj = $("#summaryStatisticalReportsDataTable");
	// 表单
	var formObj = $("#summaryStatisticalReportsSearchForm");
	// 时间控件
	var startobj = formObj.find("input[name=beginTime]");
	var endobj = formObj.find("input[name=endTime]");
	// 请求路径
	var apiUrl = '/admin/summaryStatisticalReports/data';

	var summaryStatisticalReportsTree;
	var options = {
		beforeSubmit : function(formData, jqForm, options) {
			var flg = formObj.data('bootstrapValidator');
			return flg.validate().isValid();
		},
		success : function(result) {
			if (result.success) {
				$.NOTIFY.showSuccess("提醒", "操作成功", '');
				reflush();
			} else {
				if (result.message) {
					$.NOTIFY.showError("错误", result.message, '');
				}
			}
		},
		url : apiUrl,
		type : 'post',
		dataType : 'json',
		clearForm : false,
		resetForm : false,
		timeout : 3000
	};

	// 查询
	function search() {
		var orgNo = summaryStatisticalReportsTree.getVal;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var startDate = startobj.val();
		if (startDate === null || startDate == '') {
			$.NOTIFY.showError("错误", '请选择开始日期!', '');
			return;
		}
		var endDate = endobj.val();
		if (endDate === null || endDate === '') {
			$.NOTIFY.showError("错误", '请选择结束日期!', '');
			return;
		}
		tableObj.bootstrapTable('refreshOptions', {
			pageNumber : 1,
			queryParams : function(params) {
				var queryObj = {orgNo : orgNo, startTime : startDate, endTime : endDate};
				var query = $.extend(true, params, queryObj);
				return query;
			}
		});
	}

	// 机构树
	function formaterOrgProps(handel) {
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
				// 这个树形下拉会在和其同级位置生成一个默认隐藏的input输入框以提供表单数据
				summaryStatisticalReportsTree = $("#summaryStatisticalReportsTree").ztreeview({
					name : 'name',
					key : 'code',
					// 是否
					clearable : true,
					expandAll : true,
					data : msg
				}, setting);
				var nodes = summaryStatisticalReportsTree.ztree.getNodes();
				summaryStatisticalReportsTree.updateValue(nodes[0].id);
				handel(nodes[0].id);
				summaryStatisticalReportsTree.updateCode(msg[0].id,msg[0].code);
			}
		});
	}

	function orgFormatter(val) {
		var org = $('#hisPayDedailedOrgNo').val();
		var orgJSON = JSON.parse(org);
		return orgJSON[val];
	}
	function formatter(val) {
		var typeJSON = $('#hisPayDedailedType').val();
		var typesJSON = JSON.parse(typeJSON);
		
		//console.log(typesJSON);
		return typesJSON[val];
	}
	
	function succHandel(res, accountDate){
		tableObj.bootstrapTable({
			url : apiUrl,
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
			queryParams: function(params) {
				var queryObj = {orgNo : res, startTime : accountDate, endTime : accountDate};
				var query = $.extend(true, params, queryObj);
				return query;
			}, //参数
		});
		// /时间控件初始化
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

		// 获取后台传递的值
		startobj.datepicker("setDate", accountDate);
		endobj.datepicker("setDate", accountDate);
	}

	function init(accountDate) {
		formaterOrgProps(function(data){
			succHandel(data,accountDate);
		});
	}
	
	function number(value, row, index) {
		var pageSize = tableObj.bootstrapTable('getOptions').pageSize;
		var pageNumber = tableObj.bootstrapTable('getOptions').pageNumber;
		return pageSize * (pageNumber - 1) + index + 1;
	}
	
	function exportJasper(){
		var orgNo = summaryStatisticalReportsTree.getVal;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		
		var startDate = startobj.val();
		if (startDate === null || startDate == '') {
			$.NOTIFY.showError("错误", '请选择开始日期!', '');
			return;
		}
		var endDate = endobj.val();
		if (endDate === null || endDate === '') {
			$.NOTIFY.showError("错误", '请选择结束日期!', '');
			return;
		}
		
		bootbox.confirm('确定执行此操作?',function(r){
	        if (r){
	        	var where = '/admin/summaryStatisticalReports/exportJasper?a=1';
	        	where = where + '&orgNo=' + orgNo + '&startTime=' + startDate + '&endTime=' + endDate + '&t=' + new Date().getTime();
	    		window.open(where);
	        }
		});
	}
	function moneyFormat(val, row, index){
		return new Number(val).toFixed(2);
	}

	return {
		init : init,
		search : search,
		succHandel : succHandel,
		formatter : formatter,
		orgFormatter : orgFormatter,
		number : number,
		exportJasper : exportJasper,
		moneyFormat:moneyFormat
	}
})();
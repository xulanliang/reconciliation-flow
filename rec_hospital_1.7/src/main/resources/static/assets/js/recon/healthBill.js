NB.ns("app.admin").healthBill = (function() {
	//表格
	var tableObj = $("#healthBillDataTable");
	//表单
	var formObj = $("#healthBillSearchForm");
	var formObjDlg = $("#healthDealFollowDlgDlg");
	var dlgObj2 = $('#healthDealFollowDlg');
	// 时间控件
	var startobj = formObj.find("input[name=startTime]");
	//请求路径
	var apiUrl = '/admin/healthAccount/data';
	var healthBillTree;
	var orgJSON;
	var colorJSON = {1:'#fdc17e',2:'#25dab4',3:'#49bcfc',4:'#ec649f',5:'#15dde7',6:'#ff8da9',7:'#8d91ff',8:'#ad97fe',9:'#ffa9b1',10:'#b1e284',11:'#ed9dd7'};
	var colorArr = ['#fdc17e','#25dab4','#49bcfc','#ec649f','#15dde7','#ff8da9','#8d91ff','#ad97fe','#ffa9b1','#b1e284','#ed9dd7'];

	var options = {
		beforeSubmit : function(formData, jqForm, options) {
			var flg = formObj.data('bootstrapValidator');
			return flg.validate().isValid();
		},
		success : function(result) {
			if (result.success) {
				$.NOTIFY.showSuccess("提醒", "操作成功", '');
				dlgObj.modal('hide');
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
	//对账
	function startRec() {
		var orgNo = healthBillTree.getVal;
		if (orgNo == null || orgNo == '') {
			$.NOTIFY.showError('提示', '请选择机构!', '');
			return;
		}
		var startDate = startobj.val();
		if (startDate == "" || startDate == null) {
			$.NOTIFY.showError("提示", "请选择日期!", '');
			return;
		}
		var url = apiUrl + "/account";
		$('#followLoading').modal('show');
		$.ajax({
			url : url,
			type : "post",
			data : {
				orgNo : orgNo,
				accountDate : startDate
			},
			dataType : "json",
			success : function(result) {
				if (result.success) {
					$('#followLoading').modal('hide');
					$.NOTIFY.showSuccess("提醒", result.message, '');
					search();
				} else {
					$('#followLoading').modal('hide');
					$.NOTIFY.showError("出错了", result.message, '');
				}
			}
		});
	}

	//查询
	function search(th) {
		var orgNo = healthBillTree.getVal;
		
		var startDate;
		var endDate;
		var rangeTime = startobj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		}

		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}

		// 数据来源  mz-门诊  zy-住院
		var dataSource =  $("#dataSourceTree").val();
		if(dataSource == undefined || dataSource == null || dataSource == '全部'){
			dataSource = "";
		} else if (dataSource == '住院'){
			dataSource = 'zy';
		} else {
			dataSource = 'mz';
		}

		//医保统计
		$.ajax({
			url : apiUrl,
			type : "get",
			data : {
				orgNo : orgNo,
				startDate : startDate,
				endDate : endDate,
				dataSource : dataSource,
			},
			contentType : "application/json",
			dataType : "json",
			success : function(msg) {
				var index;
				var map=msg.data.billList;
				var str="";
				$("#billDiv").html("");
				index = 0;
				initPie("billDrawingDiv", map);
				for(var key in map){
					if(key=='医疗总费用'){
						$('#billAmountDiv').text(new Number(map[key]).toFixed(2))
					}
					if(key!="合计"&&key!='医疗总费用'){
						if(new Number(map[key])!=0){
							var position = (index++ % 2 == 0) ? 'left' : 'right';
							$("#billDiv").append('<li class="'+position+'" style="color:'+colorJSON[index]+'"><p class="classify-title">'+key+
								'</p><p class="classify-amount">'+new Number(map[key]).toFixed(2)+'</p></li>');
						}
					}
					/*if(key!="合计"){
						var position = (index++ % 2 == 0) ? 'left' : 'right';
						$("#billDiv").append('<li class="'+position+'" style="color:'+colorJSON[index]+'"><p class="classify-title">'+key+
								'</p><p class="classify-amount">'+new Number(map[key]).toFixed(2)+'</p></li>');
					}else{
						$("#billAmountDiv").html(new Number(map[key]).toFixed(2));
					}*/
				}

				map=msg.data.hisList;
				$("#hisDiv").html("");
				initPie("hisDrawingDiv", map);
				index = 0;
				for(var key in map){
					if(key=='医疗总费用'){
						$('#hisAmountDiv').text(new Number(map[key]).toFixed(2))
					}
					if(key!="合计"&&key!='医疗总费用'){
						if(new Number(map[key])!=0){
							var position = (index++ % 2 == 0) ? 'left' : 'right';
							$("#hisDiv").append('<li class="'+position+'" style="color:'+colorJSON[index]+'"><p class="classify-title">'+key+
								'</p><p class="classify-amount">'+new Number(map[key]).toFixed(2)+'</p></li>');
						}
					}
					/*if(key!="合计"){
						var position = (index++ % 2 == 0) ? 'left' : 'right';
						$("#hisDiv").append('<li class="'+position+'" style="color:'+colorJSON[index]+'"><p class="classify-title">'+key+
								'</p><p class="classify-amount">'+new Number(map[key]).toFixed(2)+'</p></li>');
					}else{
						$("#hisAmountDiv").html(new Number(map[key]).toFixed(2));
					}*/
				}

				map=msg.data.xList;
				$("#xDiv").html("");
				initDiff("xDrawingDiv", map);
				index = 0;
				for(var key in map){
					if(key=='医疗总费用'){
						$('#xAmountDiv').text(new Number(map[key]).toFixed(2))
					}
					if(key!="合计"&&key!='医疗总费用'){
						if(new Number(map[key])!=0){
							var position = (index++ % 2 == 0) ? 'left' : 'right';
							$("#xDiv").append('<li class="'+position+'" style="color:'+colorJSON[index]+'"><p class="classify-title">'+key+
								'</p><p class="classify-amount">'+new Number(map[key]).toFixed(2)+'</p></li>');
						}
					}
					/*					if(key!="合计"){
                                            var position = (index++ % 2 == 0) ? 'left' : 'right';
                                            $("#xDiv").append('<li class="'+position+'" style="color:'+colorJSON[index]+'"><p class="classify-title">'+key+
                                                    '</p><p class="classify-amount">'+new Number(map[key]).toFixed(2)+'</p></li>');
                                        }else{
                                            $("#xAmountDiv").html(new Number(map[key]).toFixed(2));
                                        }
                    */				}
			}
		});

		var healthType= $('#healthType').val();
		//医保异常
		healthExceptionList(orgNo,startDate,endDate,healthType, th, dataSource);
	}

	function save() {
		var objButton=$("#save");
		objButton.attr('disabled',true);
		var ajax_option = {
			url : apiUrl + "/dealFollow",
			type : 'post',
			success : function(result) {
				if (JSON.parse(result).success) {
					search();
					dlgObj2.modal('hide');
					$.NOTIFY.showSuccess("提醒", "处理成功", '');
				} else {
					$.NOTIFY.showError("错误", JSON.parse(result).message, '');
				}
				objButton.attr('disabled',false);
			}
		};
		formObjDlg.ajaxSubmit(ajax_option);
	}

	// 显示饼状图表数据
	function initPie(id, map){
		var jsontxt = "[";
		for(var key in map){
			if(key!="合计"&&key!='医疗总费用'&&new Number(map[key])!=0){
				jsontxt += '{value:'+new Number(map[key]).toFixed(2)+', name:"'+key+'"},';
			}
		}
		if(jsontxt != "["){
			jsontxt = jsontxt.substring(0, jsontxt.length-1);
		}else{
			jsontxt+= '{value:0.00, name:"合计"}';
		}
		jsontxt +="]";
		option = {
			tooltip : {
				formatter: "{b} : {c} ({d}%)"
			},
			series : [
				{
					type:'pie',
					radius : '60',
					center: ['50%', '50%'],
					label:{show:false},
					data:eval(jsontxt)
				}
			],
			color: colorArr
		};
		var myChart = echarts.init(document.getElementById(id));
		myChart.setOption(option);
	}
	// 显示差异图形数据
	function initDiff(id, map){
		var diff = 0;
		for(var key in map){
			if(key!="合计"){
				diff += map[key];
			}
		}
		if (diff != 0) {
			$("#xDrawingDiv" ).css("background", "#ec639f");
		}else{
			$("#xDrawingDiv" ).css("background", "#eff3f9");
		}
	}

	function healthExceptionList(orgNo,startDate,endDate,healthType, th, dataSource){
		if(orgNo == null){
			orgNo = healthBillTree.getVal;
		}
		if(startDate==null){
			startDate = startobj.val();
		}
		if(healthType==null){
			healthType=$('#healthType').val();
		}
		if (orgNo == 9999 || orgNo == null || orgNo == '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		if(healthType=="全部" || healthType==null || healthType == 9999)healthType="";

		// tableObj.bootstrapTable("destroy")
		tableObj.bootstrapTable('refreshOptions', {
//            height:300, //给表格指定高度，就会有行冻结的效果
//            fixedColumns:true, //是否开启列冻结
//            fixedNumber:1, //需要冻结的列数
			pageNumber : 1,
			queryParams : function(params) {
				var queryObj = {
					orgNo : orgNo,
					startTime : startDate,
					endTime : endDate,
					healthType:healthType,
					// 数据来源
					dataSource:dataSource
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
				healthBillTree = $("#healthBillTree").ztreeview({
					name : 'name',
					key : 'code',
					//是否
					clearable : true,
					expandAll : true,
					data : msg
				}, setting);
				healthBillTree.updateCode(msg[0].id, msg[0].code);
				// 选择隐藏还是现实机构下拉选择
				var length = msg.length;
				if(length && length>1){
					$("#healthBillTree").parent().parent().parent().show();
				}else{
					$("#healthBillTree").parent().parent().parent().hide();
				}
				search();
			}
		});
	}

	function formatHandler(val, row, index) {
		var disabledStatus = "";
		if (row.state == "已抹平" || row.state == "已平账") {
			disabledStatus = 'disabled="true"';
		}
		var detail = '<button href="javascript:;" ' + disabledStatus + ' class="btn btn-info btn-sm m-primary" ' +
			'onclick="app.admin.healthBill.dealFollow(\'' + row.payFlowNo + '\',\'' + row.orgCode + '\',\'' + '1' + '\',\'' + row.costTotalInsurance + '\',\'' + row.tradeDataTime + '\',\'' + '\')"> 抹平 </button> &nbsp; ';
		detail = detail + '<a href="javascript:;" class="btn btn-info btn-sm m-primary" ' +
			'onclick="app.admin.healthBill.details(\'' + row.payFlowNo + '\',\''+ row.orgCode + '\',\'' + row.orderState + '\')"> 查看 </a>';
		return detail;
	}

	// 抹平操作
	function dealFollow(orderNo,orgNo, checkState, tradeAmount, tradeDatetime) {
		formObjDlg[0].reset();
		$("#recover").prop("hidden", true);
		$("#recoverSeason").prop("hidden", true);
		$("#floating").prop("hidden", true);
		$("#floatingSeason").prop("hidden", true);
		formObjDlg.find("input[name=payFlowNo]").val(orderNo);
		formObjDlg.find("input[name=checkState]").val(checkState);
		formObjDlg.find("input[name=tradeAmount]").val(tradeAmount);
		formObjDlg.find("input[name=orgCode]").val(orgNo);
		formObjDlg.find("input[name=tradeDatetime]").val(tradeDatetime);
		formObjDlg.find("input[name=payType]").val("0559");
		// billSource设置为医保支付：0559
		formObjDlg.find("input[name=billSource]").val("0559");
		if (checkState == 10) {
			$("#recoverSeason").prop("hidden", false);
		} else {
			$("#floating").prop("hidden", false);
			$("#floatingSeason").prop("hidden", false);
		}
		dlgObj2.modal('show');
	}

	function currentState(val, row, index) {
		var state = row.state;
		var color = "";
		if (state == "待处理") {
			color = "#ffa94b";
		} else {
			// 已抹平、已平账
			color = "";
		}
		return "<span style=\"color:" + color + "\">" + val + "</span>";
	}

	function details(payFlowNo,orgCode,orderState){
		var orgNo = healthBillTree.getVal;
		$.ajax({
			url:apiUrl,
			type : "get",
			data : {
				// 机构选择下拉框机构编码值
				orgNo:orgNo,
				orgCode:orgCode,
				payFlowNo : payFlowNo,
				orderState : orderState
			},
			contentType:"application/json",
			dataType:"json",
			success:function(msg){
				if(!msg.success){
					$.NOTIFY.showError("提醒", msg.message, '');
					return false;
				}
				var num=0;
				var billMap=msg.data.billList;
				var hisMap=msg.data.hisList;
				var xMap=msg.data.xList;
				$("#healthDetailsBody").html("");
				for(var key in billMap){
					if(key!="合计"){
						$("#healthDetailsBody").append(
							'<tr id="billTr_'+num+'">'+
							'<td style="text-align:center">'+key+':'+new Number(billMap[ key ]).toFixed(2)+'</td>'+
							'</tr>'
						);
						num++;
					}
				}
				num=0;
				for(var key in hisMap){
					if(key!="合计"){
						$("#billTr_"+num).append(
							'<td style="text-align:center">'+key+':'+new Number(hisMap[ key ]).toFixed(2)+'</td>'
						);
						num++;
					}
				}
				num=0;
				for (var key in xMap) {
					if (key != "合计") {
						$("#billTr_" + num).append(
							'<td style="text-align:center;color: red;">' + new Number(xMap[key]).toFixed(2) + '</td>'
						);
						num++;
					}
				}
				// 处理说明
				var dealDetail = msg.data.dealMap;
				if (dealDetail != null && typeof (dealDetail) != "undefined") {
					// 显示医保处理说明
					$("#description").html(dealDetail.dealReason);
					$("#dealDesc").show();
				}else{
					$("#dealDesc").hide();
				}
				// 处理说明文件
				var dealDetailFile = msg.data.dealMap;
				if (dealDetailFile != null && typeof (dealDetailFile) != "undefined") {
					$("#descImg").attr("value","/admin/electronic/readImage?adress=" + dealDetailFile.fileLocation);
					// 显示医保处理图片
					$("#dealImage").show();
				}else{
					$("#dealImage").hide();
				}
				$("#healthDetails").modal('show');
			}
		});
	}

	// 查看图片
	function showImg(th){
		window.open($(th).attr("value"));
	}

	function formatter(val) {
		var typeJSON = $('#healthBillType').val();
		var typesJSON = JSON.parse(typeJSON);
//		return typesJSON[val];
		if (typesJSON[val] == null || typesJSON[val] == undefined){
			return typesJSON[val];
		}
		return '<p data-toggle="tooltip" title=\"'+ typesJSON[val] +'\">' + typesJSON[val] +'</p>'
	}

	/**
	 * 初始化日期
	 */
	function initDate() {
		var nowDate = new Date();
		var rangeTime = $("#healthBillTradeDate").val();
		var startLayDate = laydate.render({
			elem : '#healthBillTime',
			btns: ['confirm'],
			theme : '#A9BCF5',
			type: "date",
			value: rangeTime+" ~ "+rangeTime,
			range:"~",
			format:"yyyy-MM-dd",
			max: nowDate.getTime()
		});
	}

	function initTable(){
		tableObj.bootstrapTable({
			// height:300,
			// fixedColumns:true,
			// fixedNumber:2,
			url : apiUrl + "/exceptionList",
			dataType : "json",
			uniqueId : "id",
			resizable: true,
			singleSelect : true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
		});
	}

	//支付类型数据
	function healthTypeData(){
		$.ajax({
			url:"/admin/reconciliation/typeValue?typeValue=healthcare_type&isIncludeAll=true",
			contentType:"application/json",
			dataType:"json",
			success:function(msg){
				changeSelectData(msg);
				$('#healthType').select2({
					data:msg,
					placeholder: '==请选择类型==',
					minimumResultsForSearch: -1,
					width:'150px',
					allowClear: true,
					templateResult:function(repo){
						return repo.name;
					},
					templateSelection:function(repo){
						return repo.name;
					}
				});
			}
		});
	}
	function init(orgJSON_temp) {
		orgJSON = orgJSON_temp;
		initDate();
		initTree();
		initTable();
		healthTypeData();
	}
	function moneyFormat(val, row, index){
//		return new Number(val).toFixed(2);
		return '<p data-toggle="tooltip" title=\"'+ Number(val).toFixed(2) +'\">' + Number(val).toFixed(2) +'</p>'
	}

	/**
	 * 医保渠道账单情况翻译
	 * @param val
	 * @param row
	 * @param index
	 * @returns {string}
	 */
	function zdBIllStateFormat(val, row, index){
		if(row.billState == 0){
			return '<p data-toggle="tooltip" title=\"--\">--</p>'
		}else {
			return '<p data-toggle="tooltip" title=\"'+ Number(val).toFixed(2) +'\">' + Number(val).toFixed(2) +'</p>'
		}
	}

	/**
	 * 医保His账单情况翻译
	 * @param val
	 * @param row
	 * @param index
	 * @returns {string}
	 */
	function zdHisStateFormat(val, row, index){
		if(row.hisState == 0){
			return '<p data-toggle="tooltip" title=\"--\">--</p>'
		}else {
			return '<p data-toggle="tooltip" title=\"'+ Number(val).toFixed(2) +'\">' + Number(val).toFixed(2) +'</p>'
		}
	}

	//导出
	function exportData() {

		var startDate;
		var endDate;
		var rangeTime = startobj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		}
		
		// 数据来源  mz-门诊  zy-住院
		var dataSource = $("#dataSourceTree").val();
		if (dataSource == undefined || dataSource == null || dataSource == '全部') {
			dataSource = "";
		} else if (dataSource == '住院') {
			dataSource = 'zy';
		} else {
			dataSource = 'mz';
		}

		var orgNo = healthBillTree.getVal;
		var orgName = healthBillTree.getText;
		var sortName = tableObj.bootstrapTable('getOptions').sortName || '';
		var sortOrder = tableObj.bootstrapTable('getOptions').sortOrder;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var billSource = $("#billSource").val();
		var dataSourceType = "all";

		bootbox.confirm('确定执行此操作?', function (r) {
			if (r) {
				var where = 'orgNo=' + orgNo + '&startTime=' + startDate +'&endTime=' + endDate + '&dataSource=' + dataSource;
				var url = apiUrl + '/exportDate?' + where;
				window.location.href = url;
			}
		});
	}

	return {
		init : init,
		search : search,
		formatter : formatter,
		zdBIllStateFormat : zdBIllStateFormat,
		zdHisStateFormat : zdHisStateFormat,
		startRec : startRec,
		dealFollow : dealFollow,
		formatHandler:formatHandler,
		moneyFormat:moneyFormat,
		details:details,
		healthExceptionList:healthExceptionList,
		exportData:exportData,
		save:save,
		currentState:currentState,
		showImg:showImg,
	}
})();

NB.ns("app.admin").unusualBil = (function() {
	//表格
	var tableObj2 = $("#unusualBillDataTable");
	var dlgObj2 = $('#unusualBillDealFollowDlg');
	var dlgObj3 = $('#unusualBillShowHandFollowDlg');
	var dlgObj4 = $('#unusualBillRefundFollowDlg');
	var followDetailDlg = $('#unusualBIllFollowDetailDlg');
	var electronicDlg = $('#unusualBillDlg');
	//表单
	var formObj = $("#unusualBillSearchForm");
	var formObjDlg = $("#unusualBillDealFollowDlgDlg");
	var refundFormObjDlg = $("#unusualBillRefundFollowDlgDlg");
	var payOrderTableObj = $('#unusualPayOrderTable');
	var hisOrderTableObj = $('#unusualHisOrderTable');
	var hisDlgFormObj = electronicDlg.find("li[form=hisOrder]");
	var payDlgFormObj = electronicDlg.find("li[form=payOrder]");
	var hisOrderTableForm = electronicDlg.find("li[form=unusualHisOrderTableForm]");
	var payOrderTableForm = electronicDlg.find("li[form=unusualPayOrderTableForm]");
	var unusualBillOrderStep = electronicDlg.find("#unusualBillOrderStep");

	var payDetailDialog = $("#unusualBill-data-div")
	// 时间控件
	var startobj = formObj.find("input[name=startTime]");
	var billSourceObj = formObj.find("select[name=billSource]");
	var businessNoObj = formObj.find("input[name=businessNo]");
	var hisFlowNoObj = formObj.find("input[name=hisFlowNo]");

	var unusuaBillAmount=$('#unusuaBillAmount');
	var unusuaBillOrderNumber=$('#unusuaBillOrderNumber');

	var billSourceExt = "";

	//请求路径
	var apiUrl = '/admin/unusualBill';
	var orgTree;
	var orgJSON;
	var correction = null;
	var billSource = null;
	var followDetailArray = [];
	var imageObjArray = [];
	var exceptionTrade = "all";
	var electronicIsRefundExamine = $("#unusualBillIsRefundExamine").val()
	//var removeFlow;
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
		url : apiUrl+"/data",
		type : 'post',
		dataType : 'json',
		clearForm : false,
		resetForm : false,
		timeout : 3000
	};

	// 报表导出
	function exportData() {
		var orgNo = orgTree.getVal;
		var orgName = orgTree.getText;
		startDate = startobj.val();
		endDate = startobj.val();
		var businessNo = businessNoObj.val();
		var hisFlowNo = hisFlowNoObj.val();
		var billSource = billSourceObj.val();

		var sortName = tableObj2.bootstrapTable('getOptions').sortName||'';
		var sortOrder = tableObj2.bootstrapTable('getOptions').sortOrder;
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

		bootbox.confirm('确定执行此操作?',function(r){
			if (r){
				var orgNo= orgTree.getVal;
				var starttime = "";
				var endtime = "";
				var rangeTime = startobj.val();
				if(rangeTime){
					starttime = rangeTime.split("~")[0];
					endtime = rangeTime.split("~")[1];
				}
				// orgNo: orgNo,
				// startDate: starttime,
				// endDate: endtime,
				// dataSourceType: exceptionTrade,
				// correction: correction,
				// billSource: billSource
				var where = 'sort=' + sortName + '&order=' + sortOrder + '&businessNo=' + businessNo + '&hisFlowNo='
					+ hisFlowNo + '&billSource=' + billSource + '&orgNo=' + orgNo + '&startDate=' + starttime + '&endDate='
					+ endtime + '&dataSourceType=all' + '&correction=' + correction + "&orgName=" + orgName + "&fileName="
					+ starttime + "至" + endtime + orgName + "异常账单汇总报表" + "&workSheetName=" + "异常账单汇总表"
					+ "&offset=0&limit=1000000&patType=" + dataSource;
				var url = apiUrl+'/dcExcel?' + where;
				window.location.href=url;
			}
		});
	}

	// 打印
	function  printData(){
		// $("#printDiv").jqprint();
		// var printData = $('.bootstrap-table').parent().html();
		// var div = $("#printDiv").innerHTML = printData;
		// debugger
		// window.open("");
		// // div.innerHTML = printData;
		// window.document.body.innerHTML = printData;
		// // 开始打印
		// window.print();
		// window.location.reload(false);

		// var headstr = "<html><head><title></title></head><body>";
		// var footstr = "</body></html>";
		// // 获得 div 里的所有 html 数据
		// var printData = document.getElementById("printDiv").innerHTML;
		// var oldstr = document.body.innerHTML;
		// document.body.innerHTML = headstr + printData + footstr;
		// window.print();
		// document.body.innerHTML = oldstr;

		//打印
		$("#printDiv").printThis({
			debug: false,
			// importCSS: false,
			importStyle: false,
			printContainer: true,
			loadCSS: ["/assets/css/bootstrap.min.css"],
			pageTitle: "异常账单",
			removeInline: true,
			printDelay: 333,
			header: null,
			formValues: true
		});
	}

	function searchPayDetail(){
		var orgNo = orgTree.getVal;
		// 数据来源  mz-门诊  zy-住院
		var dataSource =  $("#dataSourceTree").val();
		if(dataSource == undefined || dataSource == null || dataSource == '全部'){
			dataSource = "";
		} else if (dataSource == '住院'){
			dataSource = 'zy';
		} else {
			dataSource = 'mz';
		}
		$.ajax({
			url:"/admin/electronic/data",
			type:"POST",
			data: {
				"orgNo": orgNo,
				"startDate": "",
				"patType": dataSource
			},
			dataType:"json",
			success:function(result){
				putDataPayDetail1(result.data.payDetailMap);
			}
		});
	}

	function putDataPayDetail1(detailInfo){
		var txt = "";
		for(var billSource in detailInfo){
			var params = detailInfo[billSource];
			var name = formatterBillSource1(billSource);
			txt += " <option value='"+name+"'>"+name+"</option>";
		}
		// 支付详情数据为空的时候隐藏
		if (txt === "") {
			billSourceObj.hide();
		} else {
			txt = "<option value=''>全部</option> "+txt;
			billSourceObj.show()
			billSourceObj.html(txt);
		}
	}
	function formatterBillSource1(val) {
		var typeJSON = $('#electronicType').val();
		var typesJSON = JSON.parse(typeJSON);
		if(typesJSON[val]==null || typesJSON[val]==undefined){
			return typesJSON[val];
		}
		return typesJSON[val];
	}
	//查询
	function search(th) {
		var orgNo = orgTree.getVal;
		var starttime = "";
		var endtime = "";
		var rangeTime = startobj.val();
		var businessNo = businessNoObj.val();
		var hisFlowNo = hisFlowNoObj.val();
		if(rangeTime){
			starttime = rangeTime.split("~")[0];
			endtime = rangeTime.split("~")[1];
		}
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}

		// searchSumary(orgNo, startDate);

		if ($("#correction").is(':checked')) {
			correction = 1;
		} else {
			correction = 2;
		}
		billSource = billSourceObj.val();
		// 数据来源  mz-门诊  zy-住院
		var dataSource = $("#dataSourceTree").val();
		if (dataSource == undefined || dataSource == null || dataSource == '全部') {
			dataSource = "";
		} else if (dataSource == '住院') {
			dataSource = 'zy';
		} else {
			dataSource = 'mz';
		}

		tableObj2.bootstrapTable('refreshOptions', {
			resizable: true,
			pageNumber : 1,
			queryParams : function(params) {
				var queryObj = {
					orgNo : orgNo,
					startDate : starttime,
					endDate : endtime,
					correction : correction,
					billSource:billSource,
					businessNo:businessNo,
					hisFlowNo:hisFlowNo,
					dataSourceType: exceptionTrade,
					patType:dataSource
				};
				var query = $.extend(true, params, queryObj);
				return query;
			},
			onPreBody:function(data){
				$(th).button('loading');
			},
			onLoadSuccess:function(data){
				unusuaBillOrderNumber.text((data.total||0)+' 条');
				loadDiffAmount();
				$(th).button("reset");
			}

		});
	}
	function loadDiffAmount(){
		var orgNo = orgTree.getVal;
		var starttime = "";
		var endtime = "";
		var rangeTime = startobj.val();
		var businessNo = businessNoObj.val();
		var hisFlowNo = hisFlowNoObj.val();
		if(rangeTime){
			starttime = rangeTime.split("~")[0];
			endtime = rangeTime.split("~")[1];
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
		billSource = billSourceObj.val();
		$.ajax({
			url:apiUrl+'/diffAmount',
			type:"POST",
			data:{
				orgNo : orgNo,
				startDate : starttime,
				endDate : endtime,
				correction : correction,
				billSource:billSource,
				businessNo:businessNo,
				hisFlowNo:hisFlowNo,
				dataSourceType: exceptionTrade,
				patType: dataSource,
			},
			dataType:"json",
			success:function(result){
				unusuaBillAmount.text(result.data.diffAmount+" 元")
			}
		})
	}
	function putDataPayDetail(detailInfo){
		var txt = "";
		for(var billSource in detailInfo){
			var params = detailInfo[billSource];
			txt += " <li onclick='app.admin.electronic.chsPayMethods("+JSON.stringify(params)+", this)' >"+formatterBillSource(billSource)+"</li>";
		}
		// 支付详情数据为空的时候隐藏
		if (txt === "") {
			payDetailDialog.find(".separator").hide();
			payDetailDialog.find(".pay-type-list").hide();
			payDetailDialog.find(".pay-methods").hide();
		} else {
			payDetailDialog.find(".separator").show();
			payDetailDialog.find(".pay-type-list").show();
			payDetailDialog.find(".pay-methods").show();
			payDetailDialog.find(".pay-type-list").html(txt);
			payDetailDialog.find(".pay-type-list").find("li:first").click();
		}
	}

	var imgFileJSON = {'0149':'zgyh', '0249':'wx', '0349':'zfb', '1649':'jhzf', '0559':'ybzf'};
	function chsPayMethods(payArr, th){
		// 改变选中颜色
		var obj = payDetailDialog.find(".pay-type-list").find("li");
		$.each(obj, function(o){
			$(obj[o]).removeClass("color-link");
		});
		$(th).addClass("color-link");

		var txt = "";
		$.each(payArr, function(i){
			if(payArr[i].payType == "sum"){
				txt += '<li class="border">'
					+'<div class="pay-text">'
					+'<p class="pay-title zh">总和</p>'
					+'<div class=" pay-list">'
					+'<p class="pay-left title color-nav">总金额(元)</p>'
					+'<p class="pay-right">'+moneyFormat(payArr[i].payAmountSum)+'</p>'
					+' </div>'
					+'<div class=" pay-list">'
					+'<p class="pay-left title color-nav">支付笔数</p>'
					+'<p class="pay-right">'+payArr[i].realPayAcountSum+'</p>'
					+' </div>'
					+'<div class=" pay-list">'
					+'<p class="pay-left title color-nav">退款笔数</p>'
					+'<p class="pay-right">'+payArr[i].refundAcountSum+'</p>'
					+'</div>'
					+' </div>'
					+'</li>';
			}else{
				txt += '<li class="border">'
					+'<div class="pay-text">'
					+'<p class="pay-title '+imgFileJSON[payArr[i].payType]+'">'+formatterPayType(payArr[i].payType)+'</p>'
					+'<div class=" pay-list">'
					+'<p class="pay-left title color-nav">实收总金额(元)</p>'
					+'<p class="pay-right">'+moneyFormat(payArr[i].payAmount)+'</p>'
					+' </div>'
					+'<div class=" pay-list">'
					+'<p class="pay-left title color-nav">支付笔数</p>'
					+'<p class="pay-right">'+payArr[i].realPayAcount+'</p>'
					+' </div>'
					+'<div class=" pay-list">'
					+'<p class="pay-left title color-nav">退款笔数</p>'
					+'<p class="pay-right">'+payArr[i].refundAcount+'</p>'
					+'</div>'
					+' </div>'
					+'</li>';
			}

		});
		$(".pay-methods").html(txt);
		var liText = $(th).text();
		$("#billSource").val(liText);
		showExceptionTrade("all");
	}

	function putDataSumary(recResult){
		payDetailDialog.find(".payAllAmount").html(moneyFormat(recResult.payAllAmount));
		payDetailDialog.find(".payAcount").html(numberFormat(recResult.payAcount));
		payDetailDialog.find(".hisAllAmount").html(moneyFormat(recResult.hisAllAmount));
		payDetailDialog.find(".hisPayAcount").html(numberFormat(recResult.hisPayAcount));
		payDetailDialog.find(".settlementAmount").html(moneyFormat(recResult.settlementAmount));
		payDetailDialog.find(".settlementPayAcount").html(numberFormat(recResult.settlementPayAcount));
		payDetailDialog.find(".tradeDiffAmount").html(moneyFormat(recResult.untreatedThirdAmount - recResult.untreatedHisAmount));
//		payDetailDialog.find(".tradeDiffAmount").html(moneyFormat(recResult.untreatedThirdAmount+recResult.untreatedHisAmount));
//		payDetailDialog.find(".tradeDiffAmount").html(moneyFormat(recResult.tradeDiffAmount));
		payDetailDialog.find(".tradeDiffPayAcount").html(numberFormat(recResult.tradeDiffPayAcount));
		payDetailDialog.find(".untreatedThirdAcount").html(recResult.untreatedThirdAcount);
		payDetailDialog.find(".untreatedHisAcount").html(recResult.untreatedHisAcount);
		payDetailDialog.find(".untreatedThirdAmount").html(moneyFormat(recResult.untreatedThirdAmount));
		payDetailDialog.find(".untreatedHisAmount").html(moneyFormat(recResult.untreatedHisAmount));

	}
	function numberFormat(val){
		if(isNaN(val)){
			return new Number(0);
		}
		return new Number(val);
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
			},
			callback: {
				onClick: function(event, data) {
					searchPayDetail();
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
				orgTree = $("#unusualBillOrgTree").ztreeview({
					name : 'name',
					key : 'code',
					//是否
					clearable : true,
					expandAll : true,
					data : msg,
				}, setting);
				orgTree.updateCode(msg[0].id, msg[0].code);

				var accountOrgNo = $("#electronicOrgNoInit").val();
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
					$("#unusualBillOrgTree").parent().parent().parent().show();
				}else{
					$("#unusualBillOrgTree").parent().parent().parent().hide();
				}
				search();
				searchPayDetail();
			}
		});
	}

	function followHandler(val, row, index) {

		if (row.checkState == "1") {
			var obj = {};
			obj.id = row.id;
			obj.description = row.description;
			obj.fileLocation = apiUrl + "/readImage?adress=" + row.fileLocation;
			imageObjArray.push(obj);
//			pubDescription = row.description;
//			pubFileLocation = apiUrl + "/readImage?adress=" + row.fileLocation;
			return "<a title='查看处理详情.' style='cursor:pointer; text-decoration:underline;' onclick='app.admin.following.showHandDealInfo("+row.id+")' >"
				+ row.businessNo + "</a>";
		}
		return row.businessNo;
	}

	function showHandDealInfo(id) {
		for(var i=0;i<imageObjArray.length;i++){
			if(imageObjArray[i].id == id){
				dlgObj3.find("lable[name=description]").html(imageObjArray[i].description);
				dlgObj3.find("img[name=fileLocation]").attr('src', imageObjArray[i].fileLocation);
				dlgObj3.modal('show');
			}
		}
	}

	function formatHandler(val, row, index) {
		var orderState = row.tradeName;
		var id = row.id;
		var checkState = row.checkState;
		var oriCheckState = row.oriCheckState;
		var orgNo = row.orgNo;
		var tradeDatetime =  row.tradeDate;
		var tradetime =  row.tradeTime;
		//账单来源
		var billSource = row.billSource;
		//订单号
		var orderNo = row.businessNo;
		//支付渠道
		var payCode = row.payName;
		var tradeAmount = row.tradeAmount;
		// 01012冲正
		var isCorrection = row.isCorrection;
		var checkState=row.checkState;
		var tradeAmount = row.tradeAmount;
		// 支付业务类型
		var businessType = row.businessType;
		var shopNo = row.shopNo;
		var patType = row.patType;
		var payNo = row.payNo;
		var terminalNo = row.terminalNo;
		var payName=row.payName;
		// 发票号
		var invoiceNo = row.invoiceNo;

		var detail = "  &nbsp;<a href='javascript:;' onclick='app.admin.unusualBil.detail(\""+ row.recHisId + "\", \""+row.recThridId+"\", \""+ row.id + "\", \""+row.businessNo+"\", \""+orderState+"\", \""+billSource+"\")' class='btn btn-info btn-sm m-primary '> 详情 </a>  &nbsp;";

		var electronicRecDetailButtonOnly = $("#unusualBillDetailButtonOnly").val();
		if(electronicRecDetailButtonOnly == 'true'){
			return detail;
		}

		var name = "";
		var disabledStatus = '';
		// 订单号为空，禁止操作订单
		if(null == orderNo || undefined == orderNo || orderNo.length ==0||(checkState!='2'&&checkState!='3'&&oriCheckState != "6")||isCorrection == "01012" ) {
			disabledStatus = 'disabled="true"';
		}
		if(row.checkState==8){//已驳回状态
			disabledStatus = "";
		}
		if ((oriCheckState == "2" || oriCheckState == "6")) {
			var recover = "10";//追回状态
			name = "<button href='javascript:;' "+disabledStatus+" onclick='app.admin.unusualBil.dealFollow(" + id + ",\"" + orderNo + "\"," + recover + ",\"" + tradeAmount
				+ "\", \""+orgNo+"\", \""+tradeDatetime+"\", \""+payName+"\", \""+billSource+"\")' class='btn btn-info btn-sm m-primary'>追回 </button>  &nbsp;" +
				"<button href='javascript:;' "+disabledStatus+" onclick='app.admin.unusualBil.dealFollow(" + id + ",\"" + orderNo + "\"," + '1' + ",\"" + tradeAmount
				+  "\", \""+orgNo+"\", \""+tradeDatetime+"\", \""+payName+"\", \""+billSource+"\")' class='btn btn-info btn-sm m-primary'>抹平 </button>";
		}else if (oriCheckState == "3" ) {
//		}else if (orderState == "0156" && (billSource == "self"||billSource == "self_jd"||billSource == "self_td_jd") && checkState == "3" && isCorrection != "01012") {
			var refundName = "退费";
			if(electronicIsRefundExamine == 1){
				refundName = "退费";
			}
			//是否可以退款
			if(row.requireRefund==undefined||row.requireRefund==null||row.requireRefund==1){
				name = "<button href='javascript:;' "+disabledStatus+" onclick='app.admin.unusualBil.refundHandler(" + id + ",\"" + orgNo + "\",\"" + orderNo + "\",\"" + payCode + "\",\""+ tradeAmount + "\",\""
					+ billSource + "\",\"1\",\"" + tradeDatetime + "\", \"" + businessType + "\",\"" + shopNo + "\",\"" + patType + "\",\"" + tradetime
					+ "\",\"" + payNo + "\",\"" + terminalNo + "\",\"" + invoiceNo + "\",\""+payName+"\")' class='btn btn-info btn-sm m-primary ' >"+refundName+"</button>  &nbsp;" ;
			}

			name+= "<button href='javascript:;' "+disabledStatus+" onclick='app.admin.unusualBil.dealFollow(" + id + ",\"" + orderNo + "\"," + '1' + ",\"" + tradeAmount
				+  "\", \""+orgNo+"\", \""+tradeDatetime+"\", \""+payName+"\", \""+billSource+"\")' class='btn btn-info btn-sm m-primary'>抹平 </button>";
		}


		var col = "<div style='text-align:center;'>" + name + detail + "</div>";

		return col;
	}

	// 订单详情页面
	// function detail(id, businessNo, orderState){
	// 	payOrderTableForm.hide();
	// 	hisOrderTableForm.hide();
	// 	payDlgFormObj.hide();
	// 	hisDlgFormObj.hide();
	// 	var row = tableObj2.bootstrapTable('getRowByUniqueId',id);
	// 	payDlgFormObj.loadDetailReset();
	// 	payDlgFormObj.loadDetail(row);
	// 	var tradeTime = row.tradeTime;
	// 	var orgNo = row.orgNo;
	// 	var apiUrlStr = "/admin/electronic";
	// 	$.ajax({
	// 		type: 'POST',
	// 		url : apiUrlStr+"/exceptionTrade/detail",
	// 		data: {"businessNo":businessNo, "orgNo":orgNo, "orderState":orderState, "tradeTime":tradeTime},
	// 		dataType : "json",
	// 		success : function(result) {
	// 			if(!result.success){
	// 				$.NOTIFY.showError("提醒", result.message, '');
	// 				return ;
	// 			}
	// 			if(result.data.hisOrderState != ""){
	// 				hisDlgFormObj.find("span[data-name=his_titleState]").text("("+result.data.hisOrderState+")");
	// 				hisOrderTableForm.find("span[data-name=his_titleState]").text("("+result.data.hisOrderState+")");
	// 			} else {
	// 				hisDlgFormObj.find("span[data-name=his_titleState]").text("");
	// 				hisOrderTableForm.find("span[data-name=his_titleState]").text("");
	// 			}
	// 			// 支付订单信息
	// 			var thirdOrder = result.data.thirdOrder;
	// 			if(thirdOrder != null && typeof (thirdOrder) != "undefined" && thirdOrder.length >1){
	// 				var data = [];
	// 				for(var i=0;i<thirdOrder.length;i++){
	// 					row = {};
	// 					row['orderNo'] = thirdOrder[i].payFlowNo;
	// 		            row['orderState'] = formatter(thirdOrder[i].orderState);
	// 		            row['patType'] = formatter(thirdOrder[i].patType);
	// 		            row['payAmount'] = moneyFormat(thirdOrder[i].payAmount);
	// 		            row['businessType'] = formatter(thirdOrder[i].payBusinessType);
	// 		            row['tradeDatatime'] = thirdOrder[i].tradeDatatime;
	// 		            row['payType'] = formatter(thirdOrder[i].payType);
	// 		            row['custName'] = thirdOrder[i].custName;
	// 		            row['patientCardNo'] = thirdOrder[i].patientCardNo;
	// 		            data.push(row);
	// 				}
	// 				payOrderTableObj.bootstrapTable('destroy').bootstrapTable({
	// 					resizable: true,
	// 		            data: data
	// 		        });
	// 				payOrderTableForm.show();
	// 			}else{
	// 				if(thirdOrder != null && typeof (thirdOrder) != "undefined"){
	// 					var payFlowNo = thirdOrder[0].payFlowNo;
	// 					payDlgFormObj.find("p[data-name=orderNo]").html(payFlowNo);
	// 					payDlgFormObj.find("p[data-name=orderState]").html(formatter(thirdOrder[0].orderState));
	// 					payDlgFormObj.find("p[data-name=patType]").html(formatter(thirdOrder[0].patType));
	// 					payDlgFormObj.find("p[data-name=payAmount]").html(moneyFormat(thirdOrder[0].payAmount) +"元");
	// 					payDlgFormObj.find("p[data-name=businessType]").html(formatter(thirdOrder[0].payBusinessType));
	// 					payDlgFormObj.find("p[data-name=tradeDatatime]").html(thirdOrder[0].tradeDatatime);
	// 					payDlgFormObj.find("p[data-name=payType]").html(formatter(thirdOrder[0].payType));
	// 					payDlgFormObj.find("p[data-name=custName]").html(thirdOrder[0].custName);
	// 					payDlgFormObj.find("p[data-name=patientCardNo]").html(thirdOrder[0].patientCardNo);
	// 				}else{
	// 					payDlgFormObj.find("p[data-name=orderNo]").html("");
	// 					payDlgFormObj.find("p[data-name=orderState]").html("");
	// 					payDlgFormObj.find("p[data-name=patType]").html("");
	// 					payDlgFormObj.find("p[data-name=payAmount]").html("");
	// 					payDlgFormObj.find("p[data-name=businessType]").html("");
	// 					payDlgFormObj.find("p[data-name=tradeDatatime]").html("");
	// 					payDlgFormObj.find("p[data-name=payType]").html("");
	// 					payDlgFormObj.find("p[data-name=custName]").html("");
	// 					payDlgFormObj.find("p[data-name=patientCardNo]").html("");
	// 				}
	// 				payDlgFormObj.show();
	// 			}
	//
	// 			// HIS订单信息
	// 			var hisOrder = result.data.hisOrder;
	// 			if(hisOrder != null && typeof (hisOrder) != "undefined" && hisOrder.length >1){
	// 				var data = [];
	// 				for(var i=0;i<hisOrder.length;i++){
	// 					row = {};
	// 					row['patName'] = hisOrder[i].patientName;
	// 		            row['patType'] = formatter(hisOrder[i].patientType);
	// 		            row['patNumber'] = hisOrder[i].patientNo;
	// 		            row['hisFlowNo'] = hisOrder[i].hisNo;
	// 		            row['payFlowNo'] = hisOrder[i].payNo;
	// 		            row['payType'] = formatter(hisOrder[i].payType);
	// 		            row['orderState'] = formatter(hisOrder[i].orderState);
	// 		            row['tradeTime'] = hisOrder[i].tradeTime;
	// 		            row['tradeAmount'] = moneyFormat(hisOrder[i].tradeAmount);
	// 		            data.push(row);
	// 				}
	// 				hisOrderTableObj.bootstrapTable('destroy').bootstrapTable({
	// 					resizable: true,
	// 		            data: data
	// 		        });
	// 				hisOrderTableForm.show();
	// 			}else{
	// 				if (hisOrder != null && typeof (hisOrder) != "undefined") {
	// 					hisDlgFormObj.find("p[data-name=patName]").html(hisOrder[0].patientName);
	// 					hisDlgFormObj.find("p[data-name=patType]").html(formatter(hisOrder[0].patientType));
	// 					hisDlgFormObj.find("p[data-name=patNumber]").html(hisOrder[0].patientNo);
	// 					hisDlgFormObj.find("p[data-name=hisFlowNo]").html(hisOrder[0].hisNo);
	// 					hisDlgFormObj.find("p[data-name=payFlowNo]").html(hisOrder[0].payNo);
	// 					hisDlgFormObj.find("p[data-name=payType]").html(formatter(hisOrder[0].payType));
	// 					hisDlgFormObj.find("p[data-name=orderState]").html(formatter(hisOrder[0].orderState));
	// 					hisDlgFormObj.find("p[data-name=tradeTime]").html(hisOrder[0].tradeTime);
	// 					hisDlgFormObj.find("p[data-name=tradeAmount]").html(moneyFormat(hisOrder[0].tradeAmount) + " 元");
	// 				}else{
	// 					hisDlgFormObj.find("p[data-name=patName]").html("");
	// 					hisDlgFormObj.find("p[data-name=patType]").html("");
	// 					hisDlgFormObj.find("p[data-name=patNumber]").html("");
	// 					hisDlgFormObj.find("p[data-name=hisFlowNo]").html("");
	// 					hisDlgFormObj.find("p[data-name=payFlowNo]").html("");
	// 					hisDlgFormObj.find("p[data-name=payType]").html("");
	// 					hisDlgFormObj.find("p[data-name=orderState]").html("");
	// 					hisDlgFormObj.find("p[data-name=tradeTime]").html("");
	// 					hisDlgFormObj.find("p[data-name=tradeAmount]").html("");
	// 				}
	// 				hisDlgFormObj.show();
	// 			}
	// 			// 处理说明
	// 			var dealDetail = result.data.dealDetail;
	// 			if (dealDetail != null && typeof (dealDetail) != "undefined") {
	// 				payDlgFormObj.find("p[data-name=description]").html(
	// 						dealDetail.description);
	// 				payDlgFormObj.find("a[data-name=descImg]").attr("value",apiUrl + "/readImage?adress=" + dealDetail.fileLocation);
	// 				$("#handle-description").show();
	// 			}else{
	// 				$("#handle-description").hide();
	// 			}
	// 			electronicDlg.modal('show');
	// 		}
	// 	});
	//
	// }

	function loadPayStep(businessNo, billSource, orgNo, recHisId, recThirdId) {
		if(!businessNo){
			unusualBillOrderStep.hide();
			return;
		}
		var innerObj = unusualBillOrderStep.find(".carousel-inner");
		$.ajax({
			url:"/admin/electronic/payStep",
			data: {
				businessNo: businessNo,
				orgCode: orgNo,
				billSource: billSource,
				recHisId: recHisId,
				recThirdId: recThirdId
			},
			dataType:'json',
			success:function(res){
				innerObj.html('');
				if(!res.data){
					unusualBillOrderStep.hide();
					return;
				}else{
					unusualBillOrderStep.show();
				}
				var data = res.data;

				var html = "";
				var ul = "";
				var pageSize = 6;
				if(data.length<pageSize){
					unusualBillOrderStep.find('.electronic-rec-pay-step-left').hide();
					unusualBillOrderStep.find('.electronic-rec-pay-step-right').hide();
				}else{
					unusualBillOrderStep.find('.electronic-rec-pay-step-left').show();
					unusualBillOrderStep.find('.electronic-rec-pay-step-right').show();
				}
				$.each(data,function(index,val){
					var typeClass="";
					var payType = "";
					var state="";
					var amount = formatMoney(val.payAmount)
					if(val.billSource=='his'){
						typeClass="pay-step-his";
						payType="HIS";
						state=val.orderState=='0256'?'退款':'入账'
					}else if(val.refundType!=0){
						typeClass="pay-step-refund";
						if(val.refundType==1){
							payType=formatterVal(val.payType).replace(/支付$/gi,'');
						}else{
							payType="现金";
						}
						state=val.orderState=='0256'?'退款':'支付'
					}else{
						typeClass="pay-step-thrid";
						payType=formatterVal(val.payType).replace(/支付$/gi,'');
						state=val.orderState=='0256'?'退款':'支付'
					}

					ul+="<li class='electronic-rec-pay-step-item "+typeClass+"'>" +
						"<div class='electronic-rec-pay-item-text'>"+
						"<div>"+(payType+state)+"</div>" +
						"<div>"+amount+"元</div>" +
						"</div>" +
						"<div class='electronic-rec-pay-step-item-point'><div></div></div>" +
						"<div class='electronic-rec-pay-step-item-time'>"+val.tradeDate+"</div>" +
						"</li>";
					if(index%pageSize==(pageSize-1)||index==data.length-1){
						ul+='</ul>'
						var lastClass = "";
						if(index<data.length-1){
							var next = data[index+1];
							if(next.billSource=='his'){
								lastClass="last-his"
							}else if(next.refundType!=0){
								lastClass="last-refund"
							}else{
								lastClass="last-thrid"
							}
						}
						if(index<pageSize){
							lastClass+=' active'
						}
						ul = '<ul class="electronic-rec-pay-step '+lastClass+' item ">'+ul+'</ul>';
						html+=ul;
						ul="";
					}
				});
				innerObj.html(html);
			}
		});
	}

	function detail(recHisId,recThirdId,id, businessNo, orderState,billSource){
		if(recHisId == "null"){
			recHisId = "";
		}
		if(recThirdId == "null"){
			recThirdId = "";
		}
		var row = tableObj2.bootstrapTable('getRowByUniqueId',id);
		var orgNo = row.orgNo;
		loadPayStep(businessNo,billSource,orgNo, recHisId, recThirdId);
		payOrderTableForm.hide();
		hisOrderTableForm.hide();
		payDlgFormObj.hide();
		hisDlgFormObj.hide();
		payDlgFormObj.loadDetailReset();
		payDlgFormObj.loadDetail(row);
		var tradeTime = row.tradeTime;
		var apiUrlStr = "/admin/electronic";
		$.ajax({
			type: 'POST',
			url : apiUrlStr+"/exceptionTrade/detail",
			data: {"recHisId":recHisId,"recThirdId":recThirdId,"businessNo":businessNo,"billSource":billSource, "orgNo":orgNo, "orderState":orderState, "tradeTime":tradeTime},
			dataType : "json",
			success : function(result) {
				if(!result.success){
					$.NOTIFY.showError("提醒", result.message, '');
					return ;
				}
				if(result.data.hisOrderState != ""){
					hisDlgFormObj.find("span[data-name=his_titleState]").text("("+result.data.hisOrderState+")");
					hisOrderTableForm.find("span[data-name=his_titleState]").text("("+result.data.hisOrderState+")");
				} else {
					hisDlgFormObj.find("span[data-name=his_titleState]").text("");
					hisOrderTableForm.find("span[data-name=his_titleState]").text("");
				}
				var orderLength = 0;
				// 支付订单信息
				var thirdOrder = result.data.thirdOrder;
				if(thirdOrder != null && typeof (thirdOrder) != "undefined" && thirdOrder.length >1){
					orderLength = thirdOrder.length;
					var data = [];
					for(var i=0;i<thirdOrder.length;i++){
						row = {};
						row['orderNo'] = thirdOrder[i].payFlowNo;
						row['orderState'] = formatter(thirdOrder[i].orderState);
						row['patType'] = formatter(thirdOrder[i].patType);
						row['payAmount'] = moneyFormat(thirdOrder[i].payAmount);
						row['businessType'] = formatter(thirdOrder[i].payBusinessType);
						row['tradeDatatime'] = thirdOrder[i].tradeDatatime;
						row['payType'] = formatter(thirdOrder[i].payType);
						row['custName'] = thirdOrder[i].custName;
						row['patientCardNo'] = thirdOrder[i].patientCardNo;
						// 商户号
						row['payShopNo'] = thirdOrder[i].payShopNo;
						// 商户订单号
						row['shopFlowNo'] = thirdOrder[i].shopFlowNo;
						// 终端号
						row['payTermNo'] = thirdOrder[i].payTermNo;
						// 参考号
						row['referenceNum'] = thirdOrder[i].referenceNum;
						// 授权码
						row['authoriCode'] = thirdOrder[i].authoriCode;
						// 就诊卡号
						row['patientCardNo'] = thirdOrder[i].patientCardNo;
						data.push(row);
					}
					payOrderTableObj.bootstrapTable('destroy').bootstrapTable({
						resizable: true,
						data: data
					});
					payOrderTableForm.show();
				}else{
					if(thirdOrder != null && typeof (thirdOrder) != "undefined" && thirdOrder.length != 0){
						orderLength = 1;
						var payFlowNo = thirdOrder[0].payFlowNo;
						payDlgFormObj.find("p[data-name=thirdDetail_orderNo]").html(payFlowNo);
						payDlgFormObj.find("p[data-name=thirdDetail_orderState]").html(formatter(thirdOrder[0].orderState));
						payDlgFormObj.find("p[data-name=thirdDetail_patType]").html(formatter(thirdOrder[0].patType));
						payDlgFormObj.find("p[data-name=thirdDetail_payAmount]").html(moneyFormat(thirdOrder[0].payAmount) +"元");
						payDlgFormObj.find("p[data-name=thirdDetail_businessType]").html(formatter(thirdOrder[0].payBusinessType));
						payDlgFormObj.find("p[data-name=thirdDetail_tradeDatatime]").html(thirdOrder[0].tradeDatatime);
						payDlgFormObj.find("p[data-name=thirdDetail_payType]").html(formatter(thirdOrder[0].payType));
						payDlgFormObj.find("p[data-name=thirdDetail_custName]").html(thirdOrder[0].custName);
						payDlgFormObj.find("p[data-name=patientCardNo]").html(thirdOrder[0].patientCardNo);
						payDlgFormObj.find("p[data-name=thirdDetail_businessFlowNo]").html(thirdOrder[0].shopFlowNo);
						// 商户号
						payDlgFormObj.find("p[data-name=payShopNo]").html(thirdOrder[0].payShopNo);
						// 商户订单号
						payDlgFormObj.find("p[data-name=shopFlowNo]").html(thirdOrder[0].shopFlowNo);
						// 终端号
						payDlgFormObj.find("p[data-name=payTermNo]").html(thirdOrder[0].payTermNo);
						// 参考号
						payDlgFormObj.find("p[data-name=referenceNum]").html(thirdOrder[0].referenceNum);
						// 授权码
						payDlgFormObj.find("p[data-name=thirdDetail_authoriCode]").html(thirdOrder[0].authoriCode);
						//就诊卡号
						payDlgFormObj.find("p[data-name=patientCardNo]").html(thirdOrder[0].patientCardNo);
					}else{
						payDlgFormObj.find("p[data-name=thirdDetail_orderNo]").html("");
						payDlgFormObj.find("p[data-name=thirdDetail_orderState]").html("");
						payDlgFormObj.find("p[data-name=thirdDetail_patType]").html("");
						payDlgFormObj.find("p[data-name=thirdDetail_payAmount]").html("");
						payDlgFormObj.find("p[data-name=thirdDetail_businessType]").html("");
						payDlgFormObj.find("p[data-name=thirdDetail_tradeDatatime]").html("");
						payDlgFormObj.find("p[data-name=thirdDetail_payType]").html("");
						payDlgFormObj.find("p[data-name=thirdDetail_custName]").html("");
						payDlgFormObj.find("p[data-name=patientCardNo]").html("");
						// 商户号
						payDlgFormObj.find("p[data-name=payShopNo]").html("");
						// 商户订单号
						payDlgFormObj.find("p[data-name=shopFlowNo]").html("");
						// 终端号
						payDlgFormObj.find("p[data-name=payTermNo]").html("");
						// 参考号
						payDlgFormObj.find("p[data-name=referenceNum]").html("");
						payDlgFormObj.find("p[data-name=thirdDetail_businessFlowNo]").html("");
						// 授权码
						payDlgFormObj.find("p[data-name=thirdDetail_authoriCode]").html("");
						// 就诊卡号
						payDlgFormObj.find("p[data-name=patientCardNo]").html("");
					}
					payDlgFormObj.show();
				}

				// HIS订单信息
				var hisOrder = result.data.hisOrder;
				if(hisOrder != null && typeof (hisOrder) != "undefined" && hisOrder.length >1){
					var data = [];
					for(var i=0;i<hisOrder.length;i++){
						row = {};
						row['patName'] = hisOrder[i].patientName;
						row['patType'] = formatter(hisOrder[i].patientType);
						row['patNumber'] = hisOrder[i].patientNo;
						row['hisFlowNo'] = hisOrder[i].hisNo;
						row['payFlowNo'] = hisOrder[i].payNo;
						row['payType'] = formatter(hisOrder[i].payType);
						row['orderState'] = formatter(hisOrder[i].orderState);
						row['tradeTime'] = hisOrder[i].tradeTime;
						row['tradeAmount'] = moneyFormat(hisOrder[i].tradeAmount);
						// 商户订单号
						row['shopFlowNo'] = hisOrder[i].shopFlowNo;
						// 商户号
						row['payShopNo'] = hisOrder[i].payShopNo;
						// 终端号
						row['terminalNo'] = hisOrder[i].terminalNo;
						// 参考号
						row['referenceNum'] = hisOrder[i].referenceNum;
						// 就诊卡号
						row['visitNumber'] = hisOrder[i].visitNumber;
						data.push(row);
					}
					hisOrderTableObj.bootstrapTable('destroy').bootstrapTable({
						resizable: true,
						data: data
					});
					hisOrderTableForm.show();
				}else{
					if (hisOrder != null && typeof (hisOrder) != "undefined" &&hisOrder.length!=0) {
						hisDlgFormObj.find("p[data-name=patName]").html(hisOrder[0].patientName);
						hisDlgFormObj.find("p[data-name=patType]").html(formatter(hisOrder[0].patientType));
						hisDlgFormObj.find("p[data-name=patNumber]").html(hisOrder[0].patientNo);
						hisDlgFormObj.find("p[data-name=hisFlowNo]").html(hisOrder[0].hisNo);
						hisDlgFormObj.find("p[data-name=payFlowNo]").html(hisOrder[0].payNo);
						hisDlgFormObj.find("p[data-name=payType]").html(formatter(hisOrder[0].payType));
						hisDlgFormObj.find("p[data-name=orderState]").html(formatter(hisOrder[0].orderState));
						hisDlgFormObj.find("p[data-name=tradeTime]").html(hisOrder[0].tradeTime);
						hisDlgFormObj.find("p[data-name=tradeAmount]").html(moneyFormat(hisOrder[0].tradeAmount) + " 元");
						// 商户订单号
						hisDlgFormObj.find("p[data-name=shopFlowNo]").html(hisOrder[0].shopFlowNo);
						// 商户号
						hisDlgFormObj.find("p[data-name=payShopNo]").html(hisOrder[0].payShopNo);
						// 终端号
						hisDlgFormObj.find("p[data-name=terminalNo]").html(hisOrder[0].terminalNo);
						// 参考号
						hisDlgFormObj.find("p[data-name=referenceNum]").html(hisOrder[0].referenceNum);
						hisDlgFormObj.find("p[data-name=visitNumber]").html(hisOrder[0].visitNumber);

					}else{
						hisDlgFormObj.find("p[data-name=patName]").html("");
						hisDlgFormObj.find("p[data-name=patType]").html("");
						hisDlgFormObj.find("p[data-name=patNumber]").html("");
						hisDlgFormObj.find("p[data-name=hisFlowNo]").html("");
						hisDlgFormObj.find("p[data-name=payFlowNo]").html("");
						hisDlgFormObj.find("p[data-name=payType]").html("");
						hisDlgFormObj.find("p[data-name=orderState]").html("");
						hisDlgFormObj.find("p[data-name=tradeTime]").html("");
						hisDlgFormObj.find("p[data-name=tradeAmount]").html("");
						// 商户订单号
						hisDlgFormObj.find("p[data-name=shopFlowNo]").html("");
						// 商户号
						hisDlgFormObj.find("p[data-name=payShopNo]").html("");
						// 终端号
						hisDlgFormObj.find("p[data-name=terminalNo]").html("");
						// 参考号
						hisDlgFormObj.find("p[data-name=referenceNum]").html("");
						hisDlgFormObj.find("p[data-name=visitNumber]").html("");
					}
					hisDlgFormObj.show();
				}
				// 处理说明
				var dealDetail = result.data.dealDetail;
				if (dealDetail != null && typeof (dealDetail) != "undefined") {
					payDlgFormObj.find("p[data-name=description]").html(
						dealDetail.description);
					$(".handle-description").show();
					if(dealDetail.fileLocation){
						$(".handle-description .electronic-desc-img").show()
						payDlgFormObj.find("a[data-name=descImg]").attr("value","/admin/electronic/readImage?adress=" + dealDetail.fileLocation);
					}else{
						$(".handle-description .electronic-desc-img").hide()
					}
				}else{
					$(".handle-description").hide();
				}
				electronicDlg.modal('show');
			}
		});
	}

	// 查看图片
	function showImg(th){
		window.open($(th).attr("value"));
	}

	//抹平
	function dealFollow(id, orderNo,checkState,tradeAmount, orgNo, tradeDatetime) {
		formObjDlg[0].reset();
		$("#recover").prop("hidden",true);
		$("#recoverSeason").prop("hidden",true);
		$("#floating").prop("hidden",true);
		$("#floatingSeason").prop("hidden",true);
		formObjDlg.find("input[name=payFlowNo]").val(orderNo);
		formObjDlg.find("input[name=checkState]").val(checkState);
		formObjDlg.find("input[name=tradeAmount]").val(tradeAmount);
		formObjDlg.find("input[name=orgCode]").val(orgNo);
		formObjDlg.find("input[name=tradeDatetime]").val(tradeDatetime);
		if(checkState == 10){
			$("#recover").prop("hidden",false);
			$("#recoverSeason").prop("hidden",false);
		}else{
			$("#floating").prop("hidden",false);
			$("#floatingSeason").prop("hidden",false);
		}
		dlgObj2.modal('show');
	}

	function save() {
		var objButton=$("#save");
		objButton.attr('disabled',true);
		var ajax_option = {
			url : "/admin/electronic" + "/dealFollow",
			type : 'post',
			success : function(result) {
				if (JSON.parse(result).success) {

					search();
					dlgObj2.modal('hide');
					$.NOTIFY.showSuccess("提醒", "处理成功", '');

//					showExceptionTrade(exceptionTrade);
//					dlgObj2.modal('hide');
//					$.NOTIFY.showSuccess("提醒", "处理成功", '');
//					updateDifferenceAmount();
				} else {
					$.NOTIFY.showError("错误", JSON.parse(result).message, '');
				}
				objButton.attr('disabled',false);
			}
		};
		formObjDlg.ajaxSubmit(ajax_option);
	}

	function operation(value, row, index) {
		if(null != row.payAllAmount){
			return "<a style='text-decoration:underline;cursor:pointer;' onclick='app.admin.following.showFollowDetailDlg()'>"+new Number(row.payAllAmount).toFixed(2)+"</a>";
		}
		return "0.00";
	}

	function showFollowDetailDlg() {
		$.ajax({
			type: 'POST',
			url : apiUrl+"/data",
			data: {"orgNo":orgTree.getVal,"startDate":startobj.val()},
			dataType : "json",
			success : function(result) {
				if (result.success) {
					followDetailDlg.find("div[name=electronicFollowDetailDlgBody]").html("");
					if (result.data != null && result.data.length > 0) {
						for (var i = 0; i < result.data.length; i++) {
							var typeJSON = $('#electronicType').val();
							var typesJSON = JSON.parse(typeJSON);
							var billSource = typesJSON[result.data[i].bill_source];
							var payType = typesJSON[result.data[i].rec_pay_type];
							var name = payType;
							if(undefined != billSource){
								name = billSource+" - "+payType;
							}
							var amount = result.data[i].pay_amount;

							var html = "<div class=\"form-group\">";
							html = html + "<label class=\"col-sm-4 control-label\">"
								+ name + "：</label>";
							html = html + "<div class=\"col-sm-8\">";
							html = html + "<label class=\"col-sm-9 control-label\">"
								+ new Number(amount).toFixed(2) + "</label>";
							html = html + "</div></div>";
							followDetailDlg.find("div[name=electronicFollowDetailDlgBody]").prepend(html);
						}
						followDetailDlg.modal('show');
					}
				}
			}
		});


	}

	function refundSave(){
		var objButton=$("#refundSave");
		if ((billSourceExt == "8391" || billSourceExt == "8301") && $("#payType").val() == "0149") {
			if ($("#pjhId").val() == null || $("#pjhId").val() == "") {
				$.NOTIFY.showError("错误", "中行订单请输入票据号", '');
				return;
			}
			if ($("#sysNoId").val() == null || $("#sysNoId").val() == "") {
				$.NOTIFY.showError("错误", "中行订单请输入系统订单号", '');
				return;
			}
			if ($("#cashierId").val() == null || $("#cashierId").val() == "") {
				$.NOTIFY.showError("错误", "中行订单请输入操作员", '');
				return;
			}
			if ($("#counterNoId").val() == null || $("#counterNoId").val() == "") {
				$.NOTIFY.showError("错误", "中行订单请输入柜员号", '');
				return;
			}
			if ($("#bocNoId").val() == null || $("#bocNoId").val() == "") {
				$.NOTIFY.showError("错误", "中行订单请输入流水号", '');
				return;
			}
		}
		// 退款金额
		var refundAmount = $("#refundAmount").val();
		// 订单金额
		var tradeAmount = $( "#tradeAmount").val();
		if (eval(refundAmount) > eval(tradeAmount)) {
			$.NOTIFY.showError("错误", "退款金额必须小于等于订单金额", '');
			return;
		}
		// 退费金额
		refundFormObjDlg.find("input[name=tradeAmount]").val(refundAmount);
		// 订单金额
		refundFormObjDlg.find("input[name=payAmount]").val(tradeAmount);
		objButton.button('loading');
		var url ="/admin/refund/data/refund";
		var ajax_option = {
			url : url,
			type : 'post',
			success : function(result) {
				if (JSON.parse(result).success) {
					search()
					dlgObj4.modal('hide');
					$.NOTIFY.showSuccess("提醒", "处理成功", '');
				} else {
					$.NOTIFY.showError("错误", JSON.parse(result).message, '');
				}
				objButton.button('reset');
			},
			error:function(){
				objButton.button('reset');
			},
			complete:function(){
				objButton.button('reset');
			}
		};
		refundFormObjDlg.ajaxSubmit(ajax_option);
	}

	//退费
	function refundHandler(val, orgNo,orderNo, payCode, tradeAmount ,billSource,state,time, businessType, shopNo,
						   patType,tradetime,payNo, terminalNo, invoiceNo,payType) {
		refundFormObjDlg[0].reset();
		refundFormObjDlg.find("input[name=id]").val(val);
		refundFormObjDlg.find("input[name=orderNo]").val(orderNo);
		refundFormObjDlg.find("input[name=payCode]").val(payCode);
		refundFormObjDlg.find("input[name=tradeAmount]").val(tradeAmount);
		refundFormObjDlg.find("input[id=refundAmount]").val(tradeAmount);
		refundFormObjDlg.find("input[name=billSource]").val(billSource);
//		var orgNo= orgTree.getVal;
		refundFormObjDlg.find("input[name=orgCode]").val(orgNo);
		refundFormObjDlg.find("input[name=state]").val(state);
		refundFormObjDlg.find("input[name=time]").val(time);
		refundFormObjDlg.find("input[name=tradetime]").val(tradetime);

		refundFormObjDlg.find("input[name=businessType]").val(businessType);
		refundFormObjDlg.find("input[name=shopNo]").val(shopNo);
		refundFormObjDlg.find("input[name=patType]").val(patType);
		refundFormObjDlg.find("input[name=payNo]").val(payNo);
		refundFormObjDlg.find("input[name=terminalNo]").val(terminalNo);
		// 发票号
		refundFormObjDlg.find("input[name=invoiceNo]").val(invoiceNo);
		refundFormObjDlg.find("input[name=payType]").val(payType);

		$("#payType").val(payType);
		billSourceExt = billSource;

		$("#sqm").hide();
		$("#pjh").hide();
		$("#sysNo").hide();
		$("#cashier").hide();
		$("#counterNo").hide();
		$("#bocNo").hide();

		//中行退费弹框
		if((billSource=="8391" || billSource=="8301") && payType=='0149'){
			$("#sqmId").val("");
			$("#pjhId").val("");
			$("#sysNoId").val("");
			$("#cashierId").val("");
			$("#counterNoId").val("");
			$("#bocNoId").val("");
			$("#sqm").show();
			$("#pjh").show();
			$("#sysNo").show();
			$("#cashier").show();
			$("#counterNo").show();
			$("#bocNo").show();
		}
		dlgObj4.modal('show');
	}
	function formatter(val) {
		var typeJSON = $('#electronicType').val();
		var typesJSON = JSON.parse(typeJSON);
		if(typesJSON[val]==null || typesJSON[val]==undefined){
			return typesJSON[val]||'';
		}
		return '<p data-toggle="tooltip" title=\"'+ typesJSON[val] +'\">' + typesJSON[val] +'</p>'
//		return typesJSON[val];
	}
	function formatterVal(val) {
		var typeJSON = $('#electronicType').val();
		var typesJSON = JSON.parse(typeJSON);
		if(typesJSON[val]==null || typesJSON[val]==undefined){
			return typesJSON[val];
		}
//		return '<p data-toggle="tooltip" title=\"'+ typesJSON[val] +'\">' + typesJSON[val] +'</p>'
		return typesJSON[val];
	}
	function formatterBillSource(val) {
		var typeJSON = $('#electronicType').val();
		var typesJSON = JSON.parse(typeJSON);
		if(typesJSON[val]==null || typesJSON[val]==undefined){
			return typesJSON[val];
		}
		return typesJSON[val];
	}

	function formatterPayType(val) {
		var typeJSON = $('#electronicType').val();
		var typesJSON = JSON.parse(typeJSON);
		if(typesJSON[val]==null || typesJSON[val]==undefined){
			return typesJSON[val];
		}
		return typesJSON[val];
	}

	function orgFormatter(val) {
		var orgJSONs = $.parseJSON(orgJSON);
		return "<div style=\"word-wrap:break-word;word-break:break-all\">"
			+ orgJSONs[val] + "</div>";
	}

	/**
	 * 初始化日期
	 */
	function initDate() {
		var nowDate = new Date();
		var rangeTime = beginTime + " ~ " + endTime;
		var startLayDate = laydate.render({
			elem : '#unusualBillRecTime',
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

	function initTable(){
		var starttime = "";
		var endtime = "";
		var rangeTime = startobj.val();
		if(rangeTime){
			starttime = rangeTime.split("~")[0];
			endtime = rangeTime.split("~")[1];
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

		tableObj2.bootstrapTable({
			url: apiUrl + "/data",
			dataType: "json",
			uniqueId: "id",
			singleSelect: true,
			resizable: true,
			pagination: true, // 是否分页
			sidePagination: 'server',// 选择服务端分页
			queryParams: {
				orgNo: $("#electronicOrgNoInit").val(),
				startDate: starttime,
				endDate: endtime,
				patType: dataSource
			},
			onLoadSuccess: function () {

			}
		});
	}

	function initTableClick(){
		//图片放大显示
		var x = 10;
		var y = 20;
		dlgObj3.find("img[name=fileLocation]")
			.mouseover(function (e) {
				var tooTip = "<div id='tooTip'><img style='width:1000px;height:700px;' src='" + this.currentSrc + "'></img><div>";
				dlgObj3.append(tooTip);
				$("#tooTip").css({ position: "absolute",
					'top': (e.pageY + y) + "px", "left": (e.pageX + x) + "px"
				}).show("fast");
			}).mouseout(function () {
			$("#tooTip").remove();
		}).mousemove(function (e) {
			$("#tooTip").css({ position: "absolute",
				'top': (e.pageY + y) + "px", "left": (e.pageX + x) + "px"
			});
		});

		$("#unusualBIllTable").find("button[name=electronicBtn]").on('click', function(){

			$("#unusualBIllTable").find("button[name='electronicBtn']").css('background-color', 'white');
			$(this).css('background-color', '#ECF5FF');
		});

	}

	function init(orgJSON_temp, startTime, endTime) {
		orgJSON = orgJSON_temp;
		initDate(startTime, endTime);
		initTree();
		initTable();
		initTableClick();
	}

	function showExceptionTrade(dataSourceType,e){
		exceptionTrade = dataSourceType ;
		var orgNo= orgTree.getVal;
		var starttime = "";
		var endtime = "";
		var rangeTime = startobj.val();
		var businessNo = businessNoObj.val();
		var hisFlowNo = hisFlowNoObj.val();
		if(rangeTime){
			starttime = rangeTime.split("~")[0];
			endtime = rangeTime.split("~")[1];
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

		options.url=apiUrl+"/exceptionTrade";
		if($("#correction").is(':checked')){
			correction=1;
		}else{
			correction=2;
		}
		billSource = billSourceObj.val();
		tableObj2.bootstrapTable('refreshOptions', {
			resizable: true,
			pageNumber: 1,
			queryParams: function (params) {
				var queryObj = {
					orgNo: orgNo,
					startDate: starttime,
					endDate: endtime,
					dataSourceType: exceptionTrade,
					correction: correction,
					billSource: billSource,
					businessNo:businessNo,
					hisFlowNo:hisFlowNo,
					patType:dataSource
				};
				var query = $.extend(true, params, queryObj);
				return query;
			}
		});
	}

	function number(value, row, index) {
		var pageSize = tableObj2.bootstrapTable('getOptions').pageSize;//通过表的#id 可以得到每页多少条
		var pageNumber = tableObj2.bootstrapTable('getOptions').pageNumber;//通过表的#id 可以得到当前第几页
		return pageSize * (pageNumber - 1) + index + 1; //返回每条的序号： 每页条数 * （当前页 - 1 ）+ 序号
	}

	function removeOrAddFlow(e) {
		showExceptionTrade(exceptionTrade);
	}

	function moneyFormat(val, row, index){
//		return '<p data-toggle="tooltip" title=\"'+ new Number(val).toFixed(2) +'\">' + new Number(val).toFixed(2) +'</p>'
		return new Number(val).toFixed(2);
	}

	function exceptionState(val, row, index){
		var checkState = row.oriCheckState;

		var color = "";
		var value = "";
		if ("2" == checkState || "6" == checkState) {
//			color = "green";
			value = "短款";
		} else if ("3" == checkState || "5" == checkState) {
//			color = "red";
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
	function currentState(val,row,index){
		var checkState = row.checkState;
		var color = "";
		if(checkState=="9"){//已退费
			color="#26d9b5";
		}else if(checkState=="2"||checkState=="3"){
			color="#ffa94b";
		}else if(checkState=="1"){//已抹平
			color="";
		}else if(checkState=="10"){//已追回
			color="";
		}else if(checkState=="8"){//已驳回
			color="#ec639f";
		}else if(checkState=="7"){//待审核
			color="#49bbfc";
		}else{
			color="#ffa94b";
		}
		return "<span style=\"color:"+color+"\">"+val+"</span>";
	}
	function formatMoney (num) {
		if(!num){
			return '0.00';
		}
		num += '';
		if (!num.includes('.')) num += '.00';
		return num.replace(/(\d)(?=(\d{3})+\.)/g, function($0, $1) {
			return $1 + ',';
		}).replace(/\.$/, '');
	}
	function numExt(obj){
		obj.value = obj.value.replace(/[^\d.]/g,""); //清除"数字"和"."以外的字符
		obj.value = obj.value.replace(/^\./g,""); //验证第一个字符是数字
		obj.value = obj.value.replace(/\.{2,}/g,"."); //只保留第一个, 清除多余的
		obj.value = obj.value.replace(".","$#$").replace(/\./g,"").replace("$#$",".");
		obj.value = obj.value.replace(/^(\-)*(\d+)\.(\d\d).*$/,'$1$2.$3'); //只能输入两个小数
	}

	return {
		init : init,
		search : search,
		formatHandler : formatHandler,
		refundHandler : refundHandler,
		formatter : formatter,
		orgFormatter : orgFormatter,
		number : number,
		removeOrAddFlow : removeOrAddFlow,
		operation : operation,
		showFollowDetailDlg : showFollowDetailDlg,
		dealFollow:dealFollow,
		save:save,
		followHandler:followHandler,
		showHandDealInfo:showHandDealInfo,
		showExceptionTrade:showExceptionTrade,
		refundSave:refundSave,
		moneyFormat:moneyFormat,
		chsPayMethods:chsPayMethods,
		detail:detail,
		showImg:showImg,
		exceptionState:exceptionState,
		exportData:exportData,
		formatterBillSource:formatterBillSource,
		numExt:numExt,
		printData:printData,
		currentState:currentState,
		searchPayDetail: searchPayDetail
	}
})();

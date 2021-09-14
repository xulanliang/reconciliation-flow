NB.ns("app.admin").electronic = (function() {
	//表格
	var tableObj2 = $("#electronicRecDataTable");
	var dlgObj2 = $('#electronicDealFollowDlg');
	var dlgObj3 = $('#electronicShowHandFollowDlg');
	var dlgObj4 = $('#electronicRefundFollowDlg');
	var followDetailDlg = $('#electronicFollowDetailDlg');
	var electronicDlg = $('#electronicDlg');
	//表单
	var formObj = $("#electronicSearchForm");
	var formObjDlg = $("#electronicDealFollowDlgDlg");
	var refundFormObjDlg = $("#electronicRefundFollowDlgDlg");
	var payOrderTableObj = $('#payOrderTable');
	var hisOrderTableObj = $('#hisOrderTable');
	var hisDlgFormObj = electronicDlg.find("li[form=hisOrder]");
	var payDlgFormObj = electronicDlg.find("li[form=payOrder]");
	var hisOrderTableForm = electronicDlg.find("li[form=hisOrderTableForm]");
	var payOrderTableForm = electronicDlg.find("li[form=payOrderTableForm]");
	var electronicRecOrderStep = electronicDlg.find("#electronicRecOrderStep");
	var payDetailDialog = $("#electronic-data-div")
	// 时间控件
	var startobj = formObj.find("input[name=startTime]");
	//请求路径
	var apiUrl = '/admin/electronic';
	var orgTree;
	var orgJSON;
	var correction = null;
	var billSource = null;
	var businessNo = null;
	var followDetailArray = [];
	var imageObjArray = [];
	var exceptionTrade = "all";
	var electronicIsRefundExamine = $("#electronicIsRefundExamine").val()
	var targetBillSource = '';
	var isCollapse = false;//是否展开更多，默认关闭

	var billSourceExt = "";
	var billAmount = 0;
	var hisAmount = 0;
	//退费按钮
	var refundPayType=null;

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

	//导出
	function exportData() {
		var orgNo = orgTree.getVal;
		var orgName = orgTree.getText;
		startDate = startobj.val();
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
		var billSource = $("#billSource").val();
		var businessNo = $("#businessNo").val();
		bootbox.confirm('确定执行此操作?', function (r) {
			if (r) {
				var where = 'sort=' + sortName + '&order=' + sortOrder + '&orgNo=' + orgNo + '&startDate=' + startDate + "&orgName=" + orgName
					+ "&billSource=" + billSource + "&dataSourceType=all&patType=" + dataSource + "&businessNo=" + businessNo;
				var url = apiUrl + '/api/dcExcel?' + where;
				window.location.href = url;
			}
		});
	}

	// 查询
	function search(th) {
		var orgNo = orgTree.getVal;
		startDate = startobj.val();
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var businessNo = $("#businessNo").val();
		if(businessNo != undefined && businessNo != null && businessNo != ""){
			setTimeout(function () {
				$(".show-buinessOrder").css('display', 'none');
			}, 150);
		}else {
			searchSumary(orgNo, startDate);
		}
		showExceptionTrade(exceptionTrade);
	}

	function searchSumary(orgNo, startDate){
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
			url:apiUrl+"/newData",
			type:"POST",
			data:{"orgNo":orgNo, "startDate":startDate,"billSource":targetBillSource,"patType":dataSource},
			dataType:"json",
			success:function(result){
//				putDataSumary(result.data.recResult);
				putDataPayDetail(result.data.headLine);
				putDataPayMethod(result.data.headData);
			}
		});
	}
	function putDataPayMethod(headData){
		var box = payDetailDialog.find('.electronic-pay-methods-box');
		if(headData&&headData.length>0){
			box.show();
		}else{
			box.hide();
			return;
		}
		var errBadge = '<span class="badge electronic-diff-err">异常</span>';
		box.html('')
		var html = '';
		if(headData&&headData.length>0){
			var moreEle = '';
			if(headData.length>1){
				moreEle ='<a class="pay-type-more-box" onclick="app.admin.electronic.clickCollapse()" data-toggle="collapse" data-parent="#electronPayMethodsBox"  href="#electronPayTypecollapse">'
					+'<span class="fa fa-angle-double-down more-arrow"></span>'
					+'<span class="pay-type-more-text">更多详情</span>'
					+'</a>'
			}

			var ul = '';
			$.each(headData,function(index,data){
				if(data.payType=='hj'){
					html += '<div class="electronic-pay-methods-sum">'
						+'<div class="electronic-pay-methods-item pay-method-icon">'
						+'<img src="/assets/img/electronic/zj-icon.png">'
						+'<span class="text">总计</span>'
						+'</div>'
						+'<div class="electronic-pay-methods-item-bar">'
						+'<div class="electronic-pay-methods-item">'
						+'<span class="title">渠道实收金额(元)</span>'
						+'<span class="text">'+(formatMoney(data.thridPayAmount))+'</span>'
						+'</div>'
						+'<div class="electronic-pay-methods-item">'
						+'<span class="title">HIS应收金额(元)</span>'
						+'<span class="text">'+(formatMoney(data.hisPayAmount))+'</span>'
						+'</div>'
						+'</div>'
						+'<div class="electronic-pay-methods-item-bar">'
						+'<div class="electronic-pay-methods-item '+(formatMoney(data.afterDifference)!='0.00'&&data.afterAcount!=0?'electron-err':'')+'">'
						+'<span class="title">对账后差异金额(元)'+errBadge+'</span>'
						+'<span class="text">'+(formatMoney(data.afterDifference))+'</span>'
						+'</div>'
						+'<div class="electronic-pay-methods-item">'
						+'<span class="title">对账后差异笔数</span>'
						+'<span class="text">'+data.afterAcount+'</span>'
						+'</div>'
						+'</div>'
						+'<div class="electronic-pay-methods-item-bar">'
						+'<div class="electronic-pay-methods-item '+(formatMoney(data.dealAfterDifference)!='0.00'&&data.dealAfterAcount!=0?'electron-err':'')+'">'
						+'<span class="title">调账后差异金额(元)'+errBadge+'</span>'
						+'<span class="text">'+(formatMoney(data.dealAfterDifference))+'</span>'
						+'</div>'
						+'<div class="electronic-pay-methods-item">'
						+'<span class="title">调账后差异笔数</span>'
						+'<span class="text">'+data.dealAfterAcount+'</span>'
						+'</div>'
						+'</div>'
						+moreEle
						+'</div>';
				}else{
					ul +='<li>'
						+'<div class="electronic-pay-methods-item pay-method-icon">'
						+'<img class="pay-type-'+data.payType+'" src="/assets/img/electronic/b-xj-icon.png">'
						+'<span class="text">'+formatterPayType(data.payType)+'</span>'
						+'</div>'
						+'<div class="electronic-pay-methods-item-box">'
						+'<div class="electronic-pay-methods-box-item">'
						+'<div class="electronic-pay-methods-item">'
						+'<span class="title">渠道实收总金额(元)</span>'
						+'<span class="text">'+formatMoney(data.thridPayAmount)+'</span>'
						+'</div>'
						+'<div class="electronic-pay-methods-item">'
						+'<span class="title">渠道支付笔数</span>'
						+'<span class="text">'+data.thridRealPayAcount+'</span>'
						+'</div>'
						+'<div class="electronic-pay-methods-item">'
						+'<span class="title">渠道退款笔数</span>'
						+'<span class="text">'+data.thirdRefundAcount+'</span>'
						+'</div>'
						+'</div>'
						+'<div class="electronic-pay-methods-box-item">'
						+'<div class="electronic-pay-methods-item">'
						+'<span class="title">HIS应收总金额(元)</span>'
						+'<span class="text">'+formatMoney(data.hisPayAmount)+'</span>'
						+'</div>'
						+'<div class="electronic-pay-methods-item">'
						+'<span class="title">HIS支付笔数</span>'
						+'<span class="text">'+data.hisRealPayAcount+'</span>'
						+'</div>'
						+'<div class="electronic-pay-methods-item">'
						+'<span class="title">HIS退款笔数</span>'
						+'<span class="text">'+data.hisRefundAcount+'</span>'
						+'</div>'
						+'</div>'
						+'</div>'
						+'<div class="electronic-pay-methods-diff">'
						+'<div class="electronic-pay-methods-diff-item">'
						+'<div class="electronic-pay-methods-diff-item-box '+(formatMoney(data.afterDifference)!='0.00'&&data.afterAcount!='0'?'electron-err':'')+'">'
						+'<span class="title">对账后差异金额(元)'+errBadge+'</span>'
						+'<span class="text">'+formatMoney(data.afterDifference)+'</span>'
						+'</div>'
						+'<div class="electronic-pay-methods-diff-item-box">'
						+'<span class="title">对账后差异笔数</span>'
						+'<span class="text">'+data.afterAcount+'</span>'
						+'</div>'
						+'</div>'
						+'<div class="electronic-pay-methods-diff-item">'
						+'<div class="electronic-pay-methods-diff-item-box '+(formatMoney(data.dealAfterDifference)!='0.00'&&data.dealAfterAcount!='0'?'electron-err':'')+'">'
						+'<span class="title">调账后差异金额(元)'+errBadge+'</span>'
						+'<span class="text">'+formatMoney(data.dealAfterDifference)+'</span>'
						+'</div>'
						+'<div class="electronic-pay-methods-diff-item-box">'
						+'<span class="title">调账后差异笔数</span>'
						+'<span class="text">'+data.dealAfterAcount+'</span>'
						+'</div>'
						+'</div>'
						+'</div>'
						+'</li>'
				}
			})
			if(ul){
				html += '<div id="electronPayTypecollapse" class="panel-collapse '+(isCollapse?'in':'collapse')+'"><ul class="pay-methods">'+ul+'</ul></div>'
			}
		}
		box.html(html)
		if(isCollapse){
			$('#electronPayMethodsBox .pay-type-more-box .more-arrow').removeClass('fa-angle-double-down');
			$('#electronPayMethodsBox .pay-type-more-box .more-arrow').addClass('fa-angle-double-up');
			$('#electronPayMethodsBox .pay-type-more-box .pay-type-more-text').text('收起')
		}
	}
	function clickCollapse(){
		var arr = $('#electronPayMethodsBox .pay-type-more-box .more-arrow');
		if(arr.hasClass('fa-angle-double-down')){
			arr.removeClass('fa-angle-double-down');
			arr.addClass('fa-angle-double-up');
			$('#electronPayMethodsBox .pay-type-more-box .pay-type-more-text').text('收起')
			isCollapse=true
		}else{
			arr.removeClass('fa-angle-double-up');
			arr.addClass('fa-angle-double-down');
			$('#electronPayMethodsBox .pay-type-more-box .pay-type-more-text').text('更多详情')
			isCollapse=false
		}
	}
	function putDataPayDetail(detailInfo){
		var txt = "";
		var colorLinkClass= '';
		$.each(detailInfo,function(index,billSource){
			if(billSource.value==targetBillSource){
				colorLinkClass = "color-link"
			}else{
				colorLinkClass="";
			}
			txt += " <li data-value='"+billSource.value+"' data-text='"+(billSource.value?billSource.name:'')+"' class='"+colorLinkClass+"' onclick='app.admin.electronic.chsPayMethods(this)' >"+billSource.name;
			if(billSource.exceptionNum&&billSource.exceptionNum!='0'){
				txt += "<span class='badge electronic-badge'>"+billSource.exceptionNum+"</span>";
			}
			txt += "</li>";
		})
		// 支付详情数据为空的时候隐藏
		if (txt === "") {
			payDetailDialog.find(".pay-type-list").hide();
			payDetailDialog.find(".electronic-pay-methods-box").hide();
		} else {
			payDetailDialog.find(".pay-type-list").show();
			payDetailDialog.find(".electronic-pay-methods-box").show();
			payDetailDialog.find(".pay-type-list").html(txt);
		}
		initLeftRightBtn();
	}

	var imgFileJSON = {'0149':'zgyh', '0249':'wx', '0349':'zfb', '1649':'jhzf', '0559':'ybzf'};
	function chsPayMethods(th){
		// 改变选中颜色
		var obj = payDetailDialog.find(".pay-type-list").find("li");
		$.each(obj, function(o){
			$(obj[o]).removeClass("color-link");
		});
		$(th).addClass("color-link");
		var billSource = $(th).data('value');
		if(billSource==targetBillSource){
			return;
		}
		targetBillSource = billSource
		var txt = "";
//		$.each(payArr, function(i){
//			if(payArr[i].payType == "sum"){
//				txt += '<li class="border">'
//					+'<div class="pay-text">'
//					+'<p class="pay-title zh">总和</p>'
//					+'<div class=" pay-list">'
//					+'<p class="pay-left title color-nav">总金额(元)</p>'
//					+'<p class="pay-right">'+moneyFormat(payArr[i].payAmountSum)+'</p>'
//					+' </div>'
//					+'<div class=" pay-list">'
//					+'<p class="pay-left title color-nav">支付笔数</p>'
//					+'<p class="pay-right">'+payArr[i].realPayAcountSum+'</p>'
//					+' </div>'
//					+'<div class=" pay-list">'
//					+'<p class="pay-left title color-nav">退款笔数</p>'
//					+'<p class="pay-right">'+payArr[i].refundAcountSum+'</p>'
//					+'</div>'
//					+' </div>'
//					+'</li>';
//			}else{
//				txt += '<li class="border">'
//					+'<div class="pay-text">'
//					+'<p class="pay-title '+imgFileJSON[payArr[i].payType]+'">'+formatterPayType(payArr[i].payType)+'</p>'
//					+'<div class=" pay-list">'
//					+'<p class="pay-left title color-nav">实收总金额(元)</p>'
//					+'<p class="pay-right">'+moneyFormat(payArr[i].payAmount)+'</p>'
//					+' </div>'
//					+'<div class=" pay-list">'
//					+'<p class="pay-left title color-nav">支付笔数</p>'
//					+'<p class="pay-right">'+payArr[i].realPayAcount+'</p>'
//					+' </div>'
//					+'<div class=" pay-list">'
//					+'<p class="pay-left title color-nav">退款笔数</p>'
//					+'<p class="pay-right">'+payArr[i].refundAcount+'</p>'
//					+'</div>'
//					+' </div>'
//					+'</li>';
//			}
//
//		});
//		$(".pay-methods").html(txt);
		var liText = $(th).data('text');
		$("#billSource").val(liText);

		var orgNo = orgTree.getVal;
		startDate = startobj.val();

		searchSumary(orgNo,startDate)
		showExceptionTrade(exceptionTrade);
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
			}
		};
		$.ajax({
			url : "/admin/organization/data",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(msg) {
				//这个树形下拉会在和其同级位置生成一个默认隐藏的input输入框以提供表单数据
				orgTree = $("#electronicOrgTree").ztreeview({
					name : 'name',
					key : 'code',
					//是否
					clearable : true,
					expandAll : true,
					data : msg
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
					$("#electronicOrgTree").parent().parent().parent().show();
				}else{
					$("#electronicOrgTree").parent().parent().parent().hide();
				}
				search();
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
			return "<a title='查看处理详情.' style='cursor:pointer; text-decoration:underline;' onclick='app.admin.electronic.showHandDealInfo("+row.id+")' >"
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
	
	function initRefundButton(){
		debugger
		$.ajax({
			url:"electronic/refund/Button",
			async:false,
			success:function(data){
				debugger
				refundPayType=data.toString();
			}
		});
	}
	
	function equal(payType,payTypeArray){
		for(var i=0;i<payTypeArray.length;i++){
			if(payType==payTypeArray[i]){
				return true
			}
		}
		return false		
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
		var paymentRefundFlow = row.paymentRefundFlow;
		// \""+ row.recHisId + "\", \""+row.recThridId+"\"
		var recHisId = row.recHisId;
		var recThridId = row.recThridId;
		
		
		var refundButton="none";
		
		
		if(refundPayType==null||refundPayType==""){
			refundButton="inline";
		}else{
			var payTypeArray=refundPayType.split(",");
			if(equal(payName,payTypeArray)){
				refundButton="inline";
			}
		}
		
		
		if(billSource=="szzk"){
			refundButton="inline";
		}
		
		debugger

		var detail = "  &nbsp;<a href='javascript:;' onclick='app.admin.electronic.detail(\""+ row.recHisId + "\", \""+row.recThridId+"\", \""+ row.id + "\", \""+row.businessNo+"\", \""+orderState+"\", \""+billSource+"\")' class='btn btn-info btn-sm m-primary '> 详情 </a>  &nbsp;";

		var electronicRecDetailButtonOnly = $("#electronicRecDetailButtonOnly").val();
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
			name = "<button href='javascript:;' "+disabledStatus+" onclick='app.admin.electronic.dealFollow(" + id + ",\"" + orderNo + "\"," + recover + ",\"" + tradeAmount
				+ "\", \"" + orgNo + "\", \"" + tradeDatetime + "\", \"" + payName + "\", \"" + billSource + "\", \"" + patType + "\",\"" + recHisId + "\", \"" + recThridId + "\")' class='btn btn-info btn-sm m-primary'>追回 </button>  &nbsp;" +
				"<button href='javascript:;' "+disabledStatus+" onclick='app.admin.electronic.dealFollow(" + id + ",\"" + orderNo + "\"," + '1' + ",\"" + tradeAmount
				+ "\", \"" + orgNo + "\", \"" + tradeDatetime + "\", \"" + payName + "\", \"" + billSource + "\", \"" + patType + "\",\"" + recHisId + "\", \"" + recThridId + "\")' class='btn btn-info btn-sm m-primary'>抹平 </button>";
		}else if (oriCheckState == "3" ) {
			var refundName = "退费";
			if(electronicIsRefundExamine == 1){
				refundName = "退费";
			}
			//是否可以退款
			if(row.requireRefund==undefined||row.requireRefund==null||row.requireRefund==1){
				name = "<button href='javascript:;' "+disabledStatus+" onclick='app.admin.electronic.refundHandler(" + id + ",\"" + orgNo + "\",\"" + orderNo + "\",\"" + payCode + "\",\""+ tradeAmount + "\",\""
					+ billSource + "\",\"1\",\"" + tradeDatetime + "\", \"" + businessType + "\",\"" + shopNo + "\",\"" + patType + "\",\"" + tradetime
					+ "\",\"" + payNo + "\",\"" + terminalNo + "\",\"" + invoiceNo + "\",\""+payName+"\",\""+paymentRefundFlow+"\")' class='btn btn-info btn-sm m-primary ' style='display:"+refundButton+"' >"+refundName+"</button>  &nbsp;" ;
			}
			name+= "<button href='javascript:;' "+disabledStatus+" onclick='app.admin.electronic.dealFollow(" + id + ",\"" + orderNo + "\"," + '1' + ",\"" + tradeAmount
				+ "\", \"" + orgNo + "\", \"" + tradeDatetime + "\", \"" + payName + "\", \"" + billSource + "\", \"" + patType + "\",\"" + recHisId + "\", \"" + recThridId + "\")' class='btn btn-info btn-sm m-primary'>抹平 </button>";
		}
		var col = "<div style='text-align:center;'>" + name + detail + "</div>";

		return col;
	}

	function loadPayStep(businessNo, billSource, orgNo, recHisId, recThirdId) {
		if(!businessNo){
			electronicRecOrderStep.hide();
			return;
		}
		var innerObj = electronicRecOrderStep.find(".carousel-inner");
		$.ajax({
			url:apiUrl+"/payStep",
			data:{
				businessNo:businessNo,
				orgCode:orgNo,
				billSource:billSource,
				recHisId:recHisId,
				recThirdId:recThirdId
			},
			dataType:'json',
			success:function(res){
				innerObj.html('');
				if(!res.data){
					electronicRecOrderStep.hide();
					return;
				}else{
					electronicRecOrderStep.show();
				}
				var data = res.data;

				var html = "";
				var ul = "";
				var pageSize = 6;
				if(data.length<pageSize){
					electronicRecOrderStep.find('.electronic-rec-pay-step-left').hide();
					electronicRecOrderStep.find('.electronic-rec-pay-step-right').hide();
				}else{
					electronicRecOrderStep.find('.electronic-rec-pay-step-left').show();
					electronicRecOrderStep.find('.electronic-rec-pay-step-right').show();
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
	function applyPayStep(data){
		electronicRecOrderStep.html('');
		if(!data){
			electronicRecOrderStep.hide();
			return;
		}else{
			electronicRecOrderStep.show();
		}
		var html = "";
		$.each(data,function(index,val){
			if((index+1)%6==0){
				html+="<li style='clear:both;'></li>"
			}
			if(val.billSource=='his'){
				html += "<li class='electronic-rec-pay-step-item pay-step-his'>"
					+"<div class='electronic-rec-pay-step-item-arr'>"+(index+1)+"</div>"
					+"<div><div>"+val.tradeDate+"</div>"
					+"<div>HIS"+(val.orderState=='0256'?'退款':'入账')+val.payAmount+"元</div></div>"
					+"</li>";
			}else{
				html += "<li class='electronic-rec-pay-step-item pay-step-his'>"
					+"<div><div>"+(formatter(val.payType).replace('支付',''))+(val.orderState=='0256'?'退款':'支付')+val.payAmount+"元</div>"
					+"<div>"+val.tradeDate+"</div></div>"
					+"<div class='electronic-rec-pay-step-item-arr'>"+(index+1)+"</div>"
					+"</li>";
			}
		});
		electronicRecOrderStep.html(html);
	}
	// 订单详情页面
	function detail(recHisId,recThirdId,id, businessNo, orderState,billSource){
		$.bootstrapLoading.start({ loadingTips: "正在处理数据，请稍候..." });
		if(recHisId == "null"){
			recHisId = "";
		}
		if(recThirdId == "null"){
			recThirdId = "";
		}
		var row = tableObj2.bootstrapTable('getRowByUniqueId',id);
		var orgNo = row.orgNo;
		loadPayStep(businessNo, billSource, orgNo, recHisId, recThirdId);
		payOrderTableForm.hide();
		hisOrderTableForm.hide();
		payDlgFormObj.hide();
		hisDlgFormObj.hide();
		payDlgFormObj.loadDetailReset();
		payDlgFormObj.loadDetail(row);
		var tradeTime = row.tradeTime;
		$.ajax({
			type: 'POST',
			url : apiUrl+"/exceptionTrade/detail",
			data: {"recHisId":recHisId,"recThirdId":recThirdId,"businessNo":businessNo, "orgNo":orgNo,"billSource":billSource, "orderState":orderState, "tradeTime":tradeTime},
			dataType : "json",
			success : function(result) {
				billAmount = 0;
				hisAmount = 0;
				$.bootstrapLoading.end();
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
						// 就诊号
						row['visitNumbe'] = thirdOrder[i].visitNumbe;
						row['patientCardNo'] = thirdOrder[i].patientCardNo;
						// 银行卡号
						row['paymentAccount'] = thirdOrder[i].paymentAccount;
						billAmount = (thirdOrder[i].orderState == '0156') ? (billAmount + thirdOrder[i].payAmount) : (billAmount - thirdOrder[i].payAmount);
						data.push(row);
					}
					payOrderTableObj.bootstrapTable('destroy').bootstrapTable({
						resizable: true,
						data: data
					});
					payOrderTableForm.show();
				}else{
					if(thirdOrder != null && typeof (thirdOrder) != "undefined" && thirdOrder.length==1){
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
						// 就诊号
						payDlgFormObj.find("p[data-name=visitNumbe]").html(thirdOrder[0].visitNumbe);
						payDlgFormObj.find("p[data-name=patientCardNo]").html(thirdOrder[0].patientCardNo);
						// 银行卡号
						payDlgFormObj.find("p[data-name=paymentAccount]").html(thirdOrder[0].paymentAccount);
						billAmount = (thirdOrder[0].orderState == '0156') ? (billAmount + thirdOrder[0].payAmount) : (billAmount - thirdOrder[0].payAmount);
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
						// 就诊号
						payDlgFormObj.find("p[data-name=visitNumbe]").html("");
						payDlgFormObj.find("p[data-name=patientCardNo]").html("");
						// 银行卡号
						payDlgFormObj.find("p[data-name=paymentAccount]").html("");
						billAmount = 0;
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
						// 就诊号
						row['visitNumber'] = hisOrder[i].visitNumber;
						// 门诊号
						row['mzCode'] = hisOrder[i].mzCode;
						// 发票号
						row['invoiceNo'] = hisOrder[i].invoiceNo;
						// 支付业务类型
						row['payBusinessType'] = formatter(hisOrder[i].payBusinessType);
						hisAmount = (hisOrder[i].orderState == '0156') ? (hisAmount + hisOrder[i].tradeAmount) : (hisAmount - hisOrder[i].tradeAmount);
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
						// 就诊号
						hisDlgFormObj.find("p[data-name=visitNumber]").html(hisOrder[0].visitNumber);
						// 门诊号
						hisDlgFormObj.find("p[data-name=mzCode]").html(hisOrder[0].mzCode);
						// 发票号
						hisDlgFormObj.find("p[data-name=invoiceNo]").html(hisOrder[0].invoiceNo);
						// 支付业务类型
						hisDlgFormObj.find("p[data-name=payBusinessType]").html(formatter(hisOrder[0].payBusinessType))
						hisAmount = (hisOrder[0].orderState == '0156') ? (hisAmount + hisOrder[0].tradeAmount) : (hisAmount - hisOrder[0].tradeAmount);
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
						// 就诊号
						hisDlgFormObj.find("p[data-name=visitNumber]").html("");
						// 门诊号
						hisDlgFormObj.find("p[data-name=mzCode]").html("");
						// 发票号
						hisDlgFormObj.find("p[data-name=invoiceNo]").html("");
						// 支付业务类型
						hisDlgFormObj.find("p[data-name=payBusinessType]").html("");
						hisAmount = 0;
					}
					hisDlgFormObj.show();
				}
				// 计算差额
				hisDlgFormObj.find("p[data-name=differenceAmount]").html(moneyFormat(billAmount - hisAmount) + "元");

				// 处理说明
				var dealDetail = result.data.dealDetail;
				if (dealDetail != null && typeof (dealDetail) != "undefined") {
					var obj = payDlgFormObj;
					if(orderLength > 1){
						obj = payOrderTableForm;
					}
					obj.find("p[data-name=description]").html(dealDetail.description);
					if(dealDetail.fileLocation){
						obj.find('.electronic-desc-img').show()
						obj.find("a[data-name=descImg]").attr("value",apiUrl + "/readImage?adress=" + dealDetail.fileLocation);
					}else{
						obj.find('.electronic-desc-img').hide()
					}
					$(".electronic-handle-description").show();
				}else{
					$(".electronic-handle-description").hide();
				}
				electronicDlg.modal('show');
			}
		});

	}

	// 查看图片
	function showImg(th){
		window.open($(th).attr("value"));
	}
	// 重置查询条件
	function resetData(){
		$("#businessNo").val("");
		$(".show-buinessOrder").css('display', 'block');
		search("all");
	}

	function dealFollow(id, orderNo, checkState, tradeAmount, orgNo, tradeDatetime, payName, billSource, patType, recHisId, recThridId) {
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
		formObjDlg.find("input[name=payType]").val(payName);
		formObjDlg.find("input[name=billSource]").val(billSource);
		formObjDlg.find("input[name=patType]").val(patType);
		formObjDlg.find("input[name=recHisId]").val(recHisId);
		formObjDlg.find("input[name=recThridId]").val(recThridId);
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

	function operation(value, row, index) {
		if(null != row.payAllAmount){
			return "<a style='text-decoration:underline;cursor:pointer;' onclick='app.admin.electronic.showFollowDetailDlg()'>"+new Number(row.payAllAmount).toFixed(2)+"</a>";
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
	/*
	 * 处理后金额
	 */
	function updateDifferenceAmount(){
		var ajax_option = {
			url : apiUrl + "/updateDifferenceAmount",
			type : 'post',
			success : function(result) {
				var resultJson = JSON.parse(result);
				var dealDiffAmount = resultJson[0];
				var dealDiffPayAcount = resultJson[1];
				var tradeDiffAmount = payDetailDialog.find(".tradeDiffAmount").text();
				var diffAmoumtCount = parseFloat(tradeDiffAmount)+parseFloat(dealDiffAmount);
				payDetailDialog.find(".dealDiffAmount").html(new Number(diffAmoumtCount).toFixed(2));
				payDetailDialog.find(".dealDiffPayAcount").html(numberFormat(dealDiffPayAcount));

				//这部分代码保留，处理后金额专用
				/*var resultJson = JSON.parse(result);
                //差额
                var dealDiffAmount = resultJson[0];
                //笔数
                var dealDiffPayAcount = resultJson[1];
                //差异总金额
                var tradeDiffAmount = payDetailDialog.find(".tradeDiffAmount").text();
                //差异笔数
                var tradeDiffPayAcount = payDetailDialog.find(".tradeDiffPayAcount").text();
                //处理后差额
                var diffAmoumt = parseFloat(tradeDiffAmount)+parseFloat(dealDiffAmount);
                //处理后笔数
                var diffAmoumtCount = tradeDiffPayAcount-dealDiffPayAcount;
                payDetailDialog.find(".dealDiffAmount").html(new Number(diffAmoumt).toFixed(2));
                payDetailDialog.find(".dealDiffPayAcount").html(numberFormat(diffAmoumtCount));*/
			}
		};
		formObjDlg.ajaxSubmit(ajax_option);
	}

	function refundSave(){
		var objButton=$("#refundSave");
		var billSource = billSourceExt;
		var payType = $("#payType").val();
		if ((billSource == "8391" || billSource == "8301") && payType == "0149") {
			if($("#pjhId").val()==null||$("#pjhId").val()==""){
				$.NOTIFY.showError("错误", "中行订单请输入票据号", '');
				return;
			}
			if($("#sysNoId").val()==null||$("#sysNoId").val()==""){
				$.NOTIFY.showError("错误", "中行订单请输入系统订单号", '');
				return;
			}
			if($("#bocNoId").val()==null||$("#bocNoId").val()==""){
				$.NOTIFY.showError("错误", "中行订单请输入流水号", '');
				return;
			}
		}
		// 退款金额
		var refundAmount = $("#refundAmount").val();
		// 订单金额
		var tradeAmount = $( "#tradeAmount").val();
		if ((refundAmount - tradeAmount) > 0) {
			$.NOTIFY.showError("错误", "退款金额必须小于等于订单金额", '');
			return;
		}
		if (refundAmount <= 0 ) {
			$.NOTIFY.showError("错误", "退款金额必须大于等于0", '');
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

		$("#sqm").hide();
		$("#pjh").hide();
		$("#sysNo").hide();
		$("#cashier").hide();
		$("#counterNo").hide();
		$("#bocNo").hide();

		$("#payType").val(payType);

		billSourceExt = billSource;
		//中行退费弹框
		if ((billSource == "8391" || billSource == "8301") && payType == '0149') {
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
		var rangeTime = $("#electronicTradeDate").val();
		var startLayDate = laydate.render({
			elem : '#electronicRecTime',
			btns: ['confirm'],
			theme : '#A9BCF5',
			type: "date",
			value: rangeTime,
			format:"yyyy-MM-dd",
			max: nowDate.getTime()
		});
	}

	function initTable(){
		// 数据来源  mz-门诊  zy-住院
		var dataSource = $("#dataSourceTree").val();
		if (dataSource == undefined || dataSource == null || dataSource == '全部') {
			dataSource = "";
		} else if (dataSource == '住院') {
			dataSource = 'zy';
		} else {
			dataSource = 'mz';
		}
		tableObj2.bootstrapTable({
			url: apiUrl + "/exceptionTrade",
			dataType: "json",
			uniqueId: "id",
			singleSelect: true,
			resizable: true,
			pagination: true, // 是否分页
			sidePagination: 'server',// 选择服务端分页
			queryParams: {
				orgNo: $("#electronicOrgNoInit").val(),
				startDate: $("#electronicTradeDate").val(),
				patType: dataSource
			},
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

		$("#electronicTable").find("button[name=electronicBtn]").on('click', function(){

			$("#electronicTable").find("button[name='electronicBtn']").css('background-color', 'white');
			$(this).css('background-color', '#ECF5FF');
		});

	}

	function init(orgJSON_temp, billSource, patType) {
		orgJSON = orgJSON_temp;
		initRefundButton();
		initDate();
		initTree();
		initTable();
//		updateDifferenceAmount();
		initTableClick();
		$('#electronicRecPayStepBox').on('slide.bs.carousel', function () {
			if($('#electronicRecPayStepBox').find('.item:first-child').hasClass('active')){
				$('.electronic-rec-pay-step-left').addClass('disabled');
			}else{
				$('.electronic-rec-pay-step-left').removeClass('disabled');
			}
			if($('#electronicRecPayStepBox').find('.item:last-child').hasClass('active')){
				$('.electronic-rec-pay-step-right').addClass('disabled');
			}else{
				$('.electronic-rec-pay-step-right').removeClass('disabled');
			}
		})
		$('#electronBillSourceLeft').on('click',function(){
			var left = payDetailDialog.find(".pay-type-list-content").scrollLeft()-200
			payDetailDialog.find(".pay-type-list-content").animate({scrollLeft:left},200);
			initLeftRightBtn()
		})
		$('#electronBillSourceRight').on('click',function(){
			var left = payDetailDialog.find(".pay-type-list-content").scrollLeft()+200
			payDetailDialog.find(".pay-type-list-content").animate({scrollLeft:left},200);
			initLeftRightBtn()
		})
		window.onresize=function(){initLeftRightBtn()}
	}
	function initLeftRightBtn(){
		setTimeout(function(){
			var left = payDetailDialog.find(".pay-type-list-content").scrollLeft()
			var scrollWidth = payDetailDialog.find("#electronBillSourceContent")[0].scrollWidth;
			var offsetWidth = payDetailDialog.find("#electronBillSourceNav")[0].offsetWidth;
			var right = scrollWidth - offsetWidth - left
			if(right==0){
				$('#electronBillSourceRight').addClass('disable')
			}else{
				$('#electronBillSourceRight').removeClass('disable')
			}
			if(left==0){
				$('#electronBillSourceLeft').addClass('disable')
			}else{
				$('#electronBillSourceLeft').removeClass('disable')
			}
			if(left==0&&right==0){
				$('#electronBillSourceLeft').hide();
				$('#electronBillSourceRight').hide();
			}else{
				$('#electronBillSourceLeft').show();
				$('#electronBillSourceRight').show();
			}
		},210)
	}
	function showExceptionTrade(dataSourceType,e){
		exceptionTrade = dataSourceType ;
		var orgNo= orgTree.getVal;
		startDate = startobj.val();
		options.url=apiUrl+"/exceptionTrade";
		if($("#correction").is(':checked')){
			correction=1;
		}else{
			correction=2;
		}
		billSource = $("#billSource").val();
		// 订单号
		businessNo = $("#businessNo").val();

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
			pageNumber: 1,
			queryParams: function (params) {
				var queryObj = {
					orgNo: orgNo, startDate: startDate,
					dataSourceType: exceptionTrade, correction: correction,
					billSource: billSource,
					businessNo: businessNo,
					patType: dataSource
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
		resetData:resetData,
		clickCollapse:clickCollapse,
		currentState:currentState
	}
})();

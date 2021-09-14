NB.ns("app.admin").cashRec = (function() {
	//表格
	var tableObj2 = $("#cashRecDataTable");
	//表单
	var formObj = $("#cashRecSearchForm");
	var cashDialog = $("#cash-data-div");
	// 抹平处理的模态框
	var dealDlg = $("#cashRecDealFollowDlg");
	var dealDlgForm = $("#cashRecDealFollowDlgForm")
	// 详情模态框
	var cashDetailDlg = $("#cashDetailDlg");
	var hisDlgFormObj = cashDetailDlg.find("li[form=hisOrder]");
	var payDlgFormObj = cashDetailDlg.find("li[form=payOrder]");
	var hisOrderTableForm = cashDetailDlg.find("li[form=hisOrderTableForm]");
	var payOrderTableForm = cashDetailDlg.find("li[form=payOrderTableForm]");
	// 详情表格框
	var payOrderTableObj = $('#payCashOrderTable');
	var hisOrderTableObj = $('#hisCashOrderTable');
	// 时间控件
	var rangeTimeObj = formObj.find("#cashRecTime");
	//请求路径
	var apiUrl = '/admin/cashrec';
	var orgTree;
	var orgJSON;
	var correction = null;
	var followDetailArray = [];
	var imageObjArray = [];
	var exceptionTrade = "all";

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

	//查询
	function search(th) {
		var orgNo = orgTree.getVal;
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		}
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
			
		searchSumary(orgNo, startDate, endDate);
		
		options.url = apiUrl + "/exceptionTrade";
		if ($("#correction").is(':checked')) {
			correction = 1;
		} else {
			correction = 2;
		}
		tableObj2.bootstrapTable('refreshOptions', {
			resizable: true,
			pageNumber : 1,
			queryParams : function(params) {
				var queryObj = {
					orgNo : orgNo,
					startDate : startDate,
					endDate : endDate,
					correction : correction
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
	
	function searchSumary(orgNo, startDate, endDate){
		$.ajax({
			url:apiUrl+"/data",
			type:"POST",
			data:{"orgNo":orgNo, "startDate":startDate, 'endDate': endDate},
			dataType:"json",
			success:function(result){
				putDataSumary(result.data.recResult);
				putDataPayDetail(result.data.payDetailMap);
			}
		});
	}
	
	function putDataPayDetail(detailInfo){
		var selfHelpUl = cashDialog.find(".self-help-div ul");
		var windowUl = cashDialog.find(".window-div ul");
		var selfHelpTxt = "";
		var windowTxt = "";
		var json = [ {
			"patType" : "总计"
		}, {
			"patType" : "门诊"
		}, {
			"patType" : "住院"
		} ];
		for ( var payLocation in detailInfo) {
			// 自助机
			if ("0001" === payLocation) {
				selfHelpTxt = createTxt(detailInfo[payLocation]);
			}
			// 窗口
			if ("0002" === payLocation) {
				windowTxt = createTxt(detailInfo[payLocation]);
			}
		}
		if (selfHelpTxt === "") {
			selfHelpTxt = createTxt(json);
		}
		selfHelpUl.html(selfHelpTxt);
		if (windowTxt === "") {
			windowTxt = createTxt(json);
		}
		windowUl.html(windowTxt);
	}
	
	function createTxt(detail){
		var txt = "";
		$.each(detail, function(i){
			txt += 
				'<li>'
					+'<p class="earning-title">'+detail[i].patType+'</p>'
					+'<div class=" earning-list">'
						+'<p class="earning-left title">实收总金额</p>'
						+'<p class="earning-right">'+moneyFormat(detail[i].payAmount)+'</p>'
					+'</div>'
					+'<div class=" earning-list">'
						+'<p class="earning-left title">支付笔数</p>'
						+'<p class="earning-right">'+numberFormat(detail[i].realPayAcount)+'</p>'
					+'</div>'
					+'<div class=" earning-list">'
						+'<p class="earning-left title">退款笔数</p>'
						+'<p class="earning-right">'+numberFormat(detail[i].refundAcount)+'</p>'
					+'</div>'
				+'</li>';
		});
		return txt;
	}
	
	function putDataSumary(recResult){
		cashDialog.find(".payAllAmount").html(moneyFormat(recResult.payAllAmount));
		cashDialog.find(".payAcount").html(numberFormat(recResult.payAcount));
		cashDialog.find(".refundAcount").html(numberFormat(recResult.refundAcount));
		cashDialog.find(".hisAllAmount").html(moneyFormat(recResult.hisAllAmount));
		cashDialog.find(".hisPayAcount").html(numberFormat(recResult.hisPayAcount));
		cashDialog.find(".hisRefundAcount").html(numberFormat(recResult.hisRefundAcount));
		cashDialog.find(".tradeDiffAmount").html(moneyFormat(recResult.tradeDiffAmount));
		cashDialog.find(".tradeDiffPayAcount").html(numberFormat(recResult.tradeDiffPayAcount));
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
					orgTree = $("#cashOrgTree").ztreeview({
						name : 'name',
						key : 'code',
						//是否
						clearable : true,
						expandAll : true,
						data : msg
					}, setting);
					orgTree.updateCode(msg[0].id, msg[0].code);
					
					var accountOrgNo = $("#cashOrgNoInit").val();
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
						$("#cashOrgTree").parent().parent().parent().show();
					}else{
						$("#cashOrgTree").parent().parent().parent().hide();
					}
					search();
				}
			});
	}

	function formatter(val) {
		var typeJSON = $('#cashType').val();
		var typesJSON = JSON.parse(typeJSON);
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
		var beginTime = $("#cashStartDate").val();
		var endTime = $("#cashEndDate").val();
		var nowDate = new Date();
		var rangeTime = beginTime + " ~ " + endTime;
		var startLayDate = laydate.render({
			elem : '#cashRecTime',
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
		tableObj2.bootstrapTable({
			url : apiUrl + "/exceptionTrade",
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable: true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
			queryParams : {
				orgNo : $("#cashOrgNoInit").val(),
				startDate : $("#cashStartDate").val(),
				endDate : $("#cashEndDate").val(),
            },
		});
	}
	
	function init(orgJSON_temp) {
		orgJSON = orgJSON_temp;
		initDate();
		initTree();
		initTable();
		$("#cashTable").find("button[name=cashBtn]").on('click', function(){
			
			$("#cashTable").find("button[name='cashBtn']").css('background-color', 'white');
		    $(this).css('background-color', '#ECF5FF');
		});
	}
	
	function showExceptionTrade(dataSourceType,e){
		exceptionTrade = dataSourceType ;
		var orgNo= orgTree.getVal;
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		}
	    options.url=apiUrl+"/exceptionTrade";
	    if($("#correction").is(':checked')){
			correction=1;
		}else{
			correction=2;
		}
		tableObj2.bootstrapTable('refreshOptions', {
			  resizable: true,
			  pageNumber:1,
			  queryParams:function(params){
				    var queryObj ={orgNo:orgNo,startDate:startDate,endDate:endDate,dataSourceType:exceptionTrade,correction:correction};
	                var query = $.extend( true, params, queryObj);
	                return query;
	            }
		});
	}
	
	function exportData() {
		var orgNo = orgTree.getVal;
		var orgName = orgTree.getText;
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		}
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}

		bootbox.confirm('确定执行此操作?',function(r){
			if (r) {
				var where = 'orgNo=' + orgNo + '&orgName=' + orgName
						+ '&startDate=' + startDate + '&endDate=' + endDate;
				var url = apiUrl + '/dcExcel?' + where;
				window.location.href = url;
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
		if(isNaN(val)){
//			return '<p data-toggle="tooltip" title=\"'+ new Number(0.00).toFixed(2)+'\">' + new Number(0.00).toFixed(2) +'</p>'
			return new Number(0.00).toFixed(2);
		}
//		return '<p data-toggle="tooltip" title=\"'+ new Number(val).toFixed(2)+'\">' + new Number(val).toFixed(2) +'</p>'
		return new Number(val).toFixed(2);
	}
	function numberFormat(val){
		if(isNaN(val)){
//			return '<p data-toggle="tooltip" title=\"'+ new Number(0)+'\">' + new Number(0) +'</p>'
			return new Number(0);
		}
//		return '<p data-toggle="tooltip" title=\"'+ new Number(val)+'\">' + new Number(val) +'</p>'
		return new Number(val);
	}
	

	function formatOpt(val, row, index) {
		var id = row.id;
		var checkState = row.checkState;
		var orderState = row.tradeName;
		var disabledStatus = '';
		if (checkState == 1) {
			disabledStatus = 'disabled="true"';
		}
		var detail = "  &nbsp;<button href='javascript:;' onclick='app.admin.cashRec.detail(\""+ row.recHisId + "\", \""+row.recThridId+"\", \""+ row.id + "\", \""+row.businessNo+"\", \""+orderState+"\")' class='btn btn-info btn-sm m-primary '> 详情 </button>  &nbsp;";
		var btnHtml = "<button href='javascript:;' "+disabledStatus+" onclick='app.admin.cashRec.dealFollow(" + id + ")' class='btn btn-info btn-sm m-primary'>抹平 </button>";
		btnHtml += detail;
		return btnHtml;
	}
	
	// 订单详情页面
	function detail(recHisId,recThirdId,id, businessNo, orderState){
		if(recHisId == "null"){
			recHisId = "";
		}
		if(recThirdId == "null"){
			recThirdId = "";
		}
		payOrderTableForm.hide();
		hisOrderTableForm.hide();
		payDlgFormObj.hide();
		hisDlgFormObj.hide();
		var row = tableObj2.bootstrapTable('getRowByUniqueId',id);		
		payDlgFormObj.loadDetailReset();
		payDlgFormObj.loadDetail(row);
		var tradeTime = row.tradeTime;
		var orgNo = row.orgNo;
		$.ajax({
			type: 'POST',
			url : apiUrl+"/exceptionTrade/detail",
			data: {"recHisId":recHisId,"recThirdId":recThirdId,"businessNo":businessNo, "orgNo":orgNo, "tradeTime":tradeTime},
			dataType : "json",
			success : function(result) {
				if(!result.success){
					$.NOTIFY.showError("提醒", result.message, '');
					return ;
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
						// 银行卡号
						row['paymentAccount'] = thirdOrder[i].paymentAccount;
			            data.push(row);
					}
					payOrderTableObj.bootstrapTable('destroy').bootstrapTable({
						resizable: true,
			            data: data
			        });
					payOrderTableForm.show();
				}else{
					if(thirdOrder != null && typeof (thirdOrder) != "undefined" &&thirdOrder.length!=0){
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
						// 银行卡号
						payDlgFormObj.find("p[data-name=paymentAccount]").html(thirdOrder[0].paymentAccount);
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
                        // 银行卡号
                        payDlgFormObj.find("p[data-name=paymentAccount]").html("");
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
			            data.push(row);
					}
					hisOrderTableObj.bootstrapTable('destroy').bootstrapTable({
						resizable: true,
			            data: data
			        });
					hisOrderTableForm.show();
				}else{
					if (hisOrder != null &&hisOrder.length>0 && typeof (hisOrder) != "undefined") {
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
					}
					hisDlgFormObj.show();
				}
				// 处理说明
				var dealDetail = result.data.dealDetail;
				if (dealDetail != null && typeof (dealDetail) != "undefined") {
					var obj = payDlgFormObj;
					if(orderLength > 1){
						obj = payOrderTableForm;
					}
					obj.find("p[data-name=description]").html(dealDetail.description);
					$(".cash-handle-description").show();
					if(dealDetail.fileLocation){
						$(".cash-handle-description .electronic-desc-img").show();
						obj.find("a[data-name=descImg]").attr("value","/admin/electronic/readImage?adress=" + dealDetail.fileLocation);
					}else{
						$(".cash-handle-description .electronic-desc-img").hide();
					}
				}else{
					$(".cash-handle-description").hide();
				}
				cashDetailDlg.modal('show');
			}
		});
		
	}
		
	// 查看图片
	function showImg(th){
		window.open($(th).attr("value"));
	}
	
	function dealFollow(id){
		var objButton = $("#save");
		objButton.attr('disabled', false);
		dealDlgForm[0].reset();
		dealDlgForm.find("input[name=id]").val(id);
		dealDlg.modal("show");
	}
	

	function save() {
		var objButton = $("#save");
		objButton.attr('disabled', true);
		var option = {
			url : apiUrl + "/dealFollow",
			type : "post",
			success : function(data) {
				if (JSON.parse(data).success) {
					search();
					dealDlg.modal('hide');
					$.NOTIFY.showSuccess("提醒", "处理成功", '');
				} else {
					$.NOTIFY.showError("错误", JSON.parse(data).message, '');
				}
				objButton.attr('disabled', false);
			}
		}
		dealDlgForm.ajaxSubmit(option);
	}
	
	function exceptionState(val, row, index){
		var checkState = row.checkState;
		
		var color = "";
		var value = "";
		if ("2" == checkState || "6" == checkState) {
			color = "green";
			value = "短款";
		} else if ("3" == checkState || "5" == checkState) {
			color = "red";
			value = "长款";
		} else if ("7" == checkState) {
			value = "待审核";
		} else if ("8" == checkState) {
			value = "已驳回";
		} else if ("9" == checkState) {
			value = "已退费";
		} else if ("1" == checkState) {
			value = "已处理";
		}
		var res = "";
		if(color != ""){
			res = "<span style=\"color:"+color+"\">"+value+"</span>";
		}else{
			res = "<span>"+value+"</span>";
		}
		return res;
	}
	return {
		init : init,
		search : search,
		formatter : formatter,
		orgFormatter : orgFormatter,
		number : number,
		removeOrAddFlow : removeOrAddFlow,
		showExceptionTrade:showExceptionTrade,
		moneyFormat:moneyFormat,
		numberFormat:numberFormat,
		exportData:exportData,
		exceptionState:exceptionState,
		formatOpt:formatOpt,
		save:save,
		dealFollow:dealFollow,
		detail:detail,
		showImg:showImg
	}
})();

NB.ns("app.admin").following = (function() {
	//表格
	var tableObj = $("#followingSumDataTable");
	var tableObj2 = $("#followingDataTable");
	var dlgObj = $('#followingDiv');
	var dlgObj2 = $('#dealFollowDlg');
	var dlgObj3 = $('#showHandFollowDlg');
	var dlgObj4 = $('#refundFollowDlg');
	var followDetailDlg = $('#followDetailDlg');
	//表单
	var formObj = $("#followingSearchForm");
	var formObjDlg = $("#dealFollowDlgDlg");
	var refundFormObjDlg = $("#refundFollowDlgDlg");
	// 时间控件
	var startobj = formObj.find("input[name=startTime]");
	var endobj = formObj.find("input[name=endTime]");
	//请求路径
	var apiUrl = '/admin/nextDayAccount/data';
	var followingTree;
	var orgJSON;
	var correction = null;
	var followDetailArray = [];
	var imageObjArray = [];
	var exceptionTrade = "all";

	//var removeFlow;
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

	//导出
	function exportData() {
		var orgNo = followingTree.getVal;
		var orgName = followingTree.getText;
		if (orgNo == "" || orgName == null) {
			$.NOTIFY.showError('提示', '请选择机构!', '');
			return;
		}
		var startDate = startobj.val();
		var endDate = endobj.val();
		if ($("#correction").is(':checked')) {
			correction = 1;
		} else {
			correction = 2;
		}
		if (startDate == "" || startDate == null || endDate == ""
				|| endDate == null) {
			$.NOTIFY.showError("提示", "请选择日期!", '');
			return false;
		}
		bootbox.confirm('确定执行此操作?', function(r) {
			if (r) {
				where = '&orgNo=' + orgNo + '&startDate=' + startDate
						+ '&endDate=' + endDate + "&orgName="
						+ encodeURI(orgName) + "&correction=" + correction;
				url = apiUrl + '/dcExcel?' + where + "&t="
						+ new Date().getTime();
				window.location.href = url;
			}
		});
	}
	//对账
	function startRec() {
		var orgNo = followingTree.getVal;
		if (orgNo == null || orgNo == '') {
			$.NOTIFY.showError('提示', '请选择机构!', '');
			return;
		}
		var startDate = startobj.val();
		var endDate = endobj.val();
		if (startDate == "" || startDate == null || endDate == ""
				|| endDate == null) {
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
				startDate : startDate,
				endDate : endDate
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
	function search() {
		var orgNo = followingTree.getVal;
		startDate = startobj.val();
		endDate = endobj.val();
		
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}

		tableObj.bootstrapTable('refreshOptions', {
			pageNumber : 1,
			resizable: true,
			queryParams : function(params) {
				var queryObj = {
					orgNo : orgNo,
					startDate : startDate,
					endDate : endDate
				};
				var query = $.extend(true, params, queryObj);
				return query;
			}
		});
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
					followingTree = $("#followingTree").ztreeview({
						name : 'name',
						key : 'code',
						//是否
						clearable : true,
						expandAll : true,
						data : msg
					}, setting);
					followingTree.updateCode(msg[0].id, msg[0].code);
					
					var accountOrgNo = $("#followingOrgNoInit").val();
					if((accountOrgNo != "" && accountOrgNo != null && accountOrgNo != undefined)){
						for(var i=0;i<msg.length;i++){
							if(accountOrgNo == msg[i].code){
								followingTree.updateCode(msg[i].id,msg[i].code);
							}
						}
					}
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
		//账单来源
		var billSource = row.billSource;
		//订单号
		var orderNo = row.businessNo;
		//支付渠道
		var payCode = row.payName;
		var tradeAmount = row.tradeAmount;
		// 01012冲正
		var isCorrection = row.isCorrection;
		
		var name = "";
		
		if ((checkState == "2" || checkState == "6") && isCorrection != "01012") {
			name = "<button type='button' class='btn btn-primary' style='cursor:pointer; text-decoration:underline;'  onclick='app.admin.following.dealFollow(" + id + ",\"" + orderNo + "\")' >平账 </button>";
		}else if (orderState == "0156" && (billSource == "self"||billSource == "self_jd"||billSource == "self_td_jd") && checkState == "3" && isCorrection != "01012") {
			name = "<button type='button' class='btn btn-primary' style='cursor:pointer; text-decoration:underline;' onclick='app.admin.following.refundHandler(" + id + ",\"" + orderNo + "\",\"" + payCode + "\",\""+ tradeAmount + "\",\"" + billSource + "\")' >退费</button>";
		}
		var col = "<div style='width:70px;text-align:center;'>" + name + "</div>";
		return col;
	}

	function dealFollow(id, orderNo) {
		formObjDlg[0].reset();
		formObjDlg.find("input[name=payFlowNo]").val(orderNo);
		dlgObj2.modal('show');
	}

	function save() {
		var ajax_option = {
			url : apiUrl + "/dealFollow",
			type : 'post',
			success : function(result) {
				if (JSON.parse(result).success) {
					
					showExceptionTrade(exceptionTrade);
					dlgObj2.modal('hide');
					$.NOTIFY.showSuccess("提醒", "处理成功", '');
				} else {
					$.NOTIFY.showError("错误", JSON.parse(result).message, '');
				}
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
			url : apiUrl,
			data: {"orgNo":followingTree.getVal,"startDate":startobj.val(),"endDate":endobj.val()},
			dataType : "json",
			success : function(result) { 
				if (result.success) {
					followDetailDlg.find("div[name=followDetailDlgBody]").html("");
					if (result.data != null && result.data.length > 0) {
						for (var i = 0; i < result.data.length; i++) {
							var typeJSON = $('#followingType').val();
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
							followDetailDlg.find("div[name=followDetailDlgBody]").prepend(html);
						}
						followDetailDlg.modal('show');
					}
				}
			}
    	});
		
		
	}

	function refundSave(){
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
				
			}
		};
		refundFormObjDlg.ajaxSubmit(ajax_option);
	}
	
	//退费
	function refundHandler(val, orderNo, payCode, tradeAmount ,billSource) {
		refundFormObjDlg[0].reset();
		refundFormObjDlg.find("input[name=id]").val(val);
		refundFormObjDlg.find("input[name=orderNo]").val(orderNo);
		refundFormObjDlg.find("input[name=payCode]").val(payCode);
		refundFormObjDlg.find("input[name=tradeAmount]").val(tradeAmount);
		refundFormObjDlg.find("input[name=billSource]").val(billSource);
		dlgObj4.modal('show');
		
	}
	function formatter(val) {
		var typeJSON = $('#followingType').val();
		var typesJSON = JSON.parse(typeJSON);
		return typesJSON[val];
	}

	function orgFormatter(val) {
		var orgJSONs = $.parseJSON(orgJSON);
		return "<div style=\"word-wrap:break-word;word-break:break-all\">"
				+ orgJSONs[val] + "</div>";
	}
	
	function initDate(){
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
		startobj.datepicker("setDate", $("#followingTradeDate").val());
		endobj.datepicker("setDate", $("#followingTradeDate").val());
	}
	
	function initTable(){
		
		tableObj.bootstrapTable({
			url : apiUrl,
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable: true,
			queryParams : {
				orgNo : $("#followingOrgNoInit").val(),
				startDate : $("#followingTradeDate").val(),
				endDate : $("#followingTradeDate").val()
            },
//			onLoadSuccess : function(result) {
//				if(result != null){
//					followDetailArray = [];
//					if (result.data[0].bankAllAmount != null) {
//						followDetailArray.push({
//							name : "银行卡",
//							payAmount : result.data[0].bankAllAmount
//						});
//					}
//					if (result.data[0].socialInsuranceAmount != null) {
//						followDetailArray.push({
//							name : "医保",
//							payAmount : result.data[0].socialInsuranceAmount
//						});
//					}
//					if (result.data[0].cashAllAmount != null) {
//						followDetailArray.push({
//							name : "现金",
//							payAmount : result.data[0].cashAllAmount
//						});
//					}
//					if (result.data[0].alipayAllAmount != null) {
//						followDetailArray.push({
//							name : "支付宝",
//							payAmount : result.data[0].alipayAllAmount
//						});
//					}
//					if (result.data[0].wechatAllAmount != null) {
//						followDetailArray.push({
//							name : "微信",
//							payAmount : result.data[0].wechatAllAmount
//						});
//					}
//				}
//			}
		});

		tableObj2.bootstrapTable({
			url : apiUrl + "/exceptionTrade",
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable: true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
//			height: $(window).height()-390,
			queryParams : {
				orgNo : $("#followingOrgNoInit").val(),
				startDate : $("#followingTradeDate").val(),
				endDate : $("#followingTradeDate").val()
            },
		});
		resetTableHeight(tableObj2, 390);
	}
	
	function initShowOrHidden(billSource){
		
		//隐藏显示列
		if (billSource != "1") {
			tableObj.bootstrapTable('hideColumn', 'billSource');
			tableObj2.bootstrapTable('hideColumn', 'billSource');
		}
		if (patType != "1") {
			tableObj.bootstrapTable('hideColumn', 'patType');
			tableObj2.bootstrapTable('hideColumn', 'patType');
		}
	}
	
	function initTableClick(){
		//点击行数据时,进入查看界面
		//		tableObj.on("click-row.bs.table",function(e, row, element){
		//			var orgNo= followingTree.getVal;
		//			startDate = startobj.val();
		//			endDate = endobj.val();
		//		    options.url=apiUrl+"/exceptionTrade";
		//		    if($("#correction").is(':checked')){
		//				correction=1;
		//			}else{
		//				correction=2;
		//			}
		//			tableObj2.bootstrapTable('refreshOptions', {
		//				  pageNumber:1,
		//				  queryParams:function(params){
		//					    var queryObj ={orgNo:orgNo,startDate:startDate,endDate:endDate,patType:row.patType,billSource:row.billSource,correction:correction};
		//		                var query = $.extend( true, params, queryObj);
		//		                return query;
		//		            }
		//			});
		//			$('.success').removeClass('success');//去除之前选中的行的，选中样式
		//			$(element).addClass('success');//添加当前选中的 success样式用于区别
		//			var index = $('#formTempDetailTable_new').find('tr.success').data('index');//获得选中的行的id
		//		});
		
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
		
		$("#followTable").find("button[name=followBtn]").on('click', function(){
			$("#followTable").find("button[name=followBtn]").css('background-color', 'white');
		    $(this).css('background-color', '#ECF5FF');
		});
		
	}

	function init(orgJSON_temp, billSource, patType) {
		orgJSON = orgJSON_temp;
		initDate();
		initTree();
		initTable();
		initShowOrHidden(billSource);
//		initTableClick();
	}
	
	function showExceptionTrade(dataSourceType,e){
		exceptionTrade = dataSourceType ;
		var orgNo= followingTree.getVal;
		startDate = startobj.val();
		endDate = endobj.val();
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
	
	function number(value, row, index) {
		var pageSize = tableObj2.bootstrapTable('getOptions').pageSize;//通过表的#id 可以得到每页多少条
		var pageNumber = tableObj2.bootstrapTable('getOptions').pageNumber;//通过表的#id 可以得到当前第几页
		return pageSize * (pageNumber - 1) + index + 1; //返回每条的序号： 每页条数 * （当前页 - 1 ）+ 序号
	}
	function numberSum(value, row, index) {
		var pageSize = tableObj.bootstrapTable('getOptions').pageSize;//通过表的#id 可以得到每页多少条
		var pageNumber = tableObj.bootstrapTable('getOptions').pageNumber;//通过表的#id 可以得到当前第几页
		return pageSize * (pageNumber - 1) + index + 1; //返回每条的序号： 每页条数 * （当前页 - 1 ）+ 序号
	}

	function removeOrAddFlow(e) {
		showExceptionTrade(exceptionTrade);
	}
	
	function moneyFormat(val, row, index){
		return new Number(val).toFixed(2);
	}
	return {
		init : init,
		search : search,
		formatHandler : formatHandler,
		refundHandler : refundHandler,
		formatter : formatter,
		orgFormatter : orgFormatter,
		exportData : exportData,
		startRec : startRec,
		number : number,
		numberSum : numberSum,
		removeOrAddFlow : removeOrAddFlow,
		operation : operation,
		showFollowDetailDlg : showFollowDetailDlg,
		dealFollow:dealFollow,
		save:save,
		followHandler:followHandler,
		showHandDealInfo:showHandDealInfo,
		showExceptionTrade:showExceptionTrade,
		refundSave:refundSave,
		moneyFormat:moneyFormat
	}
})();

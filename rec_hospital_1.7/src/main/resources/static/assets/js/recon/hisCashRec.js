NB.ns("app.admin").hisCashRec = (function() {	
	//表格
	var tableObj =$("#hisCashRecDataTable");
	//表单
	var formObj = $("#hisCashRecSearchForm");	
	//对话框
	var dlgObj = $('#hisCashRecDlg');
	var dlgFormObj = dlgObj.find("dl[form=detail]");
	// 时间控件
	var rangeTimeObj = $("#hisCashRecTime");
	//请求路径
	var apiUrl = '/admin/hisCashRec/data';
	var cashRecDialog = $("#cashRec-data-div")
	
	var targetBillSource = '';
	var isCollapse = false;//是否展开更多，默认关闭

	var hisCashRecTree;
	var options = { 
            beforeSubmit:  function(formData, jqForm, options){
            	 var flg = formObj.data('bootstrapValidator');
			     return flg.validate().isValid();
            },
            success: function(result){
            	if(result.success){
            		$.NOTIFY.showSuccess ("提醒", "操作成功",'');
            		reflush();
            	}else{
            		if(result.message){
            			$.NOTIFY.showError  ("错误", result.message,'');
            		}
            	}
            },
            url:       apiUrl ,      
            type:      'post', 
            dataType:  'json',
            clearForm: false ,       
            resetForm: false ,       
            timeout:   3000 
        };
	
	//查询
	function search(th){
		var orgNo= hisCashRecTree.getVal;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var payType = $('#hisCashRec_payType').val();
		if (payType == '全部' || payType == null) {
			payType = "";
		}
		var patType = $('#hisCashRec_patType').val();
		if (patType == '全部' || patType == null) {
			patType = "";
		}
		var billSource = $('#hisCashRec_billSource').val();
		if (billSource == '全部' || billSource == null) {
			billSource = "";
		}
		
		var payFlowNo = $('#hisCashRec_sysSerial').val();

		var hisOrderState = $('#hisCashRec_orderState').val();
		if (hisOrderState == '9999' || hisOrderState == null) {
			hisOrderState = "";
		}
		var cashier=$("#cashier").val();
		var hisCredentialsNo = $('#hisCredentialsNo').val();
		var hisOriPayFlowNo = $('#hisOriPayFlowNo').val();
		var hisInvoiceNo = $('#hisInvoiceNo').val();
		var hisPatCode = $('#hisPatCode').val();
		
		var hisMzCode=$("#hisMzCode").val();
		
		var businessFlowNo=$("#businessFlowNo").val();
		
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime + " 00:00:00";
			endDate = rangeTime + " 23:59:59";
//			startDate = rangeTime.split("~")[0];
//			endDate = rangeTime.split("~")[1];
		}
//		collect(orgNo);
		searchSumary(orgNo, startDate.split(" ")[0]);
		tableObj.bootstrapTable('refreshOptions', {
			  resizable: true,
			  pageNumber:1,
			  queryParams:function(params){
				    var queryObj ={orgNo:orgNo,payType:payType,patType:patType,startTime:startDate,
				    		endTime:endDate,payFlowNo:payFlowNo, hisOrderState:hisOrderState, 
				    		hisCredentialsNo:hisCredentialsNo, flowNo:hisOriPayFlowNo, 
				    		hisInvoiceNo:hisInvoiceNo, hisPatCode:hisPatCode,hisMzCode:hisMzCode,
				    		cashier:cashier,cashBillSource:billSource,
				    		businessFlowNo:businessFlowNo};
	                var query = $.extend( true, params, queryObj);
	                return query;
	            },
				onPreBody:function(data){
	            	$(th).button('loading');
	            },
	            onLoadSuccess:function(data){
	            	$(th).button("reset");
		        }
		});
//		searchSumary(orgNo, startDate);
	}
	//机构树
	function formaterOrgProps(){
		var setting = {
			        view: {
			          dblClickExpand: false,
			          showLine: false,
			          selectedMulti: false,
			          fontCss: {fontSize:'18px'}
			        },
			        data: {
				          key:{
				            isParent: "parent",
				            title:''
				          },
				          simpleData: {
				            enable:true,
				            idKey: "id",
				            pIdKey: "parent",
				            rootPId: null
				          }
				    }
			 };
		$.ajax({
           url:"/admin/organization/data",
           type:"get",
           contentType:"application/json",
           dataType:"json",
           async:false,
           success:function(msg){
        	   //这个树形下拉会在和其同级位置生成一个默认隐藏的input输入框以提供表单数据
        	   hisCashRecTree = $("#hisCashRecTree").ztreeview({
	       			name: 'name',
	       			key: 'code', 
	       			//是否
	       			clearable:true,
	                   expandAll:true,
	       			data: msg
	       		}, setting);
        	   hisCashRecTree.updateCode(msg[0].id,msg[0].code);
        	   // 选择隐藏还是现实机构下拉选择
        	   var length = msg.length;
        	   if(length && length>1){
        		   $("#hisCashRecTree").parent().parent().parent().show();
        	   }else{
        		   $("#hisCashRecTree").parent().parent().parent().hide();
        	   }
        	   
//        	   collect(hisCashRecTree.getVal);
        	   var startDate ;
        	   var endDate  ;
        	   var rangeTime = rangeTimeObj.val();
        	   if(rangeTime){
        		   startDate = rangeTime + " 00:00:00";
       			endDate = rangeTime + " 23:59:59";
//        	   	startDate = rangeTime.split("~")[0];
//        	   	endDate = rangeTime.split("~")[1];
        	   }
//   			   var queryObj  = {orgNo:hisCashRecTree.getVal,startTime:startDate,endTime:endDate};
//   			   queryData(tableObj, queryObj);
           }
       });
	}
	
	//计算渠道金额
	function collect(orgNo){
		var orgNo= hisCashRecTree.getVal;
		if (orgNo === '全部' || orgNo === null) {
			orgNo = "";
		}
//		var payType = $('#hisCashRec_payType').val();
//		if (payType == '全部' || payType == null) {
//			payType = "";
//		}
//		var patType = $('#hisCashRec_patType').val();
//		if (patType == '全部' || patType == null) {
//			patType = "";
//		}
//		var payFlowNo = $('#hisCashRec_sysSerial').val();
//
//		var hisOrderState = $('#hisCashRec_orderState').val();
//		if (hisOrderState == '9999' || hisOrderState == null) {
//			hisOrderState = "";
//		}
//		var cashier=$("#cashier").val();
//		var hisCredentialsNo = $('#hisCredentialsNo').val();
//		var hisOriPayFlowNo = $('#hisOriPayFlowNo').val();
//		var hisInvoiceNo = $('#hisInvoiceNo').val();
//		var hisPatCode = $('#hisPatCode').val();
//		var hisMzCode=$("#hisMzCode").val();
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime + " 00:00:00";
			endDate = rangeTime + " 23:59:59";
//			startDate = rangeTime.split("~")[0];
//			endDate = rangeTime.split("~")[1];
		}
		var cashBillSource = $('#hisCashRec_billSource').val();
		if (cashBillSource == '全部' || cashBillSource == null) {
			cashBillSource = "";
		}
//		var businessFlowNo=$("#businessFlowNo").val();
		var url = apiUrl+"/countSum";
		$.ajax({
	           url:url,
	           type:"get",
	           data:{
	        	   orgNo:orgNo,payType:payType,startTime:startDate,endTime:endDate,payFlowNo:payFlowNo, hisOrderState:hisOrderState, 
		    		hisCredentialsNo:hisCredentialsNo, flowNo:hisOriPayFlowNo, hisInvoiceNo:hisInvoiceNo, hisPatCode:hisPatCode,
		    		hisMzCode:hisMzCode,cashier:cashier,patType:patType,cashBillSource:cashBillSource,businessFlowNo:businessFlowNo,
		    		hisOrderState:hisOrderState
	           },
	           contentType:"application/json",
	           dataType:"json",
	           success:function(result){
//	        	   	$("#hisAllAmount").html(new Number(result.data.allAmount).toFixed(2));
//	        	   	$("#hisAllNum").html(result.data.allNum);
	        	   
	           }
	       });
	}
	
	//支付类型数据
	function tradePayTypeData(){
		$.ajax({
            url:"/admin/reconciliation/typeValue?typeValue=Pay_Type&isIncludeAll=true",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	
            	changeSelectData(msg);
            	$('#hisCashRec_payType').select2({
					data:msg,
					placeholder: '==请选择类型==',
					minimumResultsForSearch: -1,
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
	
	//门诊住院
	function tradePatTypeData(){
		$.ajax({
            url:"/admin/reconciliation/typeValue?typeValue=pat_code&isIncludeAll=true",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	
            	changeSelectData(msg);
            	$('#hisCashRec_patType').select2({
					data:msg,
					placeholder: '==请选择类型==',
					minimumResultsForSearch: -1,
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
	
	//账单来源
	function billSourceData(){
		$.ajax({
            url:"/admin/reconciliation/typeValue?typeValue=cash_bill_source&isIncludeAll=true",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	
            	changeSelectData(msg);
            	$('#hisCashRec_billSource').select2({
					data:msg,
					placeholder: '==请选择类型==',
					minimumResultsForSearch: -1,
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
	
	function orgFormatter(val) {
		var org = $('#hisCashRecOrgNo').val();
		var orgJSON = JSON.parse(org);
		return '<p data-toggle="tooltip" title=\"'+ orgJSON[val] +'\">' + orgJSON[val] +'</p>'
//		return orgJSON[val];
	}
	function formatter(val) {
		var typeJSON = $('#hisCashRecType').val();
		var typesJSON = JSON.parse(typeJSON);
		if(val==null){
			return "未知";
		}
		if(typesJSON[val]==undefined){
			return "未知";
		}
		return '<p data-toggle="tooltip" title=\"'+ typesJSON[val] +'\">' + typesJSON[val] +'</p>'
	}
	function patIdFormatter(val,row) {
		if(row.patType == 'mz'){
			return row.mzCode;
		}else{
			return row.patCode;
		}
	}
	
	function formatterPatType(val) {
		if(val == "mz"){
			return '<p data-toggle="tooltip" title=\"门诊\">门诊</p>'
		}else if(val == "zy") {
			return '<p data-toggle="tooltip" title=\"住院\">住院</p>'
		}
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
		
		$("#hisCashRecTable").find("button[name=hisCashRecBtn]").on('click', function(){
			
			$("#hisCashRecTable").find("button[name='hisCashRecBtn']").css('background-color', 'white');
		    $(this).css('background-color', '#ECF5FF');
		});
		
	}
	function putDataPayMethod(headData){
		var box = cashRecDialog.find('.hisCashRec-pay-methods-box');
//		if(headData&&headData.length>0){
			box.show();
//		}else{
//			box.hide();
//			return;
//		}
//		var errBadge = '<span class="badge hisCashRec-diff-err">异常</span>';
		box.html('')
		var html = '';
//		if(headData&&headData.length>0){
//			var moreEle = '';
//			if(headData.length>1){
//				moreEle ='<a class="pay-type-more-box" onclick="app.admin.hisCashRec.clickCollapse()" data-toggle="collapse" data-parent="#cashRecPayMethodsBox"  href="#electronPayTypecollapse">'
//				+'<span class="fa fa-angle-double-down more-arrow"></span>'
//				+'<span class="pay-type-more-text">更多详情</span>'
//				+'</a>'
//			}
			
//			var ul = '';
//			$.each(headData,function(index,data){
//				if(data.payType=='hj'){
					html += '<ul class="cash segment3 clear ">'
						+'<li class="cash-list">'
					+'<div class="list-content block-center-hv clear">'
				     +'<div class="left">'
				         +'<div class="cashcircle cashcircle-allamount"></div>'
				             +'</div>'
				         +'<div class="right">'
				         +'<p class="title">HIS应收总金额</p>'
				           +'<p class="title color-warning payAllAmount"></p>'
				           +'<p class="title">支付笔数&nbsp;<span class="num payAcount"></span></p>'
				           +'<p class="title">退款笔数&nbsp;<span class="num refundAcount"></span></p>'
				           +'</div>'
				         +'</div>'
				     +'</li>'
				 +'<li class="cash-list">'
				    +'<div class="list-content block-center-hv clear">'
				        +'<div class="left">'
				            +'<div class="cashcircle cashcircle-his"></div>'
				                +'</div>'
				            +'<div class="right">'
				            +'<p class="title">门诊</p>'
				                +'<p class="title color-warning hisAllAmount"></p>'
				                +'<p class="title">支付笔数&nbsp;<span class="num hisPayAcount"></span></p>'
				                +'<p class="title">退款笔数&nbsp;<span class="num hisRefundAcount"></span></p>'
				                +'</div>'
				            +'</div>'
				        +'</li>'
				    +'<li class="cash-list">'
				    +'<div class="list-content block-center-hv clear">'
				        +'<div class="left">'
				            +'<div class="cashcircle cashcircle-dif"></div>'
				                +'</div>'
				            +'<div class="right">'
				            +'<p class="title">住院</p>'
			                +'<p class="title color-warning zyAllAmount"></p>'
			                +'<p class="title">支付笔数&nbsp;<span class="num zyPayAcount"></span></p>'
			                +'<p class="title">退款笔数&nbsp;<span class="num zyRefundAcount"></span></p>'
				                +'</div>'
				            +'</div>'
				        +'</li>'
				+'</ul>';
//					}else{
//					}
//			})
//			if(ul){
//				html += '<div id="electronPayTypecollapse" class="panel-collapse '+(isCollapse?'in':'collapse')+'"><ul class="pay-methods">'+ul+'</ul></div>'
//			}
//		}
		box.html(html)
		if(isCollapse){
			$('#cashRecPayMethodsBox .pay-type-more-box .more-arrow').removeClass('fa-angle-double-down');
			$('#cashRecPayMethodsBox .pay-type-more-box .more-arrow').addClass('fa-angle-double-up');
			$('#cashRecPayMethodsBox .pay-type-more-box .pay-type-more-text').text('收起')
		}
	}
	function clickCollapse(){
		var arr = $('#cashRecPayMethodsBox .pay-type-more-box .more-arrow');
		if(arr.hasClass('fa-angle-double-down')){
			arr.removeClass('fa-angle-double-down');
			arr.addClass('fa-angle-double-up');
			$('#cashRecPayMethodsBox .pay-type-more-box .pay-type-more-text').text('收起')
			isCollapse=true
		}else{
			arr.removeClass('fa-angle-double-up');
			arr.addClass('fa-angle-double-down');
			$('#cashRecPayMethodsBox .pay-type-more-box .pay-type-more-text').text('更多详情')
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
			txt += " <li data-value='"+billSource.value+"' data-text='"+(billSource.value?billSource.name:'')+"' class='"+colorLinkClass+"' onclick='app.admin.hisCashRec.chsPayMethods(this)' >"+billSource.name;
//			if(billSource.exceptionNum&&billSource.exceptionNum!='0'){
//				txt += "<span class='badge hisCashRec-badge'>"+billSource.exceptionNum+"</span>";
//			}
			txt += "</li>";
		})
        // 支付详情数据为空的时候隐藏
        if (txt === "") {
            cashRecDialog.find(".pay-type-list").hide();
            cashRecDialog.find(".hisCashRec-pay-methods-box").hide();
        } else {
            cashRecDialog.find(".pay-type-list").show();
            cashRecDialog.find(".hisCashRec-pay-methods-box").show();
            cashRecDialog.find(".pay-type-list").html(txt);
        }
        initLeftRightBtn();
    }
	function searchSumary(orgNo, startDate){
		startTime = startDate+" 00:00:00";
		endTime = startDate+" 23:59:59";
		$.ajax({
			url:apiUrl+"/searchSumary",
			type:"GET",
			data:{"orgNo":orgNo, "startTime":startTime,"endTime":endTime,"cashBillSource":targetBillSource},
			dataType:"json",
			success:function(result){
				putDataPayDetail(result.data.headLine);
				putDataPayMethod(result.data.headData);
				putDataSumary(result.data.headData);
			}
		});
	}
	function putDataSumary(recResult){
		cashRecDialog.find(".payAllAmount").html(moneyFormat(recResult[2].hzAllAmount));//汇总金额
		cashRecDialog.find(".payAcount").html(numberFormat(recResult[2].hzPayCount));//汇总缴费笔数
		cashRecDialog.find(".refundAcount").html(numberFormat(recResult[2].hzRefundCount));//汇总退费笔数
		cashRecDialog.find(".hisAllAmount").html(moneyFormat(recResult[0].mzAllAmount));//门诊金额
		cashRecDialog.find(".hisPayAcount").html(numberFormat(recResult[0].mzPayCount));//门诊缴费笔数
		cashRecDialog.find(".hisRefundAcount").html(numberFormat(recResult[0].mzRefundCount));//门诊退费笔数
		cashRecDialog.find(".zyAllAmount").html(moneyFormat(recResult[1].zyAllAmount));//住院金额
		cashRecDialog.find(".zyPayAcount").html(numberFormat(recResult[1].zyPayCount));//住院缴费笔数
		cashRecDialog.find(".zyRefundAcount").html(numberFormat(recResult[1].zyRefundCount));//住院退费笔数
	}
	var imgFileJSON = {'0149':'zgyh', '0249':'wx', '0349':'zfb', '1649':'jhzf', '0559':'ybzf'};
	function chsPayMethods(th){
		// 改变选中颜色
		var obj = cashRecDialog.find(".pay-type-list").find("li");
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
		var liText = $(th).data('text');
		$("#billSource").val(liText);

		var orgNo = hisCashRecTree.getVal;
		var startDate;
 	   	var endDate;
 	   	var rangeTime = rangeTimeObj.val();
 	   	if(rangeTime) {
 	   	startDate = rangeTime;
		endDate = rangeTime;
//	 	   	startDate = rangeTime.split("~")[0];
//	 	   	endDate = rangeTime.split("~")[1];
 	   	}
// 	   startDate= startDate.split(" ")[0];
		searchSumary(orgNo,startDate)
	}
	
	function init(accountDate) {
		initDate(accountDate);
		formaterOrgProps();
		tradePayTypeData();
		tradePatTypeData();
		billSourceData();
		if($("#hisIsDisplay").val()==1){
			$("#hisCount").show();
		}
		$("#hisAllAmount").html("0.00");
		$("#hisAllNum").html(0);
		
		var startDate;
 	   	var endDate;
 	   	var rangeTime = rangeTimeObj.val();
 	   	if(rangeTime) {
 	   	startDate = rangeTime + " 00:00:00";
		endDate = rangeTime + " 23:59:59";
//	 	   	startDate = rangeTime.split("~")[0];
//	 	   	endDate = rangeTime.split("~")[1];
 	   	}

		searchSumary(hisCashRecTree.getVal,startDate.split(" ")[0])
 	   	
		var queryObj = {orgNo:hisCashRecTree.getVal,startTime:startDate,endTime:endDate};
		//初始化表格
		tableObj.bootstrapTable({
   			url : apiUrl,
   			dataType : "json",
   			uniqueId : "id",
   			resizable: true,
   			singleSelect : true,
   			pagination : true, // 是否分页
   			sidePagination : 'server',// 选择服务端分页
   			queryParams:function(params){
   	            var query = $.extend( true, params, queryObj);
   	            return query;
   	        }
   		});
		$('#hisCashRecRecPayStepBox').on('slide.bs.carousel', function () {
			if($('#hisCashRecRecPayStepBox').find('.item:first-child').hasClass('active')){
				$('.hisCashRec-rec-pay-step-left').addClass('disabled');
			}else{
				$('.hisCashRec-rec-pay-step-left').removeClass('disabled');				
			}
			if($('#hisCashRecRecPayStepBox').find('.item:last-child').hasClass('active')){
				$('.hisCashRec-rec-pay-step-right').addClass('disabled');
			}else{
				$('.hisCashRec-rec-pay-step-right').removeClass('disabled');
			}
		})
		$('#cashRecBillSourceLeft').on('click',function(){
			var left = cashRecDialog.find(".pay-type-list-content").scrollLeft()-200
			cashRecDialog.find(".pay-type-list-content").animate({scrollLeft:left},200);
			initLeftRightBtn()
		})
		$('#cashRecBillSourceRight').on('click',function(){
			var left = cashRecDialog.find(".pay-type-list-content").scrollLeft()+200
			cashRecDialog.find(".pay-type-list-content").animate({scrollLeft:left},200);
			initLeftRightBtn()
		})
		window.onresize=function(){initLeftRightBtn()}
	}
	function initLeftRightBtn(){
		setTimeout(function(){
			var left = cashRecDialog.find(".pay-type-list-content").scrollLeft()
			var scrollWidth = cashRecDialog.find("#cashRecBillSourceContent")[0].scrollWidth;
			var offsetWidth = cashRecDialog.find("#cashRecBillSourceNav")[0].offsetWidth;
			var right = scrollWidth - offsetWidth - left
			if(right==0){
				$('#cashRecBillSourceRight').addClass('disable')
			}else{
				$('#cashRecBillSourceRight').removeClass('disable')
			}
			if(left==0){
				$('#cashRecBillSourceLeft').addClass('disable')
			}else{
				$('#cashRecBillSourceLeft').removeClass('disable')
			}
			if(left==0&&right==0){
				$('#cashRecBillSourceLeft').hide();
				$('#cashRecBillSourceRight').hide();
			}else{
				$('#cashRecBillSourceLeft').show();
				$('#cashRecBillSourceRight').show();
			}
		},210)
	}
	function number(value, row, index) {
		   var pageSize=tableObj.bootstrapTable('getOptions').pageSize;
	       var pageNumber=tableObj.bootstrapTable('getOptions').pageNumber;
	       return pageSize * (pageNumber - 1) + index + 1;
	}
	
	//导出
	function exportData() {
		var orgNo= hisCashRecTree.getVal;
		var orgName= hisCashRecTree.getText;
		if (orgNo === '全部' || orgNo === null) {
			orgNo = "";
		}
		var payType = $('#hisCashRec_payType').val();
		if (payType == '全部' || payType == null) {
			payType = "";
		}
		var payFlowNo = $('#hisCashRec_sysSerial').val();

		var patType = $('#hisCashRec_patType').val();
		if (patType == '全部' || patType == null) {
			patType = "";
		}
		var billSource = $('#hisCashRec_billSource').val();
		if (billSource == '全部' || billSource == null) {
			billSource = "";
		}
		var hisOrderState = $('#hisCashRec_orderState').val();
		if (hisOrderState == '全部' || hisOrderState == null) {
			hisOrderState = "";
		}
		var businessFlowNo = $('#businessFlowNo').val();
		var hisCredentialsNo = $('#hisCredentialsNo').val();
		var hisOriPayFlowNo = $('#hisOriPayFlowNo').val();
		var hisInvoiceNo = $('#hisInvoiceNo').val();
		var hisPatCode = $('#hisPatCode').val();
		var hisMzCode=$("#hisMzCode").val();
		var cashier=$("#cashier").val();
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime + " 00:00:00";
			endDate = rangeTime + " 23:59:59";
//			startDate = rangeTime.split("~")[0];
//			endDate = rangeTime.split("~")[1];
		}
		
		var hour = $.fn.getHour(startDate, endDate);
		if(hour > 24*31){
			$.NOTIFY.showError("错误", '时间范围超过31天，请缩短时间范围', '');
			return false;
		}
		
		bootbox.confirm('确定执行此操作?',function(r){
	        if (r){
	        	
	        	var where = '/admin/hisCashRec/exportData?a=1';
	    		
	    		if(orgNo != null && orgNo != undefined){
	    			where = where + '&orgNo=' + orgNo;
	    		}
	    		if(orgName != null && orgName != undefined){
	    			where = where + '&orgName=' + orgName;
	    		}
	    		
	    		if(payType != null && payType != undefined){
	    			where = where + '&payType=' + payType;
	    		}
	    		if(payFlowNo != null && payFlowNo != undefined){
	    			where = where + '&payFlowNo=' + payFlowNo;
	    		}
	    		if(hisOrderState != null && hisOrderState != undefined){
	    			where = where + '&hisOrderState=' + hisOrderState;
	    		}
	    		if(hisCredentialsNo != null && hisCredentialsNo != undefined){
	    			where = where + '&hisCredentialsNo=' + hisCredentialsNo;
	    		}
	    		if(hisOriPayFlowNo != null && hisOriPayFlowNo != undefined){
	    			where = where + '&flowNo=' + hisOriPayFlowNo;
	    		}
	    		if(hisInvoiceNo != null && hisInvoiceNo != undefined){
	    			where = where + '&hisInvoiceNo=' + hisInvoiceNo;
	    		}
	    		if(hisPatCode != null && hisPatCode != undefined){
	    			where = where + '&hisPatCode=' + hisPatCode;
	    		}
	    		if(hisMzCode != null && hisMzCode != undefined){
	    			where = where + '&hisMzCode=' + hisMzCode;
	    		}
	    		if(startDate != null && startDate != undefined){
	    			where = where + '&startTime=' + startDate;
	    		}
	    		if(endDate != null && endDate != undefined){
	    			where = where + '&endTime=' + endDate;
	    		}
	    		if(cashier !=null && cashier != undefined){
	    			where = where + '&cashier=' + cashier;
	    		}
	    		if(patType !=null && patType != undefined){
	    			where = where + '&patType=' + patType;
	    		}
	    		if(billSource !=null && billSource != undefined){
	    			where = where + '&cashBillSource=' + billSource;
	    		}
	    		if(businessFlowNo !=null && businessFlowNo != undefined){
	    			where = where + '&businessFlowNo=' + businessFlowNo;
	    		}
	    		
	    		
	    		where = where + '&t='+new Date().getTime();
	    		window.location.href=where;
	        }
	    });
	}
	function moneyFormat(val, row, index){
		return new Number(val).toFixed(2);
	}
	
	function formatOpt(index,row) {
		return "<a href='javascript:;' onclick='app.admin.hisCashRec.detail("+ row.id + ")' class='btn btn-info btn-sm m-primary '> 详情 </a>  &nbsp;"
	}
	function formatterPayType(val) {
		debugger
		var typeJSON = $('#hisCashRecType').val();
		var typesJSON = JSON.parse(typeJSON);
		if(typesJSON[val]==null || typesJSON[val]==undefined){
			return typesJSON[val];
		}
		return typesJSON[val];
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
	
	function detail(id){
		var row = tableObj.bootstrapTable('getRowByUniqueId',id);		
		dlgFormObj.loadDetailReset();
		dlgFormObj.loadDetail(row);
		dlgFormObj.find("dd[data-name=orgNo]").html(orgFormatter(row.orgNo));
		dlgFormObj.find("dd[data-name=payType]").html(formatter(row.payType));
		dlgFormObj.find("dd[data-name=orderState]").html(formatter(row.orderState));
		dlgFormObj.find("dd[data-name=patType]").html(formatterPatType(row.patType));
		dlgFormObj.find("dd[data-name=payAmount]").html(moneyFormat(row.payAmount));
		dlgObj.modal('show');
	}
	
	/**
	 * 初始化日期
	 */
	function initDate(accountDate) {
		var nowDate = new Date();
		var rangeTime = accountDate;
		var startLayDate = laydate.render({
			elem : '#hisCashRecTime',
			btns: ['confirm'],
			theme : '#A9BCF5',
			type: "date",
			value: rangeTime,
			format:"yyyy-MM-dd",
			max: nowDate.getTime()
		});
		/*var startLayDate = laydate.render({
			elem : '#hisCashRecTime',
			btns: ['confirm'],
			theme : '#A9BCF5',
			type: "datetime",
			value: rangeTime,
			format:"yyyy-MM-dd HH:mm:ss",
			range:"~",
			max: nowDate.getTime(),
			ready: function(date){
				var layym = $(".laydate-main-list-0 .laydate-set-ym span").eq(0).attr("lay-ym").split("-")[1];
				if(layym == (nowDate.getMonth()+1)){
					$(".laydate-main-list-0 .laydate-prev-m").click();
				}
			}
		});*/
	}
	
	function numberFormat(val){
		if(isNaN(val)){
			return new Number(0);
		}
		return new Number(val);
	}
	
	return {
		init : init,
		search:search,
		formatter:formatter,
		formatterPatType : formatterPatType,
		patIdFormatter:patIdFormatter,
		orgFormatter:orgFormatter,
		number:number,
		exportData:exportData,
		moneyFormat:moneyFormat,
		detail:detail,
		formatOpt:formatOpt,
		chsPayMethods:chsPayMethods,
		formatterPayType:formatterPayType,
		formatMoney:formatMoney,
		clickCollapse:clickCollapse,
		numberFormat:numberFormat
	}
})();
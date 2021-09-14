NB.ns("app.admin").thirdTrade = (function() {
	//表格
	var tableObj =$("#thirdTradeDataTable");
	
	var billTable=$("#billDataTable");
	var yibaoTable=$("#yibaoDataTable");
	var cashTable=$("#cashDataTable");
	var billType=1;
	var yibaoType=1;
	var cashType=1
	//表单
	var formObj = $("#thirdTradeSearchForm");
	// 时间控件
	var rangeTimeObj = $("#thirdtradeTime");
	//请求路径
	var apiUrl = '/admin/thirdTrade/data';
	var thirdTradeTree;
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
	
	//导出
	function exportData() {
		var orgNo= thirdTradeTree.getVal;
		var orgName= thirdTradeTree.getText;
		if(orgNo=="全部" || orgNo==null )orgNo="";
		var payType = $('#thirdtrade_payType').val();
		if(payType=="全部" || payType==null )payType="";
		var billSource = $('#thirdtrade_billSource').val();
		if(billSource=="全部" || billSource==null )billSource="";
		var sysSerial = $('#thirdtrade_sysSerial').val();
		var businessFlowNo = $("#thirdtrade_businessFlowNo").val();
		// 支付账号
		var payAccount = $("#payAccount").val();
		var orderState = $('#thirdtrade_orderState').val();
		if(orderState=="全部" || orderState==null )orderState="";
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		}
		var sortName = tableObj.bootstrapTable('getOptions').sortName||'';
		var sortOrder = tableObj.bootstrapTable('getOptions').sortOrder;
		var hour = $.fn.getHour(startDate, endDate);
		if(hour > 24*31){
			$.NOTIFY.showError("错误", '时间范围超过31天，请缩短时间范围', '');
			return false;
		}
		bootbox.confirm('确定执行此操作?',function(r){
            if (r){
				where = 'sort=' + sortName + '&order=' + sortOrder + '&orgNo=' + orgNo + '&sysSerial=' + sysSerial
					+ '&startTime=' + startDate + '&endTime=' + endDate + "&payType=" + payType + "&orgName=" + orgName
					+ "&billSource=" + billSource + '&businessFlowNo=' + businessFlowNo + "&orderState=" + orderState + "&payAccount=" + payAccount;
        		url = apiUrl+'/dcExcel?' + where;
                window.location.href=url;
            }
        });
	}
	//查询
	function search(th){
		var orgNo= thirdTradeTree.getVal;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		if(orgNo=="全部" || orgNo==null )orgNo="";
		var payType = $('#thirdtrade_payType').val();
		if(payType=="全部" || payType==null )payType="";
		var billSource = $('#thirdtrade_billSource').val();
		if(billSource=="全部" || billSource==null )billSource="";
		var sysSerial = $('#thirdtrade_sysSerial').val();
		var businessFlowNo = $("#thirdtrade_businessFlowNo").val();
		var orderState = $('#thirdtrade_orderState').val();
		var payAccount = $('#payAccount').val();
		if(orderState=="全部" || orderState==null )orderState="";
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		}
		
		collect(orgNo);
		var thirdtradeSummaryDisplay = $("#thirdtradeSummaryDisplay").val();
   		if(thirdtradeSummaryDisplay=="true") {
   			summary(orgNo);
   		}
		
		tableObj.bootstrapTable('refreshOptions', {
			  pageNumber:1,
			  queryParams:function(params){
				  var queryObj = {
					  orgNo: orgNo, payType: payType, startTime: startDate,
					  endTime: endDate, sysSerial: sysSerial, billSource: billSource,
					  businessFlowNo: businessFlowNo, orderState: orderState, payAccount: payAccount
				  };
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
	}
	//详情按钮
	function formatOpt(val, row){ 
		var name = '<a href="javascript:" class=\'btn btn-info btn-sm m-primary\' onclick="app.admin.thirdTrade.details(\''+row.id +'\',\''+row.payType +'\')">' + "详情" + "</a>";
		return name;
	}
	//详情查看
	function details(id,payType){
    	   if(payType!='0049'&&payType!='0449'){//渠道
    		   if(billType==2){
    			var dlgObj = $("#billData");
    			var dlgFormObj = dlgObj.find("dl[form=detail]");
    			$.ajax({
		            url:apiUrl + '/billDetails?id=' + id,
		            data:{},
		            type:"get",
		            dataType:"json",
		            success:function(data){
	            		var obj =  data.rows;
	            		dlgFormObj.find("dd[data-name=payFlowNo]").html(obj[0].payFlowNo);
	            		dlgFormObj.find("dd[data-name=orgNo]").html(orgFormatter(obj[0].orgNo));
	                	dlgFormObj.find("dd[data-name=orderState]").html(formatter(obj[0].orderState));
	                	dlgFormObj.find("dd[data-name=payType]").html(formatter(obj[0].payType));
	                	dlgFormObj.find("dd[data-name=paySource]").html(formatter(obj[0].paySource));
	                	dlgFormObj.find("dd[data-name=payTermNo]").html(obj[0].payTermNo);
	                	dlgFormObj.find("dd[data-name=payAccount]").html(obj[0].payAccount);
	                	dlgFormObj.find("dd[data-name=payAmount]").html(moneyFormat(obj[0].payAmount));
	                	dlgFormObj.find("dd[data-name=payBatchNo]").html(obj[0].payBatchNo);
	                	dlgFormObj.find("dd[data-name=tradeDatatime]").html(obj[0].tradeDatatime);
	                	dlgFormObj.find("dd[data-name=businessFlowNo]").html(obj[0].businessFlowNo);
	                	dlgFormObj.find("dd[data-name=shopFlowNo]").html(obj[0].shopFlowNo);
	                	dlgFormObj.find("dd[data-name=custName]").html(obj[0].custName);
	                	dlgFormObj.find("dd[data-name=patientCardNo]").html(obj[0].patientCardNo);
		            	}
		        	});
    		   }
    		   $("#billData").modal('show');  
    	   }
    	   if(payType=='0049'){//现金
    		   if(cashType==2){
    			var dlgObj = $("#cashData");
         		var dlgFormObj = dlgObj.find("dl[form=detail]");
			    $.ajax({
		            url:apiUrl + '/cashDetails?id=' + id,
		            data:{},
		            type:"get",
		            dataType:"json",
		            success:function(data){
	            		var obj =  data.rows;
	            		dlgFormObj.find("dd[data-name=tradeDatatime]").html(obj[0].tradeDatatime);
	                	dlgFormObj.find("dd[data-name=orgNo]").html(orgFormatter(obj[0].orgNo));
	                	dlgFormObj.find("dd[data-name=tradeCode]").html(formatter(obj[0].tradeCode));
	                	dlgFormObj.find("dd[data-name=payBusinessType]").html(formatter(obj[0].payBusinessType));
	                	dlgFormObj.find("dd[data-name=payType]").html(formatter(obj[0].payType));
	                	dlgFormObj.find("dd[data-name=patientName]").html(obj[0].patientName);
	                	dlgFormObj.find("dd[data-name=custIdentifyType]").html(formatter(obj[0].custIdentifyType));
	                	dlgFormObj.find("dd[data-name=payFlowNo]").html(obj[0].payFlowNo);
	                	dlgFormObj.find("dd[data-name=payAmount]").html(moneyFormat(obj[0].payAmount));
	                	dlgFormObj.find("dd[data-name=cashier]").html(obj[0].cashier);
	                	dlgFormObj.find("dd[data-name=paySource]").html(formatter(obj[0].paySource));
	                	dlgFormObj.find("dd[data-name=businessFlowNo]").html(obj[0].businessFlowNo);
		            	}
		        	});
    		   }
    		   $("#cashData").modal('show');  
    	   }
    	   if(payType=='0449'){//医保
    		   if(yibaoType==2){
    			var dlgObj = $("#yibaoData");
       			var dlgFormObj = dlgObj.find("dl[form=detail]");
       			$.ajax({
		            url:apiUrl + '/yibaoDetails?id=' + id,
		            data:{},
		            type:"get",
		            dataType:"json",
		            success:function(data){
	            		var obj =  data.rows;
	            		dlgFormObj.find("dd[data-name=tradeDatatime]").html(obj[0].tradeDatatime);
	                	dlgFormObj.find("dd[data-name=orgNo]").html(orgFormatter(obj[0].orgNo));
	                	dlgFormObj.find("dd[data-name=operationType]").html(formatter(obj[0].operationType));
	                	dlgFormObj.find("dd[data-name=payFlowNo]").html(obj[0].payFlowNo);
	                	dlgFormObj.find("dd[data-name=healthcareTypeCode]").html(formatter(obj[0].healthcareTypeCode));
	                	dlgFormObj.find("dd[data-name=businessCycleNo]").html(app.admin.toolsUtils.columnEllipsisFormatter(obj[0].businessCycleNo));
	                	dlgFormObj.find("dd[data-name=costAll]").html(moneyFormat(obj[0].costAll));
	                	dlgFormObj.find("dd[data-name=costBasic]").html(moneyFormat(obj[0].costBasic));
	                	dlgFormObj.find("dd[data-name=costAccount]").html(moneyFormat(obj[0].costAccount));
	                	dlgFormObj.find("dd[data-name=costCash]").html(moneyFormat(obj[0].costCash));
	                	dlgFormObj.find("dd[data-name=costWhole]").html(moneyFormat(obj[0].costWhole));
	                	dlgFormObj.find("dd[data-name=costRescue]").html(moneyFormat(obj[0].costRescue));
	                	dlgFormObj.find("dd[data-name=costSubsidy]").html(moneyFormat(obj[0].costSubsidy));
	                	dlgFormObj.find("dd[data-name=businessFlowNo]").html(obj[0].businessFlowNo);
		            	}
		        	});
    		   }
    		   $("#yibaoData").modal('show');  
    	   }
	}
	//计算渠道金额
	function collect(orgNo){
		var payType = $('#thirdtrade_payType').val();
		if(payType=="全部" || payType==null )payType="";
		var payFlowNo = $('#thirdtrade_sysSerial').val();
		var billSource = $('#thirdtrade_billSource').val();
		if(billSource=="全部" || billSource==null )billSource="";
		var businessFlowNo = $("#thirdtrade_businessFlowNo").val();
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		}
		var orderState = $('#thirdtrade_orderState').val();
		if(orderState=="全部" || orderState==null )orderState="";
		if(payType=="全部")payType="";
		var url = apiUrl+"/collect";
		$.ajax({
	           url:url,
	           type:"get",
	           data:{
	        	   startDate:startDate,
	        	   endDate:endDate,
	        	   orgNo:orgNo,
	        	   payType:payType,
	        	   payFlowNo:payFlowNo,
	        	   billSource:billSource,
	        	   shopFlowNo:businessFlowNo,
	        	   orderState:orderState
	           },
	           contentType:"application/json",
	           dataType:"json",
	           success:function(result){
	        		$("#tradeAllAmount").html(new Number(result.data.allAccount).toFixed(2));
	        	   	$("#tradeAllNum").html(result.data.wechatAllNum+result.data.aliAllNum+result.data.jdBankAllNum+result.data.thridBankAllNum+/*result.data.yibaoAllNum+*/result.data.cashAllNum);
					$("#wechatAllAmount").html(new Number(result.data.wechatAllAmount).toFixed(2));
					$("#wechatAllNum").html(result.data.wechatAllNum);
					$("#aliAllAmount").html(new Number(result.data.aliAllAmount).toFixed(2));
					$("#aliAllNum").html(result.data.aliAllNum);
					$("#jdBankAllAmount").html(new Number(result.data.jdBankAllAmount).toFixed(2));
					$("#jdBankAllNum").html(result.data.jdBankAllNum);
					$("#thridBankAllAmount").html(new Number(result.data.thridBankAllAmount).toFixed(2));
					$("#thridBankAllNum").html(result.data.thridBankAllNum);
					/*$("#yibaoAllAmount").html(new Number(result.data.yibaoAllAmount).toFixed(2));
					$("#yibaoAllNum").html(result.data.yibaoAllNum);*/
					$("#cashAllAmount").html(new Number(result.data.cashAllAmount).toFixed(2));
					$("#cashAllNum").html(result.data.cashAllNum);
	           }
	       });
	}
	
	//汇总金额
	function summary(orgNo){
		var payType = $('#thirdtrade_payType').val();
		if(payType=="全部" || payType==null )payType="";
		var payFlowNo = $('#thirdtrade_sysSerial').val();
		var billSource = $('#thirdtrade_billSource').val();
		if(billSource=="全部" || billSource==null )billSource="";
		var businessFlowNo = $("#thirdtrade_businessFlowNo").val();
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		}
		var orderState = $('#thirdtrade_orderState').val();
		if(orderState=="全部" || orderState==null )orderState="";
		if(payType=="全部")payType="";
		var url = apiUrl+"/summary";
		$.ajax({
	           url:url,
	           type:"get",
	           data:{
	        	   startDate:startDate,
	        	   endDate:endDate,
	        	   orgNo:orgNo,
	        	   payType:payType,
	        	   payFlowNo:payFlowNo,
	        	   billSource:billSource,
	        	   shopFlowNo:businessFlowNo,
	        	   orderState:orderState
	           },
	           contentType:"application/json",
	           dataType:"json",
	           success:function(result){
					$("#elecAmount").html(new Number(result.data.elecAmount).toFixed(2));
					$("#elecCount").html(result.data.elecCount);
					$("#healthcareAmount").html(new Number(result.data.healthcareAmount).toFixed(2));
					$("#healthcareCount").html(result.data.healthcareCount);
					$("#cashAmount").html(new Number(result.data.cashAmount).toFixed(2));
					$("#cashCount").html(result.data.cashCount);
	           }
	       });
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
           success:function(msg){
        	 //这个树形下拉会在和其同级位置生成一个默认隐藏的input输入框以提供表单数据
        	   thirdTradeTree = $("#thirdTradeTree").ztreeview({
	       			name: 'name',
	       			key: 'code', 
	       			//是否
	       			clearable:true,
	                   expandAll:true,
	       			data: msg
	       		}, setting);
        	   thirdTradeTree.updateCode(msg[0].id,msg[0].code);
        	   // 选择隐藏还是现实机构下拉选择
        	   var length = msg.length;
        	   if(length && length>1){
        		   $("#thirdTradeTree").parent().parent().parent().show();
        	   }else{
        		   $("#thirdTradeTree").parent().parent().parent().hide();
        	   }
	       		var startDate ;
	       		var endDate  ;
	       		var rangeTime = rangeTimeObj.val();
	       		if(rangeTime){
	       			startDate = rangeTime.split("~")[0];
	       			endDate = rangeTime.split("~")[1];
	       		}
       		   
	       		// 加载完机构树后查询汇总数据
	       		collect(thirdTradeTree.getVal);
	       		var thirdtradeSummaryDisplay = $("#thirdtradeSummaryDisplay").val();
	       		if(thirdtradeSummaryDisplay=="true") {
	       			summary(thirdTradeTree.getVal);
	       		}
	       		
	       		var queryObj  = {orgNo:thirdTradeTree.getVal,startTime:startDate,endTime:endDate};
	       		queryData(tableObj, queryObj);
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
            	$('#thirdtrade_payType').select2({
					data:msg,
					placeholder: '==请选择类型==',
					minimumResultsForSearch: -1,
				    width:'220px',
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
	
	// 账单来源
	function billSourceData(){
		$.ajax({
            url:"/admin/reconciliation/typeValue?typeValue=bill_source&isIncludeAll=true",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	
            	changeSelectData(msg);
            	$('#thirdtrade_billSource').select2({
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
		var org = $('#thirdTradeOrgNo').val();
		var orgJSON = JSON.parse(org); 
		return '<p data-toggle="tooltip" title=\"'+ orgJSON[val] +'\">' + orgJSON[val] +'</p>'
//		return orgJSON[val];
	}
	function formatter(val) {
		var typeJSON = $('#thirdTradeType').val();
		var typesJSON = JSON.parse(typeJSON); 
		if(val==null || typesJSON[val]==null || typesJSON[val]==undefined){
			console.log(val);
			return "未知";
		}
		return '<p data-toggle="tooltip" title=\"'+ typesJSON[val] +'\">' + typesJSON[val] +'</p>'
//		return typesJSON[val];
	}
	
	function init() {
	   tableObj.bootstrapTable({
			url : apiUrl,
			dataType : "json",
			uniqueId : "id",
			resizable:true,
			singleSelect : true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
		});
		billTable.bootstrapTable({
		    url : apiUrl+"/billDetails",
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
		});
	    billType=2;
	    
	    cashTable.bootstrapTable({
				url : apiUrl+"/cashDetails",
				dataType : "json",
				uniqueId : "id",
				singleSelect : true,
				pagination : true, // 是否分页
				sidePagination : 'server',// 选择服务端分页
			});
	   cashType=2;
	   
	   yibaoTable.bootstrapTable({
			    url : apiUrl+"/yibaoDetails",
				dataType : "json",
				uniqueId : "id",
				singleSelect : true,
				pagination : true, // 是否分页
				sidePagination : 'server',// 选择服务端分页
			});
	    yibaoType=2;
		
		$("#tradeAllAmount").html("0.00");
		$("#tradeAllNum").html(0);
		$("#wechatAllAmount").html("0.00");
		$("#wechatAllNum").html(0);
		$("#aliAllAmount").html("0.00");
		$("#aliAllNum").html(0);
		$("#bankAllAmount").html("0.00");
		$("#bankAllNum").html(0);
		/*$("#yibaoAllAmount").html("0.00");
		$("#yibaoAllNum").html(0);*/
		$("#cashAllAmount").html("0.00");
		$("#cashAllNum").html(0);
		$("#jdBankAllAmount").html("0.00");
		$("#jdBankAllNum").html(0);
		$("#thridBankAllAmount").html("0.00");
		$("#thridBankAllNum").html(0);
		if($("#thirdTradeIsDisplay").val()==1){
			$("#thirdTradeCount").show();
		}
		formaterOrgProps();
		tradePayTypeData();
		billSourceData();
		initDate();
	}
	function number(value, row, index) {
	 	   var pageSize=tableObj.bootstrapTable('getOptions').pageSize;
	        var pageNumber=tableObj.bootstrapTable('getOptions').pageNumber;
	        return pageSize * (pageNumber - 1) + index + 1;
	}
	function moneyFormat(val, row, index){
		return '<p data-toggle="tooltip" title=\"'+ new Number(val).toFixed(2) +'\">' + new Number(val).toFixed(2) +'</p>'
//		return new Number(val).toFixed(2);
	}
	
	/**
	 * 初始化日期
	 */
	function initDate() {
		var nowDate = new Date();
		var accountDate = $("#accountDate").val();
		var beginTime = accountDate + " 00:00:00";
		var endTime = accountDate + " 23:59:59";
		var rangeTime = beginTime + " ~ " + endTime;
		var startLayDate = laydate.render({
			elem : '#thirdtradeTime',
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
		});
	}
	
	return {
		init : init,
		search:search,
		exportData:exportData,
		formatter:formatter,
		orgFormatter:orgFormatter,
		formatOpt:formatOpt,
		details:details,
		number:number,
		moneyFormat:moneyFormat
	}
})();
 


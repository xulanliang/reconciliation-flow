NB.ns("app.admin").hisPayDedailed = (function() {	
	//表格
	var tableObj =$("#hisPayDedailedDataTable");
	//表单
	var formObj = $("#hisPayDedailedSearchForm");	
	//对话框
	var dlgObj = $('#hisPayDedailedDlg');
	var dlgFormObj = dlgObj.find("dl[form=detail]");
	// 时间控件
	var rangeTimeObj = $("#hisPayDetailTime");
	//请求路径
	var apiUrl = '/admin/hisPayDedailed/data';

	var hisPayDedailedTree;
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
		var orgNo= hisPayDedailedTree.getVal;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var payType = $('#hisPayDedailed_payType').val();
		if (payType == '全部' || payType == null) {
			payType = "";
		}
		var patType = $('#hisPayDedailed_patType').val();
		if (patType == '全部' || patType == null) {
			patType = "";
		}
		var billSource = $('#hisPayDedailed_billSource').val();
		if (billSource == '全部' || billSource == null) {
			billSource = "";
		}
		
		var payFlowNo = $('#hisPayDedailed_sysSerial').val();

		var hisOrderState = $('#hisPayDedailed_orderState').val();
		if (hisOrderState == '9999' || hisOrderState == null) {
			hisOrderState = "";
		}
		var cashier=$("#cashier").val();
		var hisCredentialsNo = $('#hisCredentialsNo').val();
		var hisOriPayFlowNo = $('#hisOriPayFlowNo').val();
		var hisInvoiceNo = $('#hisInvoiceNo').val();
		var hisPatCode = $('#hisPatCode').val();
		var payAccount = $('#payAccount').val();

		var hisMzCode=$("#hisMzCode").val();
		
		var businessFlowNo=$("#businessFlowNo").val();
		
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		}
		collect(orgNo);
		tableObj.bootstrapTable('refreshOptions', {
			  resizable: true,
			  pageNumber:1,
			  queryParams:function(params){
				    var queryObj ={orgNo:orgNo,payType:payType,patType:patType,startTime:startDate,
				    		endTime:endDate,payFlowNo:payFlowNo, hisOrderState:hisOrderState, 
				    		hisCredentialsNo:hisCredentialsNo, flowNo:hisOriPayFlowNo, 
				    		hisInvoiceNo:hisInvoiceNo, hisPatCode:hisPatCode,hisMzCode:hisMzCode,
				    		cashier:cashier,billSource:billSource,payAccount:payAccount,
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
        	   hisPayDedailedTree = $("#hisPayDedailedTree").ztreeview({
	       			name: 'name',
	       			key: 'code', 
	       			//是否
	       			clearable:true,
	                   expandAll:true,
	       			data: msg
	       		}, setting);
        	   hisPayDedailedTree.updateCode(msg[0].id,msg[0].code);
        	   // 选择隐藏还是现实机构下拉选择
        	   var length = msg.length;
        	   if(length && length>1){
        		   $("#hisPayDedailedTree").parent().parent().parent().show();
        	   }else{
        		   $("#hisPayDedailedTree").parent().parent().parent().hide();
        	   }
        	   
        	   collect(hisPayDedailedTree.getVal);
        	   var startDate ;
        	   var endDate  ;
        	   var rangeTime = rangeTimeObj.val();
        	   if(rangeTime){
        	   	startDate = rangeTime.split("~")[0];
        	   	endDate = rangeTime.split("~")[1];
        	   }
//   			   var queryObj  = {orgNo:hisPayDedailedTree.getVal,startTime:startDate,endTime:endDate};
//   			   queryData(tableObj, queryObj);
           }
       });
	}
	
	//计算渠道金额
	function collect(orgNo){
		var orgNo= hisPayDedailedTree.getVal;
		if (orgNo === '全部' || orgNo === null) {
			orgNo = "";
		}
		var payType = $('#hisPayDedailed_payType').val();
		if (payType == '全部' || payType == null) {
			payType = "";
		}
		var patType = $('#hisPayDedailed_patType').val();
		if (patType == '全部' || patType == null) {
			patType = "";
		}
		var payFlowNo = $('#hisPayDedailed_sysSerial').val();

		var hisOrderState = $('#hisPayDedailed_orderState').val();
		if (hisOrderState == '9999' || hisOrderState == null) {
			hisOrderState = "";
		}
		var cashier=$("#cashier").val();
		var hisCredentialsNo = $('#hisCredentialsNo').val();
		var hisOriPayFlowNo = $('#hisOriPayFlowNo').val();
		var hisInvoiceNo = $('#hisInvoiceNo').val();
		var hisPatCode = $('#hisPatCode').val();
		var hisMzCode=$("#hisMzCode").val();
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		}
		var billSource = $('#hisPayDedailed_billSource').val();
		if (billSource == '全部' || billSource == null) {
			billSource = "";
		}
		var businessFlowNo=$("#businessFlowNo").val();
		var url = apiUrl+"/countSum";
		$.ajax({
	           url:url,
	           type:"get",
	           data:{
	        	   orgNo:orgNo,payType:payType,startTime:startDate,endTime:endDate,payFlowNo:payFlowNo, hisOrderState:hisOrderState, 
		    		hisCredentialsNo:hisCredentialsNo, flowNo:hisOriPayFlowNo, hisInvoiceNo:hisInvoiceNo, hisPatCode:hisPatCode,
		    		hisMzCode:hisMzCode,cashier:cashier,patType:patType,billSource:billSource,businessFlowNo:businessFlowNo,
		    		hisOrderState:hisOrderState
	           },
	           contentType:"application/json",
	           dataType:"json",
	           success:function(result){
	        	   	$("#hisAllAmount").html(new Number(result.data.allAmount).toFixed(2));
	        	   	$("#hisAllNum").html(result.data.allNum);
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
            	$('#hisPayDedailed_payType').select2({
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
            	$('#hisPayDedailed_patType').select2({
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
            url:"/admin/reconciliation/typeValue?typeValue=bill_source&isIncludeAll=true",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	
            	changeSelectData(msg);
            	$('#hisPayDedailed_billSource').select2({
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
		var org = $('#hisPayDedailedOrgNo').val();
		var orgJSON = JSON.parse(org);
		return '<p data-toggle="tooltip" title=\"'+ orgJSON[val] +'\">' + orgJSON[val] +'</p>'
//		return orgJSON[val];
	}
	function formatter(val) {
		var typeJSON = $('#hisPayDedailedType').val();
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
	function payBusinessTypeFormatter(row) {
		if(row == '0051'){
			return "未知";
		} else if (row == '0151'){
			return "门诊充值";
		} else if (row == '0251'){
			return "办卡";
		} else if (row == '0351'){
			return "补卡";
		} else if (row == '0451'){
			return "挂号";
		} else if (row == '0551'){
			return "缴费";
		} else if (row == '0751'){
			return "住院充值";
		} else if (row == '0851'){
			return "预约挂号";
		}
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
	 	   	startDate = rangeTime.split("~")[0];
	 	   	endDate = rangeTime.split("~")[1];
 	   	}
 	   	
		var queryObj = {orgNo:hisPayDedailedTree.getVal,startTime:startDate,endTime:endDate};
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
		
	}
	function number(value, row, index) {
		   var pageSize=tableObj.bootstrapTable('getOptions').pageSize;
	       var pageNumber=tableObj.bootstrapTable('getOptions').pageNumber;
	       return pageSize * (pageNumber - 1) + index + 1;
	}
	
	//导出
	function exportData() {
		var orgNo= hisPayDedailedTree.getVal;
		var orgName= hisPayDedailedTree.getText;
		if (orgNo === '全部' || orgNo === null) {
			orgNo = "";
		}
		var payType = $('#hisPayDedailed_payType').val();
		if (payType == '全部' || payType == null) {
			payType = "";
		}
		var payFlowNo = $('#hisPayDedailed_sysSerial').val();

		var patType = $('#hisPayDedailed_patType').val();
		if (patType == '全部' || patType == null) {
			patType = "";
		}
		var billSource = $('#hisPayDedailed_billSource').val();
		if (billSource == '全部' || billSource == null) {
			billSource = "";
		}
		var hisOrderState = $('#hisPayDedailed_orderState').val();
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
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		}
		
		var hour = $.fn.getHour(startDate, endDate);
		if(hour > 24*31){
			$.NOTIFY.showError("错误", '时间范围超过31天，请缩短时间范围', '');
			return false;
		}
		
		bootbox.confirm('确定执行此操作?',function(r){
	        if (r){
	        	
	        	var where = '/admin/hisPayDedailed/exportData?a=1';
	    		
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
	    			where = where + '&billSource=' + billSource;
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
		return "<a href='javascript:;' onclick='app.admin.hisPayDedailed.detail("+ row.id + ")' class='btn btn-info btn-sm m-primary '> 详情 </a>  &nbsp;"
	}
	
	function detail(id){
		var row = tableObj.bootstrapTable('getRowByUniqueId',id);		
		dlgFormObj.loadDetailReset();
		dlgFormObj.loadDetail(row);
		console.log(row);
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
		var beginTime = accountDate + " 00:00:00";
		var endTime = accountDate + " 23:59:59";
		var nowDate = new Date();
		var rangeTime = beginTime + " ~ " + endTime;
		var startLayDate = laydate.render({
			elem : '#hisPayDetailTime',
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
		formatter:formatter,
		formatterPatType : formatterPatType,
		patIdFormatter:patIdFormatter,
		orgFormatter:orgFormatter,
		number:number,
		exportData:exportData,
		moneyFormat:moneyFormat,
		detail:detail,
		formatOpt:formatOpt,
		payBusinessTypeFormatter: payBusinessTypeFormatter
	}
})();
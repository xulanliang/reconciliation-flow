NB.ns("app.admin").trade = (function() {
	//表格
	var tableObj =$("#tradeDataTable");
	//表单
	var formObj = $("#tradeSearchForm");
	//请求路径
	var apiUrl = '/admin/tradeData';
	//对话框
	var dlgObj = $('#tradeDedailedDlg');
	var dlgFormObj = dlgObj.find("dl[form=detail]");
	// 时间控件
	var startobj = formObj.find("input[name=beginTime]");
	var endobj =   formObj.find("input[name=endTime]");
	var tradeTree;
    var options = { 
	            beforeSubmit:  function(formData, jqForm, options){
	            	 var flg = formObj.data('bootstrapValidator');
				     return flg.validate().isValid();
	            },
	            success: function(result){
	            	if(result.success){
	            		$.NOTIFY.showSuccess ("提醒", "操作成功");
	            		dlgObj.modal('hide');
	            		reflush();
	            	}else{
	            		if(result.message){
	            			$.NOTIFY.showError  ("错误", result.message);
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
	function orgFormatter(val) {
		var org = $('#tradeOrgNo').val();
		var orgJSON = JSON.parse(org);
		return orgJSON[val];
	}
	function formatter(val) {
		var typeJSON = $('#tradeType').val();
		var typesJSON = JSON.parse(typeJSON);
		return typesJSON[val];
	}
	
	//交易类型
	function tradeTypeData(){
		$.ajax({
            url:"/admin/reconciliation/typeValue?typeValue=Trade_Code&isIncludeAll=true",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	changeSelectData(msg);
            	$.fn.modal.Constructor.prototype.enforceFocus = function () { };
            	$('#trade_tradeType').select2({
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
	//业务类型数据
	function payBusinessTypeData(){
		$.ajax({
            url:"/admin/reconciliation/typeValue?typeValue=Pay_Business_Type&isIncludeAll=true",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	changeSelectData(msg);
            	$('#trade_businessType').select2({
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
	//支付类型数据
	function tradePayTypeData(){
		$.ajax({
            url:"/admin/reconciliation/typeValue?typeValue=Pay_Type&isIncludeAll=true",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	changeSelectData(msg);
            	$('#trade_payType').select2({
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
	//支付来源数据
	function tradePaySource(){
		$.ajax({
            url:"/admin/reconciliation/typeValue?typeValue=Pay_Source&isIncludeAll=true",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	changeSelectData(msg);
            	$('#trade_paySource').select2({
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
	
	function exportData() {
		var orgNo= tradeTree.getVal;
		var orgName= tradeTree.getText;
		var tradeCode = $('#trade_tradeType').val();
		if(tradeCode=="全部")tradeCode='';
		var payType = $('#trade_payType').val();
		if(payType=="全部")payType='';
		var deviceNo = $('#trade_payTermNo').val();
		var sysSerial = $('#trade_sysSerial').val();
		var businessType = $('#trade_businessType').val();
		var startDate = $('#trade_startDate').val();
		var endDate = $('#trade_endDate').val();   
		var dataSource = $('#trade_dataSource').val();
		var paySource = $('#trade_paySource').val();
		if(paySource=="全部")paySource='';
		var startDate='';
		var endDate='';
		if(startobj.val()!=null && startobj.val()!=''){
			startDate = startobj.val()+" 00:00:00";
		}
		if(endobj.val()!=null && endobj.val()!=''){
			endDate = endobj.val()+" 23:59:59";
		}
		if(orgNo=="全部" || orgNo==null)orgNo="";
		if(businessType=="全部")businessType="";
		//var where ='orgNo='+ orgNo +'&sysSerial='+ sysSerial +'&payTermNo='+ payTermNo+'&businessType='+ businessType+'&startDate='+ startDate+'&endDate='+endDate+'&dataSource='+dataSource+'&payType='+payType;
		bootbox.confirm('确定执行此操作?',function(r){
            if (r){
            	var where ='orgNo='+ orgNo +'&tradeCode='+ tradeCode +'&payType='+ payType+'&deviceNo='+ deviceNo+'&sysSerial='+sysSerial+'&businessType='+businessType+"&t="+new Date().getTime()+'&startDate='+ startDate+'&endDate='+endDate+'&dataSource='+
            	dataSource+'&paySource='+paySource+"&orgName="+orgName;
        		var url = apiUrl+'/api/dcExcel?' + where;
        		window.location.href=url;
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
           url:"/admin/organization",
           type:"get",
           contentType:"application/json",
           dataType:"json",
           success:function(msg){
        	 //这个树形下拉会在和其同级位置生成一个默认隐藏的input输入框以提供表单数据
        	   tradeTree = $("#tradeTree").ztreeview({
	       			name: 'name',
	       			key: 'code', 
	       			//是否
	       			clearable:true,
	                   expandAll:true,
	       			data: msg
	       		}, setting);
        	   tradeTree.updateCode(msg[0].id,msg[0].code);
        	   collect(tradeTree.getVal);
        	   var startDate=startobj.val()+" 00:00:00";
        	   var endDate=endobj.val()+" 23:59:59";
        	   var queryObj  = {orgNo:tradeTree.getVal,startDate:startDate,endDate:endDate};
			   queryData(tableObj, queryObj);
           }
       });
	}
	function resetValidator(){
		formObj.data('bootstrapValidator').destroy();
		formObj.data('bootstrapValidator',null);
		initValid();
	}
	function reflush(){
		tableObj.bootstrapTable('refresh');
	}
	function search(){
		options.url=apiUrl;
		options.type="get";
		var orgNo= tradeTree.getVal;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var tradeCode = $('#trade_tradeType').val();
		if(tradeCode=="全部")tradeCode='';
		var deviceNo = $('#trade_payTermNo').val();
		var sysSerial = $('#trade_sysSerial').val();
		var businessType = $('#trade_businessType').val();
		var payType = $('#trade_payType').val();
		if(payType=="全部")payType='';
		var dataSource = $('#trade_dataSource').val();
		var paySource = $('#trade_paySource').val();
		if(paySource=="全部")paySource='';
		var startDate='';
		var endDate='';
		if(startobj.val()!=null && startobj.val()!=''){
			startDate = startobj.val()+" 00:00:00";
		}
		if(endobj.val()!=null && endobj.val()!=''){
			endDate = endobj.val()+" 23:59:59";
		}
		if(orgNo=="全部" || orgNo==null)orgNo="";
		if(businessType=="全部")businessType="";
		collect(orgNo);
		tableObj.bootstrapTable('refreshOptions', {
			  pageNumber:1,
			  queryParams:function(params){
				    var queryObj ={orgNo:orgNo,tradeCode:tradeCode,deviceNo:deviceNo,sysSerial:sysSerial,
				    		businessType:businessType,payType:payType,startDate:startDate,endDate:endDate,dataSource:dataSource,paySource:paySource
				    };
	                var query = $.extend( true, params, queryObj);
	                return query;
	            }
		});
		
	}
	
	//计算渠道金额
	function collect(orgNo){
		var orgNo= tradeTree.getVal;
		var tradeCode = $('#trade_tradeType').val();
		if(tradeCode=="全部")tradeCode='';
		var deviceNo = $('#trade_payTermNo').val();
		var sysSerial = $('#trade_sysSerial').val();
		var businessType = $('#trade_businessType').val();
		var payType = $('#trade_payType').val();
		if(payType=="全部")payType='';
		var dataSource = $('#trade_dataSource').val();
		var paySource = $('#trade_paySource').val();
		if(paySource=="全部")paySource='';
		var startDate='';
		var endDate='';
		if(startobj.val()!=null && startobj.val()!=''){
			startDate = startobj.val()+" 00:00:00";
		}
		if(endobj.val()!=null && endobj.val()!=''){
			endDate = endobj.val()+" 23:59:59";
		}
		if(orgNo=="全部" || orgNo==null)orgNo="";
		if(businessType=="全部")businessType="";
		var url = apiUrl+"/countSum";
		$.ajax({
	           url:url,
	           type:"get",
	           data:{
	        	   orgNo:orgNo,tradeCode:tradeCode,deviceNo:deviceNo,sysSerial:sysSerial,
		    		businessType:businessType,payType:payType,startDate:startDate,endDate:endDate,dataSource:dataSource,paySource:paySource
	           },
	           contentType:"application/json",
	           dataType:"json",
	           success:function(result){
	        	   	$("#pAllAmount").html(new Number(result.data.allAmount).toFixed(2));
	        	   	$("#pAllNum").html(new Number(result.data.allNum).toFixed(2));
	           }
	       });
	}
	
	function init(accountDate) {
	   tableObj.bootstrapTable({
     			url : apiUrl,
     			dataType : "json",
     			uniqueId : "id",
     			resizable: true,
     			singleSelect : true,
     			pagination : true, // 是否分页
     			sidePagination : 'server',// 选择服务端分页
     		});
		///时间控件初始化
		startobj.datepicker({
			minView : "month",
			language : 'zh-CN',
			format : 'yyyy-mm-dd',
			autoclose : true,
			clearBtn: true,
			todayHighlight: true, 
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
			clearBtn: true,
			todayHighlight: true, 
			pickerPosition : "bottom-left"
				
		}).on('changeDate', function(ev) {
			var starttime = startobj.val();
			var endtime = endobj.val();
			startobj.datepicker('setEndDate', endtime);
			endobj.datepicker('hide');
		});
		startobj.datepicker("setDate", accountDate);
		endobj.datepicker("setDate", accountDate);
		payBusinessTypeData();
		formaterOrgProps();
		tradeTypeData();
		tradePaySource();
		tradePayTypeData();
	}
	function number(value, row, index) {
	 	   var pageSize=tableObj.bootstrapTable('getOptions').pageSize;
	        var pageNumber=tableObj.bootstrapTable('getOptions').pageNumber;
	        return pageSize * (pageNumber - 1) + index + 1;
	}
	function moneyFormat(val, row, index){
		return new Number(val).toFixed(2);
	}
	function formatOpt(index,row) {
		return "<a href='javascript:;' onclick='app.admin.trade.detail("+ row.id + ")' class='btn btn-info btn-sm m-primary '> 详情 </a>  &nbsp;"
	}
	function detail(id){
		var row = tableObj.bootstrapTable('getRowByUniqueId',id);	
		dlgFormObj.loadDetailReset();
		dlgFormObj.loadDetail(row);
		dlgFormObj.find("dd[data-name=orgNo]").html(orgFormatter(row.orgNo));
		dlgFormObj.find("dd[data-name=orderState]").html(formatter(row.orderState));
		dlgFormObj.find("dd[data-name=tradeCode]").html(formatter(row.tradeCode));
		dlgFormObj.find("dd[data-name=payBusinessType]").html(formatter(row.payBusinessType));
		dlgFormObj.find("dd[data-name=payType]").html(formatter(row.payType));
		dlgFormObj.find("dd[data-name=paySource]").html(formatter(row.paySource));
		dlgFormObj.find("dd[data-name=custIdentifyType]").html(formatter(row.custIdentifyType));
		dlgFormObj.find("dd[data-name=payAmount]").html(moneyFormat(row.payAmount));
		dlgObj.modal('show');
	}
	
	return {
		init : init,
		search:search,
		orgFormatter:orgFormatter,
		formatter:formatter,
		exportData:exportData,
		number:number,
		moneyFormat:moneyFormat,
		formatOpt:formatOpt,
		detail:detail
	}
})();
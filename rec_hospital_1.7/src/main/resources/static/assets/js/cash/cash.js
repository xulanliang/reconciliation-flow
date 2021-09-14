NB.ns("app.admin").cash = (function() {
	//表格
	var tableObj =$("#cashDataTable");
	//表单
	var formObj = $("#cashSearchForm");
	//请求路径
	var apiUrl = '/admin/cash/data';
	// 时间控件
	var startobj = formObj.find("input[name=beginTime]");
	var endobj =   formObj.find("input[name=endTime]");
	var cashTree;
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
		var org = $('#cashOrgNo').val();
		var orgJSON = JSON.parse(org);
		return orgJSON[val];
	}
	function formatter(val) {
		var typeJSON = $('#cashType').val();
		var typesJSON = JSON.parse(typeJSON);
		return typesJSON[val];
	}
	
	//业务类型数据
	function payBusinessTypeData(){
		$.ajax({
            url:"/admin/reconciliation/typeValue?typeValue=Pay_Business_Type&isIncludeAll=true",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	changeSelectData(msg);
            	$('#cash_businessType').select2({
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
	
	function exportData() {
		var orgNo= cashTree.getVal;
		//var payTermNo = $('#cash_payTermNo').val();
		var cashier=$("#cash_cashier").val();
		var sysSerial = $('#cash_sysSerial').val();
		var businessType = $('#cash_businessType').val();
		var startDate='';
		var endDate='';
		if(startobj.val()!=null && startobj.val()!=''){
			startDate = startobj.val();
		}
		if(endobj.val()!=null && endobj.val()!=''){
			endDate = endobj.val();
		}
		var payType = $('#cash_payType').val();
		var dataSource = $('#cash_dataSource').val();
		if(orgNo=="全部" || orgNo==null)orgNo="";
		if(businessType=="全部")businessType="";
		//var where ='orgNo='+ orgNo +'&sysSerial='+ sysSerial +'&payTermNo='+ payTermNo+'&businessType='+ businessType+'&startDate='+ startDate+'&endDate='+endDate+'&dataSource='+dataSource+'&payType='+payType;
		bootbox.confirm('确定执行此操作?',function(r){
            if (r){
            	var where ='orgNo='+ orgNo +'&sysSerial='+ sysSerial +'&cashier='+ cashier+'&businessType='+ businessType+'&dataSource='+dataSource+'&payType='+payType+"&t="+new Date().getTime()+'&startTime='+ startDate+'&endTime='+endDate;
        		var url = apiUrl+'/dcExcel?' + where;
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
        	   cashTree = $("#cashTree").ztreeview({
	       			name: 'name',
	       			key: 'code', 
	       			//是否
	       			clearable:true,
	                   expandAll:true,
	       			data: msg
	       		}, setting);
        	   cashTree.updateCode(msg[0].id,msg[0].code);
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
		var orgNo= cashTree.getVal;
		//var payTermNo = $('#cash_payTermNo').val();
		var cashier=$("#cash_cashier").val();
		var sysSerial = $('#cash_sysSerial').val();
		var businessType = $('#cash_businessType').val();
		var startDate='';
		var endDate='';
		if(startobj.val()!=null && startobj.val()!=''){
			startDate = startobj.val()+" 00:00:00";
		}
		if(endobj.val()!=null && endobj.val()!=''){
			endDate = endobj.val()+" 23:59:59";
		}
		var payType = $('#cash_payType').val();
		var dataSource = $('#cash_dataSource').val();
		if(orgNo=="全部" || orgNo==null)orgNo="";
		if(businessType=="全部")businessType="";
		tableObj.bootstrapTable('refreshOptions', {
			  pageNumber:1,
			  queryParams:function(params){
				    var queryObj ={orgNo:orgNo,cashier:cashier,sysSerial:sysSerial,businessType:businessType,
				    		startDate:startDate,endDate:endDate,payType:payType,dataSource:dataSource
				    };
	                var query = $.extend( true, params, queryObj);
	                return query;
	            }
		});
		
	}
	
	function init() {
		tableObj.bootstrapTable({
			url : apiUrl,
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
		});
		payBusinessTypeData();
		formaterOrgProps();
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
	}
	
	function number(value, row, index) {
	 	   var pageSize=tableObj.bootstrapTable('getOptions').pageSize;
	        var pageNumber=tableObj.bootstrapTable('getOptions').pageNumber;
	        return pageSize * (pageNumber - 1) + index + 1;
	}
	function moneyFormat(val, row, index){
		return new Number(val).toFixed(2);
	}
	
	return {
		init : init,
		search:search,
		orgFormatter:orgFormatter,
		formatter:formatter,
		exportData:exportData,
		number:number,
		moneyFormat:moneyFormat
	}
})();
 


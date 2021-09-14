NB.ns("app.admin").blendRefund = (function() {	
	//表格
	var tableObj =$("#blendRefundDataTable");
	//表单
	var formObj = $("#blendRefundSearchForm");	
	//对话框
	var dlgObj = $('#blendRefundDlg');
	var dlgFormObj = dlgObj.find("dl[form=detail]");
	// 时间控件
	var rangeTimeObj = $("#blendRefundTime");
	//请求路径
	var apiUrl = '/admin/blendRefund/data';

	var blendRefundTree;
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
	function search(){
		var orgNo= blendRefundTree.getVal;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var payBusinessType = $('#payBussinessType').val();
		if (payBusinessType == '全部' || payBusinessType == null) {
			payBusinessType = "";
		}
		var cashier=$("#cashier").val();
		var refundOrderNo=$("#refundOrderNo").val();
		
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		}
		
		tableObj.bootstrapTable('refreshOptions', {
			  resizable: true,
			  pageNumber:1,
			  queryParams:function(params){
				    var queryObj ={orgNo:orgNo,startTime:startDate,endTime:endDate,refundOrderNo:refundOrderNo, payBusinessType:payBusinessType,cashier:cashier};
	                var query = $.extend( true, params, queryObj);
	                return query;
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
            	$('#payBussinessType').select2({
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
        	   blendRefundTree = $("#blendRefundTree").ztreeview({
	       			name: 'name',
	       			key: 'code', 
	       			//是否
	       			clearable:true,
	                   expandAll:true,
	       			data: msg
	       		}, setting);
        	   blendRefundTree.updateCode(msg[0].id,msg[0].code);
        	   // 选择隐藏还是现实机构下拉选择
				var length = msg.length;
				if(length && length>1){
					$("#blendRefundTree").parent().parent().parent().show();
				}else{
					$("#blendRefundTree").parent().parent().parent().hide();
				}
        	   search();
           }
       });
	}
	
	
	function orgFormatter(val) {
		var org = $('#blendRefundOrgNo').val();
		var orgJSON = JSON.parse(org);
		return '<p data-toggle="tooltip" title=\"'+ orgJSON[val] +'\">' + orgJSON[val] +'</p>';
	}
	function formatter(val) {
		var typeJSON = $('#blendRefundType').val();
		var typesJSON = JSON.parse(typeJSON);
		return typesJSON[val];
	}
	
	function formatterPatType(val) {
		if(val == "mz"){
			return "门诊";
		}else if(val == "zy") {
			return "住院";
		}
	}
	
	function init(accountDate) {
		//初始化表格
		tableObj.bootstrapTable({
   			url : apiUrl,
   			dataType : "json",
   			uniqueId : "id",
   			resizable: true,
   			singleSelect : true,
   			pagination : true, // 是否分页
   			sidePagination : 'server',// 选择服务端分页
   		});
		formaterOrgProps();
		payBusinessTypeData();
		initDate(accountDate);
		
	}
	function moneyFormat(val, row, index){
		return new Number(val).toFixed(2);
	}
	
	function formatOpt(index,row) {
		return "<a href='javascript:;' onclick='app.admin.blendRefund.detail(\""+ row.refundOrderNo + "\")' class='btn btn-info btn-sm m-primary '> 详情 </a>  &nbsp;"
	}
	
	function detail(refundOrderNo){
		$("#blendPayOrderTable").bootstrapTable('destroy');
		$("#refundRecordTable").bootstrapTable('destroy');
		$.ajax({
	           url:apiUrl+"/detail",
	           type:"get",
	           data:{refundOrderNo:refundOrderNo},
	           contentType:"application/json",
	           dataType:"json",
	           success:function(msg){
	        	 if(msg.success){
	        		 $("#blendPayOrderTable").bootstrapTable({
        	   			url : apiUrl,
        	   			dataType : "json",
        	   			uniqueId : "id",
        	   			resizable: true,
        	   			singleSelect : true,
        	   			data:msg.data[0]
        	   		});
	        		 $("#refundRecordTable").bootstrapTable({
        	   			url : apiUrl,
        	   			dataType : "json",
        	   			uniqueId : "id",
        	   			resizable: true,
        	   			singleSelect : true,
        	   			data:msg.data[1]
        	   		});
	        	 }
	           }
	       });
		dlgObj.modal('show');
	}
	
	function refundStrategy(val, row, index){
		if(val=='01'){
			return '先进先出';
		}
		return '先大后小';
	}
	
	function refundState(val, row, index){
		if(val=='1'){
			return '成功';
		}
		return '失败';
	}
	
	//重新申请
	function refundFormat(index,row){
		if(row.refundState!='1'){
			return "<a href='javascript:;' onclick='app.admin.blendRefund.retryApply("+row.refundCount+",\""+ row.refundOrderNo + "\","+row.id+")' class='btn btn-info btn-sm m-primary '> 重新退款 </a>  &nbsp;"
		}
	}
	//重新申请
	function retryApply(refundCount,refundOrderNo,id){
		$.ajax({
	           url:apiUrl+"/retryApply",
	           type:"get",
	           data:{refundOrderNo:refundOrderNo,id:id},
	           contentType:"application/json",
	           dataType:"json",
	           success:function(msg){
	        	 if(msg.success){
	        		 $.NOTIFY.showSuccess ("提醒", "退费成功",'');
	        		 /*$("#refundRecordTable").bootstrapTable('updateByUniqueId', {
	 	    		    id: id,
	 	    		    row: {
	 	    		    	refundState:"成功",
	 	    		    	refundCount:refundCount+1,
	 	    		    	refundStateInfo:""
	 	    		    }
	     		     });*/
	        	 }else{
	        		 $.NOTIFY.showError  ("错误", msg.message,'');
	        		 /*$("#refundRecordTable").bootstrapTable('updateByUniqueId', {
		 	    		    id: id,
		 	    		    row: {
		 	    		    	refundState:"成功",
		 	    		    	refundCount:refundCount+1,
		 	    		    	refundStateInfo:msg.message
		 	    		    }
		     		 });*/
	        	 }
	     		 $("#refundRecordTable").bootstrapTable('destroy');
	     		 $.ajax({
	     	           url:apiUrl+"/detail",
	     	           type:"get",
	     	           data:{refundOrderNo:refundOrderNo},
	     	           contentType:"application/json",
	     	           dataType:"json",
	     	           success:function(msg){
	     	        	 if(msg.success){
	     	        		 $("#refundRecordTable").bootstrapTable({
	             	   			url : apiUrl,
	             	   			dataType : "json",
	             	   			uniqueId : "id",
	             	   			resizable: true,
	             	   			singleSelect : true,
	             	   			data:msg.data[1]
	             	   		});
	     	        	 }
	     	           }
	     	       });
	           }
	       });
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
			elem : '#blendRefundTime',
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
		orgFormatter:orgFormatter,
		moneyFormat:moneyFormat,
		refundStrategy:refundStrategy,
		refundState:refundState,
		detail:detail,
		formatOpt:formatOpt,
		refundFormat:refundFormat,
		retryApply:retryApply
	}
})();
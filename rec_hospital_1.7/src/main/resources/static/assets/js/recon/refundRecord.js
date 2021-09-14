NB.ns("app.admin").onlinerefund = (function() {
	// 表格
	var tableObj = $("#onlinerefundDataTable"); 
	// 请求路径
	var apiUrl = '/admin/onlineRefund/data';
	// 表单
	var formObj = $("#onlinerefundSearchForm"); 
	// 时间控件
	var rangeTimeObj = $("#refundRecordTime");
	// 下拉框对象
	var treeInput = formObj.find("input[name=orgNoTree]");
	var treeUrl = '/admin/organization/data';
	var ztreeObj;
	var businessType = formObj.find("select[name=businessType]");
	var state =formObj.find("select[name=state]");
	var typesJSON ;
	var orgJSON; 
	var statusJSON; 
		
	function init(typesJSON_tmp,orgJSON_tmp,statusJSON_temp,accountDate) { 
		
		//初始化赋值
		typesJSON = typesJSON_tmp;
		orgJSON = orgJSON_tmp;
		statusJSON = statusJSON_temp; 
		 
		//初始化控件
		initDate(accountDate);  
		initTable();  
		initTree();
		tradePayTypeData();
		
		// 从其他页面带过来的参数
		if ($("#initState").val()) {
			state.val($("#initState").val());
		}
	}
	
	//初始化表格
	function initTable(){ 
		tableObj.bootstrapTable({
			url:apiUrl,
			dataType : "json",
			uniqueId : "id",
			resizable: true,
			singleSelect : true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
		});
	}
	
	/**
	 * 初始化日期
	 */
	function initDate(accountDate) {
		
		var startDate = $("#startDate").val();
		if(!startDate){
			startDate = accountDate;
		}
		var nowDate = new Date();
		var rangeTime = startDate + " ~ " + accountDate;
		if(allDate){
			rangeTime='';
		}
		var startLayDate = laydate.render({
			elem : '#refundRecordTime',
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
	
	///初始化下拉框
	function initTree() {
		var setting = {
			view : {
				dblClickExpand : false,
				showLine : false,
				selectedMulti : false,
				fontCss : {
					fontSize : '30px'
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
		
		//初始化组织结构
		$.ajax({
			url : treeUrl,
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) {
				
				//搜索下拉框
				if(ztreeObj){
            		ztreeObj.refresh(data);
            	}else{
            		//这个树形下拉会在和其同级位置生成一个默认隐藏的input输入框以提供表单数据
            		ztreeObj = treeInput.ztreeview({
            			name: 'name',
            			key: 'code', 
            			//是否
            			clearable:true,
                        expandAll:true,
            			data: data
            		}, setting);
            		if($("#initOrgNo").val()!=null&&$("#initOrgNo").val()!=''){
    					//获取用户机构
    					$.ajax({
    			            url:"/admin/deviceInfo/"+data[0].id+"/org",
    			            data:{code:$("#initOrgNo").val()},
    			            type:"get",
    			            success:function(data){
    			            	ztreeObj.updateCode(data,$("#initOrgNo").val());
    			            	search();
    			            }
    			        });
    					return;
    				}else{
    					ztreeObj.updateCode(data[0].id,data[0].code);
    				}
            	}
				// 选择隐藏还是现实机构下拉选择
				var length = data.length;
				if(length && length>1){
					treeInput.parent().parent().parent().show();
				}else{
					treeInput.parent().parent().parent().hide();
				}
				search();
			}
		});
	}
	function formatterCheck(val, row, index){
		var name = '<a href="javascript:" plain="true" onclick="app.admin.onlinerefund.check(\''+row.id +'\',\''+row.tradeCode +'\',\''+row.orgNo +'\',\''+row.payShopNo +'\',\''+row.tradeFrom +'\',\''+row.payType +'\',\''+row.payFlowNo +'\',\''+row.thirdAmount +'\',\''+row.oriPayFlowNo +'\',\''+row.deviceNo +'\')">' + "退费" + "</a>";
		return name;
	}
	
	function orgFormatter(val) {
		var orgJSONs = $.parseJSON(orgJSON);
//		return orgJSONs[val];
		return '<p data-toggle="tooltip" title=\"'+ orgJSONs[val] +'\">' + orgJSONs[val] +'</p>'
	}
	function formatter(val) {
		var typesJSONs = $.parseJSON(typesJSON);
//		return typesJSONs[val]; 
		return '<p data-toggle="tooltip" title=\"'+ typesJSONs[val] +'\">' + typesJSONs[val] +'</p>'
	}
	
	function search(th) { 
		//参数
		var orgNo= ztreeObj.getVal;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var businessTypeValue = businessType.val();
		if(businessTypeValue=="全部" || businessTypeValue==null )businessTypeValue="";
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		}
		var paymentRequestFlow = formObj.find("input[name=paymentRequestFlow]").val();
		var stateVal=state.val();
		var queryObj = {orgNo:orgNo,businessType:businessTypeValue,startTime:startDate,endTime:endDate,paymentRequestFlow:paymentRequestFlow,state:stateVal};
		
		//刷新表格
		tableObj.bootstrapTable('refreshOptions', {
			  pageNumber:1,
			  url:apiUrl,
			  queryParams:function(params){
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
	//支付类型数据
	function tradePayTypeData(){
		$.ajax({
            url:"/admin/reconciliation/typeValue?typeValue=Pay_Type&isIncludeAll=true",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	changeSelectData(msg);
            	businessType.select2({
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
	
	//导出
	function exportData() {
		var orgNo= ztreeObj.getVal;
		var orgName= ztreeObj.getText;
		var businessTypeValue = businessType.val();
		if(businessTypeValue=="全部" || businessTypeValue==null )businessTypeValue="";
		var paymentRequestFlow = formObj.find("input[name=paymentRequestFlow]").val();
		
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		} else {
			$.NOTIFY.showError("提醒", "请选择时间", '');
			return ;
		}

		var hour = $.fn.getHour(startDate, endDate);
		if(hour > 24*31){
			$.NOTIFY.showError("错误", '时间范围超过31天，请缩短时间范围', '');
			return false;
		}
		var stateVal=state.val();
		bootbox.confirm('确定执行此操作?',function(r){
            if (r){
            	where ='orgNo='+ orgNo +'&businessType='+ businessTypeValue +'&startTime='+ startDate+'&endTime='+endDate+"&paymentRequestFlow="+paymentRequestFlow+"&orgName="+orgName+"&state="+stateVal;
        		url = apiUrl+'/dcExcel?' + where;
                window.location.href=url;
            }
        });
	}
	
	function formatHandler(val, row, index){
		var name =null;
		if(row.state==1){//判断状态,//待审核 
			//财务主任
			if($("#roleType").val()==1||$("#roleType").val()=='admin'){
				name = "<a href='javascript:;' onclick='app.admin.onlinerefund.rejectOrExamine(" + row.id + ",\"" + row.tradeAmount + "\",1)' class='btn btn-info btn-sm m-primary '> 审核 </a>";
				name=name+"&nbsp;<a href='javascript:;' onclick='app.admin.onlinerefund.rejectOrExamine(" + row.id + ",\"" + row.tradeAmount + "\",2)' class='btn btn-info btn-sm m-primary '> 驳回 </a>";
				name=name+"&nbsp;<a href='javascript:;' onclick='app.admin.onlinerefund.details(" + row.id + ")' class='btn btn-info btn-sm m-primary '> 查看 </a>";
			}else{//收费员
				name = "<a href='javascript:;' disabled='true' class='btn btn-info btn-sm m-primary '> 审核 </a>";
				name=name+"&nbsp;<a href='javascript:;' disabled='true' class='btn btn-info btn-sm m-primary '> 驳回 </a>";
				name=name+"&nbsp;<a href='javascript:;' onclick='app.admin.onlinerefund.details(" + row.id + ")' class='btn btn-info btn-sm m-primary '> 查看 </a>";
			}
		}else if(row.state==2){//驳回
			//财务主任
			if($("#roleType").val()==1){
				
//				name = "<a href='javascript:;' onclick='app.admin.onlinerefund.rejectOrExamine(" + row.id + ",\"" + row.tradeAmount + "\",3)' class='btn btn-info btn-sm m-primary '> 退费 </a>";
				name = "<a href='javascript:;' disabled='true' class='btn btn-info btn-sm m-primary '> 退费 </a>";
				name=name+ "&nbsp;<a href='javascript:;' onclick='app.admin.onlinerefund.deleteRecord(" + row.id + ")' class='btn btn-info btn-sm m-primary '> 删除 </a>";
				name=name+"&nbsp;<a href='javascript:;' onclick='app.admin.onlinerefund.details(" + row.id + ")' class='btn btn-info btn-sm m-primary '> 查看 </a>";
			}else if($("#roleType").val()=='admin'){
//				name = "<a href='javascript:;' onclick='app.admin.onlinerefund.rejectOrExamine(" + row.id + ",\"" + row.tradeAmount + "\",3)' class='btn btn-info btn-sm m-primary '> 退费 </a>";
				name = "<a href='javascript:;' disabled='true' class='btn btn-info btn-sm m-primary '> 退费 </a>";
				name =name+ "&nbsp;<a href='javascript:;' onclick='app.admin.onlinerefund.deleteRecord(" + row.id + ")' class='btn btn-info btn-sm m-primary '> 删除 </a>";
				name=name+"&nbsp;<a href='javascript:;' onclick='app.admin.onlinerefund.details(" + row.id + ")' class='btn btn-info btn-sm m-primary '> 查看 </a>";
			}else{//收费员
//				name = "<a href='javascript:;' onclick='app.admin.onlinerefund.rejectOrExamine(" + row.id + ",\"" + row.tradeAmount + "\",3)' class='btn btn-info btn-sm m-primary '> 退费 </a>";
				name = "<a href='javascript:;' disabled='true' class='btn btn-info btn-sm m-primary '> 退费 </a>";
				name=name+"&nbsp;<a href='javascript:;' onclick='app.admin.onlinerefund.deleteRecord(" + row.id + ")' class='btn btn-info btn-sm m-primary '> 删除 </a>";
				name=name+"&nbsp;<a href='javascript:;' onclick='app.admin.onlinerefund.details(" + row.id + ")' class='btn btn-info btn-sm m-primary '> 查看 </a>";
			}
		}else if(row.state==3){//退费
			name = "<a href='javascript:;' disabled='true' class='btn btn-info btn-sm m-primary '> 退费 </a>";
			name=name+"&nbsp;<a href='javascript:;' disabled='true' class='btn btn-info btn-sm m-primary '> 删除 </a>";
			name=name+"&nbsp;<a href='javascript:;' onclick='app.admin.onlinerefund.details(" + row.id + ")' class='btn btn-info btn-sm m-primary '>查看</a>";
		}
		return name;
	}
	//审核以及驳回页面
	function rejectOrExamine(id,tradeAmount,type){
		$("#tAmount").html(new Number(tradeAmount).toFixed(2));
		$("#id").val(id);
		$("#state").val(type);
		$("#reason").val("");
		$("#file").val("");
		if(type==1){//审核
			$("#tName").html("审核");
			$("#tReason").html("审核原因:");
		}else if(type==2){//驳回
			$("#tName").html("驳回");
			$("#tReason").html("驳回原因:");
		}else{
			$("#tName").html("申请");
			$("#tReason").html("申请原因:");
		}
		$("#rejectOrExamine").modal("show");
	}
	
	//审核以及驳回调用
	function updateData(){
		var form = new FormData();
		var reason=$("#reason").val();
		form.append('id', $("#id").val());
		form.append('state', $("#state").val());
		if(reason == null || reason == ''){
			$.NOTIFY.showError("错误", '原因不能为空', '');
			return false;
		}
		form.append('handleRemark', reason);
		var maxsize = 2*1024*1024;//2M 
		var file=$("#file")[0].files[0];
		var fileTypes = new Array("bmp","png","jpeg","jpg","gif");  //定义可支持的文件类型数组
		if(file!=null&&file.size>0){
			var newFileName = file.type.split('image/');
			if($.inArray(newFileName[1],fileTypes)==-1){
				$.NOTIFY.showError("错误", '图片格式不正确', '');
				return false;
			}
			if(file.size>maxsize){
				$.NOTIFY.showError("错误", '图片不能超过'+maxsize/(1024*1024)+"m", '');
				return false;
			}
		}
		form.append('file', file);
		$("#refundRecordButton").button('loading');
		$.ajax({
	         url:apiUrl,
	         type:"post",
	         data:form,
	         processData:false,
	         contentType:false,
	         dataType:"json",
	         success:function(data){
	        	if(!data.success){
	        		$.NOTIFY.showError("错误", data.message, '');
	        	}else{
	        		$.NOTIFY.showSuccess("提醒", "操作成功", '');
	        		search();
	        		$("#rejectOrExamine").modal("hide");
	        	}
	        	$("#refundRecordButton").button('reset');
	         }
	     });      
	}
	
	function deleteRecord(id){
		bootbox.confirm('确定执行删除操作?',function(r){
            if (r){
            	$.ajax({
        			type: "post",
        			url: apiUrl+"/delete",
        			data:{id:id},
        			dataType: "json",
        			success: function(result) {
        				if(!result.success){
        					$.NOTIFY.showError("错误", result.message, '');
        				}else{
        					search();
        					$.NOTIFY.showSuccess("提醒", "操作成功", '');
        				}
        			}
        		});
            }
        });
	}
	function formatState(val, row, index){
		var name =null;
		if(val==1){
			name="<span style='color: #49bbfc;'>待审核</span>";
		}else if(val==2){
			name="<span style='color: #ec639f;'>已驳回</span>";
		}else if(val==3){
			name="<span style='color: #26d9b5;'>已退费</span>";
		}
		return name;
//		return '<p data-toggle="tooltip" title=\"'+ name +'\">' + name +'</p>'
	}
	
	function details(id){
		$.ajax({
			type: "get",
			url: apiUrl+"/details",
			data:{id:id},
			dataType: "json",
			success: function(result) {
				if(!result.success){
					$.NOTIFY.showError("错误", result.message, '');
				}else{
					var list=result.data;
					var boby="<div class='modal-body'>";
					var last="</div>";
					var hr="<hr style='margin-top: 0px;margin-bottom: 0px;'>";
					var html="";
					var payType="";
					var typesJSONs = $.parseJSON(typesJSON);
					var detailsTime="";
					var userName="";
					var flowNo="";
					var amount="";
					var explain="";
					$("#detailsDiv").html("");
					for(var i=0;i<list.length;i++){
						var url="";
						if(list[i].imgUrl!=null&&list[i].imgUrl!=''){
							url="/admin/nextDayAccount/data/readImage?adress=" + list[i].imgUrl;
							url="<a href='"+url+"'style='width: 20%;float: left;margin-left: 7px;' target='_Blank' class='btn btn-info btn-sm m-primary '>图片附件</a>"
						}
						if(list[i].state==1)$("#detailsTitle").html("待审核");
						if(list[i].state==2)$("#detailsTitle").html("驳回");
						if(list[i].state==3)$("#detailsTitle").html("审核完成");
						if(list[i].state==1||i==0){
							detailsTime="申请时间:";
							userName="申请人:";
							amount="申请退款金额:"
							flowNo="支付流水号:";
							explain="申请说明:";
						}
						if(list[i].state==2&&i!=0){
							detailsTime="驳回时间:";
							userName="驳回人:";
							amount="申请退款金额:"
							flowNo="支付流水号:";
							explain="驳回说明:";
						}
						if((list[i].state==3&&i!=0)||(list[i].state==3&&list.length==1)){
							detailsTime="退款时间:";
							userName="审核人:";
							amount="退款金额:"
							flowNo="退款流水号:";
							explain="审核说明:";
						}
						//时间
						html=html+"<div style='width: 100%;display: inline-block;'><div><label class='control-label' style='text-align: right;width:auto;float: left;'>"+detailsTime+"</label><label class='control-label' style='text-align: left;width:20%;float: left;font-weight:normal;margin-left: 7px;'>"+list[i].handleDateTime+"</label>";
						//流水号  
						html=html+"<label class='control-label' style='text-align: right;width:13%;float: left;'>"+userName+"</label><label class='control-label' style='text-align: left;width:16%;float: left;font-weight:normal;margin-left: 7px;'>"+list[i].userName+"</label>";
						//退款金额
						html=html+"<label class='control-label' style='text-align: right;width:15%;float: left;'>"+amount+"</label><label class='control-label' style='text-align: left;width:10%;float: left;font-weight:normal;margin-left: 7px;'>"+new Number(list[i].tradeAmount).toFixed(2)+"元</label></div>";
						//交易时间
						html = html + "<div style='width: 100%;display: inline-block;float: left;'><label class='control-label' style='text-align: right;width:auto;float: left;'>交易时间:" + "</label><label class='control-label' style='text-align: left;width:20%;float: left;font-weight:normal;margin-left: 7px;'>" + list[i].tradeTime + "</label>";
						// 支付类型
						html=html+"<label class='control-label' style='text-align: right;width:13%;float: left;'>支付类型:"+"</label><label class='control-label' style='text-align: left;width:20%;float: left;font-weight:normal;margin-left: 7px;'>"+typesJSONs[list[i].payName]+"</label>";
						// 患者姓名
						html = html + "<label class='control-label' style='text-align: right;width:11%;float: left;'>患者姓名:" + "</label><label class='control-label' style='text-align: left;width:20%;float: left;font-weight:normal;margin-left: 7px;'>" + (list[i].patientName||'') + "</label></div>";
						// 就诊卡号
						html = html + "<div style='width: 100%;display: inline-block;float: left;'><label class='control-label' style='text-align: right;width:auto;float: left;'>就诊卡号:" + "</label><label class='control-label' style='text-align: left;width:20%;float: left;font-weight:normal;margin-left: 7px;'>" + (list[i].patientNo||'') + "</label>";
						//申请人
						html=html+"<label class='control-label' style='text-align: right;width:13%;float: left;'>"+flowNo+"</label><label class='control-label' style='text-align: left;width:20%;float: left;font-weight:normal;margin-left: 7px;'>"+list[i].paymentRequestFlow+"</label></div>";
						html=html+"<div style='width: 100%;display: inline-block;float: left;'><label class='control-label' style='text-align: right;width:auto;float: left;'>退款类型:</label><label class='control-label' style='text-align: left;width:20%;float: left;font-weight:normal;margin-left: 7px;'>"+(list[i].refundType&&list[i].refundType==2?'线下现金退回':'原路退回')+"</label></div>";
						//说明
						html=html+"<div style='width: 100%;display: inline-block;'>" +
								"<label class='control-label' style='width: auto;text-align: right;float: left;'>"+explain+"</label>" +
								url+
								"</div>";
						html=html+"<div style='width: 100%;display: inline-block;'><textarea disabled='disabled' class='form-control' style='width: 100%;height: 100px;'>"+list[i].handleRemark+"</textarea></div>"
						$("#detailsDiv").append(boby+html+last+hr);
						html="";
						detailsTime="";
						userName="";
						flowNo="";
						amount="";
						explain="";
					}
					$("#refundDetails").modal('show');
				}
			}
		});
		
	}
	
	function check(id,tradeCode,orgNo,payShopNo,paySource,payType,payFlowNo,thirdAmount,oriPayFlowNo,deviceNo){
		var url = '/admin/onlinerefund/plat/refund?Trade_Code='+tradeCode+"&Org_No="+orgNo+"&Pay_Source="+paySource+"&Pay_Type="+payType+"&Pay_Flow_No="+payFlowNo+"&Third_Amount="+thirdAmount+"&Pay_Round="+thirdAmount+"&Ori_Pay_Flow_No="+oriPayFlowNo+"&Device_No="+deviceNo+"&Pay_Shop_No="+payShopNo+"&id="+id;
		$.ajax({
			type: "GET",
			url: url,
			timeout:5000,
			dataType: "json",
			error: function() {
				$.NOTIFY.showError("错误", result.message, '');
			},
			success: function(result) {
				if(result.code==1){
					$.NOTIFY.showError("错误", result.message, '');
				}else{
					$.NOTIFY.showSuccess("提醒", "操作成功", '');
				}
				
				search();
			}
		});
	}
	function SerialNumberFormat(value, row, index){
		return index+1;
	}
	function moneyFormat(val, row, index){
//		return new Number(val).toFixed(2);
		return '<p data-toggle="tooltip" title=\"'+ new Number(val).toFixed(2) +'\">' + new Number(val).toFixed(2) +'</p>'
	}

	return {
		orgFormatter:orgFormatter,
		formatter:formatter,
		init : init,
		search:search ,
		check :check ,
		formatterCheck:formatterCheck,
		SerialNumberFormat:SerialNumberFormat,
		formatHandler:formatHandler,
		details:details,
		exportData:exportData,
		moneyFormat:moneyFormat,
		formatState:formatState,
		rejectOrExamine:rejectOrExamine,
		updateData:updateData,
		deleteRecord:deleteRecord
	}
})();

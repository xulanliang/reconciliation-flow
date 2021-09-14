NB.ns("app.admin").tradeDetail = (function() {
	//表格
	var tableObj =$("#tradeDataTable");
	//表单
	var formObj = $("#tradeSearchForm");
	//请求路径
	var apiUrl = '/admin/tradeDetailData';
	var payCode = formObj.find("select[name=tradePayCode]");
	//对话框
	var dlgObj = $('#tradeDedailedDlg');
	var refundDlgObj = $('#tradeRefundDlg');
	var refundFormObjDlg = $('#refundForm');
	var dlgFormObj = dlgObj.find("ul.detail");
	// 时间控件
	var rangeTimeObj = formObj.find("#tradeDetailStartTime");
	var tradeTree;
	var orderStateVal;
	var systemFrom;

	var billSourceExt = "";

	//初始化
	function init(accountDate,date,orgCode,orderState) {
		payBusinessTypeData();
		tradePayTypeData();
		tradeDetailPayTypeData();
		formaterOrgProps(orgCode);
		$("#TradeDetailDiv .tab-nav").on("tabChange",function (event) {
			//在这些做你想做的任何事
			var name = $(event.item).attr("name");
			$('#selectType').val(name);
			//显示列表的系统来源列
			if(name=='all'){
				tableObj.bootstrapTable("showColumn","systemFrom")
			}else{
				tableObj.bootstrapTable("hideColumn","systemFrom")
			}
			showTradeCondition(name);
		});
//		tradePaySource();
		orderStateData(orderState);
		if($("#tradeDetailIsDisplay").val()==1){
			$("#tradeDetailCount").show();
		}
		// 初始化日期
		if(!date){
			date = accountDate;
		}
		initDate(date, accountDate);
		if((date != "" && orgCode != "" && orderState != "") &&
				(date != null && orgCode != null && orderState != null)){
			var startDate='';
			var endDate='';
			orderStateVal = orderState;
			if(date!=null && date!=''){
				startDate = date + " 00:00:00";
			}
			if(date!=null && date!=''){
				endDate =  date + " 23:59:59";
			}
			accountDate = date
			tableObj.bootstrapTable({
	 			url : apiUrl,
	 			onPostBody:choseColor,
	 			dataType : "json",
	 			uniqueId : "id",
	 			resizable: true,
	 			singleSelect : true,
	 			pagination : true, // 是否分页
	 			sidePagination : 'server',// 选择服务端分页
	 			queryParams : function(params) {
					var queryObj = {
							orgCode:orgCode,
							orderState:orderState
					};
					var query = $.extend(true, params, queryObj);
					return query;
				}
	 		});
		}else{
			orderStateVal = '';
			tableObj.bootstrapTable({
	 			url : "",
	 			dataType : "json",
	 			uniqueId : "id",
	 			onPostBody:choseColor,
	 			resizable: true,
	 			singleSelect : true,
	 			pagination : true, // 是否分页
	 			sidePagination : 'server',// 选择服务端分页
	 		});
		}
		
	}
	//支付类型数据
	function tradeDetailPayTypeData(){
		$.ajax({
            url:"/admin/reconciliation/typeValue?typeValue=Pay_Type&isIncludeAll=true",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	changeSelectData(msg);
            	$("#tradePayCode").select2({
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
		var org = $('#tradeOrgNo').val();
		var orgJSON = JSON.parse(org);
		return '<p data-toggle="tooltip" title=\"'+ orgJSON[val]+'\">' + orgJSON[val] +'</p>'
	}
	function formatter(val) {
		var typeJSON = $('#tradeType').val();
		var typesJSON = JSON.parse(typeJSON);
		return typesJSON[val];
	}
	//不同系统选项卡数据
	function selectTypeData(){
		$.ajax({
            url:"/admin/reconciliation/typeValue?typeValue=bill_source&isIncludeAll=true",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	var buttonHtml = "";
            	TabNav.destroy();
            	for(var i=1,l=msg.length;i<l;i++){
            		var name = msg[i]["name"];
            		var value = msg[i]["value"];
            		var button = "<li class=\"btn btn-default tab-item\"  name=\""+value+"\" >"+name+"</li>";
            		buttonHtml += button;
            	}
            	$('#followTable').html("<li  class=\"btn btn-default tab-item\" name=\"all\" >全部</li>" +
            			"" + buttonHtml);
            	TabNav.init();
            }
        });
	}
	
	//Tab分类
	function showTradeCondition(dataSourceType, e) {
		var orgNo= tradeTree.getVal;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var payBussinessType = $('#payBussinessType').val();
		var deviceNo = $('#deviceNo').val();
		var paySystemNo = $('#paySystemNo').val();
		var hisOrderNO = $('#hisOrderNO').val();
		var orderState = $('#orderState').val();
		var visitNumber = $('#visitNumber').val();
		var custName = $('#custName').val();
		var patType = $('#patType').val();
		var payType = $('#tradePayCode').val();
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		}
		if(orgNo=="全部" || orgNo==null)orgNo="";
		if(payBussinessType=="全部" || payBussinessType==null)payBussinessType="";
		if(patType=="全部" || patType==null)patType="";
		if(payType=="全部" || payType==null)payType="";
		if(deviceNo=="全部" || deviceNo==null)deviceNo="";
		if(orderState=="全部" || orderState==null)orderState="";
		if((orderStateVal != null || orderStateVal !="") && orderState == "")orderState=orderStateVal
		systemFrom = dataSourceType;
		if(systemFrom=="all" || systemFrom==null)systemFrom="";
		collect(orgNo);
		tableObj.bootstrapTable('refreshOptions', {
			url : apiUrl + "/tradeCondition",
			pageNumber : 1,
			queryParams : function(params) {
				var queryObj = {
						orgCode:orgNo,startDate:startDate,endDate:endDate,
			    		payBussinessType:payBussinessType,deviceNo:deviceNo,
			    		paySystemNo:paySystemNo,hisOrderNO:hisOrderNO,orderState:orderState,
			    		visitNumber:visitNumber,custName:custName,patType:patType,
			    		systemFrom:systemFrom,payType:payType
				};
				var query = $.extend(true, params, queryObj);
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
	//患者类型数据
	function tradePayTypeData(){
		$.ajax({
            url:"/admin/reconciliation/typeValue?typeValue=pat_code&isIncludeAll=true",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	changeSelectData(msg);
            	$('#patType').select2({
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
	//设备编号数据
	function tradePaySource() {
		/*$.ajax({
			url : "/admin/deviceInfo/getDeviceInfos",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) {
				var jsonstr="[{'id':'全部','deviceNo':'全部'}]";
				var jsonarray = eval('('+jsonstr+')');
				for (var i = 0; i < data.length; i++) {
					data[i].id = data[i].deviceNo;
					jsonarray.push(data[i]);
				}
				// 初始化
				$("#deviceNo").select2({
					placeholder : '==请选择类型==',
					allowClear : true,
					minimumResultsForSearch : Infinity,
					data : jsonarray,
					width:'220px',
					templateResult : function(repo) {
						return repo.deviceNo;
					},
					templateSelection : function(repo) {
						return repo.deviceNo;
					}
				});
			}
		});*/
	}
	//订单状态下拉框
	function orderStateData(orderState){
		$.ajax({
            url:"/admin/reconciliation/typeValue?typeValue=Order_State&isIncludeAll=true",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	changeSelectData(msg);
            	$('#orderState').select2({
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
            	// 如果orderState不为空，则设置为默认选项
            	if(orderState){
            		$('#orderState').val(orderState).trigger("change").change();
            	}
            }
        });
	}
	//导出
	function exportData() {
		var orgNo= tradeTree.getVal;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var payBussinessType = $('#payBussinessType').val();
		var deviceNo = $('#deviceNo').val();
		var paySystemNo = $('#paySystemNo').val();
		var hisOrderNO = $('#hisOrderNO').val();
		var orderState = $('#orderState').val();
		var visitNumber = $('#visitNumber').val();
		var custName = $('#custName').val();
		var patType = $('#patType').val();
		var systemFrom = $('#selectType').val();
		if(systemFrom=="all" || systemFrom==null)systemFrom="";
		
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		} else {
			$.NOTIFY.showError("提醒", "请选择导出时间", '');
			return false;
		}
		var payType = $('#tradePayCode').val();
		if(payType=="全部" || payType==null)payType="";
		if(orgNo=="全部" || orgNo==null)orgNo="";
		if(deviceNo=="全部" || deviceNo==null)deviceNo="";
		if(patType=="全部" || patType==null)patType="";
		if(payBussinessType=="全部" || payBussinessType==null)payBussinessType="";
		if(orderState=="全部" || orderState==null)orderState="";
		
		var hour = $.fn.getHour(startDate, endDate);
		if(hour > 24*31){
			$.NOTIFY.showError("错误", '时间范围超过31天，请缩短时间范围', '');
			return false;
		}
		
		bootbox.confirm('确定执行此操作?',function(r){
            if (r){
            	var where ='orgCode='+ orgNo +'&payBussinessType='+ payBussinessType +'&deviceNo='+ deviceNo+
            	'&paySystemNo='+ paySystemNo+'&hisOrderNO='+hisOrderNO+'&orderState='+orderState+'&visitNumber='+visitNumber+
            	"&custName="+custName+'&patType='+ patType+'&startDate='+startDate+'&endDate='+endDate+'&systemFrom='+systemFrom+'&payType='+payType;
        		var url = apiUrl+'/api/dcExcel?' + where;
        		window.location.href=url;
            }
        });
	}
	
	//机构树
	function formaterOrgProps(orgCode){
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
           url:"/admin/organization/data",
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
        	   if(orgCode != null && orgCode != ""){
        		   for(var i=0;i<msg.length;i++){
            		   if(orgCode == msg[i].code){
            			   tradeTree.updateCode(msg[i].id,msg[i].code);
            		   }
            	   } 
        	   }
        	   
        	   // 选择隐藏还是现实机构下拉选择
				var length = msg.length;
				if(length && length>1){
					$("#tradeTree").parent().parent().parent().show();
				}else{
					$("#tradeTree").parent().parent().parent().hide();
				}
			   selectTypeData();
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
	//查询
	function search(th){
		var serialNumber =  $("#serialNumber").val();
		if(serialNumber != "" || serialNumber != null){
			$('#paySystemNo').val(serialNumber);
			$('#hisOrderNO').val(serialNumber);
		}
		var selectType = $('#selectType').val();
		var orgNo= tradeTree.getVal;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var payType = $('#tradePayCode').val();
		if(payType=="全部" || payType==null)payType="";
		var payBussinessType = $('#payBussinessType').val();
		var deviceNo = $('#deviceNo').val();
		var paySystemNo = $('#paySystemNo').val();
		var hisOrderNO = $('#hisOrderNO').val();
		var orderState = $('#orderState').val();
		var visitNumber = $('#visitNumber').val();
		var custName = $('#custName').val();
		var patType = $('#patType').val();
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		}
		if(orgNo=="全部" || orgNo==null)orgNo="";
		if(payBussinessType=="全部" || payBussinessType==null)payBussinessType="";
		if(patType=="全部" || patType==null)patType="";
		if(deviceNo=="全部" || deviceNo==null)deviceNo="";
		if(orderState=="全部" || orderState==null)orderState="";
		systemFrom = selectType;
		if(systemFrom=="all" || systemFrom==null)systemFrom="";
		collect(orgNo);
		tableObj.bootstrapTable('refreshOptions', {
				url:apiUrl,
			  pageNumber:1,
			  onPostBody:choseColor,
			  resizable:true,
			  queryParams:function(params){
				    var queryObj ={orgCode:orgNo,startDate:startDate,endDate:endDate,
				    		payBussinessType:payBussinessType,deviceNo:deviceNo,
				    		paySystemNo:paySystemNo,hisOrderNO:hisOrderNO,orderState:orderState,
				    		visitNumber:visitNumber,custName:custName,patType:patType,
				    		systemFrom:systemFrom,payType:payType
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
	
	//计算渠道金额
	function collect(orgNo){
		
		var serialNumber =  $("#serialNumber").val();
		if(serialNumber != "" || serialNumber != null){
			$('#paySystemNo').val(serialNumber);
			$('#hisOrderNO').val(serialNumber);
		}
		var selectType = $('#selectType').val();
		var orgNo= tradeTree.getVal;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var payBussinessType = $('#payBussinessType').val();
		var deviceNo = $('#deviceNo').val();
		var paySystemNo = $('#paySystemNo').val();
		var hisOrderNO = $('#hisOrderNO').val();
		var orderState = $('#orderState').val();
		
		var visitNumber = $('#visitNumber').val();
		var custName = $('#custName').val();
		var patType = $('#patType').val();
		
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		}
		var payType = $('#tradePayCode').val();
		if(payType=="全部" || payType==null)payType="";
		if(orgNo=="全部" || orgNo==null)orgNo="";
		if(payBussinessType=="全部" || payBussinessType==null)payBussinessType="";
		if(patType=="全部" || patType==null)patType="";
		if(deviceNo=="全部" || deviceNo==null)deviceNo="";
		if(orderState=="全部" || orderState==null)orderState="";
		systemFrom = selectType;
		if(systemFrom=="all" || systemFrom==null)systemFrom="";
		var url = apiUrl+"/countSum";
		$("#tradeDetailAllAmount").html("0.00(元)");
		$("#tradeDetailPayAllNum").html(0);
		$("#tradeDetailRefundAllNum").html(0);
		$("#wechatTradeAllAmount").html("0.00(元)");
		$("#wechatTradePayAllNum").html(0);
		$("#wechatTradeRefundAllNum").html(0);
		$("#aliTradeAllAmount").html("0.00(元)");
		$("#aliTradePayAllNum").html(0);
		$("#aliTradeRefundAllNum").html(0);
		$("#bankAllAmount").html("0.00(元)");
		$("#bankPayAllNum").html(0);
		$("#bankRefundAllNum").html(0);
		$("#ybAllAmount").html("0.00(元)");
		$("#ybPayAllNum").html(0);
		$("#ybRefundAllNum").html(0);
		$("#cashAmount").html("0.00(元)");
		$("#cashPayAllNum").html(0);
		$("#cashRefundAllNum").html(0);
		$.ajax({
	           url:url,
	           type:"get",
	           data:{
	        	    orgCode:orgNo,startDate:startDate,endDate:endDate,
		    		payBussinessType:payBussinessType,deviceNo:deviceNo,
		    		paySystemNo:paySystemNo,hisOrderNO:hisOrderNO,orderState:orderState,
		    		visitNumber:visitNumber,custName:custName,patType:patType,
		    		systemFrom:systemFrom,payType:payType
	           },
	           contentType:"application/json",
	           dataType:"json",
	           success:function(result){
	        	   var sumAmount=0;
	       		   var sumCount=0;
	       		   var sumRefundCount=0;
	        	   for(var i=0;i<result.data.length;i++){
	        		   if(result.data[i].payType!='undefined'&&result.data[i].payType!=null){
	        			   var num=result.data[i].payNum;
	        			   var refundNum=result.data[i].refundNum;
	        			   var amount=new Number(result.data[i].amount).toFixed(2)+"(元)";
	        			   //微信
	        			   if(result.data[i].payType=='0249'){
	        				   $("#wechatTradeAllAmount").html(amount);
	        				   $("#wechatTradePayAllNum").html(num);
	        				   $("#wechatTradeRefundAllNum").html(refundNum);
	        				   sumAmount=sumAmount+result.data[i].amount;
		        			   sumCount=sumCount+num;
		        			   sumRefundCount=sumRefundCount+refundNum;
	        			   }
	        			   //支付宝
	        			   if(result.data[i].payType=='0349'){
	        				   $("#aliTradeAllAmount").html(amount);
	        				   $("#aliTradePayAllNum").html(num);
	        				   $("#aliTradeRefundAllNum").html(refundNum);
	        				   sumAmount=sumAmount+result.data[i].amount;
		        			   sumCount=sumCount+num;
		        			   sumRefundCount=sumRefundCount+refundNum;
	        			   }
	        			   //银行
	        			   if(result.data[i].payType=='0149'){
	        				   $("#bankAllAmount").html(amount);
	        				   $("#bankPayAllNum").html(num);
	        				   $("#bankRefundAllNum").html(refundNum);
	        				   sumAmount=sumAmount+result.data[i].amount;
		        			   sumCount=sumCount+num;
		        			   sumRefundCount=sumRefundCount+refundNum;
	        			   }
	        			   //医保
	        			   if(result.data[i].payType=='0449'){
	        				   $("#ybAllAmount").html(amount);
	        				   $("#ybPayAllNum").html(num);
	        				   $("#ybRefundAllNum").html(refundNum);
	        				   sumAmount=sumAmount+result.data[i].amount;
		        			   sumCount=sumCount+num;
		        			   sumRefundCount=sumRefundCount+refundNum;
	        			   }
	        			   //现金
	        			   if(result.data[i].payType=='0049'){
	        				   $("#cashAmount").html(amount);
	        				   $("#cashPayAllNum").html(num);
	        				   $("#cashRefundAllNum").html(refundNum);
	        				   sumAmount=sumAmount+result.data[i].amount;
		        			   sumCount=sumCount+num;
		        			   sumRefundCount=sumRefundCount+refundNum;
	        			   }
	        			   if(result.data[i].payType=='1649'){
	        				   sumAmount=sumAmount+result.data[i].amount;
	        			   }
	        			   //总计
        				   $("#tradeDetailAllAmount").html(new Number(sumAmount).toFixed(2)+"(元)");
        				   $("#tradeDetailPayAllNum").html(sumCount); 
        				   $("#tradeDetailRefundAllNum").html(sumRefundCount); 
	        		   }
	        	   }
	           }
	       });
	}
	
	function choseColor(){
		var tt = $("table tr").find("td:eq(9)");
		if($('#selectType').val() != null && $('#selectType').val() == "all"){
			tt = $("table tr").find("td:eq(10)");
		}
		 for(var i =0 ;i<tt.length;i++){
			 var text = tt[i].textContent;
			 if(text == "交易异常"){
				 tt[i].style="color:#ff4949"
			 }else if(text == "交易失败"){
				 tt[i].style="color:#333"
			 }else if(text == "支付完成"){
				 tt[i].style="color:#12ce8a"
			 }else if(text == "待审核"){
				 tt[i].style="color:#ff9c00"
			 }else if(text == "已退款"){
				 tt[i].style="color:#333"
			 }
			 
		 }
	}
	function number(value, row, index) {
	 	   var pageSize=tableObj.bootstrapTable('getOptions').pageSize;
	        var pageNumber=tableObj.bootstrapTable('getOptions').pageNumber;
	        return pageSize * (pageNumber - 1) + index + 1;
	}
	function moneyFormat(val, row, index){
		//处理医保金额
		if(row && row.payType=='0449'){
			return new Number(row.ybPayAmount).toFixed(2);
		}
		//自费金额
		else{
			return new Number(val).toFixed(2);
		}
	}
	//操作列按钮
	function formatOpt(index,row) {
		var hConfig = $('#hConfig').val();
		
		var disabledStatus = '';
		if(row.orderState != '1809300' && row.orderState != '1809302' && row.orderState != '1809305'){
			disabledStatus = 'disabled="true"';
		}
		return "<button href='javascript:;' "+disabledStatus+" onclick='app.admin.tradeDetail.refund(\""+ row.id +"\",\"" + row.billSource + "\")' class='btn btn-info btn-sm m-primary '> 退费 </button>  &nbsp;" +
		"<button href='javascript:;' onclick='app.admin.tradeDetail.detail(\""+ row.id +"\")' class='btn btn-info btn-sm m-primary '> 详情 </button>  &nbsp;"
		
//		if(row.orderState == '1809300' || row.orderState == '1809302'){
//			var hConfigJSON = JSON.parse(hConfig);
//			if(hConfigJSON['isRefundExamine'] == 1){
//				return "<a href='javascript:;' onclick='app.admin.tradeDetail.refund(\""+ row.id +"\",\"" + row.billSource + "\")' class='btn btn-info btn-sm m-primary '> 退费 </a>  &nbsp;" +
//				"<a href='javascript:;' onclick='app.admin.tradeDetail.detail(\""+ row.id +"\")' class='btn btn-info btn-sm m-primary '> 详情 </a>  &nbsp;"
//			}else{
//				return "<a href='javascript:;' onclick='app.admin.tradeDetail.refund(\""+ row.id +"\",\"" + row.billSource + "\")' class='btn btn-info btn-sm m-primary '> 退费 </a>  &nbsp;" +
//				"<a href='javascript:;' onclick='app.admin.tradeDetail.detail(\""+ row.id +"\")' class='btn btn-info btn-sm m-primary '> 详情 </a>  &nbsp;"
//			}
//		}else{
//			return "<a href='javascript:;' onclick='app.admin.tradeDetail.detail(\""+ row.id +"\")' class='btn btn-info btn-sm m-primary '> 详情 </a>  &nbsp;"
//			
////			return "<a href='javascript:;' onclick='app.admin.tradeDetail.refund(\""+ row.id +"\")' class='btn btn-info btn-sm m-primary '> 退款 </a>  &nbsp;" +
////			"<a href='javascript:;' onclick='app.admin.tradeDetail.detail(\""+ row.id +"\")' class='btn btn-info btn-sm m-primary '> 详情 </a>  &nbsp;"
//		}
	}
	//申请退款
	function refund(id,billSource){
		refundFormObjDlg[0].reset();
		var row = tableObj.bootstrapTable('getRowByUniqueId',id);	
		dlgFormObj.loadDetailReset();
		dlgFormObj.loadDetail(row);
		refundDlgObj.find("input[name=id]").val(row.id);
		refundDlgObj.find("input[name=billSource]").val(row.billSource);
		refundDlgObj.find("input[name=extendArea]").val(row.extendArea);
		// 发票号
		refundDlgObj.find("input[name=invoiceNo]").val(row.invoiceNo);
		var payAmount = row.payAmount;
		var ybPayAmount = row.ybPayAmount;
		var payType = row.payType;
        refundDlgObj.find("input[name=tradeAmount]").val(payAmount == 0 ? ybPayAmount : payAmount);
        refundDlgObj.find("input[id=tradeRefundAmountt]").val(payAmount == 0 ? ybPayAmount : payAmount);
		refundDlgObj.find("input[name=tradeDataTime]").val(row.tradeDataTime);
		refundDlgObj.find("input[name=payBusinessType]").val(row.payBusinessType);
		refundDlgObj.find("input[name=outTradeNo]").val(row.outTradeNo);

		$("#tradePayType").val(payType);
		billSourceExt = billSource;

		$("#tradeSqm").hide();
		$("#tradePjh").hide();
		$("#tradeSysNo").hide();
		$("#tradeCashier").hide();
		$("#tradeCounterNo").hide();
		$("#tradeBocNo").hide();
		//中行退费弹框
		if ((billSource == "8391" || billSource == "8301") && payType == '0149') {
			$("#tradeSqmId").val("");
			$("#tradePjhId").val("");
			$("#tradeSysNoId").val("");
			$("#tradeCashierId").val("");
			$("#tradeCounterNoId").val("");
			$("#tradeBocNoId").val("");
			$("#tradeSqm").show();
			$("#tradePjh").show();
			$("#tradeSysNo").show();
			$("#tradeCashier").show();
			$("#tradeCounterNo").show();
			$("#tradeBocNo").show();
		}
		refundDlgObj.modal('show');
	}
	//申请退款确定按钮
	function refundButton(){
		var refundBut = $("#refundButton");
        // 退款金额
        var refundAmount = $("#tradeRefundAmountt").val();
        // 订单金额
        var tradeAmount = $( "#tradeTradeAmountt").val();
        if ((tradeAmount-refundAmount) < 0) {
            $.NOTIFY.showError("错误", "退款金额必须小于等于订单金额", '');
            return;
        }
        refundFormObjDlg.find("input[name=tradeAmount]").val(refundAmount);
        refundFormObjDlg.find("input[name=payAmount]").val(tradeAmount);
		var payType = $("#tradePayType").val();
		var tradeBillSource = billSourceExt;
		if ((tradeBillSource == "8391" || tradeBillSource == "8301") && payType == '0149') {
            if($("#tradePjhId").val()==null||$("#tradePjhId").val()==""){
                $.NOTIFY.showError("错误", "中行订单请输入票据号", '');
                return;
            }
            if($("#tradeSysNoId").val()==null||$("#tradeSysNoId").val()==""){
                $.NOTIFY.showError("错误", "中行订单请输入系统订单号", '');
                return;
            }
            if($("#tradeBocNoId").val()==null||$("#tradeBocNoId").val()==""){
                $.NOTIFY.showError("错误", "中行订单请输入流水号", '');
                return;
            }
        }
		bootbox.confirm('确定执行删除操作?',function(r){
            if (r){
            	refundBut.button('loading');
        		var url = apiUrl+"/refundButton";
        		var reasonVal = $("#reason").val();
        		var detailID = $("#detailID").val();
        		var ajax_option = {
        			url : url,
        			type : 'post',
        			success : function(result) {
        				if (JSON.parse(result).success) {
        					search()
        					refundDlgObj.modal('hide');
        					$.NOTIFY.showSuccess("提醒", "处理成功", '');
        				} else {
        					$.NOTIFY.showError("错误", JSON.parse(result).message, '');
        				}
        				refundBut.button('reset');
        			},
        			error:function(){
        				refundBut.button('reset');
        			},
        			complete:function(){
        				refundBut.button('reset');
        			}
        			
        		};
        		refundFormObjDlg.ajaxSubmit(ajax_option);
            }
        });
	}
	/**
	 * 生成节流函数
	 * @param throttleDelay : ms   节流的时间限制，单位毫秒
	 * @param handle : function    超过 throttleDelay 时，所要执行的函数
	 */
	function createThrottle(throttleDelay,handle) {

	    var thenTime = new Date() ;

	    //节流
	    function throttle(event) {
	        var now = new Date();
	        if (now - thenTime >= throttleDelay) {
	            handle(event);
	            thenTime = now;
	        }
	    }

	    return throttle;

	}
	//详情按钮信息赋值
	function detail(id){
		$("#refundInfo").hide();
		var row = tableObj.bootstrapTable('getRowByUniqueId',id);	
		dlgFormObj.loadDetailReset();
		dlgFormObj.loadDetail(row);
		//调用his接口
		var url = apiUrl+"/getHISInfo";
		var orgNo=row.orgCode;
		var payFlowNo = row.paySystemNo;

		var hisOrderNO = row.hisOrderNO;
		var tradeDataTime = row.tradeDataTime;
		
		var billSource = row.billSource;
		$.ajax({
	           url:url,
	           contentType:"application/json",
	           data:{
	        	   orgNo:orgNo,payFlowNo:payFlowNo,
	        	   billSource:billSource,
                   hisOrderNO:hisOrderNO,
                   tradeDataTime:tradeDataTime,

	           },
	           success : function(result) {
	        	   var jsonResult = JSON.parse(result);
					var data = jsonResult.data;
					var success = jsonResult.success;
					dlgFormObj.find("span[data-name=his_titleState]").text("");
					if(success == true && data != ""){
						if(data.titleState){
							dlgFormObj.find("span[data-name=his_titleState]").text("("+data.titleState+")");
						}
						dlgFormObj.find("p[data-name=his_patientName]").text(data.patientName);
						dlgFormObj.find("p[data-name=his_patientType]").text(formatter(data.patientType));
						dlgFormObj.find("p[data-name=his_patientNo]").text(data.patientNo);
						dlgFormObj.find("p[data-name=his_hisNo]").text(data.hisNo);
						dlgFormObj.find("p[data-name=his_payNo]").text(data.payNo);
						dlgFormObj.find("p[data-name=his_payType]").text(formatter(data.payType));
						dlgFormObj.find("p[data-name=his_orderState]").text(formatter(data.orderState));
						dlgFormObj.find("p[data-name=his_tradeTime]").text(data.tradeTime);
						dlgFormObj.find("p[data-name=his_tradeAmount]").text(moneyFormat(data.tradeAmount) + "元");
					}else{
						if(data.titleState != ""){
							dlgFormObj.find("span[data-name=his_titleState]").text("("+data.titleState+")");
						}
					}
				}
	       });
		dlgFormObj.find("p[data-name=orderNo]").text(row.orderNo);
		dlgFormObj.find("p[data-name=payAmount]").text(moneyFormat(row.payAmount,row) + "元");
		dlgFormObj.find("p[data-name=orderState]").text(formatter(row.orderState));
		dlgFormObj.find("p[data-name=payBusinessType]").text(formatter(row.payBussinessType));
		dlgFormObj.find("p[data-name=payType]").text(formatter(row.payType));
		dlgFormObj.find("p[data-name=cashier]").text(row.cashier);
		//默认交易异常1809300
		var titleUrl ="url(../assets/img/abnormal.png)";
		if(row.orderState == "1809300"){
			//交易异常
			showTitle(titleUrl,formatter(row.orderState));
			showStepBar(4,3,row.stateRemark,row.orderSourceState);
		}else if (row.orderState == "1809301") {
			//交易失败
			titleUrl = "url(../assets/img/icon-10.png)";
			showTitle(titleUrl,formatter(row.orderState));
			showStepBar(5,3,row.stateRemark,row.orderSourceState);
		}else if(row.orderState == "1809302"){
			//支付完成
			titleUrl = "url(../assets/img/icon-03.png)";
			showTitle(titleUrl,formatter(row.orderState));
			showStepBar(4,0,"",row.orderSourceState);
		}else if(row.orderState == "1809303"){
			//审核中
			titleUrl = "url(../assets/img/icon-03.png)";
			showTitle(titleUrl,formatter(row.orderState));
			showStepBar(4,3,"",row.orderSourceState);
		}else if(row.orderState == "1809304"){
			//已退款
			titleUrl = "url(../assets/img/icon-03.png)";
			showTitle(titleUrl,formatter(row.orderState));
			showStepBar(5,0,"",row.orderSourceState);
			var refundUrl = apiUrl+"/getRefundInfo";
			$.ajax({
		           url:refundUrl,
		           contentType:"application/json",
		           data:{
		        	   orgNo:orgNo,payFlowNo:payFlowNo
		           },
		           success : function(result) {
		        	   var jsonResult = JSON.parse(result);
						var resultDate = jsonResult.data;
						var success = jsonResult.success;
						if(success == true && resultDate != ""){
							var data = [];
							for(var i=0;i<resultDate.length;i++){
								row = {};
								row['refundOrderNo'] = resultDate[i].refundOrderNo;
					            row['refundAmount'] = resultDate[i].refundAmount;
					            row['refundDateTime'] = resultDate[i].refundDateTime;
					            data.push(row);
							}
							$("#refundTable").bootstrapTable('destroy').bootstrapTable({
								resizable: true,
					            data: data
					        });
							$("#refundInfo").show();
						}else{
							$("#refundInfo").hide();
						}
					}
		       });
		}else if(row.orderState == "1809305"){
			//被驳回
			titleUrl = "url(../assets/img/abnormal.png)";
			showTitle(titleUrl,formatter(row.orderState));
			showStepBar(4,3,row.stateRemark,row.orderSourceState);
		}
		dlgObj.modal('show');
	}
	//详情页标题变化
	function showTitle(titleUrl,titleText){
		dlgObj.find(".text-title").css("background-image",titleUrl);
		dlgObj.find(".abnormal-title").text(titleText);
		dlgObj.find(".title").text("");
	}
	//状态点公共方法
	function showStepBar(activeStep,dangerStep,errorMsg,val){
		var stepsTitle = "HIS交易失败";
		if(val == "1809302"){
			stepsTitle="HIS交易成功";
			dangerStep="0";
		}
		$("#step-bar").html("")
		$("#step-bar").loadStep({
	        //激活的步数序号
	        activeStep: activeStep,
	        //提醒的步数序号
	        dangerStep: dangerStep,
	        //byStep中包含的步骤
	        steps: [{
	            //步骤名称
	            title: "交易创建"
	        },{
	            title: "支付成功"
	        },{
	            title: stepsTitle,
	            content: errorMsg
	        },{
	        	title: "数据上送成功"
	        },{
	            title: "已退款"
	        }]
	    });
	}
	
	/**
	 * 初始化日期
	 */
	function initDate(startDate, endDate) {
		var beginTime = "";
		var endTime = "";
		if(accountDate){
			beginTime = startDate + " 00:00:00";
			endTime =  endDate + " 23:59:59";
		}
		var nowDate = new Date();
		var rangeTime = beginTime + " ~ " + endTime;
		var startLayDate = laydate.render({
			elem : '#tradeDetailStartTime',
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
	
	function pullTradeDetail(th){
		var orgNo= tradeTree.getVal;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var startDate ;
		var endDate  ;
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			startDate = rangeTime.split("~")[0];
			endDate = rangeTime.split("~")[1];
		}
		var hour = $.fn.getHour(startDate, endDate);
		if(hour > 24){
			$.NOTIFY.showError("错误", '时间范围超过1天，请缩短时间范围', '');
			return false;
		}
		
		bootbox.confirm('确定执行此操作?',function(r){
            if (r){
            	$("#pullTradeDetailModal").modal("show");
            	
            	$.ajax({
            		url: apiUrl + "/api/pullhis",
            		type:"post",
            		dataType:"json",
            		data:{orgCode:orgNo, startTime:startDate, endTime:endDate},
            		timeout:120000,
            		success:function (data){
            			$("#pullTradeDetailModal").modal("hide");
            			if(data.success){
            				$.NOTIFY.showSuccess("提示", '处理成功!', '');
            			}else{
            				$.NOTIFY.showError("错误", '处理异常请重试!', '');
            			}
            		},
            		error:function(data){
            			$("#pullTradeDetailModal").modal("hide");
            		}
            	});
            }
        });
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
		search:search,
		orgFormatter:orgFormatter,
		formatter:formatter,
		exportData:exportData,
		number:number,
		moneyFormat:moneyFormat,
		formatOpt:formatOpt,
		detail:detail,
		showTradeCondition:showTradeCondition,
		refund:refund,
		refundButton:refundButton,
		choseColor:choseColor,
		orderStateData:orderStateData,
		pullTradeDetail:pullTradeDetail,
        numExt:numExt
	}
})();
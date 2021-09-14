NB.ns("app.admin").citizenCardBill = (function() {
	
	var tableObj=$('#citizenCardBillDataTable');
	var formObj = $("#citizenCardBillSearchForm");
	var startObj=$('#citizenCardBillRecTime');
	var payFlowNoObj = formObj.find('input[name=payFlowNo]');
	var orderStateObj = formObj.find('select[name=orderState]');
	
	var citizenCardBillSummary = $('#citizenCardBillSummary');
	var orgTree;
	var apiUrl = '/admin/citizenCardBill';
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
				orgTree = $("#citizenCardBillOrgTree").ztreeview({
					name : 'name',
					key : 'code',
					//是否
					clearable : true,
					expandAll : true,
					data : msg
				}, setting);
				orgTree.updateCode(msg[0].id, msg[0].code);
				
				var accountOrgNo = $("#citizenCardBillOrgNoInit").val();
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
					$("#citizenCardBillOrgTree").parent().parent().parent().show();
				}else{
					$("#citizenCardBillOrgTree").parent().parent().parent().hide();
				}
				search();
			}
		});
	}
	function initTable(){
		//TODO  初始化默认值为  888888
		var orgNo= '888888'
		tableObj.bootstrapTable({
			url : apiUrl + "/data",
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable: true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
			queryParams : {
				orgNo : orgNo,
				startDate : startDate,
				endDate : endDate
            },
		});
	}
	function loadSummary(){
		var orgNo= orgTree.getVal;
		var rangeTime = startObj.val();
		var starttime = "";
	    var endtime = "";
	    if(rangeTime){
            starttime = rangeTime.split("~")[0];
            endtime = rangeTime.split("~")[1];
        }
	    var payFlowNo = payFlowNoObj.val();
	    var orderState = orderStateObj.val();
	    var queryObj ={orgCode:orgNo,startDate:starttime,endDate:endtime,payFlowNo:payFlowNo,orderState:orderState};
	    $.ajax({
	    	url:apiUrl+"/summary",
	    	type : "get",
			dataType : "json",
			data:queryObj,
			success:function(rec){
				citizenCardBillSummary.html('');
				for ( var index in rec) {
					var item=rec[index]
					var html = '<div class="pay-type-amount-box">'
						+'<div class="pay-type-amount-title-line">'
						+'<img src="/assets/img/rec/zh-icon.png">'
						+'<span>'+formatterState(item.orderState)+'</span>'
						+'</div>'
						+'<div class="pay-type-amount-line">'
						+'<span >总金额</span><span id="ybAllAmount">'+moneyFormat(item.money)+'</span>'
						+'</div>'
						+'<div class="pay-type-amount-line">'
						+'<span >笔数</span><span id="ybPayAllNum">'+item.count+'</span>'
						+'</div>'
						+'</div>'
						;
					citizenCardBillSummary.append(html);
				}
			}
	    })
	}
	function search(){
		var orgNo= orgTree.getVal;
		var rangeTime = startObj.val();
		var starttime = "";
	    var endtime = "";
	    if(rangeTime){
            starttime = rangeTime.split("~")[0];
            endtime = rangeTime.split("~")[1];
        }
	    var payFlowNo = payFlowNoObj.val();
	    var orderState= orderStateObj.val();
	    loadSummary();
		tableObj.bootstrapTable('refreshOptions', {
			  resizable: true,
			  pageNumber:1,
			  queryParams:function(params){
				  var queryObj ={orgCode:orgNo,startDate:starttime,endDate:endtime,payFlowNo:payFlowNo,orderState:orderState};
				  var query = $.extend( true, params, queryObj);
				  return query;
			  }
		});
	}
	/**
	 * 初始化日期
	 */
	function initDate() {
		var nowDate = new Date();
		var rangeTime = startDate + " ~ " + endDate;
		var startLayDate = laydate.render({
            elem : '#citizenCardBillRecTime',
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
	function exportData(){
		var orgNo= orgTree.getVal;
		var rangeTime = startObj.val();
		var sortName = tableObj.bootstrapTable('getOptions').sortName||'';
		var sortOrder = tableObj.bootstrapTable('getOptions').sortOrder;
		var starttime = "";
	    var endtime = "";
	    if(rangeTime){
            starttime = rangeTime.split("~")[0];
            endtime = rangeTime.split("~")[1];
        }
	    var payFlowNo = payFlowNoObj.val();
	    var orderState = orderStateObj.val()
	    var queryObj ={orgCode:orgNo,startDate:starttime,endDate:endtime,payFlowNo:payFlowNo,orderState:orderState};
		bootbox.confirm('确定执行此操作?',function(r){
            if (r){
            	var where ='sort='+sortName+'&order='+sortOrder+'&orgCode='+ orgNo +'&startDate='+starttime +
            	'&endDate=' + endtime + '&orderState='+orderState+'&limit=9999999';
        		var url = apiUrl+'/exportData?' + where;
        		window.location.href=url;
            }
        });
	}
	function formatHandler(val,row){
		return '操作按钮';
	}
	function formatterState(val,row){
		var state = val;
		if(val=='10011'){
			state='充值';
		}else if(val=='10021'){
			state='消费';
		}else if(val=='30011'){
			state='退款';
		}else if(val=='10031'){
			state='提现';
		}else if(val=='30031'){
			state='充值撤销';
		}
		return state;
	}
	function moneyFormat(val, row, index){
		return new Number(val).toFixed(2);
	}
	function init(){
		initTree();
		initTable();
		initDate();
	}
	
	
	return {
		init : init,
		formatterState:formatterState,
		moneyFormat:moneyFormat,
		formatHandler:formatHandler,
		search:search,
		exportData:exportData
	}
})();
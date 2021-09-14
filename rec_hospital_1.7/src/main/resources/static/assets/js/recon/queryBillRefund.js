NB.ns("app.admin").queryBillRefund = (function() {
	var apiUrl="/admin/queryBillRefund/data";	
	var objButton=$("#queryBillRefund");
	var queryButton=$("#refundRecordButton");
	function init() { 
		objButton.attr('disabled',true);
	}
	
	//查账方法
	function queryBill(){
		var payNo=$("#quertBillPayNo").val();
		queryButton.button('loading');
		if(payNo==null || payNo ==''){
			$.NOTIFY.showError  ("错误", '订单号、流水号不能为空','');
		}
		$.ajax({
			type: 'POST',
			url : apiUrl+"/getDate",
			data: {"payNo":payNo},
			dataType : "json",
			success : function(result) {
				$("#tipsDiv").html("");
				if (result.success) {
					var json=JSON.parse(result.data);
					//$("#queryBillTradeAmount").val($("#billRefundAmount").html());
					$("#queryDdh").html(json.out_trade_no);
					$("#tsnNo").html(json.tsn);
					$("#queryBillPayCode").html(json.pay_name);
					$("#billRefundAmount").html(moneyFormat(json.order_amount));
					if(json.refund_amount ==null){
						$("#queryTkStata").html("未退款");
						$("#queryTkAmount").html("0.00");
					}else{
						if(json.refund_state=='1'||json.refund_state=='2'){
							$("#queryTkStata").html("已退款");
						}else{
							$("#queryTkStata").html("退款失败");
						}
						$("#queryTkAmount").html(moneyFormat(json.refund_amount));
					}
					objButton.attr('disabled',false);
				}else{
					$("#queryDdh").html("-");
					$("#tsnNo").html("-");
					$("#queryBillPayCode").html("-");
					$("#billRefundAmount").html("-");
					$("#queryTkStata").html("-");
					$("#queryTkAmount").html("-");
					$("#tipsDiv").html('<label class="control-label" style="color: red;float: left;">'+result.message+'</label>');
				}
				queryButton.button('reset');
			}
    	});
		
	}
	function moneyFormat(tradeAmount){
		return new Number(tradeAmount).toFixed(2)+"元";
	}
	
	//操作退费
	function executionRefund(){
		$("#tipsDiv").html("");
		var tradeAmount=$("#queryBillTradeAmount").val();
		var payNo=$("#tsnNo").html();
		var payCode=$("#queryBillPayCode").html();
		var passWord=$("#queryBillPassWord").val();
		var billAmount=$("#billRefundAmount").html();
		var payAmount=billAmount.split("元");
		if((payAmount[0]-tradeAmount)<0){
			$("#tipsDiv").html('<label class="control-label" style="color: red;float: left;">退款金额不能超过总支付金额</label>');
			return;
		}
		if(tradeAmount==null||tradeAmount==''){
			$("#tipsDiv").html('<label class="control-label" style="color: red;float: left;">退款金额不能为空！</label>');
			return;
		}
		$.ajax({
			type: 'POST',
			url : apiUrl,
			data: {"tradeAmount":tradeAmount,"payNo":payNo,"payCode":payCode,"passWord":passWord,"payAmount":payAmount[0]},
			dataType : "json",
			success : function(result) { 
				if (result.success) {
					$.NOTIFY.showSuccess("提醒", "操作成功", '');
					objButton.attr('disabled',true);
				}else{
					$("#tipsDiv").html('<label class="control-label" style="color: red;float: left;">'+result.message+'</label>');
					objButton.attr('disabled',false);
				}
			}
    	});
	}


	return {
		init : init,
		queryBill:queryBill,
		executionRefund:executionRefund
	}
})();

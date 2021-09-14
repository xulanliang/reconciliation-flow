NB.ns("app.admin").tradeCheck = (function() {
	//表格
	var tableObj =$("#tradeCheckDataTable");
	var dlgObj = $('#tradeCheckDiv');
	//表单
	var formObj = $("#tradeCheckSearchForm");
	// 时间控件
	var startobj = formObj.find("input[name=beginTime]");
	var dlgObj2 = $('#fileUp');
	//表单
	var formObj2 = dlgObj2.find("form");
	//请求路径
	var apiUrl = '/admin/tradeCheck/data';
	var tradeCheckTree;
	var options = { 
            beforeSubmit:  function(formData, jqForm, options){
            	 var flg = formObj2.data('bootstrapValidator');
			     return true;
            },
            success: function(result){
            	if(result.success){
            		$.NOTIFY.showSuccess ("提醒", "操作成功");
            		dlgObj2.modal('hide');
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
	
	//设备编码
	function equipmentNoData(){
		$.ajax({
            url:"/admin/deviceInfo/getDeviceInfos?isIncludeAll=true",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	$('#tradeCheck_equipmentNo').select2({
					data:msg,
					placeholder: '==请选择类型==',
				    width:'150px',
				    allowClear: true,
				    templateResult:function(repo){
				    	return repo.deviceNo;
				    },
				    templateSelection:function(repo){
				    	return repo.deviceNo;
				    }
				});
            }
        });
	}
	//微信商户号
	function wechatDate(){
		$.ajax({
            url:"/admin/shopInfo/data/shopInfoList?payName=微信",
            type:'get',
            success:function(msg){
            	$('#wechatApplyId').select2({
					data:msg,
					placeholder: '==请选择类型==',
					minimumResultsForSearch: -1,
				    width:'150px',
				    allowClear: true,
				    templateResult:function(repo){
				    	return repo.payShopNo;
				    },
				    templateSelection:function(repo){
				    	return repo.payShopNo;
				    }
				});
            	$('#wechatApplyId').val("").trigger("change");
            }
        });
	}
	//支付宝
	function alipayDate(){
		$.ajax({
            url:"/admin/shopInfo/data/shopInfoList?payName=支付宝",
            type:'get',
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	$('#alipayApplyId').select2({
					data:msg,
					placeholder: '==请选择类型==',
					minimumResultsForSearch: -1,
				    width:'150px',
				    allowClear: true,
				    templateResult:function(repo){
				    	return repo.payShopNo;
				    },
				    templateSelection:function(repo){
				    	return repo.payShopNo;
				    }
				});
            	$('#alipayApplyId').val("").trigger("change");
            }
        });
	}
	//对账
	function startRec(){
		   var orgNo = tradeCheckTree.getVal;
			if(orgNo==null||orgNo==''){
				$.NOTIFY.showError ('提示','请选择机构!','');
				return false;
			}
		   var accountDate = startobj.val();
		   if(accountDate==""||accountDate==null){
				$.NOTIFY.showError ("提示", "请选择日期!",'');
				return false;
			}
		   var url = apiUrl+"/"+orgNo+"/account?accountDate="+accountDate+"&orgNo="+orgNo;
		   $('#tradeCheckLoading').modal('show');
		   $.ajax({
	           url:url,
	           type:"post",
	           contentType:"application/json",
	           dataType:"json",
	           success:function(result){
	        	   if (result.success) {
	        		   $('#tradeCheckLoading').modal('hide');
	        		   $.NOTIFY.showSuccess ("提醒", result.message,'');
					} else {
						$('#tradeCheckLoading').modal('hide');
						$.NOTIFY.showError ("出错了", "账单校验失败!",'');
					}
	        	   
	           }
		   });
	   }
	
	function importData(){
		resetValidator();
		dlgObj2.modal("show");
	}
	function importSave() {
		var wechatApplyId = $('#wechatApplyId').val();
		var wechatPayShopNo = $('#wechatApplyId').val();
		$('#wechatPayShopNo').val(wechatPayShopNo);
		var alipayApplyId = $('#alipayApplyId').val();
		var alipayPayShopNo = $('#alipayApplyId').val();
		$('#alipayPayShopNo').val(alipayPayShopNo);
		var fileStrOne = $('#file1').val();
		var fileStrTwo = $('#file2').val();
		var fileStrThree = $('#file3').val();
		var fileStrFour = $('#file4').val();
		if(fileStrOne.length<=0&&fileStrTwo.length<=0&&fileStrThree.length<=0&&fileStrFour.length<=0){
			$.NOTIFY.showError ("提醒", "请选择需要上传的文件",'');
			return;
		}
		if(fileStrOne.length>1||fileStrTwo.length>1){
			if(wechatPayShopNo==""){
				$.NOTIFY.showError ("提醒", "请选择微信商户号",'');
				return;
			}
		}
		if(fileStrThree.length>1||fileStrFour.length>1){
			if(alipayPayShopNo==""){
				$.NOTIFY.showError ("提醒", "请选择支付宝商户号",'');
				return;
			}
		}
		$('#tradeCheckLoading').modal('show');
		options.url = apiUrl;
		options.type='post';
		formObj2.ajaxSubmit(options); 
	}
	
	
	//查询
	function search(){
		options.url=apiUrl;
		options.type="get";
		var orgNo= tradeCheckTree.getVal;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var tradeDate='';
		if(startobj.val()!=null && startobj.val()!=''){
			tradeDate = startobj.val();
		}
		var businessNo = $('#tradeCheck_businessNo').val();
		var payNo = $('#tradeCheck_payNo').val();
		var hisFlowNo = $('#tradeCheck_hisFlowNo').val();
		var equipmentNo = $('#tradeCheck_equipmentNo').val();
		tableObj.bootstrapTable('refreshOptions', {
			  pageNumber:1,
			  queryParams:function(params){
				    var queryObj ={orgNo:orgNo,tradeDate:tradeDate,
				    	equipmentNo:equipmentNo,businessNo:businessNo,payNo:payNo,hisFlowNo:hisFlowNo
				    };
	                var query = $.extend( true, params, queryObj);
	                return query;
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
        	   tradeCheckTree = $("#tradeCheckTree").ztreeview({
	       			name: 'name',
	       			key: 'code', 
	       			//是否
	       			clearable:true,
	                   expandAll:true,
	       			data: msg
	       		}, setting);
        	   tradeCheckTree.updateCode(msg[0].id,msg[0].code);
           }
       });
	}
	
	/*function formatHandler(val, row, index) {
		var checkState = row.checkState;
		var tradeName = row.tradeName;
		var id = row.id;
		var name = "";
		if(checkState==3&&tradeName=="支付"){
			name="<a onclick='app.admin.tradeChecking.handler("+ id + ")' class='btn btn-info btn-sm m-primary'>退费</a>";
		}
		return name;
	}*/
	//退费
	/*function handler(val) {
		var id = val;
		var url = apiUrl+"/"+id+"/refund?id="+id;
		$('#tradeCheckLoading').modal('show');
		$.get(url, {}, function(result) {
			if(result.success){
				$('#tradeCheckLoading').modal('hide');
				$.NOTIFY.showSuccess ("提示", result.message);
			}else{
				layer.close(index);
				$.NOTIFY.showError ("提示", result.message);
			}
			
		},"json");
	}*/
	function formatter(val) {
		var typeJSON = $('#tradeCheckType').val();
		var typesJSON = JSON.parse(typeJSON);
		return typesJSON[val];
	}
	function orgFormatter(val) {
		var orgJSON = $('#tradeCheckOrgNo').val();
		var orgsJSON = JSON.parse(orgJSON);
		return orgsJSON[val];
	}
	
	function init() {
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
			startobj.datepicker('hide');
		});
		startobj.val($("#tradeCheckTime").val());
		tableObj.bootstrapTable({
			url : apiUrl,
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
		});
		formaterOrgProps();
		equipmentNoData();
		wechatDate();
		alipayDate();
	}
	function resetValidator(){
		formObj2.resetForm();
		$("#wechatApplyId").empty();
		$("#alipayApplyId").empty();
		wechatDate();
		alipayDate();
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
		formatter:formatter,
		startRec:startRec,
		orgFormatter:orgFormatter,
		importData:importData,
		importSave:importSave,
		number:number,
		moneyFormat:moneyFormat
	}
})();
 


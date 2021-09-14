NB.ns("app.admin").shopInfo = (function() {
	//表格
	var tableObj =$("#shopInfoDataTable");
	//修改用户框
	var dlgObj = $('#shopInfoDlg');
	//表单
	var formObj = dlgObj.find("form");
	//请求路径
	var apiUrl = '/admin/shopInfo/data';
	//机构赋值type
	var type=0;
	var orgNo='';
	var codeTree;
	var orgNameTree;
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
	function formaterOpt(index,row) {
		var html="<a onclick='app.admin.shopInfo.edit("+ row.id + ",\""+row.payName+"\",\""+row.deviceNos+"\")' class='btn btn-info btn-sm m-primary'>编辑</a>   &nbsp;";
		html=html+"<a onclick='app.admin.shopInfo.destroy("+ row.id + ")' class='btn btn-info btn-sm m-danger'>删除</a>  &nbsp;";
		return html;
	}
	
	function clearSelect2(val,deviceNos){
		$("#deviceNos").empty();
		initType(val,deviceNos);
	}
	
	function create() {
		$("#shopId").val('');
		options.url=apiUrl+"/save";
		dlgObj.find("h4").text("新增商户");
		options.type='post';
		formObj.resetForm();
		resetValidator();
		clearSelect2(null,null);
		dlgObj.modal('show');
	}
	/*//设备编码数据
	function deviceNoData(){
		var html='';
		$.ajax({
            url:"/admin/deviceInfo/getDeviceInfos",
            type:"post",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	$("#deviceNoDiv").find("select[name=deviceNo]").html('');
            	$("#deviceNoDiv").find("select[name=deviceNo]").append("<option value=''></option>");
            	for(var i=0;i<msg.length;i++){
            		$("#deviceNoDiv").find("select[name=deviceNo]").append("<option value='"+msg[i].deviceNo+"'>"+msg[i].deviceNo+"</option>");
            	}
            }
        });
	}*/
	//支付渠道类型数据
	function payNameData(){
		var html='';
		$.ajax({
            url:"/admin/dict/typeValue?typeValue=Pay_Channel",
            type:"get",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	$("#payNameDiv").find("select[name=payName]").html('');
            	$("#payNameDiv").find("select[name=payName]").append("<option value=''></option>");
            	for(var i=0;i<msg.length;i++){
            		$("#payNameDiv").find("select[name=payName]").append("<option name='"+msg[i].id+"' value='"+msg[i].name+"'>"+msg[i].name+"</option>");
            	}
            }
        });
	}
	//投资银行数据
	function metaDataBankIdData(){
		var html='';
		$.ajax({
            url:"/admin/dict/typeValue?typeValue=Bank_Type&amp;isIncludeAll=true",
            type:"get",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	$("#metaDataBankIdDiv").find("select[name=metaDataBankId]").html('');
            	$("#metaDataBankIdDiv").find("select[name=metaDataBankId]").append("<option value=''></option>");
            	for(var i=0;i<msg.length;i++){
            		$("#metaDataBankIdDiv").find("select[name=metaDataBankId]").append("<option value='"+msg[i].id+"'>"+msg[i].name+"</option>");
            	}
            }
        });
	}
	//pin算法数据
	function pinAlgorithmData(){
		var html='';
		$.ajax({
            url:"/admin/dict/typeValue?typeValue=Pin_Algorithm&amp;isIncludeAll=true",
            type:"get",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	$("#pinAlgorithmDiv").find("select[name=pinAlgorithm]").html('');
            	$("#pinAlgorithmDiv").find("select[name=pinAlgorithm]").append("<option value=''></option>");
            	for(var i=0;i<msg.length;i++){
            		$("#pinAlgorithmDiv").find("select[name=pinAlgorithm]").append("<option value='"+msg[i].id+"'>"+msg[i].name+"</option>");
            	}
            }
        });
	}
	//mac算法数据
	function macAlgorithmData(){
		var html='';
		$.ajax({
            url:"/admin/dict/typeValue?typeValue=Mac_Algorithm&amp;isIncludeAll=true",
            type:"get",
            contentType:"application/json",
            dataType:"json",
            success:function(msg){
            	$("#macAlgorithmDiv").find("select[name=macAlgorithm]").html('');
            	$("#macAlgorithmDiv").find("select[name=macAlgorithm]").append("<option value=''></option>");
            	for(var i=0;i<msg.length;i++){
            		$("#macAlgorithmDiv").find("select[name=macAlgorithm]").append("<option value='"+msg[i].id+"'>"+msg[i].name+"</option>");
            	}
            }
        });
	}
	
	function payNameChange(){
		var text = $("#payNameDiv").find("select[name=payName] option:selected").text();
		if (text.indexOf('支付宝')>=0) {
			$('#metaDataBankIdDiv').hide();
			$('#shopPayTpduDiv').hide();
			$('#pinAlgorithmDiv').hide();
			$('#macAlgorithmDiv').hide();
			$('#payPinkeyIdDiv').hide();
			$('#payMackeyIdDiv').hide();
			$('#payPkeyIdDiv').hide();
			$('#payTermNoDiv').hide();
			$('#bussShortnameDiv').hide();
			$('#wxPayKeyDiv').hide();
			$('#wxSslcertPasswordDiv').hide();
			$('#applyIdDiv').show();
			$('#qrcodeTimeoutDiv').show();
			$('#orderTimeoutDiv').show();
			$('#companyPidDiv').show();
			$('#serviceAddressDiv').show();
			$('#billFilePathDiv').show();
			$('#serviceAddress').val("https://openapi.alipay.com/gateway.do");
			$('#qrcodeTimeout').val("5m");
			$('#orderTimeout').val("5m");
			$('#billFilePath').val("D:\BillDownload");
			
		} else if(text.indexOf('微信')>=0){
			$('#metaDataBankIdDiv').hide();
			$('#shopPayTpduDiv').hide();
			$('#pinAlgorithmDiv').hide();
			$('#macAlgorithmDiv').hide();
			$('#payPinkeyIdDiv').hide();
			$('#payMackeyIdDiv').hide();
			$('#payPkeyIdDiv').hide();
			$('#payTermNoDiv').hide();
			$('#bussShortnameDiv').hide();
			$('#companyPidDiv').hide();
			$('#applyIdDiv').show();
			$('#qrcodeTimeoutDiv').show();
			$('#orderTimeoutDiv').show();
			$('#wxPayKeyDiv').show();
			$('#wxSslcertPasswordDiv').show();
			$('#serviceAddressDiv').show();
			$('#billFilePathDiv').show();
			$('#serviceAddress').val("https://api.mch.weixin.qq.com");
			$('#qrcodeTimeout').val(360);
			$('#orderTimeout').val(20);
			$('#billFilePath').val("D:\BillDownload");
			$('#wxPayKey').val("00000000000000000000000000000000");
			
		}else {
			$('#metaDataBankIdDiv').show();
			$('#shopPayTpduDiv').show();
			$('#pinAlgorithmDiv').show();
			$('#macAlgorithmDiv').show();
			$('#payPinkeyIdDiv').show();
			$('#payMackeyIdDiv').show();
			$('#payPkeyIdDiv').show();
			$('#payTermNoDiv').show();
			$('#bussShortnameDiv').show();
			$('#applyIdDiv').hide();
			$('#qrcodeTimeoutDiv').hide();
			$('#orderTimeoutDiv').hide();
			$('#companyPidDiv').hide();
			$('#wxPayKeyDiv').hide();
			$('#wxSslcertPasswordDiv').hide();
			$('#serviceAddressDiv').hide();
			$('#billFilePathDiv').hide();
			$('#payPinkeyId').val(0);
			$('#payMackeyId').val(1);
		}
	}
	
	function edit(id,name,deviceNos){
		dlgObj.find("h4").text("修改商户信息");
		var row = tableObj.bootstrapTable('getRowByUniqueId',id);
		formObj.resetForm();
		formObj.loadForm(row);
		resetValidator();
		options.url=apiUrl+"/update";
		options.type="put";
		
		clearSelect2(row.orgNo,deviceNos);
		//获取用户机构
		$.ajax({
            url:apiUrl+"/"+id+"/org",
            data:{code:row.orgNo},
            type:"get",
            success:function(data){
            	orgNameTree.updateCode(data,row.orgNo);
            }
        });
		
		if (name.indexOf('支付宝')>=0) {
			$('#metaDataBankIdDiv').hide();
			$('#shopPayTpduDiv').hide();
			$('#pinAlgorithmDiv').hide();
			$('#macAlgorithmDiv').hide();
			$('#payPinkeyIdDiv').hide();
			$('#payMackeyIdDiv').hide();
			$('#payPkeyIdDiv').hide();
			$('#payTermNoDiv').hide();
			$('#bussShortnameDiv').hide();
			$('#wxPayKeyDiv').hide();
			$('#wxSslcertPasswordDiv').hide();
			$('#applyIdDiv').show();
			$('#qrcodeTimeoutDiv').show();
			$('#orderTimeoutDiv').show();
			$('#companyPidDiv').show();
			$('#serviceAddressDiv').show();
			$('#billFilePathDiv').show();
			
		} else if(name.indexOf('微信')>=0){
			$('#metaDataBankIdDiv').hide();
			$('#shopPayTpduDiv').hide();
			$('#pinAlgorithmDiv').hide();
			$('#macAlgorithmDiv').hide();
			$('#payPinkeyIdDiv').hide();
			$('#payMackeyIdDiv').hide();
			$('#payPkeyIdDiv').hide();
			$('#payTermNoDiv').hide();
			$('#bussShortnameDiv').hide();
			$('#companyPidDiv').hide();
			$('#applyIdDiv').show();
			$('#qrcodeTimeoutDiv').show();
			$('#orderTimeoutDiv').show();
			$('#wxPayKeyDiv').show();
			$('#wxSslcertPasswordDiv').show();
			$('#serviceAddressDiv').show();
			$('#billFilePathDiv').show();
			
		}else {
			$('#metaDataBankIdDiv').show();
			$('#shopPayTpduDiv').show();
			$('#pinAlgorithmDiv').show();
			$('#macAlgorithmDiv').show();
			$('#payPinkeyIdDiv').show();
			$('#payMackeyIdDiv').show();
			$('#payPkeyIdDiv').show();
			$('#payTermNoDiv').show();
			$('#bussShortnameDiv').show();
			$('#applyIdDiv').hide();
			$('#qrcodeTimeoutDiv').hide();
			$('#orderTimeoutDiv').hide();
			$('#companyPidDiv').hide();
			$('#wxPayKeyDiv').hide();
			$('#wxSslcertPasswordDiv').hide();
			$('#serviceAddressDiv').hide();
			$('#billFilePathDiv').hide();
		}
		//回显select2
		/*var arr=new Array();
		var atr="2,3";
		arr=atr.split(",");*/
		
		//$("#deviceNos").val('56565').trigger("change");
		//$("#deviceNos").select2("val","{"+row.orgNo+"}").trigger("change");
		dlgObj.modal('show');
	}
	
	function save(){
		$("#metaDataPayId").val($("#payNameDiv").find("select[name=payName] option:selected").attr("name"));
		options.type="put";
		formObj.ajaxSubmit(options);
	}
	function orgName(value){
		var typeJSON = $('#orgMap').val();
		var typesJSON = JSON.parse(typeJSON);
		return typesJSON[value];
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
			        callback: {
			    		onClick: function (){
			    			clearSelect2(orgNameTree.getVal,null);
			    		}
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
        	   codeTree = $("#codeTree").ztreeview({
	       			name: 'name',
	       			key: 'code', 
	       			//是否
	       			clearable:true,
	                   expandAll:true,
	       			data: msg
	       		}, setting);
        	   codeTree.updateCode(msg[0].id,msg[0].code);
        	   orgNameTree = $("#orgNameTree").ztreeview({
	       			name: 'name',
	       			key: 'code', 
	       			//是否
	       			clearable:true,
	                   expandAll:true,
	       			data: msg
	       		}, setting);
           }
       });
	}
	
	function destroy(id){
		bootbox.confirm('确认删除该记录吗?', function(r) {
               if (r) {
            	   $.ajax({
       	            url:apiUrl+'/'+id+"?id="+id,
       	            type:"delete",
       	            success:function(msg){
       	            	$.NOTIFY.showSuccess ("提醒", "删除成功",'');
       	            	reflush();
       	            	
       	            },
       	            error:function(xhr,textstatus,thrown){

       	            }
       	        });
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
		var orgNo =codeTree.getVal;
		if (orgNo == 9999)
			orgNo = "";
		tableObj.bootstrapTable('refreshOptions', {
			  pageNumber:1,
			  queryParams:function(params){
				    var queryObj ={orgNo:orgNo};
	                var query = $.extend( true, params, queryObj);
	                return query;
	            }
		});
		
	}
	
	function initType(orgNo,deviceNos){
		if(orgNo ==null){
			$('#deviceNos').select2({
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
		}else{
			$.ajax({
				url : "/admin/deviceInfo/"+orgNo+"/deviceInfo",
				data:{
					orgNo:orgNo
				},
				type : "get",
				dataType : "json",
				success : function(data) {
					var num=0;
					$('#deviceNos').select2({
						data:data,
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
					var arr=new Array();
					var atr="";
					for(var i=0;i<data.length;i++){
						var zu=deviceNos.split(",")
						for(var z=0;z<zu.length;z++){
							if(zu[z]==data[i].deviceNo){
								if(atr==""){
									atr=data[i].id
								}else{
									atr=atr+","+data[i].id;
									num=1;
								}
							}
						}
					}
					if(num==0){
						$("#deviceNos").val(atr).trigger("change");
					}else{
						arr=atr.split(",");
						$("#deviceNos").val(arr).trigger("change");
					}
					
				}
			});
		}
		
	}
	
	
	function init() {
		tableObj.bootstrapTable({
			url : apiUrl,
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			pagination : true, // 是否分页
			height: $(window).height()-360,
			sidePagination : 'server',// 选择服务端分页
		});
		resetTableHeight(tableObj, 360);
		initValid();
		formaterOrgProps();
		initType();
		payNameData();
		metaDataBankIdData();
		pinAlgorithmData();
		macAlgorithmData();
	}
	
	
	function initValid(){
		formObj.bootstrapValidator({
            message: '不能为空',
            feedbackIcons: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            fields: {
            	payShopNo: {
                    validators: {
                    	notEmpty: {
                            message: '商户号不能为空'
                        },
                        stringLength: {
                            max: 30,
                            message: '最大30字符'
                        }
                    }
                },
                deviceNos: {
                    validators: {
                        notEmpty: {
                            message: '设备号不能为空'
                        }
                    }
                },
                payName: {
                    validators: {
                        notEmpty: {
                            message: '支付渠道不能为空'
                        }
                    }
                },
                orgNo: {
                    validators: {
                        notEmpty: {
                            message: '支付渠道不能为空'
                        }
                    }
                }
            }
        })
	}
	function number(value, row, index) {
	   var pageSize=tableObj.bootstrapTable('getOptions').pageSize;
       var pageNumber=tableObj.bootstrapTable('getOptions').pageNumber;
       return pageSize * (pageNumber - 1) + index + 1;
	}
	return {
		init : init,
		create : create,
		edit:edit,
		destroy:destroy,
		save:save,
		search:search,
		payNameChange:payNameChange,
		formaterOpt : formaterOpt,
		number:number,
		orgName:orgName
	}
})();
 


NB.ns("app.reconciliation").channelschedule = (function() {

	//表格
	var tableObj = $("#channelscheduleDataTable");
	//对话框
	var dlgObj = $('#channelscheduleDlg');
	//表单
	var formObj = dlgObj.find("form");
	//请求路径
	var apiUrl = '/admin/channelScheduleInfo/data';
	var payChannelUrl ='/admin/dict/typeValue?typeValue=Pay_Channel';
	var ztreeObj_search;
	var ztreeObj_form;
	var payTypeList=[];
	var typeObj= $("#channelschedule_CronSelect");
	var weeksObj = $("#channelschedule_weekSelect");
    var hoursObj = $("#channelschedule_hourSelect");
    var minutesObj = $("#channelschedule_minuteSelect");
    var secondsObj = $("#channelschedule_secondSelect");
    var cronValObj=$("#channelschedule_comtomCronInput");
   
    
	var options = {
		beforeSubmit : function(formData, jqForm, options) {
			var flg = formObj.data('bootstrapValidator').validate().isValid();
			return flg;
		},
		success : function(result) {
			if (result.success) {
				dlgObj.modal('hide');
				reflush();
				$.NOTIFY.showSuccess("提醒", "操作成功", '');
			} else {
				if (result.message) {
					$.NOTIFY.showError("错误", result.message, '');
				}
			}
		},
		url : apiUrl,
		type : 'post',
		dataType : 'json',
		clearForm : false,
		resetForm : false,
		timeout : 3000
	};
	
	function formatPayType(value, row, index) {
		var temp = "未知类型";
		if (payTypeList.length > 0) {
			for (var i = 0; i < payTypeList.length; i++) {
				if (payTypeList[i].id == row.metaPayId) {
					temp = payTypeList[i].value;
					break;
				}
			}
		}
		return temp;
	}
	function formatOpt(index, row) {
		var txt ='';
		var stat =0;
		if(row.jobsStatus == 1){
			txt ='暂停';
			stat=0;
		}else if(row.jobsStatus == 0){
			txt='重置';
			stat=1;
		}
		return "<a href='javascript:;' onclick='app.reconciliation.channelschedule.edit(" + row.id
        + ")' class='btn btn-info btn-sm m-primary '> 编辑 </a>  &nbsp;"
		+ "<a <a href='javascript:;' onclick='app.reconciliation.channelschedule.destroy("
		+ row.id + ")' class='btn btn-info btn-sm m-danger'> 删除 </a>" 
		+" <a <a href='javascript:;' onclick='app.reconciliation.channelschedule.updateStatus("
		+ row.id + ","+stat+")' class='btn btn-info btn-sm m-danger'> "+txt+"</a>";
	}
	
    function formatStatus(index, row){
    	var text ='未知';
    	if(row.jobsStatus == 1){
    		text ='运行';
    	}else if(row.jobsStatus == 0){
    		text ='停止';
    	}
    	return text;
    }
	
	function resetValidator(){
		formObj.data('bootstrapValidator').destroy();
		formObj.data('bootstrapValidator',null);
		initValid();
	}


	function create() {
		dlgObj.find("h4").text("新增任务");
		options.type = 'post';
		recoveryCronSelect();
		formObj.resetForm();
		resetValidator();
		dlgObj.modal('show');
	}

	function edit(id) {
		dlgObj.find("h4").text("修改任务");
		options.type = 'put';
		var row = tableObj.bootstrapTable('getRowByUniqueId', id);
		formObj.resetForm();
		formObj.loadForm(row);
		resetValidator();
		dlgObj.modal('show');
		$("#channelschedulePaytype").val(row.metaPayId).trigger("change");
		initCron(row.jobCorn);
		ztreeObj_form.updateValue(row.orgNo);
	}

	function save() {
		setCornValue();
		formObj.ajaxSubmit(options);
	}

    function updateStatus(id,status){
    	var title =status ==1?'确定重置吗?':'确定暂停吗?'
    	bootbox.confirm({
		    title: "提示?",
		    message: title,
		    buttons: {
		        confirm: {
		            className: 'btn-primary btn-sm'
		        },
		        cancel: {
		            className: 'btn-info btn-sm'
		        }
		    },
		    callback: function (r) {
		    	if (r) {
					$.ajax({
						url : apiUrl + '/' + id+'/status/'+status,
						type : "put",
						contentType : "application/json",
						dataType : "json",
						success : function(result) {
							if (result.success) {
								$.NOTIFY.showSuccess("提醒", "删除成功", '');
								reflush();
							} else {
								if (result.message) {
									$.NOTIFY.showError("错误", result.message, '');
								}
							}
						}
					});
				}
		    }
		});
    }
    
	function destroy(id) {
		bootbox.confirm({
		    title: "提示?",
		    message: "确认删除该记录吗?",
		    buttons: {
		        confirm: {
		            className: 'btn-primary btn-sm'
		        },
		        cancel: {
		            className: 'btn-info btn-sm'
		        }
		    },
		    callback: function (r) {
		    	if (r) {
					$.ajax({
						url : apiUrl + '/' + id,
						type : "delete",
						contentType : "application/json",
						dataType : "json",
						success : function(result) {
							if (result.success) {
								$.NOTIFY.showSuccess("提醒", "删除成功", '');
								reflush();
							} else {
								if (result.message) {
									$.NOTIFY.showError("错误", result.message, '');
								}
							}
						}
					});
				}
		    }
		});
	}

	function reflush() {
		tableObj.bootstrapTable('refresh');
	}

	function search() {	
	  	tableObj.bootstrapTable('refreshOptions', {
			  pageNumber:1,
			  queryParams:function(params){
				    var queryObj ={orgId:ztreeObj_search.getVal};
	                var query = $.extend( true, params, queryObj);
	                return query;
	            }
		});
	}
	
	
	function initValid() {
		formObj.bootstrapValidator({
			message : '不能为空',
			feedbackIcons : {
				valid : 'glyphicon glyphicon-ok',
				invalid : 'glyphicon glyphicon-remove',
				validating : 'glyphicon glyphicon-refresh'
			},
			
			fields : {
			/*	channelscheduleOrgNo : {
					validators : {
						notEmpty : {
							message : '请选择机构'
						}
					
					}
				},*/
				metaPayId : {
					validators : {
						notEmpty : {
							message : '请选择支付渠道'
						}
					
					}
				},
				jobName : {
					validators : {
						notEmpty : {
							message : '任务名称不能为空'
						},
						stringLength : {
							max : 20,
							message : '最大20字符'
						}
					}
				},
				jobCron : {
					validators : {
						notEmpty : {
							message : '执行周期不能为空'
						}
					}
				},
				jobDesc : {
					validators : {
						stringLength : {
							max : 100,
							message : '描述最多100字符'
						}
					}
				}
			}
		})
	}
	
	
	function initTable() {
		tableObj.bootstrapTable({
			url : apiUrl,
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			showPaginationSwitch:false,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
		});
	}
	
	function initOrgTree(){
		var setting = {
	    		view: {
	    			dblClickExpand: false,
	    			showLine: false,
	    			selectedMulti: false,
	    			fontCss: {fontSize:'30px'}
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
		            url:'/admin/organization',
		            type:"get",
		            contentType:"application/json",
		            dataType:"json",
		            success:function(data){
		            		//这个树形下拉会在和其同级位置生成一个默认隐藏的input输入框以提供表单数据
		            		ztreeObj_search = $("#channelscheduleSearchOrgSelect").ztreeview({
		            			name: 'name',
		            			key: 'id', 
		            			//是否
		            			clearable:true,
		                        expandAll:true,
		            			data: data
		            		}, setting);
		            		ztreeObj_search.updateCode(data[0].id,data[0].code);
		            		ztreeObj_form = $("#channelscheduleFormOrgSelect").ztreeview({
		            			name: 'name',
		            			key: 'id', 
		            			//是否
		            			clearable:true,
		                        expandAll:true,
		            			data: data
		            		}, setting);
		            		
		            		
		            }
		        });
	}
	
	function initCronSelect(){
	  var hour='';
	  var minute='';
	  var second='';
	  for(var i=0;i<24;i++){
		  hour  = hour + "<option value='"+ i +"'>"+ (i) + "时</option>";
	  }
	  for(var i=1;i<60;i++){
	
		  minute  = minute + "<option value='"+ i +"'>"+ (i) + "分</option>";
		  second  = second + "<option value='"+ i +"'>"+ (i) + "秒</option>";
	  }	
	  hoursObj.html(hour);  
	  minutesObj.html(minute);  
	  secondsObj.html(second);  
	  
	}
	
	function recoveryCronSelect(){
		  typeObj.val('1');
		  secondsObj.val('1');
		  weeksObj.hide();
		  hoursObj.hide();
		  minutesObj.hide();
		  secondsObj.show();
		  cronValObj.hide();
	}
	
	//初始化cron表达式
	 function initCron(jobCorn){
		     weeksObj.hide();
		     hoursObj.hide();
		     minutesObj.hide();
		     secondsObj.hide();
		     cronValObj.hide();
	     
	     var cornArr,typeVal=-1,cornV;
	     if(jobCorn!=null)
	     	{
	     	 cornArr = jobCorn.split('@');
	     	 typeVal  = cornArr[1];
	     	 cornV = cornArr[0];
	     	}
	     typeObj.val(typeVal);
	     switch(parseInt(typeVal)){
	      case 1:
	    	  var jsonVal = cornV.split('/')[1].split(' ')[0];
	    	  secondsObj.show();
	    	  secondsObj.val(jsonVal);
	    	  break;
	      case 2:
	    	  var jsonVal = cornV.split('/')[1].split(' ')[0];
	    	  minutesObj.show();
	    	  minutesObj.val(jsonVal);
	     	  break;
	      case 3:
	    	  var jsonVal = cornV.split('/')[1].split(' ')[0];
	    	  hoursObj.show();
	    	  hoursObj.val(jsonVal);
	     	  break;
	      case 4:
	    	  jsonVal = cornV.split(' ');
	    	  minutesObj.show();
	    	  hoursObj.show();
	    	  minutesObj.val(jsonVal[1]);
	    	  hoursObj.val(jsonVal[2]);
	     	 break;
	      case 5:
	    	  jsonVal = cornV.split(' ');
	    	  weeksObj.show();
	    	  minutesObj.show();
	    	  hoursObj.show();
	    	  minutesObj.val(jsonVal[1]);
	    	  hoursObj.val(jsonVal[2]);
	    	  weeksObj.val(jsonVal[5]);
	     	 break;
	      case 6:
	    	  cronValObj.show();
	    	  cronValObj.val(cornV);
	     	 break;	 
	     }
	 }	
	 

	 
	 function setCornValue(){		
	        var cronType = typeObj.val();	
	        var weekVal=weeksObj.val();
	        var hourVal=hoursObj.val();
	        var minuteVal=minutesObj.val();
	        var secondval=secondsObj.val();
			var cornvalue;
			switch(parseInt(cronType)){
			 case 1:
				  cornvalue = "*/"+secondval +" * * * * ?";
				  break;
			 case 2:
				  cornvalue = "0 */"+minuteVal+" * * * ?"
				  break;
			 case 3:
				 cornvalue = "0 0 0/"+hourVal+" * * ?"
				  break;
			 case 4:
				  if (minuteVal =='')
					{
					  minuteVal = 0;
					}
					if(hourVal != '')
					{
						cornvalue = "0 "+minuteVal+" "+hourVal+" * * ?";
					}
				  break;
			 case 5:
				   if(minuteVal=='')
					{
					   minuteVal = 0;
					}
					if(weekVal!='' && hourVal!='')
					{
						cornvalue = "0 "+minuteVal+" "+hourVal+" ? * "+weekVal;
					}
				  break;
			 case 6:
				  cornvalue = cronValObj.val();
				  break;
			}
					 
			var text =typeObj.find("option:selected").text();
			cronValObj.val(cornvalue+"@"+cronType+"@"+text);
		}
  
    

   	function cronchagneHandle(obj){
   		var v =$(obj).val();
   		switch(parseInt(v)){
   		//秒
   		case 1:
   			secondsObj.val('1');
   			secondsObj.show();
   			minutesObj.hide();
   			hoursObj.hide();
   			weeksObj.hide();
   			cronValObj.hide();
   			break;
   		//分
   		case 2:
   			minutesObj.val('1');
   			secondsObj.hide();
   			minutesObj.show();
   			hoursObj.hide();
   			weeksObj.hide();
   			cronValObj.hide();
   			break;
   		//时
   		case 3:
   			hoursObj.val('1');
   			secondsObj.hide();
   			minutesObj.hide();
   			hoursObj.show();
   			weeksObj.hide();
   			cronValObj.hide();
   			break;
   		//n点m分
   		case 4:
   			minutesObj.val('1');
   			hoursObj.val('1');
   			secondsObj.hide();
   			minutesObj.show();
   			hoursObj.show();
   			weeksObj.hide();
   			cronValObj.hide();
   			break;
   		//x周n点m分
   		case 5:
   			minutesObj.val('1');
   			hoursObj.val('1');
   			weeksObj.val('MON');
   			secondsObj.hide();
   			minutesObj.show();
   			hoursObj.show();
   			weeksObj.show();
   			cronValObj.hide();
   			break;
   		case 6:
   			secondsObj.hide();
   			minutesObj.hide();
   			hoursObj.hide();
   			weeksObj.hide();
   			cronValObj.show();
   			cronValObj.val('');
   			break;
   		}
   	}
   	
	function initPayType(){
		$.ajax({
			url : payChannelUrl,
			type : "GET",
			dataType : "json",
			success : function(data) {
				payTypeList=data;
				$("#channelschedulePaytype").select2({
					placeholder: '==请选择类型==',
				    allowClear: true,
				    width:'100%',
				    minimumResultsForSearch: Infinity,
				    data:data,
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
	
    function init(){
    	initPayType();
    	initOrgTree();
    	initValid();
    	initCronSelect();
    	initTable();
    }
    
	return {
		init:init,
		create : create,
		edit:edit,
		destroy:destroy,
		updateStatus:updateStatus,
		save:save,
		formatOpt : formatOpt,
		formatStatus:formatStatus,
		formatPayType:formatPayType,
		search : search,
		cronchagneHandle:cronchagneHandle
	}

})();

$(function(){
	app.reconciliation.channelschedule.init();
});
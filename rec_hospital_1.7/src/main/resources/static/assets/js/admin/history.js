NB.ns("app.admin").history = (function() {
	
	// 表单
	var formObj = $("#historydataSearchForm");
	//表格
	var tableObj = $("#historylogDataTable");
	var orgSelectObj=$("#historylogOrgSelect");
	// 时间控件
	var startobj = formObj.find("input[name=startTime]");
	var endobj =   formObj.find("input[name=endTime]");
	
	//请求路径
	var apiUrl = '/admin/historydata/data';
	var typesJSON =$.parseJSON(tableObj.attr("typesJSON"));
	var orgJSON =  $.parseJSON(tableObj.attr("orgJSON"));
	
	var ztreeObj_search;
	
    function formatOpt(index, row){
    	var orgNo=row.orgNo;
    	var orderDate=row.orderDate;
    	var id = row.id;
    	return "<a href='javascript:;' onclick='app.admin.history.recon("+id+","+orgNo+",\""+orderDate+"\")' class='btn btn-info btn-sm m-primary '> 重新获取 </a>";
    }
    
    function formatOrg(val,index, row){
	   if(orgJSON[val]){
      	 return orgJSON[val];
       }
       return '未知';
    }
    
    function formatDate(val,index, row){
    	if(val != null){
    		return val.substring(0, 10)
    	}else{
    		return val;
    	}
    }
    
    function formatStat(val,index,row){
    	if(typesJSON[val]){
    		return typesJSON[val];
    	}
    	return '未知';
    }
    
    function recon(id,orgNo,orderDate){
    	var validate = false;
    	if(null != id){
    		validate = true;
    	}else{
    		validate = formObj.data('bootstrapValidator').validate().isValid();
    	}

		if(validate){
			var startTime;
	    	var endTime;
	    	if(null != orderDate){
	    		startTime = orderDate;
	    		endTime = orderDate;
	    	}else{
	    		startTime = startobj.val();
	    		endTime = endobj.val();
	    	}
	    	if(null == orgNo){
	    		orgNo = ztreeObj_search.getVal;
	    	}
	    	
	    	if(startTime != null  && endTime != null && startTime != ""  && endTime != ""){
	    		bootbox.confirm({
				    title: "提示?",
				    message: '确定要获取账单吗?',
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
				    		$('#histortDataLoading').modal('show');
				    		$.post(apiUrl,{id:id,orgNo:orgNo,startTime:startTime,endTime:endTime},function(result){
				    			$('#histortDataLoading').modal('hide');
				    			if (result.success) {
				    				search();
								    $.NOTIFY.showSuccess("提醒", result.message, '');
								} else {
									if (result.message) {
										$.NOTIFY.showError("错误", result.message, '');
									}
								}
				    		},"json");
						}
				    }
				});
	    	}else{
	    		$.NOTIFY.showSuccess("提醒", '请选择时间', '');
	    	}
	    	
		}
    }
    
	function search() {	
		var orgNoTemp= ztreeObj_search.getVal;
		if (orgNoTemp === 9999 || orgNoTemp === null || orgNoTemp === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		
	  	tableObj.bootstrapTable('refreshOptions', {
	  		  resizable: true,
			  pageNumber:1,
			  queryParams:function(params){
				  	
				    var queryObj ={orgNo:ztreeObj_search.getVal,startTime:startobj.val(),endTime:endobj.val()};
	                var query = $.extend( true, params, queryObj);
	                return query;
	            }
		});
	}
	
	
	function initTable() {
		tableObj.bootstrapTable({
			url : apiUrl,
			dataType : "json",
			uniqueId : "id",
			resizable: true,
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
	            		ztreeObj_search = orgSelectObj.ztreeview({
	            			name: 'name',
	            			key: 'code', 
	            			//是否
	            			clearable:true,
	                        expandAll:true,
	            			data: data
	            		}, setting);
	            		ztreeObj_search.updateCode(data[0].id,data[0].code);
		            }
		        });
	}

	//初始化时间
	function initDate(){ 
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
	
	//初始化验证插件
    function initValid(){
    	formObj.bootstrapValidator({
            message: '不能为空',
            feedbackIcons: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            fields: {
            	historylogOrgSelect: {
                    validators: {
                        notEmpty: {
                            message: ' '
                        }
                    }
                }
            }
        })
    }
    function init(){
    	initOrgTree();
    	initTable();
    	initDate();
    	initValid();
    }
    
	return {
		init:init,
		formatOpt:formatOpt,
    	formatOrg:formatOrg,
    	formatStat:formatStat,
    	search:search,
    	recon:recon,
    	number:number,
    	formatDate:formatDate
	}

})();
$(function(){
	app.admin.history.init();
});
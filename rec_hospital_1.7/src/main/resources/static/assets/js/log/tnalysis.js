NB.ns("app.reconciliation").tnalysis = (function() {

	//表格
	var tableObj = $("#tnalysisDataTable");
	//请求路径
	var apiUrl = '/admin/tnalysis/data';
	var typesJSON =$.parseJSON(tableObj.attr("typesJSON"));
	var typesJSONS =$.parseJSON(tableObj.attr("typesJSONS"));
	var orgJSON =  $.parseJSON(tableObj.attr("orgJSON"));
	var ztreeObj_search;
	
    function formatOpt(index, row){
    	var id=row.id;
    	var fileId=row.fileId;
    	var orgCode=row.orgCode;
    	var systemCode =row.systemCode;
    	var orderDate=row.orderDate;
    	var payChannel =row.payChannel;
    	return '<a href="javascript:;" onclick="app.reconciliation.tnalysis.reparse(\''+id+'\',\''+fileId+'\',\''+orgCode+'\',\''+systemCode+'\',\''+orderDate+'\',\''+payChannel+'\')" class="btn btn-info btn-sm m-primary" > 重新解析 </a>';
    }
    
    //组织机构
    function formatOrg(val,index, row){
    	   if(orgJSON[val]){
          	 return orgJSON[val];
           }
           return '未知';
    	
    }
    //系统名称
    function formatSys(val,index,row){
    	if(typesJSONS[val]){
    		return typesJSONS[val];
    	}
    	return '未知';
    }
    //状态
    function format(val,index,row){
    	if(typesJSON[val]){
    		return typesJSON[val];
    	}
    	return '未知';
    }
    
    function reparse(id,fileId,orgCode,systemCode,orderDate,payChannel){
        	bootbox.confirm({
    		    title: "提示?",
    		    message: '确定要重新解析吗?',
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
    		    		$.post(apiUrl,{id:id,fileId:fileId,orgCode:orgCode,systemCode:systemCode,orderDate:orderDate,payChannel:payChannel},function(result){
    		    			if (result.success) {
							    $.NOTIFY.showSuccess("提醒", "对账成功", '');
							} else {
								if (result.message) {
									$.NOTIFY.showError("错误", result.message, '');
								}
							}
    		    		  },"json");
    				}
    		    }
    		});
    }
    
	function search() {	
		var orgNoTemp = ztreeObj_search.getVal;
		if (orgNoTemp === 9999 || orgNoTemp === null || orgNoTemp === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
	  	tableObj.bootstrapTable('refreshOptions', {
			  pageNumber:1,
			  queryParams:function(params){
				    var queryObj ={orgCode:ztreeObj_search.getVal};
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
	    				idKey: "code",
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
	            		ztreeObj_search = $("#tnalysisOrgSelect").ztreeview({
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
	


   	

    function init(){
    	initOrgTree();
    	initTable();
    }
    
	return {
		init:init,
		reparse:reparse,
    	formatOrg:formatOrg,
    	format:format,
    	formatSys,formatSys,
    	formatOpt:formatOpt,
    	search:search
	}

})();
$(function(){
	app.reconciliation.tnalysis.init();
});
NB.ns("app.admin").yylog = (function() {

	//表格
	var tableObj = $("#yylogDataTable");
	var formObj =$("#yylogSearchForm");
	var businessType =formObj.find("select[name=businessType]")
	var payType =formObj.find("select[name=payType]")
	var paySource =formObj.find("select[name=paySource]")
	var typesJSON =$.parseJSON(tableObj.attr("typesJSON"));
	var orgJSON =  $.parseJSON(tableObj.attr("orgJSON"));
	
	//请求路径
	var apiUrl = '/admin/yylog/data';
	var ztreeObj_search;
  
    function formatOrg(val,index, row){
         if(orgJSON[val]){
        	 return orgJSON[val];
         }
         return '未知';
    }

    function formatType(val,index,row){
    	if(typesJSON[val]){
    		return typesJSON[val];
    	}
    	return '未知';
    }
 
	function search() {	
		var queryData = formObj.serializeObject();
		if(queryData.payType && queryData.payType instanceof Array && queryData.payType.length >0){
			queryData.payType = queryData.payType.join();
		}
	  	tableObj.bootstrapTable('refreshOptions', {
			  pageNumber:1,
			  queryParams:function(params){
	                var query = $.extend( true, params, queryData);
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
		            		ztreeObj_search = $("#yylogOrgSelect").ztreeview({
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
	

	function initDict(){
		  //业务类型
		   $.ajax({
	            url:'/admin/dict/typeValue',
	            data:{typeValue:'Pay_Business_Type'},
	            type:"get",
	            contentType:"application/json",
	            dataType:"json",
	            success:function(data){
	            	data.unshift({id:'',value:'全部'});
	            	businessType.select2({
						data:data,
					    width:'150px',
					    allowClear: false,
					    //禁止显示搜索框
					    minimumResultsForSearch: Infinity,
					    templateResult:function(repo){
					    	return repo.name;
					    },
					    templateSelection:function(repo){
					    	return repo.name;
					    }
					});
	            }
	        });
		   
		   //支付类型
		   $.ajax({
	            url:'/admin/dict/typeValue',
	            data:{typeValue:'Pay_Type'},
	            type:"get",
	            contentType:"application/json",
	            dataType:"json",
	            success:function(data){
	            	payType.select2({
						data:data,
						placeholder: '全部',
					    width:'200px',
					    allowClear: true,
					    multiple:true,
					    //禁止显示搜索框
					    minimumResultsForSearch: Infinity,
					    templateResult:function(repo){
					    	return repo.name;
					    },
					    templateSelection:function(repo){
					    	return repo.name;
					    }
					});
	            }
	        });
		   
		   //支付来源
		   $.ajax({
	            url:'/admin/dict/typeValue',
	            data:{typeValue:'Pay_Source'},
	            type:"get",
	            contentType:"application/json",
	            dataType:"json",
	            success:function(data){
	            	data.unshift({id:'',value:'全部'});
	            	paySource.select2({
						data:data,
					    width:'150px',
					    allowClear: false,
					    //禁止显示搜索框
					    minimumResultsForSearch: Infinity,
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
   	
	function initDate(){
		$("#yylogOrderDate_start").datepicker({
			minView : "month",
			language : 'zh-CN',
			format : 'yyyy-mm-dd',
			autoclose : true,
			clearBtn: true,
			todayHighlight: true, 
			pickerPosition : "bottom-left"
				
		})
		
		$("#yylogOrderDate_end").datepicker({
			minView : "month",
			language : 'zh-CN',
			format : 'yyyy-mm-dd',
			autoclose : true,
			clearBtn: true,
			todayHighlight: true, 
			pickerPosition : "bottom-left"
				
		})
	}
	

	function exportExcel(){
		bootbox.confirm({
		    title: "确认?",
		    message: '一次导出最大的数据量为5000条,确定执行此操作?',
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
		    		window.location.href=apiUrl+'/export?'+formObj.serialize();
				}
		    }
		});
	}
	
    function init(){
    	initDate();
    	initOrgTree();
    	initDict();
    	initTable();
    }
    
	return {
		init:init,
    	search:search,
    	formatOrg:formatOrg,
    	formatType:formatType,
    	exportExcel:exportExcel
	}

})();
$(function(){
	app.admin.yylog.init();
});
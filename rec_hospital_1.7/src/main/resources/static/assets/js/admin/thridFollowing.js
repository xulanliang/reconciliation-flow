NB.ns("app.admin").thridFollowing = (function() {
	//表格
	var tableObj =$("#thridFollowingSumDataTable");
	var tableObj2 =$("#thridFollowingDataTable");
	var dlgObj = $('#thridFollowingDiv');
	//表单
	var formObj = $("#thridFollowingSearchForm");
	// 时间控件
	var startobj = formObj.find("input[name=beginTime]");
	//请求路径
	var apiUrl = '/admin/nextDayThridAccount/data';
	var thridFollowingTree;
	var orgJSON;
	var options = { 
            beforeSubmit:  function(formData, jqForm, options){
            	 var flg = formObj.data('bootstrapValidator');
			     return flg.validate().isValid();
            },
            success: function(result){
            	if(result.success){
            		$.NOTIFY.showSuccess ("提醒", "操作成功",'');
            		dlgObj.modal('hide');
            		reflush();
            	}else{
            		if(result.message){
            			$.NOTIFY.showError  ("错误", result.message,'');
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
	
	//导出
	function exportData() {
		var orgNo = thridFollowingTree.getVal;
		if(orgNo==9999)orgNo="";
		var orgName=thridFollowingTree.getText;
		if(orgNo==""||orgName==null){
			$.NOTIFY.showError ('提示','请选择机构!','');
			return;
		}
		var tradeDate = startobj.val();
		if(tradeDate==""||tradeDate==null){
			$.NOTIFY.showError ("提示", "请选择日期!",'');
			return false;
		}
		bootbox.confirm('确定执行此操作?',function(r){
            if (r){
        		where ='&orgNo='+ orgNo +'&tradeDate='+ tradeDate+"&orgName="+encodeURI(orgName);
        		url = apiUrl+'/dcExcel?' + where+"&t="+new Date().getTime();
                window.location.href=url;
            }
        });
	}
	//对账
	function startRec(){
		   var orgNo = thridFollowingTree.getVal;
			if(orgNo==null||orgNo==''){
				$.NOTIFY.showError ('提示','请选择机构!','');
				return;
			}
		   var tradeDate = startobj.val();
		   if(tradeDate==""||tradeDate==null){
				$.NOTIFY.showError ("提示", "请选择日期!",'');
				return;
			}
		   var url = apiUrl+"/account";
		   $('#followLoading').modal('show');
		   $.ajax({
	           url:url,
	           type:"post",
	           data:{
	        	   orgNo:orgNo,
	        	   tradeDate:tradeDate
	           },
	           dataType:"json",
	           success:function(result){
	        	   if (result.success) {
	        		   $('#followLoading').modal('hide');
	        		   $.NOTIFY.showSuccess ("提醒", result.message,'');
	        		   search();
					} else {
						$('#followLoading').modal('hide');
						$.NOTIFY.showError ("出错了", result.message,'');
					}
	        	   
	           }
		   });
	   }
	
	
	//查询
	function search(){
		var orgNo= thridFollowingTree.getVal;
		if (orgNo === 9999 || orgNo === null || orgNo === '') {
			$.NOTIFY.showError("错误", '请选择所属机构!', '');
			return;
		}
		var startDate='';
		if(startobj.val()!=null && startobj.val()!=''){
			startDate = startobj.val();
		}
		$.ajax({
	           url:apiUrl,
	           type:"get",
	           data:{
	        	   orgNo:orgNo,
	        	   tradeDate:startDate
	           },
	           contentType:"application/json",
	           dataType:"json",
	           success:function(msg){
	        	   tableObj.bootstrapTable('refreshOptions',{
		        		data:msg.data,
		        		resizable: true,
		        		dataType : "json",
		       			uniqueId : "id",
		       			singleSelect : true
	       			});
	           }
	    });
		options.url=apiUrl+"/exceptionTrade";
    	   tableObj2.bootstrapTable('refreshOptions', {
    		   resizable: true,
			  pageNumber:1,
			  queryParams:function(params){
				    var queryObj ={orgNo:orgNo,tradeDate:startDate};
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
        	   thridFollowingTree = $("#thridFollowingTree").ztreeview({
	       			name: 'name',
	       			key: 'code', 
	       			//是否
	       			clearable:true,
	                expandAll:true,
	       			data: msg
	       		}, setting);
        	   thridFollowingTree.updateCode(msg[0].id,msg[0].code);
           }
       });
	}
	
	//退费
	function handler(val) {
		var id = val;
		var url = apiUrl+"/"+id+"/refund?id="+id;
		$('#followLoading').modal('show');
		$.post(url, {}, function(result) {
			if(result.success){
				$('#followLoading').modal('hide');
				$.NOTIFY.showSuccess ("提示", result.message,'');
			}else{
				layer.close(index);
				$.NOTIFY.showError ("提示", result.message,'');
			}
			
		},"json");
	}
	function formatter(val) {
		
		var typeJSON = $('#thridFollowingType').val();
		var typesJSON = JSON.parse(typeJSON);
		return typesJSON[val];
	}
	
	function orgFormatter(val) {
		var orgJSONs = $.parseJSON(orgJSON);
		return "<div style=\"word-wrap:break-word;word-break:break-all\">"+orgJSONs[val]+"</div>";
	}
	
	function init(orgJSON_temp,orgNo) {
		orgJSON = orgJSON_temp;
		
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
		startobj.val($("#thridFollowingTradeDate").val());
		$.ajax({
	           url:apiUrl+"?orgNo="+orgNo+"&tradeDate="+startobj.val(),
	           type:"get",
	           contentType:"application/json",
	           dataType:"json",
	           success:function(msg){
	        	   tableObj.bootstrapTable({
	        		   resizable: true,
		        		data:msg.data,
		        		dataType : "json",
		       			uniqueId : "id",
		       			singleSelect : true
	       			});
	           }
	    });
		tableObj2.bootstrapTable({
			url : apiUrl+"/exceptionTrade",
			dataType : "json",
			uniqueId : "id",
			resizable: true,
			singleSelect : true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
		});
		formaterOrgProps();
		
		//点击行数据时,进入查看界面
		tableObj.on("click-row.bs.table",function(e, row, $element){
			var orgNo= thridFollowingTree.getVal;
			if(orgNo==9999)orgNo="";
			var startDate='';
			if(startobj.val()!=null && startobj.val()!=''){
				startDate = startobj.val();
			}
		    options.url=apiUrl+"/exceptionTrade";
			tableObj2.bootstrapTable('refreshOptions', {
				resizable: true,
				  pageNumber:1,
				  queryParams:function(params){
					    var queryObj ={orgNo:orgNo,tradeDate:startDate,patType:row.patType};
		                var query = $.extend( true, params, queryObj);
		                return query;
		            }
			});
		})
	}
	function number(value, row, index) {
	   var pageSize=tableObj2.bootstrapTable('getOptions').pageSize;//通过表的#id 可以得到每页多少条
       var pageNumber=tableObj2.bootstrapTable('getOptions').pageNumber;//通过表的#id 可以得到当前第几页
       return pageSize * (pageNumber - 1) + index + 1;    //返回每条的序号： 每页条数 * （当前页 - 1 ）+ 序号
    }
	function numberSum(value, row, index) {
		var pageSize=tableObj.bootstrapTable('getOptions').pageSize;//通过表的#id 可以得到每页多少条
        var pageNumber=tableObj.bootstrapTable('getOptions').pageNumber;//通过表的#id 可以得到当前第几页
        return pageSize * (pageNumber - 1) + index + 1;    //返回每条的序号： 每页条数 * （当前页 - 1 ）+ 序号
	}
	function moneyFormat(val, row, index){
		return new Number(val).toFixed(2);
	}
	
	return {
		init : init,
		search:search,
		/*formatHandler:formatHandler,*/
		formatter:formatter,
		orgFormatter:orgFormatter,
		exportData:exportData,
		startRec:startRec,
		number:number,
		numberSum:numberSum,
		moneyFormat:moneyFormat
	}
})();
 


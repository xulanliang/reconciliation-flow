
NB.ns("app.admin").refundTable=(function(){
	var tableObj=$("#refundTableTable");
	//模态框
	var dlgObj = $('#refundTableDlg');
	//表单
	var formObj = dlgObj.find("form");
	
	var apiUrl="/refundTable";
	var options = { 
            beforeSubmit:  function(formData, jqForm, options){
            	 var flg = formObj.data('bootstrapValidator');
			     return flg.validate().isValid();
            },
            success: function(result){
            	if(result.success){
            		$.NOTIFY.showSuccess ("提醒", "退费成功");
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
	//初始化表格
	function initTable(){
		tableObj.bootstrapTable({
			url : apiUrl+"/getAllRefundTable",
			method:"get",
			dataType : "json",
			uniqueId : "id",
			singleSelect : true,
			resizable: true,
			pagination : true, // 是否分页
			//height: $(window).height()-360,
			sidePagination : 'server',// 选择服务端分页
		});
		//resetTableHeight(tableObj, 360);
	}
	/**
	 * 根据查询项进行搜索
	 */
	function search(){
		options.url=apiUrl+"/getAllrefundTable";
		options.type="get";
        var payFlowNo=$("#searchPayFlowNo").val();
        var rangeTime=$("#searchRequestTime").val();

        tableObj.bootstrapTable('refreshOptions', {
			  queryParams:function(params){
				    var queryObj ={payFlowNo:payFlowNo,rangeTime:rangeTime}
                    var query = $.extend( true, params, queryObj);
	                return query;
	            }
		});
	}
    /**
	 * 新增产品优化项
	 * 时间的为默认的现在的时间
	 */
	function create(){
		options.url=apiUrl+"/insertRefundTable";
		options.type='post';		
		formObj.resetForm();
		dlgObj.find("h4").text("新增");		
		resetValidator();
		dlgObj.modal('show');
		initDate();
	}
    /**
	 * 初始化日期
	 */
	function initDate() {
		var nowDate = new Date();
		var startLayDate1 = laydate.render({
			elem : '#searchRequestTime',
			btns: ['confirm'],
			theme : '#A9BCF5',
			type: "date",
			value: $("#nowDate").val()+" ~ "+$("#nowDate").val(),
			range:"~",
			format:"yyyy-MM-dd",
			max: nowDate.getTime()
		});
	}
	/**
	 * 重置验证
	 */
	function resetValidator(){
		formObj.data('bootstrapValidator').destroy();
		formObj.data('bootstrapValidator',null);
		initValid();
	}/**
	 * 表单验证
	 */
	function initValid(){
		formObj.bootstrapValidator({
            message: '不能为空',
            feedbackIcons: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            fields: {
                id: {
                    validators: {
                    	notEmpty: {
                            message: '不能为空'
                        },
                        stringLength: {
                            max: 30,
                            message: '最大30字符'
                        }
                    }
                },
                payFlowNo: {
                    validators: {
                    	notEmpty: {
                            message: '不能为空'
                        },
                        stringLength: {
                            max: 30,
                            message: '最大30字符'
                        }
                    }
                },
                payType: {
                    validators: {
                    	notEmpty: {
                            message: '不能为空'
                        },
                        stringLength: {
                            max: 30,
                            message: '最大30字符'
                        }
                    }
                },
                refundAmount: {
                    validators: {
                    	notEmpty: {
                            message: '不能为空'
                        },
                        stringLength: {
                            max: 30,
                            message: '最大30字符'
                        }
                    }
                },
                reason: {
                    validators: {
                    	notEmpty: {
                            message: '不能为空'
                        },
                        stringLength: {
                            max: 30,
                            message: '最大30字符'
                        }
                    }
                },
                refundState: {
                    validators: {
                    	notEmpty: {
                            message: '不能为空'
                        },
                        stringLength: {
                            max: 30,
                            message: '最大30字符'
                        }
                    }
                },
                shengheReason: {
                    validators: {
                    	notEmpty: {
                            message: '不能为空'
                        },
                        stringLength: {
                            max: 30,
                            message: '最大30字符'
                        }
                    }
                },
                shenghePeople: {
                    validators: {
                    	notEmpty: {
                            message: '不能为空'
                        },
                        stringLength: {
                            max: 30,
                            message: '最大30字符'
                        }
                    }
                },
                requestTime: {
                    validators: {
                    	notEmpty: {
                            message: '不能为空'
                        },
                        stringLength: {
                            max: 30,
                            message: '最大30字符'
                        }
                    }
                },
                shengheTime: {
                    validators: {
                    	notEmpty: {
                            message: '不能为空'
                        },
                        stringLength: {
                            max: 30,
                            message: '最大30字符'
                        }
                    }
                },
                     
            }
        })
	}
    /**
	 * 初始化一切要初始化的函数
	 */
	function init(){
		initTable();
		initValid();
		initDate();
	}
	
	/**
	 * 修改产品优化信息
	 */
	function edit(id){

		var row = tableObj.bootstrapTable('getRowByUniqueId',id);
		formObj.resetForm();
		formObj.loadForm(row);
		resetValidator();
		options.url=apiUrl+"/tuifeishenghe";
		options.type="post";
		dlgObj.modal('show');
		
	}
	/**
	 * 修改相关信息后刷新表格
	 */
	function reflush(){
		tableObj.bootstrapTable('refresh');
	}
    function destroy(id){
		bootbox.confirm('确认删除该记录吗?', function(r) {
            if (r) {
         	   $.ajax({
    	            url:apiUrl+"/deleteRefundTable?id="+id,
    	            type:"post",        
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
	
	function save(){
		options.type="post";
		formObj.ajaxSubmit(options);
	}
	
	//此函数专门用于表格中的数据提示
	function prompt(value,row){
		if(value!=null){
			var html='<p data-toggle="tooltip" title="'+value+'">'+value+'</p>';
		}
		return html;
	}
	
	//js日期的扩展,生成指定日期格式的字符串
	Date.prototype.format = function(fmt)   
	{ //author: meizz   
	  var o = {   
	    "M+" : this.getMonth()+1,                 //月份   
	    "d+" : this.getDate(),                    //日   
	    "h+" : this.getHours(),                   //小时   
	    "m+" : this.getMinutes(),                 //分   
	    "s+" : this.getSeconds(),                 //秒   
	    "q+" : Math.floor((this.getMonth()+3)/3), //季度   
	    "S"  : this.getMilliseconds()             //毫秒   
	  };   
	  if(/(y+)/.test(fmt))   
	    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
	  for(var k in o)   
	    if(new RegExp("("+ k +")").test(fmt))   
	  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
	  return fmt;   
	}
	
	function number(value, row, index) {
		   var pageSize=tableObj.bootstrapTable('getOptions').pageSize;
	       var pageNumber=tableObj.bootstrapTable('getOptions').pageNumber;
	       return pageSize * (pageNumber - 1) + index + 1;
	}
	/**
	 * 对表格中每一行的数据进行相关操作
	 */
	function formaterOpt(index,row){
       var disableHtml="";
	   if(row.refundState==2){
	      disableHtml='disabled="true"';
	   }

		var html="<button onclick='app.admin.refundTable.edit("+ row.id + ")' class='btn btn-info btn-sm m-primary'"+disableHtml+" >审核</button>&nbsp;";
		html=html+"<button onclick='app.admin.refundTable.destroy("+ row.id + ")' class='btn btn-info btn-sm m-danger'"+disableHtml+">删除</button>&nbsp;";
		return html;
	}

	function formatRefundState(value){
	    if(value==1){
	        return '<span style="color:red">待审核</span>';
	    }else{
	        return '<span style="color:green">已退费</span>';
	    }
	}
	return {
		init : init,
		initDate:initDate,
		create:create,
		search:search,
		number:number,
		destroy:destroy,
		edit:edit,
		save:save,
		resetValidator:resetValidator,
		initValid:initValid,
		formaterOpt:formaterOpt,
		prompt:prompt,
		formatRefundState:formatRefundState
	}
})();
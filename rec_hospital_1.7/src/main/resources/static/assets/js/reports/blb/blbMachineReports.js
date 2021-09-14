NB.ns("app.admin").blbMachine= (function() {
	// 表格
	var tableObj = $("#blbMachineReportsDataTable");
	var baseUrl = "/admin/blb/blbSummary";
	// 请求路径
	var apiUrl = baseUrl + '/data'; 
	// 表单
	var formObj = $("#blbMachineReportsSearchForm");
	// 时间控件
	var datePluginId = "blbMachineReportsTime"; 
	var rangeTimeObj = $("#" + datePluginId);
	
	var summaryByPayBusinessTypeTree;
	
	var typesJSON;
	
	function reflush() {
		tableObj.bootstrapTable('refresh');
	}
	
	//导出列表
	function exportList(){
		bootbox.confirm('确定执行此操作?',function(r){
            if (r){
            	var starttime = "";
        		var endtime = "";
        		var rangeTime = rangeTimeObj.val();
        		if(rangeTime){
        			starttime = rangeTime.split("~")[0];
        			endtime = rangeTime.split("~")[1];
        		}
        		var treeName = summaryByPayBusinessTypeTree.getText;
        		var orgCode = summaryByPayBusinessTypeTree.getVal;
        		var name = starttime +"至"+ endtime+treeName+"病历本汇总报表";
        		// 获取当前打开的tag页名
        		var workSheetName = $(".main-sidebar .active .active .menu-open span").html();
            	
            	var where ="orgName="+ treeName + "&beginTime=" + starttime + "&endTime="+ endtime + "&fileName=" + name + "&workSheetName=" + workSheetName
            				+ "&orgCode=" + orgCode;
            	
        		var url = baseUrl+'/dcExcel?' + where;
        		window.location.href=url;
            }
        });
	}
	function initOrgTree(orgNo,beginTime, endTime){
		
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
           url:"/admin/organization/data",
           type:"get",
           contentType:"application/json",
           dataType:"json",
           success:function(data){
        	   
	        	////表格初始化
	       		tableObj.bootstrapTable({
	       			url : apiUrl,
	       			dataType : "json",
	       			uniqueId : "id",
	       			singleSelect : false,
	       			clickToSelect:true,
	       			pagination : false, // 是否分页
	       			sidePagination : 'server',// 选择服务端分页
	       			pageSize:40,
	       			pageList:[30,50],//分页步进值
		       	 	height:500, //给表格指定高度，就会有行冻结的效果
		       	 	fixedColumns:true, //是否开启列冻结
		       	 	fixedNumber:1, //需要冻结的列数
	       		});
        	   
        	   summaryByPayBusinessTypeTree = $("#blbMachineOrgSelect").ztreeview({
		       			name: 'name',
		       			key: 'code', 
		       			//是否
		       			clearable:true,
		                expandAll:true,
		       			data: data
		       }, setting);
        	   summaryByPayBusinessTypeTree.updateCode(data[0].id,data[0].code);
	           	if((orgNo != "" && orgNo != null && orgNo != undefined)){
					for(var i=0;i<data.length;i++){
						if(orgNo == data[i].code){
							summaryByPayBusinessTypeTree.updateCode(data[i].id,data[i].code);
						}
					}
				}
	            // 选择隐藏还是显示机构下拉选择
				var length = data.length;
				if(length && length>1){
					$("#blbMachineOrgSelect").parent().parent().parent().show();
				}else{
					$("#blbMachineOrgSelect").parent().parent().parent().hide();
				}
				
	           	var treeName = summaryByPayBusinessTypeTree.getText;
	           	setTableTitleHide(tableObj,"病历本汇总报表",beginTime, endTime,treeName);
           }
       });
	}
	
	function init(orgNo, typesJSONTemp, beginTime, endTime) {
		typesJSON = JSON.parse(typesJSONTemp);
		initDate(beginTime, endTime);
		initOrgTree(orgNo,beginTime, endTime);
	}
	
	/**
	 * 生成节流函数
	 * @param throttleDelay : ms   节流的时间限制，单位毫秒
	 * @param handle : function    超过 throttleDelay 时，所要执行的函数
	 */
	function createThrottle(throttleDelay,handle) {
	    var thenTime = new Date() ;
	    //节流
	    function throttle() {
	        var now = new Date();
	        if (now - thenTime >= throttleDelay) {
	        	handle();
	            thenTime = now;
	        }
	    }
	    return throttle;
	}
	$(window).resize(createThrottle(1500,function(event) {
		resetWidth();
	}));
	
	window.addEventListener("navAreaExpandChange",function (event) {
	    setTimeout(resetWidth,300);
	});
	
	function resetWidth(){
		tableObj.bootstrapTable('resetWidth');
	}
	
	function search(th) {
		var starttime = "";
		var endtime = "";
		var rangeTime = rangeTimeObj.val();
		if(rangeTime){
			starttime = rangeTime.split("~")[0];
			endtime = rangeTime.split("~")[1];
		}
		tableObj.bootstrapTable('refreshOptions', {
			  height:500, //给表格指定高度，就会有行冻结的效果
			  fixedColumns:true, //是否开启列冻结
			  fixedNumber:1, //需要冻结的列数
			  pageNumber:1,
			  queryParams:function(params){
				    var queryObj =formObj.serializeObject();
				    queryObj['beginTime'] = starttime;
				    queryObj['endTime'] = endtime;
	                var query = $.extend( true, params, queryObj);
	                return query;
	            },
	            onPreBody:function(data){
	            	$(th).button('loading');
	            },
	            onLoadSuccess:function(data){
	            	$(th).button("reset");
		        }
		});
		var treeName = summaryByPayBusinessTypeTree.getText;
		setTableTitleHide(tableObj,"病历本汇总报表",starttime,endtime,treeName);
	}
	
	function moneyFormat(val, row, index){
		
		if(row.businessType == "合计"){
			var html = "<span style='font-weight: bold;'>"+new Number(val).toFixed(2)+"</span>";
			return html;
		}
		return new Number(val).toFixed(2);
	}
	
	function normal(val, row, index){
		if(val == null){
			val = 0 ;
		}
		if(row.businessType == "合计"){
			var html = "<span style='font-weight: bold;'>"+val+"</span>";
			return html;
		}
		return val;
	}
	
	function formatter(val, row, index){
		if(val == "合计"){
			var html = "<span style='font-weight: bold;'>"+val+"</span>";
			return html;
		}
		return typesJSON[val];
	}
	
	/**
	* 初始化日期
	*/
	function initDate(beginTime, endTime) {
		var nowDate = new Date();
		var rangeTime = beginTime + " ~ " + endTime;
		var startLayDate = laydate.render({
			elem : '#'+ datePluginId,
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

	return {
		init : init,
		search:search,
		exportList:exportList,
		moneyFormat:moneyFormat,
		normal:normal,
		formatter:formatter
	}
})();

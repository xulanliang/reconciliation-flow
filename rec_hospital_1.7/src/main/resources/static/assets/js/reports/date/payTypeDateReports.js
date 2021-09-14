NB.ns("app.admin").ReportsPayTypeDate= (function() {
	// 表格
	var tableObj = $("#payTypeDateReportsDataTable");
	var baseUrl = "/admin/date/payType";
	// 请求路径
	var apiUrl = baseUrl + '/data'; 
	// 表单
	var formObj = $("#payTypeDateReportsSearchForm");
	// 时间控件
	var startobj = formObj.find("input[name=beginTime]");
	var endobj =   formObj.find("input[name=endTime]");
	
	var waySearch;
	
	var summaryByPayBusinessTypeTree;
	
	function reflush() {
		tableObj.bootstrapTable('refresh');
	}
	
	//导出列表
	function exportList(){
		bootbox.confirm('确定执行此操作?',function(r){
			if (r){
				var starttime = "";
				var endtime = "";
				var rangeTime = $("#payTypeDateTime").val();
				if(rangeTime){
					starttime = rangeTime.split("~")[0];
					endtime = rangeTime.split("~")[1];
				}else{
					$.NOTIFY.showError("提醒", "请先查询数据", '');
					return false;
				}
				
				var treeName = summaryByPayBusinessTypeTree.getText;
				
				var name = starttime +"至"+ endtime+treeName+"日期支付方式汇总报表";
				
				// 获取当前打开的tag页名
        		var workSheetName = $(".main-sidebar .active .active .menu-open span").html();
				
				var queryObj =formObj.serializeObject();
				var collectType = queryObj['collectType'];
				var orgCode = summaryByPayBusinessTypeTree.getVal;
				var where ="orgName="+ treeName + "&beginTime=" + starttime + "&endTime="+ endtime + "&fileName=" + name + "&workSheetName=" + workSheetName
							+ "&orgCode=" + orgCode + "&collectType=" + collectType;
				
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
        	   
        	   summaryByPayBusinessTypeTree = $("#payTypeDateOrgSelect").ztreeview({
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
					$("#payTypeDateOrgSelect").parent().parent().parent().show();
				}else{
					$("#payTypeDateOrgSelect").parent().parent().parent().hide();
				}
	           	
	           	var treeName = summaryByPayBusinessTypeTree.getText;
	           	setTableTitleHide(tableObj,"日期支付方式汇总报表",beginTime,endTime,treeName);
           }
       });
	}
	
	function init(orgNo,beginTime, endTime) {
		resetDatePicker(beginTime, endTime);
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
	
	function search(way) {
		waySearch = way;
		tableObj.bootstrapTable('refreshOptions', {
				height:500, //给表格指定高度，就会有行冻结的效果
       	 		fixedColumns:true, //是否开启列冻结
       	 		fixedNumber:1, //需要冻结的列数
       	 		pageNumber:1,
       	 		queryParams:function(params){
				    var queryObj =formObj.serializeObject();
				    
				    var rangeTime = queryObj['payTypeDateTimeRange'];
				    if(rangeTime){
				    	queryObj['beginTime'] = rangeTime.split("~")[0];
				    	queryObj['endTime'] = rangeTime.split("~")[1];
					}else{
						queryObj['beginTime'] = new Date();
						queryObj['endTime'] = new Date();
					}
				    // 三个月前
				    if("day30" == waySearch){
				    	var beginTime = getNDayBefore(30);
				    	var endTime = getNDayBefore(0);
				    	queryObj['beginTime'] = beginTime;
				    	queryObj['endTime'] = endTime;
				    // 七天前	
				    }else if ("day7" == waySearch){
				    	var beginTime = getNDayBefore(7);
				    	var endTime = getNDayBefore(0);
				    	queryObj['beginTime'] = beginTime;
				    	queryObj['endTime'] = endTime;
				    }
				    resetDatePicker(queryObj['beginTime'], queryObj['endTime']);
				    
	                var query = $.extend( true, params, queryObj);
	                return query;
	            },
	            onPreBody:function(data){
	            	$("#searchPayTypeDateReportsBtn").button('loading');
	            },
	            onLoadSuccess:function(data){
	            	$("#searchPayTypeDateReportsBtn").button("reset");
		        }
		});
		
		var treeName = summaryByPayBusinessTypeTree.getText;
		var starttime = "";
		var endtime = "";
		var rangeTime = $("#payTypeDateTime").val();
		if(rangeTime){
			starttime = rangeTime.split("~")[0];
			endtime = rangeTime.split("~")[1];
		}
		setTableTitleHide(tableObj,"日期支付方式汇总报表",starttime,endtime,treeName);
	}
	
	function otherAcount(val, row, index){
		var count;
		count = row.allAcount - row.registerAcount - row.makeAppointmentAcount - row.payAcount - row.clinicAcount - row.prepaymentForHospitalizationAcount;
		if(isNaN(count)){
			count = 0;
		}
		if(row.businessType == "合计"){
			var html = "<span style='font-weight: bold;'>"+count+"</span>";
			return html;
		}
		return count;
	}
	
	function otherAmount(val, row, index){
		var amount;
		amount = row.allAmount - row.registerAmount - row.makeAppointmentAmount - row.payAmount - row.clinicAmount - row.prepaymentForHospitalizationAmount;
		if(isNaN(amount)){
			amount = 0;
		}
		if(row.businessType == "合计"){
			var html = "<span style='font-weight: bold;'>"+new Number(amount).toFixed(2)+"</span>";
			return html;
		}
		return new Number(amount).toFixed(2);
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
	function add0OnDate(date){
		return (date<10) ? ('0'+date) : date;
	}
	// 获取三个月前的日期
	function get3MonthBefore(){
	    var resultDate,year,month,date;
	    var currDate = new Date();
	    year = currDate.getFullYear();
	    month = currDate.getMonth()+1;
	    date = currDate.getDate();
	    switch(month)
	    {
	      case 1:
	      case 2:
	      case 3:
	        month += 9;
	        year--;
	        break;
	      default:
	        month -= 3;
	        break;
	    }
	    month = (month < 10) ? ('0' + month) : month;
	    resultDate = year + '-'+month+'-'+date ;
	  return resultDate;
	}
	// 获取七天前的日期
	function getNDayBefore(day){
		var nowDate = new Date();
		var date = new Date(nowDate.getTime()-day*24*60*60*1000);
		var year = date.getFullYear();
		var month = date.getMonth()+1;
		var day = date.getDate();
		
		var result = year + "-" + month + "-" + day;
		return result;
	}
	
	function resetDatePicker(beginTime, endTime) {
		if(beginTime==null || beginTime==undefined){
			var rangeTime = $("#payTypeDateTime").val();
			if(rangeTime){
				beginTime = rangeTime.split("~")[0];
				endTime = rangeTime.split("~")[1];
			}else{
				beginTime = new Date();
				endTime = new Date();
			}
		}
		var type = formObj.find("input[name=collectType]:checked").val();
		var sDate = new Date(beginTime);
		var eDate = new Date(endTime);
		
		var fmt = "", format;
		if("years" == type){
			beginTime = sDate.getFullYear();
			endTime = eDate.getFullYear();
			fmt = "year";
			format = "yyyy";
		} else if("months" == type){
			beginTime = sDate.getFullYear() + "-" + add0OnDate(sDate.getMonth()+1);
			endTime = eDate.getFullYear() + "-" + add0OnDate(eDate.getMonth()+1);
			fmt = "month";
			format = "yyyy-MM";
		}else{
			beginTime = sDate.getFullYear() + "-" +add0OnDate (sDate.getMonth()+1) + "-" + add0OnDate(sDate.getDate());
			endTime = eDate.getFullYear() + "-" + add0OnDate(eDate.getMonth()+1) + "-" + add0OnDate(eDate.getDate());
			fmt = "date";
			format = "yyyy-MM-dd";
		}
		
		initDate(fmt, format, beginTime, endTime);
	}
	
	/**
	 * 初始化日期
	 */
	function initDate(type, format, beginTime, endTime) {

		var nowDate = new Date();
		var rangeTime = beginTime + " ~ " + endTime;
		
		var beginDivDom = $('#payTypeDateTime').parent('div');
		$('#payTypeDateTime').remove();
		beginDivDom.append('<input type="text" class="form-control" style="width: 220px;" onmousemove="this.style.cursor=\'pointer\';" id="payTypeDateTime" name="payTypeDateTimeRange" readonly=\"readonly\"/>');
		var startLayDate = laydate.render({
			elem : '#payTypeDateTime',
			btns: ['confirm'],
			theme : '#A9BCF5',
			type: type,
			value: rangeTime,
			format:format,
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
		otherAcount:otherAcount,
		otherAmount:otherAmount,
		moneyFormat:moneyFormat,
		normal:normal,
		resetDatePicker:resetDatePicker
	}
})();

NB.ns("app.admin").startPage = (function() {
	var apiUrl = '/admin/start/startPage/data';
	var startPageObj = $("#startPage");
	var dateObj = startPageObj.find("div[name=selectDate]");
	var orgTree;
	var clickPageData = {
			"交易明细查询" : "/admin/tradeDetail",
			"退款管理":"/admin/refund",
			"电子对账":"/admin/electronic/index"
		};
	var currentDate ;
	var interval;
	var initTime=null;
	// 定时对账时间
	var autoRecJobTime = startPageObj.find("input[name=autoRecJobTime]").val();
	// 短款长款汇总-饼图
	var expSumPieChart; 
	// 业务汇总-折线图
	var bussinessOf7DayLineChart;
	// 7天收入汇总柱状图
	var payTypeOf7DayEchart;
	
	/**
	 * 方法34
	 * 初始化方法
	 */
	function init() {
		// 初始化机构
		initOrg();
		// 重置图表，七天报表
		initResetCharts();
		// 是否显示倒计时
		setTimmerIsShow();
		// 倒计时事件
		initDownTime();
	}
	
	/**
	 * 方法33
	 * 清除当日数据
	 */
	function clearDateData() {
		// 清楚当日汇总
		startPageObj.find("[name='third']").html('');
		startPageObj.find("[name='diffAmount']").html('');
		startPageObj.find("[name='his']").html('');
		startPageObj.find("[name='settlement']").html('');
		
		// 清除异常账单
		startPageObj.find("[name='his_trade_amount']").html('');
		startPageObj.find("[name='his_trade_acount']").html('');
		startPageObj.find("[name='thrid_trade_amount']").html('');
		startPageObj.find("[name='thrid_trade_acount']").html('');
	}
	
	/**
	 * 方法32
	 * 设置倒计时是否显示
	 */
	function setTimmerIsShow() {
		var downSecond = getDownTime();
		// 倒计时小于0，不用显示倒计时
		if (downSecond <= 0) {
			startPageObj.find("[name='welcomeExeDiv']").css('display', 'block');
			startPageObj.find("[name='welcomeDownTime']").css('display', 'none');
		}else{
			clearDateData();
			startPageObj.find("[name='welcomeExeDiv']").css('display','none');
			startPageObj.find("[name='welcomeDownTime']").css('display','block');
		}
	}
	
	/**
	 * 方法31
	 * 根据字符串获取时间
	 */ 
	function getDate(strDate) {    
        var date = eval('new Date(' + strDate.replace(/\d+(?=-[^-]+$)/,    
        function (a) { return parseInt(a, 10) - 1; }).match(/\d+/g) + ')');    
        return date;
	}
	
	/**
	 * 方法30
	 * 获取年-月-日格式日期字符串
	 */
	function getDateStr(date){
		var month = date.getMonth() + 1;
		var day = date.getDate();
		if(month < 10){
			month = "0"+month;
		}
		if(day < 10){
			day = "0"+day;
		}
		var dateStr = date.getFullYear() + '-' + month + '-' + day ;
		return dateStr;
	}
	
	/**
	 * 方法29
	 * 修改日期
	 */
	function chanageDateToStr(date){
		var month = date.getMonth() + 1;
		var day = date.getDate();
		if(month < 10){
			month = "0"+month;
		}
		if(day < 10){
			day = "0"+day;
		}
		return date.getFullYear() + '-' + month + '-' + day ;
	}
	
	/**
	 * 方法28
	 * 页面跳转
	 */
	function handlerPageLink(clickName,params) {
		var orgNo = orgTree.getVal;
		var url = clickPageData[clickName];
		if((orgNo != "" && orgNo != null && orgNo != undefined)){
			url = url + "?orgNo=" + orgNo ;
		}
		if(params != null){
			for(x in params){
				key = x;
				value = params[key];
				url = url + "&"+key+"=" + value;
			}
		}
		addTab(clickName, url);
	}
	
	/**
	 * 方法27
	 * 跳转退费管理
	 */
	function handlerPageLinkRefund(clickName,state){
		var params;
		params = {"date":dateList[index],"state":state};
		handlerPageLink(clickName,params);
	}
	
	/**
	 * 方法26
	 * 跳转到异常订单
	 */
	function handlerPageLinkOrderExcetion(clickName){
		var params;
		params = {"date":dateList[index], "Order_State":"1809300"};
		handlerPageLink(clickName,params);
	}
	
	/**
	 * 方法25
	 * 电子对账跳转
	 */
	function handlerPageLinkToElectronic(clickName){
		var params;
		params = {"date":dateList[index]};
		handlerPageLink(clickName,params);
	}
	
	/** 
	 * 方法24
	 * 添加页签 
	 */
	function addTab(title, url){
		var nthTabsObj= $.AdminLTE.sidebar.nthTabs;
  	  	var temTab=$.AdminLTE.sidebar.findTab(title);
  	  	//创建新的选项卡
		var tabId=$.AdminLTE.sidebar.createUUID();
  	  	$.ajax({
  	  		type:"GET",
  	  		url:url,
  	  		cache:false,
  	  		dataType:"html",
  	  		success:function (html){
	  	  		// 限制多次点击出现多个页签的缺陷
  	  			var temTabNew = $.AdminLTE.sidebar.findTab(title);
				if (temTabNew) {
					nthTabsObj.setActTab(temTabNew.id);
					setTimeout(function() {
						nthTabsObj.locationTab();
					}, 1);
					return;
				}
	  	  		if(temTab){
	  	  			nthTabsObj.delTab(temTab.id);
	  	  	  	}
	  	  		nthTabsObj.addTab({
	  	  			id:tabId,
	  	  			title: title,
	  	  			content:html
	  	  		});
	  	  		$.AdminLTE.contentHeight.resize();
	  	  		nthTabsObj.setActTab(tabId);
	  	  		setTimeout(function () {
	  	  			nthTabsObj.locationTab();
	  	  		},1);
  	  		},
  	  		error:function(XMLHttpRequest, textStatus, errorThrown){
  	  			bootbox.alert("服务器错误,请联系管理员!");
  	  		}
  	  	});
	}
	
	/**
	 * 生成节流函数
	 * @param throttleDelay : ms   节流的时间限制，单位毫秒
	 * @param handle : function    超过 throttleDelay 时，所要执行的函数
	 */
	function createThrottle(throttleDelay,handle) {
	    var thenTime = new Date() ;
	    //节流
	    function throttle(event) {
	        var now = new Date();
	        if (now - thenTime >= throttleDelay) {
	            handle(event);
	            thenTime = now;
	        }
	    }
	    return throttle;
	}
	
	/**
	 * 方法23
	 * 报表样式监听事件，监听浏览器窗口大小变化事件
	 */
	function initResetCharts(){
		//重置echarts的尺寸
		var echartDomSelectorList = ["#bussinessOf7DayLineChart","#payTypeOf7Day"];
		// 浏览器窗口大小变化事件
		$(window).resize(createThrottle(30,function() {
			var activedTitle = $('.content-tabs-container').find('.active').find('span').html();
			if('首页' != activedTitle){
				return;
			}
			var echartJQDomList = echartDomSelectorList.map(function (selector) {
		        return $(selector);
		    });
		    echartJQDomList.forEach(function (jqDoms, index, array) {
		        jqDoms.each(function(index, echartDom){
		            var echartInst = echarts.getInstanceByDom(echartDom);
		            echartInst.resize();
		        });
		    });
		}));
		
		// 导航菜单缩进展开事件
		window.addEventListener("navAreaExpandChange",function (event) {
		    setTimeout(resizeEcharts,30);
		});
		function resizeEcharts() {
		    var echartJQDomList = echartDomSelectorList.map(function (selector) {
		        return $(selector);
		    });
		    echartJQDomList.forEach(function (jqDoms, index, array) {
		        jqDoms.each(function(index, echartDom){
		            var echartInst = echarts.getInstanceByDom(echartDom);
		            echartInst.resize();
		        });
		    });
		}
	}
	
	/**
	 * 方法22
	 * 判断字典是否包含
	 */
	function isHave(value , array){
		for(var i=0 ;i < array.length;i++){
			if(array[i] == value){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 方法21
	 * 初始化定时器
	 */
	function initDownTime() {
		clearInterval(interval);
		var dSecond = getDownTime();
		if(dSecond>0){
			interval = setInterval(function() {
				var dSecond = getDownTime();
//				console.log(dSecond,interval);
				if(dSecond<=0){
					clearInterval(interval);
					startPageObj.find("[name='welcomeExeDiv']").css('display','block');
					startPageObj.find("[name='welcomeDownTime']").css('display','none');
					loadDateData(orgTree.getVal, dateList[index]);
				}
			}, 1000);
		}
	}

	/**
	 * 方法20
	 * 构造倒计时
	 */
	function getDownTime() {
		// 创建时间（现在）
		var nowDate = new Date();
		//判断是否去当前时间
		var nextDateStr = '';
		if(index == 0){
			nextDateStr = getDateStr(nowDate);
		}else {
			nextDateStr = dateList[index];
		}
		// 2018年时间对象
		var nextDate = getDate(nextDateStr + " " + autoRecJobTime);
		// 获取两个时间点 距离1970.0.1的时间（毫秒数）
		var nowTime = nowDate.getTime();
		var nextTime = nextDate.getTime();
		// 根据差值可以计算出 现在距离2018年的毫秒数 进而计算出秒数（毫秒数/1000）
		var dSecond = parseInt((nextTime - nowTime) / 1000);
		// 通过现在距离2018年的秒数求出天数（秒数/24*60*60）
		var dDay = parseInt(dSecond / (24 * 60 * 60));
		// 通过现在距离2018年的秒数取余 求出 去掉天数剩下的秒数
		var reSecond = dSecond % (24 * 60 * 60);
		// 通过计算完剩下的秒数  求出小时数
		var dHour = parseInt(reSecond / 3600);
		// 通过计算小时 剩下的秒数 求分钟数
		var reSecond1 = reSecond % 3600;
		var dMinute = parseInt(reSecond1 / 60);
		// 通过计算分钟数 剩下的秒数  就是我们想要的描述
		var nowSecond = reSecond1 % 60;
		// 赋值
		startPageObj.find("[name='welcomeHour']").html(dHour);
		startPageObj.find("[name='welcomeMin']").html(dMinute);
		startPageObj.find("[name='welcomeSecond']").html(nowSecond);
		return dSecond;
	}
	

	/**
	 * 方法19
	 * index 默认值是0
	 * 下一个日期
	 */
	function nextDate(val){
		var isLoad = true;
		if(val>0){
			index--;
		}else{
			index++;
		}
		if(index<0){
			isLoad = false;
			index=0;
		}
		if(index>6){
			isLoad = false;
			index=6;
		}
		$("#dateValue").text(dateList[index]);
		if(isLoad){
			// 只有当日才需要倒计时
			if(index == 0){
				// 倒计时事件
				initDownTime();
			}
			//当日变昨日和昨日变当日需要切换显示事件
			if(index <= 1){
				// 倒计时事件
				setTimmerIsShow();
			}
			var dSecond = getDownTime();
			if((index == 0 && dSecond <= 0) || (val == 1 && index > 0) || (val ==-1 && index >= 1)){
				// 重新渲染日期页面
	  	  		loadDateData(orgTree.getVal,dateList[index]);
			}
		}
	}
	
	/**
	 * 方法18
	 * 获取支付来源明细
	 */
	function setValueOfExceptionOrdersTitle(data) {
		var payTypeExceptionDialog = startPageObj.find("[name=payTypeExceptionDialog]");
		// 构造数据
		var length = data.length;
		if(length == undefined || length<1) {
			payTypeExceptionDialog.attr("data-original-title" , "");
		}else {
			var html = "<span  class='pay-list'>";
			for(var i=0; i<length; i++){
				var reclogDetail = data[i];
				var recState = reclogDetail.recState;
				var pngName = "";
				// 日志状态：70-异常，71-正常
				if(recState == 71 || recState == "71"){
					pngName = "icon-finish07";
				}else if(recState == 70 || recState == "70"){
					pngName = "icon-error08";
				}
				html = html+"<span class='item' > " +
				"<span class='icon-bg' style='background-image: url(/assets/img/home/"+pngName+".png);' ></span>" +
				"<span class='title color-main'>"+metaData[reclogDetail.payType]+"</span>" +
				"</span>";
			}
			html = html + "</span>";
			payTypeExceptionDialog.attr("data-original-title" , html);
			// 设置提示模版
			setTitle(payTypeExceptionDialog);
		}
	}
	
	/**
	 * 方法17
	 * 异常账单汇总
	 */
	function setValueOfExceptionOrders(data) {
	    var hisTradeAccount = data.hisTradeAccount;
	    var hisTradeAmount = data.hisTradeAmount;
	    var thridTradeAccount = data.thridTradeAccount;
	    var thridTradeAmount = data.thridTradeAmount;
		startPageObj.find("[name='his_trade_amount']").html(f_formate_data(hisTradeAmount));
		startPageObj.find("[name='his_trade_acount']").html(hisTradeAccount);
		startPageObj.find("[name='thrid_trade_amount']").html(f_formate_data(thridTradeAmount));
		startPageObj.find("[name='thrid_trade_acount']").html(thridTradeAccount);
		// 设置异常账单明细
		var billParseList = data.billParseList;
		setValueOfExceptionOrdersTitle(billParseList);
	}
	
	/**
	 * 方法16
	 * 财务情况设置title
	 */
	function setTitle(select){
		select.tooltip({
	        html:true,
	        template:'<div class="tooltip home-tip" role="tooltip">\n' +
	        '    <div class="tooltip-arrow"></div>\n' +
	        '    <div class="tooltip-inner">\n' +
	        '        Some tooltip text!\n' +
	        '    </div>\n' +
	        '</div>'
	    });
	}

	/**
	 * 方法15
	 * 设置收入明细
	 */
	function setValueOfRecAlldetail(data) {
		var payTypeDialog = startPageObj.find("[name=payTypeDialog]");
		// 构造数据
		if(jQuery.isEmptyObject(data)) {
			payTypeDialog.attr("data-original-title" , "");
		}else {
			var html = "<span class='info-list'> ";
			for(var key in data){
				html = html+"<span class='info-item' > " +
				"<span class='key color-th'>"+metaData[key]+"：</span>" +
				"<span class='color-main'>"+data[key]+"</span>" + 
				"</span>";
			}
			html = html + "</span>";
			payTypeDialog.attr("data-original-title" , html);
			// 设置提示模版
			setTitle(payTypeDialog);
		}
	}

	/**
	 * 方法14
	 * 处理小数，保留两位，四舍五入规则 
	 */
	function f_formate_data(num){
		if(null == num || undefined == num || num == 0){
			return '0.00';
		}
		num = Number(num);
		return num.toFixed(2);
	}
	
	/**
	 * 方法13
	 * 设置财务情况
	 */
	function setValueOfRecAllCount(data) {
	    var thirdSumAmount = data.thirdSumAmount;
	    var hisSumAmount = data.hisSumAmount;
	    var hisSettlementSumAmount = data.hisSettlementSumAmount;
	    var payTypeSumMap = data.payTypeSumMap;
		var diffAmount = data.thridTradeAmount-data.hisTradeAmount;
		startPageObj.find("[name='third']").html(f_formate_data(thirdSumAmount));
		startPageObj.find("[name='diffAmount']").html(f_formate_data(diffAmount));
		startPageObj.find("[name='his']").html(f_formate_data(hisSumAmount));
		startPageObj.find("[name='settlement']").html(f_formate_data(hisSettlementSumAmount));
		// 设置财务收支明细
		var payTypeSumMap = data.payTypeSumMap;
		setValueOfRecAlldetail(payTypeSumMap);
	}
	
	/**
	 * 方法12
	 * 设置饼图
	 */
	function setExpSumPie(data) {
		//设置饼图
		var his_trade_amount = data.hisTradeAccount;
		var thrid_trade_amount = data.thridTradeAccount;
		his_trade_amount = null == his_trade_amount?0:his_trade_amount;
		thrid_trade_amount = null == thrid_trade_amount?0:thrid_trade_amount;
		expSumPieChart.clear();
		expSumPieChart.setOption({
			tooltip: {
		        trigger: 'item',
		        formatter: "{a} <br/>{b}: {c} ({d}%)"
		    },
		    color:["#ffb43d","#ff5b5b"],
		    legend: {
		        orient: 'vertical',
		        left:"center",
		        top:185,
		        data:['短款','长款']
		    },
		    series: [
		        {
		            name:'访问来源',
		            type:'pie',
		            radius: [40, 55],
		            center:["50%",100],
		            avoidLabelOverlap: false,
		            label:{show:false},
	                hoverAnimation:false,
		            labelLine: {
		                normal: {
		                    show: false
		                }
		            },
		            data:[
		                {value:his_trade_amount, name:'短款'},
		                {value:thrid_trade_amount, name:'长款'}
		            ]
		        }
		    ]
		});
		expSumPieChart.resize();
	}
	
	/**
	 * 方法10
	 * 近七天业务汇总
	 */
	function setBussinessOf7DayLineChart(moduleList){
		// y轴
		var legendData=[];
		// x轴
		var xData=[];
		// 折线数据
		var seriesData=[];
		// x、y轴数据 
		for(var i=0 ;i < moduleList.length;i++){
			// 
			var o = moduleList[i];
			if(null != o.Trade_Date && !isHave(o.Trade_Date , xData)){
				xData.push(o.Trade_Date);
			}
			// 排重
			if(null != o.Pay_Business_Type && !isHave(metaData[o.Pay_Business_Type] , legendData)){
				legendData.push(metaData[o.Pay_Business_Type]);
			}
		}
		
		// 根据y轴构造线条数据
		for(var i=0 ;i < legendData.length;i++){
			var s = [] ;
			for(var j=0 ;j < xData.length;j++){
				var l = 0 ;
				for(var k=0;k<moduleList.length;k++){
					if(xData[j] == moduleList[k].Trade_Date 
							&& legendData[i] == metaData[moduleList[k].Pay_Business_Type]){
						l = moduleList[k].pay_amount;
					}
				}
				s.push(l);
			}
			var obj = {name:legendData[i], type:'line', data:s};
			seriesData.push(obj);
		}
		// x轴展示去掉年数据，只剩下月日
		var xNewData=[];
		for(var i=0 ;i < xData.length;i++){
			xNewData.push(xData[i].substring(5,10));
		}
		
		// 设置报表属性
		bussinessOf7DayLineChart.clear();
		bussinessOf7DayLineChart.setOption({
		    tooltip: {
		        trigger: 'axis'
		    },
		    color:["#12ce8a","#ffb43d","#699dff"],
		    legend: {
		        data:legendData,
                // left:"center",
                top:40,
		    },
		    grid: {
		        left: 50,
		        right: 50,
				top:100,
		        bottom: 40,
		        containLabel: true
		    },
		    xAxis: {
		        type: 'category',
		        boundaryGap: false,
		        data: xNewData
		    },
		    yAxis: {
		        type: 'value'
		    },
		    series: seriesData
		});
		// 重构高度
		bussinessOf7DayLineChart.resize();
	}
	
	/**
	 * 方法9
	 * 构造报表数据
	 */
	function buildPayTypeOf7DayData(moduleList){
		var columLabel = [] ;
		var columName = [] ;
		var columnValue = new Array();
		for(var i=0 ;i < moduleList.length;i++){
			var temp = moduleList[i];
			if(null != temp.pay_type && !isHave(metaData[temp.pay_type] , columLabel) 
					&& undefined != temp.pay_type){
				columLabel.push(metaData[temp.pay_type]);
			}
			if(!isHave(temp.Trade_Date , columName)){
				columName.push(temp.Trade_Date);
			}
		}
		
		// data
		for(var i=0 ;i < moduleList.length;i++){
			var temp = moduleList[i];
			for(var j=0 ;j < columnValue.length;j++){
				if(metaData[temp.pay_type] == columnValue[j].name){
					columnValue[j].data.push(temp.pay_amount);
				}
			}
		}
		
		// data
		for(var i=0;i<columLabel.length;i++) {
			var datas = [];
			for(var j=0;j<columName.length;j++) {
				var data = 0;
				for(var k=0;k<moduleList.length;k++) {
					if(metaData[moduleList[k].pay_type] == columLabel[i] 
						&& moduleList[k].Trade_Date == columName[j]) {
							data = moduleList[k].pay_amount;
					}
				}
				datas.push(data);
			}
			var obj = {name:columLabel[i],type:'bar',data : datas};
			columnValue.push(obj);
		}
		buildChart(columLabel,columName,columnValue);
	}
	
	/**
	 * 方法8
	 * 生成报表图形
	 */
	function buildChart(columLabel,columName,columnValue) {
        var xData=[];
        for(var i=0;i<columName.length;i++){
        	xData.push(columName[i].substring(5,10));
        }
        var option = {
        	color:["#12ce8a","#ffdf4a","#ffb43d","#699dff"],
		    tooltip : {
		        trigger: 'axis',
		        axisPointer : {            
		            type : 'shadow'        
		        }
		    },
		    legend: {
		        data:columLabel,
                top:40,
		    },
		    grid: {
                left: 50,
                right: 50,
                top:100,
                bottom: 40,
		        containLabel: true
		    },
		    xAxis : [
		        {
		        	min:0,
		            type : 'category',
		            data : xData
		        }
		    ],
		    yAxis : [
		        {
		        	min:0,
		            type : 'value'
		        }
		    ],
		    series : columnValue
		};
        payTypeOf7DayEchart.clear();
        payTypeOf7DayEchart.setOption(option);
		// 重构高度
        payTypeOf7DayEchart.resize();
	}
	
	
	/**
	 * 方法7
	 * 设置当日财务数据和异常账单数据
	 */
	function setDateData(data) {
		try {
			// 清除当日数据
			clearDateData();
			// 设置财务汇总、异常订单
			var dSecond = getDownTime();
			if(dSecond<=0){
				//设置财务情况
				setValueOfRecAllCount(data);
				//设置异常账单
				setValueOfExceptionOrders(data);
				// 设置饼图
				setExpSumPie(data);
			}
		} catch (e) {
			console.log('构造当日财务汇总和异常账单数据发生异常：',e);
		}
	}
	
	/**
	 * 方法6
	 * 设置订单数据
	 */
	function setOrderData(data) {
		try {
			var orderData = data.dealFollowCount;
			var count1 = 0;
			var count2 = 0;
			var count3 = 0;
			if(null != orderData){
				count1 = orderData.count1;
				count2 = orderData.count2;
				count3 = orderData.count3;
			}
			$("a[name='nodeal_count']").html(count1);
			$("a[name='wait_count']").html(count2);
			$("a[name='return_count']").html(count3);
		} catch (e) {
			console.log('构造订单数据发生异常：',e);
		}
	}
	
	/**
	 * 方法5
	 * 设置报表数据
	 */
	function setChartsData(data) {
		try {
			// 近七天收入分布情况
			var payTypeOf7Day = data.payTypeOf7Day;
			buildPayTypeOf7DayData(payTypeOf7Day);
			// 近七天业务汇总
			var hisBussinessCountOf7Day = data.hisBussinessCountOf7Day;
			setBussinessOf7DayLineChart(hisBussinessCountOf7Day);
		} catch (e) {
			console.log('构造报表表格发生异常：',e);
		}
	}
	
	/**
	 * 方法4
	 * 设置页面数据
	 */
	function setAllData(data) {
		// 设置财务汇总、异常订单
		setDateData(data);
		// 设置订单信息数据
		setOrderData(data);
		// 汇总报表数据
		setChartsData(data);
	}
	
	
	/**
	 * 方法3
	 * 加载指定机构和指定日期的数据
	 */
	function loadDateData(orgNo, date) {
		$.ajax({
			url : apiUrl+'/date',
			type : "get",
			data : {
				orgNo:orgNo,
				date:date
			},
			contentType : "application/json",
			dataType : "json",
			success : function(result) {
				if(result.success){
					setDateData(result.data);
				}
			}
		});
	}
	
	/**
	 * 方法2
	 * 加载指定机构和指定日期的数据
	 */
	function loadOrgAndDateData(orgNo, date) {
		$.ajax({
			url : apiUrl,
			type : "get",
			data : {
				orgNo:orgNo,
				date:date
			},
			contentType : "application/json",
			dataType : "json",
			success : function(result) {
				if(result.success){
					setAllData(result.data);
				}
			}
		});
	}
	
	/**
	 * 方法1
	 * 初始化机构初始化数据加载
	 */
	var index = 0;
	function initOrg(){
		$("#dateValue").text(dateList[index]);
		// 饼图
		expSumPieChart = echarts.init(document.getElementById("exceptionPie"));
		// 7天业务汇总折线图
		bussinessOf7DayLineChart = echarts.init(document.getElementById("bussinessOf7DayLineChart"));
		// 7天收入汇总柱状图
		payTypeOf7DayEchart = echarts.init(document.getElementById('payTypeOf7Day'));
		
		var setting = {
			view : {
				dblClickExpand : false,
				showLine : true,
				selectedMulti : false,
				fontCss : {
					fontSize : '30px'
				}
			},
			data : {
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "parent",
					rootPId : null
				}
			},
			callback: {
				onClick:function(node){
					index = 0;
					$("#dateValue").text(dateList[index]);
					// 倒计时事件
					initDownTime();
					// 倒计时事件
					setTimmerIsShow();
					loadOrgAndDateData(orgTree.getVal,dateList[index]);
				}
			}
		};
		$.ajax({
			url : "/admin/organization/data",
			type : "get",
			contentType : "application/json",
			dataType : "json",
			success : function(data) {
				//这个树形下拉会在和其同级位置生成一个默认隐藏的input输入框以提供表单数据
				orgTree = $("#welcomeOrgSelect").ztreeview({
					name : 'name',
					key : 'code',
					//是否
					clearable : true,
					expandAll : true,
					data : data
				}, setting);
				
				var length = data.length;
				if(length && length>0) {
					orgTree.updateCode(data[0].id, data[0].code);
					for(var i=0;i<data.length;i++){
						if(orgCode == data[i].code){
							orgTree.updateCode(data[i].id,data[i].code);
						}
					}
				}
				// 选择隐藏还是现实机构下拉选择
				if(length>1){
					$("#startPage .form-horizontal").show();
				}else{
					$("#startPage .form-horizontal").hide();
				}
				loadOrgAndDateData(orgTree.getVal,dateList[index]);
			}
		});
	}
	
	/** 呈现方法到页面 */
	return {
		init : init,
		handlerPageLinkToElectronic:handlerPageLinkToElectronic,
		handlerPageLinkOrderExcetion:handlerPageLinkOrderExcetion,
		handlerPageLinkRefund:handlerPageLinkRefund,
		nextDate:nextDate
	}	
})();

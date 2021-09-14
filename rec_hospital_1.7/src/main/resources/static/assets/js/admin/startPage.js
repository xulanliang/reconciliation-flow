NB.ns("app.admin").startPage = (function() {
	var apiUrl = '/admin/welcome/startPage/data';
	var startPageObj = $("#startPage");
	var startElectronBoxLine = startPageObj.find("#startElectronBoxLine");
	var startBusinessList = startPageObj.find("#startBusinessList");
	var startCashList = startPageObj.find("#startCashList");
	var startElectronBoxPlan = startPageObj.find("#startElectronBoxPlan");
	var startSecondPlan = startPageObj.find("#startSecondPlan");
	var startThirdPlan = startPageObj.find("#startThirdPlan");
	var startFourPlan = startPageObj.find("#startFourPlan");
	var startCashPlan = startPageObj.find("#startCashPlan");
	var startHealthcarePlan = startPageObj.find("#startHealthcarePlan");
	
	var orgTree;
	
	var clickPageData = {
			"交易明细查询" : "/admin/tradeDetail",
			"退款管理":"/admin/refund",
			"电子对账":"/admin/electronic/index",
			"现金对账":"/admin/cashrec/index",
			"医保对账":"/admin/reconciliation/healthAccount"
		};
	
//	var color=['#fdc07e','#26D9b5','#49bbfc','#db7cad'];
	var color=['#fdc17e','#25dab4','#49bcfc','#ec649f','#15dde7','#ff8da9','#8d91ff','#ad97fe','#ffa9b1','#b1e284','#ed9dd7'];
	var barBorderColor=['#ec639f','#26D9b5','#49bbfc'];
//	var barColor=['#ec639f99','#26D9b599','#49bbfc99'];
	var splitXLine={
        show:false,
        lineStyle: {
          type: 'solid',
          color: '#F3F8FC',//坐标分割线的颜色
          width:'1'//坐标线的宽度
        }
      };
      var axisXLine = {
        lineStyle: {
          type: 'solid',
          color: '#F3F8FC',//坐标线的颜色
          width:'1'//坐标线的宽度
        }
      };
	var axisXLabel = {
		textStyle: {
			color: '#333',//坐标值得具体的颜色
		}
	};
	// 渠道近3月收入分布图-----  X轴样式
	var axisXLabelExt = {
		textStyle: {
			color: '#333',//坐标值得具体的颜色
		},
		interval: 0,
		// rotate: 18,
		formatter: function (value) {
			var ret = "";//拼接加\n返回的类目项
			var maxLength = 5;//每项显示文字个数
			var valLength = value.length;//X轴类目项的文字个数
			var rowN = Math.ceil(valLength / maxLength); //类目项需要换行的行数
			if (rowN > 1)//如果类目项的文字大于3,
			{
				for (var i = 0; i < rowN; i++) {
					var temp = "";//每次截取的字符串
					var start = i * maxLength;//开始截取的位置
					var end = start + maxLength;//结束截取的位置
					//这里也可以加一个是否是最后一行的判断，但是不加也没有影响，那就不加吧
					temp = value.substring(start, end) + "\n";
					ret += temp; //凭借最终的字符串
				}
				return ret;
			} else {
				return value;
			}
		}
	};
      var splitYLine ={
        show:true,
        lineStyle: {
          type: 'solid',
          color: '#F3F8FC',
          width:'1'//坐标线的宽度
        }
      };
      var axisYLine = {
        lineStyle: {
          type: 'solid',
          color:'#00000000',
          width:'0'
        }
      };
	var axisYLabel = {
		textStyle: {
			color: '#333'
		},
		formatter: function (value) {
			if (Math.abs(value) > 10000 && Math.abs(value) % 10000 == 0) {
				return value / 10000 + '万';
			}
			return value + '元'
		}
	};
      
      /**
  	 * 方法1
  	 * 初始化机构初始化数据加载
  	 */
  	function initOrg(){
  		
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
  					initElectronBox();
  					loadDate(orgTree.getVal);
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
  				loadDate(orgTree.getVal);
  			}
  		});
  	}
  	/**
  	 * 获取订单信息数据
  	 */
    function initOrderInfoData(orgCode){
    	$.ajax({
  			url : apiUrl+"/orderInfo?orgCode="+orgCode,
  			type : "get",
  			dataType : "json",
  			success : function(rec) {
  				startPageObj.find('#startWanningCount').text(rec.data.warningCount);
  				startPageObj.find('#startRefundCount').text(rec.data.refundCount);
  				startPageObj.find('#startRefundTurnCount').text(rec.data.refundTurnCount);
  			}
  		});
    }
    /**
     * 获取近七天业务收入汇总数据
     */
    /*function initBusinessIncomeData(orgCode){
    	$.ajax({
  			url : apiUrl+"/businessIncomeChart?orgCode="+orgCode,
  			type : "get",
  			dataType : "json",
  			success : function(rec) {
				debugger
  				var data = rec.data;
  				if(!rec.success||!data||data.legend.length<=0){
  					showEmptyView($('.start-business-chart-box'))
  				}
  				$.each(data.legend,function(i,val){
  					data.legend[i]=metaData[val]
  				})
  				drawBar(data)
  			}
  		});
    }*/
    /**
     * 获取近近3月渠道收入汇总数据
     */
    function initBillsRelateThreeMonthsIncomeData(orgCode){
    	$.ajax({
  			url : apiUrl+"/initBillsRelateThreeMonthsIncomeData?orgCode="+orgCode,
  			type : "get",
  			dataType : "json",
  			success : function(rec) {
  				var data = rec.data;
  				if(!rec.success||!data||data.legend.length<=0){
  					showEmptyView($('.start-business-chart-box'))
  				}
  				$.each(data.xAxis,function(i,val){
  					data.xAxis[i]=metaData[val]
  				})
  				drawBar(data)
  			}
  		});
    }

    function showEmptyView(obj,showText=true,text='暂无数据'){
    	$(obj).find('.start-empty-box').remove();
    	$(obj).css('position','relative');
    	var html = '<div class="start-empty-box">'
    		+'<div class="start-empty-icon">'
    		+'<img src="assets/img/welcome/wsj_icon.png">'
    		+'</div>'
    		+(showText?('<div class="start-empty-text">'+text+'</div>'):'')
    		+'<div class="start-empty-bg"></div>'
    		+'</div>'
    		;
    	$(obj).append(html);
    }
    /**
     * 获取近七天业务收入汇总折线图
     */
    function initBusinessIncomeSummaryData(orgCode){
    	$.ajax({
    		url : apiUrl+"/businessIncomeSummary?orgCode="+orgCode,
    		type : "get",
    		dataType : "json",
    		success : function(rec) {
    			startBusinessList.html('');
    			if(!rec.success||!rec.data||rec.data.length<=0){
    				startBusinessList.hide();
    				return;
    			}
    			startBusinessList.show();
    			$.each(rec.data,function(i,val){
    				var html = '<div class="start-business-item">'
    					+'<div class="start-business-text">'+metaData[val.businessType]+'(元)</div>'
    					+'<div class="start-business-num">'+val.amount+'</div>'
    					+'</div>'
    					;
    				startBusinessList.append(html);
    			})
    		}
    	});
    }
    /**
     * 获取渠道名称分类占比
     */
    function initThridInfoData(orgCode,name){
    	$.ajax({
  			url : apiUrl+"/thridPie?orgCode="+orgCode,
  			type : "get",
  			dataType : "json",
  			success : function(rec) {
  				var data = rec.data;
  				if(!rec.success||!data||data.legend.length<=0){
  					showEmptyView($('.start-bill-source-pie-box'))
  				}
  				drawPie(data,name);
  			}
  		});
    }
    /**
     * 支付类型占比
     * @param orgCode
     */
    function initPayTypeRatioData(orgCode,name){
    	$.ajax({
  			url : apiUrl+"/payTypePie?orgCode="+orgCode,
  			type : "get",
  			dataType : "json",
  			success : function(rec) {
  				var data = rec.data;
  				if(!rec.success||!data||data.legend.length<=0){
  					showEmptyView($('.start-bill-source-pie-box'))
  				}
  				drawPie(data,name);
  			}
  		});
    }
    /**
     * 获取近七天收入分布情况折线图
     */
    function initPayTypeIncomeChartData(orgCode){
    	$.ajax({
  			url : apiUrl+"/payTypeIncomeChart?orgCode="+orgCode,
  			type : "get",
  			dataType : "json",
  			success : function(rec) {
  				$.bootstrapLoading.end();
  				var data = rec.data;
  				if(!rec.success||!data||data.legend.length<=0){
  					showEmptyView($('.start-income-chart-plan'))
  				}
  				$.each(data.legend,function(i,val){
  					data.legend[i]=metaData[val]
  				})
  				drawLine(data);
  			}
  		});
    }
    /**
     * 现金对账
     */
    function initCashInfoData(orgCode){
    	$.ajax({
  			url : apiUrl+"/cashInfo?orgCode="+orgCode,
  			type : "get",
  			dataType : "json",
  			success : function(rec) {
  				if(!rec.success||rec.data.length<=0){
  					loadCashInfo([{
  						billSource:'self',
  						hisAmount:'0.00',
  						cashAmount:'0.00',
  						diffAmount:'0.00'
  					}])
  					return;
  				}
  				startCashPlan.show();
  				loadCashInfo(rec.data);
  			}
  		});
    }
    /**
     * 渲染现金对账
     */
    function loadCashInfo(data){
    	startCashList.html('');
    	$.each(data,function(i,val){
    		if(i>=2){
    			return;
    		}
    		var html = '<div class=start-cash-item>'
    			+'<div class="start-cash-item-title">'+(metaData[val.billSource]||'')+'</div>'
    			+'<div class="start-cash-bar">'
    			+'<div class="start-cash-bar-item">'
    			+'<p class="start-cash-bar-item-label">应收金额(元)</p>'
    			+'<p class="start-cash-bar-item-money">'+val.hisAmount+'</p>'
    			+'</div>'
    			+'<div class="start-cash-bar-item">'
    			+'<p class="start-cash-bar-item-label">实收金额(元)</p>'
    			+'<p class="start-cash-bar-item-money">'+val.cashAmount+'</p>'
    			+'</div>'
    			+'<div class="start-cash-bar-item '+(val.diffAmount==0?'':'start-money-err-box')+'">'
    			+'<div class="start-cash-bar-item-label">'
    			+'<p class="start-cash-bar-item-label">差异金额(元)</p>'
    			+'<p class="start-electron-box-err">异常</p>'
    			+'</div>'
    			+'<p class="start-cash-bar-item-money">'+val.diffAmount+'</p>'
    			+'</div>'
    			+'</div>'
    			+'</div>'
    			;
    		startCashList.append(html)
    	});
    }
    /**
     * 医保对账
     */
    function initHealthcareInfoData(orgCode){
    	$.ajax({
  			url : apiUrl+"/healthcareInfo?orgCode="+orgCode,
  			type : "get",
  			dataType : "json",
  			success : function(rec) {
  				startPageObj.find('#startHealthcareAmount').text(rec.data.healthcareAmount);
  				startPageObj.find('#startHealthcareHisAmount').text(rec.data.hisAmount);
  				startPageObj.find('#startHealthcareDiffAmount').text(rec.data.diffAmount);
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
		
		// 浏览器窗口大小变化事件
		$(window).resize(createThrottle(30,function() {
//			var activedTitle = $('.content-tabs-container').find('.active').find('span').html();
//			if('首页' != activedTitle){
//				return;
//			}
			resizeEcharts();
		}));
		
		// 导航菜单缩进展开事件
		window.addEventListener("navAreaExpandChange",function (event) {
//			var activedTitle = $('.content-tabs-container').find('.active').find('span').html();
//			if('首页' != activedTitle){
//				return;
//			}
		    setTimeout(resizeEcharts,300);
		});
		
	}
	function resizeEcharts() {
		//重置echarts的尺寸
		var echartDomSelectorList = [".start-electron-echarts",".start-business-income-summary-chart",".start-bill-source-pie",".start-income-chart"];
	    var echartJQDomList = echartDomSelectorList.map(function (selector) {
	        return $(selector);
	    });
	    echartJQDomList.forEach(function (jqDoms, index, array) {
	        jqDoms.each(function(index, echartDom){
	            var echartInst = echarts.getInstanceByDom(echartDom);
	            if(echartInst){
	            	echartInst.resize();
	            }
	        });
	    });
	}
    function reloadView(){
    	startPageObj.find('.start-empty-box').remove();
    	
    	startPageObj.find('#start-order-plan-count').text('0.00');
    	
		startBusinessList.html('');
		
		if(hasCashRec){
			loadCashInfo([{
					billSource:'',
					hisAmount:'0.00',
					cashAmount:'0.00',
					diffAmount:'0.00'
				}])
  		}
		if(hasHealthCareRec){
			startPageObj.find('.start-medical-item-money').text('0.00');
		}
    }
  	function loadDate(orgCode){
  		$.bootstrapLoading.start({ loadingTips: "正在处理数据，请稍候..." });
  		reloadView();
  		initElectronInfo(orgCode);
  		initElectronChartData(orgCode);
  		initOrderInfoData(orgCode);
  		// initBusinessIncomeData(orgCode);
		initBillsRelateThreeMonthsIncomeData(orgCode);
  		initBusinessIncomeSummaryData(orgCode);
  		if(this.billSources&&this.billSources.length>1){
  			startPageObj.find('.start-bill-source-pie-plan .start-title').text('渠道名称分类占比');
  			initThridInfoData(orgCode,'渠道名称分类占比');  			
  		}else{//只有一个渠道时 显示 支付类型分类占比
  			startPageObj.find('.start-bill-source-pie-plan .start-title').text('支付类型分类占比');
  			initPayTypeRatioData(orgCode,'支付类型分类占比')
  		}
  		initPayTypeIncomeChartData(orgCode);
  		if(hasCashRec){
  			startSecondPlan.show();
  			startCashPlan.show();
  			initCashInfoData(orgCode);
  		}
  		if(hasHealthCareRec){
  			startSecondPlan.show();
  			startHealthcarePlan.show();
  			initHealthcareInfoData(orgCode);
  		}
  	}
  	
	//设置对账时间
	function initRecJobTime(){
		// 定时对账时间
		var autoRecJobTime = startPageObj.find("input[name=autoRecJobTime]").val();
		var autoRecTimeObj = startPageObj.find(".start-reconciliation-date-item");
		var autoRecBox = startPageObj.find('.start-reconciliation-date-box');
		var autoRecArrow = startPageObj.find('.start-reconciliation-date-arrow');
		var times = autoRecJobTime.split(":");
		autoRecTimeObj.each(function(index,obj){
			if(index<times.length){
				$(obj).text(times[index]);
			}
		});
		if(isShowTimeBox){
			autoRecBox.css('top','-60px');
			autoRecBox.removeClass('start-rec-date-close');
		}else{
			autoRecBox.css('top','-145px');
			autoRecBox.addClass('start-rec-date-close');
		}
		startPageObj.find('.start-reconciliation-date-arrow-box').on('click',function(){
			if(autoRecBox.hasClass('start-rec-date-close')){
				autoRecBox.animate({top:'-60px'});
				autoRecBox.removeClass('start-rec-date-close');
			}else{
				autoRecBox.animate({top:'-145px'});	
				autoRecBox.addClass('start-rec-date-close');
			}
		})
	}
	function initElectronInfo(orgCode){
		$.ajax({
  			url : apiUrl+"/electronInfo?orgCode="+orgCode,
  			type : "get",
  			dataType : "json",
  			success : function(data) {
  				if(!data.success||data.data.length<=0){
  					return;
  				}
  				startElectronBoxPlan.show();
  				loadElectronInfoView(data.data);
  			}
  		});
	}
	/**
	 * 获取渠道折线图数据
	 */
	function initElectronChartData(orgCode){
		$.ajax({
  			url : apiUrl+"/billSourceLine?orgCode="+orgCode,
  			type : "get",
  			dataType : "json",
  			success : function(data) {
  				loadElectronChart(data.data);
  			}
  		});
	}
	/**
	 * 渲染渠道应收金额折线图
	 */
	function loadElectronChart(data){
		var billSources = data.legend;
		var xAxis = data.xAxis;
		var series = data.series;
		$.each(billSources,function(i,val){
//			if(i>=3){
//				return;
//			}
			initElectronChart('startElectronEcharts_'+val,color[i],color[i]+'E5',metaData[val],xAxis,series[i])
		})
	}
	function loadElectronInfoView(data){
		$.each(data,function(i,val){
			var obj = startPageObj.find('#startElectronBox_'+val.billSource);
			obj.find('.start-electron-box-up .start-electron-box-money').text(val.hisAmount);
			obj.find('.start-electron-box-up .start-electron-box-count').text(val.hisCount);
			obj.find('.start-electron-box-down-item:first-child .start-electron-box-money').text(val.thridAmount);
			obj.find('.start-electron-box-down-item:first-child .start-electron-box-count').text(val.thridCount);
			obj.find('.start-diff-money-box .start-electron-box-money').text(val.diffAmount);
			obj.find('.start-diff-money-box .start-electron-box-count').text(val.diffCount);
			if(val.diffCount==0&&val.diffAmount==0){
				obj.find('.start-diff-money-box').removeClass('start-money-err-box');
			}else{
				obj.find('.start-diff-money-box').addClass('start-money-err-box');				
			}
		})
		
	}
	function initElectronChart(id,color,colorArea,legend,xAxis,series){
		var chartObj = startPageObj.find('#'+id);
		if(!chartObj){
			return;
		}
		var myChart = echarts.init(chartObj[0]);
		myChart.setOption({
			color:[color],
			tooltip : {
				show:true,
				trigger: 'axis',
				formatter:function(params)  
		        {
				   if(!params||params.length<=0){
					   return '';
				   }
		           var relVal = params[0].name;  
		           for (var i = 0, l = params.length; i < l; i++) {  
		                relVal += '<br/>' +params[i].marker + params[i].seriesName + ' : ' + formatMoney(params[i].value)+"元";  
		           }  
		           return relVal;  
		        },
		        position:function(point,params,dom,rect,size){   //其中p为当前鼠标的位置
		        	var contentSize = size.contentSize
		        	var viewSize = size.viewSize
		        	var x = point[0]+10;
		        	var y = point[1]+10;
		        	if(point[0]>(viewSize[0]-contentSize[0])){
		        		x = viewSize[0] - contentSize[0];
		        	}
		        	if(point[0]>contentSize[0]){
		        		x = point[0]-contentSize[0]
		        	}
		        	if(point[1]>contentSize[1]){
		        		y = point[1]-contentSize[1] -10
		        	}
		            return [x, y];
		        }
			},
			toolbox:{
				show:false
			},
			grid:{
				x: 0,
				y: 0,
				x2: 0,
				y2: 0,
			},
			legend: {
				show:false,
			},
			xAxis : {
				boundaryGap:false,
				data: xAxis,
		        axisLine:{       //y轴
		          show:false,
		          lineStyle: {
                      type: 'solid',
                      color: '#fff',//左边线的颜色
                      width:'0'//坐标线的宽度
                  }
		        },
		        splitLine: {     //网格线
		          show: false
		        }
			},
			yAxis: {
				type : 'value',
				axisLine:{       //y轴
		          show:false
		        },
		        axisTick:{       //y轴刻度线
		          show:false
		        },
		        splitLine: {     //网格线
		          show: false
		        }
			},
			series : [
				{
					name:legend,
					type:'line',
					areaStyle: {normal: {
						color:new echarts.graphic.LinearGradient(
				                0, 0, 0, 1,
				                [
				                    {offset: 0, color: colorArea},
				                    {offset: 1, color: '#ffffffE5'}
				                ]
				            )
					}},
					data:series,
					smooth:true,
					symbol: "none",
				}
			]
		});
	}
	function drawBar(data){
        // 基于准备好的dom，初始化echarts实例
		var chartObj = startPageObj.find('.start-business-income-summary-chart')[0];
		var myChart = echarts.init(chartObj);
		
		var series = [];
		$.each(data.series,function(i,val){
			series.push({
				name:data.legend[i],
				type: 'bar',
				data:val,
				barGap:0.3,
	            barWidth : 10,
	            itemStyle: {
	              emphasis: {
	                barBorderRadius: 17
	              },
	              normal: {
//	                barBorderRadius: [100, 100, 0, 0]
	            	  color:barBorderColor[i]+'99',
	            	  barBorderWidth: 1,
	            	  barBorderColor: barBorderColor[i]
	              }
	            }
			})
		})
		
        // 绘制图表
        myChart.setOption({
          title: { show:false,text: '' },
          tooltip: {
            show:true,
            trigger: 'axis',
            formatter:function(params)  
	        {
			   if(!params||params.length<=0){
				   return '';
			   }
	           var relVal = params[0].name;  
	           for (var i = 0, l = params.length; i < l; i++) {  
	                relVal += '<br/>' +params[i].marker + params[i].seriesName + ' : ' +formatMoney(params[i].value)+"元";  
	           }  
	           return relVal;  
	        }
          },
		  toolbox:{
			show:false
		  },
		  grid:{
			x: 80,
			y: 80,
			x2: 40,
			y2: 60,
		  },
          color:barBorderColor,
          legend: {
        	orient: 'horizontal',
            icon:'circle',
            data:data.legend,
            itemWidth:20,
            itemHeight:8,
            left:'center',
            top:'20',
            itemGap:20,
            textStyle:{
              fontSize:14,
              color:'#333'
            }
          },
          xAxis: {
            data: data.xAxis,
            splitLine:splitXLine,
            axisLine:axisXLine,
            axisLabel:axisXLabelExt
          },
          yAxis: {
            splitLine:splitYLine,
            axisLine:axisYLine,
            axisLabel:axisYLabel
          },
          series:series
        });
      }
	function drawLine(data){
		var chartObj = startPageObj.find('.start-income-chart')[0];
		var myChart = echarts.init(chartObj);
		var series = [];
		$.each(data.series,function(i,val){
			series.push({
				name:data.legend[i],
				type:'line',
				areaStyle: {normal: {
					color: color[i]+'4c' //改变区域颜色
				}},
				data:val,
				smooth:true,
				symbolSize:6
//				symbol: "none",
			})
		})
		
		myChart.setOption({
			color:color,
			tooltip : {
				show:true,
				trigger: 'axis',
				formatter:function(params)  
		        {
				   if(!params||params.length<=0){
					   return '';
				   }
		           var relVal = params[0].name;  
		           for (var i = 0, l = params.length; i < l; i++) {  
		                relVal += '<br/>' +params[i].marker + params[i].seriesName + ' : ' + formatMoney(params[i].value)+"元";  
		           }  
		           return relVal;  
		        }
			},
			toolbox:{
				show:false
			},
			grid:{
				x: 80,
				y: 60,
				x2: 40,
				y2: 140,
			},
			legend: {
				icon:'circle',
				data:data.legend,
				itemWidth:20,
				itemHeight:8,
				x:'center',
				y:'bottom',
				top:'360',
				padding:20,
				itemGap:20,
				textStyle:{
				  fontSize:14,
				  color:'#333'
				}
			},
			xAxis : [
				{
					boundaryGap:false,
					data: data.xAxis,
					splitLine:splitXLine,
					axisLine:axisXLine,
					axisLabel:axisXLabel,
				},
			],
			yAxis: {
				type : 'value',
				splitLine:splitYLine,
				axisLine:axisYLine,
				axisLabel:{
					textStyle: {
						color: '#333'
					},
					margin: 30,
					formatter: function (value) {
						if (Math.abs(value) > 10000 && Math.abs(value) % 10000 == 0) {
							return value / 10000 + '万';
						}
						return value + '元'
					}
				}
			},
			series : series
		});
	  }
	  function drawPie(data,name){
		var chartObj = startPageObj.find('.start-bill-source-pie')[0];
		var myChart = echarts.init(chartObj);
		var sum = 0;
		$.each(data.data,function(i,val){
			sum+=parseFloat(val.value)
		})
		myChart.setOption({
			color:color,
			title:{
				text: recDate,
				left:'center',
				top:'20',
				textStyle:{
			        //文字颜色
			        color:'#7f828f',
			        //字体风格,'normal','italic','oblique'
			        fontStyle:'normal',
			        //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
			        fontWeight:'normal',
			        //字体系列
//			        fontFamily:'PingFang-SC-Medium',
			        //字体大小
			　　　　	fontSize:18
			    }
			},
			toolbox:{
				show:false
			},
			legend: {
				icon:'circle',
				data:data.legend,
				itemWidth:20,
				itemHeight:8,
				x:'center',
				y:'310',
				padding:15,
				itemGap:5,
				textStyle:{
				   fontSize:14,
				   color:'#333'
				},
				formatter:function(name){
					var ratio = '0.00%';
					if(sum!=0){
						$.each(data.data,function(i,val){
							if(val.name==name){
								ratio=(parseFloat(val.value)/sum*100).toFixed(2)+"%"
							}
						})
					}
//					return name+' ('+ratio+')';
					return name
				}
			},
			tooltip: {
				show:true,
				trigger: 'item',
				formatter: function(params){
					return params.seriesName+'<br/>'+params.marker+params.name+' : '+formatMoney(params.value)+'(元) ('+params.percent+'%)'
//					return "{a} <br/>{b} : {c}(元) ({d}%)"
				}
			},
			series : [
			{
				name: name,
				type: 'pie',
				radius : '60%',
				center: ['50%', '40%'],
				startAngle:75,
				minAngle:5,
				data:data.data,
				label: {
					normal: {
						show: false,
						position: 'right',
					},
				},
				itemStyle: {
					
				}
			}
		]
		});
	  }
	function toNewTab(title,params){
		var url = clickPageData[title];
		var orgNo = orgTree.getVal;
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
		if(title&&url){
			addTab(title,url);
		}
	}
	/** 
	 * 方法24
	 * 添加页签 
	 */
	function addTab(title, url){
		$.AdminLTE.sidebar.openTab(title,url);
		return;
		var nthTabsObj= $.AdminLTE.sidebar.nthTabs;
  	  	//创建新的选项卡
		var tabId=$.AdminLTE.sidebar.createUUID();
  	  	$.ajax({
  	  		type:"GET",
  	  		url:url,
  	  		cache:false,
  	  		dataType:"html",
  	  		success:function (html){
//	  	  		// 限制多次点击出现多个页签的缺陷
//  	  			var temTabNew = $.AdminLTE.sidebar.findTab(title);
//				if (temTabNew) {
//					nthTabsObj.setActTab(temTabNew.id);
//					setTimeout(function() {
//						nthTabsObj.locationTab();
//					}, 1);
//					return;
//				}
  	  			var temTab=$.AdminLTE.sidebar.findTab(title);
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
	function initView(){
		if(hasCashRec&&hasHealthCareRec){
			startCashPlan.addClass('start-has-healthcare');
		}else{
			startCashPlan.removeClass('start-has-healthcare');
		}
		if(billSources.length>3){
			$('#startElectronBoxBar').addClass('start-page-more-bill-source');
		}
		$('#startElectronBoxBar').on('slid.bs.carousel', function () {
			resizeEcharts();
			if($('#startElectronBoxBar').find('.item:first-child').hasClass('active')){
				$('.start-electron-btn-left').addClass('disabled');
			}else{
				$('.start-electron-btn-left').removeClass('disabled');				
			}
			if($('#startElectronBoxBar').find('.item:last-child').hasClass('active')){
				$('.start-electron-btn-right').addClass('disabled');
			}else{
				$('.start-electron-btn-right').removeClass('disabled');
			}
		})
		startPageObj.find('.start-bill-date').text(recDate);
		startPageObj.find('#startMoreElectron').on('click',function(){
			var name = $(this).data('name');
			toNewTab(name,{date:recDateStr})
		})
		startPageObj.find('#startElectronBoxLine').on('click',function(){
			toNewTab("电子对账",{date:recDateStr})
		})
		startPageObj.find('#startMoreCash').on('click',function(){
			var name = $(this).data('name');
			toNewTab(name,{startDate:recDateStr,endDate:recDateStr})
		})
		startPageObj.find('#startMoreHealthCare').on('click',function(){
			var name = $(this).data('name');
			toNewTab(name,{payDate:recDateStr})
		})
		startPageObj.find('.start-refund-plan-btn').on('click',function(){
			var name = $(this).data('name');
			var state = $(this).data('state');
			toNewTab(name,{state:state,date:recDateStr})
		})
		startPageObj.find('#startOrderWarnningBtn').on('click',function(){
			var name = $(this).data('name');
			var state = $(this).data('state');
			toNewTab(name,{"Order_State":"1809300",date:recDateStr})
		})
	}
	function initElectronBox(){
		startElectronBoxLine.html("");
		var html = "";
		$.each(billSources,function(i,val){
			if(i%3==0){
				html +="<div class='item'><div class='start-electron-box-line'>";
			}
			html += '<div class="start-electron-box" id="startElectronBox_'+val.value+'">'
					+'<div class="start-electron-box-up">'
					+'<div class="start-electron-box-up-left">'
					+'<p class="start-electron-box-title">'+val.name+'</p>'
					+'<p class="start-electron-box-sub-title">应收金额(元)</p>'
					+'<p class="start-electron-box-money">0.00</p>'
					+'<div class="start-electron-box-count-line">'
					+'<p class="start-electron-box-count-label">笔数</p>'
					+'<p class="start-electron-box-count">0</p>'
					+'</div>'
					+'</div>'
					+'<div class="start-electron-echarts" id="startElectronEcharts_'+val.value+'"></div>'
					+'</div>'
					+'<div class="start-electron-box-down">'
					+'<div class="start-electron-box-down-item">'
					+'<p class="start-electron-box-sub-title">实收金额(元)</p>'
					+'<p class="start-electron-box-money">0.00</p>'
					+'<div class="start-electron-box-count-line">'
					+'<p class="start-electron-box-count-label">笔数</p>'
					+'<p class="start-electron-box-count">0</p>'
					+'<img class="start-diff-count-tip" src="assets/img/welcome/tx_icon.png">'
					+'</div>'
					+'</div>'
					+'<div class="start-line"></div>'
					+'<div class="start-electron-box-down-item start-diff-money-box">'
					+'<div>'
					+'<p class="start-electron-box-sub-title">差异金额(元)</p>'
					+'<p class="start-electron-box-err">异常</p>'
					+'</div>'
					+'<p class="start-electron-box-money">0.00</p>'
					+'<div class="start-electron-box-count-line">'
					+'<p class="start-electron-box-count-label">笔数</p>'
					+'<p class="start-electron-box-count">0</p>'
					+'</div>'
					+'</div>'
					+'</div>'
					+'</div>'
				;
			if(i==billSources.length-1&&i>=3){
				var n = i%3;
				for(var m=1;m<=n;m++){
					if(m==n){
						html+='<div style="margin-left:-20px;"></div>';
					}else{
						html+='<div></div>'
					}
				}
			}
			if(i%3==2||i==billSources.length-1){
				html+='</div></div>';
			}
		})
		startElectronBoxLine.append(html);
		startElectronBoxLine.find('.item:first-child').addClass('active');
		startPageObj.find('.start-diff-count-tip').tooltip({
			html:false,
			title:'实收笔数包含冲正数据',
			placement:'right',
		});
	}
	function formatMoney (num) { 
		num += '';
		if (!num.includes('.')) num += '.00';
		return num.replace(/(\d)(?=(\d{3})+\.)/g, function($0, $1) {
		  return $1 + ',';
		}).replace(/\.$/, '');
	}
	function init(){
		initOrg();
		initElectronBox();
		initRecJobTime();
		initResetCharts();
		initView();
//		initElectronChart(0,'#fdc07e','#fef3e6');
//		initElectronChart(1,'#26d9b5','#cbfff3');
//		initElectronChart(2,'#3071ea','#e0ebfe');
//		drawLine();
	}
	/** 呈现方法到页面 */
	return {
		init : init,
	}	
})();
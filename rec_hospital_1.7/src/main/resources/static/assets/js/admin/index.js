/**
 * 通用动态菜单管理 脚本，项目可以覆盖实现
 *
 */
//菜单图标目录
var iconPath = "url(../../../assets/lib/easyui1.3.6/themes/customer/menu_icon/";
NB.ns("app.admin").index = (function() {
    var timer=null;
    var selctcell=null;
    var menuData = null; // 菜单数据
    var currTab = null; // 当前tab
    function treeLoadFilter(data, parent) {
        return data;
    }
    /**
	$('body').on('contextmenu','.datagrid-view1 .datagrid-header',function(e){
		createGridHeaderContextMenu(e,'');
	});
     **/
    
    var objliSelected=null;
    function init() {
        $('#tabs').tabs();
        var curWwwPath=window.document.location.href;
        var pathName=window.document.location.pathname;
        var pos=curWwwPath.indexOf(pathName);
        var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
        jQuery.post($('#leftnavigationqd').attr("url"), {}, function(data) {
            menuData = data;
            var arrIcon = [];
            jQuery.each(data, function(i, row) {
                jQuery("#leftnavigationqd").append("<li name='mm' ><p class='normalImageP '>"+row.text+"</p></li>");
                arrIcon.push(row);
                if (arrIcon.length == data.length){
                    $("#leftnavigationqd").find("li").each(function () {
                        var index = $(this).index();
                        var pathIcon =iconPath+arrIcon[index].iconCls+'.png)';
                        $(this).find('.normalImageP').css({'background-image':pathIcon});
                        if(index%2==0){
                            $(this).addClass("normalLefttoolsqd");
                        }else{
                            $(this).addClass("normalLefttoolsqd1");
                        }
                    })
                }

            });
            var tapslierTH=null;
            $("#leftnavigationqd").find('li').each(function () {
                var index = $(this).index();
                $(this).mouseenter(function () {

                    if (objliSelected != null){
                        var  licalssName = objliSelected.attr('class');
                        objliSelected.removeClass("selectLefttoolsqd");
                        var index = objliSelected.index();
                        var pathIcon =iconPath+arrIcon[index].iconCls+'.png)';
                        objliSelected.find('p').css({'background-image':pathIcon});

                    }
                    if (liClickTag != null){
                        liClickTag.remove();

                    }
                    if (selctcell){
                        selctcell.addClass("listcellTag");
                        var index = selctcell.index();
                        var pathIcon =iconPath+arrIcon[index].iconCls+'_select.png)';
                        selctcell.find('p').css({'background-image':pathIcon});
                    }
                    var index = $(this).index();

                    $(this).addClass("selectLefttoolsqd");
                    objliSelected=$(this);
                    bindTabWithurl(data[index]);
                     if(data[index].children.length>0){
                        $('.popview_box').show();
                    var rect = document.querySelector('.selectLefttoolsqd').getBoundingClientRect();
                    var topChildUl = rect.top;
                    $('.popview_box').prepend("<div class='test-border'></div>");
                    $('.popview_box').css({'top':topChildUl+'px'});

                    var objlefttools = document.getElementById("leftNavigationToolszy").parentNode;
                    var awidth = objlefttools.style.width;
                    var widthInt = parseInt(awidth);
                    if (widthInt<180){
                        $(".popview_box").css({"left":'60px'});
                    }else {
                        $('.popview_box').css('left','180px');
                    }
                    //移除二级的所有前部分图标
                    $('.treeStyle li .tree-node').find('.tree-icon').removeClass('.tree-file .icon-security');
                    liClickTag = $('.test-border');
                    //浏览器的高度
                    var windowHeght = $(window).height()-topChildUl;
                    var contentHeght = 50*($('.treeStyle').find('li').length) ;
                    var popviewHeight = contentHeght+'px';
                    $('.popview_box').css('height',popviewHeight);
                    var a = windowHeght-contentHeght ;
                    if (a<0 ){
                        var topY =  topChildUl+a-10;
                        $('.popview_box').css('top',topY+'px');

                    }else {
                        $('.popview_box').css('bottom','');
                        $('.popview_box').css({'top':topChildUl+'px'});
                    }
                    var topPopview_box=$('.popview_box').position().top;
                    var inttop = topChildUl - topPopview_box+15;
                    $(".test-border").css({"margin-top":inttop+'px'});

                    var strAddrSelect = iconPath + arrIcon[index].iconCls+'_select.png)';
                    $(this).find('.normalImageP').css({'background-image':strAddrSelect});}
                      else if(data[index].children.length<=0){
                        $('.popview_box').hide();
                        $(this).find('.treeStyle').remove();
                    }

                })

                tapslierTH=index;
            })
            $('#leftnavigationqd ').click(function () {

                $('#leftnavigationqd').find('li').each(function () {
                    var index = $(this).index();
                    if ($(this).attr('class') == 'normalLefttoolsqd' || $(this).attr('class') == 'normalLefttoolsqd1') {
                        var strAddrSelect = iconPath + arrIcon[index].iconCls+'.png)';
                        $(this).find('p').css('background-image', strAddrSelect);
                    }
                })
            })
            var liClickTag=null;
            var expendTig=1;
            function  bindTabWithurl(data1) {
                if(data1.children && data1.children.length > 0) {
                    $('#t-tree-one').tree({
                        data: data1.children,
                        onBeforeSelect : function(node) {
                            treeLoadFilter(data,null);
                            if (currTab && currTab.tabChangeCallback) { // 当前tab存在监听逻辑？

                                return currTab.tabChangeCallback();
                            }

                        },
                        onLoadSuccess:function () {
         
                            $(this).mouseleave(function () {

                               $('.popview_box').hide();
                                $("#leftnavigationqd").find('li').removeClass("selectLefttoolsqd");
                                $('#leftnavigationqd').find('li').each(function () {
                                    var index = $(this).index();
                                    if ($(this).attr('class') == 'normalLefttoolsqd' || $(this).attr('class') == 'normalLefttoolsqd1') {
                                        var strAddrSelect = iconPath + arrIcon[index].iconCls + '.png)';
                                        $(this).find('p').css('background-image', strAddrSelect);
                                    }
                                })
                            })

                        },
                        // 选中节点事件之前的处理，如果返回false，将取消onSelect的处理
                        loadFilter : treeLoadFilter,
                        // 选中节点事件处理
                        onSelect : function(node) {
                            $('#tipfirstStyle').remove();
                            if(selctcell){
                                selctcell.removeClass("listcellTag");
                                var index = selctcell.index();
                                var pathIcon =iconPath+arrIcon[index].iconCls+'.png)';
                                selctcell.find('p').css({'background-image':pathIcon});
                            }

                            $("#leftnavigationqd").find("li").each(function () {
                                var listname = $(this).find("p").text();
                                if(listname == data1.name){
                                    $(this).addClass("listcellTag");

                                    var index =  $(this).index();
                                    var pathIcon =iconPath+arrIcon[index].iconCls+'_select.png)';
                                    $(this).find('p').css({'background-image':pathIcon});
                                    selctcell=$(this);
                                }

                            })
                            $('.popview_box').hide();
                            if (!node.url) {
                                return;
                            }

                            if(!currTab || currTab == null) {
                                currTab = $('#tabs').tabs("getTab", "欢迎使用");
                            }

                            //如果存在就设为选中
                            if($('#tabs').tabs('exists',node.text)){

                                $('#tabs').tabs('select', node.text);
                                return;
                            }
                         
                            // 如果不存在就添加新的tab.
                            $.ajax({
                                url:projectName + node.url,
                                dataType:'html',
                                success:function(html){
                                    var h = $("<div></div>");
                                    h.append(html);
                                    h.children().not("script").hide();
                                    var toolsBar=h.find(".tools_bar");
                                    $('#tabs').tabs('add', {
                                        title : node.text,
                                        content : h.html(),
                                        closable : true,
                                        iconCls : node.iconCls,
                                        tools : [{
                                            iconCls : 'icon-mini-refresh',
                                            handler : function(e) {
                                                // TODO 刷新当前tab
                                            }
                                        }]
                                    });
                                    currTab = undefined;
                                    var tab = $('#tabs').tabs("getTab",node.text);
                                    $("div",tab).show();
                                    $("div",".window").show();
                                    $('.easyui-layout',tab).layout("resize");
                                    $('.easyui-panel',tab).panel("resize");
                                    $('.easyui-accordion',tab).accordion("resize");
                                    $('.easyui-datagrid',tab).datagrid("resize");
                                    $('.easyui-treegrid',tab).treegrid("resize");
                                   // console.log("-----22222----"+tab.find('.tools_bar').height());
                                    if($(tab).find('.tools_bar').height()<59){
                                      //  alert("2222");
                                        $('.popPanel_expend',tab).remove();
                                    }else{
                                        $(tab).find('.tools_bar').css("cssText", "height:30px");
                                    }
                                    $('.easyui-datagrid',tab).datagrid("resize");
                                    $('.easyui-treegrid',tab).treegrid("resize");
                                    $('#tabs').tabs("select",node.text);
                                    var tool = h.parent();
                                }

                            });
                        }
                    });
                }
            }
            childrentag = 1;
        }, "json");

        menuNavOver();
    }

    function openPage(page) {
        var pathName=window.document.location.pathname;
        var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
        //如果存在就设为选中
        if($('#tabs').tabs('exists',page.text)){
            $('#tabs').tabs('select', page.text);
            return;
        }
        // 如果不存在就添加新的tab.
        $.ajax({
            url:projectName+page.url,
            dataType:'html',
            success:function(html){
                var h = $("<div></div>");
                h.append(html);
                h.children().not("script").hide();
                $('#tabs').tabs('add', {
                    title : page.text,
                    content : h.html(),
                    closable : true,
                    iconCls : page.iconCls,
                    tools : [{
                        iconCls : 'icon-mini-refresh',
                        handler : function(e) {
                            // TODO 刷新当前tab
                        }
                    }]
                });
                currTab = undefined;
                var tab = $('#tabs').tabs("getTab",page.text);
                $("div",tab).show();
                $("div",".window").show();
                $('.easyui-layout',tab).layout("resize");
                $('.easyui-panel',tab).panel("resize");
                $('.easyui-accordion',tab).accordion("resize");
                $('.easyui-datagrid',tab).datagrid("resize");
                $('.easyui-treegrid',tab).treegrid("resize");
                $('#tabs').tabs("select",page.text);
                console.log("-----33333----"+$(tab).find('.tools_bar').height());
                if($(tab).find('.tools_bar').height()<59){
                   
                    $('.popPanel_expend',tab).remove();
                 }else{
                     $(tab).find('.tools_bar').css("cssText", "height:30px");
                 }
            }
        });
    }
    
    function showTabByText(text) {
        if (menuData) {
            findNodeByText(menuData, text);
           // alert(_findNode);
            if (_findNode) {
                openPage(_findNode);
            }
        }
    }

    // 遍历找到最深的节点
    var _findNode = null;
    function findNodeByText(menus, text) {
        for (var i = 0; i < menus.length; i++) {
            var m = menus[i];
            if (m.text == text) {
                _findNode = m;
            }
            console.log("-----menutext:----"+m.text);
            findNodeByText(m.children, text);
        }
    }

    function setCurrentTabChangeCallback(callback) {
        if (currTab) {
            currTab.tabChangeCallback = callback;
        }
    }

    var flag1=1;
    function turnLeftState(is) {
        var objlefttools = document.getElementById("leftArrow").parentNode;
        var objmainbody = document.getElementById("mainContaintqd").parentNode;
        var objmainbody1 = document.getElementById("mainContaintqd");
        if (flag1 == 1) {
            flag1 = 0;
            $('#subWrap').layout('panel', 'west').panel('resize',{width:60});
            $('#subWrap').layout('resize');
            $(is).find('ul').each(function () {
                $(this).removeClass('normalid');
                $(this).addClass('selectlid');
            })

            // $(".test-border").remove();//css({"left":'60px'});
            $('.popview_box').hide();//css("left","70px");

        } else {
            flag1 = 1;
            $('#subWrap').layout('panel', 'west').panel('resize',{width:180});
            $('#subWrap').layout('resize');
            $(is).find('ul').each(function () {
                $(this).removeClass('selectlid');
                $(this).addClass('normalid');
            })
            // $('.test-border').remove();
            $('.popview_box').hide();

        }

    }
    
    function menuNavOver(){
    	   $("#leftArrow").mouseenter(function () {
    	        $("#leftArrow ul").find('li').each(function () {
    	            $(this).css("background-image","url(../../../assets/lib/easyui1.3.6/themes/customer/menu_icon/icon-fllow.png");
    	        })

    	    })
    	    $("#leftArrow").mouseleave(function () {
    	        $("#leftArrow ul").find('li').each(function () {
    	            $(this).css("background-image","url(../../../assets/lib/easyui1.3.6/themes/customer/menu_icon/icon-whitepoint.png");
    	        })
    	    })
    }
    
    $('#toolrecordBox1').click(function(){
		var page = {'url':'/admin/organizations','text':'机构管理'};
		openPage(page);
	})
	  $('#toolrecordBox2').click(function(){
		var page = {'url':'/admin/shopInfo','text':'商户信息管理'};
		openPage(page);
	})
	  $('#toolrecordBox3').click(function(){
		var page = {'url':'/admin/deviceInfo','text':'设备信息管理'};
		openPage(page);
	})
	  $('#toolrecordBox7').click(function(){
		var page = {'url':'/admin/trade','text':'电子明细查询'};
		openPage(page);
	})
	  $('#toolrecordBox4').click(function(){
		var page = {'url':'/admin/tradeCheck','text':'当日对账'};
		openPage(page);
	})
	  $('#toolrecordBox5').click(function(){
		var page = {'url':'/admin/reconciliation/following','text':'隔日对账'};
		openPage(page);
	})
    $('#toolrecordBox6').click(function(){
		var page = {'url':'/admin/cash','text':'现金明细查询'};
		openPage(page);
	})
	$('#toolrecordBox8').click(function(){
		var page = {'url':'/admin/cashrefund','text':'现金对账'};
		openPage(page);
	})
	
//	$('#toolrecordBox1').tooltip({
//		    position: 'top',
//		    content: '<span style="color:#fff">这是一个说明！这是一个说明！这是一个说明！这是一个说明！这是一个说明！这是一个说明！</span>',
//		    onShow: function(){
//				$(this).tooltip('tip').css({
//					backgroundColor: '#666',
//					borderColor: '#666'
//				});
//		    }
//		});

    return {
        init : init,
        setCurrentTabChangeCallback : setCurrentTabChangeCallback,
        showTabByText:showTabByText,
        turnLeftState:turnLeftState
       
    }
})();



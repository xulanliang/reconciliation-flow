/**
 * Created with JetBrains WebStorm.
 * User: cao.guanghui
 * Date: 13-6-26
 * Time: 下午11:27
 * To change this template use File | Settings | File Templates.
 * doCellTip方法的参数包含以下属性：
 * 
 * 名称	参数类型	描述以及默认值
 * onlyShowInterrupt	string	是否只有在文字被截断时才显示tip，默认值为false，即所有单元格都显示tip。
 * specialShowFields	Array	需要特殊定义显示的列，比如要求鼠标经过name列时提示standName列(可以是隐藏列)的内容,specialShowFields参数可以传入：[{field:'name',showField:'standName'}]。
 * position	string	tip的位置，可以为top,botom,right,left。
 * minWidth	string	tip的最小宽度(IE7+)。
 * maxWidth	string	tip的最大宽度(IE7+)。
 * width	string	tip的宽度，例如'200px'。
 * tipStyler	object	tip内容的样式，注意要符合jquery css函数的要求。
 * contentStyler	object	整个tip的样式，注意要符合jquery css函数的要求。
 * 
 * 
 * $('#dg').datagrid('doCellTip', {
 *         onlyShowInterrupt : onlyShowInterrupt,
 *         position : 'bottom',
 *         maxWidth : '200px',
 *         specialShowFields : [ {
 *             field : 'status',
 *             showField : 'statusDesc'
 *         } ],
 *         tipStyler : {
 *             'backgroundColor' : '#fff000',
 *             borderColor : '#ff0000',
 *             boxShadow : '1px 1px 3px #292929'
 *         }
 * 
 */
$.extend($.fn.datagrid.methods, {
    /**
     * 开打提示功能（基于1.3.3+版本）
     * @param {} jq
     * @param {} params 提示消息框的样式
     * @return {}
     */
    doCellTip:function (jq, params) {
        function showTip(showParams, td, e, dg) {
            //无文本，不提示。
            if ($(td).text() == "") return;
            params = params || {};
            var options = dg.data('datagrid');
            
            var styler = 'style="';
            if(showParams.width){
            	styler = styler + "width:" + showParams.width + ";";
            }
            if(showParams.maxWidth){
            	styler = styler + "max-width:" + showParams.maxWidth + ";";
            }
            if(showParams.minWidth){
            	styler = styler + "min-width:" + showParams.minWidth + ";";
            }
            styler = styler + '"';
            showParams.content = '<div class="tipcontent" ' + styler + '>' + showParams.content + '</div>';
            $(td).tooltip({
                content:showParams.content,
                trackMouse:true,
                position:params.position,
                onHide:function () {
                    $(this).tooltip('destroy');
                },
                onShow:function () {
                    var tip = $(this).tooltip('tip');
                    if(showParams.tipStyler){
                        tip.css(showParams.tipStyler);
                    }
                    if(showParams.contentStyler){
                        tip.find('div.tipcontent').css(showParams.contentStyler);
                    }
                }
            }).tooltip('show');
        };
        return jq.each(function () {
            var grid = $(this);
            var options = $(this).data('datagrid');
            console.log(options);
            if (!options.tooltip) {
                var panel = grid.datagrid('getPanel').panel('panel');
                panel.find('.datagrid-body').each(function () {
                    var delegateEle = $(this).find('> div.datagrid-body-inner').length ? $(this).find('> div.datagrid-body-inner')[0] : this;
                    $(delegateEle).undelegate('td', 'mouseover').undelegate('td', 'mouseout').undelegate('td', 'mousemove').delegate('td[field]', {
                        'mouseover':function (e) {
                            //if($(this).attr('field')===undefined) return;
                            var that = this;
                            var setField = null;
                            if(params.specialShowFields && params.specialShowFields.sort){
                                for(var i=0; i<params.specialShowFields.length; i++){
                                    if(params.specialShowFields[i].field == $(this).attr('field')){
                                        setField = params.specialShowFields[i];
                                    }
                                }
                            }
                            if(setField==null){
                                options.factContent = $(this).find('>div').clone().css({'margin-left':'-5000px', 'width':'auto', 'display':'inline', 'position':'absolute'}).appendTo('body');
                                var factContentWidth = options.factContent.width();
                                params.content = $(this).text();
                                if (params.onlyShowInterrupt) {
                                    if (factContentWidth > $(this).width()) {
                                        showTip(params, this, e, grid);
                                    }
                                } else {
                                    showTip(params, this, e, grid);
                                }
                            }else{
                                panel.find('.datagrid-body').each(function(){
                                    var trs = $(this).find('tr[datagrid-row-index="' + $(that).parent().attr('datagrid-row-index') + '"]');
                                    trs.each(function(){
                                        var td = $(this).find('> td[field="' + setField.showField + '"]');
                                        if(td.length){
                                            params.content = td.text();
                                        }
                                    });
                                });
                                showTip(params, this, e, grid);
                            }
                        },
                        'mouseout':function (e) {
                            if (options.factContent) {
                                options.factContent.remove();
                                options.factContent = null;
                            }
                        }
                    });
                });
            }
        });
    },
    /**
     * 关闭消息提示功能（基于1.3.3版本）
     * @param {} jq
     * @return {}
     */
    cancelCellTip:function (jq) {
        return jq.each(function () {
            var data = $(this).data('datagrid');
            if (data.factContent) {
                data.factContent.remove();
                data.factContent = null;
            }
            var panel = $(this).datagrid('getPanel').panel('panel');
            panel.find('.datagrid-body').undelegate('td', 'mouseover').undelegate('td', 'mouseout').undelegate('td', 'mousemove')
        });
    }
});
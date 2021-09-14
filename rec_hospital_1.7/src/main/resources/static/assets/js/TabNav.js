/*
插件：TabNav
作者：郭斌勇
邮箱：guobinyong@qq.com
微信：keyanzhe
QQ：guobinyong@qq.com


TabNav 实现了 tab和内容同时切换并实给tab设置相应的css类




# 使用方法
1. 给 tab的容器元素添加 `tab-nav` css类；
2. tab 元素添加 `tab-item` css类；
3. 执行 `TabNav.init(target)`  方法；





# 标签属性

## tab-nav 的标签属性
default-selected : 默认选中的 tab 索引；
active-class : 当 tabItem 被选中时，应该被设置的 css 类；
trigger : string  默认值是 click;   触发选中tabItem的事件名称；


## tab-item 的标签属性
bind-selector : 绑定的内容元素的css选择器，当该 tabItem 被选中时，该css选择器匹配到的元素会被显示，否则，会被隐藏；






# tabNav 事件
当 tab 切换时，会在 tab 的容器元素上，即 tabNav 上触发 tabChange 事件；
tabChange 事件对象 event 中有以下额外的属性：
index : 被选中的 tab 的索引
item : 被选中的 tab 的 dom 元素






# 全局对象 TabNav

引入 TabNav.js 文件后，会在全局 创建一个 TabNav 对象；该对象有以下方法：


init(target)
   在 目标 target 上初始化 TabNav
   @param target : any   可选，默认值：".tab-nav"  ；   目标对象，可以是选择器、dom、JQuery 对象 等等。


   注意：该方法支持集合操作；


   使用方式：
   ```
   TabNav.init(target)
   ```



destroy(target,clearStyle)
   销毁 目标 上的 TabNav 实例
   @param target : any   可选; 默认值：".tab-nav"  ；   目标对象，可以是选择器、dom、JQuery 对象 等等。
   @param clearStyle : boolean   可选；默认值: false ； 是否需要清除样式;
   注意：该方法支持集合操作；

   使用方式：
   ```
   TabNav.destroy(target,true)
   ```





selectItem(tabNav,selectedIndexOrDestroy,selectedItem)
   选择指定的 tabItem
   @param tabNav : SelectorString | Dom    tabNav 的 选择器 或者 Dom 元素
   @param selectedIndexOrDestroy : number | true  可选；  选中的 tabItem 的索引（从0开始） 或者 true ; 当值为 true 时，表示去除 tabNav 对 item 设置的所有样式；
   @param selectedItem : Dom     可选； 选中的 tabItem 的 Dom ;

   注意：该方法支持集合操作；

   使用方式：
   ```
   TabNav.destroy(".tab-nav",0);
   ```






# 实例方法
在 JQuery 的实例上会有以下方法：



tabNavSelectItem(indexOrItemOrDestroy : number | Dom | true)
   选择指定的 tabItem
   @param indexOrItemOrDestroy : number | Dom | true  可选；  选中的 tabItem 的索引（从0开始）或者 Dom 元素  或者 true ; 当值为 true 时，表示去除 tabNav 对 item 设置的所有样式；

   注意：该方法支持集合操作；

   使用方式：
   ```
   $(".tab-nav").tabNavSelectItem(0)
   ```


initTabNavs()
   在当前实例上（以当前实例为目标）初始化TabNavs；

   注意：该方法支持集合操作；

   使用方式：
   ```
   $(".tab-nav").initTabNavs()
   ```



destroyTabNavs(clearStyle)
   销毁 当前实例 上的 TabNav 实例
   @param target : any   可选; 默认值：".tab-nav"  ；   目标对象，可以是选择器、dom、JQuery 对象 等等。
   @param clearStyle : boolean   可选；默认值: false ； 是否需要清除样式;

   注意：该方法支持集合操作；

   使用方式：
   ```
   $(".tab-nav").destroyTabNavs()
   ```





# 使用示例

```
<ul class="tab-nav" default-selected="1" active-class="active"  trigger="mouseover" >
    <li class="tab-item" bind-selector="#div1" >tab1</li>
    <li class="tab-item" bind-selector="#div2" >tab2</li>
    <li class="tab-item" bind-selector="#div3" >tab3</li>
</ul>

<!--TabNav.js文件只要在TabNav初始化之前引用即可-->
<script src="TabNav.js"></script>

<!--initTabNavs函数需要在相应标签加入文档之后调用-->
<script>
    TabNav.init();
</script>
```

如果需要监听tab选中的事件，可以用下面的方式：

- 原生监听方式

```
var tabNav = document.getElementsByClassName("tab-nav");
tabNav.addEventListener("tabChange",function (event) {

    //在这些做你想做的任何事
    console.log("tab的索引是：",event.index,"tab的Dom元素是：",event.item);
});
```

- JQuery 的监听方式：
```
$(".tab-nav").on("tabChange",function (event) {

    //在这些做你想做的任何事
    console.log("tab的索引是：",event.index,"tab的Dom元素是：",event.item);
})
```



*/



(function($) {


    /**
     * 选择指定的 tabItem
     * @param tabNav : SelectorString | Dom    tabNav 的 选择器 或者 Dom 元素
     * @param selectedIndexOrDestroy : number | true  可选；  选中的 tabItem 的索引（从0开始） 或者 true ; 当值为 true 时，表示去除 tabNav 对 item 设置的所有样式；
     * @param selectedItem : Dom     可选； 选中的 tabItem 的 Dom ;
     *
     * 注意：该方法支持集合操作；
     */
    function selectItem(tabNav,selectedIndexOrDestroy,selectedItem) {

        var jqTabNav = $(tabNav);


        var bindSelList = [];
        var itemList = jqTabNav.find(".tab-item");
        itemList.each(function (tabItemIndex,tabItem) {
            var itemBindSel = $(tabItem).attr("bind-selector");
            bindSelList.push(itemBindSel);
        });




        var activeClass =$(tabNav).attr("active-class");
        itemList.removeClass(activeClass);


       if (selectedIndexOrDestroy === true) {
           bindSelList.forEach(function (sel,index) {
               $(sel).css("display","");
           });
           return;
       }

        bindSelList.forEach(function (sel,index) {
            $(sel).css("display","none");
        });

        var noItem = selectedItem == null ;
        var noIndex = selectedIndexOrDestroy == null ;

        if (noItem && noIndex) {
            return ;
        }else if (noItem) {
            selectedItem = itemList[selectedIndexOrDestroy] ;
        }else if (noIndex) {
            selectedIndexOrDestroy = itemList.indexOf(selectedItem)
        }


        var jqItem = $(selectedItem);
        jqItem.addClass(activeClass);

        var itemBindSel = jqItem.attr("bind-selector");
        $(itemBindSel).css("display","");




        var tabChangeEvent = $.Event("tabChange",{
            index:selectedIndexOrDestroy,
            item: selectedItem
        });
        $(tabNav).trigger(tabChangeEvent);

    }


    /**
     * 在 目标 target 上初始化 TabNav
     * @param target : any   可选，默认值：".tab-nav"  ；   目标对象，可以是选择器、dom、JQuery 对象 等等。
     *
     * 注意：该方法支持集合操作；
     */
    function initTabNavs(target){

        var tabNavs = target || ".tab-nav";

        if (!(target instanceof $)) {
            tabNavs = $(tabNavs);
        }

        tabNavs.each(function (tabIndex,tabNav) {

            var jqTabNav = $(tabNav);


            var itemList = jqTabNav.find(".tab-item");

            var defaultSelected = jqTabNav.attr("default-selected");
            defaultSelected = parseInt(defaultSelected);

            if (!isNaN(defaultSelected)) {
                selectItem(tabNav,defaultSelected);
            }



            var trigger = jqTabNav.attr("trigger") || "click";



            itemList.each(function (itemIndex,item) {

                $(item).on(trigger,function (event) {
                    selectItem(tabNav,itemIndex,event.currentTarget);
                });

            });
        });
    }


    /**
     * 销毁 目标 上的 TabNav 实例
     * @param target : any   可选; 默认值：".tab-nav"  ；   目标对象，可以是选择器、dom、JQuery 对象 等等。
     * @param clearStyle : boolean   可选；默认值: false ； 是否需要清除样式;
     *
     * 注意：该方法支持集合操作；
     */
    function destroyTabNavs(target,clearStyle){

        var tabNavs = target || ".tab-nav";

        if (!(target instanceof $)) {
            tabNavs = $(tabNavs);
        }

        tabNavs.each(function (tabIndex,tabNav) {

            if (clearStyle) {
                selectItem(tabNav,true);
            }

            var jqTabNav = $(tabNav);

            var itemList = jqTabNav.find(".tab-item");

            var trigger = jqTabNav.attr("trigger") || "click";

            itemList.off(trigger);

        });
    }





    //JQuery 实例方法

    /**
     * 选择指定的 tabItem
     * @param indexOrItemOrDestroy : number | Dom | true  可选；  选中的 tabItem 的索引（从0开始）或者 Dom 元素  或者 true ; 当值为 true 时，表示去除 tabNav 对 item 设置的所有样式；
     *
     * 注意：该方法支持集合操作；
     */
    $.fn.tabNavSelectItem = function tabNavSelectItem(indexOrItemOrDestroy) {

        var selectedIndexOrDestroy = null ;
        var selectedItem = null ;

        if (indexOrItemOrDestroy == true) {
            selectedIndexOrDestroy = true;
        }else  if (indexOrItemOrDestroy != null) {
            selectedIndexOrDestroy = parseInt(indexOrItemOrDestroy) ;

            if (isNaN(selectedIndexOrDestroy)){
                selectedIndexOrDestroy = null;
                selectedItem = indexOrItemOrDestroy ;
            }
        }



        this.each(function (index, tabNav) {
            selectItem(tabNav,selectedIndexOrDestroy,selectedItem);
        });


    }


    $.fn.initTabNavs = function (){
        initTabNavs(this);
    };


    /**
     * 销毁 当前实例 上的 TabNav 实例
     * @param target : any   可选; 默认值：".tab-nav"  ；   目标对象，可以是选择器、dom、JQuery 对象 等等。
     * @param clearStyle : boolean   可选；默认值: false ； 是否需要清除样式;
     *
     * 注意：该方法支持集合操作；
     */
    $.fn.destroyTabNavs = function (clearStyle){
        destroyTabNavs(this,clearStyle);
    };



    //全局对象
    window.TabNav = {
        init : initTabNavs,
        destroy : destroyTabNavs,
        selectItem : selectItem
    };



}(jQuery));




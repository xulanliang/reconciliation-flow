/**
 * 窗口现金核对
 * @type {{init, search, orgFormatter, formatter, exportData, number, moneyFormat, formatOpt, detail, refund, refundButton, choseColor, orderStateData, numExt}}
 */
NB.ns("app.admin").windowCashCheck = (function () {
    // 新增数据form
    var windowCashObj = $('#windowCashCheckDlg');
    //表格
    var tableObj = $("#windowCashCheckTable");
    //表单
    var formObj = $("#windowCashCheckSearchForm");
    var addRowDataButton = $("#addRowDataButton");
    var addRowDataForm = $("#addRowDataForm");

    //对话框
    var dlgObj = $('#cashCheckDedailedDlg');
    // 患者类型数据；
    var patTypeData;
    // 银行类型数据；
    var bankTypeData;

    var dlgFormObj = dlgObj.find("ul.detail");
    // 时间控件
    var rangeTimeObj = formObj.find("#cashCheckStartTime");
    var tradeTree;
    var orderStateVal;
    var systemFrom;

    //初始化表单
    function init(accountDate, date, orgCode) {
        // 初始化机构数
        formaterOrgProps(orgCode);
        // 初始化存款银行
        tradeDetailPayTypeData();
        // 初始化患者类型
        tradePayTypeData();
        // 初始化日期
        initAddRowDate();
        $("#windowCashCheckDiv .tab-nav").on("tabChange", function (event) {
            //在这些做你想做的任何事
            var name = $(event.item).attr("name");
            $('#selectType').val(name);
            //显示列表的系统来源列
            if (name == 'all') {
                tableObj.bootstrapTable("showColumn", "systemFrom")
            } else {
                tableObj.bootstrapTable("hideColumn", "systemFrom")
            }
        });
        // 初始化日期
        if (!date) {
            date = accountDate;
        }
        initDate(date, accountDate);
        var startDate = '';
        var endDate = '';
        if (date != null && date != '') {
            startDate = date + " 00:00:00";
        }
        if (date != null && date != '') {
            endDate = date + " 23:59:59";
        }
        // 存款人
        var cashierName = $("#cashierName").val();
        // 存款银行
        var bankType = $("#bankType").val();
        // 类型
        var businessType = $("#businessType").val();
        cashierName = (cashierName == '全部') ? '' : cashierName;
        bankType = (bankType == '全部') ? '' : bankType;
        businessType = (businessType == '全部') ? '' : businessType;
        tableObj.bootstrapTable({
            url: '/admin/window/data/cashCheckData',
            onPostBody: choseColor,
            dataType: "json",
            uniqueId: "id",
            resizable: true,
            singleSelect: true,
            pagination: true, // 是否分页
            sidePagination: 'server',// 选择服务端分页
            queryParams: function (params) {
                var queryObj = {
                    orgCode: orgCode,
                    startDate: startDate,
                    endDate: endDate,
                    cashierName: cashierName,
                    bankType: bankType,
                    businessType: businessType,
                };
                var query = $.extend(true, params, queryObj);
                return query;
            }
        });
    }

    // 获取机构名称
    function orgFormatter(val) {
        var org = $('#tradeOrgNo').val();
        var orgJSON = JSON.parse(org);
        return '<p data-toggle="tooltip" title=\"' + orgJSON[val] + '\">' + orgJSON[val] + '</p>'
    }

    function formatter(val) {
        for (var i = 0; i < patTypeData.length; i++) {
            var obj = patTypeData[i];
            if (obj != null && obj.id == val) {
                val = obj.name;
                break;
            }
        }
        return val;
    }

    function formatterState(val) {
        if (val == '0') {
            return '正常'
        } else if (val == '1') {
            return '异常'
        } else if (val == '2') {
            return '已通过'
        } else {
            return '未知'
        }
    }
    function formatterBankType(val) {
        for(var i =0;i<bankTypeData.length;i++){
            var obj = bankTypeData[i];
            if(obj != undefined && obj != null && obj.id == val){
                val = obj.name;
                break;
            }
        }
        return val;
    }


    //患者类型数据
    function tradePayTypeData() {
        $.ajax({
            url: "/admin/reconciliation/typeValue?typeValue=pat_code&isIncludeAll=true",
            contentType: "application/json",
            dataType: "json",
            success: function (msg) {
                changeSelectData(msg);
                // $('#businessTypeVal').val(msg);
                patTypeData = msg;
            }
        });
    }

    //导出
    function ewxportData() {
        var orgNo = tradeTree.getVal;
        if (orgNo === 9999 || orgNo === null || orgNo === '') {
            $.NOTIFY.showError("错误", '请选择所属机构!', '');
            return;
        }
        var orderState = $('#orderState').val();
        var visitNumber = $('#visitNumber').val();
        var custName = $('#custName').val();
        var patType = $('#patType').val();
        var systemFrom = $('#selectType').val();
        if (systemFrom == "all" || systemFrom == null) systemFrom = "";

        var startDate;
        var endDate;
        var rangeTime = rangeTimeObj.val();
        if (rangeTime) {
            startDate = rangeTime.split("~")[0];
            endDate = rangeTime.split("~")[1];
        } else {
            $.NOTIFY.showError("提醒", "请选择导出时间", '');
            return false;
        }
        if (orgNo == "全部" || orgNo == null) orgNo = "";
        if (patType == "全部" || patType == null) patType = "";
        if (orderState == "全部" || orderState == null) orderState = "";

        var hour = $.fn.getHour(startDate, endDate);
        if (hour > 24 * 31) {
            $.NOTIFY.showError("错误", '时间范围超过31天，请缩短时间范围', '');
            return false;
        }

        bootbox.confirm('确定执行此操作?', function (r) {
            if (r) {
                var where = 'orgCode=' + orgNo + '&orderState=' + orderState + '&visitNumber=' + visitNumber +
                    "&custName=" + custName + '&patType=' + patType + '&startDate=' + startDate + '&endDate=' + endDate + '&systemFrom=' + systemFrom;
                var url = apiUrl + '/api/dcExcel?' + where;
                window.location.href = url;
            }
        });
    }

    // 报表导出
    function exportData() {
        var orgNo = tradeTree.getVal;
        if (orgNo === 9999 || orgNo === null || orgNo === '') {
            $.NOTIFY.showError("错误", '请选择所属机构!', '');
            return;
        }
        var sortName = tableObj.bootstrapTable('getOptions').sortName;
        var sortOrder = tableObj.bootstrapTable('getOptions').sortOrder;
        // 存款人
        var cashierName = $("#cashierName").val();
        // 存款银行
        var bankType = $("#bankType").val();
        // 类型
        var businessType = $("#businessType").val();
        cashierName = (cashierName == '全部') ? '' : cashierName;
        bankType = (bankType == '全部') ? '' : bankType;
        businessType = (businessType == '全部') ? '' : businessType;
        var startDate;
        var endDate;
        var rangeTime = rangeTimeObj.val();
        if (rangeTime) {
            startDate = rangeTime.split("~")[0];
            endDate = rangeTime.split("~")[1];
        }
        if (orgNo == "全部" || orgNo == null) orgNo = "";

        bootbox.confirm('确定执行此操作?',function(r){
            if (r){
                var orgNo= tradeTree.getVal;
                var starttime = "";
                var endtime = "";
                var rangeTime = rangeTimeObj.val();
                if(rangeTime){
                    starttime = rangeTime.split("~")[0];
                    endtime = rangeTime.split("~")[1];
                }

                // orgCode: orgNo,
                // startDate: startDate,
                // endDate: endDate,
                // cashierName: cashierName,
                // bankType: bankType,
                // businessType: businessType,
                var where = '&order='+sortOrder+'&orgCode=' + orgNo + '&startDate=' + starttime + '&endDate=' + endtime + '&cashierName=' + cashierName + '&bankType=' + bankType + "&fileName=" + starttime +"至"+ endtime + "深圳市第二人名医院窗口现金核对汇总报表" + '&businessType=' + businessType  +"&workSheetName="+"窗口现金核对汇总表"+"&offset=0&limit=1000000";
                // var where = 'sort='+sortName+'&order='+sortOrder+'&orgCode=' + orgNo + '&startDate=' + starttime + '&endDate=' + endtime + '&cashierName=' + cashierName + '&bankType=' + bankType + "&fileName=" + starttime +"至"+ endtime + "深圳市第二人名医院窗口现金核对汇总报表" + '&businessType=' + businessType  +"&workSheetName="+"窗口现金核对汇总表"+"&offset=0&limit=1000000";
                var url = 'admin/window/data/exportData?' + where;
                window.location.href=url;
            }
        });
    }

    //机构树
    function formaterOrgProps(orgCode) {
        var setting = {
            view: {
                dblClickExpand: false,
                showLine: false,
                selectedMulti: false,
                fontCss: {
                    fontSize: '18px'
                }
            },
            data: {
                key: {
                    isParent: "parent",
                    title: ''
                },
                simpleData: {
                    enable: true,
                    idKey: "id",
                    pIdKey: "parent",
                    rootPId: null
                }
            }
        };
        $.ajax({
            url: "/admin/organization/data",
            type: "get",
            contentType: "application/json",
            dataType: "json",
            success: function (msg) {
                //这个树形下拉会在和其同级位置生成一个默认隐藏的input输入框以提供表单数据
                tradeTree = $("#tradeTree").ztreeview({
                    name: 'name',
                    key: 'code',
                    //是否
                    clearable: true,
                    expandAll: true,
                    data: msg
                }, setting);
                tradeTree.updateCode(msg[0].id, msg[0].code);
                if (orgCode != null && orgCode != "") {
                    for (var i = 0; i < msg.length; i++) {
                        if (orgCode == msg[i].code) {
                            tradeTree.updateCode(msg[i].id, msg[i].code);
                        }
                    }
                }
                // 选择隐藏还是现实机构下拉选择
                var length = msg.length;
                if (length && length > 1) {
                    $("#tradeTree").parent().parent().parent().show();
                } else {
                    $("#tradeTree").parent().parent().parent().hide();
                }
            }
        });
    }

    function resetValidator() {
        formObj.data('bootstrapValidator').destroy();
        formObj.data('bootstrapValidator', null);
        initValid();
    }

    function reflush() {
        tableObj.bootstrapTable('refresh');
    }

    //查询
    function search(th) {
        var orgNo = tradeTree.getVal;
        if (orgNo === 9999 || orgNo === null || orgNo === '') {
            $.NOTIFY.showError("错误", '请选择所属机构!', '');
            return;
        }
        // 存款人
        var cashierName = $("#cashierName").val();
        // 存款银行
        var bankType = $("#bankType").val();
        // 类型
        var businessType = $("#businessType").val();
        cashierName = (cashierName == '全部') ? '' : cashierName;
        bankType = (bankType == '全部') ? '' : bankType;
        businessType = (businessType == '全部') ? '' : businessType;

        var startDate;
        var endDate;
        var rangeTime = rangeTimeObj.val();
        if (rangeTime) {
            startDate = rangeTime.split("~")[0];
            endDate = rangeTime.split("~")[1];
        }
        if (orgNo == "全部" || orgNo == null) orgNo = "";
        tableObj.bootstrapTable('refreshOptions', {
            url: '/admin/window/data/cashCheckData',
            pageNumber: 1,
            onPostBody: choseColor,
            resizable: true,
            queryParams: function (params) {
                var queryObj = {
                    orgCode: orgNo,
                    startDate: startDate,
                    endDate: endDate,
                    cashierName: cashierName,
                    bankType: bankType,
                    businessType: businessType,

                };
                var query = $.extend(true, params, queryObj);
                return query;
            },
            onPreBody: function (data) {
                $(th).button('loading');
            },
            onLoadSuccess: function (data) {
                $(th).button("reset");
            }
        });
    }

    /**
     * 存款银行
     */
    function tradeDetailPayTypeData() {
        $.ajax({
            url: "/admin/reconciliation/typeValue?typeValue=Bank_Type&isIncludeAll=true",
            contentType: "application/json",
            dataType: "json",
            success: function (msg) {
                changeSelectData(msg);
                bankTypeData = msg;
                $("#bankType").select2({
                    data: msg,
                    placeholder: '==请选择类型==',
                    minimumResultsForSearch: -1,
                    allowClear: true,
                    templateResult: function (repo) {
                        return repo.name;
                    },
                    templateSelection: function (repo) {
                        return repo.name;
                    }
                });
                $("#bankTypeAddRow").select2({
                    data: msg,
                    placeholder: '==请选择类型==',
                    minimumResultsForSearch: -1,
                    allowClear: true,
                    templateResult: function (repo) {
                        return repo.name;
                    },
                    templateSelection: function (repo) {
                        return repo.name;
                    }
                });
            }
        });
    }
    /**
     * 初始化日期
     */
    function initAddRowDate() {
        var nowDate = new Date();
        var startLayDate = laydate.render({
            elem : '#cashDate',
            btns: ['confirm'],
            theme : '#A9BCF5',
            type: "datetime",
            format:"yyyy-MM-dd HH:mm:ss",
            max: nowDate.getTime()
        });
    }

    function choseColor() {
        var tt = $("table tr").find("td:eq(9)");
        if ($('#selectType').val() != null && $('#selectType').val() == "all") {
            tt = $("table tr").find("td:eq(10)");
        }
        for (var i = 0; i < tt.length; i++) {
            var text = tt[i].textContent;
            if (text == "交易异常") {
                tt[i].style = "color:#ff4949"
            } else if (text == "交易失败") {
                tt[i].style = "color:#333"
            } else if (text == "支付完成") {
                tt[i].style = "color:#12ce8a"
            } else if (text == "审核中") {
                tt[i].style = "color:#ff9c00"
            } else if (text == "已退款") {
                tt[i].style = "color:#333"
            }

        }
    }

    function number(value, row, index) {
        var pageSize = tableObj.bootstrapTable('getOptions').pageSize;
        var pageNumber = tableObj.bootstrapTable('getOptions').pageNumber;
        return pageSize * (pageNumber - 1) + index + 1;
    }

    function moneyFormat(val, row, index) {
        //处理医保金额
        if (row && row.payType == '0449') {
            return new Number(row.ybPayAmount).toFixed(2);
        }
        //自费金额
        else {
            return new Number(val).toFixed(2);
        }
    }

    //操作列按钮
    function formatOpt(index, row) {
        var hConfig = $('#hConfig').val();
        if (row.cashStatus == '1') {
            // return "<a href='javascript:;' onclick='app.admin.windowCashCheck.refund(\""+ row.id +"\",\"" + row.billSource + "\")' class='btn btn-info btn-sm m-primary '> 通过 </a>  &nbsp;" +
            return "<a href='javascript:;' onclick='app.admin.windowCashCheck.dealData(\"" + row.id + "\")' class='btn btn-info btn-sm m-primary '> 通过 </a>  &nbsp;" +
                "<a href='javascript:;' onclick='app.admin.windowCashCheck.detail(\"" + row.id + "\")' class='btn btn-info btn-sm m-primary '> 查看 </a>  &nbsp;"
        } else {
            return "<a href='javascript:;' onclick='app.admin.windowCashCheck.detail(\"" + row.id + "\")' class='btn btn-info btn-sm m-primary '> 查看 </a>  &nbsp;"
        }
    }

    //查看按钮信息赋值
    function detail(id) {
        var row = tableObj.bootstrapTable('getRowByUniqueId', id);
        row.businessType = formatter(row.businessType);
        row.bankType = formatterBankType(row.bankType);
        dlgFormObj.loadDetailReset();
        dlgFormObj.loadDetail(row);
        dlgObj.modal('show');
    }

    //详情页标题变化
    function showTitle(titleUrl, titleText) {
        dlgObj.find(".text-title").css("background-image", titleUrl);
        dlgObj.find(".abnormal-title").text(titleText);
        dlgObj.find(".title").text("");
    }

    //状态点公共方法
    function showStepBar(activeStep, dangerStep, errorMsg, val) {
        var stepsTitle = "HIS交易失败";
        if (val == "1809302") {
            stepsTitle = "HIS交易成功";
            dangerStep = "0";
        }
        $("#step-bar").html("")
        $("#step-bar").loadStep({
            //激活的步数序号
            activeStep: activeStep,
            //提醒的步数序号
            dangerStep: dangerStep,
            //byStep中包含的步骤
            steps: [{
                //步骤名称
                title: "交易创建"
            }, {
                title: "支付成功"
            }, {
                title: stepsTitle,
                content: errorMsg
            }, {
                title: "数据上送成功"
            }, {
                title: "已退款"
            }]
        });
    }

    /**
     * 初始化日期
     */
    function initDate(startDate, endDate) {
        var beginTime = "";
        var endTime = "";
        if (accountDate) {
            beginTime = startDate + " 00:00:00";
            endTime = endDate + " 23:59:59";
        }
        var nowDate = new Date();
        var rangeTime = beginTime + " ~ " + endTime;
        var startLayDate = laydate.render({
            elem: '#cashCheckStartTime',
            btns: ['confirm'],
            theme: '#A9BCF5',
            type: "datetime",
            value: rangeTime,
            format: "yyyy-MM-dd HH:mm:ss",
            range: "~",
            max: nowDate.getTime(),
            ready: function (date) {
                var layym = $(".laydate-main-list-0 .laydate-set-ym span").eq(0).attr("lay-ym").split("-")[1];
                if (layym == (nowDate.getMonth() + 1)) {
                    $(".laydate-main-list-0 .laydate-prev-m").click();
                }
            }
        });
    }

    /**
     * 添加行数据
     */
    function showAddRowDataForm() {
        // 清空表单数据
        $("#addRowDataForm input").val("");
        $("#addRowDataForm textarea").val("");
        // 初始化垫付金额
        $("#exceptionalAmount").val(0);
        windowCashObj.modal('show');
    }

    function saveRowData() {
        // 字段值校验
        // 实存金额
        var channelAmount = $("#channelAmount").val();
        if("" == channelAmount || "0" == channelAmount){
            $.NOTIFY.showError("错误", "实存金额不能为空", '');
            return;
        }
        if(channelAmount < 0){
            $.NOTIFY.showError("错误", "实存金额不能小于零", '');
            return;
        }
        // 垫付金额
        var exceptionalAmount = $("#exceptionalAmount").val();
        if("" == exceptionalAmount || "0" == exceptionalAmount){
            exceptionalAmount = 0;
        }
        if(exceptionalAmount < 0){
            $.NOTIFY.showError("错误", "垫付金额不能小于零", '');
            return;
        }
        // 存款日期
        var cashDate = $("#cashDate").val();
        if("" == cashDate){
            $.NOTIFY.showError("错误", "存款日期不能为空", '');
            return;
        }
        // 存款银行
        var bankTypeAddRow = $("#bankTypeAddRow").val();
        if("全部" == bankTypeAddRow){
            bankTypeAddRow = "";
        }
        if("" == bankTypeAddRow){
            $.NOTIFY.showError("错误", "存款银行不能为空", '');
            return;
        }
        // 类型
        var businessTypeAddRow = $("#businessTypeAddRow").val();
        if("全部" == businessTypeAddRow){
            businessTypeAddRow = "";
        }
        if("" == businessTypeAddRow){
            $.NOTIFY.showError("错误", "类型不能为空", '');
            return;
        }
        // 垫付原因
        var exceptionalReason = $("#exceptionalReason").val();
        if(exceptionalAmount > 0 && "" == exceptionalReason){
            $.NOTIFY.showError("错误", "垫付原因不能为空", '');
            return;
        }
        addRowDataButton.button('loading');
        var url = '/admin/window/data/saveCashCheckData';
        var ajax_option = {
            url : url,
            type : 'post',
            success : function(result) {
                if (JSON.parse(result).success) {
                    search();
                    windowCashObj.modal('hide');
                    $.NOTIFY.showSuccess("提醒", "处理成功", '');
                } else {
                    $.NOTIFY.showError("错误", JSON.parse(result).message, '');
                }
                addRowDataButton.button('reset');
            },
            error:function(){
                addRowDataButton.button('reset');
            },
            complete:function(){
                addRowDataButton.button('reset');
            }

        };
        addRowDataForm.ajaxSubmit(ajax_option);
    }

    /**
     * 通过操作
     */
    function dealData(id) {
        bootbox.confirm('确定通过?', function (r) {
            if (r) {
                // 调用通过方法
                $.ajax({
                    url: "/admin/window/data/updateCashCheckDataState",
                    contentType: "application/json",
                    dataType: "json",
                    data:{
                        id:id
                    },
                    success: function (msg) {
                        $.NOTIFY.showSuccess("提醒", "处理成功", '');
                        search();
                    }
                });
            }
        });
    }

    function numExt(obj) {
        obj.value = obj.value.replace(/[^\d.]/g, ""); //清除"数字"和"."以外的字符
        obj.value = obj.value.replace(/^\./g, ""); //验证第一个字符是数字
        obj.value = obj.value.replace(/\.{2,}/g, "."); //只保留第一个, 清除多余的
        obj.value = obj.value.replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
        obj.value = obj.value.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3'); //只能输入两个小数
    }

    return {
        init: init,
        search: search,
        orgFormatter: orgFormatter,
        formatter: formatter,
        exportData: exportData,
        number: number,
        moneyFormat: moneyFormat,
        formatOpt: formatOpt,
        detail: detail,
        choseColor: choseColor,
        numExt: numExt,
        showAddRowDataForm: showAddRowDataForm,
        dealData: dealData,
        formatterState: formatterState,
        saveRowData: saveRowData,
    }
})();
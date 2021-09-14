// 市二_预收款汇总表表
NB.ns("app.admin").ReportsPayBusinessType = (function () {
    // 表格
    var tableObj = $("#advancePaySumDataTable");
    var baseUrl = "admin/advance_pay/";
    // 请求路径
    var apiUrl = baseUrl + '/data';
    // 表单
    var formObj = $("#billDataReportsSearchForm");
    // 时间控件
    var rangeTimeObj = $("#billDataTime");

    var summaryByPayBusinessTypeTree;

    var advancePaySummaryDlg = $("#advancePaySummaryDlg");
    var advancePaySumDetailTable = $("#advancePaySumDetail");
    var advancePaySumDetailObj = advancePaySummaryDlg.find("li[form=advancePaySumDetailForm]");
    var billSourceObj = formObj.find("select[name=billSource]");

    function reflush() {
        tableObj.bootstrapTable('refresh');
    }

    function exportList() {
        var detailType = $("#detailType").val();
        var billSourceName = $("#billSourceName").val();
        var detailTypeName = detailType == "1" ? "增加款" : "减少款";

        bootbox.confirm('确定执行此操作?', function (r) {
            if (r) {
                /*private String orgCode;
                private String date;
                private String billSource;
                // 详情类型：1：增加款明细   0：减少款明细
                private String detailType;*/

                var orgCode = $("#orgCode").val();
                var date = rangeTimeObj.val();
                var billSource = $("#billSource").val();
                var detailType = $("#detailType").val();


                var name = date + "预收款汇总报表(" + billSourceName + ")-" + detailTypeName;
                // 获取当前打开的tag页名
                var workSheetName = "预收款汇总报表";
                /*var orgCode = summaryByPayBusinessTypeTree.getVal;*/
                var orgCode = orgCode;
                var where = "orgCode=" + orgCode + "&date=" + date + "&billSource=" + billSource + "&detailType=" +
                    detailType + "&fileName=" + name + "&workSheetName=" + workSheetName;
                var url = baseUrl + 'export?' + where;
                // window.location.href = url;
                window.open(url);
            }
        });
    }

    function initOrgTree(orgNo, beginTime, endTime) {

        var setting = {
            view: {
                dblClickExpand: false,
                showLine: false,
                selectedMulti: false,
                fontCss: {fontSize: '18px'}
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
        var date = rangeTimeObj.val();
        ;
        $.ajax({
            url: "/admin/organization/data",
            type: "get",
            contentType: "application/json",
            dataType: "json",
            success: function (data) {
                //表格初始化
                tableObj.bootstrapTable({
                    url: apiUrl,
                    dataType: "json",
                    uniqueId: "id",
                    singleSelect: false,
                    clickToSelect: true,
                    pagination: false, // 是否分页
                    sidePagination: 'server',// 选择服务端分页
                    pageSize: 40,
                    pageList: [30, 50],//分页步进值
                    height: 500, //给表格指定高度，就会有行冻结的效果
                    fixedColumns: true, //是否开启列冻结
                    fixedNumber: 1, //需要冻结的列数
                    queryParams: {
                        date: date
                    }
                });

                summaryByPayBusinessTypeTree = $("#summaryByPayBusinessTypeOrgSelect").ztreeview({
                    name: 'name',
                    key: 'code',
                    //是否
                    clearable: true,
                    expandAll: true,
                    data: data
                }, setting);
                summaryByPayBusinessTypeTree.updateCode(data[0].id, data[0].code);
                if ((orgNo != "" && orgNo != null && orgNo != undefined)) {
                    for (var i = 0; i < data.length; i++) {
                        if (orgNo == data[i].code) {
                            summaryByPayBusinessTypeTree.updateCode(data[i].id, data[i].code);
                        }
                    }
                }
                // 选择隐藏还是显示机构下拉选择
                var length = data.length;
                if (length && length > 1) {
                    $("#summaryByPayBusinessTypeOrgSelect").parent().parent().parent().show();
                } else {
                    $("#summaryByPayBusinessTypeOrgSelect").parent().parent().parent().hide();
                }

                var treeName = summaryByPayBusinessTypeTree.getText;
//	           	setTableTitleHide(tableObj,"收费员业务类型汇总报表",beginTime,endTime,treeName);
            }
        });
    }

    function init(orgNo, beginTime, endTime) {
        initDate(beginTime);
        initBillSource();
        initOrgTree(orgNo, beginTime, endTime);
    }

    /**
     * 初始化渠道下拉列表
     */
    function initBillSource() {
        $.ajax({
            type: 'GET',
            url: baseUrl + "/bill-source/down/list",
            dataType: "json",
            data: {},
            success: function (result) {
                var rows = result.rows;
                var selectBoxTxt = "<option value=''>全部</option>";
                if (rows != null && typeof rows != 'undefined') {
                    for (var billSource in rows) {
                        var value = rows[billSource].value;
                        var name = rows[billSource].name;
                        selectBoxTxt += " <option value='" + value + "'>" + name + "</option>";
                    }
                }
                billSourceObj.html(selectBoxTxt);
            }
        });
    }

    /**
     * 生成节流函数
     * @param throttleDelay : ms   节流的时间限制，单位毫秒
     * @param handle : function    超过 throttleDelay 时，所要执行的函数
     */
    function createThrottle(throttleDelay, handle) {
        var thenTime = new Date();

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

    $(window).resize(createThrottle(1500, function (event) {
        resetWidth();
    }));

    window.addEventListener("navAreaExpandChange", function (event) {
        setTimeout(resetWidth, 300);
    });

    function resetWidth() {
        tableObj.bootstrapTable('resetWidth');
    }

    function search(th) {

        var date = "";
        var rangeTime = rangeTimeObj.val();
        if (rangeTime) {
            date = rangeTime
        }
        tableObj.bootstrapTable('refreshOptions', {
            height: 500, //给表格指定高度，就会有行冻结的效果
            fixedColumns: true, //是否开启列冻结
            fixedNumber: 1, //需要冻结的列数
            pageNumber: 1,
            queryParams: function (params) {
                var queryObj = formObj.serializeObject();
                queryObj['date'] = date;
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
        var treeName = summaryByPayBusinessTypeTree.getText;
        tableObj.bootstrapTable('mergeCells', {index: 0, field: 'Cashiera', rowspan: 5});
//		setTableTitleHide(tableObj,"收费员业务类型汇总报表",starttime,endtime,treeName);
    }

    function otherAcount(val, row, index) {
        var count;
        count = row.allAcount - row.registerAcount - row.makeAppointmentAcount - row.payAcount - row.clinicAcount - row.prepaymentForHospitalizationAcount;
        if (isNaN(count)) {
            count = 0;
        }
        if (row.businessType == "合计") {
            var html = "<span style='font-weight: bold;'>" + count + "</span>";
            return html;
        }
        return count;
    }

    function otherAmount(val, row, index) {
        var amount;
        amount = row.allAmount - row.registerAmount - row.makeAppointmentAmount - row.payAmount - row.clinicAmount - row.prepaymentForHospitalizationAmount;
        if (isNaN(amount)) {
            amount = 0;
        }
        if (row.businessType == "合计") {
            var html = "<span style='font-weight: bold;'>" + new Number(amount).toFixed(2) + "</span>";
            return html;
        }
        return new Number(amount).toFixed(2);
    }

    function moneyFormat(val, row, index) {

        if (row.businessType == "合计") {
            var html = "<span style='font-weight: bold;'>" + new Number(val).toFixed(2) + "</span>";
            return html;
        }
        return new Number(val).toFixed(2);
    }

    function normal(val, row, index) {
        if (val == null) {
            val = '--';
        }
        if (row.businessType == "合计") {
            var html = "<span style='font-weight: bold;'>" + val + "</span>";
            return html;
        }
        return val;
    }

    /**
     * 增加款 页面格式化
     * @param val
     * @param row
     * @param index
     * @returns {string}
     */
    function increAmountFormatter(val, row, index) {
        if (val == null) {
            val = "--";
        }
        val = new Number(val).toFixed(2);
        if (row.orgNoName == "合计") {
            val = "<span style='font-weight: bold;'>" + new Number(val).toFixed(2) + "</span>";
            return val;
        }
        if (row.orgNoName != "合计" && val > 0) {
            var html = "<a  href='javascript:;' onclick='app.admin.ReportsPayBusinessType.amountSumDetail(\"" + row.orgNo + "\",\"" + row.billSource + "\",\"" + row.billSourceName + "\",1)' style='text-decoration:underline'>" + val + "</a>";
            return html;
        }

        return val;
    }

    function moneyFormatter(val, row, index) {
        if (val == null) {
            val = "--";
        }
        if (row.orgNoName == "合计") {
            val = "<span style='font-weight: bold;'>" + new Number(val).toFixed(2) + "</span>";
            return val;
        }
        val = new Number(val).toFixed(2);
        return val;
    }

    /**
     * 字体加粗
     * @param val
     * @param row
     * @param index
     * @returns {string}
     */
    function blodFormatter(val, row, index) {
        if (row.orgNoName == "合计") {
            val = "<span style='font-weight: bold;'>" + val + "</span>";
            return val;
        } else {
            return val;
        }
    }

    /**
     * 减少款 页面格式化
     * @param val
     * @param row
     * @param index
     * @returns {string}
     */
    function reduceAmountFormatter(val, row, index) {
        if (val == null) {
            val = "--";
        }
        val = new Number(val).toFixed(2);
        if (row.orgNoName == "合计") {
            val = "<span style='font-weight: bold;'>" + new Number(val).toFixed(2) + "</span>";
            return val;
        }
        if (row.orgNoName != "合计" && val > 0) {
            var html = "<a  href='javascript:;' onclick='app.admin.ReportsPayBusinessType.amountSumDetail(\"" + row.orgNo + "\",\"" + row.billSource + "\",\"" + row.billSourceName + "\",0)' style='text-decoration:underline'>" + val + "</a>";
            return html;
        }
        return val;
    }


    /**
     * 增加款、减少款 详情
     * @param orgNo
     * @param billSource
     * @param detailType 详情类型：1：增加款明细   0：减少款明细
     * @returns {string}
     */
    function amountSumDetail(orgNo, billSource, billSourceName, detailType) {
        $("#billSource").val(billSource);
        $("#billSourceName").val(billSourceName);
        $("#orgCode").val(orgNo);
        $("#detailType").val(detailType);

        $.bootstrapLoading.start({loadingTips: "正在处理数据，请稍候..."});
        var detailTypeName = detailType == 1 ? "增加款" : "减少款";
        $("#detailTypeName").html(detailTypeName);
        let date = rangeTimeObj.val();
        $.ajax({
            type: 'POST',
            url: baseUrl + "/detail/list",
            data: {
                "orgCode": orgNo,
                "billSource": billSource,
                "detailType": detailType,
                "date": date
            },
            dataType: "json",
            success: function (result) {
                $.bootstrapLoading.end();
                var data = [];
                var sourceData = result.rows;
                if (sourceData != null && typeof (sourceData) != "undefined" && sourceData.length > 0) {
                    for (var i = 0; i < sourceData.length; i++) {
                        var row = {};
                        row['orgNoName'] = sourceData[i].orgNoName;
                        row['billSourceName'] = sourceData[i].billSourceName;
                        row['payFlowNo'] = sourceData[i].payFlowNo;
                        row['payTypeName'] = sourceData[i].payTypeName;
                        row['amount'] = sourceData[i].amount;
                        row['payTime'] = sourceData[i].payTime;
                        row['serverDate'] = sourceData[i].serverDate;
                        data.push(row);
                    }

                    advancePaySumDetailTable.bootstrapTable('destroy').bootstrapTable({
                        resizable: true,
                        data: data,
                        // 是否分页
                        pagination: true,
                    });
                    advancePaySummaryDlg.modal('show');
                    advancePaySumDetailObj.show();
                }
            }
        });

    }

    /**
     * 初始化日期
     */
    function initDate(beginTime) {
        var now = new Date();
        var month = now.getMonth() < 10 ? "0" + now.getMonth() : now.getMonth();
        var startLayDate = laydate.render({
            elem: '#billDataTime',
            btns: ['confirm'],
            theme: '#A9BCF5',
            type: "month",
            value: now.getFullYear() + "-" + month,
            format: "yyyy-MM",
            max: beginTime,
            /*ready: function (date) {
                var layym = $(".laydate-main-list-0 .laydate-set-ym span").eq(0).attr("lay-ym").split("-")[1];
                if (layym == (nowDate.getMonth() + 1)) {
                    $(".laydate-main-list-0 .laydate-prev-m").click();
                }
            }*/
        });

    }

    return {
        init: init,
        search: search,
        exportList: exportList,
        otherAcount: otherAcount,
        otherAmount: otherAmount,
        moneyFormat: moneyFormat,
        normal: normal,
        increAmountFormatter: increAmountFormatter,
        reduceAmountFormatter: reduceAmountFormatter,
        moneyFormatter: moneyFormatter,
        amountSumDetail: amountSumDetail,
        blodFormatter: blodFormatter
    }
})();

NB.ns("app.admin").propertiesConfig = (function() {

	var searchForm = $("#propertiesConfigSearchForm");
	
	var tableObj = $("#propertiesConfigDataTable");

	var dlgObj = $("#propertiesConfigDlg");

	var digFormObj = dlgObj.find("form");

	var apiUrl = "admin/propertiesConfig";

	function formatOpt(index, row) {
		var html = "<a href='javascript:;' class='btn btn-info btn-sm m-primary' onclick='app.admin.propertiesConfig.update(\""
				+ row.id + "\")'>编辑</a>";
		html += "<a href='javascript:;' class='btn btn-info btn-sm m-primary' onclick='app.admin.propertiesConfig.del(\""
				+ row.id + "\")'>删除</a>";
		return html;
	}

	function activation(val){
		if(val==0){
			return '<p data-toggle="tooltip" title=\"禁用\">禁用</p>';
		}else{
			return '<p data-toggle="tooltip" title=\"激活\">激活</p>';
		}
	}
	
	function format(val){
		return '<p data-toggle="tooltip" title=\"'+val+'\">'+val+'</p>';
	}
	
	function formatType(val){
		if(val == "common"){
			return "公共";
		}else{
			return "医院";
		}
	}
	
	function formatModel(val){
		
		if("normal" == val){
			return "普通模式";
		}else if("loop" == val){
			return "循环模式";
		}else if("timer" == val){
			return "定时模式";
		}
		return "-";
	}

	function del(id) {
		bootbox.confirm({
			title : "提示",
			message : "确定删除该记录?",
			buttons : {
				confirm : {
					label : "确认"
				},
				cancel : {
					label : "取消"
				}
			},
			callback : function(r) {
				if (r) {
					var options = {
						url : apiUrl + "/del?id="+id,
						type : "POST",
						dataType : "json",
						success : function(result) {
							if (result.success) {
								$.NOTIFY.showSuccess("提醒", "操作成功", '');
								dlgObj.modal("hide");
								init();
							} else {
								if (result.message) {
									$.NOTIFY.showError("错误", result.message, '');
								} else {
									$.NOTIFY.showError("错误", "操作失败", '');
								}
							}
						}
					};
					digFormObj.ajaxSubmit(options);
				}
			}
		});
	}

	function saveOrUpdate() {
		var options = {
			url : apiUrl + "/saveOrUpdate",
			type : "post",
			dataType : "json",
			success : function(result) {
				if (result.success) {
					$.NOTIFY.showSuccess("提醒", "操作成功", '');
					dlgObj.modal("hide");
					
					search();
				} else {
					if (result.message) {
						$.NOTIFY.showError("错误", result.message, '');
					} else {
						$.NOTIFY.showError("错误", "操作失败", '');
					}
				}
			}
		};
		digFormObj.ajaxSubmit(options);
	}

	function update(id) {
		digFormObj[0].reset();
		var row = tableObj.bootstrapTable('getRowByUniqueId', id);
		
		var model = row.model;
		dlgObj.find("input[type=radio][value=" + model + "]").attr("checked", "checked");
		dlgObj.find("input[type=radio][value=" + model + "]").trigger("click");
		
		digFormObj.find("input[name=id]").val(row.id);
		digFormObj.find("input[name=pkey]").val(row.pkey);
		digFormObj.find("input[name=pvalue]").val(row.pvalue);
		digFormObj.find("input[name=defaultValue]").val(row.defaultValue);
		digFormObj.find("input[name=description]").val(row.description);
		digFormObj.find("input[name=type]").val(row.type);
		digFormObj.find("input[name=sort]").val(row.sort);
		digFormObj.find("select[name=isActived]").val(row.isActived);
		dlgObj.modal("show");
	}

	function add() {
		digFormObj[0].reset();
		dlgObj.find("input[type=radio][value=normal]").attr("checked", "checked");
		dlgObj.find("input[type=radio][value=normal]").trigger("click");
		
		digFormObj.find("input[name=id]").val("");
		dlgObj.modal("show");
	}
	

	function search(th) {
        var pkey = searchForm.find("input[name=pkey]").val();
        var description = searchForm.find("input[name=description]").val();
        var type = searchForm.find("select[name=type]>option:selected").val();
        var model = searchForm.find("select[name=model]>option:selected").val();
        tableObj.bootstrapTable('refreshOptions', {
            resizable: true,
            pageNumber: 1,
            queryParams: function (params) {
                var queryObj = {
                    pkey: pkey,
                    type: type,
                    model: model,
                    description: description
                };
                var query = $.extend(true, params, queryObj);
                return query;
            },
        	onPreBody:function(data){
            	$(th).button('loading');
            },
            onLoadSuccess:function(data){
            	$(th).button("reset");
	        }
        });
	}
	
	function init() {
		initTable();
		
		// 选择模式的时候点击触发事件
		dlgObj.find("input[type=radio]").on("click",function (event) {
			
			var value = event.currentTarget.value;
			
			if("normal" == value){
				setNormalOption();
			}else if("loop" == value){
				setLoopOption();
			}else if("timer" == value){
				setTimerOption();
			}
		});
	}
	
	function setNormalOption(){
		var pvalueHtml = '<input type="text" class="form-control" name="pvalue" placeholder="属性值" />';
		var defaultValueHtml = '<input type="text" class="form-control" name="defaultValue" placeholder="请输入默认值">';
		
		dlgObj.find(".config-input .config-input-pvalue").html(pvalueHtml);
		dlgObj.find(".config-input .config-input-defaultValue").html(defaultValueHtml);
	}
	function setLoopOption(){
		var pvalueHtml = '<input type="number" class="form-control" name="pvalue" placeholder="属性值" value="1" min="1" max="10000" step="1" />';
		var defaultValueHtml = '<input type="number" class="form-control" name="defaultValue" placeholder="请输入默认值" value="1" min="1" max="10000" step="1" />';
		
		dlgObj.find(".config-input .config-input-pvalue").html(pvalueHtml);
		dlgObj.find(".config-input .config-input-defaultValue").html(defaultValueHtml);
	}
	function setTimerOption(){
		var pvalueHtml = '<input AUTOCOMPLETE="off" type="text" class="form-control pull-right" placeholder="属性值(HH:mm:ss)" name="pvalue" id="timer-pvalue" readonly="readonly">';
		var defaultValueHtml = '<input AUTOCOMPLETE="off" type="text" class="form-control pull-right" placeholder="默认值(HH:mm:ss)" name="defaultValue" id="timer-defaultValue" readonly="readonly">';
		
		dlgObj.find(".config-input .config-input-pvalue").html(pvalueHtml);
		dlgObj.find(".config-input .config-input-defaultValue").html(defaultValueHtml);
		
		initDate();
	}
	
	function initDate(){
		var pvalueLayDate = laydate.render({
			elem : '#timer-pvalue',
			btns: ['confirm'],
			theme : '#A9BCF5',
			type: "time",
			format:"HH:mm:ss"
		});
		var defaultValueLayDate = laydate.render({
			elem : '#timer-defaultValue',
			btns: ['confirm'],
			theme : '#A9BCF5',
			type: "time",
			format:"HH:mm:ss"
		});
	}
	
	function initTable(){
		// 初始化表格
		tableObj.bootstrapTable("destroy");
		tableObj.bootstrapTable({
			url : apiUrl + "/data",
			dataType : "json",
			uniqueId : "id",
			resizable : true,
			singleSelect : true,
			pagination : true, // 是否分页
			sidePagination : 'server',// 选择服务端分页
		});
	}

	return {
		init : init,
		formatOpt : formatOpt,
		add : add,
		saveOrUpdate : saveOrUpdate,
		update : update,
		del : del,
		search : search,
		activation : activation,
		formatType : formatType,
		formatModel : formatModel,
		format : format
	}
})();
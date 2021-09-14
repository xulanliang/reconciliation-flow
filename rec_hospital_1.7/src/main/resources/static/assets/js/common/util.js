/**
 *  导入excel文件校验
 * @param elementId 元素id
 */
function importFileValidate(elementId) {
	var file = $("#"+elementId).val();
	if (file == null || file == '') {
		return;
	} else {
		var index = file.lastIndexOf(".");
		if (index < 0) {
			$.messager.alert('提示', '上传的文件格式不正确，请选择Excel文件！', 'error');
			document.getElementById(elementId).outerHTML = document
					.getElementById(elementId).outerHTML;
		} else {
			var suffix = file.substring(index + 1, file.length);
			if (suffix != "txt" && suffix != "xls" && suffix != "xlsx") {
				$.messager.alert('提示', '上传的文件格式不正确，请选择Excel文件或者txt文件！', function() {
				});
				document.getElementById(elementId).outerHTML = document
						.getElementById(elementId).outerHTML;
			}
		}
	}
}

//结束时间不能小于开始时间 返回false 时候判断 
function ischeckDate(startTime,endTime){
	if(startTime == '' || endTime == '')
		return true;
	if (endTime != "") {
		startDate = startTime.replace(/-/g, '/');
		endDate = endTime.replace(/-/g, '/');
		var end = new Date(Date.parse(endDate));
		var start = new Date(Date.parse(startDate));
		return start <= end; 
	}
}

//获取当前时间
function getNowFormatDate() {
       var date = new Date();
       var seperator1 = "-";
       var year = date.getFullYear();
       var month = date.getMonth() + 1;
       var strDate = date.getDate();
       if (month >= 1 && month <= 9) {
           month = "0" + month;
       }
       if (strDate >= 0 && strDate <= 9) {
           strDate = "0" + strDate;
       }
       var currentdate = year + seperator1 + month + seperator1 + strDate;
  	   
       return currentdate;
   }
//获取开始时间
function getOldFormatDate(subMonth) {
       var date = new Date();
       var seperator1 = "-";
       var year = date.getFullYear();
       var month = date.getMonth() + 1;
       var strDate = date.getDate();
       if(month<=subMonth){
       	year-=1;
       	month=12-subMonth+month;
       }else{
           month = date.getMonth() + 1-subMonth;
       }
       if (month >= 1 && month <= 9) {
           month = "0" + month;
       }
       if (strDate >= 0 && strDate <= 9) {
           strDate = "0" + strDate;
       }
       var currentdate = year + seperator1 + month + seperator1 + strDate;
       return currentdate;
   }
   
   //数组去重
	function unique(arr) {
	    var result = [], hash = {};
	    for (var i = 0, elem; (elem = arr[i]) != null; i++) {
	        if (!hash[elem]) {
	            result.push(elem);
	            hash[elem] = true;
	        }
	    }
	    return result;
	};
	function getHospitalName(id,callBack){
    	var url ='/admin/hospital/getHospitalNameById?id='+id;
    	var nameMap = new Map();
    	subData(url,'GET',null,function(res){
    		$.each(res,function(index,item){
    			nameMap.set(item.id,item.name);
    		})
			callBack(nameMap)
    	});
    };
	function subData(url,type,params,cb){
        $.ajax({
            url:url,
            type:type,
            contentType: 'application/json',
            data:JSON.stringify(params),
            success: function(res){
                cb(JSON.parse(res));
            },
            error:function(){
                $.messager.show({title: '失败',msg: "请求失败，请重试"});
            }
        });
    }

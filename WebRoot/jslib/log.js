var pageurl = window.location.href;
var index = pageurl.indexOf("data/");
var basepath = pageurl.substring(0,index+5);
/**
 * 日志工具类
 */
//记录用户操作日志
function savelog(moduleName,optype,opcontent){
	var param = {
		name:moduleName,
		optype:optype,
		opcontent:opcontent
	};
	var url = basepath+"json/UserLog!savelog";
	$.ajax({
		type:"POST",
		url:url,
		data:param,
		dataType:"json",
		success: function(rsMap){
			
		},error:function(req,status,e){
			//alert("调用ajax请求失败！"+e);
		}
	});
}
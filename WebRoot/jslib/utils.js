//jquery-easyui常用对象封装
var cache = new Object();
var TOKEN_NAME = "Albedo-Requst-Token";
function getSessionId(){  
    var c_name = 'JSESSIONID';  
    if(document.cookie.length>0){  
       c_start=document.cookie.indexOf(c_name + "=")  
       if(c_start!=-1){   
         c_start=c_start + c_name.length+1   
         c_end=document.cookie.indexOf(";",c_start)  
         if(c_end==-1) c_end=document.cookie.length  
         return unescape(document.cookie.substring(c_start,c_end));  
       }  
    }  
}
//执行ajax请求
function doAjax(url,param,op,callback){
	$.ajax({
		type:"POST",
		url:url,
		data:param,
		dataType:"json",
		beforeSend:function(xhr){
			xhr.setRequestHeader(TOKEN_NAME,url);
		},
		success: function(rsMap){
			if(callback){
				callback(rsMap,op);
			}
		},error:function(req,status,e){
			alert("调用ajax请求失败！"+e);
		}
	});
}
var loader;//进度条
var msgloader;//信息提示框
//显示进度条
function showLoading(msg){
	//$.messager.progress({title:"信息提示框",msg:msg});
	loader = document.getElementById("divProgress");
	if(loader==null){
		loader = document.createElement("div");
		loader.id = "divProgress";
		document.body.appendChild(loader);
	}
	var sb = new Array();
	sb.push('<table class="divProgressBody" border="0" style="line-height:20px;font:14px 微软雅黑;padding:5px;">');
	sb.push('<tbody>');
	sb.push('<tr>');
	sb.push('<td class="divProgressBody-Wait" onclick="hideLoading()"></td>');
	sb.push('<td style="padding:0px 5px;">'+msg+'</td>');
	//sb.push('<td class="progressTime">123</td>');
	sb.push('</tr>');
	sb.push('</tbody>');
	sb.push('</table>');
	loader.innerHTML = sb.join("");
	var w = $(".divProgressBody").width();
	var h = $(".divProgressBody").height();
	if(w>480){
		w = 480;
	}else if(w<200){
		w = 200;
	}
	$("#divProgress").window({
        title: "信息提示框",
        width: (w+30*1),
        shadow: false,
        modal: true,
        collapsible: false,
        minimizable: false,
        maximizable: false,
        resizable: false,
        closable: false,
        draggable: false
    });
    $("#divProgress").window('open');
    //reloadProgressTime();
}

var progressTime = 0;
var progressTimeout = null;
//统计进度加载时间
function reloadProgressTime(){
	progressTime+=100;
	$("#divProgress").parent().find(".panel-title").html("信息提示框  [<span style='color:blue;'>耗时："+progressTime+" ms</span>]");
	progressTimeout = setTimeout(function(){reloadProgressTime();},100);
}
//关闭进度条
function hideLoading(){
	$("#divProgress").window('close');
	$("#divProgress").parent().remove();
	clearTimeout(progressTimeout);
	progressTime = 0;
	//$.messager.progress("close");
}

function showDataLoading(id,msg){
	if(msg==null || msg==""){
		msg = "正在查询数据，请稍候……";
	}
	$("#"+id).html("<table><tr><td><img src='images/loading.gif' /></td><td style='color:black;padding-top:0px;'>"+msg+"</td></tr></table>");
	$("#"+id).slideDown("fast");
}

function hideDataLoading(id){
	$("#"+id).html("");
}
var errorTimeout;
function showErrorMsg(id,msg){
	clearTimeout(errorTimeout);
	$("#"+id).html("<table><tr><td><img src='images/no.png' /></td><td style='color:red;padding-top:0px;'>"+msg+"</td></tr></table>");
	$("#"+id).slideDown("fast");
	errorTimeout = setTimeout(function(){
		$("#"+id).slideUp("fast");
	},3000);
}
var successTimeout;
function showSuccessMsg(id,msg){
	clearTimeout(successTimeout);
	$("#"+id).html("<table><tr><td><img src='images/ok.png' /></td><td style='color:green;padding-top:0px;'>"+msg+"</td></tr></table>");
	$("#"+id).slideDown("fast");
	successTimeout = setTimeout(function(){
		$("#"+id).slideUp("fast");
	},3000);
}

function showLoadingMsg(id,msg){
	$("#"+id).html("<table><tr><td><img src='images/loading.gif' msgid='"+id+"'/></td><td style='color:green;padding-top:0px;'>"+msg+"</td></tr></table>");
	$("#"+id).slideDown("fast");
	$("#"+id+" img").click(function(){
		var id = $(this).attr("msgid");
		$("#"+id).slideUp("fast");
	});
}

function hideLoadingMsg(id){
	$("#"+id).slideUp("fast");
}

function showWarnMsg(msg){
	var sb = new Array();
	sb.push('<table class="divMsgBody" border="0" style="height:auto;line-height:20px;font:14px 微软雅黑;padding:0px;">');
	sb.push('<tbody>');
	sb.push('<tr>');
	//sb.push('<td style="width:30px;text-align:center;" class="divMsgBody-Wait"></td>');
	sb.push('<td>'+msg+'</td>');
	sb.push('</tr>');
	sb.push('</tbody>');
	sb.push('</table>');
	var msgHtml = sb.join("");
	$.messager.show({
		title:'提示信息',
		msg:msgHtml,
		showType:'slide',
		style:{
			right:'',
			top:document.body.scrollTop+document.documentElement.scrollTop,
			bottom:''
		}
	});
}

function showMessage(msg,isClose){
	msgloader = document.getElementById("divMsg");
	if(msgloader==null){
		msgloader = document.createElement("div");
		msgloader.id = "divMsg";
		document.body.appendChild(msgloader);
	}
	var sb = new Array();
	sb.push('<table class="divMsgBody" border="0" style="height:auto;line-height:20px;font:14px 微软雅黑;padding:20px;">');
	sb.push('<tbody>');
	sb.push('<tr>');
	sb.push('<td style="width:30px;text-align:center;" class="divMsgBody-Wait"></td>');
	sb.push('<td>'+msg+'</td>');
	sb.push('</tr>');
	sb.push('</tbody>');
	sb.push('</table>');
	msgloader.innerHTML = sb.join("");
	var w = $(".divMsgBody").width();
	var h = $(".divMsgBody").height();
	if(w>800){
		w = 800;
	}else if(w<200){
		w = 200;
	}
	if(h>400){
		h = 400;
	}
	$("#divMsg").window({
        title: "信息提示框",
        width: (w+60*1),
        height: (h+80*1),
        shadow: true,
        modal: true,
        collapsible: false,
        minimizable: false,
        maximizable: false,
        resizable: false,
        closable: false,
        draggable: false
    });
    setTimeout(function(){$("#divMsg").window('destroy');},3000);
}

var tipsTimeout;
function showTips(msg){
	if(document.getElementById("optips")==null){
		var html = "<div id='optips' style='text-align:center;display:none;position:absolute;left:0px;top:0px;z-index:99999;background:#3EC6FA;border:1px solid #0876D8;padding:10px;font:14px 微软雅黑;color:#fff;font-weight:bold;width:300px;'>"+msg+"</div>";
		$(document.body).append(html);
	}else{
		$("#optips").html(msg);
	}
	var w = $(document.body).width();
	var dw = 200;
	var left = (w-dw)/2;
	$("#optips").css("left",left+"px");
	$("#optips").slideDown();
	clearTimeout(tipsTimeout);
	tipsTimeout = setTimeout(function(){
		$("#optips").slideUp();
	},2500);
}

//弹出位置提示框
function showPositionMsg(title,msg,position){
	if(position=="top-left"){
		$.messager.show({
			title:title,
			msg:msg,
			showType:'show',
			style:{
				right:'',
				left:0,
				top:document.body.scrollTop+document.documentElement.scrollTop,
				bottom:''
			}
		});
	}else if(position=="top-center"){
		$.messager.show({
			title:title,
			msg:msg,
			showType:'slide',
			style:{
				right:'',
				top:document.body.scrollTop+document.documentElement.scrollTop,
				bottom:''
			}
		});
	}else if(position=="top-right"){
		$.messager.show({
			title:title,
			msg:msg,
			showType:'show',
			style:{
				left:'',
				right:0,
				top:document.body.scrollTop+document.documentElement.scrollTop,
				bottom:''
			}
		});
	}else if(position=="center-left"){
		$.messager.show({
			title:title,
			msg:msg,
			showType:'fade',
			style:{
				left:0,
				right:'',
				bottom:''
			}
		});
	}else if(position=="center"){
		$.messager.show({
			title:title,
			msg:msg,
			showType:'fade',
			style:{
				right:'',
				bottom:''
			}
		});
	}else if(position=="center-right"){
		$.messager.show({
			title:title,
			msg:msg,
			showType:'fade',
			style:{
				left:'',
				right:0,
				bottom:''
			}
		});
	}else if(position=="bottom-left"){
		$.messager.show({
			title:title,
			msg:msg,
			showType:'show',
			style:{
				left:0,
				right:'',
				top:'',
				bottom:-document.body.scrollTop-document.documentElement.scrollTop
			}
		});
	}else if(position=="bottom-center"){
		$.messager.show({
			title:title,
			msg:msg,
			showType:'slide',
			style:{
				right:'',
				top:'',
				bottom:-document.body.scrollTop-document.documentElement.scrollTop
			}
		});
	}else if(position=="bottom-right"){
		$.messager.show({
			title:title,
			msg:msg,
			showType:'show'
		});
	}
}
//打开一个模态窗口
function openModalWin(option) {
	var div = document.getElementById(option.id);
    if (div == null) {
        div = document.createElement("div");
        div.id = option.id;
        div.style.background = "#FFFFFF";
        //div.style.padding = "5px";
        document.body.appendChild(div);
    }
    if (typeof(option.html)!="undefined") {
        div.innerHTML = option.html;
    }
    var width = jQuery(document.body).width();
    var height = jQuery(document.body).height();
    var left=(width-option.width)/2;
    var top=(height-option.height)/2;
	switch(option.position){
		case "center": left=(width-option.width)/2;top=(height-option.height)/2;break;
		case "left-top": left=option.left;top=option.top;break;
		case "right-top": left=width-option.width-option.right;top=option.top;break;
		case "left-bottom": left=option.left;top=height-option.height-option.bottom;break;
		case "right-bottom":left=width-option.width-option.right;top=height-option.height-option.bottom; break;
	}
	var modal = true;
    var draggable = true;
    var collapsible = true;
    var minimizable = false;
    var closable = true;
    if(typeof(option.modal)!="undefined"){
    	modal = option.modal;
    }
    if(typeof(option.collapsible)!="undefined"){
    	collapsible = option.collapsible;
    }
    if(typeof(option.minimizable)!="undefined"){
    	minimizable = option.minimizable;
    }
    if(typeof(option.closable)!="undefined"){
    	closable = option.closable;
    }
    if(typeof(option.draggable)!="undefined"){
    	draggable = option.draggable;
    }
    $("#" + option.id).css("display", "block");
    $("#" + option.id).window({
        title: option.title,
        width: option.width,
        height: option.height,
        shadow: false,
        modal: option.modal,
        top:top,
		left:left,
		draggable: draggable,
        collapsible: collapsible,
        minimizable: minimizable,
        maximizable: false,
        resizable: false,
        closable: closable,
        onClose:function(){
        	//$(this).parent().remove();
        	if(typeof(option.closeFn)!="undefined"){
        		eval(option.closeFn);
        	}
        }
    });
}
//让弹出窗口最大化
function showMaxWindow(id){
	$("#" + id).window('maximize');
}
//给数据表格添加鼠标事件
function setTableMouseEvent(tbId){
	jQuery("#"+tbId+" tr:odd").addClass("odd");
	jQuery("#"+tbId+" tr:even").addClass("even");
	//单行悬停事件
	jQuery("#"+tbId+" tr").hover(function(){
		//jQuery(this).removeClass("odd");
		//jQuery(this).removeClass("even");
		jQuery(this).addClass("overcss");
	},function(){
		//jQuery("#"+tbId+" tr:odd").addClass("odd");
		//jQuery("#"+tbId+" tr:even").addClass("even");
		jQuery(this).removeClass("overcss");
		//jQuery("#"+tbId+" tr[selected=selected]").removeClass("odd");
		//jQuery("#"+tbId+" tr[selected=selected]").removeClass("even");
		jQuery("#"+tbId+" tr[selected=selected]").addClass("clickcss");
	});
	
	//单行选中事件
	jQuery("#"+tbId+" tr").click(function(){
		jQuery("#"+tbId+" tr").removeAttr("selected");
		jQuery("#"+tbId+" tr").removeClass("clickcss");
		jQuery(this).addClass("clickcss");
		jQuery(this).attr("selected","selected");
	});
	return;	
}

function setTextboxMouseEvent(inputId){
	$('#'+inputId).focus(
		function(){
				$(this).addClass("focus");
				if($(this).val() == this.defaultValue){
					$(this).val("");
				}
			}
		).blur(
			function(){
				$(this).removeClass("focus");
				//给文本框附加一个值，用来标记是否文本框已经输入
				if($(this).val() == ''){
					$(this).attr('isNull',true);
					$(this).val(this.defaultValue);
				}else{
					$(this).attr('isNull',false);
				}
			}
		)
}

/**
* 创建装备种类表格的表格数据
* 作者：龚伟成
* 时间：2014-03-04
*/
function getResultTable(pageInfo , arr ,data , tbodyId ,theadId , ckbAllId ,ckbId , flagId ){
	var pageNumber = pageInfo.pageNumber;
	var pageSize = pageInfo.pageSize;
	var endFor = pageInfo.endFor;
	//生成表格头
	JsCreateEquipKindThead(arr , theadId , ckbAllId);
	var sb = new StringBuilder();
	jQuery("#"+tbodyId).empty();
	var record = null;
	var rowIndex = (pageNumber-1)*pageSize;
	for(var i=0;i<endFor;i++){
		record = data[rowIndex+i];
		sb.append("<tr tid='"+record[flagId]+"' rowIndex='"+(rowIndex+i)+"'>");
		sb.append("<td style='text-align:center;width:30px;'><input class='css_ckb' name='"+ckbId+"' type='checkbox' value='"+record[flagId]+"'/></td>");
		sb.append("<td style='text-align:center;width:30px;'>"+(rowIndex+i+1)+"</td>");
		for(var j=0 ; j<arr.length;j++){
			if(arr[j].isShow == true){
				sb.append("<td style='text-align:center;'>"+record[arr[j].colname]+"</td>");
			}
		}
		sb.append("</tr>");
	}
	jQuery("#"+tbodyId).append(sb.toString());
	//让复选框自动全选/全不选
	$("#"+ckbAllId).click(function(){
		$("[name="+ckbId+"]:checkbox").attr("checked",this.checked);
	});
	$("[name="+ckbId+"]:checkbox").click(function(){
			$tmp = $('[name='+ckbId+']:checkbox');
			$("#"+ckbAllId).attr("checked",$tmp.length == $tmp.filter(':checked').length);
		});
}

/**
* 创建装备种类表格的表格头
* 作者：龚伟成
* 时间：2014-03-04
*/
function JsCreateEquipKindThead(headArr , theadId , ckbAllId){
	jQuery('#'+theadId).empty();
	var sb = new StringBuilder();
	sb.append("<th style='text-align:center;width:30px;'><input class='css_ckb' id='"+ckbAllId+"' name='ckb_all1' type='checkbox'/></th>");
	sb.append("<th style='text-align:center;width:30px;'>序号</th>");
	for(var j=0 ; j<headArr.length;j++){
		if(headArr[j].isShow == true){
			sb.append("<th style='text-align:center;'>"+headArr[j].text+"</th>");
		}
	}
	jQuery('#'+theadId).html(sb.toString());
}

/**
* 校验输入的数据
* 作者：龚伟成
* 时间：2014-03-04
*/
function checkoutInput(id , message){
	var inputIsNull = $('#'+id).attr('isNull');
	if(inputIsNull == "true"){
		showErrorMsg("ajaxTipsMsg",message);
		return false;
	}
	return true;
}


/*******************常用工具集合********************************/
String.prototype.replaceAll  = function(s1,s2){   
    return this.replace(new RegExp(s1,"gm"),s2);
} 
/**
 * 字符串拼接函数
 */
function StringBuilder()
{
    this.strings=new Array(); 
}
/**
 * 字符串拼接函数--append方法
 * @param {Object} value 要拼接的内容
 */
StringBuilder.prototype.append = function(value)
{
    if(value)
    {
        this.strings.push(value);
    }
}
/**
 * 字符串拼接函数--清除所有内容
 */
StringBuilder.prototype.clear = function ()
{
    this.strings.length = 0;
}
/**
 * 字符串拼接函数--转换成字符串
 * @return 字符串 
 */
StringBuilder.prototype.toString = function ()
{
    return this.strings.join("");
}
/***************************
 * 移除数组中指定无素
 * @param {Object} dx 数组序号
 * @memberOf {TypeName} 
 * @return {TypeName} 数组
 ***************************/
Array.prototype.remove=function(dx) 
{ 
    if(isNaN(dx)||dx>this.length){return false;} 
    for(var i=0,n=0;i<this.length;i++) 
    { 
        if(this[i]!=this[dx]) 
        { 
            this[n++]=this[i]; 
        } 
    } 
    this.length=this.length-1*1;
}
Array.prototype.removeChild=function(child){
	if(child==null){return;}
	for(var i=0,n=0;i<this.length;i++) 
    { 
        if(this[i]!=child) 
        { 
            this[n++]=this[i]; 
        } 
    } 
     this.length=this.length-1*1;
}
/**
 * 获取两个数之间的随机数
 * @param s 开始数值
 * @param e 结束数值
 * @param pos 小数位数
 * @return 数值
 */
function getRandom(s,e,pos)
{
    var random = Math.random()*(e-s+1)+s;
    random = random.toFixed(pos);
    return random;
}
/**
*校验字符串中只能是数字
*@param str  字符串
*
* 
*/
String.prototype.trim=function(){
   return this.replace(/(^\s*)|(\s*$)/g,"");//去掉字符串中的空格
}
function IsNum(s){
	if(s==null){
		
		return;
	}
	s= s.trim();
	if(s=="-"||s==""){
		return false;
	}
	if(s!=null){
		if(s.length>=2){
			var str=s.substr(0,2);
			if(str=="-."){	
				return false;	
			}
		}
		
	}
	if(s!=null){
		var r,re;
		re = /\-{0,1}\d*\.{0,1}\d*/i; //\d表示数字,*表示匹配多个数字
		r = s.match(re);
		re2=/[0]{1,}\d{1,}/i;
		r2=s.match(re2);
		if(r2==s){return false;}
		return (r==s)?true:false;
	}
	
	return false;
}
//格式化空字符串
function formatNullString(oldstr,newstr){
	var str = oldstr;
	if(oldstr==null || oldstr=="null" || typeof(oldstr)=="undefined"){
		str = newstr; 
	}
	return str;
}

/**
* 获取两个数之间的随机数
* 传入参数{a:最小值,b:最大值,p:保留小数位}
* 创建人：雷志强 2013-07-22
*/
function getRandom(a,b,p){
	var c = a + (b-a)*Math.random();
	c = c.toFixed(p);
	return c
}

/**
*功能描述：根据时间插件的值，如果相差大于7天，那么不成立
* 创建人:龚伟成
* 创建时间: 2013-09-5
**/
function  checkDateTime(startDate ,  endDate){
	//这个地方的开始时间和结束时间都是2013-07-31格式，如果不处理比较1-31和2-1的时候会有问题
	var startTime = new Date(startDate.replace(/\-/g,"\/"));
	var endTime = new Date(endDate.replace(/\-/g,"\/"));
	var checkTime = endTime.getTime() - startTime.getTime();
	var checkDays = parseInt(checkTime/(1000*60*60*24));
	/**
	var startDT = startDate.split("-");
	var startTime = new Date(startDT[0],startDT[1],startDT[2]);
	var endDT = endDate.split("-");
	var endTime = new Date(endDT[0],endDT[1],endDT[2]);
	var checkTime = endTime.getTime() - startTime.getTime();
	var checkDays = parseInt(checkTime/(1000*60*60*24));
	*/
	return checkDays;
}
/**
*检查时间是否满足要求
* 创建人:龚伟成
* 创建时间: 2013-09-5
*/
function checkDate(){
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	var checkDate =  checkDateTime(startDate , endDate);
	return checkDate;
}
/**
*检查时间是否满足要求
* 创建人:龚伟成
* 创建时间: 2013-09-5
*/
function checkDateByStartAndEnd(startDate , endDate){
	var checkDate =  checkDateTime(startDate , endDate);
	return checkDate;
}
/*
 * 功能描述：根据服务器时间初始化时间插件
 * 
* 创建人:龚伟成
* 创建时间: 2013-09-3
*/
function initTimeByServer(){
	var serverTime = 0;
	var param = new Object();
   	param.method = "loadServerTime";
   	param.format = "yyyy-MM-dd";
   	Ajax.doSystemConfigAction(param,
  		function(map){
  			if(map.state){
  				//测试成功
  				serverTime = map.serverTime;
  				$("#startDate").val(serverTime);
  				$("#endDate").val(serverTime);
  			}
  	});
}


/**
* 将标准时间格式字符串[yyyy-MM-dd HH:mm:ss]转成日期时间对象
* 传入参数：{str:标准日期时间格式字符串，如:2013-07-22 12:54:26}
* 创建人：雷志强 2013-07-22
*/
function dateString2Date(str){
	var year = parseInt(str.substring(0,4),10);
	var month = parseInt(str.substring(5,7),10)-1*1;
	var date = parseInt(str.substring(8,10),10);
	var hour = parseInt(str.substring(11,13),10);
	var minute = parseInt(str.substring(14,16),10);
	var second = parseInt(str.substring(17,19),10);
	var d = new Date(year,month,date,hour,minute,second);
	return d;
}
//日期时间工具类
function DateUtil(){
	var MINUTE = 60;
	var HOUR = 60*60;
	var DATE = 60*60*24;
	//按指定格式格式化日期时间
	this.formatDate = function(format,d){
		var year = d.getFullYear();
		var month = d.getMonth()+1;
		var date = d.getDate();
		var hour = d.getHours();
		var minute = d.getMinutes();
		var second = d.getSeconds();
		month=month<10?"0"+month:month;
		date=date<10?"0"+date:date;
		hour=hour<10?"0"+hour:hour;
		minute=minute<10?"0"+minute:minute;
		second=second<10?"0"+second:second;
		var datetime = format.replace("yyyy",year);
		datetime = datetime.replace("MM",month);
		datetime = datetime.replace("dd",date);
		datetime = datetime.replace("HH",hour);
		datetime = datetime.replace("mm",minute);
		datetime = datetime.replace("ss",second);
		return datetime;
	}
	//将日期时间按指定格式转化成字符串
	this.date2String = function(formater,date){
		if(formater==null || formater==""){
			formater = "yyyy-MM-dd HH:mm:ss";
		}
		if(date==null || date==""){
			date = new Date();
		}
		return this.formatDate(formater,date);
	}
	//将当前日期时间按指定格式转化成字符串
	this.nowDate2String = function(formater){
		return this.formatDate(formater,new Date());
	}
	//将标准日期时间格式(yyyy-MM-dd HH:mm:ss)字符串转成日期对象
	this.dateString2Date = function(dateString){
		var year = parseInt(dateString.substring(0,4),10);
		var month = parseInt(dateString.substring(5,7),10)-1*1;
		var date = parseInt(dateString.substring(8,10),10);
		var hour = parseInt(dateString.substring(11,13),10);
		var minute = parseInt(dateString.substring(14,16),10);
		var second = parseInt(dateString.substring(17,19),10);
		var d = new Date(year,month,date,hour,minute,second);
		return d;
	}
	//将标准日期时间格式(yyyy-MM-dd HH)字符串转成日期对象
	this.dateString2Date2 = function(str){
		var yearMouthDay = str.substring(0,10);
		var hour = parseInt(str.substring(11,13),10);
		var date = yearMouthDay + " " + hour + ":00:00" ;
		return date ;
	}
	//通过分钟计算时间差{dateString:标准时间字符串2013-08-07 12:00:00,amount:时间差数值[正数则是计算后面的时间，负数则是计算前面的时间]}
	this.calculateByMinute = function(dateString,amount){
		return this.calculate(dateString,MINUTE,amount);
	}
	//通过小时计算时间差{dateString:标准时间字符串2013-08-07 12:00:00,amount:时间差数值[正数则是计算后面的时间，负数则是计算前面的时间]}
	this.calculateByHour = function(dateString,amount){
		return this.calculate(dateString,HOUR,amount);
	}
	//通过天计算时间差{dateString:标准时间字符串2013-08-07 12:00:00,amount:时间差数值[正数则是计算后面的时间，负数则是计算前面的时间]}
	this.calculateByDate = function(dateString,amount){
		return this.calculate(dateString,DATE,amount);
	}
	//通过指定的类型,时间差值计算时间差
	this.calculate = function(dateString,field,amount){
		var d = dateString2Date(dateString);
		var oldtime = d.getTime();
		var space = field*amount*1000;
		var newtime = oldtime + space;
		var newDate = new Date(newtime);
		return this.date2String("yyyy-MM-dd HH:mm:ss",newDate);
	}
	/** 比较两个日期时间大小,返回时间差
	* startDate:开始时间,标准时间字符串2013-08-07 12:00:00
	* endDate:结束时间,标准时间字符串2013-08-07 12:00:00
	* 返回两个时间的差,正数说明开始时间比结束时间小，反之比它大
	*/
	this.diffDateTime = function(startDate,endDate){
		var sDate = this.dateString2Date(startDate);
		var eDate = this.dateString2Date(endDate);
		var time = null;
		if(sDate!=null&&eDate!=null){
			time = eDate.getTime()-sDate.getTime();
		}
		return time;
	}
}
var dateUtil = new DateUtil();

/**
* 获取指定格式的日期时间字符串
* 传入参数：{d:日期时间对象,format:格式化字符串,如:yyyy-MM-dd HH:mm:ss}
* 创建人：雷志强 2013-07-22
*/
function formatDate(d,format){
	var year = d.getFullYear();
	var month = d.getMonth()+1;
	var date = d.getDate();
	var hour = d.getHours();
	var minute = d.getMinutes();
	var second = d.getSeconds();
	month=month<10?"0"+month:month;
	date=date<10?"0"+date:date;
	hour=hour<10?"0"+hour:hour;
	minute=minute<10?"0"+minute:minute;
	second=second<10?"0"+second:second;
	var datetime = format.replace("yyyy",year);
	datetime = datetime.replace("MM",month);
	datetime = datetime.replace("dd",date);
	datetime = datetime.replace("HH",hour);
	datetime = datetime.replace("mm",minute);
	datetime = datetime.replace("ss",second);
	return datetime;
}
/**
* 16进制颜色值转成rgb值
* 传入参数：{hex:16进制颜色值，例如：FFCC00}
* 创建人：雷志强 2013-07-24
*/
function colorHexToRgb(hex){
	var rgb = new Array();
	rgb[0] = "255";
	rgb[1] = "255";
	rgb[2] = "255";
	if(hex.length==6){
		var r = hex.substring(0,2);
		var g = hex.substring(2,4);
		var b = hex.substring(4,6);
		rgb[0] = parseInt(r,16);
		rgb[1] = parseInt(g,16);
		rgb[2] = parseInt(b,16);
	}else{
		alert("请输入正确的16进制颜色值，例如：FFCC00");
	}
	return rgb.join(",");
}
/**
* 颜色rgb值转换成16进制颜色值
* 传入参数：{rgb:rgb值，例如：255,255,255}
* 创建人：雷志强 2013-07-24
*/
function colorRgbToHex(rgb){
	var hex = new Array();
	hex[0] = "FF";
	hex[1] = "FF";
	hex[2] = "FF";
	if(rgb!=null&&rgb!=""){
		var rgbArr = rgb.split(",");
		if(rgbArr.length==3){
			hex[0] = parseInt(rgbArr[0],10).toString(16);
			hex[1] = parseInt(rgbArr[1],10).toString(16);
			hex[2] = parseInt(rgbArr[2],10).toString(16);
			hex[0] = parseInt(hex[0],16)<16?"0"+hex[0]:hex[0];
			hex[1] = parseInt(hex[1],16)<16?"0"+hex[1]:hex[1];
			hex[2] = parseInt(hex[2],16)<16?"0"+hex[2]:hex[2];
		}else{
			alert("输入的RGB值["+rgb+"]不正确，请输入正确的RGB值,例如：255,244,244");
		}
	}
	return hex.join("");
}
/**
* 动态创建色标图例
* 传入参数：divid:图例容器div对象ID，colorInfo:图例信息，例如{unit:"单位:dBZ",width:30,height:20,colors:[{value:0,color:255,255,255},{value:10,color:255,255,150}]}
* 创建人：雷志强 2014-04-17
*/
function createColorLegent(divid,colorInfo){
	var unit = "";
	var name = "未知";
	var width = 30;
	var height = 20;
	var ttop = 0;
	var max = "--";
	var min = "--";
	var tmpArr = new Array();
	var colors = new Array();
	if(colorInfo.name){
		name = colorInfo.name;
	}
	if(colorInfo.unit){
		unit = colorInfo.unit;
	}
	if(colorInfo.width){
		width = colorInfo.width;
	}
	if(colorInfo.height){
		height = colorInfo.height;
	}
	if(colorInfo.colors){
		colors = colorInfo.colors;
	}
	ttop = parseInt(height/2);
	var sb = new StringBuilder();
	sb.append('<table class="dataTable" border="1" bordercolor="#B1C7DE" cellpadding="0" cellspacing="0">');
	sb.append('<caption>图例</caption>');
	sb.append('<tr><th>'+name+'('+unit+')</th></tr>');
	sb.append('<tr>');
	sb.append('<td style="background:#EEEEEE;padding:5px;">');
	sb.append('<div style="float:left;">');
	for(var i=0;i<colors.length;i++){
		sb.append('<div class="color_rgb" style="border:1px solid #AAAAAA;margin-top:-1px;background-color:'+colors[i].rgb+'"></div>');
	}
	sb.append('</div>');
	sb.append('<div style="float:left;margin-top:'+ttop+'px;margin-left:3px;">')
	for(var i=0;i<colors.length-1;i++){
		tmpArr = colors[i].value.split(",");
		if(tmpArr.length==2){
			min = tmpArr[0];
			max = tmpArr[1];
		}
		sb.append('<div class="color_val">'+min+'</div>');
	}
	sb.append('</div>');
	sb.append('</td>');
	sb.append('</tr>');
	sb.append('</table>');
	$("#"+divid).html(sb.toString());
	$("#"+divid+" .color_rgb").css({"width":width+"px","height":height+"px"});
	$("#"+divid+" .color_val").css({"height":height+"px","line-height":height+"px","margin-top":"1px"});
}

/**
* 下拉列表对象
* 传入参数：空
* 创建人：雷志强 2013-07-22
*/
function Select(select_id){
	var selectObj=document.getElementById(select_id);
	//清除下拉列表所有选项{select_id:下拉列表对象select的ID}
	this.clear=function(){
		if(selectObj!=null){
			selectObj.options.length=0;
		}
	}
	//动态添加下拉列表选项{select_id:下拉列表对象select的ID,text:下拉列表文本值,value:下拉列表文本对应的值}
	this.addOption=function(text,value){
		var select_opt = document.createElement("option");
	    select_opt.text = text;
	    select_opt.value = value;
	    if(selectObj!=null){
	    	selectObj.options.add(select_opt);
	    }
	}
	//设置指定的类型的选项选中{select_id:下拉列表对象select的ID,type:通过文本还是值设置选中[text,value],str:type对象匹配的值}
	this.setSelect=function(type,str){
		if(selectObj!=null){
			for(var i=0;i<selectObj.length;i++){
    			if(type=="text"){
		    		if(selectObj[i].text == str ){
		    			selectObj[i].selected=true;break;
		    		}
		    	}else if(type=="value"){
		    		if(selectObj[i].value == str ){
		    			selectObj[i].selected=true;break;
		    		}
		    	}
    		}
		}
	}
}
//var select = new Select();
function customDataPageing(pageingId,rows,pagesize,callback){
	var totalCount = rows;
	var pageSize = 10;
	var totalPage = 0;
	var endFor = 0;
	var currentPage = 1;
	if(pagesize){
		pageSize = pagesize;
		endFor = pageSize;
	}
	if(rows>0){
		if(rows%pageSize==0){
			totalPage = parseInt(rows/pageSize,10);
		}else{
			totalPage = parseInt(rows/pageSize,10) + 1;
		}
	}else{
		totalPage = 1;
		endFor = 0;
		currentPage = 1;
	}
	if(currentPage>totalPage){
		currentPage = totalPage;
	}
	if(currentPage==totalPage){
		endFor = totalCount - (currentPage-1)*pageSize;
	}else{
		endFor = pageSize;
	}
	$("#"+pageingId+" .totalCount").html(totalCount);
	$("#"+pageingId+" .pageNumber").html(currentPage);
	$("#"+pageingId+" .totalPage").html(totalPage);
	$("#"+pageingId+" .pageSize").html(pageSize);
	$("#"+pageingId+" .startRecord").html((currentPage-1)*pageSize+1);
	$("#"+pageingId+" .endRecord").html((currentPage-1)*pageSize+endFor);
	$("#"+pageingId+" li a").unbind("click");
	$("#"+pageingId+" li a").bind("click",function(){
		var pageMode = $(this).attr("name");
		switch(pageMode){
			case "pageFirst":
				currentPage = 1;
			break;
			case "pagePrev":
				currentPage = currentPage - 1;
			break;
			case "pageNext":
				currentPage = currentPage + 1;
			break;
			case "pageLast":
				currentPage = totalPage;
			break;
			default:break;
		}
		if(currentPage<=0){
			currentPage = 1;
		}else if(currentPage>=totalPage){
			currentPage = totalPage;
		}
		if(currentPage==totalPage){
			endFor = totalCount - (currentPage-1)*pageSize;
		}else{
			endFor = pageSize;
		}
		$("#"+pageingId+" .totalCount").html(totalCount);
		$("#"+pageingId+" .pageNumber").html(currentPage);
		$("#"+pageingId+" .totalPage").html(totalPage);
		$("#"+pageingId+" .pageSize").html(pageSize);
		$("#"+pageingId+" .startRecord").html((currentPage-1)*pageSize+1);
		$("#"+pageingId+" .endRecord").html((currentPage-1)*pageSize+endFor);
		var pageInfo = {pageNumber:currentPage,pageSize:pageSize,endFor:endFor};
		window[callback](pageInfo);
	});
	window[callback]({pageNumber:currentPage,pageSize:pageSize,endFor:endFor});
}

/**
用于展示时间按钮，如果该数值小于10，那么补0
创建人：龚伟成  创建时间：2013/8/15
*/
function getShowNum(val){
	if(val < 10){
		return "0"+val;
	}
	return val;
}


/**
 * 字符串拼接函数
 */
function StringBuilder()
{
    this.strings=new Array(); 
}
/**
 * 字符串拼接函数--append方法
 * @param {Object} value 要拼接的内容
 */
StringBuilder.prototype.append = function(value)
{
    if(value)
    {
        this.strings.push(value);
    }
}
/**
 * 字符串拼接函数--清除所有内容
 */
StringBuilder.prototype.clear = function ()
{
    this.strings.length = 0;
}
/**
 * 字符串拼接函数--转换成字符串
 * @return 字符串 
 */
StringBuilder.prototype.toString = function ()
{
    return this.strings.join("");
}

Array.prototype.remove=function(dx) 
{ 
    if(isNaN(dx)||dx>this.length){return false;} 
    for(var i=0,n=0;i<this.length;i++) 
    { 
        if(this[i]!=this[dx]) 
        { 
            this[n++]=this[i]; 
        } 
    } 
    this.length=this.length-1*1;
}
Array.prototype.removeChild=function(child){
	if(child==null){return;}
	for(var i=0,n=0;i<this.length;i++) 
    { 
        if(this[i]!=child) 
        { 
            this[n++]=this[i]; 
        } 
    } 
     this.length=this.length-1*1;
}
Array.prototype.hasChild=function(child){
	var hasChild = false;
	if(child==null){return;}
	for(var i=0,n=0;i<this.length;i++) 
    { 
        if(this[i]==child) 
        { 
            hasChild = true;
            break;
        } 
    } 
    return hasChild;
}
function JsArrayRemove(arr,child){
	var newArr = new Array();
	for(var i=0;i<arr.length;i++){
		if(arr[i]!=child){
			newArr.push(arr[i]);
		}
	}
	return newArr;
}
/**
 * WEB墨卡托转经纬度
 * @param px 墨卡托投影X坐标
 * @param py 墨卡托投影Y坐标
 * @return
 */
function Mercator2LonLat(px,py){
	var xy = new Array();
	var x = px/20037508.342789*180;
	var y = py/20037508.342789*180;
	var M_PI = Math.PI;
	y = 180/M_PI*(2*Math.atan(Math.exp(y*M_PI/180))-M_PI/2);
	xy[0] = x.toFixed(3);
	xy[1] = y.toFixed(3);
	return xy;
}
/**
 * 经纬度转WEB墨卡托
 * @param px 经度
 * @param py 纬度
 * @return WEB墨卡托XY坐标
 */
function LonLat2Mercator(px,py){
	px = parseFloat(px);
	py = parseFloat(py);
	var xy = new Array();
	var x = px*20037508.342789/180;
	var y = Math.log(Math.tan((90+py)*Math.PI/360))/(Math.PI/180);
	y = y * 20037508.342789/180;
	xy[0] = x.toFixed(8);
	xy[1] = y.toFixed(8);
	return xy;
}
/***************常用工具类**********************/

window.jsTool = {};
jsTool.formatFileSize = function(fileSize){
	var fileSizeString = "";
	if(fileSize<1024){
		fileSizeString = parseFloat(fileSize).toFixed(2)+"B";
	}else if(fileSize<1048576){
		fileSizeString = parseFloat(fileSize/1024).toFixed(2)+"K";
	}else if(fileSize<1073741824){
		fileSizeString = parseFloat(fileSize/1048576).toFixed(2)+"M";
	}else{
		fileSizeString = parseFloat(fileSize/1073741824).toFixed(2)+"G";
	}
	return fileSizeString;
}
jsTool.paramToHashMap=function(param){
	var tmpArr = new Array();
	if(param!=null){
		tmpArr = param.split("&");
	}
	var hashMap = new Object();
	for(var i=0;i<tmpArr.length;i++){
		var para = tmpArr[i];
		if(para.indexOf("=")!=-1){
			var kv = para.split("=");
			hashMap[kv[0]]=decodeURI(kv[1]);
		}
	}
	return hashMap;
}

//打印对象
function printObject(obj){
	for(var att in obj){
		alert(att+"="+obj[att]);
	}
}
//检查数据库字段返回的值是否为空，为空则做相应的处理
function checkColumnNull(oldVal,newVal){
	var val = oldVal;
	if(oldVal==null || oldVal=="null"){
		val = newVal;
	}
	return val;
}

//获取数据数组最大最小值
function getDataMinMax(data,field){
	var obj = new Object();
	obj.min = 0;
	obj.max = 0;
	var count = 0;
	if(data!=null){
		count = data.length;
		var maxValue = 0;
		var minValue = 0;
		var value = 0;
		for(var i=0;i<data.length;i++){
			if(data[i][field]!=null){
				value = parseFloat(data[i][field]);
			}else{
				value = 0;
			}
			if(i==0){
				minValue = maxValue = value;
			}
			if(maxValue<=value){
				maxValue = value;
			}
			if(minValue>=value){
				minValue = value;
			}
		}
		obj.min = minValue;
		obj.max = maxValue;
	}
	return obj;
}
/************表格排序相关*******************/
/*
* 快速排序，按某个属性，或按“获取排序依据的函数”，来排序.
* @method soryBy
* @static
* @param {array} arr 待处理数组
* @param {string|function} prop 排序依据属性，获取
* @param {boolean} desc 降序
* @return {array} 返回排序后的新数组
*/ 
function sortByAtt(arr,prop,desc){
	var props=[];
	var ret=[];
	var len = arr.length;
	if(typeof(prop)=='string'){
		for(var i=0;i<len;i++){
			var oI=arr[i];
			(props[i]=new String(oI&&oI[prop] || ""))._obj=oI;
		}
	}else if(typeof(prop)=='function'){
		for(var i=0;i<len;i++){
			var oI = arr[i];
			(props[i]=new String(oI&&prop(oI) || ""))._obj=oI;
		}
	}else{
		throw "参数类型错误"
	}
	props.sort();
	for(var i=0;i<len;i++){
		ret[i]=props[i]._obj;
	}
	if(desc){
		ret.reverse();
	}
	return ret;
}
//数值降序排序
function sortNumberDesc(a,b){
	return b-a;
}
//数值升序排序
function sortNumberAsc(a,b){
	return a-b;
}
//当前排序的表格对象ID
var sortTbody = "";
//存入点击列的每一个TD的内容；
var aTdCont = [];

//点击列的索引值
var thi = 0;

//重新对TR进行排序
var setTrIndex = function(tdIndex){
	for(var i=0;i<aTdCont.length;i++){
		var trCont = aTdCont[i];
		$("#"+sortTbody+" tr").each(function(){
			var thisText = $(this).children("td:eq("+tdIndex+")").text();
			if(thisText==trCont){
				$("#"+sortTbody).append($(this));
			}
		});
	}
}
//比较函数的参数函数
var compare_down = function(a,b){
	return a-b;
}
var compare_up = function(a,b){
	return b-a;
}
//比较函数
var fSort = function(compare){
	aTdCont.sort(compare);
}

//取出TD的值，并存入数组，取出前二个TD值;
var fSetTdCont = function(thIndex){
	$("#"+sortTbody+" tr").each(function(){
		var tdCont = $(this).children("td:eq("+thIndex+")").text();
		aTdCont.push(tdCont);
	});
}
//点击时需要执行的函数
var clickFun = function(tbody,thIndex){
	aTdCont = [];
	sortTbody = tbody;
	//获取点击当前列的索引值
	var nThCount = thIndex;
	//调用sort函数，取出要比较的数据
	fSetTdCont(nThCount);
}
/*************************jQuery对表格排序********************************/
jQuery.extend({
	sortTable:function(args){
		var $=jQuery,
		table=$("#"+args.tableId),
		allTr=$("tbody > tr",table).get(),
		img=$("<img src='' style='margin-left:5px;'/>"),
		tHead=$("thead > tr > th",table).not($(".noSort"));
		if(tHead.length==0){
			tHead=$("thead > tr > td",table).not($(".noSort"));
		}
		tHead.each(function(){
			$(this).unbind("click");
			$(this).find("img").remove();
			//排序的方向
			$(this).data("dir",1)
			.data("index",$(this).prevAll().length)
			.css("cursor","pointer")
			.attr("title","单击排序")
			.click(function(){
				_$this=$(this);
				allTr.sort(function(a,b){
					var td1=$(a).children("td").eq(_$this.data("index")).text();
					if(td1=="--"){
						td1 = -9;
					}
					td1=isNaN(Number(td1))?td1:Number(td1);
					var td2=$(b).children("td").eq(_$this.data("index")).text();
					if(td2=="--"){
						td2 = -9;
					}
					td2=isNaN(Number(td2))?td2:Number(td2);
					var dir=_$this.data("dir");
					if(td1>td2){
						return dir;
					}else if(td1<td2){
						return -dir;
					}else{
						return 0;
					}
				});
				$(this).data("dir",-$(this).data("dir"));
				$("tbody",table).empty();
				$(allTr).each(function(){
					$("tbody",table).append($(this));
				});
				jQuery("#"+args.tbodyId+" tr").removeClass("odd");
				jQuery("#"+args.tbodyId+" tr").removeClass("even");
				setTableMouseEvent(args.tbodyId);
				$(this).append(img.attr("src",args.imgPath+($(this).data("dir")==1?"icon_comn_sortdown":"icon_comn_sortup")+".gif"));
			});
		})
	}
});

function pagerFilter(data){
	if (typeof data.length == 'number' && typeof data.splice == 'function'){	// is array
		data = {
			total: data.length,
			rows: data
		}
	}
	var dg = $(this);
	var opts = dg.datagrid('options');
	var pager = dg.datagrid('getPager');
	pager.pagination({
		beforePageText:"第",
		afterPageText:"/{pages}页",
		displayMsg:"当前显示：从 {from} 到 {to} 条，共 {total} 条记录",
		onSelectPage:function(pageNum, pageSize){
			opts.pageNumber = pageNum;
			opts.pageSize = pageSize;
			pager.pagination('refresh',{
				pageNumber:pageNum,
				pageSize:pageSize
			});
			dg.datagrid('loadData',data);
		}
	});
	if (!data.originalRows){
		data.originalRows = (data.rows);
	}
	var start = (opts.pageNumber-1)*parseInt(opts.pageSize);
	var end = start + parseInt(opts.pageSize);
	data.rows = (data.originalRows.slice(start, end));
	return data;
}
/**
 * @method 检查所查点坐标是否在多边形内
 * @param 多边形点序列[x1,y1;x2,y2;x3,y3] regPntStr
 * @param 要判断的点X坐标  XDot
 * @param 要判断的点Y坐标 YDot
 * @return boolean 点在多边形里面返回为true,不在为false
 */
function checkDotInPolygon(regPntStr,XDot,YDot) {
	var t_bCheckFlg = false;
	var strArr = regPntStr.split(";");
	var pntNum = strArr.length;
	var PQXarray = new Array();
	var PQYarray = new Array();
	for (var i = 0; i < pntNum; i++) {
		PQXarray[i] = parseFloat(strArr[i].split(",")[0]);
		PQYarray[i] = parseFloat(strArr[i].split(",")[1]);
	}
	var t_cout = 0;
	if ((PQXarray[PQXarray.length - 1] == XDot)
			&& (PQYarray[PQYarray.length - 1] == YDot))
		t_bCheckFlg = true;
	else {
		if (((PQYarray[PQYarray.length - 1] > YDot) && (PQYarray[0] < YDot))
				|| ((PQYarray[PQYarray.length - 1] < YDot) && (PQYarray[0] > YDot))) {
			var t_X = (PQXarray[0] * YDot
					+ PQXarray[PQXarray.length - 1] * PQYarray[0]
					- PQXarray[PQXarray.length - 1] * YDot - PQXarray[0]
					* PQYarray[PQYarray.length - 1])
					/ (PQYarray[0] - PQYarray[PQYarray.length - 1]);
			if (t_X == XDot)
				t_bCheckFlg = true;
			else if (t_X > XDot)
				t_cout++;
		}
	}
	if (!t_bCheckFlg) {
		for (var i = 0; i < (PQXarray.length - 1); i++) {
			if ((PQXarray[i] == XDot) && (PQYarray[i] == YDot)) {
				t_bCheckFlg = true;
				break;
			} else {
				if (((PQYarray[i] > YDot) && (PQYarray[i + 1] < YDot))
						|| ((PQYarray[i] < YDot) && (PQYarray[i + 1] > YDot))) {
					var t_X = (PQXarray[i] * YDot + PQXarray[i + 1]
							* PQYarray[i] - PQXarray[i + 1] * YDot - PQXarray[i]
							* PQYarray[i + 1])
							/ (PQYarray[i] - PQYarray[i + 1]);
					if (t_X == XDot) {
						t_bCheckFlg = true;
						break;
					} else if (t_X > XDot)
						t_cout++;
				}
			}
		}
	}
	if ((!t_bCheckFlg) && (t_cout % 2 == 1))
		t_bCheckFlg = true;
	return t_bCheckFlg;
}
/**
 * 判断一个点是否在圆内
 * @author 雷志强
 * @param centerX 圆心坐标X
 * @param centerY 圆心坐标Y
 * @param radius 圆半径
 * @param px 判断点的X坐标
 * @param py 判断点的Y坐标
 * @return 在里面返回true，不在里面返回false
 */
function inCircle(centerX,centerY,radius,px,py){
	var a = Math.sqrt(Math.pow(px-centerX,2)+Math.pow(py-centerY,2));
	if(a>radius){
		return false;
	}else {
		return true;
	}
}

//距离转换成半径(单位：米)
function distanceToCircle(raidusMile){
	var degree = (24901*1609)/360.0;
    var dpmLat = 1/degree;
    var radiusLonLat = dpmLat*raidusMile;
    return radiusLonLat;
}

/**
 * 根据经纬度和半径计算经纬度范围
 * @author 雷志强
 * @param centerX 圆心坐标X
 * @param centerY 圆心坐标Y
 * @param radius 圆半径（单位：米）
 * @return 返回圆外包矩形范围及半径信息
 */
function getCircleRect(lat,lon,raidus){
	var circleInfo = new Object();
	var latitude = lat;  
    var longitude = lon;
    
    var degree = (24901*1609)/360.0;
    var raidusMile = raidus;
    
    var dpmLat = 1/degree;
    var radiusLat = dpmLat*raidusMile;
    var minLat = latitude - radiusLat;
    var maxLat = latitude + radiusLat;
    var mpdLng = degree*Math.cos(latitude * (Math.PI/180));
    var dpmLng = 1 / mpdLng;
    var radiusLng = dpmLng*raidusMile;
    var minLng = longitude - radiusLng;
    var maxLng = longitude + radiusLng;
    circleInfo.xmin = minLng;
    circleInfo.ymin = minLat;
    circleInfo.xmax = maxLng;
    circleInfo.ymax = maxLat;
    circleInfo.r = radiusLng;
    return circleInfo;
}
//获取多边形外包矩形范围
function getPolygonRect(pnts){
	var rect = new Object();
	var length = pnts.length;
	var pnt = null;
	var px = 0.0;
	var py = 0.0;
	var xmin = 0.0;
	var ymin = 0.0;
	var xmax = 0.0;
	var ymax = 0.0;
	for(var i=0;i<length;i++){
		pnt = pnts[i].split(",");
		px = parseFloat(pnt[0]);
		py = parseFloat(pnt[1]);
		if(i==0){
			xmin=xmax=px;
			ymin=ymax=py;
		}else{
			if(px<=xmin){
				xmin=px;
			}else if(px>=xmax){
				xmax=px;
			}
			if(py<=ymin){
				ymin=py;
			}else if(py>=ymax){
				ymax=py;
			}
		}
	}
	rect.xmin = xmin;
    rect.ymin = ymin;
    rect.xmax = xmax;
    rect.ymax = ymax;
	return rect;
}
var EARTH_RADIUS = 6378.137; //地球半径(单位：公里)
function rad(d) {
    return d * Math.PI / 180.0;
}
//计算两个经纬度点的距离(单位：公里)
function getDistance(lon1, lat1, lon2, lat2) {
    var radLat1 = rad(lat1);
    var radLat2 = rad(lat2);
    var a = radLat1 - radLat2;
    var b = rad(lon1) - rad(lon2);

    var s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
    s = s * EARTH_RADIUS;
    s = Math.round(s * 10000) / 10000;
    return s;
}
function getBroswerInfo(){
	var brow=$.browser;
	var bInfo="";
	if(brow.msie) {bInfo="Microsoft Internet Explorer "+brow.version;}
	if(brow.mozilla) {bInfo="Mozilla Firefox "+brow.version;}
	if(brow.safari) {bInfo="Apple Safari "+brow.version;}
	if(brow.opera) {bInfo="Opera "+brow.version;}
	$("#browser").html(bInfo);
}
function simpleCopy(o){
    if (o instanceof Array) {
        var n = [];
        for (var i = 0; i < o.length; ++i) {
            n[i] = o[i];
        }
        return n;
    } else if (o instanceof Object) {
        var n = {}
        for (var i in o) {
            n[i] = o[i];
        }
        return n;
    }
}
/**
 * 度分秒经纬度转数值经纬度
 */
function degreenToNumLonlat(dfmstr){
	var d = 0.0;
	//0°00′00.0″
	if(dfmstr!=null&&dfmstr!=""){
		var dindex = dfmstr.indexOf("°");
		var findex = dfmstr.indexOf("′");
		var mindex = dfmstr.indexOf("″");
		//alert(dindex+","+findex+","+mindex);
		if(dindex!=-1&&findex!=-1&&mindex!=-1){
			var dstr = dfmstr.substring(0,dindex);
			var fstr = dfmstr.substring(dindex+1,findex);
			var mstr = dfmstr.substring(findex+1,mindex);
			//alert(dstr+","+fstr+","+mstr);
			var dd = parseFloat(dstr);
			var fd = parseFloat(fstr);
			var md = parseFloat(mstr);
			//alert(dd+","+fd+","+md);
			d = dd + (fd*60+md)/3600;
		}else{
			alert("请输入度分秒格式的经纬度字符串！");
		}
	}else{
		alert("请输入你要转换的度分秒经纬度字符串！");
	}
	return d;
}

/**
 * 数值经纬度转度分秒经纬度
 */
function numToDegreenLonlat(lonlat){
	var lonlatstr = "";
	//alert(lonlat);
	if(lonlat>=-180&&lonlat<=180){
		var degree = Math.floor(lonlat);
		var tmp = (lonlat - degree)*3600;
		var minute = Math.floor(tmp/60);
		var second = tmp - minute*60;
		//alert(degree+","+minute+","+second);
		lonlatstr = parseFloat(degree).toFixed(0)+"°";
		if(minute<10){
			lonlatstr += "0"+parseFloat(minute).toFixed(0)+"′";
		}else{
			lonlatstr += parseFloat(minute).toFixed(0)+"′";
		}
		if(second<10){
			lonlatstr += "0"+parseFloat(second).toFixed(1)+"″";
		}else{
			lonlatstr += parseFloat(second).toFixed(1)+"″";
		}
	}else{
		alert("输入的数值超出经纬度范围[-180,180]！");
	}
	return lonlatstr;
}

//获取url中的参数
function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}
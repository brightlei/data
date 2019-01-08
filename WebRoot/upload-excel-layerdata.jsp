<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String table = request.getParameter("table");
	String template = request.getParameter("template");
	log("table="+table);
	log("template="+template);
	//log(table+",,,"+template);
%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Excel表格上传</title>
<script src="jslib/jeasyui.js" type="text/javascript"></script>
<script src="jslib/uploadify/jquery.uploadify.min.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="jslib/uploadify/uploadify.css">
<style type="text/css">
body {
    font: 13px Arial, Helvetica, Sans-serif;
}
.tbform td{padding:0px 6px;}
.ulcss li{display:inline-block;margin:3px;}
</style>
    <script type="text/javascript">
    $(function(){  
        $("#uploadify").uploadify({      
            'debug': false, //开启调试  
            'auto': false, //是否自动上传     
            'swf': 'jslib/uploadify/uploadify.swf',  //引入uploadify.swf    
            'uploader': 'json/Upload!importLayerDataExcel',//请求路径    
            'queueID': 'fileQueue',//队列id,用来展示上传进度的  
            'cancelImg': 'jslib/uploadify/uploadify-cancel.png',
            'width':'75',  //按钮宽度    
            'height':'24',  //按钮高度  
            'queueSizeLimit':1,//同时上传文件的个数    
            'fileTypeDesc':'支持格式:xls/xlxs',//可选择文件类型说明  
            'fileTypeExts':'*.xls;*.xlxs', //控制可上传文件的扩展名
            'multi':false,  //允许多文件上传    
            'formData':{"table":"<%=table%>"},
            'buttonText':'文件上传',//按钮上的文字    
            'fileSizeLimit':'20MB', //设置单个文件大小限制
            'fileObjName':'uploadify',  //<input type="file"/>的name    
            'method':'post',    
            'removeCompleted':true,//上传完成后自动删除队列    
            'onFallback':function(){      
                alert("您未安装FLASH控件，无法上传文件！请安装FLASH控件后再试。");
            },   
            'onUploadSuccess' : function(file, data, response){//单个文件上传成功触发
            	var rsMap = eval("("+data+")");
            	if(rsMap.state){
            		$("#"+file.id).find(".data").html("上传成功");
            		top.layer.msg("批量导入【"+rsMap.count+"】条数据！",{icon:6});
            	}else{
            		$("#"+file.id).find(".data").html("上传失败！"+rsMap.error);
            	}
            },'onQueueComplete' : function(){ //所有文件上传完成
                //alert("文件上传成功!");
            },'onUploadError':function(file, errorCode, errorMsg, errorString){
            	$("#"+file.id).find(".data").html("上传失败！"+errorCode+","+errorMsg+","+errorString);
            } 
        });  
    });  
    </script>  
  </head>
  <body style="padding:5px;overflow:hidden;">
  	<ul class="ulcss">
  		<li><a class="easyui-linkbutton" iconCls="icon-hamburg-down" style="height:28px;" href="<%=template%>">模板下载</a></li>
  		<li><input type="file" id="uploadify" name="uploadify"/></li>
  		<li><a class="easyui-linkbutton" iconCls="icon-add" style="height:28px;" href="javascript:$('#uploadify').uploadify('upload','*')">导入数据</a></li>
  		<li><a class="easyui-linkbutton" iconCls="icon-cancel" style="height:28px;" href="javascript:$('#uploadify').uploadify('cancel')">取消操作</a></li>
  	</ul>
   <div id="fileQueue"></div>
  </body>  
</html>
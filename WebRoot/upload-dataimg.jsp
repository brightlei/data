<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String imgBasePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/uploadfiles"+"/";
String id = request.getParameter("id");
log("id="+id);
if(id==null){
	id="";
}
%>
<!DOCTYPE HTML>
<html>
  <head>
    <base href="<%=basePath%>">
    <title>图片上传</title>
	<script src="jslib/jeasyui.js" type="text/javascript"></script>
	<script src="jslib/uploadify/jquery.uploadify.min.js" type="text/javascript"></script>
	<link rel="stylesheet" type="text/css" href="jslib/uploadify/uploadify.css">
	<style type="text/css">
	body{font:13px Arial, Helvetica, Sans-serif,微软雅黑;}
	.tbform td{padding:0px 6px;}
	.ulcss li{display:inline-block;margin:3px;}
	#fileQueue{position:absolute;left:270px;top:0px;z-index:99;width:400px;}
	.imgul{width:100%;}
	.imgli{display:inline-block;margin:5px;border:1px solid #ccc;position:relative;}
	.imgtitle{overflow:hidden;padding-left:5px;line-height:26px;background:#f7f7f7;margin-top:-2px;}
	.imgview{width:200px;height:200px;border:0px solid red;}
	.imgop{display:none;cursor:pointer;position:absolute;left:0px;top:0px;padding:5px;background:#eee;border:0px solid #ccc;}
	</style>
	<script type="text/javascript">
    $(function(){  
        $("#uploadify").uploadify({      
            'debug': false, //开启调试  
            'auto': false, //是否自动上传     
            'swf': 'jslib/uploadify/uploadify.swf',  //引入uploadify.swf    
            'uploader': 'json/Upload!uploadDataImage',//请求路径    
            'formData':{id:"<%=id%>"},
            'queueID': 'fileQueue',//队列id,用来展示上传进度的  
            'cancelImg': 'jslib/uploadify/uploadify-cancel.png',
            'width':'75',  //按钮宽度    
            'height':'24',  //按钮高度  
            'queueSizeLimit':1,//同时上传文件的个数    
            'fileTypeDesc':'支持格式:png/gif/png',//可选择文件类型说明  
            'fileTypeExts':'*.png;*.gif;*.jpg', //控制可上传文件的扩展名
            'multi':false,  //允许多文件上传    
            'buttonText':'图片上传',//按钮上的文字    
            'fileSizeLimit':'20MB', //设置单个文件大小限制
            'fileObjName':'uploadify',  //<input type="file"/>的name    
            'method':'post',    
            'removeCompleted':true,//上传完成后自动删除队列    
            'onFallback':function(){      
                alert("您未安装FLASH控件，无法上传图片！请安装FLASH控件后再试。");
            },   
            'onUploadSuccess' : function(file, data, response){//单个文件上传成功触发
            	var rsMap = eval("("+data+")");
            	if(rsMap.state){
            		$("#"+file.id).find(".data").html("上传成功");
            		loadImages();
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
    //加载图片信息
    function loadImages(){
    	var param = {method:"queryLayerDataImage",id:"<%=id%>"};
    	doAjax("json/DataService!listAll",param,{},function(rsMap,op){
    		if(rsMap.state){
    			var data = rsMap.data;
    			cache["imgdata"]=data;
    			var count = data.length;
    			var sb = new Array();
    			var record = null;
    			for(var i=0;i<count;i++){
    				record = data[i];
    				sb.push('<li id="'+record.id+'" class="imgli"><div class="imgop"><a>添加标注</a>&nbsp;<a href="javascript:void(0)" onclick="deleteImg(\''+record.id+'\')">删除图片</a></div><img class="imgview" layer-src="<%=imgBasePath%>'+record.imgpath+'" src="<%=imgBasePath%>'+record.smallimg+'" alt="'+record.name+'"/><div class="imgtitle">'+record.name+'</div></li>');
    			}
    			$("#imglist").html(sb.join(""));
    			$("#imglist li").hover(function(){
    				$("#imglist li").find(".imgop").css("display","none");
    				$(this).find(".imgop").css("display","block");
    			},function(){
    				$(this).find(".imgop").css("display","none");
    			});
    			$("#imglist .imgview").click(function(){
    				var index = $(this).index();
    				var imgs = [];
    				var data = cache["imgdata"];
    				var imgpath = data[index].imgpath;
    				window.open("<%=imgBasePath%>"+imgpath,"_blank","width=1000,height=600");
    			});
    		}
    	});
    }
    //删除图片
    function deleteImg(imgid){
    	var param = {id:imgid};
    	doAjax("json/Web!deleteDataImg",param,{},function(rsMap,op){
    		if(rsMap.state){
    			top.layer.alert("图片删除成功！",{icon:6});
    			$("#"+imgid).remove();
    		}else{
    			top.layer.alert("图片删除失败！"+rsMap.error,{icon:5});
    		}
    	});
    }
    </script>
  </head>
  
  <body class="easyui-layout" onload="loadImages()">
  	<div data-options="region:'north'" style="height:50px;padding:5px;overflow:hidden;">
  		<ul class="ulcss">
	  		<li><input type="file" id="uploadify" name="uploadify"/></li>
	  		<li><a class="easyui-linkbutton" iconCls="icon-add" style="height:28px;" href="javascript:$('#uploadify').uploadify('upload','*')">导入数据</a></li>
	  		<li><a class="easyui-linkbutton" iconCls="icon-cancel" style="height:28px;" href="javascript:$('#uploadify').uploadify('cancel')">取消操作</a></li>
	  	</ul>
	   <div id="fileQueue"></div>
  	</div>
  	<div data-options="region:'center'" style="padding:5px;overflow-x:hidden;">
  		<ul id="imglist" class="imgul"></ul>
  	</div>
  </body>  
</html>

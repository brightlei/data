<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String id = request.getParameter("id");
if(id==null){
	id = "";
}
System.out.println(id);
%>
<!DOCTYPE html>
<html>
  <head>
    <base href="<%=basePath%>">
    <title>修改图层数据</title>
    <style type="text/css">
    .imgicon{width:30px;height:30px;margin:5px;border:1px solid #eee;padding:5px;}
    .layui-form-item{margin-bottom:5px;}
    .dataTable td{padding:5px;}
    </style>
	<script type="text/javascript" src="jslib/jeasyui.js"></script>
	<script type="text/javascript">
	function loadData(){
		var param = {method:"queryLayerDataById",id:"<%=id%>"};
		doAjax("json/DataService!listAll",param,{},function(rsMap,op){
			if(rsMap.state){
				var data = rsMap.data;
				var record = data[0];
				$("#layercode").val(record.layercode);
				$("#name").textbox("setValue",record.name);
				$("#workshop").textbox("setValue",record.workshop);
				$("#workarea").textbox("setValue",record.workarea);
				$("#mainperson").textbox("setValue",record.mainperson);
				$("#telephone").textbox("setValue",record.telephone);
				$("#description").textbox("setValue",record.description);
				$("#longitude").val(record.longitude);
				$("#latitude").val(record.latitude);
				$("#address").val(record.address);
			}
		});
	}
	function saveLayerData(){
		var layercode = $("#layercode").val();
		var name = $("#name").textbox("getValue");
		var workshop = $("#workshop").textbox("getValue");
		var workarea = $("#workarea").textbox("getValue");
		var mainperson = $("#mainperson").textbox("getValue");
		var telephone = $("#telephone").textbox("getValue");
		var description = $("#description").textbox("getValue");
		var longitude = $("#longitude").val();
		var latitude = $("#latitude").val();
		var address = $("#address").val();
		if(name==null||name==""){
			top.layer.alert("图层数据名称不能为空！请输入图层数据名称",{icon:0});
			return;
		}
		if(longitude==null||longitude==""){
			top.layer.alert("请从地图上采集数据坐标点！",{icon:0});
			return;
		}
		var param = new Object();
		param.method="editLayerData";
		param.id = "<%=id%>";
		param.layercode = layercode;
		param.name = name;
		param.workshop = workshop;
		param.workarea = workarea;
		param.mainperson = mainperson;
		param.telephone = telephone;
		param.description = description;
		param.longitude = longitude;
		param.latitude = latitude;
		param.address = address;
		doAjax("json/DataService!editData",param,{},function(rsMap,op){
			var data = rsMap.data;
			if(data>0){
				var layerIndex = top.addLayerDataIframe.replace("layui-layer-iframe","");
				top.layer.close(layerIndex);
				top.layer.alert("修改图层数据["+name+"]成功！",{icon:6});
				top.reloadLayerData();
			}else{
				top.layer.alert("修改图层数据["+name+"]失败！",{icon:5});
			}
		});
	}
	var addLayerDataIframe = "";
	//坐标点采集事件
	function registMapClickEvent(){
		top.addMapClickEvent();
	}
	//设置坐标点信息
	function setDataPoint(longitude,latitude){
		$("#longitude").val(longitude);
		$("#latitude").val(latitude);
	}
	</script>
  </head>
  <body onload="loadData()">
  	<div class="easyui-panel" fit="true" style="padding:10px;overflow:hidden;">
  		<input type="hidden" id="id" value="<%=id%>"/>
		<input type="hidden" id="layercode" value=""/>
		<table class="dataTable" border="1" bordercolor="#ccc" cellpadding="0" cellspacing="0" style="width:100%;border-collapse:collapse;">
			<tr>
				<td style="text-align:right;width:90px;">数据名称：</td>
				<td><input id="name" class="easyui-textbox" data-options="prompt:'请输入图层数据名称'" style="width:100%;height:30px;"></td>
			</tr>
			<tr>
				<td style="text-align:right;">所属车间：</td>
				<td><input id="workshop" class="easyui-textbox" data-options="prompt:'请输入图层数据所属车间'" style="width:100%;height:30px;"></td>
			</tr>
			<tr>
				<td style="text-align:right;">所属工区：</td>
				<td><input id="workarea" class="easyui-textbox" data-options="prompt:'请输入图层数据所属工区'" style="width:100%;height:30px;"></td>
			</tr>
			<tr>
				<td style="text-align:right;">负责人：</td>
				<td><input id="mainperson" class="easyui-textbox" data-options="prompt:'请输入图层数据负责人'" style="width:100%;height:30px;"></td>
			</tr>
			<tr>
				<td style="text-align:right;">联系方式：</td>
				<td><input id="telephone" class="easyui-textbox" data-options="prompt:'请输入图层数据负责人联系方式'" style="width:100%;height:30px;"></td>
			</tr>
			<tr>
				<td style="text-align:right;">坐标信息：</td>
				<td style="position:relative;">
					<input type="hidden" id="address" value=""/>
					<p>经度：<input type="text" id="longitude" style="width:160px;" disabled/></p>
					<p style="margin-top:5px;">纬度：<input type="text" id="latitude" style="width:160px;" disabled/></p>
					<span style="position:absolute;left:220px;top:10px;"><a href="javascript:void(0)" class="easyui-linkbutton c9" style="width:100px;height:30px;" onclick="registMapClickEvent()">采集坐标点</a></span>
				</td>
			</tr>
			<tr>
				<td style="text-align:right;">描述信息：</td>
				<td><input id="description" class="easyui-textbox" data-options="prompt:'请输入图层数据描述信息',multiline:true" style="width:100%;height:90px;"></td>
			</tr>
			<tr>
				<td colspan="2">
					<a href="javascript:void(0)" class="easyui-linkbutton c9" data-options="iconCls:'icon-ok'" onclick="saveLayerData()" style="width:60px;height:30px;">提交</a>
				</td>
			</tr>
		</table>
	</div>
<script type="text/javascript">
	
</script>
  </body>
</html>

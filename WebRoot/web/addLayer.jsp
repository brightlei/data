<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
System.out.println(request.getRequestURI());
System.out.println(request.getQueryString());
String code = request.getParameter("code");
String pcode = request.getParameter("pcode");
%>

<!DOCTYPE html>
<html>
  <head>
    <base href="<%=basePath%>">
    <title>添加图层</title>
    <style type="text/css">
    .imgicon{width:30px;height:30px;margin:5px;border:1px solid #eee;padding:5px;}
    </style>
	<script type="text/javascript" src="jslib/jeasyui.js"></script>
	<script type="text/javascript" src="plugins/layui/layui-init.js"></script>
	<script type="text/javascript" src="jsweb/web/addlayer.js?t=1"></script>
	<script type="text/javascript">
	var _type = "";	
	var _iconurl = "";
	</script>
  </head>
  <body>
  	<div class="easyui-panel" fit="true" style="padding:10px;overflow:hidden;">
		<form class="layui-form" action="">
			<input type="hidden" id="method" name="method" value="editLayer"/>
			<input type="hidden" id="id" name="id" value=""/>
			<input type="hidden" id="pcode" name="pcode" value="<%=pcode%>"/>
			<input type="hidden" id="code" name="code" value="<%=code%>"/>
			<input type="hidden" id="iconurl" name="iconurl" value=""/>
			<div class="layui-form-item">
				<label class="layui-form-label">图层名称</label>
				<div class="layui-input-block">
					<input type="text" name="name" id="name" required lay-verify="required"
						placeholder="请输入图层名称" autocomplete="off" class="layui-input" value="">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">图层类型</label>
				<div class="layui-input-block">
					<select id="layertype" name="layertype" lay-verify="required">
					</select>
				</div>
			</div>
			<div class="layui-form-item layui-form-text">
				<label class="layui-form-label">图层图标</label>
				<div class="layui-input-block">
					<div id="iconlist" style="height:auto;padding:5px;border:1px solid #ccc;">
					</div>
				</div>
			</div>
			<div class="layui-form-item">
				<div class="layui-input-block">
					<button class="layui-btn" lay-submit lay-filter="formDemo">立即提交</button>
					<button type="reset" class="layui-btn layui-btn-primary">重置</button>
				</div>
			</div>
		</form>
	</div>
<script>
	/**
	//Demo
	layui.use('form', function() {
		var form = layui.form();

		//监听提交
		form.on('submit(formDemo)', function(data) {
			layer.msg(JSON.stringify(data.field));
			return false;
		});
	});
	*/
</script>
  </body>
</html>

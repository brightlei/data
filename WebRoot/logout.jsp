<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
log(session.getId());
if(session!=null){
	session.invalidate();
}
response.sendRedirect("login.html");
%>
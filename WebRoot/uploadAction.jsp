<%@page import="java.io.File"%>
<%@page import="org.apache.commons.fileupload.util.Streams"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.BufferedOutputStream"%>
<%@page import="java.io.BufferedInputStream"%>
<%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%>
<%@page import="org.apache.commons.fileupload.disk.DiskFileItem"%>
<%@page import="com.alibaba.fastjson.JSONObject"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
//设置接收的编码格式      
request.setCharacterEncoding("UTF-8");
JSONObject json = new JSONObject();
json.put("state", true);
DiskFileItemFactory fac = new DiskFileItemFactory();
log("fac="+fac);
ServletFileUpload upload = new ServletFileUpload(fac);
log("upload="+upload);
upload.setHeaderEncoding("UTF-8");
//获取多个上传文件
List fileList = fileList = upload.parseRequest(request);
log("fileList="+fileList);
log("fileCount="+fileList.size());
// 遍历上传文件写入磁盘
Iterator it = fileList.iterator();
log("it="+it);
while (it.hasNext()) {
    Object obit = it.next();
    if(obit instanceof DiskFileItem){    
        System.out.println("xxxxxxxxxxxxx");
        DiskFileItem item = (DiskFileItem) obit;
        // 如果item是文件上传表单域
        // 获得文件名及路径
        String fileName = item.getName();
        log("filename="+fileName);
        if(fileName!=null){
        	log("filename="+fileName);
        	BufferedInputStream in = new BufferedInputStream(item.getInputStream());// 获得文件输入流      
            BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(new File("D:/旅游项目/upload/"+fileName)));//获得文件输出流      
            Streams.copy(in, outStream, true);// 开始把文件写到你指定的上传文件夹  
            log("文件["+fileName+"]上传成功！");
        }
    }
}
out.clear();
out.println(json.toJSONString());
%>
/**   
 * 特别声明：本技术材料受《中华人民共和国着作权法》、《计算机软件保护条例》
 * 等法律、法规、行政规章以及有关国际条约的保护，武汉中地数码科技有限公
 * 司享有知识产权、保留一切权利并视其为技术秘密。未经本公司书面许可，任何人
 * 不得擅自（包括但不限于：以非法的方式复制、传播、展示、镜像、上载、下载）使
 * 用，不得向第三方泄露、透露、披露。否则，本公司将依法追究侵权者的法律责任。
 * 特此声明！
 * 
   Copyright (c) 2013,武汉中地数码科技有限公司
 */
/**
 * 这里是文件说明
 */
package com.zondy.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;

/**
 * 模块名称：该模块名称
 * 功能描述：该文件详细功能描述
 * 文档作者：雷志强
 * 创建时间：2017年9月19日 下午11:56:24
 * 初始版本：V1.0
 * 修改记录：
 * *************************************************
 * 修改人：雷志强
 * 修改时间：2017年9月19日 下午11:56:24
 * 修改内容：
 * *************************************************
 */
public class ServerServlet extends HttpServlet {

	/**
	 * long:serialVersionUID 用一句话描述这个变量
	 */
	private static final long serialVersionUID = 1L;

	public ServerServlet() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		String path = request.getContextPath();
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		if(session.getAttribute("user")==null){
			String loginPage = basePath+"login.html";
			out.println("top.window.location.href='"+loginPage+"';");
		}else{
			JSONObject user = (JSONObject)session.getAttribute("user");
			String username = user.getString("username");
			String xm = user.getString("xm");
			double lon = user.getDoubleValue("longitude");
			double lat = user.getDoubleValue("latitude");
			String userimg = user.getString("headimg");
			userimg = userimg.replaceAll("160X160", "48X48");
			out.println("window.user={};");
			out.println("user.username='"+username+"';");
			out.println("user.xm='"+xm+"';");
			out.println("user.lon="+lon+";");
			out.println("user.lat="+lat+";");
			out.println("user.headimg='"+basePath+userimg+"';");
		}
		out.flush();
		out.close();
	}

	public void init() throws ServletException {
		
	}
}

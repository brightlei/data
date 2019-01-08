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

package com.zondy.listener;

import java.util.Date;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.quartz.SchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.alibaba.fastjson.JSONArray;
import com.zondy.base.DAO.BaseDAOImpl;
import com.zondy.utils.DateUtil;

/**
 * 模块名称：ApplicationListener									<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：2013-9-17 上午09:44:15					<br>
 * 初始版本：V1.0	最初版本号							<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：2013-9-17 上午09:44:15					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class ApplicationListener implements ServletContextListener {
	
	private static Logger log = Logger.getLogger(ApplicationListener.class);
	
	//在线用户信息
	public static JSONArray userOnLine = new JSONArray();
	//定时任务
	public static SchedulerFactory factory;
	//系统发布目录
	public static String rootPath = "D:/apache-tomcat-7.0.67/webapps/data/";//84.160服务器
	
	//系统web目录
	public static String basePath = "";
	
	// web请求上下文
	private static ServletContext context;

	// 应用上下文
	public static ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
	
	public static BaseDAOImpl dao = (BaseDAOImpl)ctx.getBean("dao");
	
	/**
	 * 系统停止
	 */
	public void contextDestroyed(ServletContextEvent event) {
		String webName = event.getServletContext().getContextPath();
		webName = webName.substring(1, webName.length());
		log.info("WEB应用系统[" + webName + "]停止  时间：" + DateUtil.format("yyyy-MM-dd HH:mm:ss", new Date()));
	}
	
	/**
	 * 系统启动初始化
	 */
	public void contextInitialized(ServletContextEvent event) {
		String webName = event.getServletContext().getContextPath();
		webName = webName.substring(1, webName.length());
		log.info("WEB应用系统[" + webName + "]启动  时间：" + DateUtil.format("yyyy-MM-dd HH:mm:ss", new Date()));
		context = event.getServletContext();
		ctx = WebApplicationContextUtils.getWebApplicationContext(context);
		if(ctx == null){
			ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
			dao = (BaseDAOImpl)ctx.getBean("dao");
		}
		rootPath = context.getRealPath("/");
		rootPath = rootPath.replace("\\", "/");
		log.info("rootPath:" + rootPath);
		//设置环境变量
		Properties properties = new Properties();
		properties.setProperty("catalina.base", rootPath);
	}
}


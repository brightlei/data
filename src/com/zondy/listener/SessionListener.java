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

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.zondy.base.DAO.BaseDAOImpl;
import com.zondy.utils.DateUtil;
import com.zondy.utils.SystemUtil;


/**
 * 模块名称：SessionListener									<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：2013-9-17 上午10:15:46					<br>
 * 初始版本：V1.0	最初版本号							<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：2013-9-17 上午10:15:46					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class SessionListener implements ServletRequestListener,HttpSessionListener,HttpSessionAttributeListener{
	private static Logger log = Logger.getLogger(SessionListener.class);
	HttpServletRequest request;
	//session创建的时间调用
	public void sessionCreated(HttpSessionEvent event) {
		log.debug("sessionCreated=="+event.getSession().getId());
		JSONObject onlineUser = new JSONObject();
		//新创建的session
		HttpSession session = event.getSession();
		String sessionId = session.getId();
		String clientIp = SystemUtil.getClientIpAddr(request);
		String visitTime = DateUtil.date2String("yyyy-MM-dd HH:mm:ss");
		log.info("客户端访问信息["+sessionId+"]：IP地址["+clientIp+"],访问时间["+visitTime+"]");
	}
	
	//session销毁的时候调用
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		String sid = session.getId();
		String dt = DateUtil.date2String("yyyy-MM-dd HH:mm:ss");
		log.info("sessionDestroyed=="+sid+",离开时间："+dt);
		BaseDAOImpl dao = ApplicationListener.dao;
		String sql = "update t_user_login set leavetime=CONVERT(varchar, getdate(), 120) where sid='"+sid+"'";
		dao.updateObject(sql);
	}
	//销毁request时调用
	public void requestDestroyed(ServletRequestEvent event) {
		
	}
	//创建request时调用
	public void requestInitialized(ServletRequestEvent event) {
		request = (HttpServletRequest)event.getServletRequest();
	}
	
	//添加属性的时候被调用
	public void attributeAdded(HttpSessionBindingEvent event) {
		log.debug("attributeAdded=="+event.getValue().toString());
	}

	public void attributeRemoved(HttpSessionBindingEvent event) {
		String dt = DateUtil.date2String("yyyy-MM-dd HH:mm:ss");
		log.debug(dt+",attributeRemoved=="+event.getValue().toString());
	}
	//属性修改时调用
	public void attributeReplaced(HttpSessionBindingEvent event) {
		String dt = DateUtil.date2String("yyyy-MM-dd HH:mm:ss");
		log.debug(dt+",attributeReplaced=="+event.getValue().toString());
	}
	
	private void removeOnlineUser(final String sid){
		
	}
}

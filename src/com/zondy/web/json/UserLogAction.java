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

package com.zondy.web.json;


import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import com.alibaba.fastjson.JSONArray;
import com.zondy.config.ParamConfig;
import com.zondy.config.QuerySqlConfig;
import com.zondy.utils.SystemUtil;
import com.zondy.web.action.BaseAction;

/**
 * 模块名称：UserLogAction									<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：2014-5-16 下午04:57:54					<br>
 * 初始版本：V1.0	最初版本号							<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：2014-5-16 下午04:57:54					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class UserLogAction extends BaseAction{
	
	private static Logger log = Logger.getLogger(UserLogAction.class);
	//long:serialVersionUID 用一句话描述这个变量
	private static final long serialVersionUID = 1L;
	
	//保存数据
	public String savelog()throws HibernateException{
		String logName = "["+requestSessionId+"]savelog()";
		log.info("logName="+logName);
		HttpSession session = request.getSession();
		QuerySqlConfig config = new QuerySqlConfig();
		String primaryKey = ParamConfig.getColumnValue("getUid", "");
		requestParam.put("primaryKey", primaryKey);
		String ip = SystemUtil.getClientIpAddr(request);
		String username = "";
		if(session.getAttribute("username")!=null){
			username = session.getAttribute("username").toString();
		}
		requestParam.put("ip", ip);
		requestParam.put("username", username);
		String sql = config.getConfigSQL("saveUserLog");
		sql = config.getReplaceSqlParam(sql,requestParam);
		log.info(logName+"sql="+sql);
		int result = -1;
		try {
			result = dao.saveObject(sql);
			dataMap.put("state", true);
			dataMap.put("data", result);
			dataMap.put("datakey", primaryKey);
		} catch (Exception e) {
			dataMap.put("state", false);
			dataMap.put("error", e.getMessage());
			log.error("savelog[Exception]:"+e.getMessage(), new Throwable(e));
		}
		log.info(logName+"dataMap="+dataMap);
		return "map";
	}
	
	//获取用户操作日志
	public String querylog()throws HibernateException{
		String logName = "["+requestSessionId+"]querylog()";
		log.info(logName+"[requestParam]="+requestParam);
		int pageNo = requestParam.getIntValue("page");
		int pageSize = requestParam.getIntValue("rows");
		QuerySqlConfig config = new QuerySqlConfig();
		String sql = config.getConfigSQL("queryUserLogCount");
		try {
			int total = dao.countAll(sql);
			sql = config.getConfigSQL("queryUserLog");
			log.info(logName+"sql="+sql);
			List<?> list = dao.listAll(sql, pageNo, pageSize);
			dataMap.put("total", total);
			dataMap.put("rows", list);
		} catch (Exception e) {
			dataMap.put("total", 0);
			dataMap.put("rows", new JSONArray());
			log.error("querylog[Exception]:"+e.getMessage(), new Throwable(e));
		}
		System.out.println(dataMap);
		return "map";
	}
	
	public static void main(String[] args) {
		UserLogAction action = new UserLogAction();
		action.requestParam.put("page", "2");
		action.requestParam.put("rows", "2");
		action.querylog();
		System.out.println("OK");
	}
}

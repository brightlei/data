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
package com.zondy.web.json;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.zondy.config.ParamConfig;
import com.zondy.config.QuerySqlConfig;
import com.zondy.utils.SystemUtil;
import com.zondy.web.action.BaseAction;

/**
 * 模块名称：该模块名称
 * 功能描述：该文件详细功能描述
 * 文档作者：雷志强
 * 创建时间：2017年5月21日 下午8:32:27
 * 初始版本：V1.0
 * 修改记录：
 * *************************************************
 * 修改人：雷志强
 * 修改时间：2017年5月21日 下午8:32:27
 * 修改内容：
 * *************************************************
 */
public class LoginAction extends BaseAction{
	/**
	 * long:serialVersionUID 用一句话描述这个变量
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(LoginAction.class);
	public String login(){
		String logName = "["+requestSessionId+"]login()";
		log.info("logName="+logName);
		try {
			String username = null;
			String userpwd = null;
			if(requestParam.containsKey("username")){
				username = requestParam.getString("username");
			}
			if(requestParam.containsKey("userpwd")){
				userpwd = requestParam.getString("userpwd");
			}
			if(username==null||"".equals(username)){
				dataMap.put("state", false);
				dataMap.put("error", "用户名不能为空！");
			}else if(userpwd==null||"".equals(userpwd)){
				dataMap.put("state", false);
				dataMap.put("error", "用户名密码不能为空！");
			}else{
				QuerySqlConfig config = new QuerySqlConfig();
				String sql = config.getConfigSQL("userLogin");
				sql = config.getReplaceSqlParam(sql,requestParam);
				log.info(logName+"sql="+sql);
				JSONObject user = dao.loadObject(sql);
				log.info("user="+user);
				if(user==null){
					dataMap.put("state", false);
					dataMap.put("error", "用户名["+username+"]不存在！");
				}else{
					String pwd = user.get("userpwd").toString();
					if(userpwd.equals(pwd)){
						HttpSession session = request.getSession();
						String ip = SystemUtil.getClientIpAddr(request);
						user.remove("userpwd");
						session.setAttribute("ip", ip);
						session.setAttribute("username", username);
						session.setAttribute("user", user);
						session.setAttribute("isLogin", true);
						dataMap.put("state", true);
						dataMap.put("data", user.toJSONString());
						//记录用户登录日志
						JSONObject paramJson = new JSONObject();
						String userAgent = request.getHeader("user-agent");
						log.info("userAgent="+userAgent);
						boolean isMobileVisit = SystemUtil.checkAgentIsMobile(userAgent);
						String loginmethod = "pc";
						if(isMobileVisit){
							loginmethod  = "mobile";
						}
						paramJson.put("primaryKey", ParamConfig.getColumnValue("getUid", ""));
						paramJson.put("sid", session.getId());
						paramJson.put("ip", ip);
						paramJson.put("username", username);
						paramJson.put("loginmethod", loginmethod);
						String basesql = config.getConfigSQL("addLoginLog");
						sql = config.getReplaceSqlParam(basesql, paramJson);
						dao.saveObject(sql);
					}else{
						dataMap.put("state", false);
						dataMap.put("error", "用户名["+username+"]密码不正确！");
					}
				}
			}
		} catch (Exception e) {
			dataMap.put("state", false);
			dataMap.put("error", e.getMessage());
			log.error("login[Exception]", new Throwable(e));
		}
		System.out.println(dataMap);
		return "map";
	}
	
	public static void main(String[] args) {
		LoginAction action = new LoginAction();
		action.requestParam.put("username", "admin");
		action.requestParam.put("userpwd", "123456");
		action.login();
		System.out.println("OK");
	}
}

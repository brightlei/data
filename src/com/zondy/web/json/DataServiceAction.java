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


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import com.zondy.config.ParamConfig;
import com.zondy.config.QuerySqlConfig;
import com.zondy.web.action.BaseAction;

/**
 * 模块名称：ForecastAction									<br>
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
public class DataServiceAction extends BaseAction{
	
	private static Logger log = Logger.getLogger(DataServiceAction.class);
	//long:serialVersionUID 用一句话描述这个变量
	private static final long serialVersionUID = 1L;
	
	//保存数据
	public String saveData()throws HibernateException{
		String logName = "["+requestSessionId+"]saveData()";
		log.info("logName="+logName);
		QuerySqlConfig config = new QuerySqlConfig();
		String primaryKey = ParamConfig.getColumnValue("getUid", "");
		requestParam.put("primaryKey", primaryKey);
		String sql = config.getConfigSQL(requestParam.get("method").toString());
		sql = config.getReplaceSqlParam(sql,requestParam);
		log.info(logName+"sql="+sql);
		int result = -1;
		try {
			result = dao.updateObject(sql);
			dataMap.put("state", true);
			dataMap.put("data", result);
			dataMap.put("datakey", primaryKey);
		} catch (Exception e) {
			dataMap.put("state", false);
			dataMap.put("error", e.getMessage());
			log.error("saveData[Exception]:"+e.getMessage(), new Throwable(e));
		}
		log.info(logName+"dataMap="+dataMap);
		return "map";
	}
	
	//修改数据
	public String editData()throws HibernateException{
		String logName = "["+requestSessionId+"]saveData()";
		log.info("logName="+logName);
		QuerySqlConfig config = new QuerySqlConfig();
		String sql = config.getConfigSQL(requestParam.get("method").toString());
		sql = config.getReplaceSqlParam(sql,requestParam);
		log.info(logName+"sql="+sql);
		int result = -1;
		try {
			result = dao.updateObject(sql);
			dataMap.put("data", result);
			dataMap.put("state", true);
		} catch (Exception e) {
			dataMap.put("state", false);
			dataMap.put("error", e.getMessage());
			log.error("editData[Exception]:"+e.getMessage(), new Throwable(e));
		}
		log.info(logName+"dataMap="+dataMap);
		return "map";
	}
	
	//根据查询条件查询所有记录
	@SuppressWarnings("rawtypes")
	public String listAll(){
		String logName = "["+requestSessionId+"]listAll()";
		log.info("logName="+logName);
		List<?> list = new ArrayList();
		try {
			QuerySqlConfig config = new QuerySqlConfig();
			String sql = config.getConfigSQL(requestParam.get("method").toString());
			sql = config.getReplaceSqlParam(sql,requestParam);
			log.info(logName+"sql="+sql);
			list = dao.listAll(sql);
			dataMap.put("state", true);
			dataMap.put("data", list);
		} catch (Exception e) {
			dataMap.put("state", false);
			dataMap.put("error", e.getMessage());
			log.error("listAll[Exception]:"+e.getMessage(), new Throwable(e));
		}
		return "map";
	}
	
	//根据查询条件查询分页记录
	@SuppressWarnings("rawtypes")
	public String listByPage(){
		String logName = "["+requestSessionId+"]listByPage()";
		log.info("logName="+logName);
		List<?> list = new ArrayList();
		try {
			QuerySqlConfig config = new QuerySqlConfig();
			String sql = config.getConfigSQL(requestParam.get("method").toString());
			sql = config.getReplaceSqlParam(sql,requestParam);
			log.info(logName+"sql="+sql);
			int pageNo = 1;
			int pageSize = 100;
			if(requestParam.get("pageNo")!=null){
				pageNo = Integer.parseInt(requestParam.get("pageNo").toString());
			}
			if(requestParam.get("pageSize")!=null){
				pageSize = Integer.parseInt(requestParam.get("pageSize").toString());
			}
			list = dao.listAll(sql,pageNo,pageSize);
			dataMap.put("data", list);
			dataMap.put("state", true);
		} catch (Exception e) {
			dataMap.put("state", false);
			dataMap.put("error", e.getMessage());
			log.error("listByPage[Exception]:"+e.getMessage(), new Throwable(e));
		}
		return "map";
	}
	
	public static void main(String[] args) {
		System.out.println("OK");
	}
}

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
package com.zondy.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import test.test;

import com.alibaba.fastjson.JSONObject;
import com.sun.corba.se.spi.orb.StringPair;
import com.zondy.base.DAO.BaseDAOImpl;
import com.zondy.config.ParamConfig;
import com.zondy.config.QuerySqlConfig;
import com.zondy.config.WebConfig;
import com.zondy.listener.ApplicationListener;
import com.zondy.service.impl.DataImpl;
import com.zondy.utils.DotUtils;
import com.zondy.utils.ExcelUtil;
import com.zondy.web.json.WebAction;

/**
 * 模块名称：该模块名称
 * 功能描述：该文件详细功能描述
 * 文档作者：雷志强
 * 创建时间：2017年7月10日 上午6:47:06
 * 初始版本：V1.0
 * 修改记录：
 * *************************************************
 * 修改人：雷志强
 * 修改时间：2017年7月10日 上午6:47:06
 * 修改内容：
 * *************************************************
 */
public class DataImport {
	
	private static Logger log = Logger.getLogger(DataImpl.class);
	
	public int importLinePoint(String filepath) throws Exception{
		List<List<String>> list = ExcelUtil.readExcel(filepath);
		QuerySqlConfig config = new QuerySqlConfig();
		String baseSql = config.getConfigSQL("addLinePoint");
		BaseDAOImpl dao = (BaseDAOImpl)ApplicationListener.dao;
		int count = list.size();
		List<String> rowList = null;
		JSONObject record = null;
		String sql = "";
		int successCount = 0;
		for(int i=1;i<count;i++){
			rowList = list.get(i);
			record = getLinePointRecord(rowList);
			String primaryKey = ParamConfig.getColumnValue("getUid", "");
			record.put("primaryKey", primaryKey);
			sql = config.getReplaceSqlParam(baseSql, record);
			int ret = dao.saveObject(sql);
			if(ret==1){
				successCount++;
			}
		}
		return successCount;
	}
	/**
	 * 功能描述：导入图层数据
	 * 创建作者：雷志强
	 * 创建时间：2017年11月1日 上午12:44:56
	 * @param table 图层数据表
	 * @param filepath excel数据表格路径
	 * @throws Exception
	 * @return int
	 */
	public int importLayerData(String table,String filepath) throws Exception{
		List<List<String>> list = ExcelUtil.readExcel(filepath);
		BaseDAOImpl dao = (BaseDAOImpl)ApplicationListener.dao;
		int count = list.size();
		List<String> rowList = null;
		String sql = "";
		int successCount = 0;
		List<String> columnList = list.get(0);
		for(int i=2;i<count;i++){
			rowList = list.get(i);
			sql = getRecordSql(table,columnList,rowList);
			int ret = 0;
			try {
				ret = dao.saveObject(sql);
				if(ret==1){
					successCount++;
				}
			} catch (Exception e) {
				log.error("Exception", new Throwable(e));
			}
		}
		return successCount;
	}
	/**
	 * 功能描述：导入短期信息-车间日计划
	 * 创建作者：雷志强
	 * 创建时间：2017年11月13日 上午12:58:09
	 * @param table 数据表名称
 	 * @param filepath 文件路径
	 * @throws Exception
	 * @return int
	 */
	public int importShortmsgLayerData(String table,String filepath) throws Exception{
		List<List<String>> list = ExcelUtil.readExcel(filepath);
		BaseDAOImpl dao = (BaseDAOImpl)ApplicationListener.dao;
		int count = list.size();
		log.info("dataCount="+count);
		List<String> rowList = null;
		String sql = "";
		int successCount = 0;
		String columnstr = WebConfig.getConfig("t_shortmsg_export_columns");
		List<String> columnList = Arrays.asList(columnstr.split(","));
		String job_address = "";
		String mileageString = "";
		String[] tmpArr = null;
		for(int i=2;i<count;i++){
			rowList = list.get(i);
			job_address = rowList.get(13);
			mileageString = readMileageString(job_address);
			tmpArr = mileageString.split("至");
			rowList.add(tmpArr[0]);
			rowList.add(tmpArr[1]);
			sql = getRecordSql(table,columnList,rowList);
			int ret = 0;
			try {
				ret = dao.saveObject(sql);
				if(ret==1){
					successCount++;
				}
			} catch (Exception e) {
				log.error("Exception", new Throwable(e));
			}
		}
		return successCount;
	}
	/**
	 * 功能描述：获取上传的数据表格表字段
	 * 创建作者：雷志强
	 * 创建时间：2017年8月7日 下午3:33:21
	 * @return List<String>
	 */
	private List<String> getUploadTableColumns(List<String> headList){
		List<String> columnList = new ArrayList<String>();
		int count = headList.size();
		int sindex = 0;
		int eindex = 0;
		String columnText = null;
		String column = null;
		for(int i=0;i<count;i++){
			columnText = headList.get(i);
			sindex = columnText.indexOf("(");
			eindex = columnText.indexOf(")");
			column = columnText.substring(sindex+1, eindex);
			columnList.add(column);
		}
		return columnList;
	}
	/**
	 * 功能描述：根据字段类型及一行值获取json对象
	 * 创建作者：雷志强
	 * 创建时间：2017年8月7日 下午3:37:03
	 * @param columnList
	 * @param rowList
	 * @return JSONObject
	 */
	private JSONObject getRecordJsonobject(List<String> columnList,List<String> rowList){
		JSONObject record = new JSONObject();
		if(rowList!=null&&rowList.size()>0){
			try {
				int columnCount = columnList.size();
				for(int i=0;i<columnCount;i++){
					record.put(columnList.get(i), rowList.get(i));
				}
			} catch (Exception e) {
				log.error("Exception", new Throwable(e));
			}
		}
		return record;
	}
	
	private String getRecordSql(String table,List<String> columnList,List<String> rowList){
		String sql = "";
		if(rowList!=null&&rowList.size()>0){
			try {
				int columnCount = columnList.size();
				StringBuffer columns = new StringBuffer();
				StringBuffer values = new StringBuffer();
				for(int i=0;i<columnCount;i++){
					columns.append(columnList.get(i));
					values.append("'"+rowList.get(i)+"'");
					if(i<columnCount-1){
						columns.append(",");
						values.append(",");
					}
				}
				String columnstr = columns.toString();
				String valuestr = values.toString();
				String primaryKey = ParamConfig.getColumnValue("getUid", "");
				sql = "insert into "+table+"(id,"+columnstr+") values('"+primaryKey+"',"+valuestr+")";
			} catch (Exception e) {
				log.error("Exception", new Throwable(e));
			}
		}
		return sql;
	}
	
	//将表格数据转换成Json对象
	private JSONObject getLinePointRecord(List<String> rowList){
		JSONObject record = new JSONObject();
		if(rowList!=null&&rowList.size()>0){
			try {
				record.put("latstr", rowList.get(0));
				record.put("lonstr", rowList.get(1));
				record.put("mileage", rowList.get(2));
				record.put("name", rowList.get(3));
				record.put("linenum", rowList.get(4));
				record.put("direction", rowList.get(5));
				record.put("longchain", rowList.get(6));
				record.put("longitude", DotUtils.dfmToLonlat(record.getString("lonstr")));
				record.put("latitude", DotUtils.dfmToLonlat(record.getString("latstr")));
			} catch (Exception e) {
				log.error("Exception", new Throwable(e));
			}
		}
		return record;
	}
	
	public static String readMileageString(String str){
		String mileageStr = null;
		Pattern p = Pattern.compile("\\d+.+\\d{2,}");//这个2是指连续数字的最少个数
//        u = "站内 汉丹线 ：站内平林车站 124.200 至 126.100 股道编号：5";
        Matcher m = p.matcher(str);
        while (m.find()) {
        	if(mileageStr==null){
        		mileageStr = m.group();
        	}
        }
        if(mileageStr!=null){
        	mileageStr = mileageStr.replaceAll(" ", "");
        }
		return mileageStr;
	}
	
	public static void main(String[] args) {
		try {
			//testImportLinePoint();
			//testImportLayerData();
			testImportShortMsgLayerData();
//			String str = "站内 汉丹线 ：站内平林车站 124.200 至 126.100 股道编号：5";
//			System.out.println(readMileageString(str));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("OK");
	}
	
	public static void testImportLinePoint() throws Exception{
		DataImport imp = new DataImport();
		String filepath = "D:/apache-tomcat-7.0.67/webapps/data/upload/template/铁路线坐标点模板.xls";
		int count = imp.importLinePoint(filepath);
		System.out.println(count);
	}
	
	public static void testImportLayerData() throws Exception{
		DataImport imp = new DataImport();
//		String filepath = "D:/test/固定信息模板.xls";
//		String table = "t_fixdata";
//		String filepath = "D:/test/人工信息模板.xls";
//		String table = "t_rengongxx";
		String filepath = "D:/apache-tomcat-7.0.67/webapps/data/upload/template/测试图层1-批量导入模板[t_layer_21].xls";
		String table = "t_layer_21";
		int count = imp.importLayerData(table,filepath);
		System.out.println(count);
	}
	
	public static void testImportShortMsgLayerData() throws Exception{
		DataImport imp = new DataImport();
//		String filepath = "D:/test/固定信息模板.xls";
//		String table = "t_fixdata";
//		String filepath = "D:/test/人工信息模板.xls";
//		String table = "t_rengongxx";
		String filepath = "C:/Users/Administrator/Downloads/短期信息模板 (3).xls";
		String table = "t_shortmsg";
		int count = imp.importShortmsgLayerData(table,filepath);
		System.out.println(count);
	}
}

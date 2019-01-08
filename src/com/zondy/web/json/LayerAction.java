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


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zondy.config.ParamConfig;
import com.zondy.config.QuerySqlConfig;
import com.zondy.config.WebConfig;
import com.zondy.listener.ApplicationListener;
import com.zondy.service.impl.DataImpl;
import com.zondy.utils.BaiDuUtil;
import com.zondy.utils.DateUtil;
import com.zondy.utils.ExcelUtil;
import com.zondy.web.DataImport;
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
public class LayerAction extends BaseAction{
	
	private static Logger log = Logger.getLogger(LayerAction.class);
	//long:serialVersionUID 用一句话描述这个变量
	private static final long serialVersionUID = 1L;
	
	//添加业务图层
	public String addLayer()throws HibernateException{
		String logName = "["+requestSessionId+"]addLayer()";
		log.info("logName="+logName);
		QuerySqlConfig config = new QuerySqlConfig();
		String primaryKey = ParamConfig.getColumnValue("getUid", "");
		requestParam.put("primaryKey", primaryKey);
		String sql = config.getConfigSQL(requestParam.get("method").toString());
		sql = config.getReplaceSqlParam(sql,requestParam);
		log.info(logName+"sql="+sql);
		int result = -1;
		try {
			result = dao.saveObject(sql);
			sql = "select * from t_layer where id='"+primaryKey+"'";
			//查询刚插件的数据，获取code字段值，用于拼接新表名称
			JSONObject record = dao.loadObject(sql);
			if(record!=null&&record.containsKey("code")){
				String code = record.getString("code");
				//新创建的表名
				String tablename = "t_layer_"+code;
				String layername = record.getString("name");
				//SQL语句文件路径
				String sqlfilepath = ApplicationListener.rootPath+"/WEB-INF/resources/sql/layer_base.sql";
				File sqlFile = new File(sqlfilepath);
				if(sqlFile.exists()){
					List<String> list = FileUtils.readLines(sqlFile, "UTF-8");
					int count = list.size();
					String linestr = "";
					for(int i=0;i<count;i++){
						linestr = list.get(i);
						linestr = linestr.replaceAll("t_layer_base", tablename);
						result = dao.saveObject(linestr);
						log.info("[result="+result+"]sql="+linestr);
					}
					String syscolumns = WebConfig.getConfig("t_layer_base_syscolumns");
					String showcolumns = WebConfig.getConfig("t_layer_base_showcolumns");
					String templatePath = createLayerTemplate(tablename, layername);
					sql = "update t_layer set tablename='"+tablename+"',syscolumns='"+syscolumns+"',showcolumns='"+showcolumns+"',templatepath='"+templatePath+"' where id='"+primaryKey+"'";
					log.info("sql="+sql);
					result = dao.updateObject(sql);
				}else{
					log.warn("业务图层创建SQL文件不存在！"+sqlfilepath);
				}
			}
			dataMap.put("state", true);
			dataMap.put("data", result);
			dataMap.put("datakey", primaryKey);
		} catch (Exception e) {
			dataMap.put("state", false);
			dataMap.put("error", e.getMessage());
			log.error("addLayer[Exception]", new Throwable(e));
		}
		log.info(logName+"dataMap="+dataMap);
		return "map";
	}
	
	//修改图层数据
	public String editLayer()throws HibernateException{
		String logName = "["+requestSessionId+"]editLayer()";
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
			log.error("editLayer[Exception]", new Throwable(e));
		}
		log.info(logName+"dataMap="+dataMap);
		return "map";
	}
	
	//删除图层数据
	public String deleteLayer()throws HibernateException{
		String logName = "["+requestSessionId+"]deleteLayer()";
		log.info("logName="+logName);
		QuerySqlConfig config = new QuerySqlConfig();
		String sql = config.getConfigSQL(requestParam.get("method").toString());
		sql = config.getReplaceSqlParam(sql,requestParam);
		log.info(logName+"sql="+sql);
		int result = -1;
		try {
			String querysql = config.getConfigSQL("queryLayerById");
			querysql = config.getReplaceSqlParam(querysql, requestParam);
			JSONObject record = dao.loadObject(querysql);
			if(record.size()>0){
				String tablename = record.getString("tablename");
				dao.updateObject(sql);
				sql = "drop table "+tablename;
				dao.updateObject(sql);
				result = 1;
			}
			dataMap.put("data", result);
			dataMap.put("state", true);
		} catch (Exception e) {
			dataMap.put("state", false);
			dataMap.put("error", e.getMessage());
			log.error("deleteLayer[Exception]", new Throwable(e));
		}
		log.info(logName+"dataMap="+dataMap);
		return "map";
	}
	
	//删除图层分类数据
	public String deleteLayerClass()throws HibernateException{
		String logName = "["+requestSessionId+"]deleteLayerClass()";
		log.info("logName="+logName);
		QuerySqlConfig config = new QuerySqlConfig();
		String sql = config.getConfigSQL(requestParam.get("method").toString());
		sql = config.getReplaceSqlParam(sql,requestParam);
		log.info(logName+"sql="+sql);
		int result = -1;
		try {
			String querysql = config.getConfigSQL("queryLayerByIds");
			querysql = config.getReplaceSqlParam(querysql, requestParam);
			List<JSONObject> list = dao.listAll(querysql);
			int count = list.size();
			JSONObject record = null;
			String tablename = "";
			List<String> layerTables = new ArrayList<String>();
			for(int i=0;i<count;i++){
				record = list.get(i);
				tablename = record.getString("tablename");
				if(tablename!=null && !tablename.equals("")){
					layerTables.add(tablename);
				}
			}
			result = dao.updateObject(sql);
			if(result==count){
				for(int i=0;i<layerTables.size();i++){
					sql = "drop table "+layerTables.get(i);
					dao.updateObject(sql);
				}
			}
			dataMap.put("data", result);
			dataMap.put("state", true);
		} catch (Exception e) {
			dataMap.put("state", false);
			dataMap.put("error", e.getMessage());
			log.error("deleteLayerClass[Exception]", new Throwable(e));
		}
		log.info(logName+"dataMap="+dataMap);
		return "map";
	}
	
	
	//生成图层数据表格模板
	public String createLayerExcelTemplate()throws Exception{
		String logName = "["+requestSessionId+"]createLayerExcelTemplate()";
		log.info("logName="+logName);
		String layername = requestParam.getString("layername");
		String layertable = requestParam.getString("table");
		String filepath = createLayerTemplate(layertable,layername);
		dataMap.put("data", filepath);
		dataMap.put("state", true);
		String sql = "update t_layer set templatepath='"+filepath+"' where tablename='"+layertable+"'";
		dao.updateObject(sql);
		return "map";
	}
	
	/**
	 * 功能描述：根据图导数据表及图层名称生成批量导入数据模板
	 * 创建作者：雷志强
	 * 创建时间：2017年10月25日 上午1:07:16
	 * @param layertable 图层数据表名
	 * @param layername 图层名称
	 * @return String 模板文件相对路径
	 */
	public String createLayerTemplate(String layertable,String layername){
		String absolutePath = "upload/template/"+layername+"-批量导入模板["+layertable+"].xls";
		QuerySqlConfig config = new QuerySqlConfig();
		String basesql = config.getConfigSQL("getTableColumns");
		String sql = basesql.replaceAll("#table#", layertable);
		log.info("sql="+sql);
		List<JSONObject> list = dao.listAll(sql);
		int count = list.size();
		JSONObject record = null;
		List<String> fieldList = new ArrayList<String>();
		List<String> columnList = new ArrayList<String>();
		String field = null;
		List<String> exFieldList = new ArrayList<String>();
		exFieldList.add("id");
		exFieldList.add("createtime");
		exFieldList.add("edittime");
		exFieldList.add("state");
		for(int i=0;i<count;i++){
			record = list.get(i);
			field = record.getString("column_name");
			if(!exFieldList.contains(field)){
				fieldList.add(record.getString("column_name"));
				columnList.add(record.getString("description"));
			}
		}
		String[] fields = new String[fieldList.size()];
		fieldList.toArray(fields);
		String[] columns = new String[columnList.size()];
		columnList.toArray(columns);
		String filepath = ApplicationListener.rootPath+"/"+absolutePath;
		try {
			ExcelUtil.exportExcelTemplate(filepath, layername, fields,columns);
		} catch (Exception e) {
			log.error("生成图层数据批量导入模板文件异常！", new Throwable(e));
		}
		return absolutePath;
	}
	/**
	 * 功能描述：根据经纬度和半径查询业务图层数据
	 * 创建作者：雷志强
	 * 创建时间：2017年9月19日 上午9:47:22
	 * @return String
	 */
	public String queryNearLayerData(){
		String lonstr = requestParam.getString("lon");
		String latstr = requestParam.getString("lat");
		double lon = Double.parseDouble(lonstr);
		double lat = Double.parseDouble(latstr);
		String r = requestParam.getString("r");
		int raidus = Integer.parseInt(r);
		String queryInfo = requestParam.getString("queryInfo");
		try {
			DataImpl impl = new DataImpl();
			JSONArray data = impl.queryNearLayerData(lon, lat, raidus, queryInfo);
			this.dataMap.put("total", data.size());
			this.dataMap.put("rows", data);
		} catch (Exception e) {
			log.error("IOException", new Throwable(e));
		}
		return "map";
	}
	
	/**
	 * 功能描述：根据经纬度和半径查询百度周边POI信息点
	 * 创建作者：雷志强
	 * 创建时间：2017年9月19日 上午1:23:06
	 * @return String
	 */
	public String queryNearPoiData(){
		this.dataMap.remove("state");
		this.dataMap.remove("error");
		String lon = requestParam.getString("lon");
		String lat = requestParam.getString("lat");
		String r = requestParam.getString("r");
		String keywords = requestParam.getString("keywords");
		int pageno = requestParam.getIntValue("page");
		int pagesize = requestParam.getIntValue("rows");
		try {
			JSONObject result = BaiDuUtil.searchPlace(lon, lat, r, keywords,pageno,pagesize);
			if(result.getIntValue("status")==0){
				int total = result.getIntValue("total");
				JSONArray rows = result.getJSONArray("results");
				this.dataMap.put("total", total);
				this.dataMap.put("rows", rows);
			}
		} catch (IOException e) {
			log.error("IOException", new Throwable(e));
		}
		return "map";
	}
	
	public String testAction(){
		dataMap.put("nowtime", DateUtil.date2String("yyyy-MM-dd HH:mm:ss"));
		return "map";
	}
	//添加表字段
	public String addColumn()throws HibernateException{
		String logName = "["+requestSessionId+"]addColumn()";
		log.info("logName="+logName);
		QuerySqlConfig config = new QuerySqlConfig();
		String sql = config.getConfigSQL("addTableColumn");
		sql = config.getReplaceSqlParam(sql,requestParam);
		log.info(logName+"sql="+sql);
		int result = -1;
		try {
			result = dao.updateObject(sql);
			sql = config.getConfigSQL("addTableColumnComment");
			sql = config.getReplaceSqlParam(sql,requestParam);
			log.info(logName+"sql="+sql);
			result = dao.updateObject(sql);
			dataMap.put("state", true);
			dataMap.put("data", result);
			//saveTableColumnInfo(requestParam.getString("table"));
		} catch (Exception e) {
			dataMap.put("state", false);
			dataMap.put("error", e.getMessage());
			log.error("saveData[Exception]", new Throwable(e));
		}
		log.info(logName+"dataMap="+dataMap);
		return "map";
	}
	//修改表字段
	public String editColumn()throws HibernateException{
		String logName = "["+requestSessionId+"]editColumn()";
		log.info("logName="+logName);
		QuerySqlConfig config = new QuerySqlConfig();
		String sql = config.getConfigSQL("editTableColumn");
		sql = config.getReplaceSqlParam(sql,requestParam);
		log.info(logName+"sql="+sql);
		int result = -1;
		try {
			result = dao.updateObject(sql);
			if(requestParam.containsKey("addcomment")){
				sql = config.getConfigSQL("addTableColumnComment");
				sql = config.getReplaceSqlParam(sql,requestParam);
				log.info(logName+"sql="+sql);
				result = dao.updateObject(sql);
			}else if(requestParam.containsKey("editcomment")){
				sql = config.getConfigSQL("editTableColumnComment");
				sql = config.getReplaceSqlParam(sql,requestParam);
				log.info(logName+"sql="+sql);
				result = dao.updateObject(sql);
			}
			//saveTableColumnInfo(requestParam.getString("table"));
			dataMap.put("state", true);
			dataMap.put("data", result);
		} catch (Exception e) {
			dataMap.put("state", false);
			dataMap.put("error", e.getMessage());
			log.error("saveData[Exception]", new Throwable(e));
		}
		log.info(logName+"dataMap="+dataMap);
		return "map";
	}
	
	//删除表字段
	public String deleteColumn()throws HibernateException{
		String logName = "["+requestSessionId+"]deleteColumn()";
		log.info("logName="+logName);
		QuerySqlConfig config = new QuerySqlConfig();
		try {
			String basesql = config.getConfigSQL("getLayerById");
			String sql = config.getReplaceSqlParam(basesql, requestParam);
			JSONObject record = dao.loadObject(sql);
			String syscolumns = record.getString("syscolumns");
			List<String> syscolList = Arrays.asList(syscolumns.split(","));
			String columnName = requestParam.getString("column_name");
			if(syscolList.contains(columnName)){
				dataMap.put("state", false);
				dataMap.put("data", "系统自带字段不能删除！");
			}else{
				basesql = config.getConfigSQL("deleteTableColumn");
				sql = config.getReplaceSqlParam(basesql,requestParam);
				log.info(logName+"sql="+sql);
				int result = dao.updateObject(sql);
				dataMap.put("state", true);
				dataMap.put("data", result);
			}
		} catch (Exception e) {
			dataMap.put("state", false);
			dataMap.put("error", e.getMessage());
			log.error("saveData[Exception]", new Throwable(e));
		}
		log.info(logName+"dataMap="+dataMap);
		return "map";
	}
	//分页查询业务图层数据
	public String loadLayerDataByPage(){
		dataMap = new LinkedHashMap<String, Object>();
		String logName = "["+requestSessionId+"]loadLayerDataByPage()";
		log.info("logName="+logName);
		log.info(requestParam);
		List<JSONObject> list = new ArrayList<JSONObject>();
		try {
			String tableName = requestParam.getString("table");
			QuerySqlConfig config = new QuerySqlConfig();
			String baseSql = config.getConfigSQL("getTableDataCount");
			String sql = config.getReplaceSqlParam(baseSql, requestParam);
			int count = dao.countAll(sql);
			baseSql = config.getConfigSQL("getTableDataByPage");
			sql = config.getReplaceSqlParam(baseSql,requestParam);
			log.info(logName+"sql="+sql);
			int pageNo = requestParam.getIntValue("page");
			int pageSize = requestParam.getIntValue("rows");
			list = dao.listAll(sql,pageNo,pageSize);
			/**
			//如果是短期信息，则进行相关数据处理
			if(tableName.equals("t_shortmsg")){
				sql = config.getConfigSQL("listLinePoint");
				List<JSONObject> mileageList = dao.listAll(sql);
				log.info("mileageList="+mileageList);
				int dataCount = list.size();
				List<JSONObject> newList = new ArrayList<JSONObject>();
				JSONObject record = null;
				String job_address = null;
				String mileagestr = null;
				JSONObject mileagePoint = null;
				for(int i=0;i<dataCount;i++){
					record = list.get(i);
					job_address = record.getString("job_address");
					mileagestr = DataImport.readMileageString(job_address);
					mileagePoint = getMeliagePoint(mileageList, mileagestr);
					record.put("kslcs", mileagePoint.getString("kslcs"));
					record.put("jslcs", mileagePoint.getString("jslcs"));
					if(mileagePoint.containsKey("startpoint")){
						record.put("longitude", mileagePoint.getJSONObject("startpoint").getDoubleValue("longitude"));
						record.put("latitude", mileagePoint.getJSONObject("startpoint").getDoubleValue("latitude"));
					}else if(mileagePoint.containsKey("endpoint")){
						record.put("longitude", mileagePoint.getJSONObject("endpoint").getDoubleValue("longitude"));
						record.put("latitude", mileagePoint.getJSONObject("endpoint").getDoubleValue("latitude"));
					}
					newList.add(record);
				}
				list = newList;
			}
			*/
			dataMap.put("total", count);
			dataMap.put("rows", list);
		} catch (Exception e) {
			dataMap.put("state", false);
			dataMap.put("error", e.getMessage());
			log.error("listByPage[Exception]", new Throwable(e));
		}
		return "map";
	}
	
	//分页查询业务图层数据
	public String searchLayerDataByPage(){
		dataMap = new LinkedHashMap<String, Object>();
		String logName = "["+requestSessionId+"]searchLayerDataByPage()";
		log.info("logName="+logName);
		log.info(requestParam);
		List<JSONObject> list = new ArrayList<JSONObject>();
		try {
			String tableName = requestParam.getString("table");
			QuerySqlConfig config = new QuerySqlConfig();
			String baseSql = "";
			String sql = "";
			int count = 0;
			if(tableName.equals("t_shortmsg")){
				baseSql = config.getConfigSQL("searchShortMsgLayerDataCount");
				sql = config.getReplaceSqlParam(baseSql, requestParam);
				count = dao.countAll(sql);
				baseSql = config.getConfigSQL("searchShortMsgLayerDataByPage");
				sql = config.getReplaceSqlParam(baseSql,requestParam);
			}else{
				baseSql = config.getConfigSQL("searchLayerDataCount");
				sql = config.getReplaceSqlParam(baseSql, requestParam);
				count = dao.countAll(sql);
				baseSql = config.getConfigSQL("searchLayerDataByPage");
				sql = config.getReplaceSqlParam(baseSql,requestParam);
			}
			log.info(logName+"sql="+sql);
			int pageNo = requestParam.getIntValue("page");
			int pageSize = requestParam.getIntValue("rows");
			list = dao.listAll(sql,pageNo,pageSize);
			System.out.println();
			System.out.println(sql);
			dataMap.put("total", count);
			dataMap.put("rows", list);
		} catch (Exception e) {
			dataMap.put("state", false);
			dataMap.put("error", e.getMessage());
			log.error("searchLayerDataByPage[Exception]", new Throwable(e));
		}
		return "map";
	}
	
	private static JSONObject getMeliagePoint(List<JSONObject> list,String mileagestr){
		JSONObject pointInfo = new JSONObject();
		pointInfo.put("kslcs", "0.0");
		pointInfo.put("jslcs", "0.0");
		//从配置文件中读取短期信息中的里程跟里程坐标点中的里程最小值，小于这个值则认为里程坐标匹配
		double minLimeage = Double.parseDouble(WebConfig.getConfig("minMileage"));
		int count = list.size();
		JSONObject rcd = null;
		String[] tmpArr = mileagestr.split("至");
		pointInfo.put("kslcs", tmpArr[0]);
		pointInfo.put("jslcs", tmpArr[1]);
		double mileage1 = Double.parseDouble(tmpArr[0]);
		double mileage2 = Double.parseDouble(tmpArr[1]);
		double mileage = 0;
		double dx1 = 0;
		double dx2 = 0;
		double min_dx1 = 0;
		double min_dx2 = 0;
		JSONObject minPoint_x1 = null;
		JSONObject minPoint_x2 = null;
		for(int i=0;i<count;i++){
			rcd = list.get(i);
			mileage = rcd.getDoubleValue("mileage");
			dx1 = Math.abs(mileage-mileage1);
			dx2 = Math.abs(mileage-mileage2);
			if(i==0){
				min_dx1 = dx1;
				min_dx2 = dx2;
				minPoint_x1 = rcd;
				minPoint_x2 = rcd;
			}else{
				if(dx1<min_dx1){
					min_dx1 = dx1;
					minPoint_x1 = rcd;
				}
				if(dx2<min_dx2){
					min_dx2 = dx2;
					minPoint_x2 = rcd;
				}
			}
		}
		if(min_dx1<minLimeage){
			pointInfo.put("startpoint", minPoint_x1);
		}
		if(min_dx2<minLimeage){
			pointInfo.put("endpoint", minPoint_x2);
		}
		return pointInfo;
	}
	
	private String getShortMsgDataLocation(String address){
		Pattern p = Pattern.compile("\\d+.+\\d{2,}");//这个2是指连续数字的最少个数
        Matcher m = p.matcher(address);
        List<String> matchList = new ArrayList<String>();
        while (m.find()) {
        	matchList.add(m.group());
        }
        return matchList.get(0);
	}
	
	/**
	 * 功能描述：根据表字段数据生成对应的模板文件
	 * 创建作者：雷志强
	 * 创建时间：2017年8月2日 下午11:07:46
	 * @param list
	 * @param tableName
	 * @return void
	 */
	private void updateExcelModuls(List<JSONObject> list,String tableName){
		int count = list.size();
		log.info("updateExcelModuls[count]="+count);
		JSONObject record = null;
		String column_name = null;
		List<String> columnList = new ArrayList<String>();
		String columnInfo = ""; 
		for(int i=2;i<count;i++){
			record = list.get(i);
			column_name = record.getString("column_name");
			if(column_name.contains("createtime")||column_name.contains("edittime")||column_name.contains("state")){
				continue;
			}
			columnInfo = record.getString("description")+"("+record.getString("column_name")+")";
			columnList.add(columnInfo);
		}
		log.info("updateExcelModuls[columnList]="+columnList);
		String[] columnHeads = new String[columnList.size()];
		columnList.toArray(columnHeads);
		String rootPath = ApplicationListener.rootPath;
		String absolutePath = "";
		String sheetName = "固定信息";
		if(tableName.equals("t_fixdata")){
			absolutePath = "upload/template/固定信息模板.xls";
			sheetName = "固定信息";
		}else if(tableName.equals("t_shortmsg")){
			absolutePath = "upload/template/短期信息模板-new.xls";
			sheetName = "短期信息";
		}else if(tableName.equals("t_rengongxx")){
			absolutePath = "upload/template/人工信息模板.xls";
			sheetName = "人工信息";
		}
		String filepath = rootPath+"/"+absolutePath;
		log.info("updateExcelModuls[filepath]="+filepath);
		try {
			ExcelUtil.exportExcelModuls(filepath, sheetName, columnHeads);
			log.info("重新生成数据表["+tableName+"]表格数据模板成功！");
		} catch (Exception e) {
			log.error("Exception", new Throwable(e));
		}
	}
	/**
	 * 功能描述：判断字段是否为用户添加的字段
	 * 创建作者：雷志强
	 * 创建时间：2017年8月3日 上午1:08:02
	 * @param table 表名称
	 * @param column 字段名称
	 * @return boolean
	 */
	private boolean checkCustomColumn(String table,String column){
		boolean isCustomColumn = true;
		String columns = WebConfig.getConfig(table+"_columns");
		String[] columnArr = columns.split(",");
		int count = columnArr.length;
		for(int i=0;i<count;i++){
			if(columnArr[i].equals(column)){
				isCustomColumn = false;
				break;
			}
		}
		return isCustomColumn;
	}
	
	/**
	 * 功能描述：将表的字段信息存储到配置文件
	 * 创建作者：雷志强
	 * 创建时间：2017年8月2日 下午11:46:27
	 * @param tableName 表名
	 * @return String
	
	public String saveTableColumnInfo(String tableName){
		QuerySqlConfig config = new QuerySqlConfig();
		String baseSql = config.getConfigSQL("getTableColumns");
		String sql = baseSql.replaceAll("#table#", tableName);
		List<JSONObject> list = dao.listAll(sql);
		log.info("saveTableColumnInfo[list]="+list);
		JSONArray columns = new JSONArray();
		int count = list.size();
		log.info("saveTableColumnInfo[listcount]="+count);
		JSONObject record = null;
		JSONObject column = null;
		for(int i=0;i<count;i++){
			record = list.get(i);
			column = new JSONObject();
			column.put("field", record.getString("column_name"));
			column.put("text", record.getString("description"));
			column.put("type", record.getString("data_type"));
			columns.add(column);
		}
		log.info("saveTableColumnInfo[columns]="+columns);
		WebConfig webConfig = new WebConfig();
		webConfig.saveConfig(tableName+"_columns", "表["+tableName+"]字段信息", columns.toJSONString());
		log.info("更新数据表字段配置文件！");
		updateExcelModuls(list,tableName);
		dataMap.put("state", true);
		dataMap.put("error", "");
		return "map";
	}
	 */
	/**
	 * 功能描述：根据经纬度和半径查询业务图层数据
	 * 创建作者：雷志强
	 * 创建时间：2017年9月19日 上午9:47:22
	 * @return String
	 * /
	public String queryNearLayerData(){
		String lonstr = requestParam.getString("lon");
		String latstr = requestParam.getString("lat");
		double lon = Double.parseDouble(lonstr);
		double lat = Double.parseDouble(latstr);
		String r = requestParam.getString("r");
		int raidus = Integer.parseInt(r);
		String queryInfo = requestParam.getString("queryInfo");
		try {
			DataImpl impl = new DataImpl();
			JSONArray data = impl.queryNearLayerData(lon, lat, raidus, queryInfo);
			this.dataMap.put("total", data.size());
			this.dataMap.put("rows", data);
		} catch (Exception e) {
			log.error("IOException", new Throwable(e));
		}
		return "map";
	}
	
	/**
	 * 功能描述：根据经纬度和半径查询百度周边POI信息点
	 * 创建作者：雷志强
	 * 创建时间：2017年9月19日 上午1:23:06
	 * @return String
	 * /
	public String queryNearPoiData(){
		this.dataMap.remove("state");
		this.dataMap.remove("error");
		String lon = requestParam.getString("lon");
		String lat = requestParam.getString("lat");
		String r = requestParam.getString("r");
		String keywords = requestParam.getString("keywords");
		int pageno = requestParam.getIntValue("page");
		int pagesize = requestParam.getIntValue("rows");
		try {
			JSONObject result = BaiDuUtil.searchPlace(lon, lat, r, keywords,pageno,pagesize);
			if(result.getIntValue("status")==0){
				int total = result.getIntValue("total");
				JSONArray rows = result.getJSONArray("results");
				this.dataMap.put("total", total);
				this.dataMap.put("rows", rows);
			}
		} catch (IOException e) {
			log.error("IOException", new Throwable(e));
		}
		return "map";
	}
	
	public String testAction(){
		dataMap.put("nowtime", DateUtil.date2String("yyyy-MM-dd HH:mm:ss"));
		return "map";
	}
	//添加表字段
	public String addColumn()throws HibernateException{
		String logName = "["+requestSessionId+"]addColumn()";
		log.info("logName="+logName);
		QuerySqlConfig config = new QuerySqlConfig();
		String sql = config.getConfigSQL("addTableColumn");
		sql = config.getReplaceSqlParam(sql,requestParam);
		log.info(logName+"sql="+sql);
		int result = -1;
		try {
			result = dao.updateObject(sql);
			sql = config.getConfigSQL("addTableColumnComment");
			sql = config.getReplaceSqlParam(sql,requestParam);
			log.info(logName+"sql="+sql);
			result = dao.updateObject(sql);
			dataMap.put("state", true);
			dataMap.put("data", result);
			saveTableColumnInfo(requestParam.getString("table"));
		} catch (Exception e) {
			dataMap.put("state", false);
			dataMap.put("error", e.getMessage());
			log.error("saveData[Exception]", new Throwable(e));
		}
		log.info(logName+"dataMap="+dataMap);
		return "map";
	}
	//修改表字段
	public String editColumn()throws HibernateException{
		String logName = "["+requestSessionId+"]editColumn()";
		log.info("logName="+logName);
		QuerySqlConfig config = new QuerySqlConfig();
		String sql = config.getConfigSQL("editTableColumn");
		sql = config.getReplaceSqlParam(sql,requestParam);
		log.info(logName+"sql="+sql);
		int result = -1;
		try {
			result = dao.updateObject(sql);
			if(requestParam.containsKey("addcomment")){
				sql = config.getConfigSQL("addTableColumnComment");
				sql = config.getReplaceSqlParam(sql,requestParam);
				log.info(logName+"sql="+sql);
				result = dao.updateObject(sql);
			}else if(requestParam.containsKey("editcomment")){
				sql = config.getConfigSQL("editTableColumnComment");
				sql = config.getReplaceSqlParam(sql,requestParam);
				log.info(logName+"sql="+sql);
				result = dao.updateObject(sql);
			}
			saveTableColumnInfo(requestParam.getString("table"));
			dataMap.put("state", true);
			dataMap.put("data", result);
		} catch (Exception e) {
			dataMap.put("state", false);
			dataMap.put("error", e.getMessage());
			log.error("saveData[Exception]", new Throwable(e));
		}
		log.info(logName+"dataMap="+dataMap);
		return "map";
	}
	
	//删除表字段
	public String deleteColumn()throws HibernateException{
		String logName = "["+requestSessionId+"]deleteColumn()";
		log.info("logName="+logName);
		QuerySqlConfig config = new QuerySqlConfig();
		String sql = config.getConfigSQL("deleteTableColumn");
		sql = config.getReplaceSqlParam(sql,requestParam);
		log.info(logName+"sql="+sql);
		int result = -1;
		try {
			String table = requestParam.getString("table");
			String column = requestParam.getString("column_name");
			boolean isCustomColumn = checkCustomColumn(table, column);
			if(isCustomColumn){
				result = dao.updateObject(sql);
				saveTableColumnInfo(requestParam.getString("table"));
				dataMap.put("state", true);
				dataMap.put("data", result);
			}else{
				dataMap.put("state", false);
				dataMap.put("data", "系统自带字段不能删除！");
			}
		} catch (Exception e) {
			dataMap.put("state", false);
			dataMap.put("error", e.getMessage());
			log.error("saveData[Exception]", new Throwable(e));
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
			log.error("editData[Exception]", new Throwable(e));
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
			log.error("listAll[Exception]", new Throwable(e));
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
			log.error("listByPage[Exception]", new Throwable(e));
		}
		return "map";
	}
	
	public String loadLayerDataByPage(){
		dataMap = new LinkedHashMap<String, Object>();
		String logName = "["+requestSessionId+"]loadLayerDataByPage()";
		log.info("logName="+logName);
		log.info(requestParam);
		List<JSONObject> list = new ArrayList();
		try {
			String tableName = requestParam.getString("table");
			String codes = requestParam.getString("codes");
			QuerySqlConfig config = new QuerySqlConfig();
			String baseSql = config.getConfigSQL("getTableDataCount");
			String sql = config.getReplaceSqlParam(baseSql, requestParam);
			int count = dao.countAll(sql);
			baseSql = config.getConfigSQL("getTableDataByPage");
			sql = config.getReplaceSqlParam(baseSql,requestParam);
			log.info(logName+"sql="+sql);
			int pageNo = requestParam.getIntValue("page");
			int pageSize = requestParam.getIntValue("rows");
			list = dao.listAll(sql,pageNo,pageSize);
			//如果是短期信息，则进行相关数据处理
			if(tableName.equals("t_shortmsg")){
				sql = config.getConfigSQL("listLinePoint");
				List<JSONObject> mileageList = dao.listAll(sql);
				log.info("mileageList="+mileageList);
				int dataCount = list.size();
				List<JSONObject> newList = new ArrayList<JSONObject>();
				JSONObject record = null;
				String job_address = null;
				String mileagestr = null;
				JSONObject mileagePoint = null;
				for(int i=0;i<dataCount;i++){
					record = list.get(i);
					job_address = record.getString("job_address");
					mileagestr = DataImport.readMileageString(job_address);
					mileagePoint = getMeliagePoint(mileageList, mileagestr);
					record.put("kslcs", mileagePoint.getString("kslcs"));
					record.put("jslcs", mileagePoint.getString("jslcs"));
					if(mileagePoint.containsKey("startpoint")){
						record.put("longitude", mileagePoint.getJSONObject("startpoint").getDoubleValue("longitude"));
						record.put("latitude", mileagePoint.getJSONObject("startpoint").getDoubleValue("latitude"));
					}else if(mileagePoint.containsKey("endpoint")){
						record.put("longitude", mileagePoint.getJSONObject("endpoint").getDoubleValue("longitude"));
						record.put("latitude", mileagePoint.getJSONObject("endpoint").getDoubleValue("latitude"));
					}
					newList.add(record);
				}
				list = newList;
			}
			dataMap.put("total", count);
			dataMap.put("rows", list);
		} catch (Exception e) {
			dataMap.put("state", false);
			dataMap.put("error", e.getMessage());
			log.error("listByPage[Exception]", new Throwable(e));
		}
		return "map";
	}
	
	private static JSONObject getMeliagePoint(List<JSONObject> list,String mileagestr){
		JSONObject pointInfo = new JSONObject();
		//从配置文件中读取短期信息中的里程跟里程坐标点中的里程最小值，小于这个值则认为里程坐标匹配
		double minLimeage = Double.parseDouble(WebConfig.getConfig("minMileage"));
		int count = list.size();
		JSONObject rcd = null;
		String[] tmpArr = mileagestr.split("至");
		pointInfo.put("kslcs", tmpArr[0]);
		pointInfo.put("jslcs", tmpArr[1]);
		double mileage1 = Double.parseDouble(tmpArr[0]);
		double mileage2 = Double.parseDouble(tmpArr[1]);
		double mileage = 0;
		double dx1 = 0;
		double dx2 = 0;
		double min_dx1 = 0;
		double min_dx2 = 0;
		JSONObject minPoint_x1 = null;
		JSONObject minPoint_x2 = null;
		for(int i=0;i<count;i++){
			rcd = list.get(i);
			mileage = rcd.getDoubleValue("mileage");
			dx1 = Math.abs(mileage-mileage1);
			dx2 = Math.abs(mileage-mileage2);
			if(i==0){
				min_dx1 = dx1;
				min_dx2 = dx2;
				minPoint_x1 = rcd;
				minPoint_x2 = rcd;
			}else{
				if(dx1<min_dx1){
					min_dx1 = dx1;
					minPoint_x1 = rcd;
				}
				if(dx2<min_dx2){
					min_dx2 = dx2;
					minPoint_x2 = rcd;
				}
			}
		}
		if(min_dx1<minLimeage){
			pointInfo.put("startpoint", minPoint_x1);
		}
		if(min_dx2<minLimeage){
			pointInfo.put("endpoint", minPoint_x2);
		}
		return pointInfo;
	}
	
	private String getShortMsgDataLocation(String address){
		Pattern p = Pattern.compile("\\d+.+\\d{2,}");//这个2是指连续数字的最少个数
        Matcher m = p.matcher(address);
        int i = 0;
        List<String> matchList = new ArrayList<String>();
        while (m.find()) {
        	matchList.add(m.group());
            i++;
        }
        return matchList.get(0);
	}
	
	/**
	 * 功能描述：根据表字段数据生成对应的模板文件
	 * 创建作者：雷志强
	 * 创建时间：2017年8月2日 下午11:07:46
	 * @param list
	 * @param tableName
	 * @return void
	 * /
	private void updateExcelModuls(List<JSONObject> list,String tableName){
		int count = list.size();
		log.info("updateExcelModuls[count]="+count);
		JSONObject record = null;
		String column_name = null;
		List<String> columnList = new ArrayList<String>();
		String columnInfo = ""; 
		for(int i=2;i<count;i++){
			record = list.get(i);
			column_name = record.getString("column_name");
			if(column_name.contains("createtime")||column_name.contains("edittime")||column_name.contains("state")){
				continue;
			}
			columnInfo = record.getString("description")+"("+record.getString("column_name")+")";
			columnList.add(columnInfo);
		}
		log.info("updateExcelModuls[columnList]="+columnList);
		String[] columnHeads = new String[columnList.size()];
		columnList.toArray(columnHeads);
		String rootPath = ApplicationListener.rootPath;
		String absolutePath = "";
		String sheetName = "固定信息";
		if(tableName.equals("t_fixdata")){
			absolutePath = "upload/template/固定信息模板.xls";
			sheetName = "固定信息";
		}else if(tableName.equals("t_shortmsg")){
			absolutePath = "upload/template/短期信息模板-new.xls";
			sheetName = "短期信息";
		}else if(tableName.equals("t_rengongxx")){
			absolutePath = "upload/template/人工信息模板.xls";
			sheetName = "人工信息";
		}
		String filepath = rootPath+"/"+absolutePath;
		log.info("updateExcelModuls[filepath]="+filepath);
		try {
			ExcelUtil.exportExcelModuls(filepath, sheetName, columnHeads);
			log.info("重新生成数据表["+tableName+"]表格数据模板成功！");
		} catch (Exception e) {
			log.error("Exception", new Throwable(e));
		}
	}
	/**
	 * 功能描述：判断字段是否为用户添加的字段
	 * 创建作者：雷志强
	 * 创建时间：2017年8月3日 上午1:08:02
	 * @param table 表名称
	 * @param column 字段名称
	 * @return boolean
	  * /
	private boolean checkCustomColumn(String table,String column){
		boolean isCustomColumn = true;
		String columns = WebConfig.getConfig(table+"_columns");
		String[] columnArr = columns.split(",");
		int count = columnArr.length;
		for(int i=0;i<count;i++){
			if(columnArr[i].equals(column)){
				isCustomColumn = false;
				break;
			}
		}
		return isCustomColumn;
	}
	
	/**
	 * 功能描述：将表的字段信息存储到配置文件
	 * 创建作者：雷志强
	 * 创建时间：2017年8月2日 下午11:46:27
	 * @param tableName 表名
	 * @return String
	 * /
	public String saveTableColumnInfo(String tableName){
		QuerySqlConfig config = new QuerySqlConfig();
		String baseSql = config.getConfigSQL("getTableColumns");
		String sql = baseSql.replaceAll("#table#", tableName);
		List<JSONObject> list = dao.listAll(sql);
		log.info("saveTableColumnInfo[list]="+list);
		JSONArray columns = new JSONArray();
		int count = list.size();
		log.info("saveTableColumnInfo[listcount]="+count);
		JSONObject record = null;
		JSONObject column = null;
		for(int i=0;i<count;i++){
			record = list.get(i);
			column = new JSONObject();
			column.put("field", record.getString("column_name"));
			column.put("text", record.getString("description"));
			column.put("type", record.getString("data_type"));
			columns.add(column);
		}
		log.info("saveTableColumnInfo[columns]="+columns);
		WebConfig webConfig = new WebConfig();
		webConfig.saveConfig(tableName+"_columns", "表["+tableName+"]字段信息", columns.toJSONString());
		log.info("更新数据表字段配置文件！");
		updateExcelModuls(list,tableName);
		dataMap.put("state", true);
		dataMap.put("error", "");
		return "map";
	}
	*/
	public static void main(String[] args) {
//		List<String> list = new ArrayList<String>();
//		list.add("aaaaa");
//		list.add("bbbbb");
//		list.add("ccccc");
//		System.out.println(list);
//		String[] strArr = new String[list.size()];
//		list.toArray(strArr);
//		System.out.println(strArr[0]);
//		System.out.println(strArr[1]);
//		System.out.println(strArr[2]);
		//testAddLayer();
		//testEditLayer();
		//testDeleteLayer();
		try {
			//testDeleteLayerClass();
			testSearchLayer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("OK");
	}
	
	public static void testSearchLayer(){
		LayerAction action = new LayerAction();
		action.requestParam.put("table", "t_shortmsg");
		action.requestParam.put("keyword", "");
		action.requestParam.put("page", 1);
		action.requestParam.put("rows", 10);
		action.searchLayerDataByPage();
		System.out.println(action.dataMap);
	}
	
	public static void testDeleteLayerClass(){
		LayerAction action = new LayerAction();
		action.requestParam.put("method", "deleteLayerByIds");
		action.requestParam.put("ids", "'3c87617f59bb42af8abaf63990a1cf8d','59655823a2544480b3527f300c4d858f','7f0eec2ab4e848a2a95672edcf55fd57','aa80caa2a1b04987a9bc3d06285e34bd','f9c570f54024460e80508f5bdad4f7e1'");
		action.deleteLayerClass();
	}
	
	public static void testAddLayer(){
		LayerAction action = new LayerAction();
		action.requestParam.put("method", "addLayer");
		action.requestParam.put("name", "测试图层");
		action.requestParam.put("pcode", "1");
		action.addLayer();
	}
	
	public static void testEditLayer(){
		LayerAction action = new LayerAction();
		action.requestParam.put("method", "editLayer");
		action.requestParam.put("name", "测试图层123");
		action.requestParam.put("iconurl", "images/icon_location_20X24.png");
		action.requestParam.put("id", "e2c88c0e8fc846cb9a0c518989e62d64");
		action.editLayer();
	}
	
	public static void testDeleteLayer(){
		LayerAction action = new LayerAction();
		action.requestParam.put("method", "deleteLayer");
		action.requestParam.put("id", "95bf7a14e12246f6a5e8fda7d3b01ba4");
		action.deleteLayer();
	}
	
	public static void testCreateExcelTemplate() throws Exception{
		LayerAction action = new LayerAction();
		action.requestParam.put("table", "t_layer_21");
		action.requestParam.put("layername", "测试图层");
		action.createLayerExcelTemplate();
	}
}

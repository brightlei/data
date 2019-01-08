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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zondy.config.ParamConfig;
import com.zondy.config.QuerySqlConfig;
import com.zondy.config.WebConfig;
import com.zondy.listener.ApplicationListener;
import com.zondy.utils.DateUtil;
import com.zondy.utils.ExcelUtil;
import com.zondy.utils.FileUtil;
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
public class WebAction extends BaseAction{
	
	private static Logger log = Logger.getLogger(WebAction.class);
	//long:serialVersionUID 用一句话描述这个变量
	private static final long serialVersionUID = 1L;
	public String testAction(){
		dataMap.put("nowtime", DateUtil.date2String("yyyy-MM-dd HH:mm:ss"));
		return "map";
	}
	
	/**
	 * 功能描述：保存图层数据
	 * 创建作者：雷志强
	 * 创建时间：2017年8月8日 上午8:58:15
	 * @return String
	 */
	public String saveLayerData(){
		this.dataMap.put("state", true);
		this.dataMap.put("error", "");
		String sql = "";
		String table = requestParam.getString("table");
		requestParam.remove("table");
		Set<Entry<String, Object>> set = requestParam.entrySet();
		Iterator<Entry<String, Object>> iterator = set.iterator();
		Entry<String, Object> entry = null;
		String key = "";
		String val = "";
		StringBuffer columns = new StringBuffer();
		StringBuffer values = new StringBuffer();
		while(iterator.hasNext()){
			entry = iterator.next();
			key = entry.getKey();
			val = (String)entry.getValue();
			columns.append(key).append(",");
			values.append("'"+val+"'").append(",");
		}
		String columnstr = columns.toString();
		String valuestr = values.toString();
		columnstr = columnstr.substring(0, columnstr.length()-1);
		valuestr = valuestr.substring(0,valuestr.length()-1);
		String primaryKey = ParamConfig.getColumnValue("getUid", "");
		sql = "insert into "+table+"(id,"+columnstr+") values('"+primaryKey+"',"+valuestr+")";
		log.info("sql="+sql);
		int result = dao.saveObject(sql);
		this.dataMap.put("data", result);
		log.info("dataMap="+dataMap);
		return "map";
	}
	/**
	 * 功能描述：修改图层数据
	 * 创建作者：雷志强
	 * 创建时间：2017年8月8日 上午9:11:59
	 * @return String
	 */
	public String editLayerData(){
		this.dataMap.put("state", true);
		this.dataMap.put("error", "");
		String sql = "";
		String table = requestParam.getString("table");
		String id = requestParam.getString("id");
		requestParam.remove("table");
		requestParam.remove("id");
		Set<Entry<String, Object>> set = requestParam.entrySet();
		Iterator<Entry<String, Object>> iterator = set.iterator();
		Entry<String, Object> entry = null;
		String key = "";
		String val = "";
		StringBuffer columns = new StringBuffer();
		while(iterator.hasNext()){
			entry = iterator.next();
			key = entry.getKey();
			val = (String)entry.getValue();
			columns.append(key+"='"+val+"'").append(",");
		}
		String columnstr = columns.toString();
		columnstr = columnstr.substring(0, columnstr.length()-1);
		sql = "update "+table+" set "+columnstr+" where id='"+id+"'";
		log.info("sql="+sql);
		int result = dao.updateObject(sql);
		this.dataMap.put("data", result);
		log.info("dataMap="+dataMap);
		return "map";
	}
	/**
	 * 功能描述：删除图层数据
	 * 创建作者：雷志强
	 * 创建时间：2017年8月8日 上午9:12:13
	 * @return String
	 */
	public String deleteLayerData(){
		this.dataMap.put("state", true);
		this.dataMap.put("error", "");
		String sql = "";
		String table = requestParam.getString("table");
		String id = requestParam.getString("id");
		requestParam.remove("table");
		requestParam.remove("id");
		sql = "delete from "+table+" where id='"+id+"'";
		log.info("sql="+sql);
		int result = dao.updateObject(sql);
		this.dataMap.put("data", result);
		log.info("dataMap="+dataMap);
		return "map";
	}
	
	/**
	 * 功能描述：获取所有配置信息列表
	 * 创建作者：雷志强
	 * 创建时间：2017年8月3日 上午1:14:31
	 * @return
	 * @return String
	 */
	public String loadConfigs(){
		WebConfig config = new WebConfig();
		JSONArray list = config.loadConfigList();
		dataMap.put("list", list);
		System.out.println(dataMap);
		return "map";
	}
	/**
	 * 功能描述：修改配置信息
	 * 创建作者：雷志强
	 * 创建时间：2017年8月3日 上午1:43:31
	 * @return String
	 */
	public String editConfig(){
		String nodeName = requestParam.getString("nodeName");
		String nodeValue = requestParam.getString("nodeValue");
		WebConfig config = new WebConfig();
		JSONObject info = config.getAttConfig(nodeName);
		String title = info.getString("title");
		config.saveConfig(nodeName, title, nodeValue);
		dataMap.put("state", true);
		System.out.println(dataMap);
		return "map";
	}
	
	/**
	 * 功能描述：加载图标数据
	 * 创建作者：雷志强
	 * 创建时间：2017年7月13日 下午11:37:53
	 * @return String
	 */
	public String loadIcons(){
		List<String> list = new ArrayList<String>();
		String rootPath = ApplicationListener.rootPath;
		String forldPath = rootPath+"/upload/icon";
		File file = new File(forldPath);
		if(file.exists()){
			File[] files = FileUtil.sortFilesByLashModify(forldPath);
			int count = files.length;
			String filepath = null;
			String absolutePath = null;
			for(int i=0;i<count;i++){
				filepath = files[i].getAbsolutePath();
				filepath = filepath.replaceAll("\\\\", "/");
				absolutePath = filepath.substring(filepath.lastIndexOf("/data/")+6, filepath.length());
				list.add(absolutePath);
			}
			this.dataMap.put("data", list);
		}
		return "map";
	}
	
	public String deleteDataImg(){
		String imgid = request.getParameter("id");
		this.dataMap.put("state", true);
		this.dataMap.put("error", "");
		QuerySqlConfig config = new QuerySqlConfig();
		String basesql = config.getConfigSQL("getLayerDataImage");
		String sql = config.getReplaceSqlParam(basesql, requestParam);
		JSONObject record = dao.loadObject(sql);
		if(record==null){
			this.dataMap.put("state", false);
			this.dataMap.put("error", "找不对您要删除的图片！");
		}else{
			String imgpath = record.get("imgpath").toString();
			String smallImgpath = record.get("smallimg").toString();
			String forldPath = WebConfig.getConfig("uploadForld");
			String imgfilepath = forldPath+"/"+imgpath;
			String smallimgfilepath = forldPath+"/"+smallImgpath;
			int ret = dao.updateObject("delete from t_dataimg where id='"+imgid+"'");
			if(ret==1){
				File imgFile = new File(imgfilepath);
				File smallImgFile = new File(smallimgfilepath);
				imgFile.delete();
				smallImgFile.delete();
				this.dataMap.put("data", ret);
			}else{
				this.dataMap.put("state", false);
				this.dataMap.put("error", "图片删除失败！");
			}
		}
		return "map";
	}
	
	/**
	 * 功能描述：删除一个图标
	 * 创建作者：雷志强
	 * 创建时间：2017年7月13日 下午11:45:28
	 * @return String
	 */
	public String deleteIcon(){
		String iconpath = request.getParameter("path");
		this.dataMap.put("state", true);
		this.dataMap.put("error", "");
		if(iconpath!=null){
			String rootPath = ApplicationListener.rootPath;
			String filepath = rootPath+"/"+iconpath;
			File file = new File(filepath);
			if(file.exists()){
				boolean isdelete = file.delete();
				file.deleteOnExit();
				QuerySqlConfig config = new QuerySqlConfig();
				String sql = config.getConfigSQL("deleteLayerIconurl");
				sql = sql.replaceAll("#iconurl#", iconpath);
				dao.updateObject(sql);
				this.dataMap.put("data", isdelete);
			}else{
				this.dataMap.put("state", false);
				this.dataMap.put("error", "找不到你要删除的图标文件，请确保图标文件路径正确！");
			}
		}else{
			this.dataMap.put("state", false);
			this.dataMap.put("error", "要删除的图标文件路径不能为空！");
		}
		return "map";
	}
	
	public String readExcelDataToDb() throws Exception{
		this.dataMap.put("state", true);
		this.dataMap.put("error", "");
		String path = request.getParameter("path");
		String type = request.getParameter("layertype");
		String layercode = request.getParameter("layercode");
		String rootPath = ApplicationListener.rootPath;
		String filepath = rootPath+"/"+path;
		List<List<String>> list = ExcelUtil.readExcel(filepath);
		int count = list.size();
		log.info("count="+count);
		int result = 0;
		if(type.equals("fix")){
			result = importFixData(list,layercode);
		}else if(type.equals("shortmsg")){
			
		}else{
			
		}
		this.dataMap.put("data", result);
		return "map";
	}
	
	private int importFixData(List<List<String>> list,String layercode){
		QuerySqlConfig config = new QuerySqlConfig();
		String baseSql = config.getConfigSQL("importFixData");
		int count = list.size();
		List<String> rowList = null;
		JSONObject record = null;
		String sql = "";
		int successCount = 0;
		for(int i=1;i<count;i++){
			rowList = list.get(i);
			record = getFixDataRecord(rowList);
			String primaryKey = ParamConfig.getColumnValue("getUid", "");
			record.put("primaryKey", primaryKey);
			record.put("layercode", layercode);
			sql = config.getReplaceSqlParam(baseSql, record);
			int ret = dao.saveObject(sql);
			if(ret==1){
				successCount++;
			}
		}
		return successCount;
	}
	
	//将表格数据转换成Json对象
	private JSONObject getFixDataRecord(List<String> rowList){
		JSONObject record = new JSONObject();
		if(rowList!=null&&rowList.size()>0){
			try {
				record.put("name", rowList.get(0));
				record.put("description", rowList.get(1));
				record.put("mainperson", rowList.get(2));
				record.put("telephone", rowList.get(3));
				record.put("workshop", rowList.get(4));
				record.put("workarea", rowList.get(5));
				record.put("longitude", rowList.get(6));
				record.put("latitude", rowList.get(7));
				record.put("address", rowList.get(8));
			} catch (Exception e) {
				log.error("Exception", new Throwable(e));
			}
		}
		return record;
	}
	/**
	 * 功能描述：加载系统运行日志功能
	 * 创建作者：雷志强
	 * 创建时间：2017年7月16日 下午10:44:27
	 * @return String
	 */
	public String listLogs(){
		log.info(this.requestParam);
		QuerySqlConfig config = new QuerySqlConfig();
		String sql = config.getConfigSQL("listLog");
		int count = 0;
		int pageNo = 1;
		int pageSize = 10;
		List<?> list = new ArrayList();
		String key = requestParam.getString("key");
		if(requestParam.containsKey("page")){
			pageNo = requestParam.getIntValue("page");
		}
		if(requestParam.containsKey("rows")){
			pageSize = requestParam.getIntValue("rows");
		}
		if(key.equals("")){
			count = dao.countAll("select count(*) from t_log4j");
			list = dao.listAll(sql, pageNo, pageSize);
		}else{
			count = dao.countAll("select count(*) from t_log4j where logLevel='"+key+"'");
			list = dao.listAll("select * from t_log4j where logLevel='"+key+"' order by id desc", pageNo, pageSize);
		}
		this.dataMap.put("total", count);
		this.dataMap.put("rows", list);
		return "map";
	}
	
	public static void main(String[] args) {
		WebAction action = new WebAction();
		try {
			//testSaveLayerData();
			//testEditLayerData();
			testDeleteLayerData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("OK");
	}
	
	public static void testSaveLayerData(){
		WebAction action = new WebAction();
		action.requestParam.put("table", "t_fixdata");
		action.requestParam.put("layercode", "aaaa");
		action.requestParam.put("longitude", "113.132");
		action.requestParam.put("latitude", "30.123");
		action.requestParam.put("name", "测试一下");
		action.saveLayerData();
	}
	
	public static void testEditLayerData(){
		WebAction action = new WebAction();
		action.requestParam.put("table", "t_fixdata");
		action.requestParam.put("id", "faea3b5094014061872ed0015180aaee");
		action.requestParam.put("layercode", "bbbbbb");
		action.requestParam.put("longitude", "113.132");
		action.requestParam.put("latitude", "30.123");
		action.requestParam.put("name", "测试一下修改222");
		action.requestParam.put("description", "测试一下修改222");
		action.requestParam.put("address", "测试一下修改22");
		action.requestParam.put("workshop", "测试一下修改22");
		action.editLayerData();
	}
	
	public static void testDeleteLayerData(){
		WebAction action = new WebAction();
		action.requestParam.put("table", "t_fixdata");
		action.requestParam.put("id", "f43360a9a227483c90fea75ac9ee28c2");
		action.deleteLayerData();
	}
}

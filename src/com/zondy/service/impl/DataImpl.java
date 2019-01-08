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
package com.zondy.service.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zondy.base.DAO.BaseDAOImpl;
import com.zondy.config.QuerySqlConfig;
import com.zondy.config.WebConfig;
import com.zondy.listener.ApplicationListener;
import com.zondy.utils.DotUtils;
import com.zondy.web.DataImport;

/**
 * 模块名称：该模块名称
 * 功能描述：该文件详细功能描述
 * 文档作者：雷志强
 * 创建时间：2017年5月11日 上午12:30:39
 * 初始版本：V1.0
 * 修改记录：
 * *************************************************
 * 修改人：雷志强
 * 修改时间：2017年5月11日 上午12:30:39
 * 修改内容：
 * *************************************************
 */
public class DataImpl {
	private static Logger log = Logger.getLogger(DataImpl.class);
	//返回结果
	private JSONObject resultJson = new JSONObject();
	public JSONObject queryLocation(){
		
		
		return resultJson;
	}
	
	public JSONObject login(String username,String userpwd){
		if(username.equals("admin")){
			if(userpwd.equals("admin")){
				JSONObject user = new JSONObject();
				user.put("userid", 2);
				user.put("xm", "雷志强");
				resultJson.put("data", user);
				resultJson.put("state", true);
				resultJson.put("error", "");
			}else{
				resultJson.put("state", false);
				resultJson.put("error", "用户名登录密码不正确！");
			}
		}else{
			resultJson.put("state", false);
			resultJson.put("error", "该用户名不存在！");
		}
		return resultJson;
	}
	/**
	 * 功能描述：周边信息查询模块
	 * 创建作者：雷志强
	 * 创建时间：2017年11月15日 上午12:24:09
	 * @param lon 定位点中心点经度
	 * @param lat 定位点中心点纬度
	 * @param raidus 查询半径（单位：米）
	 * @param queryInfo 查询图层表字符串
	 * @return JSONArray
	 */
	public JSONArray queryNearLayerData(double lon,double lat,int raidus,String queryInfo){
		JSONArray dataArray = new JSONArray();
		JSONObject paramJson = DotUtils.getAround(lat, lon, raidus);
		double dx = paramJson.getDoubleValue("xmax")-paramJson.getDoubleValue("xmin");
		double dy = paramJson.getDoubleValue("ymax")-paramJson.getDoubleValue("ymin");
		double r = 0;
		if(dx>dy){
			r = dx/2;
		}else{
			r = dy/2;
		}
		String[] layerArray = queryInfo.split(",");
		int layerCount = layerArray.length;
		String tableName = "";
		QuerySqlConfig config = new QuerySqlConfig();
		BaseDAOImpl dao = ApplicationListener.dao;
		String basesql = "";
		String sql = "";
		List<JSONObject> list = null;
		//里程坐标点集合
		List<JSONObject> mileageList = null;
		JSONArray matchData = null;
		for(int i=0;i<layerCount;i++){
			tableName = layerArray[i];
			paramJson.put("table", tableName);
			if(tableName.equals("t_shortmsg")){
				basesql = config.getConfigSQL("queryNearShortMsgData");
				sql = config.getReplaceSqlParam(basesql, paramJson);
				//log.info("["+tableName+"]sql="+sql);
				list = dao.listAll(sql);
				sql = config.getConfigSQL("listLinePoint");
				mileageList = dao.listAll(sql);
				matchData = getShortMsgLayerData(lon,lat,r,mileageList,list,tableName);
			}else{
				basesql = config.getConfigSQL("queryNearLayerData");
				sql = config.getReplaceSqlParam(basesql, paramJson);
				//log.info("["+tableName+"]sql="+sql);
				list = dao.listAll(sql);
				matchData = checkDataInCircle(lon, lat, r, list, tableName);
				dataArray.addAll(matchData);
			}
		}
		return dataArray;
	}
	/**
	 * 功能描述：查询范围内短期信息
	 * 创建作者：雷志强
	 * 创建时间：2017年11月15日 上午12:10:19
	 * @return JSONArray 符合条件的数据
	 */
	public static JSONArray getShortMsgLayerData(double lon,double lat,double r,List<JSONObject> mileageList,List<JSONObject> dataList,String tableName){
		//存储符合条件的数据
		JSONArray matchData = new JSONArray();
		//根据查询范围找到在区域内的里程坐标点
		JSONArray matchMileageData = checkPointInCircle(lon, lat, r, mileageList);
		log.info("matchMileageData="+matchMileageData);
		int matchMileageCount = matchMileageData.size();
		log.info("matchMileageCount="+matchMileageCount);
		if(matchMileageCount>0){
			int count = dataList.size();
			JSONObject record = null;
			String job_address = "";
			String mileageInfo = "";
			JSONObject inCirclePoint = new JSONObject();
			for(int i=0;i<count;i++){
				record = dataList.get(i);
				job_address = record.getString("job_address");
				mileageInfo = DataImport.readMileageString(job_address);
				System.out.println(mileageInfo);
				inCirclePoint = getMileagePoint(mileageInfo,matchMileageData);
				//判断短信信息中作业地点中的里程数能不能在区域内的里程坐标点匹配
				if(inCirclePoint.getBooleanValue("isIn")){
					record.put("layer", "短期信息");
					record.put("kslcs", inCirclePoint.getString("kslcs"));
					record.put("jslcs", inCirclePoint.getString("jslcs"));
					if(inCirclePoint.containsKey("startpoint")){
						record.put("longitude", inCirclePoint.getJSONObject("startpoint").getDoubleValue("longitude"));
						record.put("latitude", inCirclePoint.getJSONObject("startpoint").getDoubleValue("latitude"));
					}else if(inCirclePoint.containsKey("endpoint")){
						record.put("longitude", inCirclePoint.getJSONObject("endpoint").getDoubleValue("longitude"));
						record.put("latitude", inCirclePoint.getJSONObject("endpoint").getDoubleValue("latitude"));
					}
					record.put("layer", tableName);
					matchData.add(record);
				}
			}
		}
		return matchData;
	}
	
	/**
	 * 功能描述：判断短期信息中哪些数据在查询范围内
	 * 创建作者：雷志强
	 * 创建时间：2017年9月20日 上午9:40:34
	 * @param mileagestr
	 * @param points
	 * @return JSONObject
	 */
	private static JSONObject getMileagePoint(String mileagestr,JSONArray points){
		//从配置文件中读取短期信息中的里程跟里程坐标点中的里程最小值，小于这个值则认为里程坐标匹配
		double minLimeage = Double.parseDouble(WebConfig.getConfig("minMileage"));
		JSONObject pointInfo = new JSONObject();
		pointInfo.put("isIn", false);
		int count = points.size();
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
			rcd = points.getJSONObject(i);
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
			pointInfo.put("isIn", true);
			pointInfo.put("startpoint", minPoint_x1);
		}
		if(min_dx2<minLimeage){
			pointInfo.put("isIn", true);
			pointInfo.put("endpoint", minPoint_x2);
		}
		return pointInfo;
	}
	
	/**
	 * 功能描述：判断里程坐标点是否在查询范围内
	 * 创建作者：雷志强
	 * 创建时间：2017年9月20日 上午2:32:17
	 * @param lon 圆心X坐标
	 * @param lat 圆心Y坐标
	 * @param radius 半径
	 * @param list 查询结果
	 * @return JSONArray 在查询范围内的结果
	 */
	private static JSONArray checkPointInCircle(double lon,double lat,double radius,List<JSONObject> list){
		JSONArray dataArray = new JSONArray();
		if(list!=null){
			int count = list.size();
			JSONObject record = null;
			double px = 0.0;
			double py = 0.0;
			boolean isIn = false;
			for(int i=0;i<count;i++){
				record = list.get(i);
				px = record.getDoubleValue("longitude");
				py = record.getDoubleValue("latitude");
				isIn = DotUtils.inCircle(lon, lat, radius, px, py);
				if(isIn){//判断记录坐标点是否在圆里面，在的话就添加到数组中
					dataArray.add(record);
				}
			}
		}
		return dataArray;
	}
	
	/**
	 * 功能描述：判断查询到的数据是否在查询范围内
	 * 创建作者：雷志强
	 * 创建时间：2017年9月19日 下午3:06:22
	 * @param lon 圆心X坐标
	 * @param lat 圆心Y坐标
	 * @param radius 半径
	 * @param list 查询结果
	 * @return JSONArray 在查询范围内的结果
	 */
	private static JSONArray checkDataInCircle(double lon,double lat,double radius,List<JSONObject> list,String layer){
		JSONArray dataArray = new JSONArray();
		if(list!=null){
			int count = list.size();
			JSONObject record = null;
			double px = 0.0;
			double py = 0.0;
			boolean isIn = false;
			for(int i=0;i<count;i++){
				record = list.get(i);
				px = record.getDoubleValue("longitude");
				py = record.getDoubleValue("latitude");
				isIn = DotUtils.inCircle(lon, lat, radius, px, py);
				if(isIn){//判断记录坐标点是否在圆里面，在的话就添加到数组中
					record.put("layer", layer);
					dataArray.add(record);
				}
			}
		}
		return dataArray;
	}
	
	/***以下是测试方法************/
	
	public static void main(String[] args) {
		testQueryNearLayerData();
		System.out.println("OK");
	}
	
	public static void testQueryNearLayerData(){
		double lon = 114.32;
		double lat = 33.24;
		int raidus = 10000;
		String queryInfo = "t_layer_31";
		DataImpl impl = new DataImpl();
		JSONArray data = impl.queryNearLayerData(lon,lat,raidus,queryInfo);
		System.out.println(data);
	}
	
	public static void createJson(){
		JSONObject json = new JSONObject();
		json.put("GJZB", "AAAAA");
		json.put("XMSJ", "AAAAA");
		System.out.println(json);
	}
}

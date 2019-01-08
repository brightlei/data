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
package com.zondy.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 模块名称：该模块名称								<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：2015-10-6 下午06:43:22					<br>
 * 初始版本：V1.0										<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：2015-10-6 下午06:43:22					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class BaiDuUtil {
	
	private static Logger log = Logger.getLogger(BaiDuUtil.class);
	
	//百度AK密钥
	private static String AK = "bzcSdMH8dH4Im3HjRPUTAulr";
	//定义一些常量
	private static double x_PI = 3.14159265358979324 * 3000.0 / 180.0;
	private static double PI = 3.1415926535897932384626;
	private static double a = 6378245.0;
	private static double ee = 0.00669342162296594323;
	/**
	 * 百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02)的转换
	 * 即 百度 转 谷歌、高德
	 * @param bd_lon
	 * @param bd_lat
	 * @returns {*[]}
	 */
	public static double[] bd09togcj02(double bd_lon, double bd_lat) {
	    double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
	    double x = bd_lon - 0.0065;
	    double y = bd_lat - 0.006;
	    double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
	    double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
	    double gg_lng = z * Math.cos(theta);
	    double gg_lat = z * Math.sin(theta);
	    double[] coord = new double[2];
	    coord[0] = gg_lng;
	    coord[1] = gg_lat;
	    return coord;
	}
	/**
	 * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换
	 * 即谷歌、高德 转 百度
	 * @param lng
	 * @param lat
	 * @returns {*[]}
	 */
	public static double[] gcj02tobd09(double lng,double lat) {
	    double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * x_PI);
	    double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * x_PI);
	    double bd_lng = z * Math.cos(theta) + 0.0065;
	    double bd_lat = z * Math.sin(theta) + 0.006;
	    double[] coord = new double[2];
	    coord[0] = bd_lng;
	    coord[1] = bd_lat;
	    return coord;
	}
	/**
	 * WGS84转GCj02
	 * @param lng
	 * @param lat
	 * @returns {*[]}
	 */
	public static double[] wgs84togcj02(double lng, double lat) {
		double[] coord = new double[2];
	    coord[0] = lng;
	    coord[1] = lat;
		if (out_of_china(lng, lat)) {
	        return coord;
	    }
	    else {
	        double dlat = transformlat(lng - 105.0, lat - 35.0);
	        double dlng = transformlng(lng - 105.0, lat - 35.0);
	        double radlat = lat / 180.0 * PI;
	        double magic = Math.sin(radlat);
	        magic = 1 - ee * magic * magic;
	        double sqrtmagic = Math.sqrt(magic);
	        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
	        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
	        double mglat = lat + dlat;
	        double mglng = lng + dlng;
	        coord[0] = mglng;
		    coord[1] = mglat;
	        return coord;
	    }
	}
	/**
	 * GCJ02 转换为 WGS84
	 * @param lng
	 * @param lat
	 * @returns {*[]}
	 */
	public static double[] gcj02towgs84(double lng,double lat) {
		double[] coord = new double[2];
	    coord[0] = lng;
	    coord[1] = lat;
		if (out_of_china(lng, lat)) {
	        return coord;
	    }
	    else {
	        double dlat = transformlat(lng - 105.0, lat - 35.0);
	        double dlng = transformlng(lng - 105.0, lat - 35.0);
	        double radlat = lat / 180.0 * PI;
	        double magic = Math.sin(radlat);
	        magic = 1 - ee * magic * magic;
	        double sqrtmagic = Math.sqrt(magic);
	        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
	        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
	        double mglat = lat + dlat;
	        double mglng = lng + dlng;
	        coord[0] = lng * 2 - mglng;
		    coord[1] = lat * 2 - mglat;
	        return coord;
	    }
	}
	/**
	 * 功能描述：百度坐标车WGS84坐标<br>
	 * 创建作者：雷志强<br>
	 * 创建时间：2016-5-10 下午03:36:24<br>
	 * @param lng
	 * @param lat
	 * @return double[]
	 */
	public static double[] bd09towgs84(double lng,double lat){
		double[] coord = new double[2];
	    coord[0] = lng;
	    coord[1] = lat;
		if (out_of_china(lng, lat)) {
	        return coord;
	    }
	    else {
	    	coord = bd09togcj02(lng, lat);
	    	double glng = coord[0];
	        double glat = coord[1];
	        coord = gcj02towgs84(glng, glat);
	        return coord;
	    }
	}
	
	public static double transformlat(double lng,double lat) {
	    double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
	    ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
	    ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
	    ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
	    return ret;
	}

	public static double transformlng(double lng,double lat) {
		double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
	    ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
	    ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
	    ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
	    return ret;
	}

	/**
	 * 判断是否在国内，不在国内则不做偏移
	 * @param lng
	 * @param lat
	 * @returns {boolean}
	 */
	public static boolean out_of_china(double lng,double lat) {
	    return (lng < 72.004 || lng > 137.8347) || ((lat < 0.8293 || lat > 55.8271) || false);
	}
	
	public static String getCity(String lat, String lng) throws IOException {
		JSONObject obj = getLocationInfo(lat, lng).getJSONObject("result")
				.getJSONObject("addressComponent");
		return obj.getString("city");
	}
	
	public static JSONObject getPointLocation(String lat, String lng) throws IOException {
		JSONObject obj = getLocationInfo(lat, lng).getJSONObject("result")
				.getJSONObject("addressComponent");
		return obj;
	}

	public static JSONObject getLocationInfo(String lat, String lng)
			throws IOException {
		String serviceUrl = "http://api.map.baidu.com/geocoder/v2/?location="
				+ lat + "," + lng + "&output=json&ak=" + AK + "&pois=1";
		//3wrserviceUrl = "http://m.weather.com.cn/mweather/101200101.shtml";
		//log.info("serviceUrl="+serviceUrl);
		URL url = new URL(serviceUrl);
		InputStream is = url.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"UTF-8"));
		String tmp = null;
		String htmlRet = "";
		while ((tmp = reader.readLine()) != null) {
			htmlRet += tmp + "\r\n";
		}
		String result = htmlRet;
		//log.info("result="+result);
		JSONObject jsonResult = JSONObject.parseObject(result);
		is.close();
		is = null;
		return jsonResult;
	}

	public static JSONObject getLonLatByLocation(String location,String pageNum)
			throws IOException {
		String queryKey = java.net.URLEncoder.encode(location,"UTF-8");
		//System.out.println(queryKey);
		String serviceUrl = "http://api.map.baidu.com/place/v2/search?ak="
				+ AK
				+ "&output=json&query="+queryKey+"&page_size=10&page_num="+pageNum+"&scope=1&region=%25%25";
		//log.info("serviceUrl="+serviceUrl);
		URL url = new URL(serviceUrl);
		InputStream is = url.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"UTF-8"));
		String tmp = null;
		String htmlRet = "";
		while ((tmp = reader.readLine()) != null) {
			htmlRet += tmp + "\r\n";
		}
		String result = htmlRet;
		//log.info("result="+result);
		//System.out.println(result);
		JSONObject jsonResult = JSONObject.parseObject(result);
		JSONArray results = jsonResult.getJSONArray("results");
		int pntCount = results.size();
		JSONObject jObject = null;
		double lng = 0.0;
		double lat = 0.0;
		for(int i=0;i<pntCount;i++){
			jObject = results.getJSONObject(i);
			if(jObject.containsKey("location")){
				lng = jObject.getJSONObject("location").getDouble("lng");
				lat = jObject.getJSONObject("location").getDouble("lat");
				double[] coord = bd09towgs84(lng, lat);
				jObject.getJSONObject("location").put("lng", coord[0]);
				jObject.getJSONObject("location").put("lat", coord[1]);
			}
		}
		log.info("更新百度坐标到WPS84坐标转换2016-05-10");
		is.close();
		is = null;
		return jsonResult;
	}

	//可调出匹配用户输入的关键字的建议列表
	public static JSONObject placeSuggestion(String keyword) throws IOException{
		JSONObject jsonResult = new JSONObject();
		String encodeKeyword = URLEncoder.encode(keyword, "UTF-8");
		String serviceUrl = "http://api.map.baidu.com/place/v2/suggestion?query="+encodeKeyword+"&region=%E5%85%A8%E5%9B%BD&output=json&ak="+AK;
		URL url = new URL(serviceUrl);
		InputStream is = url.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
		String tmp = null;
		String htmlRet = "";
		while ((tmp = reader.readLine()) != null) {
			htmlRet += tmp;
		}
		String result = htmlRet;
		jsonResult = JSONObject.parseObject(result);
		is.close();
		is = null;
		return jsonResult;
	}
	
	public static JSONObject direction() throws IOException{
		String serviceUrl = "http://api.map.baidu.com/direction/v1?mode=driving&origin=清华大学&destination=北京大学&origin_region=北京&destination_region=北京&output=json&ak="+AK;
		URL url = new URL(serviceUrl);
		InputStream is = url.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
		String tmp = null;
		String htmlRet = "";
		while ((tmp = reader.readLine()) != null) {
			htmlRet += tmp + "\r\n";
		}
		String result = htmlRet;
		log.info("result="+result);
		JSONObject jsonResult = JSONObject.parseObject(result);
		is.close();
		is = null;
		return jsonResult;
	}
	
	public static JSONObject searchPlace(String lon,String lat,String r,String keywords,int pageNo,int pageSize) throws IOException{
		JSONObject jsonResult = new JSONObject();
		String serviceUrl = "http://api.map.baidu.com/place/v2/search?query="+keywords+"&page_size="+pageSize+"&page_num="+(pageNo-1)+"&scope=2&output=json&location="+lat+","+lon+"&radius="+r+"&filter=sort_name:distance|sort_rule:1&ak="+AK;
		log.info("serviceUrl="+serviceUrl);
		URL url = new URL(serviceUrl);
		InputStream is = url.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
		String tmp = null;
		String htmlRet = "";
		while ((tmp = reader.readLine()) != null) {
			htmlRet += tmp + "\r\n";
		}
		String result = htmlRet;
		log.info("result="+result);
		jsonResult = JSONObject.parseObject(result);
		is.close();
		is = null;
		return jsonResult;
	}
	
	public static void main(String[] args) {
		try {
			//String date = DateUtil.date2String("yyyy-MM-dd");
			//int day = DateUtil.dayOfWeek();
			//Date dt = DateUtil.dateTimeString2Date("20151003", "yyyyMMdd");
			//String week = DateUtil.getDayOfWeek(dt);
			//System.out.println(week);
			//System.out.println(BaiDuUtil.getPointLocation("44.2520", "92.8980"));
			//System.out.println(BaiDuUtil.getLonLatByLocation("襄阳","0"));
			//"lat":30.511795,"lng":114.404495
			//double[] coord = BaiDuUtil.bd09togcj02(114.404495, 30.511795);
			//System.out.println(coord[0]+","+coord[1]);
			//coord = BaiDuUtil.gcj02towgs84(coord[0], coord[1]);
			//System.out.println(coord[0]+","+coord[1]);
			//116.403322, 39.928689
			//116.390681,39.920619;
			//百度:116.529264, 39.764046
			//正常:116.516666,39.756352>>>>>>lng=116.52899126331,lat=39.763872391326
			//转换后：116.52899126331,39.763872391326
			//0.012641000000002123
			//0.008069999999996469
			//0.01232526331000372
			//0.007520391325996911
			//System.out.println((116.52899126331-116.516666));
			//System.out.println((39.763872391326-39.756352));
			//[{"name":"神武门","location":{"lat":39.928677,"lng":116.403336},"address":"景山前街4号","street_id":"811084a37a30dd1a4107c107","detail":1,"uid":"811084a37a30dd1a4107c107"},{"name":"神武门广场","location":{"lat":39.929577,"lng":116.40318}
			//System.out.println(BaiDuUtil.getLocationInfo("30.1231","114.123123"));
			//116.403215, 39.928932
			//116.390455,,39,921096
			JSONObject json = searchPlace("114.313","30.231","5000","餐饮$银行$酒店$购物",1,10);
			System.out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

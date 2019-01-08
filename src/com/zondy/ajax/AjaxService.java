package com.zondy.ajax;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zondy.config.WebConfig;
import com.zondy.listener.ApplicationListener;
import com.zondy.utils.BaiDuUtil;
import com.zondy.utils.Base64Util;
import com.zondy.utils.DateUtil;
import com.zondy.utils.ImageUtils;
import com.zondy.utils.PinyinUtil;

public class AjaxService {
	//Logger中有五个级别：track,debug,info,warn,error。对于每个级别，分别有五个log方法，以info级别为例子：
	private static Logger log = Logger.getLogger(AjaxService.class);
	public String test(){
		return "hello";
	}
	
	public JSONObject testJson(){
		JSONObject json = new JSONObject();
		json.put("name", "LZQ");
		return json;
	}
	
	public HashMap<String, Object> testMap(){
		HashMap<String, Object> json = new LinkedHashMap<String, Object>();
		json.put("name", "LZQ");
		JSONArray jsonArray = new JSONArray();
		JSONObject record = new JSONObject();
		record.put("id", "11");
		record.put("name", "name1");
		jsonArray.add(record);
		record = new JSONObject();
		record.put("id", "12");
		record.put("name", "name2");
		jsonArray.add(record);
		json.put("data", jsonArray);
		System.out.println(json);
		return json;
	}
	
	public String getNamePinYin(String name){
		String py = PinyinUtil.getPingYin(name);
		return py;
	}
	
	/**
	 * 将用户上传的头像图片压缩为固定尺寸
	 * @param base64str
	 * @return
	 */
	public String uploadUserPhoto(String base64str){
		String absolutePath = "";
		String imgname = "user-"+DateUtil.date2String("yyyyMMddHHmmssSSS")+".png";
		String rootPath = ApplicationListener.rootPath;
		String tmpeFilepath = rootPath+"/temp/"+imgname;
		try {
			boolean result = Base64Util.GenerateImage(base64str, tmpeFilepath);
			if(result){
				int width = Integer.parseInt(WebConfig.getConfig("userPhotoImgWidth"));
				int height = Integer.parseInt(WebConfig.getConfig("userPhotoImgHeight"));
				absolutePath = "upload/userimg/"+imgname;
				String newfilepath = rootPath+"/"+absolutePath;
				ImageUtils.thumbnailsBySize(tmpeFilepath, width, height, newfilepath);
			}
		} catch (IOException e) {
			log.error("用户头像图片上传异常",new Throwable(e));
		}
		return absolutePath;
	}
	
	public String uploadIconImg(String base64str){
		String absolutePath = "";
		String imgname = "icon-"+DateUtil.date2String("yyyyMMddHHmmssSSS")+".png";
		String rootPath = ApplicationListener.rootPath;
		String tmpeFilepath = rootPath+"/temp/"+imgname;
		try {
			boolean result = Base64Util.GenerateImage(base64str, tmpeFilepath);
			if(result){
				int width = Integer.parseInt(WebConfig.getConfig("iconImgWidth"));
				int height = Integer.parseInt(WebConfig.getConfig("iconImgHeight"));
				absolutePath = "upload/icon/"+imgname;
				String newfilepath = rootPath+"/"+absolutePath;
				ImageUtils.thumbnailsBySize(tmpeFilepath, width, height, newfilepath);
			}
		} catch (IOException e) {
			absolutePath = "";
			log.error("图标图片上传异常",new Throwable(e));
		}
		return absolutePath;
	}
	
	/**
	 * 根据经纬度获取地理位置
	 * @param lon 经度
	 * @param lat 纬度
	 * @return
	 */
	public HashMap<String, Object> getLocationByLonLat(String lon,String lat){
		HashMap<String, Object> json = new LinkedHashMap<String, Object>();
		json.put("state", true);
		json.put("error", "");
		try {
			JSONObject info = BaiDuUtil.getLocationInfo(lat, lon);
			String address = "";
			if(info.containsKey("status")){
				int status = info.getIntValue("status");
				if(status==0){
					JSONObject result = info.getJSONObject("result");
					if(result.containsKey("formatted_address")){
						address = result.getString("formatted_address");
					}
				}
			}
			log.debug("info="+info);
			json.put("data", address);
		} catch (IOException e) {
			json.put("state", false);
			json.put("error", e.getMessage());
			log.error("根据经纬度获取地理位置异常：",new Throwable(e));
		}
		return json;
	}
	/**
	 * 根据经纬度获取地理位置
	 * @param lon 经度
	 * @param lat 纬度
	 * @return
	 */
	public HashMap<String, Object> getLocationInfoByLonLat(String lon,String lat){
		HashMap<String, Object> json = new LinkedHashMap<String, Object>();
		json.put("state", true);
		json.put("error", "");
		json.put("data", "");
		try {
			JSONObject info = BaiDuUtil.getLocationInfo(lat, lon);
			log.info("info="+info);
			if(info.containsKey("status")){
				int status = info.getIntValue("status");
				HashMap<String, Object> map = new LinkedHashMap<String, Object>();
				if(status==0){
					JSONObject result = info.getJSONObject("result");
					JSONObject addressComponent = result.getJSONObject("addressComponent");
					map.put("country", addressComponent.getString("country"));
					map.put("province", addressComponent.getString("province"));
					map.put("city", addressComponent.getString("city"));
					map.put("district", addressComponent.getString("district"));
					map.put("address", result.getString("formatted_address"));
				}
				json.put("data", map);
			}
		} catch (IOException e) {
			json.put("state", false);
			json.put("error", e.getMessage());
			log.error("根据经纬度获取地理位置异常：",new Throwable(e));
		}
		return json;
	}
	
	public HashMap<String, Object> getSuggectLocation(String keyword){
		HashMap<String, Object> json = new LinkedHashMap<String, Object>();
		json.put("state", true);
		json.put("error", "");
		try {
			JSONObject info = BaiDuUtil.placeSuggestion(keyword);
			if(info.containsKey("status")){
				int status = info.getIntValue("status");
				if(status==0){
					JSONArray result = info.getJSONArray("result");
					json.put("data", result.toJSONString());
				}else{
					json.put("state", false);
					json.put("error", info.getString("message"));
				}
			}
		} catch (IOException e) {
			json.put("state", false);
			json.put("error", e.getMessage());
			log.error("根据经纬度获取地理位置异常：",new Throwable(e));
		}
		return json;
	}
	
	public static void main(String[] args) {
//		log.debug("debug");
//		log.warn("warn");
//		log.error("error");
//		String filepath = "c:/login-bg2.jpg";
//		String base64str = Base64Util.GetImageStr(filepath);
//		System.out.println(base64str);
//		AjaxService service = new AjaxService();
//		String newimgBase64str = service.getUserPhoto(base64str);
//		System.out.println(newimgBase64str);
		//testSuggest();
		//testGetLocationByLonLat();
//		JSONObject json = new JSONObject();
//		json.put("id", "111");
//		json.put("name", "2222");
//		json.put("age", "333");
		AjaxService service = new AjaxService();
		String username = service.getNamePinYin("雷志强");
		System.out.println(username);
		System.out.println("OK");
	}
	
	public static void testGetLocationByLonLat(){
		String lon = "119.23";
		String lat = "37.56";
		AjaxService service = new AjaxService();
		HashMap<String, Object> info = service.getLocationInfoByLonLat(lon, lat);
		System.out.println(info);
	}
	
	public static void testSuggest(){
		String keyword = "武汉";
		AjaxService service = new AjaxService();
		HashMap<String, Object> info = service.getSuggectLocation(keyword);
		System.out.println(info);
	}
}

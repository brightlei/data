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
import java.net.MalformedURLException;
import java.net.URL;

import com.alibaba.fastjson.JSONObject;

/**
 * 模块名称：该模块名称								<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：2015-10-7 下午04:35:32					<br>
 * 初始版本：V1.0										<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：2015-10-7 下午04:35:32					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class WeatherUtil {
	
	public static JSONObject getWeatherInfo(String cityId) throws MalformedURLException,IOException{
		JSONObject jsonResult = new JSONObject();
		String serviceUrl = "http://api.k780.com:88/?app=weather.today&weaid="+cityId+"&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
		URL url = new URL(serviceUrl);
	    InputStream is = url.openStream();
	    BufferedReader reader=new BufferedReader(new InputStreamReader(is,"UTF-8"));
		String tmp=null;
		String htmlRet=""; 
		while((tmp=reader.readLine())!=null){
			htmlRet+=tmp+"\r\n";
		}
		String result = htmlRet;
		jsonResult = JSONObject.parseObject(result);
	    is.close();
	    is = null;
		return jsonResult;
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(getWeatherInfo("").get("result"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

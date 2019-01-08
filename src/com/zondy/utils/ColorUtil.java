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

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


/**
 * 模块名称：ColorUtil									<br>
 * 功能描述：颜色工具类						<br>
 * 文档作者：雷志强									<br>
 * 创建时间：2014-4-18 上午10:01:26					<br>
 * 初始版本：V1.0	最初版本号							<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：2014-4-18 上午10:01:26					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class ColorUtil {
	/**
	 * 功能描述：生成十六进制随机颜色值<br>
	 * 创建作者：雷志强<br>
	 * 创建时间：2014-4-18 上午10:09:24<br>
	 * @return
	 * @return String
	 */
	public static String getRamdonColor(){
		String hexColor = "000000";
		String red = "";//红色
		String green = "";//绿色
		String blue = "";//蓝色
		//生成随机对象
		Random random = new Random();
		//随机生成红色颜色代码
		red = Integer.toHexString(random.nextInt(256)).toUpperCase();
		//随机生成绿色颜色代码
		green = Integer.toHexString(random.nextInt(256)).toUpperCase();
		//随机生成蓝色颜色代码
		blue = Integer.toHexString(random.nextInt(256)).toUpperCase();
		red = red.length()==1 ? "0" + red : red;
		green = green.length()==1 ? "0" + green : green;
		blue  = blue.length()==1 ? "0" + blue : blue;
		//生成十六进制颜色值
		hexColor = "#"+red+green+blue;
		return hexColor;
	}
	
	public static String rgbToHex(String rgb){
		String hexColor = "#000000";
		int r = 0;
		int g = 0;
		int b = 0;
		String red = "";//红色
		String green = "";//绿色
		String blue = "";//蓝色
		if(rgb!=null&&rgb.length()>0){
			String[] tmpArr = rgb.split(",");
			if(tmpArr.length==3){
				r = Integer.parseInt(tmpArr[0]);
				g = Integer.parseInt(tmpArr[1]);
				b = Integer.parseInt(tmpArr[2]);
				red = Integer.toHexString(r).toUpperCase();
				green = Integer.toHexString(g).toUpperCase();
				blue = Integer.toHexString(b).toUpperCase();
				hexColor = "#"+red+green+blue;
			}else{
				System.out.println("输入的RGB值["+rgb+"]不正确，请输入正确的RGB值,例如：255,244,244");
			}
		}else{
			throw new NullPointerException("[rgbToHex] - 参数rgb值为空");
		}
		return hexColor;
	}
	
	/**
	 * 功能描述：16进制颜色值转成rgb值<br>
	 * 创建作者：雷志强<br>
	 * 创建时间：2014-4-18 上午10:35:59<br>
	 * @param hex
	 * @return
	 * @return Color
	 */
	public static Color hexToRgb(String hex){
		Color color = Color.black;
		if(hex.length()==6){
			String red = hex.substring(0,2);
			String green = hex.substring(2,4);
			String blue = hex.substring(4,6);
			int r = Integer.parseInt(red,16);;
			int g = Integer.parseInt(green,16);;
			int b = Integer.parseInt(blue,16);;
			color = new Color(r,g,b);
		}
		return color;
	}
	
	
	
	//将rgb字符串转换成Color对象
	public static Color getColorFromString(String rgbStr){
		Color color = new Color(255,255,255);
		if(rgbStr!=null&&!rgbStr.equals("")){
			String[] rgbArr = rgbStr.split(",");
			if(rgbArr.length==3){
				int r = Integer.parseInt(rgbArr[0]);
				int g = Integer.parseInt(rgbArr[1]);
				int b = Integer.parseInt(rgbArr[2]);
				color = new Color(r,g,b);
			}
		}
		return color;
	}
	
	@SuppressWarnings("unchecked")
	public static Color getColorByLevelValue(List<?> colorLevels,double value){
		Color color = null;
		int length = colorLevels.size();
		String rgb = null;
		double min = 0.0;
		double max = 0.0;
		HashMap<String, Object> hsMap = null;
		for(int i=0;i<length;i++){
			hsMap = (HashMap<String, Object>)colorLevels.get(i);
			min = (Double)hsMap.get("min");
			max = (Double)hsMap.get("max");
			rgb = (String)hsMap.get("rgb");
			if(min<=value&&value<max){
				color = getColorFromString(rgb);
				break;
			}
		}
		//System.out.println("color="+color);
		//如果值在色标范围之外的取最近的颜色值
		if(color == null){
			double colorMin = 0.0;
			double colorMax = 0.0;
			HashMap<String, Object> minHsMap = (HashMap<String, Object>)colorLevels.get(0);
			colorMin = (Double)minHsMap.get("min");
			HashMap<String, Object> maxHsMap = (HashMap<String, Object>)colorLevels.get(length-1);
			colorMax = (Double)hsMap.get("max");
			if(value<=colorMin){
				color = getColorFromString((String)minHsMap.get("rgb"));
			}else if(value>=colorMax){
				color = getColorFromString((String)maxHsMap.get("rgb"));
			}
		}
		return color;
	}
	
	public static void main(String[] args) {
		//System.out.println(getRamdonColor());
		//System.out.println(hexToRgb("B1C7DE"));
		//System.out.println(rgbToHex("252,123,123"));
		//String xmlPath = "D:/Tomcat6.0/webapps/hbcf/resources/xml/colorConfig/YBZQL_XML_COLOR.xml";
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("OK");
	}
}

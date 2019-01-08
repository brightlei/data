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

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * 模块名称：SystemUtil								<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：2013-9-17 上午10:18:05					<br>
 * 初始版本：V1.0	最初版本号							<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：2013-9-17 上午10:18:05					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class SystemUtil {
	public final static String[] AGENT = { "Android", "iPhone", "iPod", "iPad", "Windows Phone", "MQQBrowser" };
	
	/**
	 * 功能描述：判断请求来源是PC端还是移动端
	 * 创建作者：雷志强
	 * 创建时间：2017年10月30日 下午2:00:47
	 * @param ua
	 * @return boolean
	 */
	public static boolean checkAgentIsMobile(String ua) {
		String[] agent = AGENT;
		boolean flag = false;
		if (!ua.contains("Windows NT") || (ua.contains("Windows NT") && ua.contains("compatible; MSIE 9.0;"))) {
			// 排除 苹果桌面系统
			if (!ua.contains("Windows NT") && !ua.contains("Macintosh")) {
				for (String item : agent) {
					if (ua.contains(item)) {
						flag = true;
						break;
					}
				}
			}
		}
		return flag;
	}
	/**
	 * 功能描述：获取客户端真实IP地址<br>
	 * 创建作者：雷志强<br>
	 * 创建时间：2013-8-19 下午02:09:34<br>
	 * 
	 * @return
	 * @return String 客户IP地址
	 */
	public static String getClientIpAddr(HttpServletRequest request) {
		String clientIp = "";
		clientIp = request.getHeader("x-forwared-for");
		if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
			clientIp = request.getHeader("Proxy-Client-IP");
		}
		if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
			clientIp = request.getHeader("WL-Proxy-Client-IP");
		}
		if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
			clientIp = request.getRemoteAddr();
		}
		return clientIp;
	}
	
	/**
	 * 功能描述：获取服务器当前时间<br>
	 * 创建作者：雷志强<br>
	 * 创建时间：2013-11-1 下午05:31:17<br>
	 * @param format
	 * @return
	 * @return String
	 */
	public static String getServerTime(String format){
		String datetime = DateUtil.date2String(format);
		return datetime;
	}
	
	public static List<HashMap<String, String>> getDisk(){
		List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		String dirName = "";
		HashMap<String, String> map = null;
		int indexId = 0;
		for(char c='A';c<='Z';c++){
			dirName = c+":/";
			File win = new File(dirName);
			if(win.exists()){
				map = new HashMap<String, String>();
				/*
				long total = win.getTotalSpace();
				long used = win.getUsableSpace();
				long free = win.getFreeSpace();
				*/
				long total = 1000;
				long used = 800;
				long free = 200;
				System.out.println(dirName+",总大小："+FileUtil.FormatFileSize(total)+",可用空间："+FileUtil.FormatFileSize(used));
				map.put("id", String.valueOf(indexId));
				map.put("name", String.valueOf(c));
				map.put("path", dirName);
				map.put("used", FileUtil.FormatFileSize(used));
				map.put("free", FileUtil.FormatFileSize(free));
				map.put("total", FileUtil.FormatFileSize(total));
				list.add(map);
				indexId++;
			}
		}
		return list;
	}
	public List<HashMap<String, String>> getDirForlds(String forldPath){
		List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		DecimalFormat df = new DecimalFormat("#0.0");
		File forldFile = new File(forldPath);
		File[] files = forldFile.listFiles();
		File file = null;
		int fileCount = files.length; 
		for(int i=0;i<fileCount;i++){
			file = files[i];
			if(file.isDirectory()){
				System.out.println(file.getName()+",总大小："+FileUtil.FormatFileSize(FileUtil.getForldSize(file)));
			}
		}
		return list;
	}
	public static void main(String[] args) {
		SystemUtil util = new SystemUtil();
		List list = util.getDisk();
		System.out.println(list);
		//long size = FileUtil.getForldSize(new File("c:/tmp"));
		//System.out.println(FileUtil.FormatFileSize(size));
	}
}

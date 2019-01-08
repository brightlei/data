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

package com.zondy.config;

import java.util.UUID;

/**
 * 模块名称：ParamConfig									<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：Aug 29, 2016 2:41:56 PM					<br>
 * 初始版本：V1.0	最初版本号							<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：Aug 29, 2016 2:41:56 PM					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class ParamConfig {
	
	public enum ColumnMethod{
		getUid,
		rolecode
	}
	
	public static String getColumnValue(String methodStr,String paramStr){
		String columnValue = "";
		ColumnMethod method = null;
		try {
			method = ColumnMethod.valueOf(methodStr);
			switch (method) {
				case getUid:
					columnValue = UUID.randomUUID().toString();
					columnValue = columnValue.replaceAll("-","");
				break;
				case rolecode:
					long t = System.currentTimeMillis();
					columnValue = "ROLE-"+t;
				break;
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return columnValue;
	}
	
	/**
	 * 功能描述：请用一句话描述这个方法实现的功能<br>
	 * 创建作者：雷志强<br>
	 * 创建时间：Aug 29, 2016 2:41:56 PM<br>
	 * @param args
	 * @return void
	 */
	public static void main(String[] args) {
		String uid = ParamConfig.getColumnValue("getUid","");
		System.out.println(uid+","+uid.length());
		String rolecode = ParamConfig.getColumnValue("rolecode", "");
		System.out.println(rolecode);
		System.out.println("OK");
	}
}

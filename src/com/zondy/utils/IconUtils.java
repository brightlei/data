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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.zondy.listener.ApplicationListener;

/**
 * 模块名称：IconUtils									<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：2014-11-26 下午01:58:14					<br>
 * 初始版本：V1.0	最初版本号							<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：2014-11-26 下午01:58:14					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class IconUtils {

	/**
	 * 功能描述：请用一句话描述这个方法实现的功能<br>
	 * 创建作者：雷志强<br>
	 * 创建时间：2014-11-26 下午01:58:14<br>
	 * @param args
	 * @return void
	 */
	public static void main(String[] args) {
		IconUtils utils = new IconUtils();
		String string = utils.loadIconList("berlin");
		System.out.println(string);
	}
	FileFilter filefilter = new FileFilter() {
        public boolean accept(File file) {
            if (file.getName().endsWith(".png")) {
                return true;
            }
            return false;
        }
    };

	public String loadIconList(String iconType){
		StringBuffer sb = new StringBuffer();
		String absolutePath = "jslib/themes/icons/icon-" + iconType + "/16x16";
		String iconForldPath = ApplicationListener.rootPath + absolutePath;
		File iconForldFile = new File(iconForldPath);
		if(iconForldFile.exists()){
			File[] files = iconForldFile.listFiles(filefilter);
			int count = files.length;
			File f = null;
			String iconCls = "";
			String fileName = "";
			for(int i=0;i<count;i++){
				f = files[i];
				fileName = f.getName().replaceAll(".png", "");
                iconCls = "icon-"+iconType+"-"+fileName;
                sb.append("<img iconCls='" + iconCls + "' style='border:1px solid #FFFFFF;' src='" + absolutePath + "/" + f.getName() + "' title='" + f.getName() + "'/>");
			}
		}
		return sb.toString();
	}
	
	public List<?> loadIcons(String iconType){
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		String absolutePath = "jslib/themes/icons/icon-" + iconType + "/16x16";
		String iconForldPath = ApplicationListener.rootPath + absolutePath;
		File iconForldFile = new File(iconForldPath);
		if(iconForldFile.exists()){
			File[] files = iconForldFile.listFiles(filefilter);
			int count = files.length;
			File f = null;
			String iconCls = "";
			String fileName = "";
			HashMap<String, Object> record = null;
			for(int i=0;i<count;i++){
				f = files[i];
				fileName = f.getName().replaceAll(".png", "");
                iconCls = "icon-"+iconType+"-"+fileName;
                record = new LinkedHashMap<String, Object>();
                record.put("fileName", fileName);
                record.put("iconCls", iconCls);
                record.put("filePath", f.getAbsolutePath().replaceAll("\\\\", "/"));
			}
		}
		return list;
	}
}

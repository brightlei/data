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
package data;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jcifs.dcerpc.msrpc.netdfs;
import net.glxn.qrgen.javase.QRCode;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jni.FileInfo;
import org.junit.Test;

import sun.net.TelnetInputStream;
import sun.net.ftp.FtpClient;

import com.alibaba.fastjson.JSONObject;
import com.zondy.base.DAO.BaseDAOImpl;
import com.zondy.config.ParamConfig;
import com.zondy.config.QuerySqlConfig;
import com.zondy.config.WebConfig;
import com.zondy.listener.ApplicationListener;
import com.zondy.web.json.LayerAction;

/**
 * 模块名称：该模块名称
 * 功能描述：该文件详细功能描述
 * 文档作者：雷志强
 * 创建时间：2017年5月21日 上午10:44:17
 * 初始版本：V1.0
 * 修改记录：
 * *************************************************
 * 修改人：雷志强
 * 修改时间：2017年5月21日 上午10:44:17
 * 修改内容：
 * *************************************************
 */
public class UnitTest {
	
	@Test
	public void testLayerAction_addLayer(){
		LayerAction action = new LayerAction();
		action.requestParam.put("method", "addLayer");
		action.requestParam.put("name", "DDDDD");
		action.requestParam.put("pcode", "3");
		action.addLayer();
		System.out.println(action.dataMap);
		System.out.println("OK");
	}
	
	@Test
	public void savetxt() throws IOException{
		String filepath = "d:/1123.txt";
		File file = new File(filepath);
		FileUtils.writeStringToFile(file, "aaaaaaaaaaaaaaa", "UTF-8");
	}
	
	@Test
	public void testAAA(){
		Pattern p = Pattern.compile("\\d+.+\\d{2,}");//这个2是指连续数字的最少个数
        String u = "区间 汉丹线 ：下行 93.000 至 96.000";
        u = "站内 汉丹线 ：站内平林车站 124.200 至 126.100 股道编号：5";
        u = "站内 汉丹线 ：站内平林车站 124.200 至 126.100 股道编号：5";
        Matcher m = p.matcher(u);
        int i = 0;
        while (m.find()) {
            System.out.println(m.group());
            i++;
        }
        System.out.println(i);
        
//		//正则表达式中“\d”表示[0-9]的数字，“\d+”表示由[0-9]的数字组成的数字，“\w”表示[A-Z0-9]，“\w+”表示由数字、26个英文字母或者下划线组成的字符串，“\d+.+\d+”表示小数
//		String  str="SUN站内 汉丹线Line ：站内平林车站 124.200至126.100 股道编号：5";  
//        String s = "\\d+.\\d{2,}+|\\w+|\\d{2,}";
//        Pattern p=Pattern.compile("^.*((?<!//d)//d+).*$"); 
//        Matcher ma=p.matcher(str);
//        //Pattern  pattern=Pattern.compile(s);  
//        //Matcher  ma=pattern.matcher(str);
//   
//        while(ma.find()){  
//            System.out.println(ma.group());  
//        }  
	}
	
	@Test
	public void test() throws UnsupportedEncodingException{
		String string = "%C3%C0%C3%B7%B9%FA%BC%CA%BE%C6%B5%EA";
		String ss = URLEncoder.encode("雷志强", "UTF-8");
		System.out.println(ss);
		ss = URLDecoder.decode(ss, "UTF-8");
		System.out.println(ss);
		ss = URLDecoder.decode(string, "GBK");
		System.out.println(ss);
		JSONObject json = new JSONObject();
		json.put("id", 1);
		json.put("name","asdsadsadsad");
		json.put("age",22);
	}
	
	@Test
	public void testAlterColumn(){
		String sql = "ALTER TABLE [t_test] ADD dddd varchar(500);";
		BaseDAOImpl daoImpl = (BaseDAOImpl)ApplicationListener.ctx.getBean("dao");
		int ret = 0;
		//int ret = daoImpl.updateObject(sql);
		//System.out.println(ret);
		//sql = "sp_addextendedproperty N'MS_Description', N'测试添加字段', 'USER', N'dbo', 'TABLE', N't_test', 'COLUMN', N'dddd'";
		//ret = daoImpl.updateObject(sql);
		//System.out.println(ret);
		sql = "EXEC sp_updateextendedproperty 'MS_Description','AAAAAAAA','user',dbo,'table','t_test','column',dddd";
		ret = daoImpl.updateObject(sql);
		System.out.println(ret);
		//sp_addextendedproperty N'MS_Description', N'卡号', 'USER', N'dbo', 'TABLE', N'ClipInfo', 'COLUMN', N'ClipNum'"
	}
	
	@Test
	public void testDao(){
		BaseDAOImpl daoImpl = (BaseDAOImpl)ApplicationListener.ctx.getBean("dao");
		System.out.println(daoImpl);
		String sql = "";
		int count = 10;
		for(int i=0;i<count;i++){
			sql = "insert into t_dic(id,code,name) values('"+ParamConfig.getColumnValue("getUid","")+"','000"+i+"','字典"+i+"')";
			int ret = daoImpl.saveObject(sql);
			System.out.println("ret="+ret);
		}
	}
	
	@Test
	public void testAddDept(){
		BaseDAOImpl daoImpl = (BaseDAOImpl)ApplicationListener.ctx.getBean("dao");
		System.out.println(daoImpl);
		String sql = "insert into t_layer(id,code,name,pcode) values('"+ParamConfig.getColumnValue("getUid","")+"','00','业务图层','0')";
		int ret = daoImpl.saveObject(sql);
	}
	
	@Test
	public void testSql(){
		QuerySqlConfig config = new QuerySqlConfig();
		JSONObject paramJson = new JSONObject();
		paramJson.put("", "");
		String baseSql = config.getConfigSQL("");
		String sql = "";
	}
	
	@Test
	public void testGetTableInfo(){
		String tableName = "t_rengongxx";
		QuerySqlConfig config = new QuerySqlConfig();
		String baseSql = config.getConfigSQL("getTableColumns");
		String sql = baseSql.replaceAll("#table#", tableName);
		BaseDAOImpl daoImpl = (BaseDAOImpl)ApplicationListener.ctx.getBean("dao");
		List<JSONObject> list = daoImpl.listAll(sql);
		System.out.println(list);
		JSONObject json = null;
		List<String> columnList = new ArrayList<String>();
		for(int i=0;i<list.size();i++){
			json = list.get(i);
			columnList.add(json.get("column_name").toString());
		}
		String columns = StringUtils.join(columnList, ",");
		System.out.println(columns);
		String columnString = WebConfig.getConfig(tableName);
		System.out.println(columnString);
	}
}

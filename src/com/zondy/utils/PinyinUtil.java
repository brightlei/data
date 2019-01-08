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
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
/**
 * 模块名称：该模块名称								<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：2015-10-7 下午02:52:35					<br>
 * 初始版本：V1.0										<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：2015-10-7 下午02:52:35					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class PinyinUtil {
	//将汉字转换为全拼
	public static String getPingYin(String src) {
		char[] t1 = null;
		t1 = src.toCharArray();
		String[] t2 = new String[t1.length];
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		for (int i = 0; i < t0; i++) {
			// 判断是否为汉字字符
			if (java.lang.Character.toString(t1[i]).matches(
					"[\\u4E00-\\u9FA5]+")) {
				t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
				t4 += t2[0];
			} else
				t4 += java.lang.Character.toString(t1[i]);
		}
		return t4;
	}

	//返回中文的首字母
	public static String getPinYinHeadChar(String str) {
		String convert = "";
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		for (int j = 0; j < str.length(); j++) {
			char word = str.charAt(j);
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word, format);
			if (pinyinArray != null) {
				convert += pinyinArray[0].charAt(0);
			} else {
				convert += word;
			}
		}
		return convert;
	}

	// 将字符串转移为ASCII码
	public static String getCnASCII(String cnStr) {
		StringBuffer strBuf = new StringBuffer();
		byte[] bGBK = cnStr.getBytes();
		for (int i = 0; i < bGBK.length; i++) {
			strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
		}
		return strBuf.toString();
	}

	public static void main(String[] args) {
		//获取中文字符首字母
		String ss = getPinYinHeadChar("雷志强");
		System.out.println(ss);
		//获取中文字符拼音
		ss = getPingYin("雷志强");
		System.out.println(ss);
	}
}

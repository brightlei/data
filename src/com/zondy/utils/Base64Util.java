package com.zondy.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.codec.binary.Base64;

public class Base64Util {
	public static void main(String[] args) throws IOException {
		String filepath = "c:/login-bg2.jpg";
		filepath = "D:/apache-tomcat-7.0.67/webapps/fm/cache/userphoto/userphoto-18040596086.png";
		filepath = "d:/FreeMeso风能与气象公共服务平台二期建设方案V0.9.docx";
		filepath = "d:/aaa_100X100.png";
		String strImg = GetImageStr(filepath);
		System.out.println(strImg);
//		int width = 320;
//		int height = 320;
//		String newfilepath = "c:/login-bg2_320X320.jpg";
//		ImageUtils.thumbnailsBySize(filepath, width, height, newfilepath);
		//GenerateImage(strImg,"c:/login-bg2_new.jpg");
		// FileUtils.writeStringToFile(new File("E:/base64.txt"), strImg,
		// "UTF-8");
		// GenerateImage(strImg);
		System.out.println("OK");
	}

	// 图片转化成base64字符串
	public static String GetImageStr(String filepath) {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		//String imgFile = "E:/雷志强/Penguins.jpg";// 待处理的图片
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(filepath);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 对字节数组Base64编码
		return new String(Base64.encodeBase64(data));// 返回Base64编码过的字节数组字符串
		//BASE64Encoder encoder = new BASE64Encoder();
		//return encoder.encode(data);
	}

	// base64字符串转化成图片
	public static boolean GenerateImage(String imgStr,String filepath) throws IOException {
		// 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null) // 图像数据为空
			return false;
		try {
			int index = imgStr.indexOf("base64");
			if(index!=-1){
				imgStr = imgStr.substring(index+6);
			}
			// Base64解码
			byte[] b = Base64.decodeBase64(imgStr.getBytes());
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			// 生成jpeg图片
			//String imgFilePath = "E:/雷志强/222.jpg";// 新生成的图片
			OutputStream out = new FileOutputStream(filepath);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}

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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

/**
 * 模块名称：ImageUtils									<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：Oct 12, 2016 4:48:08 PM					<br>
 * 初始版本：V1.0	最初版本号							<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：Oct 12, 2016 4:48:08 PM					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class ImageUtils {
	
	public static String thumbnailsBySize(String filepath,int width,int height,String newfilepath) throws IOException{
		BufferedImage img = ImageIO.read(new File(filepath));
		double sizer = width*1.0/height;
		int w = img.getWidth();
		int h = img.getHeight();
		double imgr = w*1.0/h;
		int neww = w;
		int newh = h;
		DecimalFormat df = new DecimalFormat("#");
		if(imgr>sizer){
			double dneww = newh*sizer;
			neww = Integer.parseInt(df.format(dneww));
		}else{
			double dnewh = neww/sizer;
			newh = Integer.parseInt(df.format(dnewh));
		}
		FileUtils.forceMkdir(new File(newfilepath).getParentFile());
		int index = newfilepath.lastIndexOf(".");
		String filetype = newfilepath.substring(index+1);
		Thumbnails.of(filepath).outputFormat(filetype).sourceRegion(Positions.CENTER, neww,newh).size(width, height).toFile(newfilepath);
		return newfilepath;
	}
	/**
	 * 功能描述：请用一句话描述这个方法实现的功能<br>
	 * 创建作者：雷志强<br>
	 * 创建时间：Oct 12, 2016 4:48:08 PM<br>
	 * @param args
	 * @return void
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//String imgpath = "D:/dataupload/123.gif";
		//String newimgpath = "D:/dataupload/123_48X48.gif";
		//thumbnailsBySize(imgpath,48,48,newimgpath);
		System.out.println("OK");
	}
	
	public static void test11(){
		
	}
	
	public static void test() throws IOException{
		/***************1、指定大小进行缩放*********************/
		//size(宽度, 高度)
		/*  
		 * 若图片横比200小，高比300小，不变  
		 * 若图片横比200小，高比300大，高缩小到300，图片比例不变  
		 * 若图片横比200大，高比300小，横缩小到200，图片比例不变  
		 * 若图片横比200大，高比300大，图片按比例缩小，横为200或高为300  
		 */ 
		Thumbnails.of("images/a380_1280x1024.jpg") 
		        .size(200, 300)
		        .toFile("c:/a380_200x300.jpg");

		Thumbnails.of("images/a380_1280x1024.jpg") 
		        .size(2560, 2048) 
		        .toFile("c:/a380_2560x2048.jpg");
		/*****************2、按照比例进行缩放****************************/
		//scale(比例)
		Thumbnails.of("images/a380_1280x1024.jpg") 
		        .scale(0.25f)
		        .toFile("c:/a380_25%.jpg");

		Thumbnails.of("images/a380_1280x1024.jpg") 
		        .scale(1.10f)
		        .toFile("c:/a380_110%.jpg");
		/*****************3、不按照比例，指定大小进行缩放****************************/
		//keepAspectRatio(false) 默认是按照比例缩放的
		Thumbnails.of("images/a380_1280x1024.jpg") 
		        .size(200, 200) 
		        .keepAspectRatio(false) 
		        .toFile("c:/a380_200x200.jpg");
		
		/*****************4、旋转****************************/
		//rotate(角度),正数：顺时针 负数：逆时针
		Thumbnails.of("images/a380_1280x1024.jpg") 
				.size(1280, 1024)
		        .rotate(90) 
		        .toFile("c:/a380_rotate+90.jpg"); 

		Thumbnails.of("images/a380_1280x1024.jpg") 
				.size(1280, 1024)
		        .rotate(-90) 
		        .toFile("c:/a380_rotate-90.jpg"); 
		/*****************5、水印****************************/
		//watermark(位置，水印图，透明度)
		Thumbnails.of("images/a380_1280x1024.jpg") 
				.size(1280, 1024)
		        .watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File("images/watermark.png")), 0.5f) 
		        .outputQuality(0.8f) 
		        .toFile("c:/a380_watermark_bottom_right.jpg");

		Thumbnails.of("images/a380_1280x1024.jpg") 
				.size(1280, 1024)
		        .watermark(Positions.CENTER, ImageIO.read(new File("images/watermark.png")), 0.5f) 
		        .outputQuality(0.8f) 
		        .toFile("c:/a380_watermark_center.jpg");
		/*****************6、裁剪****************************/
		//sourceRegion()

		//图片中心400*400的区域
		Thumbnails.of("images/a380_1280x1024.jpg")
				.sourceRegion(Positions.CENTER, 400,400)
				.size(200, 200)
		        .keepAspectRatio(false) 
		        .toFile("c:/a380_region_center.jpg");

		//图片右下400*400的区域
		Thumbnails.of("images/a380_1280x1024.jpg")
				.sourceRegion(Positions.BOTTOM_RIGHT, 400,400)
				.size(200, 200)
		        .keepAspectRatio(false) 
		        .toFile("c:/a380_region_bootom_right.jpg");

		//指定坐标
		Thumbnails.of("images/a380_1280x1024.jpg")
				.sourceRegion(600, 500, 400, 400)
				.size(200, 200)
		        .keepAspectRatio(false) 
		        .toFile("c:/a380_region_coord.jpg");
		/*****************7、转化图像格式****************************/
		//outputFormat(图像格式)
		Thumbnails.of("images/a380_1280x1024.jpg") 
				.size(1280, 1024)
		        .outputFormat("png") 
		        .toFile("c:/a380_1280x1024.png"); 

		Thumbnails.of("images/a380_1280x1024.jpg") 
				.size(1280, 1024)
		        .outputFormat("gif") 
		        .toFile("c:/a380_1280x1024.gif"); 
		/*****************8、输出到OutputStream****************************/
		//toOutputStream(流对象)
		OutputStream os = new FileOutputStream("c:/a380_1280x1024_OutputStream.png");
		Thumbnails.of("images/a380_1280x1024.jpg") 
				.size(1280, 1024)
		        .toOutputStream(os);
		/*****************9、输出到BufferedImage****************************/
		//asBufferedImage() 返回BufferedImage
		BufferedImage thumbnail = Thumbnails.of("images/a380_1280x1024.jpg") 
				.size(1280, 1024)
				.asBufferedImage();
		ImageIO.write(thumbnail, "jpg", new File("c:/a380_1280x1024_BufferedImage.jpg")); 

		/*****************7、转化图像格式****************************/
		
	}
	
}

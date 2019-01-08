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

import org.apache.log4j.Logger;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * 模块名称：Word2PDF <br>
 * 功能描述：该文件详细功能描述 <br>
 * 文档作者：雷志强 <br>
 * 创建时间：2013-9-29 上午10:43:21 <br>
 * 初始版本：V1.0 最初版本号 <br>
 * 修改记录： <br>
 * *************************************************<br>
 * 修改人：雷志强 <br>
 * 修改时间：2013-9-29 上午10:43:21 <br>
 * 修改内容： <br>
 * *************************************************<br>
 */
public class Word2PDF {
	static final int wdDoNotSaveChanges = 0;// 不保存待定的更改
	static final int wdFormatPDF = 17;// PDF 格式

	private static Logger log = Logger.getLogger(Word2PDF.class);
	
	/**
	 * 
	* @功能描述: 将word文件转化为PDF文件
	* @创建作者: wangms 
	* @创建日期: Mar 1, 2016 2:23:33 PM 
	*
	* @param wordFilePath  word文件路径
	* @param pdfFilePath PDF文件路径
	* @throws Exception    
	* @return String
	 */
	public static boolean convertWordToPDF(String wordFilePath,
			String pdfFilePath) throws Exception {
		boolean isSuccess=false;
		log.info("启动word...");
		long start = System.currentTimeMillis();
		ActiveXComponent app = null;
		Dispatch doc = null;
		try {
			app = new ActiveXComponent("Word.Application");
			app.setProperty("Visible", false);
			Dispatch docs = app.getProperty("Documents").toDispatch();
			log.info("打开文档..." + wordFilePath);
			doc = Dispatch.invoke(
					docs,
					"Open",
					Dispatch.Method,
					new Object[] { wordFilePath, new Variant(false),
							new Variant(true),// 是否只读
							new Variant(false), new Variant("pwd") },
					new int[1]).toDispatch();
			log.info("转换文档到PDF..." + pdfFilePath);
			File toFile = new File(pdfFilePath);
			if (toFile.exists()) {
				toFile.delete();
			}
			Dispatch.call(doc, "SaveAs", pdfFilePath, wdFormatPDF);
			Dispatch.call(doc, "Close", false);
			long end = System.currentTimeMillis();
			log.info("转换完成...用时：" + (end - start) + "ms.");
			isSuccess=true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("=======Error:文档转换失败：" + e.getMessage());
		} finally {
			if (app != null) {
				app.invoke("Quit", wdDoNotSaveChanges);
			}
		}
		return isSuccess;
	}

	/**
	 * 
	 * @功能描述: 将Excel文件转化为swf文件
	 * @创建作者: wangms
	 * @创建日期: Dec 18, 2014 9:05:32 PM
	 * 
	 * @param excelFilePath
	 * @param pdfFilePath
	 * @throws Exception
	 * @return String
	 * @throws
	 */
	public static String convertExcelToPDF(String excelFilePath,
			String pdfFilePath) throws Exception {
		log.info("启动Excel...");
		ActiveXComponent app = null;
		Dispatch workbooks = null;
		Dispatch workbook = null;
		try {
			app = new ActiveXComponent("Excel.Application");
			app.setProperty("Visible", false);
			workbooks = app.getProperty("Workbooks").toDispatch();
			workbook = Dispatch.invoke(
					workbooks,
					"Open",
					Dispatch.Method,
					new Object[] { excelFilePath, new Variant(false),
							new Variant(false) }, new int[3]).toDispatch();
			Dispatch.invoke(workbook, "SaveAs", Dispatch.Method, new Object[] {
					pdfFilePath, new Variant(57), new Variant(false),
					new Variant(57), new Variant(57), new Variant(false),
					new Variant(true), new Variant(57), new Variant(true),
					new Variant(true), new Variant(true) }, new int[1]);
			Variant f = new Variant(false);
			System.out.println("to pdf " + pdfFilePath);
			Dispatch.call(workbook, "Close", f);
		} catch (Exception e) {
			System.out.println("Error:Operation fail:" + e.getMessage());
		} finally {
			if (app != null) {
				app.invoke("Quit", new Variant[] {});
			}
		}
		return "";
	}

	public static void main(String[] args) {
		String wordFilePath = "D:/blank.docx";
		String pdfFilePath = wordFilePath + ".pdf";
		try {
			Word2PDF.convertWordToPDF(wordFilePath, pdfFilePath);
		} catch (Exception ex) {

		}
	}
}

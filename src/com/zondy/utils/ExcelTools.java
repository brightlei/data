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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 模块名称：ExcelTools									<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：May 4, 2017 9:25:38 PM					<br>
 * 初始版本：V1.0	最初版本号							<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：May 4, 2017 9:25:38 PM					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class ExcelTools {
	/* public static void main(String[] args) throws Exception {
	File file = new File("D:\\AA.xls");
	String[][] result = getData(file, 1);
	 */

	public static void main(String[] args) {
		readXml("D:\\AA.xls");
		System.out.println("-------------");
		//         readXml("d:/test2.xls");  
	}

	public static void readXml(String fileName) {
		boolean isE2007 = false; //判断是否是excel2007格式  
		if (fileName.endsWith("xlsx"))
			isE2007 = true;
		try {
			InputStream input = new FileInputStream(fileName); //建立输入流  
			Workbook wb = null;
			//根据文件格式(2003或者2007)来初始化  
			if (isE2007)
				wb = new XSSFWorkbook(input);
			else
				wb = new HSSFWorkbook(input);
			int sheetSize = wb.getNumberOfSheets();
			System.out.println("一共有" + sheetSize + "个SHEET页");
			Sheet sheet = wb.getSheetAt(0); //获得第一个表单  
			Iterator<Row> rows = sheet.rowIterator(); //获得第一个表单的迭代器  
			while (rows.hasNext()) {
				Row row = rows.next(); //获得行数据  
				System.out.println("第" + row.getRowNum() + "行"); //获得行号从0开始  
				Iterator<Cell> cells = row.cellIterator(); //获得第一行的迭代器  
				while (cells.hasNext()) {
					Cell cell = cells.next();
					System.out.println("第 " + cell.getColumnIndex() + "列");
					switch (cell.getCellType()) { //根据cell中的类型来输出数据  
					case HSSFCell.CELL_TYPE_NUMERIC:
						System.out.println(cell.getNumericCellValue());
						break;
					case HSSFCell.CELL_TYPE_STRING:
						System.out.println(cell.getStringCellValue());
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						System.out.println(cell.getBooleanCellValue());
						break;
					case HSSFCell.CELL_TYPE_FORMULA:
						System.out.println(cell.getCellFormula());
						break;
					default:
						System.out.println("unsuported sell type");
						break;
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}

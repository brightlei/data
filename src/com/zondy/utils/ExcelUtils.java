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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 项目名称:gisplat 类名称:ExcelUtil 类描述:Excel操作类 创建人:雷志强 创建时间:2013-8-5 下午04:32:21
 * 修改人:雷志强 修改时间:2013-8-5 下午04:32:21 修改备注:请用一句话修改的内容及功能 版本:V1.0 最初版本号
 */
public class ExcelUtils {

	private Workbook wb = null;
	private HSSFSheet sheet = null;
	private Boolean hasRegioHead = false;
	
	public ExcelUtils(Workbook wb, HSSFSheet sheet) {
		super();
		this.wb = wb;
		this.sheet = sheet;
	}

	public Workbook getWb() {
		return wb;
	}

	public void setWb(Workbook wb) {
		this.wb = wb;
	}

	public HSSFSheet getSheet() {
		return sheet;
	}

	public void setSheet(HSSFSheet sheet) {
		this.sheet = sheet;
	}

	public static Workbook getWorkbook(String excelFile) throws FileNotFoundException, IOException{
		Workbook book = null;
        try {
            book = new XSSFWorkbook(excelFile);
            System.out.println("2007");
        } catch (Exception ex) {
            book = new HSSFWorkbook(new FileInputStream(excelFile));
            System.out.println("2003");
        }
        return book;
	}

	/**
	 * 功能描述：输出Excel文件<br>
	 * 创建作者：雷志强<br>
	 * 创建时间：2013-8-28 下午01:55:11<br>
	 * 
	 * @param fileName
	 *            excel文件全路径
	 * @return void
	 */
	public void outputExcel(String fileName) {
		FileOutputStream fos = null;
		try {
			FileUtil.checkFileOnDisk(fileName, "file");
			fos = new FileOutputStream(new File(fileName));
			wb.write(fos);
			fos.close();
			fos = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 功能描述：读取Excel文件的行数据<br>
	 * 创建作者：雷志强<br>
	 * 创建时间：2013-10-25 上午10:02:08<br>
	 * 
	 * @param excelFilePath
	 * @return List<String>
	 */
	public static List<List<String>> readExcel(String excelFilePath) throws Exception {
		List<List<String>> excelRows = new ArrayList<List<String>>();
		File excelFile = new File(excelFilePath);
		if (excelFile.exists()) {
			Workbook wb = getWorkbook(excelFilePath);
			Sheet sheet = wb.getSheetAt(0);
			int totalRows = sheet.getLastRowNum()+1;
			int totalCells = 0;
			if(totalRows>0 && sheet.getRow(0)!=null){
				totalCells = sheet.getRow(0).getLastCellNum();
			}
			String cellValue = "";
			Row row = null;
			Cell cell = null;
			List<String> rowList = new ArrayList<String>();
			for(int r=0;r<totalRows;r++){
				row = sheet.getRow(r);
				if(row==null){
					continue;
				}
				rowList = new ArrayList<String>();
				for(int c=0;c<totalCells;c++){
					cell = row.getCell(c);
					if(cell==null){
						cellValue = "";
					}else{
						cellValue = getCellValue(cell);
					}
					rowList.add(cellValue);
				}
				excelRows.add(rowList);
			}
		} else {
			throw new Exception("文件不存在:" + excelFilePath);
		}
		return excelRows;
	}
	
	public static String getDateCellFormat(String dataformat){
		String format = "";
		if(dataformat.contains("yyyy")&&dataformat.contains("m")&&dataformat.contains("d")&&dataformat.contains("h")&&dataformat.contains("mm")&&dataformat.contains("ss")){
			format = "yyyy-MM-dd HH:mm:ss";
		}else if(dataformat.contains("yyyy")&&dataformat.contains("m")&&dataformat.contains("d")&&dataformat.contains("h")&&dataformat.contains("mm")){
			format = "yyyy-MM-dd HH:mm";
		}else if(dataformat.contains("yyyy")&&dataformat.contains("m")&&dataformat.contains("d")&&dataformat.contains("h")){
			format = "yyyy-MM-dd HH";
		}else if(dataformat.contains("yyyy")&&dataformat.contains("m")&&dataformat.contains("d")){
			format = "yyyy-MM-dd";
		}else if(dataformat.contains("yyyy")&&dataformat.contains("m")){
			format = "yyyy-MM";
		}else if(dataformat.contains("yyyy")){
			format = "yyyy";
		}else if(dataformat.contains("h")&&dataformat.contains("mm")&&dataformat.contains("ss")){
			format = "HH:mm:ss";
		}else if(dataformat.contains("h")&&dataformat.contains("mm")){
			format = "HH:mm";
		}
		return format;
	}
	
	//获取表格数值
	public static String getCellValue(Cell cell) {
		Object value = "";
		if (cell != null) {
			switch (cell.getCellType()) {
			case HSSFCell.CELL_TYPE_NUMERIC:// getCellType=0
				if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式  
					String dataFormat = cell.getCellStyle().getDataFormatString();
					SimpleDateFormat sdf = new SimpleDateFormat(getDateCellFormat(dataFormat));
	                Date date = cell.getDateCellValue();
	                value = sdf.format(date);
	            } else if (cell.getCellStyle().getDataFormat() == 179) {
	                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	                double dvalue = cell.getNumericCellValue(); 
	                Date date = DateUtil.getJavaDate(dvalue);
	                value = sdf.format(date);
	            } else {  
	                double dvalue = cell.getNumericCellValue();
	                DecimalFormat format = new DecimalFormat("#");  
	                value = format.format(dvalue);
	            }  
				break;
			case HSSFCell.CELL_TYPE_STRING:// getCellType=1
				value = cell.getRichStringCellValue().toString();
				break;
			case HSSFCell.CELL_TYPE_FORMULA:// getCellType=2
				FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();  
                evaluator.evaluateFormulaCell(cell);  
                CellValue cellValue = evaluator.evaluate(cell);
                value = NumberToTextConverter.toText(cell.getNumericCellValue());
                //value = String.valueOf(cellValue.getNumberValue()) ;
				break;
			case HSSFCell.CELL_TYPE_BLANK:// getCellType=3
				value = "";
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN:// getCellType=4
				boolean cellBool = cell.getBooleanCellValue();
				value = Boolean.toString(cellBool);
				break;
			case HSSFCell.CELL_TYPE_ERROR:// getCellType=5
				value = "";
				break;
			default:
				// System.out.println("unsuported sell type");
				break;
			}
		}
		return value.toString();
	}
	
	public static void printCellType(){
		System.out.println("CELL_TYPE_NUMERIC:"+Cell.CELL_TYPE_NUMERIC);
		System.out.println("CELL_TYPE_STRING:"+Cell.CELL_TYPE_STRING);
		System.out.println("CELL_TYPE_FORMULA:"+Cell.CELL_TYPE_FORMULA);
		System.out.println("CELL_TYPE_BLANK:"+Cell.CELL_TYPE_BLANK);
		System.out.println("CELL_TYPE_BOOLEAN:"+Cell.CELL_TYPE_BOOLEAN);
		System.out.println("CELL_TYPE_ERROR:"+Cell.CELL_TYPE_ERROR);
	}
	
		/**
		switch (cell.getCellType()) {
			case HSSFCell.CELL_TYPE_NUMERIC://getCellType=0
				//System.err.println("*****"+HSSFDateUtil.isCellDateFormatted(cell));
				//System.err.println("*****"+DateUtil.isCellDateFormatted(cell));
				if(DateUtil.isCellDateFormatted(cell)){
					Date theDate = cell.getDateCellValue();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					cellValueStr = sdf.format(theDate).toString();
				}else{
					cellValueStr = NumberToTextConverter.toText(cell.getNumericCellValue());
				}
				break;
			case HSSFCell.CELL_TYPE_STRING://getCellType=1
				//cellValueStr = cell.getStringCellValue();
				cellValueStr = cell.getRichStringCellValue().toString();
				break;
			case HSSFCell.CELL_TYPE_FORMULA://getCellType=2
				System.out.print("*****");
				cellValueStr = cell.getCellFormula();
				break;
			case HSSFCell.CELL_TYPE_BLANK://getCellType=3
				cellValueStr = "";
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN://getCellType=4
				boolean cellBool = cell.getBooleanCellValue();
				cellValueStr = Boolean.toString(cellBool);
				break;
			case HSSFCell.CELL_TYPE_ERROR://getCellType=5
				cellValueStr = "";
				break;
			default:
				//System.out.println("unsuported sell type");
				break;
		}
		return cellValueStr;
	}
	
	public static String formarValue(String val){
		String value = val;
		DecimalFormat df = new DecimalFormat("#");
		try {
			if(val.equals("")){
				//value = null;
			}else{
				value = df.format(Double.parseDouble(val));
			}
		} catch (Exception e) {
			//System.err.println(e.getMessage());
		}
		return value;
	}

	public Boolean getHasRegioHead() {
		return hasRegioHead;
	}

	public void setHasRegioHead(Boolean hasRegioHead) {
		this.hasRegioHead = hasRegioHead;
	}
	*/
	//读取txt一行内容转换成List数组
	public static List<String> getStringList(String str){
		List<String> list = new ArrayList<String>();
		if(str!=null&&!str.equals("")){
			String[] tmpArr = str.split(" ");
			int count = tmpArr.length;
			for(int i=0;i<count;i++){
				if(!tmpArr[i].trim().equals("")){
					list.add(tmpArr[i].trim());
				}
			}
		}
		return list;
	}
	
	public static void main(String[] args) {
		try {
			testReadExcel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("OK");
	}
	
	public static void testReadExcel() throws Exception{
		//printCellType();
		String excelFilePath = "H:/tanny/001.xlsx";
		List<List<String>> list = readExcel(excelFilePath);
		System.out.println(list);
		
	}
}

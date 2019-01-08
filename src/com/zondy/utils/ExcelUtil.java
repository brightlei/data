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
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSONObject;
import com.zondy.base.DAO.BaseDAOImpl;
import com.zondy.listener.ApplicationListener;

/**
 * 项目名称:gisplat 类名称:ExcelUtil 类描述:Excel操作类 创建人:雷志强 创建时间:2013-8-5 下午04:32:21
 * 修改人:雷志强 修改时间:2013-8-5 下午04:32:21 修改备注:请用一句话修改的内容及功能 版本:V1.0 最初版本号
 */
public class ExcelUtil {

	private Workbook wb = null;
	private HSSFSheet sheet = null;
	private Boolean hasRegioHead = false;
	
	public ExcelUtil(Workbook wb, HSSFSheet sheet) {
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
            //System.out.println("2007");
        } catch (Exception ex) {
            book = new HSSFWorkbook(new FileInputStream(excelFile));
            //System.out.println("2003");
        }
        return book;
	}
	
	public void setCellFont(CellStyle cellStyle,String fontName,int fontSize,boolean isBold){
		Font font = wb.createFont();
		font.setFontName(fontName);
		font.setFontHeightInPoints((short)fontSize);
		if(isBold){
			font.setBold(isBold);
		}
		cellStyle.setFont(font);
	}
	
	public void setCellBorder(CellStyle cellStyle){
		// 设置单元格边框
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
		cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
	}
	
	public void setCellBackground(CellStyle cellStyle){
		// 设置单元格背景色
		cellStyle.setFillForegroundColor(HSSFColor.LEMON_CHIFFON.index);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	}
	
	public void setCellCenter(CellStyle cellStyle){
		cellStyle.setAlignment(HorizontalAlignment.CENTER);//指定单元格居中对齐
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 指定单元格垂直居中对齐
		cellStyle.setWrapText(true);// 指定单元格自动换行
	}
	
	/**
	 * 功能描述：创建通用Excel头部<br>
	 * 创建作者：雷志强<br>
	 * 创建时间：2013-8-28 上午11:07:18<br>
	 * 
	 * @param headString
	 *            头部显示的字符
	 * @param colSum
	 *            数据表格的列数
	 * @return void
	 */
	public void createNormalHead(String headString, int colSum) {
		HSSFRow row = sheet.createRow(0);
		// 设置第一行
		HSSFCell cell = row.createCell(0);
		//row.setHeight((short) 400);
		row.setHeightInPoints(30);
		// 定义单元格为字符串类型
		cell.setCellType(CellType.STRING);
		cell.setCellValue(headString);

		// 指定合并区域
		CellRangeAddress cellRange = new CellRangeAddress(0, (short) 0, 0, (short) colSum - 1);
		sheet.addMergedRegion(cellRange);
		CellStyle cellStyle = wb.createCellStyle();
		//设置单元格居中显示
		setCellCenter(cellStyle);
		//设置单元格字体
		setCellFont(cellStyle, "微软雅黑", 12, true);
		//设置单元格边框
		setCellBorder(cellStyle);
		
		cell.setCellStyle(cellStyle);
	}
	
	public void createRegionText(int rowIndex,String headString, int colSum) {
		HSSFRow row = sheet.createRow(rowIndex);
		// 指定合并区域
		CellRangeAddress cellRange = new CellRangeAddress(rowIndex, (short) rowIndex, 0, (short) colSum - 1);
		sheet.addMergedRegion(cellRange);
		// 设置第一行
		HSSFCell cell = row.createCell(0);
		row.setHeight((short) 400);
		// 定义单元格为字符串类型
		cell.setCellType(CellType.STRING);
		cell.setCellValue(headString);

		CellStyle cellStyle = wb.createCellStyle();
		//设置单元格居中显示
		setCellCenter(cellStyle);
		//设置单元格字体
		setCellFont(cellStyle, "微软雅黑", 12, true);
		//设置单元格边框
		setCellBorder(cellStyle);
		int border = 1;
        RegionUtil.setBorderBottom(border,cellRange, sheet);   
        RegionUtil.setBorderLeft(border,cellRange, sheet);   
        RegionUtil.setBorderRight(border,cellRange, sheet);   
        RegionUtil.setBorderTop(border,cellRange, sheet); 
		cell.setCellStyle(cellStyle);
	}
	
	/**
	 * 功能描述：创建数据报表标题<br>
	 * 创建作者：雷志强<br>
	 * 创建时间：2013-8-28 上午11:20:55<br>
	 * 
	 * @param columnHeader
	 *            标题字符数组
	 * @return void
	 */
	public void createColumnHead(String[] columnHeader,int rowIndex) {
		// 设置列头
		HSSFRow row = sheet.createRow(rowIndex);
		row.setHeight((short)400);
		CellStyle cellStyle = wb.createCellStyle();
		//设置单元格居中显示
		setCellCenter(cellStyle);
		//设置单元格字体
		setCellFont(cellStyle, "微软雅黑", 10, true);
		//设置单元格边框
		setCellBorder(cellStyle);
		// 设置单元格背景色
		//cellStyle.setFillForegroundColor(HSSFColor.LEMON_CHIFFON.index);
		cellStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		HSSFCell cell = null;
		int colCount = columnHeader.length;
		for (int i = 0; i < columnHeader.length; i++) {
			cell = row.createCell(i);
			cell.setCellType(CellType.STRING);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(new HSSFRichTextString(columnHeader[i]));
//			sheet.setColumnWidth(i, 3600);
		}
		//在表格中添加完数据之后再设置列自适应，节省时间
		for(int i=0;i<colCount;i++){
			sheet.autoSizeColumn(i);
		}
	}
	//创建带合并单元格的表头
	public void createRegionColumnHead(String[] columnHeader) {
		int rowIndex = 1;
		// 设置列头
		HSSFRow row = sheet.createRow(rowIndex);
		HSSFRow row2 = null;
		row.setHeight((short) 400);
		CellStyle cellStyle = wb.createCellStyle();
		//设置单元格居中显示
		setCellCenter(cellStyle);
		//设置单元格字体
		setCellFont(cellStyle, "微软雅黑", 12, true);
		//设置单元格边框
		setCellBorder(cellStyle);
		// 设置单元格背景色
		 cellStyle.setFillForegroundColor(HSSFColor.LEMON_CHIFFON.index);
		cellStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	
		HSSFCell cell = row.createCell(0);
        String columnName = "";
        for(int i=0;i<columnHeader.length;i++){
        	columnName = columnHeader[i];
        	if(columnName.contains(":")){
        		row2 = sheet.createRow((short)(rowIndex+1));
        		setHasRegioHead(true);
        		break;
        	}
        }
        int cellIndex = 0;
        String[] tmpArr = null;
        String[] regionColumns = null;
        for(int i=0;i<columnHeader.length;i++){
        	columnName = columnHeader[i];
        	if(columnName.contains(":")){
        		tmpArr = columnName.split(":");
        		columnName = tmpArr[0];
        		regionColumns = tmpArr[1].split("\\&");
        		sheet.addMergedRegion(new CellRangeAddress(rowIndex,(short)cellIndex, rowIndex, (short)(cellIndex+regionColumns.length-1)));
        		cell = row.createCell(cellIndex);
        		cell.setCellType(CellType.STRING);
        		cell.setCellValue(columnName);
        		cell.setCellValue(new HSSFRichTextString(columnName));
        		cell.setCellStyle(cellStyle);
        		for(int j=0;j<regionColumns.length;j++){
        			columnName = regionColumns[j];
        			cell = row2.createCell(cellIndex);
        			cell.setCellType(CellType.STRING);
        			cell.setCellValue(new HSSFRichTextString(columnName));
            		cell.setCellStyle(cellStyle);
            		cellIndex++;
        		}
        	}else{
        		if(row2!=null){
        			sheet.addMergedRegion(new CellRangeAddress(rowIndex,(short)cellIndex, rowIndex+1, (short)cellIndex));
        		}
        		cell = row.createCell(cellIndex);
        		cell.setCellType(CellType.STRING);
        		cell.setCellValue(new HSSFRichTextString(columnName));
        		cell.setCellStyle(cellStyle);
        		cellIndex++;
        	}
        }
		
		//在表格中添加完数据之后再设置列自适应，节省时间
		for(int i=0;i<cellIndex;i++){
			sheet.autoSizeColumn(i);
		}
	}

	//创建带合并单元格的表头
	public void createRegionColumnHead(List<String> columnHeader) {
		int rowIndex = 1;
		// 设置列头
		HSSFRow row = sheet.createRow(rowIndex);
		HSSFRow row2 = null;
		row.setHeight((short) 400);
		CellStyle cellStyle = wb.createCellStyle();
		//设置单元格居中显示
		setCellCenter(cellStyle);
		//设置单元格字体
		setCellFont(cellStyle, "微软雅黑", 12, true);
		//设置单元格边框
		setCellBorder(cellStyle);
		// 设置单元格背景色
		// cellStyle.setFillForegroundColor(HSSFColor.LEMON_CHIFFON.index);
		cellStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	
		HSSFCell cell = row.createCell(0);
        String columnName = "";
        int columnCount = columnHeader.size();
        for(int i=0;i<columnCount;i++){
        	columnName = columnHeader.get(i);
        	if(columnName.contains(":")){
        		row2 = sheet.createRow((short)(rowIndex+1));
        		setHasRegioHead(true);
        		break;
        	}
        }
        int cellIndex = 0;
        String[] tmpArr = null;
        String[] regionColumns = null;
        for(int i=0;i<columnCount;i++){
        	columnName = columnHeader.get(i);
        	if(columnName.contains(":")){
        		tmpArr = columnName.split(":");
        		columnName = tmpArr[0];
        		regionColumns = tmpArr[1].split("\\&");
        		sheet.addMergedRegion(new CellRangeAddress(rowIndex,(short)cellIndex, rowIndex, (short)(cellIndex+regionColumns.length-1)));
        		cell = row.createCell(cellIndex);
        		cell.setCellType(CellType.STRING);
        		cell.setCellValue(columnName);
        		cell.setCellValue(new HSSFRichTextString(columnName));
        		cell.setCellStyle(cellStyle);
        		for(int j=0;j<regionColumns.length;j++){
        			columnName = regionColumns[j];
        			cell = row2.createCell(cellIndex);
        			cell.setCellType(CellType.STRING);
        			cell.setCellValue(new HSSFRichTextString(columnName));
            		cell.setCellStyle(cellStyle);
            		cellIndex++;
        		}
        	}else{
        		if(row2!=null){
        			sheet.addMergedRegion(new CellRangeAddress(rowIndex,(short)cellIndex, rowIndex+1, (short)cellIndex));
        		}
        		cell = row.createCell(cellIndex);
        		cell.setCellType(CellType.STRING);
        		cell.setCellValue(new HSSFRichTextString(columnName));
        		cell.setCellStyle(cellStyle);
        		cellIndex++;
        	}
        }
		
		//在表格中添加完数据之后再设置列自适应，节省时间
		for(int i=0;i<cellIndex;i++){
			sheet.autoSizeColumn(i);
		}
	}
	
	/**
	 * 功能描述：创建数据表格<br>
	 * 创建作者：雷志强<br>
	 * 创建时间：2013-8-28 下午02:41:54<br>
	 * 
	 * @param list
	 *            数据
	 * @param align
	 *            字符对齐方式
	 * @return void
	 */
	public void createDataRow(List<List<String>> list, int rowIndex, short align) {
		CellStyle cellStyle = wb.createCellStyle();
		//设置单元格居中显示
		//setCellCenter(cellStyle);
		//设置单元格字体
		setCellFont(cellStyle, "微软雅黑", 11, false);
		//设置单元格边框
		setCellBorder(cellStyle);
		// 设置单元格背景色
		cellStyle.setFillForegroundColor((short)67);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		HSSFRow row = null;
		HSSFCell cell = null;

		int rowCount = list.size();
		List<String> cellData = null;
		int colCount = 0;
		for (int i = 0; i < rowCount; i++) {
			row = sheet.createRow(rowIndex + i);
			row.setHeightInPoints(22);
			//row.setHeight((short) 400);
			cellData = list.get(i);
			colCount = cellData.size();
			for (int j = 0; j < cellData.size(); j++) {
				cell = row.createCell(j);
				cell.setCellType(CellType.STRING);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(new HSSFRichTextString(cellData.get(j)));
				// sheet.autoSizeColumn(j);//做这个操作很耗时
			}
		}
		//在表格中添加完数据之后再设置列自适应，节省时间
		for(int i=0;i<colCount;i++){
			sheet.autoSizeColumn(i);
		}
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
	 * 功能描述：Excel数据导出功能<br>
	 * 创建作者：雷志强<br>
	 * 创建时间：2013-8-28 下午03:19:33<br>
	 * 
	 * @param filePath
	 *            excel文件全路径
	 * @param sheetName
	 *            excel工作表名称
	 * @param normalHeader
	 *            excel表格大标题
	 * @param columnHeader
	 *            excel数据表列集合
	 * @param list
	 *            数据集合
	 * @throws Exception
	 * @return void
	 */
	public static void export(String filePath, String sheetName, String normalHeader, String[] columnHeader,
			List<List<String>> list) throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(sheetName);
		ExcelUtil excelUtil = new ExcelUtil(wb, sheet);
		int cellIndex = 0;
		String[] regioColumns = null;
		for(int i=0;i<columnHeader.length;i++){
			if(columnHeader[i].contains(":")){
				regioColumns = columnHeader[i].split(":")[1].split("\\&");
				for(int j=0;j<regioColumns.length;j++){
					cellIndex++;
				}
			}else{
				cellIndex++;
			}
		}
		excelUtil.createNormalHead(normalHeader, cellIndex);
		excelUtil.createRegionColumnHead(columnHeader);
		int dataRowIndex = 2;
		if(excelUtil.hasRegioHead){
			dataRowIndex = 3;
		}
		excelUtil.createDataRow(list, dataRowIndex, HSSFCellStyle.ALIGN_CENTER);
		excelUtil.outputExcel(filePath);
	}
	
	public static void export(String filePath, String sheetName, String normalHeader, List<String> columnHeader,
			List<List<String>> list) throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(sheetName);
		ExcelUtil excelUtil = new ExcelUtil(wb, sheet);
		int cellIndex = 0;
		String[] regioColumns = null;
		int columnCount = columnHeader.size();
		String column = "";
		for(int i=0;i<columnCount;i++){
			column = columnHeader.get(i);
			if(column.contains(":")){
				regioColumns = column.split(":")[1].split("\\&");
				for(int j=0;j<regioColumns.length;j++){
					cellIndex++;
				}
			}else{
				cellIndex++;
			}
		}
		excelUtil.createNormalHead(normalHeader, cellIndex);
		excelUtil.createRegionColumnHead(columnHeader);
		int dataRowIndex = 2;
		if(excelUtil.hasRegioHead){
			dataRowIndex = 3;
		}
		excelUtil.createDataRow(list, dataRowIndex, HSSFCellStyle.ALIGN_CENTER);
		excelUtil.outputExcel(filePath);
	}
	
	public static void exportExcelModuls(String filePath, String sheetName, String[] columnHeader) throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(sheetName);
		ExcelUtil excelUtil = new ExcelUtil(wb, sheet);
		excelUtil.createColumnHead(columnHeader,0);
		excelUtil.outputExcel(filePath);
	}
	
	public static void exportExcelTemplate(String filePath, String sheetName, String[] fieldHeader,String[] columnHeader) throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(sheetName);
		ExcelUtil excelUtil = new ExcelUtil(wb, sheet);
		excelUtil.createColumnHead(fieldHeader,0);
		excelUtil.createColumnHead(columnHeader,1);
		excelUtil.outputExcel(filePath);
	}
	
	public static void createExcelModuls(String filePath, String sheetName, List<JSONObject> list) throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(sheetName);
		int count = list.size();
		ExcelUtil excelUtil = new ExcelUtil(wb, sheet);
		//excelUtil.createColumnHead(columnHeader,0);
		//excelUtil.outputExcel(filePath);
	}
	
	public static void exportReport(String filePath, String sheetName, String normalHeader, String[] columnHeader,
			List<List<String>> list,List<String> footList){
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(sheetName);
		ExcelUtil excelUtil = new ExcelUtil(wb, sheet);
		int cellIndex = 0;
		String[] regioColumns = null;
		for(int i=0;i<columnHeader.length;i++){
			if(columnHeader[i].contains(":")){
				regioColumns = columnHeader[i].split(":")[1].split("\\&");
				for(int j=0;j<regioColumns.length;j++){
					cellIndex++;
				}
			}else{
				cellIndex++;
			}
		}
		excelUtil.createNormalHead(normalHeader, cellIndex);
		excelUtil.createRegionColumnHead(columnHeader);
		int dataRowIndex = 2;
		if(excelUtil.hasRegioHead){
			dataRowIndex = 3;
		}
		excelUtil.createDataRow(list, dataRowIndex, HSSFCellStyle.ALIGN_CENTER);
		dataRowIndex = list.size()+2;
		for(int i=0;i<footList.size();i++){
			excelUtil.createRegionText(dataRowIndex++,footList.get(i),cellIndex);
		}
		excelUtil.outputExcel(filePath);
	}

	/**
	 * 功能描述：读取Excel文件的行数据<br>
	 * 创建作者：王明生<br>
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
			wb.setForceFormulaRecalculation(true);
			Sheet sheet = wb.getSheetAt(0);
			FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();  
			int totalRows = sheet.getLastRowNum()+1;
			int totalCells = 0;
			if(totalRows>0 && sheet.getRow(0)!=null){
				totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
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
						cellValue = getCellValue(cell,evaluator);
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
	
	//获取表格数值
	public static String getCellValue(Cell cell,FormulaEvaluator evaluator){
		String cellValueStr = "";
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
				System.out.println("*****");
				try {
					CellValue cellValue = evaluator.evaluate(cell);
					cellValueStr = String.valueOf(cellValue.getNumberValue());
				} catch (Exception e) {
					System.out.println(e.toString());
				}
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
		//readExcleTest();
		//testExportData();
		try {
			//testUpdateTannyExcel();
			testSaveExcelModuls();
			//testTannyExcel();
			//testExportTestExcel();
			//testCreateCellBgExcel();
			//testTannyExcelDemo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("OK");
	}
	
	public static void testSaveExcelTemp(){
		
	}
	
	public static void testExportTestExcel() throws Exception{
		String key = "贫困地区";
		BaseDAOImpl dao = (BaseDAOImpl)ApplicationListener.dao;
		String sql = "select ylmc,ylbs,ylmd,qttj,czbz,yqjg,scjg from t_test where xtmc like '%"+key+"%' and jgzt=0 and sjxg=0";
		List<JSONObject> list = dao.listAll(sql);
		int count = list.size();
		List<List<String>> dataList = new ArrayList<List<String>>();
		List<String> rowList = null;
		String[] columns_en = new String[]{"index","ylmc","ylbs","ylmd","qttj","czbz","yqjg","scjg"};
		String[] columns_cn = new String[]{"序号","用例名称","用例标识","用例目的","前提条件","操作步骤","预期结果","实测结果"};
		JSONObject rcd = null;
		for(int i=0;i<count;i++){
			rcd = list.get(i);
			rowList = new ArrayList<String>();
			for(int k=0;k<columns_en.length;k++){
				if(k==0){
					rowList.add(String.valueOf(i+1));
				}else{
					rowList.add((String)rcd.get(columns_en[k]));
				}
			}
			dataList.add(rowList);
		}
		export("h:/"+key+"系统.xls", "模型测试结果", "", columns_cn, dataList);
	}
	
//	public static boolean checkRowIsNull(Row row){
//		boolean isNull = true;
//		int count = row.getLastCellNum();
//		System.out.println(count);
//		String  cellValue = "";
//		Cell cell = null;
//		for(int i=0;i<count;i++){
//			cell = row.getCell(i);
//			System.out.println(cell);
//			if(cell!=null){
//				cellValue = getCellValue(row.getCell(i));
//				System.out.println("cellValue="+cellValue);
//				if(cellValue!=null&&!cellValue.equals("")){
//					isNull = false;
//					break;
//				}
//			}
//		}
//		return isNull;
//	}
	
	public static void testSaveExcelModuls() throws Exception{
		String filepath = "c:/aaa.xls";
		String[] columnHeader = new String[]{"编号ID","经度LON","纬度LAT","高程HEIGHT","风速WINDSPEED","机型","年发电量","有效小时数","空气密度"};
		exportExcelModuls(filepath,"固定信息",columnHeader);
	}
	
	public static void testRead2007Excel() throws Exception{
		String filepath = "G:/88.项目资料/24.金风科技二期项目/00.技术资料/金风风力发电经济评价模型(1).xlsm";
		List<List<String>> list = readExcel(filepath);
		int count = list.size();
		System.out.println("count="+count);
		for(int i=0;i<count;i++){
			System.out.println(list.get(i));
		}
	}
	
	
	public static void testReadExcel() throws Exception{
		String filepath = "D:/apache-tomcat-7.0.67/webapps/data/upload/excel/固定信息模板--数据.xls";
		List<?> list = readExcel(filepath);
		System.out.println(list);
		int count = list.size();
		String sql = "";
		List<String> rowList = null;
		BaseDAOImpl dao = (BaseDAOImpl)ApplicationListener.dao;
		//dao.saveObject("delete from t_modelclass");
 		for(int i=1;i<count;i++){
 			rowList = (List<String>)list.get(i);
 			String code = rowList.get(1);
 			String name = rowList.get(0);
 			String pid = rowList.get(2);
 			sql = "insert into t_modelclass(code,name,pid,sortIndex,descript,count) values('"+code+"','"+name+"','"+pid+"',100,'',0)";
 			int ret = dao.saveObject(sql);
 			if(ret==1){
 				System.out.println("模型分类【"+name+"】入库成功");
 			}
		}
	}
	
	public static void testExportData(){
		String fileName = "收益分析评传报告-"+com.zondy.utils.DateUtil.date2String("yyyyMMddHHmmss")+".xls";
		String filePath = ApplicationListener.rootPath+"temp/"+fileName;
		String sheetName = "收益分析评估报告";
		String normalHeader = "风机详细信息列表";
		String[] columnHeader = new String[]{"编号","经度","纬度","高程","风速","机型","年发电量","有效小时数","空气密度"};
		List list = new ArrayList();
		StringBuffer sb = new StringBuffer();
		sb.append("T1 107.5214 33.9336 3179.5796 GW93-1500 4.70 1967392.78 1311.60 0.94;");
		sb.append("T2 107.5239 33.9345 3208.2800 GW93-1500 4.70 1967392.78 1311.60 0.94;");
		sb.append("T3 107.5267 33.9336 3200.3891 GW93-1500 4.70 1967392.78 1311.60 0.94;");
		sb.append("T4 107.5284 33.9317 3184.0063 GW93-1500 4.70 1967392.78 1311.60 0.94;");
		String dataStr = sb.toString();
		String[] tmpArr = dataStr.split(";");
		List<String> dataList = null;
		for(int i=0;i<tmpArr.length;i++){
			dataList = getStringList(tmpArr[i]);
			list.add(dataList);
		}
		List<String> footList = new ArrayList<String>();
		footList.add("风机台数：4台，年总发电量：786.96万千瓦，年平均等效小时数：1311.60");
		footList.add("最短道路长度：1200米，全场平均风速：4.70m/s，平均空气密度：0.94Kg/m³");
		footList.add("电价：0.65元/度，限电率：2%，单位造价：3788元/千瓦");
		//footList.add("模拟风电场收益分析结果：资本金IRR：5.43%，全投资IRR：9.89%");
		footList.add("资本金IRR：5.43%，全投资IRR：9.89%");
		try {
			exportReport(filePath, sheetName, normalHeader, columnHeader, list,footList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

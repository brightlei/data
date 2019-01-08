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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FileUtil {

	private static Logger log = Logger.getLogger(FileUtil.class);
	private String[] sourceFile = new String[255]; //源文件名  
	private String[] suffix = new String[255]; //文件后缀名  
	private String canSuffix = ""; //可上传的文件后缀名  
	private String objectPath = "c:/"; //目标文件目录  
	private String[] objectFileName = new String[255]; //目标文件名  
	private ServletInputStream sis = null; //输入流  
	private String[] description = new String[255]; //描述状态  
	private long size = 100 * 1024; //限制大小  
	private int count = 0; //已传输文件数目  
	private byte[] b = new byte[4096]; //字节流存放数组  
	private boolean successful = true;  
	private Hashtable<String,String> fields = new Hashtable<String,String>();
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		long st = System.currentTimeMillis();
		//FileToZip("Z:/space/cimiss_BABJ/storefile/gis/gisplat/sate/FY2D/20120918", "c:/1.zip");
		checkFileOnDisk("E:/ftp/adasdasds","folder");
		long et = System.currentTimeMillis();
		System.out.println("exetime="+(et-st)+"ms");
	}
	/**
	 * 获取文件大小
	 * @param f 文件
	 * @return
	 */
	public static long getFileSize(File f){
		long size = 0;
		try {
			if(f.exists()){
				FileInputStream fis = null;
				fis = new FileInputStream(f);
				size = fis.available();
			}else{ 
				f.createNewFile();
				log.info("文件不存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}
	
	/**
	 * 递归获取文件夹大小	
	 * @param f 方件夹
	 * @return
	 */
	public static long getForldSize(File f){
		long size = 0;
		File[] files = f.listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].isDirectory()){
				size = size + getForldSize(files[i]);
			}else{
				size = size + files[i].length();
			}
		}
		return size;
	}
	/**
	 * 转换文件大小
	 * @param fileSize 文件大小
	 * @return
	 */
	public static String FormatFileSize(long fileSize){
		DecimalFormat df = new DecimalFormat("#0.00");
		String fileSizeString = "";
		if(fileSize<1024){
			fileSizeString = df.format((double)fileSize)+"B";
		}else if(fileSize<1048576){
			fileSizeString = df.format((double)fileSize/1024)+"K";
		}else if(fileSize<1073741824){
			fileSizeString = df.format((double)fileSize/1048576)+"M";
		}else{
			fileSizeString = df.format((double)fileSize/1073741824)+"G";
		}
		return fileSizeString;
	}
	/**
	 * 递归获取文件目录文件个数
	 * @return
	 */
	public static long getForldFiles(File f){
		long size = 0;
		File[] files = f.listFiles();
		size = files.length;
		for(int i=0;i<files.length;i++){
			if(files[i].isDirectory()){
				size = size + getForldFiles(files[i]);
				size = size - 1;
			}
		}
		return size;
	}
	
	/**
	 * 目录文件按最后修改日期排列
	 * @param forldPath
	 * @return
	 */
	public static File[] sortFilesByLashModify(String forldPath){
		File file = new File(forldPath);
		File[] fs = null;
		try {
			if(file.exists()){
				fs = file.listFiles();
				Arrays.sort(fs,new FileUtil.CompratorByLastModified());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fs;
	}
	/**
	public static String getContentType(String type){
		String contentType = "text/html";
		//String rootPath = HttpContext.rootPath;
		String rootPath = "C:/Tomcat6/webapps/weather/";
		String path = rootPath+"/WEB-INF/classes/contentType.xml";
		log.info("getContentType[path] - "+path);
		try {
			File f = new File(path);
			log.info("getContentType[file] - "+f);
			Document doc = DOMUtils.readXml(f);
			log.info("getContentType[doc] - "+doc);
			NodeList keyList = doc.getElementsByTagName("key");
			NodeList valueList = doc.getElementsByTagName("value");
			String key = null;
			String value = null;
			for(int i=0;i<keyList.getLength();i++){
				key = keyList.item(i).getFirstChild().getNodeValue();
				value = valueList.item(i).getFirstChild().getNodeValue();
				if(key.equals(type)){
					contentType = value;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contentType;
	}*/
	
	/**
	 * 删除指定路径的文件
	 * @param filePath
	 */
	public static void deleteFile(String filePath){
		File file = new File(filePath);
		if(file.exists()){
			file.delete();
		}
	}
	
	/**
	 * 功能描述：删除某个文件夹下的所有文夹和文件<br>
	 * 创建作者：雷志强<br>
	 * 创建时间：2013-11-15 上午09:53:59<br>
	 * @param dirPath
	 * @return
	 * @return boolean
	 */
	public static void deleteDirectory(String dirPath){
		File dirFile = new File(dirPath);
		if(dirFile.exists()){
			if(dirFile.isDirectory()){
				File[] filelist = dirFile.listFiles();
				int fileCount = filelist.length;
				File deleteFile = null;
				for(int i=0;i<fileCount;i++){
					deleteFile = filelist[i];
					if(deleteFile.isDirectory()){
						deleteDirectory(deleteFile.getAbsolutePath());
						deleteFile.delete();
					}else{
						deleteFile.delete();
					}
				}
				dirFile.delete();
			}else{
				dirFile.delete();
			}
		}
	}
	
	/**检查文件目录是否存在，不存在并创建
	 * @author LZQ
	 * @param filePath 文件目录地址
	 */
	public static void checkDirExist(String filePath){
		int index = filePath.lastIndexOf("/");
		if(index==-1){
			index=filePath.lastIndexOf('\\');
		}
		String dirPath = filePath.substring(0,index);
		try {
			File dirFile = new File(dirPath);
			if(!dirFile.exists()){
				dirFile.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**读取文件生成byte数组
	 * @author LZQ
	 * @param file 文件路径
	 * @return byte数组
	 * @throws IOException
	 *************************************************/
	public static byte[] FileToByteArray(File file)throws IOException
	{
		byte[] bt = new byte[128];
		InputStream is = new FileInputStream(file);
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		int ch;
		while((ch = is.read()) != -1)
		{
			byteArray.write(ch);
		}
		bt = byteArray.toByteArray();
		byteArray.close();
		return bt;
	}
	
	/**byte数组生成文件
	 * @author LZQ
	 * @param bt byte数组
	 * @param outFile 文件输出路径
	 * @return 文件路径
	 * @throws IOException
	 *********************************/
	public static String ByteArrayToFile(byte[] bt,String outFile)
	{
		String outSrc = "";
		try {
			checkDirExist(outFile);
			File file = new File(outFile);
			FileOutputStream outStream = new FileOutputStream(file);
			outStream.write(bt);
			outStream.close();
			outSrc = outFile;
		} catch (Exception e) {
			e.getStackTrace();
		}
		return outSrc;
	}
	
	/**输入流转换成文件
	 * @author LZQ
	 * @param is 输入流
	 * @param filePath 文件输出路径
	 * @return 文件输出路径
	 */
	public static String iStreamToFile(InputStream is,String filePath){
		String rs = "";
		try {
			OutputStream os = new FileOutputStream(filePath);
			while(is.available()>0){
				int data = is.read();
				os.write(data);
			}
			os.close();
			is.close();
			rs = filePath;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	/**输入流转换成byte数组
	 * @author LZQ
	 * @param is 输入流
	 * @return byte数组
	 */
	public static byte[] getBytesFromIS(InputStream is){
		try {
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			int len = 0;
			byte[] bt = new byte[1024];
			while((len=is.read(bt, 0, bt.length))!=-1){
				bao.write(bt,0,len);
			}
			return bao.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**读取文件生成StringBuffer字符串
	 * @author LZQ
	 * @param path 文件路径
	 * @return  StringBuffer字符串
	 * @throws IOException
	 */
	public static StringBuffer BufferReaderFile(String path)throws IOException
	{
		File file = new File(path);
		StringBuffer sb = new StringBuffer();
		if(!file.exists()||file.isDirectory())
		{
			throw new FileNotFoundException();
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String temp = "";
		temp = br.readLine();
		while(temp!=null)
		{
			sb.append(temp+"\r\n");
			temp = br.readLine();
		}
		br.close();
		return sb;
	}
	
	public static StringBuffer BufferReaderFile(String path,String encode)throws IOException
	{
		File file = new File(path);
		StringBuffer sb = new StringBuffer();
		if(!file.exists()||file.isDirectory())
		{
			throw new FileNotFoundException();
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encode));
		String temp = "";
		temp = br.readLine();
		while(temp!=null)
		{
			sb.append(temp+"\r\n");
			temp = br.readLine();
		}
		br.close();
		return sb;
	}
	public static BufferedReader GetFileBufferReader(File file)throws IOException
	{
		if(!file.exists()||file.isDirectory())
		{
			throw new FileNotFoundException();
		}
		BufferedReader br = new BufferedReader(new FileReader(file));
		return br;
	}
	
	/**将StringBuffer字符写入到一个文件
	 * @author LZQ
	 * @param path 文件输出路径
	 * @param sb StringBuffer对象
	 * @throws IOException
	 */
	public static void BufferWriteFile(String path,StringBuffer sb)throws IOException
	{
		checkFileOnDisk(path,"file");
		File file = new File(path);
		FileOutputStream out = new FileOutputStream(file,true);
		out.write(sb.toString().getBytes("UTF-8"));
		out.close();
	}
	
	/**将String字符写入到一个文件
	 * @author LZQ
	 * @param path 文件输出路径
	 * @param str String字符串
	 * @throws IOException
	 */
	public static void BufferWriteFile(String path,String str)throws IOException
	{
		checkFileOnDisk(path,"file");
		try {
			File file = new File(path);
			FileOutputStream out = new FileOutputStream(file,true);
			out.write(str.getBytes("UTF-8"));
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**检查磁盘上文件或者目录是否存在
	 * @author LZQ
	 * @param path 文件或者目录路径
	 * @param fileType 文件还是目录
	 */
	public static void checkFileOnDisk(String path,String fileType){
		if(fileType.equals("file")){
			File file = new File(path);
			if(!file.exists()){
				String folderPath = path.substring(0,path.lastIndexOf("/"));
				try {
					File folderFile = new File(folderPath);
					folderFile.mkdirs();
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else if(fileType.equals("folder")){
			File folderFile = new File(path);
			if(!folderFile.exists()){
				folderFile.mkdirs();
			}
		}else if(fileType.equals("dir")){
			File folderFile = new File(path);
			if(!folderFile.exists()){
				folderFile.mkdirs();
			}
		}
	}
	
	public static void download(HttpServletResponse response,String filepath){
		try {
			OutputStream os = response.getOutputStream();
			byte[] bt = new byte[1024];
			File fileLoad = new File(filepath);
			String fileName = fileLoad.getName();
			fileName = URLEncoder.encode(fileName, "UTF-8");//处理中文文件名
			fileName = new String(fileName.getBytes("UTF-8"),"GBK");//处理中文文件名
			response.addHeader("Content-disposition", "attachment;filename=" + fileName);
			response.addHeader("Content-Length", "" + fileLoad.length());
			//response.setContentType("application/x-msdownload");
			response.setContentType("application/octet-stream;charset=ISO8859-1");
			//response.setContentType("application/vnd.ms-excel");
			FileInputStream fis = new FileInputStream(fileLoad);
			int n = 0;
			while ((n = fis.read(bt)) != -1) {
				os.write(bt, 0, n);
			}
			os.flush();
			os.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**将指定文件目录下的文件打成压缩包
	 * @author LZQ
	 * @param folder 指定文件目录路径
	 * @param zipPath 压缩包存放地址
	 */
	public static void FileToZip(String folder,String zipPath){
		int buffer = 2048;
		try {
			BufferedInputStream origin = null;
			byte[] data = new byte[buffer];
			File f = new File(folder);
			if(f.isDirectory()){
				checkFileOnDisk(zipPath, "file");
				FileOutputStream dest = new FileOutputStream(zipPath);
				ZipOutputStream zOut = new ZipOutputStream(new BufferedOutputStream(dest));
				File files[] = f.listFiles();
				for(int i=0;i<files.length;i++){
					FileInputStream fis = new FileInputStream(files[i]);
					origin = new BufferedInputStream(fis,buffer);
					ZipEntry entry = new ZipEntry(files[i].getName());
					zOut.putNextEntry(entry);
					int count;
					while((count=origin.read(data, 0, buffer))!=-1){
						zOut.write(data, 0, count);
					}
					origin.close();
				}
				zOut.close();
			}else{
				System.out.println("打包的文件目录不存在！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void FileToZip(File[] files,String zipPath){
		int buffer = 2048;
		try {
			BufferedInputStream origin = null;
			byte[] data = new byte[buffer];
			//checkFileOnDisk(zipPath, "file");
			FileOutputStream dest = new FileOutputStream(zipPath);
			ZipOutputStream zOut = new ZipOutputStream(new BufferedOutputStream(dest));
			for(int i=0;i<files.length;i++){
				FileInputStream fis = new FileInputStream(files[i]);
				origin = new BufferedInputStream(fis,buffer);
				ZipEntry entry = new ZipEntry(files[i].getName());
				zOut.putNextEntry(entry);
				int count;
				while((count=origin.read(data, 0, buffer))!=-1){
					zOut.write(data, 0, count);
				}
				origin.close();
			}
			zOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**将压缩包解压到指定目录下
	 * @author LZQ
	 * @param folder 文件存放目录
	 * @param zipPath 压缩包文件路径
	 */
	@SuppressWarnings("unchecked")
	public static void ZipToFile(String folder,String zipPath){
		int buffer = 2048;
		try {
			File zf = new File(zipPath);
			if(zf.exists()){
				int slen = zipPath.length();
				if(zipPath.substring(zipPath.lastIndexOf("."),slen).equals(".zip")){
					ZipFile zFile = new ZipFile(zf);
					Enumeration emu = zFile.entries();
					//int i = 0;
					while(emu.hasMoreElements()){
						ZipEntry entry = (ZipEntry)emu.nextElement();
						//会把目录做为一个file文件读出一次，所以只建立目录就可以了，之后的文件还会被迭代到
						if(entry.isDirectory()){
							new File(folder+entry.getName()).mkdirs();
							continue;
						}
						BufferedInputStream bis = new BufferedInputStream(zFile.getInputStream(entry));
						File file = new File(folder+entry.getName());
						//加入这个原因是因为zFile读取文件是随机读取的，而这个文件的目录还没有出现过，所以要先建立目录
						File pf = file.getParentFile();
						if(pf!=null&&(!pf.exists())){
							pf.mkdirs();
						}
						FileOutputStream fos = new FileOutputStream(file);
						BufferedOutputStream bos = new BufferedOutputStream(fos,buffer);
						int count;
						byte[] data = new byte[buffer];
						while((count=bis.read(data, 0, buffer))!=-1){
							bos.write(data, 0, count);
						}
						bos.flush();
						bos.close();
						bis.close();
					}
					zFile.close();
					System.out.println("压缩文件解压成功");
				}else{
					System.out.println("请检查文件是否为解压缩格式！");
				}
			}else{
				System.out.println("解压缩文件不存在！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	public static void addXmlNode(String xmlPath,String pngName,String rect){
		File file = new File(xmlPath);
		if(file.exists()){
			try {
				Document doc = DOMUtils.readXml(file);
				Node root = doc.getFirstChild();
				Node child = doc.createElement("MapDocPng");
				Node pngNode = doc.createElement("pngName");
				Node tPngName = doc.createTextNode(null);
				tPngName.setNodeValue(pngName);
				pngNode.appendChild(tPngName);	
				Node boxNode = doc.createElement("pngRect");
				Node tPngRect = doc.createTextNode(null);
				tPngRect.setNodeValue(rect);
				boxNode.appendChild(tPngRect);
				child.appendChild(pngNode);
				child.appendChild(boxNode);
				root.appendChild(child);
				doc.appendChild(root);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			
		}else{
			Document doc = DOMUtils.createDocument();
			Node node = doc.createElement("root");
			Node child = doc.createElement("MapDocPng");
			Node pngNode = doc.createElement("pngName");
			//Node tPngName = doc.createTextNode(null);
			//tPngName.setNodeValue(pngName);
			//pngNode.appendChild(tPngName);	
			Node boxNode = doc.createElement("pngRect");
			//Node tPngRect = doc.createTextNode(null);
			//tPngRect.setNodeValue(rect);
			//boxNode.appendChild(tPngRect);
			child.appendChild(pngNode);
			child.appendChild(boxNode);
			node.appendChild(child);
			doc.appendChild(node);
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(file);
				DOMUtils.writeXml(doc, fos);
				//((XmlDocument) doc).write(fos);
				fos.flush();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	*/
	//设置上传文件的后缀名  
	public void setSuffix(String canSuffixStr) {
		log.info("canSuffixStr --> "+canSuffixStr);
		this.canSuffix = canSuffixStr;
		log.info("this.canSuffix --> "+this.canSuffix);
	}
	public String getSuffix(){
		return this.canSuffix;
	}
	// 设置文件保存路径
	public void setObjectPath(String objectPathStr) {
		log.info("objectPathStr --> "+objectPathStr);
		this.objectPath = objectPathStr;
		log.info("this.objectPath --> "+this.objectPath);
	}
	public String ObjectPath(){
		return this.objectPath;
	}
	// 设置文件保存路径
	public void setSize(long maxSize) {
		log.info("maxSize --> "+maxSize);
		this.size = maxSize;
		log.info("this.size --> "+this.size);
	}
	public long getSize() {
		return this.size;
	}
	// 文件上传处理程序
	public void setSourceFile(HttpServletRequest request) throws IOException {
		sis = request.getInputStream();
		int a = 0;
		int k = 0;
		String s = "";
		while ((a = sis.readLine(b, 0, b.length)) != -1) {
			s = new String(b, 0, a);
			if ((k = s.indexOf("filename=\"")) != -1) {
				// 取得文件数据
				s = s.substring(k + 10);
				k = s.indexOf("\"");
				s = s.substring(0, k);
				sourceFile[count] = s;
				objectFileName[count] = s;
				k = s.lastIndexOf(".");
				suffix[count] = s.substring(k + 1);
				if (canTransfer(count)) {
					transferFile(count);
				}
				++count;
			} else if ((k = s.indexOf("name=\"")) != -1) {
				// 普通表单输入元素，获取输入元素名字
				String fieldName = s.substring(k + 6, s.length() - 3);
				sis.readLine(b, 0, b.length);
				StringBuffer fieldValue = new StringBuffer(b.length);
				while ((a = sis.readLine(b, 0, b.length)) != -1) {
					s = new String(b, 0, a - 2);
					if ((b[0] == 45) && (b[1] == 45) && (b[2] == 45) && (b[3] == 45) && (b[4] == 45)) {
						break;
					} else {
						fieldValue.append(s);
					}
				}
				fields.put(fieldName, fieldValue.toString());
			}
			if (!successful)
				break;
		}
	}

	// 取得表单元素值
	public String getFieldValue(String fieldName) {
		if (fields == null || fieldName == null) {
			return null;
		}
		return (String) fields.get(fieldName);
	}

	// 取得上传文件数
	public int getCount() {
		return count;
	}

	// 取得目标路径
	public String getObjectPath() {
		return objectPath;
	}

	// 取得源文件名
	public String[] getSourceFile() {
		return sourceFile;
	}

	// 取得目标文件名
	public String[] getObjectFileName() {
		return objectFileName;
	}

	// 取得上传状态描述
	public String[] getDescription() {
		return description;
	}

	// 判断上传文件的类型
	private boolean canTransfer(int i) {
		canSuffix = getSuffix(); 
		log.info("canSuffix --> "+canSuffix);
		suffix[i] = suffix[i].toLowerCase();
		log.info("suffix[i] --> "+suffix[i]);
		// 这个是用来传图片的，各位可以把后缀名改掉或者不要这个条件
		if (sourceFile[i].equals("") || (!(canSuffix.indexOf("." + suffix[i]) >= 0))) {
			log.info("sourceFile[i] --> "+sourceFile[i]);
			description[i] = "noSuffix";
			return false;
		} else {
			return true;
		}
	}

	// 上传文件转换
	private void transferFile(int i) {
		//String x = Long.toString(new java.util.Date().getTime());
		try {
			//objectFileName[i] = x + "." + suffix[i];
			//FileOutputStream out = new FileOutputStream(objectPath + objectFileName[i]);
			FileOutputStream out = new FileOutputStream(objectPath + objectFileName[i]);
			int a = 0;
			@SuppressWarnings("unused")
			int k = 0;
			long hastransfered = 0; // 标示已经传输的字节数
			String s = "";
			while ((a = sis.readLine(b, 0, b.length)) != -1) {
				s = new String(b, 0, a);
				if ((k = s.indexOf("Content-Type:")) != -1) {
					break;
				}
			}
			sis.readLine(b, 0, b.length);
			log.info("size --> "+size);
			while ((a = sis.readLine(b, 0, b.length)) != -1) {
				s = new String(b, 0, a);
				if ((b[0] == 45) && (b[1] == 45) && (b[2] == 45) && (b[3] == 45) && (b[4] == 45)) {
					break;
				}
				out.write(b, 0, a);
				hastransfered += a;
				if (hastransfered >= size) {
					description[count] = "outOfSize,"+size;
					//description[count] = "ERR: The file " + sourceFile[count] + " is too large to transfer. The whole process is interrupted.";
					successful = false;
					break;
				}
			}
			log.info("hastransfered --> "+hastransfered);
			if (successful) {
				description[count] = "successful,"+objectFileName[count];
				//description[count] = "Right: The file " + sourceFile[count] + " has been transfered successfully.";
			}
			out.close();
			if (!successful) {
				sis.close();
				File tmp = new File(objectPath + objectFileName[count]);
				tmp.delete();
			}
		} catch (IOException ioe) {
			description[i] = ioe.toString();
		}
	}
	/**
	 * 文件按日期升序排序
	 * @author 雷志强
	 *
	 */
	static class CompratorByLastModified implements Comparator<File>{  
		public int compare(File f1, File f2) {  
			long diff = f1.lastModified() - f2.lastModified();  
			if(diff>0)  
				return 1;  
			else if(diff==0)
				return 0;  
			else  
				return -1;  
		}  
		public boolean equals(Object obj){
			return true;  
		}
	}
}

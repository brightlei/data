package com.zondy.web.json;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONObject;
import com.zondy.config.ParamConfig;
import com.zondy.config.QuerySqlConfig;
import com.zondy.config.WebConfig;
import com.zondy.listener.ApplicationListener;
import com.zondy.utils.DateUtil;
import com.zondy.utils.ImageUtils;
import com.zondy.web.DataImport;
import com.zondy.web.action.BaseAction;

@SuppressWarnings("serial")
public class UploadAction extends BaseAction {
	private static Logger log = Logger.getLogger(UploadAction.class);
	private File uploadify;//上传文件file对象
	private String uploadifyFileName;//上传文件名
	private String uploadifyContentType;//上传文件类型
	private String description;//上传文件的描述
	private String uploadDir;//保存上传文件的目录,相对于web应用程序的根路径,在struts.xml文件中配置
	public File getUploadify() {
		return uploadify;
	}

	public void setUploadify(File uploadify) {
		this.uploadify = uploadify;
	}

	public String getUploadifyFileName() {
		return uploadifyFileName;
	}

	public void setUploadifyFileName(String uploadifyFileName) {
		this.uploadifyFileName = uploadifyFileName;
	}

	public String getUploadifyContentType() {
		return uploadifyContentType;
	}

	public void setUploadifyContentType(String uploadifyContentType) {
		this.uploadifyContentType = uploadifyContentType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUploadDir() {
		return uploadDir;
	}

	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}
	
	private String getFileName(){
		long r = Math.round((Math.random()+1)*10000);
		String nowtime = DateUtil.date2String("yyyyMMddHHmmssSSS")+"-"+String.valueOf(r);
		return nowtime;
	}
	/**
	 * 功能描述：上传标注图标
	 * 创建作者：雷志强
	 * 创建时间：2017年7月13日 下午11:54:28
	 * @throws Exception
	 * @return String
	 */
	public String uploadMarkerIcon() throws Exception{
		log.info("uploadifyFileName="+uploadifyFileName);
		String newFileName=null;
		String fname = getFileName();
		//得到保存上传文件的真实路径
		int index=uploadifyFileName.lastIndexOf(".");
		//获取文件后缀名
		String filetype = uploadifyFileName.substring(index+1);
		//判断上传文件是否有扩展名,以时间戳作为新的文件名
		if (index==-1) {
			filetype = "png";
		}
		BufferedOutputStream bos=null;
		BufferedInputStream bis=null;
		//读取保存在临时目录下的上传文件,写入到新的文件中
		try {
			FileInputStream fis=new FileInputStream(uploadify);
			BufferedImage img = ImageIO.read(fis);
			int width = img.getWidth();
			int height = img.getHeight();
			log.info("width="+width+",height="+height);
			String sizeInfo = "_"+width+"X"+height;
			newFileName="icon-"+fname+sizeInfo+"."+filetype;
			String absolutePath = "upload/icon/"+newFileName;
			String filepath = ApplicationListener.rootPath+absolutePath;
			log.info("filepath="+filepath);
			if(width>64||height>64){
				this.dataMap.put("state",false);
				this.dataMap.put("data", "上传的图标图片尺寸超过规定限制，请按说明上传图标文件！");
			}else{
				ImageIO.write(img, filetype, new File(filepath));
//				bis=new BufferedInputStream(fis);
//				FileOutputStream fos=new FileOutputStream(new File(filepath));
//				bos=new BufferedOutputStream(fos);
//				byte [] buf=new byte[4096];
//				int len=-1;
//				while ((len=bis.read(buf))!=-1) {
//					bos.write(buf,0,len);
//				}
				this.dataMap.put("state",true);
				this.dataMap.put("data", absolutePath);
			}
		} catch (FileNotFoundException e) {
			this.dataMap.put("state",false);
			this.dataMap.put("error",e.getMessage());
			log.error("FileNotFoundException", new Throwable(e));
		} catch (IOException e) {
			this.dataMap.put("state",false);
			this.dataMap.put("error",e.getMessage());
			log.error("IOException", new Throwable(e));
		}finally{
			if (null!=bis) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null!=bos) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//按指定大小进行缩放图片
		//ImageUtils.thumbnailsBySize(filepath, width, height, filepath);
		return "map";
	}
	
	public String uploadUserImg(){
		String newFileName=null;
		String fname = getFileName();
		int width = Integer.parseInt(WebConfig.getConfig("userPhotoImgWidth"));
		int height = Integer.parseInt(WebConfig.getConfig("userPhotoImgHeight"));
		//得到保存上传文件的真实路径
		int index=uploadifyFileName.lastIndexOf(".");
		//获取文件后缀名
		String filetype = uploadifyFileName.substring(index+1);
		//判断上传文件是否有扩展名,以时间戳作为新的文件名
		if (index==-1) {
			filetype = "png";
		}
		String sizeInfo = "_"+width+"X"+height;
		String smallSizeInfo = "_48X48";
		newFileName = "user-"+fname+sizeInfo+"."+filetype;
		String smallFileName = "user-"+fname+smallSizeInfo+"."+filetype;
		String absolutePath = "upload/userimg/"+newFileName;
		String absolutePath_smallimg = "upload/userimg/"+smallFileName;
		log.info("absolutePath="+absolutePath);
		String filepath = ApplicationListener.rootPath+absolutePath;
		log.info("filepath="+filepath);
		String smallImagePath = ApplicationListener.rootPath+absolutePath_smallimg;
		log.info("smallImagePath="+smallImagePath);
		BufferedOutputStream bos=null;
		BufferedInputStream bis=null;
		//读取保存在临时目录下的上传文件,写入到新的文件中
		try {
			FileInputStream fis=new FileInputStream(uploadify);
			bis=new BufferedInputStream(fis);
			FileOutputStream fos=new FileOutputStream(new File(filepath));
			bos=new BufferedOutputStream(fos);
			byte [] buf=new byte[4096];
			int len=-1;
			while ((len=bis.read(buf))!=-1) {
				bos.write(buf,0,len);
			}
			this.dataMap.put("state",true);
			this.dataMap.put("data", absolutePath);
		} catch (FileNotFoundException e) {
			this.dataMap.put("state",false);
			this.dataMap.put("error",e.getMessage());
			log.error("FileNotFoundException", new Throwable(e));
		} catch (IOException e) {
			this.dataMap.put("state",false);
			this.dataMap.put("error",e.getMessage());
			log.error("IOException", new Throwable(e));
		}finally{
			if (null!=bis) {
				try {
					bis.close();
				} catch (IOException e) {
					log.error("IOException", new Throwable(e));
					//e.printStackTrace();
				}
			}
			if (null!=bos) {
				try {
					bos.close();
				} catch (IOException e) {
					log.error("IOException", new Throwable(e));
					//e.printStackTrace();
				}
			}
		}
		//将用户头像按指定大小进行缩放
		try {
			ImageUtils.thumbnailsBySize(filepath, width, height, filepath);
			//生成48X48综略图
			ImageUtils.thumbnailsBySize(filepath, 48, 48, smallImagePath);
		} catch (IOException e) {
			log.error("IOException", new Throwable(e));
		}
		return "map";
	}
	
	//上传表格数据文件
	public String importExcel() throws Exception{
		String newFileName=null;
		//得到当前时间开始流逝的毫秒数,将这个毫秒数作为上传文件新的文件名
		//long now = new Date().getTime();
		//log.debug("now="+now);
		String now = DateUtil.date2String("yyyyMMddHHmmssSSS");
		//得到保存上传文件的真实路径
		int index=uploadifyFileName.lastIndexOf(".");
		log.debug("index="+index);
		//判断上传文件是否有扩展名,以时间戳作为新的文件名
		if (index!=-1) {
			newFileName="excel-"+now+uploadifyFileName.substring(index);
		}else {
			newFileName="excel-"+now;
		}
		String absolutePath = "upload/excel/"+newFileName;
		String filepath = ApplicationListener.rootPath+absolutePath;
		log.info("filepath="+filepath);
		File file = new File(filepath);
		FileUtils.forceMkdir(file.getParentFile());
		BufferedOutputStream bos=null;
		BufferedInputStream bis=null;
		//读取保存在临时目录下的上传文件,写入到新的文件中
		try {
			FileInputStream fis=new FileInputStream(uploadify);
			bis=new BufferedInputStream(fis);
			FileOutputStream fos=new FileOutputStream(new File(filepath));
			bos=new BufferedOutputStream(fos);
			byte [] buf=new byte[4096];
			int len=-1;
			while ((len=bis.read(buf))!=-1) {
				bos.write(buf,0,len);
			}
			this.dataMap.put("data", absolutePath);
			this.dataMap.put("state", true);
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException", new Throwable(e));
			this.dataMap.put("state", false);
			this.dataMap.put("error", "上传文件失败！找不到该文件！"+filepath);
		} catch (IOException e) {
			//e.printStackTrace();
			this.dataMap.put("state", false);
			this.dataMap.put("error", "上传文件失败！"+e.getMessage());
		}finally{
			if (null!=bis) {
				try {
					bis.close();
				} catch (IOException e) {
					log.error("IOException", new Throwable(e));
					this.dataMap.put("state", false);
					this.dataMap.put("error", "上传文件失败！"+e.getMessage());
					//e.printStackTrace();
				}
			}
			if (null!=bos) {
				try {
					bos.close();
				} catch (IOException e) {
					log.error("IOException", new Throwable(e));
					this.dataMap.put("state", false);
					this.dataMap.put("error", "上传文件失败！"+e.getMessage());
					//e.printStackTrace();
				}
			}
		}
		return "map";
	}
	
	//上传铁路线坐标点表格数据文件
	public String importLinePointExcel() throws Exception{
		String newFileName=null;
		String now = DateUtil.date2String("yyyyMMddHHmmssSSS");
		//得到保存上传文件的真实路径
		int index=uploadifyFileName.lastIndexOf(".");
		log.debug("index="+index);
		//判断上传文件是否有扩展名,以时间戳作为新的文件名
		if (index!=-1) {
			newFileName="excel-"+now+uploadifyFileName.substring(index);
		}else {
			newFileName="excel-"+now;
		}
		String absolutePath = "upload/excel/"+newFileName;
		String filepath = ApplicationListener.rootPath+absolutePath;
		log.info("filepath="+filepath);
		File file = new File(filepath);
		FileUtils.forceMkdir(file.getParentFile());
		BufferedOutputStream bos=null;
		BufferedInputStream bis=null;
		//读取保存在临时目录下的上传文件,写入到新的文件中
		try {
			FileInputStream fis=new FileInputStream(uploadify);
			bis=new BufferedInputStream(fis);
			FileOutputStream fos=new FileOutputStream(new File(filepath));
			bos=new BufferedOutputStream(fos);
			byte [] buf=new byte[4096];
			int len=-1;
			while ((len=bis.read(buf))!=-1) {
				bos.write(buf,0,len);
			}
			this.dataMap.put("data", absolutePath);
			this.dataMap.put("state", true);
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException", new Throwable(e));
			this.dataMap.put("state", false);
			this.dataMap.put("error", "上传文件失败！找不到该文件！"+filepath);
		} catch (IOException e) {
			this.dataMap.put("state", false);
			this.dataMap.put("error", "上传文件失败！"+e.getMessage());
		}finally{
			if (null!=bis) {
				try {
					bis.close();
				} catch (IOException e) {
					log.error("IOException", new Throwable(e));
					this.dataMap.put("state", false);
					this.dataMap.put("error", "上传文件失败！"+e.getMessage());
					//e.printStackTrace();
				}
			}
			if (null!=bos) {
				try {
					bos.close();
				} catch (IOException e) {
					log.error("IOException", new Throwable(e));
					this.dataMap.put("state", false);
					this.dataMap.put("error", "上传文件失败！"+e.getMessage());
					//e.printStackTrace();
				}
			}
		}
		DataImport imp = new DataImport();
		int count = 0;
		try {
			count = imp.importLinePoint(filepath);
			this.dataMap.put("count", count);
		} catch (Exception e) {
			this.dataMap.put("state", false);
			this.dataMap.put("error", "批量导入数据失败！"+e.getMessage());
		}
		return "map";
	}
	
	//上传图层坐标点表格数据文件
	public String importLayerDataExcel() throws Exception{
		String newFileName=null;
		String now = DateUtil.date2String("yyyyMMddHHmmssSSS");
		//得到保存上传文件的真实路径
		int index=uploadifyFileName.lastIndexOf(".");
		log.debug("index="+index);
		//判断上传文件是否有扩展名,以时间戳作为新的文件名
		if (index!=-1) {
			newFileName="excel-"+now+uploadifyFileName.substring(index);
		}else {
			newFileName="excel-"+now;
		}
		String absolutePath = "upload/excel/"+newFileName;
		String filepath = ApplicationListener.rootPath+absolutePath;
		log.info("filepath="+filepath);
		File file = new File(filepath);
		FileUtils.forceMkdir(file.getParentFile());
		BufferedOutputStream bos=null;
		BufferedInputStream bis=null;
		//读取保存在临时目录下的上传文件,写入到新的文件中
		try {
			FileInputStream fis=new FileInputStream(uploadify);
			bis=new BufferedInputStream(fis);
			FileOutputStream fos=new FileOutputStream(new File(filepath));
			bos=new BufferedOutputStream(fos);
			byte [] buf=new byte[4096];
			int len=-1;
			while ((len=bis.read(buf))!=-1) {
				bos.write(buf,0,len);
			}
			this.dataMap.put("data", absolutePath);
			this.dataMap.put("state", true);
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException", new Throwable(e));
			this.dataMap.put("state", false);
			this.dataMap.put("error", "上传文件失败！找不到该文件！"+filepath);
		} catch (IOException e) {
			this.dataMap.put("state", false);
			this.dataMap.put("error", "上传文件失败！"+e.getMessage());
		}finally{
			if (null!=bis) {
				try {
					bis.close();
				} catch (IOException e) {
					log.error("IOException", new Throwable(e));
					this.dataMap.put("state", false);
					this.dataMap.put("error", "上传文件失败！"+e.getMessage());
					//e.printStackTrace();
				}
			}
			if (null!=bos) {
				try {
					bos.close();
				} catch (IOException e) {
					log.error("IOException", new Throwable(e));
					this.dataMap.put("state", false);
					this.dataMap.put("error", "上传文件失败！"+e.getMessage());
					//e.printStackTrace();
				}
			}
		}
		String table = request.getParameter("table");
		DataImport imp = new DataImport();
		int count = 0;
		try {
			//如果是短期信息--车间日计划
			if(table.equals("t_shortmsg")){
				count = imp.importShortmsgLayerData(table,filepath);
			}else{
				count = imp.importLayerData(table,filepath);
			}
			this.dataMap.put("count", count);
		} catch (Exception e) {
			this.dataMap.put("state", false);
			this.dataMap.put("error", "批量导入数据失败！"+e.getMessage());
		}
		return "map";
	}
	
	//上传图层数据平面图
	public String uploadDataImage() throws Exception{
		String newFileName=null;
		String fname = getFileName();
		//得到保存上传文件的真实路径
		int index=uploadifyFileName.lastIndexOf(".");
		//获取文件后缀名
		String filetype = uploadifyFileName.substring(index+1);
		//判断上传文件是否有扩展名,以时间戳作为新的文件名
		if (index==-1) {
			filetype = "png";
		}
		newFileName = "dataimg-"+fname+"-SIZE."+filetype;
		String yearmonth = DateUtil.date2String("yyyyMM");
		String _absolutePath = "dataimg/"+yearmonth+"/"+newFileName;
		String forldPath = WebConfig.getConfig("uploadForld");
		String tempfilepath = forldPath+"/"+_absolutePath;
		log.info("tempfilepath="+tempfilepath);
		File file = new File(tempfilepath);
		//创建文件存储目录
		FileUtils.forceMkdir(file.getParentFile());
		BufferedOutputStream bos=null;
		BufferedInputStream bis=null;
		this.dataMap.put("state", true);
		//读取保存在临时目录下的上传文件,写入到新的文件中
		try {
			String dataid = request.getParameter("id");
			log.info("dataid="+dataid);
			FileInputStream fis=new FileInputStream(uploadify);
			BufferedImage img = ImageIO.read(fis);
			int width = img.getWidth();
			int height = img.getHeight();
			int smallImgWidth = Integer.parseInt(WebConfig.getConfig("dataSmallImgWidth"));
			int smallImgHeight = Integer.parseInt(WebConfig.getConfig("dataSmallImgHeight"));
			log.info("width="+width+",height="+height+",smallImgWidth="+smallImgWidth+",smallImgHeight="+smallImgHeight);
			String sizeinfo = "_"+width+"X"+height;
			String sizeinfo_small = "_"+smallImgWidth+"X"+smallImgHeight; 
			String absolutePath = _absolutePath.replaceAll("-SIZE", sizeinfo);
			String absolutePath_small = _absolutePath.replaceAll("-SIZE", sizeinfo_small);
			String filepath = forldPath+"/"+absolutePath;
			String smallImgFilepath = forldPath+"/"+absolutePath_small;
			JSONObject paramJson = new JSONObject();
			String primaryKey = ParamConfig.getColumnValue("getUid", "");
			paramJson.put("primaryKey", primaryKey);
			paramJson.put("dataid", dataid);
			paramJson.put("name", "未知名称");
			paramJson.put("imgpath", absolutePath);
			paramJson.put("imgwidth", width);
			paramJson.put("imgheight", height);
			paramJson.put("smallimg", absolutePath_small);
			ImageIO.write(img, filetype, new File(filepath));
			ImageUtils.thumbnailsBySize(filepath, smallImgWidth, smallImgHeight, smallImgFilepath);
			QuerySqlConfig config = new QuerySqlConfig();
			String baseSql = config.getConfigSQL("addLayerDataImage");
			String sql = config.getReplaceSqlParam(baseSql, paramJson);
			int ret = dao.saveObject(sql);
			this.dataMap.put("state", true);
			this.dataMap.put("data", ret);
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException", new Throwable(e));
			this.dataMap.put("state", false);
			this.dataMap.put("error", "上传文件失败！找不到该文件！"+e.getMessage());
		} catch (IOException e) {
			log.error("IOException", new Throwable(e));
			this.dataMap.put("state", false);
			this.dataMap.put("error", "上传文件失败！"+e.getMessage());
		}finally{
			if (null!=bis) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null!=bos) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "map";
	}
}

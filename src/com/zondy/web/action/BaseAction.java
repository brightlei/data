package com.zondy.web.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.alibaba.fastjson.JSONObject;
import com.opensymphony.xwork2.ActionSupport;
import com.zondy.base.DAO.BaseDAOImpl;
import com.zondy.listener.ApplicationListener;

@SuppressWarnings("serial")
public class BaseAction extends ActionSupport implements SessionAware,
		ServletRequestAware, ServletResponseAware {
	private static Logger log = Logger.getLogger(BaseAction.class);
	/**
	 * 当需要返回JSON格式数据时，将需要返回到前台的数据加入到dataMap中，
	 * 应用场景： 后台JsonAction中 dataMap.put("data",需要往前台发送的数据)
	 * 则前台获取时  function(data,status) {alert(data.data);//data.data就是后台放入的数据}
	 */
	public Map<String,Object> dataMap = new LinkedHashMap<String,Object>();
	public Map<String, Object> map = new LinkedHashMap<String,Object>();
	public HttpServletRequest request;
	public HttpServletResponse response;
	public BaseDAOImpl dao = ApplicationListener.dao;
	public JSONObject requestParam = new JSONObject();
	public String requestSessionId = "";
	public JSONObject roleInfo = new JSONObject();
	
	@SuppressWarnings("unchecked")
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
		this.requestParam=paramToJson(request.getParameterMap());
		String uri = request.getRequestURI();
		log.info("getRequestURI="+uri);
		requestSessionId = request.getSession().getId();
		log.info("["+requestSessionId+"]requestParam="+requestParam);
		String referer = request.getHeader("referer");
		log.info("["+requestSessionId+"]referer="+referer);
		HashMap<String, String> headinfo = getHeadersInfo();
		log.info("["+requestSessionId+"]headinfo="+headinfo);
		this.dataMap.put("state",true);
		this.dataMap.put("error","");
	}
	
	public void setServletResponse(HttpServletResponse response) {
		this.response=response;
	}

	public void setSession(Map<String, Object> map) {
		this.map=map;
	}


	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}
	
	private HashMap<String, String> getHeadersInfo() {
		HashMap<String, String> map = new LinkedHashMap<String, String>();
	    Enumeration headerNames = request.getHeaderNames();
	    while (headerNames.hasMoreElements()) {
	        String key = (String) headerNames.nextElement();
	        String value = request.getHeader(key);
	        map.put(key, value);
	    }
	    return map;
	  }
	
	public JSONObject paramToJson(Map<String, Object> pMap){
		JSONObject paraJson = new JSONObject();
		Set<?> set = pMap.entrySet();
		Iterator<?> iterator =  set.iterator();
		Entry<String, Object> entry = null;
		while(iterator.hasNext()){
			entry = (Entry<String, Object>)iterator.next();
			String key = entry.getKey().toString();
			String[] value = (String[])entry.getValue();
			paraJson.put(key, value[0]);
		}
		return paraJson;
	}
	
	protected JSONObject getParam() {
		// System.out.println("接收参数");
		JSONObject jsonObject;// 用来接收移动端发送的请求
		InputStream input = null;// 用流来接收请求参数
		BufferedReader br = null;// 使用缓冲区提高效率
		String param = "";// 接收请求
		try {
			input = request.getInputStream();// 获取输入流
			// 用缓冲去包装输入流提高效率以及设置编码，否则客户端发送中文乱码
			br = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			param = br.readLine();// 从输入流中接收参数
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 将String类型请求参数转换成json类型，方便解析
		jsonObject = JSONObject.parseObject(param);
		return jsonObject;
	}
}
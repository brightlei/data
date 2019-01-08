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

package com.zondy.base.DAO;
/**
 * 模块名称：BaseDAOImpl									<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：2014-2-27 下午02:36:18					<br>
 * 初始版本：V1.0	最初版本号							<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：2014-2-27 下午02:36:18					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.alibaba.fastjson.JSONObject;
import com.zondy.listener.ApplicationListener;

/** 统一数据访问接口实现 */
public class BaseDAOImpl extends HibernateDaoSupport implements BaseDAO {
	
	//通过@Resource注解注入HibernateTemplate实例
	@Resource
	HibernateTemplate hibernateTemplate;
	/** 从连接池中取得一个JDBC连接 */
	@SuppressWarnings("deprecation")
	public Connection getConnection() {
		return hibernateTemplate.getSessionFactory().getCurrentSession().connection();
	}
	/** 保存对象返回主键信息 */
	public int saveObject(final String sql) {
		return ((Integer)hibernateTemplate.execute(new HibernateCallback<Object>(){
			public Object doInHibernate(Session session) throws HibernateException{
				Query query = session.createSQLQuery(sql);
				return query.executeUpdate();
			}
		})).intValue();
	}
	
	/** 更新或者删除对象返回信息：1成功,0失败*/
	public int updateObject(final String sql) {
		return ((Integer)hibernateTemplate.execute(new HibernateCallback<Object>(){
			public Object doInHibernate(Session session) throws HibernateException{
				Query query = session.createSQLQuery(sql);
				return query.executeUpdate();
			}
		})).intValue();	
	}

	/* 
	 * 方法描述：请用一句话描述这个方法<br>	
	 * 创建时间：2015-8-26 下午04:19:29<br>
	 * @see com.zondy.base.DAO.BaseDAO#loadObject(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public JSONObject loadObject(final String sql){
		return ((JSONObject)hibernateTemplate.execute(new HibernateCallback<Object>(){
			public Object doInHibernate(Session session) throws HibernateException{
				Query query = session.createSQLQuery(sql);
				query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
				Object object = query.uniqueResult();
				if(object!=null){
					return mapToJson((HashMap<String, Object>)object);
				}else{
					return null;
				}
			}
		}));
	}
	
	/* 
	 * 方法描述：请用一句话描述这个方法<br>	
	 * 创建时间：2015-8-26 下午03:42:39<br>
	 * @see com.zondy.base.DAO.BaseDAO#countAll(java.lang.String)
	 */
	public int countAll(final String sql) {
		return ((Integer)hibernateTemplate.execute(new HibernateCallback<Object>(){
			public Object doInHibernate(Session session) throws HibernateException{
				Query query = session.createSQLQuery(sql);
				return query.uniqueResult();
			}
		})).intValue();
	}
	
	/* 
	 * 方法描述：请用一句话描述这个方法<br>	
	 * 创建时间：2015-8-26 下午03:41:24<br>
	 * @see com.zondy.base.DAO.BaseDAO#listAll(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<JSONObject> listAll(String sql) {
		final String hql1 = sql;
		return hibernateTemplate.executeFind(new HibernateCallback<Object>(){
			public Object doInHibernate(Session session) throws HibernateException{
				Query query = session.createSQLQuery(hql1);
				List<?> list = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
				if (!Hibernate.isInitialized(list))Hibernate.initialize(list);
				List<JSONObject> jsonList = listToJsonArray(list);
				return jsonList;
			}
		});	
	}
	
	/* 
	 * 方法描述：请用一句话描述这个方法<br>	
	 * 创建时间：2015-8-26 下午03:40:39<br>
	 * @see com.zondy.base.DAO.BaseDAO#listAll(java.lang.String, int, int)
	 */
	@SuppressWarnings("unchecked")
	public List<JSONObject> listAll(String sql, int pageNo, int pageSize) {
		final int pNo = pageNo;
		final int pSize = pageSize;
		final String hql1 = sql;
		return hibernateTemplate.executeFind(new HibernateCallback<Object>(){
			public Object doInHibernate(Session session) throws HibernateException{
				Query query = session.createSQLQuery(hql1);
				query.setMaxResults(pSize);
				query.setFirstResult((pNo-1)*pSize);
				List<?> result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
				if (!Hibernate.isInitialized(result))Hibernate.initialize(result);
				List<JSONObject> jsonList = listToJsonArray(result);
				return jsonList;
			}
		});	
	}
	
	/**
	 * 功能描述：将hashmap转换成JSONObject对象<br>
	 * 创建作者：雷志强<br>
	 * 创建时间：Apr 9, 2017 8:28:52 PM<br>
	 * @param pMap map对象
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	private static JSONObject mapToJson(HashMap<String, Object> pMap){
		JSONObject paraJson = new JSONObject();
		if(pMap!=null){
			Set<?> set = pMap.entrySet();
			Iterator<?> iterator =  set.iterator();
			Entry<String, Object> entry = null;
			while(iterator.hasNext()){
				entry = (Entry<String, Object>)iterator.next();
				String key = entry.getKey().toString();
				key = key.toLowerCase();
				String value = "";
				if(entry.getValue()!=null){
					value = entry.getValue().toString();
				}
				paraJson.put(key,value);
			}
		}
		return paraJson;
	}
	/**
	 * 功能描述：将List对象转换成JSONArray对象<br>
	 * 创建作者：雷志强<br>
	 * 创建时间：Apr 9, 2017 8:27:33 PM<br>
	 * @param list List对象
	 * @return List<JSONObject> JSONArray对象
	 */
	@SuppressWarnings("unchecked")
	private static List<JSONObject> listToJsonArray(List<?> list){
		List<JSONObject> dataList = new ArrayList<JSONObject>();
		int count = list.size();
		JSONObject recordJson = null;
		HashMap<String, Object> map = null;
		for(int i=0;i<count;i++){
			recordJson = new JSONObject();
			map = (HashMap<String, Object>)list.get(i);
			recordJson = mapToJson(map);
			dataList.add(recordJson);
		}
		return dataList;
	}
	
	public static void main(String[] args) throws IOException {
		//testExportLayer();
		//testExportDepartment();
		//testExportDic();
		testExportDicdata();
	}
	
	@SuppressWarnings("unchecked")
	public static void testExportLayer() throws IOException{
		String sql = "select * from t_layer";
		BaseDAOImpl dao = (BaseDAOImpl)ApplicationListener.dao;
		List<?> list = dao.listAll(sql);
		System.out.println(list);
		int count = list.size();
		HashMap<String, Object> record = null;
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<count;i++){
			record = (HashMap<String, Object>)list.get(i);
			sb.append("insert into t_layer(id,code,name,pcode,rank,type,iconurl,createtime,edittime,state) values(");
			sb.append("'"+record.get("id").toString()+"',");
			sb.append("'"+record.get("code").toString()+"',");
			sb.append("'"+record.get("name").toString()+"',");
			sb.append("'"+record.get("pcode").toString()+"',");
			sb.append("'"+record.get("rank").toString()+"',");
			sb.append("'"+(String)record.get("type")+"',");
			sb.append("'"+(String)record.get("iconurl")+"',");
			sb.append("'"+record.get("createtime").toString()+"',");
			sb.append("'"+record.get("edittime").toString()+"',");
			sb.append("'"+record.get("state").toString()+"'");
			sb.append(");\r\n");
		}
		FileUtils.writeStringToFile(new File("d:/t_layer.sql"), sb.toString(),"UTF-8");
	}
	
	@SuppressWarnings("unchecked")
	public static void testExportDepartment() throws IOException{
		String sql = "select * from t_department";
		BaseDAOImpl dao = (BaseDAOImpl)ApplicationListener.dao;
		List<?> list = dao.listAll(sql);
		System.out.println(list);
		int count = list.size();
		HashMap<String, Object> record = null;
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<count;i++){
			record = (HashMap<String, Object>)list.get(i);
			sb.append("insert into t_department(id,code,name,pcode,createtime,edittime,state) values(");
			sb.append("'"+record.get("id").toString()+"',");
			sb.append("'"+record.get("code").toString()+"',");
			sb.append("'"+record.get("name").toString()+"',");
			sb.append("'"+record.get("pcode").toString()+"',");
			sb.append("'"+record.get("createtime").toString()+"',");
			sb.append("'"+record.get("edittime").toString()+"',");
			sb.append("'"+record.get("state").toString()+"'");
			sb.append(");\r\n");
		}
		FileUtils.writeStringToFile(new File("d:/t_department.sql"), sb.toString(),"UTF-8");
	}
	
	@SuppressWarnings("unchecked")
	public static void testExportDic() throws IOException{
		String sql = "select * from t_dic";
		BaseDAOImpl dao = (BaseDAOImpl)ApplicationListener.dao;
		List<?> list = dao.listAll(sql);
		System.out.println(list);
		int count = list.size();
		HashMap<String, Object> record = null;
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<count;i++){
			record = (HashMap<String, Object>)list.get(i);
			sb.append("insert into t_dic(id,code,name,createtime,edittime,state) values(");
			sb.append("'"+record.get("id").toString()+"',");
			sb.append("'"+record.get("code").toString()+"',");
			sb.append("'"+record.get("name").toString()+"',");
			sb.append("'"+record.get("createtime").toString()+"',");
			sb.append("'"+record.get("edittime").toString()+"',");
			sb.append("'"+record.get("isdelete").toString()+"'");
			sb.append(");\r\n");
		}
		FileUtils.writeStringToFile(new File("d:/t_dic.sql"), sb.toString(),"UTF-8");
	}
	
	@SuppressWarnings("unchecked")
	public static void testExportDicdata() throws IOException{
		String sql = "select * from t_dicdata";
		BaseDAOImpl dao = (BaseDAOImpl)ApplicationListener.dao;
		List<?> list = dao.listAll(sql);
		System.out.println(list);
		int count = list.size();
		HashMap<String, Object> record = null;
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<count;i++){
			record = (HashMap<String, Object>)list.get(i);
			sb.append("insert into t_dicdata(id,diccode,code,name,rank,createtime,edittime,state) values(");
			sb.append("'"+record.get("id").toString()+"',");
			sb.append("'"+record.get("diccode").toString()+"',");
			sb.append("'"+record.get("code").toString()+"',");
			sb.append("'"+record.get("name").toString()+"',");
			sb.append("'"+record.get("rank").toString()+"',");
			sb.append("'"+record.get("createtime").toString()+"',");
			sb.append("'"+record.get("edittime").toString()+"',");
			sb.append("'"+record.get("state").toString()+"'");
			sb.append(");\r\n");
		}
		FileUtils.writeStringToFile(new File("d:/t_dicdata.sql"), sb.toString(),"UTF-8");
	}
}

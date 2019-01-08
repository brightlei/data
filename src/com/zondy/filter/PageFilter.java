package com.zondy.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PageFilter extends HttpServlet implements Filter {

	private static final long serialVersionUID = 1L;
	private FilterConfig filterConfig = null;
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String excludePage = this.filterConfig.getInitParameter("exclude");
		String[] pages = excludePage.split(";");
		HttpServletRequest req=(HttpServletRequest)request;
		HttpServletResponse res=(HttpServletResponse)response;
		if(req.getSession().getAttribute("user")!=null){
			chain.doFilter(request, response);
		}else{
			String currentUrl = req.getRequestURI();
			if(isMatch(pages, currentUrl) || currentUrl.contains("data/mobile/")){
				chain.doFilter(request, response);
			}else{
				String loginPath=req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()+req.getContextPath()+"/login.html";
				res.getWriter().println("<script type='text/javascript' language='javascript'>window.top.location.href='"+loginPath+"';</script>");
			}
		}
	}
	public void init(FilterConfig config) throws ServletException {
		this.filterConfig = config;
	}
	/**
	 * 判断当前访问页面是不是放行页面
	 * @param pagelist 放行页面字符串
	 * @param currentPage 当前页面
	 * @return
	 */
	public boolean isMatch(String[] pagelist,String currentPage){
		boolean isMatch = false;
		int count = pagelist.length;
		for(int i=0;i<count;i++){
			if(currentPage.contains(pagelist[i])){
				isMatch = true;
				break;
			}
		}
		return isMatch;
	}
}

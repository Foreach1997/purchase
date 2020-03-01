//package com.pu.purchase.util;
//
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.ServletContext;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//
//
///**
// * WEB作用域工具类
// * @author LJH
// *
// */
//public class WebUtils {
//
//	/**
//	 * 得到当前请求对象
//	 * @return
//	 */
//	public static HttpServletRequest getHttpServletRequest() {
//		ServletRequestAttributes previousAttributes = (ServletRequestAttributes)
//				RequestContextHolder.getRequestAttributes();
//		HttpServletRequest request = previousAttributes.getRequest();
//		return request;
//	}
//	/**
//	 * 得到当前请求HttpSession
//	 * @return
//	 */
//	public static HttpSession getHttpSession() {
//		return getHttpServletRequest().getSession();
//	}
//	/**
//	 * 得到当前请求ServletContext
//	 * @return
//	 */
//	public static ServletContext getServletContext() {
//		return getHttpServletRequest().getServletContext();
//	}
//	/**
//	 * 得到当前用户的登陆对象  session.setAttribute("user" user);
//	 */
//	public static User getCurrentUser() {
//		return (User) getHttpSession().getAttribute("user");
//	}
//	/**
//	 * 得到当前登陆的用户名
//	 * @return
//	 */
//	public static String getCurrentUserName() {
//		return getCurrentUser().getUserName();
//	}
//}

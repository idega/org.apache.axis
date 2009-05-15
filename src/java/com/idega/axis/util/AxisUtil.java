/*
 * $Id: AxisUtil.java,v 1.4 2009/05/15 07:29:34 valdas Exp $ Created on Aug 9, 2005
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.axis.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOService;
import com.idega.business.IBOSession;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.IWUserContextImpl;

/**
 * A helper class for axis webservices in IdegaWeb applications.
 * 
 * Last modified: $Date: 2009/05/15 07:29:34 $ by $Author: valdas $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.4 $
 */
public class AxisUtil {

	/**
	 * Returns an initialized IWUserContext
	 * 
	 * @return IWUserContext
	 */
	public static IWUserContext getIWUserContext() {
		HttpSession session = getHttpSession();
		ServletContext servletContext = getServletContext();
		if (session != null & servletContext != null) {
			return new IWUserContextImpl(getHttpSession(), getServletContext());
		}
		return null;
	}
	
	/**
	 * Returns an initialized IWApplicationContext
	 * 
	 * @return IWApplicationContext
	 */
	public static IWApplicationContext getIWApplicationContext() {
		return IWMainApplication.getDefaultIWApplicationContext();
	}
	

	public static HttpServletRequest getHttpServletRequest() {
		MessageContext axisContext = MessageContext.getCurrentContext();
		if (axisContext != null) {
			return ((HttpServletRequest) axisContext.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST));
		}
		return null;
	}

	public static HttpServletResponse getHttpServletResponse() {
		MessageContext axisContext = MessageContext.getCurrentContext();
		if (axisContext != null) {
			return ((HttpServletResponse) axisContext.getProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE));
		}
		return null;
	}

	public static ServletContext getServletContext() {
		MessageContext axisContext = MessageContext.getCurrentContext();
		if (axisContext != null) {
			HttpServlet servlet = (HttpServlet) axisContext.getProperty(HTTPConstants.MC_HTTP_SERVLET);
			return servlet.getServletContext();
		}
		return null;
	}

	public static HttpSession getHttpSession() {
		HttpServletRequest request = getHttpServletRequest();
		if (request != null) {
			return request.getSession();
		}
		return null;
	}
	
	public static <T> T getServiceBean(Class<? extends IBOService> serviceBeanInterface) throws IBOLookupException{
		return IBOLookup.getServiceInstance(getIWApplicationContext(),serviceBeanInterface);
	}
	
	public static <T> T getSessionBean(Class<? extends IBOSession> sessionBeanInterface) throws IBOLookupException{
		return IBOLookup.getSessionInstance(getIWUserContext(),sessionBeanInterface);
	}
	
}
/*
 * $Id: AxisExtendedServlet.java,v 1.1 2006/02/06 12:27:56 tryggvil Exp $
 * Created on 6.2.2006 in project org.apache.axis
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.axis.servlet;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.transport.http.AxisServlet;

import com.idega.axis.deployment.WSDDAutoDeployer;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWModuleLoader;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.StringUtil;


/**
 * <p>
 * Extended version of the standard AxisServlet to make auto-deployment of wsdd
 * files within jar-files possible.
 * </p>
 *  Last modified: $Date: 2006/02/06 12:27:56 $ by $Author: tryggvil $
 *
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class AxisExtendedServlet extends AxisServlet {

	private static final long serialVersionUID = -5586830560973425845L;

	public AxisExtendedServlet() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.apache.axis.transport.http.AxisServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		super.doGet(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.axis.transport.http.AxisServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void doPost(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		super.doPost(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.axis.transport.http.AxisServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		super.init();

		autoDeployServices();
	}


	public void autoDeployServices(){
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(getServletContext());

		IWModuleLoader loader = new IWModuleLoader(iwma, getServletContext());

		WSDDAutoDeployer deployer = new WSDDAutoDeployer();
		try {
			deployer.setEngine(getEngine());
		}
		catch (AxisFault e) {
			e.printStackTrace();
		}
		loader.getJarLoaders().add(deployer);
		loader.loadBundlesFromJars();

		IWContext iwc = CoreUtil.getIWContext();
 		String serviceURL = iwc == null ? null : iwc.getServerURL();
 		serviceURL = serviceURL == null ? IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty(IWMainApplication.PROPERTY_DEFAULT_SERVICE_URL) : serviceURL;

		if (!StringUtil.isEmpty(serviceURL)) {
			if (!serviceURL.endsWith(CoreConstants.SLASH)) {
				serviceURL = serviceURL.concat(CoreConstants.SLASH);
			}
			if (!serviceURL.endsWith("services/")) {
				serviceURL = serviceURL.concat("services/");
			}

			Iterator<?> i= null;
	        try {
	            i = getEngine().getConfig().getDeployedServices();
	        } catch (ConfigurationException configException) {
	        	configException.printStackTrace();
	        } catch (AxisFault e) {
				e.printStackTrace();
			}

	        if (i != null){
		        while (i.hasNext()) {
		            ServiceDesc sd = (ServiceDesc) i.next();
		            sd.setEndpointURL(serviceURL);
		        }
	        }
		}

	}
}
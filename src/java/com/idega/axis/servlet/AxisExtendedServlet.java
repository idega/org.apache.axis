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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.axis.AxisFault;
import org.apache.axis.transport.http.AxisServlet;
import com.idega.axis.deployment.WSDDAutoDeployer;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWModuleLoader;


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

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -5586830560973425845L;


	/**
	 * 
	 */
	public AxisExtendedServlet() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see org.apache.axis.transport.http.AxisServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.axis.transport.http.AxisServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.axis.transport.http.AxisServlet#init()
	 */
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		autoDeployServices();
	}

	
	public void autoDeployServices(){
	
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(getServletContext());
		
		IWModuleLoader loader = new IWModuleLoader(iwma,getServletContext());
		
		WSDDAutoDeployer deployer = new WSDDAutoDeployer();
		try {
			deployer.setEngine(getEngine());
		}
		catch (AxisFault e) {
			e.printStackTrace();
		}
		loader.getJarLoaders().add(deployer);
		loader.loadBundlesFromJars();
		
	}
	
	
}

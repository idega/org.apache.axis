/*
 * $Id: WSDDAutoDeployer.java,v 1.2 2006/04/09 11:58:12 laddi Exp $
 * Created on 5.2.2006 in project org.apache.axis
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.axis.deployment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import com.idega.idegaweb.JarLoader;


/**
 * <p>
 * Implementation of JarLoader to automatically load an Axis .wsdd deployment descriptor
 * and deploy in an idegaWeb application.
 * </p>
 *  Last modified: $Date: 2006/04/09 11:58:12 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class WSDDAutoDeployer implements JarLoader{

	private static Logger log = Logger.getLogger(WSDDAutoDeployer.class.getName());
	
	private AxisEngine engine;
	
	/**
	 * 
	 */
	public WSDDAutoDeployer() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.JarLoader#loadJar(java.io.File, java.util.jar.JarFile, java.lang.String)
	 */
	public void loadJar(File bundleJarFile, JarFile jarFile, String jarPath) {
		Enumeration entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = (JarEntry) entries.nextElement();
			//JarFileEntry entryName = (JarFileEntry)entries.nextElement();
			String entryName = entry.getName();
			if(entryName.endsWith("deploy.wsdd")){
				try {
					//JarEntry entry = jarFile.getJarEntry(entryName);
					//if(entryName.equals("org/apache/axis/client/client-config.wsdd")||entryName.equals("org/apache/axis/server/server-config.wsdd")){
						//not handle
					//}
					//else{
					if(!entryName.endsWith("undeploy.wsdd")){
						log.info("Found webservice deployment file and autodeploying: "+entryName);
						InputStream stream = jarFile.getInputStream(entry);
						processWSDD(stream);
					}
					//}
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	

	public void processWSDD(InputStream stream) throws ParserConfigurationException, SAXException, IOException{
        DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(stream);
        processWSDD(document);
	}
	
	
	public void processWSDD(Document document){
		
		Element rootElement = document.getDocumentElement();
		AxisEngine engine = getEngine();
		try {
			processWSDD(null,engine,rootElement);
		}
		catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Method copied from org.apache.axis.utils.Admin because method is protected in that class
    protected static Document processWSDD(MessageContext msgContext, AxisEngine engine, Element root)
    throws Exception
{
    Document doc = null;
    String action = root.getLocalName();
    if(action.equals("passwd"))
    {
        String newPassword = root.getFirstChild().getNodeValue();
        engine.setAdminPassword(newPassword);
        doc = XMLUtils.newDocument();
        doc.appendChild(root = doc.createElementNS("", "Admin"));
        root.appendChild(doc.createTextNode(Messages.getMessage("done00")));
        return doc;
    }
    if(action.equals("quit"))
    {
        log.severe(Messages.getMessage("quitRequest00"));
        if(msgContext != null) {
					msgContext.setProperty("quit.requested", "true");
				}
        doc = XMLUtils.newDocument();
        doc.appendChild(root = doc.createElementNS("", "Admin"));
        root.appendChild(doc.createTextNode(Messages.getMessage("quit00", "")));
        return doc;
    }
    if(action.equals("list")){
        //return listConfig(engine);
    }
    if(action.equals("clientdeploy")) {
			engine = engine.getClientEngine();
		}
    WSDDDocument wsddDoc = new WSDDDocument(root);
    org.apache.axis.EngineConfiguration config = engine.getConfig();
    if(config instanceof WSDDEngineConfiguration)
    {
        WSDDDeployment deployment = ((WSDDEngineConfiguration)config).getDeployment();
        wsddDoc.deploy(deployment);
    }
    engine.refreshGlobalOptions();
    engine.saveConfiguration();
    doc = XMLUtils.newDocument();
    doc.appendChild(root = doc.createElementNS("", "Admin"));
    root.appendChild(doc.createTextNode(Messages.getMessage("done00")));
    return doc;
}

	
	/**
	 * @return Returns the engine.
	 */
	public AxisEngine getEngine() {
		if(this.engine==null){
			Map environment = System.getProperties();
			AxisServer axisServer;
			try {
				axisServer = AxisServer.getServer(environment);
				this.engine = axisServer;
			}
			catch (AxisFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.engine;
	}

	
	/**
	 * @param engine The engine to set.
	 */
	public void setEngine(AxisEngine engine) {
		this.engine = engine;
	}

}

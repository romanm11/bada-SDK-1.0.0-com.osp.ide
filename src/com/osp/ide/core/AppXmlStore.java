package com.osp.ide.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.osp.ide.IConstants;


public class AppXmlStore {

	public static final String ELEMENT_APP = "Application"; //$NON-NLS-1$
	
	public static final String ATTR_ENTRY = "Entry"; //$NON-NLS-1$
	public static final String ELEMENT_NAME = "Name"; //$NON-NLS-1$
	public static final String ATTR_VENDOR = "Vendor"; //$NON-NLS-1$
	public static final String ATTR_DESC = "Description"; //$NON-NLS-1$
//	private static final String ATTR_SDK = "sdk"; //$NON-NLS-1$
	
	public static final String ELEMENT_ICON = "Icons"; //$NON-NLS-1$
	public static final String ATTR_MAINMENU = "MainMenu"; //$NON-NLS-1$
	public static final String ATTR_SETTING = "Setting"; //$NON-NLS-1$
	public static final String ATTR_TICKER = "Ticker"; //$NON-NLS-1$
	public static final String ATTR_QUICKPANEL = "QuickPanel"; //$NON-NLS-1$
	public static final String ATTR_LAUNCHIMAGE = "LaunchImage"; //$NON-NLS-1$
	
	public static final String ELEMENT_AUTOSCALING = "AutoScaling"; //$NON-NLS-1$
	public static final String ATTR_ENABLED = "Enabled"; //$NON-NLS-1$
	public static final String ATTR_BASE_RESOLUTION = "BaseResolution"; //$NON-NLS-1$	

	String ba_entry="";
	java.util.List <AppNameItem> nameList;
	String ba_vendor="";
	String ba_desc="";
	
	String iconMainMenu="";
	String iconSetting="";
	String iconTicker="";
	String iconQuickPanel="";
	String iconLaunchImage="";
	
	boolean bAutoScalingEnabled=false;
	String baseResolution="";
	
	
//	String ba_sdk="";
	
	private IFile file=null;
	
	public AppXmlStore(IFile path)
	{
		nameList = new ArrayList<AppNameItem>();
		file = path;
		//clear();
	}
	
	public boolean loadXML()
	{
		return loadXML(file);
	}
	
	public boolean loadXML(IFile path)
	{
		clear();
		if(path == null || !path.exists()) return false;
		
		try {
			InputStream stream=path.getContents();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		    DocumentBuilder  db = dbf.newDocumentBuilder();
		    Document dom = db.parse(stream);
		    
		    Element root = dom.getDocumentElement(); 
		    String rootName = root.getNodeName();
		    if( rootName.equals(ELEMENT_APP))
		    {
		    	for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
		    		String name = node.getNodeName();
		    		if( node.getFirstChild() != null)
		    		{
		    			String value = node.getFirstChild().getNodeValue();
		    			if( name.equals(ATTR_ENTRY)) ba_entry = value;
		    			else if( name.equals(ELEMENT_NAME))
		    			{
		    		    	for (Node nodeName = node.getFirstChild(); nodeName != null; nodeName = nodeName.getNextSibling()) {
		    		    		String nameName = nodeName.getNodeName();
		    		    		if( nodeName.getFirstChild() != null)
		    		    		{
		    		    			String valueName = nodeName.getFirstChild().getNodeValue();
		    		    			AppNameItem item = new AppNameItem(nameName, valueName);
		    		    			nameList.add(item);
		    		    		}
		    		    	}

		    			}
		    			else if( name.equals(ATTR_VENDOR)) ba_vendor = value;
		    			else if( name.equals(ATTR_DESC)) ba_desc = value;
		    			else if( name.equals(ELEMENT_ICON))
		    			{
		    		    	for (Node nodeIcon = node.getFirstChild(); nodeIcon != null; nodeIcon = nodeIcon.getNextSibling()) {
		    		    		String nameIcon = nodeIcon.getNodeName();
		    		    		if( nodeIcon.getFirstChild() != null)
		    		    		{
		    		    			String valueIcon = nodeIcon.getFirstChild().getNodeValue();
		    		    			if( nameIcon.equals(ATTR_MAINMENU)) iconMainMenu = valueIcon;
		    		    			else if( nameIcon.equals(ATTR_SETTING)) iconSetting = valueIcon;
		    		    			else if( nameIcon.equals(ATTR_TICKER)) iconTicker = valueIcon;
		    		    			else if( nameIcon.equals(ATTR_QUICKPANEL)) iconQuickPanel = valueIcon;
		    		    			else if( nameIcon.equals(ATTR_LAUNCHIMAGE)) iconLaunchImage = valueIcon;
		    		    		}
		    		    	}
		    			}
		    			else if( name.equals(ELEMENT_AUTOSCALING))
		    			{
		    		    	for (Node nodeAutoScaling = node.getFirstChild(); nodeAutoScaling != null; nodeAutoScaling = nodeAutoScaling.getNextSibling()) {
		    		    		String nameAutoScaling = nodeAutoScaling.getNodeName();
		    		    		if( nodeAutoScaling.getFirstChild() != null)
		    		    		{
		    		    			String valueAutoScaling = nodeAutoScaling.getFirstChild().getNodeValue();
		    		    			if( nameAutoScaling.equals(ATTR_ENABLED))
		    		    			{
		    		    				
		    		    				try {		    				
		    		    					bAutoScalingEnabled = Boolean.parseBoolean(valueAutoScaling);
		    		    				}
		    		    				catch (NumberFormatException e) 
		    		    				{ 
		    		    					bAutoScalingEnabled = false;
		    		    				}		    		    				
		    		    			}
		    		    			else if( nameAutoScaling.equals(ATTR_BASE_RESOLUTION)) baseResolution = valueAutoScaling;
		    		    		}
		    		    	}
		    			}
//		    			else if( name.equals(ATTR_SDK)) ba_sdk = value;
		    		}
		    		
		    	}
		    }
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return true;
	}
	
	private void appendChild(Document dom, Element parent, String name, String value)
	{
		Element element = dom.createElement(name);
		Text textNode = dom.createTextNode(value);
		element.appendChild(textNode);	
		
		parent.appendChild(element);		
	}
	
	public boolean storeXML()
	{
		if(file== null ) return false;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			//get an instance of builder
			DocumentBuilder db = dbf.newDocumentBuilder();
	
			//create an instance of DOM
			Document dom = db.newDocument();
			
			Element rootEle = dom.createElement(ELEMENT_APP);
			dom.appendChild(rootEle);
			
			appendChild(dom, rootEle, ATTR_ENTRY, ba_entry);
			Element nameEle = dom.createElement(ELEMENT_NAME);
			rootEle.appendChild(nameEle);
			

			Iterator<AppNameItem> itName = nameList.iterator();
			while (itName.hasNext()) {
				AppNameItem item = itName.next();
				appendChild(dom, nameEle, item.getLanugage(), item.getName());	
			}			
			
//			appendChild(dom, rootEle, ATTR_NAME, ba_name);
			appendChild(dom, rootEle, ATTR_VENDOR, ba_vendor);
			appendChild(dom, rootEle, ATTR_DESC, ba_desc);
//			appendChild(dom, rootEle, ATTR_SDK, ba_sdk);
			
			Element iconEle = dom.createElement(ELEMENT_ICON);
			rootEle.appendChild(iconEle);
			appendChild(dom, iconEle, ATTR_MAINMENU, iconMainMenu);
			appendChild(dom, iconEle, ATTR_SETTING, iconSetting);
			appendChild(dom, iconEle, ATTR_TICKER, iconTicker);
			appendChild(dom, iconEle, ATTR_QUICKPANEL, iconQuickPanel);
			appendChild(dom, iconEle, ATTR_LAUNCHIMAGE, iconLaunchImage);
			
			
			
			Element scalEle = dom.createElement(ELEMENT_AUTOSCALING);
			rootEle.appendChild(scalEle);
			appendChild(dom, scalEle, ATTR_ENABLED, Boolean.toString(bAutoScalingEnabled));
			if( bAutoScalingEnabled )
			{
				appendChild(dom, scalEle, ATTR_BASE_RESOLUTION, baseResolution);	
			}
			
			
			String buff = write(dom);		
			if( buff == null ) return false;
			
			if (file.exists()) {
				try {
					//file.setReadOnly(false);
					ResourceAttributes attributes = file.getResourceAttributes();
					if(attributes != null) {
						attributes.setReadOnly(false);
						file.setResourceAttributes(attributes);
					}
				
					file.setContents(new ByteArrayInputStream(buff.getBytes("UTF-8")), IResource.FORCE, new NullProgressMonitor()); //$NON-NLS-1$
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			else {
				file.create(new ByteArrayInputStream(buff.getBytes("UTF-8")), IResource.FORCE, new NullProgressMonitor()); //$NON-NLS-1$				
			}
		
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} 	
		return true;
	}
	

	 private String write(Document dom) {
		 TransformerFactory factory = TransformerFactory.newInstance();
		 //factory.setAttribute("indent-number", new Integer(4));
		 factory.setAttribute("indent-number", Integer.valueOf(4) );
  
		 StringWriter sw = null;
		 String resStr=null;
		 try {
			 sw = new StringWriter();
			 Transformer transformer = factory.newTransformer();
   
			 transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			 transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
			 transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			 transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(4));
	
			 transformer.transform(new DOMSource(dom), new StreamResult(sw));
			 
			 resStr = sw.getBuffer().toString();

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			
		} catch (TransformerException e) {
			e.printStackTrace();
		} finally {
			if( sw != null)
				try {
					sw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
		}
		  
		return resStr;
	 }
	 
	public void clear()
	{
		ba_entry="";
		nameList.clear();
		ba_vendor="";
		ba_desc="";
		
		iconMainMenu="";
		iconSetting="";
		iconTicker="";
		iconQuickPanel="";
		iconLaunchImage="";
		
		bAutoScalingEnabled = false;
		baseResolution="";
		
//		ba_sdk="";
	}
	
	
	public void setEntry(String entry)
	{
		ba_entry = entry;
	}
	
	public String getEntry()
	{
		return ba_entry;
	}	
	
	public void setDefaultName(String name)
	{
		nameList.clear();

		ArrayList<LanguageData> list = LanguageListXmlStore.getInstance().getDefaultLanguages();  
		Iterator<LanguageData> itList = list.iterator();
		while (itList.hasNext()) {
			LanguageData item = itList.next();
			nameList.add(new AppNameItem(item.getId(), name));
		}		
//		LanguageData lang = LanguageListXmlStore.getInstance().getDefaultLanguages();
//		nameList.add(new AppNameItem(lang.getId(), name));
	}
	
	public java.util.List <AppNameItem> getNameList()
	{
		return nameList;
	}
	
	public java.util.List <AppNameItem> getNameListClone()
	{
		java.util.List <AppNameItem> nameListClone = new ArrayList<AppNameItem>();
		
		Iterator<AppNameItem> itName = nameList.iterator();
		while (itName.hasNext()) {
			AppNameItem item = itName.next();
			nameListClone.add(item.clone());
		}
		return nameListClone;
	}
	
	
	public void setNameList(java.util.List <AppNameItem> list)
	{
		nameList.clear();
		
		Iterator<AppNameItem> itName = list.iterator();
		while (itName.hasNext()) {
			AppNameItem item = itName.next();
			nameList.add(item.clone());
		}
	}
	
	public void setBaseResolution(String res)
	{
		baseResolution = res;
	}
	
	public String getBaseResolution()
	{
		return baseResolution;
	}	

	public void setAutoScalingEnabled(boolean bEnable)
	{
		bAutoScalingEnabled = bEnable;
	}
	
	public boolean getAutoScalingEnabled()
	{
		return bAutoScalingEnabled;
	}
	
	public void setIconMainMenu(String icon)
	{
		iconMainMenu = icon;
	}
	
	public String getIconMainMenu()
	{
		return iconMainMenu;
	}	

	public void setIconSetting(String icon)
	{
		iconSetting = icon;
	}
	
	public String getIconSetting()
	{
		return iconSetting;
	}	

	
	public void setIconTicker(String icon)
	{
		iconTicker = icon;
	}
	
	public String getIconTicker()
	{
		return iconTicker;
	}		
	
	public void setIconQuickPanel(String icon)
	{
		iconQuickPanel = icon;
	}
	
	public String getIconQuickPanel()
	{
		return iconQuickPanel;
	}	
	
	public void setIconLaunchImage(String icon)
	{
		iconLaunchImage = icon;
	}
	
	public String getIconLaunchImage()
	{
		return iconLaunchImage;
	}	
	
	public void setVendor(String vendor)
	{
		ba_vendor = vendor;
	}
	
	public String getVendor()
	{
		return ba_vendor;
	}	
	
	public void setDescription(String description)
	{
		ba_desc = description;
	}
	
	public String getDescription()
	{
		return ba_desc;
	}	
	
//	public void setSdkPath(String sdk)
//	{
//		ba_sdk = sdk;
//	}
//	
//	public String getSdkPath()
//	{
//		return ba_sdk;
//	}
}

package com.osp.ide.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.osp.ide.IConstants;


public class OspPropertyStore {

	private static final String PT_OUT_DIR_PREF="PT Output ";
	private static final String PT_OUT_DIR_DEFAULT="${project_loc}\\";

	private static final String PT_CERT_FILE_PREF="PT CertFile ";
	private static final String PT_CERT_FILE_DEFAULT="${project_loc}\\";
	private static final String PT_CERT_FILE_DEFAULTFILE="\\cert.cer";
	
	private static final String PT_CMD_ARGS_PREF="PT CmdArgs ";
	private static final String PT_CMD_ARGS_DEFAULT="";
	
	
	private static final String ELEMENT_NAME = "bada"; //$NON-NLS-1$
	
	private static final String ATTR_PRJTYPE = "type"; //$NON-NLS-1$
	private static final String ATTR_SDK = "sdk"; //$NON-NLS-1$
	private static final String ATTR_MODEL = "model"; //$NON-NLS-1$
	
	private static final String NODE_PROPERTIES = "properties"; //$NON-NLS-1$
	private static final String NODE_PROP_DATA = "data"; //$NON-NLS-1$
	private static final String NODE_PROP_DATA_KEY = "key"; //$NON-NLS-1$
	private static final String NODE_PROP_DATA_VALUE = "value"; //$NON-NLS-1$
	
	
	int fPrjType = IConstants.PRJ_TYPE_APP_FRAME;
	String fSdkPath = "";
	String fModel = "";
	
	private static final String BOOL_TRUE="true";
	private static final String BOOL_FALSE="false";	
	
	private IProject project=null;
	
	Map<String, String> properties = new HashMap<String, String>();
	
	public OspPropertyStore(IProject project)
	{
		load(project);
	}
	
	private boolean load(IProject project)
	{
		this.project = project;
		
		clear();
		
		String file = null;
		InputStream stream = null;
		
		IFile path = project.getFile(IConstants.CONFIGSTORE_FILE);
		if(path == null || !path.exists())
		{
			file = project.getLocation().append(IConstants.CONFIGSTORE_FILE).toString();
			path = null;
			
			if( !(new File(file)).exists() ) return false;
		}
		
		try {
			if( file == null )
			{
				if( path == null ) return false;
				stream  = path.getContents();
			}
			else
			{
				stream = new FileInputStream(file);
			}
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		    DocumentBuilder  db = dbf.newDocumentBuilder();
		    Document dom = db.parse(stream);
		    
		    Element root = dom.getDocumentElement(); 
		    String rootName = root.getNodeName();
		    if( rootName.equals(ELEMENT_NAME))
		    {
		    	for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
		    		String name = node.getNodeName();
		    		if( node.getFirstChild() != null)
		    		{
		    			String value = node.getFirstChild().getNodeValue();
		    			if( name.equals(ATTR_SDK))
		    			{
		    				fSdkPath = value;
		    			}
		    			else if( name.equals(ATTR_MODEL))
		    			{
		    				fModel = value;
		    			}
		    			else if( name.equals(ATTR_PRJTYPE))
		    			{
		    				try {		    				
		    					fPrjType = Integer.parseInt(value);
		    				}
		    				catch (NumberFormatException e) 
		    				{ 
		    					fPrjType = IConstants.PRJ_TYPE_APP_FORM;
		    				}	
		    					
		    			}
		    			else if( name.equals(NODE_PROPERTIES))
		    			{
		    				NodeList list = node.getChildNodes();
		    				int length = list.getLength();
		    				for (int i = 0; i < length; ++i) {
		    					Node nodeProf = list.item(i);
		    					short type = nodeProf.getNodeType();
		    					if (type == Node.ELEMENT_NODE) {
		    						Element entry = (Element) nodeProf;
		    						if(entry.getNodeName().startsWith(NODE_PROP_DATA))
		    						{
			    		    			String profName = entry.getAttribute(NODE_PROP_DATA_KEY);
			    		    			String profValue = entry.getAttribute(NODE_PROP_DATA_VALUE);
			    		    			properties.put(profName, profValue);
		    							
		    							
		    						}
		    					}	
		    				}
		    			}
		    		}
		    	}
		    }
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		} catch (FileNotFoundException e) {
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
		} finally {
			if( stream != null )
				try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
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
	
	public boolean store()
	{
		if(project== null ) return false;
		IFile path = project.getFile(IConstants.CONFIGSTORE_FILE);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			//get an instance of builder
			DocumentBuilder db = dbf.newDocumentBuilder();
	
			//create an instance of DOM
			Document dom = db.newDocument();
			
			Element rootEle = dom.createElement(ELEMENT_NAME);
			dom.appendChild(rootEle);
			
			appendChild(dom, rootEle, ATTR_PRJTYPE, Integer.toString(fPrjType));
			appendChild(dom, rootEle, ATTR_SDK, fSdkPath);
			appendChild(dom, rootEle, ATTR_MODEL, fModel);
			

			Element nodeProperties = dom.createElement(NODE_PROPERTIES);
			rootEle.appendChild(nodeProperties);
			
			if( !properties.isEmpty() )
			{
				Iterator iterator = properties.entrySet().iterator();
				int index = 0;
				while (iterator.hasNext()) {
					Map.Entry entry = (Map.Entry)iterator.next();
					String keyName = (String)entry.getKey();
					String value = (String)entry.getValue();
					
					Element elData= dom.createElement(NODE_PROP_DATA + Integer.toString(index)); //$NON-NLS-1$
					elData.setAttribute(NODE_PROP_DATA_KEY, keyName);
					elData.setAttribute(NODE_PROP_DATA_VALUE, value);

					nodeProperties.appendChild(elData);
					index++;
				}
			}
			
			String buff = write(dom);		
			if( buff == null ) return false;
			
			if (path.exists()) {
				try {
					//path.setReadOnly(false);
					ResourceAttributes attributes = path.getResourceAttributes();
					if(attributes != null) {
						attributes.setReadOnly(false);
						attributes.setHidden(false);
						path.setResourceAttributes(attributes);
					}
					
					path.setContents(new ByteArrayInputStream(buff.getBytes("UTF-8")), IResource.FORCE, new NullProgressMonitor()); //$NON-NLS-1$
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			else {
				path.create(new ByteArrayInputStream(buff.getBytes("UTF-8")), IResource.FORCE, new NullProgressMonitor()); //$NON-NLS-1$				
			}
		
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		} finally {
		} 	
		return true;
	}
	

	 private String write(Document dom) {
		 TransformerFactory factory = TransformerFactory.newInstance();
		 //factory.setAttribute("indent-number", new Integer(4));
		 factory.setAttribute("indent-number", Integer.valueOf(4));
  
		 StringWriter sw = null;
		 String retStr= null;
		 try {
			 sw = new StringWriter();
			 Transformer transformer = factory.newTransformer();
   
			 transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			 transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
			 transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			 transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(4));
	
			 transformer.transform(new DOMSource(dom), new StreamResult(sw));
   
			 retStr = sw.getBuffer().toString();

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			
		} catch (TransformerException e) {
			e.printStackTrace();
		} finally {
			if( sw != null )
				try {
					sw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		  
		return retStr;
	 }
	 
	public void clear()
	{
		fPrjType = IConstants.PRJ_TYPE_APP_FRAME;
		fSdkPath = "";
		fModel = "";
		
		properties.clear();
	}
	
	public String getSdkPath() {
		return fSdkPath;
	}

	public void setSdkPath(String path) {
		fSdkPath = path;
	}
	
	public String getModel() {
		return fModel;
	}

	public void setModel(String model) {
		fModel = model;
	}
	
	
	public void setProjectType(int type)
	{
		fPrjType = type;
	}
	
	public int getProjectType()
	{
		return fPrjType;
	}
	
	
	public String getPropertyString(String name)
	{
		return properties.get(name);
	}
	
	public void setPropertyString(String name, String value)
	{
		properties.put(name, value);
	}
	
	
	public boolean getPropertyBool(String name)
	{
		String value= properties.get(name);
		if( value == null || value.equals(BOOL_FALSE)) return false;
		
		return true;
	}
	
	public void setPropertyBool(String name, boolean value)
	{
		String valueStr = (value) ? BOOL_TRUE : BOOL_FALSE;
		
		properties.put(name, valueStr);
	}

	
	// Packing Tool Output Directory
	static public String getKeyPackagingToolOutputDir(String cfgName)
	{
		return PT_OUT_DIR_PREF+cfgName;
	}
	
	static public String getPackagingToolOutputDirDefault(String cfgName)
	{
		return PT_OUT_DIR_DEFAULT + "." + cfgName;
	}

	public String getPackagingToolOutputDir(String cfgName)
	{
		if( cfgName == null || cfgName.length() == 0 ) return "";
		
		String name = getKeyPackagingToolOutputDir(cfgName);
		
		String value = getPropertyString(name);
		
		// return default
		if( value == null ) return getPackagingToolOutputDirDefault(cfgName);
			
		return value;	
	}
	
	public void setPackagingToolOutputDir(String cfgName, String value)
	{
		if( cfgName == null || cfgName.length() == 0 ) return ;
		
		setPropertyString(getKeyPackagingToolOutputDir(cfgName), value);
	}
	
	// Packing Tool Certification File
	static public String getPackagingToolCertFileDefault(String cfgName)
	{
		return PT_CERT_FILE_DEFAULT+"."+cfgName+PT_CERT_FILE_DEFAULTFILE;
	}
	
	static public String getKeyPackagingToolCertFile(String cfgName)
	{
		return PT_CERT_FILE_PREF+cfgName;
	}	

	public String getPackagingToolCertFile(String cfgName)
	{
		if( cfgName == null || cfgName.length() == 0 ) return "";
		
		String name = getKeyPackagingToolCertFile(cfgName);
		
		String value = getPropertyString(name);
		
		// return default
		if( value == null ) return getPackagingToolCertFileDefault(cfgName);
			
		return value;	
	}
	
	public void setPackagingToolCertFile( String cfgName, String value)
	{
		if(cfgName == null || cfgName.length() == 0 ) return ;
		
		setPropertyString(getKeyPackagingToolCertFile(cfgName), value);
	}	
	
	// Packing Tool Command Line Arguments	
	static public String getKeyPackagingToolCmdArgs(String cfgName)
	{
		return PT_CMD_ARGS_PREF+cfgName;
	}	
	
	static public String getPackagingToolCmdArgsDefault()
	{
		return PT_CMD_ARGS_DEFAULT;
	}	
	
	public String getPackagingToolCmdArgs(String cfgName)
	{
		if( cfgName == null || cfgName.length() == 0 ) return "";
		
		String name = getKeyPackagingToolCmdArgs(cfgName);
		
		String value = getPropertyString(name);
		
		// return default
		if( value == null ) return getPackagingToolCmdArgsDefault();
			
		return value;	
	}
	
	public void setPackagingToolCmdArgs(String cfgName, String value)
	{
		if( cfgName == null || cfgName.length() == 0 ) return ;
		
		setPropertyString(getKeyPackagingToolCmdArgs(cfgName), value);
	}		
}

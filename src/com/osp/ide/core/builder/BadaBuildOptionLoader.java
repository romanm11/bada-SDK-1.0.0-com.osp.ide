package com.osp.ide.core.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.osp.ide.IConstants;
import com.osp.ide.utils.WorkspaceUtils;


public class BadaBuildOptionLoader {
	// use manifest.xml instead of the directory name 
	public static final String ATTR_USEMANIFEST = "use_manifest";	
	public static boolean useManifest()
	{
		boolean b = false;
		
		String ideInstalledPath = WorkspaceUtils.getIdeInstalledPath();		
		if(ideInstalledPath == null || ideInstalledPath.length()==0) return b;

		if( !ideInstalledPath.endsWith(IConstants.FILE_SEP_BSLASH)) ideInstalledPath += IConstants.FILE_SEP_BSLASH;
		String path = ideInstalledPath + OPTION_FILENAME;

		FileInputStream stream = null;
		try {
			File file = new File(path);
			if (!file.exists()) return b;

			stream = new FileInputStream(file);
			
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
		    			if( name.equals(ATTR_USEMANIFEST))
		    			{
		    				String value = node.getFirstChild().getNodeValue();
		    				if (value.toLowerCase().equals("yes")) 
		    					return true;		    						    				
		    			}
		    		}
		    	}
		    }
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
	
		return b;
	}
	
	public static final String ELEMENT_NAME = "buildOption"; //$NON-NLS-1$
	public static final String ATTR_MODEL = "model"; //$NON-NLS-1$
	
	public static final String ATTR_NAME = "name"; //$NON-NLS-1$
	public static final String ATTR_COMP = "comp"; //$NON-NLS-1$
	public static final String ATTR_LINK = "link"; //$NON-NLS-1$
	public static final String ATTR_POSTFIX_LINK = "postfix_link"; //$NON-NLS-1$
	
	public static final String ATTR_LINK_FILTER = "link_filter"; //$NON-NLS-1$
	
	public static final String OPTION_FILENAME = "buildoptions.xml"; //$NON-NLS-1$
	

	public static Map load()
	{
		Map optionList = new HashMap();
		
		String ideInstalledPath = WorkspaceUtils.getIdeInstalledPath();		
		if(ideInstalledPath == null || ideInstalledPath.length()==0) return optionList;

		if( !ideInstalledPath.endsWith(IConstants.FILE_SEP_BSLASH)) ideInstalledPath += IConstants.FILE_SEP_BSLASH;
		String path = ideInstalledPath + OPTION_FILENAME;

		FileInputStream stream = null;
		try {
			File file = new File(path);
			if (!file.exists()) return optionList;

			stream = new FileInputStream(file);
			
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
//		    			String value = node.getFirstChild().getNodeValue();
		    			if( name.equals(ATTR_MODEL))
		    			{
		    				BadaBuildOption buildOpt = new BadaBuildOption();
		    				for (Node nodeProfile = node.getFirstChild(); nodeProfile != null; nodeProfile = nodeProfile.getNextSibling()) {
		    					String nameProfileNode = nodeProfile.getNodeName();
		    		    		if( nodeProfile.getFirstChild() != null)
		    		    		{
		    		    			String value = nodeProfile.getFirstChild().getNodeValue();
		    		    			if( nameProfileNode.equals(ATTR_NAME)) buildOpt.setModel(value);
		    		    			else if( nameProfileNode.equals(ATTR_COMP)) buildOpt.setCompileOption(value);
		    		    			else if( nameProfileNode.equals(ATTR_LINK)) buildOpt.setLinkOption(value);
		    		    			else if( nameProfileNode.equals(ATTR_POSTFIX_LINK)) buildOpt.setPostfixLinkOption(value);
		    		    			else if( nameProfileNode.equals(ATTR_LINK_FILTER)) buildOpt.setLinkFilter(value);
		    		    		}
		    				}
		    		    	
		    				if( buildOpt.getModel().length() > 0 )
		    		    	{
		    		    		optionList.put(buildOpt.getModel(), buildOpt);
		    		    	}		    				
		    			}
		    		}
		    	}
		    }
			
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
	
		return optionList;
	}

	static final String FLAGS_FOR_GENERAL = "-fpic -fshort-wchar -mcpu=cortex-a8 -mfpu=vfpv3 -mfloat-abi=hard -mlittle-endian -mthumb-interwork";
	static final String LINK_FLAGS_FOR_GENERAL = "-nostdlib";
	static final String LINK_FLAGS2_FOR_GENERAL = "-lc-newlib -lm-newlib -lstdc++ -lgcc_s";
	static final String LINK_FILTERS_FOR_GENERAL = "libc-newlib.so libm-newlib.so stdc++ gcc_s";
	static public BadaBuildOption getGeneralOption()
	{
		BadaBuildOption options = new BadaBuildOption();
		
		options.setCompileOption(FLAGS_FOR_GENERAL);
		options.setLinkOption(LINK_FLAGS_FOR_GENERAL);
		options.setPostfixLinkOption(LINK_FLAGS2_FOR_GENERAL);
		options.setLinkFilter(LINK_FILTERS_FOR_GENERAL);
		
		return options;
	}	
}

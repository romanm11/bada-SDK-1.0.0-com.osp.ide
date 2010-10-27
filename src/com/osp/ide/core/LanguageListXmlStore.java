package com.osp.ide.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.osp.ide.IdePlugin;

public class LanguageListXmlStore {
	
	private static LanguageListXmlStore 	thisInstance	= null;
	
	public static final String TEMPLATE_FOLDER = "templates/resource/"; //$NON-NLS-1$
	public static final String FILE_NAME = "lang_contry_list.xml"; //$NON-NLS-1$
	public static final String ELEMENT_NAME = "languages"; //$NON-NLS-1$

	public static final String TAG_LANG = "lang"; //$NON-NLS-1$

	public static final String ATTR_ID = "id"; //$NON-NLS-1$
	public static final String ATTR_NAME = "name"; //$NON-NLS-1$
	
	public static final String LANG_VALUE_ENGLISH = "English"; //$NON-NLS-1$

	private String filePath = "";
	private ArrayList<LanguageData> languageList = new ArrayList<LanguageData>();

	
	public static LanguageListXmlStore getInstance() {
		if (LanguageListXmlStore.thisInstance == null)
			thisInstance = new LanguageListXmlStore();
		return thisInstance;
	}
	
	public LanguageListXmlStore() {
		filePath = IdePlugin.getDefault().getResourceLocationURL(
				TEMPLATE_FOLDER + FILE_NAME).getFile();
//	    filePath = TEMPLATE_FOLDER + FILE_NAME;

		loadXML(filePath);
	}

	public ArrayList<LanguageData> getLanguageList() {
		return languageList;
	}
	
	public ArrayList<LanguageData> getDefaultLanguages()
	{
		ArrayList<LanguageData> list = new ArrayList<LanguageData>();
//		
//		if( languageList.size() > 1 )
//		{
//			list.add(languageList.get(0));
//			list.add(languageList.get(1));
//		}
//		else
		{
			list.add(new LanguageData("English", LANG_VALUE_ENGLISH));
			list.add(new LanguageData("eng-GB", "English (England(UK))"));
		}
		
		return list;
	}

	private boolean loadXML(String path) {
		clear();
		if (path == null || path.length() == 0)
			return false;

		FileInputStream stream = null;
		try {
			File file = new File(path);
			if (!file.exists())
				return false;

			stream = new FileInputStream(file);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(stream);

			Element root = dom.getDocumentElement();
			String rootName = root.getNodeName();
			if (rootName.equals(ELEMENT_NAME)) {
				String id = "", name = "";
				for (Node text = root.getFirstChild(); text != null; text = text
						.getNextSibling()) {
					if (text != null && text.getNodeName() != null
							&& text.getNodeName().equals(TAG_LANG)) {
						id = text.getAttributes().getNamedItem(ATTR_ID)
								.getNodeValue();
						name = text.getAttributes().getNamedItem(ATTR_NAME)
								.getNodeValue();
						if (id != null && !id.isEmpty()) {
							LanguageData data = new LanguageData(id, name);
							languageList.add(data);
						}
					}
				}
			} else
				return false;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	public void clear() {
		if (languageList != null)
			languageList.clear();
	}
	
	public String findId(String name)
	{
		Iterator<LanguageData> itName = languageList.iterator();
		while (itName.hasNext()) {
			LanguageData item = itName.next();
			
			if( item.getName().equals(name) )
				return item.getId();			
		}
		
		return "";
	}

	public String findName(String id)
	{
		Iterator<LanguageData> itName = languageList.iterator();
		while (itName.hasNext()) {
			LanguageData item = itName.next();
			
			if( item.getId().equals(id) )
				return item.getName();			
		}
		
		return "";
	}
	
}

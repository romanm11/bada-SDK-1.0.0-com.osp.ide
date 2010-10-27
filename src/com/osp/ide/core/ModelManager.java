package com.osp.ide.core;

import java.io.File;
import java.util.ArrayList;

import com.osp.ide.IConstants;

public class ModelManager {

	static public String[] getModels(String skdRoot)
	{
		ArrayList<String> models = new ArrayList<String>();
		
		File f = new File(skdRoot+IConstants.FILE_SEP_BSLASH + IConstants.DIR_MODEL);
		if( f.exists() )
		{
			File lists[] = f.listFiles();
			if( lists != null && lists.length > 0 )
			{
				for( int i =0; i < lists.length; i++ )
				{
					String name = lists[i].getName();
					if( lists[i].isDirectory()
							&& name.equals(".") == false
							&& name.equals("..") == false)
					{
						models.add(lists[i].getName());						
					}
				}
			}
		}
		if( models.size() == 0) return new String[0];

		return (String[])models.toArray(new String[models.size()]);		
	}
	

	
	static public String getDefaultModel(String skdRoot)
	{
		String[] models = getModels(skdRoot);
		
		if( models != null && models.length > 0 )
			return models[0];
		
		return "";		
	}	
}

package com.osp.ide.utils;

import java.io.File;

import org.eclipse.core.resources.IProject;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;


public class BrokerUtils {

	static public File getBrokerDirectory(IProject project)
	{
		if(verifyOrgBrokerFile(project) != null )
		{
			String path = IdePlugin.getDefault().getSDKPath(project) + IConstants.PATH_BROKER; 
			File f = new File(path);
		
			if( f.exists() ) return f;
		}
		else
		{
			String path = IdePlugin.getDefault().getSDKPath(project) + java.io.File.separatorChar + "OspdSdk" + java.io.File.separatorChar + "Template" + java.io.File.separatorChar + "Tools" + java.io.File.separatorChar + "Broker"; 
			File f = new File(path);
		
			if( f.exists() ) return f;
		}
		
		return null;
	}
	
	static public String verifyBrokerFile(IProject project)
	{
		String broker_file = verifyOrgBrokerFile(project);
		
		if( broker_file != null ) return broker_file;
		
		return verifyOtherBrokerFile(project);
	}
	
	static private String verifyOrgBrokerFile(IProject project)
	{
		String path = IdePlugin.getDefault().getSDKPath(project) 
						+ IConstants.PATH_BROKER
						+ IConstants.FILE_SEP_BSLASH
						+ IConstants.BROKER_FILENAME;
		File f = new File(path);
		
		if( f.exists() ) return path;
		
		return null;
	}
	
	static private String verifyOtherBrokerFile(IProject project)
	{
		String path = IdePlugin.getDefault().getSDKPath(project) 
						+ java.io.File.separatorChar + "OspdSdk" + java.io.File.separatorChar + "Template" + java.io.File.separatorChar + "Tools" + java.io.File.separatorChar + "Broker" + java.io.File.separatorChar
						+ IConstants.BROKER_FILENAME;

		File f = new File(path);
		if( f.exists() ) return path;

		return null;

	}	
	
	
}

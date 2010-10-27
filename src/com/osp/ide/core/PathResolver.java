package com.osp.ide.core;

import org.eclipse.core.resources.IProject;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;

public class PathResolver {

	public static final String START_PATTERN = "${"; //$NON-NLS-1$
	public static final String END_PATTERN = "}"; //$NON-NLS-1$

	
	
	static public String getAbsolutePath(String str, IProject proj)
	{
		int start= 0, end= 0;
		String envString;
		while ((start = str.indexOf(START_PATTERN)) >= 0) {
			end = str.indexOf(END_PATTERN, start);
			
			if (end != -1) {
				envString = str.substring(start, end) + END_PATTERN;
				String var = getEnvVaraible(envString, proj);
				
				str = str.replace(envString, var);
			} 
		}
		return str;
	}

	static public String getAbsoluteCygwinPath(String str, IProject proj)
	{
		return pathToCygwinPath(getAbsolutePath(str, proj));
	}
	
	static public String pathToCygwinPath(String path)
	{
		return path.replaceAll("\\\\", "/");
	}
	
	static public String getApplicatuonID(IProject proj)
	{
		if( proj == null ) return "";
		
		ManifestXmlStore store = IdePlugin.getDefault().getManifestXmlStore(proj);
		
		if( store != null ) return store.getId();
		else return "";		
	}
	
	
	static private String getEnvVaraible(String str, IProject proj)
	{
/*		
		if(str.equals(IConstants.ENV_OSPROOT_VAR))
		{
			return IdePlugin.getDefault().getOSPRoot();
		}
		else
*/			
		if(proj != null)
		{
			if(str.equals(IConstants.ENV_SDKROOT_VAR))
			{
				return IdePlugin.getDefault().getSDKPath(proj);
			}
			else if(str.equals(IConstants.ENV_PROJECT_NAME))
			{
				return proj.getName();
			}
			else if(str.equals(IConstants.ENV_APPLICATION_ID))
			{
				ManifestXmlStore store = IdePlugin.getDefault().getManifestXmlStore(proj);
				
				if( store != null ) return store.getId();
				else return "";
			}
			else if(str.equals(IConstants.ENV_PROJECT_ROOT_VAR))
			{
				return proj.getLocation().toString();
			}	
			else if(str.equals(IConstants.ENV_TARGRT_LIB_PATH_VAR))
			{
				String path = IdePlugin.getDefault().getSDKPath(proj)
							+ IConstants.FILE_SEP_BSLASH + IConstants.DIR_MODEL
							+ IConstants.FILE_SEP_BSLASH + IdePlugin.getDefault().getModel(proj)
							+ IConstants.FILE_SEP_BSLASH + IConstants.DIR_TARGET;
				
				return path;
				//return IdePlugin.getDefault().getSDKPath(proj) + IConstants.PATH_TARGRT_LIB;
			}
			else if(str.equals(IConstants.ENV_SIMULATOR_LIB_PATH_VAR))
			{
/*				
				return IdePlugin.getDefault().getSDKPath(proj) 
							+ IConstants.PATH_SIMUAL_LIB 
							+ "/" 
							+ IdePlugin.getDefault().getSimualLibDir(proj);
*/
//				return IdePlugin.getDefault().getSDKPath(proj)
//							+ IConstants.PATH_SIMUAL_LIB;
				String path = IdePlugin.getDefault().getSDKPath(proj)
							+ IConstants.FILE_SEP_BSLASH + IConstants.DIR_MODEL
							+ IConstants.FILE_SEP_BSLASH + IdePlugin.getDefault().getModel(proj)
							+ IConstants.FILE_SEP_BSLASH + IConstants.DIR_SIMULATOR;
			
				return path;				
				
			}
			else if(str.equals(IConstants.ENV_SCREEN_DIR_VAR))
			{
				return IdePlugin.getDefault().getSimualLibDir(proj); 
			}
			else if(str.equals(IConstants.ENV_MODEL_NAME_VAR))
			{
				return IdePlugin.getDefault().getModel(proj); 
			}			
		}

		String env = System.getenv(str);
		if( env == null ) return "";
			
		return env;
	}
}

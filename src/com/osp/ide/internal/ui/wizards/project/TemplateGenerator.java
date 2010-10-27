package com.osp.ide.internal.ui.wizards.project;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;


public class TemplateGenerator {
	
	public final static String BASENAME = "baseName";
	public final static String BASENAME_UPPER = "baseName_upper";		//bk
	
	public final static String ENTRY = "entry";
	public final static String APP_NAME = "application_name";
	public final static String APP_ID = "application_id";
	public final static String ICON = "icon";
	public final static String VENDOR = "vendor";
	public final static String DESCRIPTION = "description";
	public final static String NAMESPACE = "namespace";
	public final static String INCLUDES = "includes";
	public final static String SECRET = "secret";

	public final static String FORMNAME = "formName";
	public final static String FORMNAME_UPPER = "formName_upper";
	
	
	public TemplateGenerator()	{
		
	}

	public boolean createTemplateFile(IProject prj, URL tmplPath, String distPath, Map<String, String> valueStore )
	{
		String fileContents = TemplateHelper.readFromFile(tmplPath);
		fileContents = TemplateHelper.getValueAfterExpandingMacros(fileContents, TemplateHelper.getReplaceKeys(fileContents), valueStore);
		
		if(fileContents == null || fileContents.length() == 0 ) return false;
		
		InputStream contents = new ByteArrayInputStream(fileContents.getBytes());
		
		try {
			IFile iFile = prj.getFile(distPath);
			if (iFile.exists()) {
				iFile.setContents(contents, true, true, null);
			}
			else
			{
				iFile.create(contents, true, null);
				iFile.refreshLocal(IResource.DEPTH_ONE, null);					
			}
		} catch (CoreException e) {
			return false;
		}
		
		return true;
	}

	public boolean createDirectory(IProject prj, String name)
	{
		IPath projPath = prj.getFullPath();
		
		IFolder folder = prj.getFolder(name);
		if (!folder.exists()) {
			try {
				folder.create(true, true, new NullProgressMonitor());
			} catch (CoreException e) {
				return false;
			}			
		}
		
		return true;
	}
	
	public boolean copyFile(IProject prj, URL tmplPath, String distPath)
	{
		IPath prjPath = prj.getLocation();
		prjPath = prjPath.append(distPath);
		
		File file = prjPath.makeAbsolute().toFile();
		
		try {
			TemplateHelper.copyBinaryFile(tmplPath, file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		}
		
		return true;

	}
	
}

package com.osp.ide.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

public class PackageData {

	IFile fileAppXml = null;
	IFile fileExe = null;
	IFile fileManifestXml = null;
	
	List <IFile> libList;
	
	IProject project;
	String configName;
	boolean checkSecurity=true;
	IFolder buildDir;
	private List resourcesToExport;
	
	public PackageData()
	{
		clear();
	}

	public void setAppXml(IFile file)
	{
		fileAppXml = file;
	}
	
	public IFile getAppXml()
	{
		return fileAppXml;
	}	
	
	public void setManifestXml(IFile file)
	{
		fileManifestXml = file;
	}	
	
	public IFile getManifestXml()
	{
		return fileManifestXml;
	}	
	
	public void setExeFile(IFile file)
	{
		fileExe = file;
	}	

	public IFile getExeFile()
	{
		return fileExe;
	}	


	public void setProject(IProject prj)
	{
		project = prj;
	}
	
	public IProject getProject()
	{
		return project;
	}
	
	public void setConfigName(String name)
	{
		configName = name;
	}
	
	public String getConfigName()
	{
		return configName;
	}
	
	public void setCheckSecurity(boolean flag)
	{
		checkSecurity = flag;
	}
	
	public boolean getCheckSecurity()
	{
		return checkSecurity;
	}	
	
	public void setBuildDirectory(IFolder dir)
	{
		buildDir = dir;
	}	

	public IFolder getBuildDirectory()
	{
		return buildDir;
	}		
	
	public void setLibraryList(List <IFile> list)
	{
		libList = list;
	}
	
	public IFile[] getLibraryList()
	{
		if(libList == null) return new IFile[0];
		
		return (IFile[]) libList.toArray(new IFile[libList.size()]);
	}
	
	public int getLibraryListCount()
	{
		if(libList == null) return 0;
		
		return libList.size();
	}
	
	public List getExportResources()
	{
		return resourcesToExport;
	}
	
	public void setExportResources(List list)
	{
		resourcesToExport = list;
	}
	
	
	public void clear()
	{
    	fileAppXml = null;
    	fileExe = null;
    	fileManifestXml = null;
    	
    	project = null;
    	configName = "";
    	
    	checkSecurity = true;
    	buildDir = null;
    	libList = null;
    	resourcesToExport = null;
	}
}

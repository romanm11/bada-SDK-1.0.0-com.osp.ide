package com.osp.ide.internal.ui.wizards.project;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.ui.wizards.CfgHolder;
import org.eclipse.swt.graphics.Point;

import com.osp.ide.IConstants;

public class OSPCreateDataStore {
	
	String projectName="";
	IToolChain fToolChain=null;
	int projectType=0;
	
	String ba_name="";
	String ba_vendor="";
	String ba_desc="";
	String ba_icon="";
	String ba_entry="";
	
	String ba_formName = IConstants.DEFAULT_FORM_NAME;
	String ba_formBaseClass = IConstants.FORM_CLASS[IConstants.INX_FORM_CLASS_DEFAULT];
	
	CfgHolder[] cfgHolder=null;
	String sdkPath="";
	String modelName="";
	
	private ArrayList tc_listeners;
	private ArrayList prj_name_listeners;
	private ArrayList prj_type_listeners;
	private ArrayList sdk_path_listeners;
	
	
	boolean dev_touchscreen=true;
	String[] dev_screenSize=null;
	String dev_cypType="";
	
	String manifestFile="";
	
	public static final int FEATRUE_3D        = 0;
	public static final int FEATRUE_DATABASE  = 1;
	public static final int FEATRUE_LOCATIONS = 2;
	public static final int FEATRUE_MEDIA     = 3;
	public static final int FEATRUE_NETWORK   = 4;
	public static final int FEATRUE_UI        = 5;
	public static final int FEATRUE_NUMBER    = 6;
	
	boolean appFeature[];
	
	boolean checkAutoScroll = false;
	String baseResolution="";
	
	private Point		wizardPos;
	
	public OSPCreateDataStore()
	{
		tc_listeners = new ArrayList();
		prj_name_listeners = new ArrayList();
		prj_type_listeners = new ArrayList();
		sdk_path_listeners = new ArrayList();
		
		appFeature = new boolean[FEATRUE_NUMBER];
		for(int i =0; i < FEATRUE_NUMBER; i++)
			appFeature[i] = false;
	}
	
	// main
	public void setProjectName(String name)
	{
		projectName = name;
		
		ba_entry = name;
		
		fireProjectNameChanged(name);
	}
	
	public String getProjectName()
	{
		return projectName;
	}
	
	public void setToolChain(IToolChain tc)
	{
		fToolChain = tc;
		fireToolChainChanged(tc);
	}
	
	public IToolChain getToolChain()
	{
		return fToolChain;
	}
	
	public void setProjectType(int type)
	{
		projectType = type;
		fireProjectTypeChanged(type);
	}
	
	public int getProjectType()
	{
		return projectType;
	}	

	
	public void setAppName(String name)
	{
		ba_name = name;
	}
	
	public String getAppName()
	{
		return ba_name;
	}
	
	public void setFormName(String name)
	{
		ba_formName = name;
	}
	
	public String getFormName()
	{
		return ba_formName;
	}	
	
	public void setVendor(String vendor)
	{
		ba_vendor = vendor;
	}
	
	public String getVendor()
	{
		return ba_vendor;
	}	
	
	
	public void setDescription(String desc)
	{
		ba_desc = desc;
	}
	
	public String getDescription()
	{
		return ba_desc;
	}	
	
	public String getEntry()
	{
		return ba_entry;
	}	

	public void setEntry(String entry)
	{
		ba_entry = entry;
	}	
	
	public String getIcon()
	{
		return ba_icon;
	}	

	public void setIcon(String icon)
	{
		ba_icon = icon;
	}	
	
	public void setScreenSize(String size[])
	{
		dev_screenSize = size;
	}
	
	public String[] getScreenSize()
	{
		return dev_screenSize;
	}
	
	
	public void setTouchScreen(boolean flag)
	{
		dev_touchscreen = flag;
	}
	
	public boolean getTouchScreen()
	{
		return dev_touchscreen;
	}
	
	public CfgHolder[] getCfgHolder()
	{
		return cfgHolder;
	}
	
	public void setCfgHolder(CfgHolder[] holder)
	{
		cfgHolder =holder;
	}
	
	public void setManifestFile(String fileName)
	{
		manifestFile = fileName;
	}
	
	public String getManifestFile()
	{
		return manifestFile;
	}
	
	public void setAutoScrollEnabled(boolean flag)
	{
		checkAutoScroll = flag;
	}
	
	public boolean getAutoScrollEnabled()
	{
		return checkAutoScroll;
	}

	
	public void setBaseResolution(String resolution)
	{
		baseResolution = resolution;
	}
	
	public String getBaseResolution()
	{
		return baseResolution;
	}
	
		
	public boolean getAppFeature(int feature_id)
	{
		return appFeature[feature_id];
	}
	
	public void setAppFeature(int feature_id, boolean value)
	{
		appFeature[feature_id] = value;
	}	
	
	// SDKs
	public String getSdkPath() {
		return sdkPath;
	}

	public void setSdkPath(String path) {
		sdkPath = path;
		fireSdkPathChanged(sdkPath);
	}
	
	// Model
	public String getModel() {
		return modelName;
	}

	public void setModel(String model) {
		modelName = model;
		
		fireModelChanged(model);
	}	
	
	
	// ToolChain Change Event Listener
	public void addToolChainChangeListener(IToolChainChangeListener l)
	{
		tc_listeners.add(l);
	}
	
	public void removeToolChainChangeListener(IToolChainChangeListener l)
	{
		tc_listeners.remove(l);
	}	
	
	public void fireToolChainChanged(IToolChain tc)
	{
		for(Iterator i = tc_listeners.iterator(); i.hasNext(); )
		{
			((IToolChainChangeListener)i.next()).toolChainChanged(tc);
		}		
	}
	
	public void addProjectNameChangeListener(IProjectNameChangeListener l)
	{
		prj_name_listeners.add(l);	
	}
	
	public void removeProjectNameChangeListener(IProjectNameChangeListener l)
	{
		prj_name_listeners.remove(l);
	}	
	
	public void fireProjectNameChanged(String name)
	{
		for(Iterator i = prj_name_listeners.iterator(); i.hasNext(); )
		{
			((IProjectNameChangeListener)i.next()).projectNameChanged(name);
		}		
	}
	
	public void addProjectTypeChangeListener(IProjectTypeChangeListener l)
	{
		prj_type_listeners.add(l);	
	}
	
	public void removeProjectTypeChangeListener(IProjectTypeChangeListener l)
	{
		prj_type_listeners.remove(l);
	}	
	
	public void fireProjectTypeChanged(int type)
	{
		for(Iterator i = prj_type_listeners.iterator(); i.hasNext(); )
		{
			((IProjectTypeChangeListener)i.next()).projectTypeChanged(type);
		}		
	}

	public void addSdkPathChangeListener(ISDKPathChangeListener l)
	{
		sdk_path_listeners.add(l);	
	}
	
	public void removeSdkPathChangeListener(ISDKPathChangeListener l)
	{
		sdk_path_listeners.remove(l);
	}	
	
	public void fireSdkPathChanged(String path)
	{
		for(Iterator i = sdk_path_listeners.iterator(); i.hasNext(); )
		{
			((ISDKPathChangeListener)i.next()).sdkPathChanged(path);
		}		
	}
	
	public void fireModelChanged(String path)
	{
		for(Iterator i = sdk_path_listeners.iterator(); i.hasNext(); )
		{
			((ISDKPathChangeListener)i.next()).modelChanged(path);
		}		
	}		
	
	static String convArtifactType(int type)
	{
	  	switch(type)
    	{
    		case IConstants.PRJ_TYPE_APP_FRAME:
    			return IConstants.ARTIFACT_EXE;
    		case IConstants.PRJ_TYPE_APP_FORM:
    			return IConstants.ARTIFACT_EXE;
    		case IConstants.PRJ_TYPE_LIB_SHARED:
    			return IConstants.ARTIFACT_SHAREDLIB;
    		case IConstants.PRJ_TYPE_LIB_STATIC:
    			return IConstants.ARTIFACT_STATICLIB;
    		case IConstants.PRJ_TYPE_APP_EMPTY:
    			return IConstants.ARTIFACT_EXE;
    	}
		return IConstants.ARTIFACT_EXE;	  	
	}
	
	public String getSelectedArtifactType()
	{
		return convArtifactType(projectType);
	}

	public void setWizardPos(Point wizardPos) {
		this.wizardPos = wizardPos;
	}

	public Point getWizardPos() {
		return wizardPos;
	}

	public void  setFormBaseClass(String name)
	{
		ba_formBaseClass = name;
	}

	public String getFormBaseClass()
	{
		return ba_formBaseClass;
	}	
	
}

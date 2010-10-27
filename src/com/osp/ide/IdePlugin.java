package com.osp.ide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.osp.ide.core.ManifestManager;
import com.osp.ide.core.ManifestXmlStore;
import com.osp.ide.core.OspPropertyStore;
import com.osp.ide.core.OspPropertyManager;
import com.osp.ide.core.ProjectChangeListener;
import com.osp.ide.core.builder.TargetOptionProvider;
import com.osp.ide.utils.WorkspaceUtils;

/**
 * The activator class controls the plug-in life cycle
 */
public class IdePlugin extends AbstractUIPlugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "com.osp.ide";


	// The shared instance
	private static IdePlugin plugin;
	protected ManifestManager manifestManager;
	protected OspPropertyManager ospPrefManager;
	ProjectChangeListener projectChangeListener;
	//	protected ResourceChangeListner resourceChangeListner;
	
	protected int getSimulatorOrConhostPid()
	{
		// I think this method implemented only considering that 
		// only a single simulator.exe can be run at the same time
		try {
			String line, name;
			Process p = Runtime.getRuntime().exec("tasklist.exe /fo csv /nh");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			while ((line = input.readLine()) != null) {
				if (!line.trim().equals("")) {
					// keep only the process name
					// "dgdersvc.exe","2036","Console","0","1,500 K"
					line = line.substring(1);
					name = line.substring(0, line.indexOf("\""));
					if( name.equals("Simulator.exe"))
					{
						input.close();
						
						line = line.substring(line.indexOf("\"")+1);
						// skip ."
						line = line.substring(line.indexOf("\"")+1);
						String pid = line.substring(0, line.indexOf("\""));
						return Integer.parseInt(pid);
					}
				}
			}
			input.close();
		}
		catch (Exception err) {
			err.printStackTrace();
		}
		
		return -1;		
	}

	/**
	 * The constructor
	 */
	public IdePlugin() {
		manifestManager = new ManifestManager();
		ospPrefManager = new OspPropertyManager();
		projectChangeListener = new ProjectChangeListener();

		plugin = this;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		//		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		//		plugin = null;		
		
		// Before terminating IDE, kill Simulator.exe (or corresponding const.exe) process
		// ecause error occurs during terminating Simulator.exe due to conhost.exe		
		int pid = getSimulatorOrConhostPid();		
		if(pid != -1){
			Process proc = Runtime.getRuntime().exec("tskill.exe " + pid);
			int result = proc.waitFor();
			if(result == 0)
				System.out.println("-- succeed in killing " + pid);
			else
				System.out.println("-- fail to kill " + pid);
		}else{
			System.out.println("cannot find conhost.exe");
		}
		
		
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static IdePlugin getDefault() {
		if( plugin == null ) plugin = new IdePlugin();

		return plugin;

	}

	public ManifestXmlStore getManifestXmlStore(IProject prj)
	{
		return manifestManager.getManifestXmlStore(prj);
	}
	
	public void manifestChanged(IProject prj)
	{
		manifestManager.mainfestChanged(prj);
		TargetOptionProvider.getInstance().modelChanged(prj);
		
		final IProject project = prj;
	    Display.getDefault().asyncExec(new Runnable() {
            @Override
            public synchronized void run() {
            	WorkspaceUtils.refreshOspProject(project);
            }
        });		
		
	}

	public void sdkRootPathChanged()
	{
		manifestManager.clearAll();
		WorkspaceUtils.refreshOSPProjects();
	}

	public OspPropertyStore getOspPropertyStore(IProject prj)
	{
		return ospPrefManager.getOspPropertyStore(prj);
	}

	public String getModel(IProject prj) {
		OspPropertyStore store = getOspPropertyStore(prj);

		if( store != null )
		{
			return store.getModel();
		}

		return "";
	}


	public String getSDKPath(IProject prj) {
		OspPropertyStore store = getOspPropertyStore(prj);

		if( store != null )
		{
			return store.getSdkPath();
		}

		return "";
	}

	public String getSimualLibDir(IProject prj) {
		ManifestXmlStore store = getManifestXmlStore(prj);

		if( store != null )
		{
			int resol = store.getScreenResolution();
			return IConstants.SCREEN_DIR_NAME[resol];
		}

		return "";
	}	


	//		return getPreferenceStore().getString(IConstants.ENV_OSPROOT);
	//	}


	public int getSimulatorConnectPort()
	{
		return getPreferenceStore().getInt(IConstants.PREP_ID_SIMULATOR_PORT);
	}

	public void setSimulatorConnectPort(int port)
	{
		getPreferenceStore().setValue(IConstants.PREP_ID_SIMULATOR_PORT, port);
	}


	public String getDeveloperSite() {
		return getPreferenceStore().getString(IConstants.PREP_ID_DEVELOPER_SITE);
	}		

	public int getLastCreatedProjectType() {
		return getPreferenceStore().getInt(IConstants.PREP_ID_LAST_PRJ_TYPE);
	}		

	public void setLastCreatedProjectType(int type) {
		getPreferenceStore().setValue(IConstants.PREP_ID_LAST_PRJ_TYPE, type);
	}

	public boolean getShowBrowser() {
		return getPreferenceStore().getBoolean(IConstants.PREP_ID_SHOW_BROWSER);
	}		

	public void setShowBrowser(boolean bShow) {
		getPreferenceStore().setValue(IConstants.PREP_ID_SHOW_BROWSER, bShow);
	}

	/*	
	public String getManifestPath() {
		return getPreferenceStore().getString(MANIFEST_PATH);
	}		

	public void setManifestPath(String path) {
		getPreferenceStore().setValue(MANIFEST_PATH, path);
	}	
	 */	


	public URL getResourceLocationURL(String path)
	{
		String loc = "";
		if( getBundle() != null )
		{
			URL installURL = getBundle().getEntry("/" + path);
			URL localURL = null;
			try {
				localURL= Platform.asLocalURL(installURL);
				return localURL;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return installURL;
			}			
		}

		return null;
	}	

	protected void initializeDefaultPreferences(IPreferenceStore store)
	{
		store.setDefault(IConstants.PREP_ID_DEVELOPER_SITE, IConstants.DEFAULT_DEVELOPER_SITE);
		//    	store.setDefault(IConstants.ENV_OSPROOT, IConstants.DEFAULT_OSP_ROOT);
		store.setDefault(IConstants.PREP_ID_LAST_PRJ_TYPE, IConstants.DEFAULT_LAST_PRJ_TYPE);

		store.setDefault(IConstants.PREP_ID_SHOW_BROWSER, IConstants.DEFAULT_SHOW_BROWSER);
		store.setDefault(IConstants.PREP_ID_MANIFEST_PATH, "");

		store.setDefault(IConstants.PREP_ID_SIMULATOR_PORT, IConstants.DEFAULT_SIMULATOR_PORT);
	}	

	public static ImageDescriptor createImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}		

}

package com.osp.ide.internal.ui.wizards.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.IPathEntry;
import org.eclipse.cdt.core.settings.model.CSourceEntry;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescriptionManager;
import org.eclipse.cdt.core.settings.model.ICResourceDescription;
import org.eclipse.cdt.core.settings.model.ICSourceEntry;
import org.eclipse.cdt.core.settings.model.WriteAccessException;
import org.eclipse.cdt.core.settings.model.extension.CConfigurationData;
import org.eclipse.cdt.managedbuilder.buildproperties.IBuildProperty;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuilder;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IResourceInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.cdt.managedbuilder.ui.properties.ManagedBuilderUIPlugin;
import org.eclipse.cdt.managedbuilder.ui.wizards.CfgHolder;
import org.eclipse.cdt.ui.newui.CDTPropertyManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;
import com.osp.ide.core.AppXmlStore;
import com.osp.ide.core.ManifestXmlStore;
import com.osp.ide.core.OspPropertyStore;

public class OSPCCProjectHelper {

	private static final String PROPERTY = "org.eclipse.cdt.build.core.buildType"; //$NON-NLS-1$
	private static final String PROP_VAL = PROPERTY + ".debug"; //$NON-NLS-1$
	
	
	OSPCreateDataStore dataStore;
	
	protected CfgHolder[] cfgs = null;
	
	String nameSpaceString = "";
	String includeString = "";
	
	public OSPCCProjectHelper(OSPCreateDataStore dStore) {
		dataStore = dStore;
	}
	
	public void createProject(IProject project, boolean onFinish) throws CoreException {
		
		if( dataStore == null )
		{
			throw new CoreException(new Status(IStatus.ERROR, 
					ManagedBuilderUIPlugin.getUniqueIdentifier(),
					"Project info data not found")); //$NON-NLS-1$
		}
		
		copyManifestFile(project);
		createOspPropertiesStore(project);
		
		ICProjectDescriptionManager mngr = CoreModel.getDefault().getProjectDescriptionManager();
		ICProjectDescription des = mngr.createProjectDescription(project, false, !onFinish);
		ManagedBuildInfo info = ManagedBuildManager.createBuildInfo(project);

		cfgs = dataStore.getCfgHolder();
		
		if (cfgs == null || cfgs.length == 0)
		{
			IToolChain tc = dataStore.getToolChain();
			if( tc != null )
				cfgs = ToolChainUtil.getDefaultCfgs(tc, dataStore.getSelectedArtifactType());
		}
		
		if (cfgs == null || cfgs.length == 0 || cfgs[0].getConfiguration() == null) {
			throw new CoreException(new Status(IStatus.ERROR, 
					ManagedBuilderUIPlugin.getUniqueIdentifier(),
					"Cannot create managed project with NULL configuration")); //$NON-NLS-1$
		}
		Configuration cf = (Configuration)cfgs[0].getConfiguration();
		ManagedProject mProj = new ManagedProject(project, cf.getProjectType());
		info.setManagedProject(mProj);

		cfgs = CfgHolder.unique(cfgs);
		cfgs = CfgHolder.reorder(cfgs);
		
		ICConfigurationDescription cfgDebug = null;
		ICConfigurationDescription cfgFirst = null;
		
		for(int i = 0; i < cfgs.length; i++){
			cf = (Configuration)cfgs[i].getConfiguration();
			String id = ManagedBuildManager.calculateChildId(cf.getId(), null);
			Configuration config = new Configuration(mProj, cf, id, false, true);
			CConfigurationData data = config.getConfigurationData();
			ICConfigurationDescription cfgDes = des.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID, data);
			//ICConfigurationDescription cfgDes = des.createConfiguration(cf, data);
			config.setConfigurationDescription(cfgDes);
			config.exportArtifactInfo();

			IBuilder bld = config.getEditableBuilder();
			if (bld != null) { 	bld.setManagedBuildOn(true); }
			
			config.setName(cfgs[i].getName());
			config.setArtifactName(removeSpaces(project.getName()));
			
			try {
				if( dataStore.getProjectType() != IConstants.PRJ_TYPE_LIB_STATIC)
					config.setBuildArtefactType("org.eclipse.cdt.build.core.buildArtefactType.sharedLib");
				if (bld != null) 
				{ 	
					bld.setUseDefaultBuildCmd(false); 
				}

			} catch (BuildException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if( dataStore.getProjectType() == IConstants.PRJ_TYPE_LIB_STATIC )
				config.setArtifactExtension("a");
			else if( dataStore.getProjectType() == IConstants.PRJ_TYPE_LIB_SHARED )
			{
				if( config.getName().indexOf("Simul") != -1 )
					config.setArtifactExtension("dll");
				else
					config.setArtifactExtension("so");
			}
			else
			{
//				if( config.getName().indexOf(IConstants.CONFIG_TARGET_DEBUG_NAME) != -1 )
//				{
//					String exeName = config.getArtifactName();
//					
//					if( exeName != null && !exeName.endsWith("_debug"))
//					{
//						config.setArtifactName(exeName+"_debug");
//					}
//				}
				
				config.setArtifactExtension("exe");
			}
			
			IBuildProperty b = config.getBuildProperties().getProperty(PROPERTY);
			if (cfgDebug == null && b != null && b.getValue() != null && PROP_VAL.equals(b.getValue().getId()))
				cfgDebug = cfgDes;
			if (cfgFirst == null) // select at least first configuration 
				cfgFirst = cfgDes; 
		}
		
		mngr.setProjectDescription(project, des);
		
//		if( dataStore.getProjectType() == IConstants.PRJ_TYPE_APP_FRAME  || dataStore.getProjectType() == IConstants.PRJ_TYPE_APP_FORM)
//			setProjectOption(project);

		setOSPDesc(project);
		doCreateAppXML(project);		
		doTemplatesPostProcess(project);
		doCustom(project);
	}
	
	protected void doCreateAppXML(IProject prj)
	{
		OspPropertyStore profStore = IdePlugin.getDefault().getOspPropertyStore(prj);
		
		if( profStore.getProjectType() == IConstants.PRJ_TYPE_LIB_STATIC ||
			profStore.getProjectType() == IConstants.PRJ_TYPE_LIB_SHARED )
		{
			return;
		}
		
		IFile projectFile = prj.getFile(new Path(IConstants.APP_XML_FILE));
		
		AppXmlStore xmlManager= new AppXmlStore(projectFile);
		
		xmlManager.setEntry(dataStore.getEntry());
		xmlManager.setDefaultName(dataStore.getAppName());
		
		xmlManager.setIconMainMenu(getDefaultMainMenuIconName(prj));
//		xmlManager.setIconSetting("");
//		xmlManager.setIconTicker("");
//		xmlManager.setIconQuickPanel("");
		xmlManager.setIconLaunchImage(IConstants.TEMPLATE_SPLASH_ICON_NAME);
		xmlManager.setVendor(dataStore.getVendor());
		xmlManager.setDescription(dataStore.getDescription());
//		xmlManager.setSdkPath(profStore.getSdkPath());
		
		xmlManager.setAutoScalingEnabled(dataStore.getAutoScrollEnabled());
		xmlManager.setBaseResolution(dataStore.getBaseResolution());
		
		xmlManager.storeXML();
	}

	protected void copyManifestFile(IProject prj)
	{
		String path = dataStore.getManifestFile();
		if( path ==null || path.length() == 0 )
			return;
		
		TemplateHelper.copyFileToProject(prj, path, IConstants.MANIFEST_FILE, false);		
	}
	
	
	Map<String, String> getValueStore(String baseName) {
		Map<String, String> valueStore = new HashMap<String, String>();
		
		if( baseName != null && baseName.length() > 0 )
		{
			valueStore.put(TemplateGenerator.BASENAME, baseName);
			valueStore.put(TemplateGenerator.BASENAME_UPPER, baseName.toUpperCase(Locale.getDefault()));	//bk
		}
		
		valueStore.put(TemplateGenerator.ENTRY, dataStore.getEntry());
		valueStore.put(TemplateGenerator.APP_NAME, dataStore.getAppName());
		valueStore.put(TemplateGenerator.VENDOR, dataStore.getVendor());
		valueStore.put(TemplateGenerator.DESCRIPTION, dataStore.getDescription());
		valueStore.put(TemplateGenerator.ICON, dataStore.getIcon());
		valueStore.put(TemplateGenerator.NAMESPACE, nameSpaceString);
		valueStore.put(TemplateGenerator.INCLUDES, includeString);
		
		String formName = dataStore.getFormName();
		if( formName != null && formName.length() > 0 )
		{
			valueStore.put(TemplateGenerator.FORMNAME, formName);
			valueStore.put(TemplateGenerator.FORMNAME_UPPER, formName.toUpperCase(Locale.getDefault()));
		}
		
		String path = dataStore.getManifestFile();
		String id = "";
		String secret = "";
		if( path !=null && path.length() > 0 )
		{
			ManifestXmlStore  manifestStore = new ManifestXmlStore(path); 
			id = manifestStore.getId();
			secret= manifestStore.getSecret();
		}
					
		valueStore.put(TemplateGenerator.APP_ID, id);
		valueStore.put(TemplateGenerator.SECRET, secret);
		
		return valueStore;
	}	
	
	protected void doTemplatesPostProcess(IProject prj) {
		int type = dataStore.getProjectType();
		
		TemplateGenerator generator = new TemplateGenerator();
		
		if( createSourceFolder(prj.getName(), IConstants.DIR_SOURCE, new NullProgressMonitor()) == false)
		{
			generator.createDirectory(prj, IConstants.DIR_SOURCE);
		}

		String prjName = dataStore.getProjectName();
		Map<String, String> valueStore = getValueStore(prjName);
		
		switch(type)
		{
			case IConstants.PRJ_TYPE_APP_FRAME:
//				generator.createDirectory(prj, IConstants.DIR_INCLUDE);
				if( createSourceFolder(prj.getName(), IConstants.DIR_INCLUDE, new NullProgressMonitor()) == false)
				{
					generator.createDirectory(prj, IConstants.DIR_INCLUDE);
				}
				generator.createDirectory(prj, IConstants.DIR_RESOURCE);
				generator.createDirectory(prj, IConstants.DIR_ICON);
				generator.createDirectory(prj, IConstants.DIR_HOME);
				
				generator.copyFile(prj, 
						IdePlugin.getDefault().getResourceLocationURL(IConstants.TEMPLATE_PROJECT_ICON_FILE),
						IConstants.DIR_ICON+IConstants.FILE_SEP_FSLASH+prjName+IConstants.EXT_ICON);

				generator.copyFile(prj, 
						IdePlugin.getDefault().getResourceLocationURL(IConstants.TEMPLATE_SPLASH_ICON_FILE),
						IConstants.DIR_ICON+IConstants.FILE_SEP_FSLASH+IConstants.TEMPLATE_SPLASH_ICON_NAME);
				
				// create project_name.cpp
				generator.createTemplateFile(prj,
						IdePlugin.getDefault().getResourceLocationURL(IConstants.TEMPLATE_FRAMEAPP_CCFILE),
						IConstants.DIR_SOURCE+IConstants.FILE_SEP_FSLASH+prjName+IConstants.EXT_CC,
						valueStore);
	
				// create project_name.h
				generator.createTemplateFile(prj,
						IdePlugin.getDefault().getResourceLocationURL(IConstants.TEMPLATE_FRAMEAPP_CCHEADER),
						IConstants.DIR_INCLUDE+IConstants.FILE_SEP_FSLASH+prjName+IConstants.EXT_HEADER,
						valueStore);	
				
				// create Entry.cpp
				generator.createTemplateFile(prj,
						IdePlugin.getDefault().getResourceLocationURL(IConstants.TEMPLATE_FRAMEAPP_CCENTRY),
						IConstants.DIR_SOURCE+IConstants.FILE_SEP_FSLASH+prjName+IConstants.TEMPLATE_ENTRY_CC_NAME_POSTFIX,
						valueStore);	
				
				
				// dataStore.setIcon(prjName+IConstants.EXT_ICON);
			break;
		
			case IConstants.PRJ_TYPE_APP_FORM:
				//generator.createDirectory(prj, IConstants.DIR_INCLUDE);
				if( createSourceFolder(prj.getName(), IConstants.DIR_INCLUDE, new NullProgressMonitor()) == false)
				{
					generator.createDirectory(prj, IConstants.DIR_INCLUDE);
				}
				generator.createDirectory(prj, IConstants.DIR_RESOURCE);
				generator.createDirectory(prj, IConstants.DIR_ICON);
				generator.createDirectory(prj, IConstants.DIR_HOME);
				

//				String screenSize[] = null;
//				String maniPath = dataStore.getManifestFile();
//				if( maniPath !=null && maniPath.length() > 0 )
//				{
//					ManifestXmlStore  manifestStore = new ManifestXmlStore(maniPath); 
//					screenSize = manifestStore.getScreenSizes();
//				}
//				
//				if( screenSize == null || screenSize.length == 0)
//				{
//					screenSize = IConstants.DEFAULT_SCREEN_SIZE;
//				}
                String screenSize[] = IConstants.DEFAULT_SCREEN_SIZE;

				for( int i = 0; i < screenSize.length; i++)
				{
					String formXmlDir = IConstants.DIR_RESOURCE + IConstants.FILE_SEP_FSLASH + screenSize[i];
					generator.createDirectory(prj, formXmlDir);
				}


				String formName = dataStore.getFormName();
				
				generator.copyFile(prj, 
						IdePlugin.getDefault().getResourceLocationURL(IConstants.TEMPLATE_PROJECT_ICON_FILE),
						IConstants.DIR_ICON+IConstants.FILE_SEP_FSLASH+prjName+IConstants.EXT_ICON);
				
				generator.copyFile(prj, 
						IdePlugin.getDefault().getResourceLocationURL(IConstants.TEMPLATE_SPLASH_ICON_FILE),
						IConstants.DIR_ICON+IConstants.FILE_SEP_FSLASH+IConstants.TEMPLATE_SPLASH_ICON_NAME);

				// create project_name.cpp
				generator.createTemplateFile(prj,
						IdePlugin.getDefault().getResourceLocationURL(IConstants.TEMPLATE_FORMAPP_CCFILE),
						IConstants.DIR_SOURCE+IConstants.FILE_SEP_FSLASH+prjName+IConstants.EXT_CC,
						valueStore);
				
				// create project_name.h
				generator.createTemplateFile(prj,
						IdePlugin.getDefault().getResourceLocationURL(IConstants.TEMPLATE_FORMAPP_CCHEADER),
						IConstants.DIR_INCLUDE+IConstants.FILE_SEP_FSLASH+prjName+IConstants.EXT_HEADER,
						valueStore);	
				
				// create Entry.cpp
				generator.createTemplateFile(prj,
						IdePlugin.getDefault().getResourceLocationURL(IConstants.TEMPLATE_FORMAPP_CCENTRY),
						IConstants.DIR_SOURCE+IConstants.FILE_SEP_FSLASH+prjName+IConstants.TEMPLATE_ENTRY_CC_NAME_POSTFIX,
						valueStore);
				
				// create Form.cpp
				generator.createTemplateFile(prj,
						IdePlugin.getDefault().getResourceLocationURL(IConstants.TEMPLATE_FORMAPP_CCFORM),
						IConstants.DIR_SOURCE+IConstants.FILE_SEP_FSLASH+formName+IConstants.EXT_CC,
						valueStore);

				// create Form.h
				generator.createTemplateFile(prj,
						IdePlugin.getDefault().getResourceLocationURL(IConstants.TEMPLATE_FORMAPP_CCFORMHEADER),
						IConstants.DIR_INCLUDE+IConstants.FILE_SEP_FSLASH+formName+IConstants.EXT_HEADER,
						valueStore);
				
/*				
				// create English.xml
				generator.createTemplateFile(prj,
						IdePlugin.getDefault().getResourceLocationURL(IConstants.TEMPLATE_FORMAPP_ENGLISH_XML),
						IConstants.DIR_RESOURCE+IConstants.FILE_SEP_FSLASH+IConstants.TEMPLATE_ENGLISH_XML_NAME,
						valueStore);	
*/				
				// create Form.xml
				for( int i = 0; i < screenSize.length; i++)
				{
					String formXmlDir = IConstants.DIR_RESOURCE + IConstants.FILE_SEP_FSLASH + screenSize[i];

					String formTemplatePath = IConstants.TEMPLATE_FORMAPP_DIR + IConstants.FILE_SEP_FSLASH 
								+ screenSize[i] + IConstants.FILE_SEP_FSLASH + IConstants.TEMPLATE_FORM_XML_NAME; 

					generator.createTemplateFile(prj,
							IdePlugin.getDefault().getResourceLocationURL(formTemplatePath),
							formXmlDir+IConstants.FILE_SEP_FSLASH+ IConstants.PREFIX_FORM_XML + formName.toUpperCase(Locale.getDefault())+IConstants.EXT_XML,
							valueStore);							
				}
				
				break;
			case IConstants.PRJ_TYPE_LIB_SHARED:
				
				if( createSourceFolder(prj.getName(), IConstants.DIR_INCLUDE, new NullProgressMonitor()) == false)
				{
					generator.createDirectory(prj, IConstants.DIR_INCLUDE);
				}
				generator.createDirectory(prj, IConstants.DIR_RESOURCE);
				generator.createDirectory(prj, IConstants.DIR_ICON);
				generator.createDirectory(prj, IConstants.DIR_HOME);
				
				// create Entry.cpp
				generator.createTemplateFile(prj,
						IdePlugin.getDefault().getResourceLocationURL(IConstants.TEMPLATE_DLL_CCPROJECT),
						IConstants.DIR_SOURCE+IConstants.FILE_SEP_FSLASH+prjName+IConstants.EXT_CC,
						valueStore);
				generator.createTemplateFile(prj,
						IdePlugin.getDefault().getResourceLocationURL(IConstants.TEMPLATE_DLL_CCPROJECT_H),
						IConstants.DIR_INCLUDE+IConstants.FILE_SEP_FSLASH+prjName+IConstants.EXT_HEADER,
						valueStore);
				
				break;
			case IConstants.PRJ_TYPE_LIB_STATIC:
				break;
				
			case IConstants.PRJ_TYPE_APP_EMPTY:
				if( createSourceFolder(prj.getName(), IConstants.DIR_INCLUDE, new NullProgressMonitor()) == false)
				{
					generator.createDirectory(prj, IConstants.DIR_INCLUDE);
				}
				generator.createDirectory(prj, IConstants.DIR_RESOURCE);
				generator.createDirectory(prj, IConstants.DIR_ICON);
				generator.createDirectory(prj, IConstants.DIR_HOME);
				
				break;
		}
	}
	
	protected void createOspPropertiesStore(IProject newProject)
	{
		OspPropertyStore profStore = new OspPropertyStore(newProject);
		
		profStore.setProjectType(dataStore.getProjectType());
		
		profStore.setSdkPath(dataStore.getSdkPath());
		profStore.setModel(dataStore.getModel());
		
		profStore.store();
		
	}
	
	protected void setOSPDesc(IProject newProject)
	{
		OspPropertyStore profStore = IdePlugin.getDefault().getOspPropertyStore(newProject);
		
		for(int i = 0; i < cfgs.length; i++){
			String cfgName = cfgs[i].getName();
			
			String value =  OspPropertyStore.getPackagingToolOutputDirDefault(cfgName);
			profStore.setPackagingToolOutputDir(cfgName, value);
			
			value =  OspPropertyStore.getPackagingToolCertFileDefault(cfgName);
			profStore.setPackagingToolCertFile(cfgName, value);
			
			value =  OspPropertyStore.getPackagingToolCmdArgsDefault();
			profStore.setPackagingToolCmdArgs(cfgName, value);
		}
		
		profStore.store();
	}
	
	protected void doCustom(IProject newProject) {
	}
	
	public void postProcess(IProject newProject) {
		deleteExtraConfigs(newProject);
		// calls are required only if the project was
		// created before for <Advanced Settings> feature.
	}
	
	private void deleteExtraConfigs(IProject newProject) {
	}
	

	public static String removeSpaces(String s) {
		char[] cs = s.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<cs.length; i++) {
			if (Character.isWhitespace(cs[i])) 
				continue;
			sb.append(cs[i]);
		}
		return sb.toString();	
	}
	

	public static String getProjectTypeDesc(int type) {
		
		switch(type)
		{
			case IConstants.PRJ_TYPE_APP_FRAME:
				return ("Create a frame based bada application. (e.g. 2D/3D Games)");
			case IConstants.PRJ_TYPE_APP_FORM:
				return ("Create a form based bada application. The applications of this type can have multiple forms each with optional indicator, title and softkeys.");		
			case IConstants.PRJ_TYPE_LIB_SHARED:
				return ("Create a bada shared library.");
			case IConstants.PRJ_TYPE_LIB_STATIC:
				return ("Create a bada static library.");
			case IConstants.PRJ_TYPE_APP_EMPTY:
				return ("Create an empty project.");
			case IConstants.PRJ_TYPE_APP_TREE:
				return ("Create a bada application.");
			case IConstants.PRJ_TYPE_LIB_TREE:
				return ("Create a bada library.");				
			default:
				return ("Create a new C++ application project for bada");
		}
	}
	
	public static boolean isTypeValid(int type) {
		
		if( type == IConstants.PRJ_TYPE_APP_TREE || type == IConstants.PRJ_TYPE_LIB_TREE ) return false;
		
		return true;
	}	
	
	public static String getProjectTypeLabel(int type) {
		
		switch(type)
		{
			case IConstants.PRJ_TYPE_APP_FRAME:
				return ("bada Frame Based Application");
			case IConstants.PRJ_TYPE_APP_FORM:
				return ("bada Form Based Application");		
			case IConstants.PRJ_TYPE_LIB_SHARED:
				return ("bada Shared Library");
			case IConstants.PRJ_TYPE_LIB_STATIC:
				return ("bada Static Library");		
			case IConstants.PRJ_TYPE_APP_EMPTY:
				return ("bada Empty Project");				
			default:
				return ("");
		}
	}
	
	protected boolean createSourceFolder(String projectName, String targetPath, IProgressMonitor monitor) {
	
		IProject projectHandle = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		
		if (!projectHandle.exists()) {
			return false;
		}
		
		IPath projPath = projectHandle.getFullPath();
		
		IFolder folder = projectHandle.getFolder(targetPath);
		if (!folder.exists()) {
			try {
				folder.create(true, true, monitor);
			} catch (CoreException e) {
				return false;
			}			
		}
		
		try {
			ICProject cProject = CoreModel.getDefault().create(projectHandle);
			if (cProject != null) {
				if(CCorePlugin.getDefault().isNewStyleProject(cProject.getProject())){
					//create source folder for new style project
					createNewStyleProjectFolder(monitor, projectHandle, folder);
				} else {
					//create source folder for all other projects 
					createFolder(targetPath, monitor, projPath, cProject);
				}
			}
		} catch (WriteAccessException e) {
			return false;
		} catch (CoreException e) {
			return false;
		}

		return true;
	}
		
	/**
	 * @param monitor
	 * @param projectHandle
	 * @param folder
	 * @throws CoreException
	 * @throws WriteAccessException
	 */
	private void createNewStyleProjectFolder(IProgressMonitor monitor, IProject projectHandle, IFolder folder) throws CoreException, WriteAccessException {
		ICSourceEntry newEntry = new CSourceEntry(folder, null, 0); 
		ICProjectDescription description = CCorePlugin.getDefault().getProjectDescription(projectHandle);

		ICConfigurationDescription configs[] = description.getConfigurations();
		for(int i=0; i < configs.length; i++){
			ICConfigurationDescription config = configs[i];
			ICSourceEntry[] entries = config.getSourceEntries();
			Set<ICSourceEntry> set = new HashSet<ICSourceEntry>();
			for (int j=0; j < entries.length; j++) {
				if(new Path(entries[j].getValue()).segmentCount() == 1)
					continue;
				set.add(entries[j]);
			}
			set.add(newEntry);
			config.setSourceEntries(set.toArray(new ICSourceEntry[set.size()]));
		}

		CCorePlugin.getDefault().setProjectDescription(projectHandle, description, false, monitor);
	}

	/**
	 * @param targetPath
	 * @param monitor
	 * @param projPath
	 * @param cProject
	 * @throws CModelException
	 */
	private void createFolder(String targetPath, IProgressMonitor monitor, IPath projPath, ICProject cProject) throws CModelException {
		IPathEntry[] entries = cProject.getRawPathEntries();
		List<IPathEntry> newEntries = new ArrayList<IPathEntry>(entries.length + 1);

		int projectEntryIndex= -1;
		IPath path = projPath.append(targetPath);

		for (int i = 0; i < entries.length; i++) {
			IPathEntry curr = entries[i];
			if (path.equals(curr.getPath())) {
				// just return if this folder exists already
				return;
			}
			if (projPath.equals(curr.getPath())) {
				projectEntryIndex = i;
			}	
			newEntries.add(curr);
		}

		IPathEntry newEntry = CoreModel.newSourceEntry(path);

		if (projectEntryIndex != -1) {
			newEntries.set(projectEntryIndex, newEntry);
		} else {
			newEntries.add(CoreModel.newSourceEntry(path));
		}

		cProject.setRawPathEntries(newEntries.toArray(new IPathEntry[newEntries.size()]), monitor);
	}
	
	public static String getDefaultMainMenuIconName(IProject prj)
	{
		return prj.getName()+IConstants.EXT_ICON;			
	}

	public void setProjectOption(IProject prj)
	{
		ICConfigurationDescription[] cfgDescs = CDTPropertyManager.getProjectDescription(prj).getConfigurations();
		
		final ICProjectDescription local_prjd = CoreModel.getDefault().getProjectDescription(prj);
		
		if( cfgDescs == null ) return;
		
		int prjType = dataStore.getProjectType();
		
		for( int i = 0; i < cfgDescs.length; i ++ )
		{

			ICConfigurationDescription c = findCfg(local_prjd, cfgDescs[i].getConfiguration());
			//IConfiguration cfg = ManagedBuildManager.getConfigurationForDescription(cfgDescs[i].getConfiguration());
			
			ICResourceDescription lc = c.getRootFolderDescription();
			
			IResourceInfo resInfo = getResCfg(lc);
			if( resInfo == null ) continue;

			boolean isSimual = false;
			if( cfgDescs[i].getName().indexOf("Simul") != -1 ) isSimual = true;

			List<String> options = new ArrayList<String>();
			List<String> allOptions = new ArrayList<String>();
			List<String> newOptions = new ArrayList<String>();
			
			if( isSimual )
			{
				allOptions.add("FGraphics");
				if( prjType == IConstants.PRJ_TYPE_APP_FRAME || prjType == IConstants.PRJ_TYPE_APP_EMPTY  )
					options.add("FGraphics");
			}
			else // Target
			{
				allOptions.add("\"${TARGET_LIB_PATH}/FGraphics.so\"");
				if( prjType == IConstants.PRJ_TYPE_APP_FRAME || prjType == IConstants.PRJ_TYPE_APP_EMPTY  )
					options.add("\"${TARGET_LIB_PATH}/FGraphics.so\"");						
			}

			
//			ManagedBuildManager.setOption(recDesc, changedHolder, changedOption, val);
			
			
			ITool tool = resInfo.getParent().getTargetTool();
			if( tool == null) continue;
			
			IOption[] opts = tool.getOptions();
			if( opts != null )
			{
				if( isSimual )
				{
					for (int j = 0; j < opts.length; j++) {
						IOption option = opts[j];
						try {
							if (option.getValueType() == IOption.LIBRARIES) {
								
								String[] libs = option.getLibraries();
								for( int cnt = 0; cnt < libs.length; cnt++)
								{
									if(allOptions.contains(libs[cnt]) == false)
										newOptions.add(libs[cnt]);
								}								
								newOptions.addAll(options);
								option.setValue(newOptions.toArray(new String[newOptions.size()]));
								//ManagedBuildManager.setOption(resInfo, tool, option, newOptions.toArray(new String[newOptions.size()]));
								resInfo.setOption(tool, option, newOptions.toArray(new String[newOptions.size()]));
								break;
							}
						} catch (BuildException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else  // Target
				{
					for (int j = 0; j < opts.length; j++) {
						IOption option = opts[j];
						try {
							if (option.getValueType() == IOption.OBJECTS) {
								
								String[] uobjs = option.getUserObjects();
								for( int cnt = 0; cnt < uobjs.length; cnt++)
								{
									if(allOptions.contains(uobjs[cnt]) == false)
										newOptions.add(uobjs[cnt]);
								}								
								
								newOptions.addAll(options);
								option.setValue(newOptions.toArray(new String[newOptions.size()]));
								ManagedBuildManager.setOption(resInfo, tool, option, newOptions.toArray(new String[newOptions.size()]));
								break;
							}
						} catch (BuildException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
			}
		}
		
		try {
			CoreModel.getDefault().setProjectDescription(prj, local_prjd);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	
    private ICConfigurationDescription findCfg(ICProjectDescription prj, ICConfigurationDescription cfg) {
    	String id = cfg.getId();
    	// find config with the same ID as original one
		ICConfigurationDescription c = prj.getConfigurationById(id);
		// if there's no cfg found, try to create it
		if (c == null) {
			try {
				c = prj.createConfiguration(id, cfg.getName(), cfg);
				c.setDescription(cfg.getDescription());
			} catch (CoreException e) { 
				/* do nothing: c is already null */ 
			}
		}

    	return c;
    }
    
	public IResourceInfo getResCfg(ICResourceDescription cfgd) {
		IConfiguration cfg = ManagedBuildManager.getConfigurationForDescription(cfgd.getConfiguration());
		
		return cfg.getRootFolderInfo();
	}    
	
}

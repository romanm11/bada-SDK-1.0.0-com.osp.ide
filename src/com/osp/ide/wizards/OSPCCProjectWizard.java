package com.osp.ide.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescriptionManager;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.CommonBuilder;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;
import com.osp.ide.core.badaNature;
import com.osp.ide.core.builder.OspBuilder;
//import com.osp.ide.internal.ui.wizards.project.AppFeaturePage;
import com.osp.ide.internal.ui.wizards.project.AutoScrollingSettingPage;
import com.osp.ide.internal.ui.wizards.project.BasicSettingPage;
import com.osp.ide.internal.ui.wizards.project.DeviceConfigPage;
import com.osp.ide.internal.ui.wizards.project.FormSettingPage;
import com.osp.ide.internal.ui.wizards.project.ICreateDataStore;
import com.osp.ide.internal.ui.wizards.project.OSPCCProjectHelper;
import com.osp.ide.internal.ui.wizards.project.OSPCreateDataStore;
import com.osp.ide.internal.ui.wizards.project.OSPMainWizardPage;
import com.osp.ide.internal.ui.wizards.project.SelectConfigPage;
import com.osp.ide.internal.ui.wizards.project.SelectOspSdkPage;
import com.osp.ide.internal.ui.wizards.project.SummaryPage;

public class OSPCCProjectWizard extends Wizard implements INewWizard, IExecutableExtension, ICreateDataStore {

	private static final String title = "Error Creating Project"; //$NON-NLS-1$
	private static final String message = "Project cannot be created"; //$NON-NLS-1$
	
	OSPMainWizardPage fMainPage;
	SelectOspSdkPage fSelectOspSdkPage;
	BasicSettingPage fBasicSettingPage;
	AutoScrollingSettingPage fAutoScrollingSettingPage;
	SelectConfigPage fSelectConfigPage;
	DeviceConfigPage fDeviceConfigPage;
	FormSettingPage  fFormSettingPage;
//	AppFeaturePage fAppFeaturePage;
	SummaryPage fSummaryPage;
	
	OSPCreateDataStore dataStore;
	OSPCCProjectHelper prj_helper;
	
	protected IProject newProject=null;
	
	private boolean existingPath = false;
	private String lastProjectName = null;
	private URI lastProjectLocation = null;
	
	private IWorkbench workbench;
	protected IConfigurationElement fConfigElement;
	
	private boolean isEmptyProject = false;

	private boolean oldDialogHelpAvailable = TrayDialog.isDialogHelpAvailable();
	
	public OSPCCProjectWizard() {
		super();
		TrayDialog.setDialogHelpAvailable(false);

		setDialogSettings(CUIPlugin.getDefault().getDialogSettings());
		setNeedsProgressMonitor(true);
		setForcePreviousAndNextButtons(true);
		setWindowTitle("New bada C++ project");
		
		dataStore = new OSPCreateDataStore();
		prj_helper = new OSPCCProjectHelper(dataStore);
	}

	public void dispose() {
		super.dispose();
		TrayDialog.setDialogHelpAvailable(oldDialogHelpAvailable);
	}
	
	public IProject getProject(boolean onFinish) {
		if (newProject == null)	{
            existingPath = false;
            
		  	try {
		  		IFileStore fs;
				URI p = fMainPage.getProjectLocation();
			  	if (p == null) { 
			  		fs = EFS.getStore(ResourcesPlugin.getWorkspace().getRoot().getLocationURI());
				    fs = fs.getChild(fMainPage.getProjectName());
			  	} else
			  		fs = EFS.getStore(p);
		  		IFileInfo f = fs.fetchInfo();
		  		if (f.exists() && f.isDirectory()) {
		  			if (fs.getChild(".project").fetchInfo().exists()) { //$NON-NLS-1$
	                	if (!
	                    		MessageDialog.openConfirm(getShell(), 
	                    				"Old project will be overridden",  //$NON-NLS-1$
	            						"Existing project settings will be overridden.\nImport feature can be used instead to preserve old settings.\nOK to override ?") //$NON-NLS-1$
	            						)
	                		return null;
	                }
	                existingPath = true;
		  		}
        	} catch (CoreException e) {
        		CUIPlugin.log(e.getStatus());
        	}
        	
			lastProjectName = fMainPage.getProjectName();
			lastProjectLocation = fMainPage.getProjectLocation();
			// start creation process
			invokeRunnable(getRunnable(onFinish)); 
		} 
		return newProject;
	}
	
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		if (getProject(true) == null) return false;
		
		try {
			badaNature.addNature(newProject, new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			System.out.println("Nature add error");
		}
		
		prj_helper.postProcess(newProject);
		try {
			setCreated();
		} catch (CoreException e) {
			// TODO log or display a message
			e.printStackTrace();
			return false;
		}
		
		if( !isEmptyProject )
			IdePlugin.getDefault().setLastCreatedProjectType(dataStore.getProjectType());
		
		BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
		selectAndReveal(newProject);
		
		return true;

	}
	
    public boolean performCancel() {
    	clearProject();
    	
        return true;
    }
	
	
    protected void selectAndReveal(IResource newResource) {
    	BasicNewResourceWizard.selectAndReveal(newResource, workbench.getActiveWorkbenchWindow());
    }	
	
	protected boolean setCreated() throws CoreException {
		ICProjectDescriptionManager mngr = CoreModel.getDefault().getProjectDescriptionManager();
		
		ICProjectDescription des = mngr.getProjectDescription(newProject, false);
		if(des.isCdtProjectCreating()){
			des = mngr.getProjectDescription(newProject, true);
			des.setCdtProjectCreated();
			mngr.setProjectDescription(newProject, des, false, null);
			return true;
		}
		return false;
	}
	
	
	
	private boolean invokeRunnable(IRunnableWithProgress runnable) {
		IRunnableWithProgress op= new WorkspaceModifyDelegatingOperation(runnable);
		try {
			getContainer().run(true, true, op);
		} catch (InvocationTargetException e) {
			CUIPlugin.errorDialog(getShell(), title, message, e.getTargetException(), false);
			clearProject();
			return false;
		} catch  (InterruptedException e) {
			clearProject();
			return false;
		}
		return true;
	}

	private IRunnableWithProgress getRunnable(final boolean onFinish) {
		return new IRunnableWithProgress() {
			public void run(IProgressMonitor imonitor) throws InvocationTargetException, InterruptedException {
				getShell().getDisplay().syncExec(new Runnable() {
					public void run() { 
						try {
							newProject = createIProject(lastProjectName, lastProjectLocation);
							if (newProject != null) 
							{
								prj_helper.createProject(newProject, onFinish);
								setNewBuilder(newProject);
							}
						} catch (CoreException e) {	CUIPlugin.getDefault().log(e); }
						
					}
				});
			}
		};
	}
	
	public IProject createIProject(final String name, final URI location) throws CoreException{
		if (newProject != null)	return newProject;
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		final IProject newProjectHandle = root.getProject(name);
		
		if (!newProjectHandle.exists()) {
//			IWorkspaceDescription workspaceDesc = workspace.getDescription();
//			workspaceDesc.setAutoBuilding(false);
//			workspace.setDescription(workspaceDesc);
			IProjectDescription description = workspace.newProjectDescription(newProjectHandle.getName());
			if(location != null)
				description.setLocationURI(location);
			newProject = CCorePlugin.getDefault().createCDTProject(description, newProjectHandle, new NullProgressMonitor());
		} else {
			IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					newProjectHandle.refreshLocal(IResource.DEPTH_INFINITE, monitor);
				}
			};
			NullProgressMonitor monitor = new NullProgressMonitor();
			workspace.run(runnable, root, IWorkspace.AVOID_UPDATE, monitor);
			newProject = newProjectHandle;
		}
        
		// Open the project if we have to
		if (!newProject.isOpen()) {
			newProject.open(new NullProgressMonitor());
		}
		return continueCreation(newProject);	
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		this.workbench = workbench;
	}
	
	
	
	@Override
	public void addPages() {
		// TODO Auto-generated method stub
		//super.addPages();
		fMainPage = new OSPMainWizardPage(isEmptyProject);
		addPage(fMainPage);
		
		fSelectOspSdkPage = new SelectOspSdkPage();
		addPage(fSelectOspSdkPage);

		fDeviceConfigPage = new DeviceConfigPage();
		addPage(fDeviceConfigPage);

		fFormSettingPage = new FormSettingPage();
		addPage(fFormSettingPage);
		
//		fAppFeaturePage = new AppFeaturePage();
//		addPage(fAppFeaturePage);
		
		fAutoScrollingSettingPage = new AutoScrollingSettingPage();
		addPage(fAutoScrollingSettingPage);
		
		fBasicSettingPage = new BasicSettingPage();
		addPage(fBasicSettingPage);
		
		fSelectConfigPage = new SelectConfigPage();
		addPage(fSelectConfigPage);
		
		fSummaryPage = new SummaryPage();
		addPage(fSummaryPage);
	}

	public String[] getNatures() {
		return new String[] { CProjectNature.C_NATURE_ID, CCProjectNature.CC_NATURE_ID };
	}

	protected IProject continueCreation(IProject prj) {
		try {
			CProjectNature.addCNature(prj, new NullProgressMonitor());
			CCProjectNature.addCCNature(prj, new NullProgressMonitor());
		} catch (CoreException e) {e.printStackTrace();}
		return prj;
	}

	public OSPCreateDataStore getDataStore() {
		// TODO Auto-generated method stub
		return dataStore;
	}
	
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		fConfigElement= config;
	}	
	
	private void clearProject() {
		if (lastProjectName == null) return;
		try {
			ResourcesPlugin.getWorkspace().getRoot().getProject(lastProjectName).delete(!existingPath, true, null);
		} catch (CoreException ignore) {} // ignore
		newProject = null;
		lastProjectName = null;
		lastProjectLocation = null;
	}

	protected void setNewBuilder(IProject project)
	{
		IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(project);
		if( info != null  && info.getManagedProject() != null)
		{
			
			ICommand cmd[] = ((Project) project).internalGetDescription().getBuildSpec(false);
			if( cmd != null )
			{
				for( int i = 0 ; i < cmd.length; i++ )
				{
					if( cmd[i].getBuilderName().equals(CommonBuilder.BUILDER_ID))
						cmd[i].setBuilderName(OspBuilder.BUILDER_ID);
				}
			}
		}
	}
	
	public void setEmptyProject(boolean flag)
	{
		isEmptyProject = flag;
	}	
	
    public IWizardPage getNextPage(IWizardPage page) {
    	
    	if( dataStore.getProjectType() != IConstants.PRJ_TYPE_APP_FORM )
    	{
    		if( page instanceof DeviceConfigPage)
    		{
    			page = super.getNextPage(page);
    		}
    	}
        return super.getNextPage(page);
    }
	
    public IWizardPage getPreviousPage(IWizardPage page) {
    	
    	if( dataStore.getProjectType() != IConstants.PRJ_TYPE_APP_FORM )
    	{
    		if( page instanceof BasicSettingPage )
    		{
    			page = super.getPreviousPage(page);
    		}
    	}
        return super.getPreviousPage(page);
    }
    

    
    
}

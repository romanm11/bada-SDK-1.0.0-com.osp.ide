package com.osp.ide.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICResourceDescription;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IResourceInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.PropertyManager;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.PreferenceConstants;
import org.eclipse.cdt.ui.newui.CDTPropertyManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;

import com.osp.ide.IConstants;

public class WorkspaceUtils {
	
	public static Display getStandardDisplay() {
		Display display= Display.getCurrent();
		if (display == null) {
			display= Display.getDefault();
		}
		return display;		
	}

	
	public static void showView(String viewId)
	{
		final String id = viewId;
		getStandardDisplay().asyncExec(new Runnable() {
	        public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if(window != null)
				{
					IWorkbenchPage page= window.getActivePage();
			        if (page != null) {
			        	try {
							page.showView(id);
							
			        	} catch (PartInitException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
				}
				
	        }});
	}
	
	
	public static void ActiveConsoleView(IConsole console)
	{
		final IConsole ac_console = console;
		
		getStandardDisplay().asyncExec(new Runnable() {
	        public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if(window != null)
				{
					IWorkbenchPage page= window.getActivePage();
			        if (page != null) {
			        	try {
			        		IConsoleView view = (IConsoleView)page.showView(IConsoleConstants.ID_CONSOLE_VIEW);
			        		if( view != null ) view.display(ac_console);
			        	} catch (PartInitException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
				}
				
	        }});
	}	
	
	
	public static void showOutputView()
	{
		final String id = IConstants.VIEW_ID_OUTPUT;
		getStandardDisplay().asyncExec(new Runnable() {
	        public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if(window != null)
				{
					IWorkbenchPage page= window.getActivePage();
			        if (page != null) {
			        	try {

			        		// if already view active
		        			IViewReference viewRef = page.findViewReference(id);
		        			if( viewRef == null )
		        			{
			        			// open new view not active
			        			page.showView(id, null, IWorkbenchPage.VIEW_CREATE);
			        			viewRef = page.findViewReference(id);
		        			}
		        			
		        			// if not minimize active view
		        			if( viewRef!= null)
		        			{
		        				if( !viewRef.isFastView() ) page.showView(id);

		        			}
		        			
			        	} catch (PartInitException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
				}
	        }});
	}	
	
	static public void openEditor(IFile file)
	{
		final IFile openFile = file;
		getStandardDisplay().asyncExec(new Runnable() {
	        public void run() {
				if( PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null)
				{
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					if( page != null )
						try {
							IDE.openEditor(page, (IFile) openFile, true);
						} catch (PartInitException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        }
			}
        });
	}
	
	static public void refreshOSPProjects()
	{
		try {		
			ICProject[] cProjects = CoreModel.getDefault().getCModel().getCProjects();
			for (int i = 0; i < cProjects.length; i++) {
				
				if( cProjects[i].getProject().isAccessible())
					refreshOspProject(cProjects[i].getProject());
				
			}
		} catch (CModelException e) {
			e.printStackTrace();
		}		
	}
	
	static void updateCView(IProject prj) {
		if (prj == null) return;  
		IWorkbenchPartReference refs[] = CUIPlugin.getActiveWorkbenchWindow().getActivePage().getViewReferences();
		for (IWorkbenchPartReference ref : refs) {
			IWorkbenchPart part = ref.getPart(false);
			
			if (part != null && part instanceof IPropertyChangeListener)
				((IPropertyChangeListener)part).propertyChange(new PropertyChangeEvent(prj, PreferenceConstants.PREF_SHOW_CU_CHILDREN, null, null));
		}
	}	
	
	static void updateProjectExploreView(IProject prj) {
		if (prj == null) return;  
		IWorkbenchPartReference refs[] = CUIPlugin.getActiveWorkbenchWindow().getActivePage().getViewReferences();
		for (IWorkbenchPartReference ref : refs) {
			IWorkbenchPart part = ref.getPart(false);
			
			if (part != null&&  part instanceof ProjectExplorer )
			{
				StructuredViewer viewer = ((ProjectExplorer)part).getCommonViewer();
				if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) 
					viewer.refresh(prj);				
				break;
			}
		}
	}	
	
	static public void touchOspProject(final IProject project)
	{
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(project);
				
				IConfiguration allCfgs[] = info.getManagedProject().getConfigurations();
				
				try {
					ManagedBuildManager.updateCoreSettings(project, allCfgs, true);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
	}
	
	static public void refreshOspProject(IProject project)
	{

		try {
			ICProjectDescription prjd = CoreModel.getDefault().getProjectDescription(project);
			
			ICConfigurationDescription[] cfgs = CDTPropertyManager.getProjectDescription(project).getConfigurations();

			if(cfgs != null )
			{
				for( int i =0; i < cfgs.length; i++ )
				{
					Configuration cfg01 = (Configuration)ManagedBuildManager.getConfigurationForDescription(prjd.getConfigurationById(cfgs[i].getId()));
//					cfg01.enableInternalBuilder(cfg01.isInternalBuilderEnabled());
					cfg01.getBuilder().setDirty(true);
					cfg01.setRebuildState(true);
					PropertyManager.getInstance().serialize(cfg01);
				}
				
				CoreModel.getDefault().setProjectDescription(project, prjd);
				
				updateCView(project);
				updateProjectExploreView(project);
			}
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(project);
//		IConfiguration[] cfgs = info.getManagedProject().getConfigurations();
//		for (IConfiguration cfg : cfgs) {
//			cfg.setRebuildState(true);
//			PropertyManager.getInstance().serialize(cfg);
//		}
		
	}

	public static String getIdeInstalledPath()
	{
		if( Platform.getInstallLocation() != null)
		{
			String p = new Path (Platform.getInstallLocation().getURL().getPath()).toOSString();
			
			return p;
			
		}
		
		return "";
	}

	public static String getDefaultBadaSdkRoot()
	{
		if( Platform.getInstallLocation() != null)
		{
			String sdkRoot = new Path (Platform.getInstallLocation().getURL().getPath()).removeLastSegments(1).toOSString();
			
			if( sdkRoot.endsWith("\\")) sdkRoot = sdkRoot.substring(0, sdkRoot.length()-1);
			
			return sdkRoot;
			
		}
		
		return "";
	}
}

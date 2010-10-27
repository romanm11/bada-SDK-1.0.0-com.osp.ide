package com.osp.ide.internal.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.cdt.core.model.ICContainer;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.make.ui.actions.AbstractTargetAction;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.console.IConsoleConstants;

import com.osp.ide.IConstants;
import com.osp.ide.core.badaNature;
import com.osp.ide.internal.ui.PrivileageCheckJob;
import com.osp.ide.utils.WorkspaceUtils;

public class PrivilegeCheckAction extends AbstractTargetAction {
	
	private IContainer fContainer;
	
	protected IContainer getSelectedContainer() {
		return fContainer;
	}	
	
	public void run(IAction action) {
		IContainer container = getSelectedContainer();

		if (container != null && container instanceof IResource) {
			//Shell parentshell = getShell();
			
			PrivileageCheckJob job = new PrivileageCheckJob(container.getProject());
			job.schedule();
		}
	}
	
/*
	public void selectionChanged(IAction action, ISelection selection) {
		boolean enabled = false;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object obj = sel.getFirstElement();
			if (obj instanceof ICElement) {
				if ( obj instanceof ICContainer || obj instanceof ICProject) {
					fContainer = (IContainer) ((ICElement) obj).getUnderlyingResource();
				} else {
					obj = ((ICElement)obj).getResource();
					if ( obj != null) {
						fContainer = ((IResource)obj).getParent();
					}
				}
			} else if (obj instanceof IResource) {
				if (obj instanceof IContainer) {
					fContainer = (IContainer) obj;
				} else {
					fContainer = ((IResource)obj).getParent();
				}
			} else {
				fContainer = null;
			}
			//if (fContainer != null && MakeCorePlugin.getDefault().getTargetManager().hasTargetBuilder(fContainer.getProject())) {
			if (fContainer != null && isEnableProject(fContainer.getProject())) {
				enabled = true;
			}
			
		}
		action.setEnabled(enabled);
	}
*/
	
	public void selectionChanged(IAction action, ISelection selection) {
		boolean enabled = false;
		IContainer container = null;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object obj = sel.getFirstElement();
			if (obj instanceof ICElement) {
				if ( obj instanceof ICContainer || obj instanceof ICProject) {
					container = (IContainer) ((ICElement) obj).getUnderlyingResource();
				} else {
					obj = ((ICElement)obj).getResource();
					if ( obj != null) {
						container = ((IResource)obj).getParent();
					}
				}
			} else if (obj instanceof IResource) {
				if (obj instanceof IContainer) {
					container = (IContainer) obj;
				} else {
					container = ((IResource)obj).getParent();
				}
			} else {
				container = null;
			}
//			if (fContainer != null && isEnableProject(fContainer.getProject())) {
//				enabled = true;
//			}
		}
		
		if( container != null ) fContainer = container;
	
		if (fContainer != null && isEnableProject(fContainer.getProject())) 
			enabled = true;
		
		action.setEnabled(enabled);
	}	
	
	private boolean isEnableProject(IProject project)
	{
		if(project == null ) return false;
		
		boolean enable = false;
		try {
			enable = project.hasNature(badaNature.OSP_NATURE_ID);
			if( enable )
			{
				IConfiguration currentConfig = null;
				IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(project);		
				currentConfig = info.getDefaultConfiguration();
				if( currentConfig == null )
				{
					enable = false;
				}
//				else
//				{
//					String configName = currentConfig.getName();
//					
//					if( configName.equals(IConstants.CONFIG_SIMUAL_DEBUG_NAME) ||
//					configName.equals(IConstants.CONFIG_TARGET_DEBUG_NAME) ||
//					configName.equals(IConstants.CONFIG_TARGET_RELEASE_NAME) )
//					enable = true;
//				else
//					enable = false;					
//				}			
			}
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			enable = false;
		}
		
		return enable;
	}
}


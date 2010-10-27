package com.osp.ide.internal.actions;



import org.eclipse.cdt.core.model.ICContainer;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.make.ui.actions.AbstractTargetAction;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.osp.ide.core.badaNature;
import com.osp.ide.internal.ui.GetDebugInfoJob;

public class GetDebugInfoAction extends AbstractTargetAction {
	
	private IContainer fContainer;
	
	protected IContainer getSelectedContainer() {
		return fContainer;
	}	
	
	public void run(IAction action) {
		IContainer container = getSelectedContainer();

		if (container != null && container instanceof IResource) {
			//Shell parentshell = getShell();
			
			GetDebugInfoJob job = new GetDebugInfoJob(container.getProject(), true);
			job.schedule();
		}
	}
	

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
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			enable = false;
		}
		
		return enable;
	}
}


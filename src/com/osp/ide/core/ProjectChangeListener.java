package com.osp.ide.core;

import java.io.File;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.fasoo.bada.FAPIAnalysis;
import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;
import com.osp.ide.utils.WorkspaceUtils;

public class ProjectChangeListener implements IResourceChangeListener {

	public ProjectChangeListener() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
				IResourceChangeEvent.POST_CHANGE );		
	}
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		// TODO Auto-generated method stub
		if (event.getSource() instanceof IWorkspace) {
			IResourceDelta delta = event.getDelta();
			
			if(event.getType() ==  IResourceChangeEvent.POST_CHANGE)
			{
				IResourceDelta[] removed = delta.getAffectedChildren(IResourceDelta.REMOVED);
				IResourceDelta[] added = delta.getAffectedChildren(IResourceDelta.ADDED);
				
				if(removed != null && added != null)
				{
					if(removed.length == 1 && added.length == 1)
					{
						IResource resource = added[0].getResource();		
						if( resource != null && resource.getType() == IResource.PROJECT )
						{
							IProject prj = ((IProject)resource);
							try {
								if( prj.hasNature(badaNature.OSP_NATURE_ID) || prj.hasNature(IConstants.CDT_C_NATURE))
								{
									// reset project macro
									WorkspaceUtils.touchOspProject(prj);
									
								}
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					}
				}
				
				IResourceDelta[] changed = delta.getAffectedChildren(IResourceDelta.CHANGED);
				if( changed != null )
				{
					for( int i  = 0; i < changed.length; i++ )
					{
						IResourceDelta[] childs = changed[i].getAffectedChildren();
						
						if( childs != null)
						{
							for( int j  = 0; j < childs.length; j++ )
							{
								IResource res = childs[j].getResource();
								
								if( res instanceof IFolder )
								{
									IResourceDelta[] removeChild = childs[j].getAffectedChildren(IResourceDelta.REMOVED);
									
									if( removeChild != null)
									{
										for( int k  = 0; k < removeChild.length; k++ )
										{
											IResource r_res = removeChild[k].getResource();
											try {
												if( r_res.getProject().hasNature(badaNature.OSP_NATURE_ID))
												{
													String ext = r_res.getFileExtension();
													
													if( ext != null && ext.length() > 0 )
													{
														ext = ext.toLowerCase();
														if( ext.endsWith("cpp") || ext.endsWith("c") || ext.endsWith("h"))
														{
															notifySoureFileRemoved(r_res);
														}
													}
												}
											} catch (CoreException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
									}
								}
								else if( res.getFullPath().segmentCount() == 2 && res.getName().equals(IConstants.MANIFEST_FILE))
								{
									IdePlugin.getDefault().manifestChanged(res.getProject());
								}
							}
						}
					}
				}
								
			}
		}		
	}
	
	private void notifySoureFileRemoved(IResource res)
	{
		String repositoryRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString() + "\\" + IConstants.DIR_REPOSITORY + "\\" + res.getProject().getName();
		
		ICProjectDescription prjd = CoreModel.getDefault().getProjectDescription(res.getProject(), false);
		if (prjd != null)
		{
			ICConfigurationDescription[] configDescs = prjd.getConfigurations();
			if (configDescs ==null || configDescs.length == 0) return;
			
			for( int i = 0; i < configDescs.length; i++ )
			{
				String workingDirectory = repositoryRoot + "\\" + configDescs[i].getName();
				if( (new File(workingDirectory)).exists() )
				{
					FAPIAnalysis.FAnalysisDelete(workingDirectory, res.getName());
				}
			}
		}
	}
}

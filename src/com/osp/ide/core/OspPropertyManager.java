package com.osp.ide.core;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.osp.ide.IConstants;

public class OspPropertyManager implements IResourceChangeListener {

	private Map<IProject, OspPropertyStore> fManifestMap = new HashMap<IProject, OspPropertyStore>();
	
	public OspPropertyManager() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
				IResourceChangeEvent.PRE_DELETE );		
	}
	
	
	public OspPropertyStore getOspPropertyStore(IProject prj)
	{
		OspPropertyStore store = fManifestMap.get(prj);
		
		if( store == null )
		{
			store = new OspPropertyStore(prj);
			fManifestMap.put(prj, store);
		}
		
		return store;
	}


	public void clearAll()
	{
		fManifestMap.clear();
	}
	
	public void projectDeleted(IProject prj) {
		// TODO Auto-generated method stub
		fManifestMap.remove(prj);
	}


	public void resourceChanged(IResourceChangeEvent event) {
		// TODO Auto-generated method stub
		if (event.getSource() instanceof IWorkspace) {
			IResourceDelta delta = event.getDelta();
			IResource resource = event.getResource();
			switch(event.getType()){
				case IResourceChangeEvent.PRE_DELETE :
				try{
					if (resource.getType() == IResource.PROJECT )
					{
						IProject prj = ((IProject)resource);
						if( prj.hasNature(badaNature.OSP_NATURE_ID) || prj.hasNature(IConstants.CDT_C_NATURE))
						{
							this.projectDeleted(prj);
						}
					}
					
				}catch (CoreException e){
					e.printStackTrace();	
				}
				break;
			}
		}		
	}
}

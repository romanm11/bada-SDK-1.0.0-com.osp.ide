package com.osp.ide.internal.ui.wizards.project;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.cdt.managedbuilder.buildproperties.IBuildPropertyManager;
import org.eclipse.cdt.managedbuilder.buildproperties.IBuildPropertyType;
import org.eclipse.cdt.managedbuilder.buildproperties.IBuildPropertyValue;
import org.eclipse.cdt.managedbuilder.core.BuildListComparator;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.ui.wizards.CfgHolder;

import com.osp.ide.IConstants;

public class ToolChainUtil {
	
	private static final String SYS_OSP = "osp";	

	static public IToolChain[] getOSPToolChains(boolean supportedOnly, String artifact)
	{
		IBuildPropertyManager bpm = ManagedBuildManager.getBuildPropertyManager();
		IBuildPropertyType bpt = bpm.getPropertyType(IConstants.ARTIFACT);
		IBuildPropertyValue[] vs = bpt.getSupportedValues();
		Arrays.sort(vs, BuildListComparator.getInstance());
		
		
		ArrayList<IToolChain> items = new ArrayList<IToolChain>();
		for (int i=0; i<vs.length; i++) {
			
			if( vs[i].getId().equals(artifact) == false) continue;
			
			IToolChain[] tcs = ManagedBuildManager.getExtensionsToolChains(IConstants.ARTIFACT, vs[i].getId(), false);
			if (tcs == null || tcs.length == 0) continue;
			for (int j=0; j<tcs.length; j++) {
				if (isValid(tcs[j], supportedOnly)) 
				{
					//if( tcs[j].getId().startsWith("cdt.managedbuild.toolchain.osp.gnu"))
					items.add(tcs[j]);
				}
			}
		}
		return (IToolChain[])items.toArray(new IToolChain[items.size()]);
	}
	
	static protected boolean isValid(IToolChain tc, boolean supportedOnly) {
		
		// Do not do further check if all toolchains are permitted	
		if (!supportedOnly) 
			return true;
		
		// Filter off unsupported and system toolchains
		if (tc == null || !tc.isSupported() || tc.isAbstract() || tc.isSystemObject() )
			return false;
		
		// Check for platform compatibility

		String[] osList = tc.getOSList();
		if( osList != null )
		{
			for( int i = 0; i < osList.length; i++ )
			{
				if( SYS_OSP.equals(osList[i]) ) return true;
			}
			
		}
		
		return false;
		//return ManagedBuildManager.isPlatformOk(tc);
	}
	
	static public CfgHolder[] getDefaultCfgs(IToolChain tc, String artifact) {

		ArrayList<CfgHolder> out = new ArrayList<CfgHolder>();

		CfgHolder[] cfgs = null;
		cfgs = CfgHolder.cfgs2items(ManagedBuildManager.getExtensionConfigurations(tc, IConstants.ARTIFACT, artifact));

		if (cfgs == null) return null;
			
		for (int j=0; j<cfgs.length; j++) {
			//if (cfgs[j].isSystem() || (handler.supportedOnly() && !cfgs[j].isSupported())) continue;
			out.add(cfgs[j]);
		}

		return out.toArray(new CfgHolder[out.size()]);
	}	
	
}

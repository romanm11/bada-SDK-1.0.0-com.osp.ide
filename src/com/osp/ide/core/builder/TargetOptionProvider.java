package com.osp.ide.core.builder;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;

import com.osp.ide.IdePlugin;
import com.osp.ide.core.ManifestXmlStore;

public class TargetOptionProvider {
	
	private static TargetOptionProvider thisInstance	= null;

	private static final String MODEL_DEFAULT = "Default";
	
	Map taregtOptionList = null;
	Map optionMap = null;
	
	public static TargetOptionProvider getInstance() {
		if (TargetOptionProvider.thisInstance == null)
			thisInstance = new TargetOptionProvider();
		return thisInstance;
	}
	
	private TargetOptionProvider()
	{
		taregtOptionList = new HashMap();
	}
	
	private BadaBuildOption LoadOption(IProject project)
	{
		// using manifest.xml
		boolean bUseManifest = BadaBuildOptionLoader.useManifest();
		ManifestXmlStore maniStore = new ManifestXmlStore(project); 
		String cpuType = "";
		if (maniStore != null) cpuType = maniStore.getCpu();		
		
		BadaBuildOption option =null;
		if( optionMap == null )
			optionMap = BadaBuildOptionLoader.load();		
		
		if( optionMap != null && optionMap.size() > 0 )
		{
			String model = null;
			if (bUseManifest)	// using manifest.xml
				model = cpuType;
			else				// using directory name as model 
				model = IdePlugin.getDefault().getModel(project);
			if( model != null && model.length() > 0 )
			{
				if (model.indexOf("_") > 0) model = model.substring(0, model.indexOf("_"));
				
				option = (BadaBuildOption) optionMap.get(model);
				if (option == null) {
					// if the first character of model folder is lower case
					String model2 = model.substring(0, 1).toUpperCase() + model.substring(1);
					option = (BadaBuildOption) optionMap.get(model2);
				}
				
				if( option != null ) return option;
			}
			
			option = (BadaBuildOption) optionMap.get(MODEL_DEFAULT);
			if( option != null ) return option;
		}
		
		return BadaBuildOptionLoader.getGeneralOption();
		
	}
	
	public BadaBuildOption getTargetOption(IProject project)
	{
		BadaBuildOption option = (BadaBuildOption) taregtOptionList.get(project.getName());
		
		if( option != null ) return option;
		
		option = LoadOption(project);
		
		taregtOptionList.put(project.getName(), option);
		
		return option;
	}
	
	
	public String getTargetSpecificCompileOptions(IProject project)
	{
		BadaBuildOption option = getTargetOption(project);
		
		if(option == null) return "";
		else return option.getCompileOption();
	}
	
	public String getTargetSpecificLinkOptions(IProject project)
	{
		BadaBuildOption option = getTargetOption(project);
		
		if(option == null) return "";
		else return option.getLinkOption();
	}
	
	public String getTargetSpecificPostfixLinkOptions(IProject project)
	{
		BadaBuildOption option = getTargetOption(project);
		
		if (option == null) return "";
		else return option.getPostfixLinkOption();
	}
	
	public String[] getTargetSpecificLinkFilter(IProject project)
	{
		BadaBuildOption option = getTargetOption(project);
		
		if (option == null) return null;
		else return option.getLinkFilter();
	}
		
	public void modelChanged(IProject project)
	{
		taregtOptionList.remove(project.getName());
	}	
	
}

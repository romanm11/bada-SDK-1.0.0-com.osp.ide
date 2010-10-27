package com.osp.ide.core.variable;


import org.eclipse.cdt.core.cdtvariables.ICdtVariable;
import org.eclipse.cdt.managedbuilder.core.IManagedProject;
import org.eclipse.cdt.managedbuilder.internal.macros.BuildMacro;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacro;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;
import org.eclipse.cdt.managedbuilder.macros.IProjectBuildMacroSupplier;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;
import com.osp.ide.core.builder.TargetOptionProvider;

public class OSPProjectMacroSupplier implements IProjectBuildMacroSupplier {

	public IBuildMacro getMacro(String macroName, IManagedProject project,
			IBuildMacroProvider provider) {
		
		BuildMacro macro = null;

		if(macroName.equals(IConstants.ENV_SDKROOT)) {
			String sdkRoot = IdePlugin.getDefault().getSDKPath(project.getOwner().getProject());
			
			macro = new BuildMacro(macroName, ICdtVariable.VALUE_PATH_DIR, 
					sdkRoot.replaceAll("\\\\", "/"));
		}
		else if(macroName.equals(IConstants.ENV_PROJECT_ROOT)) {
			String root = project.getOwner().getProject().getLocation().toString();
			
			macro = new BuildMacro(macroName, ICdtVariable.VALUE_PATH_DIR, 
					root.replaceAll("\\\\", "/"));
		}
				
		else if(macroName.equals(IConstants.ENV_SIMULATOR_LIB_PATH)) {
			String sim_path = IdePlugin.getDefault().getSDKPath(project.getOwner().getProject())
							+ IConstants.FILE_SEP_BSLASH + IConstants.DIR_MODEL
							+ IConstants.FILE_SEP_BSLASH + IdePlugin.getDefault().getModel(project.getOwner().getProject())
							+ IConstants.FILE_SEP_BSLASH + IConstants.DIR_SIMULATOR;
			
			macro = new BuildMacro(macroName, ICdtVariable.VALUE_PATH_DIR, 
					sim_path.replaceAll("\\\\", "/"));
		} else if(macroName.equals(IConstants.ENV_SCREEN_DIR)) {
				String dir = IdePlugin.getDefault().getSimualLibDir(project.getOwner().getProject());
				
			 macro = new BuildMacro(macroName, ICdtVariable.VALUE_TEXT, dir);
		} else if( macroName.startsWith("workspace_loc:")) {
			
			int index = macroName.indexOf(":");

			String param = macroName.substring(index+1);
			String varValue = project.getOwner().getProject().getLocation().removeLastSegments(1).toOSString();
			
			varValue += param.replaceAll("/", "\\\\");
			
			macro = new BuildMacro(macroName, ICdtVariable.VALUE_PATH_DIR, 
					varValue);
		}
/*		
		else if(macroName.equals(IConstants.ENV_MODEL_NAME)) {
			String dir = IdePlugin.getDefault().getModel(project.getOwner().getProject());
		
			macro = new BuildMacro(macroName, ICdtVariable.VALUE_TEXT, dir);
		}		
*/		
		return macro;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.macros.IProjectBuildMacroSupplier#getMacros(org.eclipse.cdt.managedbuilder.core.IManagedProject, org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider)
	 */
	public IBuildMacro[] getMacros(IManagedProject project,
			IBuildMacroProvider provider) {
		
		int addCount=0;
//		String macroName = IConstants.ENV_OSPROOT;
//		String macroPath = IdePlugin.getDefault().getOSPRoot();
//		BuildMacro macroOspRoot = new BuildMacro(macroName, ICdtVariable.VALUE_PATH_DIR, 
//				macroPath.replaceAll("\\\\", "/"));
//		addCount++;
		
		String macroName = IConstants.ENV_SDKROOT;
		String macroPath = IdePlugin.getDefault().getSDKPath(project.getOwner().getProject());
		BuildMacro macroSdkPath = new BuildMacro(macroName, ICdtVariable.VALUE_PATH_DIR, 
				macroPath.replaceAll("\\\\", "/"));		
		addCount++;
		
		macroName = IConstants.ENV_PROJECT_ROOT;
		macroPath = project.getOwner().getProject().getLocation().toString();
		BuildMacro macroProjectPath = new BuildMacro(macroName, ICdtVariable.VALUE_PATH_DIR, 
				macroPath.replaceAll("\\\\", "/"));		
		addCount++;
		
		macroName = IConstants.ENV_TARGRT_LIB_PATH;
//		macroPath = IdePlugin.getDefault().getSDKPath(project.getOwner().getProject()) + IConstants.PATH_TARGRT_LIB;
		macroPath = IdePlugin.getDefault().getSDKPath(project.getOwner().getProject())
					+ IConstants.FILE_SEP_BSLASH + IConstants.DIR_MODEL
					+ IConstants.FILE_SEP_BSLASH + IdePlugin.getDefault().getModel(project.getOwner().getProject())
					+ IConstants.FILE_SEP_BSLASH + IConstants.DIR_TARGET;		
		
		BuildMacro macroTargetLibPath = new BuildMacro(macroName, ICdtVariable.VALUE_PATH_DIR, 
				macroPath.replaceAll("\\\\", "/"));		
		addCount++;		
		
		macroName = IConstants.ENV_SIMULATOR_LIB_PATH;
		macroPath = IdePlugin.getDefault().getSDKPath(project.getOwner().getProject())
					+ IConstants.FILE_SEP_BSLASH + IConstants.DIR_MODEL
					+ IConstants.FILE_SEP_BSLASH + IdePlugin.getDefault().getModel(project.getOwner().getProject())
					+ IConstants.FILE_SEP_BSLASH + IConstants.DIR_SIMULATOR;
		
		BuildMacro macroSimualLibPath = new BuildMacro(macroName, ICdtVariable.VALUE_PATH_DIR, 
				macroPath.replaceAll("\\\\", "/"));		
		addCount++;		

		
		macroName = IConstants.ENV_SCREEN_DIR;
		macroPath = IdePlugin.getDefault().getSimualLibDir(project.getOwner().getProject());	
		
		BuildMacro macroScreenDir = new BuildMacro(macroName, ICdtVariable.VALUE_TEXT, 
				macroPath.replaceAll("\\\\", "/"));		
		addCount++;		
				
/*		
		macroName = IConstants.ENV_MODEL_NAME;
		macroPath = IdePlugin.getDefault().getModel(project.getOwner().getProject());	
		
		BuildMacro macroModel = new BuildMacro(macroName, ICdtVariable.VALUE_TEXT, 
				macroPath);		
		addCount++;		
*/

		IBuildMacro[] macros = new IBuildMacro[addCount];
		
		macros[0] = macroSdkPath;
		macros[1] = macroProjectPath;
		macros[2] = macroTargetLibPath;
		macros[3] = macroSimualLibPath;
		macros[4] = macroScreenDir;
//		macros[5] = macroTargetSpecpicRoot;
		
		return macros;
		
	}
}

package com.osp.ide.internal.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.xml.sax.SAXException;

import com.fasoo.bada.FAPIAnalysis;
import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;
import com.osp.ide.utils.WorkspaceUtils;

public class PrivileageCheckJob extends Job {

	IProject fProject = null;
	
	public PrivileageCheckJob(IProject project) {
		super("Privilege Check");
		// TODO Auto-generated constructor stub
		this.fProject = project;
	}
	
	private MessageConsole findConsole(String name) {
		
		  ConsolePlugin plugin = ConsolePlugin.getDefault();
	      IConsoleManager conMan = plugin.getConsoleManager();
	      IConsole[] existing = conMan.getConsoles();
	      for (int i = 0; i < existing.length; i++) {
	    	  if (name.equals(existing[i].getName()))
//	    		  conMan.removeConsoles(new IConsole[] {existing[i]});
//	    	  	  break;
		          return (MessageConsole) existing[i];
	      }
	         
	      //no console found, so create a new one
	      MessageConsole myConsole = new MessageConsole(name, null);
	      conMan.addConsoles(new IConsole[]{myConsole});
	      return myConsole;
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
		monitor.beginTask("Privilige checking ...", IProgressMonitor.UNKNOWN); 
		
		List <String> dispList = null;
		
		IConfiguration currentConfig = null;
		IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(fProject);		
		currentConfig = info.getDefaultConfiguration();
		if( currentConfig == null )
		{
			monitor.done(); 
			return new Status(Status.ERROR, IdePlugin.PLUGIN_ID, "Default configuration not found."); 
		}
		
//		if(currentConfig.getName().contains(IConstants.CONFIG_SIMUAL_DEBUG_NAME) )
//		{
//			monitor.done(); 
//			return new Status(Status.ERROR, IdePlugin.PLUGIN_ID, "Simulator configuration is not support."); 
//		}			
		

		MessageConsole myConsole = findConsole("Privilege");
		myConsole.clearConsole();
		WorkspaceUtils.ActiveConsoleView(myConsole);
		
		
		MessageConsoleStream out = myConsole.newMessageStream();
		
		try {
			out.println("\n**** Privilige check of configuration " + currentConfig.getName() +  " for project "+ fProject.getName() + " ****\n");
			out.flush();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		
		String repositoryRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + "/" + IConstants.DIR_REPOSITORY + "/" + fProject.getName() + "/" + currentConfig.getName();
		String projectRoot = fProject.getLocation().makeAbsolute().toOSString() + IConstants.FILE_SEP_BSLASH;
		String sdkHome = IdePlugin.getDefault().getSDKPath(fProject);
		String sbuildRoot = sdkHome + IConstants.PATH_TOOLS + java.io.File.separatorChar + "sbuild";
		
		try {
			dispList = FAPIAnalysis.FPrivilegeAnlaysis(sdkHome + IConstants.DIR_IDE,
											fProject.getName(),
											projectRoot + IConstants.MANIFEST_FILE,
											sbuildRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_PRIVGROUP_XML,
											repositoryRoot);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			dispList = new ArrayList<String>();
			dispList.add(e1.getMessage());
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			dispList = new ArrayList<String>();
			dispList.add(e1.getMessage());
			
		}
		
		try {

			if( dispList != null )
			{
				Iterator<String> itList = dispList.iterator();
				while (itList.hasNext()) {
					out.println(itList.next()); // show debug message
					out.flush();
				}
				
			}
			else  // error
			{
				out.println("Unknown error occured.\n");
				out.flush();
			}

			out.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		monitor.done(); 
		
		return Status.OK_STATUS;
	}

}

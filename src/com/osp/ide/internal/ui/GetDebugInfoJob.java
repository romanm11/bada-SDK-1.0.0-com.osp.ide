package com.osp.ide.internal.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.cdt.utils.spawner.ProcessFactory;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;
import com.osp.ide.core.PathResolver;
import com.osp.ide.utils.BrokerUtils;
import com.osp.ide.utils.FileUtil;
import com.osp.ide.utils.WorkspaceUtils;

public class GetDebugInfoJob extends Job {

	IProject fProject = null;
	private boolean useConsole = true;
	
	public GetDebugInfoJob(IProject project, boolean useConsole) {
		super("Get Debug Information");
		// TODO Auto-generated constructor stub
		this.fProject = project;
		this.useConsole = useConsole;
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

		monitor.beginTask("Get Debug Information ...", IProgressMonitor.UNKNOWN);
		
		String brokerExe = BrokerUtils.verifyBrokerFile(fProject);
		if ( brokerExe == null || brokerExe.length() == 0 ) {
			monitor.done(); 
			return new Status(Status.ERROR, IdePlugin.PLUGIN_ID, "Broker not found."); 
		}
		
		
		IFolder infoFolder = FileUtil.createFolder(fProject, IConstants.DIR_DEBUGINFO, monitor);
		if (infoFolder == null)
			return new Status(Status.ERROR, IdePlugin.PLUGIN_ID, "Can not create " + IConstants.DIR_DEBUGINFO + " dictory.");
		
		
		String appId = PathResolver.getApplicatuonID(fProject);
		if( appId == null || appId.length() == 0)
		{
			return new Status(Status.ERROR, IdePlugin.PLUGIN_ID, "Can not get application ID.");
			
		}
		
		MessageConsoleStream out = null;
		if( useConsole )
		{
			MessageConsole myConsole = findConsole("Debug Information");
			myConsole.clearConsole();
			WorkspaceUtils.ActiveConsoleView(myConsole);
			out = myConsole.newMessageStream();
		
			try {
				out.println("\n**** Debug Information for project "+ fProject.getName() + " ****\n");
				out.flush();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
		else
		{
			WorkspaceUtils.showView(IConstants.VIEW_ID_OUTPUT);			
		}
		
		String cmd = brokerExe 
					+ " -i " +  appId 
					+ " -r " +  IConstants.DEFAULT_TARGET_CODE_BINARY_PATH
					+ " --dbginfo-folder=" + infoFolder.getLocation().toOSString() 
					+ " --gdi-only";
		
		
		Process proc = null;
		try {
			// Execute a command without arguments
			proc = ProcessFactory.getFactory().exec(cmd, null);;
			BufferedReader stdout = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			BufferedReader stderr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}			

			BufferedReader bufferedreader = new BufferedReader(stdout);
			BufferedReader bufferedreaderErr = new BufferedReader(stderr);
			String line=null;
			while ( (line = bufferedreader.readLine()) != null ) {
				if( useConsole )
				{
					out.println(line);
					out.flush();
				}
			}
			
			while ( (line = bufferedreaderErr.readLine()) != null ) {
				if( useConsole )
				{
					out.println(line);
					out.flush();
				}
			}				
			
			if( useConsole ) out.close();

			proc.waitFor();
//			int k = proc.exitValue();
			
			try {
				infoFolder.refreshLocal(IResource.DEPTH_ONE, monitor);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			if(proc != null ) proc.destroy();					
		}			
		
		
		monitor.done(); 
		
		return Status.OK_STATUS;
	}

}

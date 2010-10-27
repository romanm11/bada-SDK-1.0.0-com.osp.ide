package com.osp.ide.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

import com.fasoo.bada.FHashBinary;
import com.fasoo.bada.FSigning;
import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;
import com.osp.ide.core.ManifestXmlStore;

public class AnalysisUtil {
	
	public static final int INX_EXT_HTB = 0;
	public static final int INX_SIGNATURE_XML = 1;
	public static final int NUMBER_OF_IDX = 2;
	
	
	private String errorString=null;
	Throwable exception = null;
	
	public IFile[] createAnalysisFile(IProject badaProject, IPath exePath, IProgressMonitor monitor) {
		return createAnalysisFile(badaProject, badaProject, exePath, monitor);
	}

	public IFile[] createAnalysisFile(IProject badaProject, IProject rootProject, IPath exePath, IProgressMonitor monitor) {
		IFile[] analFile = new IFile[NUMBER_OF_IDX];
		
		try {
			ManifestXmlStore maniStore = IdePlugin.getDefault().getManifestXmlStore(badaProject);
			
			String fileExeName = exePath.toFile().getName();
			int index = fileExeName.lastIndexOf(".");
			if( index > 0 ) fileExeName = fileExeName.substring(0, index) + IConstants.EXT_HTB;
			else fileExeName = fileExeName + IConstants.EXT_HTB;
			
			FHashBinary.FHashApplication(maniStore.getId().getBytes(),
					maniStore.getAppVersion(),
					maniStore.getSecret().getBytes(), // app secrete 32 byte
					exePath.toOSString(),
					rootProject.getLocation().toOSString() + IConstants.FILE_SEP_BSLASH  + fileExeName);
			
			IFile htbFile = rootProject.getFile(fileExeName);
			htbFile.refreshLocal(IResource.DEPTH_ONE, null);
			
			if( htbFile == null || htbFile.exists() == false)
			{
				addErrorMsg("Analysis error: " + fileExeName + " not generated.", null);
				return null;
			}
			else
			{
				analFile[INX_EXT_HTB] = htbFile;
			}
			
			String sdkHome = IdePlugin.getDefault().getSDKPath(badaProject);
			String manifestFilePath = badaProject.getLocation().append(IConstants.MANIFEST_FILE).toOSString();
			
			FSigning.FSigningPackage(sdkHome + IConstants.DIR_IDE,
					exePath.toOSString(),
					manifestFilePath,
					htbFile.getLocation().toOSString(),
					rootProject.getLocation().toOSString() + IConstants.FILE_SEP_BSLASH + IConstants.FILE_SIGNATURE_XML
					);
			
			IFile sigFile = rootProject.getFile(IConstants.FILE_SIGNATURE_XML);
			sigFile.refreshLocal(IResource.DEPTH_ONE, null);
			
			if( sigFile == null || sigFile.exists() == false)
			{
				addErrorMsg("Analysis error: " + IConstants.FILE_SIGNATURE_XML + " not generated.", null);
				return null;
			}
			else
			{
				analFile[INX_SIGNATURE_XML] = sigFile;
			}
			
			
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			addErrorMsg(NLS.bind("Analysis error occured.", e.getMessage()), e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			addErrorMsg(NLS.bind("Analysis error occured.", e.getMessage()), e);
		}		
		
		return analFile;
	}
	
	protected void addErrorMsg(String msg, Throwable e)
	{
		errorString = msg;
		exception = e;
	}
	
	public String getErrorMsg()
	{
		return errorString;
	}
	
	public boolean isErrorOccured()
	{
		return !(errorString == null);
	}
	
	public Throwable getException()
	{
		return exception;
	}
	
	
}

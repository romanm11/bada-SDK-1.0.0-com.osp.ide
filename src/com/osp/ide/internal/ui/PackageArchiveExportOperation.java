package com.osp.ide.internal.ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.wizards.datatransfer.DataTransferMessages;
import org.xml.sax.SAXException;
//import org.eclipse.ui.internal.wizards.datatransfer.ZipFileExporter;

import com.fasoo.bada.AnalysisResultFile;
import com.fasoo.bada.FAPIAnalysis;
import com.fasoo.bada.FHashBinary;
import com.fasoo.bada.FSigning;
import com.fasoo.bada.IFProgressMonitor;
import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;
import com.osp.ide.core.ManifestXmlStore;

public class PackageArchiveExportOperation implements IRunnableWithProgress {
    private ZipFileExporter exporter;

    private String destinationFilename;

    private IProgressMonitor monitor;

    private List resourcesToExport;
    
    private PackageData packageData;
    
    private IFile[] analysisFiles=null;

    private IResource resource;

    private List errorTable = new ArrayList(1); //IStatus

    private boolean useCompression = true;
    
    public static int IDX_FILE_APP_XML = 0;
    public static int IDX_FILE_EXE = 1;
    public static int IDX_FILE_MANIFEST_XML = 2;
    
    
    public class AnalysisProgressMonitor  implements IFProgressMonitor 
    {
    	private IProgressMonitor monitor;

    	public AnalysisProgressMonitor(IProgressMonitor monitor)
    	{
    		this.monitor = monitor;
    	}
    	
    	public void worked(int work) throws Exception
    	{
   			monitor.worked(work);
    	}

    	public void subTask(String task) throws Exception
    	{
    		monitor.subTask(task);
    	}
    };
    

    /**
     *	Create an instance of this class.  Use this constructor if you wish to
     *	export specific resources without a common parent resource
     *
     *	@param resources java.util.Vector
     *	@param filename java.lang.String
     */
    public PackageArchiveExportOperation(List resources, String filename) {
        super();

        // Eliminate redundancies in list of resources being exported
        Iterator elementsEnum = resources.iterator();
        while (elementsEnum.hasNext()) {
            IResource currentResource = (IResource) elementsEnum.next();
            if (isDescendent(resources, currentResource)) {
				elementsEnum.remove(); //Removes currentResource;
			}
        }

        resourcesToExport = resources;
        destinationFilename = filename;
    }

    /**
     *  Create an instance of this class.  Use this constructor if you wish
     *  to recursively export a single resource.
     *
     *  @param res org.eclipse.core.resources.IResource;
     *  @param filename java.lang.String
     */
    public PackageArchiveExportOperation(IResource res, String filename) {
        super();
        resource = res;
        destinationFilename = filename;
    }

    /**
     *  Create an instance of this class.  Use this constructor if you wish to
     *  export specific resources with a common parent resource (affects container
     *  directory creation)
     *
     *  @param res org.eclipse.core.resources.IResource
     *  @param resources java.util.Vector
     *  @param filename java.lang.String
     */
    public PackageArchiveExportOperation(IResource res, String filename, PackageData packageData) {
        this(res, filename);
        resourcesToExport = packageData.getExportResources();
        this.packageData = packageData;
    }

    /**
     * Add a new entry to the error table with the passed information
     */
    protected void addError(String message, Throwable e) {
        errorTable.add(new Status(IStatus.ERROR,
                IDEWorkbenchPlugin.IDE_WORKBENCH, 0, message, e));
    }

    /**
     *  Answer the total number of file resources that exist at or below self
     *  in the resources hierarchy.
     *
     *  @return int
     *  @param checkResource org.eclipse.core.resources.IResource
     */
    protected int countChildrenOf(IResource checkResource) throws CoreException {
        if (checkResource.getType() == IResource.FILE) {
			return 1;
		}

        int count = 0;
        if (checkResource.isAccessible()) {
            IResource[] children = ((IContainer) checkResource).members();
            for (int i = 0; i < children.length; i++) {
				count += countChildrenOf(children[i]);
			}
        }

        return count;
    }

    /**
     *	Answer a boolean indicating the number of file resources that were
     *	specified for export
     *
     *	@return int
     */
    protected int countSelectedResources() throws CoreException {
        int result = 0;
        Iterator resources = resourcesToExport.iterator();
        while (resources.hasNext()) {
			result += countChildrenOf((IResource) resources.next());
		}

        return result;
    }

    /**
     *  Export the passed resource to the destination .zip. Export with
     * no path leadup
     *
     *  @param exportResource org.eclipse.core.resources.IResource
     */
    protected void exportResource(IResource exportResource)
            throws InterruptedException {
        exportResource(exportResource, 1);
    }

    
    private String getExportedPath(IPath path, boolean isDirectory)
    {
    	String destinationName;

        if(path.segmentCount() > 1 || isDirectory == true)
        {
        	String firstDir = path.segment(0).toLowerCase(Locale.getDefault());
        	if( firstDir.equals(IConstants.DIR_RESOURCE.toLowerCase(Locale.getDefault()) ))
        	{
        		//destinationName = IConstants.BADA_APP_DIR_RES + IConstants.FILE_SEP_FSLASH + fullPath.removeFirstSegments(1);
        		destinationName = path.toString();
        	}
        	else if( firstDir.equals(IConstants.DIR_ICON.toLowerCase(Locale.getDefault()) ))
        	{
        		destinationName = IConstants.BADA_APP_DIR_RES + IConstants.FILE_SEP_FSLASH + path.removeFirstSegments(1).toString();
        	}
        	else if( firstDir.equals(IConstants.DIR_HOME.toLowerCase(Locale.getDefault()) ))
        	{
        		// uncompress share dir
        		if( path.segmentCount() > 1)
        		{
        			String secondDir = path.segment(1).toLowerCase(Locale.getDefault());
        			if( secondDir.equals(IConstants.DIR_SHARE)) return null;
        		}
        		
        		destinationName = IConstants.BADA_APP_DIR_DATA + IConstants.FILE_SEP_FSLASH + path.removeFirstSegments(1).toString();
        	}
        	else
        	{
        		destinationName = IConstants.BADA_APP_DIR_DATA + IConstants.FILE_SEP_FSLASH + path.toString();
        	}
        }
        else
        {
			destinationName = IConstants.BADA_APP_DIR_DATA + IConstants.FILE_SEP_FSLASH + path.toString();
        	
        }
    	
    	return destinationName;
    }
    
//    final static String DIR_DATA = "Data/";
//    final static String DIR_NEW_RESOURCE = "Res/";
    
    /**
     *  Export the passed resource to the destination .zip
     *
     *  @param exportResource org.eclipse.core.resources.IResource
     *  @param leadupDepth the number of resource levels to be included in
     *                     the path including the resourse itself.
     */
    protected void exportResource(IResource exportResource, int leadupDepth)
            throws InterruptedException {
        if (!exportResource.isAccessible()) {
			return;
		}

        if( exportResource.getName().startsWith(IConstants.DOT))
        {
        	return;
        }
        
        if (exportResource.getType() == IResource.FILE) {
            
            IPath fullPath = exportResource.getFullPath();
            
            fullPath = fullPath.removeFirstSegments(leadupDepth);
            
//            fullPath = fullPath.removeFirstSegments(
//                    fullPath.segmentCount() - leadupDepth);
            
            String destinationName = getExportedPath(fullPath, false);
            if( destinationName == null ) return;
            
//            if(destinationName.equals(IConstants.MANIFEST_FILE))
//            	destinationName = IConstants.MANIFEST_FILE_ORG;
            
            monitor.subTask(destinationName);

            try {
                exporter.write((IFile) exportResource, destinationName);
            } catch (IOException e) {
                addError(NLS.bind(DataTransferMessages.DataTransfer_errorExporting, exportResource.getFullPath().makeRelative(), e.getMessage()), e);
            } catch (CoreException e) {
                addError(NLS.bind(DataTransferMessages.DataTransfer_errorExporting, exportResource.getFullPath().makeRelative(), e.getMessage()), e);
            }

            monitor.worked(1);
            ModalContext.checkCanceled(monitor);
        } else {
            IResource[] children = null;

            try {
                children = ((IContainer) exportResource).members();
            } catch (CoreException e) {
                // this should never happen because an #isAccessible check is done before #members is invoked
                addError(NLS.bind(DataTransferMessages.DataTransfer_errorExporting, exportResource.getFullPath()), e);
            }

            if( children.length == 0 )
            {
            	IPath fullPath = exportResource.getFullPath();
                fullPath = fullPath.removeFirstSegments(fullPath.segmentCount() - leadupDepth);
                
                String destinationName = getExportedPath(fullPath, true);
                if( destinationName == null ) return;
                
            	try {
					exporter.writeBlankDirectory(destinationName);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					addError(NLS.bind(DataTransferMessages.DataTransfer_errorExporting, exportResource.getFullPath().makeRelative(), e.getMessage()), e);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					addError(NLS.bind(DataTransferMessages.DataTransfer_errorExporting, exportResource.getFullPath().makeRelative(), e.getMessage()), e);
				}            	
            }
            else
            {
	            for (int i = 0; i < children.length; i++) {
					//exportResource(children[i], leadupDepth + 1);
	            	exportResource(children[i], 1);
				}
            }
        }
    }

    
    protected void exportResourceFile(IFile exportResource, String destinationName)
    		throws InterruptedException {
		if (!exportResource.isAccessible()) {
			return;
		}
	    
	    monitor.subTask(destinationName);
	
	    try {
	        exporter.write((IFile) exportResource, destinationName);
	    } catch (IOException e) {
	        addError(NLS.bind(DataTransferMessages.DataTransfer_errorExporting, exportResource.getFullPath().makeRelative(), e.getMessage()), e);
	    } catch (CoreException e) {
	        addError(NLS.bind(DataTransferMessages.DataTransfer_errorExporting, exportResource.getFullPath().makeRelative(), e.getMessage()), e);
	    }
	
	    monitor.worked(1);
	    ModalContext.checkCanceled(monitor);
 
    }

    
    /**
     *	Export the resources contained in the previously-defined
     *	resourcesToExport collection
     */
    protected void exportSpecifiedResources() throws InterruptedException {
        Iterator resources = resourcesToExport.iterator();

        while (resources.hasNext()) {
            IResource currentResource = (IResource) resources.next();
            exportResource(currentResource);
        }
        
        if( packageData.getAppXml() != null )
        	exportResourceFile(packageData.getAppXml(), IConstants.BADA_APP_DIR_INFO + IConstants.FILE_SEP_FSLASH + packageData.getAppXml().getName());
        
        if( packageData.getManifestXml() != null )
        	exportResourceFile(packageData.getManifestXml(), IConstants.BADA_APP_DIR_INFO + IConstants.FILE_SEP_FSLASH + IConstants.MANIFEST_FILE);

        if( packageData.getExeFile() != null )
        	exportResourceFile(packageData.getExeFile(), IConstants.BADA_APP_DIR_BIN + IConstants.FILE_SEP_FSLASH + packageData.getExeFile().getName());
        
        
        IFile libs[] = packageData.getLibraryList();
        if(libs != null && libs.length > 0)
        {
        	for( int i = 0 ; i < libs.length; i++ )
        	{
        		exportResourceFile(libs[i], IConstants.BADA_APP_DIR_BIN + IConstants.FILE_SEP_FSLASH +libs[i].getName());
        	}
        }
        
        
        if( analysisFiles != null)
        {
        	for( int i = 0; i < analysisFiles.length; i++ )
        	{
        		if( analysisFiles[i] != null )
        		{
        			if( analysisFiles[i].getName().equals(IConstants.FILE_SIGNATURE_XML))
        				exportResourceFile(analysisFiles[i], analysisFiles[i].getName());
        			else
        				exportResourceFile(analysisFiles[i], IConstants.BADA_APP_DIR_INFO + IConstants.FILE_SEP_FSLASH + analysisFiles[i].getName());
        		}
        	}
        }
    }

    /**
     * Returns the status of the operation.
     * If there were any errors, the result is a status object containing
     * individual status objects for each error.
     * If there were no errors, the result is a status object with error code <code>OK</code>.
     *
     * @return the status
     */
    public IStatus getStatus() {
        IStatus[] errors = new IStatus[errorTable.size()];
        errorTable.toArray(errors);
        return new MultiStatus(
                IDEWorkbenchPlugin.IDE_WORKBENCH,
                IStatus.OK,
                errors,
                DataTransferMessages.FileSystemExportOperation_problemsExporting,
                null);
    }

    /**
     *	Initialize this operation
     *
     *	@exception java.io.IOException
     */
    protected void initialize() throws IOException {
       	exporter = new ZipFileExporter(destinationFilename, useCompression);
    }

    /**
     *  Answer a boolean indicating whether the passed child is a descendent
     *  of one or more members of the passed resources collection
     *
     *  @return boolean
     *  @param resources java.util.Vector
     *  @param child org.eclipse.core.resources.IResource
     */
    protected boolean isDescendent(List resources, IResource child) {
        if (child.getType() == IResource.PROJECT) {
			return false;
		}

        IResource parent = child.getParent();
        if (resources.contains(parent)) {
			return true;
		}

        return isDescendent(resources, parent);
    }

    /**
     *	Export the resources that were previously specified for export
     *	(or if a single resource was specified then export it recursively)
     */
    public void run(IProgressMonitor progressMonitor)
            throws InvocationTargetException, InterruptedException {
        this.monitor = progressMonitor;

        try {
            initialize();
        } catch (IOException e) {
            throw new InvocationTargetException(e, NLS.bind(DataTransferMessages.ZipExport_cannotOpen, e.getMessage()));
        }

        try {
            // ie.- a single resource for recursive export was specified
            int totalWork = IProgressMonitor.UNKNOWN;
            try {
                if (resourcesToExport == null) {
					totalWork = countChildrenOf(resource);
				} else {
					totalWork = countSelectedResources();
				}
            } catch (CoreException e) {
                // Should not happen
            	e.printStackTrace();
            }
            
            totalWork += packageData.getLibraryListCount();
            
            if( packageData.getCheckSecurity() ) totalWork += 62;
            
            monitor.beginTask("Packaging...", totalWork);
            
            boolean bContinue = true;
            if( packageData.getCheckSecurity() )
            {
            	bContinue = createAnalysisFiles();
            }
            if(  bContinue )
            {
	            if (resourcesToExport == null) {
	                exportResource(resource);
	            } else {
	                // ie.- a list of specific resources to export was specified
	                exportSpecifiedResources();
	            }
	
	            try {
	                exporter.finished();
	            } catch (IOException e) {
	                throw new InvocationTargetException(
	                        e,
	                        NLS.bind(DataTransferMessages.ZipExport_cannotClose, e.getMessage()));
	            }
            }
        } finally {
        	
        	removeAnalysisFiles();
        	
            monitor.done();
        }
    }

    /**
     *	Set this boolean indicating whether exported resources should
     *	be compressed (as opposed to simply being stored)
     *
     *	@param value boolean
     */
    public void setUseCompression(boolean value) {
        useCompression = value;
    }
    
	private boolean createAnalysisFiles()
	{
    	monitor.subTask("Analysis...");
    	
    	IProject project = packageData.getProject();
		IFile fileExe = packageData.getExeFile();
    	
		String projectRoot = project.getLocation().makeAbsolute().toOSString() + IConstants.FILE_SEP_BSLASH;
		String sdkHome = IdePlugin.getDefault().getSDKPath(project);
		String sbuildRoot = sdkHome + IConstants.PATH_TOOLS + "\\sbuild";
		String repositoryRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + "/" + IConstants.DIR_REPOSITORY + "/" + project.getName() + "/" + packageData.getConfigName();
		
		
		ManifestXmlStore maniStore = IdePlugin.getDefault().getManifestXmlStore(project);
		
		if( maniStore == null )
		{
			addError("Analysis error: Can not find Manifest.xml file.", null);
			return false;
		}

		String fileExeName = fileExe.getName();
		int index = fileExeName.lastIndexOf(".");
		if( index > 0 ) fileExeName = fileExeName.substring(0, index) + IConstants.EXT_HTB;
		else fileExeName = fileExeName + IConstants.EXT_HTB;
		
		String fileExePath = fileExe.getLocation().removeLastSegments(1).makeAbsolute().toOSString() + IConstants.FILE_SEP_BSLASH  + fileExeName;
		
		monitor.worked(1);
		try {
			String analXmlRoot = fileExe.getLocation().removeLastSegments(1).toOSString();
			
			AnalysisProgressMonitor mon = new AnalysisProgressMonitor(monitor); 
			
			FAPIAnalysis.FAnalysisAPI ( sdkHome  + IConstants.DIR_IDE,
					mon,
					project.getName(), 
					projectRoot + IConstants.MANIFEST_FILE,
					sbuildRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_FUNCTIONS_FUNC_XML, 
					sbuildRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_PRIVGROUP_XML,
					repositoryRoot,
					analXmlRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_NON_PREV_XML, 
					analXmlRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_NON_OPEN_XML,
					analXmlRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_EXTERNAL_XML,
					sbuildRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_VULNERABLE_FUNC_LIST_XML,
					sbuildRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_STRING_LIST_XML,
					sbuildRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_TARGET_FUNC_LIST_XML,
					analXmlRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_VULNERABLE_STRING_XML,
					analXmlRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_SUSPICIOUS_CALL_XML,
					analXmlRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_VULNERABLE_API_XML,
					analXmlRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_UNUSED_PRIV_XML
			);

			
			monitor.subTask("Hash Application.");
			FHashBinary.FHashApplication(maniStore.getId().getBytes(),
					maniStore.getAppVersion(),
					maniStore.getSecret().getBytes(), // app secrete 32 byte
					fileExe.getLocation().makeAbsolute().toString(),
					fileExePath);			
			
			monitor.worked(2);
			
			
			monitor.subTask("Signing Package.");
			String manifestFilePath = project.getLocation().append(IConstants.MANIFEST_FILE).toOSString();
			FSigning.FSigningPackage(sdkHome + IConstants.DIR_IDE,
					fileExe.getLocation().makeAbsolute().toString(),
					manifestFilePath,
					fileExePath,
					analXmlRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_SIGNATURE_XML
					);
			
			AnalysisResultFile analResultFile= new AnalysisResultFile();
			analResultFile.setAnalysisResultSignFile(analXmlRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_SIGNATURE_ANAL_RESULT_XML);
			analResultFile.setAppFile(fileExe.getLocation().toOSString());
			analResultFile.setExternalFile(analXmlRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_EXTERNAL_XML);
			analResultFile.setNonOpenFile(analXmlRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_NON_OPEN_XML);
			analResultFile.setNonPriFile(analXmlRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_NON_PREV_XML);
			analResultFile.setSusCallFile(analXmlRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_SUSPICIOUS_CALL_XML);
			analResultFile.setUnusedPrivFile(analXmlRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_UNUSED_PRIV_XML);
			analResultFile.setVulApiFile(analXmlRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_VULNERABLE_API_XML);
			analResultFile.setVulStringFile(analXmlRoot + IConstants.FILE_SEP_BSLASH + IConstants.FILE_VULNERABLE_STRING_XML);
			monitor.worked(2);
			
			monitor.subTask("Signing Analysis Result.");
			
			FSigning.FSigningAnalysisResult(sdkHome + IConstants.DIR_IDE, analResultFile);
			monitor.worked(2);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			addError(NLS.bind("Analysis error occured.", e.getMessage()), e);
			return false;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			addError(NLS.bind("Analysis error occured.", e.getMessage()), e);
			
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			addError(NLS.bind("Analysis error occured.", e.getMessage()), e);
			return false;
		}			

		try {
			packageData.getBuildDirectory().refreshLocal(IResource.DEPTH_ONE, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String genFile[] = new String[] {IConstants.FILE_NON_PREV_XML,
										 IConstants.FILE_NON_OPEN_XML,
										 IConstants.FILE_EXTERNAL_XML,
										 IConstants.FILE_VULNERABLE_STRING_XML,
										 IConstants.FILE_SUSPICIOUS_CALL_XML,
										 IConstants.FILE_VULNERABLE_API_XML,
										 IConstants.FILE_UNUSED_PRIV_XML,
										 IConstants.FILE_SIGNATURE_ANAL_RESULT_XML,
										 fileExeName,
										 IConstants.FILE_SIGNATURE_XML
										 };
		
		
		analysisFiles = new IFile[genFile.length];
		for( int i = 0; i < genFile.length; i++ )
		{
			analysisFiles[i] = packageData.getBuildDirectory().getFile(genFile[i]);
			if( analysisFiles[i] == null  || !analysisFiles[i].exists())
			{
				addError("Analysis error: " + genFile[i] + " not generated.", null);
				return false;
			}
		}
		
    	monitor.worked(1);
    	
		return true;
	}

	
	private void removeAnalysisFiles()
	{
        if( analysisFiles != null)
        {
        	for( int i = 0; i < analysisFiles.length; i++ )
        	{
        		if( analysisFiles[i] != null && analysisFiles[i].exists() )
        		{
        			try {
						analysisFiles[i].delete(true, null);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        	}
        }
	}
	
}

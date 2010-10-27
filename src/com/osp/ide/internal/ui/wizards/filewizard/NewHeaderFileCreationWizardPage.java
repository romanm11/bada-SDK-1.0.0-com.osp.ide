/*******************************************************************************
 * Copyright (c) 2004, 2008 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - initial API and implementation
 *     Anton Leherbauer (Wind River Systems)
 *     Sergey Prigogin (Google)
******************************************************************************/
package com.osp.ide.internal.ui.wizards.filewizard;

import java.util.ArrayList;

import org.eclipse.cdt.core.CConventions;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICContainer;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ISourceRoot;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.CodeGeneration;

import org.eclipse.cdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.cdt.internal.corext.util.CModelUtil;

import org.eclipse.cdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.cdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.cdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.cdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.cdt.internal.ui.wizards.dialogfields.StringDialogField;
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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

import com.osp.ide.IConstants;

public class NewHeaderFileCreationWizardPage extends AbstractFileCreationWizardPage {
	private final String KEY_LAST_USED_TEMPLATE = "LastUsedHeaderTemplate"; //$NON-NLS-1$

	private ITranslationUnit fNewFileTU = null;
	private StringDialogField fNewFileDialogField;
	
	public NewHeaderFileCreationWizardPage() {
		super(NewFileWizardMessages.NewHeaderFileCreationWizardPage_title); 
		setDescription(NewFileWizardMessages.NewHeaderFileCreationWizardPage_description); 

		fNewFileDialogField = new StringDialogField();
		fNewFileDialogField.setDialogFieldListener(new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				handleFieldChanged(NEW_FILE_ID);
			}
		});
		fNewFileDialogField.setLabelText(NewFileWizardMessages.NewHeaderFileCreationWizardPage_headerFile_label); 
	}
	
	/**
	 * Sets the focus on the starting input field.
	 */		
	@Override
	protected void setFocus() {
		fNewFileDialogField.setFocus();
	}

	/**
	 * Creates the controls for the file name field. Expects a <code>GridLayout</code> with at 
	 * least 2 columns.
	 * 
	 * @param parent the parent composite
	 * @param nColumns number of columns to span
	 */		
	@Override
	protected void createFileControls(Composite parent, int nColumns) {
		fNewFileDialogField.doFillIntoGrid(parent, nColumns);
		Text textControl = fNewFileDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(textControl, getMaxFieldWidth());
		textControl.addFocusListener(new StatusFocusListener(NEW_FILE_ID));
	}
	
	@Override
	public IPath getFileFullPath() {
		String str = fNewFileDialogField.getText();
        IPath path = null;
	    if (str.length() > 0) {
	        path = new Path(str);
	        if (!path.isAbsolute()) {
	            IPath folderPath = getSourceFolderFullPath();
	        	if (folderPath != null)
	        	    path = folderPath.append(path);
	        }
	    }
	    return path;
	}

	@Override
	protected IStatus fileNameChanged() {
		StatusInfo status = new StatusInfo();
		
		IPath filePath = getFileFullPath();
		if (filePath == null) {
			status.setError(NewFileWizardMessages.NewHeaderFileCreationWizardPage_error_EnterFileName); 
			return status;
		}

		IPath sourceFolderPath = getSourceFolderFullPath();
		if (sourceFolderPath == null || !sourceFolderPath.isPrefixOf(filePath)) {
			status.setError(NewFileWizardMessages.NewHeaderFileCreationWizardPage_error_FileNotInSourceFolder); 
			return status;
		}
		
		// check if file already exists
		IResource file = getWorkspaceRoot().findMember(filePath);
		if (file != null && file.exists()) {
	    	if (file.getType() == IResource.FILE) {
	    		status.setError(NewFileWizardMessages.NewHeaderFileCreationWizardPage_error_FileExists); 
	    	} else if (file.getType() == IResource.FOLDER) {
	    		status.setError(NewFileWizardMessages.NewHeaderFileCreationWizardPage_error_MatchingFolderExists); 
	    	} else {
	    		status.setError(NewFileWizardMessages.NewHeaderFileCreationWizardPage_error_MatchingResourceExists); 
	    	}
			return status;
		}
		
		// check if folder exists
		IPath folderPath = filePath.removeLastSegments(1).makeRelative();
		IResource folder = getWorkspaceRoot().findMember(folderPath);
		if (folder == null || !folder.exists() || (folder.getType() != IResource.PROJECT && folder.getType() != IResource.FOLDER)) {
		    status.setError(NLS.bind(NewFileWizardMessages.NewHeaderFileCreationWizardPage_error_FolderDoesNotExist, folderPath)); 
			return status;
		}

		IStatus convStatus = CConventions.validateHeaderFileName(getCurrentProject(), filePath.lastSegment());
		if (convStatus.getSeverity() == IStatus.ERROR) {
			status.setError(NLS.bind(NewFileWizardMessages.NewHeaderFileCreationWizardPage_error_InvalidFileName, convStatus.getMessage())); 
			return status;
		} else if (convStatus.getSeverity() == IStatus.WARNING) {
			status.setWarning(NLS.bind(NewFileWizardMessages.NewHeaderFileCreationWizardPage_warning_FileNameDiscouraged, convStatus.getMessage())); 
		}
		return status;
	}
	
	@Override
	public void createFile(IProgressMonitor monitor) throws CoreException {
        IPath filePath = getFileFullPath();
        if (filePath != null) {
            if (monitor == null)
	            monitor = new NullProgressMonitor();
            try {
	            fNewFileTU = null;
	            IFile newFile = NewSourceFileGenerator.createHeaderFile(filePath, true, monitor);
	            if (newFile != null) {
	            	fNewFileTU = (ITranslationUnit) CoreModel.getDefault().create(newFile);
	            	if (fNewFileTU != null) {
	            		String lineDelimiter= StubUtility.getLineDelimiterUsed(fNewFileTU);
						String content= CodeGeneration.getHeaderFileContent(getTemplate(), fNewFileTU, null, null, lineDelimiter);
						if (content != null) {
							fNewFileTU.getBuffer().setContents(content.toCharArray());
							fNewFileTU.save(monitor, true);
						}
	            	}
	            }
	        } finally {
	            monitor.done();
	        }
        }
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.internal.ui.wizards.filewizard.AbstractFileCreationWizardPage#getCreatedFileTU()
	 */
	@Override
	public ITranslationUnit getCreatedFileTU() {
		return fNewFileTU;
	}

	/*
	 * @see org.eclipse.cdt.internal.ui.wizards.filewizard.AbstractFileCreationWizardPage#getApplicableTemplates()
	 */
	@Override
	protected Template[] getApplicableTemplates() {
		return StubUtility.getFileTemplatesForContentTypes(
				new String[] { CCorePlugin.CONTENT_TYPE_CXXHEADER, CCorePlugin.CONTENT_TYPE_CHEADER }, null);
	}
	
	/*
	 * @see org.eclipse.cdt.internal.ui.wizards.filewizard.AbstractFileCreationWizardPage#getPreferredTemplateName()
	 */
	@Override
	public String getLastUsedTemplateName() {
		return getDialogSettings().get(KEY_LAST_USED_TEMPLATE);
	}
	
	/*
	 * @see org.eclipse.cdt.internal.ui.wizards.filewizard.AbstractFileCreationWizardPage#savePreferredTemplateName(String)
	 */
	@Override
	public void saveLastUsedTemplateName(String name) {
		getDialogSettings().put(KEY_LAST_USED_TEMPLATE, name);
	}
	
    protected void initSourceFolder(ICElement elem) {
    	IFolder folder = null;
    	if (elem != null) {
			ICProject cproject = elem.getCProject();
			if (cproject != null) {
				if (cproject.exists()) {
					folder = cproject.getProject().getFolder(IConstants.DIR_INCLUDE);
				}
			}
    	}
		if (folder != null) {
			setSourceFolderFullPath(folder.getFullPath(), false);
		}
		else {
			super.initSourceFolder(elem);
		}
    }
    
	protected IStatus sourceFolderChanged() {
		StatusInfo status = new StatusInfo();
		
		IPath folderPath = getSourceFolderFullPath();
		if (folderPath == null) {
			status.setError(NewFileWizardMessages.AbstractFileCreationWizardPage_error_EnterSourceFolderName); 
			return status;
		}

		IResource res = getWorkspaceRoot().findMember(folderPath);
		if (res != null && res.exists()) {
			int resType = res.getType();
			if (resType == IResource.PROJECT || resType == IResource.FOLDER) {
				IProject proj = res.getProject();
				if (!proj.isOpen()) {
					status.setError(NLS.bind(NewFileWizardMessages.AbstractFileCreationWizardPage_error_NotAFolder, folderPath)); 
					return status;
				}
			    if (!CoreModel.hasCCNature(proj) && !CoreModel.hasCNature(proj)) {
					if (resType == IResource.PROJECT) {
						status.setError(NewFileWizardMessages.AbstractFileCreationWizardPage_warning_NotACProject); 
						return status;
					}
					status.setWarning(NewFileWizardMessages.AbstractFileCreationWizardPage_warning_NotInACProject); 
				}
			    ICElement e = CoreModel.getDefault().create(res.getFullPath());
//			    if (CModelUtil.getSourceFolder(e) == null) {
//					status.setError(NLS.bind(NewFileWizardMessages.AbstractFileCreationWizardPage_error_NotASourceFolder, folderPath)); 
//					return status;
//				}
			} else {
				status.setError(NLS.bind(NewFileWizardMessages.AbstractFileCreationWizardPage_error_NotAFolder, folderPath)); 
				return status;
			}
		} else {
			status.setError(NLS.bind(NewFileWizardMessages.AbstractFileCreationWizardPage_error_FolderDoesNotExist, folderPath)); 
			return status;
		}

		return status;
	}
	
	protected IPath chooseSourceFolder(IPath initialPath) {
		IResource res = null;
		res = getWorkspaceDialog(getShell(), initialPath, true);
		
		if( res != null ) return res.getFullPath();
		
		return null; 
	}
	
	private IProject getProjectFromPath(IPath path) {
	    if (path == null)
	        return null;
	    while (path.segmentCount() > 0) {
		    IResource res = getWorkspaceRoot().findMember(path);
			if (res != null && res.exists()) {
				return res.getProject();
			}
			path = path.removeLastSegments(1);
	    }
		return null;
	}
	
	private IResource getWorkspaceDialog(Shell shell, IPath initialPath, boolean dir) {
		IPath path = initialPath;
		
		IProject prj = getProjectFromPath(initialPath);
		
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(shell,
				new WorkbenchLabelProvider(), getResourceProvider(IResource.FOLDER | IResource.PROJECT));

		if (prj == null)
			dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		else
			dialog.setInput(prj);
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
	
		if (dir)	{
			IResource container = null;
			if(path.isAbsolute()){

				IContainer cs[] = ResourcesPlugin.getWorkspace().getRoot().findContainersForLocation(path);
				if(cs != null && cs.length > 0)
				{
					container = cs[0];
				}
				else
				{
					if( prj != null ) container = prj.getFolder(path.removeFirstSegments(1));					
				}
			}
			
			dialog.setInitialSelection(container);
			dialog.setValidator(new ISelectionStatusValidator() {
			    public IStatus validate(Object[] selection) {
			    	if (selection != null)
			    		if (selection.length > 0)
			    			if ((selection[0] instanceof IFile))
			    				return new StatusInfo(IStatus.ERROR, "The selected element is not a directory.");
			    	return new StatusInfo();
			    }
			});
			dialog.setTitle("Folder selection"); 
            dialog.setMessage("Select a folder from workspace:"); 
		} else {
			IResource resource = null;
			if(path.isAbsolute()){
				IFile fs[] = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(path);
				if(fs != null && fs.length > 0)
					resource = fs[0];
			}
			dialog.setInitialSelection(resource);
			dialog.setValidator(new ISelectionStatusValidator() {
			    public IStatus validate(Object[] selection) {
			    	if (selection != null)
			    		if (selection.length > 0)
			    			if (!(selection[0] instanceof IFile))
			    				return new StatusInfo(IStatus.ERROR, "The selected element is not a file.");
			    	return new StatusInfo();
			    }
			});
			dialog.setTitle("File selection"); 
            dialog.setMessage("Select a file from workspace:"); 
		}
		if (dialog.open() == Window.OK) {
			IResource resource = (IResource) dialog.getFirstResult();
			
			return resource;
		}
		return null;
	}
	
    private ITreeContentProvider getResourceProvider(final int resourceType) {
        return new WorkbenchContentProvider() {
            public Object[] getChildren(Object o) {
                if (o instanceof IContainer) {
                    IResource[] members = null;
                    try {
                        members = ((IContainer) o).members();
                    } catch (CoreException e) {
                        //just return an empty set of children
                        return new Object[0];
                    }

                    //filter out the desired resource types
                    ArrayList results = new ArrayList();
                    
                    if(  o instanceof IProject )
                    {
                        for (int i = 0; i < members.length; i++) {
                            //And the test bits with the resource types to see if they are what we want
                            if ((members[i].getType() & resourceType) > 0) {
                            	
                           		String name  = members[i].getName();
                           		if( !name.startsWith("."))
                           			results.add(members[i]);
                            }
                        }
                    	
                    }
                    else
                    {
                        for (int i = 0; i < members.length; i++) {
                            //And the test bits with the resource types to see if they are what we want
                            if ((members[i].getType() & resourceType) > 0) {
//                            	String name  = members[i].getName();
//                            	if( !name.startsWith("."))
                            		results.add(members[i]);
                            }
                        }
                    	
                    }
                    
                    return results.toArray();
                } 
                //input element case
                if (o instanceof ArrayList) {
                    return ((ArrayList) o).toArray();
                } 
                return new Object[0];
            }
        };
    }

	
	
}

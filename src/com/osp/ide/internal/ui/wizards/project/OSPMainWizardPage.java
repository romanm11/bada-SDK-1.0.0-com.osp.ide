package com.osp.ide.internal.ui.wizards.project;

import java.net.URI;

import org.eclipse.cdt.internal.ui.CPluginImages;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.newui.ProjectContentsArea.IErrorMessageReporter;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
//import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
//import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;

public class OSPMainWizardPage extends WizardNewProjectCreationPage {

	private static final Image IMG_CATEGORY = CPluginImages.get(CPluginImages.IMG_OBJS_SEARCHFOLDER);
	private static final Image IMG_ITEM = CPluginImages.get(CPluginImages.IMG_OBJS_VARIABLE);

    private Tree fTemplTree;
//    private Table fToolChainTable;
    private Text fMsgTxt;

	private OSPCreateDataStore dataStore=null;
	private IToolChain[] tc=null;
	
	private boolean isEmptyProject = false;
//	private boolean isCenterRuned = false;

	public OSPMainWizardPage(boolean bEmptyProject)	{
		super("OSPMainWizardPage");
		
		isEmptyProject = bEmptyProject;
		
		setTitle("bada Project");
		setDescription("Create a new bada project with the selected project type.");
		setImageDescriptor(IdePlugin.getDefault().createImageDescriptor(IConstants.IMG_WIZARD));
	}
	

	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		super.createControl(parent);
		
		createDynamicGroup((Composite)getControl()); 

		setPageComplete(validatePage());
        // Show description on opening
        setErrorMessage(null);
        setMessage(null);
	}
	
    private void createDynamicGroup(Composite parent) {
        Composite c = new Composite(parent, SWT.NONE);
        c.setLayoutData(new GridData(GridData.FILL_BOTH));
    	c.setLayout(new GridLayout(1, false));
    	
        Label label_type = new Label(c, SWT.NONE);
        label_type.setFont(parent.getFont());
        label_type.setLayoutData(new GridData(GridData.BEGINNING));
        label_type.setText("Project type:");
    	
        fTemplTree = new Tree(c, SWT.SINGLE | SWT.BORDER);
        fTemplTree.setLayoutData(new GridData(GridData.FILL_BOTH));
        fTemplTree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				templateTreeSelectChanged();
			}});

        fMsgTxt = new Text(c, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 40;
        gd.widthHint = 100;
        gd.horizontalSpan = 1;
        fMsgTxt.setLayoutData(gd);
        fMsgTxt.setText("Create a new C++ application project for bada");

        //setToolChains();
        fillTree();
        
    }
    
    private void fillTree()
    {
    	TreeItem ti_osp = new TreeItem(fTemplTree, SWT.NONE);
    	ti_osp.setText("Application");
    	ti_osp.setImage(IMG_CATEGORY);
    	ti_osp.setData(Integer.valueOf(IConstants.PRJ_TYPE_APP_TREE));

    	TreeItem ti_app_frame = new TreeItem(ti_osp, SWT.NONE);
    	ti_app_frame.setText(OSPCCProjectHelper.getProjectTypeLabel(IConstants.PRJ_TYPE_APP_FRAME));
    	ti_app_frame.setImage(IMG_ITEM);
    	ti_app_frame.setData(Integer.valueOf(IConstants.PRJ_TYPE_APP_FRAME));    	
    	
    	TreeItem ti_app_form = new TreeItem(ti_osp, SWT.NONE);
    	ti_app_form.setText(OSPCCProjectHelper.getProjectTypeLabel(IConstants.PRJ_TYPE_APP_FORM));
    	ti_app_form.setImage(IMG_ITEM);
    	ti_app_form.setData(Integer.valueOf(IConstants.PRJ_TYPE_APP_FORM));
    	
    	TreeItem ti_app_empty = new TreeItem(ti_osp, SWT.NONE);
    	ti_app_empty.setText(OSPCCProjectHelper.getProjectTypeLabel(IConstants.PRJ_TYPE_APP_EMPTY));
    	ti_app_empty.setImage(IMG_ITEM);
    	ti_app_empty.setData(Integer.valueOf(IConstants.PRJ_TYPE_APP_EMPTY));    	
    	
    	ti_osp.setExpanded(true);

    	TreeItem ti_lib = new TreeItem(fTemplTree, SWT.NONE);
    	ti_lib.setText("Library");
    	ti_lib.setImage(IMG_CATEGORY);
    	ti_lib.setData(Integer.valueOf(IConstants.PRJ_TYPE_LIB_TREE));    	

    	TreeItem ti_lib_sh = new TreeItem(ti_lib, SWT.NONE);
    	ti_lib_sh.setText(OSPCCProjectHelper.getProjectTypeLabel(IConstants.PRJ_TYPE_LIB_SHARED));
    	ti_lib_sh.setImage(IMG_ITEM);
    	ti_lib_sh.setData(Integer.valueOf(IConstants.PRJ_TYPE_LIB_SHARED));   	

    	TreeItem ti_lib_st = new TreeItem(ti_lib, SWT.NONE);
    	ti_lib_st.setText(OSPCCProjectHelper.getProjectTypeLabel(IConstants.PRJ_TYPE_LIB_STATIC));
    	ti_lib_st.setImage(IMG_ITEM);
    	ti_lib_st.setData(Integer.valueOf(IConstants.PRJ_TYPE_LIB_STATIC));
    	
    	ti_lib.setExpanded(true);
    	
    	int type = IConstants.PRJ_TYPE_APP_FRAME;
    	if( isEmptyProject )
    		type = IConstants.PRJ_TYPE_APP_EMPTY;
    	else
    		type = IdePlugin.getDefault().getLastCreatedProjectType();
    	
    	TreeItem selected = null;
    	switch(type)
    	{
    		case IConstants.PRJ_TYPE_APP_FRAME:
    			selected = ti_app_frame;
    			break;
    		case IConstants.PRJ_TYPE_APP_FORM:
    			selected = ti_app_form;
    			break;
    		case IConstants.PRJ_TYPE_LIB_SHARED:
    			selected = ti_lib_sh;
    			break;
    		case IConstants.PRJ_TYPE_LIB_STATIC:
    			selected = ti_lib_st;
    			break;
    		case IConstants.PRJ_TYPE_APP_EMPTY:
    			selected = ti_app_empty;
    			break;
    			
    		default:
    			selected = ti_app_frame;
    	}
    	
    	
    	fTemplTree.select(selected);
    	templateTreeSelectChanged();
    }
    
    private void setToolChains(int type)
    {
        tc = ToolChainUtil.getOSPToolChains(true, OSPCreateDataStore.convArtifactType(type));
        if(tc.length > 0 )
        {	
        	OSPCreateDataStore store = getDataStore();
        	if( store != null ) store.setToolChain(tc[0]);
        	
        }
    }
    
/*
	private IErrorMessageReporter getErrorReporter() {
		return new IErrorMessageReporter(){
			public void reportError(String errorMessage) {
				setErrorMessage(errorMessage);
				boolean valid = errorMessage == null;
				if(valid) valid = validatePage();
				setPageComplete(valid);
			}
		};
	}
*/
	protected OSPCreateDataStore getDataStore()
	{
		if( dataStore == null )
		{
			IWizard wiz= getWizard();
			if( wiz instanceof ICreateDataStore)
				return ((ICreateDataStore)wiz).getDataStore();
			
			return null;
		}
		
		return dataStore;
	}	

	
	private void templateTreeSelectChanged()
	{
		TreeItem[] tis = fTemplTree.getSelection();
		
		if( tis != null && tis.length > 0 )
		{
			Object obj = tis[0].getData();
			if( obj instanceof Integer)
			{
				OSPCreateDataStore store = getDataStore();
				int type = ((Integer)obj).intValue();
				
				if( store != null ) store.setProjectType(type);
				
				if( OSPCCProjectHelper.isTypeValid(type) )
				{
					setToolChains(type);
				}
				
				fMsgTxt.setText(OSPCCProjectHelper.getProjectTypeDesc(type));
			}
		}
		setPageComplete(validatePage());
	}
	
	
	
    public URI getProjectLocation() {
    	return useDefaults() ? null : getLocationURI();
    }
    
    protected boolean validatePage() {
		setMessage(null);
    	if (!super.validatePage())
    		return false;

        if (getProjectName().indexOf('#') >= 0) {
            setErrorMessage("Project name cannot contain special symbol");	             //$NON-NLS-1$
            return false;
        }    	

        IProject handle = getProjectHandle();

    	try {
    		IFileStore fs;
        	URI p = getProjectLocation();
        	if (p == null) {
        		fs = EFS.getStore(ResourcesPlugin.getWorkspace().getRoot().getLocationURI());
        		fs = fs.getChild(getProjectName());
        	} else
        		fs = EFS.getStore(p);
    		IFileInfo f = fs.fetchInfo();
        	if (f.exists()) {
        		if (f.isDirectory()) {
        			//setMessage("Directory with specified name already exists.", IMessageProvider.WARNING); //$NON-NLS-1$
        			//return true;
        			setErrorMessage("Directory with specified name already exists."); //$NON-NLS-1$
        			return false;
        		}
				setErrorMessage("File with specified name already exists."); //$NON-NLS-1$
				return false;
        	}
    	} catch (CoreException e) {
    		CUIPlugin.log(e.getStatus());
    	}
    	
    	
		OSPCreateDataStore store = getDataStore();
		if( store != null ) store.setProjectName(getProjectName());

    	if( !OSPCCProjectHelper.isTypeValid(store.getProjectType()) )
    	{
            setErrorMessage("Select a projct type.");
            return false;    		
    	}
		
        
        if (!useDefaults()) {
            IStatus locationStatus = ResourcesPlugin.getWorkspace().validateProjectLocationURI(handle,
            		getLocationURI());
            if (!locationStatus.isOK()) {
                setErrorMessage(locationStatus.getMessage());
                return false;
            }
        }
        
        // it is not an error, but we cannot continue
        if (tc == null || tc.length == 0) {
            setErrorMessage("No ToolChain");
	        return false;	        	
        }

        
        setErrorMessage(null);
        return true;
    }
    
    public void setVisible(boolean visible) {
		super.setVisible(visible);
		
//		if( isCenterRuned == false)
//		{
//			Shell shell = getShell();
//	    	shell.setSize(IConstants.WIZARD_PAGE_WIDTH, IConstants.WIZARD_PAGE_HEIGHT);
//	    	
//	    	// Center Dialog
//	    	if( shell.getParent() != null && shell.getParent().getShell() != null)
//	    	{
//	    		Rectangle parentSize = shell.getParent().getShell().getBounds();
//	    		Rectangle mySize = shell.getBounds();
//	
//	
//	    		int locationX, locationY;
//	    		locationX = (parentSize.width - mySize.width)/2+parentSize.x;
//	    		locationY = (parentSize.height - mySize.height)/2+parentSize.y;
//	
//	
//	    		shell.setLocation(new Point(locationX, locationY));
//	    	}
//	    	isCenterRuned = true;
//		}
	}
}

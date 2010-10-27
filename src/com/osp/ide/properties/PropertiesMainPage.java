package com.osp.ide.properties;

import java.io.File;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescriptionManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;
import com.osp.ide.core.OspPropertyStore;
import com.osp.ide.core.badaNature;
import com.osp.ide.core.builder.TargetOptionProvider;
import com.osp.ide.utils.FileUtil;
import com.osp.ide.utils.WorkspaceUtils;

public class PropertiesMainPage extends PropertyPage implements IModelChangerListner {

	protected IProject fProject = null;
	protected boolean noContentOnPage = false;
	
	Table tablePrivilege;
	TableTree tableTreeDevice;
	
	private Text fSdkPath;
	ModelComp modelComp;
	private Text labelDesc;	
	
	@Override
	protected Control createContents(Composite parent) {
		// TODO Auto-generated method stub
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		GridLayout compositeLayout = new GridLayout();
		compositeLayout.numColumns = 1;
		compositeLayout.marginHeight = 0;
		compositeLayout.marginWidth = 0;
		composite.setLayout( compositeLayout );
		
		String s = null;
		if (!checkElement()) {
			s = "This element not a project";
		} 
		else
		{
			fProject = getProject();
			if (!isBadaProject(fProject)) 
				s = "This project is not a bada project"; //$NON-NLS-1$
		}
		
	    if (s == null) {
	    	createWidgets(composite);
	    	return composite;
	    }		
		
		// no contents
		Label label = new Label(composite, SWT.LEFT);
		label.setText(s);
		label.setFont(composite.getFont());
		noContentOnPage = true;
		noDefaultAndApplyButton();
		return composite;
	}

	protected boolean checkElement() {
		boolean isProject=false;
		IResource internalElement = null;
		
		IAdaptable el = super.getElement();
		if (el instanceof ICElement) 
			internalElement = ((ICElement)el).getResource();
		else if (el instanceof IResource) 
			internalElement = (IResource)el;
		if (internalElement == null) return false;
		isProject = internalElement instanceof IProject;

		return isProject;
	}
	
	public IProject getProject() {
		Object element = getElement();
		if (element != null) { 
			if (element instanceof IProject)
			{
				IResource f = (IResource) element;
				return f.getProject();
			}
			else if (element instanceof ICProject)
				return ((ICProject)element).getProject();
		}
		return null;
	}
	
	public static boolean isCDTPrj(IProject p) {
		ICProjectDescription prjd = CoreModel.getDefault().getProjectDescription(p, false); 
		if (prjd == null) return false; 
		ICConfigurationDescription[] cfgs = prjd.getConfigurations();
		return (cfgs != null && cfgs.length > 0);
	}
	
	public boolean isBadaProject(IProject p) {
		ICProjectDescription prjd = CoreModel.getDefault().getProjectDescription(p, false); 
		if (prjd == null) return false; 
		ICConfigurationDescription[] cfgs = prjd.getConfigurations();
		
		boolean flag = (cfgs != null && cfgs.length > 0);
		
		if( flag )
		{
			try {
				flag = p.hasNature(badaNature.OSP_NATURE_ID);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				flag = false;
			}
		}
		return flag;
	}
	
	protected void createWidgets(Composite parent) {
		// TODO Auto-generated method stub
		createApplicationGroup(parent);
		
        loadData();
	}
	
	protected void createApplicationGroup(Composite parent) {
	
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setFont(parent.getFont());
        
        GridLayout layout = new GridLayout(3, false);
        layout.verticalSpacing = 8;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        Label label = new Label(composite, SWT.NONE);
    	label.setText("SDK Root:");
    	label.setLayoutData(new GridData());

		
    	fSdkPath = new Text(composite, SWT.BORDER);
    	fSdkPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));        
    	fSdkPath.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				sdkPathChanged();
			}
		}); 
    	
    	
		Button changeButton = new Button(composite, SWT.PUSH);
        changeButton.setText("Browse...");
        changeButton.setLayoutData(new GridData());
        changeButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
            	handleSdkPathButtonPressed();
            }
        });
        
        Label labelModel = new Label(composite, SWT.NONE);
        labelModel.setText("Model:");
        labelModel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        
        modelComp = new ModelComp(composite, SWT.NONE);
        modelComp.setLayoutData(new GridData(GridData.BEGINNING| GridData.FILL_BOTH));
        modelComp.addModelChangeListener(this);
        
        Label lableModelEndDummy = new Label(composite, SWT.NONE);
        lableModelEndDummy.setLayoutData(new GridData());
        
        // Description
        Label lableDescDummy = new Label(composite, SWT.NONE);
        lableDescDummy.setLayoutData(new GridData());        
        
        Group gropuDesc = new Group(composite, SWT.NONE);
        gropuDesc.setFont(parent.getFont());
        layout = new GridLayout(1, false);
        gropuDesc.setLayout(layout);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = 100;
        gd.heightHint = 120;
        gropuDesc.setLayoutData(gd);
        gropuDesc.setText("Description");

        labelDesc = new Text(gropuDesc, SWT.WRAP | SWT.V_SCROLL  | SWT.READ_ONLY | SWT.MULTI);
        labelDesc.setLayoutData(new GridData(GridData.FILL_BOTH)); 
        
        Label lableDescEndDummy = new Label(composite, SWT.NONE);
        lableDescEndDummy.setLayoutData(new GridData());        
	}
	
	protected void handleSdkPathButtonPressed() {

		DirectoryDialog dialog = new DirectoryDialog(getShell());
		dialog.setMessage("Select bada SDK Path");

		String dirName = fSdkPath.getText().trim();

		if (dirName.length() > 0) {
			File path = new File(dirName);
			if (path.exists())
				dialog.setFilterPath(new Path(dirName).toOSString());
		}

		String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			if (!dirName.equals(selectedDirectory)) {
				fSdkPath.setText(selectedDirectory);
			}
		}
	}	
	
	protected void loadData() {
		if( fProject == null ) return;
		
		OspPropertyStore store = IdePlugin.getDefault().getOspPropertyStore(fProject);
		fSdkPath.setText(store.getSdkPath());

		
		changeSdkPath(store.getSdkPath(), store.getModel());
	}
	
	protected boolean isNoContentOnPage()
	{
		return noContentOnPage;
	}
    
	public void performApply() { 
		performSave(); 
	}
    
	public boolean performOk() {
		return performSave(); 
    }
	
	private boolean performSave()	{
		
		storeProperty();
		return true;
	}
	
	protected void performDefaults() {
		// TODO Auto-generated method stub
		super.performDefaults();
		
		//String sdkRoot = System.getenv(IConstants.ENV_BADA_SDK_HOME);
		String sdkRoot = WorkspaceUtils.getDefaultBadaSdkRoot();
		if( sdkRoot == null ) sdkRoot = "";
		fSdkPath.setText(sdkRoot);
	}	
	
	protected void storeProperty() {
		// TODO Auto-generated method stub
		
		if(fProject == null ) return;
		
		OspPropertyStore store = IdePlugin.getDefault().getOspPropertyStore(fProject);

		store.setSdkPath(fSdkPath.getText());
		store.setModel(modelComp.getModel());
		store.store();
		
		WorkspaceUtils.refreshOspProject(fProject);
		TargetOptionProvider.getInstance().modelChanged(fProject);
		
//		try {
//			CCorePlugin.getDefault().updateProjectDescriptions(new IProject[]{fProject}, new NullProgressMonitor());
//		} catch (CoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	private void sdkPathChanged()
	{
		changeSdkPath(fSdkPath.getText().trim(), "");
	}

	private void changeSdkPath(String sdkPath, String currModel)
	{
		modelComp.setSDKPath(sdkPath, currModel);
		checkPage();	
	}
	
	private void loadDescription(String sdkPath, String model)
	{
		if( model == null || model.length() == 0)
		{
			labelDesc.setText("");
		}
		else
		{
			String text = FileUtil.loadFromFile( sdkPath + IConstants.FILE_SEP_BSLASH + IConstants.DIR_MODEL + IConstants.FILE_SEP_BSLASH + model + IConstants.FILE_SEP_BSLASH +IConstants.FILE_DESC_TXT);
			if( text != null ) labelDesc.setText(text);
			else labelDesc.setText("");
		}
	}
	
	protected void checkPage()
	{
		setValid(validatePage());
	}
	
    protected boolean validatePage() {
		setErrorMessage(null);


		String path = fSdkPath.getText().trim(); 
		if( path.length() <= 0)
		{
			setErrorMessage("SDK Root field empty.");
			return false;
		}
		
		if( new File(path).exists() == false )
		{
			setErrorMessage("Invalid SDK Root path.");
			return false;
			
		}
		 
		String model = modelComp.getModel();
		if( model ==null || model.length() == 0 )
		{
			setErrorMessage("Model does not exist.");
			return false;
			
		}		

		return true;
    }

	@Override
	public void modelChanged(String model) {
		// TODO Auto-generated method stub
		
		if( model == null || model.length() == 0 )
			loadDescription("","");
		else
			loadDescription(fSdkPath.getText().trim(), model);
	}	


}

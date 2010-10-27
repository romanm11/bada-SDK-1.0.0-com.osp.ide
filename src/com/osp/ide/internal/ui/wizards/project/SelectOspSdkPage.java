package com.osp.ide.internal.ui.wizards.project;


import java.io.File;

import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;
import com.osp.ide.properties.IModelChangerListner;
import com.osp.ide.properties.ModelComp;
import com.osp.ide.utils.FileUtil;
import com.osp.ide.utils.WorkspaceUtils;
import com.osp.ide.wizards.NewElementWizardPage;
import com.osp.ide.wizards.StatusInfo;

public class SelectOspSdkPage extends NewElementWizardPage implements IModelChangerListner {

	private Text fTextSdk;
	ModelComp modelComp;
	private Text labelDesc;    
	
	private StatusInfo fSdkStatus;
	private StatusInfo fModelStatus;
	
	public SelectOspSdkPage() {
		super("SelectOspSdkPage");
		// TODO Auto-generated constructor stub
		
		setTitle("Select bada SDK");
		setDescription("Define the path to the bada SDK.");	
		setImageDescriptor(IdePlugin.getDefault().createImageDescriptor(IConstants.IMG_WIZARD));
		
		fSdkStatus = new StatusInfo();
		fModelStatus = new StatusInfo();
	}

	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setFont(parent.getFont());
        
        initializeDialogUnits(parent);
        
        GridLayout layout = new GridLayout(3, false);
        layout.numColumns = 3;
        layout.verticalSpacing = 8;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        Label label = new Label(composite, SWT.NONE);
        label.setText("bada SDK Root:");
        label.setLayoutData(new GridData());
        
        fTextSdk = new Text(composite, SWT.BORDER);
        fTextSdk.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fTextSdk.addModifyListener(new ModifyListener() {
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
        
        
        Label lableDummy = new Label(composite, SWT.NONE);
        lableDummy.setLayoutData(new GridData());
        
        
        //Label lableDummy2 = new Label(composite, SWT.NONE);
        //lableDummy2.setLayoutData(new GridData(GridData.FILL_VERTICAL));

        
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
        
        setData();
        setControl(composite);
        
        sdkPathChanged();
	}

	protected void handleSdkPathButtonPressed() {

		DirectoryDialog dialog = new DirectoryDialog(getShell());
		dialog.setMessage("Select bada SDK Path");

		String dirName = fTextSdk.getText().trim();

		if (dirName.length() > 0) {
			File path = new File(dirName);
			if (path.exists())
				dialog.setFilterPath(new Path(dirName).toOSString());
		}

		String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			if (!dirName.equals(selectedDirectory)) {
				fTextSdk.setText(selectedDirectory);
			}
		}
	}
	
	
	private void setData()
	{
		//String sdkRoot = System.getenv(IConstants.ENV_BADA_SDK_HOME);
		String sdkRoot = WorkspaceUtils.getDefaultBadaSdkRoot();
		
		if( sdkRoot != null )
		{
			fTextSdk.setText(sdkRoot);
		}
	}
	
	
	private void sdkPathChanged()
	{
		String dirName = fTextSdk.getText().trim();
		if (dirName.length() > 0) {
			File path = new File(dirName);
			if (path.exists())
			{
				fSdkStatus.setOK();
			}
			else
			{
				fSdkStatus.setError("Invalid bada SDK Root path.");
			}
		}
		else
		{
			fSdkStatus.setError("bada SDK Root is empty.");
		}
		
		OSPCreateDataStore store = getDataStore();
		if( store != null) store.setSdkPath(dirName);
		
		modelComp.setSDKPath(dirName, "");
		
		updateStatus(new IStatus[] { fSdkStatus, fModelStatus});
	}
	
	

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
    	if( visible ) fTextSdk.setFocus();
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
	
	@Override
	public void modelChanged(String model) {
		// TODO Auto-generated method stub

		OSPCreateDataStore store = getDataStore();
		if( store != null) store.setModel(model);
		
		if( model == null || model.length() == 0 ) fModelStatus.setError("Model does not exist.");
		else fModelStatus.setOK();

		updateStatus(new IStatus[] { fSdkStatus, fModelStatus});
		
		if( model == null || model.length() == 0 )
            loadDescription("","");
        else
            loadDescription(fTextSdk.getText().trim(), model);
	}
}

package com.osp.ide.internal.ui.wizards.project;


import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.core.runtime.IStatus;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;
import com.osp.ide.wizards.NewElementWizardPage;
import com.osp.ide.wizards.StatusInfo;

public class BasicSettingPage extends NewElementWizardPage implements IProjectNameChangeListener {

	private Text fName;
	private Text fVendor;
	private Text fDesc;
	
	private StatusInfo fNameStatus;
	
	private static final int FIELD_NAME = 0;
	private static final int FIELD_VENDOR = 1;
	private static final int FIELD_DESC = 2;
	private static final int FIELD_ALL = 3;
	
	public BasicSettingPage() {
		super("BasicSettingPage");
		// TODO Auto-generated constructor stub
		
		setTitle("Basic Settings");
		setDescription("Define the basic properties of the project.");	
		setImageDescriptor(IdePlugin.getDefault().createImageDescriptor(IConstants.IMG_WIZARD));
		
		fNameStatus = new StatusInfo();
	}

	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setFont(parent.getFont());
        
        initializeDialogUnits(parent);
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.verticalSpacing = 10;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        Label label = new Label(composite, SWT.NONE);
        label.setText("Name:");
        label.setLayoutData(new GridData());
        
        fName = new Text(composite, SWT.BORDER);
        fName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updatefield(FIELD_NAME);
			}
		});        
        
        label = new Label(composite, SWT.NONE);
        label.setText("Vendor:");
        label.setLayoutData(new GridData());
        
        fVendor = new Text(composite, SWT.BORDER);
        fVendor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));           
        fVendor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updatefield(FIELD_VENDOR);
			}
		});          
        
        label = new Label(composite, SWT.NONE);
        label.setText("Description:");
        label.setLayoutData(new GridData());
        
        fDesc = new Text(composite, SWT.BORDER);
        fDesc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));        
        fDesc.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updatefield(FIELD_DESC);
			}
		});
        
        setControl(composite);
        
        updatefield(FIELD_ALL);

        OSPCreateDataStore store = getDataStore(); 
        if( store != null )
        	store.addProjectNameChangeListener(this);
	}
	
	
	private void updatefield(int field_id)
	{
		switch(field_id)
		{
			case FIELD_NAME :
				updateNameStatus();
				break;
			case FIELD_VENDOR :
				updateVendorStatus();
				break;
			case FIELD_DESC :
				updateDescStatus();
				break;
			case FIELD_ALL :
				updateNameStatus();
				updateVendorStatus();
				updateDescStatus();
				break;				
		}
		
		updateStatus(new IStatus[] { fNameStatus});
	}

	
	
	private void updateNameStatus()
	{
		String text = fName.getText();
		OSPCreateDataStore store = getDataStore(); 
		if( text.length() == 0 )
		{
			fNameStatus.setError("Name field is empty.");
			if( store != null) store.setAppName("");
			
		}
		else
		{
			fNameStatus.setOK();
			if( store != null) store.setAppName(text);
		}
	}	

	
	private void updateVendorStatus()
	{
		String text = fVendor.getText();
		
		OSPCreateDataStore store = getDataStore();
		if( store != null) store.setVendor(text);
	}
	
	
	private void updateDescStatus()
	{
		String text = fDesc.getText();
		
		OSPCreateDataStore store = getDataStore();
		if( store != null) store.setDescription(text);
	}		

	public void projectNameChanged(String name) {
		// TODO Auto-generated method stub
		if( name != null && name.length() > 0 )
		{
			String transname = name.substring(0, 1).toUpperCase(Locale.getDefault()) + name.substring(1);
			fName.setText(transname);
		}
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
    	
    	if( visible ) fName.setFocus();
	}
}

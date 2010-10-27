package com.osp.ide.internal.ui.wizards.project;


import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.core.runtime.IStatus;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;
import com.osp.ide.wizards.NewElementWizardPage;
import com.osp.ide.wizards.StatusInfo;

public class FormSettingPage extends NewElementWizardPage implements IProjectNameChangeListener, IProjectTypeChangeListener {

	private StatusInfo fFormNameStatus;
	
	private Text fFormName;
	private Text fFormSrc;
	private Text fFormHeader;
	private Combo fBaseClass;

	private Label fFormNameLabel;
	private Label fFormSrcLabel;
	private Label fFormHeaderLabel;
	private Label fBaseClassLabel;	
	
	public FormSettingPage() {
		super("FormSettingPage");
		// TODO Auto-generated constructor stub
		
		setTitle("Generated Files");
		setDescription("Enter a form name to be created.");	
		setImageDescriptor(IdePlugin.getDefault().createImageDescriptor(IConstants.IMG_WIZARD));
		
		fFormNameStatus = new StatusInfo(); 
	}

	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setFont(parent.getFont());
        
        initializeDialogUnits(parent);
        
        GridLayout layout = new GridLayout();
        layout.makeColumnsEqualWidth = true;
        layout.numColumns = 2;
        layout.marginHeight = 2;
        layout.marginWidth = 15;
        layout.horizontalSpacing = 20;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        
        fFormNameLabel = new Label(composite, SWT.NONE);
        fFormNameLabel.setText("Form name:");
        fFormNameLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        fFormHeaderLabel = new Label(composite, SWT.NONE);
        fFormHeaderLabel.setText(".h file:");
        fFormHeaderLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fFormHeaderLabel.setEnabled(false);

        fFormName = new Text(composite, SWT.BORDER);
        fFormName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fFormName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updatefield();
			}
		});        
        
        fFormHeader = new Text(composite, SWT.BORDER);
        fFormHeader.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fFormHeader.setEnabled(false);
        
        
        Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
      
        fBaseClassLabel = new Label(composite, SWT.NONE);
        fBaseClassLabel.setText("Base class:");
        fBaseClassLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        
        fFormSrcLabel = new Label(composite, SWT.NONE);
        fFormSrcLabel.setText(".cpp file:");
        fFormSrcLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));        
        fFormSrcLabel.setEnabled(false);
        
        fBaseClass = new Combo(composite, SWT.DROP_DOWN|SWT.READ_ONLY);
        fBaseClass.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fBaseClass.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				handleFormBaseClassChanged();
			}
		});
        

        fFormSrc = new Text(composite, SWT.BORDER);
        fFormSrc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fFormSrc.setEnabled(false);
        
        setData();
        handleFormBaseClassChanged();
        
        setControl(composite);
        
        updatefield();

        if( getDataStore() != null )
        {
        	getDataStore().addProjectNameChangeListener(this);
           	getDataStore().addProjectTypeChangeListener(this);
           	projectTypeChanged(getDataStore().getProjectType());
        }
	}
	
	
	private void setData()
	{
		fBaseClass.setItems(IConstants.FORM_CLASS);
		fBaseClass.select(IConstants.INX_FORM_CLASS_DEFAULT);
		
		fFormName.setText(IConstants.DEFAULT_FORM_NAME);
	}
	
	private void handleFormBaseClassChanged()
	{
		String baseName = fBaseClass.getText();		
		if( getDataStore() != null )
			getDataStore().setFormBaseClass(baseName);
	}
	
	private void updatefield()
	{
		updateFormNameStatus();		
		updateStatusInfo();
	}

	private void updateStatusInfo()
	{
		updateStatus(new IStatus[] {fFormNameStatus});
	}
	
	
	private void updateFormNameStatus()
	{
		OSPCreateDataStore store = getDataStore();
		if( store != null && store.getProjectType() == IConstants.PRJ_TYPE_APP_FORM )
		{
			String text = fFormName.getText();
			if( text.length() == 0 )
			{
				fFormHeader.setText("");
				fFormSrc.setText("");
				fFormNameStatus.setError("Form Name field is empty.");
			}
			else
			{
				String prjName = store.getProjectName().toLowerCase(Locale.getDefault());
				
				if( prjName.length() > 0 && prjName.equals(text.toLowerCase(Locale.getDefault())))
				{
					fFormHeader.setText("");
					fFormSrc.setText("");					
					fFormNameStatus.setError("Invalid Form Name.");
				}
				else
				{
					fFormHeader.setText(text + IConstants.EXT_HEADER);
					fFormSrc.setText(text + IConstants.EXT_CC);					
					fFormNameStatus.setOK();
				}
			}
			
			if( store != null) getDataStore().setFormName(text);			
		}
		else
		{
			fFormNameStatus.setOK();
		}
	}		
	


	public void projectNameChanged(String name) {
		// TODO Auto-generated method stub
		updatefield();
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
    	if( visible )
    	{
    		fFormName.setFocus();
    		updateStatusInfo();
    	}
	}

	@Override
	public void projectTypeChanged(int type) {
		// TODO Auto-generated method stub
		updatefield();
	}
}

package com.osp.ide.internal.ui.wizards.project;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;
import com.osp.ide.wizards.NewElementWizardPage;
import com.osp.ide.wizards.StatusInfo;


public class AppFeaturePage extends NewElementWizardPage implements IProjectTypeChangeListener {


	Button check3d;
	Button checkDatabase;
	Button checkLocations;
	Button checkMedia;
	Button checkNetwork;
	Button checkUi;
	
	public AppFeaturePage() {
		super("AppFeaturePage");
		// TODO Auto-generated constructor stub
		
		setTitle("Advanced application feature");
		setDescription("Select the features to be used in the application.");
		setImageDescriptor(IdePlugin.getDefault().createImageDescriptor(IConstants.IMG_WIZARD));
	}	
	
	
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());
        
        initializeDialogUnits(parent);
        
        GridLayout layout = new GridLayout(1, false);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        Label m_label = new Label(composite, SWT.NONE);
        m_label.setText("Select th feature to be used");
        m_label.setLayoutData(new GridData());

        
        Group groupFeature = new Group(composite, SWT.NONE);
        groupFeature.setFont(composite.getFont());
        layout = new GridLayout(1, false);
        groupFeature.setLayout(layout);
        groupFeature.setLayoutData(new GridData(GridData.FILL_BOTH));
        

        check3d = new Button(groupFeature, SWT.CHECK);
        check3d.setText("3D");
        check3d.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        check3d.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				getDataStore().setAppFeature(OSPCreateDataStore.FEATRUE_3D, check3d.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
        

        checkDatabase = new Button(groupFeature, SWT.CHECK);
        checkDatabase.setText("Database");
        checkDatabase.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        checkDatabase.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				getDataStore().setAppFeature(OSPCreateDataStore.FEATRUE_DATABASE, checkDatabase.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		}); 
        
        checkLocations = new Button(groupFeature, SWT.CHECK);
        checkLocations.setText("Locations");
        checkLocations.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        checkLocations.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				getDataStore().setAppFeature(OSPCreateDataStore.FEATRUE_LOCATIONS, checkLocations.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});         
        
        checkMedia = new Button(groupFeature, SWT.CHECK);
        checkMedia.setText("Media");
        checkMedia.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        checkMedia.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				getDataStore().setAppFeature(OSPCreateDataStore.FEATRUE_MEDIA, checkMedia.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});         
        
        checkNetwork = new Button(groupFeature, SWT.CHECK);
        checkNetwork.setText("Network");
        checkNetwork.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        checkNetwork.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				getDataStore().setAppFeature(OSPCreateDataStore.FEATRUE_NETWORK, checkNetwork.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});         
        
        checkUi = new Button(groupFeature, SWT.CHECK);
        checkUi.setText("UI");
        checkUi.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        checkUi.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				getDataStore().setAppFeature(OSPCreateDataStore.FEATRUE_UI, checkUi.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});         
        
        setControl(composite);
        
        OSPCreateDataStore store = getDataStore(); 
        if( store != null )
        {
        	projectTypeChanged(store.getProjectType());
        	store.addProjectTypeChangeListener(this);
        }
        
		updateStatus(new IStatus[] { new StatusInfo() });
	}


	public void projectTypeChanged(int type) {
		// TODO Auto-generated method stub

		if( type == IConstants.PRJ_TYPE_APP_FORM || type == IConstants.PRJ_TYPE_APP_FRAME)
		{
			check3d.setEnabled(true);
			checkDatabase.setEnabled(true);
			checkLocations.setEnabled(true);
			checkMedia.setEnabled(true);
			checkNetwork.setEnabled(true);
			checkUi.setEnabled(true);			
		}
		else
		{
			check3d.setEnabled(false);
			checkDatabase.setEnabled(false);
			checkLocations.setEnabled(false);
			checkMedia.setEnabled(false);
			checkNetwork.setEnabled(false);
			checkUi.setEnabled(false);			
		}
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}
}


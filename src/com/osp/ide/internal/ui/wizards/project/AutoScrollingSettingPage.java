package com.osp.ide.internal.ui.wizards.project;



import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.core.runtime.IStatus;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;
import com.osp.ide.wizards.NewElementWizardPage;
import com.osp.ide.wizards.StatusInfo;

public class AutoScrollingSettingPage extends NewElementWizardPage implements ISDKPathChangeListener{

	private Button fCheckAutoScroll;
	private Combo fComboResolution;
	Label fLabelResolution;
	
	private Text fMsgTxt;
	
	public AutoScrollingSettingPage() {
		super("AutoScrollingSettingPage");
		// TODO Auto-generated constructor stub
		
		setTitle("Auto-scaling Settings");
		setDescription("Mark on the checkbox to enable auto-scaling for your application. You must set a base resolution if the auto-scaling feature is turned on.");	
		setImageDescriptor(IdePlugin.getDefault().createImageDescriptor(IConstants.IMG_WIZARD));
	}

	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setFont(parent.getFont());
        
        initializeDialogUnits(parent);
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        fCheckAutoScroll= new Button(composite, SWT.CHECK);
        fCheckAutoScroll.setText("Auto-scaling");
        fCheckAutoScroll.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fCheckAutoScroll.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				handleAutoScrollButtonChanged();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
        
        Composite compResolution = new Composite(composite, SWT.NULL);
        
        layout = new GridLayout();
        layout.numColumns = 2;
        compResolution.setLayout(layout);
        compResolution.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        fLabelResolution = new Label(compResolution, SWT.NONE);
        fLabelResolution.setFont(parent.getFont());
        fLabelResolution.setLayoutData(new GridData());
        fLabelResolution.setText("Base Resolution:");        
        
        
        fComboResolution = new Combo(compResolution, SWT.DROP_DOWN|SWT.READ_ONLY);
        GridData gd = new GridData();
        gd.widthHint = 100;
        fComboResolution.setLayoutData(gd);
        fComboResolution.add(IConstants.STR_SCREEN_WVGA);
        fComboResolution.add(IConstants.STR_SCREEN_WQVGA);
        fComboResolution.select(0);
    	
        fComboResolution.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				handleResolutionChanged();
			}
		});
        
        Label label_dummy = new Label(composite, SWT.NONE);
        label_dummy.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        
        fMsgTxt = new Text(composite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 60;
        gd.widthHint = 100;
        gd.horizontalSpan = 1;
        fMsgTxt.setLayoutData(gd);
        fMsgTxt.setText("Enable auto-scaling if you want your application UI to be automatically scaled to match the device resolution. You can simply develop the application UI using the base resolution that you defined for the UI. If you want to handle the application UI for different device resolutions yourself, disable auto-scaling.");
        
        
        setControl(composite);
        
    	
    	if( getDataStore() != null ) getDataStore().addSdkPathChangeListener(this);

    	handleAutoScrollButtonChanged();
    	handleResolutionChanged();    	
    	modelChanged(getDataStore().getModel());
    	
        updateStatus(new IStatus[] { new StatusInfo() });
	}
	
	protected void handleResolutionChanged()
	{
		getDataStore().setBaseResolution(fComboResolution.getText());
	}
	
	protected void handleAutoScrollButtonChanged()
	{
		boolean enabled = fCheckAutoScroll.getSelection();
		
		fComboResolution.setEnabled(enabled);
		fLabelResolution.setEnabled(enabled);
		
		getDataStore().setAutoScrollEnabled(enabled);
	}

	@Override
	public void sdkPathChanged(String skdPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modelChanged(String model) {
		// TODO Auto-generated method stub
		if( model != null && model.startsWith("WaveWQ_") )
		{
			fComboResolution.select(1);
		}
		else
		{
			fComboResolution.select(0);
		}
		
		getDataStore().setBaseResolution(fComboResolution.getText());
	}
	
	
}

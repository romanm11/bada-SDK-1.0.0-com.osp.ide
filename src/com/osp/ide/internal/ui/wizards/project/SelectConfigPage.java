package com.osp.ide.internal.ui.wizards.project;

import java.util.ArrayList;

import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.ui.properties.ManagedBuilderUIImages;
import org.eclipse.cdt.managedbuilder.ui.wizards.CfgHolder;
import org.eclipse.cdt.ui.newui.CDTPrefUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;
import com.osp.ide.wizards.NewElementWizardPage;
import com.osp.ide.wizards.OSPCCProjectWizard;
import com.osp.ide.wizards.StatusInfo;


public class SelectConfigPage extends NewElementWizardPage implements IToolChainChangeListener {

	private static final Image IMG = ManagedBuilderUIImages.get(ManagedBuilderUIImages.IMG_BUILD_CONFIG);
	
	private Table config_table;
	private CheckboxTableViewer config_viewer;
	private Label l_projtype;
	private Label l_chains;
	
	StatusInfo fStatus;
	
	public SelectConfigPage() {
		super("SelectConfigPage");
		// TODO Auto-generated constructor stub
		
		setTitle("Select Configurations");
		setDescription("Select the configurations on which you want to deploy.");
		setImageDescriptor(IdePlugin.getDefault().createImageDescriptor(IConstants.IMG_WIZARD));
		
		fStatus = new StatusInfo();
	}	
	
	
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
//        initializeDialogUnits(parent);
		
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        
		Composite c1 = new Composite(composite, SWT.NONE);
		c1.setLayout(new GridLayout(2, false));
		c1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		setupLabel(c1, "Project type:", GridData.BEGINNING); //$NON-NLS-1$
		l_projtype = setupLabel(c1, "", GridData.FILL_HORIZONTAL);
		setupLabel(c1, "Toolchains:", GridData.BEGINNING); //$NON-NLS-1$
		l_chains = setupLabel(c1, "", GridData.FILL_HORIZONTAL);
		setupLabel(c1, "Configurations:", GridData.BEGINNING); //$NON-NLS-1$
		setupLabel(c1, "", GridData.BEGINNING);
			
		Composite c2 = new Composite(composite, SWT.NONE);
		c2.setLayout(new GridLayout(2, false));
		c2.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		config_table = new Table(c2, SWT.BORDER | SWT.CHECK | SWT.V_SCROLL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		config_table.setLayoutData(gd);
		
		config_viewer = new CheckboxTableViewer(config_table);
		config_viewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				return (Object[])inputElement;
			}
			public void dispose() {}
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
		});
		config_viewer.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return element == null ? "" : ((CfgHolder)element).getName();
			}
			public Image getImage(Object element) { return IMG; }
		});
		config_viewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				checkedItemChanged();
				
			}});
		
		Composite c = new Composite(c2, SWT.NONE);
		c.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		c.setLayout(new GridLayout());

		Button b1 = new Button(c, SWT.PUSH);
		b1.setText("Select all"); //$NON-NLS-1$
		b1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		b1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) { 
				config_viewer.setAllChecked(true);
				updatefield();
			}});

		Button b2 = new Button(c, SWT.PUSH);
		b2.setText("Deselect all"); //$NON-NLS-1$
		b2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		b2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				config_viewer.setAllChecked(false);
				updatefield();
			}});

		// dummy placeholder
		new Label(c, 0).setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Button b3 = new Button(c, SWT.PUSH);
		b3.setText("Advanced settings..."); //$NON-NLS-1$
		b3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		b3.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				advancedDialog();
			}});

		Group gr = new Group(composite, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gr.setLayoutData(gd);
		gr.setLayout(new FillLayout());
		Label lb = new Label(gr, SWT.NONE);
		lb.setText("To edit the project's properties, click the \"Advanced settings\" button.\n\nAdditional configurations can be added after the project creation.\nTo add them, click the \"Manage configuration\" button on the toolbar or the property pages.");		
				
		
        setControl(composite);
        
		setConfigurations();
        updatefield();
        
        OSPCreateDataStore store = getDataStore();
        if( store!= null ) store.addToolChainChangeListener(this);
	}

    private Label setupLabel(Composite c, String name, int mode) {
		Label l = new Label(c, SWT.WRAP);
		l.setText(name);
		GridData gd = new GridData(mode);
		gd.verticalAlignment = SWT.TOP;
		l.setLayoutData(gd);
		Composite p = l.getParent();
		l.setFont(p.getFont());
		return l;
	}
    
	private void checkedItemChanged()
	{
		CfgHolder[] its = null;
		if( config_table.getItemCount() > 0 )
		{
			ArrayList<CfgHolder> out = new ArrayList<CfgHolder>(config_table.getItemCount());
			for (TableItem ti : config_table.getItems()) {
				if (ti.getChecked())
					out.add((CfgHolder)ti.getData()); 
			}
			its = out.toArray(new CfgHolder[out.size()]);
		}
		
        OSPCreateDataStore store = getDataStore();
		if( store != null) store.setCfgHolder(its);
		
		updatefield();
	}

	
	private void advancedDialog() {
		
		if (getWizard() instanceof OSPCCProjectWizard) {
			OSPCCProjectWizard nmWizard = (OSPCCProjectWizard)getWizard();
			IProject newProject = nmWizard.getProject(false);
			if (newProject != null) {
				boolean oldManage = CDTPrefUtil.getBool(CDTPrefUtil.KEY_NOMNG);
				// disable manage configurations button
				CDTPrefUtil.setBool(CDTPrefUtil.KEY_NOMNG, true);
				try {
					PreferenceDialog dlg = PreferencesUtil.createPropertyDialogOn(getWizard().getContainer().getShell(), newProject, null , null, null);
					if( dlg != null )
					{
						int res = dlg.open();
						if (res != Window.OK) {
							// if user presses cancel, remove the project.
							nmWizard.performCancel();
							checkedItemChanged();
						}
						else
						{
							checkedItemChanged();
						}
					}
					else
					{
						checkedItemChanged();
					}
				} finally {
					CDTPrefUtil.setBool(CDTPrefUtil.KEY_NOMNG, oldManage);
				}
			}
		}
	}
	
	
	private void setConfigurations()
	{
		OSPCreateDataStore store = getDataStore();
		if( store != null )
		{
			config_table.removeAll();
			
			IToolChain tc = store.getToolChain();
			if( tc != null )
			{
				CfgHolder[] holder = ToolChainUtil.getDefaultCfgs(tc, store.getSelectedArtifactType());
				if( holder != null && holder.length > 0)
				{
					config_viewer.setInput(CfgHolder.unique(holder));
					config_viewer.setAllChecked(true);
					
					store.setCfgHolder(holder);
				}
				
				l_chains.setText(tc.getUniqueRealName());				
			}
			else
			{
				store.setCfgHolder(null);
				l_chains.setText("");
			}
		}
	}
	
	private void updatefield()
	{
		if (config_table.getItemCount() == 0) {
			fStatus.setError("Build configurations is empty");
		}
		else if (config_viewer.getCheckedElements().length == 0) {
			fStatus.setError("No selected build configurations");
		}
		else {
			fStatus.setOK();
		}
		
		updateStatus(new IStatus[] { fStatus });
	}

	public void toolChainChanged(IToolChain tc) {
		// TODO Auto-generated method stub
		setConfigurations();
		checkedItemChanged();
        updatefield();
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);

    	OSPCreateDataStore store = getDataStore();
    	if( store!= null ) 
		{
			l_projtype.setText(OSPCCProjectHelper.getProjectTypeLabel(store.getProjectType()));
		}
	}

}

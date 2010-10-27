package com.osp.ide.preference;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;

public class ManifestPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	MainfestStringFieldEditor fDeveloperSite;
	
	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		
//		Composite parent = getFieldEditorParent();
		
//		Group groupSite = new Group(parent, SWT.NONE);
//		groupSite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		groupSite.setText("bada Developers Site");
		
		
		fDeveloperSite = new MainfestStringFieldEditor(
				IConstants.PREP_ID_DEVELOPER_SITE,
				"Address:",
				getFieldEditorParent());
		addField(fDeveloperSite);	
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		setPreferenceStore(IdePlugin.getDefault().getPreferenceStore());
	}
	
	protected void performDefaults() {
		// TODO Auto-generated method stub
		//super.performDefaults();
		fDeveloperSite.loadDefault();
	}

	@Override
	public boolean performOk() {
		// TODO Auto-generated method stub
		fDeveloperSite.store();
		return super.performOk();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub
		super.propertyChange(event);
		
//		if( event.getSource() instanceof MainfestStringFieldEditor )
//		{
//			MainfestStringFieldEditor editor = (MainfestStringFieldEditor) event.getSource();
//			String newValue = editor.getStringValue();
//		}
	}	
}

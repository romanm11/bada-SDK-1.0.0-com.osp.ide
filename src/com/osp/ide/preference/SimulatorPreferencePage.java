package com.osp.ide.preference;

import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;

public class SimulatorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	IntegerFieldEditor fSimulatorPort;

	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		
		fSimulatorPort = new IntegerFieldEditor(
				IConstants.PREP_ID_SIMULATOR_PORT,
				"Simulator connection port:",
				getFieldEditorParent());
		addField(fSimulatorPort);	
		
	}
	
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		setPreferenceStore(IdePlugin.getDefault().getPreferenceStore());
	}	
	
	@Override
	protected void performDefaults() {
		// TODO Auto-generated method stub
		//super.performDefaults();
		fSimulatorPort.loadDefault();
	}

	@Override
	public boolean performOk() {
		// TODO Auto-generated method stub
		fSimulatorPort.store();
		
		return super.performOk();
	}
}

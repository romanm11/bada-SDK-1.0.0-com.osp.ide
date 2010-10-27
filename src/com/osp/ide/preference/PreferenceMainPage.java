package com.osp.ide.preference;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferenceMainPage extends PreferencePage implements IWorkbenchPreferencePage {

	@Override
	protected Control createContents(Composite parent) {
		// TODO Auto-generated method stub
		
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);
		composite.setFont(parent.getFont());
		
		
		Label label = new Label(composite, SWT.NONE);
		label.setText("Setting bada Preference");
		
		return composite;
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}

}

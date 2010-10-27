package com.osp.ide.internal.actions;

import org.eclipse.cdt.internal.ui.ICHelpContextIds;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.PlatformUI;

import com.osp.ide.wizards.OspWizardRegistry;


public class NewProjectDropDownAction extends AbstractWizardDropDownAction {

	
	public NewProjectDropDownAction() {
	    super();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, ICHelpContextIds.OPEN_PROJECT_WIZARD_ACTION);
	}
	
	
	@Override
	protected IAction[] getWizardActions() {
		// TODO Auto-generated method stub
		return OspWizardRegistry.getProjectWizardActions();
	}
}

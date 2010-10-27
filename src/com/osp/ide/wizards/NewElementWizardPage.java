package com.osp.ide.wizards;


import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;

import com.osp.ide.internal.ui.wizards.project.ICreateDataStore;
import com.osp.ide.internal.ui.wizards.project.OSPCreateDataStore;

/**
 * Base class for wizard page responsible to create C elements. The class
 * provides API to update the wizard's status line and OK button according to
 * the value of a <code>IStatus</code> object.
 */
public abstract class NewElementWizardPage extends WizardPage {

	private IStatus fCurrStatus;
	private boolean fPageVisible;
	
	private OSPCreateDataStore dataStore=null;	

	/**
	 * Creates a <code>NewElementWizardPage</code>.
	 * 
	 * @param name the wizard page's name
	 */	
	public NewElementWizardPage(String name) {
		super(name);
		fPageVisible = false;
		fCurrStatus =  new StatusInfo();
	}
		
	// ---- WizardPage ----------------
	
	/*
	 * @see WizardPage#becomesVisible
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		fPageVisible = visible;
		// policy: wizards are not allowed to come up with an error message
		if (visible && fCurrStatus.matches(IStatus.ERROR)) {
			StatusInfo status = new StatusInfo();
			status.setError("");  //$NON-NLS-1$
			fCurrStatus = status;
		} 
		updateStatus(fCurrStatus);
	}	

	/**
	 * Updates the status line and the ok button according to the given status
	 * 
	 * @param status status to apply
	 */
	protected void updateStatus(IStatus status) {
		fCurrStatus = status;
		setPageComplete(!status.matches(IStatus.ERROR));
		if (fPageVisible) {
			applyToStatusLine(this, status);
		}
	}
	
	/**
	 * Updates the status line and the ok button according to the status evaluate from
	 * an array of status. The most severe error is taken.  In case that two status with 
	 * the same severity exists, the status with lower index is taken.
	 * 
	 * @param status the array of status
	 */
	protected void updateStatus(IStatus[] status) {
		updateStatus(getMostSevere(status));
	}	
			
	public static IStatus getMostSevere(IStatus[] status) {
		IStatus max= null;
		for (int i= 0; i < status.length; i++) {
			IStatus curr= status[i];
			if (curr.matches(IStatus.ERROR)) {
				return curr;
			}
			if (max == null || curr.getSeverity() > max.getSeverity()) {
				max= curr;
			}
		}
		return max;
	}	
	
	public static void applyToStatusLine(DialogPage page, IStatus status) {
		String message= status.getMessage();
		switch (status.getSeverity()) {
			case IStatus.OK:
				page.setMessage(message, IMessageProvider.NONE);
				page.setErrorMessage(null);
				break;
			case IStatus.WARNING:
				page.setMessage(message, IMessageProvider.WARNING);
				page.setErrorMessage(null);
				break;				
			case IStatus.INFO:
				page.setMessage(message, IMessageProvider.INFORMATION);
				page.setErrorMessage(null);
				break;			
			default:
				if (message.length() == 0) {
					message= null;
				}
				page.setMessage(null);
				page.setErrorMessage(message);
				break;		
		}
	}
	
	protected OSPCreateDataStore getDataStore()
	{
		if( dataStore == null )
		{
			IWizard wiz= getWizard();
			if( wiz instanceof ICreateDataStore)
				return ((ICreateDataStore)wiz).getDataStore();
			
			return null;
		}
		
		return dataStore;
	}	
}
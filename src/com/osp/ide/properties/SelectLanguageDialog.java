package com.osp.ide.properties;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.osp.ide.core.AppNameItem;
import com.osp.ide.core.LanguageData;
import com.osp.ide.core.LanguageListXmlStore;


public class SelectLanguageDialog extends Dialog {

	protected Text fTextName;
	
	private Group fLangGroup;
	private List langList;
	
	private String fName="";
	private String fLanguage="";
	private String fId="";
	
	private java.util.List <AppNameItem> nameList = null;
	
	/** 
	 * Constructor for SelectLanguageDialog. 
	 */
	public SelectLanguageDialog( Shell parentShell, java.util.List <AppNameItem> nameList ) {
		super( parentShell );
		this.nameList = nameList;
	}
	
	public void create() {
		// TODO Auto-generated method stub
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE);
		super.create();
		
		getShell().setSize(300,450);
	}	
	
	protected Control createDialogArea( Composite parent ) {
		Composite composite = (Composite)super.createDialogArea( parent );
	
		Composite subComp = new Composite(composite, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 2;
		layout.horizontalSpacing = 2;
		subComp.setLayout(layout);
		subComp.setLayoutData(new GridData(GridData.FILL_BOTH ));
		subComp.setFont( parent.getFont() );
		
        fLangGroup = new Group(subComp, SWT.NONE);
        fLangGroup.setLayout(new GridLayout(1, false));
        
        fLangGroup.setText("Select Language");
	    fLangGroup.setLayoutData(new GridData(GridData.FILL_BOTH));


		langList = new List(fLangGroup, SWT.NONE | SWT.BORDER | SWT.V_SCROLL);
		GridData gd = new GridData(GridData.FILL_BOTH );
        gd.minimumHeight = 80;
		langList.setLayoutData(gd);
		langList.addSelectionListener(new SelectionAdapter () {
			public void widgetSelected( SelectionEvent event ) {
				if(langList.getSelectionIndex() != -1) {
					fLanguage = langList.getItem(langList.getSelectionIndex());
				}
			}
		});
		setLangListEntry(langList);
		
		Composite nameComp = new Composite(composite, SWT.NULL); 
		nameComp.setLayout(new GridLayout(2, false));
		nameComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL ));
		nameComp.setFont( parent.getFont() );
		
		Label label = new Label(nameComp, SWT.NONE);
		label.setText("Name");
		label.setLayoutData(new GridData());
	
		fTextName = new Text( nameComp, SWT.SINGLE | SWT.BORDER );
		fTextName.setText(fName);		
		fTextName.setLayoutData( new GridData( GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL ) );
		fTextName.addModifyListener( new ModifyListener() {
	
			public void modifyText( ModifyEvent e ) {
				updateOKButton();
			}
		} );
	
		applyDialogFont( composite );
		return composite;
	}
	
	protected void configureShell( Shell newShell ) {
		super.configureShell( newShell );
		newShell.setText( "Select Language" ); //$NON-NLS-1$
	}
	
	public String getName() {
		return fName;
	}
	
	public String getSelectedId() {
		
		if( fLanguage != null && fLanguage.length() > 0 )
		{
			fId = LanguageListXmlStore.getInstance().findId(fLanguage);
		}
		else
			fId = "";
		
		return fId;
	}
	
	
	protected void buttonPressed( int buttonId ) {
		if ( buttonId == IDialogConstants.OK_ID ) {
			fName = fTextName.getText().trim();
		}
		else {
		}
		super.buttonPressed( buttonId );
	}
	
	protected void updateOKButton() {
		Button okButton = getButton( IDialogConstants.OK_ID );
		String text = fTextName.getText();
		okButton.setEnabled( isValid( text ) );
	}
	
	protected boolean isValid( String text ) {
		boolean valid = text.trim().length() > 0;
		// if( valid ) valid = (text.trim().indexOf(" ") == -1);
		return (valid);
	}
	
	protected Control createButtonBar( Composite parent ) {
		Control control = super.createButtonBar( parent );
		
		Button okButton = getButton(IDialogConstants.OK_ID);
		okButton.setText("Add");
		
        Button closeButton = getButton(IDialogConstants.CANCEL_ID);
        closeButton.setText("Cancel");		
		
		updateOKButton();
		return control;
	}
	
	private boolean alreadyExists(String lang)
	{
		if( nameList == null || nameList.size() <= 0 ) return false;
		
		Iterator<AppNameItem> itName = nameList.iterator();
		while (itName.hasNext()) {
			AppNameItem item = itName.next();
			if( item.getLanugage().equals(lang)) return true;
		}		
		
		
		return false;
	}
	
	private void setLangListEntry(List list) {
		list.removeAll();

		ArrayList<LanguageData> languageList = LanguageListXmlStore.getInstance().getLanguageList();
		
		Iterator<LanguageData> itName = languageList.iterator();
		while (itName.hasNext()) {
			LanguageData item = itName.next();
			
			if( fId != null && fId.length() > 0 )
			{
				if( fId.equals(item.getId()))
				{
					fLanguage = item.getName();
				}
			}
			
			if( !alreadyExists(item.getId()) )
				list.add(item.getName());			
		}
		
		if( list.getItemCount() > 0 )
		{
			int index = 0;
			if( fLanguage != null || fLanguage.length() > 0 )
			{
				index = list.indexOf(fLanguage);
				if( index == -1 ) index = 0;
			}
			
			list.select(index);
			fLanguage = langList.getItem(index);
		}
	}
	
}
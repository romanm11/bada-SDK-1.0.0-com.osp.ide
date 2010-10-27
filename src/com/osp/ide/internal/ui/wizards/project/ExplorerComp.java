package com.osp.ide.internal.ui.wizards.project;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ExplorerComp extends Composite {

	private OleClientSite site;
	private OleAutomation automation;
	
	String currURL="";
	OleFrame frame;
	
	public ExplorerComp(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
		
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        this.setLayout(layout);	
        this.setLayoutData(new GridData(GridData.FILL_BOTH));		
		
		try {
			frame = new OleFrame(this, SWT.NONE);
			frame.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			site = new OleClientSite(frame, SWT.NONE, "Shell.Explorer");
			site.doVerb(OLE.OLEIVERB_SHOW);
			automation = new OleAutomation(site);
			
		} catch (SWTError e) {
			Label label = new Label(this, SWT.NONE);
			
			label.setText("Unable to open activeX control");
		}		
		
	}
	
	public void setUrl(String url)
	{
		currURL = url;
	}
		
	public void showBrowser(boolean bShow)
	{
		GridData gd = (GridData)frame.getLayoutData();
		if(bShow)
		{
//			gd.widthHint = 1024;
//			gd.heightHint = 600;
			frame.setVisible(true);
			openUrl(currURL);
		}
		else
		{
//			gd.widthHint = 320;
//			gd.heightHint = 240;			
			frame.setVisible(false);
		}
	}
	
	
	private void openUrl(String url)
	{
		int[] rgdispid = automation.getIDsOfNames(new String[]{"Navigate"});
		if( rgdispid != null )
		{
			int dispIdMember = rgdispid[0];
			Variant[] rgvarg = new Variant[1]; // this is the URL parameter
			rgvarg[0] = new Variant(url);
			Variant pVarResult = automation.invoke(dispIdMember, rgvarg);
		}
	}

}


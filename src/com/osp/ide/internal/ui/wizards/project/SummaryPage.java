package com.osp.ide.internal.ui.wizards.project;

import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.ui.wizards.CfgHolder;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;
import com.osp.ide.wizards.NewElementWizardPage;
import com.osp.ide.wizards.StatusInfo;

public class SummaryPage extends NewElementWizardPage {

	private final String TAB = "\t";
	private final String LINE_FEED = "\r\n";
	
	Text fSummary;
	
	public SummaryPage() {
		super("SummaryPage");
		// TODO Auto-generated constructor stub
		
		setTitle("Summary");
		setDescription("");
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
        
        fSummary = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL);
        fSummary.setLayoutData(new GridData(GridData.FILL_BOTH));        
        
        setControl(composite);
        
        updateStatus(new IStatus[] { new StatusInfo() });
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
    	refresh();
	}
	
	private void refresh()
	{
		OSPCreateDataStore store = getDataStore();
		if( store != null)
		{
			String text="";
			text += "Project Name:" + LINE_FEED;
			text += TAB + store.getProjectName() + LINE_FEED;
			
			text += LINE_FEED;
			text += "Toolchain:" + LINE_FEED;
			IToolChain tc = store.getToolChain(); 
			
			if( tc != null)
				text += TAB + tc.getUniqueRealName() + LINE_FEED;
			else
				text += LINE_FEED;
			
			text += LINE_FEED;
			text += "Selected Configurations:" + LINE_FEED;
			
			CfgHolder[] holder = store.getCfgHolder();
			if( holder == null && tc != null )
				holder = ToolChainUtil.getDefaultCfgs(tc, store.getSelectedArtifactType());
			
			if( holder != null ) {
				for( int i = 0; i < holder.length; i++ ) {
					text += TAB + holder[i].getName() + LINE_FEED;
				}
			}
			else {
				text += TAB + "No build configurations" + LINE_FEED;
			}
			
			text += LINE_FEED;
			text += "SDK:" + LINE_FEED;
			text += TAB + store.getSdkPath() + LINE_FEED;

			text += LINE_FEED;
			text += "Model:" + LINE_FEED;
			text += TAB + store.getModel() + LINE_FEED;			
			
			text += LINE_FEED;
			text += "Basic Settings:" + LINE_FEED;
			text += TAB + "Entry: " + store.getProjectName() + LINE_FEED;
			text += TAB + "Name: " + store.getAppName() + LINE_FEED;
			text += TAB + "Vendor: " + store.getVendor() + LINE_FEED;
			text += TAB + "Description: " + store.getDescription() + LINE_FEED;
			
			text += LINE_FEED;
			text += "Device configuration:" + LINE_FEED;
//			text += TAB + "CPU: " + store.getCpuType()+ LINE_FEED;

			String size[] = store.getScreenSize();
			String sizeStr = "";
			
			for( int j = 0; j < size.length; j++)
			{
				if( j == 0 ) sizeStr = size[j];
				else sizeStr += ", " + size[j];
			}
			text += TAB + "Screen Size: " + sizeStr + LINE_FEED;
			
			text += TAB + "Touch screen support: ";
			if(store.getTouchScreen()) text += "true" + LINE_FEED;
			else text += "false" + LINE_FEED;

			fSummary.setText(text);
			
		}
	}		

}

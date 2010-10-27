package com.osp.ide.editor;

import java.util.Iterator;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.IFileBuffer;
import org.eclipse.core.filebuffers.IFileBufferListener;
import org.eclipse.core.filebuffers.IFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import com.osp.ide.IConstants;
import com.osp.ide.core.AppNameItem;
import com.osp.ide.core.AppXmlStore;
import com.osp.ide.core.LanguageListXmlStore;

public class AppXmlEditor extends EditorPart {

	private Text fEntry;
	private Table fNameTable;
	private Text fVendor;
	private Text fDesc;
	
	private Group fnameGroup;
	private Group fIconGroup;
	private Group fAutoScalingGroup;
	
	private Text fIconMainMenu;
	private Text fIconSetting;
	private Text fIconTicker;
	private Text fIconQuickPanel;
	private Text fIconLaunchImage;
	
	private Button fCheckAutoScroll;
	private Text fTextResolution;
	
//	private Text fSdkPath;
	
	IFileBufferManager manager=null;
	IPath fullPath=null;
	
	private final IFileBufferListener fFileBufferListener= new FileBufferListener();
	
	protected class FileBufferListener implements IFileBufferListener  {

		public void bufferContentAboutToBeReplaced(IFileBuffer buffer) {
			// TODO Auto-generated method stub
		}

		public void bufferContentReplaced(IFileBuffer buffer) {
			// TODO Auto-generated method stub
			elementContentReplaced();
		}

		public void bufferCreated(IFileBuffer buffer) {
			// TODO Auto-generated method stub
		}

		public void bufferDisposed(IFileBuffer buffer) {
			// TODO Auto-generated method stub
		}

		public void dirtyStateChanged(IFileBuffer buffer, boolean isDirty) {
			// TODO Auto-generated method stub
		}

		public void stateChangeFailed(IFileBuffer buffer) {
			// TODO Auto-generated method stub
		}

		public void stateChanging(IFileBuffer buffer) {
			// TODO Auto-generated method stub
		}

		public void stateValidationChanged(IFileBuffer buffer,
				boolean isStateValidated) {
			// TODO Auto-generated method stub
		}

		public void underlyingFileDeleted(IFileBuffer buffer) {
			// TODO Auto-generated method stub
			elementDeleted(buffer.getLocation());
			
		}

		public void underlyingFileMoved(IFileBuffer buffer, IPath path) {
			// TODO Auto-generated method stub
		}
	};
	
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
	
		setSite(site);
		setInput(input);
		setPartName(input.getName());
		
		if (input instanceof FileEditorInput) {
			manager = FileBuffers.getTextFileBufferManager();
			if( manager != null)
			{
				manager.addFileBufferListener(fFileBufferListener);
				
				fullPath = ((FileEditorInput)input).getFile().getFullPath();
				try {
					manager.connect(fullPath, LocationKind.IFILE, new NullProgressMonitor());
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
	}

	
	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}


	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		parent.setLayout(new GridLayout());
		
		Display display = parent.getDisplay();
		Color bg = JFaceColors.getBannerBackground( display );
//		Color fg = JFaceColors.getBannerForeground( display );
		parent.setBackground(bg);
		
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setFont(parent.getFont());
		composite.setBackground(bg);
        
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.verticalSpacing = 10;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        Label label = new Label(composite, SWT.NONE);
        label.setText(AppXmlStore.ATTR_ENTRY + IConstants.COLON);
        label.setLayoutData(new GridData());
        label.setBackground(bg);        
        
        fEntry = new Text(composite, SWT.BORDER);
        fEntry.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fEntry.setEditable(false);
        fEntry.setBackground(bg);

        
        fnameGroup = new Group(composite, SWT.NONE);
        layout = new GridLayout(1, false);
        fnameGroup.setLayout(layout);
        
        fnameGroup.setText(AppXmlStore.ELEMENT_NAME);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.minimumHeight = 80;
	    gd.heightHint = 100;
	    gd.horizontalSpan = 2;
        fnameGroup.setLayoutData(gd);
        fnameGroup.setBackground(bg);
        
        fNameTable = new Table(fnameGroup, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
        fNameTable.setLinesVisible(true);
        fNameTable.setHeaderVisible(false);
        fNameTable.setLayoutData(new GridData(GridData.FILL_BOTH));
        //fNameTable.setBackground(bg);

		TableLayout tablelayout = new TableLayout();
		tablelayout.addColumnData(new ColumnWeightData(45, 20, true));
		tablelayout.addColumnData(new ColumnWeightData(55, 20, true));
		fNameTable.setLayout(tablelayout);        
        
	    TableColumn columnLang  = new TableColumn(fNameTable,SWT.LEFT);
	    columnLang.setText("Lang");
		TableColumn columnName  = new TableColumn(fNameTable,SWT.LEFT);
		columnName.setText("Name");
	        
        label = new Label(composite, SWT.NONE);
        label.setText(AppXmlStore.ATTR_VENDOR + IConstants.COLON);
        label.setLayoutData(new GridData());
        label.setBackground(bg);
        
        fVendor = new Text(composite, SWT.BORDER);
        fVendor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fVendor.setEditable(false);
        fVendor.setBackground(bg);
        
    	label = new Label(composite, SWT.NONE);
    	label.setText(AppXmlStore.ATTR_DESC + IConstants.COLON);
    	label.setLayoutData(new GridData());
    	label.setBackground(bg);
        
    	fDesc = new Text(composite, SWT.BORDER);
        fDesc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fDesc.setEditable(false);
        fDesc.setBackground(bg);
        
        
        fIconGroup = new Group(composite, SWT.NONE);
        layout = new GridLayout(2, false);
        fIconGroup.setLayout(layout);
        
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        fIconGroup.setLayoutData(gd);
        fIconGroup.setBackground(bg);
        fIconGroup.setText(AppXmlStore.ELEMENT_ICON);
        
    	label = new Label(fIconGroup, SWT.NONE);
    	label.setText(AppXmlStore.ATTR_MAINMENU + IConstants.COLON);
    	label.setLayoutData(new GridData());
    	label.setBackground(bg);
        
    	fIconMainMenu = new Text(fIconGroup, SWT.BORDER);
    	fIconMainMenu.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	fIconMainMenu.setEditable(false);
    	fIconMainMenu.setBackground(bg);        
    	
    	label = new Label(fIconGroup, SWT.NONE);
    	label.setText(AppXmlStore.ATTR_SETTING + IConstants.COLON);
    	label.setLayoutData(new GridData());
    	label.setBackground(bg);
        
    	fIconSetting = new Text(fIconGroup, SWT.BORDER);
    	fIconSetting.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	fIconSetting.setEditable(false);
    	fIconSetting.setBackground(bg);      	
    	
        
    	label = new Label(fIconGroup, SWT.NONE);
    	label.setText(AppXmlStore.ATTR_TICKER + IConstants.COLON);
    	label.setLayoutData(new GridData());
    	label.setBackground(bg);
        
    	fIconTicker = new Text(fIconGroup, SWT.BORDER);
    	fIconTicker.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	fIconTicker.setEditable(false);
    	fIconTicker.setBackground(bg);      	

    	label = new Label(fIconGroup, SWT.NONE);
    	label.setText(AppXmlStore.ATTR_QUICKPANEL + IConstants.COLON);
    	label.setLayoutData(new GridData());
    	label.setBackground(bg);
        
    	fIconQuickPanel = new Text(fIconGroup, SWT.BORDER);
    	fIconQuickPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	fIconQuickPanel.setEditable(false);
    	fIconQuickPanel.setBackground(bg);  
    	
    	label = new Label(fIconGroup, SWT.NONE);
    	label.setText(AppXmlStore.ATTR_LAUNCHIMAGE + IConstants.COLON);
    	label.setLayoutData(new GridData());
    	label.setBackground(bg);
        
    	fIconLaunchImage = new Text(fIconGroup, SWT.BORDER);
    	fIconLaunchImage.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	fIconLaunchImage.setEditable(false);
    	fIconLaunchImage.setBackground(bg);     
/*        
    	label = new Label(composite, SWT.NONE);
    	label.setText("SDK Root:");
    	label.setLayoutData(new GridData());
    	label.setBackground(bg);

    	fSdkPath = new Text(composite, SWT.BORDER);
    	fSdkPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));        
    	fSdkPath.setEditable(false);
    	fSdkPath.setBackground(bg);
*/    	
    	
    	fAutoScalingGroup = new Group(composite, SWT.NONE);
        layout = new GridLayout(1, false);
        fAutoScalingGroup.setLayout(layout);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        fAutoScalingGroup.setLayoutData(gd);
        fAutoScalingGroup.setBackground(bg);
        
        fCheckAutoScroll= new Button(fAutoScalingGroup, SWT.CHECK);
        fCheckAutoScroll.setText("Auto-scaling");
        fCheckAutoScroll.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fCheckAutoScroll.setEnabled(false);
        fCheckAutoScroll.setBackground(bg);
    	
        Composite compResolution = new Composite(fAutoScalingGroup, SWT.NULL);
        layout = new GridLayout(2, false);
        compResolution.setLayout(layout);
        compResolution.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        compResolution.setBackground(bg);
        
        Label labelResolution = new Label(compResolution, SWT.NONE);
        labelResolution.setBackground(bg);
        labelResolution.setLayoutData(new GridData());
        labelResolution.setText("Base Resolution:");        
    	
        
        fTextResolution = new Text(compResolution, SWT.BORDER);
        fTextResolution.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fTextResolution.setEditable(false);
        fTextResolution.setBackground(bg);  
        
    	
        loadPage();
        
//        fEntry.setFocus();
	}

	private void loadPage()
	{
		IEditorInput input = getEditorInput();

		if (input instanceof IFileEditorInput)
		{
			IFile file = ((IFileEditorInput) input).getFile();
			AppXmlStore xmlManager = new AppXmlStore(file);
			xmlManager.loadXML();
			
			LanguageListXmlStore langStore = LanguageListXmlStore.getInstance();
			
			fEntry.setText(xmlManager.getEntry());
			
			fNameTable.removeAll();
			java.util.List <AppNameItem> nameList = xmlManager.getNameList();
			Iterator<AppNameItem> itName = nameList.iterator();
			while (itName.hasNext()) {
				AppNameItem item = itName.next();
				TableItem tabelItem = new TableItem(fNameTable, SWT.LEFT);
				tabelItem.setText(0, langStore.findName(item.getLanugage()));
				tabelItem.setText(1, item.getName());
			}			
			
			fIconMainMenu.setText(xmlManager.getIconMainMenu());
			fIconSetting.setText(xmlManager.getIconSetting());
			fIconTicker.setText(xmlManager.getIconTicker());
			fIconQuickPanel.setText(xmlManager.getIconQuickPanel());
			fIconLaunchImage.setText(xmlManager.getIconLaunchImage());
			
			fVendor.setText(xmlManager.getVendor());
			fDesc.setText(xmlManager.getDescription());
//			fSdkPath.setText(xmlManager.getSdkPath());
			
			boolean bAutoScroll = xmlManager.getAutoScalingEnabled();
			
			fCheckAutoScroll.setSelection(bAutoScroll);
			
			if( bAutoScroll ) fTextResolution.setText(xmlManager.getBaseResolution());
			else fTextResolution.setText("");
		}
	}
	

	public void setFocus() {
		// TODO Auto-generated method stub
		if(fEntry != null ) fEntry.setFocus();
	}
	
	

	public void dispose() {
		// TODO Auto-generated method stub
		
		if( manager != null && fullPath != null )
		{
			try {
				manager.disconnect(fullPath, LocationKind.IFILE, new NullProgressMonitor());
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			manager.removeFileBufferListener(fFileBufferListener);
		}

		
		super.dispose();
	}
	
	protected void elementContentReplaced()
	{
		loadPage();
	}

	public void elementDeleted(IPath deletedElement) {
		
		if( deletedElement != null )
		{
			IEditorInput editorInput = getEditorInput();
			if (editorInput instanceof FileEditorInput) {
				if( deletedElement.equals(((FileEditorInput)editorInput).getFile().getFullPath()))
				{
					Runnable r= new Runnable() {
						public void run() {
							close(false);
						}
					};
					execute(r, false);
				}
			}
		}
	}
	
	public void close(final boolean save) {

		getSite().getPage().closeEditor(this, false);
	}

	private void execute(Runnable runnable, boolean postAsync) {
		if (postAsync || Display.getCurrent() == null) {
			Display fDisplay= getSite().getShell().getDisplay();
			fDisplay.asyncExec(runnable);
		} else
			runnable.run();
	}	
	
}

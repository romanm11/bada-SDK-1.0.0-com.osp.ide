package com.osp.ide.editor;


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
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import com.osp.ide.core.ManifestXmlStore;
import com.osp.ide.core.PrivilegeData;

public class ManifestXmlEditor extends EditorPart {

	Table tablePrivilege;
	TableTree tableTreeDevice;
//	TableTree tableTreeAgent;
	
	Text fTextId;
	Text fTextVersion;
	
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

	public void setDirty(boolean dirty) { 
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
		
        
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout fileMainLayout = new GridLayout(1, false);
		composite.setLayout(fileMainLayout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setBackground(bg);
		
		
		Composite compMain = new Composite(composite, SWT.NONE);
		GridLayout fileGroupLayout = new GridLayout(2, false);
		fileGroupLayout.marginWidth = 0;		
		compMain.setLayout(fileGroupLayout);
		compMain.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		compMain.setBackground(bg);		
		
		
		Label labelId = new Label(compMain, SWT.NONE);
		labelId.setLayoutData(new GridData());
		labelId.setText("Id:");
		labelId.setFont(JFaceResources.getFont(JFaceResources.BANNER_FONT));
		labelId.setBackground(bg);
		
		fTextId = new Text(compMain, SWT.NONE);
		fTextId.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fTextId.setFont(JFaceResources.getFont(JFaceResources.BANNER_FONT));
		fTextId.setBackground(bg);
		fTextId.setEnabled(false);
		

		Label labelVersion = new Label(compMain, SWT.NONE);
		labelVersion.setLayoutData(new GridData());
		labelVersion.setText("AppVersion:");
		labelVersion.setFont(JFaceResources.getFont(JFaceResources.BANNER_FONT));
		labelVersion.setBackground(bg);
		
		fTextVersion = new Text(compMain, SWT.NONE);
		fTextVersion.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fTextVersion.setFont(JFaceResources.getFont(JFaceResources.BANNER_FONT));
		fTextVersion.setBackground(bg);
		fTextVersion.setEnabled(false);		
		
		// set Privilege		
		Label label_Privilege = new Label(composite, SWT.NONE);
		label_Privilege.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label_Privilege.setText("Privileges");
		label_Privilege.setFont(JFaceResources.getFont(JFaceResources.BANNER_FONT));
		label_Privilege.setBackground(bg);

		
		tablePrivilege = new Table(composite, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		tablePrivilege.setLayoutData(gridData);
		
		
		TableLayout layoutPrivilege = new TableLayout();
		layoutPrivilege.addColumnData(new ColumnWeightData(100, 30, true));
		tablePrivilege.setLayout(layoutPrivilege);
		
		TableColumn privilegeName = new TableColumn(tablePrivilege, SWT.LEFT);
		privilegeName.setText("Name");
		tablePrivilege.setHeaderVisible(false);
		tablePrivilege.setLinesVisible(true);		
		tablePrivilege.setBackground(bg);
		
		
		// set Device Profile
		Label label_Profile= new Label(composite, SWT.NONE);
		label_Profile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label_Profile.setText("Device Profile");
		label_Profile.setFont(JFaceResources.getFont(JFaceResources.BANNER_FONT));
		label_Profile.setBackground(bg);
		
		tableTreeDevice = new TableTree(composite, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		gridData = new GridData(GridData.FILL_BOTH);
		tableTreeDevice.setLayoutData(gridData);
		
	    Table tableDevice = tableTreeDevice.getTable();
	    
		TableLayout layoutDevice = new TableLayout();
		layoutDevice.addColumnData(new ColumnWeightData(35, 30, true));
		layoutDevice.addColumnData(new ColumnWeightData(65, 70, true));
		tableDevice.setLayout(layoutDevice);
		
		TableColumn DeviceName = new TableColumn(tableDevice, SWT.LEFT);
		DeviceName.setText("Name");
		TableColumn DeviceValue = new TableColumn(tableDevice, SWT.LEFT);
		DeviceValue.setText("Value");
		tableDevice.setHeaderVisible(false);
		tableDevice.setLinesVisible(true);

/*
		// set Agent		
		Label label_Agent= new Label(composite, SWT.NONE);
		label_Agent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label_Agent.setText("Agent");
		label_Agent.setFont(JFaceResources.getFont(JFaceResources.BANNER_FONT));
		label_Agent.setBackground(bg);
		
		tableTreeAgent = new TableTree(composite, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		gridData = new GridData(GridData.FILL_BOTH);
		tableTreeAgent.setLayoutData(gridData);
		
	    Table tableAgent = tableTreeAgent.getTable();
	    
		TableLayout layoutAgent = new TableLayout();
		layoutAgent.addColumnData(new ColumnWeightData(35, 30, true));
		layoutAgent.addColumnData(new ColumnWeightData(65, 70, true));
		tableAgent.setLayout(layoutAgent);
		
		TableColumn AgentName = new TableColumn(tableAgent, SWT.LEFT);
		AgentName.setText("Name");
		TableColumn AgentValue = new TableColumn(tableAgent, SWT.LEFT);
		AgentValue.setText("Value");
		tableAgent.setHeaderVisible(false);
		tableAgent.setLinesVisible(true);
*/	    
	    
	    loadPage();

	}
	
	protected void elementContentReplaced()
	{
		loadPage();
	}
	
	protected void clearPage()
	{
		fTextId.setText("");
		fTextVersion.setText("");
		
		tablePrivilege.removeAll();
		tableTreeDevice.removeAll();
	}
	
	protected void loadPage() {

		clearPage();
		
		IEditorInput input = getEditorInput();
		
		if (input instanceof IFileEditorInput)
		{
			IFile file = ((IFileEditorInput) input).getFile();
			ManifestXmlStore store = new ManifestXmlStore(file.getLocation().toOSString());
			
			fTextId.setText(store.getId());
			fTextVersion.setText(store.getAppVersion());
		
			// Load Privilege
			PrivilegeData[] privilegeData = store.getPrivileges();
			for( int i = 0; i < privilegeData.length; i++ )
			{
				TableItem newItem = new TableItem(tablePrivilege, SWT.NONE);
				newItem.setText(0, privilegeData[i].getName());
			}
			
//			TableTreeItem parent = new TableTreeItem(tableTreeDevice, SWT.NONE);
//			parent.setText(0, ManifestXmlStore.ATTR_DP_FRAMEWORK );
//			parent.setText(1, store.getFramework() );
			
			// Load Device Profile
			TableTreeItem parent = new TableTreeItem(tableTreeDevice, SWT.NONE);
			parent.setText(0, ManifestXmlStore.ATTR_DP_CPU );
			parent.setText(1, store.getCpu() );
			
			parent = new TableTreeItem(tableTreeDevice, SWT.NONE);
			parent.setText(0, ManifestXmlStore.ATTR_DP_MINIMUMHEAPSIZE );
			parent.setText(1, store.getMinimumHeapSize() );		

			parent = new TableTreeItem(tableTreeDevice, SWT.NONE);
			parent.setText(0, ManifestXmlStore.ATTR_DP_MINIMUMVRAMSIZE );
			parent.setText(1, store.getMinimumVramSize() );			
			
			parent = new TableTreeItem(tableTreeDevice, SWT.NONE);
			parent.setText(0, ManifestXmlStore.ATTR_DP_3DACCELATOR );
			String[] accel3dData = store.get3DAccelerators();
			for( int i = 0; i < accel3dData.length; i++ )
			{
				if( i == 0 ) {
					parent.setText(1, accel3dData[i] );		
				} else {
					TableTreeItem child = new TableTreeItem(parent, SWT.NONE);
					child.setText(1, accel3dData[i]);
				}
			}		
					
			parent = new TableTreeItem(tableTreeDevice, SWT.NONE);
			parent.setText(0, ManifestXmlStore.ATTR_DP_BOLUETOOTHPROFILE );
			String[] boluetoothData = store.getBoluetoothProfiles();
			for( int i = 0; i < boluetoothData.length; i++ )
			{
				if( i == 0 ) {
					parent.setText(1, boluetoothData[i] );		
				} else {
					TableTreeItem child = new TableTreeItem(parent, SWT.NONE);
					child.setText(1, boluetoothData[i]);
				}
			}		
			
			
			parent = new TableTreeItem(tableTreeDevice, SWT.NONE);
			parent.setText(0, ManifestXmlStore.ATTR_DP_WIFIMODE );
			
			String[] wifiData = store.getWifiModes();
			for( int i = 0; i < wifiData.length; i++ )
			{
				if( i == 0 ) {
					parent.setText(1, wifiData[i] );		
				} else {
					TableTreeItem child = new TableTreeItem(parent, SWT.NONE);
					child.setText(1, wifiData[i]);
				}
			}		
			
			parent = new TableTreeItem(tableTreeDevice, SWT.NONE);
			parent.setText(0, ManifestXmlStore.ATTR_DP_SENSOR );
			
			String[] sensorData = store.getSensors();
			for( int i = 0; i < sensorData.length; i++ )
			{
				if( i == 0 ) {
					parent.setText(1, sensorData[i] );		
				} else {
					TableTreeItem child = new TableTreeItem(parent, SWT.NONE);
					child.setText(1, sensorData[i]);
				}
			}

//			parent = new TableTreeItem(tableTreeDevice, SWT.NONE);
//			parent.setText(0, ManifestXmlStore.ATTR_DP_INPUTDEVICE );
//			
//			String[] inputDeviceData = store.getInputDevices();
//			for( int i = 0; i < inputDeviceData.length; i++ )
//			{
//				if( i == 0 ) {
//					parent.setText(1, inputDeviceData[i] );		
//				} else {
//					TableTreeItem child = new TableTreeItem(parent, SWT.NONE);
//					child.setText(1, inputDeviceData[i]);
//				}
//			}

			parent = new TableTreeItem(tableTreeDevice, SWT.NONE);
			parent.setText(0, ManifestXmlStore.ATTR_DP_SCREENSIZE );
			
			String[] screenSizeData = store.getScreenSizes();
			for( int i = 0; i < screenSizeData.length; i++ )
			{
				if( i == 0 ) {
					parent.setText(1, screenSizeData[i] );		
				} else {
					TableTreeItem child = new TableTreeItem(parent, SWT.NONE);
					child.setText(1, screenSizeData[i]);
				}
			}
			
			parent = new TableTreeItem(tableTreeDevice, SWT.NONE);
			parent.setText(0, ManifestXmlStore.ATTR_DP_SOUNDMIXING );
			parent.setText(1, store.getSoundMixing() );
			
/*			
			// Load Agent
			parent = new TableTreeItem(tableTreeAgent, SWT.NONE);
			parent.setText(0, ManifestXmlStore.ATTR_AG_LAUNCHONBOOT );
			parent.setText(1, store.getLaunchOnBoot());
*/			
		}
		
	}
	

	public void setFocus() {
		// TODO Auto-generated method stub
		if(tablePrivilege != null ) tablePrivilege.setFocus();
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

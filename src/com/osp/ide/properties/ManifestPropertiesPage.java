package com.osp.ide.properties;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import com.osp.ide.IdePlugin;
import com.osp.ide.core.ManifestXmlStore;
import com.osp.ide.core.PrivilegeData;
import com.osp.ide.core.badaNature;

public class ManifestPropertiesPage extends PropertyPage {

	protected IProject fProject = null;
	protected boolean noContentOnPage = false;
	
	Table tablePrivilege;
	TableTree tableTreeDevice;
//	TableTree tableTreeAgent;
	
	Text fTextId;
	Text fTextVersion;	
	
	@Override
	protected Control createContents(Composite parent) {
		// TODO Auto-generated method stub
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		GridLayout compositeLayout = new GridLayout();
		compositeLayout.numColumns = 1;
		compositeLayout.marginHeight = 0;
		compositeLayout.marginWidth = 0;
		composite.setLayout( compositeLayout );
		
		String s = null;
		if (!checkElement()) {
			s = "This element not a project";
		} 
		else
		{
			fProject = getProject();
			if (!isBadaProject(fProject)) 
				s = "This project is not a bada project"; //$NON-NLS-1$
		}
		
	    if (s == null) {
	    	createWidgets(composite);
	    	return composite;
	    }		
		
		// no contents
		Label label = new Label(composite, SWT.LEFT);
		label.setText(s);
		label.setFont(composite.getFont());
		noContentOnPage = true;
		noDefaultAndApplyButton();
		return composite;
	}

	protected boolean checkElement() {
		boolean isProject=false;
		IResource internalElement = null;
		
		IAdaptable el = super.getElement();
		if (el instanceof ICElement) 
			internalElement = ((ICElement)el).getResource();
		else if (el instanceof IResource) 
			internalElement = (IResource)el;
		if (internalElement == null) return false;
		isProject = internalElement instanceof IProject;

		return isProject;
	}
	
	public IProject getProject() {
		Object element = getElement();
		if (element != null) { 
			if (element instanceof IProject)
			{
				IResource f = (IResource) element;
				return f.getProject();
			}
			else if (element instanceof ICProject)
				return ((ICProject)element).getProject();
		}
		return null;
	}
	
	public static boolean isCDTPrj(IProject p) {
		ICProjectDescription prjd = CoreModel.getDefault().getProjectDescription(p, false); 
		if (prjd == null) return false; 
		ICConfigurationDescription[] cfgs = prjd.getConfigurations();
		return (cfgs != null && cfgs.length > 0);
	}
	
	public boolean isBadaProject(IProject p) {
		ICProjectDescription prjd = CoreModel.getDefault().getProjectDescription(p, false); 
		if (prjd == null) return false; 
		ICConfigurationDescription[] cfgs = prjd.getConfigurations();
		
		boolean flag = (cfgs != null && cfgs.length > 0);
		
		if( flag )
		{
			try {
				flag = p.hasNature(badaNature.OSP_NATURE_ID);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				flag = false;
			}
		}
		return flag;
	}
	
	protected void createWidgets(Composite parent) {
		// TODO Auto-generated method stub
		createManifestGroup(parent);
		
        loadData();
	}
	
	
	protected void createManifestGroup(Composite parent) {
	
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout fileMainLayout = new GridLayout(1, false);
		composite.setLayout(fileMainLayout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// Set Main
		Composite compMain = new Composite(composite, SWT.NONE);
		GridLayout fileGroupLayout = new GridLayout(2, false);
		fileGroupLayout.marginWidth = 0;
		compMain.setLayout(fileGroupLayout);
		compMain.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		Label labelId = new Label(compMain, SWT.NONE);
		labelId.setLayoutData(new GridData());
		labelId.setText("Id:");
		labelId.setFont(JFaceResources.getFont(JFaceResources.BANNER_FONT));
		
		fTextId = new Text(compMain, SWT.NONE);
		fTextId.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fTextId.setFont(JFaceResources.getFont(JFaceResources.BANNER_FONT));
		fTextId.setEnabled(false);
		

		Label labelVersion = new Label(compMain, SWT.NONE);
		labelVersion.setLayoutData(new GridData());
		labelVersion.setText("AppVersion:");
		labelVersion.setFont(JFaceResources.getFont(JFaceResources.BANNER_FONT));
		
		fTextVersion = new Text(compMain, SWT.NONE);
		fTextVersion.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fTextVersion.setFont(JFaceResources.getFont(JFaceResources.BANNER_FONT));
		fTextVersion.setEnabled(false);		
		
		// set Privilege
		Label label_Privilege = new Label(composite, SWT.NONE);
		label_Privilege.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label_Privilege.setText("Privileges");
		label_Privilege.setFont(JFaceResources.getFont(JFaceResources.BANNER_FONT));

		
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
	
		
		// set Device Profile
		Label label_Profile= new Label(composite, SWT.NONE);
		label_Profile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label_Profile.setText("Device Profile");
		label_Profile.setFont(JFaceResources.getFont(JFaceResources.BANNER_FONT));
		
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
	}
	
	protected void loadData() {
		ManifestXmlStore store = IdePlugin.getDefault().getManifestXmlStore(fProject);
		
		fTextId.setText(store.getId());
		fTextVersion.setText(store.getAppVersion());
		
		PrivilegeData[] privilegeData = store.getPrivileges();
		for( int i = 0; i < privilegeData.length; i++ )
		{
			TableItem newItem = new TableItem(tablePrivilege, SWT.NONE);
			newItem.setText(0, privilegeData[i].getName());
		}

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

//		parent = new TableTreeItem(tableTreeDevice, SWT.NONE);
//		parent.setText(0, ManifestXmlStore.ATTR_DP_INPUTDEVICE );
//		
//		String[] inputDeviceData = store.getInputDevices();
//		for( int i = 0; i < inputDeviceData.length; i++ )
//		{
//			if( i == 0 ) {
//				parent.setText(1, inputDeviceData[i] );		
//			} else {
//				TableTreeItem child = new TableTreeItem(parent, SWT.NONE);
//				child.setText(1, inputDeviceData[i]);
//			}
//		}

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
	
	protected boolean isNoContentOnPage()
	{
		return noContentOnPage;
	}
    
	public void performApply() { 
		performSave(); 
	}
    
	public boolean performOk() {
		return performSave(); 
    }
	
	private boolean performSave()	{
		return true;
	}
	
}

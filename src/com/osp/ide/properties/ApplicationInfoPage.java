package com.osp.ide.properties;


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.PropertyPage;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;
import com.osp.ide.core.AppNameItem;
import com.osp.ide.core.AppXmlStore;
import com.osp.ide.core.LanguageData;
import com.osp.ide.core.LanguageListXmlStore;
import com.osp.ide.core.OspPropertyStore;
import com.osp.ide.core.badaNature;
import com.osp.ide.internal.ui.wizards.project.OSPCCProjectHelper;
import com.osp.ide.internal.ui.wizards.project.TemplateHelper;


public class ApplicationInfoPage extends PropertyPage {

	public static final String ITEM_LANG = "lang";
	public static final String ITEM_NAME = "name";

	public static final String[] ITEM_PROPS = { ITEM_LANG, ITEM_NAME };
	
	protected IProject fProject = null;
	protected boolean noContentOnPage = false;
	protected String fDefaultLocation;
	
	private Text fEntry;
	private TableViewer nameTableViewer;
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
	
	private Button buttonNew;
	private Button buttonDelete;
	
	private Button fCheckAutoScroll;
	private Combo fComboResolution;
	Label fLabelResolution;
	
	
	private AppXmlStore xmlManager = null;
	private java.util.List <AppNameItem> nameList = null;
	
	
	class NameTableContentProvider implements IStructuredContentProvider {
		  public Object[] getElements(Object inputElement) {
		    return ((List) inputElement).toArray();
		  }

		  public void dispose() {
		    // Do nothing
		  }

		  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		    // Ignore
		  }
	}
	
	class nameLabelProvider implements ITableLabelProvider {

		  public Image getColumnImage(Object element, int columnIndex) {
		    return null;
		  }

		  public String getColumnText(Object element, int columnIndex) {
			  AppNameItem p = (AppNameItem) element;
			  switch (columnIndex) {
			  	case 0:
			  		//return p.getLanugage();
			  		return LanguageListXmlStore.getInstance().findName(p.getLanugage());
			  	case 1:
			  		return p.getName();
			  }
			  return null;
		  }

		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}
	}
	
	
	class nameCellModifier implements ICellModifier {
		  private ApplicationInfoPage page;

		  public nameCellModifier(ApplicationInfoPage page) {
		    this.page = page;
		  }

		  public boolean canModify(Object element, String property) {
		    // Allow editing of all values
			  if( element instanceof AppNameItem)
			  {
				    if (ITEM_NAME.equals(property))
				    	return true;
			  }
			  
		    return false;
		  }

		  public Object getValue(Object element, String property) {
			  AppNameItem p = (AppNameItem) element;
			  
		    if (ITEM_LANG.equals(property))
		      //return p.getLanugage();
		    	return LanguageListXmlStore.getInstance().findName(p.getLanugage());
		    else if (ITEM_NAME.equals(property))
		    	return p.getName();
		    
		      return null;
		  }

		  public void modify(Object element, String property, Object value) {
		    if (element instanceof Item)
		      element = ((Item) element).getData();

		    AppNameItem p = (AppNameItem) element;
		    
		    if (ITEM_NAME.equals(property))
		    	p.setName(((String)value).trim());

		    // Force the viewer to refresh
		    page.languageNameChanged();
		  }
	}
	
	
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
			if( fProject == null )
				s = "Does not contained project information"; //$NON-NLS-1$
			else
			{
				if (!isBadaProject(fProject)) 
					s = "This project is not a bada project"; //$NON-NLS-1$
				else if( !hasAppXml(fProject) )
					s = "This project is not a bada executiable project"; //$NON-NLS-1$
			}
		}
		
	    if (s == null) {
	    	fDefaultLocation = fProject.getLocation().append(IConstants.DIR_ICON).toOSString();
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
	
	public boolean  hasAppXml(IProject p) {
		OspPropertyStore profStore = IdePlugin.getDefault().getOspPropertyStore(fProject);
		if( profStore.getProjectType() == IConstants.PRJ_TYPE_LIB_STATIC ||
			profStore.getProjectType() == IConstants.PRJ_TYPE_LIB_SHARED )
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	protected void createWidgets(Composite parent) {
		// TODO Auto-generated method stub
		createApplicationGroup(parent);
		
        loadData();
	}
	
	protected void createApplicationGroup(Composite parent) {
	
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setFont(parent.getFont());
        
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.verticalSpacing = 10;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        Label label = new Label(composite, SWT.NONE);
        label.setText(AppXmlStore.ATTR_ENTRY + IConstants.COLON);
        label.setLayoutData(new GridData());
        
        fEntry = new Text(composite, SWT.BORDER);
        fEntry.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fEntry.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				checkPage();
			}
		});       
        
        fnameGroup = new Group(composite, SWT.NONE);
        layout = new GridLayout(2, false);
        fnameGroup.setLayout(layout);
        
        fnameGroup.setText(AppXmlStore.ELEMENT_NAME);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.minimumHeight = 80;
	    gd.heightHint = 100;
	    gd.horizontalSpan = 2;
        fnameGroup.setLayoutData(gd);
        

        nameTableViewer = new TableViewer(fnameGroup, SWT.FULL_SELECTION| SWT.H_SCROLL | SWT.V_SCROLL| SWT.BORDER);
        nameTableViewer.setContentProvider(new NameTableContentProvider());
        nameTableViewer.setLabelProvider(new nameLabelProvider());

	    // Set up the table
	    Table table = nameTableViewer.getTable();
	    gd = new GridData(GridData.FILL_BOTH);
	    table.setLayoutData(gd);
	    
		TableLayout tablelayout = new TableLayout();
		tablelayout.addColumnData(new ColumnWeightData(45, 20, true));
		tablelayout.addColumnData(new ColumnWeightData(55, 20, true));
		table.setLayout(tablelayout);        
	    
		TableColumn columnLang = new TableColumn(table, SWT.NULL);
		columnLang.setText("lang");

		TableColumn columnName = new TableColumn(table, SWT.NULL);
		columnName.setText("name");

	    table.setHeaderVisible(false);
	    table.setLinesVisible(true);
	    
	    CellEditor[] editors = new CellEditor[2];
	    editors[0] = new TextCellEditor(table);
	    editors[1] = new TextCellEditor(table);	    
        
	    nameTableViewer.setColumnProperties(ITEM_PROPS);
	    nameTableViewer.setCellModifier(new nameCellModifier(this));
	    nameTableViewer.setCellEditors(editors);
	    
        
		Composite compButton = new Composite(fnameGroup, SWT.NONE);
		layout = new GridLayout(1, true);
		layout.marginHeight = 0;
		compButton.setLayout(layout);		
		compButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		
		buttonNew = new Button(compButton, SWT.PUSH);
		buttonNew.setText("New");
		buttonNew.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				newName();
			}
		});
		buttonNew.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        
		buttonDelete = new Button(compButton, SWT.PUSH);
		buttonDelete.setText("Delete");
		buttonDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				deleteName();
			}
		});
		buttonDelete.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));        
        
        
        label = new Label(composite, SWT.NONE);
        label.setText(AppXmlStore.ATTR_VENDOR + IConstants.COLON);
        label.setLayoutData(new GridData());
        
        fVendor = new Text(composite, SWT.BORDER);
        fVendor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));  		
        fVendor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
			}
		});   
        
    	label = new Label(composite, SWT.NONE);
    	label.setText(AppXmlStore.ATTR_DESC + IConstants.COLON);
    	label.setLayoutData(new GridData());
        
    	fDesc = new Text(composite, SWT.BORDER);
        fDesc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));        
        fDesc.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
			}
		});
        
        fIconGroup = new Group(composite, SWT.NONE);
        layout = new GridLayout(3, false);
        fIconGroup.setLayout(layout);
        
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        fIconGroup.setLayoutData(gd);
        fIconGroup.setText(AppXmlStore.ELEMENT_ICON);
        
        
        fIconMainMenu = createIconButtonGroup(fIconGroup, AppXmlStore.ATTR_MAINMENU + IConstants.COLON);
        fIconSetting = createIconButtonGroup(fIconGroup, AppXmlStore.ATTR_SETTING + IConstants.COLON);
        fIconTicker = createIconButtonGroup(fIconGroup, AppXmlStore.ATTR_TICKER + IConstants.COLON);
        fIconQuickPanel = createIconButtonGroup(fIconGroup, AppXmlStore.ATTR_QUICKPANEL + IConstants.COLON);
        fIconLaunchImage = createIconButtonGroup(fIconGroup, AppXmlStore.ATTR_LAUNCHIMAGE + IConstants.COLON);
        
        
    	fAutoScalingGroup = new Group(composite, SWT.NONE);
        layout = new GridLayout(1, false);
        fAutoScalingGroup.setLayout(layout);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        fAutoScalingGroup.setLayoutData(gd);

        
        fCheckAutoScroll= new Button(fAutoScalingGroup, SWT.CHECK);
        fCheckAutoScroll.setText("Auto-scaling");
        fCheckAutoScroll.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        fCheckAutoScroll.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				handleAutoScrollButtonChanged();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

    	
        Composite compResolution = new Composite(fAutoScalingGroup, SWT.NULL);
        layout = new GridLayout(2, false);
        compResolution.setLayout(layout);
        compResolution.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        fLabelResolution = new Label(compResolution, SWT.NONE);
        fLabelResolution.setFont(parent.getFont());
        fLabelResolution.setLayoutData(new GridData());
        fLabelResolution.setText("Base Resolution:");        
    	
        
        fComboResolution = new Combo(compResolution, SWT.DROP_DOWN|SWT.READ_ONLY);
        gd = new GridData();
        gd.widthHint = 100;
        fComboResolution.setLayoutData(gd);
        fComboResolution.add(IConstants.STR_SCREEN_WVGA);
        fComboResolution.add(IConstants.STR_SCREEN_WQVGA);
	}
	
	protected void loadData() {
		if( fProject == null ) return;
		
		IFile file = fProject.getFile(new Path(IConstants.APP_XML_FILE));
		if( !file.exists() ) return;
		
		xmlManager = new AppXmlStore(file);
		xmlManager.loadXML();
		
		fEntry.setText(xmlManager.getEntry());
		nameList = xmlManager.getNameListClone();
		
		fIconMainMenu.setText(xmlManager.getIconMainMenu());
		fIconSetting.setText(xmlManager.getIconSetting());
		fIconTicker.setText(xmlManager.getIconTicker());
		fIconQuickPanel.setText(xmlManager.getIconQuickPanel());
		fIconLaunchImage.setText(xmlManager.getIconLaunchImage());
		
		fVendor.setText(xmlManager.getVendor());
		fDesc.setText(xmlManager.getDescription());

	    nameTableViewer.setInput(nameList);	    
		refreshNameViewer();
		
		
		boolean bAutoScroll = xmlManager.getAutoScalingEnabled();
		fCheckAutoScroll.setSelection(bAutoScroll);
		
		if( bAutoScroll ) fComboResolution.setText(xmlManager.getBaseResolution());
		else fComboResolution.select(0);	
		
		handleAutoScrollButtonChanged();
		
		checkPage();
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
		
		storeProperty();
		return true;
	}
	
	protected void performDefaults() {
		// TODO Auto-generated method stub
		super.performDefaults();

		if(fProject == null) return;
		
		nameList.clear();
		
		String name = fProject.getName();
		if( name != null && name.length() > 0 )
		{
			fEntry.setText(name);
			
			String transname = name.substring(0, 1).toUpperCase(Locale.getDefault()) + name.substring(1);

			ArrayList<LanguageData> list = LanguageListXmlStore.getInstance().getDefaultLanguages();  
			Iterator<LanguageData> itList = list.iterator();
			while (itList.hasNext()) {
				LanguageData item = itList.next();
				nameList.add(new AppNameItem(item.getId(), transname));
			}
		}
		else
		{
			fEntry.setText("");
		}
		
		fVendor.setText("");
		fDesc.setText("");

		fIconMainMenu.setText(OSPCCProjectHelper.getDefaultMainMenuIconName(fProject));
		fIconSetting.setText("");
		fIconTicker.setText("");
		fIconQuickPanel.setText("");
		fIconLaunchImage.setText(IConstants.TEMPLATE_SPLASH_ICON_NAME);
		
		refreshNameViewer();
		
		
		fCheckAutoScroll.setSelection(false);
		
		String model = IdePlugin.getDefault().getModel(fProject);
		if( model != null && model.startsWith("WaveWQ_") )
		{
			fComboResolution.select(1);
		}
		else
		{
			fComboResolution.select(0);
		}

		handleAutoScrollButtonChanged();
		
		checkPage();
	}	
	
	protected void refreshNameViewer()
	{
		nameTableViewer.refresh(true);
	}	
	
	protected void storeProperty() {
		// TODO Auto-generated method stub
		
		if(fProject == null || xmlManager == null ) return;
		
		xmlManager.setEntry(fEntry.getText());
		xmlManager.setNameList(nameList);
		
		xmlManager.setIconMainMenu(fIconMainMenu.getText().trim());
		xmlManager.setIconSetting(fIconSetting.getText().trim());
		xmlManager.setIconTicker(fIconTicker.getText().trim());
		xmlManager.setIconQuickPanel(fIconQuickPanel.getText().trim());
		xmlManager.setIconLaunchImage(fIconLaunchImage.getText().trim());
		
		xmlManager.setVendor(fVendor.getText());
		xmlManager.setDescription(fDesc.getText());
		
		
		boolean bAutoScroll = fCheckAutoScroll.getSelection(); 
		xmlManager.setAutoScalingEnabled(bAutoScroll);
		
		if( bAutoScroll ) xmlManager.setBaseResolution(fComboResolution.getText());
		else xmlManager.setBaseResolution("");;	
		
		xmlManager.storeXML();
	}
	
	
	protected void checkPage()
	{
		setValid(validatePage());
	}
	
    protected boolean validatePage() {
		setErrorMessage(null);

		if( fEntry.getText().trim().length() <= 0)
		{
			setErrorMessage("Entry field empty.");
			return false;
		}
		
		if( nameList == null || nameList.size() <= 0)
		{
			setErrorMessage("Name List field empty.");
			return false;
		}
		
		Iterator<AppNameItem> itName = nameList.iterator();
		while (itName.hasNext()) {
			AppNameItem item = itName.next();
			if( item.getName().length() <= 0 )
			{
				setErrorMessage("Name List " + item.getLanugage() + " field value empty.");
				return false;
			}
		}
/*
		String path = fSdkPath.getText().trim(); 
		if( path.length() <= 0)
		{
			setErrorMessage("SDK Root field empty.");
			return false;
		}
		
		if( new File(path).exists() == false )
		{
			setErrorMessage("Invalid SDK Root path.");
			return false;
			
		}
*/		
		return true;
    }
    
    
	protected Text createIconButtonGroup(Composite comp, String labelText)
	{
		Label label = new Label(comp, SWT.NONE);
    	label.setText(labelText);
    	label.setLayoutData(new GridData());
        
    	Text text = new Text(comp, SWT.BORDER);
    	text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
			}
		});        

		setupFileSystemButton(comp, text);
		
		return text;
	}
	
	protected Button setupFileSystemButton(Composite c, Text text) {
		Button b = new Button(c, SWT.PUSH);
		b.setText("File system");
		GridData fd = new GridData(GridData.CENTER);
		fd.minimumWidth = 120; 
		b.setLayoutData(fd);
		b.setData(text);
		b.addSelectionListener(new SelectionAdapter() {
	        public void widgetSelected(SelectionEvent event) {
	        	fileSystemButtonPressed(event);
	        }});
		return b;
	}
	
	protected void fileSystemButtonPressed(SelectionEvent e) {
		Widget b = e.widget;
		if (b == null || b.getData() == null) return; 
		if (b.getData() instanceof Text) {
			String selectFile = getFileFromFileDialog(getShell(), fDefaultLocation, new String[] {"*.png", "*.*"});
			if (selectFile != null)
			{
				File f = new File(selectFile);
				String newPath = f.getParent();
				if( newPath != null && newPath.equals(fDefaultLocation) )
				{
					((Text)b.getData()).setText(f.getName());	
				}
				else
				{
					String distName = IConstants.DIR_ICON + IConstants.FILE_SEP_FSLASH + f.getName();
					
					IFile ifile = fProject.getFile(distName);
					if( ifile.exists())
					{
	                	if (! MessageDialog.openConfirm(getShell(), 
	                    				"Overwrite files?",
	                    				distName + " file exists. Do you want to overwrite?") //$NON-NLS-1$
	            			)
	                		return;
					}
					
					TemplateHelper.copyFileToProject(fProject, selectFile, distName, true);
					((Text)b.getData()).setText(f.getName());
				}
			}
		}
	}	
	
	protected String getFileFromFileDialog(Shell shell, String startingDirectory, String[] extensions ) {
	    FileDialog dialog = new FileDialog(shell, SWT.OPEN);
	    if (startingDirectory != null && startingDirectory.length() > 0) {
			dialog.setFilterPath(startingDirectory);
		}
	    if (extensions != null) {
			dialog.setFilterExtensions(extensions);
		}
	    return dialog.open();
	}
	
	protected void newName()
	{
		SelectLanguageDialog dialog = new SelectLanguageDialog(getShell(), nameList);
		
		if (dialog.open() == Window.OK) {
			AppNameItem item = new AppNameItem(dialog.getSelectedId(), dialog.getName());
			nameList.add(item);
			refreshNameViewer();
			checkPage();			
		}
	}
	
	protected void deleteName()
	{
		IStructuredSelection selection = (IStructuredSelection) nameTableViewer.getSelection();
		if( selection != null )
		{
			Object obj = selection.getFirstElement();
			if( (obj != null) && (obj instanceof AppNameItem) )
			{
				if( ((AppNameItem)obj).getLanugage().equals(LanguageListXmlStore.LANG_VALUE_ENGLISH) )
					return;
				
				int index = nameList.indexOf(obj);
				nameList.remove(obj);
    	  
				refreshNameViewer();
    	  
				if(!nameList.isEmpty())
				{
					if( index >= nameList.size()) index--;
					nameTableViewer.getTable().select(index);
				}
			}
		}
	
		checkPage();
	}
	
	public void languageNameChanged()
	{
		refreshNameViewer();
		checkPage();
	}
	
	protected void handleAutoScrollButtonChanged()
	{
		boolean enabled = fCheckAutoScroll.getSelection();
		
		fComboResolution.setEnabled(enabled);
		fLabelResolution.setEnabled(enabled);
	}
	
}

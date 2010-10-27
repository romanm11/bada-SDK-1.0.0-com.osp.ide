package com.osp.ide.internal.ui.wizards.project;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;
import com.osp.ide.core.ManifestXmlStore;
import com.osp.ide.wizards.NewElementWizardPage;
import com.osp.ide.wizards.StatusInfo;

public class DeviceConfigPage extends NewElementWizardPage implements ISDKPathChangeListener {

	Composite mainComposite;
	Text manifestText;
	ExplorerComp explorerComp;
	Button createButton;
	Button checkDefault;
	Button browseButton;
	
	private StatusInfo deviceStatus;
	boolean fShowBrowser=false;
	
	String defaultManifestPath=null;
	
	Rectangle oldBound = null;
	Point compSize = null;
	
	public DeviceConfigPage() {
		super("DeviceConfigPage");
		// TODO Auto-generated constructor stub
		
		setTitle("Device Configurations");
		setDescription("Select the manifest file with the correct device configurations.");
		setImageDescriptor(IdePlugin.getDefault().createImageDescriptor(IConstants.IMG_WIZARD));
		
		deviceStatus = new StatusInfo();
	}	
	
	
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setFont(parent.getFont());
        
        initializeDialogUnits(parent);
        
        GridLayout layout = new GridLayout(1, false);
        mainComposite.setLayout(layout);
        mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        
        checkDefault = new Button(mainComposite, SWT.CHECK);
        checkDefault.setText("Default manifest file");
        checkDefault.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        checkDefault.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				
				defaultButtonChanged();
				
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
        
        
        
        Composite compManifest = new Composite(mainComposite, SWT.NULL);
        compManifest.setFont(mainComposite.getFont());
        layout = new GridLayout(3, false);
        compManifest.setLayout(layout);
        compManifest.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        

        Label m_label = new Label(compManifest, SWT.NONE);
        m_label.setText("Manifest file:");
        m_label.setLayoutData(new GridData());
        
        manifestText = new Text(compManifest, SWT.BORDER);
        manifestText.setLayoutData(new GridData());
        manifestText.setLayoutData(new GridData(GridData.FILL_BOTH));

/*        
        String path = IdePlugin.getDefault().getManifestPath();
		if( path != null && path.length() > 0 )
			manifestText.setText(path);
*/        
        
        manifestText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				valueChanged();
			}
		});  
        
		browseButton = new Button(compManifest, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.setFont(mainComposite.getFont());
		browseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                String newValue = changePressed();
                if (newValue != null) {
                    String oldValue = manifestText.getText();
                    if (!oldValue.equals(newValue)) {
                    	manifestText.setText(newValue);
                        valueChanged();
                    }
                }
            }
        });
        
		createButton = new Button(mainComposite, SWT.CHECK);
		createButton.setText("Create a new manifest file");
		createButton.setFont(mainComposite.getFont());
		createButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
            	showBrowserButtonChanged();
            }
        });		
        
        explorerComp = new ExplorerComp(mainComposite, SWT.BORDER); 
        explorerComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        explorerComp.setUrl(IdePlugin.getDefault().getDeveloperSite());
        
        setControl(mainComposite);

        
        initalize();
        
        if( getDataStore() != null ) getDataStore().addSdkPathChangeListener(this);
        
        valueChanged();
	}
	
	private void defaultButtonChanged()
	{
		boolean selection = checkDefault.getSelection();
		if( selection )
		{
			manifestText.setEnabled(false);
			createButton.setEnabled(false);
			browseButton.setEnabled(false);
			if( defaultManifestPath != null)
			{
				if( defaultManifestPath.endsWith(java.io.File.separator) )  
					manifestText.setText(defaultManifestPath + "Samples" + java.io.File.separatorChar + "Manifest.xml");
				else
					manifestText.setText(defaultManifestPath + java.io.File.separatorChar + "Samples" + java.io.File.separatorChar + "Manifest.xml");
			}
			createButton.setSelection(false);
			showBrowserButtonChanged();
		}
		else
		{
			manifestText.setEnabled(true);
			createButton.setEnabled(true);
			browseButton.setEnabled(true);			
		}
		
	}
	
	private void changedSdkPath()
	{
		OSPCreateDataStore store = getDataStore();
		if( store != null )
			defaultManifestPath = store.getSdkPath();
		else
			defaultManifestPath = null;
		
		if( defaultManifestPath == null || defaultManifestPath.length() == 0)
		{
			checkDefault.setEnabled(false);
		}
		else
		{
			checkDefault.setEnabled(true);
			checkDefault.setSelection(true);			
		}
		
		defaultButtonChanged();
	}
	
	private void initalize()
	{
		changedSdkPath();
		
		//fShowBrowser = IdePlugin.getDefault().getShowBrowser();
		fShowBrowser = false;
        createButton.setSelection(fShowBrowser);
        if( fShowBrowser )
        {
        	showBrowserButtonChanged();
        }
        else
        {
        	explorerComp.showBrowser(fShowBrowser);	
        }		
	}
	
	private void restoreDialogSize()
	{
		if( oldBound != null)
		{
			Shell shell = getShell();
			shell.setBounds(oldBound);
			if( compSize != null )
			{
				mainComposite.setSize(compSize.x, compSize.y);
				mainComposite.getParent().redraw();
				compSize = null;
			}
			//centerDialog(shell);
			oldBound = null;
		}		
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if( visible )
		{
			updateStatus(new IStatus[] { deviceStatus });	
		}
		else
		{
			fShowBrowser = false;
			createButton.setSelection(fShowBrowser);
			explorerComp.showBrowser(fShowBrowser);

			restoreDialogSize();
		}
	}
	
    protected String changePressed() {
        File f = new File(manifestText.getText());
        if (!f.exists()) {
			f = null;
		}
        File d = getFile(f);
        if (d == null) {
			return null;
		}

        return d.getAbsolutePath();
    }
    
    private File getFile(File startingDirectory) {

        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
        if (startingDirectory != null) {
			dialog.setFileName(startingDirectory.getPath());
		}
        String[] extensions = new String[] {"*.xml", "*.*"};
        if (extensions != null) {
			dialog.setFilterExtensions(extensions);
		}
        String file = dialog.open();
        if (file != null) {
            file = file.trim();
            if (file.length() > 0) {
				return new File(file);
			}
        }

        return null;
    }
    
    private void valueChanged()
    {
		String text = manifestText.getText();
		OSPCreateDataStore store = getDataStore();
		
		if( text.length() == 0 )
		{
			deviceStatus.setError("Manifest file Name field is empty.");
			if( store != null) store.setManifestFile("");
			
		}
		else
		{
	        File f = new File(text);
	        if (f.exists()) {
	        	ManifestXmlStore reader = new ManifestXmlStore(text);
	        	
	        	if( reader.isValid())
	        	{
	        		if( store != null)
	        		{
	        			store.setScreenSize(reader.getScreenSizes());
//	        			store.setTouchScreen(reader.isTouchSupport());
//	        			store.setSdkId(reader.getFramework());
	        			store.setManifestFile(text);
	        		}
	        		
	        		deviceStatus.setOK();

	        	}
	        	else
	        	{
		        	deviceStatus.setError("Invalid Manifest File.");
		        	if( store != null) store.setManifestFile("");	        		
	        	}
			}
	        else
	        {
	        	deviceStatus.setError("Manifest File does not exist.");
	        	if( store != null) store.setManifestFile("");
	        }
			
		}
		updateStatus(new IStatus[] { deviceStatus });
    }
    
    private void showBrowserButtonChanged()
    {
    	// Show Borswer
    	fShowBrowser = createButton.getSelection();
    	explorerComp.showBrowser(fShowBrowser);
    	
//    	IdePlugin.getDefault().setShowBrowser(fShowBrowser);
    	
    	// Change Size
    	Shell shell = getShell();
    	
    	if(fShowBrowser)
    	{
    		oldBound = shell.getBounds();
    		compSize = mainComposite.getSize();
    		
    		Dimension screenSize
    			= Toolkit.getDefaultToolkit().getScreenSize();
    		
        	Point p = new Point(screenSize.width,screenSize.height);
        	if( p.x > 1280 ) p.x = 1280;
        	if( p.x < 1280-50) p.x -= 50;
        	
        	if( p.y > 900 ) p.y = 900;
        	if( p.y < 900-50) p.y -= 50;

    		shell.setSize(p.x, p.y);
    		
    		int deltaX =  (p.x * compSize.x) / oldBound.width;
    		int deltaY = mainComposite.getSize().y;
    		
    		mainComposite.setSize(deltaX, deltaY);
    		
    		centerDialog(shell);
   		}
    	else
    	{
    		restoreDialogSize();
//    		shell.setSize(IConstants.WIZARD_PAGE_WIDTH, IConstants.WIZARD_PAGE_HEIGHT);
    	}

    }
    
    private void centerDialog(Shell shell)
    {
    	if( shell.getParent() != null && shell.getParent().getShell() != null)
    	{
    		Rectangle parentSize = shell.getParent().getShell().getBounds();
    		Rectangle mySize = shell.getBounds();


    		int locationX, locationY;
    		locationX = (parentSize.width - mySize.width)/2+parentSize.x;
    		locationY = (parentSize.height - mySize.height)/2+parentSize.y;


    		shell.setLocation(new Point(locationX, locationY));
    	}
    	
    }


	public void sdkPathChanged(String skdPath) {
		// TODO Auto-generated method stub
//		defaultManifestPath = skdPath;
		changedSdkPath();
	}


	@Override
	public void modelChanged(String model) {
		// TODO Auto-generated method stub
		
	}
}


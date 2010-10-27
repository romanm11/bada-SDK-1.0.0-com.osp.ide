package com.osp.ide.internal.ui;

//import org.eclipse.cdt.internal.ui.wizards.CWizardRegistry;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.preferences.IDebugPreferenceConstants;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.navigator.resources.ProjectExplorer;

import com.osp.ide.IConstants;
import com.osp.ide.wizards.OspWizardRegistry;

public class OSPPerspectiveFactory implements IPerspectiveFactory {

	static final String ID_OSP_ELEMENT_CREATION_ACTION_SET = "com.osp.ide.CElementCreationActionSet"; 
	public OSPPerspectiveFactory() {
		super();
	}
	
	public void createInitialLayout(IPageLayout layout) {
		// TODO Auto-generated method stub
 		String editorArea = layout.getEditorArea();
		
		IFolderLayout folder1= layout.createFolder("topLeft", IPageLayout.LEFT, (float)0.25, editorArea); //$NON-NLS-1$
		folder1.addView(ProjectExplorer.VIEW_ID);
		folder1.addPlaceholder(CUIPlugin.CVIEW_ID);
		folder1.addPlaceholder(IPageLayout.ID_RES_NAV);
		folder1.addPlaceholder(IPageLayout.ID_BOOKMARKS);
		
		
		//layout.addView(IPageLayout.ID_TASK_LIST, IPageLayout.BOTTOM, (float)0.50, ProjectExplorer.VIEW_ID);
		
		IFolderLayout folder2= layout.createFolder("bottomRight", IPageLayout.BOTTOM, (float)0.75, editorArea); //$NON-NLS-1$
		folder2.addView(IPageLayout.ID_PROBLEM_VIEW);
		folder2.addView(IPageLayout.ID_TASK_LIST);
		folder2.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		folder2.addView(IPageLayout.ID_PROP_SHEET);
		folder2.addView(IConstants.VIEW_ID_OUTPUT);
		//folder2.addView(IConstants.VIEW_ID_FILE_EXPLORER);

		//folder2.addView(VIEW_REMOTE_EXPLORER);
		//folder2.addView(VIEW_EVENT);
		
		IFolderLayout folder3= layout.createFolder("topRight", IPageLayout.RIGHT,(float)0.75, editorArea); //$NON-NLS-1$
		folder3.addView(IConstants.VIEW_ID_SAMPLE_BROWER);
		folder3.addView(IPageLayout.ID_OUTLINE);
		
		IFolderLayout folder4= layout.createFolder("bottomLeft", IPageLayout.BOTTOM, (float)0.55, "topLeft"); //$NON-NLS-1$
		folder4.addView(IConstants.VIEW_ID_RESOURCE_EXPLORER);

		layout.addActionSet(CUIPlugin.SEARCH_ACTION_SET_ID);
		//layout.addActionSet(CUIPlugin.ID_CELEMENT_CREATION_ACTION_SET);
		layout.addActionSet(ID_OSP_ELEMENT_CREATION_ACTION_SET);
		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
		
		// views - build console
		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		
		// views - searching
		layout.addShowViewShortcut(NewSearchUI.SEARCH_VIEW_ID);
		
		// views - standard workbench
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(CUIPlugin.CVIEW_ID);
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);

		addOspWizardShortcuts(layout);
		
		initializeDefaultPreferences();
		setEditorColor();
	}
/*	
	private void addCWizardShortcuts(IPageLayout layout) {
		// new actions - C project creation wizard
		String[] wizIDs = CWizardRegistry.getProjectWizardIDs();
		for (int i = 0; i < wizIDs.length; ++i) {
			layout.addNewWizardShortcut(wizIDs[i]);
		}
		
		// new actions - C folder creation wizard
		wizIDs = CWizardRegistry.getFolderWizardIDs();
		for (int i = 0; i < wizIDs.length; ++i) {
			layout.addNewWizardShortcut(wizIDs[i]);
		}
		// new actions - C file creation wizard
		wizIDs = CWizardRegistry.getFileWizardIDs();
		for (int i = 0; i < wizIDs.length; ++i) {
			layout.addNewWizardShortcut(wizIDs[i]);
		}
		// new actions - C type creation wizard
		wizIDs = CWizardRegistry.getTypeWizardIDs();
		for (int i = 0; i < wizIDs.length; ++i) {
			layout.addNewWizardShortcut(wizIDs[i]);
		}
	}
*/	
		
	private void addOspWizardShortcuts(IPageLayout layout) {
		// new actions - bada project creation wizard
		String[] wizIDs = OspWizardRegistry.getProjectWizardIDs();
		for (int i = 0; i < wizIDs.length; ++i) {
			layout.addNewWizardShortcut(wizIDs[i]);
		}
		
		// new actions - bada folder creation wizard
		wizIDs = OspWizardRegistry.getFolderWizardIDs();
		for (int i = 0; i < wizIDs.length; ++i) {
			layout.addNewWizardShortcut(wizIDs[i]);
		}
		// new actions - bada file creation wizard
		wizIDs = OspWizardRegistry.getFileWizardIDs();
		for (int i = 0; i < wizIDs.length; ++i) {
			layout.addNewWizardShortcut(wizIDs[i]);
		}
		// new actions - bada type creation wizard
		wizIDs = OspWizardRegistry.getTypeWizardIDs();
		for (int i = 0; i < wizIDs.length; ++i) {
			layout.addNewWizardShortcut(wizIDs[i]);
		}
	}
	
	private void initializeDefaultPreferences()
	{
		IPreferenceStore store = DebugUIPlugin.getDefault().getPreferenceStore();
		
		if( store != null )
		{
			store.setValue(IDebugPreferenceConstants.CONSOLE_LIMIT_CONSOLE_OUTPUT, false);
			
			store.setValue(IDebugPreferenceConstants.CONSOLE_OPEN_ON_ERR, false);
			store.setValue(IDebugPreferenceConstants.CONSOLE_OPEN_ON_OUT, false);
			
			//store.setValue(IDebugUIConstants.PREF_BUILD_BEFORE_LAUNCH, false);
			
			//store.setValue(ResourcesPlugin.PREF_ENCODING, "UTF-8");
			//ResourcesPlugin.getPlugin().getPluginPreferences().setValue(ResourcesPlugin.PREF_ENCODING, "UTF-8");
		}
		
		try {
			enableAutoBuild( false );
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private boolean enableAutoBuild(boolean enable) throws  CoreException {
        IWorkspace workspace= ResourcesPlugin.getWorkspace();
        if( workspace == null ) return false;
        
        IWorkspaceDescription desc= workspace.getDescription();
        boolean isAutoBuilding= desc.isAutoBuilding();
        if (isAutoBuilding != enable) {
            desc.setAutoBuilding(enable);
            workspace.setDescription(desc);
        }
        return true;
    }

	private void setEditorColor()
	{
		IPreferenceStore store = CUIPlugin.getDefault().getPreferenceStore();
		store.setValue("semanticHighlighting.class.color", "64,0,0");
		store.setValue("semanticHighlighting.typedef.color", "64,0,0");
	}	
}

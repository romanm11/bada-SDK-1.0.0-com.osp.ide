package com.osp.ide.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.internal.ui.wizards.OpenNewWizardAction;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.PlatformUI;

import com.osp.ide.IConstants;

public class OspWizardRegistry {

	private final static String PL_NEW = "newWizards"; //$NON-NLS-1$
	private final static String TAG_WIZARD = "wizard"; //$NON-NLS-1$
	private final static String ATT_CATEGORY = "category";//$NON-NLS-1$
	private final static String ATT_PROJECT = "project";//$NON-NLS-1$
	private final static String TAG_ID = "id"; //$NON-NLS-1$
	private final static String TAG_CLASS = "class"; //$NON-NLS-1$
	private final static String TAG_PARAMETER = "parameter";//$NON-NLS-1$
	private final static String TAG_NAME = "name";//$NON-NLS-1$
	private final static String TAG_VALUE = "value";//$NON-NLS-1$	
	private final static String ATT_OSPFOLDER = "ospfolder";//$NON-NLS-1$
	private final static String ATT_OSPFILE = "ospfile";//$NON-NLS-1$
	private final static String ATT_OSPTYPE = "osptype";//$NON-NLS-1$
	
	//IConstants.ID_WIZARD_OSP_PROJECT	
	
    public static IAction[] getProjectWizardActions() {
	    return createActions(getProjectWizardElements());
    }	

	public static IAction[] getFolderWizardActions() {
		// TODO Auto-generated method stub
		return createActions(getFolderWizardElements());
	}    
	
	public static IAction[] getFileWizardActions() {
		// TODO Auto-generated method stub
		return createActions(getFileWizardElements());
	}	

	public static IAction[] getTypeWizardActions() {
		// TODO Auto-generated method stub
		return createActions(getTypeWizardElements());
	}	
	
	public static String[] getProjectWizardIDs() {
		return getWizardIDs(getProjectWizardElements());
	}
	
	public static String[] getFolderWizardIDs() {
		return getWizardIDs(getFolderWizardElements());
	}

	public static String[] getFileWizardIDs() {
		return getWizardIDs(getFileWizardElements());
	}

	public static String[] getTypeWizardIDs() {
		return getWizardIDs(getTypeWizardElements());
	}	
    
    private static boolean isProjectWizard(IConfigurationElement element) {
	    String project = element.getAttribute(ATT_PROJECT);
	    if (project != null) {
	        return Boolean.valueOf(project).booleanValue();
	    }
		// fall back, if no <class> element found then assume it's a project wizard
		return false;
    }
    
	public static IConfigurationElement[] getProjectWizardElements() {
		List<IConfigurationElement> elemList = new ArrayList<IConfigurationElement>();
	    IConfigurationElement[] elements = getAllWizardElements();
	    for (int i = 0; i < elements.length; ++i) {
			IConfigurationElement element = elements[i];
			if (isProjectWizard(element)) {
			    elemList.add(element);
            }
	    }
		return elemList.toArray(new IConfigurationElement[elemList.size()]);
	}
    
    
	public static IConfigurationElement[] getAllWizardElements() {
		List<IConfigurationElement> elemList = new ArrayList<IConfigurationElement>();
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(PlatformUI.PLUGIN_ID, PL_NEW);
		if (extensionPoint != null) {
			IConfigurationElement[] elements = extensionPoint.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(TAG_WIZARD)) {
				    String category = element.getAttribute(ATT_CATEGORY);
				    if (category != null &&
				        (category.equals(IConstants.ID_OSP_PROJECT_CATEGORY))) {
			            elemList.add(element);
				    }
				}
			}
		}
		return elemList.toArray(new IConfigurationElement[elemList.size()]);
	}

    private static IAction[] createActions(IConfigurationElement[] elements) {
	    List<String> idList = new ArrayList<String>();
	    List<IAction> actionList = new ArrayList<IAction>();

	    // add C wizards first
	    for (int i = 0; i < elements.length; ++i) {
			IConfigurationElement element = elements[i];

			String id = element.getAttribute(TAG_ID);
	        if (id != null && !idList.contains(id)) {
	           	idList.add(id);
	    	    IAction action = new OpenNewWizardAction(element);
	    	    actionList.add(action);
	        }

	    }
	    
		return actionList.toArray(new IAction[actionList.size()]);
    }

	public static IConfigurationElement[] getFolderWizardElements() {
		List<IConfigurationElement> elemList = new ArrayList<IConfigurationElement>();
	    IConfigurationElement[] elements = getAllWizardElements();
	    for (int i = 0; i < elements.length; ++i) {
			IConfigurationElement element = elements[i];
			if (isFolderWizard(element)) {
			    elemList.add(element);
            }
	    }
		return elemList.toArray(new IConfigurationElement[elemList.size()]);
	}

    private static boolean isFolderWizard(IConfigurationElement element) {
		IConfigurationElement[] classElements = element.getChildren(TAG_CLASS);
		if (classElements.length > 0) {
			for (IConfigurationElement classElement : classElements) {
				IConfigurationElement[] paramElements = classElement.getChildren(TAG_PARAMETER);
				for (IConfigurationElement curr : paramElements) {
					String name = curr.getAttribute(TAG_NAME);
					if (name != null && name.equals(ATT_OSPFOLDER)) {
					    String value = curr.getAttribute(TAG_VALUE);
					    if (value != null)
					        return Boolean.valueOf(value).booleanValue();
					}
				}
			}
		}
		return false;
    }

	public static IConfigurationElement[] getFileWizardElements() {
		List<IConfigurationElement> elemList = new ArrayList<IConfigurationElement>();
	    IConfigurationElement[] elements = getAllWizardElements();
	    for (int i = 0; i < elements.length; ++i) {
			IConfigurationElement element = elements[i];
			if (isFileWizard(element)) {
			    elemList.add(element);
            }
	    }
		return elemList.toArray(new IConfigurationElement[elemList.size()]);
	}

    private static boolean isFileWizard(IConfigurationElement element) {
		IConfigurationElement[] classElements = element.getChildren(TAG_CLASS);
		if (classElements.length > 0) {
			for (IConfigurationElement classElement : classElements) {
				IConfigurationElement[] paramElements = classElement.getChildren(TAG_PARAMETER);
				for (IConfigurationElement curr : paramElements) {
					String name = curr.getAttribute(TAG_NAME);
					if (name != null && name.equals(ATT_OSPFILE)) {
					    String value = curr.getAttribute(TAG_VALUE);
					    if (value != null)
					        return Boolean.valueOf(value).booleanValue();
					}
				}
			}
		}
		return false;
    }
    
	public static IConfigurationElement[] getTypeWizardElements() {
		List<IConfigurationElement> elemList = new ArrayList<IConfigurationElement>();
	    IConfigurationElement[] elements = getAllWizardElements();
	    for (int i = 0; i < elements.length; ++i) {
			IConfigurationElement element = elements[i];
			if (isTypeWizard(element)) {
			    elemList.add(element);
            }
	    }
		return elemList.toArray(new IConfigurationElement[elemList.size()]);
	}
	
    private static boolean isTypeWizard(IConfigurationElement element) {
		IConfigurationElement[] classElements = element.getChildren(TAG_CLASS);
		if (classElements.length > 0) {
			for (IConfigurationElement classElement : classElements) {
				IConfigurationElement[] paramElements = classElement.getChildren(TAG_PARAMETER);
				for (IConfigurationElement curr : paramElements) {
					String name = curr.getAttribute(TAG_NAME);
					if (name != null && name.equals(ATT_OSPTYPE)) {
					    String value = curr.getAttribute(TAG_VALUE);
					    if (value != null)
					        return Boolean.valueOf(value).booleanValue();
					}
				}
			}
		}
		return false;
    }
    
	private static String[] getWizardIDs(IConfigurationElement[] elements) {
	    List<String> idList = new ArrayList<String>();

	    // add C wizards first
	    for (int i = 0; i < elements.length; ++i) {
			IConfigurationElement element= elements[i];
			if (isOspProjectWizard(element)) {
	            String id = element.getAttribute(TAG_ID);
	            if (id != null && !idList.contains(id)) {
	            	idList.add(id);
	            }
			}
	    }
	    
		return idList.toArray(new String[idList.size()]);
	}

	
	public static boolean isOspProjectWizard(IConfigurationElement element) {
	    String category = element.getAttribute(ATT_CATEGORY);
	    return (category != null && category.equals(IConstants.ID_OSP_PROJECT_CATEGORY));
	}    
}

package com.osp.ide.internal.ui.wizards.project;

import org.eclipse.core.internal.resources.ResourceStatus;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class ProjectNameValidator {

	private final static String ILLEGAL_FILE_CHARS = "/\\:<>?*|\"!@#$%^&(){}[];'~`+=-"; //$NON-NLS-1$
	
	static protected boolean isLegalFilename(String name) {
		if (name == null || name.length() == 0) {
			return false;
		}

		//TODO we need platform-independent validation, see bug#24152
		
		int len = name.length();
		for (int i = 0; i < len; i++) {
			char c = name.charAt(i);
			if (ILLEGAL_FILE_CHARS.indexOf(c) != -1) {
				return false;
			}
		}
		return true;
	}	
	
	static public IStatus validateName(String name)
	{
		if (name == null || name.length() == 0) {
			return new ResourceStatus(IResourceStatus.INVALID_VALUE, null, "Empty project name.");
		}		
		
		if (!isLegalFilename(name)) {
			return new ResourceStatus(IResourceStatus.INVALID_VALUE, null, "Project name contains non-standard or illegal characters.");
		}		
		
		String trimmed = name.trim();
		if ((!name.equals(trimmed)) || (name.indexOf(" ") != -1)) { //$NON-NLS-1$
			return new ResourceStatus(IResourceStatus.INVALID_VALUE, null,"Project name contains spaces.");
		}
		
		char fc = name.charAt(0);
		if( Character.isDigit(fc) )	{
			return new ResourceStatus(IResourceStatus.INVALID_VALUE, null,"The first character must be letter.");
		}
		
		return Status.OK_STATUS;
	}
}

/*******************************************************************************
 * Copyright (c) 2005, 2007 Intel Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intel Corporation - Initial API and implementation
 *******************************************************************************/
package com.osp.ide.core.builder;

import java.util.Locale;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.envvar.IBuildEnvironmentVariable;
import org.eclipse.cdt.managedbuilder.envvar.IConfigurationEnvironmentVariableSupplier;
import org.eclipse.cdt.managedbuilder.envvar.IEnvironmentVariableProvider;
import org.eclipse.cdt.managedbuilder.gnu.cygwin.CygwinPathResolver;
import org.eclipse.cdt.managedbuilder.internal.envvar.BuildEnvVar;
import org.eclipse.core.resources.IProject;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;


public class TargetConfigurationEnvironmentSupplier implements
		IConfigurationEnvironmentVariableSupplier {

	static final String VARNAME = "PATH";        //$NON-NLS-1$
	static final String DELIMITER_UNIX = ":";    //$NON-NLS-1$
	static final String PROPERTY_DELIMITER = "path.separator"; //$NON-NLS-1$
	static final String PROPERTY_OSNAME    = "os.name"; //$NON-NLS-1$
	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.envvar.IConfigurationEnvironmentVariableSupplier#getVariable(java.lang.String, org.eclipse.cdt.managedbuilder.core.IConfiguration, org.eclipse.cdt.managedbuilder.envvar.IEnvironmentVariableProvider)
	 */
	public IBuildEnvironmentVariable getVariable(String variableName,
			IConfiguration configuration, IEnvironmentVariableProvider provider) {

		String osName = System.getProperty(PROPERTY_OSNAME);
		if( osName == null )
			return null;
		else if (!osName.toLowerCase(Locale.getDefault()).startsWith("windows ")) //$NON-NLS-1$ 
			return null;
		
		
		if (variableName == null) return null;
		if (!VARNAME.equalsIgnoreCase(variableName)) return null;
		
		if( configuration.getOwner() != null )
		{
			IProject prj = configuration.getOwner().getProject();
			if( prj != null )
			{
				String p = IdePlugin.getDefault().getSDKPath(prj) + IConstants.PATH_TOOLS + "\\Toolchains\\ARM\\bin";
				return new BuildEnvVar(VARNAME, p.replace('/','\\'), IBuildEnvironmentVariable.ENVVAR_PREPEND, System.getProperty(PROPERTY_DELIMITER, DELIMITER_UNIX)); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		return null;	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.envvar.IConfigurationEnvironmentVariableSupplier#getVariables(org.eclipse.cdt.managedbuilder.core.IConfiguration, org.eclipse.cdt.managedbuilder.envvar.IEnvironmentVariableProvider)
	 */
	public IBuildEnvironmentVariable[] getVariables(
			IConfiguration configuration, IEnvironmentVariableProvider provider) {
		
		IBuildEnvironmentVariable[] tmp = new IBuildEnvironmentVariable[1];   
		tmp[0] = getVariable(VARNAME, configuration, provider);
		if (tmp[0] != null) return tmp; 
		return null;
	}
}

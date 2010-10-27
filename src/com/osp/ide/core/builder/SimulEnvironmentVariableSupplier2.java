/*******************************************************************************
 * Copyright (c) 2006, 2007 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *******************************************************************************/

package com.osp.ide.core.builder;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.envvar.IBuildEnvironmentVariable;
import org.eclipse.cdt.managedbuilder.envvar.IConfigurationEnvironmentVariableSupplier;
import org.eclipse.cdt.managedbuilder.envvar.IEnvironmentVariableProvider;
import org.eclipse.cdt.managedbuilder.gnu.cygwin.CygwinPathResolver;
import org.eclipse.cdt.utils.WindowsRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

/**
 * @author Doug Schaefer
 *
 */
public class SimulEnvironmentVariableSupplier2 implements
		IConfigurationEnvironmentVariableSupplier {

	private static class SimulBuildEnvironmentVariable implements IBuildEnvironmentVariable {
		private final String name;
		private final String value;
		private final int operation;
		
		public SimulBuildEnvironmentVariable(String name, String value, int operation) {
			this.name = name;
			this.value = value;
			this.operation = operation;
		}
		
		public String getName() {
			return name;
		}
		
		public String getValue() {
			return value;
		}
		
		public int getOperation() {
			return operation;
		}
		
		public String getDelimiter() {
			return ";";
		}
	}
	
	private IBuildEnvironmentVariable path;
	
	public static IPath getBinDir() {

		//IPath subPath = new Path("mingw\\bin");
		IPath subPath = new Path("Samsung Sourcery G++ for IA32 Windows\\bin");
		// 1. Try the tool Chain directory in the platform install directory
		IPath installPath = new Path(Platform.getInstallLocation().getURL().getFile());
		IPath binPath = installPath.append(subPath);
		if (binPath.toFile().isDirectory())
			return binPath;
		
		// 2. Try the directory above the install dir
		binPath = installPath.removeLastSegments(1).append(subPath);
		if (binPath.toFile().isDirectory())
			return binPath;
		
		// 3. Try looking if the tool Chain installer ran
		WindowsRegistry registry = WindowsRegistry.getRegistry();
		if (registry==null) return null; // probably not even windows
		
		String toolChainPath = registry.getLocalMachineValue(
				//"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\MinGW",
					"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Samsung Sourcery G++ for IA32 Windows",
					"InstallLocation");
		if (toolChainPath != null) {
			binPath = new Path(toolChainPath).append("bin");
			if (binPath.toFile().isDirectory())
				return binPath;
		}
		
		// 4. Try the default tool Chain install dir
		binPath = new Path("C:\\Program Files\\CodeSourcery\\Sourcery G++\\bin");
		//binPath = new Path("C:\\MinGW\\bin");
		if (binPath.toFile().isDirectory())
			return binPath;
		
		// No dice, return null
		return null;
	}
	
	public static IPath getMsysBinDir() {
		// Just look in the install location parent dir
		IPath installPath = new Path(Platform.getInstallLocation().getURL().getFile()).removeLastSegments(1);
		IPath msysBinPath = installPath.append("msys\\bin");
		return msysBinPath.toFile().isDirectory() ? msysBinPath : null;
	}
	
	public SimulEnvironmentVariableSupplier2() {
		IPath binPath = getBinDir();
		if (binPath != null) {
			String pathStr = binPath.toOSString();
			IPath msysBinPath = getMsysBinDir();
			if (msysBinPath != null)
				pathStr += ';' + msysBinPath.toOSString();
			
			String p = CygwinPathResolver.getBinPath();
			if (p != null)
			{
				pathStr += ';' + p.replace('/','\\');
			}
			
			path = new SimulBuildEnvironmentVariable("PATH", pathStr, IBuildEnvironmentVariable.ENVVAR_PREPEND);
		}
	}
	
	public IBuildEnvironmentVariable getVariable(String variableName,
			IConfiguration configuration, IEnvironmentVariableProvider provider) {
		if (path != null && variableName.equals(path.getName()))
			return path;
		else
			return null;
	}

	public IBuildEnvironmentVariable[] getVariables(
			IConfiguration configuration, IEnvironmentVariableProvider provider) {
		return path != null
			? new IBuildEnvironmentVariable[] { path }
			: new IBuildEnvironmentVariable[0];
	}

}

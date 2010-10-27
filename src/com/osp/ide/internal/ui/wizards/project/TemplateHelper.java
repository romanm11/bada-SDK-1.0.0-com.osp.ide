package com.osp.ide.internal.ui.wizards.project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;


public class TemplateHelper {
	public static final String START_PATTERN = "$("; //$NON-NLS-1$
	public static final String END_PATTERN = ")"; //$NON-NLS-1$

	
	public static String readFromFile(URL source) {
		char[] chars = new char[4092];
		InputStreamReader contentsReader = null;
		StringBuffer buffer = new StringBuffer();
		if (!new java.io.File(source.getFile()).exists()) {
			return "";
		} else {
			try {
				//contentsReader = new InputStreamReader(source.openStream());
				contentsReader = new InputStreamReader(source.openStream(), Charset.defaultCharset());
				
				int c;
				do {
					c = contentsReader.read(chars);
					if (c == -1)
						break;
					buffer.append(chars, 0, c);
				} while (c != -1);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return "";
			} finally {
				if( contentsReader != null )
					try {
						contentsReader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}

		}
		return buffer.toString();
	}

	
	public static Set<String> getReplaceKeys(String str) {
		Set<String> replaceStrings = new HashSet<String>();
		int start= 0, end= 0;
		while ((start = str.indexOf(START_PATTERN, start)) >= 0) {
			end = str.indexOf(END_PATTERN, start);
			if (end != -1) {
				replaceStrings.add(str.substring(start + START_PATTERN.length(), end));
				start = end + END_PATTERN.length();
			} else
				start++;
		}
		return replaceStrings;
	}
	
	public static String getValueAfterExpandingMacros(String string, Set<String> macros, Map<String, String> valueStore) {
		for (Iterator<String> i = macros.iterator(); i.hasNext();) {
			String key = i.next();
			String value = valueStore.get(key);
			if (value != null) {
				string = string.replace(START_PATTERN + key + END_PATTERN, value);
			}
			else
			{
				string = string.replace(START_PATTERN + key + END_PATTERN, "");
			}
		}
		return string;
	}
	
	public static void copyBinaryFile(URL source, File dest) throws IOException {
		byte[] bytes = new byte[4092];
		if (source != null && dest != null) {
			File file = new File(source.getFile());
			if (file.isFile()) {
				FileInputStream fis = new FileInputStream(file);
				FileOutputStream fos = new FileOutputStream(dest);
				int ch;
				while (true) {
					ch = fis.read(bytes);
					if (ch == -1) {
						break;
					}
					fos.write(bytes, 0, ch);
				}
				fis.close();
				fos.close();				
			}
		}
	}

	public static boolean copyFileToProject(IProject prj, String sourcePath, String distName, boolean refresh)
	{
		IPath prjPath = prj.getLocation();
		prjPath = prjPath.append(distName);
		
		File file = prjPath.makeAbsolute().toFile();
		
		File srcFile = new File(sourcePath);
		if( !srcFile.exists()) return false;
		
		try {
			if( copyBinaryFile2(srcFile, file) )
			{
				if(refresh)
				{
					IFile fi = prj.getFile(distName);		
					try {
						fi.refreshLocal(IResource.DEPTH_ONE, null);
					} catch (CoreException e1) {
						e1.printStackTrace();
					}
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;

	}

	public static boolean createFile(IProject prj, String text, String distName)
	{
		boolean succ = true;
		IPath prjPath = prj.getLocation();
		prjPath = prjPath.append(distName);
		
		File dest = prjPath.makeAbsolute().toFile();
		
		if ( dest != null) {
			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest)));
				bw.write(text);
				
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				succ = false;
			} finally {
				if( bw != null )
					try {
						bw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
					
		}
		else
		{
			succ =  false;
		}
		
		if( succ )
		{
			IFile fi = prj.getFile(distName);		
	        try {
	        	fi.refreshLocal(IResource.DEPTH_ONE, null);
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
		}
		
		return succ;
	}	
	
	
	public static boolean copyBinaryFile2(File source, File dest) throws IOException {
		byte[] bytes = new byte[4092];
		if (source != null && dest != null) {
			if (source.isFile()) {
				FileInputStream fis = new FileInputStream(source);
				FileOutputStream fos = new FileOutputStream(dest);
				int ch;
				while (true) {
					ch = fis.read(bytes);
					if (ch == -1) {
						break;
					}
					fos.write(bytes, 0, ch);
				}
				fis.close();
				fos.close();
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
		return true;
	}	
}

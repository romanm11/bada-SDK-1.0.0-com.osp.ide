package com.osp.ide.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

//import org.eclipse.cdt.internal.core.model.BinaryContainer;


import com.osp.ide.IConstants;

public class FileUtil {
	
	String errorMsg = "";
	Throwable exception = null;
	
	List systemFilesList;
	
	public FileUtil()
	{
		systemFilesList = new ArrayList();
		systemFilesList.add("thumbs.db");
	}
	
	public String getErrorMsg()
	{
		return errorMsg;
	}
	
	public Throwable getException()
	{
		return exception;
	}
	
	
	public String readString(InputStream is) {
		if (is == null)
			return null;
		BufferedReader reader= null;
		try {
			StringBuffer buffer= new StringBuffer();
			char[] part= new char[2048];
			int read= 0;
			//reader= new BufferedReader(new InputStreamReader(is)); //$NON-NLS-1$
			reader= new BufferedReader(new InputStreamReader(is, Charset.defaultCharset())); //$NON-NLS-1$
			

			while ((read= reader.read(part)) != -1)
				buffer.append(part, 0, read);

			return buffer.toString();

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public boolean copyFile(String sourceFile, String distPath) 
	{
		File srcFile = new File(sourceFile);
		if( !srcFile.exists() )
		{
			errorMsg = "Error:\nsource file \"" + sourceFile + "\" not found";
			exception = null;
			return false;
		}
		
		File distFile = new File(distPath + srcFile.separator + srcFile.getName());
		
		try {
			copyFile_internal(srcFile, distFile);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			errorMsg = "File copy Error";
			exception = e;
			return false;
		}
		
		//destFile.setExecutable(true, false);
		
		return true;
	}
	
//	private void copyFile_internal(File sourceFile, File destinationFile) throws IOException {
//	    FileChannel sourceChannel = new FileInputStream(sourceFile).getChannel();
//	    FileChannel destinationChannel = new FileOutputStream(destinationFile).getChannel();
//	    destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
//	    sourceChannel.close();
//	    destinationChannel.close();
//	}
	
	private void copyFile_internal(File sourceFile, File destinationFile) throws IOException {
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
	
		try {
			inputStream = new FileInputStream(sourceFile);
		} catch (FileNotFoundException e) {
			throw e;
		}

		try {
			outputStream = new FileOutputStream(destinationFile);
		} catch (FileNotFoundException e) {
			throw e;
		}
	
		BufferedInputStream source = null;
		BufferedOutputStream destination =null;
		try {

			source = new BufferedInputStream(inputStream);
			destination = new BufferedOutputStream(outputStream);

			byte[] buffer = new byte[8192];
			while (true) {
				int bytesRead = -1;
				if ((bytesRead = source.read(buffer)) == -1)
					break;
				destination.write(buffer, 0, bytesRead);
			}
			
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if( source != null ) source.close();
			} catch (IOException e) {
				// ignore
			}
			try {
				if( destination != null ) destination.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	public boolean mkDir(String dirName)
	{
		File f = new File(dirName);
		if( !f.exists())
		{
			boolean ret = f.mkdirs();
			if( ret == false )
			{
				errorMsg = "Can't create directory \"" + dirName + "\"";
				exception = null;				
			}
			return ret;
		}
		
		return true;
	}
	
	public boolean rmDir(String path) {
		return rmDir(new File(path));
	}
		 
	public boolean rmDir(File file) 
	{
		if(file.exists()) {
			File[] files =file.listFiles();
		   
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					if( rmDir(files[i]) == false) return false;
				} else {
					boolean ret = files[i].delete();
					if( ret == false )
					{
						errorMsg = "Can't delete \"" + files[i].getPath() + "\"";
						exception = null;
						return false;
					}					
				}
			}
		   
			boolean ret = file.delete();
			if( ret == false )
			{
				errorMsg = "Can't delete \"" + file.getPath() + "\"";
				exception = null;
				return false;
			}					
			
		} else {
			return true;
		}
		
		return true;
	} 

	
	public boolean copyDirectory(String srcDir, String dstDir)
	{
		File src = new File(srcDir);
		File dst = new File(dstDir);

		boolean succ = true;
		try {
			succ = copyDirectory(src, dst);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			succ =  false;
		}
		
		return succ;
	}
	
	public boolean copyDirectory(File srcPath, File dstPath)  throws IOException
	{

		if( srcPath.getName().startsWith(IConstants.DOT)) return true;
		
		if (srcPath.isDirectory()) {
			
			if (!dstPath.exists()) {
				if( dstPath.mkdir() == false )
				{
					errorMsg = "Can't create directory \"" + dstPath.getPath() + "\"";
					exception = null;	
					return false;
				}
			}

			String files[] = srcPath.list();
			
			if( files != null )
			{
				for(int i = 0; i < files.length; i++) {
					if( copyDirectory(new File(srcPath, files[i]),	new File(dstPath, files[i])) == false)
						return false;
				}
			}
		}
		else {
			if(!srcPath.exists()) {
				errorMsg = "Error:\nsource file \"" + srcPath.getPath() + "\" not found";
				exception = null;
				
				return false;
			}
			else
			{
				if( !systemFilesList.contains(srcPath.getName().toLowerCase(Locale.getDefault())))
				{
					try {
						copyFile_internal(srcPath, dstPath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						errorMsg = "File copy Error";
						exception = e;
						return false;
					}
				}
			}
		}

		return true;
	}
	
	
	public boolean copyHomeFolder(String srcDir, String dstDir)
	{
		File src = new File(srcDir);
		File dst = new File(dstDir);

		boolean succ = true;
		try {
			succ = copyHomeFolder(src, dst);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			succ =  false;
		}
		
		return succ;
	}
	
	private boolean copyHomeFolder(File srcPath, File dstPath)  throws IOException
	{
		if( srcPath.getName().startsWith(IConstants.DOT)) return true;
		
		if (srcPath.isDirectory()) {
			
			if( srcPath.getName().toLowerCase(Locale.getDefault()).equals(IConstants.DIR_SHARE))
			{
				String parent = srcPath.getParent();
				if( parent != null && parent.length() > 0 )
				{
					if( (new File(parent)).getName().toLowerCase(Locale.getDefault()).equals(IConstants.DIR_HOME.toLowerCase(Locale.getDefault())))
					{
						return true;
					}
				}
			}
			
			if (!dstPath.exists()) {
				if( dstPath.mkdir() == false )
				{
					errorMsg = "Can't create directory \"" + dstPath.getPath() + "\"";
					exception = null;	
					return false;
				}
			}

			String files[] = srcPath.list();
			
			if( files != null )
			{
				for(int i = 0; i < files.length; i++) {
					if( copyHomeFolder(new File(srcPath, files[i]),	new File(dstPath, files[i])) == false)
						return false;
				}
			}
		}
		else {
			if(!srcPath.exists()) {
				errorMsg = "Error:\nsource file \"" + srcPath.getPath() + "\" not found";
				exception = null;
				
				return false;
			}
			else
			{
				if( !systemFilesList.contains(srcPath.getName().toLowerCase(Locale.getDefault())))
				{
					try {
						copyFile_internal(srcPath, dstPath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						errorMsg = "File copy Error";
						exception = e;
						return false;
					}
				}
			}
		}

		return true;
	}
	
	static public String loadFromFile(String path) {
		
		if( (new File(path)).exists() == false)
		{
			return "";
		}
		
		BufferedReader reader= null;
		try {
			InputStream is = new FileInputStream(path);
			
			StringBuffer buffer= new StringBuffer();
			char[] part= new char[2048];
			int read= 0;
			reader= new BufferedReader(new InputStreamReader(is, Charset.defaultCharset())); //$NON-NLS-1$
			

			while ((read= reader.read(part)) != -1)
				buffer.append(part, 0, read);

			return buffer.toString();

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		return "";
	}	
	
	
	static public IFolder createFolder(IProject project, String name, IProgressMonitor monitor) {
		IFolder folder = project.getFolder(name);
		if (!folder.exists()) {
			try {
				folder.create(true, true, monitor);
			} catch (CoreException e) {
				return null;
			}			
		}
		
		return folder;
	}
	
	public boolean copyFiles(String srcDir, String dstDir, final String ext)
	{
		File sDir = new File(srcDir);
		if( sDir.exists() == false ) return true;
		
		String list[] = sDir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase(Locale.getDefault()).endsWith(ext);
			}
		});
		
		if( list == null ) return true;
		srcDir += sDir.separator;
		
		for( int i = 0; i < list.length; i++)
		{
			if( copyFile(srcDir + list[i], dstDir) == false ) return false;
		}
		
		
		return true;
	}
		
}

package com.osp.ide.internal.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.cdt.core.model.ICContainer;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.osp.ide.IConstants;
import com.osp.ide.core.AppXmlStore;

// RTL을 지원하기 위한 MakePackageHelper
public class MakePackageHelper {
	MakePackageDialog dial;
	IContainer fContainer;
	String ErrorM;

	protected IProject fProject = null;
	protected boolean noContentOnPage = false;
	protected IConfiguration[] cfgDescs = null;

	private IConfiguration currentConfig = null;
	private String buildTargetName = "";
	private String buildTargetExt = "";

	private ResourceTreeAndListGroup resourceGroup;

	IFolder folderIcon = null;

	boolean makeButtonEnabled = false;
	boolean bFileCheckError = false;

	String libExt = IConstants.EXT_SO;
	String outFilePath;
	String fileCheckErrorMag = "";

	PackageData packageData;

	IWorkbenchWindow window;

	List list;
	List items;

	boolean flag = true;

	// build가 되어있지 않은 경우 새로 build를 하기 위한 runnable 클래스
	protected class BuildBinary implements IRunnableWithProgress {
		IProject prj;

		// 생성자 - 현재 선택 된 프로젝트를 인자로 전달
		public BuildBinary(IProject prj) {
			this.prj = prj;
		}

		// 실제 수행 하는 메소드
		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			// TODO Auto-generated method stub
			// 전달 받은 프로젝트가 null이 아닐 경우
			if (prj != null && prj.exists()) {
				// build의 정보를 전달 받는다
				IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(prj);
				IConfiguration[] cfgs = info.getManagedProject()
						.getConfigurations();
				IConfiguration releaseCfg = null;
				// target-release의 build정보를 가져온다
				for (int i = 0; i < cfgs.length; i++) {
					if (cfgs[i].getName().equals(
							IConstants.CONFIG_TARGET_RELEASE_NAME)) {
						releaseCfg = cfgs[i];
						break;
					}
				}
				// default build 정보를 가져온다
				IConfiguration defCfg = info.getDefaultConfiguration();
				// target-release의 build 정보가 null이 아닐 경우
				if (releaseCfg != null) {
					// target-release의 build 정보와 default build 정보를 비교하여 같지 않을 경우
					if (releaseCfg != defCfg)
						// default build 정보를 target-release 정보로 변경한다.
						info.setDefaultConfiguration(releaseCfg);
					try {
						// target-release 정보를 이용하여 build를 수행한다.
						prj.build(IncrementalProjectBuilder.INCREMENTAL_BUILD,
								monitor);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (releaseCfg != defCfg)
						// default build 정보로 다시 build 정보를 설정한다.
						info.setDefaultConfiguration(defCfg);
				}
			}
		}
	}

	public MakePackageHelper(ISelection selection) {
		// TODO Auto-generated constructor stub
		// selection에서 container를 추출한다.
		fContainer = getContainer(selection);
		fProject = fContainer.getProject();
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		// 
		setCurrentConfig();
		packageData = new PackageData();
		this.setDefaultPackageData();
		// dial = new MakePackageDialog(Display.getDefault().getActiveShell(),
		// fContainer);
		// dial.create();
		// SelectorChange();
		// dial.handleConfigSelectionRTL();
		// dial.setDefaultSelectRTL();
	}

	public MakePackageHelper(IContainer container) {
		// TODO Auto-generated constructor stub
		fContainer = container;
		fProject = fContainer.getProject();
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		setCurrentConfig();
		packageData = new PackageData();
		this.setDefaultPackageData();
		// dial = new MakePackageDialog(Display.getDefault().getActiveShell(),
		// fContainer);
		// dial.create();
		// SelectorChange();
		// dial.handleConfigSelectionRTL();
		// dial.setDefaultSelectRTL();
	}

	public MakePackageHelper(IProject project) {
		// TODO Auto-generated constructor stub
		fContainer = project;
		fProject = project;
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		setCurrentConfig();
		packageData = new PackageData();
		this.setDefaultPackageData();
		// dial = new MakePackageDialog(Display.getDefault().getActiveShell(),
		// fContainer);
		// dial.create();
		// SelectorChange();
		// dial.handleConfigSelectionRTL();
		// dial.setDefaultSelectRTL();
	}

	private void fileCheck() {
		bFileCheckError = false;
		fileCheckErrorMag = "";

		IFile exeFile = packageData.getExeFile();
		IFile appXml = packageData.getAppXml();
		if (exeFile == null) {
			fileCheckErrorMag = "Exe File Not Found";
		}
		if (exeFile != null && appXml != null) {
			String fileName = exeFile.getName();
			fileName = fileName.substring(0, fileName.length() - 4); // remove
			// .exe

			AppXmlStore xmlManager = new AppXmlStore(appXml);
			xmlManager.loadXML();

			if (!xmlManager.getEntry().equals(fileName)) {
				bFileCheckError = true;
				fileCheckErrorMag = "The name of the project executable binary is not identical with the value of the <Entry> field in application.xml.";
				return;
			}

			if (fProject == null)
				return;

			String iconName = xmlManager.getIconMainMenu();
			if (iconName != null && iconName.length() > 0) {
				IFile iconFile = fProject.getFile(IConstants.DIR_ICON
						+ IConstants.FILE_SEP_FSLASH + iconName);
				if (iconFile == null || !iconFile.exists()) {
					bFileCheckError = true;
					fileCheckErrorMag = "Cannot find the "
							+ iconName
							+ " image specified in the <MainMenu> field in application.xml.";
					return;
				}
			}

			iconName = xmlManager.getIconLaunchImage();
			if (iconName != null && iconName.length() > 0) {
				IFile iconFile = fProject.getFile(IConstants.DIR_ICON
						+ IConstants.FILE_SEP_FSLASH + iconName);
				if (iconFile == null || !iconFile.exists()) {
					bFileCheckError = true;
					fileCheckErrorMag = "Cannot find the "
							+ iconName
							+ " image specified in the <LaunchImage> field in application.xml.";
					return;
				}
			}

			iconName = xmlManager.getIconQuickPanel();
			if (iconName != null && iconName.length() > 0) {
				IFile iconFile = fProject.getFile(IConstants.DIR_ICON
						+ IConstants.FILE_SEP_FSLASH + iconName);
				if (iconFile == null || !iconFile.exists()) {
					bFileCheckError = true;
					fileCheckErrorMag = "Cannot find the "
							+ iconName
							+ " image specified in the <QuickPanel> field in application.xml.";
					return;
				}
			}

			iconName = xmlManager.getIconSetting();
			if (iconName != null && iconName.length() > 0) {
				IFile iconFile = fProject.getFile(IConstants.DIR_ICON
						+ IConstants.FILE_SEP_FSLASH + iconName);
				if (iconFile == null || !iconFile.exists()) {
					bFileCheckError = true;
					fileCheckErrorMag = "Cannot find the "
							+ iconName
							+ " image specified in the <Setting> field in application.xml.";
					return;
				}
			}

			iconName = xmlManager.getIconTicker();
			if (iconName != null && iconName.length() > 0) {
				IFile iconFile = fProject.getFile(IConstants.DIR_ICON
						+ IConstants.FILE_SEP_FSLASH + iconName);
				if (iconFile == null || !iconFile.exists()) {
					bFileCheckError = true;
					fileCheckErrorMag = "Cannot find the "
							+ iconName
							+ " image specified in the <Ticker> field in application.xml.";
					return;
				}
			}
		}
	}

	protected void setCurrentConfig() {
		IConfiguration defCfg = null;
		IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(fProject);
		cfgDescs = info.getManagedProject().getConfigurations();
		if (cfgDescs == null || cfgDescs.length == 0)
			return;
		defCfg = info.getDefaultConfiguration();

		for (int i = 0; i < cfgDescs.length; i++) {
			if (cfgDescs[i].getName().equals(
					IConstants.CONFIG_TARGET_RELEASE_NAME)) {
				currentConfig = cfgDescs[i];
			}
		}
		buildTargetName = info.getBuildArtifactName();
		// Get its extension
		buildTargetExt = info.getBuildArtifactExtension();
	}

	public void setDefaultPackageData() {

		packageData.clear();

		if (noContentOnPage == false && fProject != null
				&& currentConfig != null) {
			List selectedResources = new ArrayList();

			IFile file = fProject.getFile(IConstants.APP_XML_FILE);
			if (file != null) {
				if (!file.exists())
					file = null;
			}
			packageData.setAppXml(file);

			file = fProject.getFile(IConstants.MANIFEST_FILE);
			if (file != null) {
				if (!file.exists())
					file = null;
			}
			packageData.setManifestXml(file);

			ITool targetTool = currentConfig.calculateTargetTool();
			String outputPrefix = "";
			if (targetTool != null) {
				outputPrefix = targetTool.getOutputPrefix();
			}
			String targetName = buildTargetName;
			if (buildTargetExt.length() > 0)
				targetName += "." + buildTargetExt;

			String targetFilePath = outputPrefix + IConstants.PREFIX_CONFIG_DIR
					+ currentConfig.getName() + IConstants.FILE_SEP_FSLASH
					+ targetName;

			// targetExePath =
			// fProject.getLocation().makeAbsolute().toOSString() +
			// IConstants.FILE_SEP_BSLASH + outputPrefix +
			// IConstants.PREFIX_CONFIG_DIR + currentConfig.getName();

			packageData.setBuildDirectory(fProject.getFolder(outputPrefix
					+ IConstants.PREFIX_CONFIG_DIR + currentConfig.getName()));
			// packageData.setBuildDirectory(fProject.getFolder(outputPrefix));

			// check exe File
			file = fProject.getFile(targetFilePath);
			if (file != null) {
				if (!file.exists())
					file = null;
			}
			packageData.setExeFile(file);

			// check rbin
			IFolder folder = fProject.getFolder(IConstants.DIR_RESOURCE);
			if (folder != null && folder.exists())
				selectedResources.add(folder);

			// folder = fProject.getFolder(IConstants.DIR_ICON);
			// if( folder != null && folder.exists() )
			// selectedResources.add(folder);

			folder = fProject.getFolder(IConstants.DIR_HOME);
			if (folder != null && folder.exists())
				selectedResources.add(folder);

			folderIcon = fProject.getFolder(IConstants.DIR_ICON);
			if (folderIcon == null || !folderIcon.exists())
				folderIcon = null;
			setResource(selectedResources);
		} else {
		}
	}

	private void setResource(List sel) {
		items = new ArrayList();
		Iterator it = sel.iterator();
		while (it.hasNext()) {
			IResource currentResource = (IResource) it.next();
			if (currentResource.getType() == IResource.FILE) {
				items.add(currentResource);
			} else {
				items.add(currentResource);
			}
		}
	}

	public void SelectorChange() {
		Combo config = dial.getConfigSelector();
		config.setText(config.getItem(2));
	}

	public void setErrorMessage(String ErrorM) {
		this.ErrorM = ErrorM;
	}

	public String getErrorMessage() {
		return ErrorM;
	}

	public String MakePackageStart(String path) {
		// String str = dial.checkFilesRTL();
		this.fileCheck();
		String str = null;
		if (!fileCheckErrorMag.equals("")) {
			str = fileCheckErrorMag;
		}
		if (str != null) {
			if (str.equals("Exe File Not Found")) {
				final IProject prj = fContainer.getProject();
				final BuildBinary oper = new BuildBinary(prj);
				getStandardDisplay().syncExec(new Runnable() {
					public void run() {
						IWorkbenchWindow window = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow();
						if (window != null) {
							ProgressMonitorDialog dialog = new ProgressMonitorDialog(
									window.getShell());

							try {
								dialog.run(true, true, oper);
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				});
				this.setDefaultPackageData();
			} else {
				this.setErrorMessage(fileCheckErrorMag);
				return "Error";
			}
		}
		// dial.PressOK(null, path);
		this.startMakePackage(null, path);
		this.setErrorMessage(fileCheckErrorMag);
		return outFilePath;
	}

	private IContainer getContainer(ISelection selection) {
		IContainer container = null;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object obj = sel.getFirstElement();
			if (obj instanceof ICElement) {
				if (obj instanceof ICContainer || obj instanceof ICProject) {
					container = (IContainer) ((ICElement) obj)
							.getUnderlyingResource();
				} else {
					obj = ((ICElement) obj).getResource();
					if (obj != null) {
						container = ((IResource) obj).getParent();
					}
				}
			} else if (obj instanceof IResource) {
				if (obj instanceof IContainer) {
					container = (IContainer) obj;
				} else {
					container = ((IResource) obj).getParent();
				}
			} else {
				container = null;
			}
		}
		return container;
	}

	private Display getStandardDisplay() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

	private void startMakePackage(String s, String path) {
		if (folderIcon != null) {
			items.add(folderIcon);
		}
		packageData.setExportResources(items);

		String outFileName = fProject.getName() + "_target-Releaze";
		if (!outFileName.toLowerCase(Locale.getDefault()).endsWith(
				IConstants.EXT_PACKAGE))
			outFileName += IConstants.EXT_PACKAGE;
		if (path != null)
			outFilePath = path + IConstants.FILE_SEP_BSLASH + outFileName;

		packageData.setProject(fProject);
		packageData.setConfigName(currentConfig.getName());
		// packageData.setCheckSecurity(isSecurityChecked);
		setLibaryList();

		if (executeExportOperation(new PackageArchiveExportOperation(null,
				outFilePath, packageData))) {
			if (s != null)
				MessageDialog.openInformation(getStandardDisplay()
						.getActiveShell(), "Error", s);
		}
	}

	private void setLibaryList() {
		list = new ArrayList<IFolder>();
		IFolder folder = fProject.getFolder(IConstants.DIR_LIB);
		if (folder != null && folder.exists()) {
			IResource[] members = null;
			try {
				members = folder.members();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

			if (members != null) {
				for (int i = 0; i < members.length; i++) {
					// And the test bits with the resource types to see if they
					// are what we want
					if ((members[i].getType() & IResource.FILE) > 0) {

						if (members[i].getName().toLowerCase(
								Locale.getDefault()).endsWith(libExt)) {
							list.add(members[i]);
						}
					}
				}
			}
		}
		packageData.setLibraryList(list);
	}

	public boolean executeExportOperation(PackageArchiveExportOperation op) {
		op.setUseCompression(true);
		final PackageArchiveExportOperation oper;
		oper = op;

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				try {
					new ProgressMonitorDialog(getStandardDisplay()
							.getActiveShell()).run(false, true, oper);
				} catch (InterruptedException e) {
					flag = false;
				} catch (InvocationTargetException e) {
					MessageDialog.openError(getStandardDisplay()
							.getActiveShell(), "Internal Error for Export", e
							.getTargetException().getMessage());
					flag = false;
				}
				IStatus status = oper.getStatus();
				if (!status.isOK()) {
					ErrorDialog.openError(
							getStandardDisplay().getActiveShell(),
							"Export error", null, // no
							// special
							// message
							status);
					flag = false;
				}

				if (fProject != null) {
					try {
						fProject.refreshLocal(IResource.DEPTH_ONE, null);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		});
		// new
		// ProgressMonitorDialog(getStandardDisplay().getActiveShell()).run(false,
		// true, op);
		// }
		// } catch (InterruptedException e) {
		// return false;
		// } catch (InvocationTargetException e) {
		// MessageDialog.openError(getStandardDisplay().getActiveShell(),
		// "Internal Error for Export", e.getTargetException()
		// .getMessage());
		// return false;
		// }

		return flag;
	}
}

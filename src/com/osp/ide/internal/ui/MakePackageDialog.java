package com.osp.ide.internal.ui;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.macros.BuildMacroException;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;
import org.eclipse.cdt.utils.ui.controls.ControlFactory;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text; //import org.eclipse.ui.internal.ide.dialogs.ResourceTreeAndListGroup;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.DialogUtil;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.osp.ide.IConstants;
import com.osp.ide.core.AppXmlStore;
import com.osp.ide.core.badaNature;
import com.osp.ide.utils.FileUtil;

public class MakePackageDialog extends TitleAreaDialog {

	IContainer fContainer;

	protected IProject fProject = null;
	protected boolean noContentOnPage = false;

	protected IConfiguration[] cfgDescs = null;
	private IConfiguration currentConfig = null;
	private String buildTargetName = "";
	private String buildTargetExt = "";
	// private String targetExePath="";

	private ResourceTreeAndListGroup resourceGroup;

	protected Combo configSelector;
	protected Text textOutFolder;
	protected Text textPkgName;

	// protected Button checkSecurity;

	Button makeButton = null;
	boolean makeButtonEnabled = false;

	IFolder folderIcon = null;

	String libExt = IConstants.EXT_SO;

	PackageData packageData;

	 MakePackageHelper helper;

	String outFilePath;

	boolean bFileCheckError = false;
	String fileCheckErrorMag = "";

	Composite parent;

	static int MANIFEST_CHECK_TRUE = 0; // manifest.xml 파일 서버와 동일
	static int MANIFEST_CHECK_FALSE = 1; // manifest.xml 파일 서버와 다름
	static int MANIFEST_CHECK_OFFLINE = 2; // manifest.xml 파일 서버와 연결이 않됨
	static int MANIFEST_CHECK_NOTFOUND = 3; // manifest.xml 동일한 id를 서버에서 찾을 수 없음
	static int MANIFEST_CHECK_USERCANCEL = 4;// User cancel

	static final String ERR_MSG_MANIFEST_CHECK_FALSE = "Invalid Manifest file.";
	static final String ERR_MSG_MANIFEST_CHECK_OFFLINE = "Server Off-line.";
	static final String ERR_MSG_MANIFEST_CHECK_NOTFOUND = "Id Not Found.";
	static final String ERR_MSG_MANIFEST_CHECK_USERCANCEL = "User Cancel.";

	public Combo getConfigSelector() {
		return configSelector;
	}

	protected class CheckManifestOperaton implements IRunnableWithProgress {

		IProject project;
		String surl;

		int result = MANIFEST_CHECK_OFFLINE;

		public CheckManifestOperaton(String sUrl) {
			this.surl = sUrl;
		}

		public int getResult() {
			return result;
		}

		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			// TODO Auto-generated method stub

			monitor.beginTask("Check Manifest...", -1);

			URL url;
			try {
				url = new URL(surl);
				URLConnection conn = url.openConnection();
				HttpURLConnection hurl = (HttpURLConnection) conn;
				hurl.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				hurl.setRequestMethod("POST");
				hurl.setDoOutput(true);
				hurl.setDoInput(true);
				hurl.setUseCaches(false);
				hurl.setDefaultUseCaches(false);
				boolean flag = false; // 서버가 접속이 되었는지 되지 않았는지 체크합니다.
				String cookie = null; // 서버에서 받은 답의0번 헤더를 가져옵니다.
				String bufferline = null; // 서버에서 받은 답변을 buffer에서 한줄 씩 가져오기 위한
											// 문자열입니다.
				String manifestvalid = ""; // 서버에서 받은 답변의 바디 부분을 모은 문자열입니다.
				String onlinecheck = ""; // 현재 서버가 접속이 되었는지 되지 않았는지 체크하기 위해 0번
											// 헤더의
				// 토큰을 하나씩 비교하기 위한 문자열입니다.
				cookie = conn.getHeaderField(0);
				StringTokenizer stk = new StringTokenizer(cookie, " "); // 가져온
																		// 0번
																		// 헤더의
																		// 토큰을
																		// " "
																		// 으로
																		// 구분합니다.
				while (stk.hasMoreElements()) {
					String str1 = stk.nextToken();
					if (str1.equals("200")) {
						flag = !flag;
					}
				}
				BufferedReader in = new BufferedReader(new InputStreamReader(
						hurl.getInputStream()));
				while ((bufferline = in.readLine()) != null) {
					manifestvalid = manifestvalid.concat(bufferline);
				}
				// System.out.println(str2); // 받아온 문서의 내용 테스트 용입니다.
				// System.out.println(flag);
				if (flag == false) { // 서버의 응답이 200이 아닌 경우입니다.
					result = MANIFEST_CHECK_OFFLINE;
					monitor.done();
					return;
				} else if (manifestvalid.equals("true")) { // 서버가 접속 되어 있으며
															// manifest.xml이 서버와
															// 동일한 경우
					result = MANIFEST_CHECK_TRUE;
					monitor.done();
					return;
				} else if (manifestvalid.equals("false")) { // 서버도 접속 되지 않고
															// manifest.xml도 서버와
															// 다른 경우
					result = MANIFEST_CHECK_FALSE;
					monitor.done();
					return;
				} else if (manifestvalid.equals("NotFound")) { // 서버에서 동일한 id의
																// manifest.xml을
																// 찾을 수 없는 경우
					result = MANIFEST_CHECK_NOTFOUND;
					monitor.done();
					return;
				}
				// 받아오는 헤더의 정보가 아닌 바디의 정보가
				// true/false의 값으로 넘어오기 때문에 더이상
				// 헤더는 필요 없습니다.
				// boolean flag = false; // 문자열을 비교하여 바로 true/false를 반환 합니다.
				// String cookie = null;
				// String str3 = "";
				// cookie = conn.getHeaderField(0);
				// StringTokenizer stk = new StringTokenizer(cookie, " ");
				// while(stk.hasMoreElements())
				// {
				// String str1 = stk.nextToken();
				// if(str1.equals("200")){
				// flag = !flag;
				// }
				// }
				// System.out.println(cookie); // 0번 헤더 내용 출력
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
			} catch (Exception e) {
			}

			if (monitor.isCanceled())
				result = MANIFEST_CHECK_USERCANCEL;
			else
				result = MANIFEST_CHECK_OFFLINE; // 페이지가 존재하지 않는 경우입니다.
			// 위의 try/catch문에서 NullPointExeption이 불려옵니다.
			// 때문에 Exception을 통해서 try문을 무시하게 됩니다.
			monitor.done();

		}
	}

	public MakePackageDialog(Shell parentShell, IContainer container) {
		super(parentShell);
		// TODO Auto-generated constructor stub
		fContainer = container;

		 helper = new MakePackageHelper(container);
		packageData = new PackageData();
		// helper.setPackageData(packageData);
	}

	public void create() {
		// TODO Auto-generated method stub
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE);
		super.create();

		if (noContentOnPage)
			getShell().setSize(450, 400);
		updateWidgetEnablements();
	}

	@Override
	protected void configureShell(Shell newShell) {
		// TODO Auto-generated method stub
		super.configureShell(newShell);

		newShell.setText("Make Package");
	}

	protected Control createDialogArea(Composite parent) {
		this.parent = parent;
		Composite composite = (Composite) super.createDialogArea(parent);
		((GridLayout) composite.getLayout()).numColumns = 1;

		Composite compArea = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 10;
		layout.verticalSpacing = 10;
		compArea.setLayout(layout);
		compArea.setLayoutData(new GridData(GridData.FILL_BOTH));

		String s = null;
		if (!checkElement()) {
			s = "This element not a project";
		} else {
			fProject = getProject();
			if (!isBadaProject(fProject))
				s = "This project is not a bada project"; //$NON-NLS-1$

			// if( !isManifestFileValid(fProject))
			// // 서버가 접속 되어있으며 manifest.xml이 다른 경우
			// if(fileCheckErrorMag.equals("Invalid Manifest file."))
			//					s = "This project manifest file is invalid."; //$NON-NLS-1$
			// // 서버가 접속이 되어있지 않은 경우
			// else
			// s = "This server Off-Line.";
		}

		if (s == null) {
			contentForConfiguration(compArea);
			createWidgets(compArea);
			setOutputFolder();
			initConfigurations();
			return composite;
		}

		// no contents
		Label label = new Label(compArea, SWT.LEFT);
		label.setText(s);
		label.setFont(compArea.getFont());
		noContentOnPage = true;

		return composite;

	}

	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		super.createButtonsForButtonBar(parent);

		makeButton = getButton(IDialogConstants.OK_ID);
		makeButton.setText("&Make");
		setMakeButton(makeButtonEnabled);

		Button closeButton = getButton(IDialogConstants.CANCEL_ID);
		closeButton.setText("Cancel");

		setTitle("Select files to package");
	}

	protected void createWidgets(Composite parent) {
		Composite treeComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		treeComposite.setLayout(layout);
		treeComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label_tree = new Label(treeComposite, SWT.NONE);
		label_tree.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label_tree
				.setText("Select the folders and files to include in the bada application package.");

		createResourcesGroup(treeComposite);
		createTreeButtonsGroup(treeComposite);

		Composite folderComposite = new Composite(parent, SWT.NONE);
		layout = new GridLayout(3, false);
		folderComposite.setLayout(layout);
		folderComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label_pkgName = new Label(folderComposite, SWT.NONE);
		label_pkgName.setLayoutData(new GridData());
		label_pkgName.setText("Package name:");

		textPkgName = new Text(folderComposite, SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		textPkgName.setLayoutData(gd);
		textPkgName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateWidgetEnablements();
			}
		});

		Label label_folder = new Label(folderComposite, SWT.NONE);
		label_folder.setLayoutData(new GridData());
		label_folder.setText("Output folder:");

		textOutFolder = new Text(folderComposite, SWT.BORDER);
		textOutFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textOutFolder.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				outFolderChanged();
			}
		});

		Button changeButton = new Button(folderComposite, SWT.PUSH);
		changeButton.setText("Browse...");
		changeButton.setLayoutData(new GridData());
		changeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				handleOutDirButtonPressed();
			}
		});

		// checkSecurity = new Button(folderComposite, SWT.CHECK);
		// checkSecurity.setText("Check the application's security violation");
		// gd = new GridData(GridData.FILL_HORIZONTAL);
		// gd.horizontalSpan = 3;
		// checkSecurity.setLayoutData(gd);

		Label labelSep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL
				| SWT.LINE_DOT);
		;
		labelSep.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	}

	protected void handleOutDirButtonPressed() {

		DirectoryDialog dialog = new DirectoryDialog(getShell());
		dialog.setMessage("Select Output Folder");

		String dirName = textOutFolder.getText().trim();

		if (dirName.length() > 0) {
			File path = new File(dirName);
			if (path.exists())
				dialog.setFilterPath(new Path(dirName).toOSString());
		}

		String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			if (!dirName.equals(selectedDirectory)) {
				textOutFolder.setText(selectedDirectory);
				outFolderChanged();
			}
		}
	}

	protected final void createResourcesGroup(Composite parent) {

		// create the input element, which has the root resource
		// as its only child
		List input = new ArrayList();

		if (fProject != null) {
			// input.add(fProject);

			IFolder folder = fProject.getFolder(IConstants.DIR_HOME);
			if (folder != null && folder.exists())
				input.add(folder);

			folder = fProject.getFolder(IConstants.DIR_RESOURCE);
			if (folder != null && folder.exists())
				input.add(folder);
		}

		this.resourceGroup = new ResourceTreeAndListGroup(parent, input,
				getResourceProvider(IResource.FOLDER), WorkbenchLabelProvider
						.getDecoratingWorkbenchLabelProvider(),
				getResourceProvider(IResource.FILE), WorkbenchLabelProvider
						.getDecoratingWorkbenchLabelProvider(), SWT.NONE,
				DialogUtil.inRegularFontMode(parent));

		ICheckStateListener listener = new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				updateWidgetEnablements();
			}
		};

		this.resourceGroup.addCheckStateListener(listener);

	}

	protected final void createTreeButtonsGroup(Composite parent) {

		Font font = parent.getFont();

		// top level group
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setFont(parent.getFont());

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		buttonComposite.setLayout(layout);
		buttonComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL));

		Button selectButton = createTreeButton(buttonComposite,
				IDialogConstants.SELECT_ALL_ID, "&Select All", false);

		SelectionListener listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				resourceGroup.setAllSelections(true);
			}
		};
		selectButton.addSelectionListener(listener);
		selectButton.setFont(font);
		setButtonLayoutData(selectButton);

		Button deselectButton = createTreeButton(buttonComposite,
				IDialogConstants.DESELECT_ALL_ID, "&Deselect All", false);

		listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				resourceGroup.setAllSelections(false);
			}
		};
		deselectButton.addSelectionListener(listener);
		deselectButton.setFont(font);
		setButtonLayoutData(deselectButton);
	}

	protected Button createTreeButton(Composite parent, int id, String label,
			boolean defaultButton) {
		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;

		Button button = new Button(parent, SWT.PUSH);

		GridData buttonData = new GridData(GridData.FILL_HORIZONTAL);
		button.setLayoutData(buttonData);

		button.setData(Integer.valueOf(id));
		button.setText(label);
		button.setFont(parent.getFont());

		if (defaultButton) {
			Shell shell = parent.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
			button.setFocus();
		}
		button.setFont(parent.getFont());
		setButtonLayoutData(button);
		return button;
	}

	private ITreeContentProvider getResourceProvider(final int resourceType) {
		return new WorkbenchContentProvider() {
			public Object[] getChildren(Object o) {
				if (o instanceof IContainer) {
					IResource[] members = null;
					try {
						members = ((IContainer) o).members();
					} catch (CoreException e) {
						// just return an empty set of children
						return new Object[0];
					}

					// filter out the desired resource types
					ArrayList results = new ArrayList();

					if (o instanceof IProject) {
						for (int i = 0; i < members.length; i++) {
							// And the test bits with the resource types to see
							// if they are what we want
							if ((members[i].getType() & resourceType) > 0) {

								String name = members[i].getName();
								if (!name.startsWith(IConstants.DOT)
										&& !name
												.equals(IConstants.APP_XML_FILE)
										&& !name
												.equals(IConstants.MANIFEST_FILE))
									results.add(members[i]);
							}
						}

					} else {
						if (o instanceof IFolder) {
							if (((IFolder) o).getName().equals(
									IConstants.DIR_HOME)) {
								for (int i = 0; i < members.length; i++) {
									// And the test bits with the resource types
									// to see if they are what we want
									if ((members[i].getType() & resourceType) > 0) {

										if (!members[i].getName().startsWith(
												IConstants.DOT)
												&& !IConstants.DIR_SHARE
														.equals(members[i]
																.getName()
																.toLowerCase(
																		Locale
																				.getDefault())))
											results.add(members[i]);
									}
								}

								return results.toArray();
							}

						}

						for (int i = 0; i < members.length; i++) {
							// And the test bits with the resource types to see
							// if they are what we want
							if ((members[i].getType() & resourceType) > 0) {

								if (!members[i].getName().startsWith(
										IConstants.DOT))
									results.add(members[i]);
							}
						}

					}

					return results.toArray();
				}
				// input element case
				if (o instanceof ArrayList) {
					return ((ArrayList) o).toArray();
				}
				return new Object[0];
			}
		};
	}

	protected boolean checkElement() {
		boolean isProject = false;
		IResource internalElement = null;

		IAdaptable el = fContainer;
		if (el instanceof ICElement)
			internalElement = ((ICElement) el).getResource();
		else if (el instanceof IResource)
			internalElement = (IResource) el;
		if (internalElement == null)
			return false;

		if (internalElement.getProject() != null)
			isProject = true;
		else
			isProject = false;

		return isProject;
	}

	public IProject getProject() {
		/*
		 * Object element = fContainer; if (element != null) { if (element
		 * instanceof IProject) { IResource f = (IResource) element; return
		 * f.getProject(); } else if (element instanceof ICProject) return
		 * ((ICProject)element).getProject(); } return null;
		 */
		IResource internalElement = null;

		IAdaptable el = fContainer;
		if (el instanceof ICElement)
			internalElement = ((ICElement) el).getResource();
		else if (el instanceof IResource)
			internalElement = (IResource) el;
		if (internalElement == null)
			return null;

		return internalElement.getProject();

	}

	public boolean isCDTPrj(IProject p) {
		ICProjectDescription prjd = CoreModel.getDefault()
				.getProjectDescription(p, false);
		if (prjd == null)
			return false;
		ICConfigurationDescription[] cfgs = prjd.getConfigurations();
		return (cfgs != null && cfgs.length > 0);
	}

	public boolean isBadaProject(IProject p) {
		ICProjectDescription prjd = CoreModel.getDefault()
				.getProjectDescription(p, false);
		if (prjd == null)
			return false;
		ICConfigurationDescription[] cfgs = prjd.getConfigurations();

		boolean flag = (cfgs != null && cfgs.length > 0);

		if (flag) {
			try {
				flag = p.hasNature(badaNature.OSP_NATURE_ID);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				flag = false;
			}
		}
		return flag;
	}

	protected void contentForConfiguration(Composite composite) {

		// Add a config selection area
		Group configGroup = ControlFactory.createGroup(composite, "", 1);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.grabExcessHorizontalSpace = true;
		gd.widthHint = 150;
		configGroup.setLayoutData(gd);
		configGroup.setLayout(new GridLayout(3, false));

		Label configLabel = new Label(configGroup, SWT.NONE);
		configLabel.setText("Configuration:"); //$NON-NLS-1$
		configLabel.setLayoutData(new GridData(GridData.BEGINNING));

		configSelector = new Combo(configGroup, SWT.READ_ONLY | SWT.DROP_DOWN);
		configSelector.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				handleConfigSelection();
			}
		});
		gd = new GridData(GridData.FILL_BOTH);
		configSelector.setLayoutData(gd);
	}

	public void handleConfigSelectionRTL() {
		handleConfigSelection();
	}

	private void handleConfigSelection() {
		// If there is nothing in config selection widget just bail
		if (configSelector.getItemCount() == 0)
			return;
		int selectionIndex = configSelector.getSelectionIndex();
		if (selectionIndex == -1)
			return;

		String strName = cfgDescs[selectionIndex].getName();
		if (strName.equals(IConstants.CONFIG_SIMUAL_DEBUG_NAME)) {
			// checkSecurity.setEnabled(false);
			libExt = IConstants.EXT_DLL;
		} else {
			// checkSecurity.setEnabled(true);
			libExt = IConstants.EXT_SO;
		}

		cfgChanged(cfgDescs[selectionIndex]);
	}

	private void initConfigurations() {
		IProject prj = getProject();
		// Do nothing in case of Preferences page.
		if (prj == null)
			return;

		// Do not re-read if list already created by another page
		IConfiguration defCfg = null;
		IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(prj);
		cfgDescs = info.getManagedProject().getConfigurations();
		if (cfgDescs == null || cfgDescs.length == 0)
			return;
		defCfg = info.getDefaultConfiguration();

		buildTargetName = info.getBuildArtifactName();
		// Get its extension
		buildTargetExt = info.getBuildArtifactExtension();

		try {
			// try to resolve the build macros in the target extension
			buildTargetExt = ManagedBuildManager.getBuildMacroProvider()
					.resolveValueToMakefileFormat(buildTargetExt,
							"", //$NON-NLS-1$
							" ", //$NON-NLS-1$
							IBuildMacroProvider.CONTEXT_CONFIGURATION,
							info.getDefaultConfiguration());
		} catch (BuildMacroException e) {
			e.printStackTrace();
		}

		try {
			// try to resolve the build macros in the target name
			String resolved = ManagedBuildManager.getBuildMacroProvider()
					.resolveValueToMakefileFormat(buildTargetName,
							"", //$NON-NLS-1$
							" ", //$NON-NLS-1$
							IBuildMacroProvider.CONTEXT_CONFIGURATION,
							info.getDefaultConfiguration());
			if (resolved != null && (resolved = resolved.trim()).length() > 0)
				buildTargetName = resolved;
		} catch (BuildMacroException e) {
			e.printStackTrace();
		}

		// Clear and replace the contents of the selector widget
		configSelector.removeAll();

		int currIdx = 0;
		for (int i = 0; i < cfgDescs.length; ++i) {
			configSelector.add(cfgDescs[i].getName());
			if (defCfg != null
					&& defCfg.getName().equals(cfgDescs[i].getName()))
				currIdx = i;
		}

		configSelector.select(currIdx);

		handleConfigSelection();
	}

	protected void cfgChanged(IConfiguration config) {
		currentConfig = config;
		setDefaultSelect();
		setPackageName();

		checkFiles();
	}

	public String checkFilesRTL() {
		checkFiles();
		IFile exeFile = packageData.getExeFile();
		IFile appXml = packageData.getAppXml();

		if (fileCheckErrorMag.equals("")) {
			if (exeFile == null) {
				return "Exe File Not Found";
			} else if (appXml == null) {
				return "App File Not Found";
			}
		} else {
			return fileCheckErrorMag;
		}
		return null;
	}

	public String getFileCheckErrorMsg() {
		return fileCheckErrorMag;
	}

	private void checkFiles() {
		bFileCheckError = false;
		fileCheckErrorMag = "";

		IFile exeFile = packageData.getExeFile();
		IFile appXml = packageData.getAppXml();

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

		updateWidgetEnablements();
	}

	private void setPackageName() {
		if (fProject != null) {
			textPkgName.setText(fProject.getName() + "_"
					+ configSelector.getText());
		}
	}

	private void setOutputFolder() {
		if (fProject != null) {
			textOutFolder.setText(fProject.getLocation().toString());
		}
	}

	public void setDefaultSelectRTL() {
		setDefaultSelect();
	}

	private void setDefaultSelect() {
		resourceGroup.setAllSelections(false);

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

			setSelect(selectedResources);

		} else {
			updateWidgetEnablements();
		}
	}

	protected void updateWidgetEnablements() {

		setMakeButton(false);

		if (bFileCheckError) {
			setErrorMessage(fileCheckErrorMag);
			return;

		}

		if (packageData.getAppXml() == null) {
			setErrorMessage("The project does not have an applicationl.xml file.");
			return;
		}

		if (packageData.getExeFile() == null) {
			setErrorMessage("The project does not have an .exe file.");
			return;
		}

		if (packageData.getManifestXml() == null) {
			setErrorMessage("The project does not have a manifest.xml file.");
			return;
		}

		if (textPkgName.getText().trim().length() <= 0) {
			setErrorMessage("You must define a name for the package."); //$NON-NLS-1$
			return;

		}

		String outFolderPath = textOutFolder.getText().trim();
		if (outFolderPath.length() == 0) {
			setErrorMessage("You must define an output folder for the package."); //$NON-NLS-1$
			return;
		}

		if (outFolderPath.equals(".") || outFolderPath.equals("..")) { //$NON-NLS-1$ //$NON-NLS-2$
			setErrorMessage("You must define an output folder for the package."); //$NON-NLS-1$
			return;
		}

		IPath binOutFolderPath = new Path(outFolderPath);
		if (!binOutFolderPath.toFile().exists()) {
			setErrorMessage("The output folder does not exist. Create or redefine the folder."); //$NON-NLS-1$
			return;
		}

		setErrorMessage(null);
		setMakeButton(true);
	}

	protected void outFolderChanged() {
		updateWidgetEnablements();
	}

	private void setMakeButton(boolean enabled) {
		if (makeButton != null)
			makeButton.setEnabled(enabled);
		else
			makeButtonEnabled = enabled;
	}

	protected void setSelect(List sel) {
		if (resourceGroup == null)
			return;

		resourceGroup.setAllSelections(false);

		Iterator it = sel.iterator();
		while (it.hasNext()) {
			IResource currentResource = (IResource) it.next();
			if (currentResource.getType() == IResource.FILE) {
				this.resourceGroup.initialCheckListItem(currentResource);
			} else {
				this.resourceGroup.initialCheckTreeItem(currentResource);
			}
		}
		updateWidgetEnablements();
	}

	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		String s = null;
		if (!isManifestFileValid(fProject)) {
			// 서버가 접속 되어있으며 manifest.xml이 다른 경우
			if (fileCheckErrorMag.equals(ERR_MSG_MANIFEST_CHECK_FALSE)) {
				s = "The project manifest file is not identical with the manifest file created for the application in bada Developers."; //$NON-NLS-1$
				MessageDialog.openInformation(parent.getShell(), "Error", s);
				return;
				// 서버가 접속이 되어있지 않은 경우
			} else if (fileCheckErrorMag
					.equals(ERR_MSG_MANIFEST_CHECK_NOTFOUND)) {
				s = "Cannot find the project manifest file in bada Developers.";
				MessageDialog.openInformation(parent.getShell(), "Error", s);
				return;
			} else if (fileCheckErrorMag
					.equals(ERR_MSG_MANIFEST_CHECK_USERCANCEL)) {
				// s = "Cancelled.";
				// MessageDialog.openInformation(parent.getShell(), "Error", s);
				return;
			} else {
				s = "The manifest file cannot be validated due to a network problem.";
			}
		}

		// if(s != null)
		// noContentOnPage = true;
		//		
		// if( noContentOnPage ){
		// System.out.println(s);
		// // this.cancelPressed();
		// }

		PressOK(s, null);
	}

	public void PressOK(String s, String path) {
		List items = resourceGroup.getAllCheckedItems();
		if (folderIcon != null) {
			items.add(folderIcon);
		}

		packageData.setExportResources(items);

		String outFileName = textPkgName.getText().trim();
		if (!outFileName.toLowerCase(Locale.getDefault()).endsWith(
				IConstants.EXT_PACKAGE))
			outFileName += IConstants.EXT_PACKAGE;

		if (path != null)
			outFilePath = path + IConstants.FILE_SEP_BSLASH + outFileName;
		else
			outFilePath = textOutFolder.getText().trim()
					+ IConstants.FILE_SEP_BSLASH + outFileName;

		if (path == null)
			if ((new File(outFilePath).exists())) {
				if (!MessageDialog.openConfirm(getShell(),
						"Confirm File Overwrite", "Package file " + outFileName
								+ " exists. Do you want to overwrite?")) {
					return;
				}
			}

		// boolean isSecurityChecked = checkSecurity.getSelection();
		// if
		// (currentConfig.getName().equals(IConstants.CONFIG_SIMUAL_DEBUG_NAME))
		// isSecurityChecked = false;

		packageData.setProject(fProject);
		packageData.setConfigName(currentConfig.getName());
		// packageData.setCheckSecurity(isSecurityChecked);
		setLibaryList();

		if (helper.executeExportOperation(new PackageArchiveExportOperation(null,
				outFilePath, packageData))) {
			if (s != null)
				MessageDialog.openInformation(parent.getShell(), "Error", s);
			super.okPressed();
		}
	}

	public String getoutFilePath() {
		return outFilePath;
	}

	private void setLibaryList() {
		List list = new ArrayList<IFile>();

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

	protected boolean executeExportOperation(PackageArchiveExportOperation op) {
		op.setUseCompression(true);

		try {
			new ProgressMonitorDialog(getShell()).run(false, true, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			MessageDialog.openError(getShell(), "Internal Error for Export", e
					.getTargetException().getMessage());
			return false;
		}

		IStatus status = op.getStatus();
		if (!status.isOK()) {
			ErrorDialog.openError(getShell(), "Export error", null, // no
																	// special
																	// message
					status);
			return false;
		}

		if (fProject != null) {
			try {
				fProject.refreshLocal(IResource.DEPTH_ONE, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return true;
	}

	private boolean isManifestFileValid(IProject prj) {

		IFile maniFile = prj.getFile(IConstants.MANIFEST_FILE);

		if (!maniFile.exists()) {
			bFileCheckError = true;
			fileCheckErrorMag = "Manifest file does not exist.";
		}

		String url = "http://developer.bada.com/apis/tools/sdk/compareManifest.do?xml="
				+ URLEncoder.encode(FileUtil.loadFromFile(maniFile
						.getLocation().toOSString()));

		// 서버가 접속 되었으나 manifest.xml이 동일하지 않은 경우
		int result = getContents(url);
		if (result == MANIFEST_CHECK_FALSE) {
			bFileCheckError = true;
			fileCheckErrorMag = ERR_MSG_MANIFEST_CHECK_FALSE;

			return false;
			// 서버가 접속 되어있지 않은 경우
		} else if (result == MANIFEST_CHECK_OFFLINE) {
			bFileCheckError = true;
			fileCheckErrorMag = ERR_MSG_MANIFEST_CHECK_OFFLINE;

			return false;
		} else if (result == MANIFEST_CHECK_NOTFOUND) {
			bFileCheckError = true;
			fileCheckErrorMag = ERR_MSG_MANIFEST_CHECK_NOTFOUND;

			return false;
		} else if (result == MANIFEST_CHECK_USERCANCEL) {
			bFileCheckError = true;
			fileCheckErrorMag = ERR_MSG_MANIFEST_CHECK_USERCANCEL;

			return false;
		}

		return true;
	}

	private Display getStandardDisplay() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

	private int getContents(String surl) {

		final CheckManifestOperaton oper = new CheckManifestOperaton(surl);
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

		return oper.getResult();
	}
	/*
	 * private int getContents(String surl){
	 * 
	 * URL url; try { url = new URL(surl); URLConnection conn =
	 * url.openConnection(); HttpURLConnection hurl = (HttpURLConnection) conn;
	 * hurl.setRequestProperty("Content-Type",
	 * "application/x-www-form-urlencoded"); hurl.setRequestMethod("POST");
	 * hurl.setDoOutput(true); hurl.setDoInput(true); hurl.setUseCaches(false);
	 * hurl.setDefaultUseCaches(false); boolean flag = false; // 서버가 접속이 되었는지 되지
	 * 않았는지 체크합니다. String cookie = null; // 서버에서 받은 답의0번 헤더를 가져옵니다. String
	 * bufferline = null; // 서버에서 받은 답변을 buffer에서 한줄 씩 가져오기 위한 문자열입니다. String
	 * manifestvalid = ""; // 서버에서 받은 답변의 바디 부분을 모은 문자열입니다. String onlinecheck =
	 * ""; // 현재 서버가 접속이 되었는지 되지 않았는지 체크하기 위해 0번 헤더의 // 토큰을 하나씩 비교하기 위한 문자열입니다.
	 * cookie = conn.getHeaderField(0); StringTokenizer stk = new
	 * StringTokenizer(cookie, " "); // 가져온 0번 헤더의 토큰을 " " 으로 구분합니다.
	 * while(stk.hasMoreElements()) { String str1 = stk.nextToken();
	 * if(str1.equals("200")){ flag = !flag; } } BufferedReader in = new
	 * BufferedReader(new InputStreamReader(hurl.getInputStream()));
	 * while((bufferline = in.readLine()) != null){ manifestvalid =
	 * manifestvalid.concat(bufferline); } // System.out.println(str2); // 받아온
	 * 문서의 내용 테스트 용입니다. // System.out.println(flag); if(flag == false){ // 서버의
	 * 응답이 200이 아닌 경우입니다. return MANIFEST_CHECK_OFFLINE; }else
	 * if(manifestvalid.equals("true")){ // 서버가 접속 되어 있으며 manifest.xml이 서버와 동일한
	 * 경우 return MANIFEST_CHECK_TRUE; }else if(manifestvalid.equals("false")){
	 * // 서버도 접속 되지 않고 manifest.xml도 서버와 다른 경우 return MANIFEST_CHECK_FALSE;
	 * }else if(manifestvalid.equals("NotFound")){ // 서버에서 동일한 id의 manifest.xml을
	 * 찾을 수 없는 경우 return MANIFEST_CHECK_NOTFOUND; } // 받아오는 헤더의 정보가 아닌 바디의 정보가
	 * // true/false의 값으로 넘어오기 때문에 더이상 // 헤더는 필요 없습니다. // boolean flag = false;
	 * // 문자열을 비교하여 바로 true/false를 반환 합니다. // String cookie = null; // String
	 * str3 = ""; // cookie = conn.getHeaderField(0); // StringTokenizer stk =
	 * new StringTokenizer(cookie, " "); // while(stk.hasMoreElements()) // { //
	 * String str1 = stk.nextToken(); // if(str1.equals("200")){ // flag =
	 * !flag; // } // } // System.out.println(cookie); // 0번 헤더 내용 출력 } catch
	 * (MalformedURLException e1) { // TODO Auto-generated catch block
	 * e1.printStackTrace(); } catch (IOException e) { } catch (Exception e){ }
	 * return MANIFEST_CHECK_OFFLINE; // 페이지가 존재하지 않는 경우입니다. // 위의 try/catch문에서
	 * NullPointExeption이 불려옵니다. // 때문에 Exception을 통해서 try문을 무시하게 됩니다. }
	 */

}

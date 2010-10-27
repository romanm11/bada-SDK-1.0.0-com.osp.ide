package com.osp.ide;


public class IConstants {

	public static final String ID_OSP_PROJECT_CATEGORY = "com.osp.ide.newWizardCategory";
	//public static final String ID_NEW_OSP_PROJECT_WIZARD = "com.osp.ide.wizards.newOspCCwizard";
	
	public static final String IMG_CHECK="icons/obj16/checked.gif";
	public static final String IMG_UNCHECK="icons/obj16/unchecked.gif";
	
	public static final String IMG_WIZARD="icons/wizard/app_wizard.bmp";
	public static final String IMG_WIZARD_BG="icons/wizard/bada_wizard.png"; 
	
	public static final String FILE_SEP_FSLASH="/";
	public static final String FILE_SEP_BSLASH="/";		//"\\";
	public static final String COLON=":";
	public static final String DOT=".";
	
//	public static final String ENV_OSPROOT="OSPROOT";
//	public static final String ENV_OSPROOT_VAR="${OSPROOT}";
	
	public static final String ENV_BADA_SDK_HOME = "BADA_SDK_HOME";
	public static final String ENV_SDKROOT="SDKROOT";
	public static final String ENV_SDKROOT_VAR="${SDKROOT}";
	public static final String ENV_PROJECT_ROOT="PROJECT_ROOT";
	public static final String ENV_PROJECT_ROOT_VAR="${PROJECT_ROOT}";

	
	public static final String ENV_APPLICATION_ID="${APPLICATION_ID}";
	public static final String ENV_PROJECT_NAME="${PROJECT_NAME}";
	
	public static final String ENV_TARGRT_LIB_PATH="TARGET_LIB_PATH";
	public static final String ENV_TARGRT_LIB_PATH_VAR="${TARGET_LIB_PATH}";	

	public static final String ENV_SIMULATOR_LIB_PATH="SIMULATOR_LIB_PATH";
	public static final String ENV_SIMULATOR_LIB_PATH_VAR="${SIMULATOR_LIB_PATH}";
	
	public static final String ENV_MODEL_NAME="MODEL_NAME";
	public static final String ENV_MODEL_NAME_VAR="${MODEL_NAME}";	
	
	
	public static final String ENV_SCREEN_DIR="SCREEN_DIR";
	public static final String ENV_SCREEN_DIR_VAR="${SCREEN_DIR}";
	
//	public static final String PATH_TARGRT_LIB = "\\lib\\S8500";
//	public static final String PATH_SIMUAL_LIB = "\\lib\\WinSgpp";
	//public static final String PATH_TOOLS = "\\IDE\\Tools";
	//public static final String DIR_IDE = "\\IDE";
	public static final String PATH_TOOLS = java.io.File.separatorChar + "Tools";
	public static final String DIR_IDE = "";
    public static final String PATH_BROKER = PATH_TOOLS + java.io.File.separatorChar + "Broker";
	
	public static final String CONFIGSTORE_FILE=".badaprj";
	
	public static final String APP_XML_FILE="application.xml";
	public static final String DEVINFO_FILE="default.dbi";
	public static final String KEYPAD_STR="Qwerty";
	public static final String MANIFEST_FILE="manifest.xml";
	
    public static final String FILE_DESC_TXT = "Description.txt";
    
	public static final String FILE_NON_PREV_XML = "non-priv.xml";
	public static final String FILE_NON_OPEN_XML = "non-open.xml";
	public static final String FILE_EXTERNAL_XML = "external.xml";
	public static final String FILE_VULNERABLE_STRING_XML = "vulnerable_string.xml";
	public static final String FILE_SUSPICIOUS_CALL_XML = "suspicious_call.xml";
	public static final String FILE_VULNERABLE_API_XML = "vulnerable_api.xml";
	public static final String FILE_SIGNATURE_XML = "signature.xml";
	public static final String FILE_UNUSED_PRIV_XML = "unused-priv.xml";
	public static final String FILE_SIGNATURE_ANAL_RESULT_XML = "signature_analysis_result.xml";
	
	public static final String FILE_VULNERABLE_FUNC_LIST_XML = "bada_vulnerable_function_list.xml";
	public static final String FILE_STRING_LIST_XML = "bada_string_list.xml";
	public static final String FILE_TARGET_FUNC_LIST_XML = "bada_target_function_list.xml";
	public static final String FILE_FUNCTIONS_FUNC_XML = "functions_func.xml";
	public static final String FILE_PRIVGROUP_XML = "privgroup.xml";
	
	public static final String PREFIX_CONFIG_DIR=".";
	public static final String CONFIG_SIMUAL_DEBUG_NAME="Simulator-Debug";
	public static final String CONFIG_SIMUAL_DEBUG_DIR=".Simulator-Debug";
	public static final String CONFIG_TARGET_DIR_PREFIX=".Target-";
	public static final String CONFIG_TARGET_NAME_PREFIX="Target-";
	public static final String CONFIG_TARGET_DEBUG_NAME="Target-Debug";
	public static final String CONFIG_TARGET_RELEASE_NAME="Target-Release";
	
	public static final String BADA_APP_DIR_BIN="Bin";
	public static final String BADA_APP_DIR_INFO="Info";
	public static final String BADA_APP_DIR_DATA="Data";
	//public static final String BADA_APP_DIR_RES="Data\\Res";
	public static final String BADA_APP_DIR_RES="Res";
	
	public static final String DEFAULT_FORM_NAME = "Form1";

    public static final int PRJ_TYPE_APP_FRAME = 0;
    public static final int PRJ_TYPE_APP_FORM = 1;
    public static final int PRJ_TYPE_LIB_STATIC = 2;
    public static final int PRJ_TYPE_LIB_SHARED = 3;
    public static final int PRJ_TYPE_APP_EMPTY = 4;
    
    public static final int PRJ_TYPE_APP_TREE = 10;
    public static final int PRJ_TYPE_LIB_TREE = 11;
    
    public static final String DIR_MODEL = "Model";
    public static final String DIR_SOURCE = "src";
    public static final String DIR_RESOURCE = "Res";
    public static final String DIR_LIB = "lib";
    public static final String DIR_HOME = "Home";
//    public static final String DIR_FORM_XML = DIR_RESOURCE + FILE_SEP_FSLASH + "480x800";
    public static final String DIR_ICON = "Icons";
    public static final String DIR_INCLUDE = "inc";
    public static final String DIR_JS = "js";
    public static final String EXT_CC = ".cpp";
    public static final String EXT_HEADER = ".h";
    public static final String EXT_JS = ".js";
    public static final String EXT_ICON = ".png";
    public static final String EXT_XML = ".xml";
    public static final String EXT_PACKAGE = ".zip";
    public static final String EXT_HTB = ".htb";
    public static final String EXT_DLL = ".dll";
    public static final String EXT_SO = ".so";
    public static final String PREFIX_FORM_XML="IDF_";
    public static final String DIR_SHARE = "share";
    public static final String DIR_SIMULATOR = "Simulator";
    public static final String DIR_TARGET = "Target";
    public static final String DIR_REPOSITORY ="repository";
    
    public static final String DIR_DEBUGINFO ="DebugInfo";

    
    public static final String TEMPLATE_DIR = "templates";
    public static final String TEMPLATE_CC_NAME = "app_classname.cpp";
    public static final String TEMPLATE_ENTRY_CC_NAME_POSTFIX = "Entry.cpp";
    public static final String TEMPLATE_ENTRY_CC_NAME = "entry.cpp";
    public static final String TEMPLATE_DLL_PROJECT_CC_NAME = "Projectname2.cpp";
    public static final String TEMPLATE_DLL_PROJECT__NAME = "Projectname2.h";

    public static final String TEMPLATE_FORM_CC_NAME = "Form1.cpp";
    public static final String TEMPLATE_FORM_HEADER_NAME = "Form1.h";
//    public static final String TEMPLATE_ENGLISH_XML_NAME = "English.xml";
    public static final String TEMPLATE_FORM_XML_NAME = "IDF_FORM1.xml";
    
    
    public static final String TEMPLATE_CCHEADER_NAME = "app_classname.h";
    public static final String TEMPLATE_PROJECT_ICON_NAME = "projectname.png";
    public static final String TEMPLATE_SPLASH_ICON_NAME = "Splash.png";
    
    public static final String TEMPLATE_COMMON_DIR = TEMPLATE_DIR+"/common";
    public static final String TEMPLATE_PROJECT_ICON_FILE = TEMPLATE_COMMON_DIR+FILE_SEP_FSLASH+TEMPLATE_PROJECT_ICON_NAME;
    public static final String TEMPLATE_SPLASH_ICON_FILE = TEMPLATE_COMMON_DIR+FILE_SEP_FSLASH+TEMPLATE_SPLASH_ICON_NAME;

    
    public static final String TEMPLATE_FRAMEAPP_DIR = TEMPLATE_DIR+"/FrameAppCCProject";
    public static final String TEMPLATE_FRAMEAPP_CCFILE = TEMPLATE_FRAMEAPP_DIR+FILE_SEP_FSLASH+TEMPLATE_CC_NAME;
    public static final String TEMPLATE_FRAMEAPP_CCHEADER = TEMPLATE_FRAMEAPP_DIR+FILE_SEP_FSLASH+TEMPLATE_CCHEADER_NAME;
    public static final String TEMPLATE_FRAMEAPP_CCENTRY = TEMPLATE_FRAMEAPP_DIR+FILE_SEP_FSLASH+TEMPLATE_ENTRY_CC_NAME;


    public static final String TEMPLATE_FORMAPP_DIR = TEMPLATE_DIR+"/FormAppCCProject";
    public static final String TEMPLATE_FORMAPP_CCFILE = TEMPLATE_FORMAPP_DIR+FILE_SEP_FSLASH+TEMPLATE_CC_NAME;
    public static final String TEMPLATE_FORMAPP_CCHEADER = TEMPLATE_FORMAPP_DIR+FILE_SEP_FSLASH+TEMPLATE_CCHEADER_NAME;
    public static final String TEMPLATE_FORMAPP_CCENTRY = TEMPLATE_FORMAPP_DIR+FILE_SEP_FSLASH+TEMPLATE_ENTRY_CC_NAME;
    public static final String TEMPLATE_FORMAPP_CCFORM = TEMPLATE_FORMAPP_DIR+FILE_SEP_FSLASH+TEMPLATE_FORM_CC_NAME;
    public static final String TEMPLATE_FORMAPP_CCFORMHEADER = TEMPLATE_FORMAPP_DIR+FILE_SEP_FSLASH+TEMPLATE_FORM_HEADER_NAME;
//    public static final String TEMPLATE_FORMAPP_ENGLISH_XML = TEMPLATE_FORMAPP_DIR+FILE_SEP_FSLASH+TEMPLATE_ENGLISH_XML_NAME;
//    public static final String TEMPLATE_FORMAPP_FORM_XML = TEMPLATE_FORMAPP_DIR+FILE_SEP_FSLASH+TEMPLATE_FORM_XML_NAME;
    public static final String[] DEFAULT_SCREEN_SIZE= {"240x400", "480x800"};
    
    public static final String TEMPLATE_DLL_DIR = TEMPLATE_DIR+"/DllProject";
    public static final String TEMPLATE_DLL_CCENTRY = TEMPLATE_DLL_DIR+FILE_SEP_FSLASH+TEMPLATE_ENTRY_CC_NAME;
    public static final String TEMPLATE_DLL_CCPROJECT = TEMPLATE_DLL_DIR+FILE_SEP_FSLASH+TEMPLATE_DLL_PROJECT_CC_NAME;
    public static final String TEMPLATE_DLL_CCPROJECT_H = TEMPLATE_DLL_DIR+FILE_SEP_FSLASH+TEMPLATE_DLL_PROJECT__NAME;
    
	public static final String ARTIFACT = "org.eclipse.cdt.build.core.buildArtefactType";  //$NON-NLS-1$
	public static final String ARTIFACT_EXE = "org.eclipse.cdt.build.core.buildArtefactType.exe";
	public static final String ARTIFACT_SHAREDLIB = "org.eclipse.cdt.build.core.buildArtefactType.sharedLib";
	public static final String ARTIFACT_STATICLIB = "org.eclipse.cdt.build.core.buildArtefactType.staticLib";
	
	
	public static final String VIEW_ID_OUTPUT = "com.osp.ide.message.view.Output";
//	public static final String VIEW_ID_OSP_HEAP = "com.osp.ide.message.view.rm.heap";
//	public static final String VIEW_ID_OSP_THREAD = "com.osp.ide.message.view.rm.thread";
	public static final String VIEW_ID_RESOURCE_MONITOR = "com.osp.ide.message.view.ResourceMonitor"; 
	public static final String VIEW_ID_FILE_EXPLORER = "com.osp.ide.fileexplorer.views.FileExplorer";
	public static final String VIEW_ID_RESOURCE_EXPLORER = "com.osp.ide.resource.resourceexplorer.ResourceExplorer";
	public static final String VIEW_ID_SAMPLE_BROWER = "com.osp.samplebrowser.views.SampleView";
	
	public static final String CDT_C_NATURE = "org.eclipse.cdt.core.cnature";
	public static final String CDT_CC_NATURE = "org.eclipse.cdt.core.ccnature";
	

	public static final String PREP_ID_DEVELOPER_SITE = "Developer Site";
	public static final String PREP_ID_SIMULATOR_PORT = "Simulator Port";
	public static final String PREP_ID_MANIFEST_PATH = "Manifest Path";
	public static final String PREP_ID_LAST_PRJ_TYPE = "Last Project Type";
	public static final String PREP_ID_SHOW_BROWSER = "Show Browser";

	public static final String DEFAULT_DEVELOPER_SITE = "http://developer.bada.com/member/loginForm.do";
	public static final String DEFAULT_OSP_ROOT = "C:\\OSP";
	public static final int DEFAULT_SIMULATOR_PORT = 6200;
	public static final int DEFAULT_LAST_PRJ_TYPE=IConstants.PRJ_TYPE_APP_FRAME;
	public static final boolean DEFAULT_SHOW_BROWSER = false;
	
	public static final int WIZARD_PAGE_WIDTH = 525;
	public static final int WIZARD_PAGE_HEIGHT = 520;
	
	public static final String[] FORM_CLASS = {"Form"};
	public static final int INX_FORM_CLASS_DEFAULT = 0;
	
	public static final String[] SCREEN_DIR_NAME = {"WVGA", "WQVGA"};
	public static final int  SCREEN_WVGA = 0;
	public static final int  SCREEN_WQVGA = 1;
	public static final String STR_SCREEN_WVGA = "480x800";
	public static final String STR_SCREEN_WQVGA = "240x400";

	public static final String BROKER_FILENAME="Broker.exe";
	public static final String DEFAULT_TARGET_CODE_BINARY_PATH = "/Osp/Applications";	
}

package com.osp.ide.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.osp.ide.IConstants;


public class ManifestXmlStore {

	public static final String ELEMENT_NAME = "Manifest"; //$NON-NLS-1$
	
	public static final String ATTR_ID = "Id"; //$NON-NLS-1$
	public static final String ATTR_SECRET = "Secret"; //$NON-NLS-1$
	public static final String ATTR_APPVERSION = "AppVersion"; //$NON-NLS-1$
	
	public static final String ATTR_DEVICEPROFILE = "DeviceProfile"; //$NON-NLS-1$
	public static final String ATTR_DP_MINIMUMHEAPSIZE = "MinimumHeapSize"; //$NON-NLS-1$
	public static final String ATTR_DP_MINIMUMVRAMSIZE = "MinimumVramSize"; //$NON-NLS-1$
	public static final String ATTR_DP_3DACCELATOR = "Accelerator3D"; //$NON-NLS-1$
	public static final String ATTR_DP_CPU = "CPU"; //$NON-NLS-1$
	public static final String ATTR_DP_BOLUETOOTHPROFILE = "BluetoothProfile"; //$NON-NLS-1$
	
	public static final String ATTR_DP_WIFIMODE = "WiFiMode"; //$NON-NLS-1$
	public static final String ATTR_DP_SENSOR = "Sensor"; //$NON-NLS-1$
	
	public static final String ATTR_DP_SCREENSIZE = "ScreenSize"; //$NON-NLS-1$
//	public static final String ATTR_DP_INPUTDEVICE = "InputDevice"; //$NON-NLS-1$
	public static final String ATTR_DP_SOUNDMIXING = "SoundMixing"; //$NON-NLS-1$
	
	public static final String ATTR_PRIVILIEGES = "Privileges"; //$NON-NLS-1$
	public static final String ATTR_PRI_PRIVILIEGE = "Privilege"; //$NON-NLS-1$
	public static final String ATTR_PRI_PRIVILIEGE_NAME = "Name"; //$NON-NLS-1$
//	public static final String ATTR_PRI_PRIVILIEGE_GROUPID = "GroupId"; //$NON-NLS-1$
	
	public static final String ATTR_AGENT = "Agent"; //$NON-NLS-1$
	public static final String ATTR_AG_LAUNCHONBOOT = "LaunchOnBoot"; //$NON-NLS-1$
	
	
	private String filePath="";
	
	private String m_Id="";
	private String m_Secret="";
	private String m_AppVersion="1.0.0";
	private String m_MinimumHeapSize="";
	private String m_MinimumVramSize="";
	private String m_Cpu="";
	
	private String m_SoundMixing ="";
	private String m_LaunchOnBoot="";
	
	
	ArrayList<PrivilegeData> privilegeList = new ArrayList<PrivilegeData>();
	ArrayList<String> accel3dList = new ArrayList<String>();
	ArrayList<String> wifiModeList = new ArrayList<String>();
	ArrayList<String> bluetoothProfileList = new ArrayList<String>();
	ArrayList<String> sensorList = new ArrayList<String>();
//	ArrayList<String> inputDeviceList = new ArrayList<String>();
	ArrayList<String> screenSizeList = new ArrayList<String>();
	
	int screenResol = IConstants.SCREEN_WVGA;
	
	
	public ManifestXmlStore(String path)
	{
		filePath = path;
		loadXML(filePath);
	}
	
	public ManifestXmlStore(IProject project)
	{
		if( project ==null ) 
		{
			clear();
			return;
		}
		
		filePath = project.getLocation().append(IConstants.MANIFEST_FILE).toOSString();
		loadXML(filePath);
	}	
	
	
	private boolean loadXML(String path)
	{
		clear();
		if(path == null || path.length()==0) return false;
		
		FileInputStream stream = null;
		try {
			File file = new File(path);
			if (!file.exists()) return false;

			stream = new FileInputStream(file);
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		    DocumentBuilder  db = dbf.newDocumentBuilder();
		    Document dom = db.parse(stream);
		    
		    Element root = dom.getDocumentElement(); 
		    String rootName = root.getNodeName();
		    if( rootName.equals(ELEMENT_NAME))
		    {
		    	for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
		    		String name = node.getNodeName();
		    		if( node.getFirstChild() != null)
		    		{
		    			String value = node.getFirstChild().getNodeValue();
		    			if( name.equals(ATTR_ID)) m_Id = value;
		    			if( name.equals(ATTR_SECRET)) m_Secret = value;
		    			if( name.equals(ATTR_APPVERSION)) m_AppVersion = value;		    			
		    			else if( name.equals(ATTR_DEVICEPROFILE))
		    			{
		    				for (Node nodeProfile = node.getFirstChild(); nodeProfile != null; nodeProfile = nodeProfile.getNextSibling()) {
		    					String nameProfileNode = nodeProfile.getNodeName();
		    		    		if( nodeProfile.getFirstChild() != null)
		    		    		{
		    		    			String valueProfileNode = nodeProfile.getFirstChild().getNodeValue();
		    		    			if( nameProfileNode.equals(ATTR_DP_MINIMUMHEAPSIZE)) m_MinimumHeapSize = valueProfileNode;
		    		    			else if( nameProfileNode.equals(ATTR_DP_MINIMUMVRAMSIZE)) m_MinimumVramSize = valueProfileNode;
		    		    			else if( nameProfileNode.equals(ATTR_DP_CPU)) m_Cpu = valueProfileNode;
		    		    			else if( nameProfileNode.equals(ATTR_DP_SCREENSIZE)) screenSizeList.add(valueProfileNode);
//		    		    			else if( nameProfileNode.equals(ATTR_DP_INPUTDEVICE)) inputDeviceList.add(valueProfileNode);
		    		    			else if( nameProfileNode.equals(ATTR_DP_3DACCELATOR)) accel3dList.add(valueProfileNode);
		    		    			else if( nameProfileNode.equals(ATTR_DP_BOLUETOOTHPROFILE)) bluetoothProfileList.add(valueProfileNode);
		    		    			else if( nameProfileNode.equals(ATTR_DP_WIFIMODE)) wifiModeList.add(valueProfileNode);
		    		    			else if( nameProfileNode.equals(ATTR_DP_SENSOR)) sensorList.add(valueProfileNode);
		    		    			else if( nameProfileNode.equals(ATTR_DP_SOUNDMIXING)) m_SoundMixing = valueProfileNode;
		    		    		}
		    				}
		    			}
		    			else if( name.equals(ATTR_PRIVILIEGES))
		    			{
		    				for (Node nodePriv = node.getFirstChild(); nodePriv != null; nodePriv = nodePriv.getNextSibling()) {
		    					String namePrivNode = nodePriv.getNodeName();
		    		    		if( nodePriv.getFirstChild() != null)
		    		    		{
		    		    			if( namePrivNode.equals(ATTR_PRI_PRIVILIEGE))
		    		    			{
		    		    				PrivilegeData data = new PrivilegeData();
		    		    				for (Node nodePrivData = nodePriv.getFirstChild(); nodePrivData != null; nodePrivData = nodePrivData.getNextSibling()) {
		    		    					String namePrivDataNode = nodePrivData.getNodeName();
		    		    		    		if( nodePrivData.getFirstChild() != null)
		    		    		    		{
		    		    		    			String valuePrviDataNode = nodePrivData.getFirstChild().getNodeValue();
		    		    		    			if( namePrivDataNode.equals(ATTR_PRI_PRIVILIEGE_NAME))
		    		    		    			{
		    		    		    				data.setName(valuePrviDataNode);
		    		    		    			}
/*		    		    		    			else if(namePrivDataNode.equals(ATTR_PRI_PRIVILIEGE_GROUPID))
		    		    		    			{
		    		    		    				data.setGroupId(valuePrviDataNode);
		    		    		    			}
*/		    		    		    			
		    		    		    		}
		    		    				}
		    		    				
		    		    				if(data.getName().length() > 0)
		    		    				{
		    		    					privilegeList.add(data);
		    		    				}
		    		    			}
		    		    		}
		    				}
		    			}
		    			else if( name.equals(ATTR_AGENT))
		    			{
		    				for (Node nodeAgent = node.getFirstChild(); nodeAgent != null; nodeAgent = nodeAgent.getNextSibling()) {
		    					String nameAgentNode = nodeAgent.getNodeName();
		    		    		if( nodeAgent.getFirstChild() != null)
		    		    		{
		    		    			String valueAgentNode = nodeAgent.getFirstChild().getNodeValue();
		    		    			if( nameAgentNode.equals(ATTR_AG_LAUNCHONBOOT)) m_LaunchOnBoot = valueAgentNode;
		    		    		}
		    				}
		    			}
		    		}
		    	}
		    }
		    
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if( stream != null )
				try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
				
				if( screenSizeList.size() > 0 )
				{
					String strSize = screenSizeList.get(0);
					if( strSize.toLowerCase(Locale.getDefault()).equals( IConstants.STR_SCREEN_WQVGA))
						screenResol = IConstants.SCREEN_WQVGA;
				}					
		}
	
		return true;
	}
	
	
	 
	public void clear()
	{
		m_Id="";
		m_Secret="";
		m_AppVersion="1.0.0";
		m_MinimumHeapSize="";
		m_MinimumVramSize="";
		m_Cpu="";
		m_SoundMixing = "";
		m_LaunchOnBoot = "";

		bluetoothProfileList.clear();
		screenSizeList.clear();
		privilegeList.clear();
		accel3dList.clear();
		wifiModeList.clear();
//		inputDeviceList.clear();		
		sensorList.clear();
	}
	
	public String getId()
	{
		return m_Id;
	}
	
	public String getSecret()
	{
		return m_Secret;
	}	

	public String getAppVersion()
	{
		return m_AppVersion;
	}	
	
	public String getMinimumHeapSize()
	{
		return m_MinimumHeapSize;
	}
	public String getMinimumVramSize()
	{
		return m_MinimumVramSize;
	}	
	
	public String getCpu()
	{
		return m_Cpu;
	}
	
	public String getSoundMixing()
	{
		return m_SoundMixing;
	}	

	public String getLaunchOnBoot()
	{
		return m_LaunchOnBoot;
	}	
	
	public int getScreenResolution()
	{
		return screenResol;
	}
	
	public String[] getScreenSizes()
	{
		if( screenSizeList.size() == 0 ) return new String[0];
	
		return (String[])screenSizeList.toArray(new String[screenSizeList.size()]);
	}
	

//	public String[] getInputDevices()
//	{
//		if( inputDeviceList.size() == 0 ) return new String[0];
//		
//		return (String[])inputDeviceList.toArray(new String[inputDeviceList.size()]);
//	}

	public String[] getBoluetoothProfiles()
	{
		if( bluetoothProfileList.size() == 0 ) return new String[0];
		
		return (String[])bluetoothProfileList.toArray(new String[bluetoothProfileList.size()]);
	}
	
//	public boolean isTouchSupport()
//	{
//		if( inputDeviceList.size() == 0 ) return false;
//		
//		if( inputDeviceList.contains("Touch")) return true;
//		
//		return false;
//	}	

	
	public String[] get3DAccelerators()
	{
		if( accel3dList.size() == 0 ) return new String[0];
		
		return (String[])accel3dList.toArray(new String[accel3dList.size()]);
	}
	
	public String[] getWifiModes()
	{
		if( wifiModeList.size() == 0 ) return new String[0];
		
		return (String[])wifiModeList.toArray(new String[wifiModeList.size()]);
	}	
	
	public String[] getSensors()
	{
		if( sensorList.size() == 0 ) return new String[0];
		
		return (String[])sensorList.toArray(new String[sensorList.size()]);
	}		
	
	
	
	public PrivilegeData[] getPrivileges()
	{
		if( privilegeList.size() == 0 ) return new PrivilegeData[0];
		
		return (PrivilegeData[])privilegeList.toArray(new PrivilegeData[privilegeList.size()]);
	}
	
	public boolean isValid()
	{
		if( m_Id.length() != 10 ) return false;  // ID length 10
		
//		if( m_Framework.length() == 0 || !SDKManager.isSDKExist(m_Framework)) return false; // SDK info check
		
		if( screenSizeList.size() == 0 ) return false;  // Screen size field check
		
//		if( !isTouchSupport() ) return false;  // Touch support field check
		
		return true;
	}
}

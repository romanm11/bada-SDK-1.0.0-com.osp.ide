package com.osp.ide.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.osp.ide.IConstants;
import com.osp.ide.IdePlugin;

public class OspPreferenceInitializer extends AbstractPreferenceInitializer {


	@Override
	public void initializeDefaultPreferences() {
		// TODO Auto-generated method stub
		IPreferenceStore store = IdePlugin.getDefault().getPreferenceStore();
		store.setDefault(IConstants.PREP_ID_DEVELOPER_SITE, IConstants.DEFAULT_DEVELOPER_SITE);
//		store.setDefault(IConstants.ENV_OSPROOT, IConstants.DEFAULT_OSP_ROOT);
		store.setDefault(IConstants.PREP_ID_LAST_PRJ_TYPE, IConstants.DEFAULT_LAST_PRJ_TYPE);

		store.setDefault(IConstants.PREP_ID_SHOW_BROWSER, IConstants.DEFAULT_SHOW_BROWSER);
		store.setDefault(IConstants.PREP_ID_MANIFEST_PATH, "");
	
		store.setDefault(IConstants.PREP_ID_SIMULATOR_PORT, IConstants.DEFAULT_SIMULATOR_PORT);
	}

}



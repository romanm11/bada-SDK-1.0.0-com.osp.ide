package com.osp.ide.makegen.osp;

import java.util.ArrayList;

public class GnuDependencyGroupInfo {
	
	//  Member Variables
	String groupBuildVar;
	boolean conditionallyInclude;
	ArrayList groupFiles;
	
	//  Constructor
	public GnuDependencyGroupInfo(String groupName, boolean bConditionallyInclude) {
		groupBuildVar = groupName;
		conditionallyInclude = bConditionallyInclude;
		//  Note: not yet needed
		groupFiles = null;
	}

}

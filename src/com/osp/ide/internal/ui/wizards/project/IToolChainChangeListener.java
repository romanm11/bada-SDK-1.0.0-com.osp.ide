package com.osp.ide.internal.ui.wizards.project;

import org.eclipse.cdt.managedbuilder.core.IToolChain;

public interface IToolChainChangeListener {
	
	public void toolChainChanged(IToolChain tc);
}

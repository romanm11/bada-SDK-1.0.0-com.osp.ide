package com.osp.ide.core.builder;

import java.util.StringTokenizer;

public class BadaBuildOption {
	String model;
	String compOpt;
	String linkOpt;
	String postfixLinkOpt;
	String[] linkFilters;
	
	public BadaBuildOption()
	{
		model = "";
		compOpt = "";
		linkOpt = "";
		postfixLinkOpt = "";
		linkFilters = null;		
	}

	public void setModel(String model)
	{
		this.model = model;
	}

	public String getModel()
	{
		return model;
	}
	
	public void setCompileOption(String compOpt)
	{
		this.compOpt = compOpt;
	}

	public String getCompileOption()
	{
		return compOpt;
	}
	
	public void setLinkOption(String linkOpt)
	{
		this.linkOpt = linkOpt;
	}
	
	public String getLinkOption()
	{
		return linkOpt;
	}
	
	public void setPostfixLinkOption(String postfixLinkOpt)
	{
		this.postfixLinkOpt = postfixLinkOpt;		
	}
	
	public String getPostfixLinkOption()
	{
		return postfixLinkOpt;
	}
	
	public void setLinkFilter(String linkFilter)
	{
		StringTokenizer st = new StringTokenizer(linkFilter);
		int count = st.countTokens();
		linkFilters = new String[count];
		for (int i = 0; i < count; i++)
			linkFilters[i] = st.nextToken();
	}
	
	public String[] getLinkFilter()
	{
		return linkFilters;
	}
}

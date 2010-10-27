package com.osp.ide.core;

public class AppNameItem {
	String lang;
	String name;

	public AppNameItem(String lang) {
		this.lang = lang;
		this.name = "";
	};
	
	public AppNameItem(String lang, String value) {
		this.lang = lang;
		this.name = value;
	};

	public String getLanugage()
	{
		return lang;
	}	
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public AppNameItem clone()
	{
		AppNameItem item = new AppNameItem(this.lang, this.name);
		
		return item;
	}
	
}

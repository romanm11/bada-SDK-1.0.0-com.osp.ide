package com.osp.ide.core;

public class LanguageData {
	String id;
	String name;

	public LanguageData(String id, String value) {
		this.id = id;
		this.name = value;
	};

	public String getId()
	{
		return id;
	}	
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public LanguageData clone()
	{
		LanguageData item = new LanguageData(this.id, this.name);
		
		return item;
	}
	
}

package com.mike.bombobject;

import android.content.Context;
import cn.bmob.v3.BmobInstallation;

public class MyBmobInstallation extends BmobInstallation {

	String PersonName;//用户名(mac地址)
	
	public MyBmobInstallation(Context arg0) {
		super(arg0);
	}

	public String getPersonName() {
		return PersonName;
	}

	public void setPersonName(String name) {
		this.PersonName = name;
	}
}

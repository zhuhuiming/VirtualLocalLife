package com.mike.bombobject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

public class UserInfo extends BmobUser {
	// �绰����
	String TelPhone;
	// �Ա�
	String Gender;
	// ����
	Integer Age;
	// ͷ��
	BmobFile UserIcon;
	// �û���ס��ַ
	String UserLiveAddress;
	// �û���������Id��(����)
	String UserLiveID;

	public String getTelPhone() {
		return TelPhone;
	}

	public void setTelPhone(String strTel) {
		this.TelPhone = strTel;
	}

	public String getGender() {
		return Gender;
	}

	public void setGender(String strTel) {
		this.Gender = strTel;
	}

	public Integer getAge() {
		return Age;
	}

	public void setAge(Integer value) {
		this.Age = value;
	}

	public BmobFile getUserIcon() {
		return UserIcon;
	}

	public void setUserIcon(BmobFile file) {
		this.UserIcon = file;
	}

	public String getUserLiveAddress() {
		return UserLiveAddress;
	}

	public void setUserLiveAddress(String strTel) {
		this.UserLiveAddress = strTel;
	}

	public String getUserLiveID() {
		return UserLiveID;
	}

	public void setUserLiveID(String strTel) {
		this.UserLiveID = strTel;
	}
}

package com.mike.bombobject;

import cn.bmob.v3.BmobObject;

//������Ϣ��
public class AreaInfo extends BmobObject {

	Integer XIndex;// x�᷽���������
	Integer YIndex;// y�᷽���������
	String ID;// ����id��
	String FindPersonName;// �����˵�mac����
	String AreaName;// ��������
	String DeScribe;// ����������Ϣ
	String LanderOwnerName;// ��������

	public Integer getXIndex() {
		return XIndex;
	}

	public void setXIndex(Integer x) {
		XIndex = x;
	}

	public Integer getYIndex() {
		return YIndex;
	}

	public void setYIndex(Integer x) {
		YIndex = x;
	}

	public String getID() {
		return ID;
	}

	public void setID(String strid) {
		this.ID = strid;
	}

	public String getFindPersonName() {
		return FindPersonName;
	}

	public void setFindPersonName(String strname) {
		this.FindPersonName = strname;
	}

	public String getAreaName() {
		return AreaName;
	}

	public void setDeScribe(String strname) {
		this.DeScribe = strname;
	}

	public String getDeScribe() {
		return DeScribe;
	}

	public void setAreaName(String strname) {
		this.AreaName = strname;
	}

	public String getLanderOwnerName() {
		return LanderOwnerName;
	}

	public void setLanderOwnerName(String strname) {
		this.LanderOwnerName = strname;
	}
}

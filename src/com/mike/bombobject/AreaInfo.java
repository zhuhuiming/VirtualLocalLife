package com.mike.bombobject;

import cn.bmob.v3.BmobObject;

//区域信息表
public class AreaInfo extends BmobObject {

	Integer XIndex;// x轴方向的索引号
	Integer YIndex;// y轴方向的索引号
	String ID;// 区域id号
	String FindPersonName;// 发现人的mac名称
	String AreaName;// 区域名称
	String DeScribe;// 区域描述信息
	String LanderOwnerName;// 地主名称

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

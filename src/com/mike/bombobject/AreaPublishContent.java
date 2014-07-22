package com.mike.bombobject;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

//存储区域发布内容表
public class AreaPublishContent extends BmobObject implements Serializable{
	String TextContent;// 文字内容
	BmobFile FirstImage;// 第一张图片
	BmobFile SecondImage;// 第二张图片
	BmobFile ThirdImage;// 第三张图片
	String PublishPersonName;// 发布人名(MAC)
	String PublishPersonNickName;//发布人昵称
	String PublishAddress;// 发布地点
	Integer ScanTimes;// 浏览次数
	Integer CommentTimes;// 评论次数
	Integer CreditValue;// 赞值
	String AreaID;// 区域id号
	String InstallationId;//设备id号

	public String getTextContent() {
		return TextContent;
	}

	public void setTextContent(String text) {
		this.TextContent = text;
	}

	public BmobFile getFirstImage() {
		return FirstImage;
	}

	public void setFirstImage(BmobFile file) {
		FirstImage = file;
	}

	public BmobFile getSecondImage() {
		return SecondImage;
	}

	public void setSecondImage(BmobFile file) {
		SecondImage = file;
	}

	public BmobFile getThirdImage() {
		return ThirdImage;
	}

	public void setThirdImage(BmobFile file) {
		ThirdImage = file;
	}

	public String getPublishPersonName() {
		return PublishPersonName;
	}

	public void setPublishPersonName(String name) {
		this.PublishPersonName = name;
	}
	
	public String getPublishPersonNickName() {
		return PublishPersonNickName;
	}

	public void setPublishPersonNickName(String name) {
		this.PublishPersonNickName = name;
	}

	public String getPublishAddress() {
		return PublishAddress;
	}

	public void setPublishAddress(String address) {
		this.PublishAddress = address;
	}

	public Integer getScanTimes() {
		return ScanTimes;
	}

	public void setScanTimes(Integer times) {
		ScanTimes = times;
	}

	public Integer getCommentTimes() {
		return CommentTimes;
	}

	public void setCommentTimes(Integer times) {
		CommentTimes = times;
	}

	public Integer getCreditValue() {
		return CreditValue;
	}

	public void setCreditValue(Integer times) {
		CreditValue = times;
	}

	public String getAreaID() {
		return AreaID;
	}

	public void setAreaID(String address) {
		this.AreaID = address;
	}
	
	public String getInstallationId() {
		return InstallationId;
	}

	public void setInstallationId(String id) {
		this.InstallationId = id;
	}
}

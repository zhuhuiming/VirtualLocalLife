package com.mike.bombobject;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class CommentContent extends BmobObject {

	String PublishContentObjectId;//评论所关联的发布内容objectid号
	String TalkPersonInstallationID;//评论人ID号,用来推送消息
	//String AcceptTalkPersonInstallationID;//被评论人ID号,用来推送消息
	String TalkPersonName;//评论人名称(mac地址)
	String TalkPersonNickName;//评论人的昵称
	String AcceptTalkPersonName;//接收评论的人的名称(mac地址)
	String AcceptTalkPersonNickName;//接收评论的人的昵称
	//String AcceptTalkPersonName;//接收评论的人的名称(mac地址)
	String TalkTextContent;//评论内容(文字)
	BmobFile TalkImageContent;//评论内容(图片)
	
	public String getPublishContentObjectId() {
		return PublishContentObjectId;
	}

	public void setPublishContentObjectId(String strid) {
		this.PublishContentObjectId = strid;
	}
	
	public String getTalkPersonInstallationID() {
		return TalkPersonInstallationID;
	}

	public void setTalkPersonInstallationID(String strid) {
		this.TalkPersonInstallationID = strid;
	}
	
	/*public String getAcceptTalkPersonInstallationID() {
		return AcceptTalkPersonInstallationID;
	}

	public void setAcceptTalkPersonInstallationID(String strid) {
		this.AcceptTalkPersonInstallationID = strid;
	}*/
	
	public String getTalkPersonName() {
		return TalkPersonName;
	}

	public void setTalkPersonName(String strname) {
		this.TalkPersonName = strname;
	}
	
	public String getTalkPersonNickName() {
		return TalkPersonNickName;
	}

	public void setTalkPersonNickName(String strname) {
		this.TalkPersonNickName = strname;
	}
	
	public String getAcceptTalkPersonName() {
		return AcceptTalkPersonName;
	}

	public void setAcceptTalkPersonName(String strname) {
		this.AcceptTalkPersonName = strname;
	}
	
	public String getAcceptTalkPersonNickName() {
		return AcceptTalkPersonNickName;
	}

	public void setAcceptTalkPersonNickName(String strname) {
		this.AcceptTalkPersonNickName = strname;
	}
	
	/*public String getAcceptTalkPersonName() {
		return AcceptTalkPersonName;
	}

	public void setAcceptTalkPersonName(String strname) {
		this.AcceptTalkPersonName = strname;
	}*/
	
	public String getTalkTextContent() {
		return TalkTextContent;
	}

	public void setTalkTextContent(String strname) {
		this.TalkTextContent = strname;
	}
	
	public BmobFile getTalkImageContent() {
		return TalkImageContent;
	}

	public void setTalkImageContent(BmobFile file) {
		this.TalkImageContent = file;
	}
}

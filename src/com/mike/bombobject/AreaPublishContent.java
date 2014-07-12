package com.mike.bombobject;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

//�洢���򷢲����ݱ�
public class AreaPublishContent extends BmobObject {
	String TextContent;// ��������
	BmobFile FirstImage;//��һ��ͼƬ
	BmobFile SecondImage;//��һ��ͼƬ
	BmobFile ThirdImage;//��һ��ͼƬ
	String PublishPersonName;// ��������(MAC)
	String PublishAddress;// �����ص�
	Integer ScanTimes;// �������
	Integer CommentTimes;// ���۴���
	Integer CreditValue;// ��ֵ

	public AreaPublishContent() {
		this.setTableName("AreaPublishTab");
	}

	public String getTextContent() {
		return TextContent;
	}

	public void setTextContent(String text) {
		this.TextContent = text;
	}
	
	public BmobFile getFirstImage(){
		return FirstImage;
	}
	
	public void setFirstImage(BmobFile file){
		FirstImage = file;
	}
	
	public BmobFile getSecondImage(){
		return SecondImage;
	}
	
	public void setSecondImage(BmobFile file){
		SecondImage = file;
	}
	
	public BmobFile getThirdImage(){
		return ThirdImage;
	}
	
	public void setThirdImage(BmobFile file){
		ThirdImage = file;
	}

	public String getPublishPersonName() {
		return PublishPersonName;
	}

	public void setPublishPersonName(String name) {
		this.PublishPersonName = name;
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
}

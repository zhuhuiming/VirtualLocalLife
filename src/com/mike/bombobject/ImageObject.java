package com.mike.bombobject;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

//�洢ͼƬ�ı�
public class ImageObject extends BmobObject {
	BmobFile ImageContent;// ͼƬ����

	public BmobFile getImageContent() {
		return ImageContent;
	}

	public void setImageContent(BmobFile file) {
		this.ImageContent = file;
	}
}

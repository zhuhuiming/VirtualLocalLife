package com.mike.bombobject;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

//存储图片的表
public class ImageObject extends BmobObject {
	BmobFile ImageContent;// 图片内容

	public BmobFile getImageContent() {
		return ImageContent;
	}

	public void setImageContent(BmobFile file) {
		this.ImageContent = file;
	}
}

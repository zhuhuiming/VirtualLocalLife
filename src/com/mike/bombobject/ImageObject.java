package com.mike.bombobject;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

//´æ´¢Í¼Æ¬µÄ±í
public class ImageObject extends BmobObject {
	BmobFile ImageContent;// Í¼Æ¬ÄÚÈÝ

	public BmobFile getImageContent() {
		return ImageContent;
	}

	public void setImageContent(BmobFile file) {
		this.ImageContent = file;
	}
}

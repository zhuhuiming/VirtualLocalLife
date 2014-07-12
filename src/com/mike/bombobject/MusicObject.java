package com.mike.bombobject;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

//´æ´¢ÒôÀÖµÄ±í
public class MusicObject extends BmobObject {
	BmobFile MusicContent;// ÒôÀÖÄÚÈİ

	public BmobFile getMusicContent() {
		return MusicContent;
	}

	public void setMusicContent(BmobFile file) {
		this.MusicContent = file;
	}
}

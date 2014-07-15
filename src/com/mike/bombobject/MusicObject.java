package com.mike.bombobject;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

//存储音乐的表
public class MusicObject extends BmobObject {
	BmobFile MusicContent;// 音乐内容

	public BmobFile getMusicContent() {
		return MusicContent;
	}

	public void setMusicContent(BmobFile file) {
		this.MusicContent = file;
	}
}

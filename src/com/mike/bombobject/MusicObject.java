package com.mike.bombobject;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

//�洢���ֵı�
public class MusicObject extends BmobObject {
	BmobFile MusicContent;// ��������

	public BmobFile getMusicContent() {
		return MusicContent;
	}

	public void setMusicContent(BmobFile file) {
		this.MusicContent = file;
	}
}

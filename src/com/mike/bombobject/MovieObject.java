package com.mike.bombobject;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

//�洢��Ӱ�ı�
public class MovieObject extends BmobObject {
	BmobFile MovieContent;// ��Ӱ����

	public BmobFile getMovieContent() {
		return MovieContent;
	}

	public void setMovieContent(BmobFile file) {
		this.MovieContent = file;
	}
}

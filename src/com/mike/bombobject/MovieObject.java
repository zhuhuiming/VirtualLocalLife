package com.mike.bombobject;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

//存储电影的表
public class MovieObject extends BmobObject {
	BmobFile MovieContent;// 电影内容

	public BmobFile getMovieContent() {
		return MovieContent;
	}

	public void setMovieContent(BmobFile file) {
		this.MovieContent = file;
	}
}

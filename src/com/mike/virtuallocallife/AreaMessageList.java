package com.mike.virtuallocallife;

import com.mike.pulltorefresh.PullToRefreshView;
import com.mike.pulltorefresh.PullToRefreshView.OnFooterRefreshListener;
import com.mike.pulltorefresh.PullToRefreshView.OnHeaderRefreshListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

public class AreaMessageList extends Activity implements
OnHeaderRefreshListener, OnFooterRefreshListener{
	
	PullToRefreshView mPullToRefreshView1;
	//发布控件
	ImageView publishimageview;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.message_list);
		
		mPullToRefreshView1 = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView1.setOnHeaderRefreshListener(this);
		mPullToRefreshView1.setOnFooterRefreshListener(this);
		//mPullToRefreshView1.setVisibility(View.GONE);
		InitActivity();
	}

	public void InitActivity(){
		publishimageview = (ImageView)findViewById(R.id.messagelist_publishimageview);
		
		publishimageview.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//切换到发布内容界面
				Intent it = new Intent(AreaMessageList.this,PublishActivity.class);
				startActivity(it);
			}
			
		});
	}
	
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		
	}
}

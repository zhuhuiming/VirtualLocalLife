package com.mike.virtuallocallife;

import java.util.List;

import com.baidu.mapapi.SDKInitializer;
import com.mike.Utils.CommonUtils;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends Activity {

	CommonUtils mUtils = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(getApplicationContext());
		// 初始化 Bmob SDK
		Bmob.initialize(this, "0556064ba5deea19b15e6c2075d45286");
		// 启动推送服务
		BmobPush.startWork(this, "0556064ba5deea19b15e6c2075d45286");

		mUtils = new CommonUtils(this);
		// 判断用户之前是否注册过,如果注册过就直接切换到主界面,否则进入到注册界面
		DealUserOpera();
	}

	private void DealUserOpera() {
		// 首先判断程序是否保存了用户上次的登录信息
		BmobUser bmobUser = BmobUser.getCurrentUser(MainActivity.this);
		// 如果没有找到登录信息,那么就到数据库中寻找
		if (null == bmobUser) {
			// 先获取手机mac地址
			String strMac = mUtils.strGetPhoneMac();
			// 根据mac地址获取用户信息
			BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
			query.addQueryKeys("username");
			query.addWhereEqualTo("password", strMac);
			// 先从缓存获取数据，如果没有，再从网络获取
			// query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
			query.findObjects(MainActivity.this, new FindListener<BmobUser>() {
				@Override
				public void onSuccess(List<BmobUser> object) {
					if (object.size() > 0) {
						// 如果找到了
						CommonUtils.ShowToastCenter(MainActivity.this,
								"找到了注册用户，名称为:" + object.get(0).getUsername(),
								Toast.LENGTH_LONG);
						// 进入主界面
						Intent it = new Intent(MainActivity.this,
								AreaInfoActivity.class);
						startActivity(it);
						finish();
					} else {
						// 此时说明该用户没有注册,那么就进入注册页面
						Intent it = new Intent(MainActivity.this,
								RegeditActivity.class);
						startActivity(it);
						finish();
					}
				}

				@Override
				public void onError(int code, String Errormsg) {
					CommonUtils.ShowToastCenter(MainActivity.this, "出现错误,code:"
							+ code + " " + Errormsg, Toast.LENGTH_LONG);
				}
			});
		} else {// 如果找到了登录信息,那么就直接进入到主界面
			// 进入主界面
			Intent it = new Intent(MainActivity.this, AreaInfoActivity.class);
			startActivity(it);
			finish();
		}
	}
}

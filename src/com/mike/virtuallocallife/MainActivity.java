package com.mike.virtuallocallife;

import java.util.List;

import com.baidu.mapapi.SDKInitializer;
import com.igexin.sdk.PushManager;
import com.mike.Utils.CommonUtils;
import com.mike.bombobject.MyBmobInstallation;
import com.mike.bombobject.UserInfo;
import com.mike.commondata.commondata;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends Activity {

	CommonUtils mUtils = null;
	SharedPreferences msettings = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(getApplicationContext());
		// 初始化 Bmob SDK
		Bmob.initialize(this, commondata.strBmobKey);
		// 启动推送服务
		BmobPush.startWork(this, commondata.strBmobKey);
		//初始化个推sdk
		PushManager.getInstance().initialize(this.getApplicationContext());
		msettings = getSharedPreferences(commondata.PreferencesName, 0);
		
		mUtils = new CommonUtils(this);
		// 判断用户之前是否注册过,如果注册过就直接切换到主界面,否则进入到注册界面
		DealUserOpera();
		// 保存设备信息
		SaveDeviceInfo();
	}

	private void SaveDeviceInfo(){
		MyBmobInstallation myinst = new MyBmobInstallation(this);
		CommonUtils util = new CommonUtils(this);
		String strMac = util.strGetPhoneMac();
		myinst.setPersonName(strMac);
		myinst.save(this,new SaveListener(){

			@Override
			public void onFailure(int arg0, String arg1) {
				
			}

			@Override
			public void onSuccess() {
				
			}
			
		});
	}
	
	private void DealUserOpera() {
		// 首先判断程序是否保存了用户上次的登录信息
		BmobUser bmobUser = BmobUser.getCurrentUser(MainActivity.this);
		// 如果没有找到登录信息,那么就到数据库中寻找
		if (null == bmobUser) {
			// 先获取手机mac地址
			final String strMac = mUtils.strGetPhoneMac();
			// 根据mac地址获取用户信息
			BmobQuery<UserInfo> query = new BmobQuery<UserInfo>();
			query.addQueryKeys("username");
			query.addWhereEqualTo("password", strMac);
			// 先从缓存获取数据，如果没有，再从网络获取
			// query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
			query.findObjects(MainActivity.this, new FindListener<UserInfo>() {
				@Override
				public void onSuccess(List<UserInfo> object) {
					if (object.size() > 0) {
						// 如果找到了
						// 将用户性别和名称信息保存起来
						SharedPreferences.Editor editor = msettings.edit();
						editor.putString(commondata.UserGender, object.get(0).getGender());
						editor.putString(commondata.UserNickName, object.get(0).getUsername());
						editor.commit();
						
						UserInfo bu2 = new UserInfo();
						bu2.setUsername(object.get(0).getUsername());
						bu2.setPassword(strMac);
						bu2.login(MainActivity.this, new SaveListener() {
							@Override
							public void onSuccess() {
								// 进入主界面
								Intent it = new Intent(MainActivity.this,
										AreaInfoActivity.class);
								startActivity(it);
								finish();
							}

							@Override
							public void onFailure(int code, String msg) {
								CommonUtils.ShowToastCenter(MainActivity.this,
										"登录失败,code:" + code + msg,
										Toast.LENGTH_LONG);
							}
						});
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
	
	public void onDestroy() {
		super.onDestroy();
	}
}

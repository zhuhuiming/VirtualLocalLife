package com.mike.virtuallocallife;

import cn.bmob.v3.listener.SaveListener;

import com.mike.Utils.CommonUtils;
import com.mike.bombobject.AreaInfo;
import com.mike.commondata.commondata;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CaptureAreaActivity extends Activity {
	// 显示地盘的控件
	EditText areanameedittext;
	Button finishbutton;

	SharedPreferences msettings = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		msettings = getSharedPreferences(commondata.PreferencesName, 0);
		setContentView(R.layout.make_areaname);

		InitActivity();

	}

	private void InitActivity() {
		areanameedittext = (EditText) findViewById(R.id.makearename_nameedittext);
		finishbutton = (Button) findViewById(R.id.makeareaname_nextbutton);

		finishbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断地盘名称是否有值
				if (areanameedittext.getText().toString().equals("")) {
					CommonUtils.ShowToastCenter(CaptureAreaActivity.this,
							"请输入地盘名称", Toast.LENGTH_LONG);
				} else {
					// 获取当前用户地址的经纬度方向索引号
					String strXIndex = msettings.getString(commondata.XIndex,
							"");
					String strYIndex = msettings.getString(commondata.YIndex,
							"");
					Integer nXIndex = Integer.parseInt(strXIndex);
					Integer nYIndex = Integer.parseInt(strYIndex);

					CommonUtils util = new CommonUtils(CaptureAreaActivity.this);
					// 将信息保存到区域信息表中
					// mac地址(作为用户名)
					String strMac = util.strGetPhoneMac();
					// 将该名称写入到数据库中
					AreaInfo areainfo = new AreaInfo();
					areainfo.setXIndex(nXIndex);
					areainfo.setYIndex(nYIndex);
					areainfo.setAreaName(areanameedittext.getText().toString());

					areainfo.setDeScribe("");
					areainfo.setLanderOwnerName(strMac);
					areainfo.setFindPersonName(strMac);
					areainfo.save(CaptureAreaActivity.this, new SaveListener() {

						@Override
						public void onSuccess() {

							CommonUtils.ShowToastCenter(
									CaptureAreaActivity.this, "该地盘现在属于你的啦",
									Toast.LENGTH_LONG);
							CaptureAreaActivity.this.finish();
						}

						@Override
						public void onFailure(int code, String arg0) {
							CommonUtils.ShowToastCenter(
									CaptureAreaActivity.this, "抢地盘失败,code = "
											+ code + " 错误原因:" + arg0,
									Toast.LENGTH_LONG);
						}
					});
				}
			}

		});
	}
}

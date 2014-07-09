package com.mike.virtuallocallife;

import com.mike.Utils.CommonUtils;
import com.mike.commondata.commondata;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class RegeditActivity extends Activity {
	CommonUtils mUtils = null;
	// "男"单选控件
	RadioButton ManRadio;
	// "女"单选控件
	RadioButton WomanRadio;
	// 用户名称控件
	EditText UserNameEditText;
	// 下一步按钮控件
	Button NextButton;
	// 用户性别信息
	String strSex = "男";
	SharedPreferences msettings = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.regedit_tel);
		mUtils = new CommonUtils(this);

		msettings = getSharedPreferences(commondata.PreferencesName, 0);
		InitActivity();
	}

	private void InitActivity() {
		UserNameEditText = (EditText) findViewById(R.id.regedittel_username);
		NextButton = (Button) findViewById(R.id.regedittel_nextbutton);
		ManRadio = (RadioButton) findViewById(R.id.regeditname_maleradiobutton);
		WomanRadio = (RadioButton) findViewById(R.id.regeditname_femaleradiobutton);

		ManRadio.setChecked(true);
		WomanRadio.setChecked(false);

		ManRadio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				strSex = "男";
				WomanRadio.setChecked(false);
			}

		});

		WomanRadio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				strSex = "女";
				ManRadio.setChecked(false);
			}

		});

		NextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断用户名称是否输入了
				if (UserNameEditText.getText().toString().equals("")) {
					CommonUtils.ShowToastCenter(RegeditActivity.this,
							"用户名称不能为空", Toast.LENGTH_LONG);
				} else {
					// 将用户性别和名称信息保存起来
					SharedPreferences.Editor editor = msettings.edit();
					editor.putString(commondata.UserGender, strSex);
					editor.putString(commondata.UserNickName, UserNameEditText
							.getText().toString());
					editor.commit();
					// 切换到下一个界面
					Intent it = new Intent(RegeditActivity.this,
							FinishRegeditActivity.class);
					startActivity(it);
					finish();
				}
			}

		});
	}

	@Override
	public void onBackPressed() {
		//启动MainActivity
		Intent it = new Intent(RegeditActivity.this,
				MainActivity.class);
		startActivity(it);
		finish();
	}
}

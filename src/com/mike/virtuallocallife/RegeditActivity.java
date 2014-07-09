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
	// "��"��ѡ�ؼ�
	RadioButton ManRadio;
	// "Ů"��ѡ�ؼ�
	RadioButton WomanRadio;
	// �û����ƿؼ�
	EditText UserNameEditText;
	// ��һ����ť�ؼ�
	Button NextButton;
	// �û��Ա���Ϣ
	String strSex = "��";
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
				strSex = "��";
				WomanRadio.setChecked(false);
			}

		});

		WomanRadio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				strSex = "Ů";
				ManRadio.setChecked(false);
			}

		});

		NextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// �ж��û������Ƿ�������
				if (UserNameEditText.getText().toString().equals("")) {
					CommonUtils.ShowToastCenter(RegeditActivity.this,
							"�û����Ʋ���Ϊ��", Toast.LENGTH_LONG);
				} else {
					// ���û��Ա��������Ϣ��������
					SharedPreferences.Editor editor = msettings.edit();
					editor.putString(commondata.UserGender, strSex);
					editor.putString(commondata.UserNickName, UserNameEditText
							.getText().toString());
					editor.commit();
					// �л�����һ������
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
		//����MainActivity
		Intent it = new Intent(RegeditActivity.this,
				MainActivity.class);
		startActivity(it);
		finish();
	}
}

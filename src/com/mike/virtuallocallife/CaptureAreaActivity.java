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
	// ��ʾ���̵Ŀؼ�
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
				// �жϵ��������Ƿ���ֵ
				if (areanameedittext.getText().toString().equals("")) {
					CommonUtils.ShowToastCenter(CaptureAreaActivity.this,
							"�������������", Toast.LENGTH_LONG);
				} else {
					// ��ȡ��ǰ�û���ַ�ľ�γ�ȷ���������
					String strXIndex = msettings.getString(commondata.XIndex,
							"");
					String strYIndex = msettings.getString(commondata.YIndex,
							"");
					Integer nXIndex = Integer.parseInt(strXIndex);
					Integer nYIndex = Integer.parseInt(strYIndex);

					CommonUtils util = new CommonUtils(CaptureAreaActivity.this);
					// ����Ϣ���浽������Ϣ����
					// mac��ַ(��Ϊ�û���)
					String strMac = util.strGetPhoneMac();
					// ��������д�뵽���ݿ���
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
									CaptureAreaActivity.this, "�õ����������������",
									Toast.LENGTH_LONG);
							CaptureAreaActivity.this.finish();
						}

						@Override
						public void onFailure(int code, String arg0) {
							CommonUtils.ShowToastCenter(
									CaptureAreaActivity.this, "������ʧ��,code = "
											+ code + " ����ԭ��:" + arg0,
									Toast.LENGTH_LONG);
						}
					});
				}
			}

		});
	}
}

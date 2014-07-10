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
		// ��ʹ�� SDK �����֮ǰ��ʼ�� context ��Ϣ������ ApplicationContext
		SDKInitializer.initialize(getApplicationContext());
		// ��ʼ�� Bmob SDK
		Bmob.initialize(this, "0556064ba5deea19b15e6c2075d45286");
		// �������ͷ���
		BmobPush.startWork(this, "0556064ba5deea19b15e6c2075d45286");

		mUtils = new CommonUtils(this);
		// �ж��û�֮ǰ�Ƿ�ע���,���ע�����ֱ���л���������,������뵽ע�����
		DealUserOpera();
	}

	private void DealUserOpera() {
		// �����жϳ����Ƿ񱣴����û��ϴεĵ�¼��Ϣ
		BmobUser bmobUser = BmobUser.getCurrentUser(MainActivity.this);
		// ���û���ҵ���¼��Ϣ,��ô�͵����ݿ���Ѱ��
		if (null == bmobUser) {
			// �Ȼ�ȡ�ֻ�mac��ַ
			String strMac = mUtils.strGetPhoneMac();
			// ����mac��ַ��ȡ�û���Ϣ
			BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
			query.addQueryKeys("username");
			query.addWhereEqualTo("password", strMac);
			// �ȴӻ����ȡ���ݣ����û�У��ٴ������ȡ
			// query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
			query.findObjects(MainActivity.this, new FindListener<BmobUser>() {
				@Override
				public void onSuccess(List<BmobUser> object) {
					if (object.size() > 0) {
						// ����ҵ���
						CommonUtils.ShowToastCenter(MainActivity.this,
								"�ҵ���ע���û�������Ϊ:" + object.get(0).getUsername(),
								Toast.LENGTH_LONG);
						// ����������
						Intent it = new Intent(MainActivity.this,
								AreaInfoActivity.class);
						startActivity(it);
						finish();
					} else {
						// ��ʱ˵�����û�û��ע��,��ô�ͽ���ע��ҳ��
						Intent it = new Intent(MainActivity.this,
								RegeditActivity.class);
						startActivity(it);
						finish();
					}
				}

				@Override
				public void onError(int code, String Errormsg) {
					CommonUtils.ShowToastCenter(MainActivity.this, "���ִ���,code:"
							+ code + " " + Errormsg, Toast.LENGTH_LONG);
				}
			});
		} else {// ����ҵ��˵�¼��Ϣ,��ô��ֱ�ӽ��뵽������
			// ����������
			Intent it = new Intent(MainActivity.this, AreaInfoActivity.class);
			startActivity(it);
			finish();
		}
	}
}

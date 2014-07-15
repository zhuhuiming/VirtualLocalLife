package com.mike.virtuallocallife;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;

import com.mike.Utils.CommonUtils;
import com.mike.bombobject.AreaPublishContent;
import com.mike.bombobject.UserInfo;
import com.mike.pulltorefresh.PullToRefreshView;
import com.mike.pulltorefresh.PullToRefreshView.OnFooterRefreshListener;
import com.mike.pulltorefresh.PullToRefreshView.OnHeaderRefreshListener;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AreaMessageList extends ListActivity implements
		OnHeaderRefreshListener, OnFooterRefreshListener {

	PullToRefreshView mPullToRefreshView1;

	private TaskInfoListAdpater madapter2 = null;
	// �洢���ݵ�����
	private List<HashMap<String, Object>> mListData1 = null;
	// һ���Լ������ݵ�����
	private static final int MAXLOADDATANUM = 10;
	// ����ͼƬ�Ŀ�
	private static final int publishimagewidth = 300;
	// ����ͼƬ�ĸ�
	private static final int publishimageheight = 300;

	/********************** �ؼ����� *********************/
	// �����ؼ�
	ImageView publishimageview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.message_list);

		mPullToRefreshView1 = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView1.setOnHeaderRefreshListener(this);
		mPullToRefreshView1.setOnFooterRefreshListener(this);
		// mPullToRefreshView1.setVisibility(View.GONE);
		InitActivity();
		// ��ʼ��������
		LoadData();
	}

	public void InitActivity() {
		publishimageview = (ImageView) findViewById(R.id.messagelist_publishimageview);

		publishimageview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// �л����������ݽ���
				Intent it = new Intent(AreaMessageList.this,
						PublishActivity.class);
				startActivity(it);
			}

		});
	}

	private void LoadData() {
		BmobQuery<AreaPublishContent> query = new BmobQuery<AreaPublishContent>();
		// һ���Բ�ѯ����������
		query.setLimit(MAXLOADDATANUM);
		query.order("-createdAt");
		//query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
		query.addWhereEqualTo("AreaID", AreaInfoActivity.strCurSelectAreaId);
		query.findObjects(AreaMessageList.this,
				new FindListener<AreaPublishContent>() {

					@Override
					public void onError(int arg0, String arg1) {
						CommonUtils.ShowToastCenter(AreaMessageList.this,
								"��������ʧ��,code:" + arg0 + "error:" + arg1,
								Toast.LENGTH_LONG);
					}

					@Override
					public void onSuccess(List<AreaPublishContent> areainfo) {
						// ����ѯ����������ʾ���б���
						mListData1 = getListData(areainfo);
						SetShareAdapter(false);
					}

				});
	}

	// ����mac��ȡ�û����ƺ�ͷ��ͬʱ��ʾ
	private void ShowPersonInfoByMac(final ImageView personimageview,
			final TextView usernametextview, String strMac) {
		// ����mac��ַ��ȡ�û���Ϣ
		BmobQuery<UserInfo> query = new BmobQuery<UserInfo>();
		query.addQueryKeys("UserIcon,username");
		query.addWhereEqualTo("password", strMac);
		// �ȴӻ����ȡ���ݣ����û�У��ٴ������ȡ
		query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
		query.findObjects(AreaMessageList.this, new FindListener<UserInfo>() {

			@Override
			public void onError(int arg0, String arg1) {
				CommonUtils.ShowToastCenter(AreaMessageList.this, "��ȡ�û���Ϣʧ��",
						Toast.LENGTH_LONG);
			}

			@Override
			public void onSuccess(List<UserInfo> arg0) {
				if (arg0.size() > 0) {
					usernametextview.setText(arg0.get(0).getUsername());
					BmobFile file = arg0.get(0).getUserIcon();
					file.loadImage(AreaMessageList.this, personimageview,
							publishimagewidth, publishimageheight);
				} else {
					CommonUtils.ShowToastCenter(AreaMessageList.this,
							"û���ҵ���Ӧ���û���Ϣ", Toast.LENGTH_LONG);
				}
			}
		});
	}

	private List<HashMap<String, Object>> getListData(
			List<AreaPublishContent> areainfo) {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = null;

		String TextContent;// ��������
		BmobFile FirstImage;// ��һ��ͼƬ
		BmobFile SecondImage;// ��һ��ͼƬ
		BmobFile ThirdImage;// ��һ��ͼƬ
		String PublishPersonName;// ��������(MAC)
		String PublishAddress;// �����ص�
		Integer ScanTimes;// �������
		Integer CommentTimes;// ���۴���
		Integer CreditValue;// ��ֵ
		String PublishTime;// ����ʱ��

		if (areainfo != null) {
			int nCount = areainfo.size();
			for (int i = 0; i < nCount; i++) {
				// ����ȡ�������ݴ洢��������
				map = new HashMap<String, Object>();
				TextContent = areainfo.get(i).getTextContent();
				FirstImage = areainfo.get(i).getFirstImage();
				SecondImage = areainfo.get(i).getSecondImage();
				ThirdImage = areainfo.get(i).getThirdImage();
				PublishPersonName = areainfo.get(i).getPublishPersonName();
				PublishAddress = areainfo.get(i).getPublishAddress();
				ScanTimes = areainfo.get(i).getScanTimes();
				CommentTimes = areainfo.get(i).getCommentTimes();
				CreditValue = areainfo.get(i).getCreditValue();
				PublishTime = areainfo.get(i).getCreatedAt();

				map.put("TextContent", TextContent);
				map.put("FirstImage", FirstImage);
				map.put("SecondImage", SecondImage);
				map.put("ThirdImage", ThirdImage);
				map.put("PublishPersonName", PublishPersonName);
				map.put("PublishAddress", PublishAddress);
				map.put("ScanTimes", ScanTimes);
				map.put("CommentTimes", CommentTimes);
				map.put("CreditValue", CreditValue);
				map.put("PublishTime", PublishTime);
				list.add(map);
			}
		}
		return list;
	}

	class TaskInfoListAdpater extends SimpleAdapter {

		private LayoutInflater mInflater;
		Context context;
		int count = 0;
		private List<HashMap<String, Object>> mItemList;

		@SuppressWarnings("unchecked")
		public TaskInfoListAdpater(Context context,
				List<? extends HashMap<String, Object>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.context = context;
			mItemList = (List<HashMap<String, Object>>) data;
			if (data == null) {
				count = 0;
			} else {
				count = data.size();
			}
		}

		public int getCount() {
			return mItemList.size();
		}

		public Object getItem(int pos) {
			return mItemList.get(pos);
		}

		public long getItemId(int pos) {
			return pos;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) getItem(position);

			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.messageitem, null);
			}
			// ������ͷ��
			ImageView personimageview = (ImageView) convertView
					.findViewById(R.id.messageitem_personimageview);
			// ����������
			TextView personname = (TextView) convertView
					.findViewById(R.id.messageitem_textview3);
			// ����ʱ��
			TextView publishtime = (TextView) convertView
					.findViewById(R.id.messageitem_timetextview);
			// ������Ϣ����
			TextView publishtitle = (TextView) convertView
					.findViewById(R.id.messageitem_titletextview);
			// �����ĵ�һ��ͼƬ
			ImageView firstimageview = (ImageView) convertView
					.findViewById(R.id.messageitem_icon1);
			// �����ĵ�һ��ͼƬ
			ImageView secondimageview = (ImageView) convertView
					.findViewById(R.id.messageitem_icon2);
			// �����ĵ�һ��ͼƬ
			ImageView thirdimageview = (ImageView) convertView
					.findViewById(R.id.messageitem_icon3);
			// ��ֵ����
			TextView zantextview = (TextView) convertView
					.findViewById(R.id.messageitem_zantextview);
			// ���۸���
			TextView commenttextview = (TextView) convertView
					.findViewById(R.id.messageitem_commenttextview);
			// �������
			TextView scantextview = (TextView) convertView
					.findViewById(R.id.messageitem_looktextview);

			publishtime.setText(map.get("PublishTime").toString());
			publishtitle.setText(map.get("TextContent").toString());
			BmobFile file = (BmobFile) map.get("FirstImage");
			if (file != null) {
				file.loadImage(AreaMessageList.this, firstimageview,
						publishimagewidth, publishimageheight);
			}else{
				firstimageview.setImageBitmap(null);
			}
			file = (BmobFile) map.get("SecondImage");
			if (file != null) {
				file.loadImage(AreaMessageList.this, secondimageview,
						publishimagewidth, publishimageheight);
			}else{
				secondimageview.setImageBitmap(null);
			}
			file = (BmobFile) map.get("ThirdImage");
			if (file != null) {
				file.loadImage(AreaMessageList.this, thirdimageview,
						publishimagewidth, publishimageheight);
			}else{
				thirdimageview.setImageBitmap(null);
			}
			zantextview.setText(map.get("CreditValue").toString());
			commenttextview.setText(map.get("CommentTimes").toString());
			scantextview.setText(map.get("ScanTimes").toString());

			// ��ʼ����ͷ����û�����
			ShowPersonInfoByMac(personimageview, personname,
					map.get("PublishPersonName").toString());
			return convertView;
		}

	}

	public void SetShareAdapter(boolean bIsReset) {
		if (null == madapter2) {
			if (mListData1 != null) {
				madapter2 = new TaskInfoListAdpater(AreaMessageList.this,
						mListData1, R.layout.messageitem, null, null);
				setListAdapter(madapter2);
			}

		} else {
			madapter2.mItemList = mListData1;
			madapter2.notifyDataSetChanged();
		}
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {

	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {

	}
}

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
	// 存储数据的容器
	private List<HashMap<String, Object>> mListData1 = null;
	// 一次性加载数据的条数
	private static final int MAXLOADDATANUM = 10;
	// 发布图片的宽
	private static final int publishimagewidth = 300;
	// 发布图片的高
	private static final int publishimageheight = 300;

	/********************** 控件对象 *********************/
	// 发布控件
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
		// 开始加载数据
		LoadData();
	}

	public void InitActivity() {
		publishimageview = (ImageView) findViewById(R.id.messagelist_publishimageview);

		publishimageview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 切换到发布内容界面
				Intent it = new Intent(AreaMessageList.this,
						PublishActivity.class);
				startActivity(it);
			}

		});
	}

	private void LoadData() {
		BmobQuery<AreaPublishContent> query = new BmobQuery<AreaPublishContent>();
		// 一次性查询的数据条数
		query.setLimit(MAXLOADDATANUM);
		query.order("-createdAt");
		//query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
		query.addWhereEqualTo("AreaID", AreaInfoActivity.strCurSelectAreaId);
		query.findObjects(AreaMessageList.this,
				new FindListener<AreaPublishContent>() {

					@Override
					public void onError(int arg0, String arg1) {
						CommonUtils.ShowToastCenter(AreaMessageList.this,
								"加载数据失败,code:" + arg0 + "error:" + arg1,
								Toast.LENGTH_LONG);
					}

					@Override
					public void onSuccess(List<AreaPublishContent> areainfo) {
						// 将查询到的数据显示到列表中
						mListData1 = getListData(areainfo);
						SetShareAdapter(false);
					}

				});
	}

	// 根据mac获取用户名称和头像同时显示
	private void ShowPersonInfoByMac(final ImageView personimageview,
			final TextView usernametextview, String strMac) {
		// 根据mac地址获取用户信息
		BmobQuery<UserInfo> query = new BmobQuery<UserInfo>();
		query.addQueryKeys("UserIcon,username");
		query.addWhereEqualTo("password", strMac);
		// 先从缓存获取数据，如果没有，再从网络获取
		query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
		query.findObjects(AreaMessageList.this, new FindListener<UserInfo>() {

			@Override
			public void onError(int arg0, String arg1) {
				CommonUtils.ShowToastCenter(AreaMessageList.this, "获取用户信息失败",
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
							"没有找到相应的用户信息", Toast.LENGTH_LONG);
				}
			}
		});
	}

	private List<HashMap<String, Object>> getListData(
			List<AreaPublishContent> areainfo) {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = null;

		String TextContent;// 文字内容
		BmobFile FirstImage;// 第一张图片
		BmobFile SecondImage;// 第一张图片
		BmobFile ThirdImage;// 第一张图片
		String PublishPersonName;// 发布人名(MAC)
		String PublishAddress;// 发布地点
		Integer ScanTimes;// 浏览次数
		Integer CommentTimes;// 评论次数
		Integer CreditValue;// 赞值
		String PublishTime;// 发布时间

		if (areainfo != null) {
			int nCount = areainfo.size();
			for (int i = 0; i < nCount; i++) {
				// 将获取到的数据存储到容器中
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
			// 发布人头像
			ImageView personimageview = (ImageView) convertView
					.findViewById(R.id.messageitem_personimageview);
			// 发布人名称
			TextView personname = (TextView) convertView
					.findViewById(R.id.messageitem_textview3);
			// 发布时间
			TextView publishtime = (TextView) convertView
					.findViewById(R.id.messageitem_timetextview);
			// 发布信息标题
			TextView publishtitle = (TextView) convertView
					.findViewById(R.id.messageitem_titletextview);
			// 发布的第一张图片
			ImageView firstimageview = (ImageView) convertView
					.findViewById(R.id.messageitem_icon1);
			// 发布的第一张图片
			ImageView secondimageview = (ImageView) convertView
					.findViewById(R.id.messageitem_icon2);
			// 发布的第一张图片
			ImageView thirdimageview = (ImageView) convertView
					.findViewById(R.id.messageitem_icon3);
			// 赞值个数
			TextView zantextview = (TextView) convertView
					.findViewById(R.id.messageitem_zantextview);
			// 评论个数
			TextView commenttextview = (TextView) convertView
					.findViewById(R.id.messageitem_commenttextview);
			// 浏览次数
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

			// 开始加载头像和用户名称
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

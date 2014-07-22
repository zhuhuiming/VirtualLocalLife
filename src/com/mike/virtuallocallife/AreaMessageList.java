package com.mike.virtuallocallife;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.mike.Utils.CommonUtils;
import com.mike.bombobject.AreaPublishContent;
import com.mike.bombobject.UserInfo;
import com.mike.pulltorefresh.PullToRefreshView;
import com.mike.pulltorefresh.PullToRefreshView.OnFooterRefreshListener;
import com.mike.pulltorefresh.PullToRefreshView.OnHeaderRefreshListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AreaMessageList extends Activity implements
		OnHeaderRefreshListener, OnFooterRefreshListener {

	PullToRefreshView mPullToRefreshView1;

	private TaskInfoListAdpater madapter1 = null;
	// 存储数据的容器
	private List<HashMap<String, Object>> mListData1 = null;
	// 一次性加载数据的条数
	private static final int MAXLOADDATANUM = 10;
	// 发布图片的宽
	// private static final int publishimagewidth = 300;
	// 发布图片的高
	// private static final int publishimageheight = 300;
	// 存储发布信息
	public static List<AreaPublishContent> publishcontent = null;
	// 发布信息的最大条数
	private static final int nMaxPublishDataNum = 60;
	// 最旧的信息发布时间
	String strOldestTime = "";

	/********************** 控件对象 *********************/
	// 发布控件
	ImageView publishimageview;
	// ListView控件
	ListView listview;

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

	private void UpdateInfo(String strobjectid, int nzanvalue,
			int ncommentvalue, int nscanvalue) {
		if (publishcontent != null) {
			int nSize = publishcontent.size();
			for (int i = 0; i < nSize; i++) {
				if (publishcontent.get(i).getObjectId().equals(strobjectid)) {
					publishcontent.get(i).setCreditValue(nzanvalue);
					publishcontent.get(i).setCommentTimes(ncommentvalue);
					publishcontent.get(i).setScanTimes(nscanvalue);
					// 重新获取数据
					mListData1 = getListData(publishcontent);
					SetShareAdapter(false);
					break;
				}
			}
		}
	}

	// 启动子项finish后调用的函数
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (1 == requestCode) {
			if (data.getExtras() != null) {
				// 获取objectid
				String strobjectid = data.getExtras().getString("objectid");
				// 获取传过来的赞值,评论数,浏览次数
				int nZanValue = data.getExtras().getInt("nZanValue");
				int nCommentValue = data.getExtras().getInt("nCommentValue");
				int nScanValue = data.getExtras().getInt("nScanValue");
				// 将这些值更新到相应的变量中
				UpdateInfo(strobjectid, nZanValue, nCommentValue, nScanValue);
			}
		}
	}

	public void InitActivity() {
		publishimageview = (ImageView) findViewById(R.id.messagelist_publishimageview);
		listview = (ListView) findViewById(R.id.messagelist_listview);

		publishimageview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 切换到发布内容界面
				Intent it = new Intent(AreaMessageList.this,
						PublishActivity.class);
				startActivity(it);
			}

		});

		// listview的点击事件
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				AreaPublishContent content = publishcontent.get(position);
				Intent it = new Intent(AreaMessageList.this,
						AreaShareMessageActivity.class);
				Bundle mBundle = new Bundle();
				mBundle.putSerializable("content", content);
				it.putExtras(mBundle);
				startActivityForResult(it, 1);
			}

		});
	}

	private void LoadData() {
		BmobQuery<AreaPublishContent> query = new BmobQuery<AreaPublishContent>();
		// 一次性查询的数据条数
		query.setLimit(MAXLOADDATANUM);
		query.order("-updatedAt");
		// query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
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

						// 保存获取到的信息数据
						publishcontent = areainfo;
						int nSize = areainfo.size();
						if (nSize > 0) {
							// 保存最旧的时间
							strOldestTime = areainfo.get(nSize - 1)
									.getCreatedAt();
						}
						// 将查询到的数据显示到列表中
						mListData1 = getListData(areainfo);
						SetShareAdapter(false);
					}

				});
	}

	// 根据mac获取用户名称和头像同时显示
	private void ShowPersonInfoByMac(final ImageView personimageview,
			String strMac) {
		// 根据mac地址获取用户信息
		BmobQuery<UserInfo> query = new BmobQuery<UserInfo>();
		query.addQueryKeys("UserIcon");
		query.addWhereEqualTo("password", strMac);
		// 先从缓存获取数据，如果没有，再从网络获取
		query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
		query.findObjects(AreaMessageList.this, new FindListener<UserInfo>() {

			@Override
			public void onError(int arg0, String arg1) {
				// CommonUtils.ShowToastCenter(AreaMessageList.this, "获取用户信息失败",
				// Toast.LENGTH_LONG);
			}

			@Override
			public void onSuccess(List<UserInfo> arg0) {
				if (arg0.size() > 0) {
					BmobFile file = arg0.get(0).getUserIcon();
					new DownloadImageTask().execute(file.getFileUrl(),
							file.getFilename(), personimageview);
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
		BmobFile SecondImage;// 第二张图片
		BmobFile ThirdImage;// 第三张图片
		String PublishPersonName;// 发布人名(MAC)
		String PublishPersonNickName;// 发布人的昵称
		String PublishAddress;// 发布地点
		Integer ScanTimes;// 浏览次数
		Integer CommentTimes;// 评论次数
		Integer CreditValue;// 赞值
		String PublishTime;// 发布时间
		String ObjectId;// 消息id

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
				ObjectId = areainfo.get(i).getObjectId();
				PublishPersonNickName = areainfo.get(i)
						.getPublishPersonNickName();

				map.put("TextContent", TextContent);
				map.put("FirstImage", FirstImage);
				map.put("SecondImage", SecondImage);
				map.put("ThirdImage", ThirdImage);
				map.put("PublishPersonName", PublishPersonName);
				map.put("PublishPersonNickName", PublishPersonNickName);
				map.put("PublishAddress", PublishAddress);
				map.put("ScanTimes", ScanTimes);
				map.put("CommentTimes", CommentTimes);
				map.put("CreditValue", CreditValue);
				map.put("PublishTime", PublishTime);
				map.put("ObjectId", ObjectId);
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
			final Map<String, Object> map = (Map<String, Object>) getItem(position);

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
			// 发布的第二张图片
			ImageView secondimageview = (ImageView) convertView
					.findViewById(R.id.messageitem_icon2);
			// 发布的第三张图片
			ImageView thirdimageview = (ImageView) convertView
					.findViewById(R.id.messageitem_icon3);
			// 赞值个数
			final TextView zantextview = (TextView) convertView
					.findViewById(R.id.messageitem_zantextview);
			// 评论个数
			TextView commenttextview = (TextView) convertView
					.findViewById(R.id.messageitem_commenttextview);
			// 浏览次数
			TextView scantextview = (TextView) convertView
					.findViewById(R.id.messageitem_looktextview);
			// 赞imageview控件
			final ImageView zanimageview = (ImageView) convertView
					.findViewById(R.id.messageitem_zanimageview);

			final Resources res = getResources();
			Bitmap curzanbmp = BitmapFactory.decodeResource(res,
					R.drawable.zan0);
			zanimageview.setImageBitmap(curzanbmp);

			zantextview.setText(map.get("CreditValue").toString());

			// 用来响应点赞操作的linearlayout控件
			LinearLayout zanlinearlayout = (LinearLayout) convertView
					.findViewById(R.id.messageitem_zanlayout);
			zanlinearlayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CommonUtils util = new CommonUtils(AreaMessageList.this);
					// 获取mac地址
					String strMac = util.strGetPhoneMac();
					// 如果该信息的发布者是本人
					if (strMac.equals(map.get("PublishPersonName").toString())) {
						CommonUtils.ShowToastCenter(AreaMessageList.this,
								"不能给自己赞哦", Toast.LENGTH_LONG);
					} else {
						// 赞值加一
						AreaPublishContent content = new AreaPublishContent();
						content.increment("CreditValue"); // 赞值递增1
						content.update(AreaMessageList.this, map
								.get("ObjectId").toString(),
								new UpdateListener() {

									@Override
									public void onFailure(int arg0, String arg1) {
										CommonUtils.ShowToastCenter(
												AreaMessageList.this,
												"操作失败,Code:" + arg0 + "error:"
														+ arg1,
												Toast.LENGTH_LONG);
									}

									@Override
									public void onSuccess() {
										// 将赞图标设为红色
										Bitmap zanbmp = BitmapFactory
												.decodeResource(res,
														R.drawable.zan1);
										zanimageview.setImageBitmap(zanbmp);
										int nzanvalue = Integer.parseInt(map
												.get("CreditValue").toString());
										nzanvalue += 1;
										zantextview.setText(nzanvalue + "");
									}

								});
					}
				}

			});

			publishtime.setText(map.get("PublishTime").toString());
			publishtitle.setText(map.get("TextContent").toString());
			BmobFile file1 = (BmobFile) map.get("FirstImage");
			BmobFile file2 = (BmobFile) map.get("SecondImage");
			BmobFile file3 = (BmobFile) map.get("ThirdImage");
			if (file1 != null) {
				new DownloadImageTask().execute(file1.getFileUrl(),
						file1.getFilename(), firstimageview);
			} else {
				firstimageview.setImageBitmap(null);
			}
			file2 = (BmobFile) map.get("SecondImage");
			if (file2 != null) {
				new DownloadImageTask().execute(file2.getFileUrl(),
						file2.getFilename(), secondimageview);
			} else {
				secondimageview.setImageBitmap(null);
			}
			file3 = (BmobFile) map.get("ThirdImage");
			if (file3 != null) {
				new DownloadImageTask().execute(file3.getFileUrl(),
						file3.getFilename(), thirdimageview);
			} else {
				thirdimageview.setImageBitmap(null);
			}
			commenttextview.setText(map.get("CommentTimes").toString());
			scantextview.setText(map.get("ScanTimes").toString());
			personname.setText(map.get("PublishPersonNickName").toString());
			// 开始加载头像和用户名称
			ShowPersonInfoByMac(personimageview, map.get("PublishPersonName")
					.toString());
			return convertView;
		}

	}

	private Drawable loadImageFromNetwork(String imageUrl, String strName) {
		Drawable drawable = null;
		try {
			// 可以在这里通过文件名来判断，是否本地有此图片
			drawable = Drawable.createFromStream(
					new URL(imageUrl).openStream(), strName);
		} catch (IOException e) {
		}
		if (drawable == null) {

		} else {

		}

		return drawable;
	}

	private class DownloadImageTask extends AsyncTask<Object, Void, Drawable> {
		private ImageView imageView = null;

		protected Drawable doInBackground(Object... urls) {
			String strUrl = (String) urls[0];
			String strName = (String) urls[1];
			imageView = (ImageView) urls[2];
			return loadImageFromNetwork(strUrl, strName);
		}

		protected void onPostExecute(Drawable result) {
			imageView.setImageDrawable(result);
		}
	}

	public void SetShareAdapter(boolean bIsReset) {
		if (null == madapter1) {
			if (mListData1 != null) {
				madapter1 = new TaskInfoListAdpater(AreaMessageList.this,
						mListData1, R.layout.messageitem, null, null);
				listview.setAdapter(madapter1);
			}

		} else {
			madapter1.mItemList = mListData1;
			madapter1.notifyDataSetChanged();
		}
		// 如果列表中没有数据,那么就隐藏listview
		if (mListData1.size() <= 0) {
			listview.setVisibility(View.GONE);
		} else {
			listview.setVisibility(View.VISIBLE);
		}
	}

	// 将获取到的新数据合并到原有数据中
	private void DealLoadNearShareTaskData(List<AreaPublishContent> data) {

		int nCount = data.size();
		int nSize = publishcontent.size();
		int nRemainCount = nCount + nSize - nMaxPublishDataNum;
		// 如果数据条数超过了容器最大容量,那么就要去掉几条最新数据
		if (nRemainCount > 0) {
			// 去掉nRemainCount条数据
			for (int i = 0; i < nRemainCount; i++) {
				publishcontent.remove(0);
			}
			// 将加载数据添加到容器中
			publishcontent.addAll(nSize - nRemainCount, data);
		} else {// 如果数据没有超过最大值
			// 将加载数据添加到容器中
			publishcontent.addAll(nSize, data);
		}
	}

	// 对数据进行去重处理
	private List<AreaPublishContent> DeleteSameData(
			List<AreaPublishContent> data) {
		if (data != null) {
			int nSize = data.size();
			for (int i = 0; i < nSize; i++) {
				if (publishcontent != null) {
					int nSize1 = publishcontent.size();
					for (int j = 0; j < nSize1; j++) {
						// 如果有相同的数据
						if (data.get(i).getObjectId()
								.equals(publishcontent.get(j).getObjectId())) {
							// 将相同的数据删除
							data.remove(i);
							i--;
							nSize = data.size();
							break;
						}
					}
				}

			}
		}
		return data;
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// 将日期字符串转换成Date
		Date date = CommonUtils.ChangeTimeToDate(strOldestTime,
				"yyyy-MM-dd HH:mm:ss");
		BmobQuery<AreaPublishContent> query = new BmobQuery<AreaPublishContent>();
		// 一次性查询的数据条数
		query.setLimit(MAXLOADDATANUM);
		query.order("-updatedAt");
		// 查询时间小于等于上一次查询的最旧时间
		query.addWhereLessThanOrEqualTo("updatedAt", new BmobDate(date));
		// query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
		query.addWhereEqualTo("AreaID", AreaInfoActivity.strCurSelectAreaId);
		query.findObjects(AreaMessageList.this,
				new FindListener<AreaPublishContent>() {

					@Override
					public void onError(int arg0, String arg1) {
						// 关掉进度控件
						mPullToRefreshView1.onFooterRefreshComplete();
						CommonUtils.ShowToastCenter(AreaMessageList.this,
								"加载数据失败,code:" + arg0 + "error:" + arg1,
								Toast.LENGTH_LONG);
					}

					@Override
					public void onSuccess(List<AreaPublishContent> areainfo) {

						// 保存获取到的信息数据
						publishcontent = areainfo;
						int nSize = areainfo.size();
						if (nSize > 0) {
							// 保存最旧的时间
							strOldestTime = areainfo.get(nSize - 1)
									.getCreatedAt();
							// 对数据进行去重处理
							areainfo = DeleteSameData(areainfo);
							// 合并数据
							DealLoadNearShareTaskData(areainfo);
							// 将查询到的数据显示到列表中
							mListData1 = getListData(areainfo);
							SetShareAdapter(false);
						}
						// 关掉进度控件
						mPullToRefreshView1.onFooterRefreshComplete();
					}

				});
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		BmobQuery<AreaPublishContent> query = new BmobQuery<AreaPublishContent>();
		// 一次性查询的数据条数
		query.setLimit(MAXLOADDATANUM);
		query.order("-updatedAt");
		// query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
		query.addWhereEqualTo("AreaID", AreaInfoActivity.strCurSelectAreaId);
		query.findObjects(AreaMessageList.this,
				new FindListener<AreaPublishContent>() {

					@Override
					public void onError(int arg0, String arg1) {
						// 关掉进度控件
						mPullToRefreshView1.onHeaderRefreshComplete();
						CommonUtils.ShowToastCenter(AreaMessageList.this,
								"加载数据失败,code:" + arg0 + "error:" + arg1,
								Toast.LENGTH_LONG);
					}

					@Override
					public void onSuccess(List<AreaPublishContent> areainfo) {

						// 保存获取到的信息数据
						publishcontent = areainfo;
						int nSize = areainfo.size();
						if (nSize > 0) {
							// 保存最旧的时间
							strOldestTime = areainfo.get(nSize - 1)
									.getCreatedAt();
							// 将查询到的数据显示到列表中
							mListData1 = getListData(areainfo);
							SetShareAdapter(false);
						}
						// 关掉进度控件
						mPullToRefreshView1.onHeaderRefreshComplete();
					}

				});
	}
}

package com.mike.virtuallocallife;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.mike.Utils.CommonUtils;
import com.mike.Utils.ExpressionUtil;
import com.mike.bombobject.AreaPublishContent;
import com.mike.bombobject.CommentContent;
import com.mike.bombobject.UserInfo;
import com.mike.commondata.commondata;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AreaShareMessageActivity extends Activity {
	// 发布数据信息
	AreaPublishContent content = null;
	// 存储表情图片索引的数据
	private int[] imageIds = new int[107];
	// 用于推送服务
	BmobPushManager bmobPush;
	// 发送图片的路径
	String strSendImagePath = "";
	public static final String[] addPhoto = new String[] { "从相册选择", "现在拍摄",
			"取消" };
	// 当前评论的最新时间
	private String strCommentTime = "2014-06-18 11:31:27";
	// 存储评论数据
	private List<CommentContent> mCommentInfos = null;
	// 当前评论接收用户的昵称
	private String strCurAcceptPersonNickName = "";
	// 当前评论接收用户的mac地址
	private String strCurAcceptPersonMac = "";
	// 当前评论接收用户的Installation
	private String strCurAcceptPersonInstallationID = "";
	SharedPreferences msettings = null;
	/********************** 控件对象 *************************/
	// 发布人头像控件
	ImageView personimageview;
	// 发布人名称
	TextView personnametextview;
	// 发布时间控件
	TextView publishtimetextview;
	// 发布内容的标题控件
	TextView titletextview;
	// 发布图片控件
	ImageView firstpublishimageview;
	ImageView secondpublishimageview;
	ImageView thirdpublishimageview;
	// 发布地址
	TextView publishaddresstextview;
	// 赞值控件
	TextView zantextview;
	// 评论条数控件
	TextView commenttextview;
	// 浏览次数控件
	TextView scantimetextview;
	// 显示表情的gridview控件
	GridView gridview;
	// 发送评论编辑框
	ClearEditText edittext1;
	// 显示表情窗口的linearlayout控件
	public static LinearLayout gridviewlinearlayout;
	// 文字输入框控件
	LinearLayout editlinearlayout;
	// 包含评论图标的linearlayout控件
	LinearLayout commentIconlinearlayout;
	// 返回imageview控件
	ImageView returnimageview;
	// 发送信息的button控件
	Button sendbutton;
	// 显示评论内容的linearlayout控件
	LinearLayout commentlinearlayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		bmobPush = new BmobPushManager(this);
		// 程序启动的时候避免光标处在editview中而弹出输入法窗口
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// 获取传过来的数据
		content = (AreaPublishContent) getIntent().getSerializableExtra(
				"content");
		setContentView(R.layout.areashareinfo);

		msettings = getSharedPreferences(commondata.PreferencesName, 0);
		// 对显示表情的gridview控件进行优化
		InitGridView();
		// 初始化其它控件
		InitActivity();
		// 对浏览进行记录
		RecordScanOpera();
		// 显示评论内容
		ShowComments();
	}

	private void ShowComments() {
		// 将日期字符串转换成Date
		Date date = CommonUtils.ChangeTimeToDate(strCommentTime,
				"yyyy-MM-dd HH:mm:ss");
		BmobQuery<CommentContent> query = new BmobQuery<CommentContent>();
		query.setLimit(1000);
		query.order("updatedAt");
		query.addWhereGreaterThan("updatedAt", new BmobDate(date));
		// query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
		query.addWhereEqualTo("PublishContentObjectId", content.getObjectId());
		query.findObjects(AreaShareMessageActivity.this,
				new FindListener<CommentContent>() {

					@Override
					public void onError(int arg0, String arg1) {

					}

					@Override
					public void onSuccess(List<CommentContent> areainfo) {
						int nSize = areainfo.size();
						if (nSize > 0) {
							// 更新评论最新的时间
							strCommentTime = areainfo.get(nSize - 1)
									.getUpdatedAt();
							mCommentInfos = areainfo;
							// 显示评论到界面上
							AutoCreateCommentWidows(areainfo);
						}
					}

				});

	}

	private void AutoCreateCommentWidows(List<CommentContent> comment) {

		int nCount = comment.size();
		if (nCount <= 0) {
			commenttextview.setText("暂无评论");
		} else {
			// 设置评论条数
			commenttextview.setText("" + comment.size());

			for (int i = 0; i < nCount; i++) {

				final CommentContent publishcontent = comment.get(i);

				View cfg_view = getLayoutInflater().inflate(
						R.layout.comment_item, null);

				// 图标
				ImageView imageview = (ImageView) cfg_view
						.findViewById(R.id.comment_item_imageview1);
				// 评论人名称
				TextView AccountPerosn = (TextView) cfg_view
						.findViewById(R.id.comment_item_textview1);
				// 接收评论人名称
				TextView ReceivePerson = (TextView) cfg_view
						.findViewById(R.id.comment_item_textview3);
				// 评论时间
				TextView CommentTime = (TextView) cfg_view
						.findViewById(R.id.comment_item_textview4);

				// 评论内容
				TextView Contenttext = (TextView) cfg_view
						.findViewById(R.id.comment_item_textview5);
				// 评论图片
				// ImageView largeimageview = (ImageView) cfg_view
				// .findViewById(R.id.comment_item_imageview2);

				TextView textview10 = (TextView) cfg_view
						.findViewById(R.id.comment_item_textview2);

				/***************** 开始给控件赋值 **********************/
				// 给评论发布人图片控件赋值
				ShowPersonInfoByMac(imageview,
						publishcontent.getTalkPersonName());
				// 评论人名称(昵称)
				AccountPerosn.setText(publishcontent.getTalkPersonNickName());
				// 接收评论人名称(昵称)
				ReceivePerson.setText(publishcontent
						.getAcceptTalkPersonNickName());
				// 评论时间
				CommentTime.setText(publishcontent.getUpdatedAt());
				
				/*************************显示评论内容(主要针对表情图片)**********************/
				// 将内容转换成图标形式
				SpannableString spancontent = ExpressionUtil
						.getExpressionString(
								AreaShareMessageActivity.this,
								publishcontent.getTalkTextContent(),
								commondata.zhengze);
				// 评论内容
				Contenttext.setText(spancontent);
				// 如果接收评论人为空,那么就将"回复"控件隐藏
				if (publishcontent.getAcceptTalkPersonName().equals("")) {
					textview10.setVisibility(View.GONE);
				} else {
					textview10.setVisibility(View.VISIBLE);
				}

				commentlinearlayout.addView(cfg_view);
				// 处理点击事件,这里通过点击指定的评论,从而将信息发送到指定的用户
				cfg_view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 获取当前用户的InstalltionId
						String strInst = BmobInstallation
								.getInstallationId(AreaShareMessageActivity.this);
						// 判断当前用户是否与要回复的用户是否是同一个人
						if (strInst.equals(publishcontent
								.getTalkPersonInstallationID())) {
							edittext1.setHint("点击名字回复对方");
							strCurAcceptPersonNickName = "";
							strCurAcceptPersonInstallationID = "";
							strCurAcceptPersonMac = "";
						} else {
							String strContent = "回复"
									+ publishcontent.getTalkPersonNickName();
							strCurAcceptPersonNickName = publishcontent
									.getTalkPersonNickName();
							strCurAcceptPersonInstallationID = publishcontent
									.getTalkPersonInstallationID();
							strCurAcceptPersonMac = publishcontent
									.getTalkPersonName();
							edittext1.setHint(strContent);
						}
					}
				});
			}
		}
	}

	private void InitGridView() {
		edittext1 = (ClearEditText) findViewById(R.id.areashareinfo_edittext1);
		gridviewlinearlayout = (LinearLayout) findViewById(R.id.areashareinfo_gridviewlayout);
		gridview = (GridView) findViewById(R.id.areashareinfo_gridview);
		// 界面打开时默认为隐藏
		gridviewlinearlayout.setVisibility(View.GONE);

		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		// 生成107个表情的id，封装
		for (int i = 0; i < 107; i++) {
			try {
				if (i < 10) {
					Field field = R.drawable.class.getDeclaredField("f00" + i);
					int resourceId = Integer.parseInt(field.get(null)
							.toString());
					imageIds[i] = resourceId;
				} else if (i < 100) {
					Field field = R.drawable.class.getDeclaredField("f0" + i);
					int resourceId = Integer.parseInt(field.get(null)
							.toString());
					imageIds[i] = resourceId;
				} else {
					Field field = R.drawable.class.getDeclaredField("f" + i);
					int resourceId = Integer.parseInt(field.get(null)
							.toString());
					imageIds[i] = resourceId;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("image", imageIds[i]);
			listItems.add(listItem);
		}

		SimpleAdapter simpleAdapter = new SimpleAdapter(
				AreaShareMessageActivity.this, listItems,
				R.layout.team_layout_single_expression_cell,
				new String[] { "image" }, new int[] { R.id.image });
		gridview.setAdapter(simpleAdapter);
		gridview.setGravity(Gravity.CENTER);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bitmap bitmap = null;
				bitmap = BitmapFactory.decodeResource(getResources(),
						imageIds[arg2 % imageIds.length]);
				ImageSpan imageSpan = new ImageSpan(
						AreaShareMessageActivity.this, bitmap);
				String str = null;
				if (arg2 < 10) {
					str = "f00" + arg2;
				} else if (arg2 < 100) {
					str = "f0" + arg2;
				} else {
					str = "f" + arg2;
				}
				SpannableString spannableString = new SpannableString(str);
				spannableString.setSpan(imageSpan, 0, 4,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				edittext1.append(spannableString);
			}
		});
	}

	private void InitActivity() {
		personimageview = (ImageView) findViewById(R.id.areashareinfo_imageview1);
		personnametextview = (TextView) findViewById(R.id.areashareinfo_textview1);
		publishtimetextview = (TextView) findViewById(R.id.areashareinfo_textview2);
		titletextview = (TextView) findViewById(R.id.areashareinfo_textview6);
		firstpublishimageview = (ImageView) findViewById(R.id.areashareinfo_icon1);
		secondpublishimageview = (ImageView) findViewById(R.id.areashareinfo_icon2);
		thirdpublishimageview = (ImageView) findViewById(R.id.areashareinfo_icon3);
		publishaddresstextview = (TextView) findViewById(R.id.areashareinfo_addresstextview);
		zantextview = (TextView) findViewById(R.id.areashareinfo_zantextview);
		commenttextview = (TextView) findViewById(R.id.areashareinfo_talknumtextview);
		scantimetextview = (TextView) findViewById(R.id.areashareinfo_looktimetextview);
		editlinearlayout = (LinearLayout) findViewById(R.id.areashareinfo_cleareditlinearlayout);
		commentIconlinearlayout = (LinearLayout) findViewById(R.id.areashareinfo_talklinearlayout);
		// 一开始隐藏表情窗口和编辑窗口,只有点击评论图标后才显示编辑窗口,表情窗口点击相应图标后显示
		gridviewlinearlayout.setVisibility(View.GONE);
		editlinearlayout.setVisibility(View.GONE);
		returnimageview = (ImageView) findViewById(R.id.areashareinfo_returnimageview);
		sendbutton = (Button) findViewById(R.id.areashareinfo_button1);
		commentlinearlayout = (LinearLayout) findViewById(R.id.areashareinfo_linearlayout1);
		// 开始对控件进行赋值
		if (content != null) {
			// 给用户名控件赋值
			personnametextview.setText(content.getPublishPersonNickName());
			// 开始加载头像
			ShowPersonInfoByMac(personimageview, content.getPublishPersonName());
			// 发布时间
			publishtimetextview.setText(content.getCreatedAt());
			// 标题
			titletextview.setText(content.getTextContent());
			// 发布图片
			if (content.getFirstImage() != null) {
				new DownloadImageTask().execute(content.getFirstImage()
						.getFileUrl(), content.getFirstImage().getFilename(),
						firstpublishimageview);
			}
			if (content.getSecondImage() != null) {
				new DownloadImageTask().execute(content.getSecondImage()
						.getFileUrl(), content.getSecondImage().getFilename(),
						secondpublishimageview);
			}
			if (content.getThirdImage() != null) {
				new DownloadImageTask().execute(content.getThirdImage()
						.getFileUrl(), content.getThirdImage().getFilename(),
						thirdpublishimageview);
			}
			// 发布地址
			publishaddresstextview.setText(content.getPublishAddress());
			// 赞控件
			zantextview.setText(content.getCreditValue() + "");
			// 评论
			commenttextview.setText(content.getCommentTimes() + "");
			// 浏览
			scantimetextview.setText(content.getScanTimes() + "");
		}

		// 评论图标点击响应
		commentIconlinearlayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 如果此时编辑文字控件显示,那么就隐藏它
				if (editlinearlayout.getVisibility() == View.VISIBLE) {
					editlinearlayout.setVisibility(View.GONE);
				} else {// 否则显示
					// 显示编辑框
					editlinearlayout.setVisibility(View.VISIBLE);
				}
			}

		});

		returnimageview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra("objectid", content.getObjectId());
				data.putExtra("nZanValue",
						Integer.parseInt(zantextview.getText().toString()));
				data.putExtra("nCommentValue",
						Integer.parseInt(commenttextview.getText().toString()));
				data.putExtra("nScanValue",
						Integer.parseInt(scantimetextview.getText().toString()));
				// 请求代码可以自己设置，这里设置成20
				setResult(0, data);
				// 关闭掉这个Activity
				finish();
			}

		});

		// 发送评论
		sendbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CommonUtils util = new CommonUtils(
						AreaShareMessageActivity.this);
				// 获取用户mac地址
				String strMac = util.strGetPhoneMac();
				// 获取评论内容
				String strContent = edittext1.getText().toString();
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(AreaShareMessageActivity.this,
								strContent, commondata.zhengze);

				// 如果用户没有指定评论的针对人,那么就默认为发布人,否则为指定人
				String strPublishPersonName = "";// 评论针对人(昵称)
				String strPublishPersonMac = "";// 评论针对人mac地址
				String strPublishInstalltionID = "";// 评论针对人(推送消息的ID)
				// 如果没有指定接收人
				if (strCurAcceptPersonInstallationID.equals("")) {
					// 如果评论人与接收人是同一个人
					if (content
							.getInstallationId()
							.equals(BmobInstallation
									.getInstallationId(AreaShareMessageActivity.this))) {
						strPublishPersonName = "";
						strPublishInstalltionID = "";
						strPublishPersonMac = "";
					} else {
						strPublishPersonName = content
								.getPublishPersonNickName();
						strPublishInstalltionID = content.getInstallationId();
						strPublishPersonMac = content.getPublishPersonName();
					}
				} else {
					strPublishPersonName = strCurAcceptPersonNickName;
					strPublishInstalltionID = strCurAcceptPersonInstallationID;
					strPublishPersonMac = strCurAcceptPersonMac;
				}
				//获取评论人昵称
				String strTalkNickName = msettings.getString(commondata.UserNickName,"");
				final String strPublishInstallIDtemp = strPublishInstalltionID;
				CommentContent common = new CommentContent();
				common.setPublishContentObjectId(content.getObjectId());
				common.setTalkPersonInstallationID(BmobInstallation
						.getCurrentInstallation(AreaShareMessageActivity.this)
						.getInstallationId());
				common.setTalkPersonName(strMac);
				common.setTalkPersonNickName(strTalkNickName);
				common.setAcceptTalkPersonName(strPublishPersonMac);
				common.setAcceptTalkPersonNickName(strPublishPersonName);
				common.setTalkTextContent(spannableString.toString());
				// 这里本来还有图片,但暂时没加
				// 保存评论
				common.save(AreaShareMessageActivity.this, new SaveListener() {

					@Override
					public void onFailure(int arg0, String arg1) {
						CommonUtils.ShowToastCenter(
								AreaShareMessageActivity.this, "发送失败，code:"
										+ arg0 + "error:" + arg1,
								Toast.LENGTH_LONG);
					}

					@Override
					public void onSuccess() {
						CommonUtils.ShowToastCenter(
								AreaShareMessageActivity.this, "发送成功",
								Toast.LENGTH_LONG);
						// 判断通知方是否是本人自己,如果是就不通知
						if (!strPublishInstallIDtemp.equals(BmobInstallation
								.getInstallationId(AreaShareMessageActivity.this))) {
							// 通知对方,表示有新的评论消息
							BmobPushManager bmobPush = new BmobPushManager(
									AreaShareMessageActivity.this);
							BmobQuery<BmobInstallation> query = BmobInstallation
									.getQuery();
							query.addWhereEqualTo("installationId",
									strPublishInstallIDtemp);
							bmobPush.setQuery(query);
							bmobPush.pushMessage("你有新的评论消息");
						}
					}

				});
			}
		});
	}

	private void RecordScanOpera() {
		if (content == null) {
			CommonUtils.ShowToastCenter(AreaShareMessageActivity.this, "操作失败",
					Toast.LENGTH_LONG);
		} else {
			content.increment("ScanTimes"); // 浏览次数递增1
			content.update(AreaShareMessageActivity.this,
					content.getObjectId(), new UpdateListener() {

						@Override
						public void onFailure(int arg0, String arg1) {
							CommonUtils.ShowToastCenter(
									AreaShareMessageActivity.this, "操作失败,Code:"
											+ arg0 + "error:" + arg1,
									Toast.LENGTH_LONG);
						}

						@Override
						public void onSuccess() {
							int nSanvalue = content.getScanTimes() + 1;
							scantimetextview.setText(nSanvalue + "");
						}

					});
		}
	}

	// 根据mac获取用户名称和头像同时显示
	private void ShowPersonInfoByMac(final ImageView personimageview,
			String strMac) {
		// 根据mac地址获取用户信息
		BmobQuery<UserInfo> query = new BmobQuery<UserInfo>();
		query.addQueryKeys("UserIcon,username");
		query.addWhereEqualTo("password", strMac);
		// 先从缓存获取数据，如果没有，再从网络获取
		query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
		query.findObjects(AreaShareMessageActivity.this,
				new FindListener<UserInfo>() {

					@Override
					public void onError(int arg0, String arg1) {
						// CommonUtils.ShowToastCenter(AreaShareMessageActivity.this,
						// "获取用户信息失败",
						// Toast.LENGTH_LONG);
					}

					@Override
					public void onSuccess(List<UserInfo> arg0) {
						if (arg0.size() > 0) {
							BmobFile file = arg0.get(0).getUserIcon();
							new DownloadImageTask().execute(file.getFileUrl(),
									file.getFilename(), personimageview);
						} else {
							CommonUtils.ShowToastCenter(
									AreaShareMessageActivity.this,
									"没有找到相应的用户信息", Toast.LENGTH_LONG);
						}
					}
				});
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

	// 用来动态加载用户头像图片
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

	@Override
	public void onBackPressed() {
		Intent data = new Intent();
		data.putExtra("objectid", content.getObjectId());
		data.putExtra("nZanValue",
				Integer.parseInt(zantextview.getText().toString()));
		data.putExtra("nCommentValue",
				Integer.parseInt(commenttextview.getText().toString()));
		data.putExtra("nScanValue",
				Integer.parseInt(scantimetextview.getText().toString()));
		// 请求代码可以自己设置，这里设置成20
		setResult(0, data);
		// 关闭掉这个Activity
		finish();
	}
}

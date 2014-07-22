package com.mike.virtuallocallife;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.mike.Utils.CommonUtils;
import com.mike.bombobject.UserInfo;
import com.mike.commondata.commondata;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class FinishRegeditActivity extends Activity {
	// 用户头像控件
	ImageView personimageview;
	// 电话号码控件
	EditText phoneedittext;
	// 按钮控件
	Button button;

	// 保存所选图片的路径
	private String mImgPaths = "";
	public static final String[] addPhoto = new String[] { "默认头像", "从相册选择",
			"现在拍摄", "取消" };
	CommonUtils mUtils = null;
	SharedPreferences msettings = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.regedit_name);
		mUtils = new CommonUtils(this);

		msettings = getSharedPreferences(commondata.PreferencesName, 0);
		InitActivity();
	}

	private void InitActivity() {
		personimageview = (ImageView) findViewById(R.id.regeditname_personpicture);
		phoneedittext = (EditText) findViewById(R.id.regeditname_edittext);
		button = (Button) findViewById(R.id.regeditname_finishbutton);

		personimageview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(0);
			}

		});

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 判断电话号码控件中是否有值
				if (phoneedittext.getText().toString().equals("")) {
					CommonUtils.ShowToastCenter(FinishRegeditActivity.this,
							"请输入电话号码", Toast.LENGTH_LONG);
				} else {

					if (!mImgPaths.equals("")) {
						// 首先上传用户头像
						final BmobFile bmobFile = new BmobFile(new File(
								mImgPaths));
						bmobFile.uploadblock(FinishRegeditActivity.this,
								new UploadFileListener() {

									@Override
									public void onSuccess() {
										// 图片上传成功后,开始上传其他数据
										String strUserName = msettings
												.getString(
														commondata.UserNickName,
														"");
										// 获取mac地址
										String strMac = mUtils.strGetPhoneMac();
										String strGender = msettings.getString(
												commondata.UserGender, "");
										String strPhoneNumber = phoneedittext
												.getText().toString();

										UserInfo bu = new UserInfo();
										bu.setTelPhone(strPhoneNumber);
										bu.setGender(strGender);
										bu.setUserIcon(bmobFile);
										bu.setUsername(strUserName);
										bu.setPassword(strMac);
										bu.signUp(FinishRegeditActivity.this,
												new SaveListener() {
													@Override
													public void onSuccess() {
														CommonUtils
																.ShowToastCenter(
																		FinishRegeditActivity.this,
																		"注册成功",
																		Toast.LENGTH_LONG);
														// 切换到主界面
														// 切换到下一个界面
														Intent it = new Intent(
																FinishRegeditActivity.this,
																AreaInfoActivity.class);
														startActivity(it);
														FinishRegeditActivity.this
																.finish();
													}

													@Override
													public void onFailure(
															int code, String msg) {
														CommonUtils
																.ShowToastCenter(
																		FinishRegeditActivity.this,
																		"注册失败",
																		Toast.LENGTH_LONG);
													}
												});
									}

									@Override
									public void onProgress(Integer value) {

									}

									@Override
									public void onFailure(int code, String msg) {
										CommonUtils.ShowToastCenter(
												FinishRegeditActivity.this,
												"头像上传失败", Toast.LENGTH_LONG);
									}
								});
					} else {
						// 如果没有图片,那么就上传其他数据
						String strUserName = msettings.getString(
								commondata.UserGender, "");
						// 获取mac地址
						String strMac = mUtils.strGetPhoneMac();
						String strGender = msettings.getString(
								commondata.UserNickName, "");
						String strPhoneNumber = phoneedittext.getText()
								.toString();

						UserInfo bu = new UserInfo();
						bu.setTelPhone(strPhoneNumber);
						bu.setGender(strGender);
						bu.setUsername(strUserName);
						bu.setPassword(strMac);
						bu.signUp(FinishRegeditActivity.this,
								new SaveListener() {
									@Override
									public void onSuccess() {
										CommonUtils.ShowToastCenter(
												FinishRegeditActivity.this,
												"注册成功", Toast.LENGTH_LONG);
										FinishRegeditActivity.this.finish();
									}

									@Override
									public void onFailure(int code, String msg) {
										CommonUtils.ShowToastCenter(
												FinishRegeditActivity.this,
												"注册失败", Toast.LENGTH_LONG);
									}
								});
					}
				}
			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Bitmap bitmap = null;

		// if (resultCode == RESULT_OK)
		{
			if (requestCode == 0) {
				String strImagePath = Environment.getExternalStorageDirectory()
						+ "/" + commondata.strRootFileName + "/"
						+ commondata.strPhotoParentFileName + "/"
						+ commondata.strUserImageName;
				// 先判断该文件是否存在
				File pImageFile = new File(strImagePath);
				if (pImageFile.exists()) {
					// 对图片进行旋转处理
					mUtils.rotatePhoto(strImagePath);

					File imgFile = new File(strImagePath);
					try {
						Uri imgUri = Uri
								.parse(android.provider.MediaStore.Images.Media
										.insertImage(getContentResolver(),
												imgFile.getAbsolutePath(),
												null, null));
						String[] proj = { MediaStore.Images.Media.DATA };
						@SuppressWarnings("deprecation")
						Cursor cursor = managedQuery(imgUri, proj, null, null,
								null);
						int column_index = cursor
								.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();
						String path = cursor.getString(column_index);
						mImgPaths = path;

						ContentResolver cr = this.getContentResolver();
						bitmap = BitmapFactory.decodeStream(cr
								.openInputStream(imgUri));
						personimageview.setImageBitmap(bitmap);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					Resources res = getResources();
					Bitmap bmp = BitmapFactory.decodeResource(res,
							R.drawable.noperson);
					personimageview.setImageBitmap(bmp);
				}

			} else if (requestCode == 1) {
				if (data == null) {
					return;
				}
				Uri uri = data.getData();
				// ContentResolver cr = this.getContentResolver();
				String[] proj = { MediaStore.Images.Media.DATA };
				@SuppressWarnings("deprecation")
				Cursor cursor = managedQuery(uri, proj, null, null, null);
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				String path = cursor.getString(column_index);
				mImgPaths = path;
				// 对图片进行旋转处理
				bitmap = mUtils.PhotoRotation(uri);
				personimageview.setImageBitmap(bitmap);
			}
		}
	}

	protected Dialog onCreateDialog(int id) {
		AlertDialog dialog = null;
		AlertDialog.Builder builder = null;
		switch (id) {
		case 0:
			builder = new AlertDialog.Builder(this);
			builder.setTitle("添加用户图片");
			builder.setItems(addPhoto, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 保存图片的路径
					String strPhotoPath = "";
					String haveSD = Environment.getExternalStorageState();
					if (!haveSD.equals(Environment.MEDIA_MOUNTED)) {
						CommonUtils.ShowToastCenter(FinishRegeditActivity.this,
								"存储卡不可用", Toast.LENGTH_LONG);
						return;
					}
					File dir = new File(Environment
							.getExternalStorageDirectory()
							+ "/"
							+ commondata.strRootFileName
							+ "/"
							+ commondata.strPhotoParentFileName);
					if (!dir.exists()) {
						dir.mkdirs();
					} else {// 如果已经存在了该文件夹
							// 判断该文件夹中是否存在Car.jpg文件,如果存在则删除
						strPhotoPath = Environment
								.getExternalStorageDirectory()
								+ "/"
								+ commondata.strRootFileName
								+ "/"
								+ commondata.strPhotoParentFileName
								+ "/"
								+ commondata.strUserImageName;
						File pPhotoFile = new File(strPhotoPath);
						if (pPhotoFile.exists()) {
							pPhotoFile.delete();
						}
					}

					if (0 == which) {

						String strUserName = msettings.getString(
								commondata.UserGender, "");
						// 如果使用默认头像,那么就将默认图片保存到指定路径下
						Resources res = getResources();
						Bitmap bmp = null;
						if (strUserName.equals("男")) {
							bmp = BitmapFactory.decodeResource(res,
									R.drawable.boy);
						} else {
							bmp = BitmapFactory.decodeResource(res,
									R.drawable.girl);
						}
						if (bmp != null) {
							try {
								mUtils.saveBitmapToFile(bmp, strPhotoPath);
								mImgPaths = strPhotoPath;
								personimageview.setImageBitmap(bmp);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					} else if (which == 1) {
						Intent intent = new Intent(
								"android.intent.action.GET_CONTENT");
						intent.addCategory("android.intent.category.OPENABLE");
						intent.setType("image/*");
						FinishRegeditActivity.this.startActivityForResult(
								intent, 1);
					} else if (which == 2) {
						Intent intent = new Intent(
								android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						File imgFile = new File(dir,
								commondata.strUserImageName);
						Uri u = Uri.fromFile(imgFile);
						intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
						startActivityForResult(intent, 0);
					}
				}
			});
			dialog = builder.create();
			break;

		default:
			break;
		}
		return dialog;
	}

	@Override
	public void onBackPressed() {
		// 启动RegeditActivity
		Intent it = new Intent(FinishRegeditActivity.this,
				RegeditActivity.class);
		startActivity(it);
		finish();
	}
}

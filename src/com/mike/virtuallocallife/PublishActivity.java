package com.mike.virtuallocallife;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.mike.Utils.CommonUtils;
import com.mike.bombobject.AreaPublishContent;
import com.mike.commondata.commondata;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class PublishActivity extends Activity {

	// 保存通过文件方式所选图片的路径
	// private String mImgPaths = "";
	public static final String[] addPhoto = new String[] { "从相册选择", "现在拍摄",
			"取消" };
	CommonUtils mUtils = null;
	SharedPreferences msettings = null;
	// 当前点击的ImageView控件的索引
	int nPos = 0;
	// 进度条对象
	public static ProgressDialog m_ProgressDialog = null;
	// 一下三个字符串变量分别为要发布的图片的三个文件路径
	String strFirstImagePath = "";
	String strSecondImagePath = "";
	String strThirdImagePath = "";
	// 一次性上传图片的个数
	public static final int MAXUPLOADIMAGEFILE = 3;
	// 当上传的图片个数
	int currentimagefileSize = 0;
	// 存储要上传图片的BmobFile对象
	Map<Integer, BmobFile> ImageMaps = new HashMap<Integer, BmobFile>();
	/********************* 控件变量 ***********************/
	// 用户当前位置控件
	EditText addressedittext;
	// 返回控件
	ImageView returnimageview;
	// 输入内容控件
	EditText contentedittext;
	// 第一个图片控件
	ImageView firstphotoimageview;
	// 第二个图片控件
	ImageView secondphotoimageview;
	// 第三个图片控件
	ImageView thirdphotoimageview;
	// 发布内容按钮
	Button publishbutton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.makepublic);

		mUtils = new CommonUtils(this);

		msettings = getSharedPreferences(commondata.PreferencesName, 0);

		InitActivity();
	}

	private void InitActivity() {
		addressedittext = (EditText) findViewById(R.id.makepublic_EditText2);
		// 设置用户当前位置
		addressedittext.setText(AreaInfoActivity.strUserCurPosition);
		returnimageview = (ImageView) findViewById(R.id.makepublic_returnimageview);
		contentedittext = (EditText) findViewById(R.id.makepublic_EditText1);
		firstphotoimageview = (ImageView) findViewById(R.id.makepublic_photoimageview1);
		secondphotoimageview = (ImageView) findViewById(R.id.makepublic_photoimageview2);
		thirdphotoimageview = (ImageView) findViewById(R.id.makepublic_photoimageview3);
		publishbutton = (Button) findViewById(R.id.makepublic_publishbutton);

		returnimageview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});

		firstphotoimageview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nPos = 0;
				showDialog(0);
			}

		});

		secondphotoimageview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (strFirstImagePath.equals("")) {
					CommonUtils.ShowToastCenter(PublishActivity.this,
							"请先选择第一张图片", Toast.LENGTH_LONG);
				} else {
					nPos = 1;
					showDialog(0);
				}
			}

		});

		thirdphotoimageview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (strSecondImagePath.equals("")) {
					CommonUtils.ShowToastCenter(PublishActivity.this,
							"请先选择第二张图片", Toast.LENGTH_LONG);
				} else {
					nPos = 2;
					showDialog(0);
				}
			}

		});

		publishbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				m_ProgressDialog = ProgressDialog.show(PublishActivity.this,
						"", "发布中,请等待...", true);
				m_ProgressDialog.setCancelable(true);

				insertBatchDatasWithMany();
			}

		});
	}

	/**
	 * 此方法用来发布内容
	 * 
	 * @Title: insertBatchDatasWithOne
	 * @throws
	 */
	private void insertBatchDatasWithMany() {
		File[] fs = {null,null,null};//new File[MAXUPLOADIMAGEFILE];
		currentimagefileSize = 0;
		ImageMaps.clear();
		int nCount = 0;
		String strName;
		//计算发送图片的个数
		if (!strFirstImagePath.equals("")) {
			fs[0] = new File(strFirstImagePath);
			strName = strFirstImagePath;
			nCount++;
			if (!strSecondImagePath.equals("")) {
				fs[1] = new File(strSecondImagePath);
				strName = strSecondImagePath;
				nCount++;
				if (!strThirdImagePath.equals("")) {
					fs[2] = new File(strThirdImagePath);
					strName = strThirdImagePath;
					nCount++;
				}
			}
		}

		for (int i = 0; i < nCount; i++) {
			uploadSongFile(i, nCount, fs[i]);
		}
	}

	private void uploadSongFile(final int i, final int nCount, File file) {
		final BmobFile bmobFile = new BmobFile(file);
		bmobFile.uploadblock(this, new UploadFileListener() {
			@Override
			public void onSuccess() {
				currentimagefileSize++;
				ImageMaps.put(i, bmobFile);
				if (currentimagefileSize == nCount) {
					AreaPublishContent areapublish = new AreaPublishContent();
					areapublish.setTextContent(contentedittext.getText()
							.toString());
					CommonUtils util = new CommonUtils(PublishActivity.this);
					String strMac = util.strGetPhoneMac();
					areapublish.setAreaID(AreaInfoActivity.strCurSelectAreaId);
					areapublish.setPublishPersonName(strMac);
					areapublish
							.setPublishAddress(AreaInfoActivity.strUserCurPosition);
					areapublish.setScanTimes(0);
					areapublish.setCommentTimes(0);
					areapublish.setCreditValue(0);
					// 将图片插入
					int nSize = ImageMaps.size();
					for (int j = 0; j < nSize; j++) {
						if (0 == j) {
							areapublish.setFirstImage(ImageMaps.get(0));
						} else if (1 == j) {
							areapublish.setSecondImage(ImageMaps.get(1));
						} else if (2 == j) {
							areapublish.setThirdImage(ImageMaps.get(2));
						}
					}
					// 插入操作
					insertObject(areapublish);
				}
			}

			@Override
			public void onProgress(Integer arg0) {
			}

			@Override
			public void onFailure(int code, String arg0) {
				// 取消等待框
				if (m_ProgressDialog != null) {
					m_ProgressDialog.dismiss();
					m_ProgressDialog = null;
				}
				CommonUtils.ShowToastCenter(PublishActivity.this, "发布失败",
						Toast.LENGTH_LONG);
			}
		});

	}

	/** 创建操作
	  * insertObject
	  * @return void
	  * @throws
	  */
	private void insertObject(final BmobObject obj){
		obj.save(PublishActivity.this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				// 取消等待框
				if (m_ProgressDialog != null) {
					m_ProgressDialog.dismiss();
					m_ProgressDialog = null;
				}
				CommonUtils.ShowToastCenter(PublishActivity.this,
						"发布成功", Toast.LENGTH_LONG);
				PublishActivity.this.finish();
			}
			
			@Override
			public void onFailure(int code, String arg0) {
				// 取消等待框
				if (m_ProgressDialog != null) {
					m_ProgressDialog.dismiss();
					m_ProgressDialog = null;
				}
				CommonUtils.ShowToastCenter(PublishActivity.this,
						"发布失败", Toast.LENGTH_LONG);
			}
		});
	}

	private ImageView GetImageViewByIndex() {
		ImageView imageview = null;
		int nImageViewIndex = nPos;
		if (0 == nImageViewIndex) {
			imageview = firstphotoimageview;
		} else if (1 == nImageViewIndex) {
			imageview = secondphotoimageview;
		} else if (2 == nImageViewIndex) {
			imageview = thirdphotoimageview;
		}
		return imageview;
	}

	// 获取当前拍照图片的路径
	private void CreateImagePathByCamera(String strPath) {
		if (0 == nPos) {
			strFirstImagePath = strPath;
		} else if (1 == nPos) {
			strSecondImagePath = strPath;
		} else if (2 == nPos) {
			strThirdImagePath = strPath;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Bitmap bitmap = null;
		// ImageView的对象
		ImageView imageview = null;
		// 获取相应的imagview对象
		imageview = GetImageViewByIndex();
		// 如果imageview为null,那么就直接返回(一般情况下不会出现这种情况)
		if (null == imageview) {
			return;
		}

		// if (resultCode == RESULT_OK)
		{
			if (requestCode == 0) {// 通过拍照获取的照片

				// 获取图片名称
				String strImageName = GetImagePath();
				String strImagePath = Environment.getExternalStorageDirectory()
						+ "/" + commondata.strParentFileName + "/"
						+ strImageName;
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
						// mImgPaths = path;

						// 照片获取成功后将相应的路径保存
						CreateImagePathByCamera(path);

						ContentResolver cr = this.getContentResolver();
						bitmap = BitmapFactory.decodeStream(cr
								.openInputStream(imgUri));
						imageview.setImageBitmap(bitmap);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					CommonUtils.ShowToastCenter(PublishActivity.this,
							"图片不存在,请重新获取", Toast.LENGTH_LONG);
				}

			} else if (requestCode == 1) {// 表示通过现有的文件获取的照片
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
				// mImgPaths = path;
				CreateImagePathByCamera(path);
				// 对图片进行旋转处理
				bitmap = mUtils.PhotoRotation(uri);
				imageview.setImageBitmap(bitmap);
			}
		}
	}

	// 根据点击的ImageView控件获取该图片的存储图片文件名称(只有在通过拍照获取照片的情况,通过文件就是图片文件原来的地址)
	private String GetImagePath() {
		String strImageFileName = "";
		if (0 == nPos) {
			strImageFileName = commondata.strPublishFirstImageName;
		} else if (1 == nPos) {
			strImageFileName = commondata.strPublishSecondImageName;
		} else if (2 == nPos) {
			strImageFileName = commondata.strPublishThirdImageName;
		}
		return strImageFileName;
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

					if (which == 0) {// 通过文件获取照片
						Intent intent = new Intent(
								"android.intent.action.GET_CONTENT");
						intent.addCategory("android.intent.category.OPENABLE");
						intent.setType("image/*");
						PublishActivity.this.startActivityForResult(intent, 1);
					} else if (which == 1) {// 通过拍照获取照片

						// 首先获取照片存储的图片名称
						String strImageName = GetImagePath();

						// 保存图片的路径
						String strPhotoPath = "";
						String haveSD = Environment.getExternalStorageState();
						if (!haveSD.equals(Environment.MEDIA_MOUNTED)) {
							CommonUtils.ShowToastCenter(PublishActivity.this,
									"存储卡不可用", Toast.LENGTH_LONG);
							return;
						}
						File dir = new File(Environment
								.getExternalStorageDirectory()
								+ "/"
								+ commondata.strParentFileName);
						if (!dir.exists()) {
							dir.mkdirs();
						} else {// 如果已经存在了该文件夹
							strPhotoPath = Environment
									.getExternalStorageDirectory()
									+ "/"
									+ commondata.strParentFileName
									+ "/"
									+ strImageName;
							File pPhotoFile = new File(strPhotoPath);
							if (pPhotoFile.exists()) {
								pPhotoFile.delete();
							}
						}

						Intent intent = new Intent(
								android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						File imgFile = new File(dir, strImageName);
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
		// 取消等待框
		if (m_ProgressDialog != null) {
			m_ProgressDialog.dismiss();
			m_ProgressDialog = null;
		}
		finish();
	}
}

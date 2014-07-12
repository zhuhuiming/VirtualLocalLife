package com.mike.virtuallocallife;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

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

	// ����ͨ���ļ���ʽ��ѡͼƬ��·��
	// private String mImgPaths = "";
	public static final String[] addPhoto = new String[] { "�����ѡ��", "��������",
			"ȡ��" };
	CommonUtils mUtils = null;
	SharedPreferences msettings = null;
	// ��ǰ�����ImageView�ؼ�������
	int nPos = 0;
	// ����������
	public static ProgressDialog m_ProgressDialog = null;
	// һ�������ַ��������ֱ�ΪҪ������ͼƬ�������ļ�·��
	String strFirstImagePath = "";
	String strSecondImagePath = "";
	String strThirdImagePath = "";
	/********************* �ؼ����� ***********************/
	// �û���ǰλ�ÿؼ�
	EditText addressedittext;
	// ���ؿؼ�
	ImageView returnimageview;
	// �������ݿؼ�
	EditText contentedittext;
	// ��һ��ͼƬ�ؼ�
	ImageView firstphotoimageview;
	// �ڶ���ͼƬ�ؼ�
	ImageView secondphotoimageview;
	// ������ͼƬ�ؼ�
	ImageView thirdphotoimageview;
	// �������ݰ�ť
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
		// �����û���ǰλ��
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
							"����ѡ���һ��ͼƬ", Toast.LENGTH_LONG);
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
							"����ѡ��ڶ���ͼƬ", Toast.LENGTH_LONG);
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
						"��ʾ", "������,��ȴ�...", true);
				m_ProgressDialog.setCancelable(true);

				final List<BmobFile> ImageFiles = new ArrayList<BmobFile>();
				// �жϵ�һ���ؼ��Ƿ�ѡ����ͼƬ
				if (!strFirstImagePath.equals("")) {
					final BmobFile bmobFile1 = new BmobFile(new File(
							strFirstImagePath));
					// �ϴ���һ��ͼƬ
					bmobFile1.uploadblock(PublishActivity.this,
							new UploadFileListener() {

								@Override
								public void onFailure(int arg0, String arg1) {
									// ȡ���ȴ���
									if (m_ProgressDialog != null) {
										m_ProgressDialog.dismiss();
										m_ProgressDialog = null;
									}
									CommonUtils.ShowToastCenter(
											PublishActivity.this, "����ʧ��,code:"
													+ arg0 + "error:" + arg1,
											Toast.LENGTH_LONG);
									return;
								}

								@Override
								public void onProgress(Integer arg0) {
								}

								@Override
								public void onSuccess() {
									ImageFiles.add(bmobFile1);
									// �жϵڶ����ؼ��Ƿ�ѡ����ͼƬ
									if (!strSecondImagePath.equals("")) {
										final BmobFile bmobFile2 = new BmobFile(
												new File(strSecondImagePath));
										// �ϴ��ڶ���ͼƬ
										bmobFile2.uploadblock(
												PublishActivity.this,
												new UploadFileListener() {

													@Override
													public void onFailure(
															int arg0,
															String arg1) {
														// ȡ���ȴ���
														if (m_ProgressDialog != null) {
															m_ProgressDialog
																	.dismiss();
															m_ProgressDialog = null;
														}
														CommonUtils
																.ShowToastCenter(
																		PublishActivity.this,
																		"����ʧ��,code:"
																				+ arg0
																				+ "error:"
																				+ arg1,
																		Toast.LENGTH_LONG);
														return;
													}

													@Override
													public void onProgress(
															Integer arg0) {

													}

													@Override
													public void onSuccess() {
														ImageFiles.add(bmobFile2);
														// �жϵ������ؼ��Ƿ�ѡ����ͼƬ
														if (!strThirdImagePath
																.equals("")) {
															final BmobFile bmobFile3 = new BmobFile(
																	new File(
																			strThirdImagePath));
															// �ϴ�������ͼƬ
															bmobFile3
																	.uploadblock(
																			PublishActivity.this,
																			new UploadFileListener() {

																				@Override
																				public void onFailure(
																						int arg0,
																						String arg1) {
																					// ȡ���ȴ���
																					if (m_ProgressDialog != null) {
																						m_ProgressDialog
																								.dismiss();
																						m_ProgressDialog = null;
																					}
																					CommonUtils
																							.ShowToastCenter(
																									PublishActivity.this,
																									"����ʧ��,code:"
																											+ arg0
																											+ "error:"
																											+ arg1,
																									Toast.LENGTH_LONG);
																					return;
																				}

																				@Override
																				public void onProgress(
																						Integer arg0) {

																				}

																				@Override
																				public void onSuccess() {
																					ImageFiles
																							.add(bmobFile3);
																					PublishContent(ImageFiles);
																				}

																			});
														} else {// ���û��ѡ�������ͼƬ
															PublishContent(ImageFiles);
														}
													}

												});
									} else {// ���û��ѡ��ڶ���ͼƬ
										PublishContent(ImageFiles);
									}
								}

							});
				} else {// ���û��ѡ��ͼƬ
					PublishContent(ImageFiles);
				}

			}

		});
	}

	// ��������
	private void PublishContent(List<BmobFile> ImageFiles) {
		// ��ʼ�ϴ���������
		AreaPublishContent areapublish = new AreaPublishContent();
		areapublish.setTextContent(contentedittext.getText().toString());
		CommonUtils util = new CommonUtils(PublishActivity.this);
		String strMac = util.strGetPhoneMac();
		areapublish.setPublishPersonName(strMac);
		areapublish.setPublishAddress(AreaInfoActivity.strUserCurPosition);
		areapublish.setScanTimes(0);
		areapublish.setCommentTimes(0);
		areapublish.setCreditValue(0);
		//��ͼƬ����
		if (ImageFiles != null) {
			int nSize = ImageFiles.size();
			for (int i = 0; i < nSize; i++) {
				if (0 == i) {
					areapublish.setFirstImage(ImageFiles.get(0));
				} else if (1 == i) {
					areapublish.setSecondImage(ImageFiles.get(1));
				} else if (2 == i) {
					areapublish.setThirdImage(ImageFiles.get(2));
				}
			}
		}

		areapublish.save(PublishActivity.this, new SaveListener() {
			@Override
			public void onFailure(int arg0, String arg1) {
				// ȡ���ȴ���
				if (m_ProgressDialog != null) {
					m_ProgressDialog.dismiss();
					m_ProgressDialog = null;
				}
				CommonUtils.ShowToastCenter(PublishActivity.this, "����ʧ��",
						Toast.LENGTH_LONG);
			}

			@Override
			public void onSuccess() {
				// ȡ���ȴ���
				if (m_ProgressDialog != null) {
					m_ProgressDialog.dismiss();
					m_ProgressDialog = null;
				}
				CommonUtils.ShowToastCenter(PublishActivity.this, "�����ɹ�",
						Toast.LENGTH_LONG);
				PublishActivity.this.finish();
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

	// ��ȡ��ǰ����ͼƬ��·��
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
		// ImageView�Ķ���
		ImageView imageview = null;
		// ��ȡ��Ӧ��imagview����
		imageview = GetImageViewByIndex();
		// ���imageviewΪnull,��ô��ֱ�ӷ���(һ������²�������������)
		if (null == imageview) {
			return;
		}

		// if (resultCode == RESULT_OK)
		{
			if (requestCode == 0) {// ͨ�����ջ�ȡ����Ƭ

				// ��ȡͼƬ����
				String strImageName = GetImagePath();
				String strImagePath = Environment.getExternalStorageDirectory()
						+ "/" + commondata.strParentFileName + "/"
						+ strImageName;
				// ���жϸ��ļ��Ƿ����
				File pImageFile = new File(strImagePath);
				if (pImageFile.exists()) {
					// ��ͼƬ������ת����
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

						// ��Ƭ��ȡ�ɹ�����Ӧ��·������
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
							"ͼƬ������,�����»�ȡ", Toast.LENGTH_LONG);
				}

			} else if (requestCode == 1) {// ��ʾͨ�����е��ļ���ȡ����Ƭ
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
				// ��ͼƬ������ת����
				bitmap = mUtils.PhotoRotation(uri);
				imageview.setImageBitmap(bitmap);
			}
		}
	}

	// ���ݵ����ImageView�ؼ���ȡ��ͼƬ�Ĵ洢ͼƬ�ļ�����(ֻ����ͨ�����ջ�ȡ��Ƭ�����,ͨ���ļ�����ͼƬ�ļ�ԭ���ĵ�ַ)
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
			builder.setTitle("����û�ͼƬ");
			builder.setItems(addPhoto, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					if (which == 0) {// ͨ���ļ���ȡ��Ƭ
						Intent intent = new Intent(
								"android.intent.action.GET_CONTENT");
						intent.addCategory("android.intent.category.OPENABLE");
						intent.setType("image/*");
						PublishActivity.this.startActivityForResult(intent, 1);
					} else if (which == 1) {// ͨ�����ջ�ȡ��Ƭ

						// ���Ȼ�ȡ��Ƭ�洢��ͼƬ����
						String strImageName = GetImagePath();

						// ����ͼƬ��·��
						String strPhotoPath = "";
						String haveSD = Environment.getExternalStorageState();
						if (!haveSD.equals(Environment.MEDIA_MOUNTED)) {
							CommonUtils.ShowToastCenter(PublishActivity.this,
									"�洢��������", Toast.LENGTH_LONG);
							return;
						}
						File dir = new File(Environment
								.getExternalStorageDirectory()
								+ "/"
								+ commondata.strParentFileName);
						if (!dir.exists()) {
							dir.mkdirs();
						} else {// ����Ѿ������˸��ļ���
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
		// ȡ���ȴ���
		if (m_ProgressDialog != null) {
			m_ProgressDialog.dismiss();
			m_ProgressDialog = null;
		}
		finish();
	}
}

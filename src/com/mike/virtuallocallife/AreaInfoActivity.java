package com.mike.virtuallocallife;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapDoubleClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.mike.Utils.CommonUtils;
import com.mike.bombobject.AreaInfo;
import com.mike.bombobject.UserInfo;
import com.mike.commondata.DegreeDatas;
import com.mike.commondata.NinePolygonData;
import com.mike.commondata.commondata;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

public class AreaInfoActivity extends Activity implements
		OnGetGeoCoderResultListener {

	SharedPreferences msettings = null;
	// ��λ���
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	BitmapDescriptor mCurrentMarker;
	//�û���ǰ���ڵ�λ��
	public static String strUserCurPosition = "";

	MapView mMapView;
	BaiduMap mBaiduMap;
	private UiSettings mUiSettings;
	boolean isFirstLoc = true;// �Ƿ��״ζ�λ

	GeoCoder mSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��

	int nCurPosxIndex = -1;// �û���ǰ����λ�õ�x��������������
	int nCurPosyIndex = -1;// �û���ǰ����λ�õ�y��������������
	double dCurLongitude = 0;// �û���ǰ����λ�õľ���
	double dCurLatitude = 0;// �û���ǰ����λ�õ�γ��
	private Marker mMarkerA;// �Ź����м䷽��ͼ���־
	// �û���ǰ���������x��y��������������
	int nClickPosxIndex = -1;
	int nClickPosyIndex = -1;
	// ��ǰѡ�������id��
	String strCurSelectAreaId = "";

	/***********************�ؼ�����**************************/
	// ռ���̿ؼ�
	LinearLayout linearlayout1;
	// ռ���̰�ť�ؼ�
	Button capturebutton;
	// ��ʾ������Ϣ�ؼ�
	LinearLayout linearlayout2;
	// ��ʾ��ַ�Ŀؼ�
	TextView addresstextview;
	// ��ʾ��ǰλ�õĿؼ�
	LinearLayout locatelinearlayout;
	// �������ƿؼ�
	TextView areanametextview;
	// ��ʾ�ؼ�
	TextView prompttextview;
	RelativeLayout relativelayout;
	// ��ʾ�û����ͼƬ����ַ�Ŀؼ�
	TextView curuseraddresstextview;
	// ��Ϊ���ؾ���Ŀؼ�
	TextView makepersontextview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		msettings = getSharedPreferences(commondata.PreferencesName, 0);
		InitActivity();
		// ��ͼ��ʼ��
		mMapView = (MapView) findViewById(R.id.main_bmapView);
		mBaiduMap = mMapView.getMap();
		mUiSettings = mBaiduMap.getUiSettings();
		// ����ת�����š����ӡ�ָ����ȹ��ܽ�ֹ��
		SetDisableFunction();
		// �������ż���
		perfomZoom(13.5f);
		// ��ʼ������ģ�飬ע���¼�����
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		// �������ſؼ�
		HideZoomCotroller();
		// ���ذٶ�logo
		HideLogo();

		// ������λͼ��
		mBaiduMap.setMyLocationEnabled(true);
		// ��λ��ʼ��
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(99999999);
		option.setAddrType("all");
		mLocClient.setLocOption(option);
		mLocClient.start();
		initListener();
		// ��������ͼ��ͼƬ��Ϣ
		DealClickImage();
	}

	private void InitActivity() {
		linearlayout1 = (LinearLayout) findViewById(R.id.activitymain_linearlayout1);
		linearlayout2 = (LinearLayout) findViewById(R.id.activitymain_linearlayout2);
		capturebutton = (Button) findViewById(R.id.activitymain_capturebutton);
		addresstextview = (TextView) findViewById(R.id.activitymain_addresstextview);
		locatelinearlayout = (LinearLayout) findViewById(R.id.activitymain_locatelinearlayout);
		areanametextview = (TextView) findViewById(R.id.activitymain_areanametextview);
		prompttextview = (TextView) findViewById(R.id.activitymain_prompttextview);
		relativelayout = (RelativeLayout) findViewById(R.id.activitymain_relativlayout);
		curuseraddresstextview = (TextView) findViewById(R.id.activitymain_addresstextview1);
		makepersontextview = (TextView) findViewById(R.id.activitymain_makepersontextview);

		capturebutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// if(nCurPosxIndex > 0 && nCurPosyIndex > 0){
				// ���澭γ��������
				SharedPreferences.Editor editor = msettings.edit();
				editor.putString(commondata.XIndex, nCurPosxIndex + "");
				editor.putString(commondata.YIndex, nCurPosyIndex + "");
				editor.commit();
				Intent it = new Intent(AreaInfoActivity.this,
						CaptureAreaActivity.class);
				startActivityForResult(it, 2);
				// }else{
				// CommonUtils.ShowToastCenter(AreaInfoActivity.this, "��Ч����",
				// Toast.LENGTH_LONG);
				// }
			}

		});

		locatelinearlayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ���¶�λ
				LatLng ll = new LatLng(dCurLatitude, dCurLongitude);
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}

		});

		// ��Ϊָ���������Ĳ���
		makepersontextview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(AreaInfoActivity.this);
				builder.setMessage("ȷ��Ҫ��Ϊ�õصľ�����?");
				builder.setTitle("��ʾ");
				builder.setPositiveButton("ȷ��",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								CommonUtils util = new CommonUtils(
										AreaInfoActivity.this);
								// �����û���mac��ַ��ȡ�û���Ϣ���е�objectId�� �����������
								String strMac = util.strGetPhoneMac();
								BmobQuery<UserInfo> UserInfo = new BmobQuery<UserInfo>();
								UserInfo.addQueryKeys("objectId,UserLiveID");
								UserInfo.addWhereEqualTo("password", strMac);
								UserInfo.findObjects(AreaInfoActivity.this,
										new FindListener<UserInfo>() {

											@Override
											public void onError(int code,
													String Errormsg) {
												CommonUtils.ShowToastCenter(
														AreaInfoActivity.this,
														"ʵ�ڱ�Ǹ,���ִ���,code:"
																+ code + " "
																+ Errormsg,
														Toast.LENGTH_LONG);
											}

											@Override
											public void onSuccess(
													List<UserInfo> arg0) {
												// �ж��Ƿ��ѯ����id��
												if (arg0.size() > 0) {
													// ���ж��û��Ƿ��Ѿ��Ǹ�����ľ���
													if (strCurSelectAreaId
															.equals(arg0
																	.get(0)
																	.getUserLiveID())) {
														CommonUtils
																.ShowToastCenter(
																		AreaInfoActivity.this,
																		"���Ѿ��Ǹ�����ľ�����",
																		Toast.LENGTH_LONG);
													} else {
														// �ɹ�֮�󽫻�ȡ��������id��д�뵽�û���Ϣ����
														UserInfo UserInfo = BmobUser.getCurrentUser(AreaInfoActivity.this, UserInfo.class);
														UserInfo.setUserLiveID(strCurSelectAreaId);
														UserInfo.update(
																AreaInfoActivity.this,
																arg0.get(0)
																		.getObjectId(),
																new UpdateListener() {

																	@Override
																	public void onSuccess() {
																		CommonUtils
																				.ShowToastCenter(
																						AreaInfoActivity.this,
																						"��ӭ����Ϊ�õ����ľ���",
																						Toast.LENGTH_LONG);
																	}

																	@Override
																	public void onFailure(
																			int code,
																			String msg) {
																		CommonUtils
																				.ShowToastCenter(
																						AreaInfoActivity.this,
																						"ʵ�ڱ�Ǹ,����ʧ��,code:"
																								+ code
																								+ " "
																								+ msg,
																						Toast.LENGTH_LONG);
																	}
																});
													}
												} else {
													CommonUtils
															.ShowToastCenter(
																	AreaInfoActivity.this,
																	"û���ҵ�������",
																	Toast.LENGTH_LONG);
												}
											}

										});
							}
						});
				builder.setNegativeButton("ȡ��",
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
			}

		});
	}

	/**
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null)
				return;

			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				// ���浱ǰ��γ��
				dCurLongitude = location.getLongitude();
				dCurLatitude = location.getLatitude();

				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
				// ���ݾ�γ�ȼ�������������
				// ����dx��ȡx�᷽�������
				nCurPosxIndex = commondata
						.GetWestToEastIndexByLon(ll.longitude);
				// ����dy��ȡy�᷽�������
				nCurPosyIndex = commondata
						.GetNorthToSouthIndexByLat(ll.latitude);
				// ��ʾ��ǰλ��
				addresstextview.setText(location.getAddrStr());
				strUserCurPosition = location.getAddrStr();
				// ���ݵ�ǰ���ڵľ�γ�ȷ��������Ż�ȡ��ǰ��������������Ϣ
				DealCurAreaInfo();
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	private void DrawLine(LatLng ln1, LatLng ln2) {
		List<LatLng> points = new ArrayList<LatLng>();
		points.add(ln1);
		points.add(ln2);
		OverlayOptions ooPolyline = new PolylineOptions().width(5)
				.color(0xAA0066FF).points(points);
		mBaiduMap.addOverlay(ooPolyline);
	}

	private void SetDisableFunction() {
		// �����Ž�ֹ��
		mUiSettings.setZoomGesturesEnabled(false);
		// ����ת��ֹ��
		mUiSettings.setRotateGesturesEnabled(false);
		// ��ָ�����ֹ��
		mUiSettings.setCompassEnabled(false);
		// �����ӽ�ֹ��
		mUiSettings.setOverlookingGesturesEnabled(false);
	}

	// �������ſؼ�
	private void HideZoomCotroller() {
		int childCount = mMapView.getChildCount();

		View zoom = null;

		for (int i = 0; i < childCount; i++) {

			View child = mMapView.getChildAt(i);

			if (child instanceof ZoomControls) {

				zoom = child;

				break;

			}

		}

		zoom.setVisibility(View.GONE);
	}

	// ���ذٶ�logo child instanceof ImageView
	private void HideLogo() {
		int childCount = mMapView.getChildCount();

		View zoom = null;

		for (int i = 0; i < childCount; i++) {

			View child = mMapView.getChildAt(i);

			if (child instanceof ImageView) {

				zoom = child;

				break;

			}

		}

		zoom.setVisibility(View.GONE);
	}

	/**
	 * �������� sdk ���ż���Χ�� [3.0,19.0]
	 */
	private void perfomZoom(float zoomLevel) {
		try {
			MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoomLevel);
			mBaiduMap.setMapStatus(u);
		} catch (NumberFormatException e) {
			Toast.makeText(this, "��������ȷ�����ż���", Toast.LENGTH_SHORT).show();
		}
	}

	private void initListener() {

		//��ͼ״̬�ı���
		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {

			@Override
			public void onMapStatusChange(MapStatus arg0) {
				if (mBaiduMap != null) {
					// ����һ���ڵ�ͼ�ϻ���ͼ�����
					mBaiduMap.clear();
				}
			}

			@Override
			public void onMapStatusChangeFinish(MapStatus arg0) {

				// ����dx��ȡx�᷽�������
				int nIndexX = commondata
						.GetWestToEastIndexByLon(arg0.target.longitude);
				// ����dy��ȡy�᷽�������
				int nIndexY = commondata
						.GetNorthToSouthIndexByLat(arg0.target.latitude);

				// ����nIndexX��nIndexY�����������½Ǻ��ұ߾�γ��
				commondata.GetDegrees(nIndexX, nIndexY);
				// �����½ǵľ�γ��ת������Ļ��������
				LatLng ln = new LatLng(DegreeDatas.letfbottomposlat,
						DegreeDatas.letfbottomposlon);
				// ���ұߵ�ľ�γ��ת������Ļ��������
				LatLng ln1 = new LatLng(DegreeDatas.righttopposlat,
						DegreeDatas.righttopposlon);
				// �������ľ�γ��ת������Ļ��������
				LatLng ln2 = new LatLng(DegreeDatas.upposlat,
						DegreeDatas.upposlon);

				Point pt = mBaiduMap.getProjection().toScreenLocation(ln);
				Point pt1 = mBaiduMap.getProjection().toScreenLocation(ln1);
				Point pt2 = mBaiduMap.getProjection().toScreenLocation(ln2);

				// �ı��ε��ĸ���������
				Point RectPt1 = new Point();
				Point RectPt2 = new Point();
				Point RectPt3 = new Point();
				Point RectPt4 = new Point();

				// ����x����λ����߿�����ش�С
				int nPixel = pt1.x - pt.x;
				// ����y����λ����߿����ش�С
				int nPixel1 = pt.y - pt2.y;

				Point pttemp = pt;
				// ��ȡҪ����ֱ������(�ܹ�Ҫ��12��ֱ��),�����Ͻǿ�ʼ
				pt.x = pttemp.x - nPixel;
				pt.y = pttemp.y - 2 * nPixel1;
				pt1.x = pt1.x + nPixel;
				pt1.y = pt1.y - 2 * nPixel1;
				// ����һ��ֱ��
				DrawLine(mBaiduMap.getProjection().fromScreenLocation(pt),
						mBaiduMap.getProjection().fromScreenLocation(pt1));

				RectPt1.x = pt.x;
				RectPt1.y = pt.y;
				RectPt2.x = pt1.x;
				RectPt2.y = pt1.y;
				// ����pt��ȡ�Ÿ����ӵĵ�����
				NinePolygonData.StoreNineGridPos(pt, nPixel, nPixel1);

				// ��ʼ���ڶ���ֱ��
				pt.x = pt.x;
				pt.y = pt.y + nPixel1;
				pt1.x = pt1.x;
				pt1.y = pt1.y + nPixel1;
				DrawLine(mBaiduMap.getProjection().fromScreenLocation(pt),
						mBaiduMap.getProjection().fromScreenLocation(pt1));

				// ��ʼ��������ֱ��
				pt.x = pt.x;
				pt.y = pt.y + nPixel1;
				pt1.x = pt1.x;
				pt1.y = pt1.y + nPixel1;
				DrawLine(mBaiduMap.getProjection().fromScreenLocation(pt),
						mBaiduMap.getProjection().fromScreenLocation(pt1));

				// ��ʼ��������ֱ��
				pt.x = pt.x;
				pt.y = pt.y + nPixel1;
				pt1.x = pt1.x;
				pt1.y = pt1.y + nPixel1;
				DrawLine(mBaiduMap.getProjection().fromScreenLocation(pt),
						mBaiduMap.getProjection().fromScreenLocation(pt1));
				RectPt3.x = pt.x;
				RectPt3.y = pt.y;
				RectPt4.x = pt1.x;
				RectPt4.y = pt1.y;
				// ��ʼ��������ֱ��
				pt.x = pt.x;
				pt.y = pt.y;
				pt1.x = pt.x;
				pt1.y = pt.y - 3 * nPixel1;
				DrawLine(mBaiduMap.getProjection().fromScreenLocation(pt),
						mBaiduMap.getProjection().fromScreenLocation(pt1));

				// ��ʼ��������ֱ��
				pt.x = pt.x + nPixel;
				pt.y = pt.y;
				pt1.x = pt1.x + nPixel;
				pt1.y = pt1.y;
				DrawLine(mBaiduMap.getProjection().fromScreenLocation(pt),
						mBaiduMap.getProjection().fromScreenLocation(pt1));

				// ��ʼ��������ֱ��
				pt.x = pt.x + nPixel;
				pt.y = pt.y;
				pt1.x = pt1.x + nPixel;
				pt1.y = pt1.y;
				DrawLine(mBaiduMap.getProjection().fromScreenLocation(pt),
						mBaiduMap.getProjection().fromScreenLocation(pt1));

				// ��ʼ���ڰ���ֱ��
				pt.x = pt.x + nPixel;
				pt.y = pt.y;
				pt1.x = pt1.x + nPixel;
				pt1.y = pt1.y;
				DrawLine(mBaiduMap.getProjection().fromScreenLocation(pt),
						mBaiduMap.getProjection().fromScreenLocation(pt1));

				// ��Ӷ����
				LatLng latpt1 = mBaiduMap.getProjection().fromScreenLocation(
						RectPt1);
				LatLng latpt2 = mBaiduMap.getProjection().fromScreenLocation(
						RectPt2);
				LatLng latpt3 = mBaiduMap.getProjection().fromScreenLocation(
						RectPt3);
				LatLng latpt4 = mBaiduMap.getProjection().fromScreenLocation(
						RectPt4);
				List<LatLng> pts = new ArrayList<LatLng>();
				pts.add(latpt1);
				pts.add(latpt2);
				pts.add(latpt4);
				pts.add(latpt3);
				OverlayOptions ooPolygon = new PolygonOptions().points(pts)
						.stroke(new Stroke(5, 0xAA0066FF))
						.fillColor(0x55000000);
				mBaiduMap.addOverlay(ooPolygon);

				LatLng Fifthlatpt1 = mBaiduMap.getProjection()
						.fromScreenLocation(
								new Point(NinePolygonData.dFifthRightPosX
										- nPixel / 2,
										NinePolygonData.dFifthRightPosY
												- nPixel1 / 2));
				OverlayOptions oo = new MarkerOptions().icon(
						BitmapDescriptorFactory
								.fromResource(R.drawable.ic_launcher))
						.position(Fifthlatpt1);
				mMarkerA = (Marker) (mBaiduMap.addOverlay(oo));
			}

			@Override
			public void onMapStatusChangeStart(MapStatus arg0) {
				if (mBaiduMap != null) {
					// ����һ���ڵ�ͼ�ϻ���ͼ�����
					mBaiduMap.clear();
				}
			}

		});

		// ��Ե�ͼ�ϵĵ���¼�������Ӧ
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			public void onMapClick(LatLng point) {
				// ��������ͼ�¼�
				DealClickAndShowInfo(point);
			}

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}
		});
		
		//˫����ͼ
		mBaiduMap.setOnMapDoubleClickListener(new OnMapDoubleClickListener(){

			@Override
			public void onMapDoubleClick(LatLng arg0) {
				//����ָ����������
				Intent it = new Intent(AreaInfoActivity.this,AreaMessageList.class);
				startActivity(it);
			}
			
		});
	}

	// ���ݵ�ַ��ȡ��γ�ȵĻص�����
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			CommonUtils.ShowToastCenter(AreaInfoActivity.this, "��Ǹ��δ���ҵ����",
					Toast.LENGTH_LONG);
		}
	}

	// ���ݾ�γ�Ȼ�ȡ��ַ�Ļص�����
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			CommonUtils.ShowToastCenter(AreaInfoActivity.this, "��Ǹ��δ���ҵ����",
					Toast.LENGTH_LONG);
		}
		curuseraddresstextview.setText(result.getAddress());

	}

	// ���ݾ�γ�ȷ��������Ż�ȡ������ǰ����ID��
	private void DealCurAreaInfo() {
		// ����mac��ַ��ȡ�û���Ϣ
		BmobQuery<AreaInfo> query = new BmobQuery<AreaInfo>();
		query.addQueryKeys("AreaName,objectId");
		query.addWhereEqualTo("XIndex", nCurPosxIndex);
		query.addWhereEqualTo("YIndex", nCurPosyIndex);
		query.findObjects(AreaInfoActivity.this, new FindListener<AreaInfo>() {
			@Override
			public void onSuccess(List<AreaInfo> object) {
				if (object.size() > 0) {
					// ����ҵ���
					linearlayout1.setVisibility(View.GONE);
					linearlayout2.setVisibility(View.VISIBLE);
					areanametextview.setText(object.get(0).getAreaName());
					strCurSelectAreaId = object.get(0).getObjectId();
				} else {
					// ��ʱ˵���������û��������,�û����Զ���������������
					linearlayout1.setVisibility(View.VISIBLE);
					relativelayout.setVisibility(View.GONE);
					prompttextview.setVisibility(View.VISIBLE);
					linearlayout2.setVisibility(View.GONE);
				}
			}

			@Override
			public void onError(int code, String Errormsg) {
				CommonUtils.ShowToastCenter(AreaInfoActivity.this, "���ִ���,code:"
						+ code + " " + Errormsg, Toast.LENGTH_LONG);
			}
		});
	}

	private void DealClickImage() {
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(final Marker marker) {

				final LatLng ll = marker.getPosition();

				if (marker == mMarkerA) {
					DealClickAndShowInfo(ll);
				}
				return true;
			}
		});
	}

	// ��������ͼ�¼�,��ʾ��Ӧ����Ϣ
	private void DealClickAndShowInfo(final LatLng ll) {
		// ����dx��ȡx�᷽�������
		nClickPosxIndex = commondata.GetWestToEastIndexByLon(ll.longitude);
		// ����dy��ȡy�᷽�������
		nClickPosyIndex = commondata.GetNorthToSouthIndexByLat(ll.latitude);
		// ����������ȡ����������Ϣ
		BmobQuery<AreaInfo> query = new BmobQuery<AreaInfo>();
		query.addQueryKeys("AreaName");
		query.addWhereEqualTo("XIndex", nClickPosxIndex);
		query.addWhereEqualTo("YIndex", nClickPosyIndex);
		query.findObjects(AreaInfoActivity.this, new FindListener<AreaInfo>() {
			@Override
			public void onSuccess(List<AreaInfo> object) {
				// �ж������Ƿ����û���ǰλ����ͬ
				if (nCurPosxIndex == nClickPosxIndex
						&& nCurPosyIndex == nClickPosyIndex) {
					if (object.size() > 0) {
						// ����ҵ���
						linearlayout1.setVisibility(View.GONE);
						linearlayout2.setVisibility(View.VISIBLE);
						curuseraddresstextview.setVisibility(View.GONE);
						areanametextview.setText(object.get(0).getAreaName());
						strCurSelectAreaId = object.get(0).getObjectId();
					} else {
						// ��ʱ˵���������û��������,�û����Զ���������������
						linearlayout1.setVisibility(View.VISIBLE);
						relativelayout.setVisibility(View.GONE);
						prompttextview.setVisibility(View.VISIBLE);
						linearlayout2.setVisibility(View.GONE);
						curuseraddresstextview.setVisibility(View.VISIBLE);
						// ��Geo����
						mSearch.reverseGeoCode(new ReverseGeoCodeOption()
								.location(ll));
					}
				} else {// ����û������ڸ������ڲ�
					if (object.size() > 0) {
						// ����ҵ���
						linearlayout1.setVisibility(View.GONE);
						linearlayout2.setVisibility(View.VISIBLE);
						curuseraddresstextview.setVisibility(View.GONE);
						areanametextview.setText(object.get(0).getAreaName());
						strCurSelectAreaId = object.get(0).getObjectId();
					} else {
						// ��ʱ˵���������û��������,�û����Զ���������������
						linearlayout1.setVisibility(View.VISIBLE);
						relativelayout.setVisibility(View.GONE);
						prompttextview.setVisibility(View.VISIBLE);
						linearlayout2.setVisibility(View.GONE);
						curuseraddresstextview.setVisibility(View.VISIBLE);
						// ��Geo����
						mSearch.reverseGeoCode(new ReverseGeoCodeOption()
								.location(ll));
					}
				}
			}

			@Override
			public void onError(int code, String Errormsg) {
				CommonUtils.ShowToastCenter(AreaInfoActivity.this, "���ִ���,code:"
						+ code + " " + Errormsg, Toast.LENGTH_LONG);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	@Override
	protected void onPause() {
		// mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// �˳�ʱ���ٶ�λ
		mLocClient.stop();
		// �رն�λͼ��
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}
}

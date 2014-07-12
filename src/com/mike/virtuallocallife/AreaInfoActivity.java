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
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	BitmapDescriptor mCurrentMarker;
	//用户当前所在的位置
	public static String strUserCurPosition = "";

	MapView mMapView;
	BaiduMap mBaiduMap;
	private UiSettings mUiSettings;
	boolean isFirstLoc = true;// 是否首次定位

	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

	int nCurPosxIndex = -1;// 用户当前所在位置的x坐标区域索引号
	int nCurPosyIndex = -1;// 用户当前所在位置的y坐标区域索引号
	double dCurLongitude = 0;// 用户当前所在位置的经度
	double dCurLatitude = 0;// 用户当前所在位置的纬度
	private Marker mMarkerA;// 九宫格中间方格图标标志
	// 用户当前点击的区域x与y方向区域索引号
	int nClickPosxIndex = -1;
	int nClickPosyIndex = -1;
	// 当前选择区域的id号
	String strCurSelectAreaId = "";

	/***********************控件变量**************************/
	// 占地盘控件
	LinearLayout linearlayout1;
	// 占地盘按钮控件
	Button capturebutton;
	// 显示区域信息控件
	LinearLayout linearlayout2;
	// 显示地址的控件
	TextView addresstextview;
	// 显示当前位置的控件
	LinearLayout locatelinearlayout;
	// 区域名称控件
	TextView areanametextview;
	// 提示控件
	TextView prompttextview;
	RelativeLayout relativelayout;
	// 显示用户点击图片处地址的控件
	TextView curuseraddresstextview;
	// 成为当地居民的控件
	TextView makepersontextview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		msettings = getSharedPreferences(commondata.PreferencesName, 0);
		InitActivity();
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.main_bmapView);
		mBaiduMap = mMapView.getMap();
		mUiSettings = mBaiduMap.getUiSettings();
		// 将旋转、缩放、俯视、指南针等功能禁止掉
		SetDisableFunction();
		// 设置缩放级别
		perfomZoom(13.5f);
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		// 隐藏缩放控件
		HideZoomCotroller();
		// 隐藏百度logo
		HideLogo();

		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(99999999);
		option.setAddrType("all");
		mLocClient.setLocOption(option);
		mLocClient.start();
		initListener();
		// 处理点击地图上图片消息
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
				// 保存经纬度索引号
				SharedPreferences.Editor editor = msettings.edit();
				editor.putString(commondata.XIndex, nCurPosxIndex + "");
				editor.putString(commondata.YIndex, nCurPosyIndex + "");
				editor.commit();
				Intent it = new Intent(AreaInfoActivity.this,
						CaptureAreaActivity.class);
				startActivityForResult(it, 2);
				// }else{
				// CommonUtils.ShowToastCenter(AreaInfoActivity.this, "无效区域",
				// Toast.LENGTH_LONG);
				// }
			}

		});

		locatelinearlayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 重新定位
				LatLng ll = new LatLng(dCurLatitude, dCurLongitude);
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}

		});

		// 成为指定区域居民的操作
		makepersontextview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(AreaInfoActivity.this);
				builder.setMessage("确定要成为该地的居民吗?");
				builder.setTitle("提示");
				builder.setPositiveButton("确认",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								CommonUtils util = new CommonUtils(
										AreaInfoActivity.this);
								// 根据用户的mac地址获取用户信息表中的objectId号 和所属区域号
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
														"实在抱歉,出现错误,code:"
																+ code + " "
																+ Errormsg,
														Toast.LENGTH_LONG);
											}

											@Override
											public void onSuccess(
													List<UserInfo> arg0) {
												// 判断是否查询到了id号
												if (arg0.size() > 0) {
													// 先判断用户是否已经是该区域的居民
													if (strCurSelectAreaId
															.equals(arg0
																	.get(0)
																	.getUserLiveID())) {
														CommonUtils
																.ShowToastCenter(
																		AreaInfoActivity.this,
																		"您已经是该区域的居民了",
																		Toast.LENGTH_LONG);
													} else {
														// 成功之后将获取到的区域id号写入到用户信息表中
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
																						"欢迎您成为该地区的居民",
																						Toast.LENGTH_LONG);
																	}

																	@Override
																	public void onFailure(
																			int code,
																			String msg) {
																		CommonUtils
																				.ShowToastCenter(
																						AreaInfoActivity.this,
																						"实在抱歉,操作失败,code:"
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
																	"没有找到该区域",
																	Toast.LENGTH_LONG);
												}
											}

										});
							}
						});
				builder.setNegativeButton("取消",
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
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;

			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				// 保存当前经纬度
				dCurLongitude = location.getLongitude();
				dCurLatitude = location.getLatitude();

				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
				// 根据经纬度计算区域索引号
				// 根据dx获取x轴方向的索引
				nCurPosxIndex = commondata
						.GetWestToEastIndexByLon(ll.longitude);
				// 根据dy获取y轴方向的索引
				nCurPosyIndex = commondata
						.GetNorthToSouthIndexByLat(ll.latitude);
				// 显示当前位置
				addresstextview.setText(location.getAddrStr());
				strUserCurPosition = location.getAddrStr();
				// 根据当前所在的经纬度方向索引号获取当前所在区域的相关信息
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
		// 将缩放禁止掉
		mUiSettings.setZoomGesturesEnabled(false);
		// 将旋转禁止掉
		mUiSettings.setRotateGesturesEnabled(false);
		// 将指南针禁止掉
		mUiSettings.setCompassEnabled(false);
		// 将俯视禁止掉
		mUiSettings.setOverlookingGesturesEnabled(false);
	}

	// 隐藏缩放控件
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

	// 隐藏百度logo child instanceof ImageView
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
	 * 处理缩放 sdk 缩放级别范围： [3.0,19.0]
	 */
	private void perfomZoom(float zoomLevel) {
		try {
			MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoomLevel);
			mBaiduMap.setMapStatus(u);
		} catch (NumberFormatException e) {
			Toast.makeText(this, "请输入正确的缩放级别", Toast.LENGTH_SHORT).show();
		}
	}

	private void initListener() {

		//地图状态改变了
		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {

			@Override
			public void onMapStatusChange(MapStatus arg0) {
				if (mBaiduMap != null) {
					// 将上一次在地图上画的图层清除
					mBaiduMap.clear();
				}
			}

			@Override
			public void onMapStatusChangeFinish(MapStatus arg0) {

				// 根据dx获取x轴方向的索引
				int nIndexX = commondata
						.GetWestToEastIndexByLon(arg0.target.longitude);
				// 根据dy获取y轴方向的索引
				int nIndexY = commondata
						.GetNorthToSouthIndexByLat(arg0.target.latitude);

				// 根据nIndexX与nIndexY计算区域左下角和右边经纬度
				commondata.GetDegrees(nIndexX, nIndexY);
				// 将左下角的经纬度转换成屏幕像素坐标
				LatLng ln = new LatLng(DegreeDatas.letfbottomposlat,
						DegreeDatas.letfbottomposlon);
				// 将右边点的经纬度转换成屏幕像素坐标
				LatLng ln1 = new LatLng(DegreeDatas.righttopposlat,
						DegreeDatas.righttopposlon);
				// 将上面点的经纬度转换成屏幕像素坐标
				LatLng ln2 = new LatLng(DegreeDatas.upposlat,
						DegreeDatas.upposlon);

				Point pt = mBaiduMap.getProjection().toScreenLocation(ln);
				Point pt1 = mBaiduMap.getProjection().toScreenLocation(ln1);
				Point pt2 = mBaiduMap.getProjection().toScreenLocation(ln2);

				// 四边形的四个像素坐标
				Point RectPt1 = new Point();
				Point RectPt2 = new Point();
				Point RectPt3 = new Point();
				Point RectPt4 = new Point();

				// 计算x方向单位区域边框的像素大小
				int nPixel = pt1.x - pt.x;
				// 计算y方向单位区域边框像素大小
				int nPixel1 = pt.y - pt2.y;

				Point pttemp = pt;
				// 获取要画的直线坐标(总共要画12条直线),从左上角开始
				pt.x = pttemp.x - nPixel;
				pt.y = pttemp.y - 2 * nPixel1;
				pt1.x = pt1.x + nPixel;
				pt1.y = pt1.y - 2 * nPixel1;
				// 画第一条直线
				DrawLine(mBaiduMap.getProjection().fromScreenLocation(pt),
						mBaiduMap.getProjection().fromScreenLocation(pt1));

				RectPt1.x = pt.x;
				RectPt1.y = pt.y;
				RectPt2.x = pt1.x;
				RectPt2.y = pt1.y;
				// 根据pt获取九个格子的点坐标
				NinePolygonData.StoreNineGridPos(pt, nPixel, nPixel1);

				// 开始画第二条直线
				pt.x = pt.x;
				pt.y = pt.y + nPixel1;
				pt1.x = pt1.x;
				pt1.y = pt1.y + nPixel1;
				DrawLine(mBaiduMap.getProjection().fromScreenLocation(pt),
						mBaiduMap.getProjection().fromScreenLocation(pt1));

				// 开始画第三条直线
				pt.x = pt.x;
				pt.y = pt.y + nPixel1;
				pt1.x = pt1.x;
				pt1.y = pt1.y + nPixel1;
				DrawLine(mBaiduMap.getProjection().fromScreenLocation(pt),
						mBaiduMap.getProjection().fromScreenLocation(pt1));

				// 开始画第四条直线
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
				// 开始画第五条直线
				pt.x = pt.x;
				pt.y = pt.y;
				pt1.x = pt.x;
				pt1.y = pt.y - 3 * nPixel1;
				DrawLine(mBaiduMap.getProjection().fromScreenLocation(pt),
						mBaiduMap.getProjection().fromScreenLocation(pt1));

				// 开始画第六条直线
				pt.x = pt.x + nPixel;
				pt.y = pt.y;
				pt1.x = pt1.x + nPixel;
				pt1.y = pt1.y;
				DrawLine(mBaiduMap.getProjection().fromScreenLocation(pt),
						mBaiduMap.getProjection().fromScreenLocation(pt1));

				// 开始画第七条直线
				pt.x = pt.x + nPixel;
				pt.y = pt.y;
				pt1.x = pt1.x + nPixel;
				pt1.y = pt1.y;
				DrawLine(mBaiduMap.getProjection().fromScreenLocation(pt),
						mBaiduMap.getProjection().fromScreenLocation(pt1));

				// 开始画第八条直线
				pt.x = pt.x + nPixel;
				pt.y = pt.y;
				pt1.x = pt1.x + nPixel;
				pt1.y = pt1.y;
				DrawLine(mBaiduMap.getProjection().fromScreenLocation(pt),
						mBaiduMap.getProjection().fromScreenLocation(pt1));

				// 添加多边形
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
					// 将上一次在地图上画的图层清除
					mBaiduMap.clear();
				}
			}

		});

		// 针对地图上的点击事件进行响应
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			public void onMapClick(LatLng point) {
				// 处理点击地图事件
				DealClickAndShowInfo(point);
			}

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}
		});
		
		//双击地图
		mBaiduMap.setOnMapDoubleClickListener(new OnMapDoubleClickListener(){

			@Override
			public void onMapDoubleClick(LatLng arg0) {
				//进入指定的区域中
				Intent it = new Intent(AreaInfoActivity.this,AreaMessageList.class);
				startActivity(it);
			}
			
		});
	}

	// 根据地址获取经纬度的回调函数
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			CommonUtils.ShowToastCenter(AreaInfoActivity.this, "抱歉，未能找到结果",
					Toast.LENGTH_LONG);
		}
	}

	// 根据经纬度获取地址的回调函数
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			CommonUtils.ShowToastCenter(AreaInfoActivity.this, "抱歉，未能找到结果",
					Toast.LENGTH_LONG);
		}
		curuseraddresstextview.setText(result.getAddress());

	}

	// 根据经纬度方向索引号获取所处当前区域ID号
	private void DealCurAreaInfo() {
		// 根据mac地址获取用户信息
		BmobQuery<AreaInfo> query = new BmobQuery<AreaInfo>();
		query.addQueryKeys("AreaName,objectId");
		query.addWhereEqualTo("XIndex", nCurPosxIndex);
		query.addWhereEqualTo("YIndex", nCurPosyIndex);
		query.findObjects(AreaInfoActivity.this, new FindListener<AreaInfo>() {
			@Override
			public void onSuccess(List<AreaInfo> object) {
				if (object.size() > 0) {
					// 如果找到了
					linearlayout1.setVisibility(View.GONE);
					linearlayout2.setVisibility(View.VISIBLE);
					areanametextview.setText(object.get(0).getAreaName());
					strCurSelectAreaId = object.get(0).getObjectId();
				} else {
					// 此时说明这个区域还没有人命名,用户可以对这个区域进行命名
					linearlayout1.setVisibility(View.VISIBLE);
					relativelayout.setVisibility(View.GONE);
					prompttextview.setVisibility(View.VISIBLE);
					linearlayout2.setVisibility(View.GONE);
				}
			}

			@Override
			public void onError(int code, String Errormsg) {
				CommonUtils.ShowToastCenter(AreaInfoActivity.this, "出现错误,code:"
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

	// 处理点击地图事件,显示相应的信息
	private void DealClickAndShowInfo(final LatLng ll) {
		// 根据dx获取x轴方向的索引
		nClickPosxIndex = commondata.GetWestToEastIndexByLon(ll.longitude);
		// 根据dy获取y轴方向的索引
		nClickPosyIndex = commondata.GetNorthToSouthIndexByLat(ll.latitude);
		// 根据索引获取这个区域的信息
		BmobQuery<AreaInfo> query = new BmobQuery<AreaInfo>();
		query.addQueryKeys("AreaName");
		query.addWhereEqualTo("XIndex", nClickPosxIndex);
		query.addWhereEqualTo("YIndex", nClickPosyIndex);
		query.findObjects(AreaInfoActivity.this, new FindListener<AreaInfo>() {
			@Override
			public void onSuccess(List<AreaInfo> object) {
				// 判断索引是否与用户当前位置相同
				if (nCurPosxIndex == nClickPosxIndex
						&& nCurPosyIndex == nClickPosyIndex) {
					if (object.size() > 0) {
						// 如果找到了
						linearlayout1.setVisibility(View.GONE);
						linearlayout2.setVisibility(View.VISIBLE);
						curuseraddresstextview.setVisibility(View.GONE);
						areanametextview.setText(object.get(0).getAreaName());
						strCurSelectAreaId = object.get(0).getObjectId();
					} else {
						// 此时说明这个区域还没有人命名,用户可以对这个区域进行命名
						linearlayout1.setVisibility(View.VISIBLE);
						relativelayout.setVisibility(View.GONE);
						prompttextview.setVisibility(View.VISIBLE);
						linearlayout2.setVisibility(View.GONE);
						curuseraddresstextview.setVisibility(View.VISIBLE);
						// 反Geo搜索
						mSearch.reverseGeoCode(new ReverseGeoCodeOption()
								.location(ll));
					}
				} else {// 如果用户不是在该区域内部
					if (object.size() > 0) {
						// 如果找到了
						linearlayout1.setVisibility(View.GONE);
						linearlayout2.setVisibility(View.VISIBLE);
						curuseraddresstextview.setVisibility(View.GONE);
						areanametextview.setText(object.get(0).getAreaName());
						strCurSelectAreaId = object.get(0).getObjectId();
					} else {
						// 此时说明这个区域还没有人命名,用户可以对这个区域进行命名
						linearlayout1.setVisibility(View.VISIBLE);
						relativelayout.setVisibility(View.GONE);
						prompttextview.setVisibility(View.VISIBLE);
						linearlayout2.setVisibility(View.GONE);
						curuseraddresstextview.setVisibility(View.VISIBLE);
						// 反Geo搜索
						mSearch.reverseGeoCode(new ReverseGeoCodeOption()
								.location(ll));
					}
				}
			}

			@Override
			public void onError(int code, String Errormsg) {
				CommonUtils.ShowToastCenter(AreaInfoActivity.this, "出现错误,code:"
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
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}
}

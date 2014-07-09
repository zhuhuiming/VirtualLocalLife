package com.mike.virtuallocallife;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.mike.Utils.CommonUtils;
import com.mike.commondata.DegreeDatas;
import com.mike.commondata.NinePolygonData;
import com.mike.commondata.commondata;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;

public class AreaInfoActivity extends Activity implements
OnGetGeoCoderResultListener {
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	BitmapDescriptor mCurrentMarker;

	MapView mMapView;
	BaiduMap mBaiduMap;
	private UiSettings mUiSettings;
	boolean isFirstLoc = true;// 是否首次定位

	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

	int nCurPosxIndex = 0;// 用户当前所在位置的x坐标区域索引号
	int nCurPosyIndex = 0;// 用户当前所在位置的y坐标区域索引号

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);
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
		mLocClient.setLocOption(option);
		mLocClient.start();
		initListener();
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
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
				// 根据经纬度计算区域索引号
				// 根据dx获取x轴方向的索引
				nCurPosxIndex = commondata
						.GetWestToEastIndexByLon(ll.longitude);
				// 根据dy获取y轴方向的索引
				nCurPosyIndex = commondata
						.GetNorthToSouthIndexByLat(ll.latitude);
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

		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {

			@Override
			public void onMapStatusChange(MapStatus arg0) {
				if (mBaiduMap != null) {
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
				LatLng ln2 = new LatLng(DegreeDatas.upposlat, DegreeDatas.upposlon);

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
				mBaiduMap.addOverlay(oo);
			}

			@Override
			public void onMapStatusChangeStart(MapStatus arg0) {
				if (mBaiduMap != null) {
					mBaiduMap.clear();
				}
			}

		});

		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			public void onMapClick(LatLng point) {

				// 根据dx获取x轴方向的索引
				int nIndexX = commondata
						.GetWestToEastIndexByLon(point.longitude);
				// 根据dy获取y轴方向的索引
				int nIndexY = commondata
						.GetNorthToSouthIndexByLat(point.latitude);
				// 将经纬度转换成像素坐标
				// Point pt = mBaiduMap.getProjection().toScreenLocation(point);
				// 判断索引是否与用户当前位置相同
				if (nCurPosxIndex == nIndexX && nCurPosyIndex == nIndexY) {
					CommonUtils.ShowToastCenter(AreaInfoActivity.this,
							"你可以对该地区进行命名", Toast.LENGTH_LONG);
				} else {
					CommonUtils.ShowToastCenter(AreaInfoActivity.this,
							"你不可以对该地区进行命名", Toast.LENGTH_LONG);
				}
				// 反Geo搜索
				mSearch.reverseGeoCode(new ReverseGeoCodeOption()
						.location(point));
			}

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}
		});
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(AreaInfoActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
		}
		/*
		 * mBaiduMap.clear(); mBaiduMap.addOverlay(new
		 * MarkerOptions().position(result.getLocation())
		 * .icon(BitmapDescriptorFactory .fromResource(R.drawable.icon_marka)));
		 * mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
		 * .getLocation())); String strInfo = String.format("纬度：%f 经度：%f",
		 * result.getLocation().latitude, result.getLocation().longitude);
		 * Toast.makeText(AreaInfoActivity.this, strInfo, Toast.LENGTH_LONG).show();
		 */
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(AreaInfoActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
		}
		/*
		 * mBaiduMap.clear(); mBaiduMap.addOverlay(new
		 * MarkerOptions().position(result.getLocation())
		 * .icon(BitmapDescriptorFactory .fromResource(R.drawable.icon_marka)));
		 * mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
		 * .getLocation())); Toast.makeText(AreaInfoActivity.this,
		 * result.getAddress(), Toast.LENGTH_LONG).show();
		 */
		CommonUtils.ShowToastCenter(AreaInfoActivity.this, result.getAddress(),
				Toast.LENGTH_LONG);

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

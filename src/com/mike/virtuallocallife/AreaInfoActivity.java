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
	// ��λ���
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	BitmapDescriptor mCurrentMarker;

	MapView mMapView;
	BaiduMap mBaiduMap;
	private UiSettings mUiSettings;
	boolean isFirstLoc = true;// �Ƿ��״ζ�λ

	GeoCoder mSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��

	int nCurPosxIndex = 0;// �û���ǰ����λ�õ�x��������������
	int nCurPosyIndex = 0;// �û���ǰ����λ�õ�y��������������

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);
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
		mLocClient.setLocOption(option);
		mLocClient.start();
		initListener();
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
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
				// ���ݾ�γ�ȼ�������������
				// ����dx��ȡx�᷽�������
				nCurPosxIndex = commondata
						.GetWestToEastIndexByLon(ll.longitude);
				// ����dy��ȡy�᷽�������
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

		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {

			@Override
			public void onMapStatusChange(MapStatus arg0) {
				if (mBaiduMap != null) {
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
				LatLng ln2 = new LatLng(DegreeDatas.upposlat, DegreeDatas.upposlon);

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

				// ����dx��ȡx�᷽�������
				int nIndexX = commondata
						.GetWestToEastIndexByLon(point.longitude);
				// ����dy��ȡy�᷽�������
				int nIndexY = commondata
						.GetNorthToSouthIndexByLat(point.latitude);
				// ����γ��ת������������
				// Point pt = mBaiduMap.getProjection().toScreenLocation(point);
				// �ж������Ƿ����û���ǰλ����ͬ
				if (nCurPosxIndex == nIndexX && nCurPosyIndex == nIndexY) {
					CommonUtils.ShowToastCenter(AreaInfoActivity.this,
							"����ԶԸõ�����������", Toast.LENGTH_LONG);
				} else {
					CommonUtils.ShowToastCenter(AreaInfoActivity.this,
							"�㲻���ԶԸõ�����������", Toast.LENGTH_LONG);
				}
				// ��Geo����
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
			Toast.makeText(AreaInfoActivity.this, "��Ǹ��δ���ҵ����", Toast.LENGTH_LONG)
					.show();
		}
		/*
		 * mBaiduMap.clear(); mBaiduMap.addOverlay(new
		 * MarkerOptions().position(result.getLocation())
		 * .icon(BitmapDescriptorFactory .fromResource(R.drawable.icon_marka)));
		 * mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
		 * .getLocation())); String strInfo = String.format("γ�ȣ�%f ���ȣ�%f",
		 * result.getLocation().latitude, result.getLocation().longitude);
		 * Toast.makeText(AreaInfoActivity.this, strInfo, Toast.LENGTH_LONG).show();
		 */
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(AreaInfoActivity.this, "��Ǹ��δ���ҵ����", Toast.LENGTH_LONG)
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
		// �˳�ʱ���ٶ�λ
		mLocClient.stop();
		// �رն�λͼ��
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}
}

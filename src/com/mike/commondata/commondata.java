package com.mike.commondata;

public class commondata {
	
	//�����ڱ��ص�Preferences����
	public static final String PreferencesName = "VirtualLocalLifePreferences";
	//�û��Ա���Ϣ
	public static final String UserGender = "gender";
	//�û��ǳ�
	public static final String UserNickName = "usernickname";
	//�û���ǰ����λ��X����
	public static final String XIndex = "xindex";
	//�û���ǰ����λ��Y����
	public static final String YIndex = "yindex";
	
	// �й���ͼ���ϽǾ�γ��
	public static final double lefttopposx = 73.55;
	public static final double lefttopposy = 53.55;
	// �й���ͼ���ϽǾ�γ��
	public static final double righttopposx = 135.083;
	public static final double righttopposy = 53.55;
	// �й���ͼ���½Ǿ�γ��
	public static final double leftbottomposx = 73.55;
	public static final double leftbottomposy = 3.85;
	// �й���ͼ���½Ǿ�γ��
	public static final double rightbottomposx = 135.083;
	public static final double rightbottomposy = 3.85;

	// ����뾶(ǧ��)
	public final static double EARTH_RADIUS_KM = 6378.137;

	// ��λ����(ǧ��)
	public final static double UNIT_LENGHT = 3;
	// ÿһ�����(����)
	public final static double UNIT_DEGREELON = 0.025;
	// ÿһ�����(γ��)
	public final static double UNIT_DEGREELAT = 0.02;

	// �й��������
	public final static double WESTTOEAST = 3939.1562;
	// �й��ϱ�����
	public final static double SOUTHTONORTH = 5532.5787;
	// pi��ֵ
	public final static double PI = 3.1415926;
	//������������id����λ��
	public final static int DIGITNUM = 6;

	public static double getDistance(double lng1, double lat1, double lng2,
			double lat2) {

		double radLat1 = Math.toRadians(lat1);

		double radLat2 = Math.toRadians(lat2);

		double radLng1 = Math.toRadians(lng1);

		double radLng2 = Math.toRadians(lng2);

		double deltaLat = radLat1 - radLat2;

		double deltaLng = radLng1 - radLng2;

		double distance = 2 * Math.asin(Math.sqrt(Math.pow(

		Math.sin(deltaLat / 2), 2)

		+ Math.cos(radLat1)

		* Math.cos(radLat2)

		* Math.pow(Math.sin(deltaLng / 2), 2)));

		distance = distance * EARTH_RADIUS_KM;

		long nvalue = Math.round(distance * 10000);
		distance = (double) nvalue / 10000;

		return distance;

	}

	// ���ݶ�������ľ�������÷���������������ֵ(��1��ʼ)
	public static int GetWestToEastIndex(double dDistance) {
		int nIndex = 1;
		int nTemp = (int) ((dDistance * 1000) % (UNIT_LENGHT * 1000));
		// ���������
		if (nTemp > 0) {
			nIndex = (int) (dDistance / UNIT_LENGHT + 1);
		} else {
			nIndex = (int) (dDistance / UNIT_LENGHT);
		}
		return nIndex;
	}

	// �����ϱ�����ľ�������÷���������������ֵ(��1��ʼ)
	public static int GetNorthToSouthIndex(double dDistance) {
		int nIndex = 1;
		int nTemp = (int) ((dDistance * 1000) % (UNIT_LENGHT * 1000));
		// ���������
		if (nTemp > 0) {
			nIndex = (int) (dDistance / UNIT_LENGHT + 1);
		} else {
			nIndex = (int) (dDistance / UNIT_LENGHT);
		}
		return nIndex;
	}

	// �����ϱ������γ�Ȳ����÷���������������ֵ(��1��ʼ)
	public static int GetNorthToSouthIndexByLat(double lat) {
		int nIndex = 1;
		// �����γ�����й������϶˵�γ�Ȳ�ֵ
		double dislat = lat - leftbottomposy;

		double dTemp = dislat / UNIT_DEGREELAT;
		// ��ȡdTemp������
		int nTemp = (int) dTemp;
		// ���������
		if (dTemp == nTemp) {
			nIndex = nTemp;
		} else {
			nIndex = nTemp + 1;
		}
		return nIndex;
	}

	// ���ݶ�������ľ��Ȳ����÷���������������ֵ(��1��ʼ)
	public static int GetWestToEastIndexByLon(double lon) {
		int nIndex = 1;
		// ����þ������й������϶˵ľ��Ȳ�ֵ
		double dislon = lon - leftbottomposx;

		double dTemp = dislon / UNIT_DEGREELON;
		// ��ȡdTemp������
		int nTemp = (int) dTemp;
		// ���������
		if (dTemp == nTemp) {
			nIndex = nTemp;
		} else {
			nIndex = nTemp + 1;
		}
		return nIndex;
	}

	// ������������������½����ұߵ�ľ�γ��(����nx��ʾx��������,ny��ʾy��������)
	public static DegreeDatas GetDegrees(double nx, double ny) {
		DegreeDatas data = new DegreeDatas(0, 0, 0, 0, 0, 0);
		DegreeDatas.letfbottomposlon = (nx - 1) * UNIT_DEGREELON
				+ leftbottomposx;
		DegreeDatas.letfbottomposlat = (ny - 1) * UNIT_DEGREELAT
				+ leftbottomposy;

		DegreeDatas.righttopposlon = (nx) * UNIT_DEGREELON + leftbottomposx;
		DegreeDatas.righttopposlat = DegreeDatas.letfbottomposlat;

		DegreeDatas.upposlon = DegreeDatas.letfbottomposlon;
		DegreeDatas.upposlat = (ny) * UNIT_DEGREELAT + leftbottomposy;
		return data;
	}
}

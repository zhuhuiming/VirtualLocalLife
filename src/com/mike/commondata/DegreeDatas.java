package com.mike.commondata;

public class DegreeDatas {

	public DegreeDatas(double x1, double y1, double x2, double y2, double x3, double y3) {
		letfbottomposlon = x1;
		letfbottomposlat = y1;
		righttopposlon = x2;
		righttopposlat = y2;
		upposlon = x3;
		upposlat = y3;
	}

	// 左下角经纬度
	public static double letfbottomposlon = 0;// 经度
	public static double letfbottomposlat = 0;// 纬度

	// 左下角右边点经纬度
	public static double righttopposlon = 0;// 经度
	public static double righttopposlat = 0;// 纬度

	// 左下角正上方点经纬度
	public static double upposlon = 0;// 经度
	public static double upposlat = 0;// 纬度
}

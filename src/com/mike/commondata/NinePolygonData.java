package com.mike.commondata;

import android.graphics.Point;
import android.graphics.Rect;

//存储九个格子的左上角与右下角像素值
public class NinePolygonData {

	/************* 第一个格子 *****************/
	// 左上角像素值(以左上角为参照)
	public static int dFirstLeftPosX;
	public static int dFirstLeftPosY;
	// 右下角像素值
	public static int dFirstRightPosX;
	public static int dFirstRightPosY;

	/************* 第二个格子 *****************/
	// 左上角像素值
	public static int dSecondLeftPosX;
	public static int dSecondLeftPosY;
	// 右下角像素值
	public static int dSecondRightPosX;
	public static int dSecondRightPosY;

	/************* 第三个格子 *****************/
	// 左上角像素值(以左上角为参照)
	public static int dThirdLeftPosX;
	public static int dThirdLeftPosY;
	// 右下角像素值
	public static int dThirdRightPosX;
	public static int dThirdRightPosY;

	/************* 第四个格子 *****************/
	// 左上角像素值
	public static int dForthLeftPosX;
	public static int dForthLeftPosY;
	// 右下角像素值
	public static int dForthRightPosX;
	public static int dForthRightPosY;

	/************* 第五个格子 *****************/
	// 左上角像素值(以左上角为参照)
	public static int dFifthLeftPosX;
	public static int dFifthLeftPosY;
	// 右下角像素值
	public static int dFifthRightPosX;
	public static int dFifthRightPosY;

	/************* 第六个格子 *****************/
	// 左上角像素值
	public static int dSixthLeftPosX;
	public static int dSixthLeftPosY;
	// 右下角像素值
	public static int dSixthRightPosX;
	public static int dSixthRightPosY;

	/************* 第七个格子 *****************/
	// 左上角像素值(以左上角为参照)
	public static int dSeventhLeftPosX;
	public static int dSeventhLeftPosY;
	// 右下角像素值
	public static int dSeventhRightPosX;
	public static int dSeventhRightPosY;

	/************* 第八个格子 *****************/
	// 左上角像素值
	public static int dEighthLeftPosX;
	public static int dEighthLeftPosY;
	// 右下角像素值
	public static int dEighthRightPosX;
	public static int dEighthRightPosY;

	/************* 第九个格子 *****************/
	// 左上角像素值(以左上角为参照)
	public static int dNinthLeftPosX;
	public static int dNinthLeftPosY;
	// 右下角像素值
	public static int dNinthRightPosX;
	public static int dNinthRightPosY;

	// 根据左上角像素点存储九个格子的左上角与右下角点
	public static void StoreNineGridPos(Point pt, int nWidthDx, int nHeightDx) {
		// 第一个点
		dFirstLeftPosX = pt.x;
		dFirstLeftPosY = pt.y;
		dFirstRightPosX = pt.x + nWidthDx;
		dFirstRightPosY = pt.y + nHeightDx;

		// 第二个点
		dSecondLeftPosX = pt.x + nWidthDx;
		dSecondLeftPosY = pt.y;
		dSecondRightPosX = dSecondLeftPosX + nWidthDx;
		dSecondRightPosY = dSecondLeftPosY + nHeightDx;

		// 第三个点
		dThirdLeftPosX = pt.x + 2 * nWidthDx;
		dThirdLeftPosY = pt.y;
		dThirdRightPosX = dThirdLeftPosX + nWidthDx;
		dThirdRightPosY = dThirdLeftPosY + nHeightDx;

		// 第四个点
		dForthLeftPosX = pt.x;
		dForthLeftPosY = pt.y + nHeightDx;
		dForthRightPosX = dForthLeftPosX + nWidthDx;
		dForthRightPosY = dForthLeftPosY + nHeightDx;

		// 第五个点
		dFifthLeftPosX = pt.x + nWidthDx;
		dFifthLeftPosY = pt.y + nHeightDx;
		dFifthRightPosX = dFifthLeftPosX + nWidthDx;
		dFifthRightPosY = dFifthLeftPosY + nHeightDx;

		// 第六个点
		dSixthLeftPosX = pt.x + 2 * nWidthDx;
		dSixthLeftPosY = pt.y + nHeightDx;
		dSixthRightPosX = dSixthLeftPosX + nWidthDx;
		dSixthRightPosY = dSixthLeftPosY + nHeightDx;

		// 第七个点
		dSeventhLeftPosX = pt.x;
		dSeventhLeftPosY = pt.y + 2 * nHeightDx;
		dSeventhRightPosX = dSeventhLeftPosX + nWidthDx;
		dSeventhRightPosY = dSeventhLeftPosY + nHeightDx;

		// 第八个点
		dEighthLeftPosX = pt.x + nWidthDx;
		dEighthLeftPosY = pt.y + 2 * nHeightDx;
		dEighthRightPosX = dEighthLeftPosX + nWidthDx;
		dEighthRightPosY = dEighthLeftPosY + nHeightDx;

		// 第九个点
		dNinthLeftPosX = pt.x + 2 * nWidthDx;
		dNinthLeftPosY = pt.y + 2 * nHeightDx;
		dNinthRightPosX = dNinthLeftPosX + nWidthDx;
		dNinthRightPosY = dNinthLeftPosY + nHeightDx;
	}

	// 根据像素点获取格子索引号
	public static int GetPolygonIndex(Point pt) {
		int nIndex = 0;
		boolean bIsIn = false;
		// 判断是否在第一个格子中
		bIsIn = IsInRect(new Rect(dFirstLeftPosX, dFirstLeftPosY,
				dFirstRightPosX, dFirstRightPosY), pt);
		if (bIsIn) {
			return 1;
		}
		// 判断是否在第二个格子中
		bIsIn = IsInRect(new Rect(dSecondLeftPosX, dSecondLeftPosY,
				dSecondRightPosX, dSecondRightPosY), pt);
		if (bIsIn) {
			return 2;
		}
		// 判断是否在第三个格子中
		bIsIn = IsInRect(new Rect(dThirdLeftPosX, dThirdLeftPosY,
				dThirdRightPosX, dThirdRightPosY), pt);
		if (bIsIn) {
			return 3;
		}
		// 判断是否在第四个格子中
		bIsIn = IsInRect(new Rect(dForthLeftPosX, dForthLeftPosY,
				dForthRightPosX, dForthRightPosY), pt);
		if (bIsIn) {
			return 4;
		}
		// 判断是否在第五个格子中
		bIsIn = IsInRect(new Rect(dFifthLeftPosX, dFifthLeftPosY,
				dFifthRightPosX, dFifthRightPosY), pt);
		if (bIsIn) {
			return 5;
		}
		// 判断是否在第六个格子中
		bIsIn = IsInRect(new Rect(dSixthLeftPosX, dSixthLeftPosY,
				dSixthRightPosX, dSixthRightPosY), pt);
		if (bIsIn) {
			return 6;
		}
		// 判断是否在第七个格子中
		bIsIn = IsInRect(new Rect(dSeventhLeftPosX, dSeventhLeftPosY,
				dSeventhRightPosX, dSeventhRightPosY), pt);
		if (bIsIn) {
			return 7;
		}
		// 判断是否在第八个格子中
		bIsIn = IsInRect(new Rect(dEighthLeftPosX, dEighthLeftPosY,
				dEighthRightPosX, dEighthRightPosY), pt);
		if (bIsIn) {
			return 8;
		}
		// 判断是否在第九个格子中
		bIsIn = IsInRect(new Rect(dNinthLeftPosX, dNinthLeftPosY,
				dNinthRightPosX, dNinthRightPosY), pt);
		if (bIsIn) {
			return 9;
		}
		return nIndex;
	}

	private static boolean IsInRect(Rect rect, Point pt) {
		return (pt.x > rect.left && pt.x < rect.right)
				&& (pt.y > rect.top && pt.y < rect.bottom);
	}

}

package com.mike.commondata;

import android.graphics.Point;
import android.graphics.Rect;

//�洢�Ÿ����ӵ����Ͻ������½�����ֵ
public class NinePolygonData {

	/************* ��һ������ *****************/
	// ���Ͻ�����ֵ(�����Ͻ�Ϊ����)
	public static int dFirstLeftPosX;
	public static int dFirstLeftPosY;
	// ���½�����ֵ
	public static int dFirstRightPosX;
	public static int dFirstRightPosY;

	/************* �ڶ������� *****************/
	// ���Ͻ�����ֵ
	public static int dSecondLeftPosX;
	public static int dSecondLeftPosY;
	// ���½�����ֵ
	public static int dSecondRightPosX;
	public static int dSecondRightPosY;

	/************* ���������� *****************/
	// ���Ͻ�����ֵ(�����Ͻ�Ϊ����)
	public static int dThirdLeftPosX;
	public static int dThirdLeftPosY;
	// ���½�����ֵ
	public static int dThirdRightPosX;
	public static int dThirdRightPosY;

	/************* ���ĸ����� *****************/
	// ���Ͻ�����ֵ
	public static int dForthLeftPosX;
	public static int dForthLeftPosY;
	// ���½�����ֵ
	public static int dForthRightPosX;
	public static int dForthRightPosY;

	/************* ��������� *****************/
	// ���Ͻ�����ֵ(�����Ͻ�Ϊ����)
	public static int dFifthLeftPosX;
	public static int dFifthLeftPosY;
	// ���½�����ֵ
	public static int dFifthRightPosX;
	public static int dFifthRightPosY;

	/************* ���������� *****************/
	// ���Ͻ�����ֵ
	public static int dSixthLeftPosX;
	public static int dSixthLeftPosY;
	// ���½�����ֵ
	public static int dSixthRightPosX;
	public static int dSixthRightPosY;

	/************* ���߸����� *****************/
	// ���Ͻ�����ֵ(�����Ͻ�Ϊ����)
	public static int dSeventhLeftPosX;
	public static int dSeventhLeftPosY;
	// ���½�����ֵ
	public static int dSeventhRightPosX;
	public static int dSeventhRightPosY;

	/************* �ڰ˸����� *****************/
	// ���Ͻ�����ֵ
	public static int dEighthLeftPosX;
	public static int dEighthLeftPosY;
	// ���½�����ֵ
	public static int dEighthRightPosX;
	public static int dEighthRightPosY;

	/************* �ھŸ����� *****************/
	// ���Ͻ�����ֵ(�����Ͻ�Ϊ����)
	public static int dNinthLeftPosX;
	public static int dNinthLeftPosY;
	// ���½�����ֵ
	public static int dNinthRightPosX;
	public static int dNinthRightPosY;

	// �������Ͻ����ص�洢�Ÿ����ӵ����Ͻ������½ǵ�
	public static void StoreNineGridPos(Point pt, int nWidthDx, int nHeightDx) {
		// ��һ����
		dFirstLeftPosX = pt.x;
		dFirstLeftPosY = pt.y;
		dFirstRightPosX = pt.x + nWidthDx;
		dFirstRightPosY = pt.y + nHeightDx;

		// �ڶ�����
		dSecondLeftPosX = pt.x + nWidthDx;
		dSecondLeftPosY = pt.y;
		dSecondRightPosX = dSecondLeftPosX + nWidthDx;
		dSecondRightPosY = dSecondLeftPosY + nHeightDx;

		// ��������
		dThirdLeftPosX = pt.x + 2 * nWidthDx;
		dThirdLeftPosY = pt.y;
		dThirdRightPosX = dThirdLeftPosX + nWidthDx;
		dThirdRightPosY = dThirdLeftPosY + nHeightDx;

		// ���ĸ���
		dForthLeftPosX = pt.x;
		dForthLeftPosY = pt.y + nHeightDx;
		dForthRightPosX = dForthLeftPosX + nWidthDx;
		dForthRightPosY = dForthLeftPosY + nHeightDx;

		// �������
		dFifthLeftPosX = pt.x + nWidthDx;
		dFifthLeftPosY = pt.y + nHeightDx;
		dFifthRightPosX = dFifthLeftPosX + nWidthDx;
		dFifthRightPosY = dFifthLeftPosY + nHeightDx;

		// ��������
		dSixthLeftPosX = pt.x + 2 * nWidthDx;
		dSixthLeftPosY = pt.y + nHeightDx;
		dSixthRightPosX = dSixthLeftPosX + nWidthDx;
		dSixthRightPosY = dSixthLeftPosY + nHeightDx;

		// ���߸���
		dSeventhLeftPosX = pt.x;
		dSeventhLeftPosY = pt.y + 2 * nHeightDx;
		dSeventhRightPosX = dSeventhLeftPosX + nWidthDx;
		dSeventhRightPosY = dSeventhLeftPosY + nHeightDx;

		// �ڰ˸���
		dEighthLeftPosX = pt.x + nWidthDx;
		dEighthLeftPosY = pt.y + 2 * nHeightDx;
		dEighthRightPosX = dEighthLeftPosX + nWidthDx;
		dEighthRightPosY = dEighthLeftPosY + nHeightDx;

		// �ھŸ���
		dNinthLeftPosX = pt.x + 2 * nWidthDx;
		dNinthLeftPosY = pt.y + 2 * nHeightDx;
		dNinthRightPosX = dNinthLeftPosX + nWidthDx;
		dNinthRightPosY = dNinthLeftPosY + nHeightDx;
	}

	// �������ص��ȡ����������
	public static int GetPolygonIndex(Point pt) {
		int nIndex = 0;
		boolean bIsIn = false;
		// �ж��Ƿ��ڵ�һ��������
		bIsIn = IsInRect(new Rect(dFirstLeftPosX, dFirstLeftPosY,
				dFirstRightPosX, dFirstRightPosY), pt);
		if (bIsIn) {
			return 1;
		}
		// �ж��Ƿ��ڵڶ���������
		bIsIn = IsInRect(new Rect(dSecondLeftPosX, dSecondLeftPosY,
				dSecondRightPosX, dSecondRightPosY), pt);
		if (bIsIn) {
			return 2;
		}
		// �ж��Ƿ��ڵ�����������
		bIsIn = IsInRect(new Rect(dThirdLeftPosX, dThirdLeftPosY,
				dThirdRightPosX, dThirdRightPosY), pt);
		if (bIsIn) {
			return 3;
		}
		// �ж��Ƿ��ڵ��ĸ�������
		bIsIn = IsInRect(new Rect(dForthLeftPosX, dForthLeftPosY,
				dForthRightPosX, dForthRightPosY), pt);
		if (bIsIn) {
			return 4;
		}
		// �ж��Ƿ��ڵ����������
		bIsIn = IsInRect(new Rect(dFifthLeftPosX, dFifthLeftPosY,
				dFifthRightPosX, dFifthRightPosY), pt);
		if (bIsIn) {
			return 5;
		}
		// �ж��Ƿ��ڵ�����������
		bIsIn = IsInRect(new Rect(dSixthLeftPosX, dSixthLeftPosY,
				dSixthRightPosX, dSixthRightPosY), pt);
		if (bIsIn) {
			return 6;
		}
		// �ж��Ƿ��ڵ��߸�������
		bIsIn = IsInRect(new Rect(dSeventhLeftPosX, dSeventhLeftPosY,
				dSeventhRightPosX, dSeventhRightPosY), pt);
		if (bIsIn) {
			return 7;
		}
		// �ж��Ƿ��ڵڰ˸�������
		bIsIn = IsInRect(new Rect(dEighthLeftPosX, dEighthLeftPosY,
				dEighthRightPosX, dEighthRightPosY), pt);
		if (bIsIn) {
			return 8;
		}
		// �ж��Ƿ��ڵھŸ�������
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

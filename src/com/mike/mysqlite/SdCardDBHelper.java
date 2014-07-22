package com.mike.mysqlite;

import com.mike.commondata.commondata;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SdCardDBHelper extends SQLiteOpenHelper {

	public static final String TAG = "SdCardDBHelper";

	/**
	 * 数据库版本
	 **/
	public static int DATABASE_VERSION = 1;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文环境
	 **/
	public SdCardDBHelper(Context context) {
		super(context, commondata.DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * 创建数据库时触发，创建离线存储所需要的数据库表
	 * 
	 * @param db
	 **/
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			// 创建图片的表
			db.execSQL("create table if not exists "
					+ commondata.TABLE_NAME
					+ "(_id integer primary key autoincrement,taskid integer(10),"
					+ "AreaId varchar(140),"
					+ "CommentTime varchar(20))");

		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
	}
}

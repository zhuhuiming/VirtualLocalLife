package com.mike.mysqlite;

import java.io.File;
import java.io.IOException;

import com.mike.commondata.commondata;

import android.content.Context;
import android.util.Log;

public class OperaDatabase {

	SdCardDBHelper mdbHelper = null;
	private Context mContext = null;

	// 判断存储版本号的文件是否存在,如果不存在那么就将数据库文件也删除掉
	private void DealFile() {
		// 判断是否存在sd卡
		boolean sdExist = android.os.Environment.MEDIA_MOUNTED
				.equals(android.os.Environment.getExternalStorageState());
		if (!sdExist) {// 如果不存在,
			Log.e("SD卡管理：", "SD卡不存在，请加载SD卡");
			return;
		} else {// 如果存在
				// 获取sd卡路径
			String dbDir = android.os.Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			dbDir += "/";
			dbDir += commondata.strRootFileName;// 数据库所在目录
			String dbPath = dbDir + commondata.DATABASE_NAME;// 数据库路径
			
			// 判断目录是否存在，不存在则创建该目录
			File dirFile = new File(dbDir);
			if (!dirFile.exists())
				dirFile.mkdirs();

			// 判断数据库文件是否存在，不存在则创建该数据库文件
			File dbFile = new File(dbPath);
			if (!dbFile.exists()) {
				try {
					dbFile.createNewFile();// 创建数据库文件
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public OperaDatabase(Context context) {
		if (mdbHelper != null) {
			mdbHelper.close();
			mdbHelper = null;
		}
		mContext = context;
		DatabaseContext dbContext = new DatabaseContext(context);
		DealFile();
		mdbHelper = new SdCardDBHelper(dbContext);
	}

	public void CloseDatabase() {
		if (mdbHelper != null) {
			mdbHelper.close();
			mdbHelper = null;
		}
	}
}

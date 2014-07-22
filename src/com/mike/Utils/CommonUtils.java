package com.mike.Utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class CommonUtils extends ContextWrapper {

	public CommonUtils(Context base) {
		super(base);
	}

	public static void ShowToastCenter(Context context, String strText,
			int nTime) {
		if (context != null) {
			Toast toast = Toast.makeText(context, strText, nTime);

			toast.setGravity(Gravity.CENTER, 0, 0);

			toast.show();
		}

	}

	// 获取手机mac地址
	public String strGetPhoneMac() {
		// 获取手机mac
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		WifiInfo info = wifi.getConnectionInfo();

		String strMac = info.getMacAddress();
		if (null == strMac) {
			strMac = "";
		}
		return strMac;
	}

	// MD5加密，32位
	public static String MD5(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	// 可逆的加密算法
	public static String encryptmd5(String str) {
		char[] a = str.toCharArray();
		for (int i = 0; i < a.length; i++) {
			a[i] = (char) (a[i] ^ 'l');
		}
		String s = new String(a);
		return s;
	}
	
	
	/**
	 * 从uri得到bmp图像
	 * 
	 * @param probe_uri
	 * @return bmp图像
	 */
	public Bitmap GetBitMapFromURI(Uri probe_uri) {
		int imgSampleSize;
		Bitmap bitmap = null;
		ContentResolver cr = null;
		try {
			cr = getContentResolver();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;// 只描边，不读取数�?
			byte[] data = readStream(cr.openInputStream(probe_uri));
			Log.i("zp1", "probe_uri:" + probe_uri.getPath());
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
					options);
			Log.i("zp1", "bitmap_new:" + bitmap);
			if (options.outWidth > options.outHeight) // must < 1024*768*2
				imgSampleSize = (options.outWidth + 360) / 720; // >1000 ->
																// resample
			else
				imgSampleSize = (options.outHeight + 360) / 720;
			options.inSampleSize = imgSampleSize;

			Log.i("zp", "Input bitmap samplesize=" + options.inSampleSize
					+ " mod: w=" + options.outWidth + ",h=" + options.outHeight);
			options.inJustDecodeBounds = false;// 读取数据
			//options.inPreferredConfig = Bitmap.Config.RGB_565;
			data = readStream(cr.openInputStream(probe_uri));
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
					options);
			Log.i("zp226",
					"bitmapwg:" + bitmap.getWidth() + " " + bitmap.getHeight());

		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 从路径得到bmp图像
	 * 
	 * @param path
	 * @return bmp图像
	 */
	public Bitmap GetBitMapFromPath(String path) {
		int imgSampleSize;
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;// 只描边，不读取数�?
			FileInputStream is = new FileInputStream(path);
			bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null,
					options);
			Log.i("zp1", "path:" + path);
			if (options.outWidth > options.outHeight) // must < 1024*768*2
				imgSampleSize = (options.outWidth + 360) / 720; // >1000 ->
																// resample
			else
				imgSampleSize = (options.outHeight + 360) / 720;
			options.inSampleSize = imgSampleSize;

			Log.i("zp220", "Input bitmap samplesize=" + options.inSampleSize
					+ " mod: w=" + options.outWidth + ",h=" + options.outHeight);
			options.inJustDecodeBounds = false;// 读取数据
			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null,
					options);
			is.close();
			Log.i("zp226",
					"bitmapwg:" + bitmap.getWidth() + " " + bitmap.getHeight());

		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public View getImageView(Bitmap facebmp) {
		ImageView imgView = new ImageView(this);
		imgView.setImageBitmap(facebmp);
		return imgView;
	}

	/**
	 * 将InputStream对象转换为Byte[]
	 * 
	 * @param inStream
	 *            输入的InputStream�?
	 * @return 转换后Byte[]
	 * @throws IOException
	 */
	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}

	/**
	 * 根据exif信息旋转人脸图像，使人脸显示方向正确
	 * 
	 * @param inPath
	 *            人脸图像路径
	 * @return 旋转后人脸图�?
	 */
	public Bitmap PhotoRotation(Uri uri_) {
		// 获取照片拍照方向
		int Rotation = 0;
		ExifInterface exif;
		try {
			exif = new ExifInterface(uri_.getPath());
			Rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
			Log.i("zp", "Rotation:" + Rotation);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Bitmap facebmp = null;
		facebmp = GetBitMapFromURI(uri_);

		switch (Rotation) {
		case 1:// 角度0
			break;
		case 6:// 角度90
			facebmp = adjustPhotoRotation(facebmp, 90);
			Log.i("zp", "Rotation6:" + facebmp.getWidth());
			break;
		case 3:// 角度180
			facebmp = adjustPhotoRotation(facebmp, 90);
			facebmp = adjustPhotoRotation(facebmp, 90);
			Log.i("zp", "Rotation3:" + facebmp.getWidth());
			break;
		case 8:// 角度270
			facebmp = adjustPhotoRotation(facebmp, 90);
			facebmp = adjustPhotoRotation(facebmp, 90);
			facebmp = adjustPhotoRotation(facebmp, 90);
			Log.i("zp", "Rotation8:" + facebmp.getWidth());
			break;
		default:
			break;
		}
		return facebmp;
		// return zoomBitmap(facebmp);

	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param fileName
	 *            文件路径
	 * @return
	 */
	public boolean fileIsExists(String fileName) {
		try {
			File f = new File(fileName);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 对网络连接状态进行判�?
	 * 
	 * @return true, 可用�?false�?不可�?
	 */
	public boolean isOpenNetwork() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}

	/**
	 * 冒泡排序算法
	 * 
	 * @param args
	 *            输入vector
	 * @return 排序后位置数�?
	 */
	public int[] bubbleSort(Vector<Integer> args) {
		int num[] = new int[args.size()];
		int temp[] = new int[args.size()];
		for (int i = 0; i < args.size(); i++) {
			num[i] = i;
			temp[i] = args.get(i);
		}
		for (int i = 0; i < args.size() - 1; i++) {
			for (int j = i + 1; j < args.size(); j++) {
				if (temp[i] < temp[j]) {
					int temp_args = temp[i];
					temp[i] = temp[j];
					temp[j] = temp_args;
					int temp1 = num[i];
					num[i] = num[j];
					num[j] = temp1;
				}
			}
		}
		return num;
	}

	// 复制数据库文件?
	public void CopyFileFromUri(Uri srcUri, String filename2) {
		try {
			Log.i("zp", "srcUri:" + srcUri + " DstFile:" + filename2);
			ContentResolver cr = getContentResolver();
			InputStream inStream = cr.openInputStream(srcUri);
			FileOutputStream outStream;
			outStream = new FileOutputStream(filename2); // 源文�?
			// 文件�?096字节,每站120字节,必须固定空间,否则无效.
			byte[] buffer = new byte[4096];
			int blocklen;
			while ((blocklen = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, blocklen); // 将内容写到新文件当中
			}
			inStream.close();
			outStream.flush();
			outStream.close();
			outStream = null; // 新文�?
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 旋转人脸图像
	 * 
	 * @param bm
	 *            输入图片
	 * @param orientationDegree
	 *            旋转角度
	 * @return 旋转后图�?
	 */
	public Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
		Bitmap bm1 = null;
		if (bm != null) {
			Matrix m = new Matrix();
			m.setRotate(orientationDegree, (float) bm.getWidth() / 2,
					(float) bm.getHeight() / 2);
			float targetX, targetY;
			if (orientationDegree == 90) {
				targetX = bm.getHeight();
				targetY = 0;
			} else {
				targetX = bm.getHeight();
				targetY = bm.getWidth();
			}

			final float[] values = new float[9];
			m.getValues(values);
			float x1 = values[Matrix.MTRANS_X];
			float y1 = values[Matrix.MTRANS_Y];
			m.postTranslate(targetX - x1, targetY - y1);
			bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(),
					Bitmap.Config.RGB_565);
			Paint paint = new Paint();
			Canvas canvas = new Canvas(bm1);
			canvas.drawBitmap(bm, m, paint);
		}
		return bm1;
	}

	/**
	 * 保存图片
	 * 
	 * @param bitmap
	 *            保存图片
	 * @param _file
	 *            保存路径
	 * @throws IOException
	 */
	@SuppressLint("SdCardPath")
	public void saveBitmapToFile(Bitmap bitmap, String _file)
			throws IOException {
		if (bitmap != null) {
			BufferedOutputStream os = null;
			try {
				File file = new File(_file);
				int end = _file.lastIndexOf(File.separator);
				String _filePath = _file.substring(0, end);
				File filePath = new File(_filePath);
				if (!filePath.exists()) {
					filePath.mkdirs();
				}
				file.createNewFile();
				os = new BufferedOutputStream(new FileOutputStream(file));
				bitmap.compress(Bitmap.CompressFormat.JPEG, 70, os);
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	/**
	 * 保存图片
	 * 
	 * @param bitmap
	 *            保存图片
	 * @param _file
	 *            保存路径
	 * @throws IOException
	 */
	@SuppressLint("SdCardPath")
	public void saveBitmapToFileNew(Bitmap bitmap, String _file)
			throws IOException {
		
		File file = new File(_file);
		int end = _file.lastIndexOf(File.separator);
		String _filePath = _file.substring(0, end);
		File filePath = new File(_filePath);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		file.createNewFile();
		if (bitmap != null) {
			BufferedOutputStream os = null;
			try {
				os = new BufferedOutputStream(new FileOutputStream(file));
				bitmap.compress(Bitmap.CompressFormat.JPEG, 70, os);
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	/**
	 * 获取exif信息，旋转图�?
	 * 
	 * @param copyFileName
	 *            旋转图像的路�?
	 */
	public void rotatePhoto(String copyFileName) {
		int Rotation = 0;
		ExifInterface exif;
		try {
			exif = new ExifInterface(copyFileName);
			Rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
			Log.i("zp", "Rotation:" + Rotation);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Bitmap facebmp = GetBitMapFromURI(Uri.fromFile(new File(copyFileName)));

		switch (Rotation) {
		case 1:// 角度0
			break;
		case 6:// 角度90
			facebmp = adjustPhotoRotation(facebmp, 90);
			break;
		case 3:// 角度180
			facebmp = adjustPhotoRotation(facebmp, 180);
			break;
		case 8:// 角度270
			facebmp = adjustPhotoRotation(facebmp, 90);
			facebmp = adjustPhotoRotation(facebmp, 180);
			break;
		default:
			break;
		}
		try {
			saveBitmapToFile(facebmp, copyFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将Bitmap转换成byte[]
	 * 
	 * @param bitmap
	 * @return 转换字符串结�?
	 */
	public byte[] bitmaptobyte(String path) {
		// 将Bitmap转换成字符串
		byte[] bytes = null;
		int Len = 0;
		try {
			FileInputStream fs = new FileInputStream(path);

			try {
				Len = fs.available();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bytes = new byte[Len];
			int count = 0;
			try {
				while ((count = fs.read(bytes)) > 0) {
					fs.read(bytes, 0, count);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	/**
	 * 将byte[]转换成Bitmap类型
	 * 
	 * @param byte[]
	 * @return 转换Bitmap结果
	 */
	public Bitmap bytetoBitmap(byte[] bitmapArray) {
		// 将字符串转换成Bitmap类型
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
					bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 将字符串转换成Bitmap类型
	 * 
	 * @param string
	 * @return 转换Bitmap结果
	 */
	public Bitmap stringtoBitmap(String string) {
		// 将字符串转换成Bitmap类型
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(string, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
					bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 删除文件或文件夹
	 * 
	 * @param file
	 *            要删除的文件
	 * @return 删除是否成功
	 */
	public boolean deleteFoder(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文�?
				file.delete(); // delete(方法 你应该知�?是删除的意�?;
			} else if (file.isDirectory()) { // 否则如果它是�?��目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				if (files != null) {
					for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
						deleteFoder(files[i]); // 把每个文�?用这个方法进行迭�?
					}
				}
			}
			boolean isSuccess = file.delete();
			if (!isSuccess) {
				return false;
			}
		}
		return true;
	}

	public Bitmap cutBitmap(Bitmap source, int x, int y, int width, int height) {
		Bitmap bitmap = null;
		bitmap = Bitmap.createBitmap(source, x, y, width, height);
		return bitmap;
	}

	public Bitmap zoomBitmap(Bitmap bitmap) {
		Matrix matrix = new Matrix(); // 创建操作图片用的 Matrix 对象
		Bitmap newbmp = bitmap;
		if (bitmap.getWidth() > 1024 || bitmap.getHeight() > 768) {
			float scaleWidth = (1024 / (float) bitmap.getWidth()); // 计算缩放比例
			float scaleHeight = (768 / (float) bitmap.getHeight());
			if (scaleWidth > scaleHeight) {
				scaleWidth = scaleHeight;
			} else {
				scaleHeight = scaleWidth;
			}
			matrix.postScale(scaleWidth, scaleHeight); // 设置缩放比例
			newbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true); // 建立新的 bitmap
														// ，其内容是对�?bitmap 的缩放后的图

		}
		return newbmp;
	}

	public Bitmap zoomBitmap(Bitmap bitmap, int nMaxWidth, int nMaxHeight) {
		Matrix matrix = new Matrix(); // 创建操作图片用的 Matrix 对象
		Bitmap newbmp = bitmap;
		if (bitmap.getWidth() > nMaxWidth || bitmap.getHeight() > nMaxHeight) {
			float scaleWidth = (nMaxWidth / (float) bitmap.getWidth()); // 计算缩放比例
			float scaleHeight = (nMaxHeight / (float) bitmap.getHeight());
			if (scaleWidth > scaleHeight) {
				scaleWidth = scaleHeight;
			} else {
				scaleHeight = scaleWidth;
			}
			matrix.postScale(scaleWidth, scaleHeight); // 设置缩放比例
			newbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true); // 建立新的 bitmap

		}
		return newbmp;
	}

	/**
	 * 根据exif信息旋转人脸图像，使人脸显示方向正确
	 * 
	 * @param inPath
	 *            人脸图像路径
	 * @return 旋转后人脸图�?
	 */
	public Bitmap PhotoRotationbyPath(String inPath) {
		// 获取照片拍照方向
		int Rotation = 0;
		ExifInterface exif;
		try {
			exif = new ExifInterface(inPath);
			Rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Bitmap facebmp = null;
		facebmp = GetBitMapFromPath(inPath);

		switch (Rotation) {
		case 1:// 角度0
			break;
		case 6:// 角度90
			facebmp = adjustPhotoRotation(facebmp, 90);
			break;
		case 3:// 角度180
			facebmp = adjustPhotoRotation(facebmp, 90);
			facebmp = adjustPhotoRotation(facebmp, 90);
			break;
		case 8:// 角度270
			facebmp = adjustPhotoRotation(facebmp, 90);
			facebmp = adjustPhotoRotation(facebmp, 90);
			facebmp = adjustPhotoRotation(facebmp, 90);
			break;
		default:
			break;
		}

		return zoomBitmap(facebmp);

	}

	/**
	 * 
	 * @param imgPath
	 * @param bitmap
	 * @param imgFormat
	 *            图片格式
	 * @return
	 */
	public String imgToBase64(String imgPath, int nWidth, int nHeight) {

		Bitmap bitmap = null;
		if (imgPath != null && imgPath.length() > 0) {
			bitmap = readBitmap(imgPath);
		}
		if (bitmap == null) {
			return null;
		}
		ByteArrayOutputStream out = null;
		try {
			bitmap = zoomBitmap(bitmap, nWidth, nHeight);
			out = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);

			out.flush();
			out.close();

			byte[] imgBytes = out.toByteArray();
			return Base64.encodeToString(imgBytes, Base64.DEFAULT);
		} catch (Exception e) {
			return null;
		} finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static Bitmap readBitmap(String imgPath) {
		try {
			return BitmapFactory.decodeFile(imgPath);
		} catch (Exception e) {
			return null;
		}

	}

	public String BitmapToBase64BySize(Bitmap bmp) {

		Bitmap bitmap = bmp;

		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);

			out.flush();
			out.close();

			byte[] imgBytes = out.toByteArray();
			return Base64.encodeToString(imgBytes, Base64.DEFAULT);
		} catch (Exception e) {
			return null;
		} finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param base64Data
	 * @param imgName
	 * @param imgFormat
	 *            图片格式
	 */
	public Bitmap base64ToBitmap(String base64Data) {
		byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

		return bitmap;
	}

	// 根据天,时,分,秒获取到总秒数
	public int GetSumSeconds(String strDay, String strHour, String strMinute,
			String strSecond) {
		int nSeconds = 0;
		if (strDay.equals("")) {
			strDay = "0";
		}
		if (strHour.equals("")) {
			strHour = "0";
		}
		if (strMinute.equals("")) {
			strMinute = "0";
		}
		if (strSecond.equals("")) {
			strSecond = "0";
		}
		int nDays = Integer.parseInt(strDay);
		int nHours = Integer.parseInt(strHour);
		int nMinutes = Integer.parseInt(strMinute);
		int nSecond = Integer.parseInt(strSecond);

		nSeconds = nDays * 24 * 3600 + nHours * 3600 + nMinutes * 60 + nSecond;

		return nSeconds;
	}

	// 根据秒数生成天,时,分,秒字符串
	public String GetStringBySeconds(long nSeconds) {
		String strTime;

		long nRemainSeconds = nSeconds;
		long nRemainDays = nSeconds / (24 * 3600);
		nRemainSeconds = nSeconds - nRemainDays * 24 * 3600;
		long nRemainHours = nRemainSeconds / (3600);
		nRemainSeconds = nRemainSeconds - nRemainHours * 3600;
		long nRemainMinute = nRemainSeconds / 60;
		nRemainSeconds = nRemainSeconds - nRemainMinute * 60;

		strTime = nRemainDays + "天" + nRemainHours + "小时" + nRemainMinute + "分"
				+ nRemainSeconds + "秒";

		return strTime;
	}

	// 取出字符串前面的空格字符
	public static String ClearFrontSpace(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != ' ') {
				s = s.substring(i, s.length());
				break;
			}
		}
		return s;
	}

	/**
	 * 从路径得到bmp图像
	 * 
	 * @param path
	 * @return bmp图像
	 */
	public Bitmap GetBitMapFromPathNew(String path) {

		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;// 读取数据
			FileInputStream is = new FileInputStream(path);
			bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null,
					options);

			is.close();

		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	//将String格式的日期转换成Date格式
	@SuppressLint("SimpleDateFormat")
	public static Date ChangeTimeToDate(String strTime,String strFormat){
		Date date = null;
		SimpleDateFormat simple1 = new SimpleDateFormat(strFormat);
		try {
			date = simple1.parse(strTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
}

package com.example.huoban.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class ImageFilesUtils {
	private static final float DISPLAYWIDTH = 400f;
	private static final float DISPLAYHEIGHT = 400f;
	private static final String APART = "/";
	private static final int FILE_LENGTH_LIMIT = 102400;

	public static List<String> compressFiles(Context context, List<String> needBeCompressed) {
		if (needBeCompressed == null || needBeCompressed.size() == 0) {
			return null;
		}
		String fileDir = null;
		List<String> afterCompress = new ArrayList<String>();
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			fileDir = Environment.getExternalStorageDirectory() + APART + context.getPackageName() + "/tempFile";
		} else {
			fileDir = context.getFilesDir().getAbsolutePath() + APART + context.getPackageName() + "/tempFile";
		}
		File file = new File(fileDir);
		if (!file.exists()) {
			file.mkdirs();
		}

		for (String imageFilePathNeedBeCompressed : needBeCompressed) {
			Bitmap bitmap = getBitmap(imageFilePathNeedBeCompressed);
			if (bitmap != null) {
				String afterStr = compressBitmap(fileDir, bitmap);
				if (afterStr != null) {
					afterCompress.add(afterStr);
				}
				if (!bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}

		return afterCompress;

	}

	public static String compressFiles(Context context, String needBeCompressed) {
		if (needBeCompressed == null) {
			return null;
		}
		String fileDir = null;
		String afterCompress = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			fileDir = Environment.getExternalStorageDirectory() + APART + context.getPackageName() + "/tempFile";
		} else {
			fileDir = context.getFilesDir().getAbsolutePath() + APART + context.getPackageName() + "/tempFile";
		}
		File file = new File(fileDir);
		if (!file.exists()) {
			file.mkdirs();
		}

		Bitmap bitmap = getBitmap(needBeCompressed);
		if (bitmap != null) {
			String afterStr = compressBitmap(fileDir, bitmap);
			afterCompress = afterStr;
			if (!bitmap.isRecycled()) {
				bitmap.recycle();
			}
		}

		return afterCompress;

	}

	public static String compressFiles(Context context, String needBeCompressed, Bitmap.CompressFormat format) {
		if (needBeCompressed == null) {
			return null;
		}
		String fileDir = null;
		String afterCompress = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			fileDir = Environment.getExternalStorageDirectory() + APART + context.getPackageName() + "/tempFile";
		} else {
			fileDir = context.getFilesDir().getAbsolutePath() + APART + context.getPackageName() + "/tempFile";
		}
		File file = new File(fileDir);
		if (!file.exists()) {
			file.mkdirs();
		}

		Bitmap bitmap = getBitmap(needBeCompressed);
		if (bitmap != null) {
			return saveBitmap(fileDir, bitmap);
		}

		return afterCompress;

	}

	public static final String saveBitmap(String fileDir, Bitmap bitmap) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
		byte[] buffer = bos.toByteArray();
		String path = fileDir + APART + System.currentTimeMillis();
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(buffer);
			fos.flush();
			fos.close();
			if (bos != null) {
				bos.close();
			}
			buffer = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		bitmap.recycle();

		return path;

	}

	public static String compressBitmap(String fileDir, Bitmap bitmap) {

		String path = null;
		int quality = 100;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
		byte[] buffer = bos.toByteArray();
		while (buffer.length > FILE_LENGTH_LIMIT) {
			quality -= 5;
			if (quality <= 0) {
				break;
			}

			bos.reset();
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
			buffer = bos.toByteArray();
		}
		path = fileDir + APART + System.currentTimeMillis();
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(buffer);
			fos.flush();
			fos.close();
			if (bos != null) {
				bos.close();
			}
			buffer = null;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return path;

	}

	public static String compressBitmap(String fileDir, Bitmap bitmap, Bitmap.CompressFormat format) {

		String path = null;
		int quality = 100;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(format, quality, bos);
		byte[] buffer = bos.toByteArray();
		while (buffer.length > FILE_LENGTH_LIMIT) {
			quality -= 5;
			if (quality <= 0) {
				break;
			}

			bos.reset();
			bitmap.compress(format, quality, bos);
			buffer = bos.toByteArray();
		}
		path = fileDir + APART + System.currentTimeMillis();
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(buffer);
			fos.flush();
			fos.close();
			if (bos != null) {
				bos.close();
			}
			buffer = null;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return path;

	}

	public static Bitmap getBitmap(String imageFilePath) {
		if (imageFilePath == null) {
			return null;
		}
		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		Bitmap bmp = null;

		BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);

		int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / DISPLAYHEIGHT);
		int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / DISPLAYWIDTH);

		if ((heightRatio > 1) || (widthRatio > 1)) {
			if (heightRatio > widthRatio) {
				bmpFactoryOptions.inSampleSize = heightRatio;
			} else {
				bmpFactoryOptions.inSampleSize = widthRatio;
			}
		}

		bmpFactoryOptions.inJustDecodeBounds = false;
		bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);

		return bmp;
	}

	public static void clearTemp(Context context) {
		if (context == null) {
			return;
		}
		String fileDir = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			fileDir = Environment.getExternalStorageDirectory() + APART + context.getPackageName() + "/tempFile";
		} else {
			fileDir = context.getFilesDir().getAbsolutePath() + APART + context.getPackageName() + "/tempFile";
		}

		File file = new File(fileDir);
		File[] fileList = file.listFiles();

		if (fileList != null && fileList.length > 0) {
			for (File file2 : fileList) {
				if (file2.exists()) {
					file2.delete();
				}
			}
		}

	}

	/**
	 * 获取拍照的路径
	 * 
	 * @param context
	 * @return
	 */
	public static String getCameraPath(Context context) {
		String fileDir = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			fileDir = Environment.getExternalStorageDirectory() + APART + context.getPackageName() + "/cameraFile";
		} else {
			fileDir = context.getFilesDir().getAbsolutePath() + APART + context.getPackageName() + "/cameraFile";
		}
		File file = new File(fileDir);
		if (!file.exists()) {
			file.mkdirs();
		}

		return fileDir + APART + System.currentTimeMillis();
	}
	
	/**
	 *将Drawable资源文件保存到本地
	 *@param context
	 *@param resId
	 *		Drawable资源Id
	 *@param imgName
	 * 		存储到本地的文件名
	 *@return 存储路径+文件名
	 *@author talent.zhao
	 * */
	public static String saveDrawable(Context context,int resId,String imgName){
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resId);
		 
		String fileDir = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			fileDir = Environment.getExternalStorageDirectory() + APART + context.getPackageName() + "/huoban/images/";
		} else {
			fileDir = context.getFilesDir().getAbsolutePath() + APART + context.getPackageName() + "/huoban/images/";
		}
		File SpicyDirectory = new File(fileDir);
		if(!SpicyDirectory.exists()){
			SpicyDirectory.mkdirs();
		}
		String fileName=fileDir+ imgName + ".png";
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(fileName);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			out=null;
		}
		return fileName;
	}
}

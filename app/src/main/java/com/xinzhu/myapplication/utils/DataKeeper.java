/*Copyright ©2015 TommyLemon(https://github.com/TommyLemon)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package com.xinzhu.myapplication.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

/**数据存储工具类
 * @must 
 * <br> 1.将fileRootPath中的包名（这里是zblibrary.demo）改为你的应用包名
 * <br> 2.在Application中调用init方法
 */
public class DataKeeper {
	private static final String TAG = "DataKeeper";

	private static final String SAVE_SUCCEED = "保存成功";
	private static final String SAVE_FAILED = "保存失败";
	private static final String DELETE_SUCCEED = "删除成功";
	private static final String DELETE_FAILED = "删除失败";

	private static final String ROOT_SHARE_PREFS_ = "DEMO_SHARE_PREFS_";

	//文件缓存<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	/**TODO 必须将fileRootPath中的包名（这里是zblibrary.demo）改为你的应用包名*/
	public static final String fileRootPath = getSDPath() != null ? (getSDPath() + "/xuezhibao/") : null;
	public static final String accountPath = fileRootPath + "account/";
	public static final String audioPath = fileRootPath + "audio/";
	public static final String videoPath = fileRootPath + "video/";
	public static final String imagePath = fileRootPath + "image/";
	public static final String tempPath = fileRootPath + "temp/";
	//文件缓存>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	//存储文件的类型<<<<<<<<<<<<<<<<<<<<<<<<<
	private static final int TYPE_FILE_TEMP = 0;								//保存保存临时文件
	private static final int TYPE_FILE_IMAGE = 1;							//保存图片
	private static final int TYPE_FILE_VIDEO = 2;							//保存视频
	private static final int TYPE_FILE_AUDIO = 3;							//保存语音

	//存储文件的类型>>>>>>>>>>>>>>>>>>>>>>>>>

	//不能实例化
	private DataKeeper() {}

	private static Context context;
	//获取context，获取存档数据库引用
	public static void init(Context context_) {
		context = context_;
		
		Log.i(TAG, "init fileRootPath = " + fileRootPath);
		
		//判断SD卡存在
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			if(fileRootPath != null) {
				File file = new File(imagePath);
				if(!file.exists()) {
					file.mkdirs();
				}
				file = new File(videoPath);
				if(!file.exists()) {
					file.mkdir();
				}
				file = new File(audioPath);
				if(!file.exists()) {
					file.mkdir();
				}
				file = new File(fileRootPath + accountPath);
				if(!file.exists()) {
					file.mkdir();
				}
				file = new File(tempPath);
				if(!file.exists()) {
					file.mkdir();
				}
			}
		}
	}
	

	public static SharedPreferences getRootSharedPreferences(Context context) {
		return context.getSharedPreferences(ROOT_SHARE_PREFS_, Context.MODE_PRIVATE);
	}

	//**********外部存储缓存***************
	/**
	 * 存储缓存文件 返回文件绝对路径
	 * @param file
	 * 		要存储的文件
	 * @param type
	 * 		文件的类型
	 *		IMAGE = "imgae";							//图片         
	 *		VIDEO = "video";							//视频        
	 *		VOICE = "voice";							//语音         
	 *		 = "voice";							//语音         
	 * @return	存储文件的绝对路径名
	 * 		若SDCard不存在返回null
	 */
	public static String storeFile(File file, String type) {

		if(!hasSDCard()) {
			return null;
		}
		String suffix = file.getName().substring(file.getName().lastIndexOf("") + 1);
		byte[] data = null;
		try {
			FileInputStream in = new FileInputStream(file);
			data = new byte[in.available()];
			in.read(data, 0, data.length);
			in.close();
		} catch (IOException e) {
			Log.e(TAG, "storeFile  try { FileInputStream in = new FileInputStream(file); ... >>" +
					" } catch (IOException e) {\n" + e.getMessage());
		}
		return storeFile(data, suffix, type);
	}

	/** @return	存储文件的绝对路径名
				若SDCard不存在返回null */
	@SuppressLint("DefaultLocale")
	public static String storeFile(byte[] data, String suffix, String type) {

		if(!hasSDCard()) {
			return null;
		}
		String path = null;
		if(type.equals(TYPE_FILE_IMAGE)) {
			path = imagePath + "IMG_" + Long.toHexString(System.currentTimeMillis()).toUpperCase()
					+ "" + suffix;
		} else if(type.equals(TYPE_FILE_VIDEO)) {
			path = videoPath + "VIDEO_" + Long.toHexString(System.currentTimeMillis()).toUpperCase()
					+ "" + suffix;
		} else if(type.equals(TYPE_FILE_AUDIO)) {
			path = audioPath + "VOICE_" + Long.toHexString(System.currentTimeMillis()).toUpperCase()
					+ "" + suffix;
		}
		try {
			FileOutputStream out = new FileOutputStream(path);
			out.write(data, 0, data.length);
			out.close();
		} catch (FileNotFoundException e) {
			Log.e(TAG, "storeFile  try { FileInputStream in = new FileInputStream(file); ... >>" +
					" } catch (FileNotFoundException e) {\n" + e.getMessage() + "\n\n >> path = null;");
			path = null;
		} catch (IOException e) {
			Log.e(TAG, "storeFile  try { FileInputStream in = new FileInputStream(file); ... >>" +
					" } catch (IOException e) {\n" + e.getMessage() + "\n\n >> path = null;");
			path = null;
		}
		return path;
	}


	/**jpg
	 * @param fileName
	 * @return
	 */
	public static String getImageFileCachePath(String fileName) {
		return getFileCachePath(TYPE_FILE_IMAGE, fileName, "jpg");
	}
	/**mp4
	 * @param fileName
	 * @return
	 */
	public static String getVideoFileCachePath(String fileName) {
		return getFileCachePath(TYPE_FILE_VIDEO, fileName, "mp4");
	}
	/**mp3
	 * @param fileName
	 * @return
	 */
	public static String getAudioFileCachePath(String fileName) {
		return getFileCachePath(TYPE_FILE_AUDIO, fileName, "mp3");
	}

	/** 获取一个文件缓存的路径  */
	public static String getFileCachePath(int fileType, String fileName, String formSuffix) {

		switch (fileType) {
		case TYPE_FILE_IMAGE:
			return imagePath + fileName + "" + formSuffix;
		case TYPE_FILE_VIDEO:
			return videoPath + fileName + "" + formSuffix;
		case TYPE_FILE_AUDIO:
			return audioPath + fileName + "" + formSuffix;
		default:
			return tempPath + fileName + "" + formSuffix;
		}
	}

	/**若存在SD 则获取SD卡的路径 不存在则返回null*/
	public static String getSDPath(){
		File sdDir = null;
		String path = null;
		//判断sd卡是否存在
		boolean sdCardExist = hasSDCard();
		if (sdCardExist) {
			//获取跟目录
			sdDir = Environment.getExternalStorageDirectory();
			path = sdDir.toString();
		}
		return path;
	}
	public static String getDiskCachePath(Context context) {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			return context.getExternalCacheDir().getPath();
		} else {
			return context.getCacheDir().getPath();
		}
	}
	/**判断是否有SD卡*/
	public static boolean hasSDCard() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	
	
	//使用SharedPreferences保存 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	/**使用SharedPreferences保存
	 * @param path
	 * @param key
	 * @param value
	 */
	public static void save(String path, String key, String value) {
		save(path, Context.MODE_PRIVATE, key, value);
	}
	/**使用SharedPreferences保存
	 * @param path
	 * @param mode
	 * @param key
	 * @param value
	 */
	public static void save(String path, int mode, String key, String value) {
		save(context.getSharedPreferences(path, mode), key, value);
	}
	/**使用SharedPreferences保存
	 * @param sdf
	 * @param key
	 * @param value
	 */
	public static void save(SharedPreferences sdf, String key, String value) {
		if (sdf == null || StringUtil.isNotEmpty(key, false) == false || StringUtil.isNotEmpty(value, false) == false) {
			Log.e(TAG, "save sdf == null || \n key = " + key + ";\n value = " + value + "\n >> return;");
			return;
		}
		sdf.edit().remove(key).putString(key, value).commit();		
	}

	//使用SharedPreferences保存 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/**
	 * 获取缓存大小
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String getTotalCacheSize(Context context) throws Exception {
		long cacheSize = getFolderSize(context.getCacheDir());
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			cacheSize += getFolderSize(context.getExternalCacheDir());
		}
		return getFormatSize(cacheSize);
	}

	/**
	 * 清除缓存
	 * @param context
	 */
	public static void clearAllCache(Context context) {
		deleteDir(context.getCacheDir());
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			deleteDir(context.getExternalCacheDir());
		}
	}

	private static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}
	// 获取文件大小
	//Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
	//Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
	private static long getFolderSize(File file) {
		long size = 0;
		try {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				// 如果下面还有文件
				if (fileList[i].isDirectory()) {
					size = size + getFolderSize(fileList[i]);
				} else {
					size = size + fileList[i].length();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	/**
	 * 格式化单位
	 * @param size
	 * @return
	 */
	private static String getFormatSize(double size) {
		double kiloByte = size / 1024;
		if (kiloByte < 1) {
//            return size + "Byte";
			return "0K";
		}

		double megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "K";
		}

		double gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "M";
		}

		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "GB";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
				+ "TB";
	}
}
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

package com.zou.fastlibrary.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.ViewConfiguration;

import java.lang.reflect.Method;

/**屏幕相关类
 * @author Lemon
 * @use ScreenUtil.xxxMethod(...);
 */
public class ScreenUtil {
	//	private static final String TAG = "SceenUtil";

	private ScreenUtil() {/* 不能实例化**/}


	public static int[] screenSize;
	public static int[] getScreenSize(Context context){
		if (screenSize == null || screenSize[0] <= 480 || screenSize[1] <= 800) {//小于该分辨率会显示不全
			screenSize = new int[2];

			DisplayMetrics dm = new DisplayMetrics();
			dm = context.getResources().getDisplayMetrics();
			// float density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
			// int densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
			// float xdpi = dm.xdpi;
			// float ydpi = dm.ydpi;
			// Log.e(TAG + " DisplayMetrics", "xdpi=" + xdpi + "; ydpi=" + ydpi);
			// Log.e(TAG + " DisplayMetrics", "density=" + density + "; densityDPI="
			// + densityDPI);

			screenSize[0] = dm.widthPixels;// 屏幕宽（像素，如：480px）
			screenSize[1] = dm.heightPixels;// 屏幕高（像素，如：800px）
		}

		return screenSize;
	}
	
	public static int getScreenWidth(Context context){
		return getScreenSize(context)[0];
	}
	public static int getScreenHeight(Context context){
		return getScreenSize(context)[1];
	}
	/**
	 * Desc: 获取虚拟按键高度 放到工具类里面直接调用即可
	 */
	public static int getNavigationBarHeight(Context context) {
		int result = 0;
		if (hasNavBar(context)) {
			Resources res = context.getResources();
			int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
			if (resourceId > 0) {
				result = res.getDimensionPixelSize(resourceId);
			}
		}
		return result;
	}

	/**
	 * 检查是否存在虚拟按键栏
	 *
	 * @param context
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static boolean hasNavBar(Context context) {
		Resources res = context.getResources();
		int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
		if (resourceId != 0) {
			boolean hasNav = res.getBoolean(resourceId);
			// check override flag
			String sNavBarOverride = getNavBarOverride();
			if ("1".equals(sNavBarOverride)) {
				hasNav = false;
			} else if ("0".equals(sNavBarOverride)) {
				hasNav = true;
			}
			return hasNav;
		} else { // fallback
			return !ViewConfiguration.get(context).hasPermanentMenuKey();
		}
	}

	/**
	 * 判断虚拟按键栏是否重写
	 *
	 * @return
	 */
	private static String getNavBarOverride() {
		String sNavBarOverride = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			try {
				Class c = Class.forName("android.os.SystemProperties");
				Method m = c.getDeclaredMethod("get", String.class);
				m.setAccessible(true);
				sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
			} catch (Throwable e) {
			}
		}
		return sNavBarOverride;
	}

}

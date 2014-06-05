package com.mykj.lobby.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * 密度常量设置,提供dip转换为px的方法
 * 
 */
public final class DensityConst {
	/** 默认密度 */
	private static float density = 1.0f;
	/** 默认每英寸像素数 */
	private static int densityDpi = 160;
	
	private static int widthPixels = 480;
	
	private static int heightPixels = 800;

	
	/**
	 * 初始化与密度相关的所有变量值
	 * 
	 * @param activity
	 */
	public static void initDensity(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		densityDpi = dm.densityDpi;
		widthPixels=dm.widthPixels;
		heightPixels=dm.heightPixels;
	}

	
	public static int getAndroidSDKVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

	
	/**
	 * dip转化为像素
	 * 
	 * @param dip
	 * @return
	 */
	public static int getPx(int dip) {
		return (int) (dip * density);
	}

	/**
	 * 像素转化为dip
	 * 
	 * @param px
	 * @return
	 */
	public static int getDip(int px) {
		return (int) (px / density);
	}
	
	/**
	 * 获取屏幕宽度
	 * @return
	 */
	public static int getWidthPixels(){
		return widthPixels;
	}
	
	/**
	 *获取屏幕高度
	 */
	public static int getHeightPixels(){
		return heightPixels;
	}
	
	
	/**
	 * 每英寸像素数 
	 * @return
	 */
	public static int getDensityDpi(){
		return densityDpi;
	}
	
}

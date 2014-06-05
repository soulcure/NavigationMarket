package com.mykj.lobby.utils;


import java.util.HashMap;
import java.util.Map;

import android.content.Context;


/**
 * 
 * @author Administrator
 * @author FWQ 增加控制是否验证版本的控制变量 20120830
 */
public class AppConfig{


	/************************以下为项目配置开关*******************************/
	public static String versionName="";
	public static String buildTime="";
	/**版本描述信息只能手动修改，每次新版发布前请更新*/
	public static String versionInfo="更新日志：\n1. 高清游戏体验；\n2. 癞子玩法，加入百搭牌，炸弹多多；\n3. 小兵玩法加入攻防，新鲜刺激；\n4. 体验优化，bug修复；";

	/** 调试开关 */
	public static boolean debug = true;   //支持配置文件

	public static boolean all_in_lobby=false;  //是否发布移动版本，表现为loading 界面风给为移动要求风格

	/**pref文件名定义*/
	public static final String SHARED_PREFERENCES = "AppMark";

	/**消息推送URL组成关键参数*/
	public static final String CONFIG_MSG="method=msg";

	public static final String CONFIG_RES="cmd=resource";

	/** 下载文件的保存文件夹 **/
	public static final String DOWNLOAD_FOLDER = "/.mingyouGames";

	public static final String THEME_PATH = DOWNLOAD_FOLDER+"/theme";

	public static final String ICON_PATH = "/icons";

	public static final String COMMENDATION ="/Pictures";

	public static final String LOTTERYBMP_PATH = "/lotterybmp";
	
	public static final String HOST = "http://192.168.6.26:8765/api.php";//"http://115.238.250.55/api.php";

	/**提供游戏全局使用context,如果当前界面没有传入context*/
	public static Context mContext; 
	
	public static String QQ= "qq_number";
	
	public static String QQGROUP = "qq_group_number";
	
	public static String EMAIL = "contact_email";
	
	public static String CONTACT_PHONE_NUMBER = "contact_phone_number";
	
	public static Map<String, FileAsyncTaskDownload> taskMap = new HashMap<String, FileAsyncTaskDownload>();
	

}

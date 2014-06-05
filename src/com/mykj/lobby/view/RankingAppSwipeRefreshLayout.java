package com.mykj.lobby.view;

import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.mykj.lobby.market.adapter.RecommendListAdapter;
import com.mykj.lobby.market.model.GameItem;
import com.mykj.lobby.utils.Configs;
import com.mykj.lobby.utils.Util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;

public class RankingAppSwipeRefreshLayout extends SwipeRefreshLayout{
	private Context mContext;
	private ListView listView;
	private String mUrl;


	private RecommendListAdapter rankingListAdapter;

	public RankingAppSwipeRefreshLayout(Context context,String url) {
		super(context);
		mContext=context;
		mUrl=url;

		SwipeRefreshLayout container = this;
		container.setFocusable(true);
		rankingListAdapter = new RecommendListAdapter(new ArrayList<GameItem>(),(Activity)context);

		listView=new ListView(mContext);
		listView.setAdapter(rankingListAdapter);

		container.addView(listView);
		setColorScheme(android.R.color.white,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light, android.R.color.holo_red_light);


		//new HttpAsyncTask().execute(url);
	}

	
	   @Override  
       protected void onDraw(Canvas canvas) {  
           // TODO Auto-generated method stub  
           super.onDraw(canvas);  
         //  new HttpAsyncTask().execute(mUrl);
	   }
	

	public void refresh(String url){
		mUrl=url;
		new HttpAsyncTask().execute(mUrl);
	}

	public void refresh(){
		new HttpAsyncTask().execute(mUrl);
	}

	public boolean isEmpty(){
		return rankingListAdapter.isEmpty();
	}
	

	/**
	 * 解析外网返回数据
	 * 
	 * @param parsestr
	 *            数据内容
	 * @param type
	 *            数据类型 1-广告数据 2-游戏数据
	 * @return
	 */
	private String translateDataStr(String parsestr) {

		String resStr = null;

		if(Util.isEmptyStr(parsestr)){
			return resStr;
		}

		int resultCodeIndex = parsestr.indexOf("#");
		String resultCode = parsestr.substring(0, resultCodeIndex); // 返回结果是否成功
		// 0-成功

		parsestr = parsestr.substring(resultCodeIndex);
		int resultDescIndex = parsestr.indexOf("#", resultCodeIndex);

		//String resultDesc = parsestr.substring(1, resultDescIndex); // 返回结果提示信息

		if ("0".equals(resultCode)) {

			resStr = parsestr.substring(resultDescIndex + 1,
					parsestr.length() - 1);
			resStr = resStr.replaceAll("&", "&amp;");
		}

		return resStr;
	}






	private GameItem parseGameItem(XmlPullParser p) {
		GameItem gameItem = new GameItem();
		gameItem.setGameId(Integer.valueOf(p.getAttributeValue(null, "id")));
		gameItem.setGameName(p.getAttributeValue(null, "name"));
		String iconUrl = p.getAttributeValue(null, "image");
		iconUrl = iconUrl.replaceAll("&amp;", "&");
		gameItem.setIconUrl(iconUrl);
		gameItem.setLeval(Integer.valueOf(p.getAttributeValue(null, "level")));
		gameItem.setDownloadCount(Integer.valueOf(p.getAttributeValue(null,
				"count")));
		gameItem.setGameDesc(p.getAttributeValue(null, "summary"));
		String packageUrl = p.getAttributeValue(null, "package");
		packageUrl = packageUrl.replaceAll("&amp;", "&");
		if(gameItem.getGameId() == 1){
			gameItem.setGameFileUrl(/* packageUrl */"http://shouji.360tpcdn.com/140416/4e62664e9383d797b9d0df6a8ba1032d/com.mfp.frozen.playgame.disney_11.apk");
		}else if(gameItem.getGameId() == 2){
			gameItem.setGameFileUrl(/* packageUrl */"http://shouji.360tpcdn.com/130911/10ba61275ba7bb013ee83bc5f97dd99e/com.popcap.pvz2cthd360_1.apk");
		}else{
			gameItem.setGameFileUrl(packageUrl);
		}
		gameItem.setMd5(p.getAttributeValue(null, "md5"));
		//gameItem.setPackageName(p.getAttributeValue(null, "packet"));
		//gameItem.setMainActivity(p.getAttributeValue(null, "packtype"));

		return gameItem;
	}






	private class HttpAsyncTask extends AsyncTask<String, GameItem, Void> {

		@Override  
		protected void onPreExecute() {  
			RankingAppSwipeRefreshLayout.this.setRefreshing(true);
		}  

		@Override
		protected Void doInBackground(String... params) {
			String res = null;
			String url=params[0];
			res = Configs.getConfigXmlByHttp(url);
			String xmlStr=translateDataStr(res);

			String tagName="item";
			if(!Util.isEmptyStr(xmlStr)){
				try {
					// 定义工厂
					XmlPullParserFactory f = XmlPullParserFactory.newInstance();
					// 定义解析器
					XmlPullParser p = f.newPullParser();
					// 获取xml输入数据
					p.setInput(new StringReader(xmlStr));
					// 解析事件
					int eventType = p.getEventType();
					while (eventType != XmlPullParser.END_DOCUMENT) {

						switch (eventType) {
						case XmlPullParser.START_DOCUMENT:
							break;
						case XmlPullParser.START_TAG:
							String tag = p.getName();
							if (tag.equals(tagName)) {
								GameItem gameItem=parseGameItem(p);
								publishProgress(gameItem);
							}
							break;
						case XmlPullParser.END_TAG:
							break;
						case XmlPullParser.END_DOCUMENT:
							break;
						default:
							break;
						}
						// 用next方法处理下一个事件，否则会造成死循环。
						eventType = p.next();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			return null;
		}


		@Override
		protected void onProgressUpdate(GameItem... progress) {
			super.onProgressUpdate(progress);
			GameItem item = progress[0];
			rankingListAdapter.addItem(item);
		}


		@Override  
		protected void onPostExecute(Void v) { 
			RankingAppSwipeRefreshLayout.this.setRefreshing(false);
		}  
	}





}

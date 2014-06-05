package com.mykj.lobby.market;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.navigationdrawerexample.R;
import com.mykj.lobby.market.adapter.AdvAdapter;
import com.mykj.lobby.market.adapter.RecommendListAdapter;
import com.mykj.lobby.market.model.AdvItem;
import com.mykj.lobby.market.model.GameItem;
import com.mykj.lobby.market.model.Result;
import com.mykj.lobby.utils.AppConfig;
import com.mykj.lobby.utils.CenterUrlHelper;
import com.mykj.lobby.utils.Configs;
import com.mykj.lobby.utils.Util;

@SuppressWarnings("deprecation")
public class RecommendActivity extends Activity implements OnScrollListener {

	protected static final String TAG = "RecommendActivity";
	protected static final int GET_RECOMMEND_GAME_XML_SUCCESS = 1;
	protected static final int GET_RECOMMEND_GAME_XML_FAIL = 0;
	protected static final int GET_ADV_XML_SUCCESS = 11;
	protected static final int GET_ADV_XML_FAIL = 10;
	
	protected static final int PARSE_DATA_TYPE_ADV = 1; // 解析数据类型-广告
	protected static final int PARSE_DATA_TYPE_GAME = 2; // 解析数据类型-推荐游戏
	
	private Gallery mRecommendAdvGallery;
	private AdvAdapter advAdapter;
	private RecommendListAdapter recommendListAdapter;
	private int sumOfDrawble; // 广告数量
	private LinearLayout pointLinear;
	private List<AdvItem> recommendAdvList = new ArrayList<AdvItem>(); // 精品推荐广告列表
	private List<GameItem> recommendGameList = new ArrayList<GameItem>(); // 精品推荐数据
	private ListView recomendListView; //
	private LinearLayout loadingLayout;
	
	private BroadcastReceiver installReceiver;
	
	private int page = 1;
	private int pageSize = 1;

	/**
	 * 设置布局显示为目标有多大就多大
	 */
	private LayoutParams WClayoutParams = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);
	/**
	 * 设置布局显示目标最大化
	 */
	private LayoutParams FFlayoutParams = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.FILL_PARENT,
			LinearLayout.LayoutParams.FILL_PARENT);
	private ProgressBar progressBar;
	private String mAction = "com.lobby.recommend.install.apk";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommend_layout);
		init();
		// 初始化精品推荐中的广告列表
		imgGalleryInit();
	}

	private void init() {
		
		installReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int gameId = intent.getIntExtra("gameId", 0);
			}
		};
		
		registerReceiver(installReceiver, new IntentFilter(mAction));
		
		// 线性布局
		LinearLayout layout = new LinearLayout(this);
		// 设置布局 水平方向
		layout.setOrientation(LinearLayout.HORIZONTAL);
		// 进度条
		progressBar = new ProgressBar(this);
		// 进度条显示位置
		progressBar.setPadding(0, 0, 15, 0);

		layout.addView(progressBar, WClayoutParams);

		TextView textView = new TextView(this);
		textView.setText("加载中...");
		textView.setGravity(Gravity.CENTER_VERTICAL);

		layout.addView(textView, FFlayoutParams);
		layout.setGravity(Gravity.CENTER);

		loadingLayout = new LinearLayout(this);
		loadingLayout.addView(layout, WClayoutParams);
		loadingLayout.setGravity(Gravity.CENTER);
		loadingLayout.setVisibility(View.INVISIBLE);

		mRecommendAdvGallery = (Gallery) findViewById(R.id.galleryImgs);
		pointLinear = (LinearLayout) findViewById(R.id.gallery_point_linear);

		recomendListView = (ListView) findViewById(R.id.recommendList);
		recomendListView.addFooterView(loadingLayout);

		registerForContextMenu(recomendListView);

		recomendListView.setOnScrollListener(this);

		recommendListAdapter = new RecommendListAdapter(recommendGameList, this);

		String params = "m=api&c=recommend&page=" + page++ + "&size="
				+ pageSize + "&ver="
				+ Util.getVersionName(RecommendActivity.this);
		params += CenterUrlHelper.getSign(params, CenterUrlHelper.secret);
		getGameList(AppConfig.HOST + "?" + params, 1);

		recomendListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View convertView,
					int pos, long id) {
				// 到详细内容页面
				GameItem item = (GameItem) recommendListAdapter.getItem(pos);
				// 游戏详细内容查询地址
				// String detailUrl = item.getDetailUrl();
				RecommendActivity.this.startActivity(new Intent(
						RecommendActivity.this, GameDetailActivity.class).putExtra("gameId", item.getGameId()));
			}
		});
	}

	/**
	 * 初始化广告列表
	 * 
	 * @param list
	 */
	private void imgGalleryInit() {

		if (mRecommendAdvGallery == null) {
			return;
		}
		String params = "m=api&c=recommend_ad";
		params += CenterUrlHelper.getSign(params, CenterUrlHelper.secret);
		getAdvList(AppConfig.HOST + "?" + params);
		/*list.add(new AdvItem(
				"http://api.139game.com/android/adv/dn_new0307.png"));
		list.add(new AdvItem(
				"http://api.139game.com/android/adv/dn_new0307.png"));
		list.add(new AdvItem(
				"http://api.139game.com/android/adv/dn_new0307.png"));*/

		mRecommendAdvGallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 到详细内容页面
				AdvItem item = (AdvItem) advAdapter.getItem(position);
				RecommendActivity.this.startActivity(new Intent(
						RecommendActivity.this, GameDetailActivity.class).putExtra("gameId", item.getGameId()));
			}
		});

		mRecommendAdvGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				if (sumOfDrawble != 0) {
					changePointView(position % sumOfDrawble);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	/**
	 * 刷新广告表示点点
	 * 
	 * @param cur
	 */
	public void changePointView(int cur) {

		for (int i = 0; i < sumOfDrawble; i++) {
			ImageView pointView = (ImageView) pointLinear.getChildAt(i);
			if (i == cur) {
				pointView.setBackgroundResource(R.drawable.point_light);
			} else {
				pointView.setBackgroundResource(R.drawable.point_dark);
			}
		}
	}

	Handler mServiceHandler = new Handler() {
		public void handleMessage(Message msg) {
			String resultDesc = null;
			int what = msg.what;
			switch (what) {
			case GET_RECOMMEND_GAME_XML_SUCCESS:
				recommendListAdapter.setmGameList(recommendGameList);
				recomendListView.setAdapter(recommendListAdapter);
				recommendListAdapter.notifyDataSetChanged();
				loadingLayout.setVisibility(View.INVISIBLE);
				recomendListView.setSelection(lastItem - visibleItemCount + 1);
				break;
			case GET_RECOMMEND_GAME_XML_FAIL:
				if(null != msg.obj){
					resultDesc = (String) msg.obj;
				}
				Log.i(TAG, "get game xml failed!" + resultDesc);
				break;
				
			case GET_ADV_XML_SUCCESS:
				advAdapter = new AdvAdapter(recommendAdvList, RecommendActivity.this);
				mRecommendAdvGallery.setAdapter(advAdapter);
				
				sumOfDrawble = recommendAdvList.size();
				pointLinear.removeAllViews();
				for (int i = 0; i < recommendAdvList.size(); i++) {
					ImageView pointView = new ImageView(RecommendActivity.this);
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);
					lp.setMargins(5, 0, 0, 0);
					pointView.setLayoutParams(lp);
					if (i == 0) {
						pointView.setBackgroundResource(R.drawable.point_light);
					} else {
						pointView.setBackgroundResource(R.drawable.point_dark);
					}
					pointLinear.addView(pointView);
				}
				break;
				
			case GET_ADV_XML_FAIL:
				
				if(null != msg.obj){
					resultDesc = (String) msg.obj;
				}
				Log.i(TAG, "get adv xml failed!" + resultDesc);
				
				break;

			default:
				break;
			}
		};
	};

	/**
	 * 获取游戏列表
	 * 
	 * @param url
	 * @param gameList
	 * @param type
	 *            类型，1-获取精品推荐，2-获取最新排名列表
	 */
	private void getGameList(final String url, final int type) {
		new Thread() {
			@Override
			public void run() {
				Message msg = mServiceHandler.obtainMessage();

				Log.v(TAG, "getGameList  xml is begin ----" + url);

				String parsestr = Configs.getConfigXmlByHttp(url);

				Result result = parseDataStr(parsestr, PARSE_DATA_TYPE_GAME);

				if ("0".equals(result.getResultCode())) {
					msg.what = GET_RECOMMEND_GAME_XML_SUCCESS;
				} else {
					msg.obj = result.getResultDesc();
					msg.what = GET_RECOMMEND_GAME_XML_FAIL;
				}

				mServiceHandler.sendMessage(msg);
			}

		}.start();
	}

	private void getAdvList(final String url) {
		new Thread() {
			@Override
			public void run() {
				Message msg = mServiceHandler.obtainMessage();

				Log.v(TAG, "getGameList  xml is begin ----" + url);

				String parsestr = Configs.getConfigXmlByHttp(url);

				Result result = parseDataStr(parsestr, PARSE_DATA_TYPE_ADV);

				if ("0".equals(result.getResultCode())) {
					msg.what = GET_ADV_XML_SUCCESS;
				} else {
					msg.what = GET_ADV_XML_FAIL;
				}

				mServiceHandler.sendMessage(msg);
			}

		}.start();
	}

	private int lastItem;
	private int visibleItemCount;

	/**
	 * 解析外网返回数据
	 * @param parsestr 数据内容
	 * @param type 数据类型 1-广告数据  2-游戏数据
	 * @return
	 */
	private Result parseDataStr(String parsestr, int type) {

		Result result = new Result();
		
		if(parsestr == null){
			result.setResultCode("1");
			return result;
		}

		int resultCodeIndex = parsestr.indexOf("#");
		String resultCode = parsestr.substring(0, resultCodeIndex); // 返回结果是否成功
																	// 0-成功

		parsestr = parsestr.substring(resultCodeIndex);
		int resultDescIndex = parsestr.indexOf("#", resultCodeIndex);

		String resultDesc = parsestr.substring(1, resultDescIndex); // 返回结果提示信息

		result.setResultCode(resultCode);
		result.setResultDesc(resultDesc);

		String dataStr = null;

		if ("0".equals(resultCode)) {

			dataStr = parsestr.substring(resultDescIndex + 1,
					parsestr.length() - 1);

			dataStr = dataStr.replaceAll("&", "&amp;");
			parseDataXml(dataStr, "item", type); // 解析游戏信息

			/*
			 * for (int i = 0; i < 10; i++) { recommendGameList.add(new
			 * GameItem( "http://api.139game.com/android/adv/dn_new0307.png",
			 * "gameName" + index++, 12205, 1, "gameDesc" + index++, "",
			 * "http://shouji.360tpcdn.com/140403/18d4e3a52a486efd0c2dcd1a07bdde7c/com.qihoo360.mobilesafe_174.apk"
			 * )); }
			 */
		}

		return result;

	}

	public boolean parseDataXml(String strXml, String tagName, int type) {
		if (Util.isEmptyStr(strXml)) {
			return false;
		}
		boolean isParseSuccess = false;
		try {
			// 定义工厂
			XmlPullParserFactory f = XmlPullParserFactory.newInstance();
			// 定义解析器
			XmlPullParser p = f.newPullParser();
			// 获取xml输入数据
			p.setInput(new StringReader(strXml));
			// 解析事件
			int eventType = p.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {

				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					String tag = p.getName();
					if (tag.equals(tagName)) {
						if(type == PARSE_DATA_TYPE_GAME){
							parseGameItem(p);
						}
						else if(type == PARSE_DATA_TYPE_ADV){
							parseAdvItem(p);
						}
					}
					isParseSuccess = true;
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
			isParseSuccess = false;
		}
		return isParseSuccess;
	}
	
	private void parseAdvItem(XmlPullParser p) {
		String imageUrl = p.getAttributeValue(null, "image");
		imageUrl = imageUrl.replaceAll("&amp;", "&");
		AdvItem advItem = new AdvItem(imageUrl);
		String gameId = p.getAttributeValue(null, "game_id");
		if(Util.isNumeric(gameId)){
			advItem.setGameId(Integer.parseInt(gameId));
		}
		else{
			advItem.setGameId(0);
		}
		
		recommendAdvList.add(advItem);
	}
	
	private void parseGameItem(XmlPullParser p) {
		GameItem gameItem = new GameItem();
		gameItem.setGameId(Integer.valueOf(p.getAttributeValue(
				null, "id")));
		gameItem.setGameName(p.getAttributeValue(null, "name"));
		String iconUrl = p.getAttributeValue(null, "image");
		iconUrl = iconUrl.replaceAll("&amp;", "&");
		gameItem.setIconUrl(iconUrl);
		gameItem.setLeval(Integer.valueOf(p.getAttributeValue(
				null, "level")));
		gameItem.setDownloadCount(Integer.valueOf(p
				.getAttributeValue(null, "count")));
		gameItem.setGameDesc(p.getAttributeValue(null,
				"summary"));
		String packageUrl = p
				.getAttributeValue(null, "package");
		packageUrl = packageUrl.replaceAll("&amp;", "&");
		gameItem.setGameFileUrl(packageUrl);
		gameItem.setMd5(p.getAttributeValue(null, "md5"));
		recommendGameList.add(gameItem);
	}

	@Override
	public void onScroll(AbsListView v, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.visibleItemCount = visibleItemCount;
		lastItem = firstVisibleItem + visibleItemCount - 1;
	}

	@Override
	public void onScrollStateChanged(AbsListView v, int state) {
		if (lastItem == recommendListAdapter.getCount()
				&& state == OnScrollListener.SCROLL_STATE_IDLE) {
			// 添加到脚页显示
			String params = "m=api&c=recommend&page=" + page++ + "&size="
					+ pageSize + "&ver="
					+ Util.getVersionName(RecommendActivity.this);
			params += CenterUrlHelper.getSign(params, CenterUrlHelper.secret);

			loadingLayout.setVisibility(View.VISIBLE);

			getGameList(AppConfig.HOST + "?" + params, 1);
			Log.i(TAG, "lastItem:" + lastItem);
		}
	}
	
}

package com.mykj.lobby.market;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.android.navigationdrawerexample.R;
import com.mykj.lobby.market.adapter.GameTypeAdapter;
import com.mykj.lobby.market.model.GameType;
import com.mykj.lobby.market.model.Result;
import com.mykj.lobby.utils.AppConfig;
import com.mykj.lobby.utils.CenterUrlHelper;
import com.mykj.lobby.utils.Configs;
import com.mykj.lobby.utils.Util;

public class GameTypeActivity extends ListActivity {

	protected static final int GET_GAME_TYPE_XML_SUCCESS = 1;
	protected static final int GET_GAME_TYPE_XML_FAIL = 0;
	private ListView gameTypeListView;
	private GameTypeAdapter gameTypeAdapter;
	protected List<GameType> gameTypeList = new ArrayList<GameType>();
	private int page = 1;
	private int pageSize = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_type_layout);
		init();
	}

	private void init() {
		gameTypeListView = getListView();

		String params = "m=api&c=classify&ver="
				+ Util.getVersionName(GameTypeActivity.this);
		params += CenterUrlHelper.getSign(params, CenterUrlHelper.secret);
		getGameTypeList(AppConfig.HOST + "?" + params);

		gameTypeListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View convertView,
					int pos, long id) {
				GameType gameType = (GameType) gameTypeAdapter.getItem(pos);
				GameTypeActivity.this.startActivity(new Intent(
						GameTypeActivity.this, GameOfTypeActivity.class)
						.putExtra("typeId", gameType.getId()).putExtra(
								"typeName", gameType.getTypeName()));
			}
		});
	}

	Handler mServiceHandler = new Handler() {

		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case GET_GAME_TYPE_XML_SUCCESS:
				gameTypeAdapter = new GameTypeAdapter(gameTypeList,
						GameTypeActivity.this);
				gameTypeListView.setAdapter(gameTypeAdapter);
				break;
			case GET_GAME_TYPE_XML_FAIL:

				break;

			default:
				break;
			}
		};
	};

	/**
	 * 获取游戏分类列表
	 * 
	 * @param url
	 * @param gameList
	 */
	private void getGameTypeList(final String url) {
		new Thread() {
			@Override
			public void run() {
				Message msg = mServiceHandler.obtainMessage();

				String parsestr = Configs.getConfigXmlByHttp(url);
				Result result = parseDataStr(parsestr);
				if ("0".equals(result.getResultCode())) {
					msg.what = GET_GAME_TYPE_XML_SUCCESS;
				} else {
					msg.what = GET_GAME_TYPE_XML_FAIL;
				}

				mServiceHandler.sendMessage(msg);
			}

		}.start();
	}

	private Result parseDataStr(String parsestr) {
		Result result = new Result();

		if (parsestr == null) {
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
			parseDataXml(dataStr, "item"); // 解析游戏信息

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

	public boolean parseDataXml(String strXml, String tagName) {
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
						GameType gameType = new GameType();
						gameType.setId(p.getAttributeValue(null, "id"));
						gameType.setIconUrl(p.getAttributeValue(null, "image"));
						gameType.setGameCount(p
								.getAttributeValue(null, "count"));
						gameType.setTypeName(p.getAttributeValue(null, "name"));
						gameTypeList.add(gameType);
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
}

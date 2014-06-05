package com.mykj.lobby.market;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.example.android.navigationdrawerexample.R;
import com.mykj.lobby.market.model.GameType;
import com.mykj.lobby.market.model.Result;
import com.mykj.lobby.utils.AppConfig;
import com.mykj.lobby.utils.CenterUrlHelper;
import com.mykj.lobby.utils.Configs;
import com.mykj.lobby.utils.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class HelpActivity extends Activity implements OnClickListener {

	protected static final int GET_CONTACT_XML_SUCCESS = 1;
	protected static final int GET_CONTACT_XML_FAIL = 0;
	private TextView tvVersion;
	private TextView tvQQ;
	private TextView tvQQGroup;
	private TextView tvEmail;
	private ImageView ivBack;

	private String qq;
	private String qqGroup;
	private String email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_layout);
		init();
	}

	private void init() {
		tvVersion = (TextView) findViewById(R.id.tvVersion);
		tvQQ = (TextView) findViewById(R.id.tv_qq);
		tvQQGroup = (TextView) findViewById(R.id.tv_qq_group);
		tvEmail = (TextView) findViewById(R.id.tv_email);
		qq = Util.getStringSharedPreferences(HelpActivity.this, AppConfig.QQ,
				"");
		qqGroup = Util.getStringSharedPreferences(HelpActivity.this,
				AppConfig.QQGROUP, "");
		email = Util.getStringSharedPreferences(HelpActivity.this,
				AppConfig.EMAIL, "");
		tvVersion.setText(Util.getVersionName(HelpActivity.this));
		ivBack = (ImageView) findViewById(R.id.back);
		ivBack.setOnClickListener(this);
		if (Util.isEmptyStr(qq) || Util.isEmptyStr(qqGroup)
				|| Util.isEmptyStr(email)) {
			String params = "m=api&c=help&ver="
					+ Util.getVersionName(HelpActivity.this);
			params += CenterUrlHelper.getSign(params, CenterUrlHelper.secret);
			obtainCantactInfo(AppConfig.HOST + "?" + params);
		} else {
			tvQQ.setText(qq);
			tvQQGroup.setText(qqGroup);
			tvEmail.setText(email);
		}
	}

	/**
	 * 获取联系信息
	 */
	private void obtainCantactInfo(final String url) {
		new Thread() {
			@Override
			public void run() {
				Message msg = mServiceHandler.obtainMessage();

				String parsestr = Configs.getConfigXmlByHttp(url);

				Result result = parseNotifiyXml(parsestr);

				if ("0".equals(result.getResultCode())) {
					msg.what = GET_CONTACT_XML_SUCCESS;

				} else {
					msg.what = GET_CONTACT_XML_FAIL;
				}

				mServiceHandler.sendMessage(msg);
			}

		}.start();
	}

	Handler mServiceHandler = new Handler() {
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case GET_CONTACT_XML_SUCCESS:
				qq = Util.getStringSharedPreferences(HelpActivity.this,
						AppConfig.QQ, "");
				qqGroup = Util.getStringSharedPreferences(HelpActivity.this,
						AppConfig.QQGROUP, "");
				email = Util.getStringSharedPreferences(HelpActivity.this,
						AppConfig.EMAIL, "");
				tvQQ.setText(qq);
				tvQQGroup.setText(qqGroup);
				tvEmail.setText(email);
				break;
			case GET_CONTACT_XML_FAIL:
				break;
			default:
				break;
			}
		};
	};

	private Result parseNotifiyXml(String parsestr) {

		Result result = new Result();

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
			parseDataXml(dataStr); // 解析游戏信息
		}

		return result;
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.back) {
			finish();
		}
	}

	public boolean parseDataXml(String strXml) {
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
					if ("qq".equals(tag)) {
						String qq = p.nextText();
						Util.setStringSharedPreferences(HelpActivity.this, AppConfig.QQ, qq);
					}else if("email".equals(tag))
					{
						String email = p.nextText();
						Util.setStringSharedPreferences(HelpActivity.this, AppConfig.EMAIL, email);
					}else if("phone".equals(tag))
					{
						String phone = p.nextText();
						Util.setStringSharedPreferences(HelpActivity.this, AppConfig.CONTACT_PHONE_NUMBER, phone);
					}else if("qq2".equals(tag))
					{
						String qqGroup = p.nextText();
						Util.setStringSharedPreferences(HelpActivity.this, AppConfig.QQGROUP, qqGroup);
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

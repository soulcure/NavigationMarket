package com.mykj.lobby.market;

import java.io.StringReader;
import java.net.URLEncoder;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.navigationdrawerexample.R;
import com.mykj.lobby.market.model.Result;
import com.mykj.lobby.utils.AppConfig;
import com.mykj.lobby.utils.CenterUrlHelper;
import com.mykj.lobby.utils.Configs;
import com.mykj.lobby.utils.Util;

public class FeedBackActivity extends Activity implements OnClickListener{
	
	protected static final int GET_PHONE_XML_SUCCESS = 1;
	protected static final int GET_PHONE_XML_FAIL = 0;
	protected static final int SAVE_FEEDBACK_SUCCESS = 11;
	protected static final int SAVE_FEEDBACK_FAIL = 10;
	
	protected static final int GET_CONTACT_XML_SUCCESS = 1;
	protected static final int GET_CONTACT_XML_FAIL = 0;
	private EditText feedbackContent;
	private Button btnSend;
	private TextView feedbackContact;
	private ImageView ivBack;
	
	private String phoneNumber;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_layout);
		init();
	}
	
	private void init(){
		feedbackContent = (EditText)findViewById(R.id.feedbackContent);
		btnSend = (Button)findViewById(R.id.btn_send);
		feedbackContact = (TextView)findViewById(R.id.feedbackContact);
		btnSend.setOnClickListener(this);
		ivBack = (ImageView)findViewById(R.id.back);
		ivBack.setOnClickListener(this);
		
		phoneNumber = Util.getStringSharedPreferences(FeedBackActivity.this, AppConfig.CONTACT_PHONE_NUMBER, "");
		
		if(Util.isEmptyStr(phoneNumber)){
			// 获取联系
			String params = "m=api&c=help&ver="
					+ Util.getVersionName(FeedBackActivity.this);
			params += CenterUrlHelper.getSign(params, CenterUrlHelper.secret);
			obtainCantactInfo(AppConfig.HOST + "?" + params);
		}else{
			feedbackContact.setText(this.getResources().getString(R.string.feedback_contact) + phoneNumber);
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
						Util.setStringSharedPreferences(FeedBackActivity.this, AppConfig.QQ, qq);
					}else if("email".equals(tag))
					{
						String email = p.nextText();
						Util.setStringSharedPreferences(FeedBackActivity.this, AppConfig.EMAIL, email);
					}else if("phone".equals(tag))
					{
						String phone = p.nextText();
						Util.setStringSharedPreferences(FeedBackActivity.this, AppConfig.CONTACT_PHONE_NUMBER, phone);
					}else if("qq2".equals(tag))
					{
						String qqGroup = p.nextText();
						Util.setStringSharedPreferences(FeedBackActivity.this, AppConfig.QQGROUP, qqGroup);
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
	
	Handler mServiceHandler = new Handler(){
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case GET_PHONE_XML_SUCCESS:
				phoneNumber = Util.getStringSharedPreferences(FeedBackActivity.this, AppConfig.CONTACT_PHONE_NUMBER, "");
				feedbackContact.setText(FeedBackActivity.this.getResources().getString(R.string.feedback_contact) + phoneNumber);
				break;
			case GET_PHONE_XML_FAIL:
				break;
				
			case SAVE_FEEDBACK_SUCCESS:
				
				break;
			case SAVE_FEEDBACK_FAIL:
				break;
				
			default:
				break;
			}
		};
	};
	
	@Override
	public void onClick(View view) {
		int id = view.getId();
		if(id == R.id.btn_send){
			saveFeedBack();
		}else if(id == R.id.back){
			finish();
		}
	}
	
	// 请求意见提交
	class FeedbackTask extends AsyncTask<String, Void, String> {
		// 从网上下载图片
		@Override
		protected String doInBackground(String... params) {
			String feedbackInfo = Configs.getConfigXmlByHttp(params[0]);
			return feedbackInfo;
		}

		@Override
		protected void onPostExecute(String result) {
			if (null != result && result.length() > 0) {
				String[] statusInfo = result.split("#");
				String info = statusInfo[1];
				Toast.makeText(getApplication(), info, Toast.LENGTH_SHORT).show();
			}

		}
	}
	
	private void saveFeedBack(){
		String content = feedbackContent.getText().toString();
		if (!content.equals("")) {

			FeedbackTask task = new FeedbackTask();
			task.execute(AppConfig.HOST + "?" + getParamsStr(content));
			Toast.makeText(FeedBackActivity.this, "意见提交中...",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(FeedBackActivity.this, "提交内容不能为空...",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 获取参数字符串
	 * 
	 * @param openId
	 * @param sessionId
	 * @return
	 */
	protected String getParamsStr(String content) {
		
		String params = "m=api&c=suggestion" + "&content=" + URLEncoder.encode(content) + "&ver="
				+ Util.getVersionName(FeedBackActivity.this);
		params += CenterUrlHelper.getSign(params, CenterUrlHelper.secret);
		return params;
	}
	
}

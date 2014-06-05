package com.mykj.lobby.market;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.AsyncTask.Status;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.navigationdrawerexample.R;
import com.mykj.lobby.market.adapter.TabContentPageAdapter;
import com.mykj.lobby.market.model.GameItem;
import com.mykj.lobby.market.model.Result;
import com.mykj.lobby.utils.AppConfig;
import com.mykj.lobby.utils.CenterUrlHelper;
import com.mykj.lobby.utils.Configs;
import com.mykj.lobby.utils.DensityConst;
import com.mykj.lobby.utils.FileAsyncTaskDownload;
import com.mykj.lobby.utils.ImageAsyncTaskDownload;
import com.mykj.lobby.utils.Util;
import com.mykj.lobby.utils.FileAsyncTaskDownload.DownLoadingListener;

public class GameDetailActivity extends Activity implements OnClickListener {

	protected static final int GET_GAME_XML_SUCCESS = 0;
	protected static final int GET_GAME_XML_FAIL = 1;
	private ImageView ivBack;
	private ViewPager gameInfoViewPager;
	private LinearLayout recommendArea;
	private GameItem gameItem;
	private List<View> mGameInfoViews; // 游戏图片列表
	private TabContentPageAdapter mGameIvPageAdapter;

	private ImageView ivIcon;
	private TextView tvGameName;
	private TextView tvDownloadCount;
	private TextView tvGameSize;
	private TextView tvVersion;
	private TextView tvGameDesc;
	private ButtonView bvDownload;
	private LinearLayout starsArea;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_detail_layout);
		init();

		int gameId = getIntent().getIntExtra("gameId", 0);

		String params = "m=api&c=detail" + "&id=" + gameId + "&ver="
				+ Util.getVersionName(GameDetailActivity.this);
		params += CenterUrlHelper.getSign(params, CenterUrlHelper.secret);

		obtainGameInfo(AppConfig.HOST + "?" + params);
	}

	private void init() {
		ivBack = (ImageView) findViewById(R.id.back);
		ivBack.setOnClickListener(this);
		gameInfoViewPager = (ViewPager) findViewById(R.id.advViewPager);
		recommendArea = (LinearLayout) findViewById(R.id.recommendGameArea);
		gameItem = new GameItem();

		ivIcon = (ImageView) findViewById(R.id.icon);
		tvGameName = (TextView) findViewById(R.id.gameName);
		tvDownloadCount = (TextView) findViewById(R.id.downloadCount);
		tvGameSize = (TextView) findViewById(R.id.gameSize);
		tvVersion = (TextView) findViewById(R.id.version);
		tvGameDesc = (TextView) findViewById(R.id.gameDesc);
		starsArea = (LinearLayout) findViewById(R.id.starsArea);
		bvDownload = (ButtonView) findViewById(R.id.download);
	}

	private void obtainGameInfo(final String url) {
		new Thread() {
			@Override
			public void run() {
				Message msg = mServiceHandler.obtainMessage();

				String parsestr = Configs.getConfigXmlByHttp(url);

				Result result = parseGameInfo(parsestr);

				if ("0".equals(result.getResultCode())) {
					msg.what = GET_GAME_XML_SUCCESS;
				} else {
					msg.what = GET_GAME_XML_FAIL;
				}
				mServiceHandler.sendMessage(msg);
			}

		}.start();
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.back:
			this.finish();
			break;
		default:
			break;
		}
	}

	Handler mServiceHandler = new Handler() {

		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case GET_GAME_XML_SUCCESS:

				tvGameName.setText(gameItem.getGameName());
				tvDownloadCount.setText(String.valueOf(gameItem.getDownloadCount()));
				tvGameSize.setText(gameItem.getGameSize());
				tvVersion.setText(gameItem.getVersion());
				tvGameDesc.setText(gameItem.getGameDesc());
				
				final String gameFileUrl = gameItem.getGameFileUrl(); // 游戏文件路径
				final String gameFilename = Util.getFileNameFromUrl(gameFileUrl); // 游戏文件保存文件名
				final String downloadpath = Util.getSdcardPath()
						+ FileAsyncTaskDownload.APKS_PATH; // 本地存储路径
				String downLoadFileTmpName = gameFilename + ".tmp"; // 设置下载的临时文件名
				
				File downLoadFileTmp = new File(downloadpath, downLoadFileTmpName);
				File downloadFile = new File(downloadpath, gameFilename);

				// 如果临时文件和游戏文件都不在在，则显示下载按钮
				if (downLoadFileTmp.exists()) {
					bvDownload.setBtnAction(ButtonView.CONTINUE);
				} else if (downloadFile.exists()) {
					bvDownload.setBtnAction(ButtonView.START);
				} else {
					bvDownload.setBtnAction(ButtonView.DOWNLOAD);
				}
				
				final DownLoadingListener mDownLoadingListener = new DownLoadingListener() {
					@Override
					public void onProgress(int rate, String strRate) {
						bvDownload.setBtnRate(rate);
					}

					@Override
					public void downloadFail(String err) {

					}

					@Override
					public void downloadSuccess(String strPath) {
						if (!Util.isEmptyStr(strPath)) {
							Util.installApk(GameDetailActivity.this, strPath);
						}
					}

					@Override
					public void setbView(ButtonView detailView,
							ButtonView listView) {
						// TODO Auto-generated method stub
						
					}

				};
				
				final FileAsyncTaskDownload fileAsyncTaskDownload = new FileAsyncTaskDownload(
						mDownLoadingListener, gameFilename);

				bvDownload.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						if (bvDownload.getBtnAction() != ButtonView.PAUSE) {
							if (fileAsyncTaskDownload.getStatus() != Status.FINISHED) {
								fileAsyncTaskDownload.execute(gameFileUrl,
										downloadpath, null);
							} else {
								new FileAsyncTaskDownload(mDownLoadingListener,
										gameFilename).execute(gameFileUrl,
										downloadpath, null);
							}
							bvDownload.setBtnAction(ButtonView.PAUSE);
						} else {
							fileAsyncTaskDownload.flag = false;
							bvDownload.setBtnAction(ButtonView.CONTINUE);
						}
					}
				});
				
				final String iconFileName = gameItem.getIconUrl();
				if (!Util.isEmptyStr(iconFileName)) {
					if (iconFileName.endsWith(".png")
							|| iconFileName.endsWith(".jpg")) {
						String iconDir = Util.getIconDir();
						int end = iconFileName.lastIndexOf("/");
						String url = iconFileName.substring(0, end);
						String fileName = iconFileName.substring(url.length() + 1,
								iconFileName.length());
						File file = new File(iconDir, fileName);
						if (file.exists()) {
							Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
							if (bitmap != null) {
								int width = bitmap.getWidth();
								int height = bitmap.getHeight();
								int disWidth = DensityConst.getWidthPixels();
								Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap,
										width * disWidth / 800,
										height * disWidth / 800, true);
								ivIcon.setImageBitmap(scaleBitmap);
							} else {
								file.delete();
								new ImageAsyncTaskDownload(iconFileName, fileName,
										ivIcon).execute();
							}
						} else {
							new ImageAsyncTaskDownload(iconFileName, fileName,
									ivIcon).execute();
						}
					}
				}

				int leval = gameItem.getLeval();
				starsArea.removeAllViews();
				for (int i = 0; i < leval; i++) {
					TextView starsView = new TextView(GameDetailActivity.this);
					LayoutParams lparams = new LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					starsView.setLayoutParams(lparams);
					Drawable img = GameDetailActivity.this.getResources()
							.getDrawable(R.drawable.stars_light);
					img.setBounds(0, 0, 20, 20);
					starsView.setCompoundDrawables(img, null, null, null);
					starsArea.addView(starsView);
				}
				if (leval < 5) {
					for (int i = 0; i < 5 - leval; i++) {
						TextView starsView = new TextView(
								GameDetailActivity.this);
						LayoutParams lparams = new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT);
						starsView.setLayoutParams(lparams);
						Drawable img = GameDetailActivity.this.getResources()
								.getDrawable(R.drawable.stars_dark);
						img.setBounds(0, 0, 20, 20);
						starsView.setCompoundDrawables(img, null, null, null);
						starsArea.addView(starsView);
					}
				}

				// 显示游戏图片列表信息
				mGameInfoViews = new ArrayList<View>();
				List<String> picUrlList = gameItem.getPicUrlList();
				for (String picUrl : picUrlList) {
					ImageView iv = new ImageView(GameDetailActivity.this);
					LayoutParams lparams = new LayoutParams(10, 10);
					iv.setLayoutParams(lparams);
					final String photoFileName = Util
							.getFileNameFromUrl(picUrl);
					if (!Util.isEmptyStr(photoFileName)) {
						if (photoFileName.endsWith(".png")
								|| photoFileName.endsWith(".jpg")) {
							String iconDir = Util.getIconDir();
							String url = picUrl;
							File file = new File(iconDir, photoFileName);
							if (file.exists()) {
								Bitmap bitmap = BitmapFactory.decodeFile(file
										.getPath());
								if (bitmap != null) {
									int width = bitmap.getWidth();
									int height = bitmap.getHeight();
									int disWidth = DensityConst
											.getWidthPixels();
									Bitmap scaleBitmap = Bitmap
											.createScaledBitmap(bitmap, width
													* disWidth / 800, height
													* disWidth / 800, true);
									iv.setImageBitmap(scaleBitmap);
								} else {
									file.delete();
									new ImageAsyncTaskDownload(url,
											photoFileName, iv).execute();
								}
							} else {
								new ImageAsyncTaskDownload(url, photoFileName,
										iv).execute();
							}
						}
					}
					mGameInfoViews.add(iv);
				}
				mGameIvPageAdapter = new TabContentPageAdapter(mGameInfoViews);
				gameInfoViewPager.setAdapter(mGameIvPageAdapter);

				// 显示伙伴推荐
				List<GameItem> recommendList = gameItem.getRecommendList();
				for (GameItem gameItem : recommendList) {
					LayoutInflater inflater = GameDetailActivity.this
							.getLayoutInflater();
					View item = inflater.inflate(R.layout.recommend_game_item,
							null);

					LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);
					layout.setMargins(30, 0, 0, 0);
					item.setLayoutParams(layout);

					ImageView icon = (ImageView) item
							.findViewById(R.id.game_image);

					final String photoFileName = gameItem.getIconUrl();
					if (!Util.isEmptyStr(photoFileName)) {
						if (photoFileName.endsWith(".png")
								|| photoFileName.endsWith(".jpg")) {
							String iconDir = Util.getIconDir();
							int end = photoFileName.lastIndexOf("/");
							String url = photoFileName.substring(0, end);
							String fileName = photoFileName.substring(
									url.length() + 1, photoFileName.length());
							File file = new File(iconDir, fileName);
							if (file.exists()) {
								Bitmap bitmap = BitmapFactory.decodeFile(file
										.getPath());
								if (bitmap != null) {
									int width = bitmap.getWidth();
									int height = bitmap.getHeight();
									int disWidth = DensityConst
											.getWidthPixels();
									Bitmap scaleBitmap = Bitmap
											.createScaledBitmap(bitmap, width
													* disWidth / 800, height
													* disWidth / 800, true);
									icon.setImageBitmap(scaleBitmap);
								} else {
									file.delete();
									new ImageAsyncTaskDownload(photoFileName,
											fileName, icon).execute();
								}
							} else {
								new ImageAsyncTaskDownload(photoFileName,
										fileName, icon).execute();
							}
						}
					}

					TextView gameName = (TextView) item
							.findViewById(R.id.gamename);
					gameName.setText(gameItem.getGameName());
					recommendArea.addView(item);
				}

				break;
			case GET_GAME_XML_FAIL:
				break;
			default:
				break;
			}
		};
	};

	private Result parseGameInfo(String parsestr) {

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
			parseDataXml(dataStr, "item", "ext"); // 解析游戏信息
		}

		return result;
	}

	public boolean parseDataXml(String strXml, String tagName, String tagName1) {
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
						gameItem.setGameId(Integer.valueOf(p.getAttributeValue(
								null, "id")));
						gameItem.setGameName(p.getAttributeValue(null, "name"));
						String iconUrl = p.getAttributeValue(null, "image");
						iconUrl = iconUrl.replaceAll("&amp;", "&");
						gameItem.setIconUrl(iconUrl);
						gameItem.setLeval(Integer.valueOf(p.getAttributeValue(
								null, "level")));
						gameItem.setDownloadCount(Integer.valueOf(p
								.getAttributeValue(null, "count1")));
						gameItem.setGameDesc(p.getAttributeValue(null,
								"summary"));
						String packageUrl = p
								.getAttributeValue(null, "package");
						packageUrl = packageUrl.replaceAll("&amp;", "&");
						gameItem.setGameFileUrl(packageUrl);
						gameItem.setGameSize(p.getAttributeValue(null, "size"));
						gameItem.setVersion(p.getAttributeValue(null, "version"));
						gameItem.setMd5(p.getAttributeValue(null, "md5"));
					}
					if(tag.equals(tagName1)){
						gameItem.getPicUrlList().add(p.getAttributeValue(null, "image"));
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

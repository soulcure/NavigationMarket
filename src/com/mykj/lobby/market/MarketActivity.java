package com.mykj.lobby.market;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.navigationdrawerexample.R;
import com.mykj.lobby.market.adapter.TabContentPageAdapter;
import com.mykj.lobby.utils.AppConfig;

@SuppressWarnings("deprecation")
public class MarketActivity extends Activity implements OnClickListener {

	protected static final String TAG = "MarketActivity";
	private static final int TAB_INDEX_RECOMMEND = 0;
	private static final int TAB_INDEX_NEWEST = 1;
	private static final int TAB_INDEX_GAME_TYPE = 2;
	private static final int TAB_INDEX_SETTING = 3;
	protected static final int GET_RECOMMEND_GAME_XML_SUCCESS = 1;
	protected static final int GET_RECOMMEND_GAME_XML_FAIL = 0;
	protected static final int GET_NEWEST_GAME_XML_SUCCESS = 11;
	protected static final int GET_NEWEST_GAME_XML_FAIL = 10;
	private TabContentPageAdapter mMarketPageAdapter;
	private ViewPager mViewPager;
	private List<View> mViews;
	private LocalActivityManager manager;
	private TextView tvRecommend;
	private TextView tvNewest;
	private TextView tvGameType;
	private TextView tvSetting;
	private LayoutInflater mInflater;
	private View mSettingView;
	private RelativeLayout aboutArea;
	private RelativeLayout feedbackArea;
	private RelativeLayout helpArea;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_market);
		AppConfig.mContext = this;
		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);
		init();
		initPager();
	}

	private void init() {
		tvRecommend = (TextView) findViewById(R.id.recommend_products);
		tvNewest = (TextView) findViewById(R.id.newest_products);
		tvGameType = (TextView) findViewById(R.id.game_type);
		tvSetting = (TextView) findViewById(R.id.settings);
		tvRecommend.setOnClickListener(this);
		tvNewest.setOnClickListener(this);
		tvGameType.setOnClickListener(this);
		tvSetting.setOnClickListener(this);
	}

	private void initPager() {
		// 获取ViewPager并添加设配器，默认显示第1项，索引为0
		mViewPager = (ViewPager) findViewById(R.id.center_view_pager);

		mViews = new ArrayList<View>();

		Intent recommendIntent = new Intent(this, RecommendActivity.class);
		mViews.add(getView("recommend", recommendIntent));

		Intent newestIntent = new Intent(this, NewestActivity.class);
		mViews.add(getView("newest", newestIntent));

		Intent gameTypeIntent = new Intent(this, GameTypeActivity.class);
		mViews.add(getView("gameType", gameTypeIntent));

		mInflater = getLayoutInflater();
		mSettingView = mInflater.inflate(R.layout.setting_layout, null);
		initSettingView(mSettingView);
		mViews.add(mSettingView);

		mMarketPageAdapter = new TabContentPageAdapter(mViews);
		mViewPager.setAdapter(mMarketPageAdapter);

		setImageBackground(TAB_INDEX_RECOMMEND);
		mViewPager.setCurrentItem(TAB_INDEX_RECOMMEND);

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int pageIndex) {
				switch (pageIndex) {
				case TAB_INDEX_RECOMMEND:
					mViewPager.setCurrentItem(TAB_INDEX_RECOMMEND);
					break;
				case TAB_INDEX_NEWEST:
					mViewPager.setCurrentItem(TAB_INDEX_NEWEST);
					break;
				case TAB_INDEX_GAME_TYPE:
					mViewPager.setCurrentItem(TAB_INDEX_GAME_TYPE);
					break;
				case TAB_INDEX_SETTING:
					mViewPager.setCurrentItem(TAB_INDEX_SETTING);
					break;
				}
				setImageBackground(pageIndex);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}

		});
	}

	private void initSettingView(View view) {
		aboutArea = (RelativeLayout) view.findViewById(R.id.about_area);
		feedbackArea = (RelativeLayout) view.findViewById(R.id.feedbackArea);
		helpArea = (RelativeLayout) view.findViewById(R.id.help_area);
		aboutArea.setOnClickListener(this);
		feedbackArea.setOnClickListener(this);
		helpArea.setOnClickListener(this);
	}

	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}

	private void setImageBackground(int position) {
		switch (position) {
		case TAB_INDEX_RECOMMEND:
			tvRecommend.setBackgroundResource(R.drawable.jingpin_pressed);

			tvNewest.setBackgroundResource(R.drawable.newest_normal);

			tvGameType.setBackgroundResource(R.drawable.game_type_normal);

			tvSetting.setBackgroundResource(R.drawable.setting_normal);

			break;
		case TAB_INDEX_NEWEST:
			tvRecommend.setBackgroundResource(R.drawable.jingpin_normal);

			tvNewest.setBackgroundResource(R.drawable.newest_pressed);

			tvGameType.setBackgroundResource(R.drawable.game_type_normal);

			tvSetting.setBackgroundResource(R.drawable.setting_normal);

			break;
		case TAB_INDEX_GAME_TYPE:
			tvRecommend.setBackgroundResource(R.drawable.jingpin_normal);

			tvNewest.setBackgroundResource(R.drawable.newest_normal);

			tvGameType.setBackgroundResource(R.drawable.game_type_pressed);

			tvSetting.setBackgroundResource(R.drawable.setting_normal);

			break;
		case TAB_INDEX_SETTING:
			tvRecommend.setBackgroundResource(R.drawable.jingpin_normal);

			tvNewest.setBackgroundResource(R.drawable.newest_normal);

			tvGameType.setBackgroundResource(R.drawable.game_type_normal);

			tvSetting.setBackgroundResource(R.drawable.setting_pressed);

			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.recommend_products:
			setImageBackground(TAB_INDEX_RECOMMEND);
			mViewPager.setCurrentItem(TAB_INDEX_RECOMMEND);
			break;
		case R.id.newest_products:
			setImageBackground(TAB_INDEX_NEWEST);
			mViewPager.setCurrentItem(TAB_INDEX_NEWEST);
			break;
		case R.id.game_type:
			setImageBackground(TAB_INDEX_GAME_TYPE);
			mViewPager.setCurrentItem(TAB_INDEX_GAME_TYPE);
			break;
		case R.id.settings:
			setImageBackground(TAB_INDEX_SETTING);
			mViewPager.setCurrentItem(TAB_INDEX_SETTING);
			break;
		case R.id.about_area:
			startActivity(new Intent(MarketActivity.this, AboutActivity.class));
			break;
		case R.id.feedbackArea:
			startActivity(new Intent(MarketActivity.this,
					FeedBackActivity.class));
			break;
		case R.id.help_area:
			startActivity(new Intent(MarketActivity.this, HelpActivity.class));
			break;

		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	    if (resultCode == RESULT_CANCELED) {
	    	
	    }  
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		return super.onKeyDown(keyCode, event);
	}

}

package com.mykj.lobby.fragment;


import java.util.ArrayList;
import java.util.List;




import com.example.android.navigationdrawerexample.R;
import com.mykj.lobby.market.adapter.CardZoneViewPagerAdapter;
import com.mykj.lobby.utils.AppConfig;
import com.mykj.lobby.utils.CenterUrlHelper;
import com.mykj.lobby.utils.DensityConst;
import com.mykj.lobby.utils.Util;
import com.mykj.lobby.view.BestAppSwipeRefreshLayout;
import com.mykj.lobby.view.RankingAppSwipeRefreshLayout;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;




public class MarketFragment extends Fragment {
	protected static final String TAG = "MarketFragment";


	public static String[] tabTitle;

	private Activity mAct;

	private RadioGroup rgNav;
	private ImageView imgNav;
	private ViewPager mViewPager;
	private int currentIndicatorLeft = 0;


	private int indicatorWidth;


	private int page = 1;
	private int pageSize = 10;

	protected static final int GET_RECOMMEND_GAME_XML_SUCCESS = 1;
	protected static final int GET_RECOMMEND_GAME_XML_FAIL = 0;
	protected static final int GET_ADV_XML_SUCCESS = 11;
	protected static final int GET_ADV_XML_FAIL = 10;

	public static final int PARSE_DATA_TYPE_ADV = 1; // 解析数据类型-广告
	public static final int PARSE_DATA_TYPE_GAME = 2; // 解析数据类型-推荐游戏


	public MarketFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mAct=activity;
	}


	/**
	 * When creating, retrieve this instance's number from its arguments.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tabTitle=getResources().getStringArray(R.array.tab_array);

	}




	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.market_fragment, container, false);
		initView(rootView,inflater);
		return rootView;
	}



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

	}


	private void initView(View view,LayoutInflater inflater){
		indicatorWidth = DensityConst.getWidthPixels()/tabTitle.length;

		rgNav = (RadioGroup) view.findViewById(R.id.rgNav);
		rgNav.removeAllViews();

		for(int i=0;i<tabTitle.length;i++){

			RadioButton rb = (RadioButton) inflater.inflate(R.layout.nav_radiogroup_item, null);
			rb.setId(i);
			rb.setText(tabTitle[i]);
			rb.setLayoutParams(new LayoutParams(indicatorWidth,
					LayoutParams.MATCH_PARENT));

			rgNav.addView(rb);
		}


		imgNav = (ImageView) view.findViewById(R.id.imgNav);
		mViewPager = (ViewPager) view.findViewById(R.id.mViewPager);

		LayoutParams cursor_Params = imgNav.getLayoutParams();
		cursor_Params.width = indicatorWidth;
		imgNav.setLayoutParams(cursor_Params);


		//		recommendListAdapter = new RecommendListAdapter(new ArrayList<GameItem>(),mAct);
		//		String params = "m=api&c=recommend&page=" + page + "&size="
		//				+ pageSize + "&ver="
		//				+ Util.getVersionName(mAct);
		//		params += CenterUrlHelper.getSign(params, CenterUrlHelper.secret);
		//
		//		String url=AppConfig.HOST + "?" + params;
		//
		//		new HttpAsyncTask().execute(url);

		List<View> views=getCardZoneViews(inflater);

		PagerAdapter adapter=new CardZoneViewPagerAdapter(views);
		mViewPager.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				if(rgNav!=null && rgNav.getChildCount()>arg0){
					((RadioButton)rgNav.getChildAt(arg0)).performClick();
				}
				
				
				CardZoneViewPagerAdapter adapter=(CardZoneViewPagerAdapter) mViewPager.getAdapter();
				View v=adapter.getView(arg0);
				if(v instanceof RankingAppSwipeRefreshLayout){
					if(((RankingAppSwipeRefreshLayout) v).isEmpty()){
						((RankingAppSwipeRefreshLayout) v).refresh();
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		rgNav.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				if(rgNav.getChildAt(checkedId)!=null){

					TranslateAnimation animation = new TranslateAnimation(
							currentIndicatorLeft ,
							((RadioButton) rgNav.getChildAt(checkedId)).getLeft(), 0f, 0f);
					animation.setInterpolator(new LinearInterpolator());
					animation.setDuration(100);
					animation.setFillAfter(true);

					imgNav.startAnimation(animation);

					mViewPager.setCurrentItem(checkedId);	//ViewPager

					currentIndicatorLeft = ((RadioButton) rgNav.getChildAt(checkedId)).getLeft();
				}
			}
		});

		mViewPager.setCurrentItem(0);

		((RadioButton)rgNav.getChildAt(0)).performClick();
	}



	private List<View>  getCardZoneViews(LayoutInflater inflater){
		List<View> listViews=new ArrayList<View>();
		String[] tTitles2 = getResources().getStringArray(R.array.planets_array2);
		String[] tTitles3 = getResources().getStringArray(R.array.planets_array3);

		ArrayAdapter<String> ad2= new ArrayAdapter<String>(mAct,R.layout.app_list_item, tTitles2);
		ArrayAdapter<String> ad3= new ArrayAdapter<String>(mAct,R.layout.app_list_item, tTitles3);

		String paramsBest = "m=api&c=recommend&page=" + page + "&size="
				+ pageSize + "&ver="
				+ Util.getVersionName(mAct);
		paramsBest += CenterUrlHelper.getSign(paramsBest, CenterUrlHelper.secret);
		final String urlBest=AppConfig.HOST + "?" + paramsBest;

		final BestAppSwipeRefreshLayout bestSwipeLayout = new BestAppSwipeRefreshLayout(mAct,urlBest);
		bestSwipeLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				getActivity().setTitle("下拉刷新");
				bestSwipeLayout.refresh(urlBest);
			}
		});

		
		
		getActivity().setTitle("商城首页");


		String paramsRank = "m=api&c=rank&page=" + page + "&size=" + pageSize
				+ "&ver=" + Util.getVersionName(mAct);
		paramsRank += CenterUrlHelper.getSign(paramsRank, CenterUrlHelper.secret);
		
		final String urlRank=AppConfig.HOST + "?" + paramsRank;
		
		final RankingAppSwipeRefreshLayout rankSwipeLayout = new RankingAppSwipeRefreshLayout(mAct,urlRank);
		rankSwipeLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				getActivity().setTitle("下拉刷新");
				rankSwipeLayout.refresh(urlRank);
			}
		});
		rankSwipeLayout.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				rankSwipeLayout.refresh();
			}
		});

		ListView v2=new ListView(mAct);
		ListView v3=new ListView(mAct);

		v2.setAdapter(ad2);
		v3.setAdapter(ad3);

		listViews.add(bestSwipeLayout);
		listViews.add(rankSwipeLayout);
		listViews.add(v2);
		listViews.add(v3);

		return listViews;
	}

















}

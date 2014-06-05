package com.mykj.lobby.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.android.navigationdrawerexample.R;
import com.mykj.lobby.market.AboutActivity;
import com.mykj.lobby.market.FeedBackActivity;
import com.mykj.lobby.market.HelpActivity;
import com.mykj.lobby.market.MarketActivity;

public class SettingFragment extends Fragment implements View.OnClickListener {


	private Activity mAct;

	private View mSettingView;
	private RelativeLayout aboutArea;
	private RelativeLayout feedbackArea;
	private RelativeLayout helpArea;

	public SettingFragment() {
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

	}




	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.setting_layout, container, false);
		initView(rootView);
		return rootView;
	}



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

	}

	private void initView(View view){
		aboutArea = (RelativeLayout) view.findViewById(R.id.about_area);
		feedbackArea = (RelativeLayout) view.findViewById(R.id.feedbackArea);
		helpArea = (RelativeLayout) view.findViewById(R.id.help_area);
		aboutArea.setOnClickListener(this);
		feedbackArea.setOnClickListener(this);
		helpArea.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.settings:
			break;
		case R.id.about_area:
			break;
		case R.id.feedbackArea:
			break;
		case R.id.help_area:
			break;
		}
	}

}

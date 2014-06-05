package com.mykj.lobby.market;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.navigationdrawerexample.R;
import com.mykj.lobby.utils.Util;

public class AboutActivity extends Activity implements OnClickListener{
	
	private TextView tvVersion;
	private ImageView ivBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_layout);
		init();
	}
	
	private void init(){
		tvVersion = (TextView)findViewById(R.id.tvVersion);
		tvVersion.setText(Util.getVersionName(AboutActivity.this));
		ivBack = (ImageView)findViewById(R.id.back);
		ivBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if(id == R.id.back){
			finish();
		}
	}
	
}

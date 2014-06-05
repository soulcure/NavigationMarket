package com.mykj.lobby.market;




import com.example.android.navigationdrawerexample.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ButtonView extends RelativeLayout{


	private Context mContext;

	private TextView tvRate;   
	private TextView tvAction;   
	
	private int action;

	private static final int BASE_ID = 0x00af5555;
	public static final int TVRATE_ID=BASE_ID+1;
	public static final int TVACTION_ID=BASE_ID+2;




	public ButtonView(Context context,AttributeSet attrs){  
		super(context,attrs);  
		mContext=context;
		RelativeLayout container = this;

		tvRate=new TextView(mContext);
		tvRate.setGravity(Gravity.CENTER);
		tvRate.setTextSize(10);
		tvRate.setTextColor(mContext.getResources().getColor(R.color.download_process_color));
		tvRate.setId(TVRATE_ID);

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		container.addView(tvRate, lp);


		lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		tvAction=new TextView(mContext);
		tvAction.setGravity(Gravity.CENTER);
		tvAction.setId(TVACTION_ID);	
		tvAction.setTextSize(12);
		lp.addRule(RelativeLayout.BELOW,TVRATE_ID);
		lp.addRule(RelativeLayout.ALIGN_LEFT,TVRATE_ID);
		lp.addRule(RelativeLayout.ALIGN_RIGHT,TVRATE_ID);
		container.addView(tvAction, lp);
	}




	public static final int DOWNLOAD=0;  //下载
	public static final int PAUSE=1;     //暂停
	public static final int START=2;     //启动  
	public static final int CONTINUE=3;  //继续

	public int getBtnAction(){
		return action;
	}

	public void setBtnAction(int action){
		this.action = action;
		if(tvAction!=null && tvRate!=null){
			switch (action) {
			case DOWNLOAD:
				tvAction.setTextColor(getResources().getColor(R.color.blue_btn_text_color));
				tvAction.setText(getResources().getString(R.string.download));

				tvRate.setText("");
				tvRate.setBackgroundResource(R.drawable.download);

				break;
			case PAUSE:
				tvAction.setTextColor(getResources().getColor(R.color.bg_clolor));
				tvAction.setText(getResources().getString(R.string.pause));

				tvRate.setText("");
				tvRate.setBackgroundResource(R.drawable.btn_downloading);
				break;
			case START:
				tvAction.setTextColor(getResources().getColor(R.color.share_bg_clolor));
				tvAction.setText(getResources().getString(R.string.start));

				tvRate.setText("");
				tvRate.setBackgroundResource(R.drawable.btn_start);
				break;
			case CONTINUE:
				tvAction.setTextColor(getResources().getColor(R.color.hilight_yellow));
				tvAction.setText(getResources().getString(R.string.continued));

				tvRate.setText("");
				tvRate.setBackgroundResource(R.drawable.btn_downloading);
				break;
			default:
				break;
			}

		}
	}

	public void setBtnRate(int rate){
		if(tvRate!=null){
			tvRate.setBackgroundResource(R.drawable.btn_downloading);
			tvRate.setText(rate+"%");
		}
	}

}
package com.mykj.lobby.market.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;


public class TabFragmentPagerAdapter extends FragmentPagerAdapter{

	private ArrayList<Fragment> mFragmentList;  
	private FragmentManager fm;  


	public TabFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}


	public TabFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {  
		super(fm);  
		this.fm = fm;  
		this.mFragmentList = fragments;  
	}  

	@Override  
	public int getCount() {  
		return mFragmentList.size();  
	}  

	@Override  
	public Fragment getItem(int arg0) {  
		return mFragmentList.get(arg0);  
	}  

	@Override  
	public int getItemPosition(Object object) {  
		return POSITION_NONE;  
	}  

	
	public void setFragmentsList(ArrayList<Fragment> fragmentsList) {  
		if(mFragmentList != null){  
			FragmentTransaction ft   = fm.beginTransaction();  
			for(Fragment f:fragmentsList){  
				ft.remove(f);  
			}  
			ft.commit();  
			ft= null;  
			mFragmentList = fragmentsList;  
			notifyDataSetChanged();  
		}  
	}  
}
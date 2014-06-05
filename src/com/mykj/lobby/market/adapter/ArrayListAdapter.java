package com.mykj.lobby.market.adapter;

import java.util.ArrayList;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public abstract class ArrayListAdapter<T> extends BaseAdapter {

	protected ArrayList<T> mList;
	protected Context mContext;
	protected ListView mListView;

	public ArrayListAdapter(ArrayList<T> list,Context context) {
		this.mContext = context;
		this.mList=list;
	}

	@Override
	public int getCount() {
		if (mList != null)
			return mList.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		return mList == null ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView,
			ViewGroup parent);

	public void setList(ArrayList<T> list) {
		this.mList = list;
		notifyDataSetChanged();
	}

	public ArrayList<T> getList() {
		return mList;
	}

	public void setList(T[] list) {
		ArrayList<T> arrayList = new ArrayList<T>(list.length);
		for (T t : list) {
			arrayList.add(t);
		}
		setList(arrayList);
	}

	public void addItem(T item){
		if(!mList.contains(item)){
			mList.add(item);
			//notifyDataSetChanged();
		}
	}


	public void setListView(ListView listView) {
		mListView = listView;
	}

	public ListView getListView() {
		return mListView;
	}



}

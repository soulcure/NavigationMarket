package com.mykj.lobby.market.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.navigationdrawerexample.R;
import com.mykj.lobby.market.model.GameType;
import com.mykj.lobby.utils.DensityConst;
import com.mykj.lobby.utils.ImageAsyncTaskDownload;
import com.mykj.lobby.utils.Util;

public class GameTypeAdapter extends BaseAdapter {
	private List<GameType> mGameTypeList; // 图片bitmap
	private Context mContext;

	public GameTypeAdapter(List<GameType> list, Context context) {
		mGameTypeList = list;
		mContext = context;

	}

	@Override
	public int getCount() {
		return mGameTypeList.size();
	}

	@Override
	public Object getItem(int position) {
		return mGameTypeList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final GameType gameType = mGameTypeList.get(position);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.game_type_item, null); // 实例化convertView
			holder.ivIcon = (ImageView) convertView.findViewById(R.id.icon);
			holder.tvTypeName = (TextView) convertView
					.findViewById(R.id.typeName);
			holder.tvGameCount = (TextView) convertView
					.findViewById(R.id.gameCount);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final String photoFileName = Util.getFileNameFromUrl(gameType
				.getIconUrl());
		if (!Util.isEmptyStr(photoFileName)) {
			if (photoFileName.endsWith(".png")
					|| photoFileName.endsWith(".jpg")) {
				String iconDir = Util.getIconDir();
				String url = gameType.getIconUrl();
				File file = new File(iconDir, photoFileName);
				if (file.exists()) {
					Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
					if (bitmap != null) {
						int width = bitmap.getWidth();
						int height = bitmap.getHeight();
						int disWidth = DensityConst.getWidthPixels();
						Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap,
								width * disWidth / 800,
								height * disWidth / 800, true);
						holder.ivIcon.setImageBitmap(scaleBitmap);
					} else {
						file.delete();
						new ImageAsyncTaskDownload(url, photoFileName,
								holder.ivIcon).execute();
					}
				} else {
					new ImageAsyncTaskDownload(url, photoFileName,
							holder.ivIcon).execute();
				}
			}
		}

		holder.tvTypeName.setText(gameType.getTypeName());
		holder.tvGameCount.setText(String.valueOf(gameType.getGameCount())
				+ mContext.getResources().getString(R.string.game_count_unit));

		return convertView;

	}

	class ViewHolder {
		ImageView ivIcon; // 游戏分类图标
		TextView tvTypeName; // 游戏分类名称
		TextView tvGameCount; // 游戏数量
	}

}

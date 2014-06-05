package com.mykj.lobby.market.adapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.navigationdrawerexample.R;
import com.mykj.lobby.market.ButtonView;
import com.mykj.lobby.market.model.GameItem;
import com.mykj.lobby.utils.AppConfig;
import com.mykj.lobby.utils.DensityConst;
import com.mykj.lobby.utils.FileAsyncTaskDownload;
import com.mykj.lobby.utils.FileAsyncTaskDownload.DownLoadingListener;
import com.mykj.lobby.utils.ImageAsyncTaskDownload;
import com.mykj.lobby.utils.Util;

public class RecommendListAdapter extends BaseAdapter {
	private List<GameItem> mGameList; // 图片bitmap
	private Activity mAct;
	private Map<String, FileAsyncTaskDownload> taskMap = new HashMap<String, FileAsyncTaskDownload>();
	private int installRequestCode = 3;

	public RecommendListAdapter(List<GameItem> list, Activity act) {
		mGameList = list;
		mAct = act;
	}

	@Override
	public int getCount() {
		return mGameList.size();
	}

	@Override
	public Object getItem(int position) {
		return mGameList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	public void setmGameList(List<GameItem> mGameList) {
		this.mGameList = mGameList;
	}

	public void addItem(GameItem item){
		mGameList.add(item);
		notifyDataSetChanged();
	}
	
	private String getFileNameFromUrl(String strUrl) {
		String fileName = null;

		try {
			if (strUrl != null) {
				String[] tmpStrArray = strUrl.split("/");
				fileName = tmpStrArray[tmpStrArray.length - 1];
				if (fileName == null || fileName.trim().length() == 0) {
					fileName = null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fileName;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final GameItem gameItem = mGameList.get(position);
		final String gameFileUrl = gameItem.getGameFileUrl(); // 游戏文件路径
		final String gameFilename = getFileNameFromUrl(gameFileUrl); // 游戏文件保存文件名
		final String downloadpath = Util.getSdcardPath()
				+ FileAsyncTaskDownload.APKS_PATH; // 本地存储路径
		String downLoadFileTmpName = gameFilename + ".tmp"; // 设置下载的临时文件名

		if (convertView == null) {
			LayoutInflater inflater = mAct.getLayoutInflater();
			convertView = inflater.inflate(R.layout.recommend_item, null);
			holder = new ViewHolder();
			holder.ivGame = (ImageView) convertView.findViewById(R.id.icon);
			holder.tvGameName = (TextView) convertView
					.findViewById(R.id.gameName);
			holder.starsbar = (RatingBar) convertView
					.findViewById(R.id.starsArea);
			holder.tvDownloadCount = (TextView) convertView
					.findViewById(R.id.downloadCount);
			holder.tvGameDesc = (TextView) convertView
					.findViewById(R.id.gameDesc);
			holder.btnAction = (ButtonView) convertView
					.findViewById(R.id.btnAction);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		int leval = gameItem.getLeval();
		holder.starsbar.setNumStars(leval);

		String photoFileName = gameItem.getIconUrl();
				int end = photoFileName.lastIndexOf("/");
				String url = photoFileName.substring(0, end);
				String fileName = photoFileName.substring(url.length() + 1,
						photoFileName.length());
						new ImageAsyncTaskDownload(photoFileName, fileName,
								holder.ivGame).execute();
		
		
		
		holder.tvGameName.setText(gameItem.getGameName());
		holder.tvDownloadCount.setText(String.valueOf(gameItem
				.getDownloadCount()));
		holder.tvGameDesc.setText(gameItem.getGameDesc());

		File downLoadFileTmp = new File(downloadpath, downLoadFileTmpName);
		File downloadFile = new File(downloadpath, gameFilename);

		// 如果临时文件和游戏文件都不在在，则显示下载按钮
		if (downLoadFileTmp.exists()) {
			holder.btnAction.setBtnAction(ButtonView.CONTINUE);
		} else if (downloadFile.exists()) {
			holder.btnAction.setBtnAction(ButtonView.START);
		} else {
			holder.btnAction.setBtnAction(ButtonView.DOWNLOAD);
		}

		final DownLoadingListener mDownLoadingListener = new DownLoadingListener() {

			@Override
			public void onProgress(int rate, String strRate) {
				holder.btnAction.setBtnRate(rate);
			}

			@Override
			public void downloadFail(String err) {

			}

			@Override
			public void downloadSuccess(String strPath) {
				holder.btnAction.setBtnAction(ButtonView.START);
				/*if (!Util.isEmptyStr(strPath)) {
					Util.installApk(mAct, strPath);
				}*/
			}

			@Override
			public void setbView(ButtonView detailView, ButtonView listView) {
				// TODO Auto-generated method stub
				
			}

		};

		holder.btnAction.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FileAsyncTaskDownload fileAsyncTaskDownload = taskMap.get(String.valueOf(gameItem.getGameId()));
				if(fileAsyncTaskDownload == null){
					fileAsyncTaskDownload = new FileAsyncTaskDownload(mDownLoadingListener,
							gameFilename);
					taskMap.put(String.valueOf(gameItem.getGameId()), fileAsyncTaskDownload);
				}
				if (holder.btnAction.getBtnAction() == ButtonView.DOWNLOAD || holder.btnAction.getBtnAction() == ButtonView.CONTINUE) {
					if (fileAsyncTaskDownload.getStatus() != Status.FINISHED) {
						fileAsyncTaskDownload.execute(gameFileUrl,
								downloadpath, null);
					} else {
						fileAsyncTaskDownload = new FileAsyncTaskDownload(mDownLoadingListener,
								gameFilename);
						taskMap.put(String.valueOf(gameItem.getGameId()), fileAsyncTaskDownload);
						fileAsyncTaskDownload.execute(gameFileUrl,
								downloadpath, null);
					}
					holder.btnAction.setBtnAction(ButtonView.PAUSE);
				} else if(holder.btnAction.getBtnAction() == ButtonView.PAUSE){
					fileAsyncTaskDownload.flag = false;
					holder.btnAction.setBtnAction(ButtonView.CONTINUE);
				} else if(holder.btnAction.getBtnAction() == ButtonView.START){
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse("file://" + downloadpath + "/" + gameFilename),
							"application/vnd.android.package-archive");
					mAct.startActivityForResult(intent, gameItem.getGameId());
				}
			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView ivGame; // 游戏图标
		TextView tvGameName; // 游戏名称
		RatingBar starsbar; // 星级内容
		TextView tvDownloadCount; // 下载量
		TextView tvGameDesc; // 游戏名称
		ButtonView btnAction;
	}

}

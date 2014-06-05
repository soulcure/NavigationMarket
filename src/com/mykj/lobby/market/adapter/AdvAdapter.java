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

import com.example.android.navigationdrawerexample.R;
import com.mykj.lobby.market.model.AdvItem;
import com.mykj.lobby.utils.DensityConst;
import com.mykj.lobby.utils.ImageAsyncTaskDownload;
import com.mykj.lobby.utils.Util;

public class AdvAdapter extends BaseAdapter{ 
	private List<AdvItem> mImgList;       //图片bitmap   
	private Context mContext;  

	public AdvAdapter(List<AdvItem> list, Context context) {  
		mImgList = list;  
		mContext = context; 

	}

	@Override  
	public int getCount() {  
		return mImgList.size();  
	}  

	@Override  
	public Object getItem(int position) {  
		return mImgList.get(position);
	}  

	@Override  
	public long getItemId(int position) {  

		return position;  
	}  

	@Override  
	public View getView(int position, View convertView, ViewGroup parent) { 
		final AdvItem advItem=mImgList.get(position);
		ImageView gallery_image;  
		if(convertView==null){  
			convertView = LayoutInflater.from(mContext).inflate(R.layout.gallery_img_item, null); //实例化convertView  
			gallery_image = (ImageView)convertView.findViewById(R.id.gallery_image);	
			final String photoFileName=advItem.getFileName();
			if (!Util.isEmptyStr(photoFileName)) {
				if (photoFileName.endsWith(".png")||photoFileName.endsWith(".jpg")) {
					String iconDir=Util.getIconDir();
					String url = advItem.getUrlPath();
					File file=new File(iconDir,photoFileName);
					if(file.exists()){
						Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
						if(bitmap!=null){
							int width = bitmap.getWidth();
							int height = bitmap.getHeight();
							int disWidth = DensityConst.getWidthPixels();
							Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, width
									* disWidth / 800, height * disWidth / 800, true);
							gallery_image.setImageBitmap(scaleBitmap);
						}else{
							file.delete();
							new ImageAsyncTaskDownload(url, photoFileName, gallery_image).execute();
						}
					}else{
						new ImageAsyncTaskDownload(url, photoFileName, gallery_image).execute();
					}
				}
			}
			convertView.setTag(gallery_image);  
		}  
		else{  
			gallery_image = (ImageView) convertView.getTag();  
		}  

		return convertView;  

	}  


}

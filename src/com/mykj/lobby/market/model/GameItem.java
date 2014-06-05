package com.mykj.lobby.market.model;

import java.util.ArrayList;
import java.util.List;

public class GameItem {
	private int gameId;
	private String iconUrl;
	private String gameName;
	private int downloadCount;
	private int leval;
	private String gameSize;
	private String version;
	private String gameDesc;
	private String detailUrl;
	private String gameFileUrl;
	private String md5;
	private List<String> picUrlList = new ArrayList<String>(); // 描述图片路径列表
	private List<GameItem> recommendList = new ArrayList<GameItem>(); // 小伙伴推荐游戏列表
	
	public GameItem(){
		
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public int getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(int downloadCount) {
		this.downloadCount = downloadCount;
	}

	public int getLeval() {
		return leval;
	}

	public String getGameSize() {
		return gameSize;
	}

	public void setGameSize(String gameSize) {
		this.gameSize = gameSize;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setLeval(int leval) {
		this.leval = leval;
	}

	public String getGameDesc() {
		return gameDesc;
	}

	public void setGameDesc(String gameDesc) {
		this.gameDesc = gameDesc;
	}

	public String getDetailUrl() {
		return detailUrl;
	}

	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}

	public List<String> getPicUrlList() {
		return picUrlList;
	}

	public void setPicUrlList(List<String> picUrlList) {
		this.picUrlList = picUrlList;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public List<GameItem> getRecommendList() {
		return recommendList;
	}

	public void setRecommendList(List<GameItem> recommendList) {
		this.recommendList = recommendList;
	}

	public String getGameFileUrl() {
		return gameFileUrl;
	}

	public void setGameFileUrl(String gameFileUrl) {
		this.gameFileUrl = gameFileUrl;
	}
}

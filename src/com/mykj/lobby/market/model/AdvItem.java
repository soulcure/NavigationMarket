package com.mykj.lobby.market.model;

import com.mykj.lobby.utils.Util;

public class AdvItem
{
	private int id;
	private String urlPath = null;
	private String fileName = null;
	private String advName; // 广告名称
	private String memo; // 广告说明
	private int gameId; // 广告代表的游戏ID

	public AdvItem(String url)
	{
		this.urlPath = url;
		this.fileName = Util.getFileNameFromUrl(url);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAdvName() {
		return advName;
	}

	public void setAdvName(String advName) {
		this.advName = advName;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	
}

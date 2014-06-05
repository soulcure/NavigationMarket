package com.mykj.lobby.market.model;

public class GameType {
	private String id; // 分类ID
	private String typeName; // 分类名称
	private String gameCount; // 游戏数量
	private String iconUrl; // 图标路径
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getGameCount() {
		return gameCount;
	}
	public void setGameCount(String gameCount) {
		this.gameCount = gameCount;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
}

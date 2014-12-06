package com.omelet.shadowdriends.model;

public class PackItem {
	
	private String rid;
	private String packTitle;
	private String packDescription;
	private int distance;
	private String currentStatus;
	private int owner;
	
	public String getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}
	public String getPackTitle() {
		return packTitle;
	}
	public void setPackTitle(String packTitle) {
		this.packTitle = packTitle;
	}
	public String getPackDescription() {
		return packDescription;
	}
	public void setPackDescription(String packDescription) {
		this.packDescription = packDescription;
	}
	public int getOwner() {
		return owner;
	}
	public void setOwner(int owner) {
		this.owner = owner;
	}
}

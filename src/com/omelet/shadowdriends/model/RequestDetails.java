package com.omelet.shadowdriends.model;

public class RequestDetails {
	
	private String rid;
	private String title;
	private String des;
	private String senderContactNumber;
	private String receiverContactNumber;
	private String carrierContactNumber;
	private String currentStatus;
	private double senderLat;
	private double senderLon;
	private double receiverLat;
	private double receiverLon;
	private String senderName;
	private String receiverName;
	private String carrierName;
	
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public String getSenderContactNumber() {
		return senderContactNumber;
	}
	public void setSenderContactNumber(String senderContactNumber) {
		this.senderContactNumber = senderContactNumber;
	}
	public String getReceiverContactNumber() {
		return receiverContactNumber;
	}
	public void setReceiverContactNumber(String receiverContactNumber) {
		this.receiverContactNumber = receiverContactNumber;
	}
	public String getCarrierContactNumber() {
		return carrierContactNumber;
	}
	public void setCarrierContactNumber(String carrierContactNumber) {
		this.carrierContactNumber = carrierContactNumber;
	}
	public String getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	public double getSenderLat() {
		return senderLat;
	}
	public void setSenderLat(double senderLat) {
		this.senderLat = senderLat;
	}
	public double getSenderLon() {
		return senderLon;
	}
	public void setSenderLon(double senderLon) {
		this.senderLon = senderLon;
	}
	public double getReceiverLat() {
		return receiverLat;
	}
	public void setReceiverLat(double receiverLat) {
		this.receiverLat = receiverLat;
	}
	public double getReceiverLon() {
		return receiverLon;
	}
	public void setReceiverLon(double receiverLon) {
		this.receiverLon = receiverLon;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public String getCarrierName() {
		return carrierName;
	}
	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}
	
}

package edu.mit.mitmobile2.objs;

import android.os.Parcel;
import android.os.Parcelable;

public class HoldListItem implements Parcelable {
	
	private int index;
	private String status;
	private String description;
	private String docNumber;
	private String material;
	private String subLibrary;
	private String barCode;
	private String callNo;
	private String author;
	private String year;
	private String title;
	private String imprint;
	private String pickupLocation;
	private String endHoldDate;
	private String ready;
	private String isbnIssnDisplay;
	private String isbnIssnType;

	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDocNumber() {
		return docNumber;
	}
	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public String getSubLibrary() {
		return subLibrary;
	}
	public void setSubLibrary(String subLibrary) {
		this.subLibrary = subLibrary;
	}
	public String getBarCode() {
		return barCode;
	}
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}
	public String getCallNo() {
		return callNo;
	}
	public void setCallNo(String callNo) {
		this.callNo = callNo;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImprint() {
		return imprint;
	}
	public void setImprint(String imprint) {
		this.imprint = imprint;
	}
	public String getPickupLocation() {
		return pickupLocation;
	}
	public void setPickupLocation(String pickupLocation) {
		this.pickupLocation = pickupLocation;
	}
	public String getEndHoldDate() {
		return endHoldDate;
	}
	public void setEndHoldDate(String endHoldDate) {
		this.endHoldDate = endHoldDate;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String getReady() {
		return ready;
	}
	public void setReady(String ready) {
		this.ready = ready;
	}
	
	
	public String getIsbnIssnDisplay() {
		return isbnIssnDisplay;
	}
	public void setIsbnIssnDisplay(String isbnIssnDisplay) {
		this.isbnIssnDisplay = isbnIssnDisplay;
	}
	public String getIsbnIssnType() {
		return isbnIssnType;
	}
	public void setIsbnIssnType(String isbnIssnType) {
		this.isbnIssnType = isbnIssnType;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub  public void writeToParcel(Parcel dest, int flags) {     
		dest.writeString(status);
		dest.writeString(description);
		dest.writeString(docNumber);
		dest.writeString(material);
		dest.writeString(subLibrary);
		dest.writeString(barCode);
		dest.writeString(callNo);
		dest.writeString(author);
		dest.writeString(year);
		dest.writeString(title);
		dest.writeString(imprint);
		dest.writeString(pickupLocation);
		dest.writeString(endHoldDate);
		dest.writeString(ready);
		dest.writeString(isbnIssnDisplay);
		dest.writeString(isbnIssnType);
	}
} 
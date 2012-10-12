package edu.mit.mitmobile2.objs;

import android.os.Parcel;
import android.os.Parcelable;

public class LoanListItem implements Parcelable {
	
	// # List
	private int index;
	
    public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	private String loanDate;
    public String getLoanDate() {
		return loanDate;
	}
	public void setLoanDate(String loanDate) {
		this.loanDate = loanDate;
	}
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	public String getReturnedDate() {
		return returnedDate;
	}
	public void setReturnedDate(String returnedDate) {
		this.returnedDate = returnedDate;
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
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public boolean isOverdue() {
		return overdue;
	}
	public void setOverdue(boolean overdue) {
		this.overdue = overdue;
	}
	public boolean isLongOverdue() {
		return longOverdue;
	}
	public void setLongOverdue(boolean longOverdue) {
		this.longOverdue = longOverdue;
	}
	public String getDisplayPendingFine() {
		return displayPendingFine;
	}
	public void setDisplayPendingFine(String displayPendingFine) {
		this.displayPendingFine = displayPendingFine;
	}
	public String getPendingFine() {
		return pendingFine;
	}
	public void setPendingFine(String pendingFine) {
		this.pendingFine = pendingFine;
	}
	public boolean isHasHold() {
		return hasHold;
	}
	public void setHasHold(boolean hasHold) {
		this.hasHold = hasHold;
	}
	public String getDueText() {
		return dueText;
	}
	public void setDueText(String dueText) {
		this.dueText = dueText;
	}
	private String dueDate;
    private String returnedDate;
    private String docNumber;
    private String material;
    private String subLibrary;
    private String barcode;
    private String status;
    private String callNo;
    private String author;
    private String year;
    private String title;
    private String imprint;
    private String isbnIssnDisplay;
    private String isbnIssnType;
    private boolean overdue;
    private boolean longOverdue;
    private String displayPendingFine;
    private String pendingFine;
    private boolean hasHold;
    private String dueText;
    private boolean renewBook = false;
    
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public boolean isRenewBook() {
		return renewBook;
	}
	public void setRenewBook(boolean renewBook) {
		this.renewBook = renewBook;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub  public void writeToParcel(Parcel dest, int flags) {     
        dest.writeString(dueDate);
        dest.writeString(returnedDate);
        dest.writeString(docNumber);
        dest.writeString(material);
        dest.writeString(subLibrary);
        dest.writeString(barcode);
        dest.writeString(status);
        dest.writeString(callNo);
        dest.writeString(author);
        dest.writeString(year);
        dest.writeString(title);
        dest.writeString(imprint);
        dest.writeString(isbnIssnDisplay);
        dest.writeString(isbnIssnType);
        dest.writeString(displayPendingFine);
        dest.writeString(pendingFine);
        dest.writeString(dueText);
	}
} 
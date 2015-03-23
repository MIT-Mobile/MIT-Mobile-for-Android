package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;
import java.util.Date;
import edu.mit.mitmobile2.objs.FineListItem;

public class FineData {

	private String balance;
	private Date fineDate;
	private ArrayList<FineListItem> fines = new ArrayList<FineListItem>();
	private boolean requestCancelled;
	
	public ArrayList<FineListItem> getFines() {
		return fines;
	}
	public void setFines(ArrayList<FineListItem> fines) {
		this.fines = fines;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public Date getFineDate() {
		return fineDate;
	}
	public void setFineDate(Date fineDate) {
		this.fineDate = fineDate;
	}
	public boolean isRequestCancelled() {
		return requestCancelled;
	}
	public void setRequestCancelled(boolean requestCancelled) {
		this.requestCancelled = requestCancelled;
	}
	
}

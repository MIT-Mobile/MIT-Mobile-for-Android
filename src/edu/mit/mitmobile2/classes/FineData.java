package edu.mit.mitmobile2.classes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.mit.mitmobile2.objs.FineListItem;
import edu.mit.mitmobile2.objs.LoanListItem;

public class FineData {

	private String balance;
	private Date fineDate;
	private ArrayList<FineListItem> fines = new ArrayList<FineListItem>();
	
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
	
}

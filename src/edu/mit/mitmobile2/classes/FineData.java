package edu.mit.mitmobile2.classes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.mit.mitmobile2.objs.LoanListItem;

public class FineData {

	private String balance;
	private Date fineDate;
	private ArrayList<LoanListItem> holds = new ArrayList<LoanListItem>();
	
	public ArrayList<LoanListItem> getHolds() {
		return holds;
	}
	public void setHolds(ArrayList<LoanListItem> holds) {
		this.holds = holds;
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

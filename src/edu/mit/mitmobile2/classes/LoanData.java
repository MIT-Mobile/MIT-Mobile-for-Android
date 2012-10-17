package edu.mit.mitmobile2.classes;

import java.util.ArrayList;
import edu.mit.mitmobile2.objs.LoanListItem;

public class LoanData {

	private int numLoan;
	private int numOverdue;
	private ArrayList<LoanListItem> loans = new ArrayList<LoanListItem>();
	private boolean requestCancelled;
	
	public ArrayList<LoanListItem> getLoans() {
		return loans;
	}
	public void setLoans(ArrayList<LoanListItem> loans) {
		this.loans = loans;
	}
	public int getNumLoan() {
		return numLoan;
	}
	public void setNumLoan(int numLoan) {
		this.numLoan = numLoan;
	}
	public int getNumOverdue() {
		return numOverdue;
	}
	public void setNumOverdue(int numOverdue) {
		this.numOverdue = numOverdue;
	}
	public boolean isRequestCancelled() {
		return requestCancelled;
	}
	public void setRequestCancelled(boolean requestCancelled) {
		this.requestCancelled = requestCancelled;
	}
	
	
}

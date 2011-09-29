package edu.mit.mitmobile2.classes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.objs.LoanListItem;

public class HoldData {

	private int numRequest;
	private int numReady;
	private ArrayList<LoanListItem> holds = new ArrayList<LoanListItem>();
	
	public ArrayList<LoanListItem> getHolds() {
		return holds;
	}
	public void setHolds(ArrayList<LoanListItem> holds) {
		this.holds = holds;
	}
	public int getNumRequest() {
		return numRequest;
	}
	public void setNumRequest(int numRequest) {
		this.numRequest = numRequest;
	}
	public int getNumReady() {
		return numReady;
	}
	public void setNumReady(int numReady) {
		this.numReady = numReady;
	}
	
	
}

package edu.mit.mitmobile2.classes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.objs.HoldListItem;
import edu.mit.mitmobile2.objs.LoanListItem;

public class HoldData {

	private int numRequest;
	private int numReady;
	private ArrayList<HoldListItem> holds = new ArrayList<HoldListItem>();
	private boolean requestCancelled;
	
	public ArrayList<HoldListItem> getHolds() {
		return holds;
	}
	public void setHolds(ArrayList<HoldListItem> holds) {
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
	public boolean isRequestCancelled() {
		return requestCancelled;
	}
	public void setRequestCancelled(boolean requestCancelled) {
		this.requestCancelled = requestCancelled;
	}
	
	
}

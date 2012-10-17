package edu.mit.mitmobile2.classes;

import java.util.ArrayList;
import edu.mit.mitmobile2.objs.RenewResponseItem;

public class RenewBookResponse {
	
	private ArrayList<RenewResponseItem> renewResponse = new ArrayList<RenewResponseItem>();

	public ArrayList<RenewResponseItem> getRenewResponse() {
		return renewResponse;
	}

	public void setRenewResponse(ArrayList<RenewResponseItem> renewResponse) {
		this.renewResponse = renewResponse;
	}
	
	
}

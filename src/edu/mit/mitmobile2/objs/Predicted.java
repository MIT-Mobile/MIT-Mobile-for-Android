package edu.mit.mitmobile2.objs;

public class Predicted {
	
	public long next;
	
	public AlertStatus alertStatus = AlertStatus.UNSET;
	
	public String route_id;
	public String stop_id;

	public boolean showAlert = true;
	
	public enum AlertStatus {
		SET,
		UNSET,
		UNKNOWN,
	}
	
	
}

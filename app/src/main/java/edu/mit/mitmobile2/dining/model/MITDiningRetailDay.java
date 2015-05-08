package edu.mit.mitmobile2.dining.model;

import java.util.ArrayList;


public class MITDiningRetailDay {
    protected String dateString;
    protected String endTimeString;
    protected String message;
    protected String startTimeString;
    protected ArrayList<MITDiningRetailVenue> retailHours;

	public String getDateString() {
		return dateString;
	}

	public String getEndTimeString() {
		return endTimeString;
	}

	public String getMessage() {
		return message;
	}

	public String getStartTimeString() {
		return startTimeString;
	}

	public ArrayList<MITDiningRetailVenue> getRetailHours() {
		return retailHours;
	}

	@Override
	public String toString() {
		return "MITDiningRetailDay{" +
			"dateString='" + dateString + '\'' +
			", endTimeString='" + endTimeString + '\'' +
			", message='" + message + '\'' +
			", startTimeString='" + startTimeString + '\'' +
			", retailHours=" + retailHours +
			'}';
	}
}
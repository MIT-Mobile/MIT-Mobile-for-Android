package edu.mit.mitmobile2.dining.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;


public class MITDiningRetailDay implements Serializable {
    @SerializedName("date")
    protected String dateString;
    @SerializedName("end_time")
    protected String endTimeString;
    protected String message;
    @SerializedName("start_time")
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
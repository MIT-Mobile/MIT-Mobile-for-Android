package edu.mit.mitmobile2.dining.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class MITDiningDining implements Serializable {

    @SerializedName("announcements_html")
    protected String announcementsHTML;

    @SerializedName("url")
    protected String url;

    @SerializedName("links")
    protected ArrayList<MITDiningLinks> links;

    @SerializedName("venues")
	protected MITDiningVenues venues;

	public String getAnnouncementsHTML() {
		return announcementsHTML;
	}

	public String getUrl() {
		return url;
	}

	public ArrayList<MITDiningLinks> getLinks() {
		return links;
	}

	public MITDiningVenues getVenues() {
		return venues;
	}

	@Override
	public String toString() {
		return "MITDiningDining{" +
			"announcementsHTML='" + announcementsHTML + '\'' +
			", url='" + url + '\'' +
			", links=" + links +
			", venues=" + venues +
			'}';
	}
}
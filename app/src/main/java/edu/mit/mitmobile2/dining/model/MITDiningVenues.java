package edu.mit.mitmobile2.dining.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;


public class MITDiningVenues implements Serializable {

	@SerializedName("house")
    protected ArrayList<MITDiningHouseVenue> house;

	@SerializedName("retail")
    protected ArrayList<MITDiningRetailVenue> retail;

    @Expose
    protected MITDiningDining dining;

	public MITDiningDining getDining() {
		return dining;
	}

	public ArrayList<MITDiningHouseVenue> getHouse() {
		return house;
	}

	public ArrayList<MITDiningRetailVenue> getRetail() {
		return retail;
	}

	@Override
	public String toString() {
		return "MITDiningVenues{" +
			"dining=" + dining +
			", house=" + house +
			", retail=" + retail +
			'}';
	}
}
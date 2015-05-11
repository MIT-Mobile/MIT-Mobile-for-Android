package edu.mit.mitmobile2.dining.model;

import com.google.gson.annotations.Expose;

import java.util.HashSet;


public class MITDiningVenues {
    protected MITDiningDining dining;
    protected HashSet<MITDiningHouseVenue> house;
    protected HashSet<MITDiningRetailVenue> retail;

	public MITDiningDining getDining() {
		return dining;
	}

	public HashSet<MITDiningHouseVenue> getHouse() {
		return house;
	}

	public HashSet<MITDiningRetailVenue> getRetail() {
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
package edu.mit.mitmobile2.dining.model;

import java.util.HashSet;


public class MITDiningVenues {
	MITDiningDining dining;
	HashSet<MITDiningHouseVenue> house;
	HashSet<MITDiningRetailVenue> retail;

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
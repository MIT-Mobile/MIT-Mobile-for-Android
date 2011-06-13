package edu.mit.mitmobile2.objs;

public class FacilitiesItem {
	
	public FacilitiesItem() {
	}
			
	public static class CategoryRecord {
		public String id;
		public String name;
	}

	public static class ProblemTypeRecord {
		public String problem_type;
	}

	public static class LocationRecord {
		public String id;
		public String name;
		public String lat_wgs84;
		public String long_wgs84;
		public String bldgnum;
		public String last_updated;
	}

	public static class LocationCategoryRecord {
		public String locationId;
		public String categoryId;
	}

	public static class RoomRecord {
		public String building;
		public String floor;
		public String room;
	}
		
}

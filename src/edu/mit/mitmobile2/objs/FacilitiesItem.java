package edu.mit.mitmobile2.objs;

import android.database.Cursor;

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
		public float lat_wgs84;
		public float long_wgs84;
		public String bldgnum;
		public String last_updated;
		
		// properties specific to bldg services
		public boolean hidden_bldg_services;
		public boolean leased_bldg_services;
		public String contact_email_bldg_services;
		public String contact_name_bldg_services;
		public String contact_phone_bldg_services;
	}

	public static class LocationCategoryRecord {
		public String locationId;
		public String categoryId;
	}
	
	public static class LocationContentRecord {
		public String location_id;
		public String name;
		public String url;
	}

	public static class LocationContentCategoryRecord {
		public String location_id;
		public String name;
		public String category;
	}

	public static class LocationContentAltnameRecord {
		public String location_id;
		public String name;
		public String altname;
	}
	
	public static class RoomRecord {
		public String building;
		public String floor;
		public String room;
		
		public RoomRecord() {
			super();
		}

		public RoomRecord(Cursor cursor) {
			this.building = cursor.getString(0);
			this.floor = cursor.getString(1);
			this.room = cursor.getString(2);
		}

	}	
}

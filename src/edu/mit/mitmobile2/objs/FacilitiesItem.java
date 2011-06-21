package edu.mit.mitmobile2.objs;

import android.database.Cursor;
import edu.mit.mitmobile2.facilities.FacilitiesDB.LocationTable;

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
	
	public static class LocationSearchRecord {		
		public String _id;
		public String id;
		public String name;
		public String bldgnum;
		public String display_name;
		
		public LocationSearchRecord() {
			super();
		}

		public LocationSearchRecord(Cursor cursor) {
			this._id = cursor.getString(0);
			this.id = cursor.getString(1);
			this.bldgnum = cursor.getString(2);
			this.name = cursor.getString(4);
			this.display_name = cursor.getString(5);
		}

	}
	
}

package edu.mit.mitmobile.objs;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class RouteItem implements Parcelable {
	
	public RouteItem () {
		stops = new ArrayList<Stops>();
		vehicleLocations = new ArrayList<Vehicle>();
	}
	
	public String route_id;
	public String title;
	public int interval;
	public boolean isSafeRide;
	public boolean isRunning;
	public boolean gpsActive;
	public List<Vehicle> vehicleLocations;
	public String summary;
	public List<Stops> stops;
	
	public static class Vehicle implements Parcelable {
		//public String lat;
		//public String lon;
		public double lat;
		public double lon;
		public int heading;
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeDouble(lat);
			dest.writeDouble(lon);
			dest.writeInt(heading);
		}
		
		private void readFromParcel(Parcel in) {
			lat = in.readDouble();
			lon = in.readDouble();
			heading = in.readInt();
		}
		public static final Parcelable.Creator<Vehicle> CREATOR = new Parcelable.Creator<Vehicle>() {

			@Override
			public Vehicle createFromParcel(Parcel source) {
				Vehicle vehicle = new Vehicle();
				vehicle.readFromParcel(source);
				return vehicle;
			}

			@Override
			public Vehicle[] newArray(int size) {
				return new Vehicle[size];
			}
			 
		};
	

		@Override
		public int describeContents() {
			return 0;
		}
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(route_id);
		dest.writeString(title);
		dest.writeInt(interval);
		writeBool(dest, isSafeRide);
		writeBool(dest, isRunning);
		writeBool(dest, gpsActive);
		dest.writeList(vehicleLocations);
		dest.writeString(summary);
		dest.writeList(stops);
	}
	
	
	@SuppressWarnings("unchecked")
	private void readFromParcel(Parcel in) {
		route_id = in.readString();
		title = in.readString();
		interval = in.readInt();
		isSafeRide = readBool(in);
		isRunning = readBool(in);
		gpsActive = readBool(in);
		vehicleLocations = in.readArrayList(RouteItem.class.getClassLoader());
		summary = in.readString();
		stops = in.readArrayList(Stops.class.getClassLoader());
	}
	
	public static final Parcelable.Creator<RouteItem> CREATOR = new Parcelable.Creator<RouteItem>() {

		@Override
		public RouteItem createFromParcel(Parcel source) {
			RouteItem routeItem = new RouteItem();
			routeItem.readFromParcel(source);
			return routeItem;
		}

		@Override
		public RouteItem[] newArray(int size) {
			return new RouteItem[size];
		}
	};
	
	
	public static class Loc implements Parcelable {
		public float lat;
		public float lon;
		
		public static final Parcelable.Creator<Loc> CREATOR = new Parcelable.Creator<Loc>() {

			@Override
			public Loc createFromParcel(Parcel source) {
				Loc loc = new Loc();
				loc.lat = source.readFloat();
				loc.lon = source.readFloat();
				return loc;
			}

			@Override
			public Loc[] newArray(int size) {
				return new Loc[size];
			}			
		};
	

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeFloat(lat);
			dest.writeFloat(lon);
		}
	}
	
	public static class Stops implements Parcelable {
		
		public Stops () {
			path = new ArrayList<Loc>();
			predictions = new ArrayList<Integer>();
		}
		
		public Stops(Parcel in) {
			readFromParcel(in);
		}
		
		public boolean alertSet = false;
		
		public String route_id;  // needed if not enclosed by RouteItem
		public String id;
		public String title;
		public String lat;
		public String lon;
		public long next;  // next arrival unixtime
		public ArrayList<Integer> predictions;
		public String direction;
		public ArrayList<Loc> path;

		public boolean upcoming = false;
		public boolean gps;
		
		public static final Parcelable.Creator<Stops> CREATOR = new Parcelable.Creator<Stops>() {

			@Override
			public Stops createFromParcel(Parcel source) {
				return new Stops(source);
			}

			@Override
			public Stops[] newArray(int size) {
				return new Stops[size];
			}
			
		};
		
		@Override
		public void writeToParcel(Parcel out, int flags) {
			out.writeString(route_id);
			out.writeString(id);
			out.writeString(title);
			out.writeString(lat);
			out.writeString(lon);
			out.writeLong(next);
			out.writeList(predictions);
			out.writeString(direction);
			out.writeList(path);
			writeBool(out, upcoming);
			writeBool(out, gps);
		}
		
		@SuppressWarnings("unchecked")
		public void readFromParcel(Parcel in) {
			route_id = in.readString();
			id = in.readString();
			title = in.readString();
			lat = in.readString();
			lon = in.readString();
			next = in.readLong();
			
			predictions = (ArrayList<Integer>) in.readArrayList(Integer.class.getClassLoader());
			
			direction = in.readString();
						
			path = in.readArrayList(Loc.class.getClassLoader());
			
			upcoming = readBool(in);
			gps = readBool(in);
		}

		@Override
		public int describeContents() {
			return 0;
		}
	}
	
	/**************************/
	void parseListView() {
		
	}
	void parseDetails() {
		
	}
	
	private static void writeBool(Parcel dest, boolean bool) {
		dest.writeInt(bool ? 1 : 0);
	}
	
	private static boolean readBool(Parcel in) {
		return (in.readInt() == 1);
	}
}

/*
[
    {"route_id":"saferidecamball",
	"title":"Cambridge All",
	"interval":60,
	"isSafeRide":true,
	"isRunning":false,
	"summary":"Runs 6pm-2am Sun-Wed, 6pm-3am Thu-Sat, during summer and holiday breaks."},
	
	{"route_id":"saferidebostonall",
		"title":"Boston All",
		"interval":60,
		"isSafeRide":true,
		"isRunning":false,
		"summary":"Runs 6pm-2am Sun-Wed, 6pm-3am Thu-Sat, during summer and holiday breaks."},
		
	{"route_id":"tech",
		"title":"Tech Shuttle",
		"interval":20,
		"isSafeRide":false,
		"isRunning":true,
		"summary":"Runs weekdays 7:15am-7:15pm, all year round."},
	{"route_id":"northwest","title":"Northwest Shuttle","interval":20,"isSafeRide":false,"isRunning":true,"summary":"Runs weekdays 7:25am-6:42pm, all year round."}
]
 */

//http://mobile-dev.mit.edu/api/shuttles/?command=stopInfo&id=kendsq_d
/*
{"stops":[
          {"id":"kendsq_d","title":"Kendall Square T","lat":"42.36237","lon":"-71.08613","next":1278095114,"predictions":[1200,2400,3600,4800],"route_id":"northwest","gps":true},
          {"id":"kendsq_d","title":"Kendall Square T","lat":"42.36237","lon":"-71.08613","next":1278095876,"predictions":[1257,2514,3771],"route_id":"tech","gps":true}
          ],
          "now":1278094962}
*/

//http://mobile-dev.mit.edu/api/shuttles/?full=true&command=routeInfo&id=tech
// >>> this "stops" has path+direction while above has route_id+gps
/*[{"route_id":"tech",
	"title":"Tech Shuttle",
	"interval":20,
	"isSafeRide":false,
	"isRunning":true,
	"summary":"Runs weekdays 7:15am-7:15pm, all year round.",
	
	"stops":[
	
	  {"id":"kendsq_d",
		"title":"Kendall Square T",
		"lat":"42.36237",
		"lon":"-71.08613",
		"next":1276701398,
		"predictions":[1257,2514,3771,5028],
		"direction":"wcamp",
		"path":[{"lat":"42.36237","lon":"-71.08613"},{"lat":"42.3623199","lon":"-71.0854899"},{"lat":"42.3623","lon":"-71.08523"},{"lat":"42.36227","lon":"-71.08484"},{"lat":"42.36227","lon":"-71.08476"},{"lat":"42.36226","lon":"-71.08467"},{"lat":"42.3622299","lon":"-71.08441"},{"lat":"42.3622","lon":"-71.08433"},{"lat":"42.3621399","lon":"-71.08429"},{"lat":"42.36206","lon":"-71.08429"},{"lat":"42.36194","lon":"-71.0843"},{"lat":"42.36145","lon":"-71.08437"},{"lat":"42.36127","lon":"-71.08439"}]},
		
		{"id":"amhewads",
			"title":"Amherst\/Wadsworth",
			"lat":"42.3612723",
			"lon":"-71.0843897",
			"next":1276701464,"predictions":[1257,2514,3771],
			"direction":"wcamp",
			"path":[{"lat":"42.36127","lon":"-71.08439"},{"lat":"42.36121","lon":"-71.0844"},{"lat":"42.36116","lon":"-71.08442"},
		}
	}
}]*/

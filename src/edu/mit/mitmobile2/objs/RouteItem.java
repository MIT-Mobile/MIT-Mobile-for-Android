package edu.mit.mitmobile2.objs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class RouteItem implements Parcelable {
	
	public String id;
	public String url;
	public String title;
	public String description;
	public String group;
	public boolean active;
	public boolean predictable;
	public int interval;
	
	public List<Stops> stops;
	public List<Vehicle> vehicles;
	public Path path;
	
	
	public RouteItem () {
		stops = new ArrayList<Stops>();
		path = new Path();
		vehicles = new ArrayList<Vehicle>();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(url);
		dest.writeString(title);
		dest.writeString(description);
		dest.writeString(group);
		writeBool(dest, active);
		writeBool(dest, predictable);
		dest.writeInt(interval);
		dest.writeList(stops);
		dest.writeList(vehicles);
		dest.writeValue(path);
	}
	
	@SuppressWarnings("unchecked")
	private void readFromParcel(Parcel in) {
		id = in.readString();
		url = in.readString();
		title = in.readString();
		description = in.readString();
		group = in.readString();
		active = readBool(in);
		predictable = readBool(in);
		interval = in.readInt();
		stops = in.readArrayList(Stops.class.getClassLoader());
		vehicles = in.readArrayList(Vehicle.class.getClassLoader());
		path = (Path) in.readValue(Path.class.getClassLoader());
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
	
	
	private static void writeBool(Parcel dest, boolean bool) {
		dest.writeInt(bool ? 1 : 0);
	}

	private static boolean readBool(Parcel in) {
		return (in.readInt() == 1);
	}
	
	
	public void calculateUpcoming() {
		for (Stops stop : stops) {
			stop.upcoming = false;
		}
		
		if (predictable) {
			setUpcomingByPredictions();
		} else {
			setUpcomingBySchedule();
		}
	}
	
	private void setUpcomingByPredictions() {
		for (Vehicle vehicle : vehicles) {
			String upcomingID = "";
			long vehicleMin = stops.get(0).now * 2;
			boolean hasUpcoming = false;
			
			for (int i = 0; i < stops.size(); i++) {
				Stops stop = stops.get(i);
				long predictionMin = vehicleMin;
				boolean hasMinTime = false;
				
				for (Prediction prediction : stop.predictions) {
					long predictionTime = prediction.timestamp / 1000;
					if (prediction.vehicleID.equals(vehicle.id) && 
							predictionTime < predictionMin &&
							predictionTime > stop.now) {
						predictionMin = predictionTime;
						hasMinTime = true;
					}
				}
				
				if (hasMinTime && predictionMin < vehicleMin) {
					vehicleMin = predictionMin;
					upcomingID = stop.id;
					hasUpcoming = true;
				}
			}
			
			if (hasUpcoming) {
				for (Stops stop : stops) {
					if (stop.id.equals(upcomingID)) {
						stop.upcoming = true;
						break;
					}
				}
			}
		}
	}
	
	private void setUpcomingBySchedule() {
		long minTimestamp = stops.get(0).next;
		long now = new Date().getTime() / 1000;
		int upcomingIndex = 0;
		
		for (int i = 0; i < stops.size(); i++) {
			Stops stop = stops.get(i);
			if (stop.next < minTimestamp && stop.next > now) {
				minTimestamp = stop.next;
				upcomingIndex = i;
			}
		}
		stops.get(upcomingIndex).upcoming = true;
	}
	
	
	public static class Vehicle implements Parcelable {

		public String id;
		public double lat;
		public double lon;
		public int heading;
		public double speed;
		public int lastReport;

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(id);
			dest.writeDouble(lat);
			dest.writeDouble(lon);
			dest.writeInt(heading);
			dest.writeDouble(speed);
			dest.writeInt(lastReport);
		}

		private void readFromParcel(Parcel in) {
			id = in.readString();
			lat = in.readDouble();
			lon = in.readDouble();
			heading = in.readInt();
			speed = in.readDouble();
			lastReport = in.readInt();
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
		
		public boolean alertSet = false;
		
		public String id;
		public String url;
		public String title;
		public double lat;
		public double lon;
		private List<Prediction> predictions;
		private List<Long> schedule;
		
		public long next;
		public boolean upcoming = false;
		public String route_id;  // needed if not enclosed by RouteItem
		public long now; // reference time for predictions
		
		
		public Stops () {
			predictions = new ArrayList<Prediction>();
			schedule = new ArrayList<Long>();
			upcoming = false;
			next = 0;
			now = new Date().getTime() / 1000;
		}
		
		public Stops(Parcel in) {
			readFromParcel(in);
		}
		
		
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
			out.writeString(id);
			out.writeString(url);
			out.writeString(title);
			out.writeDouble(lat);
			out.writeDouble(lon);
			out.writeList(predictions);
			out.writeList(schedule);
			out.writeLong(next);
			writeBool(out, upcoming);
			out.writeString(route_id);
			out.writeLong(now);
			writeBool(out, alertSet);
		}
		
		@SuppressWarnings("unchecked")
		public void readFromParcel(Parcel in) {
			id = in.readString();
			url = in.readString();
			title = in.readString();
			lat = in.readDouble();
			lon = in.readDouble();
			predictions = (ArrayList<Prediction>) in.readArrayList(Prediction.class.getClassLoader());
			schedule = (ArrayList<Long>) in.readArrayList(Long.class.getClassLoader());
			next = in.readLong();
			upcoming = readBool(in);
			route_id = in.readString();
			now = in.readLong();
			alertSet = readBool(in);
		}

		@Override
		public int describeContents() {
			return 0;
		}
		
		/**
		 * 
		 * @param schedule
		 * @param isPredictable If false set a next arrival time value using schedule.
		 */
		public void setSchedule(List<Long> schedule, boolean isPredictable) {
			this.schedule = schedule;
			if (schedule.size() == 0)
				return;
			
			if (!isPredictable) {
				int index = -1;
				long diff = now;
				
				for (int i = 0; i < schedule.size(); i++) {
					long value = schedule.get(i);
					if (value > now && (value - now < diff)) {
						diff = value - now;
						index = i;
					}
				}
				
				next = schedule.get(index != -1 ? index : 0);
			}
		}
		
		public List<Long> getSchedule() {
			return schedule;
		}
		
		/**
		 * 
		 * @param predictions
		 * @param isPredictable If true set a next arrival time value using predictions.
		 */
		public void setPredictions(List<Prediction> predictions, boolean isPredictable) {
			this.predictions = predictions;
			
			if (predictions.size() == 0)
				return;
			
			if (isPredictable) {
				for (Prediction prediction : predictions) {
					if (prediction.timestamp / 1000 > now) {
						next = prediction.timestamp / 1000;
						break;
					}
				}
			}
		}
		
		public List<Prediction> getPredictions() {
			return predictions;
		}
		
	}
	
	
	public static class Prediction implements Parcelable {
		
		public String vehicleID;
		public long timestamp;
		public int seconds;
		
		public Prediction() {
		}
		
		public Prediction(Parcel in) {
			readFromParcel(in);
		}
		
		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(vehicleID);
			dest.writeLong(timestamp);
			dest.writeInt(seconds);
		}
		
		public void readFromParcel(Parcel in) {
			vehicleID = in.readString();
			timestamp = in.readLong();
			seconds = in.readInt();
		}
		
		public static final Parcelable.Creator<Prediction> CREATOR = new Parcelable.Creator<Prediction>() {

			@Override
			public Prediction createFromParcel(Parcel source) {
				return new Prediction(source);
			}

			@Override
			public Prediction[] newArray(int size) {
				return new Prediction[size];
			}
			
		};
		
	}
	
	
	
	public static class Path implements Parcelable {
		
		public ArrayList<ArrayList<Loc>> segments;
		
		public float minLat;
		public float minLon;
		public float maxLat;
		public float maxLon;
		
		
		public Path () {
			segments = new ArrayList<ArrayList<Loc>>();
		}
		
		public Path(Parcel in) {
			readFromParcel(in);
		}
		
		public static final Parcelable.Creator<Path> CREATOR = new Parcelable.Creator<Path>() {

			@Override
			public Path createFromParcel(Parcel source) {
				return new Path(source);
			}

			@Override
			public Path[] newArray(int size) {
				return new Path[size];
			}
			
		};
		
		
		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeList(segments);
			dest.writeFloat(minLat);
			dest.writeFloat(minLon);
			dest.writeFloat(maxLat);
			dest.writeFloat(maxLon);
		}
		
		public void readFromParcel(Parcel in) {
			segments = in.readArrayList(Loc.class.getClassLoader());
			minLat = in.readFloat();
			minLon = in.readFloat();
			maxLat = in.readFloat();
			maxLon = in.readFloat();
		}
		
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

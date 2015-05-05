package edu.mit.mitmobile2.events.model;

import java.util.ArrayList;
import java.util.HashSet;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by grmartin on 4/27/15.
 */
public class MITCalendar implements Parcelable {

    public static final String EVENTS_CALENDAR_ID = "events_calendar";
    public static final String ACADEMIC_CALENDAR_ID = "academic_calendar";
    public static final String ACADEMIC_HOLIDAYS_CALENDAR_ID = "academic_holidays";

    protected String eventsUrl;
    @SerializedName("id")
    protected String identifier;
    protected String name;
    @SerializedName("short_name")
    protected String shortName;
    protected String url;
    protected ArrayList<MITCalendar> categories;
    protected ArrayList<MITCalendar> parentCategory;
    protected HashSet<MITCalendarEvent> events;

    public MITCalendar() {
        this.categories = new ArrayList<>();
        this.parentCategory = new ArrayList<>();
        this.events = new HashSet<>();
    }

    public String getEventsUrl() {
        return eventsUrl;
    }

    public void setEventsUrl(String eventsUrl) {
        this.eventsUrl = eventsUrl;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<MITCalendar> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<MITCalendar> categories) {
        this.categories = categories;
    }

    public ArrayList<MITCalendar> getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(ArrayList<MITCalendar> parentCategory) {
        this.parentCategory = parentCategory;
    }

    public HashSet<MITCalendarEvent> getEvents() {
        return events;
    }

    public void setEvents(HashSet<MITCalendarEvent> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "MITCalendar{" +
            "eventsUrl='" + eventsUrl + '\'' +
            ", identifier='" + identifier + '\'' +
            ", name='" + name + '\'' +
            ", shortName='" + shortName + '\'' +
            ", url='" + url + '\'' +
            ", categories=" + categories +
            ", parentCategory=" + parentCategory +
            ", events=" + events +
            '}';
    }

    protected MITCalendar(Parcel in) {
        eventsUrl = in.readString();
        identifier = in.readString();
        name = in.readString();
        shortName = in.readString();
        url = in.readString();
        if (in.readByte() == 0x01) {
            categories = new ArrayList<MITCalendar>();
            in.readList(categories, MITCalendar.class.getClassLoader());
        } else {
            categories = null;
        }
        if (in.readByte() == 0x01) {
            parentCategory = new ArrayList<MITCalendar>();
            in.readList(parentCategory, MITCalendar.class.getClassLoader());
        } else {
            parentCategory = null;
        }
        events = (HashSet) in.readValue(HashSet.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventsUrl);
        dest.writeString(identifier);
        dest.writeString(name);
        dest.writeString(shortName);
        dest.writeString(url);
        if (categories == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(categories);
        }
        if (parentCategory == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(parentCategory);
        }
        dest.writeValue(events);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITCalendar> CREATOR = new Parcelable.Creator<MITCalendar>() {
        @Override
        public MITCalendar createFromParcel(Parcel in) {
            return new MITCalendar(in);
        }

        @Override
        public MITCalendar[] newArray(int size) {
            return new MITCalendar[size];
        }
    };
}

package edu.mit.mitmobile2.events.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by grmartin on 4/27/15.
 */
public class MITCalendarSeriesInfo implements Parcelable {

    protected String description;
    protected String title;

    // I have a feeling this might create a cyclic reference situation, commenting out for now.
    // MITCalendarEvent event;

    public MITCalendarSeriesInfo() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    public MITCalendarEvent getEvent() {
//        return event;
//    }
//
//    public void setEvent(MITCalendarEvent event) {
//        this.event = event;
//    }

    @Override
    public String toString() {
        return "MITCalendarSeriesInfo{" +
            "description='" + description + '\'' +
            ", title='" + title + '\'' +
            '}';
    }

    protected MITCalendarSeriesInfo(Parcel in) {
        description = in.readString();
        title = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(title);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITCalendarSeriesInfo> CREATOR = new Parcelable.Creator<MITCalendarSeriesInfo>() {
        @Override
        public MITCalendarSeriesInfo createFromParcel(Parcel in) {
            return new MITCalendarSeriesInfo(in);
        }

        @Override
        public MITCalendarSeriesInfo[] newArray(int size) {
            return new MITCalendarSeriesInfo[size];
        }
    };
}

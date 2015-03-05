package edu.mit.mitmobile2.shuttles.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;


public class MITShuttlePath implements Parcelable {

    @Expose
    private List<Double> bbox = new ArrayList<>();
    @Expose
    private List<List<List<Double>>> segments = new ArrayList<List<List<Double>>>();


    public List<Double> getBbox() {
        return bbox;
    }


    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }


    public List<List<List<Double>>> getSegments() {
        return segments;
    }


    public void setSegments(List<List<List<Double>>> segments) {
        this.segments = segments;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    //TODO: Need to parcel/unparcel the bbox and segments
    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeList(this.bbox);
//        dest.writeTypedList(this.segments);
    }

    private MITShuttlePath(Parcel p) {

    }

    public static final Parcelable.Creator<MITShuttlePath> CREATOR = new Parcelable.Creator<MITShuttlePath>() {
        public MITShuttlePath createFromParcel(Parcel source) {
            return new MITShuttlePath(source);
        }

        public MITShuttlePath[] newArray(int size) {
            return new MITShuttlePath[size];
        }
    };
}
package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by serg on 5/20/15.
 */
public class MITLibrariesDate implements Parcelable {

    @SerializedName("start")
    private String start;

    @SerializedName("end")
    private String end;

    @Expose
    private Date startDate;

    @Expose
    private Date endDate;

    public MITLibrariesDate() {
        // empty constructor
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Date getStartDate() {
        if (startDate == null) {
            try {
                startDate = new SimpleDateFormat("yyyy-MM-dd").parse(start);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return startDate;
    }

    public Date getEndDate() {
        if (endDate == null) {
            try {
                endDate = new SimpleDateFormat("yyyy-MM-dd").parse(end);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return endDate;
    }

    protected MITLibrariesDate(Parcel in) {
        start = in.readString();
        end = in.readString();
        long tmpStartDate = in.readLong();
        startDate = tmpStartDate != -1 ? new Date(tmpStartDate) : null;
        long tmpEndDate = in.readLong();
        endDate = tmpEndDate != -1 ? new Date(tmpEndDate) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(start);
        dest.writeString(end);
        dest.writeLong(startDate != null ? startDate.getTime() : -1L);
        dest.writeLong(endDate != null ? endDate.getTime() : -1L);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLibrariesDate> CREATOR = new Parcelable.Creator<MITLibrariesDate>() {
        @Override
        public MITLibrariesDate createFromParcel(Parcel in) {
            return new MITLibrariesDate(in);
        }

        @Override
        public MITLibrariesDate[] newArray(int size) {
            return new MITLibrariesDate[size];
        }
    };
}

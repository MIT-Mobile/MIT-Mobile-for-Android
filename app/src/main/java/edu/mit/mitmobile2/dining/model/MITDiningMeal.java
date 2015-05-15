package edu.mit.mitmobile2.dining.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.mit.mitmobile2.DateUtils;
import edu.mit.mitmobile2.R;

public class MITDiningMeal implements Parcelable {

    @SerializedName("name")
    protected String name;

    @SerializedName("start_time")
    protected String startTimeString;

    @SerializedName("end_time")
    protected String endTimeString;

    @SerializedName("items")
    protected List<MITDiningMenuItem> items;

    protected String houseDateString;

    @Expose
    protected String message;

    public String getEndTimeString() {
        return endTimeString;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public String getStartTimeString() {
        return startTimeString;
    }

    public String getHouseDateString() {
        return houseDateString;
    }

    public List<MITDiningMenuItem> getItems() {
        return items;
    }

    public void setHouseDateString(String houseDateString) {
        this.houseDateString = houseDateString;
    }

    @Override
    public String toString() {
        return "MITDiningMeal{" +
                "endTimeString='" + endTimeString + '\'' +
                ", message='" + message + '\'' +
                ", name='" + name + '\'' +
                ", startTimeString='" + startTimeString + '\'' +
                ", houseDateString=" + houseDateString +
                ", items=" + items +
                '}';
    }

    public Date getStartTime() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:SS").parse(houseDateString + " " + startTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date getEndTime() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:SS").parse(houseDateString + " " + endTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String mealHoursDescription(Context context) {
        String description;

        if (TextUtils.isEmpty(startTimeString) || TextUtils.isEmpty(endTimeString)) {
            if (!TextUtils.isEmpty(message)) {
                description = message;
            } else {
                description = context.getString(R.string.dining_venue_status_closed);
            }
        } else {
            String startString = DateUtils.MITShortTimeOfDayString(getStartTime());
            String endString = DateUtils.MITShortTimeOfDayString(getEndTime());

            description = context.getString(R.string.dining_venue_start_end_template, startString, endString);
        }

        return description;
    }

    protected MITDiningMeal(Parcel in) {
        endTimeString = in.readString();
        message = in.readString();
        name = in.readString();
        startTimeString = in.readString();
        houseDateString = in.readString();
        if (in.readByte() == 0x01) {
            items = new ArrayList<>();
            in.readList(items, MITDiningMenuItem.class.getClassLoader());
        } else {
            items = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(endTimeString);
        dest.writeString(message);
        dest.writeString(name);
        dest.writeString(startTimeString);
        dest.writeString(houseDateString);
        if (items == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(items);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITDiningMeal> CREATOR = new Parcelable.Creator<MITDiningMeal>() {
        @Override
        public MITDiningMeal createFromParcel(Parcel in) {
            return new MITDiningMeal(in);
        }

        @Override
        public MITDiningMeal[] newArray(int size) {
            return new MITDiningMeal[size];
        }
    };
}
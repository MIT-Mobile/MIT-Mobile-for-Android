package edu.mit.mitmobile2.dining.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.google.gson.annotations.SerializedName;

import edu.mit.mitmobile2.shared.logging.LoggingManager;

public class MITDiningRetailDay implements Parcelable {
    @SerializedName("date")
    protected String dateString;
    @SerializedName("end_time")
    protected String endTimeString;
    protected String message;
    @SerializedName("start_time")
    protected String startTimeString;
    protected ArrayList<MITDiningRetailVenue> retailHours;
    protected Date retailDate;

    public String getDateString() {
        return dateString;
    }

    public String getEndTimeString() {
        return endTimeString;
    }

    public String getMessage() {
        return message;
    }

    public String getStartTimeString() {
        return startTimeString;
    }

    public ArrayList<MITDiningRetailVenue> getRetailHours() {
        return retailHours;
    }

    public Date getDate() {
        if (retailDate == null) {
            retailDate = buildDateFromString(dateString);
        }
        return retailDate;
    }

    @Override
    public String toString() {
        return "MITDiningRetailDay{" +
                "dateString='" + dateString + '\'' +
                ", endTimeString='" + endTimeString + '\'' +
                ", message='" + message + '\'' +
                ", startTimeString='" + startTimeString + '\'' +
                ", retailHours=" + retailHours +
                '}';
    }

    protected MITDiningRetailDay(Parcel in) {
        dateString = in.readString();
        endTimeString = in.readString();
        message = in.readString();
        startTimeString = in.readString();
        if (in.readByte() == 0x01) {
            retailHours = new ArrayList<MITDiningRetailVenue>();
            in.readList(retailHours, MITDiningRetailVenue.class.getClassLoader());
        } else {
            retailHours = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dateString);
        dest.writeString(endTimeString);
        dest.writeString(message);
        dest.writeString(startTimeString);
        if (retailHours == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(retailHours);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITDiningRetailDay> CREATOR = new Parcelable.Creator<MITDiningRetailDay>() {
        @Override
        public MITDiningRetailDay createFromParcel(Parcel in) {
            return new MITDiningRetailDay(in);
        }

        @Override
        public MITDiningRetailDay[] newArray(int size) {
            return new MITDiningRetailDay[size];
        }
    };

    private Date buildDateFromString(String stringToParse) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            return format.parse(stringToParse);
        } catch (ParseException e) {
            LoggingManager.Timber.e(e, "Failed");
            return new Date();
        }
    }
}
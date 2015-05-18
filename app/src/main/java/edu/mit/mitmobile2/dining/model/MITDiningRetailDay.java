package edu.mit.mitmobile2.dining.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import edu.mit.mitmobile2.DateUtils;
import edu.mit.mitmobile2.R;
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

    public Date getStartTime() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:SS").parse(dateString + " " + startTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date getEndTime() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:SS").parse(dateString + " " + endTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String hoursSummary(Context context) {
        String hoursSummary = null;

        if (!TextUtils.isEmpty(message)) {
            hoursSummary = message;
        } else if (!TextUtils.isEmpty(startTimeString) && !TextUtils.isEmpty(endTimeString)) {
            String startString = DateUtils.MITShortTimeOfDayString(getStartTime());
            String endString = DateUtils.MITShortTimeOfDayString(getEndTime());

            hoursSummary = context.getString(R.string.dining_venue_start_end_template, startString, endString).toLowerCase();
        } else {
            hoursSummary = context.getString(R.string.dining_venue_closed_for_the_day);
        }

        return hoursSummary;
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
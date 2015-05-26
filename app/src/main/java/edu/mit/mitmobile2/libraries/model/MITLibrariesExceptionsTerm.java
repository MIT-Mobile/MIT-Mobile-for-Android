package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.mit.mitmobile2.DateUtils;

/**
 * Created by serg on 5/20/15.
 */
public class MITLibrariesExceptionsTerm implements Parcelable {

    @SerializedName("dates")
    private MITLibrariesDate dates;

    @SerializedName("hours")
    private MITLibrariesDate hours;

    @SerializedName("reason")
    private String reason;

    @Expose
    private Date startDate;

    @Expose
    private Date endDate;

    public MITLibrariesExceptionsTerm() {
        // empty constructor
    }

    public MITLibrariesDate getDates() {
        return dates;
    }

    public void setDates(MITLibrariesDate dates) {
        this.dates = dates;
    }

    public MITLibrariesDate getHours() {
        return hours;
    }

    public void setHours(MITLibrariesDate hours) {
        this.hours = hours;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    /* Helpers */

    public Date getStartDate() {
        if (startDate == null) {
            String startDateString = String.format("%s %s", dates.getStart(), hours.getStart());
            try {
                startDate =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return startDate;
    }

    public Date getEndDate() {
        if (endDate == null) {
            String startDateString = String.format("%s %s", dates.getEnd(), hours.getEnd());
            try {
                endDate =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return endDate;
    }

    public boolean isOpenOnDate(Date date) {
        Date endDate = getEndDate();
        if (endDate.equals(DateUtils.startOfDay(date))) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.DATE, 1);
            endDate = calendar.getTime();
        }

        return DateUtils.dateFallsBetweenDates(date, getStartDate(), endDate);
    }

    public boolean isOpenOnDayOfDate(Date date) {
        return DateUtils.dateFallsBetweenDates(date, dates.getStartDate(), dates.getEndDate(), Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);
    }

    /* Parcelable */

    protected MITLibrariesExceptionsTerm(Parcel in) {
        dates = (MITLibrariesDate) in.readValue(MITLibrariesDate.class.getClassLoader());
        hours = (MITLibrariesDate) in.readValue(MITLibrariesDate.class.getClassLoader());
        reason = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(dates);
        dest.writeValue(hours);
        dest.writeString(reason);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLibrariesExceptionsTerm> CREATOR = new Parcelable.Creator<MITLibrariesExceptionsTerm>() {
        @Override
        public MITLibrariesExceptionsTerm createFromParcel(Parcel in) {
            return new MITLibrariesExceptionsTerm(in);
        }

        @Override
        public MITLibrariesExceptionsTerm[] newArray(int size) {
            return new MITLibrariesExceptionsTerm[size];
        }
    };
}

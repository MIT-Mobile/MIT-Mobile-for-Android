package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.mit.mitmobile2.DateUtils;

/**
 * Created by serg on 5/20/15.
 */
public class MITLibrariesRegularTerm implements Parcelable {

    @SerializedName("days")
    private String days;

    @SerializedName("hours")
    private MITLibrariesDate hours;

    public MITLibrariesRegularTerm() {
        // empty constructor
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public MITLibrariesDate getHours() {
        return hours;
    }

    public void setHours(MITLibrariesDate hours) {
        this.hours = hours;
    }

    /* Helpers */

    public boolean isOpenOnDayOfDate(Date date) {
        String dayOfWeekAbbreviation = DateUtils.MITDateCode(date);
        return days != null && days.contains(dayOfWeekAbbreviation);
    }

    public boolean isOpenOnDate(Date date) {
        if (!isOpenOnDayOfDate(date)) {
            return false;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String startDateString = String.format("%s %s", format.format(date), hours.getStart());
        String endDateString = String.format("%s %s", format.format(date), hours.getEnd());

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date startDate = dateTimeFormat.parse(startDateString);

            // We can get back 00:00:00 as a time a library shuts, which should actually be midnight of the following day, so we need to adjust for this...
            Date endDate = dateTimeFormat.parse(endDateString);

            if (endDate.equals(DateUtils.startOfDay(date))) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endDate);
                calendar.add(Calendar.DATE, 1);
                endDate = calendar.getTime();
            }

            return DateUtils.dateFallsBetweenDates(date, startDate, endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    /* Parcelable */

    protected MITLibrariesRegularTerm(Parcel in) {
        days = in.readString();
        hours = (MITLibrariesDate) in.readValue(MITLibrariesDate.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(days);
        dest.writeValue(hours);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLibrariesRegularTerm> CREATOR = new Parcelable.Creator<MITLibrariesRegularTerm>() {
        @Override
        public MITLibrariesRegularTerm createFromParcel(Parcel in) {
            return new MITLibrariesRegularTerm(in);
        }

        @Override
        public MITLibrariesRegularTerm[] newArray(int size) {
            return new MITLibrariesRegularTerm[size];
        }
    };
}

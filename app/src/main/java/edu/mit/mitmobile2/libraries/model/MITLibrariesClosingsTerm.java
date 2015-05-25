package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;

import edu.mit.mitmobile2.DateUtils;

/**
 * Created by serg on 5/20/15.
 */
public class MITLibrariesClosingsTerm implements Parcelable {

    @SerializedName("dates")
    private MITLibrariesDate dates;

    @SerializedName("reason")
    private String reason;

    public MITLibrariesClosingsTerm() {
        // empty constructor
    }

    public MITLibrariesDate getDates() {
        return dates;
    }

    public void setDates(MITLibrariesDate dates) {
        this.dates = dates;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    /* Helpers */

    /*
    - (BOOL)isClosedOnDate:(NSDate *)date
    {
        return ([date dateFallsBetweenStartDate:self.dates.startDate endDate:self.dates.endDate components:(NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit)]);
    }
    */

    public boolean isClosedOnDate(Date date) {
        return DateUtils.dateFallsBetweenDates(date, dates.getStartDate(), dates.getEndDate(), Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);
    }

    /* Parcelable */

    protected MITLibrariesClosingsTerm(Parcel in) {
        dates = (MITLibrariesDate) in.readValue(MITLibrariesDate.class.getClassLoader());
        reason = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(dates);
        dest.writeString(reason);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLibrariesClosingsTerm> CREATOR = new Parcelable.Creator<MITLibrariesClosingsTerm>() {
        @Override
        public MITLibrariesClosingsTerm createFromParcel(Parcel in) {
            return new MITLibrariesClosingsTerm(in);
        }

        @Override
        public MITLibrariesClosingsTerm[] newArray(int size) {
            return new MITLibrariesClosingsTerm[size];
        }
    };
}

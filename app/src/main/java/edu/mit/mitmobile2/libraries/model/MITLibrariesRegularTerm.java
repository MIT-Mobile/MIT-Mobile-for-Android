package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

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

    /*
    - (BOOL)isOpenOnDayOfDate:(NSDate *)date
{
    NSString *dayOfWeekAbbreviation = [date MITDateCode];

    return ([self.days rangeOfString:dayOfWeekAbbreviation].location != NSNotFound);
}
    */

    public boolean isOpenOnDayOfDate(Date date) {
        String dayOfWeekAbbreviation = DateUtils.MITDateCode(date);
        return days != null && days.contains(dayOfWeekAbbreviation);
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

package edu.mit.mitmobile2.libraries.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.mit.mitmobile2.DateUtils;
import edu.mit.mitmobile2.R;

/**
 * Created by serg on 5/20/15.
 */
public class MITLibrariesTerm implements Parcelable {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @SerializedName("name")
    private String name;

    @SerializedName("dates")
    private MITLibrariesDate dates;

    @SerializedName("regular")
    private List<MITLibrariesRegularTerm> regularTerm;

    @SerializedName("closing")
    private List<MITLibrariesClosingsTerm> closingsTerm;

    @SerializedName("exceptions")
    private List<MITLibrariesExceptionsTerm> exceptionsTerm;

    public MITLibrariesTerm() {
        // empty constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MITLibrariesDate getDates() {
        return dates;
    }

    public void setDates(MITLibrariesDate dates) {
        this.dates = dates;
    }

    public List<MITLibrariesRegularTerm> getRegularTerm() {
        return regularTerm;
    }

    public void setRegularTerm(List<MITLibrariesRegularTerm> regularTerm) {
        this.regularTerm = regularTerm;
    }

    public List<MITLibrariesClosingsTerm> getClosingsTerm() {
        return closingsTerm;
    }

    public void setClosingsTerm(List<MITLibrariesClosingsTerm> closingsTerm) {
        this.closingsTerm = closingsTerm;
    }

    public List<MITLibrariesExceptionsTerm> getExceptionsTerm() {
        return exceptionsTerm;
    }

    public void setExceptionsTerm(List<MITLibrariesExceptionsTerm> exceptionsTerm) {
        this.exceptionsTerm = exceptionsTerm;
    }

    /* Helpers */

    private SimpleDateFormat dateFormat;

    private SimpleDateFormat getDateFormat() {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat(DATE_FORMAT);
        }
        return dateFormat;
    }

    public boolean isDateFallsInTerm(Date date) {
        Date startDate = DateUtils.startOfDay(dates.getStartDate());
        Date endDate = DateUtils.startOfDay(dates.getEndDate());

        return DateUtils.dateFallsBetweenDates(date, startDate, endDate);
    }

    /*
    for (MITLibrariesClosingsTerm *term in self.closingsTerm) {
        if ([term isClosedOnDate:date]) {
            return kMITLibraryClosedMessageString;
        }
    }

    for (MITLibrariesExceptionsTerm *term in self.exceptionsTerm) {
        if ([term isOpenOnDayOfDate:date]) {
            return [term.hours hoursRangesString];
        }
    }

    for (MITLibrariesRegularTerm *term in self.regularTerm) {
        if ([term isOpenOnDayOfDate:date]) {
            return [term.hours hoursRangesString];
        }
    }

    return kMITLibraryClosedMessageString;
}
    */

    public String hoursStringForDate(Context context, Date date) {
        if (!isDateFallsInTerm(date)) {
            return context.getString(R.string.library_closed_today);
        }

        for (MITLibrariesClosingsTerm term : closingsTerm) {
            if (term.isClosedOnDate(date)) {
                return context.getString(R.string.library_closed_today);
            }
        }

        for (MITLibrariesExceptionsTerm term : exceptionsTerm) {
            if (term.isOpenOnDayOfDate(date)) {
                return term.getHours().hoursRangesString(context);
            }
        }

        for (MITLibrariesRegularTerm term : regularTerm) {
            if (term.isOpenOnDayOfDate(date)) {
                return term.getHours().hoursRangesString(context);
            }
        }

        return context.getString(R.string.library_closed_today);
    }

    /*
    - (BOOL)isOpenAtDate:(NSDate *)date
{
    [self.dateFormatter setDateFormat:@"yyyy-MM-dd"];

    NSDate *startDate = [[self.dateFormatter dateFromString:self.dates.start] startOfDay];
    NSDate *endDate = [[self.dateFormatter dateFromString:self.dates.end] endOfDay];

    // Check to see if the date even falls within the term
    if (![date dateFallsBetweenStartDate:startDate endDate:endDate])
    {
        return NO;
    }

    // Check to see if the date falls within an exception
    for (MITLibrariesExceptionsTerm *term in self.exceptionsTerm) {
        if ([term isOpenOnDate:date]) {
            return YES;
        }
    }

    // Check to see if the library is explicitly closed
    for (MITLibrariesClosingsTerm *term in self.closingsTerm) {
        if ([term isClosedOnDate:date]) {
            return NO;
        }
    }

    // Check to see if the library is open for the day of the week
    for (MITLibrariesRegularTerm *term in self.regularTerm) {
        if ([term isOpenOnDate:date]) {
            return YES;
        }
    }

    return NO;
}
    */

    public boolean isOpenAtDate(Date date) {
        // TODO:
        return false;
    }

    /*
- (BOOL)isOpenOnDayOfDate:(NSDate *)date
{
    [self.dateFormatter setDateFormat:@"yyyy-MM-dd"];

    NSDate *startDate = [[self.dateFormatter dateFromString:self.dates.start] startOfDay];
    NSDate *endDate = [[self.dateFormatter dateFromString:self.dates.end] endOfDay];

    // Check to see if the date even falls within the term
    if (![date dateFallsBetweenStartDate:startDate endDate:endDate])
    {
        return NO;
    }

    // Check to see if the date falls within an exception
    for (MITLibrariesExceptionsTerm *term in self.exceptionsTerm) {
        if ([term isOpenOnDayOfDate:date]) {
            return YES;
        }
    }

    // Check to see if the library is explicitly closed
    for (MITLibrariesClosingsTerm *term in self.closingsTerm) {
        if ([term isClosedOnDate:date]) {
            return NO;
        }
    }

    // Check to see if the library is open for the day of the week
    for (MITLibrariesRegularTerm *term in self.regularTerm) {
        if ([term isOpenOnDayOfDate:date]) {
            return YES;
        }
    }

    return NO;
}
    */

    public boolean isOpenOnDayOfDate(Date date) {
        // TODO:
        return true;
    }

    /* Parcelable */

    protected MITLibrariesTerm(Parcel in) {
        name = in.readString();
        dates = (MITLibrariesDate) in.readValue(MITLibrariesDate.class.getClassLoader());
        if (in.readByte() == 0x01) {
            regularTerm = new ArrayList<MITLibrariesRegularTerm>();
            in.readList(regularTerm, MITLibrariesRegularTerm.class.getClassLoader());
        } else {
            regularTerm = null;
        }
        if (in.readByte() == 0x01) {
            closingsTerm = new ArrayList<MITLibrariesClosingsTerm>();
            in.readList(closingsTerm, MITLibrariesClosingsTerm.class.getClassLoader());
        } else {
            closingsTerm = null;
        }
        if (in.readByte() == 0x01) {
            exceptionsTerm = new ArrayList<MITLibrariesExceptionsTerm>();
            in.readList(exceptionsTerm, MITLibrariesExceptionsTerm.class.getClassLoader());
        } else {
            exceptionsTerm = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeValue(dates);
        if (regularTerm == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(regularTerm);
        }
        if (closingsTerm == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(closingsTerm);
        }
        if (exceptionsTerm == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(exceptionsTerm);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLibrariesTerm> CREATOR = new Parcelable.Creator<MITLibrariesTerm>() {
        @Override
        public MITLibrariesTerm createFromParcel(Parcel in) {
            return new MITLibrariesTerm(in);
        }

        @Override
        public MITLibrariesTerm[] newArray(int size) {
            return new MITLibrariesTerm[size];
        }
    };
}

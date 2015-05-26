package edu.mit.mitmobile2.libraries.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.mit.mitmobile2.R;

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

    /* Helpers */

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

    /**
     * This assumes that the dates stored are hours only, no day information
     *
     * @return hours ranges String representation
     */
    public String hoursRangesString(Context context) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        // We don't really care what the date is, only the hours of that date (things sometimes go screwy if you trying to dateFromString with just hours)
        String dateDayString = format.format(new Date());

        String startDateString = String.format("%s %s", dateDayString, start);
        String endDateString = String.format("%s %s", dateDayString, end);

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date startDate = dateTimeFormat.parse(startDateString);
            Date endDate = dateTimeFormat.parse(endDateString);

            return String.format("%s-%s", smartStringForDate(context, startDate), smartStringForDate(context, endDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String smartStringForDate(Context context, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (hour == 0) {
            return context.getString(R.string.library_midnight);
        } else if (hour == 12) {
            return context.getString(R.string.library_noon);
        } else {
            String dateFormat = "";
            if (minute == 0) {
                dateFormat = "ha";
            } else {
                dateFormat = "h:mma";
            }

            return new SimpleDateFormat(dateFormat).format(date);
        }
    }

    public String dayRangesString() {
        if (start.equals(end)) {
            return new SimpleDateFormat("MMM d").format(getStartDate());
        } else {
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM ");

            String startingMonth = monthFormat.format(getStartDate());
            String endingMonth = monthFormat.format(getEndDate());

            SimpleDateFormat dayFormat = new SimpleDateFormat("d");

            String startingDay = dayFormat.format(getStartDate());
            String endingDay = dayFormat.format(getEndDate());

            if (startingMonth.equals(endingMonth)) {
                endingMonth = "";
            }

            return String.format("%s%s-%s%s", startingMonth, startingDay, endingMonth, endingDay);
        }
    }

    /* Parcelable */

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

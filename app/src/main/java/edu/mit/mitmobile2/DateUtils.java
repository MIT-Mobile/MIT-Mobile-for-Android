package edu.mit.mitmobile2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;

public class DateUtils {

    public static Date startOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static boolean areEqualToDateIgnoringTime(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Extra compact string representation of the date's time components.
     * <p/>
     * This returns only the time of day for the date. The format is similar to "h:mma", but with the minute component only included when non-zero, e.g. "9pm", "10:30am", "4:01pm".
     *
     * @param date Date to be represented as string
     * @return a compact string representation of the date's time components
     */
    public static String MITShortTimeOfDayString(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        SimpleDateFormat format;
        if (cal.get(Calendar.MINUTE) != 0) {
            format = new SimpleDateFormat("h:mma");
        } else {
            format = new SimpleDateFormat("ha");
        }

        return format.format(date);
    }

    public static Date buildLongDateFromString(String stringToParse) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        try {
            return format.parse(stringToParse);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date buildShortDateFromString(String stringToParse) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            return format.parse(stringToParse);
        } catch (ParseException e) {
            return null;
        }
    }

    public static boolean dateFallsBetweenDates(Date date, Date startDate, Date endDate) {
        return startDate.compareTo(date) * date.compareTo(endDate) > 0;
    }

    public static boolean dateFallsBetweenDates(Date date, Date startDate, Date endDate, Integer... calendarComponents) {
        return dateFallsBetweenDates(getDate(date, calendarComponents), getDate(startDate, calendarComponents), getDate(endDate, calendarComponents));
    }

    /*
    - (NSString *)MITDateCode
{
    NSCalendarUnit calendarUnits = (NSDayCalendarUnit | NSWeekdayCalendarUnit);
    NSDateComponents *dateComponents = [[NSCalendar cachedCurrentCalendar] components:calendarUnits fromDate:self];

    switch (dateComponents.weekday) {
        case 1: {
            return @"U";
        }

        case 2: {
            return @"M";
        }

        case 3: {
            return @"T";
        }

        case 4: {
            return @"W";
        }

        case 5: {
            return @"R";
        }

        case 6: {
            return @"F";
        }

        case 7: {
            return @"S";
        }

        default: {
            return nil;
        }
    }
}
    */

    private static final String[] dateCodes = {"U", "M", "T", "W", "R", "F", "S"};

    public static String MITDateCode(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return dateCodes[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    private static Date getDate(Date date, Integer... calendarComponents) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        HashMap<Integer, Integer> componentValues = new HashMap<>();
        for (Integer component : calendarComponents) {
            componentValues.put(component, calendar.get(component));
        }

        Date newDate = new Date(0);
        calendar.setTime(newDate);
        for (Integer key : componentValues.keySet()) {
            calendar.set(key, componentValues.get(key));
        }

        return calendar.getTime();
    }
}

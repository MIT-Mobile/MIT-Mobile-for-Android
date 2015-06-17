package edu.mit.mitmobile2.dining.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.DateUtils;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MapItem;
import edu.mit.mitmobile2.shared.logging.LoggingManager;

public class MITDiningRetailVenue extends MapItem implements Parcelable {
    protected List<String> cuisine;
    @SerializedName("description_html")
    protected String descriptionHTML;
    protected boolean favorite;
    @SerializedName("homepage_url")
    protected String homepageURL;
    @SerializedName("icon_url")
    protected String iconURL;
    @SerializedName("id")
    protected String identifier;
    @SerializedName("menu_html")
    protected String menuHTML;
    @SerializedName("menu_url")
    protected String menuURL;
    protected String name;
    protected List<String> payment;
    @SerializedName("short_name")
    protected String shortName;
    protected List<MITDiningRetailDay> hours;
    protected MITDiningLocation location;

    public List<String> getCuisine() {
        return cuisine;
    }

    public String getDescriptionHTML() {
        return descriptionHTML;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getHomepageURL() {
        return homepageURL;
    }

    public String getIconURL() {
        return iconURL;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getMenuHTML() {
        return menuHTML;
    }

    public String getMenuURL() {
        return menuURL;
    }

    public String getName() {
        return name;
    }

    public List<String> getPayment() {
        return payment;
    }

    public String getShortName() {
        return shortName;
    }

    public List<MITDiningRetailDay> getHours() {
        return hours;
    }

    public MITDiningLocation getLocation() {
        return location;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "MITDiningRetailVenue{" +
                "cuisine=" + cuisine +
                ", descriptionHTML='" + descriptionHTML + '\'' +
                ", favorite=" + favorite +
                ", homepageURL='" + homepageURL + '\'' +
                ", iconURL='" + iconURL + '\'' +
                ", identifier='" + identifier + '\'' +
                ", menuHTML='" + menuHTML + '\'' +
                ", menuURL='" + menuURL + '\'' +
                ", name='" + name + '\'' +
                ", payment=" + payment +
                ", shortName='" + shortName + '\'' +
                ", hours=" + hours +
                ", location=" + location +
                '}';
    }

    public boolean isOpenNow() {
        long currentTime = new Date().getTime();
        boolean isOpenNow = false;

        for (MITDiningRetailDay retailDay : hours) {
            if (!TextUtils.isEmpty(retailDay.getStartTimeString())) {
                long retailDayStartTime = retailDay.getStartTime().getTime();
                long retailDayEndTime = retailDay.getEndTime().getTime();

                if (retailDayStartTime <= currentTime && currentTime <= retailDayEndTime) {
                    isOpenNow = true;
                    break;
                }
            }
        }

        return isOpenNow;
    }

    public String hoursToday(Context context) {
        String hoursSummary = null;

        Date nowDate = new Date();
        long nowInterval = nowDate.getTime();

        MITDiningRetailDay yesterdayRetailDay = retailDayForDate(nowDate);
        long yesterdayEndTime = 0;
        try {
            yesterdayEndTime = yesterdayRetailDay.getEndTime().getTime();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (nowInterval < yesterdayEndTime) {
            hoursSummary = yesterdayRetailDay.hoursSummary(context);
        } else {
            MITDiningRetailDay todayRetailDay = retailDayForDate(nowDate);
            hoursSummary = todayRetailDay.hoursSummary(context);
        }

        return hoursSummary;
    }

    private MITDiningRetailDay retailDayForDate(Date date) {
        MITDiningRetailDay returnDay = null;
        if (date != null) {
            Date startOfDate = DateUtils.startOfDay(date);
            for (MITDiningRetailDay retailDay : hours) {
                if (DateUtils.areEqualToDateIgnoringTime(retailDay.getDate(), startOfDate)) {
                    returnDay = retailDay;
                    break;
                }
            }
        }

        return returnDay;
    }

    protected MITDiningRetailVenue(Parcel in) {
        if (in.readByte() == 0x01) {
            cuisine = new ArrayList<>();
            in.readList(cuisine, String.class.getClassLoader());
        } else {
            cuisine = null;
        }
        descriptionHTML = in.readString();
        favorite = in.readByte() != 0x00;
        homepageURL = in.readString();
        iconURL = in.readString();
        identifier = in.readString();
        menuHTML = in.readString();
        menuURL = in.readString();
        name = in.readString();
        if (in.readByte() == 0x01) {
            payment = new ArrayList<>();
            in.readList(payment, String.class.getClassLoader());
        } else {
            payment = null;
        }
        shortName = in.readString();
        if (in.readByte() == 0x01) {
            hours = new ArrayList<>();
            in.readList(hours, MITDiningRetailDay.class.getClassLoader());
        } else {
            hours = null;
        }
        location = (MITDiningLocation) in.readValue(MITDiningLocation.class.getClassLoader());
        setMarkerText(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (cuisine == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(cuisine);
        }
        dest.writeString(descriptionHTML);
        dest.writeByte((byte) (favorite ? 0x01 : 0x00));
        dest.writeString(homepageURL);
        dest.writeString(iconURL);
        dest.writeString(identifier);
        dest.writeString(menuHTML);
        dest.writeString(menuURL);
        dest.writeString(name);
        if (payment == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(payment);
        }
        dest.writeString(shortName);
        if (hours == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(hours);
        }
        dest.writeValue(location);
        dest.writeString(getMarkerText());
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITDiningRetailVenue> CREATOR = new Parcelable.Creator<MITDiningRetailVenue>() {
        @Override
        public MITDiningRetailVenue createFromParcel(Parcel in) {
            return new MITDiningRetailVenue(in);
        }

        @Override
        public MITDiningRetailVenue[] newArray(int size) {
            return new MITDiningRetailVenue[size];
        }
    };

    @Override
    public int getMapItemType() {
        return MARKERTYPE;
    }

    @Override
    public MarkerOptions getMarkerOptions() {
        MarkerOptions options = new MarkerOptions();
        if (location.getLatitude() != null && location.getLongitude() != null) {
            LatLng position = new LatLng(Double.parseDouble(location.getLatitude()), Double.parseDouble(location.getLongitude()));
            options.position(position);
        } else {
            LoggingManager.Timber.d("NULL");
        }

        MITDiningVenueSnippet snippet = new MITDiningVenueSnippet(identifier, name);
        options.snippet(snippet.toString());
        return options;
    }

    @Override
    public int getIconResource() {
        return R.drawable.ic_pin_red;
    }

    @Override
    protected String getTableName() {
        return null;
    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {

    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {

    }
}
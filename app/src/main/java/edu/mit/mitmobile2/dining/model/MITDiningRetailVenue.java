package edu.mit.mitmobile2.dining.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MITDiningRetailVenue implements Parcelable {
    protected List<String> cuisine;  /* The ObjC Folks dont know what this is it seems */
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
    protected List<String> payment;  /* The ObjC Folks dont know what this is it seems */
    @SerializedName("short_name")
    protected String shortName;
    protected List<MITDiningRetailDay> hours;
    protected MITDiningLocation location;
    protected MITDiningVenues venues;

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

    public MITDiningVenues getVenues() {
        return venues;
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
                ", venues=" + venues +
                '}';
    }

    protected MITDiningRetailVenue(Parcel in) {
        if (in.readByte() == 0x01) {
            cuisine = new ArrayList<String>();
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
            payment = new ArrayList<String>();
            in.readList(payment, String.class.getClassLoader());
        } else {
            payment = null;
        }
        shortName = in.readString();
        if (in.readByte() == 0x01) {
            hours = new ArrayList<MITDiningRetailDay>();
            in.readList(hours, MITDiningRetailDay.class.getClassLoader());
        } else {
            hours = null;
        }
        location = (MITDiningLocation) in.readValue(MITDiningLocation.class.getClassLoader());
        venues = (MITDiningVenues) in.readValue(MITDiningVenues.class.getClassLoader());
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
        dest.writeValue(venues);
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
}
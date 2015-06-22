package edu.mit.mitmobile2.qrreader.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by serg on 6/17/15.
 */
public class QrReaderDetails implements Parcelable {

    public static final String TYPE_PROPERTY_OFFICE_USE_ONLY = "tag";
    public static final String TYPE_URL = "url";                        // not a MIT QR code - display as simple url
    public static final String TYPE_OTHER = "other";

    @SerializedName("type")
    private String type;

    @SerializedName("actions")
    private List<QrReaderDetailsAction> actions;

    @SerializedName("display_type")
    private String displayType;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("share")
    private QrReaderDetailsShare share;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<QrReaderDetailsAction> getActions() {
        return actions;
    }

    public void setActions(List<QrReaderDetailsAction> actions) {
        this.actions = actions;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public QrReaderDetailsShare getShare() {
        return share;
    }

    public void setShare(QrReaderDetailsShare share) {
        this.share = share;
    }

    /* Parcelable */

    protected QrReaderDetails(Parcel in) {
        type = in.readString();
        if (in.readByte() == 0x01) {
            actions = new ArrayList<QrReaderDetailsAction>();
            in.readList(actions, QrReaderDetailsAction.class.getClassLoader());
        } else {
            actions = null;
        }
        displayType = in.readString();
        displayName = in.readString();
        share = (QrReaderDetailsShare) in.readValue(QrReaderDetailsShare.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        if (actions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(actions);
        }
        dest.writeString(displayType);
        dest.writeString(displayName);
        dest.writeValue(share);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<QrReaderDetails> CREATOR = new Parcelable.Creator<QrReaderDetails>() {
        @Override
        public QrReaderDetails createFromParcel(Parcel in) {
            return new QrReaderDetails(in);
        }

        @Override
        public QrReaderDetails[] newArray(int size) {
            return new QrReaderDetails[size];
        }
    };
}

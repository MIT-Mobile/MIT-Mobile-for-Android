package edu.mit.mitmobile2.qrreader.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by serg on 6/17/15.
 */
public class QrReaderDetails {

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
}

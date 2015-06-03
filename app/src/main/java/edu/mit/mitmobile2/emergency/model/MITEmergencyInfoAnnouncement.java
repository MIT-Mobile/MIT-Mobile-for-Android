package edu.mit.mitmobile2.emergency.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by serg on 6/3/15.
 */
public class MITEmergencyInfoAnnouncement implements Parcelable {

    @SerializedName("announcement_html")
    private String announcementHtml;

    @SerializedName("announcement_text")
    private String announcementText;

    // @SerializedName("published_at")
    @Expose
    private Date publishedAt;

    @SerializedName("url")
    private String url;

    public MITEmergencyInfoAnnouncement() {
        // empty constructor
    }

    public String getAnnouncementHtml() {
        return announcementHtml;
    }

    public void setAnnouncementHtml(String announcementHtml) {
        this.announcementHtml = announcementHtml;
    }

    public String getAnnouncementText() {
        return announcementText;
    }

    public void setAnnouncementText(String announcementText) {
        this.announcementText = announcementText;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /* Parcelable */

    protected MITEmergencyInfoAnnouncement(Parcel in) {
        announcementHtml = in.readString();
        announcementText = in.readString();
        long tmpPublishedAt = in.readLong();
        publishedAt = tmpPublishedAt != -1 ? new Date(tmpPublishedAt) : null;
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(announcementHtml);
        dest.writeString(announcementText);
        dest.writeLong(publishedAt != null ? publishedAt.getTime() : -1L);
        dest.writeString(url);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITEmergencyInfoAnnouncement> CREATOR = new Parcelable.Creator<MITEmergencyInfoAnnouncement>() {
        @Override
        public MITEmergencyInfoAnnouncement createFromParcel(Parcel in) {
            return new MITEmergencyInfoAnnouncement(in);
        }

        @Override
        public MITEmergencyInfoAnnouncement[] newArray(int size) {
            return new MITEmergencyInfoAnnouncement[size];
        }
    };
}

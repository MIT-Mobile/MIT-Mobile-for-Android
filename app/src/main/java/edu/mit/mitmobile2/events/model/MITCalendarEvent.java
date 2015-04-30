package edu.mit.mitmobile2.events.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import edu.mit.mitmobile2.shared.logging.LoggingManager;

/**
 * Created by grmartin on 4/27/15.
 */

public class MITCalendarEvent implements Parcelable {
    @SerializedName("id")
    protected String identifier;
    protected String url;
    @SerializedName("start_at")
    protected String startAt;
    protected Date startDate;
    @SerializedName("end_at")
    protected String endAt;
    protected Date endDate;
    protected String title;
    @SerializedName("description_html")
    protected String htmlDescription;
    protected String tickets;
    protected String cost;
    @SerializedName("open_to")
    protected String openTo;
    @SerializedName("owner_id")
    protected String ownerID;
    protected String lecturer;
    protected boolean cancelled;
    @SerializedName("type_code")
    protected String typeCode;
    @SerializedName("status_code")
    protected String statusCode;
    @SerializedName("created_by")
    protected String createdBy;
    @SerializedName("created_at")
    protected Date createdAt;
    @SerializedName("modified_by")
    protected String modifiedBy;
    @SerializedName("modified_at")
    protected Date modifiedAt;
    protected MITCalendarLocation location;
    protected ArrayList<MITCalendar> categories;
    protected HashSet<MITCalendarSponsor> sponsors;
    protected MITCalendarContact contact;
    @SerializedName("series_info")
    protected MITCalendarSeriesInfo seriesInfo;

    public MITCalendarEvent() {
        this.categories = new ArrayList<>();
        this.sponsors = new HashSet<>();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getStartDate() {
        if (startDate == null) {
            startDate = buildDateFromString(startAt);
        }
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        if (endDate == null) {
            endDate = buildDateFromString(endAt);
        }
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHtmlDescription() {
        return htmlDescription;
    }

    public void setHtmlDescription(String htmlDescription) {
        this.htmlDescription = htmlDescription;
    }

    public String getTickets() {
        return tickets;
    }

    public void setTickets(String tickets) {
        this.tickets = tickets;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getOpenTo() {
        return openTo;
    }

    public void setOpenTo(String openTo) {
        this.openTo = openTo;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public MITCalendarLocation getLocation() {
        return location;
    }

    public void setLocation(MITCalendarLocation location) {
        this.location = location;
    }

    public ArrayList<MITCalendar> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<MITCalendar> categories) {
        this.categories = categories;
    }

    public HashSet<MITCalendarSponsor> getSponsors() {
        return sponsors;
    }

    public void setSponsors(HashSet<MITCalendarSponsor> sponsors) {
        this.sponsors = sponsors;
    }

    public MITCalendarContact getContact() {
        return contact;
    }

    public void setContact(MITCalendarContact contact) {
        this.contact = contact;
    }

    public MITCalendarSeriesInfo getSeriesInfo() {
        return seriesInfo;
    }

    public void setSeriesInfo(MITCalendarSeriesInfo seriesInfo) {
        this.seriesInfo = seriesInfo;
    }

    public String getEndAt() {
        return endAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    @Override
    public String toString() {
        return "MITCalendarEvent{" +
                "identifier='" + identifier + '\'' +
                ", url='" + url + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", title='" + title + '\'' +
                ", htmlDescription='" + htmlDescription + '\'' +
                ", tickets='" + tickets + '\'' +
                ", cost='" + cost + '\'' +
                ", openTo='" + openTo + '\'' +
                ", ownerID='" + ownerID + '\'' +
                ", lecturer='" + lecturer + '\'' +
                ", cancelled=" + cancelled +
                ", typeCode='" + typeCode + '\'' +
                ", statusCode='" + statusCode + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", modifiedAt=" + modifiedAt +
                ", location=" + location +
                ", categories=" + categories +
                ", sponsors=" + sponsors +
                ", contact=" + contact +
                ", seriesInfo=" + seriesInfo +
                '}';
    }

    protected MITCalendarEvent(Parcel in) {
        identifier = in.readString();
        url = in.readString();
        long tmpStartAt = in.readLong();
        startDate = tmpStartAt != -1 ? new Date(tmpStartAt) : null;
        long tmpEndAt = in.readLong();
        endDate = tmpEndAt != -1 ? new Date(tmpEndAt) : null;
        startAt = in.readString();
        endAt = in.readString();
        title = in.readString();
        htmlDescription = in.readString();
        tickets = in.readString();
        cost = in.readString();
        openTo = in.readString();
        ownerID = in.readString();
        lecturer = in.readString();
        cancelled = in.readByte() != 0x00;
        typeCode = in.readString();
        statusCode = in.readString();
        createdBy = in.readString();
        long tmpCreatedAt = in.readLong();
        createdAt = tmpCreatedAt != -1 ? new Date(tmpCreatedAt) : null;
        modifiedBy = in.readString();
        long tmpModifiedAt = in.readLong();
        modifiedAt = tmpModifiedAt != -1 ? new Date(tmpModifiedAt) : null;
        location = (MITCalendarLocation) in.readValue(MITCalendarLocation.class.getClassLoader());
        if (in.readByte() == 0x01) {
            categories = new ArrayList<>();
            in.readList(categories, MITCalendar.class.getClassLoader());
        } else {
            categories = null;
        }
        sponsors = (HashSet) in.readValue(HashSet.class.getClassLoader());
        contact = (MITCalendarContact) in.readValue(MITCalendarContact.class.getClassLoader());
        seriesInfo = (MITCalendarSeriesInfo) in.readValue(MITCalendarSeriesInfo.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(identifier);
        dest.writeString(url);
        dest.writeLong(startDate != null ? startDate.getTime() : -1L);
        dest.writeLong(endDate != null ? endDate.getTime() : -1L);
        dest.writeString(startAt);
        dest.writeString(endAt);
        dest.writeString(title);
        dest.writeString(htmlDescription);
        dest.writeString(tickets);
        dest.writeString(cost);
        dest.writeString(openTo);
        dest.writeString(ownerID);
        dest.writeString(lecturer);
        dest.writeByte((byte) (cancelled ? 0x01 : 0x00));
        dest.writeString(typeCode);
        dest.writeString(statusCode);
        dest.writeString(createdBy);
        dest.writeLong(createdAt != null ? createdAt.getTime() : -1L);
        dest.writeString(modifiedBy);
        dest.writeLong(modifiedAt != null ? modifiedAt.getTime() : -1L);
        dest.writeValue(location);
        if (categories == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(categories);
        }
        dest.writeValue(sponsors);
        dest.writeValue(contact);
        dest.writeValue(seriesInfo);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITCalendarEvent> CREATOR = new Parcelable.Creator<MITCalendarEvent>() {
        @Override
        public MITCalendarEvent createFromParcel(Parcel in) {
            return new MITCalendarEvent(in);
        }

        @Override
        public MITCalendarEvent[] newArray(int size) {
            return new MITCalendarEvent[size];
        }
    };

    private Date buildDateFromString(String stringToParse) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        try {
            return format.parse(stringToParse.substring(0, stringToParse.length() - 5));
        } catch (ParseException e) {
            LoggingManager.Timber.e(e, "Failed");
            return new Date();
        }
    }
}

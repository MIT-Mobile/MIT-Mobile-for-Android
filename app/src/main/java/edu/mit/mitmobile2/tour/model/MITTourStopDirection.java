package edu.mit.mitmobile2.tour.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class MITTourStopDirection implements Parcelable {

    @SerializedName("destination_id")
    @Expose
    private String destinationId;
    @Expose
    private String title;
    @SerializedName("body_html")
    @Expose
    private String bodyHtml;
    @Expose
    private Integer zoom;

    @SerializedName("path")
    @Expose
    private List<List<Double>> pathList;

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public Integer getZoom() {
        return zoom;
    }

    public void setZoom(Integer zoom) {
        this.zoom = zoom;
    }

    public List<List<Double>> getPathList() {
        return pathList;
    }

    public void setPathList(List<List<Double>> pathList) {
        this.pathList = pathList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(destinationId);
        dest.writeString(title);
        dest.writeString(bodyHtml);
        dest.writeInt(zoom);
        dest.writeString(pathList.toString());
    }

    private MITTourStopDirection(Parcel p) {
        destinationId = p.readString();
        title = p.readString();
        bodyHtml = p.readString();
        zoom = p.readInt();

        Gson gson = new Gson();
        Type nestedListType = new TypeToken<List<List<Double>>>() {
        }.getType();
        pathList = gson.fromJson(p.readString(), nestedListType);
    }

    public static final Parcelable.Creator<MITTourStopDirection> CREATOR = new Parcelable.Creator<MITTourStopDirection>() {
        public MITTourStopDirection createFromParcel(Parcel source) {
            return new MITTourStopDirection(source);
        }

        public MITTourStopDirection[] newArray(int size) {
            return new MITTourStopDirection[size];
        }
    };
}
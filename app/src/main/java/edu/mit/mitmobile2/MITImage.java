package edu.mit.mitmobile2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class MITImage implements Parcelable {
    @Expose
    private String url;
    @Expose
    private Integer width;
    @Expose
    private Integer height;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeInt(width);
        dest.writeInt(height);
    }

    private MITImage(Parcel p) {
        url = p.readString();
        width = p.readInt();
        height = p.readInt();
    }

    public static final Parcelable.Creator<MITImage> CREATOR = new Parcelable.Creator<MITImage>() {
        public MITImage createFromParcel(Parcel source) {
            return new MITImage(source);
        }

        public MITImage[] newArray(int size) {
            return new MITImage[size];
        }
    };
}

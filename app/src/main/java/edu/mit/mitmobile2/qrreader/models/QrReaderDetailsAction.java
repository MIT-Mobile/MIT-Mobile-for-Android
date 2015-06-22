package edu.mit.mitmobile2.qrreader.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by serg on 6/17/15.
 */
public class QrReaderDetailsAction implements Parcelable {

    @SerializedName("title")
    private String title;

    @SerializedName("url")
    private String url;

    public QrReaderDetailsAction() {
        // empty constructor
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /* Parcelable */

    protected QrReaderDetailsAction(Parcel in) {
        title = in.readString();
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<QrReaderDetailsAction> CREATOR = new Parcelable.Creator<QrReaderDetailsAction>() {
        @Override
        public QrReaderDetailsAction createFromParcel(Parcel in) {
            return new QrReaderDetailsAction(in);
        }

        @Override
        public QrReaderDetailsAction[] newArray(int size) {
            return new QrReaderDetailsAction[size];
        }
    };
}

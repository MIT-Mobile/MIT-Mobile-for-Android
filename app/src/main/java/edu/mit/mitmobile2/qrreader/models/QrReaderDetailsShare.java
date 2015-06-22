package edu.mit.mitmobile2.qrreader.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by serg on 6/17/15.
 */
public class QrReaderDetailsShare implements Parcelable {

    @SerializedName("title")
    private String title;

    @SerializedName("data")
    private String date;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /* Parcelable */

    protected QrReaderDetailsShare(Parcel in) {
        title = in.readString();
        date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(date);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<QrReaderDetailsShare> CREATOR = new Parcelable.Creator<QrReaderDetailsShare>() {
        @Override
        public QrReaderDetailsShare createFromParcel(Parcel in) {
            return new QrReaderDetailsShare(in);
        }

        @Override
        public QrReaderDetailsShare[] newArray(int size) {
            return new QrReaderDetailsShare[size];
        }
    };
}

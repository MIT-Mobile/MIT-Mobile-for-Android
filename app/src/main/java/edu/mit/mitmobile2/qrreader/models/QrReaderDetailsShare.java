package edu.mit.mitmobile2.qrreader.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by serg on 6/17/15.
 */
public class QrReaderDetailsShare {

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
}

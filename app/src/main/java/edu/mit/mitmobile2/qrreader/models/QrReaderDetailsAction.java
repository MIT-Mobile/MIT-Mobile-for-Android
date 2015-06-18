package edu.mit.mitmobile2.qrreader.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by serg on 6/17/15.
 */
public class QrReaderDetailsAction {

    @SerializedName("title")
    private String title;

    @SerializedName("url")
    private String url;

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
}

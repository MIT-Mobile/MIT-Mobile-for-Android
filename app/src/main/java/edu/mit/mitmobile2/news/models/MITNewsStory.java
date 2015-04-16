package edu.mit.mitmobile2.news.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MITNewsStory implements Parcelable {

    @Expose
    private String id;
    @Expose
    private String url;
    @SerializedName("source_url")
    @Expose
    private String sourceUrl;
    @Expose
    private String title;
    @SerializedName("published_at")
    @Expose
    private String publishedAt;
    @Expose
    private String author;
    @Expose
    private String dek;
    @Expose
    private Boolean featured;
    @Expose
    private MITNewsCategory category;
    @Expose
    private String type;
    @SerializedName("body_html")
    @Expose
    private String bodyHtml;
    @SerializedName("cover_image")
    @Expose
    private MITNewsCoverImage coverImage;
    @SerializedName("gallery_images")
    @Expose
    private List<MITNewsGalleryImage> galleryImages = new ArrayList<>();

    public MITNewsStory() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDek() {
        return dek;
    }

    public void setDek(String dek) {
        this.dek = dek;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public MITNewsCategory getCategory() {
        return category;
    }

    public void setCategory(MITNewsCategory category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public MITNewsCoverImage getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(MITNewsCoverImage coverImage) {
        this.coverImage = coverImage;
    }

    public List<MITNewsGalleryImage> getGalleryImages() {
        return galleryImages;
    }

    public void setGalleryImages(List<MITNewsGalleryImage> galleryImages) {
        this.galleryImages = galleryImages;
    }

    public String getOriginalCoverImageUrl() {
        return getUrl(0);
    }

    public String getSmallCoverImageUrl() {
        return getUrl(1);
    }

    public String getFullCoverImageUrl() {
        return getUrl(2);
    }

    private String getUrl(int index) {
        return coverImage.getRepresentations().get(index).getUrl();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(dek);
        dest.writeString(publishedAt);
        dest.writeString(bodyHtml);
        dest.writeParcelable(coverImage, 0);
    }

    private MITNewsStory(Parcel p) {
        this.id = p.readString();
        this.title = p.readString();
        this.author = p.readString();
        this.dek = p.readString();
        this.publishedAt = p.readString();
        this.bodyHtml = p.readString();
        this.coverImage = p.readParcelable(MITNewsCoverImage.class.getClassLoader());
    }

    public static final Parcelable.Creator<MITNewsStory> CREATOR = new Parcelable.Creator<MITNewsStory>() {
        public MITNewsStory createFromParcel(Parcel source) {
            return new MITNewsStory(source);
        }

        public MITNewsStory[] newArray(int size) {
            return new MITNewsStory[size];
        }
    };
}
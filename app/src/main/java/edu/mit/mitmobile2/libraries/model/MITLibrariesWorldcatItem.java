package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by serg on 5/20/15.
 */
public class MITLibrariesWorldcatItem implements Parcelable {

    @SerializedName("id")
    private String identifier;

    @SerializedName("url")
    private String url;

    @SerializedName("worldcat_url")
    private String worldCatUrl;

    @SerializedName("title")
    private String title;

    @SerializedName("cover_images")
    private List<MITLibrariesCoverImage> coverImages;

    @SerializedName("authors")
    private List<Object> author;                        // TODO: clarify proper type (String?)

    @SerializedName("years")
    private List<Object> year;                          // TODO: clarify proper type (String?)

    @SerializedName("publishers")
    private List<Object> publisher;                     // TODO: clarify proper type (String?)

    @SerializedName("formats")
    private List<Object> format;                        // TODO: clarify proper type (String?)

    @SerializedName("isbns")
    private List<Object> isbns;                         // TODO: clarify proper type (String?)

    @SerializedName("subjects")
    private List<Object> subject;                       // TODO: clarify proper type (String?)

    @SerializedName("langs")
    private List<Object> language;                      // TODO: clarify proper type (String?)

    @SerializedName("extents")
    private List<Object> extent;                        // TODO: clarify proper type (String?)

    @SerializedName("summaries")
    private List<Object> summaries;                     // TODO: clarify proper type (String?)

    @SerializedName("editions")
    private List<Object> editions;                      // TODO: clarify proper type (String?)

    @SerializedName("address")
    private List<Object> address;                       // TODO: clarify proper type (String?)

    @SerializedName("holdings")
    private List<MITLibrariesHolding> holdings;

    @SerializedName("citations")
    private List<Object> rawCitations;                  // TODO: clarify proper type (HashMap?)

    @SerializedName("composed-html")
    private String composedHTML;

    @Expose
    private List<Object> citations;

    public MITLibrariesWorldcatItem() {
        // empty constructor
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

    public String getWorldCatUrl() {
        return worldCatUrl;
    }

    public void setWorldCatUrl(String worldCatUrl) {
        this.worldCatUrl = worldCatUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MITLibrariesCoverImage> getCoverImages() {
        return coverImages;
    }

    public void setCoverImages(List<MITLibrariesCoverImage> coverImages) {
        this.coverImages = coverImages;
    }

    public List<Object> getAuthor() {
        return author;
    }

    public void setAuthor(List<Object> author) {
        this.author = author;
    }

    public List<Object> getYear() {
        return year;
    }

    public void setYear(List<Object> year) {
        this.year = year;
    }

    public List<Object> getPublisher() {
        return publisher;
    }

    public void setPublisher(List<Object> publisher) {
        this.publisher = publisher;
    }

    public List<Object> getFormat() {
        return format;
    }

    public void setFormat(List<Object> format) {
        this.format = format;
    }

    public List<Object> getIsbns() {
        return isbns;
    }

    public void setIsbns(List<Object> isbns) {
        this.isbns = isbns;
    }

    public List<Object> getSubject() {
        return subject;
    }

    public void setSubject(List<Object> subject) {
        this.subject = subject;
    }

    public List<Object> getLanguage() {
        return language;
    }

    public void setLanguage(List<Object> language) {
        this.language = language;
    }

    public List<Object> getExtent() {
        return extent;
    }

    public void setExtent(List<Object> extent) {
        this.extent = extent;
    }

    public List<Object> getSummaries() {
        return summaries;
    }

    public void setSummaries(List<Object> summaries) {
        this.summaries = summaries;
    }

    public List<Object> getEditions() {
        return editions;
    }

    public void setEditions(List<Object> editions) {
        this.editions = editions;
    }

    public List<Object> getAddress() {
        return address;
    }

    public void setAddress(List<Object> address) {
        this.address = address;
    }

    public List<MITLibrariesHolding> getHoldings() {
        return holdings;
    }

    public void setHoldings(List<MITLibrariesHolding> holdings) {
        this.holdings = holdings;
    }

    public List<Object> getRawCitations() {
        return rawCitations;
    }

    public void setRawCitations(List<Object> rawCitations) {
        this.rawCitations = rawCitations;
    }

    public String getComposedHTML() {
        return composedHTML;
    }

    public void setComposedHTML(String composedHTML) {
        this.composedHTML = composedHTML;
    }

    public List<Object> getCitations() {
        if (citations == null) {
            // TODO: parse rawCitations here
            throw new UnsupportedOperationException("Method not implemented");
        }
        return citations;
    }

    protected MITLibrariesWorldcatItem(Parcel in) {
        identifier = in.readString();
        url = in.readString();
        worldCatUrl = in.readString();
        title = in.readString();
        if (in.readByte() == 0x01) {
            coverImages = new ArrayList<MITLibrariesCoverImage>();
            in.readList(coverImages, MITLibrariesCoverImage.class.getClassLoader());
        } else {
            coverImages = null;
        }
        if (in.readByte() == 0x01) {
            author = new ArrayList<Object>();
            in.readList(author, Object.class.getClassLoader());
        } else {
            author = null;
        }
        if (in.readByte() == 0x01) {
            year = new ArrayList<Object>();
            in.readList(year, Object.class.getClassLoader());
        } else {
            year = null;
        }
        if (in.readByte() == 0x01) {
            publisher = new ArrayList<Object>();
            in.readList(publisher, Object.class.getClassLoader());
        } else {
            publisher = null;
        }
        if (in.readByte() == 0x01) {
            format = new ArrayList<Object>();
            in.readList(format, Object.class.getClassLoader());
        } else {
            format = null;
        }
        if (in.readByte() == 0x01) {
            isbns = new ArrayList<Object>();
            in.readList(isbns, Object.class.getClassLoader());
        } else {
            isbns = null;
        }
        if (in.readByte() == 0x01) {
            subject = new ArrayList<Object>();
            in.readList(subject, Object.class.getClassLoader());
        } else {
            subject = null;
        }
        if (in.readByte() == 0x01) {
            language = new ArrayList<Object>();
            in.readList(language, Object.class.getClassLoader());
        } else {
            language = null;
        }
        if (in.readByte() == 0x01) {
            extent = new ArrayList<Object>();
            in.readList(extent, Object.class.getClassLoader());
        } else {
            extent = null;
        }
        if (in.readByte() == 0x01) {
            summaries = new ArrayList<Object>();
            in.readList(summaries, Object.class.getClassLoader());
        } else {
            summaries = null;
        }
        if (in.readByte() == 0x01) {
            editions = new ArrayList<Object>();
            in.readList(editions, Object.class.getClassLoader());
        } else {
            editions = null;
        }
        if (in.readByte() == 0x01) {
            address = new ArrayList<Object>();
            in.readList(address, Object.class.getClassLoader());
        } else {
            address = null;
        }
        if (in.readByte() == 0x01) {
            holdings = new ArrayList<MITLibrariesHolding>();
            in.readList(holdings, MITLibrariesHolding.class.getClassLoader());
        } else {
            holdings = null;
        }
        if (in.readByte() == 0x01) {
            rawCitations = new ArrayList<Object>();
            in.readList(rawCitations, Object.class.getClassLoader());
        } else {
            rawCitations = null;
        }
        composedHTML = in.readString();
        if (in.readByte() == 0x01) {
            citations = new ArrayList<Object>();
            in.readList(citations, Object.class.getClassLoader());
        } else {
            citations = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(identifier);
        dest.writeString(url);
        dest.writeString(worldCatUrl);
        dest.writeString(title);
        if (coverImages == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(coverImages);
        }
        if (author == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(author);
        }
        if (year == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(year);
        }
        if (publisher == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(publisher);
        }
        if (format == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(format);
        }
        if (isbns == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(isbns);
        }
        if (subject == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(subject);
        }
        if (language == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(language);
        }
        if (extent == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(extent);
        }
        if (summaries == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(summaries);
        }
        if (editions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(editions);
        }
        if (address == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(address);
        }
        if (holdings == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(holdings);
        }
        if (rawCitations == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(rawCitations);
        }
        dest.writeString(composedHTML);
        if (citations == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(citations);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLibrariesWorldcatItem> CREATOR = new Parcelable.Creator<MITLibrariesWorldcatItem>() {
        @Override
        public MITLibrariesWorldcatItem createFromParcel(Parcel in) {
            return new MITLibrariesWorldcatItem(in);
        }

        @Override
        public MITLibrariesWorldcatItem[] newArray(int size) {
            return new MITLibrariesWorldcatItem[size];
        }
    };
}

package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
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
    private List<String> author;

    @SerializedName("years")
    private List<String> year;

    @SerializedName("publishers")
    private List<String> publisher;

    @SerializedName("formats")
    private List<String> format;

    @SerializedName("isbns")
    private List<String> isbns;

    @SerializedName("subjects")
    private List<String> subject;

    @SerializedName("langs")
    private List<String> language;

    @SerializedName("extents")
    private List<String> extent;

    @SerializedName("summaries")
    private List<String> summaries;

    @SerializedName("editions")
    private List<String> editions;

    @SerializedName("address")
    private List<String> address;

    @SerializedName("holdings")
    private List<MITLibrariesHolding> holdings;

    @SerializedName("citations")
    private HashMap<String, String> rawCitations;

    @SerializedName("composed-html")
    private String composedHTML;

    @Expose
    private List<Object> citationsList;

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

    public List<String> getAuthor() {
        return author;
    }

    public void setAuthor(List<String> author) {
        this.author = author;
    }

    public List<String> getYear() {
        return year;
    }

    public void setYear(List<String> year) {
        this.year = year;
    }

    public List<String> getPublisher() {
        return publisher;
    }

    public void setPublisher(List<String> publisher) {
        this.publisher = publisher;
    }

    public List<String> getFormat() {
        return format;
    }

    public void setFormat(List<String> format) {
        this.format = format;
    }

    public List<String> getIsbns() {
        return isbns;
    }

    public void setIsbns(List<String> isbns) {
        this.isbns = isbns;
    }

    public List<String> getSubject() {
        return subject;
    }

    public void setSubject(List<String> subject) {
        this.subject = subject;
    }

    public List<String> getLanguage() {
        return language;
    }

    public void setLanguage(List<String> language) {
        this.language = language;
    }

    public List<String> getExtent() {
        return extent;
    }

    public void setExtent(List<String> extent) {
        this.extent = extent;
    }

    public List<String> getSummaries() {
        return summaries;
    }

    public void setSummaries(List<String> summaries) {
        this.summaries = summaries;
    }

    public List<String> getEditions() {
        return editions;
    }

    public void setEditions(List<String> editions) {
        this.editions = editions;
    }

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }

    public List<MITLibrariesHolding> getHoldings() {
        return holdings;
    }

    public void setHoldings(List<MITLibrariesHolding> holdings) {
        this.holdings = holdings;
    }

    public HashMap<String, String> getRawCitations() {
        return rawCitations;
    }

    public void setRawCitations(HashMap<String, String> rawCitations) {
        this.rawCitations = rawCitations;
    }

    public String getComposedHTML() {
        return composedHTML;
    }

    public void setComposedHTML(String composedHTML) {
        this.composedHTML = composedHTML;
    }

    public List<Object> getCitations() {
        if (citationsList == null) {
            // TODO: parse rawCitations here
            throw new UnsupportedOperationException("Method not implemented");
        }
        return citationsList;
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
            author = new ArrayList<String>();
            in.readList(author, String.class.getClassLoader());
        } else {
            author = null;
        }
        if (in.readByte() == 0x01) {
            year = new ArrayList<String>();
            in.readList(year, String.class.getClassLoader());
        } else {
            year = null;
        }
        if (in.readByte() == 0x01) {
            publisher = new ArrayList<String>();
            in.readList(publisher, String.class.getClassLoader());
        } else {
            publisher = null;
        }
        if (in.readByte() == 0x01) {
            format = new ArrayList<String>();
            in.readList(format, String.class.getClassLoader());
        } else {
            format = null;
        }
        if (in.readByte() == 0x01) {
            isbns = new ArrayList<String>();
            in.readList(isbns, String.class.getClassLoader());
        } else {
            isbns = null;
        }
        if (in.readByte() == 0x01) {
            subject = new ArrayList<String>();
            in.readList(subject, String.class.getClassLoader());
        } else {
            subject = null;
        }
        if (in.readByte() == 0x01) {
            language = new ArrayList<String>();
            in.readList(language, String.class.getClassLoader());
        } else {
            language = null;
        }
        if (in.readByte() == 0x01) {
            extent = new ArrayList<String>();
            in.readList(extent, String.class.getClassLoader());
        } else {
            extent = null;
        }
        if (in.readByte() == 0x01) {
            summaries = new ArrayList<String>();
            in.readList(summaries, String.class.getClassLoader());
        } else {
            summaries = null;
        }
        if (in.readByte() == 0x01) {
            editions = new ArrayList<String>();
            in.readList(editions, String.class.getClassLoader());
        } else {
            editions = null;
        }
        if (in.readByte() == 0x01) {
            address = new ArrayList<String>();
            in.readList(address, String.class.getClassLoader());
        } else {
            address = null;
        }
        if (in.readByte() == 0x01) {
            holdings = new ArrayList<MITLibrariesHolding>();
            in.readList(holdings, MITLibrariesHolding.class.getClassLoader());
        } else {
            holdings = null;
        }
        rawCitations = (HashMap) in.readValue(HashMap.class.getClassLoader());
        composedHTML = in.readString();
        if (in.readByte() == 0x01) {
            citationsList = new ArrayList<Object>();
            in.readList(citationsList, Object.class.getClassLoader());
        } else {
            citationsList = null;
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
        dest.writeValue(rawCitations);
        dest.writeString(composedHTML);
        if (citationsList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(citationsList);
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

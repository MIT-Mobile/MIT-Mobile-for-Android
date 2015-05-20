package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by serg on 5/19/15.
 */
public class MITLibrariesMITItem implements Parcelable {

    @SerializedName("call_number")
    private String callNumber;

    @SerializedName("author")
    private String author;

    @SerializedName("year")
    private String year;

    @SerializedName("title")
    private String title;

    @SerializedName("imprint")
    private String imprint;

    @SerializedName("isbn")
    private String isbn;

    @SerializedName("doc_number")
    private String docNumber;

    @SerializedName("material")
    private String material;

    @SerializedName("sub_library")
    private String subLibrary;

    @SerializedName("barcode")
    private String barcode;

    @Expose
    private List<MITLibrariesCoverImage> coverImages;

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImprint() {
        return imprint;
    }

    public void setImprint(String imprint) {
        this.imprint = imprint;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getSubLibrary() {
        return subLibrary;
    }

    public void setSubLibrary(String subLibrary) {
        this.subLibrary = subLibrary;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public List<MITLibrariesCoverImage> getCoverImages() {
        return coverImages;
    }

    public void setCoverImages(List<MITLibrariesCoverImage> coverImages) {
        this.coverImages = coverImages;
    }

    public MITLibrariesMITItem() {
        // empty constructor
    }

    protected MITLibrariesMITItem(Parcel in) {
        callNumber = in.readString();
        author = in.readString();
        year = in.readString();
        title = in.readString();
        imprint = in.readString();
        isbn = in.readString();
        docNumber = in.readString();
        material = in.readString();
        subLibrary = in.readString();
        barcode = in.readString();
        if (in.readByte() == 0x01) {
            coverImages = new ArrayList<MITLibrariesCoverImage>();
            in.readList(coverImages, MITLibrariesCoverImage.class.getClassLoader());
        } else {
            coverImages = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(callNumber);
        dest.writeString(author);
        dest.writeString(year);
        dest.writeString(title);
        dest.writeString(imprint);
        dest.writeString(isbn);
        dest.writeString(docNumber);
        dest.writeString(material);
        dest.writeString(subLibrary);
        dest.writeString(barcode);
        if (coverImages == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(coverImages);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLibrariesMITItem> CREATOR = new Parcelable.Creator<MITLibrariesMITItem>() {
        @Override
        public MITLibrariesMITItem createFromParcel(Parcel in) {
            return new MITLibrariesMITItem(in);
        }

        @Override
        public MITLibrariesMITItem[] newArray(int size) {
            return new MITLibrariesMITItem[size];
        }
    };
}

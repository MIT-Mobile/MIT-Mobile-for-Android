package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by serg on 5/20/15.
 */
public class MITLibrariesTerm implements Parcelable {

    @SerializedName("name")
    private String name;

    @SerializedName("dates")
    private MITLibrariesDate dates;

    @SerializedName("regular")
    private List<MITLibrariesRegularTerm> regularTerm;

    @SerializedName("closing")
    private List<MITLibrariesClosingsTerm> closingsTerm;

    @SerializedName("exceptions")
    private List<MITLibrariesExceptionsTerm> exceptionsTerm;

    public MITLibrariesTerm() {
        // empty constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MITLibrariesDate getDates() {
        return dates;
    }

    public void setDates(MITLibrariesDate dates) {
        this.dates = dates;
    }

    public List<MITLibrariesRegularTerm> getRegularTerm() {
        return regularTerm;
    }

    public void setRegularTerm(List<MITLibrariesRegularTerm> regularTerm) {
        this.regularTerm = regularTerm;
    }

    public List<MITLibrariesClosingsTerm> getClosingsTerm() {
        return closingsTerm;
    }

    public void setClosingsTerm(List<MITLibrariesClosingsTerm> closingsTerm) {
        this.closingsTerm = closingsTerm;
    }

    public List<MITLibrariesExceptionsTerm> getExceptionsTerm() {
        return exceptionsTerm;
    }

    public void setExceptionsTerm(List<MITLibrariesExceptionsTerm> exceptionsTerm) {
        this.exceptionsTerm = exceptionsTerm;
    }

    protected MITLibrariesTerm(Parcel in) {
        name = in.readString();
        dates = (MITLibrariesDate) in.readValue(MITLibrariesDate.class.getClassLoader());
        if (in.readByte() == 0x01) {
            regularTerm = new ArrayList<MITLibrariesRegularTerm>();
            in.readList(regularTerm, MITLibrariesRegularTerm.class.getClassLoader());
        } else {
            regularTerm = null;
        }
        if (in.readByte() == 0x01) {
            closingsTerm = new ArrayList<MITLibrariesClosingsTerm>();
            in.readList(closingsTerm, MITLibrariesClosingsTerm.class.getClassLoader());
        } else {
            closingsTerm = null;
        }
        if (in.readByte() == 0x01) {
            exceptionsTerm = new ArrayList<MITLibrariesExceptionsTerm>();
            in.readList(exceptionsTerm, MITLibrariesExceptionsTerm.class.getClassLoader());
        } else {
            exceptionsTerm = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeValue(dates);
        if (regularTerm == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(regularTerm);
        }
        if (closingsTerm == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(closingsTerm);
        }
        if (exceptionsTerm == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(exceptionsTerm);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLibrariesTerm> CREATOR = new Parcelable.Creator<MITLibrariesTerm>() {
        @Override
        public MITLibrariesTerm createFromParcel(Parcel in) {
            return new MITLibrariesTerm(in);
        }

        @Override
        public MITLibrariesTerm[] newArray(int size) {
            return new MITLibrariesTerm[size];
        }
    };
}

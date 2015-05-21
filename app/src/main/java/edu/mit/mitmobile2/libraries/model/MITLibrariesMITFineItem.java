package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class MITLibrariesMITFineItem extends MITLibrariesMITItem implements Parcelable {

    @SerializedName("status")
    private String status;

    @SerializedName("description")
    private String fineDescription;

    @SerializedName("formatted_amount")
    private String formattedAmount;

    @SerializedName("amount")
    private int amount;

    @SerializedName("fined_at")
    private String finedAtDateString;

    private Date finedAtDate;

    public MITLibrariesMITFineItem() {
        // empty constructor
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFineDescription() {
        return fineDescription;
    }

    public void setFineDescription(String fineDescription) {
        this.fineDescription = fineDescription;
    }

    public String getFormattedAmount() {
        return formattedAmount;
    }

    public void setFormattedAmount(String formattedAmount) {
        this.formattedAmount = formattedAmount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getFinedAtDateString() {
        return finedAtDateString;
    }

    public void setFinedAtDateString(String finedAtDateString) {
        this.finedAtDateString = finedAtDateString;
    }

    public Date getFinedAtDate() {
        if (finedAtDate == null) {
            // TODO: convert date format here
            throw new UnsupportedOperationException("method not implemented");
        }
        return finedAtDate;
    }

    protected MITLibrariesMITFineItem(Parcel in) {
        super(in);
        status = in.readString();
        fineDescription = in.readString();
        formattedAmount = in.readString();
        amount = in.readInt();
        finedAtDateString = in.readString();
        long tmpFinedAtDate = in.readLong();
        finedAtDate = tmpFinedAtDate != -1 ? new Date(tmpFinedAtDate) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(status);
        dest.writeString(fineDescription);
        dest.writeString(formattedAmount);
        dest.writeInt(amount);
        dest.writeString(finedAtDateString);
        dest.writeLong(finedAtDate != null ? finedAtDate.getTime() : -1L);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLibrariesMITFineItem> CREATOR = new Parcelable.Creator<MITLibrariesMITFineItem>() {
        @Override
        public MITLibrariesMITFineItem createFromParcel(Parcel in) {
            return new MITLibrariesMITFineItem(in);
        }

        @Override
        public MITLibrariesMITFineItem[] newArray(int size) {
            return new MITLibrariesMITFineItem[size];
        }
    };
}

package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by serg on 5/20/15.
 */
public class MITLibrariesUser implements Parcelable {

    @SerializedName("name")
    private String name;

    @SerializedName("loans")
    private List<MITLibrariesMITLoanItem> loans;

    @SerializedName("holds")
    private List<MITLibrariesMITHoldItem> holds;

    @SerializedName("fines")
    private List<MITLibrariesMITFineItem> fines;

    @SerializedName("formatted_balance")
    private String formattedBalance;

    @SerializedName("balance")
    private int balance;

    @SerializedName("overdue_count")
    private int overdueItemsCount;

    @SerializedName("ready_for_pickup_count")
    private int readyForPickupCount;

    public MITLibrariesUser() {
        // empty constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MITLibrariesMITLoanItem> getLoans() {
        return loans;
    }

    public void setLoans(List<MITLibrariesMITLoanItem> loans) {
        this.loans = loans;
    }

    public List<MITLibrariesMITHoldItem> getHolds() {
        return holds;
    }

    public void setHolds(List<MITLibrariesMITHoldItem> holds) {
        this.holds = holds;
    }

    public List<MITLibrariesMITFineItem> getFines() {
        return fines;
    }

    public void setFines(List<MITLibrariesMITFineItem> fines) {
        this.fines = fines;
    }

    public String getFormattedBalance() {
        return formattedBalance;
    }

    public void setFormattedBalance(String formattedBalance) {
        this.formattedBalance = formattedBalance;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getOverdueItemsCount() {
        return overdueItemsCount;
    }

    public void setOverdueItemsCount(int overdueItemsCount) {
        this.overdueItemsCount = overdueItemsCount;
    }

    public int getReadyForPickupCount() {
        return readyForPickupCount;
    }

    public void setReadyForPickupCount(int readyForPickupCount) {
        this.readyForPickupCount = readyForPickupCount;
    }

    protected MITLibrariesUser(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0x01) {
            loans = new ArrayList<MITLibrariesMITLoanItem>();
            in.readList(loans, MITLibrariesMITLoanItem.class.getClassLoader());
        } else {
            loans = null;
        }
        if (in.readByte() == 0x01) {
            holds = new ArrayList<MITLibrariesMITHoldItem>();
            in.readList(holds, MITLibrariesMITHoldItem.class.getClassLoader());
        } else {
            holds = null;
        }
        if (in.readByte() == 0x01) {
            fines = new ArrayList<MITLibrariesMITFineItem>();
            in.readList(fines, MITLibrariesMITFineItem.class.getClassLoader());
        } else {
            fines = null;
        }
        formattedBalance = in.readString();
        balance = in.readInt();
        overdueItemsCount = in.readInt();
        readyForPickupCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (loans == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(loans);
        }
        if (holds == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(holds);
        }
        if (fines == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(fines);
        }
        dest.writeString(formattedBalance);
        dest.writeInt(balance);
        dest.writeInt(overdueItemsCount);
        dest.writeInt(readyForPickupCount);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLibrariesUser> CREATOR = new Parcelable.Creator<MITLibrariesUser>() {
        @Override
        public MITLibrariesUser createFromParcel(Parcel in) {
            return new MITLibrariesUser(in);
        }

        @Override
        public MITLibrariesUser[] newArray(int size) {
            return new MITLibrariesUser[size];
        }
    };
}

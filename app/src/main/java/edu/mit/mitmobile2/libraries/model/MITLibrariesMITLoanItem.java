package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class MITLibrariesMITLoanItem extends MITLibrariesMITItem {

    @SerializedName("loaned_at")
    private String loanedAtString;

    @SerializedName("due_at")
    private String dueAtString;

    @SerializedName("overdue")
    private boolean overdue;

    @SerializedName("long_overdue")
    private boolean longOverdue;

    @SerializedName("pending_fine")
    private int pendingFine;

    @SerializedName("formatted_pending_fine")
    private String formattedPendingFine;

    @SerializedName("due_text")
    private String dueText;

    @SerializedName("has_hold")
    private boolean hasHold;

    private Date loanedAtDate;

    private Date dueAtDate;

    public String getDueAtString() {
        return dueAtString;
    }

    public void setDueAtString(String dueAtString) {
        this.dueAtString = dueAtString;
    }

    public String getLoanedAtString() {
        return loanedAtString;
    }

    public void setLoanedAtString(String loanedAtString) {
        this.loanedAtString = loanedAtString;
    }

    public Date getDueAtDate() {
        // TODO: Date Formatting
        return dueAtDate;
    }

    public Date getLoanedAtDate() {
        // TODO: Date Formatting
        return loanedAtDate;
    }

    public boolean isOverdue() {
        return overdue;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }

    public boolean isLongOverdue() {
        return longOverdue;
    }

    public void setLongOverdue(boolean longOverdue) {
        this.longOverdue = longOverdue;
    }

    public int getPendingFine() {
        return pendingFine;
    }

    public void setPendingFine(int pendingFine) {
        this.pendingFine = pendingFine;
    }

    public String getFormattedPendingFine() {
        return formattedPendingFine;
    }

    public void setFormattedPendingFine(String formattedPendingFine) {
        this.formattedPendingFine = formattedPendingFine;
    }

    public String getDueText() {
        return dueText;
    }

    public void setDueText(String dueText) {
        this.dueText = dueText;
    }

    public boolean isHasHold() {
        return hasHold;
    }

    public void setHasHold(boolean hasHold) {
        this.hasHold = hasHold;
    }

    protected MITLibrariesMITLoanItem(Parcel in) {
        super(in);
//        long tmpLoanedAt = in.readLong();
//        loanedAtString = tmpLoanedAt != -1 ? new Date(tmpLoanedAt) : null;
//        long tmpDueAt = in.readLong();
//        dueAtString = tmpDueAt != -1 ? new Date(tmpDueAt) : null;
        loanedAtString = in.readString();
        dueAtString = in.readString();
        overdue = in.readByte() != 0x00;
        longOverdue = in.readByte() != 0x00;
        pendingFine = in.readInt();
        formattedPendingFine = in.readString();
        dueText = in.readString();
        hasHold = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
//        dest.writeLong(loanedAtString != null ? loanedAtString.getTime() : -1L);
//        dest.writeLong(dueAtString != null ? dueAtString.getTime() : -1L);
        dest.writeString(loanedAtString);
        dest.writeString(dueAtString);
        dest.writeByte((byte) (overdue ? 0x01 : 0x00));
        dest.writeByte((byte) (longOverdue ? 0x01 : 0x00));
        dest.writeInt(pendingFine);
        dest.writeString(formattedPendingFine);
        dest.writeString(dueText);
        dest.writeByte((byte) (hasHold ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLibrariesMITLoanItem> CREATOR = new Parcelable.Creator<MITLibrariesMITLoanItem>() {
        @Override
        public MITLibrariesMITLoanItem createFromParcel(Parcel in) {
            return new MITLibrariesMITLoanItem(in);
        }

        @Override
        public MITLibrariesMITLoanItem[] newArray(int size) {
            return new MITLibrariesMITLoanItem[size];
        }
    };
}

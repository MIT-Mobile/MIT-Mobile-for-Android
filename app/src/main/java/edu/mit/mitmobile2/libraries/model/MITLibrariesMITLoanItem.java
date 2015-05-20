package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by serg on 5/19/15.
 */
public class MITLibrariesMITLoanItem extends MITLibrariesMITItem {

    @SerializedName("loaned_at")
    private Date loanedAt;

    @SerializedName("due_at")
    private Date dueAt;

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

    public Date getLoanedAt() {
        return loanedAt;
    }

    public void setLoanedAt(Date loanedAt) {
        this.loanedAt = loanedAt;
    }

    public Date getDueAt() {
        return dueAt;
    }

    public void setDueAt(Date dueAt) {
        this.dueAt = dueAt;
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
        long tmpLoanedAt = in.readLong();
        loanedAt = tmpLoanedAt != -1 ? new Date(tmpLoanedAt) : null;
        long tmpDueAt = in.readLong();
        dueAt = tmpDueAt != -1 ? new Date(tmpDueAt) : null;
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
        dest.writeLong(loanedAt != null ? loanedAt.getTime() : -1L);
        dest.writeLong(dueAt != null ? dueAt.getTime() : -1L);
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

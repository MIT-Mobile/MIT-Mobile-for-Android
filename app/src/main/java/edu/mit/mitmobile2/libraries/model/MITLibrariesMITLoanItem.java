package edu.mit.mitmobile2.libraries.model;

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
}

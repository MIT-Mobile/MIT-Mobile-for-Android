package edu.mit.mitmobile2.libraries.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by serg on 5/19/15.
 */
public class MITLibrariesMITFineItem extends MITLibrariesMITItem {

    @SerializedName("status")
    private String status;

    @SerializedName("description")
    private String fineDescription;

    @SerializedName("formatted_amount")
    private String formattedAmount;

    @SerializedName("amount")
    private int amount;

//    @SerializedName("status")
//    private Date finedAtDate;


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
}

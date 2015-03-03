package edu.mit.mitmobile2.shuttles.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MITShuttlePrediction {

    @SerializedName("vehicle_id")
    @Expose
    private String vehicleId;
    @Expose
    private Integer timestamp;
    @Expose
    private Integer seconds;


    public String getVehicleId() {
        return vehicleId;
    }


    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }


    public Integer getTimestamp() {
        return timestamp;
    }


    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }


    public Integer getSeconds() {
        return seconds;
    }


    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

}


package edu.mit.mitmobile2.shuttles.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MITShuttleVehiclesWrapper {

    @SerializedName("route_id")
    @Expose
    private String routeId;
    @SerializedName("route_url")
    @Expose
    private String routeUrl;
    @Expose
    private String agency;
    @Expose
    private Boolean scheduled;
    @Expose
    private Boolean predictable;
    @Expose
    private List<MITShuttleVehicle> vehicles = new ArrayList<MITShuttleVehicle>();


    public String getRouteId() {
        return routeId;
    }


    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }


    public String getRouteUrl() {
        return routeUrl;
    }


    public void setRouteUrl(String routeUrl) {
        this.routeUrl = routeUrl;
    }


    public String getAgency() {
        return agency;
    }


    public void setAgency(String agency) {
        this.agency = agency;
    }


    public Boolean getScheduled() {
        return scheduled;
    }


    public void setScheduled(Boolean scheduled) {
        this.scheduled = scheduled;
    }


    public Boolean getPredictable() {
        return predictable;
    }


    public void setPredictable(Boolean predictable) {
        this.predictable = predictable;
    }


    public List<MITShuttleVehicle> getVehicles() {
        return vehicles;
    }

    /**
     *
     * @param vehicles
     * The vehicles
     */
    public void setVehicles(List<MITShuttleVehicle> vehicles) {
        this.vehicles = vehicles;
    }

}
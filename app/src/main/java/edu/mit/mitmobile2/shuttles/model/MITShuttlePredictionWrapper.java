package edu.mit.mitmobile2.shuttles.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MITShuttlePredictionWrapper {

    public class Predictions {
        @SerializedName("route_id")
        @Expose
        private String routeId;
        @SerializedName("route_url")
        @Expose
        private String routeUrl;
        @SerializedName("route_title")
        @Expose
        private String routeTitle;
        @SerializedName("stop_id")
        @Expose
        private String stopId;
        @SerializedName("stop_url")
        @Expose
        private String stopUrl;
        @SerializedName("stop_title")
        @Expose
        private String stopTitle;
        @Expose
        private List<MITShuttlePrediction> predictions = new ArrayList<MITShuttlePrediction>();


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


        public String getRouteTitle() {
            return routeTitle;
        }


        public void setRouteTitle(String routeTitle) {
            this.routeTitle = routeTitle;
        }


        public String getStopId() {
            return stopId;
        }


        public void setStopId(String stopId) {
            this.stopId = stopId;
        }


        public String getStopUrl() {
            return stopUrl;
        }


        public void setStopUrl(String stopUrl) {
            this.stopUrl = stopUrl;
        }


        public String getStopTitle() {
            return stopTitle;
        }


        public void setStopTitle(String stopTitle) {
            this.stopTitle = stopTitle;
        }


        public List<MITShuttlePrediction> getPredictions() {
            return predictions;
        }


        public void setPredictions(List<MITShuttlePrediction> predictions) {
            this.predictions = predictions;
        }
    }

}
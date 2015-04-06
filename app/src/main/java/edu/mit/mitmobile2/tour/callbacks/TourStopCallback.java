package edu.mit.mitmobile2.tour.callbacks;

import edu.mit.mitmobile2.tour.model.MITTour;

public interface TourStopCallback {
    void switchViews(boolean toList);
    MITTour getTour();
    void showTourDetailActivity(String description);
}

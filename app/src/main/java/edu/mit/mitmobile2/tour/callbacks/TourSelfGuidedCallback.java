package edu.mit.mitmobile2.tour.callbacks;

import edu.mit.mitmobile2.tour.model.MITTour;
import edu.mit.mitmobile2.tour.model.MITTourStop;

public interface TourSelfGuidedCallback {
    void switchViews(boolean toList);
    MITTour getTour();
    void showTourDetailActivity(String description);

    void showMainLoopFragment(int currentStopNum);

    void showSideTripFragment(MITTourStop mitTourStop);

    double getDistance(MITTourStop mitTourStop);
}

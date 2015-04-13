package edu.mit.mitmobile2.tour.callbacks;

import edu.mit.mitmobile2.tour.model.MITTourStop;

public interface TourStopCallback {

    void setMainLoopActionBarTitle(int mainLoopStopNum, int mainLoopStopsSize);

    void setSideTripActionBarTitle();

    void setTourStopActionbarTitle(MITTourStop mitTourStop);
}


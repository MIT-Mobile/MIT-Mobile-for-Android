package edu.mit.mitmobile2;

import edu.mit.mitmobile2.tour.model.MITTour;
import retrofit.RetrofitError;

public class OttoBusEvent {

    public static class RetrofitFailureEvent {
        RetrofitError error;

        public RetrofitFailureEvent(RetrofitError error) {
            this.error = error;
        }

        public RetrofitError getError() {
            return error;
        }
    }

    public static class TourInfoLoadedEvent {
        private MITTour tour;

        public TourInfoLoadedEvent(MITTour tour) {
            this.tour = tour;
        }

        public MITTour getTour() {
            return tour;
        }
    }

    public static class ToggleDescriptionEvent {
        public ToggleDescriptionEvent() {
        }
    }

    public static class ChangeDateTextEvent {
        String dateText;
        int position;

        public ChangeDateTextEvent(String dateText, int position) {
            this.dateText = dateText;
            this.position = position;
        }

        public String getDateText() {
            return dateText;
        }

        public int getPosition() {
            return position;
        }
    }

    public static class UpdateDiningInfoEvent {
        public UpdateDiningInfoEvent() {
        }
    }

    public static class RefreshCompletedEvent {
        public RefreshCompletedEvent() {
        }
    }
}

package edu.mit.mitmobile2;

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
}

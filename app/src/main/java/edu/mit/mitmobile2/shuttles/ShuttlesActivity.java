package edu.mit.mitmobile2.shuttles;

import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.R;

import android.os.Bundle;

public class ShuttlesActivity extends MITModuleActivity {

    int contentLayoutId = R.layout.content_shuttles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setContentLayoutId(R.layout.content_shuttles);
        super.onCreate(savedInstanceState);

        /**
         * Samples of how to use the API Call with Retrofit
         */

//        setTitle(getString(R.string.title_activity_shuttles));

        /*HashMap<String, String> queries = new HashMap<>();
        queries.put("agency", "mit");
        queries.put("stop_number", "1");

        apiClient.get(Constants.SHUTTLES, Constants.Shuttles.PREDICTIONS_PATH, null, queries, new Callback<List<MITShuttlePredictionWrapper.Predictions>>() {
            @Override
            public void success(List<MITShuttlePredictionWrapper.Predictions> mitShuttlePredictionWrapper, Response response) {
                Timber.d("Success");
                Toast.makeText(ShuttlesActivity.this, "Finished", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e(error, "Failed");
            }
        });*/

        /*HashMap<String, String> pathParams = new HashMap<>();
        pathParams.put("route", "tech");
        pathParams.put("stop", "kendsq_d");

        apiClient.get(Constants.SHUTTLES, "/shuttles/routes/{route}/stops/{stop}", pathParams, null, new Callback<MITShuttleStopWrapper>() {
            @Override
            public void success(MITShuttleStopWrapper mitShuttleStopWrapper, Response response) {
                Log.d("ZZZ", "onResume");
                Log.d("ZZZ", "onResume");

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("ZZZ", "onResume");
                Log.d("ZZZ", "onResume");

            }
        });*/

        /*apiClient.get(Constants.SHUTTLES, "/shuttles/routes", null, null, new Callback<List<MITShuttleRouteWrapper>>() {
            @Override
            public void success(List<MITShuttleRouteWrapper> mitShuttleRouteWrappers, Response response) {
                Log.d("ZZZ", "onResume");
                Toast.makeText(ShuttlesActivity.this, "Finished", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("ZZZ", "onResume");
                Log.d("ZZZ", "onResume");
            }
        });*/

        /*HashMap<String, String> pathParams = new HashMap<>();
        pathParams.put("route", "tech");

        apiClient.get(Constants.SHUTTLES, "/shuttles/routes/{route}", pathParams, null, new Callback<MITShuttleRouteWrapper>() {
            @Override
            public void success(MITShuttleRouteWrapper mitShuttleRouteWrapper, Response response) {
                Log.d("ZZZ", "onResume");
                Log.d("ZZZ", "onResume");

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("ZZZ", "onResume");
                Log.d("ZZZ", "onResume");

            }
        });*/


    }

}

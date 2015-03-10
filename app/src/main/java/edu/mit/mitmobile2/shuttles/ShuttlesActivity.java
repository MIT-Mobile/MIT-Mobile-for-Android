package edu.mit.mitmobile2.shuttles;

import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.model.MITShuttlePredictionWrapper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

import android.os.Bundle;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

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
                Toast.makeText(ShuttlesActivity.this, "Finished", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e(error, TAG);
            }
        });*/


    }

}

package edu.mit.mitmobile2.shuttles;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRouteWrapper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class ShuttlesActivity extends MITModuleActivity {

    int contentLayoutId = R.layout.content_shuttles;
    private MITShuttleRouteWrapper data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setContentLayoutId(R.layout.content_shuttles);
        super.onCreate(savedInstanceState);


        TextView b = (TextView) findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShuttlesActivity.this, ShuttleStopsActivity.class);
                intent.putExtra("A", data);
                startActivity(intent);
            }
        });

        /**
         * Samples of how to use the API Call with Retrofit
         */

//        setTitle(getString(R.string.title_activity_shuttles));

        HashMap<String, String> paths = new HashMap<>();
        paths.put("route", "tech");

        apiClient.get(Constants.SHUTTLES, Constants.Shuttles.ROUTE_INFO_PATH, paths, null, new Callback<MITShuttleRouteWrapper>() {
            @Override
            public void success(MITShuttleRouteWrapper mitShuttleRouteWrapper, Response response) {
                Timber.d("Success");
                Toast.makeText(ShuttlesActivity.this, "Finished", Toast.LENGTH_SHORT).show();
                data = mitShuttleRouteWrapper;
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e(error, "Failed");
            }
        });
    }

}

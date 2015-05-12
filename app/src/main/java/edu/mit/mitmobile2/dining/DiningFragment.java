package edu.mit.mitmobile2.dining;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.model.MITDiningRetailVenue;
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DiningFragment extends Fragment {

    public DiningFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_dining, null);

        final Intent intent = new Intent(getActivity(), DiningRetailActivity.class);

        TextView tv = (TextView) view.findViewById(R.id.module_title);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        MITAPIClient mitApiClient = new MITAPIClient(getActivity());
        mitApiClient.get(Constants.DINING, Constants.Dining.DINING_RETAIL_PATH, null, null, new Callback<List<MITDiningRetailVenue>>() {
            @Override
            public void success(List<MITDiningRetailVenue> mitDiningRetailVenues, Response response) {
                LoggingManager.Timber.d("Success!");
                Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
                intent.putExtra(Constants.DINING_VENUE_KEY, mitDiningRetailVenues.get(10));
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
        return view;
    }
}

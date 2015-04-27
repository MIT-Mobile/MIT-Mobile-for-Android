package edu.mit.mitmobile2.events.fragment;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.events.EventManager;
import edu.mit.mitmobile2.events.model.MITCalendar;
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EventsFragment extends Fragment {
public static final String TAG = "EventsFragment";


    public EventsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_events, null);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        EventManager.getCalendars(getActivity(), new Callback<List<MITCalendar>>() {
            @Override
            public void success(List<MITCalendar> mitCalendars, Response response) {
                LoggingManager.Log.d(TAG, "MITCalendar => "+mitCalendars+ " => "+response);
                LoggingManager.Log.d(TAG, "MITCalendar => "+mitCalendars+ " => "+response);

                gotCalendars(mitCalendars);
            }

            @Override
            public void failure(RetrofitError error) {
                LoggingManager.Log.d(TAG, "ERROR => "+error);
            }
        });
    }

    private void gotCalendars(List<MITCalendar> mitCalendars) {

        MITCalendar lastOne = mitCalendars.get(mitCalendars.size() - 1);

        EventManager.getCalendarDetail(getActivity(), lastOne, new Callback<MITCalendar>() {
            @Override
            public void success(MITCalendar mitCalendar, Response response) {
                LoggingManager.Log.d(TAG, "MITCalendar => "+mitCalendar+ " => "+response);
                LoggingManager.Log.d(TAG, "MITCalendar => "+mitCalendar+ " => "+response);

            }

            @Override
            public void failure(RetrofitError error) {
                LoggingManager.Log.d(TAG, "ERROR => "+error);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}

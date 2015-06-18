package edu.mit.mitmobile2.facilities.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITMainActivity;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.FacilitiesManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProblemTypesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String PROBLEM_KEY = "problem";

    private List<String> problemTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_types);

        setTitle(getString(R.string.problem_query));

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        if (savedInstanceState != null && savedInstanceState.containsKey(PROBLEM_KEY)) {
            problemTypes = savedInstanceState.getStringArrayList(PROBLEM_KEY);
            adapter.addAll(problemTypes);
        } else {
            FacilitiesManager.getProblemTypes(this, new Callback<List<String>>() {
                @Override
                public void success(List<String> strings, Response response) {
                    problemTypes = strings;
                    adapter.addAll(problemTypes);
                }

                @Override
                public void failure(RetrofitError error) {
                    MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
                }
            });
        }

        ListView listView = (ListView) findViewById(R.id.problem_types_listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList(PROBLEM_KEY, (ArrayList) problemTypes);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String problem = (String) parent.getItemAtPosition(position);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.FACILITIES_PROBLEM_TYPE, problem);
        editor.commit();
        Intent intent = new Intent(this, MITMainActivity.class);
        startActivity(intent);
    }
}

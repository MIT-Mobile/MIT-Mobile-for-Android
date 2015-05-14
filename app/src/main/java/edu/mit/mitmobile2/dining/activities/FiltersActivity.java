package edu.mit.mitmobile2.dining.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import edu.mit.mitmobile2.R;

/**
 * Created by philipcorriveau on 5/12/15.
 */
public class FiltersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
    }

    public void onFilterRowClick(View view) {
        //TODO: Some kind of saving of selected filters
        ImageView checkImage = (ImageView) view.findViewById(R.id.checkbox);
        checkImage.setVisibility(checkImage.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }
}

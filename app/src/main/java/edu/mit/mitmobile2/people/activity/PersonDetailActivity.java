package edu.mit.mitmobile2.people.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.people.fragment.PersonDetailFragment;

public class PersonDetailActivity extends AppCompatActivity {
    public static final String PERSON_KEY = "6A3F6D22-78C0-40F6-8AB9-4D66282AD8DE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_person_detail);
        if (savedInstanceState == null) {
            PersonDetailFragment pdf = new PersonDetailFragment();
            pdf.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction()
                    .add(R.id.container, pdf)
                    .commit();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

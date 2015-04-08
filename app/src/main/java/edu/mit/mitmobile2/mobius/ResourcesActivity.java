package edu.mit.mitmobile2.mobius;

import edu.mit.mitmobile2.APIJsonResponse;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.mit.mitmobile2.maps.MapsActivity;

public class ResourcesActivity extends MITModuleActivity {

    Button btnResourceShop;
    Button btnResourceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        this.hasSearch = true;
        this.setContentLayoutId(R.layout.content_resources);
        super.onCreate(savedInstanceState);
        Log.d("ZZZ","tag = " + TAG);
        Log.d("ZZZ","Resource Activity");

        btnResourceShop = (Button)findViewById(R.id.btnResourceShop);

        btnResourceShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        btnResourceType = (Button)findViewById(R.id.btnResourceType);
    }

}

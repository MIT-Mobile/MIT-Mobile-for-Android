package edu.mit.mitmobile2.mobius;

import edu.mit.mitmobile2.MITMainActivity;
import edu.mit.mitmobile2.R;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ResourcesActivity extends MITMainActivity {

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

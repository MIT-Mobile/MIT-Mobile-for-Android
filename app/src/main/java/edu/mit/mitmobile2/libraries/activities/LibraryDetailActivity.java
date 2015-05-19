package edu.mit.mitmobile2.libraries.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import edu.mit.mitmobile2.R;

/**
 * Created by serg on 5/19/15.
 */
public class LibraryDetailActivity extends AppCompatActivity {

    private TextView textViewPrice;
    private TextView textViewTitle;
    private TextView textViewDescription;
    private ImageView imageViewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_detail);

        textViewPrice = (TextView) findViewById(R.id.library_detail_tv_price);
        textViewTitle = (TextView) findViewById(R.id.library_detail_tv_title);
        textViewDescription = (TextView) findViewById(R.id.library_detail_tv_description);
        imageViewImage = (ImageView) findViewById(R.id.library_detail_iv_image);
    }
}

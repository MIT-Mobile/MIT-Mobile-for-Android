package edu.mit.mitmobile2.libraries.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.libraries.model.MITLibrariesMITFineItem;
import edu.mit.mitmobile2.libraries.model.MITLibrariesMITItem;
import edu.mit.mitmobile2.libraries.model.MITLibrariesMITLoanItem;

public class LibraryAccountItemDetailActivity extends AppCompatActivity {

    private static final String KEY_STATE_LIBRARY_ITEM = "state_library_item";

    private TextView textViewPrice;
    private TextView textViewTitle;
    private TextView textViewDescription;
    private ImageView imageViewImage;

    private MITLibrariesMITItem librariesMITItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_account_item_detail);

        textViewPrice = (TextView) findViewById(R.id.library_detail_tv_price);
        textViewTitle = (TextView) findViewById(R.id.library_detail_tv_title);
        textViewDescription = (TextView) findViewById(R.id.library_detail_tv_description);
        imageViewImage = (ImageView) findViewById(R.id.library_detail_iv_image);

        librariesMITItem = getIntent().getParcelableExtra(Constants.ACCOUNT_ITEM_KEY);

        updateUiElements(librariesMITItem);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_STATE_LIBRARY_ITEM)) {
                librariesMITItem = savedInstanceState.getParcelable(KEY_STATE_LIBRARY_ITEM);
            }
        } else {
            // TODO: get librariesMITItem from extras or API call + remove fake
            /*librariesMITItem = new MITLibrariesMITItem();
            librariesMITItem.setTitle("Title");
            librariesMITItem.setMaterial("Format");
            librariesMITItem.setImprint("Imprint");
            librariesMITItem.setIsbn("isbn");*/
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putParcelable(KEY_STATE_LIBRARY_ITEM, librariesMITItem);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void updateUiElements(MITLibrariesMITItem item) {

        textViewTitle.setText(item.getTitle());

        String description = "";
        if (TextUtils.isEmpty(item.getAuthor())) {
            description = item.getYear();
        } else {
            description = String.format("%s; %s", item.getYear(), item.getAuthor());
        }

        textViewDescription.setText(description);
    }

    private void updateUiElements(MITLibrariesMITFineItem item) {
        textViewPrice.setText(item.getFormattedAmount());
    }

    private void updateUiElements(MITLibrariesMITLoanItem item) {
        // TODO:
    }
}

package edu.mit.mitmobile2.libraries.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
    private LinearLayout linearLayoutDetailContent;

    private MITLibrariesMITItem librariesMITItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_account_item_detail);

        textViewPrice = (TextView) findViewById(R.id.library_detail_tv_price);
        textViewTitle = (TextView) findViewById(R.id.library_detail_tv_title);
        textViewDescription = (TextView) findViewById(R.id.library_detail_tv_description);
        imageViewImage = (ImageView) findViewById(R.id.library_detail_iv_image);
        linearLayoutDetailContent = (LinearLayout) findViewById(R.id.library_detail_ll_detail_content);

        librariesMITItem = getIntent().getParcelableExtra(Constants.ACCOUNT_ITEM_KEY);

        updateUiElements(librariesMITItem);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_STATE_LIBRARY_ITEM)) {
                librariesMITItem = savedInstanceState.getParcelable(KEY_STATE_LIBRARY_ITEM);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putParcelable(KEY_STATE_LIBRARY_ITEM, librariesMITItem);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void updateUiElements(MITLibrariesMITItem item) {

        setTitle(item.getTitle());

        if (item.getCoverImages() != null && item.getCoverImages().size() > 0) {
            String url = item.getCoverImages().get(0).getUrl();
            Picasso.with(getApplicationContext()).load(url).placeholder(R.drawable.grey_rect).into(imageViewImage);
        }

        textViewTitle.setText(item.getTitle());

        String description = "";
        if (TextUtils.isEmpty(item.getAuthor())) {
            description = item.getYear();
        } else {
            description = String.format("%s; %s", item.getYear(), item.getAuthor());
        }

        textViewDescription.setText(description);

        if (item instanceof MITLibrariesMITFineItem) {
            updateUiElements((MITLibrariesMITFineItem)item);
        } else if (item instanceof MITLibrariesMITLoanItem) {
            updateUiElements((MITLibrariesMITLoanItem)item);
        }

        // fill details UI here
        ArrayList<View> detailViews = new ArrayList<>();
        if (!TextUtils.isEmpty(item.getMaterial())) {
            detailViews.add(getItemDetailView(getString(R.string.library_item_detail_format), item.getMaterial()));
        }
        if (!TextUtils.isEmpty(item.getImprint())) {
            detailViews.add(getItemDetailView(getString(R.string.library_item_detail_publisher), item.getImprint()));
        }
        if (!TextUtils.isEmpty(item.getIsbn())) {
            detailViews.add(getItemDetailView(getString(R.string.library_item_detail_isbn), item.getIsbn()));
        }

        linearLayoutDetailContent.removeAllViews();
        for (View view : detailViews) {
            linearLayoutDetailContent.addView(getItemDetailDividerView());
            linearLayoutDetailContent.addView(view);
        }
    }

    private View getItemDetailView(String title, String description) {
        View view = getLayoutInflater().inflate(R.layout.row_library_account_item_detail, null);

        TextView textViewTitle = (TextView) view.findViewById(R.id.library_item_detail_tv_title);
        TextView textViewDescription = (TextView) view.findViewById(R.id.library_item_detail_tv_description);

        textViewTitle.setText(title);
        textViewDescription.setText(description);

        return view;
    }

    private View getItemDetailDividerView() {
        return getLayoutInflater().inflate(R.layout.view_library_account_item_detail_divider, null);
    }

    private void updateUiElements(MITLibrariesMITFineItem item) {
        textViewPrice.setText(item.getFormattedAmount());
    }

    private void updateUiElements(MITLibrariesMITLoanItem item) {
        if (item.isOverdue()) {
            textViewPrice.setTextColor(getResources().getColor(R.color.statusbar_red));
            textViewPrice.setText(item.getDueText());
        } else {
            textViewPrice.setTextColor(getResources().getColor(R.color.mit_grey));
            textViewPrice.setCompoundDrawables(null, null, null, null);
        }
    }
}

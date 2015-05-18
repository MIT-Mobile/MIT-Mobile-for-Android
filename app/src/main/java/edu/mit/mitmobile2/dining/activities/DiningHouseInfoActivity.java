package edu.mit.mitmobile2.dining.activities;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.model.MITDiningHouseVenue;

public class DiningHouseInfoActivity extends AppCompatActivity {

    @InjectView(R.id.house_image_view)
    ImageView houseImageView;
    @InjectView(R.id.house_name_text_view)
    TextView houseNameTextView;
    @InjectView(R.id.house_status_text_view)
    TextView houseStatusTextView;
    @InjectView(R.id.payment_text_view)
    TextView paymentTextView;
    @InjectView(R.id.location_text_view)
    TextView locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining_house_info);
        ButterKnife.inject(this);

        MITDiningHouseVenue venue = getIntent().getParcelableExtra(Constants.Dining.HOUSE_INFO);
        String houseStatus = getIntent().getStringExtra(Constants.Dining.HOUSE_STATUS);

        setTitle(venue.getShortName());

        houseNameTextView.setText(venue.getName());
        locationTextView.setText(venue.getLocation().getLocationDescription());

        String paymentStr = "";
        for (int i = 0; i < venue.getPayment().size(); i++) {
            if (i < venue.getPayment().size() - 1) {
                paymentStr = paymentStr + venue.getPayment().get(i) + ", ";
            } else {
                paymentStr = paymentStr + venue.getPayment().get(i);
            }
        }
        paymentTextView.setText(paymentStr);

        if (houseStatus.contains("until")) {
            houseStatusTextView.setTextColor(getResources().getColor(R.color.status_green));
        } else if (houseStatus.contains("at") || houseStatus.contains("Closed")) {
            houseStatusTextView.setTextColor(getResources().getColor(R.color.status_red));
        }
        houseStatusTextView.setText(houseStatus);


        try {
            Picasso.with(this).load(venue.getIconURL()).placeholder(R.drawable.grey_rect).into(houseImageView);
        } catch (NullPointerException e) {
            Picasso.with(this).load(R.drawable.grey_rect).placeholder(R.drawable.grey_rect).into(houseImageView);
        }
    }
}

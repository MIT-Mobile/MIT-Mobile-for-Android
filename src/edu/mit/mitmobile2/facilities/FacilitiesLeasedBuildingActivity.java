package edu.mit.mitmobile2.facilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.DividerView;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationRecord;

public class FacilitiesLeasedBuildingActivity extends ModuleActivity {

	private final static String CONTACT_EMAIL_KEY = "contact_email";
	private final static String CONTACT_NAME_KEY = "contact_name";
	private final static String CONTACT_PHONE_KEY = "contact_phone";
	
	private String mEmail;
	private String mName;
	private String mPhone;
	
	private Context mContext;
	
	public static void launch(Context context, LocationRecord location) {
		Intent intent = new Intent(context, FacilitiesLeasedBuildingActivity.class);
		intent.putExtra(CONTACT_EMAIL_KEY, location.contact_email_bldg_services);
		intent.putExtra(CONTACT_NAME_KEY, location.contact_name_bldg_services);
		intent.putExtra(CONTACT_PHONE_KEY, location.contact_phone_bldg_services);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		mContext = this;
		mEmail = getIntent().getStringExtra(CONTACT_EMAIL_KEY);
		mName = getIntent().getStringExtra(CONTACT_NAME_KEY);
		mPhone = getIntent().getStringExtra(CONTACT_PHONE_KEY);
		
		setContentView(R.layout.facilities_leased_building);
		String message = "The Department of Facilities is not responsible for the maintenance of";
		message += " " + Global.sharedData.getFacilitiesData().getBuildingNumber();
		message += " - " + Global.sharedData.getFacilitiesData().getLocationName() + ". ";
		message += "Please contact " + mName + " to report any issues.";
		TextView maintainerMessageTV = (TextView) findViewById(R.id.facilitiesLeasedTV);
		maintainerMessageTV.setText(message);
		
		LinearLayout mainLinearLayout = (LinearLayout) findViewById(R.id.facilitiesLeasedMainLinearLayout);
		if(mEmail.length() > 0) {
			TwoLineActionRow emailActionRow = new TwoLineActionRow(this);
			emailActionRow.setBackgroundColor(getResources().getColor(R.color.rowBackground));
			emailActionRow.setActionIconResource(R.drawable.action_email);
			emailActionRow.setTitle("Email (" + mEmail + ")");
			emailActionRow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CommonActions.composeEmail(mContext, mEmail);
					
				}
			});
			
			mainLinearLayout.addView(new DividerView(this, null));
			mainLinearLayout.addView(emailActionRow);
		}
		
		if(mPhone.length() > 0) {
			TwoLineActionRow phoneActionRow = new TwoLineActionRow(this);
			phoneActionRow.setBackgroundColor(getResources().getColor(R.color.rowBackground));
			phoneActionRow.setActionIconResource(R.drawable.action_phone);
			phoneActionRow.setTitle("Call (" + mPhone + ")");
			phoneActionRow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CommonActions.callPhone(mContext, mPhone);
				}
			});
			
			mainLinearLayout.addView(new DividerView(this, null));
			mainLinearLayout.addView(phoneActionRow);
		}
		
		
	}
	
	@Override
	protected Module getModule() {
		return new FacilitiesModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		
	}

}

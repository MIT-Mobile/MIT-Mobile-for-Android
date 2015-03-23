package edu.mit.mitmobile2.facilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationRecord;

public class FacilitiesLeasedBuildingActivity extends NewModuleActivity {

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
		addSecondaryTitle("Where is it?");
		
		String message = "The Department of Facilities is not responsible for the maintenance of";
		message += " " + Global.sharedData.getFacilitiesData().getBuildingNumber();
		message += " - " + Global.sharedData.getFacilitiesData().getLocationName() + ". ";
		message += "Please contact " + mName + " to report any issues.";
		TextView maintainerMessageTV = (TextView) findViewById(R.id.facilitiesLeasedTV);
		maintainerMessageTV.setText(message);
		
		if(mEmail.length() > 0) {
			View emailActionContainer = findViewById(R.id.facilitiesLeasedEmailContainer);
			emailActionContainer.setVisibility(View.VISIBLE);
			TwoLineActionRow emailActionRow = (TwoLineActionRow) findViewById(R.id.facilitiesLeasedEmailActionRow);
			emailActionRow.setActionIconResource(R.drawable.action_email);
			emailActionRow.setTitle("Email (" + mEmail + ")");
			emailActionRow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CommonActions.composeEmail(mContext, mEmail);
					
				}
			});
			
		}
		
		if(mPhone.length() > 0) {
			View phoneActionContainer = findViewById(R.id.facilitiesLeasedPhoneContainer);
			phoneActionContainer.setVisibility(View.VISIBLE);
			TwoLineActionRow phoneActionRow = (TwoLineActionRow) findViewById(R.id.facilitiesLeasedPhoneActionRow);
			phoneActionRow.setActionIconResource(R.drawable.action_phone);
			String dotDelimitedNumber = mPhone.substring(0, 3) + "." + mPhone.substring(3, 6) + "." + mPhone.substring(6);
			phoneActionRow.setTitle("Call (" + dotDelimitedNumber + ")");
			phoneActionRow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CommonActions.callPhone(mContext, mPhone);
				}
			});
		}
	}
	
	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected NewModule getNewModule() {
		// TODO Auto-generated method stub
		return new FacilitiesModule();
	}

	@Override
	protected boolean isScrollable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}

}

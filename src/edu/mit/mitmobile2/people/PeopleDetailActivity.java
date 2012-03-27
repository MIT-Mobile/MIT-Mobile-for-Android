package edu.mit.mitmobile2.people;

import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Intents.Insert;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.SliderNewModuleActivity;
import edu.mit.mitmobile2.objs.PersonItem;
import edu.mit.mitmobile2.objs.PersonItem.PersonDetailItem;
import edu.mit.mitmobile2.objs.PersonItem.PersonDetailViewMode;


public class PeopleDetailActivity extends SliderNewModuleActivity {
	
	public static final String UID_KEY = "uid";
	public static final String SEARCH_TERM_KEY = "search_term";
	public static final String RECENTLY_VIEWED_FLAG = "show_recents";
	
	private List<PersonItem> mPeople = Collections.emptyList();
	
	private PersonItem mPersonToAddToContacts = null;
	
	private final static int EDIT_CONTACT = 0;
	private final static int EDIT_CONTACT_REQUEST = 1;
	private final static String EDIT_CONTACT_TEXT = "Add to existing";
	
	private final static int NEW_CONTACT = 1;
	private final static String NEW_CONTACT_TEXT = "Create new contact";
	
	private Context mContext;
	
	static void launchActivity(Context context, PersonItem item, int viewMode, String extras) {
		// load the activity that shows all the detail search results
		Intent intent = new Intent(context, PeopleDetailActivity.class);
		if(viewMode == PersonDetailViewMode.SEARCH) {
			intent.putExtra(PeopleDetailActivity.SEARCH_TERM_KEY, extras);
		} else {
			intent.putExtra(PeopleDetailActivity.RECENTLY_VIEWED_FLAG, true);
		}
		intent.putExtra(PeopleDetailActivity.UID_KEY, item.uid);
		PeopleModel.markAsRecentlyViewed(item, context);
		context.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			List<PersonItem> people = null;
			if(extras.containsKey(SEARCH_TERM_KEY)) {
				String searchTerm = extras.getString(SEARCH_TERM_KEY);
				people = PeopleModel.executeLocalSearch(searchTerm);
			} else if(extras.containsKey(RECENTLY_VIEWED_FLAG)) {
				people = PeopleModel.getRecentlyViewed(this);
			}
		
			// check if results flushed from memory
			if (people == null) {
				finish();
				return;
			}
			
			final String uid = extras.getString(UID_KEY);
			
			setPeople(people, uid); 
		} 
	}
	
	private void setPeople(List<PersonItem> people, String uid) {
		mPeople = people;
		int totalPersons = mPeople.size();
		
		for(int index = 0; index < totalPersons; ++index) {
			PersonItem person = mPeople.get(index);
			String headerTitle = Integer.toString(index+1) + " of " + Integer.toString(totalPersons);
			addScreen(new PersonSliderInterface(person), person.getName(), headerTitle);
		}
		
		int position = PeopleModel.getPosition(mPeople, uid);
		setPosition(position);
	}
	
	private void populateAddContactIntent(Intent intent, PersonItem person, boolean editOrInsert) {
		// the data that the add contact screen accepts does not quite match
		// the structure of our data, so we do a reasonably good fit between the two
		
		// puts the full name in the firstname field (this is a limitation of android see link below)
		// http://groups.google.com/group/android-developers/browse_thread/thread/39615ba0bbcfc62b
		if(!editOrInsert) {
			intent.putExtra(Insert.NAME, person.getName());
		}
		
		addFields(intent, person.phone, 
			new String[] {Insert.PHONE, Insert.SECONDARY_PHONE, Insert.TERTIARY_PHONE}, 
			new String[] {Insert.PHONE_TYPE, Insert.SECONDARY_PHONE_TYPE, Insert.TERTIARY_PHONE_TYPE},	
			CommonDataKinds.Phone.TYPE_WORK);

		addFields(intent, person.email,
			new String[] {Insert.EMAIL, Insert.SECONDARY_EMAIL, Insert.TERTIARY_EMAIL},
			new String[] {Insert.EMAIL_TYPE, Insert.SECONDARY_EMAIL_TYPE, Insert.TERTIARY_EMAIL_TYPE},
			CommonDataKinds.Email.TYPE_WORK);
		
		addField(intent, Insert.COMPANY, person.dept);
		addField(intent, Insert.JOB_TITLE, person.title);
		
		addField(intent, Insert.POSTAL, person.office);
		if(!person.office.isEmpty()) {
			intent.putExtra(Insert.POSTAL_TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK);
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != 0) {
			Cursor result = getContentResolver().query(data.getData(), new String[] {ContactsContract.Contacts._ID}, null, null, null);
			result.moveToFirst();
			int index = result.getColumnIndex(ContactsContract.Contacts._ID);
			long id = result.getLong(index);
			result.close();

			Uri myPerson = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
			Intent intent = new Intent(Intent.ACTION_EDIT, myPerson);
			populateAddContactIntent(intent, mPersonToAddToContacts, true);
			startActivity(intent);
		}
	}
	
	private void addField(Intent intent, String extraField, List<String> values) {
		if(!values.isEmpty()) {
			intent.putExtra(extraField, values.get(0));
		}
	}
	
	private void addFields(Intent intent, List<String> values, String[] fields, String[] fieldTypes, int fieldType) {
		
		for(int i = 0; i < 3; i++) {
			if(values.size() > i) {
				intent.putExtra(fields[i], values.get(0));
				intent.putExtra(fieldTypes[i], fieldType);
			}
		}
	}
	
	private void addToContact() {
		final PersonItem person = mPeople.get(getPosition());
		// create a dialog asking to create or add contact
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add Contact");
		builder.setItems(new String[] {EDIT_CONTACT_TEXT, NEW_CONTACT_TEXT}, 
			new DialogInterface.OnClickListener() {
			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(which == EDIT_CONTACT) {
						Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
						mPersonToAddToContacts = person;
						PeopleDetailActivity.this.startActivityForResult(intent, EDIT_CONTACT_REQUEST);
					} else if(which == NEW_CONTACT) {
						Intent intent = new Intent(Insert.ACTION, ContactsContract.Contacts.CONTENT_URI);
						populateAddContactIntent(intent, person, false);
						PeopleDetailActivity.this.startActivity(intent);
					}
				}
			}
		);

		builder.setNegativeButton("Cancel", null);
		builder.create().show();
	}
	
	private class PersonSliderInterface implements SliderInterface {
		private PersonItem mPerson;
		private LinearLayout mMainLayout;
		
		PersonSliderInterface(PersonItem person) {
			mPerson = person;
		}
		
		@Override
		public void onSelected() {
			PeopleModel.markAsRecentlyViewed(mPerson, mContext);
		}
	
		
		@Override
		public View getView() {
			LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mMainLayout = (LinearLayout) inflator.inflate(R.layout.people_detail, null);
			
			TextView nameView = (TextView) mMainLayout.findViewById(R.id.personName);
			nameView.setText(mPerson.getName());
			
			ImageButton addContactBtn = (ImageButton) mMainLayout.findViewById(R.id.personAddButton);
			addContactBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					addToContact();
				}
			});
			
			TextView titleView = (TextView) mMainLayout.findViewById(R.id.personTitle);
			if(mPerson.getTitle() != null) {
				titleView.setText(mPerson.getTitle());
			} else {
				titleView.setVisibility(View.GONE);
			}
			
			List<PersonDetailItem> detailItems = mPerson.getPersonDetails();
			
			for (final PersonDetailItem item : detailItems) {
				PeopleDetailItemLayout itemLayout = new PeopleDetailItemLayout(PeopleDetailActivity.this);
				itemLayout.setContentType(item.getType());
				itemLayout.setContentValue(item.getValue());
				
				String type = item.getType();
				if(type.equals("email")) {
					itemLayout.setActionIconResouce(R.drawable.action_email);					
				} else if(type.equals("phone")) {
					itemLayout.setActionIconResouce(R.drawable.action_phone);
				} else if(type.equals("office")) {
					itemLayout.setActionIconResouce(R.drawable.action_map);
				} else {
					// not one of the actionable types of data
					itemLayout.hideActionIcon();
				}
				
				itemLayout.setOnItemClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(item.getType().equals("email")) {
							CommonActions.composeEmail(ctx, item.getValue());
						} else if(item.getType().equals("phone")) {
							CommonActions.callPhone(ctx, item.getValue());
						} else if(item.getType().equals("office")) {
							CommonActions.searchMap(ctx, item.getValue());
						}
					}
				});
				mMainLayout.addView(itemLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
			return mMainLayout;
		}
		
		@Override
		public void updateView() {
			// all the populating of view is done in the constructor
		}


		@Override
		public LockingScrollView getVerticalScrollView() {
			return null;
		}


		@Override
		public void onDestroy() {
			// TODO Auto-generated method stub
			
		}
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected NewModule getNewModule() {
		// TODO Auto-generated method stub
		return new PeopleModule();
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

package edu.mit.mitmobile2.emergency;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.emergency.EmergencyDB.ContactsTable;
import edu.mit.mitmobile2.objs.EmergencyItem.Contact;

import android.content.Context;
import android.database.Cursor;
import android.text.Spannable;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

class EmergencyContactsAdapter extends CursorAdapter {

	private Context mContext;
	private TextAppearanceSpan mContactStyle;
	private TextAppearanceSpan mPhoneStyle;
	private int mListItemPrimaryPadding;
	
	public EmergencyContactsAdapter(Context context, Cursor cursor) {
		super(context, cursor);
		
		mContext = context;
		mContactStyle = new TextAppearanceSpan(mContext, R.style.ListItemPrimary);
		mPhoneStyle = new TextAppearanceSpan(mContext, R.style.ListItemSecondary);
		mListItemPrimaryPadding = context.getResources().getDimensionPixelOffset(R.dimen.ListItemPrimaryPadding);
		
	}
	
	private void setupRow(Cursor cursor, View row) {
		Contact contact = new Contact();
		contact.contact = cursor.getString(cursor.getColumnIndex(ContactsTable.CONTACT_NAME));
		contact.phone = cursor.getString(cursor.getColumnIndex(ContactsTable.CONTACT_PHONE));
		contact.description = cursor.getString(cursor.getColumnIndex(ContactsTable.CONTACT_DESCRIPTION));
		
		TextView contactTV = (TextView) row.findViewById(R.id.emergencyRowTV);
		String phone = String.format("(%s.%s.%s)",
				contact.phone.substring(0, 3),
				contact.phone.substring(3, 6),
				contact.phone.substring(6, 10));
		String namePlusPhone = contact.contact + " " + phone;
		
		contactTV.setText(namePlusPhone,  TextView.BufferType.SPANNABLE);
		Spannable spannable = (Spannable) contactTV.getText();
		
		int separator = contact.contact.length() + 1;
		spannable.setSpan(mContactStyle, 0,
				separator, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan(mPhoneStyle, separator,
				spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		TextView descTV = (TextView) row.findViewById(R.id.emergencyDescriptionTV);
		if (contact.description != null) {
			descTV.setText(contact.description);
			descTV.setVisibility(View.VISIBLE);
			contactTV.setPadding(0, contactTV.getPaddingTop(), 0, 0);
		} else {
			descTV.setVisibility(View.GONE);
			contactTV.setPadding(0, contactTV.getPaddingTop(), 0, mListItemPrimaryPadding);
		}
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		View row = view;
		setupRow(cursor, row);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.emergency_row, null);
		setupRow(cursor, row);
		return row;
	}
}

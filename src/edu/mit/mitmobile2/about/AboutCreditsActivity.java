package edu.mit.mitmobile2.about;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AboutCreditsActivity extends NewModuleActivity {
	
	private ListView mListView;
	private ArrayList<CreditsItem> mCreditsArray;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_credits);

		String[] strings = getResources().getStringArray(R.array.credits);
		mCreditsArray = new ArrayList<CreditsItem>();
		for (int i = 0; i < strings.length; i += 2) {
			CreditsItem item = new CreditsItem();
			item.role = strings[i];
			item.names = strings[i+1];
			mCreditsArray.add(item);
		}

		AboutCreditsAdapter adapter = new AboutCreditsAdapter(this, 0, mCreditsArray);
		mListView = (ListView) findViewById(R.id.aboutCreditsList);
		mListView.setAdapter(adapter);
	}

	private class CreditsItem {
		String role;
		String names;
	}
	
	private class AboutCreditsAdapter extends ArrayAdapter<CreditsItem> {
		
		private Context mContext;

		public AboutCreditsAdapter(Context context, int textViewResourceId, List<CreditsItem> objects) {
			super(context, textViewResourceId, objects);
			
			mContext = context;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.about_credits_row, null);
			}
			
			CreditsItem item = mCreditsArray.get(position);
			if (item != null) {
				TextView tv = (TextView) v.findViewById(R.id.creditsRoleTV);
				tv.setText(item.role);
				tv = (TextView) v.findViewById(R.id.creditsNamesTV);
				tv.setText(item.names);
			}

			return v;
		}
	}
	
	@Override
	protected NewModule getNewModule() {
		return new AboutModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
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

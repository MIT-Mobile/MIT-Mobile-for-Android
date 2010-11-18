package edu.mit.mitmobile.people;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import edu.mit.mitmobile.SimpleArrayAdapter;
import edu.mit.mitmobile.TwoLineActionRow;
import edu.mit.mitmobile.objs.PersonItem;

public class PeopleListAdapter extends SimpleArrayAdapter<PersonItem> {
	public PeopleListAdapter(Context context, List<PersonItem> items, int rowResourceId) {
		super(context, items, rowResourceId);
	}

	public void setLookupHandler(ListView listView, final int viewMode, final String extras) {
		setOnItemClickListener(listView,
				new SimpleArrayAdapter.OnItemClickListener<PersonItem>() {
					@Override
					public void onItemSelected(PersonItem item) {
						PeopleDetailActivity.launchActivity(getContext(), item, viewMode, extras);
					}
				}
		);
	}
	
	public void updateView(PersonItem person, View view) {			
		TwoLineActionRow twoLineActionRow = (TwoLineActionRow) view;
		twoLineActionRow.setTitle(person.getName());
		
		twoLineActionRow.setSubtitle(person.getTitle());
	}
}


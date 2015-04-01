package edu.mit.mitmobile2;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NavigationArrayAdapter extends ArrayAdapter<NavItem>  {

	public NavigationArrayAdapter(Context context, int resource,int textViewResourceId, List<NavItem> objects) {
		super(context, resource, textViewResourceId, objects);
		this.objects = (ArrayList<NavItem>)objects;
	}


	// declaring our ArrayList of items
	private ArrayList<NavItem> objects;
		

	@Override
	public View getView(int position, View convertView, ViewGroup parent){

		// assign the view we are converting to a local variable
		View v = convertView;

		// first check to see if the view is null. if so, we have to inflate it.
		// to inflate it basically means to render, or show, the view.
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.drawer_list_item, null);
		}

		NavItem i = objects.get(position);

		if (i != null) {

			// This is how you obtain a reference to the TextViews.
			// These TextViews are created in the XML files we defined.

			TextView mTextView  = (TextView) v.findViewById(R.id.navItemText);
			Log.d("ZZZ","icon = " + i.getMenuIcon());
			mTextView.setText(i.getLongName());
			mTextView.setCompoundDrawablesWithIntrinsicBounds(i.getMenuIcon(), 0, 0, 0);
		}

		// the view must be returned to our activity
		return v;

	}
}

package edu.mit.mitmobile2;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationArrayAdapter extends ArrayAdapter<NavItem> {

    private class ViewHolder {
        ImageView moduleIcon;
        TextView moduleText;
    }

    public NavigationArrayAdapter(Context context, int resource, int textViewResourceId, List<NavItem> objects) {
        super(context, resource, textViewResourceId, objects);
        this.objects = (ArrayList<NavItem>) objects;
    }


    // declaring our ArrayList of items
    private ArrayList<NavItem> objects;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.drawer_list_item, null);

            holder.moduleIcon = (ImageView) v.findViewById(R.id.module_icon);
            holder.moduleText = (TextView) v.findViewById(R.id.module_text);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        NavItem i = objects.get(position);

        if (i != null) {
            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.

            Log.d("ZZZ", "icon = " + i.getMenuIcon());

            holder.moduleText.setText(i.getShortName());
            holder.moduleIcon.setImageResource(i.getMenuIcon());
        }

        // the view must be returned to our activity
        return v;

    }
}

package edu.mit.mitmobile2.emergency.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.DrawableUtils;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.emergency.model.MITEmergencyInfoAnnouncement;
import edu.mit.mitmobile2.emergency.model.MITEmergencyInfoContact;


public class MITEmergencyContactsInfoAdapter extends MITEmergencyContactsAdapter {

    private static final int ROW_TYPE_ANNOUNCEMENT = 0;
    private static final int ROW_TYPE_CONTACT = 1;
    private static final int ROW_TYPE_SHOW_MORE = 2;

    private static final int ROW_TYPES_COUNT = 3;

    private MITEmergencyInfoAnnouncement announcement;

    public void updateAnnouncement(MITEmergencyInfoAnnouncement announcement) {
        this.announcement = announcement;

        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return ROW_TYPES_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)  {
            return ROW_TYPE_ANNOUNCEMENT;
        } else if (position <= people.size()) {
            return ROW_TYPE_CONTACT;
        } else {
            return ROW_TYPE_SHOW_MORE;
        }
    }

    @Override
    public int getCount() {
        return super.getCount() + 2; // + announcement and "More Emergency Contacts" cells
    }

    @Override
    public Object getItem(int position) {
        if (position == 0)  {
            return announcement;
        } else if (position <= people.size()) {
            return people.get(position - 1);
        } else {
            return null;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);

        switch (itemViewType) {
            case ROW_TYPE_ANNOUNCEMENT: {
                // TODO:
            }
            case ROW_TYPE_CONTACT: {
                return super.getView(position, convertView, parent);
            }
            case ROW_TYPE_SHOW_MORE: {
                // TODO:
            }
        }

        return null;
    }
}



package edu.mit.mitmobile2.emergency.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
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

    public static final int ROW_TYPE_ANNOUNCEMENT = 0;
    public static final int ROW_TYPE_CONTACT = 1;
    public static final int ROW_TYPE_SHOW_MORE = 2;

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
                ViewHolder viewHolder;
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(), R.layout.row_emergency_announcement, null);

                    viewHolder = new ViewHolder();
                    viewHolder.webViewAnnouncement = (WebView) convertView.findViewById(R.id.emergency_webview);
                    viewHolder.textViewPosted = (TextView) convertView.findViewById(R.id.emergency_tv_posted);

                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                MITEmergencyInfoAnnouncement announcement = (MITEmergencyInfoAnnouncement) getItem(position);

                if (announcement != null) {
                    String html = announcement.getAnnouncementHtml();
                    String mime = "text/html";
                    String encoding = "utf-8";

                    viewHolder.webViewAnnouncement.loadData(html, mime, encoding);
                    // M/d/y h:mm a zz
                    // viewHolder.textViewPosted.setText("stub");
                }
            }
            break;
            case ROW_TYPE_CONTACT: {
                convertView = super.getView(position, convertView, parent);
            }
            break;
            case ROW_TYPE_SHOW_MORE: {
                ViewHolder viewHolder;
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(), R.layout.row_emergency_show_more, null);

                    viewHolder = new ViewHolder();
                    viewHolder.textViewMoreContacts = (TextView) convertView.findViewById(R.id.emergency_tv_show_more_contacts);

                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
            }
            break;
        }

        return convertView;
    }

    class ViewHolder {
        // announcement
        WebView webViewAnnouncement;
        TextView textViewPosted;

        // more
        TextView textViewMoreContacts;
    }
}



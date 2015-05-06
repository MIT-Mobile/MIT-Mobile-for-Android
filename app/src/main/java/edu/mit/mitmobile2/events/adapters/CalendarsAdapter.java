package edu.mit.mitmobile2.events.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.events.model.MITCalendar;

/**
 * Created by serg on 4/28/15.
 */
public class CalendarsAdapter extends BaseExpandableListAdapter {

    private static final int ROW_TYPE_HEADER = 0;
    private static final int ROW_TYPE_GROUP = 1;
    private static final int ROW_TYPES_COUNT = 2;

    private Context context;

    private List<MITCalendar> mitCalendars;
    private List<MITCalendar> mitCalendarsRegistrar;
    private List<MITCalendar> mitCalendarsEvents;

    private int headerGroupPositionRegistrar = 0;
    private int headerGroupPositionEvents = 1;

    private MITCalendar checkedCalendar;

    public CalendarsAdapter(Context context, List<MITCalendar> mitCalendars) {
        this.context = context;
        this.mitCalendars = mitCalendars;
        mitCalendarsRegistrar = new ArrayList<>();
        mitCalendarsEvents = new ArrayList<>();
    }

    @Override
    public int getGroupTypeCount() {
        return ROW_TYPES_COUNT;
    }

    @Override
    public int getGroupType(int groupPosition) {
         if (groupPosition == headerGroupPositionRegistrar || groupPosition == headerGroupPositionEvents) {
             return ROW_TYPE_HEADER;
         } else {
             return ROW_TYPE_GROUP;
         }
    }

    @Override
    public int getGroupCount() {
        return mitCalendarsRegistrar.size() + mitCalendarsEvents.size() + 2;                    // 2 headers
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (getGroup(groupPosition) != null) {
            return getGroup(groupPosition).getCategories().size();
        }
        return 0;
    }

    @Override
    public MITCalendar getGroup(int groupPosition) {
        if (groupPosition > 0 && groupPosition < mitCalendarsRegistrar.size() + 1) {
            return mitCalendarsRegistrar.get(groupPosition - 1);                                // registrar header
        } else if (groupPosition > mitCalendarsRegistrar.size() + 1) {
            return mitCalendarsEvents.get(groupPosition - mitCalendarsRegistrar.size() - 2);    // registrar + events header
        }
        return null;
    }

    @Override
    public MITCalendar getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition).getCategories().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        int rowType = getGroupType(groupPosition);

        switch (rowType) {
            case ROW_TYPE_HEADER: {
                ViewHolder holder = new ViewHolder();
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(), R.layout.row_calendar_header, null);

                    holder.header = (TextView) convertView.findViewById(R.id.calendar_header_title);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.header.setText(groupPosition == headerGroupPositionRegistrar ? R.string.calendar_header_registrar : R.string.calendar_header_events);
            }
            break;
            case ROW_TYPE_GROUP: {
                MITCalendar calendar = getGroup(groupPosition);

                ViewHolder holder = new ViewHolder();
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(), R.layout.row_calendar_group, null);

                    holder.groupHeader = (CheckedTextView) convertView.findViewById(R.id.row_calendar_title);
                    holder.indicator = (ImageView) convertView.findViewById(R.id.row_calendar_indicator);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                boolean hasChildren = getChildrenCount(groupPosition) > 0;

                holder.groupHeader.setText(calendar.getName());
                holder.groupHeader.setChecked(isChecked(calendar));
                if (hasChildren) {
                    holder.indicator.setVisibility(View.VISIBLE);
                    holder.indicator.setRotation(isExpanded ? -90 : 90);
                } else {
                    holder.indicator.setVisibility(View.GONE);
                }
            }
            break;
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.row_calendar_child, null);

            holder.child = (CheckedTextView) convertView.findViewById(R.id.row_calendar_title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MITCalendar calendar = getChild(groupPosition, childPosition);

        holder.child.setText(calendar.getName());
        holder.child.setChecked(isChecked(calendar));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void notifyDataSetChanged() {
        mitCalendarsRegistrar.clear();
        mitCalendarsEvents.clear();

        for (MITCalendar calendar : mitCalendars) {
            if (calendar.getCategories() != null && calendar.getCategories().size() > 0) {
                if (mitCalendarsEvents.size() == 0) {
                    MITCalendar allEventsCategory = new MITCalendar();
                    allEventsCategory.setIdentifier(calendar.getIdentifier());
                    allEventsCategory.setName(context.getString(R.string.calendar_all_events));
                    mitCalendarsEvents.add(allEventsCategory);
                }
                mitCalendarsEvents.addAll(calendar.getCategories());
            } else {
                mitCalendarsRegistrar.add(calendar);
            }
        }

        headerGroupPositionEvents = mitCalendarsRegistrar.size() + 1; // 1 is for registrar header

        super.notifyDataSetChanged();
    }

    private boolean isChecked(MITCalendar calendar) {
        return checkedCalendar != null && calendar.equals(checkedCalendar);
    }

    public MITCalendar getCheckedCalendar() {
        return checkedCalendar;
    }

    public void setCheckedCalendar(MITCalendar checkedCalendar) {
        this.checkedCalendar = checkedCalendar;
        notifyDataSetInvalidated();
    }

    public boolean isCheckable(int groupPosition) {
        return getChildrenCount(groupPosition) == 0;
    }

    public boolean isCheckable(int groupPosition, int childPosition) {
        return true;
    }

    private class ViewHolder {
        // header
        private TextView header;

        // group
        private CheckedTextView groupHeader;
        private ImageView indicator;

        // child
        private CheckedTextView child;
    }
}

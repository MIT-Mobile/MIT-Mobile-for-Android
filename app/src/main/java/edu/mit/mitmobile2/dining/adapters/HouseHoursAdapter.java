package edu.mit.mitmobile2.dining.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.model.MITDiningMeal;
import edu.mit.mitmobile2.shared.logging.LoggingManager;

public class HouseHoursAdapter extends BaseAdapter {

    private List<MITDiningMeal> meals;
    private LayoutInflater listContainer;

    public HouseHoursAdapter(Context context, List<MITDiningMeal> meals) {
        this.listContainer = LayoutInflater.from(context);
        this.meals = meals;
    }

    @Override
    public int getCount() {
        return meals.size();
    }

    @Override
    public Object getItem(int position) {
        return meals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view != null) {
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = listContainer.inflate(R.layout.dining_house_hours_range_segment, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        MITDiningMeal meal = meals.get(position);

        viewHolder.nameTextView.setText(meal.getName());

        String startTime = DateFormat.format("h:mm a", formatMealTime(meal.getStartTimeString())).toString().toLowerCase();
        String endTime = DateFormat.format("h:mm a", formatMealTime(meal.getEndTimeString())).toString().toLowerCase();

        viewHolder.hoursTextView.setText(startTime + " - " + endTime);

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.meal_name_text_view)
        TextView nameTextView;
        @InjectView(R.id.meal_hours_text_view)
        TextView hoursTextView;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private Date formatMealTime(String timeString) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        try {
            date = format.parse(timeString);
        } catch (ParseException e) {
            LoggingManager.Timber.e(e, "___________DateFormatError___________");
        }
        return date;
    }

}

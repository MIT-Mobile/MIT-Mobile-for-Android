package edu.mit.mitmobile2.dining.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.model.MITDiningHouseDay;
import edu.mit.mitmobile2.dining.model.MITDiningHouseVenue;
import edu.mit.mitmobile2.dining.model.MITDiningMeal;
import edu.mit.mitmobile2.dining.model.MITDiningMenuItem;
import edu.mit.mitmobile2.dining.utils.DiningUtils;

public class HouseMenuRecyclerAdapter extends RecyclerView.Adapter<HouseMenuRecyclerAdapter.ViewHolder> {

    private static int LAYOUT_PARAMS = 40;
    private static int LAYOUT_MARGINS = 10;

    private Context context;
    private List<MITDiningHouseVenue> venues;
    private String mealName;
    private String mealDate;

    public HouseMenuRecyclerAdapter(Context context, List<MITDiningHouseVenue> venues, String mealName, String mealDate) {
        this.context = context;
        this.venues = venues;
        this.mealName = mealName;
        this.mealDate = mealDate;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView hoursTextView;
        public LinearLayout menuLayout;

        public ViewHolder(View view) {
            super(view);

            nameTextView = (TextView) view.findViewById(R.id.name_text_view);
            hoursTextView = (TextView) view.findViewById(R.id.hours_text_view);
            menuLayout = (LinearLayout) view.findViewById(R.id.menu_layout);
        }
    }

    public HouseMenuRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dining_house_menu, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MITDiningHouseVenue venue = venues.get(position);

        holder.nameTextView.setText(venue.getName());

        String startTime = "";
        String endTime = "";

        for (MITDiningHouseDay houseDay : venue.getMealsByDay()) {
            if (houseDay.getDateString().equals(mealDate)) {
                if (houseDay.getMeals() != null) {
                    for (MITDiningMeal meal : houseDay.getMeals()) {
                        if (meal.getName().equals(mealName)) {
                            if (meal.getStartTimeString().endsWith(":00:00")) {
                                startTime = DateFormat.format("h a", DiningUtils.formatMealTime(meal.getStartTimeString())).toString().toLowerCase();
                            } else {
                                startTime = DateFormat.format("h:mm a", DiningUtils.formatMealTime(meal.getStartTimeString())).toString().toLowerCase();
                            }
                            if (meal.getEndTimeString().endsWith(":00:00")) {
                                endTime = DateFormat.format("h a", DiningUtils.formatMealTime(meal.getEndTimeString())).toString().toLowerCase();
                            } else {
                                endTime = DateFormat.format("h:mm a", DiningUtils.formatMealTime(meal.getEndTimeString())).toString().toLowerCase();
                            }

                            if (meal.getItems() != null && meal.getItems().size() > 0) {
                                for (MITDiningMenuItem item : meal.getItems()) {
                                    buildAndAddMenuView(item, holder.menuLayout, true);
                                }
                            } else {
                                buildAndAddMenuView(null, holder.menuLayout, false);
                            }

                            break;
                        }
                    }
                }
            }
        }

        if (!startTime.isEmpty() && (!endTime.isEmpty())) {
            holder.hoursTextView.setText(startTime + " - " + endTime);
        } else {
            holder.hoursTextView.setText(context.getString(R.string.dining_day_closed));
        }
    }

    @Override
    public int getItemCount() {
        return venues.size();
    }

    private void buildAndAddMenuView(MITDiningMenuItem item, LinearLayout menuLayout, boolean hasMenu) {
        LinearLayout layout = (LinearLayout) View.inflate(context, R.layout.dining_house_menu_item, null);
        TextView mealNameTextView = (TextView) layout.findViewById(R.id.meal_name_text_view);
        TextView mealDescriptionTextView = (TextView) layout.findViewById(R.id.meal_description_text_view);
        LinearLayout imageLayout = (LinearLayout) layout.findViewById(R.id.image_layout);
        TextView noItemsTextView = (TextView) layout.findViewById(R.id.no_items_text_view);

        if (hasMenu) {
            noItemsTextView.setVisibility(View.GONE);
            mealNameTextView.setVisibility(View.VISIBLE);
            mealDescriptionTextView.setVisibility(View.VISIBLE);

            mealNameTextView.setText(item.getName());
            mealDescriptionTextView.setText(item.getItemDescription());

            if (item.getDietaryFlags() != null && item.getDietaryFlags().size() > 0) {
                for (String flagString : item.getDietaryFlags()) {
                    buildMenuImageView(flagString, imageLayout);
                }
            }
        } else {
            noItemsTextView.setVisibility(View.VISIBLE);
            mealNameTextView.setVisibility(View.GONE);
            mealDescriptionTextView.setVisibility(View.GONE);
        }

        menuLayout.addView(layout);
    }

    private void buildMenuImageView(String flagString, LinearLayout imageLayout) {
        RelativeLayout layout = (RelativeLayout) View.inflate(context, R.layout.dining_house_meal_dietary_flag, null);
        ImageView filterImageView = (ImageView) layout.findViewById(R.id.flag_image_view);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LAYOUT_PARAMS, LAYOUT_PARAMS);
        params.setMargins(0, LAYOUT_MARGINS, 0, 0);
        filterImageView.setLayoutParams(params);

        if (DiningUtils.getMenuDietaryFlagImage(context, flagString) > 0) {
            filterImageView.setImageResource(DiningUtils.getMenuDietaryFlagImage(context, flagString));
            imageLayout.addView(layout);
        }
    }
}

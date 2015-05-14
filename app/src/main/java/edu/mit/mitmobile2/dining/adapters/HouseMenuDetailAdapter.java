package edu.mit.mitmobile2.dining.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.R;

public class HouseMenuDetailAdapter extends BaseAdapter{

    private LayoutInflater listContainer;
    private Context context;
    private List<String> testData;

    public HouseMenuDetailAdapter(Context context, List<String> testData) {
        listContainer = LayoutInflater.from(context);
        this.context = context;
        this.testData = testData;
    }

    @Override
    public int getCount() {
        return testData.size();
    }

    @Override
    public Object getItem(int position) {
        return testData.get(position);
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
            view = listContainer.inflate(R.layout.row_dining_house_menu, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        viewHolder.menuTypeTextView.setText("BOWLS");
        viewHolder.menuNameTextView.setText("Chicken in peanut hoisin sauce");
        viewHolder.menuDetailTextView.setText("rice\npasta");
        viewHolder.menuImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.dining_meal_farm_to_fork_big));

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.menu_type_text_view)
        TextView menuTypeTextView;
        @InjectView(R.id.menu_name_text_view)
        TextView menuNameTextView;
        @InjectView(R.id.menu_image_view)
        ImageView menuImageView;
        @InjectView(R.id.menu_detail_text_view)
        TextView menuDetailTextView;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
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
import butterknife.InjectViews;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.Schema;
import edu.mit.mitmobile2.dining.model.MITDiningMenuItem;

public class HouseMealMenuAdapter extends BaseAdapter{

    private LayoutInflater listContainer;
    private List<MITDiningMenuItem> menuItems;
    private Context context;

    public HouseMealMenuAdapter(Context context, List<MITDiningMenuItem> menuItems) {
        listContainer = LayoutInflater.from(context);
        this.context = context;
        this.menuItems = menuItems;
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
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

        MITDiningMenuItem item = menuItems.get(position);

        if (item.getStation() != null) {
            viewHolder.menuTypeTextView.setText(item.getStation());
            viewHolder.menuTypeTextView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.menuTypeTextView.setVisibility(View.GONE);
        }
        viewHolder.menuNameTextView.setText(item.getName());
        if (item.getItemDescription() != null) {
            viewHolder.menuDetailTextView.setText(item.getItemDescription());
            viewHolder.menuDetailTextView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.menuDetailTextView.setVisibility(View.GONE);
        }

        List<String> flags = item.getDietaryFlags();

        if ((flags != null) && (flags.size() > 0)) {
            for (int i = 0; i < flags.size(); i++) {
                if (flags.get(i).contains("-")) {
                    flags.get(i).replaceAll("\\s", "-");
                }
                int resId =  context.getResources().getIdentifier("dining_" + flags.get(i)
                                .toLowerCase().replaceAll("\\s", ""), "drawable",
                        context.getPackageName());
                if (resId > 0) {
                    viewHolder.imageViews.get(i).setImageResource(resId);
                    viewHolder.imageViews.get(i).setVisibility(View.VISIBLE);
                } else {
                    viewHolder.imageViews.get(i).setVisibility(View.GONE);
                }
            }
        } else {
            for (ImageView imageView : viewHolder.imageViews) {
                imageView.setVisibility(View.GONE);
            }
        }

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.menu_type_text_view)
        TextView menuTypeTextView;
        @InjectView(R.id.menu_name_text_view)
        TextView menuNameTextView;
        @InjectViews({R.id.menu_first_image_view, R.id.menu_second_image_view, R.id.menu_third_image_view, R.id.menu_forth_image_view})
        List<ImageView> imageViews;
        @InjectView(R.id.menu_detail_text_view)
        TextView menuDetailTextView;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
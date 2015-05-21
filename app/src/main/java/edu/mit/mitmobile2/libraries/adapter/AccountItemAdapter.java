package edu.mit.mitmobile2.libraries.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.libraries.model.MITLibrariesMITFineItem;
import edu.mit.mitmobile2.libraries.model.MITLibrariesMITHoldItem;
import edu.mit.mitmobile2.libraries.model.MITLibrariesMITItem;
import edu.mit.mitmobile2.libraries.model.MITLibrariesMITLoanItem;

public class AccountItemAdapter extends BaseAdapter {

    private class ViewHolder {
        TextView title;
        TextView description;
        ImageView image;
        TextView status;
        TextView statusSubtext;
    }

    private Context context;
    private List<MITLibrariesMITItem> items;

    public AccountItemAdapter(Context context, List<MITLibrariesMITItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;

        if (view == null) {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.mit_library_item_row, null);

            holder.title = (TextView) view.findViewById(R.id.item_title);
            holder.description = (TextView) view.findViewById(R.id.item_description);
            holder.status = (TextView) view.findViewById(R.id.item_status);
            holder.image = (ImageView) view.findViewById(R.id.item_image);
            holder.statusSubtext = (TextView) view.findViewById(R.id.item_status_subtext);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        MITLibrariesMITItem item = (MITLibrariesMITItem) getItem(position);

        holder.title.setText(item.getTitle());
        holder.description.setText(item.getYear() + "; " + item.getAuthor());

        if (item instanceof MITLibrariesMITLoanItem) {
            MITLibrariesMITLoanItem loanItem = (MITLibrariesMITLoanItem) item;
            if (loanItem.isLongOverdue() || loanItem.isOverdue()) {
                holder.status.setVisibility(View.VISIBLE);
                holder.statusSubtext.setVisibility(View.GONE);
                holder.status.setText(loanItem.getDueText());
                holder.status.setTextColor(context.getResources().getColor(R.color.closed_red));
                holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_alert, 0, 0, 0);
            } else {
                holder.status.setVisibility(View.GONE);
                holder.statusSubtext.setVisibility(View.VISIBLE);
                holder.statusSubtext.setText(loanItem.getDueText());
            }
        } else if (item instanceof MITLibrariesMITHoldItem) {
            MITLibrariesMITHoldItem holdItem = (MITLibrariesMITHoldItem) item;
            if (holdItem.isReadyForPickup()) {
                holder.status.setText(context.getString(R.string.ready_at) + holdItem.getPickupLocation());
                holder.status.setTextColor(context.getResources().getColor(R.color.open_green));
                holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow, 0, 0, 0);
                holder.statusSubtext.setText(holdItem.getStatus());
            } else {
                holder.status.setVisibility(View.GONE);
                holder.statusSubtext.setVisibility(View.VISIBLE);
                holder.statusSubtext.setText(holdItem.getStatus());
            }
        } else if (item instanceof MITLibrariesMITFineItem) {
            MITLibrariesMITFineItem fineItem = (MITLibrariesMITFineItem) item;
            holder.status.setText(fineItem.getFormattedAmount());
            holder.status.setTextColor(context.getResources().getColor(R.color.closed_red));
            holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_alert, 0, 0, 0);
            holder.statusSubtext.setVisibility(View.GONE);
        }

        Picasso.with(context).load(item.getCoverImages().get(1).getUrl()).fit().centerCrop().into(holder.image);

        return view;
    }
}

package edu.mit.mitmobile2.qrreader.adapters;

import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.qrreader.models.QrReaderResult;

/**
 * Created by serg on 6/16/15.
 */
public class ScannerHistoryAdapter extends BaseAdapter {

    public interface OnScannerHistoryAdapterListener {
        void onDelete(int position, QrReaderResult result);
    }

    private List<QrReaderResult> results;
    private OnScannerHistoryAdapterListener listener;
    private boolean editMode;

    public ScannerHistoryAdapter(List<QrReaderResult> results, OnScannerHistoryAdapterListener listener) {
        this.results = results;
        this.listener = listener;
    }

    public void updateData(List<QrReaderResult> results) {
        this.results.clear();
        if (results != null) {
            this.results.addAll(results);
        }
        notifyDataSetChanged();
    }

    public void toggleEditMode() {
        this.editMode = !this.editMode;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public QrReaderResult getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.row_scanning_history, null);

            holder = new ViewHolder();
            holder.imageViewThumbnail = (ImageView) convertView.findViewById(R.id.scanner_iv_thumbnail);
            holder.textViewText = (TextView) convertView.findViewById(R.id.scanner_tv_text);
            holder.textViewDate = (TextView) convertView.findViewById(R.id.scanner_tv_date);
            holder.imageViewDelete = (ImageView) convertView.findViewById(R.id.delete_button);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        QrReaderResult result = getItem(position);

        holder.textViewText.setText(result.getText());
        holder.textViewDate.setText(DateUtils.getRelativeTimeSpanString(result.getDate().getTime(), new Date().getTime(), 0L, DateUtils.FORMAT_ABBREV_ALL));

        if (editMode) {
            holder.imageViewDelete.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewDelete.setVisibility(View.GONE);
        }

        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDelete(position, getItem(position));
            }
        });

        return convertView;
    }

    class ViewHolder {
        ImageView imageViewThumbnail;
        TextView textViewText;
        TextView textViewDate;

        ImageView imageViewDelete;
    }
}

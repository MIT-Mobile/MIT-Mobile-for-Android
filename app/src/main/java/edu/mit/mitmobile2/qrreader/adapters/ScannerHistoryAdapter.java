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
import edu.mit.mitmobile2.qrreader.models.QRReaderResult;

/**
 * Created by serg on 6/16/15.
 */
public class ScannerHistoryAdapter extends BaseAdapter {

    private List<QRReaderResult> results;

    public ScannerHistoryAdapter(List<QRReaderResult> results) {
        this.results = results;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public QRReaderResult getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.row_scanning_history, null);

            holder = new ViewHolder();
            holder.imageViewThumbnail = (ImageView) convertView.findViewById(R.id.scanner_iv_thumbnail);
            holder.textViewText = (TextView) convertView.findViewById(R.id.scanner_tv_text);
            holder.textViewDate = (TextView) convertView.findViewById(R.id.scanner_tv_date);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        QRReaderResult result = getItem(position);

        holder.textViewText.setText(result.getText());
        holder.textViewDate.setText(DateUtils.getRelativeTimeSpanString(result.getDate().getTime(), new Date().getTime(), 0L, DateUtils.FORMAT_ABBREV_ALL));

        return convertView;
    }

    class ViewHolder {
        ImageView imageViewThumbnail;
        TextView textViewText;
        TextView textViewDate;
    }
}

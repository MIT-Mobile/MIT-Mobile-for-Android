package edu.mit.mitmobile2.qrreader.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.qrreader.models.QrReaderDetails;
import edu.mit.mitmobile2.qrreader.models.QrReaderDetailsAction;
import edu.mit.mitmobile2.qrreader.models.QrReaderResult;

/**
 * Created by serg on 6/18/15.
 */
public class ScannerHistoryDetailAdapter extends BaseAdapter {

    private static final int ROW_TYPE_DETAIL_INFO = 0;
    private static final int ROW_TYPE_DETAIL_ACTION = 1;
    private static final int ROW_TYPES_COUNT = 2;

    private QrReaderResult result;
    private QrReaderDetails details;

    private SimpleDateFormat dateFormat;

    public ScannerHistoryDetailAdapter(QrReaderResult result) {
        this.result = result;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
    }

    public void updateDetails(QrReaderDetails details) {
        this.details = details;

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (details == null) {
            return 0;
        } else if (details.getActions() == null) {
            return 2;                                    // title and date rows
        } else {
            return details.getActions().size() + 2;      // + title and date rows
        }
    }

    @Override
    public Object getItem(int position) {
        if (position <= 1) {
            return null;
        } else {
            return details.getActions().get(position - 2);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int rowType = getItemViewType(position);

        switch (rowType) {
            case ROW_TYPE_DETAIL_INFO: {
                ViewHolder viewHolder;
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(), R.layout.row_scanner_history_detail, null);

                    viewHolder = new ViewHolder();
                    viewHolder.textViewTitle = (TextView) convertView.findViewById(R.id.scanner_detail_tv_title);
                    viewHolder.textViewDescription = (TextView) convertView.findViewById(R.id.scanner_detail_tv_description);

                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                if (position == 0) {
                    viewHolder.textViewTitle.setText(details.getType());
                    viewHolder.textViewDescription.setText(result.getText());
                } else if (position == 1) {
                    viewHolder.textViewTitle.setText(R.string.scan_scanned);
                    viewHolder.textViewDescription.setText(dateFormat.format(result.getDate()));
                }

            }
            break;
            case ROW_TYPE_DETAIL_ACTION: {
                ViewHolder viewHolder;
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(), R.layout.row_scanner_history_detail_action, null);

                    viewHolder = new ViewHolder();
                    viewHolder.textViewActionTitle = (TextView) convertView.findViewById(R.id.scanner_detail_action_tv_title);
                    viewHolder.imageViewIcon = (ImageView) convertView.findViewById(R.id.scanner_detail_action_iv_icon);

                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                QrReaderDetailsAction action = (QrReaderDetailsAction) getItem(position);

                viewHolder.textViewActionTitle.setText(action.getTitle());
            }
            break;
        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return ROW_TYPES_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (position <= 1) {
            return ROW_TYPE_DETAIL_INFO;
        } else {
            return ROW_TYPE_DETAIL_ACTION;
        }
    }

    class ViewHolder {
        // ROW_TYPE_DETAIL_INFO
        TextView textViewTitle;
        TextView textViewDescription;

        // ROW_TYPE_DETAIL_ACTION
        TextView textViewActionTitle;
        ImageView imageViewIcon;
    }
}

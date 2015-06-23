package edu.mit.mitmobile2.qrreader.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.qrreader.utils.ScannerImageUtils;
import edu.mit.mitmobile2.qrreader.activities.ScannerHistoryDetailActivity;
import edu.mit.mitmobile2.qrreader.adapters.ScannerHistoryAdapter;
import edu.mit.mitmobile2.qrreader.adapters.ScannerHistoryAdapter.OnScannerHistoryAdapterListener;
import edu.mit.mitmobile2.qrreader.models.QrReaderResult;

public class ScannerHistoryFragment extends Fragment implements OnScannerHistoryAdapterListener, AdapterView.OnItemClickListener {

    private ListView listView;

    private ScannerHistoryAdapter adapter;

    public ScannerHistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanner_history, container, false);

        listView = (ListView) view.findViewById(R.id.list);

        adapter = new ScannerHistoryAdapter(DBAdapter.getInstance().getScanningHistory(getActivity()), this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_scanner_history, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            adapter.toggleEditMode();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* OnScannerHistoryAdapterListener */

    @Override
    public void onDelete(int position, QrReaderResult result) {
        DBAdapter.getInstance().deleteQrHistoryFromDb(getActivity(), result);
        ScannerImageUtils.removeScannedImage(result);

        adapter.updateData(DBAdapter.getInstance().getScanningHistory(getActivity()));
    }

    /* AdapterView.OnItemClickListener */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        QrReaderResult result = adapter.getItem(position);

        Intent intent = new Intent(getActivity(), ScannerHistoryDetailActivity.class);
        intent.putExtra(ScannerHistoryDetailActivity.KEY_EXTRAS_SCANNER_RESULT, result);

        startActivity(intent);
    }
}

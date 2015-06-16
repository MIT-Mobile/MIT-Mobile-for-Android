package edu.mit.mitmobile2.qrreader.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.qrreader.adapters.ScannerHistoryAdapter;

public class ScannerHistoryFragment extends Fragment {

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

        adapter = new ScannerHistoryAdapter(DBAdapter.getInstance().getScanningHistory());
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_scanner_history, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

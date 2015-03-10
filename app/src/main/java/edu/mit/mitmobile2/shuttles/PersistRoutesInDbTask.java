package edu.mit.mitmobile2.shuttles;

import android.content.ContentValues;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.DatabaseObject;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.Schema;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRouteWrapper;

public class PersistRoutesInDbTask extends AsyncTask<List<MITShuttleRouteWrapper>, Void, Void> {

    @Override
    protected Void doInBackground(List<MITShuttleRouteWrapper>... params) {

        List<DatabaseObject> updatedRoutes = new ArrayList<>();

        DBAdapter dbAdapter = MitMobileApplication.dbAdapter;

        // Get previous IDs
        Set<String> ids = dbAdapter.getAllIds(Schema.Route.TABLE_NAME, Schema.Route.ALL_COLUMNS, Schema.Route.ROUTE_ID);

        for (MITShuttleRouteWrapper r : params[0]) {
            if (ids.contains(r.getId())) {
                ContentValues values = new ContentValues();
                r.fillInContentValues(values, dbAdapter);
                dbAdapter.db.update(Schema.Route.TABLE_NAME, values,
                        Schema.Route.ROUTE_ID + " = " + r.getId(), null);
            } else {
                updatedRoutes.add(r);
            }
        }

        dbAdapter.batchPersist(updatedRoutes, Schema.Route.TABLE_NAME);
        return null;
    }

}

package edu.mit.mitmobile2.qrreader;

import android.app.Activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.RetrofitManager;
import edu.mit.mitmobile2.qrreader.models.QrReaderDetails;
import edu.mit.mitmobile2.qrreader.models.QrReaderResult;
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import retrofit.Callback;
import retrofit.http.GET;

public class ScannerManager extends RetrofitManager {
    private static final MitScannerService MIT_SCANNER_SERVICE = MIT_REST_ADAPTER.create(MitScannerService.class);

    @SuppressWarnings("unused")
    public static void makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback)
            throws NoSuchFieldException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {

        Method m = findMethodViaDirectReflection(MitScannerService.class, path, pathParams, queryParams, Callback.class);
        LoggingManager.Timber.d("Method = " + m);
        m.invoke(MIT_SCANNER_SERVICE, callback);
    }

    @SuppressWarnings("unused")
    public static Object makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams)
            throws NoSuchFieldException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {

        Method m = findMethodViaDirectReflection(MitScannerService.class, path, pathParams, queryParams);
        LoggingManager.Timber.d("Method = " + m);
        return m.invoke(MIT_SCANNER_SERVICE);
    }

    public static MapManagerCall getScannerDetails(Activity activity, QrReaderResult result, Callback<QrReaderDetails> categories) {
        MapManagerCallWrapper<?> returnValue = new MapManagerCallWrapper<>(new MITAPIClient(activity), categories);

        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("q", result.getText());

        returnValue.getClient().get(Constants.SCANNER, Constants.Scanner.SCANNER_MAPPINGS_PATH, null, queryParams, returnValue);

        return returnValue;
    }

    public interface MitScannerService {
        @GET(Constants.Scanner.SCANNER_MAPPINGS_PATH)
        void _get_scanner_details(Callback<QrReaderDetails> callback);
    }

    public static class MapManagerCallWrapper<T> extends MITAPIClient.ApiCallWrapper<T> implements MapManagerCall, Callback<T> {
        public MapManagerCallWrapper(MITAPIClient client, Callback<T> callback) {
            super(client, callback);
        }
    }

    public interface MapManagerCall extends MITAPIClient.ApiCall {
    }
}

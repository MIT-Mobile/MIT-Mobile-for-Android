package edu.mit.mitmobile2.links;

import android.app.Activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.RetrofitManager;
import edu.mit.mitmobile2.dining.model.MITDiningDining;
import edu.mit.mitmobile2.links.models.MITLinksCategory;
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by grmartin on 5/7/15.
 */
public class LinksManager extends RetrofitManager {
    private static final MitLinkService MIT_LINKS_SERVICE = MIT_REST_ADAPTER.create(MitLinkService.class);

    @SuppressWarnings("unused")
    public static void makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback)
            throws NoSuchFieldException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {

        Method m = findMethodViaDirectReflection(MitLinkService.class, path, pathParams, queryParams, Callback.class);
        LoggingManager.Timber.d("Method = " + m);
        m.invoke(MIT_LINKS_SERVICE, callback);
    }

    @SuppressWarnings("unused")
    public static Object makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams)
            throws NoSuchFieldException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {

        Method m = findMethodViaDirectReflection(MitLinkService.class, path, pathParams, queryParams);
        LoggingManager.Timber.d("Method = " + m);
        return m.invoke(MIT_LINKS_SERVICE);
    }

    public static LinksManagerCall getLinks(Activity activity, Callback<ArrayList<MITLinksCategory>> links) {
        LinksManagerCallWrapper<?> returnValue = new LinksManagerCallWrapper<>(new MITAPIClient(activity), links);

        returnValue.getClient().get(Constants.LINKS, Constants.Links.LINKS_PATH, null, null, returnValue);

        return returnValue;
    }

    public interface MitLinkService {
        @GET(Constants.Links.LINKS_PATH)
        void _getlinks(Callback<ArrayList<MITLinksCategory>> callback);
    }

    public static class LinksManagerCallWrapper<T> extends MITAPIClient.ApiCallWrapper<T> implements LinksManagerCall, Callback<T> {
        public LinksManagerCallWrapper(MITAPIClient client, Callback<T> callback) {
            super(client, callback);
        }
    }

    public interface LinksManagerCall extends MITAPIClient.ApiCall {
    }
}

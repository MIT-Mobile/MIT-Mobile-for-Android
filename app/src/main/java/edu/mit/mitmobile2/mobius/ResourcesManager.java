package edu.mit.mitmobile2.mobius;

import com.google.gson.JsonElement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.RetrofitManager;
import edu.mit.mitmobile2.mobius.model.ResourceItem;
import retrofit.Callback;
import retrofit.http.GET;
import timber.log.Timber;

public class ResourcesManager extends RetrofitManager {

    private static final MitResourceService MIT_RESOURCES_SERVICE = MIT_REST_ADAPTER.create(MitResourceService.class);

    @SuppressWarnings("unused")
    public static void makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback) throws NoSuchFieldException,
            NoSuchMethodException,
            ClassNotFoundException,
            IllegalAccessException,
            InvocationTargetException {

        String methodName = buildMethodName(path, pathParams, queryParams);

        Timber.d("Method name= " + methodName);

        Method m = MIT_RESOURCES_SERVICE.getClass().getDeclaredMethod(methodName, Callback.class);
        m.invoke(MIT_RESOURCES_SERVICE, callback);
    }

    public interface MitResourceService {

        //Async calls for use outside the SyncAdapter

        @GET(Constants.Resources.RESOURCE_ROOMSET_PATH)
        void getresourceroomset(Callback<JsonElement> callback);

        //void getResourceRoomsets(Callback<List<Roomset>> callback);

        @GET(Constants.Resources.RESOURCE_PATH)
        void getResources(Callback<List<ResourceItem>> callback);


    }
}

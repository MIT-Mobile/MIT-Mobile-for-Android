package edu.mit.mitmobile2.resources;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import edu.mit.mitmobile2.RetrofitManager;

public class ResourcesManager extends RetrofitManager {

    private static final ResourcesService MIT_RESOURCES_SERVICE = MIT_REST_ADAPTER.create(ResourcesService.class);

    @SuppressWarnings("unused")
    public static void makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback) throws NoSuchFieldException,
            NoSuchMethodException,
            ClassNotFoundException,
            IllegalAccessException,
            InvocationTargetException {


    }

    public interface ResourcesService {

    }
}

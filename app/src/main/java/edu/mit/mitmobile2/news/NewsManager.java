package edu.mit.mitmobile2.news;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.RetrofitManager;
import edu.mit.mitmobile2.news.models.MITNewsCategory;
import edu.mit.mitmobile2.news.models.MITNewsStory;
import retrofit.Callback;
import retrofit.http.GET;
import edu.mit.mitmobile2.shared.logging.LoggingManager.Timber;

public class NewsManager extends RetrofitManager {

    private static final MitNewsService MIT_NEWS_SERVICE = MIT_REST_ADAPTER.create(MitNewsService.class);

    @SuppressWarnings("unused")
    public static void makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Object callback) throws NoSuchFieldException,
            NoSuchMethodException,
            ClassNotFoundException,
            IllegalAccessException,
            InvocationTargetException {

        String methodName = buildMethodName(path, pathParams, queryParams);

        Timber.d("Method name= " + methodName);

        Method m = MIT_NEWS_SERVICE.getClass().getDeclaredMethod(methodName, Callback.class);
        m.invoke(MIT_NEWS_SERVICE, callback);
    }

    @SuppressWarnings("unused")
    public static Object makeHttpCall(String apiType, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams) throws NoSuchFieldException,
            NoSuchMethodException,
            ClassNotFoundException,
            IllegalAccessException,
            InvocationTargetException {

        String methodName = buildMethodName(path, pathParams, queryParams);

        Timber.d("Method name= " + methodName);

        Method m = MIT_NEWS_SERVICE.getClass().getDeclaredMethod(methodName);
        return m.invoke(MIT_NEWS_SERVICE);
    }

    public interface MitNewsService {

        @GET(Constants.News.STORIES_PATH)
        void _getstories(Callback<List<MITNewsStory>> callback);

        @GET(Constants.News.STORIES_BY_ID_PATH)
        void _getstories_(Callback<MITNewsStory> callback);

        @GET(Constants.News.CATEGORIES_PATH)
        void _getcategories(Callback<List<MITNewsCategory>> callback);
    }
}

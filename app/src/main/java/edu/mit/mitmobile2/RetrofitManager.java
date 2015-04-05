package edu.mit.mitmobile2;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import retrofit.Endpoint;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import timber.log.Timber;

public abstract class RetrofitManager {

    private static class MitEndpoint implements Endpoint {

        public static MitEndpoint create() {
            return new MitEndpoint();
        }

        public MitEndpoint() {
        }

        private String url;

        public void setUrl(String url) {
            // Remove the last backslash because retrofit also requires a backslash at the beginning of the HTTP call path
            this.url = url.substring(0, url.length() - 1);
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public String getName() {
            return "default";
        }
    }

    protected static HashMap<String, String> paths;
    protected static HashMap<String, String> queries;

    private static MitEndpoint mitEndpoint = MitEndpoint.create();

    private static RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            if (paths != null) {
                for (Map.Entry<String, String> set : paths.entrySet()) {
                    request.addPathParam(set.getKey(), set.getValue());
                }
                paths.clear();
            }

            if (queries != null) {
                for (Map.Entry<String, String> set : queries.entrySet()) {
                    request.addQueryParam(set.getKey(), set.getValue());
                }
                queries.clear();
            }
        }
    };

    protected static RestAdapter MIT_REST_ADAPTER = new RestAdapter.Builder()
            .setEndpoint(mitEndpoint)
            .setLog(new RestAdapter.Log() {
                @Override
                public void log(String message) {
                    Timber.d(message);
                }
            })
            .setRequestInterceptor(requestInterceptor)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build();

    public static void changeEndpoint(String url) {
        if (mitEndpoint.getUrl() != null) {
            if (!(mitEndpoint.getUrl() + "/").equals(url)) {
                mitEndpoint.setUrl(url);
            }
        } else {
            mitEndpoint.setUrl(url);
        }
    }

    protected static String buildMethodName(String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams) {
        String[] pathSections = path.split("/");
        String methodName = "get";

        paths = pathParams;
        queries = queryParams;

        for (int i = 1; i < pathSections.length; i++) {
            // Skip the first
            if (!pathSections[i].contains("{")) {
                methodName += pathSections[i];
            } else {
                methodName += "_";
            }
        }

        return methodName;
    }

}

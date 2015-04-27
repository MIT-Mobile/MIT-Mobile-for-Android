package edu.mit.mitmobile2;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.mit.mitmobile2.shared.logging.LoggingManager.Timber;
import retrofit.Endpoint;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

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

    /**
     * Similar to {@see buildMethodName} this method returns the Service Interface method handling the requested
     * call via direct Annotation matching.
     *
     * If this call falls to find an expected method (this should -never- happen) we fallback to the old logic.
     * @param klass The class provided as the Interface handler.
     * @param path The path constant of the method we are looking for.
     * @param pathParams ??? This appears to be unused but we take it as input since the older call does.
     * @param queryParams ??? This appears to be unused but we take it as input since the older call does.
     * @param parameterTypes Expected parameter types to match against the method we are looking for.
     * @return A method if found for the specified web API call path.
     */
    protected static Method findMethodViaDirectReflection(Class<?> klass, String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams, Class<?>... parameterTypes) {
        assert klass != null;
        assert path != null;

        /* In theory only -we- should fall back to the old form of method handling, deprecating old interface. */
        String fallbackMethodName = buildMethodName__archaic__(path, pathParams, queryParams);
        Method fallbackMethod = null;

        for (Method method : klass.getMethods()) {
            if (!Arrays.equals(method.getParameterTypes(), parameterTypes)) continue;

            retrofit.http.GET annotationG    = method.getAnnotation(retrofit.http.GET.class);
            retrofit.http.POST annotationPo  = method.getAnnotation(retrofit.http.POST.class);
            retrofit.http.PUT annotationPu   = method.getAnnotation(retrofit.http.PUT.class);
            retrofit.http.PATCH annotationPa = method.getAnnotation(retrofit.http.PATCH.class);
            retrofit.http.HEAD annotationH   = method.getAnnotation(retrofit.http.HEAD.class);
            retrofit.http.DELETE annotationD = method.getAnnotation(retrofit.http.DELETE.class);

            if (annotationG  != null && annotationG.value().equals(path))  return method;
            if (annotationPo != null && annotationPo.value().equals(path)) return method;
            if (annotationPu != null && annotationPu.value().equals(path)) return method;
            if (annotationPa != null && annotationPa.value().equals(path)) return method;
            if (annotationH  != null && annotationH.value().equals(path))  return method;
            if (annotationD  != null && annotationD.value().equals(path))  return method;

            if (method.getName().equals(fallbackMethodName)) fallbackMethod = method;
        }

        return fallbackMethod;
    }

    private static String buildMethodName__archaic__(String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams) {
        String[] pathSections = path.split("/");
        String methodName = "_get";

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

    @Deprecated
    protected static String buildMethodName(String path, HashMap<String, String> pathParams, HashMap<String, String> queryParams) {
        return buildMethodName__archaic__(path, pathParams, queryParams);
    }

}

package edu.mit.mitmobile2.shared.logging;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import edu.mit.mitmobile2.shared.runtime.RuntimeUtils;

/**
 * Created by grmartin on 4/22/15.
 */
public class LoggingManager implements Logger {
    private static final LoggingManager INSTANCE;
    
    private Set<String> keys;
    private AtomicInteger minimumLoggingLevel;
    private AtomicReference<Logger> logger;
    private Observable observer;

    public static final int VERBOSE = Logger.VERBOSE;
    public static final int DEBUG = Logger.DEBUG;
    public static final int INFO = Logger.INFO;
    public static final int WARN = Logger.WARN;
    public static final int ERROR = Logger.ERROR;
    public static final int ASSERT = Logger.ASSERT;

    public static final int OBSERVER_TAG_GLOBAL_MIN_LEVEL_CHANGED = 1504221643;

    private static final Pattern STRING_FROMAT_SPECIFIER
            = Pattern.compile("%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])");

    static {
        INSTANCE = new LoggingManager();
    }

    public static void setGlobalMinimumLogLevel(int level) {
        INSTANCE.minimumLoggingLevel.set(level);

        INSTANCE.notifyObservers(OBSERVER_TAG_GLOBAL_MIN_LEVEL_CHANGED, level);
    }

    private void notifyObservers(int tag, Object context) {
        observer.notifyObservers(new ObservationContext(tag, context));
    }

    public static synchronized void registerObserver(Observer observer) {
        INSTANCE.observer.addObserver(observer);
    }

    public static synchronized void unregisterObserver(Observer observer) {
        INSTANCE.observer.deleteObserver(observer);
    }

    public static void setLoggingMechanism(@NonNull Logger logger) {
        INSTANCE.logger.set(logger);
    }

    public static Logger getLoggingMechanism() {
        return INSTANCE.logger.get();
    }

    public static synchronized void setKey(@NonNull String key, boolean enabled) {
        if (enabled) if (!INSTANCE.keys.contains(key)) INSTANCE.keys.add(key);
        else if (INSTANCE.keys.contains(key)) INSTANCE.keys.remove(key);
    }

    public static void enableKey(@NonNull String key) {
        setKey(key, true);
    }

    public static synchronized boolean isEnabled(@NonNull String key) {
        return INSTANCE.keys.contains(key);
    }

    private LoggingManager() {
        keys = new HashSet<String>();
        minimumLoggingLevel = new AtomicInteger(0);
        logger = new AtomicReference<>(AndroidUtilLogger.DEFAULT_INSTANCE);
        observer = new Observable();
    }

    /* Tag vs Key Split Logging Facility */
    public int v(String key, String tag, String msg) {
        if (isEnabled(key))
            return INSTANCE.logger.get().v(tag, msg);
        return 0;
    }
    public int v(String key, String tag, String msg, Throwable tr) {
        if (isEnabled(key))
            return INSTANCE.logger.get().v(tag, msg, tr);
        return 0;
    }
    public int d(String key, String tag, String msg) {
        if (isEnabled(key))
            return INSTANCE.logger.get().d(tag, msg);
        return 0;
    }
    public int d(String key, String tag, String msg, Throwable tr) {
        if (isEnabled(key))
            return INSTANCE.logger.get().d(tag, msg, tr);
        return 0;
    }
    public int i(String key, String tag, String msg) {
        if (isEnabled(key))
            return INSTANCE.logger.get().i(tag, msg);
        return 0;
    }
    public int i(String key, String tag, String msg, Throwable tr) {
        if (isEnabled(key))
            return INSTANCE.logger.get().i(tag, msg, tr);
        return 0;
    }
    public int w(String key, String tag, String msg) {
        if (isEnabled(key))
            return INSTANCE.logger.get().w(tag, msg);
        return 0;
    }
    public int w(String key, String tag, String msg, Throwable tr) {
        if (isEnabled(key))
            return INSTANCE.logger.get().w(tag, msg, tr);
        return 0;
    }

// WHERE OUR METHODS CONFLICT WITH THE "NORMAL" ANDROID ONES, SUPPORT ANDROID OVER OURS.
//    public int w(String key, String tag, Throwable tr) {
//        if (isEnabled(key))
//            return INSTANCE.logger.get().w(tag, tr);
//        return 0;
//    }

    public int e(String key, String tag, String msg) {
        if (isEnabled(key))
            return INSTANCE.logger.get().e(tag, msg);
        return 0;
    }
    public int e(String key, String tag, String msg, Throwable tr) {
        if (isEnabled(key))
            return INSTANCE.logger.get().e(tag, msg, tr);
        return 0;
    }
    public int println(int priority, String key, String tag, String msg) {
        if (this.minimumLoggingLevel.get() >= priority && isEnabled(key))
            return INSTANCE.logger.get().println(priority, tag, msg);
        return 0;
    }

    /* Normal Android Logging Facility */
    public int v(String tag, String msg) {
        if (isEnabled(tag))
            return v(tag, tag, msg);
        return 0;
    }
    public int v(String tag, String msg, Throwable tr) {
        if (isEnabled(tag))
            return v(tag, tag, msg, tr);
        return 0;
    }
    public int d(String tag, String msg) {
        if (isEnabled(tag))
            return d(tag, tag, msg);
        return 0;
    }
    public int d(String tag, String msg, Throwable tr) {
        if (isEnabled(tag))
            return d(tag, tag, msg, tr);
        return 0;
    }
    public int i(String tag, String msg) {
        if (isEnabled(tag))
            return i(tag, tag, msg);
        return 0;
    }
    public int i(String tag, String msg, Throwable tr) {
        if (isEnabled(tag))
            return i(tag, tag, msg, tr);
        return 0;
    }
    public int w(String tag, String msg) {
        if (isEnabled(tag))
            return w(tag, tag, msg);
        return 0;
    }
    public int w(String tag, String msg, Throwable tr) {
        if (isEnabled(tag))
            return w(tag, tag, msg, tr);
        return 0;
    }
    public int w(String tag, Throwable tr) {
        if (isEnabled(tag))
            return w(tag, tag, tr);
        return 0;
    }
    public int e(String tag, String msg) {
        if (isEnabled(tag))
            return e(tag, tag, msg);
        return 0;
    }
    public int e(String tag, String msg, Throwable tr) {
        if (isEnabled(tag))
            return e(tag, tag, msg, tr);
        return 0;
    }
    public int println(int priority, String tag, String msg) {
        if (isEnabled(tag))
            return println(priority, tag, tag, msg);
        return 0;
    }

    public static class ObservationContext {
        private final int eventTag;
        private final Object context;

        ObservationContext(int eventTag) {
            this(eventTag, null);
        }

        ObservationContext(int eventTag, Object context) {
            this.eventTag = eventTag;
            this.context = context;
        }

        public Object getContext() {
            return context;
        }

        public int getEventTag() {
            return eventTag;
        }

        @Override
        public String toString() {
            return "ObservationContext{" +
                    "eventTag=" + eventTag +
                    ", context=" + context +
                    '}';
        }
    }

    public static final class Timber {
        private static final String DEFAULT_TIMBER_TAG = "MockTimber";
        private static final int DEBUG_MAXIMUM_LEVEL = LoggingManager.DEBUG;
        private static String CURRENT_LOG_TAG;
        private static boolean IS_DEBUG_LEVEL;
        private static Observer OBSERVER;


        static {
            IS_DEBUG_LEVEL = ((int) INSTANCE.minimumLoggingLevel.get()) <= DEBUG_MAXIMUM_LEVEL;

            OBSERVER = new Observer() {
                @Override public void update(Observable observable, Object data) {
                    if (observable == null || data == null || !(data instanceof ObservationContext)) return;
                    ObservationContext context = (ObservationContext) data;

                    if (context.getEventTag() == OBSERVER_TAG_GLOBAL_MIN_LEVEL_CHANGED) {
                        IS_DEBUG_LEVEL = (context.getContext() == null) || context.getContext() instanceof Integer && ((int) context.getContext()) <= DEBUG_MAXIMUM_LEVEL;
                    }
                }
            };

            LoggingManager.registerObserver(OBSERVER);
        }

        public static void setTag(String t) {
            if (!TextUtils.isEmpty(t)) CURRENT_LOG_TAG = t;
            else CURRENT_LOG_TAG = null;
        }

        public static void v(String message, Object... args) {
            INSTANCE.logger.get().v(getTag(), vargs(message, args));
        }

        public static void v(Throwable t, String message, Object... args) {
            INSTANCE.logger.get().v(getTag(), vargs(message, args), t);
        }

        public static void d(String message, Object... args) {
            INSTANCE.logger.get().d(getTag(), vargs(message, args));
        }

        public static void d(Throwable t, String message, Object... args) {
            INSTANCE.logger.get().d(getTag(), vargs(message, args), t);
        }

        public static void i(String message, Object... args) {
            INSTANCE.logger.get().i(getTag(), vargs(message, args));
        }

        public static void i(Throwable t, String message, Object... args) {
            INSTANCE.logger.get().i(getTag(), vargs(message, args), t);
        }

        public static void w(String message, Object... args) {
            INSTANCE.logger.get().w(getTag(), vargs(message, args));
        }

        public static void w(Throwable t, String message, Object... args) {
            INSTANCE.logger.get().w(getTag(), vargs(message, args), t);
        }

        public static void e(String message, Object... args) {
            INSTANCE.logger.get().e(getTag(), vargs(message, args));
        }

        public static void e(Throwable t, String message, Object... args) {
            INSTANCE.logger.get().e(getTag(), vargs(message, args), t);
        }

        public static void wtf(String message, Object... args) {
            INSTANCE.logger.get().e(getTag(), vargs(message, args));
        }

        public static void wtf(Throwable t, String message, Object... args) {
            INSTANCE.logger.get().e(getTag(), vargs(message, args), t);
        }

        private static String vargs(String message, Object... args) {
            if (message == null) {
                if (args.length >= 1) {
                    return "NO MESSAGE => "+ Arrays.toString(args);
                } else {
                    return null;
                }
            } else {
                if (STRING_FROMAT_SPECIFIER.matcher(message).matches() && args.length > 0) {
                    return String.format(message, args);
                } else if (args.length > 0) {
                    return message + " => "+ Arrays.toString(args);
                } else {
                    return message;
                }
            }
        }

        private static String getTag() {
            if (CURRENT_LOG_TAG == null) {
                if (IS_DEBUG_LEVEL) {
                    StackTraceElement[] stackTrace = new Throwable().getStackTrace();

                    if (stackTrace.length <= 5) {
                        return DEFAULT_TIMBER_TAG;
                    }

                    String t = null;

                    int skip = 2;
                    for (StackTraceElement ste : stackTrace) {
                        try {
                            if (skip == 0 && !RuntimeUtils.isInRuntimePackage(Class.forName(ste.getClassName()))) {
                                t = ste.getClassName();
                                break;
                            }
                        } catch (ClassNotFoundException ignored) { }
                        skip--;
                    }

                    if (TextUtils.isEmpty(t)) return DEFAULT_TIMBER_TAG;

                    if (t.contains("$")) {
                        t = t.substring(t.lastIndexOf('$') + 1);
                    }

                    int lastIdx = t.lastIndexOf('.') + 1;

                    String newTag = String.format("%1.20s", t.substring(lastIdx));

                    if (TextUtils.isEmpty(newTag)) return DEFAULT_TIMBER_TAG; else return newTag;
                } else {
                    return DEFAULT_TIMBER_TAG;
                }
            }

            return CURRENT_LOG_TAG;
        }
    }

    public static final class Log {
        /* Drop in for Android */
        public static int v(String tag, String msg) {
            return INSTANCE.logger.get().v(tag, msg);
        }

        public static int v(String tag, String msg, Throwable tr) {
            return INSTANCE.logger.get().v(tag, msg, tr);
        }

        public static int d(String tag, String msg) {
            return INSTANCE.logger.get().d(tag, msg);
        }

        public static int d(String tag, String msg, Throwable tr) {
            return INSTANCE.logger.get().d(tag, msg, tr);
        }

        public static int i(String tag, String msg) {
            return INSTANCE.logger.get().i(tag, msg);
        }

        public static int i(String tag, String msg, Throwable tr) {
            return INSTANCE.logger.get().i(tag, msg, tr);
        }

        public static int w(String tag, String msg) {
            return INSTANCE.logger.get().w(tag, msg);
        }

        public static int w(String tag, String msg, Throwable tr) {
            return INSTANCE.logger.get().w(tag, msg, tr);
        }

        public static int w(String tag, Throwable tr) {
            return INSTANCE.logger.get().w(tag, tr);
        }

        public static int e(String tag, String msg) {
            return INSTANCE.logger.get().e(tag, msg);
        }

        public static int e(String tag, String msg, Throwable tr) {
            return INSTANCE.logger.get().e(tag, msg, tr);
        }

        public static int println(int priority, String tag, String msg) {
            return INSTANCE.logger.get().println(priority, tag, msg);
        }
    }

    private static class AndroidUtilLogger implements Logger {
        private static Logger DEFAULT_INSTANCE = new AndroidUtilLogger();

        private AndroidUtilLogger(){}

        @Override public int v(String tag, String msg) {
            return android.util.Log.v(tag, msg);
        }
        @Override public int v(String tag, String msg, Throwable tr) {
            return android.util.Log.v(tag, msg, tr);
        }
        @Override public int d(String tag, String msg) {
            return android.util.Log.d(tag, msg);
        }
        @Override public int d(String tag, String msg, Throwable tr) {
            return android.util.Log.d(tag, msg, tr);
        }
        @Override public int i(String tag, String msg) {
            return android.util.Log.i(tag, msg);
        }
        @Override public int i(String tag, String msg, Throwable tr) {
            return android.util.Log.i(tag, msg, tr);
        }
        @Override public int w(String tag, String msg) {
            return android.util.Log.w(tag, msg);
        }
        @Override public int w(String tag, String msg, Throwable tr) {
            return android.util.Log.w(tag, msg, tr);
        }
        @Override public int w(String tag, Throwable tr) {
            return android.util.Log.w(tag, tr);
        }
        @Override public int e(String tag, String msg) {
            return android.util.Log.e(tag, msg);
        }
        @Override public int e(String tag, String msg, Throwable tr) {
            return android.util.Log.e(tag, msg, tr);
        }
        @Override public int println(int priority, String tag, String msg) {
            return android.util.Log.println(priority, tag, msg);
        }
    }
}


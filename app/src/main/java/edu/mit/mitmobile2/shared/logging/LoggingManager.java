package edu.mit.mitmobile2.shared.logging;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import android.support.annotation.NonNull;

/**
 * Created by grmartin on 4/22/15.
 */
public class LoggingManager implements Logger {
    private static final LoggingManager INSTANCE;
    
    private Set<String> keys;
    private AtomicReference<Logger> logger;

    public static final int VERBOSE = Logger.VERBOSE;
    public static final int DEBUG = Logger.DEBUG;
    public static final int INFO = Logger.INFO;
    public static final int WARN = Logger.WARN;
    public static final int ERROR = Logger.ERROR;
    public static final int ASSERT = Logger.ASSERT;

    static {
        INSTANCE = new LoggingManager();
    }

    public static void setLoggingMechanism(@NonNull Logger logger) {
        INSTANCE.logger.set(logger);
    }

    public static Logger getLoggingMechanism() {
        return INSTANCE.logger.get();
    }

    static {
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
        logger = new AtomicReference<>(AndroidUtilLogger.DEFAULT_INSTANCE);
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
        if (isEnabled(key))
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


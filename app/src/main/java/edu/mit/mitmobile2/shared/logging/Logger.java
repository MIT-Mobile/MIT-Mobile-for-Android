package edu.mit.mitmobile2.shared.logging;

public interface Logger {
    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;

    int println(int priority, String tag, String msg);

    int v(String tag, String msg);
    int v(String tag, String msg, Throwable tr);
    int d(String tag, String msg);
    int d(String tag, String msg, Throwable tr);
    int i(String tag, String msg);
    int i(String tag, String msg, Throwable tr);
    int w(String tag, String msg);
    int w(String tag, String msg, Throwable tr);
    int w(String tag, Throwable tr);
    int e(String tag, String msg);
    int e(String tag, String msg, Throwable tr);
}

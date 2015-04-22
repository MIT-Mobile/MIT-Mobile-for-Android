package edu.mit.mitmobile2.shared.functional;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Created by grmartin on 4/21/15.
 */
public final class CommonTests {
    public static final Pattern EMAIL_REGEX = Pattern.compile(
        "(?i)\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}\\b"
    );

    public static final Pattern URL_REGEX = Pattern.compile(
        "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"
    );

    public static final Test<Object> NULL_TEST;
    public static final Test<String> IS_STRING_EMPTY_TEST;
    public static final Test<String> IS_VALID_EMAIL_STRING_TEST;
    public static final Test<String> IS_VALID_URL_STRING_TEST;
    public static final Test<Collection<?>> IS_COLLECTION_EMPTY_TEST;
    public static final Test<Collection<?>> IS_COLLECTION_EMPTY_OR_NULL_TEST;

    static {
        NULL_TEST = new Test<Object>() {
            @Override public boolean test(Object subject) {
                return subject == null;
            }
        };

        IS_STRING_EMPTY_TEST = new Test<String>() {
            @Override public boolean test(String subject) {
                return NULL_TEST.test(subject) || subject.length() < 1 || subject.trim().length() < 1;
            }
        };

        IS_VALID_EMAIL_STRING_TEST = new Test<String>() {
            @Override
            public boolean test(String subject) {
                return !IS_STRING_EMPTY_TEST.test(subject) &&
                        EMAIL_REGEX.matcher(subject).matches();
            }
        };

        IS_VALID_URL_STRING_TEST = new Test<String>() {
            @Override
            public boolean test(String subject) {
                return !IS_STRING_EMPTY_TEST.test(subject) &&
                        URL_REGEX.matcher(subject).matches();
            }
        };

        IS_COLLECTION_EMPTY_TEST = new Test<Collection<?>>() {
            @Override public boolean test(Collection<?> subject) {
                return subject.size() == 0;
            }
        };

        IS_COLLECTION_EMPTY_OR_NULL_TEST = new Test<Collection<?>>() {
            @Override public boolean test(Collection<?> subject) {
                return NULL_TEST.test(subject) || IS_COLLECTION_EMPTY_TEST.test(subject);
            }
        };
    }
}

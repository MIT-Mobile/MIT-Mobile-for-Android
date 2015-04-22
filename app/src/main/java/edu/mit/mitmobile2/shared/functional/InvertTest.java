package edu.mit.mitmobile2.shared.functional;

/**
 * Created by grmartin on 4/21/15.
 */
public final class InvertTest {
    public static <T> Test<T> invert(final Test<T> t) {
        return new Test<T>() {
            @Override public boolean test(final T s) {
                return !t.test(s);
            }
        };
    }
}

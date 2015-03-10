package edu.mit.mitmobile2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectionUtils {
    public static Method findAnnotatedMethod(Class<?> klass, Class<? extends Annotation> annotationClass, Class<?>... types) {
        Method[] mths = klass.getMethods();
        for (Method mth : mths) {
            Annotation annotation = mth.getAnnotation(annotationClass);
            if (annotation != null && Arrays.equals(mth.getParameterTypes(), types)) {
                return mth;
            }
        }
        return null;
    }
}
package me.allenz.androidapplog;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectUtils {

    private ReflectUtils(){
        throw new UnsupportedOperationException();
    }

    static CallerResolver callerResolver = new CallerResolver();

    public static int intReflectStaticFieldValue(final String className,
                                                 final String fieldName, final int defaultValue) {
        try {
            final Class<?> clazz = Class.forName(className);
            final Field field = clazz.getDeclaredField(fieldName);
            if (Modifier.isStatic(field.getModifiers())
                &&
                field.getType().getName().equals("int")) {
                field.setAccessible(true);
                return field.getInt(null);
            }
        } catch (final Exception e) {
        }
        return defaultValue;
    }

    public static boolean booleanReflectStaticFieldValue(
                                                         final String className, final String fieldName,
                                                         final boolean defaultValue) {
        try {
            final Class<?> clazz = Class.forName(className);
            final Field field = clazz.getDeclaredField(fieldName);
            if (Modifier.isStatic(field.getModifiers())
                &&
                field.getType().getName().equals("boolean")) {
                field.setAccessible(true);
                return field.getBoolean(null);
            }
        } catch (final Exception e) {
        }
        return defaultValue;
    }

    static String getCallerClassName(final String classBeingCalled) {
        final Class<?> caller = callerResolver.getCaller(classBeingCalled);
        if (caller == null) {
            final StackTraceElement callerStackTrace = getCallerStackTrace(
                classBeingCalled);
            return callerStackTrace == null? null: callerStackTrace
                .getClassName();
        } else {
            return caller.getName();
        }
    }

    static StackTraceElement getCallerStackTrace(
                                                 final String classBeingCalled) {
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace == null ||
            stackTrace.length <= 0) {
            return null;
        }

        for (int i = 1; i < stackTrace.length; i++) {
            final StackTraceElement stackTraceElement = stackTrace[i];
            if (stackTraceElement.getClassName().equals(classBeingCalled)) {
                return stackTrace[i + 1];
            }
        }
        return null;
    }

    static final class CallerResolver extends SecurityManager {

        @SuppressWarnings("rawtypes")
        public Class<?> getCaller(final String classBeingCalled) {
            final Class[] classContext = getClassContext();
            if (classContext == null ||
                classContext.length <= 0) {
                return null;
            }
            for (int i = 1; i < classContext.length; i++) {
                final Class clazz = classContext[i];
                if (clazz.getName().equals(classBeingCalled)) {
                    return classContext[i + 1];
                }
            }
            return null;
        }

        @SuppressWarnings("rawtypes")
        public Class[] getClassStacks() {
            return getClassContext();
        }
    }

}

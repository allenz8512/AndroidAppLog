package me.allenz.zlog;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Utility tools.
 * 
 * @author Allenz
 * @since 0.1.0-RELEASE
 */
class Utils {

	private Utils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Consider if the string is empty.
	 * 
	 * @param str
	 *            String
	 * @return if the string is {@code null} or length equals 0 return
	 *         true,otherwise return false;
	 * @since 0.1.0-RELEASE
	 */
	public static boolean isEmpty(final String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * Get the final tag for the class.
	 * 
	 * @param classFullName
	 *            fullname(with package) of the class
	 * @param tag
	 *            tag in configure
	 * @return the tag for the class
	 * @since 0.2.0-RELEASE
	 */
	public static String finalTag(final String classFullName, final String tag) {
		if (tag != null) {
			return tag;
		}
		final int dot = classFullName.lastIndexOf(".");
		return dot == -1 ? classFullName : classFullName.substring(dot + 1);
	}

	/**
	 * A method like {@link Boolean#parseBoolean(String)}, but return
	 * {@code null} when the string can not be parsed boolean.
	 * 
	 * @param str
	 *            String
	 * @return Boolean value or {@code null}
	 * @since 0.2.0-RELEASE
	 */
	public static Boolean parseBoolean(final String str) {
		if (isEmpty(str)) {
			return null;
		}
		if ("true".equalsIgnoreCase(str)) {
			return Boolean.TRUE;
		} else if ("false".equalsIgnoreCase(str)) {
			return Boolean.FALSE;
		} else {
			return null;
		}
	}

	/**
	 * Get the integer value of static field the declared in specified class.
	 * 
	 * @param className
	 *            the fullname of the class
	 * @param fieldName
	 *            the name of the field
	 * @param defaultValue
	 *            the default value to return when failed to get value of the
	 *            field
	 * @return the value of the field when successed, or the default value when
	 *         failure
	 * @since 0.1.0-RELEASE
	 */
	public static int intReflectStaticFieldValue(final String className,
			final String fieldName, final int defaultValue) {
		try {
			final Class<?> clazz = Class.forName(className);
			final Field field = clazz.getDeclaredField(fieldName);
			if (Modifier.isStatic(field.getModifiers())
					&& field.getType().getName().equals("int")) {
				field.setAccessible(true);
				return field.getInt(null);
			}
		} catch (final Exception e) {
		}
		return defaultValue;
	}

	/**
	 * Get the boolean value of static field the declared in specified class.
	 * 
	 * @param className
	 *            the fullname of the class
	 * @param fieldName
	 *            the name of the field
	 * @param defaultValue
	 *            the default value to return when failed to get value of the
	 *            field
	 * @return the value of the field when successed, or the default value when
	 *         failure
	 * @since 0.1.0-RELEASE
	 */
	public static boolean booleanReflectStaticFieldValue(
			final String className, final String fieldName,
			final boolean defaultValue) {
		try {
			final Class<?> clazz = Class.forName(className);
			final Field field = clazz.getDeclaredField(fieldName);
			if (Modifier.isStatic(field.getModifiers())
					&& field.getType().getName().equals("int")) {
				field.setAccessible(true);
				return field.getBoolean(null);
			}
		} catch (final Exception e) {
		}
		return defaultValue;
	}
}

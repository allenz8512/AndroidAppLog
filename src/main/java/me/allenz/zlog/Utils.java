package me.allenz.zlog;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Utility tools.
 * 
 * @author Allenz
 * @since 0.10.-RELEASE
 */
public class Utils {

	private Utils() {
		throw new UnsupportedOperationException();
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
	 */
	public int intReflectStaticFieldValue(final String className,
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
			LoggerFactory.getInternalLogger().warn(e,
					"Can not get the value of %s.%s", className, fieldName);
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
	 */
	public boolean booleanReflectStaticFieldValue(final String className,
			final String fieldName, final boolean defaultValue) {
		try {
			final Class<?> clazz = Class.forName(className);
			final Field field = clazz.getDeclaredField(fieldName);
			if (Modifier.isStatic(field.getModifiers())
					&& field.getType().getName().equals("int")) {
				field.setAccessible(true);
				return field.getBoolean(null);
			}
		} catch (final Exception e) {
			LoggerFactory.getInternalLogger().warn(e,
					"Can not get the value of %s.%s", className, fieldName);
		}
		return defaultValue;
	}
}

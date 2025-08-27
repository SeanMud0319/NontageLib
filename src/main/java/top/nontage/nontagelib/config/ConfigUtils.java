package top.nontage.nontagelib.config;

import java.lang.reflect.*;
import java.util.*;

public class ConfigUtils {

    @SuppressWarnings("unchecked")
    public static void applyValues(Object target, Map<String, Object> values) {
        Class<?> clazz = target.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;

            field.setAccessible(true);
            String name = field.getName();
            if (!values.containsKey(name)) continue;

            Object value = values.get(name);
            Class<?> type = field.getType();

            try {
                Object fieldValue = field.get(target);
                boolean isFinal = Modifier.isFinal(field.getModifiers());

                if (isPrimitiveOrWrapper(type) || type == String.class) {
                    if (!isFinal) field.set(target, convertValue(type, value));
                } else if (type.isEnum() && value instanceof String) {
                    Object enumVal = Enum.valueOf((Class<? extends Enum>) type, (String) value);
                    if (!isFinal) field.set(target, enumVal);
                } else if (Map.class.isAssignableFrom(type) && value instanceof Map) {
                    if (fieldValue == null && !isFinal) {
                        fieldValue = instantiateCollection(type);
                        field.set(target, fieldValue);
                    }
                    Map<Object, Object> mapInstance = (Map<Object, Object>) fieldValue;
                    mapInstance.clear();
                    mapInstance.putAll((Map<?, ?>) value);
                } else if (Collection.class.isAssignableFrom(type) && value instanceof Collection) {
                    if (fieldValue == null && !isFinal) {
                        fieldValue = instantiateCollection(type);
                        field.set(target, fieldValue);
                    }
                    Collection<Object> collInstance = (Collection<Object>) fieldValue;
                    collInstance.clear();
                    collInstance.addAll((Collection<?>) value);
                } else if (value instanceof Map) {
                    if (fieldValue == null && !isFinal) {
                        fieldValue = type.getDeclaredConstructor().newInstance();
                        field.set(target, fieldValue);
                    }
                    applyValues(fieldValue, (Map<String, Object>) value);
                } else {
                    if (!isFinal) field.set(target, value);
                }

            } catch (Exception e) {
                System.err.println("Failed to set field '" + name + "' in class " + clazz.getName() +
                        ". Expected type: " + type.getName() +
                        ", actual value type: " + (value != null ? value.getClass().getName() : "null"));
                e.printStackTrace();
            }
        }
    }

    private static boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive()
                || type == Boolean.class
                || type == Integer.class
                || type == Long.class
                || type == Double.class
                || type == Float.class
                || type == Short.class
                || type == Byte.class
                || type == Character.class;
    }

    private static Object convertValue(Class<?> type, Object value) {
        if (!(value instanceof Number num)) return value;
        if (type == int.class || type == Integer.class) return num.intValue();
        if (type == long.class || type == Long.class) return num.longValue();
        if (type == double.class || type == Double.class) return num.doubleValue();
        if (type == float.class || type == Float.class) return num.floatValue();
        if (type == short.class || type == Short.class) return num.shortValue();
        if (type == byte.class || type == Byte.class) return num.byteValue();
        return value;
    }

    private static Object instantiateCollection(Class<?> type) throws Exception {
        if (!type.isInterface()) return type.getDeclaredConstructor().newInstance();
        if (List.class.isAssignableFrom(type)) return new ArrayList<>();
        if (Set.class.isAssignableFrom(type)) return new HashSet<>();
        if (Map.class.isAssignableFrom(type)) return new LinkedHashMap<>();
        throw new IllegalArgumentException("Cannot instantiate interface type: " + type.getName());
    }
}

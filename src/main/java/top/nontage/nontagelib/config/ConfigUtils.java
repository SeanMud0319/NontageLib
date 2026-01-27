package top.nontage.nontagelib.config;

import top.nontage.nontagelib.annotations.YamlComment;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                    Map<Object, Object> mapInstance = (Map<Object, Object>) field.get(target);
                    mapInstance.clear();

                    mapInstance.putAll(convertMapValues(field, (Map<?, ?>) value));

                }
                else if (Collection.class.isAssignableFrom(type) && value instanceof Collection) {
                    if (fieldValue == null && !isFinal) {
                        fieldValue = instantiateCollection(type);
                        field.set(target, fieldValue);
                    }
                    Collection<Object> collInstance = (Collection<Object>) field.get(target);

                    try {
                        collInstance.clear();
                    } catch (UnsupportedOperationException ex) {
                        if (collInstance instanceof Set)
                            collInstance = new HashSet<>(collInstance);
                        else
                            collInstance = new ArrayList<>(collInstance);

                        if (!isFinal) field.set(target, collInstance);
                        collInstance.clear();
                    }
                    collInstance.addAll(convertCollectionValues(field, (Collection<?>) value));
                }
                else if (value instanceof Map) {
                    if (fieldValue == null && !isFinal) {
                        fieldValue = type.getDeclaredConstructor().newInstance();
                        field.set(target, fieldValue);
                    }
                    applyValues(field.get(target), (Map<String, Object>) value);

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

    public static Map<String, Object> toMap(Object obj) {
        Map<String, Object> map = new LinkedHashMap<>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) continue;

            field.setAccessible(true);
            String name = field.getName();
            try {
                Object value = field.get(obj);
                if (value == null) continue;

                if (isPrimitiveOrWrapper(value.getClass()) || value instanceof String || value.getClass().isEnum()) {
                    map.put(name, value);
                } else if (value instanceof Map<?, ?> m) {
                    Map<Object, Object> subMap = new LinkedHashMap<>();
                    m.forEach((k, v) -> subMap.put(k, toMapIfNeeded(v)));
                    map.put(name, subMap);
                } else if (value instanceof Collection<?> c) {
                    List<Object> list = new ArrayList<>();
                    for (Object item : c) {
                        list.add(toMapIfNeeded(item));
                    }
                    map.put(name, list);
                } else {
                    map.put(name, toMap(value));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    private static Object toMapIfNeeded(Object value) {
        if (value == null) return null;
        Class<?> type = value.getClass();
        if (isPrimitiveOrWrapper(type) || type == String.class || type.isEnum()) {
            return value;
        } else if (value instanceof Map<?, ?>) {
            Map<Object, Object> newMap = new LinkedHashMap<>();
            ((Map<?, ?>) value).forEach((k, v) -> newMap.put(k, toMapIfNeeded(v)));
            return newMap;
        } else if (value instanceof Collection<?>) {
            List<Object> list = new ArrayList<>();
            for (Object item : (Collection<?>) value) list.add(toMapIfNeeded(item));
            return list;
        } else {
            return toMap(value);
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

    @SuppressWarnings("unchecked")
    private static Map<Object, Object> convertMapValues(Field field, Map<?, ?> rawMap) throws Exception {
        Map<Object, Object> result = new LinkedHashMap<>();
        Class<?> valueType = getGenericType(field, 1);
        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
            Object v = entry.getValue();
            if (v instanceof Map && valueType != null && !isPrimitiveOrWrapper(valueType) && valueType != String.class) {
                Object obj = valueType.getDeclaredConstructor().newInstance();
                applyValues(obj, (Map<String, Object>) v);
                result.put(entry.getKey(), obj);
            } else {
                result.put(entry.getKey(), v);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Collection<Object> convertCollectionValues(Field field, Collection<?> rawCollection) throws Exception {
        Collection<Object> result = new ArrayList<>();
        Class<?> valueType = getGenericType(field, 0);
        for (Object item : rawCollection) {
            if (item instanceof Map && valueType != null && !isPrimitiveOrWrapper(valueType) && valueType != String.class) {
                Object obj = valueType.getDeclaredConstructor().newInstance();
                applyValues(obj, (Map<String, Object>) item);
                result.add(obj);
            } else {
                result.add(item);
            }
        }
        return result;
    }

    private static Class<?> getGenericType(Field field, int index) {
        try {
            Type generic = field.getGenericType();
            if (generic instanceof ParameterizedType pt) {
                Type typeArgument = pt.getActualTypeArguments()[index];
                if (typeArgument instanceof Class<?> c) return c;
            }
        } catch (Exception ignored) {}
        return null;
    }


    private static Object instantiateCollection(Class<?> type) throws Exception {
        if (!type.isInterface()) {
            return type.getDeclaredConstructor().newInstance();
        }

        if (List.class.isAssignableFrom(type)) return new ArrayList<>();
        if (Set.class.isAssignableFrom(type)) return new HashSet<>();
        if (Map.class.isAssignableFrom(type)) return new LinkedHashMap<>();
        throw new IllegalArgumentException("Cannot instantiate interface type: " + type.getName());
    }

    public static String toYamlStringWithComments(Object obj) {
        StringBuilder sb = new StringBuilder();
        toYamlStringWithComments(obj, sb, 0);
        return sb.toString();
    }

    private static void toYamlStringWithComments(Object obj, StringBuilder sb, int indent) {
        if (obj == null) return;
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) continue;
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value == null) continue;

                YamlComment comment = field.getAnnotation(YamlComment.class);
                if (comment == null) comment = value.getClass().getAnnotation(YamlComment.class);

                if (comment != null) {
                    String[] commentLines = comment.value().split("\n");
                    for (String line : commentLines) {
                        appendIndent(sb, indent);
                        sb.append("# ").append(line.stripLeading()).append("\n");
                    }
                }
                appendIndent(sb, indent);
                sb.append(field.getName()).append(": ");
                if (value instanceof Map<?, ?> map) {
                    sb.append("\n");
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        appendIndent(sb, indent + 2);
                        sb.append(entry.getKey()).append(": ");
                        appendYamlValue(sb, entry.getValue(), indent + 4);
                    }
                } else if (value instanceof Collection<?> coll) {
                    sb.append("\n");
                    for (Object item : coll) {
                        appendIndent(sb, indent + 2);
                        sb.append("- ");
                        appendYamlValue(sb, item, indent + 4);
                    }
                } else {
                    appendYamlValue(sb, value, indent + 2);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static void appendYamlValue(StringBuilder sb, Object value, int indent) {
        if (value == null) {
            sb.append("null\n");
        } else if (value instanceof String str) {
            sb.append("\"").append(str.replace("\"", "\\\"")).append("\"\n");
        } else if (isPrimitiveOrWrapper(value.getClass()) || value.getClass().isEnum()) {
            sb.append(value).append("\n");
        } else if (value instanceof Map<?, ?> map) {
            sb.append("\n");
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                appendIndent(sb, indent);
                sb.append(entry.getKey()).append(": ");
                appendYamlValue(sb, entry.getValue(), indent + 2);
            }
        } else if (value instanceof Collection<?> coll) {
            sb.append("\n");
            for (Object item : coll) {
                appendIndent(sb, indent);
                sb.append("- ");
                appendYamlValue(sb, item, indent + 2);
            }
        } else {
            sb.append("\n");
            toYamlStringWithComments(value, sb, indent);
        }
    }

    private static void appendIndent(StringBuilder sb, int indent) {
        sb.append(" ".repeat(Math.max(0, indent)));
    }

}

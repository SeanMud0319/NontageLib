package top.nontage.nontagelib.config;

import java.lang.reflect.Field;
import java.util.Map;

public class ConfigUtils {
    public static void applyValues(Object target, Map<String, Object> values) {
        Class<?> clazz = target.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            if (values.containsKey(name)) {
                try {
                    field.set(target, values.get(name));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

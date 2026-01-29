package top.nontage.nontagelib.config;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager {
    private static final Map<JavaPlugin, Map<Class<?>, BaseConfig>> pluginConfigs = new ConcurrentHashMap<>();

    private static JavaPlugin getCallingPlugin() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            try {
                Class<?> clazz = Class.forName(element.getClassName());
                if (JavaPlugin.class.isAssignableFrom(clazz) && !clazz.equals(JavaPlugin.class) && !clazz.equals(ConfigManager.class)) {
                    return JavaPlugin.getProvidingPlugin(clazz);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static void init() {
        JavaPlugin plugin = getCallingPlugin();
        if (plugin != null) init(plugin);
    }

    public static void init(JavaPlugin plugin) {
        pluginConfigs.putIfAbsent(plugin, new ConcurrentHashMap<>());
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
    }

    public static <T extends BaseConfig> T register(T config, String name) {
        JavaPlugin plugin = getCallingPlugin();
        if (plugin == null) throw new IllegalStateException("Could not detect calling plugin!");

        init(plugin);

        File file = new File(plugin.getDataFolder(), name + ".yml");
        config.initFile(file);
        config.reload();

        pluginConfigs.get(plugin).put(config.getClass(), config);
        return config;
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseConfig> T get(Class<T> clazz) {
        JavaPlugin plugin = getCallingPlugin();
        if (plugin == null) return null;

        Map<Class<?>, BaseConfig> configs = pluginConfigs.get(plugin);
        return (configs != null) ? (T) configs.get(clazz) : null;
    }
}
package top.nontage.nontagelib.config;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final Map<Class<?>, BaseConfig> configs = new HashMap<>();
    private static File configDir;

    public static void init(JavaPlugin plugin) {
        configDir = plugin.getDataFolder();
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
    }

    public static <T extends BaseConfig> T register(T config, String name) {
        if (configDir == null) {
            throw new IllegalStateException("ConfigManager not initialized! Call ConfigManager.init(plugin) first.");
        }

        File file = new File(configDir, name + ".yml");
        config.initFile(file);
        config.reload();
        configs.put(config.getClass(), config);
        return config;
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseConfig> T get(Class<T> clazz) {
        return (T) configs.get(clazz);
    }
}

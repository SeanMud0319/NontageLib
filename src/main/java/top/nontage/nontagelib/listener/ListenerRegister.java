package top.nontage.nontagelib.listener;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import top.nontage.nontagelib.annotations.AutoListener;

public class ListenerRegister {

    public static void registerAll(JavaPlugin plugin) {
        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(plugin.getClass().getPackageName())
                .scan()) {

            scanResult.getClassesWithAnnotation(AutoListener.class.getName())
                    .forEach(classInfo -> {
                        try {
                            Class<?> clazz = classInfo.loadClass();
                            if (Listener.class.isAssignableFrom(clazz)) {
                                Listener listener = (Listener) clazz.getDeclaredConstructor().newInstance();
                                Bukkit.getPluginManager().registerEvents(listener, plugin);
                                plugin.getLogger().info("[AutoListener] Registered " + clazz.getSimpleName());
                            }
                        } catch (Exception e) {
                            plugin.getLogger().warning("[AutoListener] Failed to register " + classInfo.getName());
                            e.printStackTrace();
                        }
                    });
        }
    }
}

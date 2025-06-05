package top.nontage.nontagelib.command;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.bukkit.plugin.java.JavaPlugin;
import top.nontage.nontagelib.NontageLib;
import top.nontage.nontagelib.annotations.CommandInfo;

import java.lang.reflect.Modifier;

public class NontageCommandLoader {

    public static void registerAll(JavaPlugin plugin) {
        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(plugin.getClass().getPackageName())
                .scan()) {
            scanResult.getClassesWithAnnotation(CommandInfo.class.getName())
                    .forEach(classInfo -> {
                        try {
                            Class<?> clazz = classInfo.loadClass();
                            if (!NontageCommand.class.isAssignableFrom(clazz)) return;
                            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) return;
                            CommandInfo info = clazz.getAnnotation(CommandInfo.class);
                            if (info == null || !info.shouldLoad()) return;
                            NontageCommand command = (NontageCommand) clazz.getDeclaredConstructor().newInstance();
                            NontageCommandManager.register(plugin, command);
                            plugin.getLogger().info("Registered command: " + info.name());
                        } catch (Exception e) {
                            plugin.getLogger().warning("Failed to register command: " + classInfo.getName());
                            e.printStackTrace();
                        }
                    });

        }
    }

    public static void forceRegisterCommand(JavaPlugin plugin, NontageCommand command) {
        CommandInfo info = command.getClass().getAnnotation(CommandInfo.class);
        if (info == null) return;
        NontageCommandManager.register(plugin, command);
        NontageLib.getInstance().getLogger().info("Registered command: " + info.name());
    }
}

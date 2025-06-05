package top.nontage.nontagelib.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import top.nontage.nontagelib.annotations.CommandInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NontageCommandManager {

    public static void register(JavaPlugin plugin, NontageCommand command) {
        try {
            CommandInfo info = command.getClass().getAnnotation(CommandInfo.class);
            if (info == null || !info.shouldLoad()) return;
            CommandMap commandMap = getCommandMap();
            if (!(commandMap instanceof SimpleCommandMap)) return;
            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
            String mainName = info.name();
            List<String> filteredAliases = new ArrayList<>();
            for (String alias : info.aliases()) {
                if (!alias.equals(mainName)) {
                    filteredAliases.add(alias);
                }
            }
            if (info.override()) {
                knownCommands.remove(mainName);
                for (String alias : filteredAliases) {
                    knownCommands.remove(alias);
                }
            }
            Command cmd = getCommand(command, mainName, info, filteredAliases);
            commandMap.register(plugin.getName().toLowerCase(), cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static @NotNull Command getCommand(NontageCommand command, String name, CommandInfo info, List<String> aliases) {
        Command cmd = new Command(name) {
            @Override
            public boolean execute(CommandSender sender, String label, String[] args) {
                command.execute(sender, label, args);
                return true;
            }
            @Override
            public @NotNull List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) throws IllegalArgumentException {
                return command.onTabComplete(sender, alias, args, location);
            }
        };
        cmd.setAliases(aliases);
        cmd.setPermission(info.permission());
        cmd.setDescription(info.description());
        return cmd;
    }


    private static CommandMap getCommandMap() throws Exception {
        Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        return (CommandMap) commandMapField.get(Bukkit.getServer());
    }
}

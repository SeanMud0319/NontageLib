package top.nontage.nontagelib.command;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface NontageCommand {
    void execute(CommandSender sender, String label, String[] args);
    default List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        return List.of();
    }
}

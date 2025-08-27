package top.nontage.nontagelib.command.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import top.nontage.nontagelib.annotations.AutoListener;
import top.nontage.nontagelib.command.NontageCommandManager;

@AutoListener
public class PluginEvent implements Listener {
    @EventHandler
    public void onPluginDisabled(PluginDisableEvent e) {
        Plugin plugin = e.getPlugin();
        if (plugin instanceof JavaPlugin) {
            NontageCommandManager.removeCommandByPlugin((JavaPlugin) plugin);
        }
    }
}

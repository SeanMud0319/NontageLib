package top.nontage.nontagelib;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import top.nontage.nontagelib.utils.inventory.PlayerListener;

public final class NontageLib extends JavaPlugin {
    private static NontageLib instance;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }

    public static NontageLib getInstance() {
        return instance;
    }
}

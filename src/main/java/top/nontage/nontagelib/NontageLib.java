package top.nontage.nontagelib;

import org.bukkit.plugin.java.JavaPlugin;
import top.nontage.nontagelib.listener.ListenerRegister;

public final class NontageLib extends JavaPlugin {
    private static NontageLib instance;

    @Override
    public void onEnable() {
        instance = this;
        ListenerRegister.registerAll(this);
    }

    public static NontageLib getInstance() {
        return instance;
    }
}

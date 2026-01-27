package top.nontage.nontagelib.utils.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickInventoryEvent {
    private final Player player;
    private final InventoryClickEvent event;
    private final InventoryBuilder builder;
    private boolean useDefaultAction = false;

    public ClickInventoryEvent(Player player, InventoryClickEvent event, InventoryBuilder builder) {
        this.player = player;
        this.event = event;
        this.builder = builder;
    }

    public Player getPlayer() {
        return player;
    }

    public InventoryClickEvent getEvent() {
        return event;
    }

    public InventoryBuilder getBuilder() {
        return builder;
    }

    public void setUseDefaultAction(boolean useDefaultAction) {
        this.useDefaultAction = useDefaultAction;
    }

    public boolean isUseDefaultAction() {
        return useDefaultAction;
    }
}
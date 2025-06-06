package top.nontage.nontagelib.utils.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class CloseInventoryEvent {

    private final Player player;

    private final InventoryCloseEvent event;

    private final InventoryBuilder inventory;

    public CloseInventoryEvent(Player player, InventoryCloseEvent event, InventoryBuilder inventory) {
        this.player = player;
        this.event = event;
        this.inventory = inventory;
    }

    public Player getPlayer() {
        return player;
    }

    public InventoryCloseEvent getEvent() {
        return event;
    }

    public InventoryBuilder getInventory() {
        return inventory;
    }
}
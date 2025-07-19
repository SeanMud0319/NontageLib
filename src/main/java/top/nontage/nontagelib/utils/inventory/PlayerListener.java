package top.nontage.nontagelib.utils.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void InventoryClickEvent(InventoryClickEvent e) {
        int slot = e.getRawSlot();
        if (slot == -999 || e.getClickedInventory() == null) {
            return;
        }
        InventoryListener.inventoryClickEvent(e);
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void InventoryDragEvent(InventoryDragEvent e) {
        InventoryListener.inventoryDragEvent(e);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void InventoryCloseEvent(InventoryCloseEvent e) {
        InventoryListener.inventoryCloseEvent(e);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerQuitEvent(PlayerQuitEvent e) {
        InventoryListener.playerQuitEvent(e);
    }
}

package top.nontage.nontagelib.utils.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

public class InventoryListener {
    static final HashMap<InventoryBuilder, Player> inventoryMap = new HashMap<>();

    public static void inventoryClickEvent(InventoryClickEvent e) {
        Inventory topInv = e.getView().getTopInventory();
        Inventory clickedInv = e.getClickedInventory();
        if (clickedInv == null) return;

        int slot = e.getRawSlot();
        Player player = (Player) e.getWhoClicked();

        for (InventoryBuilder b : InventoryListener.inventoryMap.keySet()) {
            if (!topInv.equals(b.getInventory())) continue;

            if (b.lockedInv) e.setCancelled(true);

            if (!b.allowableShiftClickDown && e.isShiftClick() && clickedInv.equals(player.getInventory())) e.setCancelled(true);
            if (!b.allowableDoubleClickDown && e.getClick() == ClickType.DOUBLE_CLICK && clickedInv.equals(player.getInventory())) e.setCancelled(true);
            if (!b.allowableShiftClickUp && e.isShiftClick() && clickedInv.equals(topInv)) e.setCancelled(true);
            if (!b.allowableDoubleClickUp && e.getClick() == ClickType.DOUBLE_CLICK && clickedInv.equals(topInv)) e.setCancelled(true);

            if (clickedInv.equals(b.getInventory())) {
                Consumer<ClickInventoryEvent> allClick = b.allClickEvent;
                Consumer<ClickInventoryEvent> singleClick = b.clickEvents.get(slot);

                if (allClick != null || singleClick != null) {
                    ClickInventoryEvent wrapper = new ClickInventoryEvent(player, e, b);

                    if (allClick != null) allClick.accept(wrapper);
                    if (singleClick != null) singleClick.accept(wrapper);

                    if (!wrapper.isUseDefaultAction()) {
                        e.setCancelled(true);
                        player.updateInventory();
                    }
                }

                if (slot >= 0 && slot < b.clickable.length && !b.clickable[slot]) {
                    e.setCancelled(true);
                }
            }
            break;
        }
    }

    public static void inventoryDragEvent(InventoryDragEvent e) {
        for (InventoryBuilder b : InventoryListener.inventoryMap.keySet()) {
            if (!e.getInventory().equals(b.getInventory())) continue;

            if (!b.allowableDrag || b.lockedInv) e.setCancelled(true);

            for (int slot : e.getRawSlots()) {
                if (slot >= 0 && slot < b.clickable.length && !b.clickable[slot]) {
                    e.setCancelled(true);
                    break;
                }
            }
            break;
        }
    }

    public static void playerQuitEvent(PlayerQuitEvent e) {
        inventoryMap.entrySet().removeIf(entry -> entry.getValue() == e.getPlayer());
    }

    public static void inventoryCloseEvent(InventoryCloseEvent e) {
        Iterator<Map.Entry<InventoryBuilder, Player>> iterator = inventoryMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<InventoryBuilder, Player> entry = iterator.next();
            InventoryBuilder b = entry.getKey();
            if (!e.getInventory().equals(b.getInventory())) continue;
            if (b.closeInventoryEvent != null) b.closeInventoryEvent.accept(new CloseInventoryEvent((Player) e.getPlayer(), e, b));
            if (b.isTempInventory()) iterator.remove();
        }
    }
}
package top.nontage.nontagelib.utils.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

public class InventoryListener {
    static final HashMap<InventoryBuilder, Player> inventoryMap = new HashMap<>();

    public static void inventoryClickEvent(InventoryClickEvent e) {
        int slot = e.getRawSlot();
        Consumer<ClickInventoryEvent> clickEvent;
        for (InventoryBuilder b : InventoryListener.inventoryMap.keySet()) {
            if (e.getView().getTopInventory().equals(b.getInventory()) && b.lockedInv) {
                e.setCancelled(true);
            }

            if (e.getView().getTopInventory().equals(b.getInventory())
                    && !b.allowableShiftClickDown
                    && e.isShiftClick()
                    && e.getClickedInventory() != null
                    && e.getClickedInventory().equals(e.getWhoClicked().getInventory())) {
                e.setCancelled(true);
            }

            if (e.getView().getTopInventory().equals(b.getInventory())
                    && !b.allowableDoubleClickDown
                    && e.getClick() == ClickType.DOUBLE_CLICK
                    && e.getClickedInventory() != null
                    && e.getClickedInventory().equals(e.getWhoClicked().getInventory())) {
                e.setCancelled(true);
            }

            if (e.getView().getTopInventory().equals(b.getInventory())
                    && !b.allowableShiftClickUp
                    && e.isShiftClick()
                    && e.getClickedInventory() != null
                    && e.getClickedInventory().equals(e.getView().getTopInventory())) {
                e.setCancelled(true);
            }

            if (e.getView().getTopInventory().equals(b.getInventory())
                    && !b.allowableDoubleClickUp
                    && e.getClick() == ClickType.DOUBLE_CLICK
                    && (e.getClickedInventory() == null || e.getClickedInventory().equals(e.getView().getTopInventory()))) {
                e.setCancelled(true);
            }


            if (b.getInventory().hashCode() != e.getClickedInventory().hashCode()) {
                continue;
            }

            clickEvent = b.allClickEvent;
            if (clickEvent != null) {
                clickEvent.accept(new ClickInventoryEvent((Player) e.getWhoClicked(), e, b));
            }

            clickEvent = b.clickEvents.get(slot);
            if (clickEvent != null) {
                clickEvent.accept(new ClickInventoryEvent((Player) e.getWhoClicked(), e, b));
            }

            if (!b.clickable[slot]) {
                e.setCancelled(true);
            }

            break;
        }
    }

    public static void inventoryDragEvent(InventoryDragEvent e) {
        for (InventoryBuilder b : InventoryListener.inventoryMap.keySet()) {
            if (b.getInventory().hashCode() != e.getInventory().hashCode()) {
                continue;
            }

            if (!b.allowableDrag) {
                e.setCancelled(true);
            }

            if (b.lockedInv) {
                e.setCancelled(true);
            }

            for (int slot : e.getRawSlots()) {
                if (!b.clickable[slot]) {
                    e.setCancelled(true);
                    break;
                }
            }
            break;
        }
    }

    public static void playerQuitEvent(PlayerQuitEvent e) {
        InventoryListener.inventoryMap.entrySet().removeIf(entry -> entry.getValue() == e.getPlayer());
    }

    public static void inventoryCloseEvent(InventoryCloseEvent e) {
        Iterator<Map.Entry<InventoryBuilder, Player>> iterator = InventoryListener.inventoryMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<InventoryBuilder, Player> entry = iterator.next();
            InventoryBuilder b = entry.getKey();
            if (b.getInventory().hashCode() != e.getInventory().hashCode()) {
                continue;
            }
            if (b.closeInventoryEvent != null) {
                b.closeInventoryEvent.accept(new CloseInventoryEvent((Player) e.getPlayer(), e, b));
            }
            if (b.isTempInventory()) {
                iterator.remove();
            }
        }
    }
}

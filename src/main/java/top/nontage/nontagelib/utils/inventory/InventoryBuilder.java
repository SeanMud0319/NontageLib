package top.nontage.nontagelib.utils.inventory;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import top.nontage.nontagelib.utils.reflection.ReflectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;


public class InventoryBuilder {
    public final Map<Integer, Consumer<ClickInventoryEvent>> clickEvents = new HashMap<>();
    public final boolean[] clickable;
    public boolean lockedInv = false;
    public boolean allowableShiftClickDown = true;
    public boolean allowableDrag = true;
    public boolean allowableDoubleClickDown = true;
    public boolean allowableShiftClickUp = true;
    public boolean allowableDoubleClickUp = true;

    public Consumer<ClickInventoryEvent> allClickEvent;
    public Consumer<CloseInventoryEvent> closeInventoryEvent;
    private final Player player;
    private final Inventory inventory;

    public InventoryBuilder(int size, String title) {
        this(null, size, title);
    }

    //item destory at InventoryListener onClose -> iterator.remove();
    public InventoryBuilder(Player player, int size, String title) {
        inventory = Bukkit.createInventory(null, size, title);
        clickable = new boolean[size + 36];
        this.player = player;
        InventoryListener.inventoryMap.put(this, player);
    }

    public InventoryBuilder(InventoryHolder holder, int size, String title) {
        inventory = Bukkit.createInventory(holder, size, title);
        clickable = new boolean[size + 36];
        this.player = null;
        InventoryListener.inventoryMap.put(this, player);
    }

    public Inventory getInventory() {
        return inventory;
    }

    boolean isTempInventory() {
        return player != null;
    }

    public void setTitle(String title) {
        ReflectionUtils.setField(Objects.requireNonNull(ReflectionUtils.getField(inventory, inventory.getClass().getSuperclass(), "inventory")), "title", title);
    }

    public InventoryBuilder setCloseEvent(Consumer<CloseInventoryEvent> e) {
        this.closeInventoryEvent = e;
        return this;
    }

    public InventoryBuilder setItem(ItemStack stack, int slot) {
        inventory.setItem(slot, stack);
        return this;
    }

    public InventoryBuilder setItem(ItemStack stack, int... slots) {
        for (int i : slots)
            setItem(stack, i);
        return this;
    }

    public InventoryBuilder setItem(ItemStack stack, int start, int end) {
        for (; start <= end; start++)
            inventory.setItem(start, stack);
        return this;
    }

    public InventoryBuilder setItem(ItemStack stack, Consumer<ClickInventoryEvent> event, int slot) {
        inventory.setItem(slot, stack);
        clickEvents.put(slot, event);
        return this;
    }

    public InventoryBuilder setItem(ItemStack stack, Consumer<ClickInventoryEvent> event, int... slots) {
        for (int i : slots) {
            setItem(stack, event, i);
        }
        return this;
    }

    public InventoryBuilder setItem(ItemStack stack, Consumer<ClickInventoryEvent> event, int start, int end) {
        for (; start <= end; start++) {
            inventory.setItem(start, stack);
            clickEvents.put(start, event);
        }
        return this;
    }

    public InventoryBuilder setClickEvent(Consumer<ClickInventoryEvent> event, int slot) {
        clickEvents.put(slot, event);
        return this;
    }

    public InventoryBuilder setClickEvent(Consumer<ClickInventoryEvent> event, int... slots) {
        for (int i : slots)
            setClickEvent(event, i);
        return this;
    }

    public InventoryBuilder setClickEvent(Consumer<ClickInventoryEvent> event, int start, int end) {
        for (; start <= end; start++)
            clickEvents.put(start, event);
        return this;
    }

    public InventoryBuilder setAllClickEvent(Consumer<ClickInventoryEvent> event) {
        this.allClickEvent = event;
        return this;
    }

    public InventoryBuilder setClickable(boolean flag, int slot) {
        clickable[slot] = flag;
        return this;
    }

    public InventoryBuilder setClickable(boolean flag, int... slots) {
        for (int i : slots)
            setClickable(flag, i);
        return this;
    }

    public InventoryBuilder setClickable(boolean flag, int start, int end) {
        for (; start <= end; start++)
            clickable[start] = flag;
        return this;
    }

    public InventoryBuilder setAllClickable(boolean flag) {
        Arrays.fill(clickable, flag);
        return this;
    }

    public InventoryBuilder setLockedInv(boolean flag) {
        lockedInv = flag;
        return this;
    }

    public void setAllowableShiftClickDown(boolean flag) {
        this.allowableShiftClickDown = flag;
    }

    public void setAllowableDrag(boolean flag) {
        this.allowableDrag = flag;
    }

    public void setAllowableDoubleClickDown(boolean allowableDoubleClickDown) {
        this.allowableDoubleClickDown = allowableDoubleClickDown;
    }
    public void setAllowableShiftClickUp(boolean flag) {
        this.allowableShiftClickUp = flag;
    }
    public void setAllowableDoubleClickUp(boolean allowableDoubleClickUp) {
        this.allowableDoubleClickUp = allowableDoubleClickUp;
    }
}

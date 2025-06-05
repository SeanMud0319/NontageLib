package top.nontage.nontagelib.utils.item;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    ItemStack itemStack;
    ItemMeta meta;

    public ItemBuilder(Material material) {
        itemStack = new ItemStack(material);
        meta = itemStack.getItemMeta();
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        meta = itemStack.getItemMeta();
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder durability(int s) {
        itemStack.setDurability((short) s);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder setName(String displayName) {
        meta.setDisplayName(displayName);
        return this;
    }

    public ItemBuilder leatherColor(int rgb) {
        return leatherColor(Color.fromRGB(rgb));
    }

    public ItemBuilder leatherColor(int r, int g, int b) {
        return leatherColor(Color.fromRGB(r, g, b));
    }

    public ItemBuilder leatherColor(Color color) {
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) meta;
        leatherArmorMeta.setColor(color);
        return this;
    }

    public Color getLeatherColor(Color color) {
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) meta;
        return leatherArmorMeta.getColor();
    }

    public ItemBuilder owner(String name) {
        SkullMeta skull = (SkullMeta) meta;
        skull.setOwner(name);
        itemStack.setItemMeta(skull);
        return this;
    }

    public ItemBuilder potion(PotionEffectType type, int duration, int amplifier) {
        PotionMeta potion = (PotionMeta) meta;
        potion.addCustomEffect(new PotionEffect(type, duration, amplifier), true);
        itemStack.setItemMeta(potion);
        return this;
    }

    public ItemBuilder unBreak() {
        meta.setUnbreakable(true);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        meta.setLore(lore);
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        addLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder addLore(List<String> lore) {
        List<String> s = meta.getLore();
        if (s == null) {
            setLore(lore);
        } else {
            s.addAll(lore);
            meta.setLore(s);
        }
        return this;
    }

    public ItemBuilder hideAttr() {
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        return this;
    }

    public ItemBuilder hideEnchant() {
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder hidePotion() {
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        return this;
    }

    public ItemBuilder hideUnbreak() {
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        return this;
    }


    public ItemStack build() {
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
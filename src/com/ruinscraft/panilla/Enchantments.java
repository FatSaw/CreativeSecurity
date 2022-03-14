package com.ruinscraft.panilla;
import org.bukkit.enchantments.Enchantment;

class Enchantments  {

    protected int getMaxLevel(EnchantmentCompat enchCompat) {
        Enchantment bukkitEnchantment = getBukkitEnchantment(enchCompat);
        if (bukkitEnchantment == null) {
            return Integer.MAX_VALUE; // unknown enchantment
        } else {
        	return bukkitEnchantment.getMaxLevel();
        }
        
    }

    protected int getStartLevel(EnchantmentCompat enchCompat) {
        Enchantment bukkitEnchantment = getBukkitEnchantment(enchCompat);
        if (bukkitEnchantment == null) {
            return Integer.MAX_VALUE; // unknown enchantment
        } else {
            return bukkitEnchantment.getStartLevel();
        }
    }

    protected boolean conflicting(EnchantmentCompat enchCompat, EnchantmentCompat _enchCompat) {
        Enchantment bukkitEnchantment = getBukkitEnchantment(enchCompat);
        Enchantment _bukkitEnchantment = getBukkitEnchantment(_enchCompat);
        if (bukkitEnchantment == null || _bukkitEnchantment == null) {
            return false; // unknown enchantment
        } else {
            return bukkitEnchantment.conflictsWith(_bukkitEnchantment);
        }
    }

    private Enchantment getBukkitEnchantment(EnchantmentCompat enchCompat) {
        Enchantment bukkitEnchantment = null;
        bukkitEnchantment = Enchantment.getByName(enchCompat.legacyName);
        return bukkitEnchantment;
    }

}

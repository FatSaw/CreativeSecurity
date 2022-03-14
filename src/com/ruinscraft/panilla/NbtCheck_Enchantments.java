package com.ruinscraft.panilla;

class NbtCheck_Enchantments extends NbtCheck {

	protected NbtCheck_Enchantments() {
        super("Enchantments", "Enchantments", "StoredEnchantments");
    }

    private static EnchantmentCompat getEnchCompat(NbtTagCompound enchantment, ServerShield panilla) {
        final EnchantmentCompat enchCompat;

        if (enchantment.hasKeyOfType("id", NbtDataType.INT) || enchantment.hasKeyOfType("id", NbtDataType.SHORT)) {
            final int id = enchantment.getInt("id");
            enchCompat = EnchantmentCompat.getByLegacyId(id);
        } else if (enchantment.hasKeyOfType("id", NbtDataType.STRING)) {
            final String namedKey = enchantment.getString("id");
            enchCompat = EnchantmentCompat.getByNamedKey(namedKey);
        } else {
            enchCompat = null;
        }

        return enchCompat;
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        String using = null;

        if (tag.hasKey(getName())) {
            using = getName();
        } else {
            for (String alias : getAliases()) {
                if (tag.hasKey(alias)) {
                    using = alias;
                }
            }
        }

        if (using == null) {
            throw new IllegalStateException("Unknown enchantment tag");
        }

        NbtTagList enchantments = tag.getList(using, NbtDataType.COMPOUND);

        for (int i = 0; i < enchantments.size(); i++) {
            NbtTagCompound enchantment = enchantments.getCompound(i);
            EnchantmentCompat enchCompat = getEnchCompat(enchantment, panilla);

            if (enchCompat == null) {
                continue;
            }

            int lvl = 0xFFFF & enchantments.getCompound(i).getShort("lvl");
            Enchantments ench = new Enchantments();
            if (lvl > ench.getMaxLevel(enchCompat)) {
                return NbtCheckResult.FAIL;
            }

            if (lvl < ench.getStartLevel(enchCompat)) {
                return NbtCheckResult.FAIL;
            }

            for (int j = 0; j < enchantments.size(); j++) {
                NbtTagCompound otherEnchantment = enchantments.getCompound(j);
                EnchantmentCompat _enchCompat = getEnchCompat(otherEnchantment, panilla);

                if (_enchCompat == null) {
                    continue;
                }

                if (enchCompat != _enchCompat) {
                    if (ench.conflicting(enchCompat, _enchCompat)) {
                        return NbtCheckResult.FAIL;
                    }
                }
            }
        }

        return NbtCheckResult.PASS;
    }

}

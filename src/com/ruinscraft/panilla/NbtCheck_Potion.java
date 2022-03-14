package com.ruinscraft.panilla;

class NbtCheck_Potion extends NbtCheck {

	protected NbtCheck_Potion() {
        super("Potion");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        return NbtCheckResult.PASS;
    }

}

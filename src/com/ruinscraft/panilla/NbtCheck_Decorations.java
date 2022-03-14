package com.ruinscraft.panilla;

class NbtCheck_Decorations extends NbtCheck {

	protected NbtCheck_Decorations() {
        super("Decorations");
    }

    // for treasure maps
    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        return NbtCheckResult.PASS;
    }

}

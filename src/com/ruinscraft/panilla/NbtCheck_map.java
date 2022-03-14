package com.ruinscraft.panilla;

class NbtCheck_map extends NbtCheck {

	protected NbtCheck_map() {
        super("map");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        return NbtCheckResult.PASS;
    }

}

package com.ruinscraft.panilla;

class NbtCheck_resolved extends NbtCheck {

	protected NbtCheck_resolved() {
        super("resolved");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        return NbtCheckResult.PASS;
    }

}

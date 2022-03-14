package com.ruinscraft.panilla;

class NbtCheck_HideFlags extends NbtCheck {

	protected NbtCheck_HideFlags() {
        super("HideFlags");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        return NbtCheckResult.FAIL;
    }

}

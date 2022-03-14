package com.ruinscraft.panilla;

class NbtCheck_CanDestroy extends NbtCheck {

	protected NbtCheck_CanDestroy() {
        super("CanDestroy");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        return NbtCheckResult.FAIL;
    }

}

package com.ruinscraft.panilla;

class NbtCheck_CustomModelData extends NbtCheck {

    // introduced in 1.14
	protected NbtCheck_CustomModelData() {
        super("CustomModelData");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        return NbtCheckResult.FAIL;
    }

}

package com.ruinscraft.panilla;

class NbtCheck_weBrushJson extends NbtCheck {

	protected NbtCheck_weBrushJson() {
        super("weBrushJson");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
    	return NbtCheckResult.FAIL;

        //return NbtCheckResult.PASS;
    }

}

package com.ruinscraft.panilla;

class NbtCheck_map_scale_direction extends NbtCheck {

	protected NbtCheck_map_scale_direction() {
        super("map_scale_direction");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        return NbtCheckResult.PASS;
    }

}

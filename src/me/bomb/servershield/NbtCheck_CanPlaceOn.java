package me.bomb.servershield;

class NbtCheck_CanPlaceOn extends NbtCheck {

	protected NbtCheck_CanPlaceOn() {
        super("CanPlaceOn");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        return NbtCheckResult.FAIL;
    }

}

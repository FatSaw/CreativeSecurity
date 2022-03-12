package me.bomb.servershield;

class NbtCheck_Unbreakable extends NbtCheck {

	protected NbtCheck_Unbreakable() {
        super("Unbreakable");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        return NbtCheckResult.FAIL;
    }

}

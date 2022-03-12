package me.bomb.servershield;


class NbtCheck_generation extends NbtCheck {

	protected NbtCheck_generation() {
        super("generation");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        return NbtCheckResult.PASS;
    }

}

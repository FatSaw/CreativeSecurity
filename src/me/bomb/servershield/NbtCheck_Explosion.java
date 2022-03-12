package me.bomb.servershield;


class NbtCheck_Explosion extends NbtCheck {

	protected NbtCheck_Explosion() {
        super("Explosion");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        return NbtCheckResult.PASS;
    }

}

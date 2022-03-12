package me.bomb.servershield;

class NbtCheck_Effects extends NbtCheck {

	protected NbtCheck_Effects() {
        super("Effects");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        return NbtCheckResult.PASS;
    }

}

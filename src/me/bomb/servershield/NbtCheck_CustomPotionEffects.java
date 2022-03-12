package me.bomb.servershield;


class NbtCheck_CustomPotionEffects extends NbtCheck {

	protected NbtCheck_CustomPotionEffects() {
        super("CustomPotionEffects");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        return NbtCheckResult.FAIL;
    }

}

package me.bomb.servershield;


class NbtCheck_CustomPotionColor extends NbtCheck {

	protected NbtCheck_CustomPotionColor() {
        super("CustomPotionColor");
    }

    @Override
    protected NbtCheckResult check(NbtTagCompound tag, String itemName, ServerShield panilla) {
        int bgr = tag.getInt(getName());
        boolean validColor = true;  // TODO:
        if (!validColor) {
            return NbtCheckResult.CRITICAL;
        }
        return NbtCheckResult.PASS;
    }

}
